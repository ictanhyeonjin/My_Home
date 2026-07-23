package com.kgict.myhome.lh.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kgict.myhome.lh.client.LhLeaseNoticeClient;
import com.kgict.myhome.lh.config.LhOpenApiProperties;
import com.kgict.myhome.lh.dto.LhNoticeFetchResult;
import com.kgict.myhome.lh.dto.LhNoticeSearchRequest;
import com.kgict.myhome.lh.repository.LhNoticeStore;

@Service
public class LhNoticeSyncService {
	private final LhLeaseNoticeClient client;
	private final LhNoticeStore store;
	private final LhOpenApiProperties properties;

	public LhNoticeSyncService(LhLeaseNoticeClient client, LhNoticeStore store, LhOpenApiProperties properties) {
		this.client = client;
		this.store = store;
		this.properties = properties;
	}

	public LhNoticeFetchResult syncDefaultNotices() {
		if (!properties.hasServiceKey()) {
			store.updateStatus("NO_SERVICE_KEY", null, "LH_SERVICE_KEY 환경변수 또는 lh.open-api.service-key 설정이 필요합니다.");
			return new LhNoticeFetchResult(List.of(), "NO_SERVICE_KEY", null, null);
		}

		LhNoticeSearchRequest request = LhNoticeSearchRequest.currentYear(properties.normalizedDefaultUppAisTpCd());
		try {
			LhNoticeFetchResult result = client.fetchNotices(request);
			store.upsertAll(result, "LH 공고 목록 동기화 완료: " + result.count() + "건");
			return result;
		} catch (Exception e) {
			store.updateStatus("ERROR", null, "LH 공고 목록 동기화 실패: " + e.getMessage());
			throw e;
		}
	}
}
