package com.kgict.myhome.lh.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record LhNoticeSearchRequest(
		String panNm,
		String uppAisTpCd,
		String cnpCd,
		String panSs,
		String panStDt,
		String panEdDt,
		String clsgStDt,
		String clsgEdDt
) {
	private static final DateTimeFormatter BASIC_DATE = DateTimeFormatter.BASIC_ISO_DATE;

	public static LhNoticeSearchRequest currentYear(String defaultUppAisTpCd) {
		LocalDate today = LocalDate.now();
		return new LhNoticeSearchRequest(
				null,
				defaultUppAisTpCd,
				null,
				null,
				today.withDayOfYear(1).format(BASIC_DATE),
				today.withMonth(12).withDayOfMonth(31).format(BASIC_DATE),
				null,
				null
		);
	}
}
