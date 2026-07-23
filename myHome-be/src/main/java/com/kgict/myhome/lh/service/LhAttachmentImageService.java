package com.kgict.myhome.lh.service;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class LhAttachmentImageService {
	private static final String LH_ORIGIN = "https://apply.lh.or.kr";
	private final RestClient restClient = RestClient.create();

	public ResponseEntity<byte[]> fetch(String fileId) {
		if (fileId == null || !fileId.matches("\\d+")) {
			throw new IllegalArgumentException("올바르지 않은 LH 이미지 fileid입니다.");
		}

		byte[] image = restClient.get()
				.uri(LH_ORIGIN + "/lhapply/lhFile.do?fileid=" + fileId)
				.retrieve()
				.body(byte[].class);
		MediaType contentType = detectImageType(image);
		if (contentType == null) {
			throw new IllegalStateException("LH 서버가 이미지 형식으로 응답하지 않았습니다.");
		}

		return ResponseEntity.ok()
				.contentType(contentType)
				.body(image);
	}

	private MediaType detectImageType(byte[] bytes) {
		if (bytes == null || bytes.length < 8) return null;
		if ((bytes[0] & 0xff) == 0xff && (bytes[1] & 0xff) == 0xd8) return MediaType.IMAGE_JPEG;
		if ((bytes[0] & 0xff) == 0x89 && bytes[1] == 'P' && bytes[2] == 'N' && bytes[3] == 'G') return MediaType.IMAGE_PNG;
		if (bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F') return MediaType.IMAGE_GIF;
		return null;
	}
}
