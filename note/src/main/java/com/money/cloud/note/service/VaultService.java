package com.money.cloud.note.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.money.cloud.common.context.UserContext;
import com.money.cloud.common.exception.BusinessException;
import com.money.cloud.note.dto.VaultItemRequest;
import com.money.cloud.note.dto.VaultItemResponse;
import com.money.cloud.note.entity.VaultItem;
import com.money.cloud.note.mapper.VaultItemMapper;
import com.money.cloud.note.util.VaultCryptoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VaultService {

    private final VaultItemMapper vaultItemMapper;
    private final VaultCryptoUtil vaultCryptoUtil;

    @Transactional
    public VaultItemResponse create(VaultItemRequest request) {
        VaultItem item = new VaultItem();
        item.setUserId(UserContext.requireUserId());
        applyRequest(item, request);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        vaultItemMapper.insert(item);
        return toResponse(item);
    }

    @Transactional
    public VaultItemResponse update(Long id, VaultItemRequest request) {
        VaultItem item = getOwnedItem(id);
        applyRequest(item, request);
        item.setUpdatedAt(LocalDateTime.now());
        vaultItemMapper.updateById(item);
        return toResponse(item);
    }

    @Transactional
    public void delete(Long id) {
        vaultItemMapper.deleteById(getOwnedItem(id).getId());
    }

    public VaultItemResponse getById(Long id) {
        return toResponse(getOwnedItem(id));
    }

    public List<VaultItemResponse> list(String keyword, String category) {
        LambdaQueryWrapper<VaultItem> wrapper = new LambdaQueryWrapper<VaultItem>()
                .eq(VaultItem::getUserId, UserContext.requireUserId())
                .orderByDesc(VaultItem::getUpdatedAt, VaultItem::getId);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(q -> q.like(VaultItem::getTitle, keyword)
                    .or().like(VaultItem::getUsername, keyword)
                    .or().like(VaultItem::getWebsite, keyword)
                    .or().like(VaultItem::getRemark, keyword));
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(VaultItem::getCategory, category);
        }
        return vaultItemMapper.selectList(wrapper).stream().map(this::toResponse).toList();
    }

    private void applyRequest(VaultItem item, VaultItemRequest request) {
        item.setTitle(request.getTitle());
        item.setCategory(trimToNull(request.getCategory()));
        item.setUsername(trimToNull(request.getUsername()));
        item.setPasswordEncrypted(vaultCryptoUtil.encrypt(request.getPassword()));
        item.setWebsite(trimToNull(request.getWebsite()));
        item.setRemark(trimToNull(request.getRemark()));
    }

    private VaultItemResponse toResponse(VaultItem item) {
        return VaultItemResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .category(item.getCategory())
                .username(item.getUsername())
                .password(vaultCryptoUtil.decrypt(item.getPasswordEncrypted()))
                .website(item.getWebsite())
                .remark(item.getRemark())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private VaultItem getOwnedItem(Long id) {
        VaultItem item = vaultItemMapper.selectOne(new LambdaQueryWrapper<VaultItem>()
                .eq(VaultItem::getId, id)
                .eq(VaultItem::getUserId, UserContext.requireUserId())
                .last("limit 1"));
        if (item == null) {
            throw new BusinessException(404, "保险箱记录不存在");
        }
        return item;
    }
}
