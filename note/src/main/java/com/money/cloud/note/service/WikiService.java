package com.money.cloud.note.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.money.cloud.common.context.UserContext;
import com.money.cloud.common.exception.BusinessException;
import com.money.cloud.note.entity.WikiPage;
import com.money.cloud.note.entity.WikiSpace;
import com.money.cloud.note.mapper.WikiPageMapper;
import com.money.cloud.note.mapper.WikiSpaceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WikiService {

    private final WikiSpaceMapper wikiSpaceMapper;
    private final WikiPageMapper wikiPageMapper;
    private final ActivityLogService activityLogService;

    // ---- Space ----

    @Transactional
    public WikiSpace createSpace(WikiSpace space) {
        space.setId(null);
        space.setUserId(UserContext.requireUserId());
        if (!StringUtils.hasText(space.getIcon())) space.setIcon("📋");
        space.setCreatedAt(LocalDateTime.now());
        space.setUpdatedAt(LocalDateTime.now());
        wikiSpaceMapper.insert(space);
        activityLogService.log("wiki", "create", space.getId(), space.getName());
        return space;
    }

    @Transactional
    public WikiSpace updateSpace(Long id, WikiSpace space) {
        WikiSpace existing = getOwnedSpace(id);
        existing.setName(space.getName());
        existing.setDescription(space.getDescription());
        if (StringUtils.hasText(space.getIcon())) existing.setIcon(space.getIcon());
        existing.setUpdatedAt(LocalDateTime.now());
        wikiSpaceMapper.updateById(existing);
        activityLogService.log("wiki", "update", id, existing.getName());
        return existing;
    }

    @Transactional
    public void deleteSpace(Long id) {
        WikiSpace space = getOwnedSpace(id);
        Long userId = UserContext.requireUserId();
        wikiPageMapper.delete(new LambdaQueryWrapper<WikiPage>()
                .eq(WikiPage::getSpaceId, id)
                .eq(WikiPage::getUserId, userId));
        wikiSpaceMapper.deleteById(id);
        activityLogService.log("wiki", "delete", id, space.getName());
    }

    public List<WikiSpace> listSpaces() {
        return wikiSpaceMapper.selectList(new LambdaQueryWrapper<WikiSpace>()
                .eq(WikiSpace::getUserId, UserContext.requireUserId())
                .orderByDesc(WikiSpace::getUpdatedAt));
    }

    public WikiSpace getSpaceById(Long id) {
        return getOwnedSpace(id);
    }

    // ---- Page ----

    @Transactional
    public WikiPage createPage(WikiPage page) {
        page.setId(null);
        page.setUserId(UserContext.requireUserId());
        WikiSpace space = getOwnedSpace(page.getSpaceId());
        if (page.getSortOrder() == null) page.setSortOrder(0);
        if (!StringUtils.hasText(page.getContentType())) page.setContentType("markdown");
        page.setCreatedAt(LocalDateTime.now());
        page.setUpdatedAt(LocalDateTime.now());
        wikiPageMapper.insert(page);
        activityLogService.log("wiki", "create", page.getId(), page.getTitle());
        return page;
    }

    @Transactional
    public WikiPage updatePage(Long id, WikiPage page) {
        WikiPage existing = getOwnedPage(id);
        if (StringUtils.hasText(page.getTitle())) existing.setTitle(page.getTitle());
        if (page.getContent() != null) existing.setContent(page.getContent());
        if (StringUtils.hasText(page.getContentType())) existing.setContentType(page.getContentType());
        if (page.getParentId() != null) existing.setParentId(page.getParentId());
        if (page.getSortOrder() != null) existing.setSortOrder(page.getSortOrder());
        existing.setUpdatedAt(LocalDateTime.now());
        wikiPageMapper.updateById(existing);
        activityLogService.log("wiki", "update", id, existing.getTitle());
        return existing;
    }

    @Transactional
    public void deletePage(Long id) {
        WikiPage page = getOwnedPage(id);
        deletePageRecursive(id, page.getUserId());
        activityLogService.log("wiki", "delete", id, page.getTitle());
    }

    public WikiPage getPageById(Long id) {
        return getOwnedPage(id);
    }

    public List<WikiPage> listPagesBySpace(Long spaceId) {
        Long userId = UserContext.requireUserId();
        getOwnedSpace(spaceId);
        return wikiPageMapper.selectList(new LambdaQueryWrapper<WikiPage>()
                .eq(WikiPage::getSpaceId, spaceId)
                .eq(WikiPage::getUserId, userId)
                .orderByAsc(WikiPage::getSortOrder)
                .orderByAsc(WikiPage::getCreatedAt));
    }

    public List<Map<String, Object>> getPageTree(Long spaceId) {
        List<WikiPage> pages = listPagesBySpace(spaceId);
        Map<Long, List<WikiPage>> childrenMap = new HashMap<>();
        List<WikiPage> roots = new ArrayList<>();

        for (WikiPage page : pages) {
            if (page.getParentId() == null) {
                roots.add(page);
            } else {
                childrenMap.computeIfAbsent(page.getParentId(), k -> new ArrayList<>()).add(page);
            }
        }

        List<Map<String, Object>> tree = new ArrayList<>();
        for (WikiPage root : roots) {
            tree.add(buildTreeNode(root, childrenMap));
        }
        return tree;
    }

    private Map<String, Object> buildTreeNode(WikiPage page, Map<Long, List<WikiPage>> childrenMap) {
        Map<String, Object> node = new HashMap<>();
        node.put("id", page.getId());
        node.put("title", page.getTitle());
        node.put("parentId", page.getParentId());
        node.put("sortOrder", page.getSortOrder());
        node.put("createdAt", page.getCreatedAt());
        node.put("updatedAt", page.getUpdatedAt());

        List<Map<String, Object>> children = new ArrayList<>();
        List<WikiPage> childPages = childrenMap.getOrDefault(page.getId(), new ArrayList<>());
        for (WikiPage child : childPages) {
            children.add(buildTreeNode(child, childrenMap));
        }
        node.put("children", children);
        return node;
    }

    private void deletePageRecursive(Long pageId, Long userId) {
        List<WikiPage> children = wikiPageMapper.selectList(new LambdaQueryWrapper<WikiPage>()
                .eq(WikiPage::getParentId, pageId)
                .eq(WikiPage::getUserId, userId));
        for (WikiPage child : children) {
            deletePageRecursive(child.getId(), userId);
        }
        wikiPageMapper.deleteById(pageId);
    }

    private WikiSpace getOwnedSpace(Long id) {
        WikiSpace space = wikiSpaceMapper.selectById(id);
        if (space == null || !space.getUserId().equals(UserContext.requireUserId())) {
            throw new BusinessException(404, "知识库不存在");
        }
        return space;
    }

    private WikiPage getOwnedPage(Long id) {
        WikiPage page = wikiPageMapper.selectById(id);
        if (page == null || !page.getUserId().equals(UserContext.requireUserId())) {
            throw new BusinessException(404, "页面不存在");
        }
        return page;
    }
}
