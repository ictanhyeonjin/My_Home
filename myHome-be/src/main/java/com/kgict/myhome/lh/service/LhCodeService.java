package com.kgict.myhome.lh.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kgict.myhome.lh.dto.LhCodeDto;
import com.kgict.myhome.lh.dto.LhCodesResponse;

@Service
public class LhCodeService {
	public LhCodesResponse findCodes() {
		return new LhCodesResponse(noticeTypes(), regions(), noticeStatuses());
	}

	private List<LhCodeDto> noticeTypes() {
		return List.of(
				new LhCodeDto("01", "토지"),
				new LhCodeDto("05", "분양주택"),
				new LhCodeDto("06", "임대주택"),
				new LhCodeDto("13", "주거복지"),
				new LhCodeDto("22", "상가"),
				new LhCodeDto("39", "신혼희망타운"));
	}

	private List<LhCodeDto> regions() {
		return List.of(
				new LhCodeDto("11", "서울특별시"),
				new LhCodeDto("12", "광주광역시"),
				new LhCodeDto("26", "부산광역시"),
				new LhCodeDto("27", "대구광역시"),
				new LhCodeDto("28", "인천광역시"),
				new LhCodeDto("30", "대전광역시"),
				new LhCodeDto("31", "울산광역시"),
				new LhCodeDto("36110", "세종특별자치시"),
				new LhCodeDto("41", "경기도"),
				new LhCodeDto("43", "충청북도"),
				new LhCodeDto("44", "충청남도"),
				new LhCodeDto("47", "경상북도"),
				new LhCodeDto("48", "경상남도"),
				new LhCodeDto("50", "제주특별자치도"),
				new LhCodeDto("51", "강원특별자치도"),
				new LhCodeDto("52", "전북특별자치도"));
	}

	private List<LhCodeDto> noticeStatuses() {
		return List.of(
				new LhCodeDto("공고중", "공고중"),
				new LhCodeDto("접수중", "접수중"),
				new LhCodeDto("접수마감", "접수마감"),
				new LhCodeDto("상담요청", "상담요청"),
				new LhCodeDto("정정공고중", "정정공고중"));
	}
}
