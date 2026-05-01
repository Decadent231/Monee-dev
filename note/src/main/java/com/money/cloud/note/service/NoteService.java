package com.money.cloud.note.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.money.cloud.common.context.UserContext;
import com.money.cloud.common.exception.BusinessException;
import com.money.cloud.note.dto.NoteCreateRequest;
import com.money.cloud.note.dto.NoteUpdateRequest;
import com.money.cloud.note.entity.FileAsset;
import com.money.cloud.note.entity.Note;
import com.money.cloud.note.entity.NoteFile;
import com.money.cloud.note.entity.NoteTemplate;
import com.money.cloud.note.mapper.FileAssetMapper;
import com.money.cloud.note.mapper.NoteFileMapper;
import com.money.cloud.note.mapper.NoteMapper;
import com.money.cloud.note.mapper.NoteTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteMapper noteMapper;
    private final NoteTemplateMapper noteTemplateMapper;
    private final NoteFileMapper noteFileMapper;
    private final FileAssetMapper fileAssetMapper;
    private final ActivityLogService activityLogService;

    @Transactional
    public Note create(NoteCreateRequest request) {
        Note note = new Note();
        note.setUserId(UserContext.requireUserId());
        if (request.getTemplateId() != null) {
            NoteTemplate tpl = noteTemplateMapper.selectById(request.getTemplateId());
            if (tpl != null && (tpl.getIsSystem() == 1 || tpl.getUserId().equals(UserContext.requireUserId()))) {
                note.setContent(tpl.getContent());
                note.setContentType(tpl.getContentType());
            }
        }
        applyRequest(note, request);
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());
        note.setDeleted(0);
        note.setPinned(0);
        note.setStarred(0);
        noteMapper.insert(note);
        activityLogService.log("note", "create", note.getId(), note.getTitle());
        return note;
    }

    @Transactional
    public void softDelete(Long id) {
        Note note = getOwnedNote(id);
        note.setDeleted(1);
        note.setDeletedAt(LocalDateTime.now());
        noteMapper.updateById(note);
        activityLogService.log("note", "delete", id, note.getTitle());
    }

    @Transactional
    public void restore(Long id) {
        Note note = getOwnedTrashedNote(id);
        note.setDeleted(0);
        note.setDeletedAt(null);
        noteMapper.updateById(note);
        activityLogService.log("note", "restore", id, note.getTitle());
    }

    @Transactional
    public void permanentlyDelete(Long id) {
        Note note = getOwnedTrashedNote(id);
        activityLogService.log("note", "delete", id, note.getTitle());
        noteMapper.deleteById(note.getId());
    }

    @Transactional
    public void emptyTrash() {
        Long userId = UserContext.requireUserId();
        List<Note> trashed = listTrash();
        noteMapper.delete(new LambdaQueryWrapper<Note>()
                .eq(Note::getUserId, userId)
                .eq(Note::getDeleted, 1));
        if (!trashed.isEmpty()) {
            activityLogService.log("note", "delete", null, "清空回收站，共" + trashed.size() + "条");
        }
    }

    @Transactional
    public Note update(Long id, NoteUpdateRequest request) {
        Note note = getOwnedNote(id);
        applyRequest(note, request);
        note.setUpdatedAt(LocalDateTime.now());
        noteMapper.updateById(note);
        activityLogService.log("note", "update", id, note.getTitle());
        return note;
    }

    public Note getById(Long id) {
        return getOwnedNote(id);
    }

    public IPage<Note> page(int current, int size, String keyword, String category, String tag, Boolean starred) {
        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<Note>()
                .eq(Note::getUserId, UserContext.requireUserId())
                .eq(Note::getDeleted, 0)
                .orderByDesc(Note::getPinned, Note::getUpdatedAt, Note::getId);
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
        if (Boolean.TRUE.equals(starred)) {
            wrapper.eq(Note::getStarred, 1);
        }
        return noteMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Transactional
    public Note togglePin(Long id) {
        Note note = getOwnedNote(id);
        note.setPinned(note.getPinned() != null && note.getPinned() == 1 ? 0 : 1);
        noteMapper.updateById(note);
        return note;
    }

    @Transactional
    public Note toggleStar(Long id) {
        Note note = getOwnedNote(id);
        note.setStarred(note.getStarred() != null && note.getStarred() == 1 ? 0 : 1);
        noteMapper.updateById(note);
        return note;
    }

    public List<Note> listStarred() {
        return noteMapper.selectList(new LambdaQueryWrapper<Note>()
                .eq(Note::getUserId, UserContext.requireUserId())
                .eq(Note::getDeleted, 0)
                .eq(Note::getStarred, 1)
                .orderByDesc(Note::getUpdatedAt));
    }

    public List<Note> listTrash() {
        return noteMapper.selectList(new LambdaQueryWrapper<Note>()
                .eq(Note::getUserId, UserContext.requireUserId())
                .eq(Note::getDeleted, 1)
                .orderByDesc(Note::getDeletedAt));
    }

    @Transactional
    public void linkFiles(Long noteId, List<Long> fileIds) {
        getOwnedNote(noteId);
        Long userId = UserContext.requireUserId();
        for (Long fileId : fileIds) {
            FileAsset file = fileAssetMapper.selectById(fileId);
            if (file == null || !file.getUserId().equals(userId)) {
                throw new BusinessException(404, "文件不存在: " + fileId);
            }
            boolean exists = noteFileMapper.selectCount(new LambdaQueryWrapper<NoteFile>()
                    .eq(NoteFile::getNoteId, noteId)
                    .eq(NoteFile::getFileId, fileId)) > 0;
            if (!exists) {
                NoteFile nf = new NoteFile();
                nf.setNoteId(noteId);
                nf.setFileId(fileId);
                nf.setCreatedAt(LocalDateTime.now());
                noteFileMapper.insert(nf);
            }
        }
    }

    @Transactional
    public void unlinkFile(Long noteId, Long fileId) {
        getOwnedNote(noteId);
        noteFileMapper.delete(new LambdaQueryWrapper<NoteFile>()
                .eq(NoteFile::getNoteId, noteId)
                .eq(NoteFile::getFileId, fileId));
    }

    public List<FileAsset> listLinkedFiles(Long noteId) {
        getOwnedNote(noteId);
        List<NoteFile> links = noteFileMapper.selectList(new LambdaQueryWrapper<NoteFile>()
                .eq(NoteFile::getNoteId, noteId));
        if (links.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> fileIds = links.stream().map(NoteFile::getFileId).collect(Collectors.toList());
        return fileAssetMapper.selectBatchIds(fileIds);
    }

    public Map<Long, Long> countLinkedFilesForNotes(List<Long> noteIds) {
        if (noteIds == null || noteIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<NoteFile> links = noteFileMapper.selectList(new LambdaQueryWrapper<NoteFile>()
                .in(NoteFile::getNoteId, noteIds));
        Map<Long, Long> result = new HashMap<>();
        for (NoteFile nf : links) {
            result.merge(nf.getNoteId(), 1L, Long::sum);
        }
        return result;
    }

    public List<Note> listByFileId(Long fileId) {
        List<NoteFile> links = noteFileMapper.selectList(new LambdaQueryWrapper<NoteFile>()
                .eq(NoteFile::getFileId, fileId));
        if (links.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> noteIds = links.stream().map(NoteFile::getNoteId).collect(Collectors.toList());
        return noteMapper.selectBatchIds(noteIds);
    }

    private void applyRequest(Note note, NoteCreateRequest request) {
        note.setTitle(request.getTitle());
        note.setCategory(trimToNull(request.getCategory()));
        note.setTags(trimToNull(request.getTags()));
        note.setSummary(trimToNull(request.getSummary()));
        note.setContent(request.getContent());
        if (StringUtils.hasText(request.getContentType())) {
            note.setContentType(request.getContentType());
        } else if (!StringUtils.hasText(note.getContentType())) {
            note.setContentType("html");
        }
    }

    private void applyRequest(Note note, NoteUpdateRequest request) {
        note.setTitle(request.getTitle());
        note.setCategory(trimToNull(request.getCategory()));
        note.setTags(trimToNull(request.getTags()));
        note.setSummary(trimToNull(request.getSummary()));
        note.setContent(request.getContent());
        if (StringUtils.hasText(request.getContentType())) {
            note.setContentType(request.getContentType());
        }
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private Note getOwnedNote(Long id) {
        Note note = noteMapper.selectOne(new LambdaQueryWrapper<Note>()
                .eq(Note::getId, id)
                .eq(Note::getUserId, UserContext.requireUserId())
                .eq(Note::getDeleted, 0)
                .last("limit 1"));
        if (note == null) {
            throw new BusinessException(404, "笔记不存在");
        }
        return note;
    }

    private Note getOwnedTrashedNote(Long id) {
        Note note = noteMapper.selectOne(new LambdaQueryWrapper<Note>()
                .eq(Note::getId, id)
                .eq(Note::getUserId, UserContext.requireUserId())
                .eq(Note::getDeleted, 1)
                .last("limit 1"));
        if (note == null) {
            throw new BusinessException(404, "回收站中不存在该笔记");
        }
        return note;
    }
}
