package com.kgict.myhome.lh.service;

import org.springframework.stereotype.Service;

import com.kgict.myhome.lh.client.LhLeaseNoticeClient;
import com.kgict.myhome.lh.config.LhOpenApiProperties;
import com.kgict.myhome.lh.dto.LhNoticeDetailResponse;
import com.kgict.myhome.lh.dto.LhNoticeDto;
import com.kgict.myhome.lh.repository.LhNoticeStore;

@Service
public class LhNoticeDetailService {
	private final LhLeaseNoticeClient client;
	private final LhNoticeStore store;
	private final LhOpenApiProperties properties;

	public LhNoticeDetailService(LhLeaseNoticeClient client, LhNoticeStore store, LhOpenApiProperties properties) {
		this.client = client;
		this.store = store;
		this.properties = properties;
	}

	public LhNoticeDetailResponse findDetail(String panId) {
		if (!properties.hasServiceKey()) {
			throw new IllegalStateException("LH_SERVICE_KEY 환경변수 또는 lh.open-api.service-key 설정이 필요합니다.");
		}

		LhNoticeDto notice = store.findByPanId(panId)
				.orElseThrow(() -> new IllegalArgumentException("목록 캐시에 없는 공고입니다. 먼저 목록 동기화를 실행해 주세요."));

		return client.fetchNoticeDetail(notice);
	}
}
