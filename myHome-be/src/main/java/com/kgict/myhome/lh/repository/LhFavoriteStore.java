package com.kgict.myhome.lh.repository;

import java.util.List;
import com.kgict.myhome.lh.dto.LhNoticeDto;

public interface LhFavoriteStore {
    void add(String panId);
    void remove(String panId);
    List<String> getAllIds();
    List<LhNoticeDto> getAllNotices(LhNoticeStore noticeStore);
}
