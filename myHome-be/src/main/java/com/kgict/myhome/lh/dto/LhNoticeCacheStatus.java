package com.kgict.myhome.lh.dto;

import java.time.LocalDateTime;

public record LhNoticeCacheStatus(
		int count,
		LocalDateTime lastFetchedAt,
		String lastResultCode,
		String lastResponseDateTime,
		String lastMessage,
		boolean serviceKeyConfigured,
		boolean schedulerEnabled
) {
}
