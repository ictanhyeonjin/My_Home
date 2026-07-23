package com.kgict.myhome.lh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kgict.myhome.lh.dto.LhCodesResponse;
import com.kgict.myhome.lh.service.LhCodeService;

@RestController
@RequestMapping("/api/lh/codes")
public class LhCodeController {
	private final LhCodeService codeService;

	public LhCodeController(LhCodeService codeService) {
		this.codeService = codeService;
	}

	@GetMapping
	public LhCodesResponse codes() {
		return codeService.findCodes();
	}
}
