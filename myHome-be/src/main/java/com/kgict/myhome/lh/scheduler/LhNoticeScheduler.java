package com.kgict.myhome.lh.scheduler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kgict.myhome.lh.config.LhOpenApiProperties;
import com.kgict.myhome.lh.service.LhNoticeSyncService;

@Component
public class LhNoticeScheduler {
	private static final Logger log = LogManager.getLogger(LhNoticeScheduler.class);

	private final LhNoticeSyncService syncService;
	private final LhOpenApiProperties properties;

	public LhNoticeScheduler(LhNoticeSyncService syncService, LhOpenApiProperties properties) {
		this.syncService = syncService;
		this.properties = properties;
	}

	@Scheduled(initialDelayString = "${lh.open-api.scheduler-initial-delay-ms:10000}", fixedRateString = "${lh.open-api.scheduler-fixed-rate-ms:3600000}")
	public void syncHourly() {
		if (!properties.schedulerEnabled()) {
			return;
		}

		try {
			syncService.syncDefaultNotices();
		} catch (Exception e) {
			log.error("LH 공고 목록 스케줄러 실행 실패", e);
		}
	}
}
