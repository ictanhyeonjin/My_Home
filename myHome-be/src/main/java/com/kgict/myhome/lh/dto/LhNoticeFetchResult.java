package com.kgict.myhome.lh.dto;

import java.time.LocalDateTime;
import java.util.List;

public record LhNoticeFetchResult(
		List<LhNoticeDto> notices,
		String ssCode,
		String responseDateTime,
		LocalDateTime fetchedAt
) {
	public int count() {
		return notices == null ? 0 : notices.size();
	}
}
