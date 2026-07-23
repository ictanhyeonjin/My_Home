package com.kgict.myhome.lh.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.kgict.myhome.lh.dto.LhNoticeDto;

@Repository
public class InMemoryLhFavoriteStore implements LhFavoriteStore {
    private final Set<String> favoritePanIds = Collections.synchronizedSet(new LinkedHashSet<>());

    @Override
    public void add(String panId) {
        if (panId != null && !panId.isBlank()) {
            favoritePanIds.add(panId);
        }
    }

    @Override
    public void remove(String panId) {
        if (panId != null) {
            favoritePanIds.remove(panId);
        }
    }

    @Override
    public List<String> getAllIds() {
        synchronized (favoritePanIds) {
            return new ArrayList<>(favoritePanIds);
        }
    }

    @Override
    public List<LhNoticeDto> getAllNotices(LhNoticeStore noticeStore) {
        List<String> ids = getAllIds();
        List<LhNoticeDto> list = new ArrayList<>();
        for (String id : ids) {
            noticeStore.findByPanId(id).ifPresent(list::add);
        }
        return list;
    }
}
