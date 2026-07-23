package com.kgict.myhome.lh.repository;

import java.util.List;
import java.util.Optional;

import com.kgict.myhome.lh.dto.LhNoticeCacheStatus;
import com.kgict.myhome.lh.dto.LhNoticeDto;
import com.kgict.myhome.lh.dto.LhNoticeFetchResult;

public interface LhNoticeStore {
	void upsertAll(LhNoticeFetchResult result, String message);

	void updateStatus(String resultCode, String responseDateTime, String message);

	List<LhNoticeDto> findAll();

	Optional<LhNoticeDto> findByPanId(String panId);

	LhNoticeCacheStatus status(boolean serviceKeyConfigured, boolean schedulerEnabled);
}
