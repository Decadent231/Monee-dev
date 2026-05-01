package com.money.cloud.note.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.money.cloud.common.context.UserContext;
import com.money.cloud.note.dto.GlobalSearchResult;
import com.money.cloud.note.dto.GlobalSearchResult.Item;
import com.money.cloud.note.entity.Note;
import com.money.cloud.note.entity.TodoItem;
import com.money.cloud.note.entity.VaultItem;
import com.money.cloud.note.mapper.NoteMapper;
import com.money.cloud.note.mapper.TodoItemMapper;
import com.money.cloud.note.mapper.VaultItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GlobalSearchService {

    private final NoteMapper noteMapper;
    private final VaultItemMapper vaultItemMapper;
    private final TodoItemMapper todoItemMapper;

    private static final int LIMIT = 6;

    public GlobalSearchResult search(String keyword) {
        GlobalSearchResult result = new GlobalSearchResult();
        if (!StringUtils.hasText(keyword)) {
            result.setNotes(new ArrayList<>());
            result.setVaultItems(new ArrayList<>());
            result.setTodos(new ArrayList<>());
            return result;
        }

        Long userId = UserContext.requireUserId();
        String like = "%" + keyword.trim() + "%";

        List<Note> notes = noteMapper.selectList(new LambdaQueryWrapper<Note>()
                .eq(Note::getUserId, userId)
                .eq(Note::getDeleted, 0)
                .and(q -> q.like(Note::getTitle, keyword)
                        .or().like(Note::getSummary, keyword)
                        .or().like(Note::getCategory, keyword)
                        .or().like(Note::getTags, keyword))
                .orderByDesc(Note::getUpdatedAt)
                .last("LIMIT " + LIMIT));

        List<VaultItem> vaultItems = vaultItemMapper.selectList(new LambdaQueryWrapper<VaultItem>()
                .eq(VaultItem::getUserId, userId)
                .and(q -> q.like(VaultItem::getTitle, keyword)
                        .or().like(VaultItem::getUsername, keyword)
                        .or().like(VaultItem::getCategory, keyword)
                        .or().like(VaultItem::getWebsite, keyword)
                        .or().like(VaultItem::getRemark, keyword))
                .orderByDesc(VaultItem::getUpdatedAt)
                .last("LIMIT " + LIMIT));

        List<TodoItem> todos = todoItemMapper.selectList(new LambdaQueryWrapper<TodoItem>()
                .eq(TodoItem::getUserId, userId)
                .and(q -> q.like(TodoItem::getTitle, keyword)
                        .or().like(TodoItem::getDescription, keyword)
                        .or().like(TodoItem::getPriority, keyword))
                .orderByDesc(TodoItem::getUpdatedAt)
                .last("LIMIT " + LIMIT));

        result.setNotes(notes.stream()
                .map(n -> new Item(n.getId(), n.getTitle(), truncate(n.getSummary() != null ? n.getSummary() : (n.getCategory() != null ? n.getCategory() : ""), 60), "note"))
                .toList());

        result.setVaultItems(vaultItems.stream()
                .map(v -> new Item(v.getId(), v.getTitle(), truncate(v.getCategory() != null ? v.getCategory() : (v.getWebsite() != null ? v.getWebsite() : ""), 60), "vault"))
                .toList());

        result.setTodos(todos.stream()
                .map(t -> new Item(t.getId(), t.getTitle(), truncate(t.getDescription() != null ? t.getDescription() : (t.getPriority() != null ? t.getPriority() : ""), 60), "todo"))
                .toList());

        return result;
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max) + "..." : s;
    }
}
