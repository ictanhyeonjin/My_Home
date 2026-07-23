package com.kgict.myhome.lh.client;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgict.myhome.lh.config.LhOpenApiProperties;
import com.kgict.myhome.lh.dto.LhNoticeDetailResponse;
import com.kgict.myhome.lh.dto.LhNoticeDto;
import com.kgict.myhome.lh.dto.LhNoticeFetchResult;
import com.kgict.myhome.lh.dto.LhNoticeSearchRequest;

@Component
public class LhLeaseNoticeClient {
	private static final TypeReference<List<Map<String, List<Map<String, Object>>>>> RESPONSE_TYPE = new TypeReference<>() {
	};

	private final RestClient restClient;
	private final ObjectMapper objectMapper;
	private final LhOpenApiProperties properties;

	public LhLeaseNoticeClient(ObjectMapper objectMapper, LhOpenApiProperties properties) {
		this.restClient = RestClient.create();
		this.objectMapper = objectMapper;
		this.properties = properties;
	}

	public LhNoticeFetchResult fetchNotices(LhNoticeSearchRequest request) {
		List<LhNoticeDto> notices = new ArrayList<>();
		String ssCode = null;
		String responseDateTime = null;

		for (int page = 1; page <= properties.maxPages(); page++) {
			ParsedResponse parsedResponse = parseResponse(requestPage(request, page));
			notices.addAll(parsedResponse.notices());
			ssCode = parsedResponse.ssCode();
			responseDateTime = parsedResponse.responseDateTime();

			if (parsedResponse.notices().isEmpty() || parsedResponse.notices().size() < properties.pageSize()) {
				break;
			}
		}

		return new LhNoticeFetchResult(notices, ssCode, responseDateTime, LocalDateTime.now());
	}

	public LhNoticeDetailResponse fetchNoticeDetail(LhNoticeDto notice) {
		validateDetailParams(notice);
		String response = restClient.get()
				.uri(properties.detailBaseUrl(), uriBuilder -> detailParams(uriBuilder, notice).build())
				.retrieve()
				.body(String.class);
		ParsedResponse parsedResponse = parseResponse(response);

		return new LhNoticeDetailResponse(
				notice.panId(),
				parsedResponse.ssCode(),
				parsedResponse.responseDateTime(),
				LocalDateTime.now(),
				parsedResponse.sections()
		);
	}

	private String requestPage(LhNoticeSearchRequest request, int page) {
		return restClient.get()
				.uri(properties.baseUrl(), uriBuilder -> listParams(uriBuilder, request, page).build())
				.retrieve()
				.body(String.class);
	}

	private UriBuilder listParams(UriBuilder builder, LhNoticeSearchRequest request, int page) {
		builder.queryParam("serviceKey", properties.serviceKey())
				.queryParam("PG_SZ", properties.pageSize())
				.queryParam("PAGE", page)
				.queryParam("PAN_ST_DT", request.panStDt())
				.queryParam("PAN_ED_DT", request.panEdDt());

		addIfPresent(builder, "PAN_NM", request.panNm());
		addIfPresent(builder, "UPP_AIS_TP_CD", request.uppAisTpCd());
		addIfPresent(builder, "CNP_CD", request.cnpCd());
		addIfPresent(builder, "PAN_SS", request.panSs());
		addIfPresent(builder, "CLSG_ST_DT", request.clsgStDt());
		addIfPresent(builder, "CLSG_ED_DT", request.clsgEdDt());
		return builder;
	}

	private UriBuilder detailParams(UriBuilder builder, LhNoticeDto notice) {
		return builder.queryParam("serviceKey", properties.serviceKey())
				.queryParam("SPL_INF_TP_CD", notice.splInfTpCd())
				.queryParam("CCR_CNNT_SYS_DS_CD", notice.ccrCnntSysDsCd())
				.queryParam("PAN_ID", notice.panId())
				.queryParam("UPP_AIS_TP_CD", notice.uppAisTpCd())
				.queryParam("AIS_TP_CD", notice.aisTpCd());
	}

	private void validateDetailParams(LhNoticeDto notice) {
		Map<String, String> requiredParams = new LinkedHashMap<>();
		requiredParams.put("PAN_ID", notice.panId());
		requiredParams.put("SPL_INF_TP_CD", notice.splInfTpCd());
		requiredParams.put("CCR_CNNT_SYS_DS_CD", notice.ccrCnntSysDsCd());
		requiredParams.put("UPP_AIS_TP_CD", notice.uppAisTpCd());
		requiredParams.put("AIS_TP_CD", notice.aisTpCd());

		List<String> missing = requiredParams.entrySet().stream()
				.filter(entry -> !StringUtils.hasText(entry.getValue()))
				.map(Map.Entry::getKey)
				.toList();
		if (!missing.isEmpty()) {
			throw new IllegalArgumentException("상세정보 조회 필수 파라미터가 없습니다: " + String.join(", ", missing));
		}
	}

	private void addIfPresent(UriBuilder builder, String name, String value) {
		if (StringUtils.hasText(value)) {
			builder.queryParam(name, value);
		}
	}

	private ParsedResponse parseResponse(String response) {
		try {
			List<Map<String, List<Map<String, Object>>>> sectionList = objectMapper.readValue(response, RESPONSE_TYPE);
			Map<String, List<Map<String, Object>>> sections = new LinkedHashMap<>();
			List<LhNoticeDto> notices = new ArrayList<>();
			String ssCode = null;
			String responseDateTime = null;

			for (Map<String, List<Map<String, Object>>> section : sectionList) {
				for (Map.Entry<String, List<Map<String, Object>>> entry : section.entrySet()) {
					sections.put(entry.getKey(), entry.getValue());
				}
				if (section.containsKey("dsList")) {
					for (Map<String, Object> item : section.get("dsList")) {
						notices.add(objectMapper.convertValue(item, LhNoticeDto.class));
					}
				}
				if (section.containsKey("resHeader") && !section.get("resHeader").isEmpty()) {
					Map<String, Object> header = section.get("resHeader").get(0);
					ssCode = stringValue(header.get("SS_CODE"));
					responseDateTime = stringValue(header.get("RS_DTTM"));
				}
			}

			return new ParsedResponse(sections, notices, ssCode, responseDateTime);
		} catch (Exception e) {
			throw new IllegalStateException("LH OpenAPI 응답을 해석하지 못했습니다.", e);
		}
	}

	private String stringValue(Object value) {
		return value == null ? null : String.valueOf(value);
	}

	private record ParsedResponse(
			Map<String, List<Map<String, Object>>> sections,
			List<LhNoticeDto> notices,
			String ssCode,
			String responseDateTime
	) {
	}
}
