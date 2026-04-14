package com.money.cloud.note.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.money.cloud.common.context.UserContext;
import com.money.cloud.common.exception.BusinessException;
import com.money.cloud.note.dto.NoteCreateRequest;
import com.money.cloud.note.dto.NoteUpdateRequest;
import com.money.cloud.note.entity.Note;
import com.money.cloud.note.mapper.NoteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteMapper noteMapper;

    @Transactional
    public Note create(NoteCreateRequest request) {
        Note note = new Note();
        note.setUserId(UserContext.requireUserId());
        applyRequest(note, request);
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());
        noteMapper.insert(note);
        return note;
    }

    @Transactional
    public void delete(Long id) {
        noteMapper.deleteById(getOwnedNote(id).getId());
    }

    @Transactional
    public Note update(Long id, NoteUpdateRequest request) {
        Note note = getOwnedNote(id);
        applyRequest(note, request);
        note.setUpdatedAt(LocalDateTime.now());
        noteMapper.updateById(note);
        return note;
    }

    public Note getById(Long id) {
        return getOwnedNote(id);
    }

    public IPage<Note> page(int current, int size, String keyword, String category, String tag) {
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<Note>()
                .eq(Note::getUserId, UserContext.requireUserId())
                .orderByDesc(Note::getUpdatedAt, Note::getId);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(q -> q.like(Note::getTitle, keyword)
                    .or().like(Note::getSummary, keyword)
                    .or().like(Note::getContent, keyword));
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(Note::getCategory, category);
        }
        if (StringUtils.hasText(tag)) {
            wrapper.like(Note::getTags, tag);
        }
        return noteMapper.selectPage(new Page<>(current, size), wrapper);
    }

    private void applyRequest(Note note, NoteCreateRequest request) {
        note.setTitle(request.getTitle());
        note.setCategory(trimToNull(request.getCategory()));
        note.setTags(trimToNull(request.getTags()));
        note.setSummary(trimToNull(request.getSummary()));
        note.setContent(request.getContent());
    }

    private void applyRequest(Note note, NoteUpdateRequest request) {
        note.setTitle(request.getTitle());
        note.setCategory(trimToNull(request.getCategory()));
        note.setTags(trimToNull(request.getTags()));
        note.setSummary(trimToNull(request.getSummary()));
        note.setContent(request.getContent());
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private Note getOwnedNote(Long id) {
        Note note = noteMapper.selectOne(new LambdaQueryWrapper<Note>()
                .eq(Note::getId, id)
                .eq(Note::getUserId, UserContext.requireUserId())
                .last("limit 1"));
        if (note == null) {
            throw new BusinessException(404, "笔记不存在");
        }
        return note;
    }
}
