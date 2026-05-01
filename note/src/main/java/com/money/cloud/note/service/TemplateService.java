package com.money.cloud.note.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.money.cloud.common.context.UserContext;
import com.money.cloud.common.exception.BusinessException;
import com.money.cloud.note.entity.NoteTemplate;
import com.money.cloud.note.mapper.NoteTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final NoteTemplateMapper noteTemplateMapper;

    public List<NoteTemplate> list() {
        Long userId = UserContext.requireUserId();
        return noteTemplateMapper.selectList(new LambdaQueryWrapper<NoteTemplate>()
                .eq(NoteTemplate::getIsSystem, 1)
                .or(q -> q.eq(NoteTemplate::getUserId, userId).eq(NoteTemplate::getIsSystem, 0))
                .orderByDesc(NoteTemplate::getIsSystem, NoteTemplate::getUpdatedAt));
    }

    public NoteTemplate getById(Long id) {
        NoteTemplate tpl = noteTemplateMapper.selectById(id);
        if (tpl == null) {
            throw new BusinessException(404, "模板不存在");
        }
        if (tpl.getIsSystem() != 1 && !tpl.getUserId().equals(UserContext.requireUserId())) {
            throw new BusinessException(403, "无权访问该模板");
        }
        return tpl;
    }

    @Transactional
    public NoteTemplate create(NoteTemplate template) {
        template.setId(null);
        template.setUserId(UserContext.requireUserId());
        template.setIsSystem(0);
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        noteTemplateMapper.insert(template);
        return template;
    }

    @Transactional
    public NoteTemplate update(Long id, NoteTemplate template) {
        NoteTemplate existing = noteTemplateMapper.selectById(id);
        if (existing == null || existing.getIsSystem() == 1 || !existing.getUserId().equals(UserContext.requireUserId())) {
            throw new BusinessException(404, "模板不存在或无权修改");
        }
        existing.setName(template.getName());
        if (StringUtils.hasText(template.getContentType())) {
            existing.setContentType(template.getContentType());
        }
        existing.setContent(template.getContent());
        existing.setUpdatedAt(LocalDateTime.now());
        noteTemplateMapper.updateById(existing);
        return existing;
    }

    @Transactional
    public void delete(Long id) {
        NoteTemplate existing = noteTemplateMapper.selectById(id);
        if (existing == null || existing.getIsSystem() == 1 || !existing.getUserId().equals(UserContext.requireUserId())) {
            throw new BusinessException(404, "模板不存在或无权删除");
        }
        noteTemplateMapper.deleteById(id);
    }
}
