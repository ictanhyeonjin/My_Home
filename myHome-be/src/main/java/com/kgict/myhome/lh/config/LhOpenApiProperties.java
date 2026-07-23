package com.kgict.myhome.lh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lh.open-api")
public record LhOpenApiProperties(
		String baseUrl,
		String detailBaseUrl,
		String serviceKey,
		int pageSize,
		int maxPages,
		String defaultUppAisTpCd,
		boolean schedulerEnabled
) {
	public boolean hasServiceKey() {
		return serviceKey != null && !serviceKey.isBlank();
	}

	public String normalizedDefaultUppAisTpCd() {
		return defaultUppAisTpCd == null || defaultUppAisTpCd.isBlank() ? null : defaultUppAisTpCd;
	}
}
