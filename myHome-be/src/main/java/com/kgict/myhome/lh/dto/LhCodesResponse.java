package com.kgict.myhome.lh.dto;

import java.util.List;

public record LhCodesResponse(
		List<LhCodeDto> noticeTypes,
		List<LhCodeDto> regions,
		List<LhCodeDto> noticeStatuses
) {
}
