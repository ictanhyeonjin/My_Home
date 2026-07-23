<script setup>
import { computed, onMounted, ref, watch } from "vue";

const notices = ref([]);
const codes = ref({ noticeTypes: [], regions: [], noticeStatuses: [] });
const filters = ref({ uppAisTpCd: "", regionName: "", panSs: "" });
const status = ref(null);
const loading = ref(false);
const syncing = ref(false);
const detailLoading = ref(false);
const errorMessage = ref("");
const detailMessage = ref("");
const selectedNotice = ref(null);
const selectedDetail = ref(null);
const detailDrawerOpen = ref(false);
const failedImageUrls = ref(new Set());

// ── 섹션 순서 (API 문서 기반) ──────────────────────────────────────
const preferredSections = [
  "dsCtrtPlc", "dsSplScdl", "dsAhflInfo",
  "dsSbd", "dsSbdDong", "dsSbdAhfl", "dsSbdDongAhfl",
  "dsEtcInfo", "dsEtcList"
];

const sectionNames = {
  dsCtrtPlc:    "📍 접수처 정보",
  dsSplScdl:    "📅 공급 일정",
  dsAhflInfo:   "📎 공고 첨부파일",
  dsSbd:        "🏢 단지 정보",
  dsSbdDong:    "🏬 상가동 정보",
  dsSbdAhfl:    "🖼️ 단지 첨부파일",
  dsSbdDongAhfl:"🖼️ 상가동 이미지",
  dsEtcInfo:    "📋 기타 안내",
  dsEtcList:    "📑 신청자격 및 안내"
};

const labelSectionNames = {
  dsCtrtPlc:    "dsCtrtPlcNm",
  dsSplScdl:    "dsSplScdlNm",
  dsAhflInfo:   "dsAhflInfoNm",
  dsSbd:        "dsSbdNm",
  dsSbdDong:    "dsSbdDongNm",
  dsSbdAhfl:    "dsSbdAhflNm",
  dsSbdDongAhfl:"dsSbdDongAhflNm",
  dsEtcInfo:    "dsEtcInfoNm",
  dsEtcList:    "dsEtcListNm"
};

// ── 컬럼 라벨 전체 매핑 (API 문서 기반) ───────────────────────────
const fallbackColumnLabels = {
  // 공통
  PAN_ID: "공고ID", PAN_NM: "공고명", PAN_SS: "공고상태", PAN_DT: "공고일",
  PAN_NT_ST_DT: "게시일", CLSG_DT: "마감일", SPL_INF_TP_CD: "공급정보구분",
  CCR_CNNT_SYS_DS_CD: "고객센터시스템", UPP_AIS_TP_CD: "상위공고유형",
  UPP_AIS_TP_NM: "상위공고유형명", AIS_TP_CD: "공고유형", AIS_TP_CD_NM: "공고유형명",
  CNP_CD_NM: "지역", DDO_AR: "전용면적",
  SBSC_ACP_ST_DT: "접수시작일", SBSC_ACP_CLSG_DT: "접수마감일",
  ACP_DTTM: "신청일시", PZWR_ANC_DT: "당첨자발표일",
  PPR_ACP_ST_DT: "서류접수시작일", PPR_ACP_CLSG_DT: "서류접수마감일",
  // dsCtrtPlc - 접수처
  CTRT_PLC_ADR: "접수처 주소", CTRT_PLC_DTL_ADR: "접수처 상세주소",
  SIL_OFC_TLNO: "전화번호", SIL_OFC_OPEN_DT: "운영 시작일",
  SIL_OFC_BCLS_DT: "운영 종료일", SIL_OFC_DT: "운영기간",
  TSK_SCD_CTS: "일정내용", SIL_OFC_GUD_FCTS: "안내사항",
  // dsSplScdl - 공급 일정
  ACP_ST_DTTM: "신청 시작", ACP_ED_DTTM: "신청 종료",
  RNK: "순위", RQS_DTTM: "신청일시", RQS_SCD: "신청일정", RQS_HR: "신청시간",
  CLSG_DTTM: "예약금 마감", LTR_DTTM: "추첨일정", PZWR_NT_DTTM: "당첨자 발표",
  GMY_PYM_CLSG_SCD: "입찰보증금 마감", GMY_PYM_CLSG_HR: "입찰보증금 마감시간",
  OPB_ST_SCD: "개찰일정", OPB_ST_HR: "개찰시간",
  OPB_ED_SCD: "개찰결과 게시일정", OPB_ED_HR: "개찰결과 게시시간",
  CTRT_ST_DT: "계약 시작일", CTRT_ED_DT: "계약 종료일",
  // dsAhflInfo - 첨부파일
  SL_PAN_AHFL_DS_CD_NM: "파일구분", CMN_AHFL_NM: "파일명", AHFL_URL: "다운로드",
  // dsEtcInfo - 기타
  PAN_DTL_CTS: "공고내용", CRC_RSN: "정정/취소사유",
  ETC_CTS2: "신청자격 안내", TRG_HS_CTS: "대상주택",
  EPZ_TRG_ARA: "사업대상지역", LSTR_CTS: "임대기간", LSC_CTS: "임대조건",
  // dsSbd - 단지
  SBD_NM: "단지명", BZDT_NM: "단지명(상가)", SBD_INF_CTS: "단지정보",
  LCT_ARA_ADR: "단지 주소", LCT_ARA_DTL_ADR: "단지 상세주소",
  SC_AR: "공급면적", HSH_CNT: "세대수", HTN_FMLA_DS_CD_NM: "난방방식",
  MVIN_XPC_YM: "입주예정", MSH_XPC_CTS: "입점시기",
  // dsSbdDong - 상가동
  SST_NM: "상가명", SSDH_SL_ADM_NO_CNT: "전체 호수",
  SST_AR: "상가면적", MSH_PSB_YM: "입점시기",
  // dsSbdAhfl / dsSbdDongAhfl - 단지/상가 첨부
  LS_SPL_INF_UPL_FL_DS_CD_NM: "파일구분",
  // dsEtcList - 안내 목록
  PAN_ETC_INF_CD_NM: "안내항목", ETC_CTS: "안내내용",
  // 기타 레거시
  LCC_NT_NM: "단지명", SBD_LGO_NM: "단지명", LGDN_ADR: "단지주소", LGDN_DTL_ADR: "상세주소",
};

// ── 섹션 타입 구분 ──────────────────────────────────────────────────
const FILE_SECTIONS = new Set(["dsAhflInfo", "dsSbdAhfl", "dsSbdDongAhfl"]);

function sectionType(key) {
  if (key === "dsEtcList") return "etc-list";
  if (FILE_SECTIONS.has(key)) return "file-list";
  return "kv";
}

// ── Computed ────────────────────────────────────────────────────────
const filteredNotices = computed(() => {
  return notices.value.filter((n) => {
    const okType   = !filters.value.uppAisTpCd || n.UPP_AIS_TP_CD === filters.value.uppAisTpCd;
    const okRegion = !filters.value.regionName  || n.CNP_CD_NM === filters.value.regionName;
    const okStatus = !filters.value.panSs       || n.PAN_SS === filters.value.panSs;
    return okType && okRegion && okStatus;
  });
});

const detailSections = computed(() => {
  const sections = selectedDetail.value?.sections || {};
  const SKIP = new Set(["dsSch", "resHeader"]);
  const keys = Object.keys(sections).filter(
    (k) => !SKIP.has(k) && !k.endsWith("Nm") && Array.isArray(sections[k]) && sections[k].length > 0
  );
  return keys.sort((a, b) => sectionOrder(a) - sectionOrder(b));
});

// ── API ──────────────────────────────────────────────────────────────
async function fetchJson(url, options) {
  const r = await fetch(url, options);
  if (!r.ok) throw new Error(`${r.status} ${r.statusText}`);
  return r.json();
}

async function loadDashboard() {
  loading.value = true;
  errorMessage.value = "";
  try {
    const [s, n, c] = await Promise.all([
      fetchJson("/api/lh/notices/status"),
      fetchJson("/api/lh/notices"),
      fetchJson("/api/lh/codes")
    ]);
    status.value = s;
    notices.value = n;
    codes.value = c;
  } catch {
    errorMessage.value = "데이터를 불러오지 못했습니다. 서버 연결을 확인해 주세요.";
  } finally {
    loading.value = false;
  }
}

async function syncNow() {
  syncing.value = true;
  errorMessage.value = "";
  try {
    await fetchJson("/api/lh/notices/sync", { method: "POST" });
    selectedNotice.value = null;
    selectedDetail.value = null;
    detailDrawerOpen.value = false;
    await loadDashboard();
  } catch {
    errorMessage.value = "동기화 실패. 서비스키와 네트워크를 확인해 주세요.";
  } finally {
    syncing.value = false;
  }
}

async function openDetail(notice) {
  selectedNotice.value = notice;
  selectedDetail.value = null;
  detailMessage.value = "";
  detailDrawerOpen.value = true;
  document.body.style.overflow = "hidden";

  if (!notice?.PAN_ID) return;
  detailLoading.value = true;
  try {
    selectedDetail.value = await fetchJson(`/api/lh/notices/${encodeURIComponent(notice.PAN_ID)}/supply-info`);
  } catch {
    detailMessage.value = "상세 정보를 불러오지 못했습니다.";
  } finally {
    detailLoading.value = false;
  }
}

function closeDetail() {
  detailDrawerOpen.value = false;
  document.body.style.overflow = "";
}

// ── Helper functions ─────────────────────────────────────────────────
function sectionOrder(k) {
  const i = preferredSections.indexOf(k);
  return i === -1 ? 999 : i;
}

function sectionTitle(k) {
  return sectionNames[k] || k;
}

function labelMapFor(k) {
  const labelSection = labelSectionNames[k];
  return selectedDetail.value?.sections?.[labelSection]?.[0] || {};
}

function columnLabel(sectionKey, col) {
  return labelMapFor(sectionKey)[col] || fallbackColumnLabels[col] || col;
}

function columnsFor(rows) {
  const keys = [];
  rows.forEach((row) => Object.keys(row).forEach((k) => { if (!keys.includes(k)) keys.push(k); }));
  return keys;
}

function isUrl(value) {
  return typeof value === "string" && /^https?:\/\//.test(value);
}

// 이미지 URL 여부 판단
function isImageUrl(value) {
  if (typeof value !== "string") return false;
  return /\.(jpg|jpeg|png|gif|bmp|webp)(\?.*)?$/i.test(value.split("?")[0])
    || /\/lhImageView\d*\.do(?:\?|$)/i.test(value);
}

function isWrappedLhImageUrl(value) {
  return typeof value === "string" && /\/lhImageView\d*\.do(?:\?|$)/i.test(value);
}

function previewImageUrl(value) {
  if (!isWrappedLhImageUrl(value)) return value;
  const fileId = new URL(value).searchParams.get("fileid");
  return fileId ? `/api/lh/images/${encodeURIComponent(fileId)}` : value;
}

function canPreviewImage(sectionKey, row) {
  const url = row?.AHFL_URL;
  if (!url || failedImageUrls.value.has(url)) return false;
  return sectionKey === "dsSbdAhfl"
    || sectionKey === "dsSbdDongAhfl"
    || isImageUrl(url);
}

function markImageFailed(url) {
  failedImageUrls.value = new Set([...failedImageUrls.value, url]);
}

// 파일 확장자 추출
function fileExt(url) {
  if (!url) return "LINK";
  const m = url.split("?")[0].match(/\.([^./]+)$/);
  return m ? m[1].toUpperCase() : "LINK";
}

// 파일 구분명 (여러 필드 중 존재하는 것)
function fileLabel(row) {
  return row.SL_PAN_AHFL_DS_CD_NM || row.LS_SPL_INF_UPL_FL_DS_CD_NM || "";
}

function displayValue(col, value) {
  if (value === null || value === undefined || value === "") return "-";
  return value;
}

function stateChipClass(s) {
  if (!s) return "state-chip--default";
  if (s.includes("접수") || s.includes("진행")) return "state-chip--open";
  if (s.includes("마감") || s.includes("종료")) return "state-chip--closed";
  return "state-chip--default";
}

const activeImageIndexes = ref({});
const fullscreenImageUrl = ref(null);

watch(selectedDetail, () => {
  activeImageIndexes.value = {};
  fullscreenImageUrl.value = null;
});

function getImagesForSection(sectionKey) {
  const rows = selectedDetail.value?.sections?.[sectionKey] || [];
  return rows.filter(row => canPreviewImage(sectionKey, row));
}

function getNonImagesForSection(sectionKey) {
  const rows = selectedDetail.value?.sections?.[sectionKey] || [];
  return rows.filter(row => !canPreviewImage(sectionKey, row));
}

function currentImageIndex(sectionKey) {
  return activeImageIndexes.value[sectionKey] || 0;
}

function prevImage(sectionKey) {
  const images = getImagesForSection(sectionKey);
  if (images.length <= 1) return;
  const curr = currentImageIndex(sectionKey);
  activeImageIndexes.value[sectionKey] = (curr - 1 + images.length) % images.length;
}

function nextImage(sectionKey) {
  const images = getImagesForSection(sectionKey);
  if (images.length <= 1) return;
  const curr = currentImageIndex(sectionKey);
  activeImageIndexes.value[sectionKey] = (curr + 1) % images.length;
}

function openFullscreen(url) {
  fullscreenImageUrl.value = url;
}

function closeFullscreen() {
  fullscreenImageUrl.value = null;
}

function stripExtension(filename) {
  if (!filename) return "";
  return filename.replace(/\.[^/.]+$/, "");
}

const currentTab = ref("list"); // "list" or "mypage"
const favoriteIds = ref(new Set());
const favoriteNotices = ref([]);
const favoritesLoading = ref(false);

async function loadFavorites() {
  try {
    const ids = await fetchJson("/api/lh/favorites/ids");
    favoriteIds.value = new Set(ids);
  } catch (err) {
    console.error("Failed to load favorite IDs", err);
  }
}

async function loadFavoriteNotices() {
  favoritesLoading.value = true;
  try {
    const noticesList = await fetchJson("/api/lh/favorites");
    favoriteNotices.value = noticesList;
  } catch (err) {
    console.error("Failed to load favorite notices", err);
  } finally {
    favoritesLoading.value = false;
  }
}

async function toggleFavorite(notice) {
  const panId = notice?.PAN_ID;
  if (!panId) return;
  
  const isFav = favoriteIds.value.has(panId);
  try {
    if (isFav) {
      await fetch("/api/lh/favorites/" + encodeURIComponent(panId), { method: "DELETE" });
      favoriteIds.value.delete(panId);
      favoriteIds.value = new Set(favoriteIds.value);
    } else {
      await fetch("/api/lh/favorites/" + encodeURIComponent(panId), { method: "POST" });
      favoriteIds.value.add(panId);
      favoriteIds.value = new Set(favoriteIds.value);
    }
    // If we are currently on mypage tab, reload favorite notices
    if (currentTab.value === 'mypage') {
      await loadFavoriteNotices();
    }
  } catch (err) {
    console.error("Failed to toggle favorite", err);
  }
}

watch(currentTab, (newTab) => {
  if (newTab === "mypage") {
    loadFavoriteNotices();
  }
});

onMounted(async () => {
  await loadDashboard();
  await loadFavorites();
});

</script>

<template>
  <div class="app-shell">

    <!-- ── Header ── -->
    <header class="toolbar">
      <div class="toolbar-brand">
        <div class="brand-icon">🏠</div>
        <div class="toolbar-title">
          <p class="eyebrow">LH OpenAPI</p>
          <h1>청약 공고</h1>
        </div>
      </div>
      <div class="toolbar-actions">
        <button class="secondary-button" type="button" @click="loadDashboard" :disabled="loading || syncing">
          ↺
        </button>
        <button class="primary-button" type="button" @click="syncNow" :disabled="loading || syncing">
          {{ syncing ? "동기화 중…" : "동기화" }}
        </button>
      </div>
    </header>

    <!-- ── Main ── -->
    <main class="main-content">

      <!-- ── 공고 목록 탭 ── -->
      <div v-if="currentTab === 'list'" style="display: flex; flex-direction: column; gap: 14px;">
        <!-- 통계 카드 -->
        <div class="status-grid">
          <div class="status-tile">
            <span>상태</span>
            <strong>{{ status?.lastResultCode === "Y" ? "✓ 완료" : status ? "대기" : "—" }}</strong>
          </div>
          <div class="status-tile">
            <span>전체</span>
            <strong>{{ notices.length }}</strong>
          </div>
          <div class="status-tile">
            <span>검색</span>
            <strong>{{ filteredNotices.length }}</strong>
          </div>
          <div class="status-tile">
            <span>스케줄</span>
            <strong>{{ status?.schedulerEnabled ? "ON" : "—" }}</strong>
          </div>
        </div>

        <!-- 필터 -->
        <div class="filter-bar">
          <label>
            <span>공고유형</span>
            <select v-model="filters.uppAisTpCd">
              <option value="">전체</option>
              <option v-for="c in codes.noticeTypes" :key="c.code" :value="c.code">{{ c.name }}</option>
            </select>
          </label>
          <label>
            <span>지역</span>
            <select v-model="filters.regionName">
              <option value="">전체</option>
              <option v-for="c in codes.regions" :key="c.code" :value="c.name">{{ c.name }}</option>
            </select>
          </label>
          <label>
            <span>상태</span>
            <select v-model="filters.panSs">
              <option value="">전체</option>
              <option v-for="c in codes.noticeStatuses" :key="c.code" :value="c.code">{{ c.name }}</option>
            </select>
          </label>
        </div>

        <!-- 에러 -->
        <p v-if="errorMessage" class="error-message">⚠️ {{ errorMessage }}</p>

        <!-- 로딩 -->
        <div v-if="loading" class="loading-overlay">
          <div class="spinner"></div>
          <span>공고 목록을 불러오는 중…</span>
        </div>

        <!-- 결과 바 -->
        <div v-else-if="filteredNotices.length > 0" class="result-bar">
          <span>공고 목록</span>
          <span class="result-count">{{ filteredNotices.length }}건</span>
        </div>

        <!-- 공고 카드 목록 -->
        <div v-if="!loading" class="notice-list">

          <!-- 빈 상태 -->
          <div v-if="filteredNotices.length === 0" class="empty-state">
            <div class="empty-state-icon">🏗️</div>
            <p class="empty-state-title">공고가 없습니다</p>
            <p class="empty-state-desc">동기화 버튼을 눌러 최신 공고를 가져오세요.</p>
          </div>

          <!-- 카드 -->
          <div
            v-for="notice in filteredNotices"
            :key="notice.PAN_ID"
            class="notice-card"
            :class="{ active: selectedNotice?.PAN_ID === notice.PAN_ID }"
            @click="openDetail(notice)"
          >
            <div class="card-chips">
              <span class="state-chip" :class="stateChipClass(notice.PAN_SS)">
                {{ notice.PAN_SS || "상태 미정" }}
              </span>
              <span v-if="notice.UPP_AIS_TP_NM || notice.AIS_TP_CD_NM" class="type-chip">
                {{ notice.UPP_AIS_TP_NM || notice.AIS_TP_CD_NM }}
              </span>
              <!-- 즐겨찾기 버튼 -->
              <button 
                class="card-fav-btn"
                :class="{ favorited: favoriteIds.has(notice.PAN_ID) }"
                type="button" 
                @click.stop="toggleFavorite(notice)"
                :aria-label="favoriteIds.has(notice.PAN_ID) ? '즐겨찾기 해제' : '즐겨찾기 등록'"
              >
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" class="star-icon">
                  <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                </svg>
              </button>
            </div>

            <p class="card-title">{{ notice.PAN_NM || "공고명 없음" }}</p>

            <div class="card-meta">
              <span v-if="notice.CNP_CD_NM" class="meta-item">
                <span class="meta-label">지역</span> {{ notice.CNP_CD_NM }}
              </span>
              <span v-if="notice.PAN_DT" class="meta-item">
                <span class="meta-label">공고일</span> {{ notice.PAN_DT }}
              </span>
              <span v-if="notice.CLSG_DT" class="meta-item">
                <span class="meta-label">마감일</span> {{ notice.CLSG_DT }}
              </span>
            </div>

            <span class="card-arrow">›</span>
          </div>
        </div>
      </div>

      <!-- ── 마이페이지 탭 ── -->
      <div v-else-if="currentTab === 'mypage'" class="mypage-view">
        <div class="mypage-header">
          <h2>⭐ 즐겨찾는 공고</h2>
          <span class="result-count">{{ favoriteNotices.length }}건</span>
        </div>

        <div v-if="favoritesLoading" class="loading-overlay">
          <div class="spinner"></div>
          <span>즐겨찾는 공고를 불러오는 중…</span>
        </div>

        <div v-else class="notice-list">
          <!-- 즐겨찾기 빈 상태 -->
          <div v-if="favoriteNotices.length === 0" class="empty-state">
            <div class="empty-state-icon">⭐</div>
            <p class="empty-state-title">즐겨찾는 공고가 없습니다</p>
            <p class="empty-state-desc">관심 있는 공고의 별(★)을 눌러 즐겨찾기에 추가해 보세요.</p>
          </div>

          <!-- 즐겨찾기 카드 목록 -->
          <div
            v-for="notice in favoriteNotices"
            :key="notice.PAN_ID"
            class="notice-card"
            :class="{ active: selectedNotice?.PAN_ID === notice.PAN_ID }"
            @click="openDetail(notice)"
          >
            <div class="card-chips">
              <span class="state-chip" :class="stateChipClass(notice.PAN_SS)">
                {{ notice.PAN_SS || "상태 미정" }}
              </span>
              <span v-if="notice.UPP_AIS_TP_NM || notice.AIS_TP_CD_NM" class="type-chip">
                {{ notice.UPP_AIS_TP_NM || notice.AIS_TP_CD_NM }}
              </span>
              <!-- 즐겨찾기 버튼 -->
              <button 
                class="card-fav-btn favorited"
                type="button" 
                @click.stop="toggleFavorite(notice)"
                aria-label="즐겨찾기 해제"
              >
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" class="star-icon">
                  <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                </svg>
              </button>
            </div>

            <p class="card-title">{{ notice.PAN_NM || "공고명 없음" }}</p>

            <div class="card-meta">
              <span v-if="notice.CNP_CD_NM" class="meta-item">
                <span class="meta-label">지역</span> {{ notice.CNP_CD_NM }}
              </span>
              <span v-if="notice.PAN_DT" class="meta-item">
                <span class="meta-label">공고일</span> {{ notice.PAN_DT }}
              </span>
              <span v-if="notice.CLSG_DT" class="meta-item">
                <span class="meta-label">마감일</span> {{ notice.CLSG_DT }}
              </span>
            </div>

            <span class="card-arrow">›</span>
          </div>
        </div>
      </div>

    </main>

    <!-- ── Bottom Navigation Bar ── -->
    <nav class="bottom-nav">
      <button 
        class="nav-tab" 
        :class="{ active: currentTab === 'list' }" 
        type="button" 
        @click="currentTab = 'list'"
      >
        <span class="nav-icon">📋</span>
        <span class="nav-label">공고 목록</span>
      </button>
      <button 
        class="nav-tab" 
        :class="{ active: currentTab === 'mypage' }" 
        type="button" 
        @click="currentTab = 'mypage'"
      >
        <span class="nav-icon">⭐</span>
        <span class="nav-label">마이페이지</span>
      </button>
    </nav>


    <!-- ── Backdrop ── -->
    <div v-if="detailDrawerOpen" class="drawer-backdrop" @click="closeDetail"></div>

    <!-- ── Detail Drawer (Bottom Sheet) ── -->
    <div class="detail-drawer" :class="{ open: detailDrawerOpen }" aria-live="polite">

      <!-- Handle -->
      <div class="drawer-handle">
        <div class="drawer-handle-bar"></div>
      </div>

      <!-- Header -->
      <div class="drawer-header">
        <div class="drawer-header-text">
          <p class="eyebrow">공고 상세</p>
          <h2 style="display: flex; align-items: flex-start; gap: 8px;">
            <button 
              class="drawer-fav-btn"
              :class="{ favorited: favoriteIds.has(selectedNotice?.PAN_ID) }"
              type="button" 
              @click.stop="toggleFavorite(selectedNotice)"
              :aria-label="favoriteIds.has(selectedNotice?.PAN_ID) ? '즐겨찾기 해제' : '즐겨찾기 등록'"
            >
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" class="star-icon">
                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
              </svg>
            </button>
            <span style="flex: 1; word-break: keep-all;">{{ selectedNotice?.PAN_NM || "—" }}</span>
          </h2>
        </div>
        <button class="drawer-close" type="button" @click="closeDetail" aria-label="닫기">✕</button>
      </div>


      <!-- Key info -->
      <div v-if="selectedNotice" class="drawer-key-section">
        <a
          v-if="selectedNotice.DTL_URL || selectedNotice.DTL_URL_MOB"
          :href="selectedNotice.DTL_URL || selectedNotice.DTL_URL_MOB"
          class="lh-original-link"
          target="_blank"
          rel="noopener noreferrer"
        >LH 원문 보기 ↗</a>
        <dl class="key-grid">
          <div class="key-item">
            <dt>지역</dt>
            <dd>{{ selectedNotice.CNP_CD_NM || "—" }}</dd>
          </div>
          <div class="key-item">
            <dt>상태</dt>
            <dd>{{ selectedNotice.PAN_SS || "—" }}</dd>
          </div>
          <div class="key-item">
            <dt>공고일</dt>
            <dd>{{ selectedNotice.PAN_DT || "—" }}</dd>
          </div>
          <div class="key-item">
            <dt>마감일</dt>
            <dd>{{ selectedNotice.CLSG_DT || "—" }}</dd>
          </div>
        </dl>
      </div>

      <!-- Drawer Body -->
      <div class="drawer-body">

        <!-- 로딩 -->
        <div v-if="detailLoading" class="drawer-loading">
          <div class="spinner"></div>
          <span>상세 정보 로딩 중…</span>
        </div>

        <!-- 에러 -->
        <p v-if="detailMessage" class="detail-error">⚠️ {{ detailMessage }}</p>

        <!-- 섹션들 -->
        <template v-if="selectedDetail && detailSections.length > 0">
          <div v-for="sectionKey in detailSections" :key="sectionKey" class="detail-section">
            <div class="detail-section-title">{{ sectionTitle(sectionKey) }}</div>

            <!-- ── 파일/이미지 섹션 (dsAhflInfo, dsSbdAhfl, dsSbdDongAhfl) ── -->
            <template v-if="sectionType(sectionKey) === 'file-list'">
              <!-- 이미지 슬라이더 (사진이 존재할 때만 표시) -->
              <div v-if="getImagesForSection(sectionKey).length > 0" class="image-slider-container">
                <div class="image-slider-wrapper">
                  <!-- 이전 화살표 -->
                  <button 
                    v-if="getImagesForSection(sectionKey).length > 1"
                    type="button" 
                    class="slider-arrow slider-arrow--prev" 
                    @click="prevImage(sectionKey)"
                    aria-label="이전 이미지"
                  >
                    ‹
                  </button>

                  <!-- 이미지 박스 -->
                  <div 
                    class="slider-image-box" 
                    @click="openFullscreen(previewImageUrl(getImagesForSection(sectionKey)[currentImageIndex(sectionKey)].AHFL_URL))"
                    title="클릭하여 크게 보기"
                  >
                    <img
                      :src="previewImageUrl(getImagesForSection(sectionKey)[currentImageIndex(sectionKey)].AHFL_URL)"
                      class="slider-img"
                      @error="markImageFailed(getImagesForSection(sectionKey)[currentImageIndex(sectionKey)].AHFL_URL)"
                      alt="단지 첨부 이미지"
                    />
                    <div class="slider-badge">
                      {{ currentImageIndex(sectionKey) + 1 }} / {{ getImagesForSection(sectionKey).length }}
                    </div>
                  </div>

                  <!-- 다음 화살표 -->
                  <button 
                    v-if="getImagesForSection(sectionKey).length > 1"
                    type="button" 
                    class="slider-arrow slider-arrow--next" 
                    @click="nextImage(sectionKey)"
                    aria-label="다음 이미지"
                  >
                    ›
                  </button>
                </div>

                <!-- 이미지 정보 -->
                <div class="slider-info">
                  <div class="slider-info-text">
                    <span v-if="fileLabel(getImagesForSection(sectionKey)[currentImageIndex(sectionKey)])" class="file-type">
                      {{ fileLabel(getImagesForSection(sectionKey)[currentImageIndex(sectionKey)]) }}
                    </span>
                    <span class="file-name">
                      {{ stripExtension(getImagesForSection(sectionKey)[currentImageIndex(sectionKey)].CMN_AHFL_NM) || "이미지" }}
                    </span>
                  </div>
                  <button
                    v-if="getImagesForSection(sectionKey)[currentImageIndex(sectionKey)].AHFL_URL"
                    type="button"
                    class="zoom-button"
                    @click="openFullscreen(previewImageUrl(getImagesForSection(sectionKey)[currentImageIndex(sectionKey)].AHFL_URL))"
                    title="확대 보기"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0zM10 7v6m3-3H7" />
                    </svg>
                    확대
                  </button>
                </div>
              </div>

              <!-- 일반 파일 목록 (사진이 아닌 경우만 리스트로 렌더링) -->
              <div
                v-for="(row, ri) in getNonImagesForSection(sectionKey)"
                :key="ri"
                class="file-item"
              >
                <div class="file-info">
                  <span v-if="fileLabel(row)" class="file-type">{{ fileLabel(row) }}</span>
                  <span class="file-name">{{ row.CMN_AHFL_NM || "파일" }}</span>
                  <a
                    v-if="row.AHFL_URL"
                    :href="row.AHFL_URL"
                    target="_blank"
                    rel="noreferrer"
                    class="file-link"
                  >
                    <span class="file-ext">{{ fileExt(row.AHFL_URL) }}</span>
                    다운로드 ↗
                  </a>
                </div>
              </div>
            </template>

            <!-- ── 기타 안내 목록 (dsEtcList) ── -->
            <template v-else-if="sectionType(sectionKey) === 'etc-list'">
              <div
                v-for="(row, ri) in selectedDetail.sections[sectionKey]"
                :key="ri"
                class="etc-card"
              >
                <div v-if="row.PAN_ETC_INF_CD_NM" class="etc-card-title">
                  {{ row.PAN_ETC_INF_CD_NM }}
                </div>
                <div class="etc-card-body">{{ row.ETC_CTS || "-" }}</div>
              </div>
            </template>

            <!-- ── 기본 키-값 렌더링 ── -->
            <template v-else>
              <template v-for="(row, ri) in selectedDetail.sections[sectionKey]" :key="ri">
                <!-- 다중 행이면 행 번호 구분선 -->
                <div
                  v-if="selectedDetail.sections[sectionKey].length > 1"
                  class="kv-row-divider"
                >
                  {{ ri + 1 }}
                </div>
                <!-- 키-값 행 -->
                <div
                  v-for="col in columnsFor(selectedDetail.sections[sectionKey])"
                  :key="col"
                  class="kv-row"
                >
                  <span class="kv-label">{{ columnLabel(sectionKey, col) }}</span>
                  <span class="kv-value">
                    <a
                      v-if="isUrl(row[col])"
                      class="kv-link"
                      :href="row[col]"
                      target="_blank"
                      rel="noreferrer"
                    >열기 ↗</a>
                    <span v-else class="kv-text">{{ displayValue(col, row[col]) }}</span>
                  </span>
                </div>
              </template>
            </template>

          </div>
        </template>

        <!-- 빈 상세 -->
        <div v-if="selectedDetail && detailSections.length === 0 && !detailLoading" class="empty-state">
          <div class="empty-state-icon">📄</div>
          <p class="empty-state-title">표시할 상세 정보가 없습니다</p>
        </div>

      </div>
    </div>

    <!-- ── Fullscreen Image Modal ── -->
    <div v-if="fullscreenImageUrl" class="fullscreen-modal" @click="closeFullscreen">
      <button class="fullscreen-close" type="button" @click="closeFullscreen" aria-label="닫기">✕</button>
      <div class="fullscreen-content">
        <img :src="fullscreenImageUrl" class="fullscreen-img" @click.stop alt="원본 이미지" />
      </div>
    </div>

  </div>
</template>
