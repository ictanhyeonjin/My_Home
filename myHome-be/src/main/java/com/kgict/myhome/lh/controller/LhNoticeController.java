package com.kgict.myhome.lh.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kgict.myhome.lh.config.LhOpenApiProperties;
import com.kgict.myhome.lh.dto.LhNoticeCacheStatus;
import com.kgict.myhome.lh.dto.LhNoticeDetailResponse;
import com.kgict.myhome.lh.dto.LhNoticeDto;
import com.kgict.myhome.lh.dto.LhNoticeFetchResult;
import com.kgict.myhome.lh.repository.LhNoticeStore;
import com.kgict.myhome.lh.service.LhNoticeDetailService;
import com.kgict.myhome.lh.service.LhNoticeSyncService;

@RestController
@RequestMapping("/api/lh/notices")
public class LhNoticeController {
	private final LhNoticeStore store;
	private final LhNoticeSyncService syncService;
	private final LhNoticeDetailService detailService;
	private final LhOpenApiProperties properties;

	public LhNoticeController(LhNoticeStore store, LhNoticeSyncService syncService, LhNoticeDetailService detailService, LhOpenApiProperties properties) {
		this.store = store;
		this.syncService = syncService;
		this.detailService = detailService;
		this.properties = properties;
	}

	@GetMapping
	public List<LhNoticeDto> notices() {
		return store.findAll();
	}

	@GetMapping("/{panId}")
	public ResponseEntity<LhNoticeDto> notice(@PathVariable String panId) {
		return store.findByPanId(panId)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/{panId}/detail")
	public LhNoticeDetailResponse detail(@PathVariable String panId) {
		return detailService.findDetail(panId);
	}

	@GetMapping("/{panId}/supply-info")
	public LhNoticeDetailResponse supplyInfo(@PathVariable String panId) {
		return detailService.findDetail(panId);
	}

	@GetMapping("/status")
	public LhNoticeCacheStatus status() {
		return store.status(properties.hasServiceKey(), properties.schedulerEnabled());
	}

	@PostMapping("/sync")
	public LhNoticeFetchResult sync() {
		return syncService.syncDefaultNotices();
	}
}
