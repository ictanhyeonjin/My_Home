# LH OpenAPI DB 저장 설계 메모

## 현재 구현

- `myHome-be`는 DB 없이 동작하도록 LH 공고 목록을 메모리 캐시에 저장한다.
- 스케줄러는 `lh.open-api.scheduler-fixed-rate-ms` 기준으로 실행되며 기본값은 1시간이다.
- 기본 스케줄러 조회는 `UPP_AIS_TP_CD`를 비워 전체 공고유형을 가져오도록 했다.
- 특정 유형만 운영하려면 `lh.open-api.default-upp-ais-tp-cd`에 `05`, `06` 같은 코드를 설정한다.
- 수동 확인 API는 `POST /api/lh/notices/sync` 이다.
- 목록 조회 API는 `GET /api/lh/notices` 이다.
- 상태 조회 API는 `GET /api/lh/notices/status` 이다.
- 코드값 조회 API는 `GET /api/lh/codes` 이다.

## 필수 저장 컬럼

다음 컬럼은 상세조회/공급정보조회 API 요청 파라미터로 다시 사용하므로 누락하면 안 된다.

| 컬럼 | 용도 |
| --- | --- |
| `PAN_ID` | 공고 PK, 상세조회 기준 |
| `SPL_INF_TP_CD` | 공급정보구분코드 |
| `CCR_CNNT_SYS_DS_CD` | 고객센터연계시스템구분코드 |
| `UPP_AIS_TP_CD` | 상위공고유형코드 |
| `AIS_TP_CD` | 공고유형코드 |

## 권장 테이블

```sql
CREATE TABLE lh_notice (
  pan_id VARCHAR(30) NOT NULL COMMENT '공고ID',
  pan_nm VARCHAR(500) NULL COMMENT '공고명',
  pan_ss VARCHAR(30) NULL COMMENT '공고상태',
  pan_dt CHAR(8) NULL COMMENT '공고일 yyyyMMdd',
  pan_nt_st_dt VARCHAR(10) NULL COMMENT '게시일',
  clsg_dt VARCHAR(10) NULL COMMENT '마감일',
  upp_ais_tp_cd VARCHAR(10) NULL COMMENT '상위공고유형코드',
  upp_ais_tp_nm VARCHAR(100) NULL COMMENT '상위공고유형명',
  ais_tp_cd VARCHAR(10) NULL COMMENT '공고유형코드',
  ais_tp_cd_nm VARCHAR(100) NULL COMMENT '공고유형명',
  cnp_cd_nm VARCHAR(100) NULL COMMENT '지역명',
  spl_inf_tp_cd VARCHAR(20) NULL COMMENT '공급정보구분코드',
  ccr_cnnt_sys_ds_cd VARCHAR(20) NULL COMMENT '고객센터연계시스템구분코드',
  dtl_url TEXT NULL COMMENT '상세 URL',
  dtl_url_mob TEXT NULL COMMENT '모바일 상세 URL',
  all_cnt INT NULL COMMENT 'API 전체 건수',
  raw_json JSON NULL COMMENT '원본 응답 보존용',
  last_synced_at DATETIME NOT NULL COMMENT '마지막 동기화 일시',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (pan_id),
  INDEX ix_lh_notice_pan_dt (pan_dt),
  INDEX ix_lh_notice_status (pan_ss),
  INDEX ix_lh_notice_type_region (upp_ais_tp_cd, ais_tp_cd, cnp_cd_nm)
);
```

## Upsert 기준

- PK는 `PAN_ID` 단일 컬럼으로 둔다.
- API 응답에 같은 `PAN_ID`가 이미 있으면 Update, 없으면 Insert 한다.
- 공고명, 상태, 마감일, URL은 정정공고나 상태 변경으로 바뀔 수 있으므로 매 동기화마다 갱신한다.

## 코드값 관리

공고유형, 지역, 공고상태는 별도 코드 테이블로 관리하는 것을 권장한다.

```sql
CREATE TABLE lh_code (
  code_group VARCHAR(50) NOT NULL,
  code VARCHAR(30) NOT NULL,
  code_name VARCHAR(100) NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  use_yn CHAR(1) NOT NULL DEFAULT 'Y',
  PRIMARY KEY (code_group, code)
);
```

초기 `code_group` 예시는 다음과 같다.

- `UPP_AIS_TP_CD`: `01` 토지, `05` 분양주택, `06` 임대주택, `13` 주거복지, `22` 상가, `39` 신혼희망타운
- `CNP_CD`: `11` 서울특별시, `41` 경기도 등 지역코드
- `PAN_SS`: 공고중, 접수중, 접수마감, 상담요청, 정정공고중

## DB 전환 시 코드 변경 지점

- 현재 저장소 인터페이스: `com.kgict.myhome.lh.repository.LhNoticeStore`
- 현재 메모리 구현체: `InMemoryLhNoticeStore`
- DB 전환 시 `DbLhNoticeStore`를 새로 만들고 `@Repository`를 그 구현체에 붙인다.
- 컨트롤러, 스케줄러, API 클라이언트는 그대로 유지한다.
