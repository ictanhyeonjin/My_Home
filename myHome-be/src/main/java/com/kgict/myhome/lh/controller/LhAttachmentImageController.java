package com.kgict.myhome.lh.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kgict.myhome.lh.service.LhAttachmentImageService;

@RestController
@RequestMapping("/api/lh/images")
public class LhAttachmentImageController {
	private final LhAttachmentImageService imageService;

	public LhAttachmentImageController(LhAttachmentImageService imageService) {
		this.imageService = imageService;
	}

	@GetMapping("/{fileId}")
	public ResponseEntity<byte[]> image(@PathVariable String fileId) {
		return imageService.fetch(fileId);
	}
}
