package com.money.cloud.monee.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.money.cloud.common.context.UserContext;
import com.money.cloud.monee.dto.CategoryRequest;
import com.money.cloud.monee.entity.Category;
import com.money.cloud.monee.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public List<Category> getAllCategories() {
        return categoryMapper.selectByUserIdOrDefault(UserContext.requireUserId());
    }

    public List<Category> getCategoriesByType(String type) {
        return categoryMapper.selectByTypeAndUserIdOrDefault(type, UserContext.requireUserId());
    }

    public Optional<Category> getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            return Optional.empty();
        }
        Long userId = UserContext.requireUserId();
        if (category.getUserId() == null || userId.equals(category.getUserId())) {
            return Optional.of(category);
        }
        return Optional.empty();
    }

    @Transactional
    public Category createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setUserId(UserContext.requireUserId());
        category.setType(request.getType());
        category.setIcon(request.getIcon());
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setSort(request.getSort() == null ? 0 : request.getSort());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.insert(category);
        return category;
    }

    @Transactional
    public Optional<Category> updateCategory(Long id, CategoryRequest request) {
        Category category = categoryMapper.selectOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getId, id)
                .eq(Category::getUserId, UserContext.requireUserId())
                .last("limit 1"));
        if (category == null) {
            return Optional.empty();
        }
        category.setType(request.getType());
        category.setIcon(request.getIcon());
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setSort(request.getSort() == null ? 0 : request.getSort());
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.updateById(category);
        return Optional.of(category);
    }

    @Transactional
    public boolean deleteCategory(Long id) {
        return categoryMapper.delete(new LambdaQueryWrapper<Category>()
                .eq(Category::getId, id)
                .eq(Category::getUserId, UserContext.requireUserId())) > 0;
    }
}
