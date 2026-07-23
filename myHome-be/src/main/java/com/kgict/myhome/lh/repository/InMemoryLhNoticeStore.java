package com.kgict.myhome.lh.repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kgict.myhome.lh.dto.LhNoticeCacheStatus;
import com.kgict.myhome.lh.dto.LhNoticeDto;
import com.kgict.myhome.lh.dto.LhNoticeFetchResult;

@Repository
public class InMemoryLhNoticeStore implements LhNoticeStore {
	private final Map<String, LhNoticeDto> notices = new LinkedHashMap<>();
	private LocalDateTime lastFetchedAt;
	private String lastResultCode = "READY";
	private String lastResponseDateTime;
	private String lastMessage = "아직 동기화 전입니다.";

	@Override
	public synchronized void upsertAll(LhNoticeFetchResult result, String message) {
		for (LhNoticeDto notice : result.notices()) {
			if (notice.panId() != null && !notice.panId().isBlank()) {
				notices.put(notice.panId(), notice);
			}
		}
		lastFetchedAt = result.fetchedAt();
		lastResultCode = result.ssCode();
		lastResponseDateTime = result.responseDateTime();
		lastMessage = message;
	}

	@Override
	public synchronized void updateStatus(String resultCode, String responseDateTime, String message) {
		lastResultCode = resultCode;
		lastResponseDateTime = responseDateTime;
		lastMessage = message;
	}

	@Override
	public synchronized List<LhNoticeDto> findAll() {
		return notices.values().stream()
				.sorted(Comparator.comparing(LhNoticeDto::panDt, Comparator.nullsLast(Comparator.reverseOrder())))
				.toList();
	}

	@Override
	public synchronized Optional<LhNoticeDto> findByPanId(String panId) {
		return Optional.ofNullable(notices.get(panId));
	}

	@Override
	public synchronized LhNoticeCacheStatus status(boolean serviceKeyConfigured, boolean schedulerEnabled) {
		return new LhNoticeCacheStatus(
				notices.size(),
				lastFetchedAt,
				lastResultCode,
				lastResponseDateTime,
				lastMessage,
				serviceKeyConfigured,
				schedulerEnabled
		);
	}
}
