package com.kgict.myhome.lh.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kgict.myhome.lh.dto.LhNoticeDto;
import com.kgict.myhome.lh.repository.LhFavoriteStore;
import com.kgict.myhome.lh.repository.LhNoticeStore;

@RestController
@RequestMapping("/api/lh/favorites")
public class LhFavoriteController {
    private final LhFavoriteStore favoriteStore;
    private final LhNoticeStore noticeStore;

    public LhFavoriteController(LhFavoriteStore favoriteStore, LhNoticeStore noticeStore) {
        this.favoriteStore = favoriteStore;
        this.noticeStore = noticeStore;
    }

    @GetMapping
    public List<LhNoticeDto> getFavorites() {
        return favoriteStore.getAllNotices(noticeStore);
    }

    @GetMapping("/ids")
    public List<String> getFavoriteIds() {
        return favoriteStore.getAllIds();
    }

    @PostMapping("/{panId}")
    public ResponseEntity<Void> addFavorite(@PathVariable String panId) {
        favoriteStore.add(panId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{panId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable String panId) {
        favoriteStore.remove(panId);
        return ResponseEntity.ok().build();
    }
}
