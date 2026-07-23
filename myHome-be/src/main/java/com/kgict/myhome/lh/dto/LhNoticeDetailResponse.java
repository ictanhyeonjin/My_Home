package com.kgict.myhome.lh.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record LhNoticeDetailResponse(
		String panId,
		String ssCode,
		String responseDateTime,
		LocalDateTime fetchedAt,
		Map<String, List<Map<String, Object>>> sections
) {
}
