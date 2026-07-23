package com.kgict.myhome.lh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LhNoticeDto(
		@JsonProperty("RNUM") String rnum,
		@JsonProperty("ALL_CNT") String allCnt,
		@JsonProperty("PAN_ID") String panId,
		@JsonProperty("PAN_NM") String panNm,
		@JsonProperty("PAN_SS") String panSs,
		@JsonProperty("PAN_DT") String panDt,
		@JsonProperty("PAN_NT_ST_DT") String panNtStDt,
		@JsonProperty("CLSG_DT") String clsgDt,
		@JsonProperty("UPP_AIS_TP_CD") String uppAisTpCd,
		@JsonProperty("UPP_AIS_TP_NM") String uppAisTpNm,
		@JsonProperty("AIS_TP_CD") String aisTpCd,
		@JsonProperty("AIS_TP_CD_NM") String aisTpCdNm,
		@JsonProperty("CNP_CD_NM") String cnpCdNm,
		@JsonProperty("SPL_INF_TP_CD") String splInfTpCd,
		@JsonProperty("CCR_CNNT_SYS_DS_CD") String ccrCnntSysDsCd,
		@JsonProperty("DTL_URL") String dtlUrl,
		@JsonProperty("DTL_URL_MOB") String dtlUrlMob
) {
}
