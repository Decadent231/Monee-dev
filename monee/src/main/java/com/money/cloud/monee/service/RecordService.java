package com.money.cloud.monee.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.money.cloud.common.context.UserContext;
import com.money.cloud.monee.dto.RecordRequest;
import com.money.cloud.monee.entity.Category;
import com.money.cloud.monee.entity.Record;
import com.money.cloud.monee.mapper.CategoryMapper;
import com.money.cloud.monee.mapper.RecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordMapper recordMapper;
    private final CategoryMapper categoryMapper;

    public IPage<Record> getRecords(int page, int size, LocalDate startDate, LocalDate endDate,
                                    String type, Long categoryId, String keyword) {
        Long userId = UserContext.requireUserId();
        IPage<Record> records = recordMapper.selectByFilters(new Page<>(page, size), userId, startDate, endDate,
                type, categoryId, keyword);
        Map<Long, String> categoryMap = getCategoryMap();
        records.getRecords().forEach(record -> record.setCategoryName(categoryMap.get(record.getCategoryId())));
        return records;
    }

    public Optional<Record> getRecordById(Long id) {
        Optional<Record> record = Optional.ofNullable(recordMapper.selectOne(new LambdaQueryWrapper<Record>()
                .eq(Record::getId, id)
                .eq(Record::getUserId, UserContext.requireUserId())
                .last("limit 1")));
        record.ifPresent(item -> {
            Category category = categoryMapper.selectById(item.getCategoryId());
            if (category != null) {
                item.setCategoryName(category.getName());
            }
        });
        return record;
    }

    @Transactional
    public Record createRecord(RecordRequest request) {
        Record record = new Record();
        record.setUserId(UserContext.requireUserId());
        record.setDate(request.getDate());
        record.setType(request.getType());
        record.setCategoryId(request.getCategoryId());
        record.setAmount(request.getAmount());
        record.setRemark(request.getRemark());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        recordMapper.insert(record);
        Category category = categoryMapper.selectById(request.getCategoryId());
        if (category != null) {
            record.setCategoryName(category.getName());
        }
        return record;
    }

    @Transactional
    public Optional<Record> updateRecord(Long id, RecordRequest request) {
        Record record = recordMapper.selectOne(new LambdaQueryWrapper<Record>()
                .eq(Record::getId, id)
                .eq(Record::getUserId, UserContext.requireUserId())
                .last("limit 1"));
        if (record == null) {
            return Optional.empty();
        }
        record.setDate(request.getDate());
        record.setType(request.getType());
        record.setCategoryId(request.getCategoryId());
        record.setAmount(request.getAmount());
        record.setRemark(request.getRemark());
        record.setUpdatedAt(LocalDateTime.now());
        recordMapper.updateById(record);
        Category category = categoryMapper.selectById(request.getCategoryId());
        if (category != null) {
            record.setCategoryName(category.getName());
        }
        return Optional.of(record);
    }

    @Transactional
    public boolean deleteRecord(Long id) {
        return recordMapper.delete(new LambdaQueryWrapper<Record>()
                .eq(Record::getId, id)
                .eq(Record::getUserId, UserContext.requireUserId())) > 0;
    }

    public List<Record> getRecentRecords() {
        List<Record> records = recordMapper.selectByUserIdOrderByDateDesc(UserContext.requireUserId());
        Map<Long, String> categoryMap = getCategoryMap();
        records.forEach(record -> record.setCategoryName(categoryMap.get(record.getCategoryId())));
        return records.size() > 5 ? records.subList(0, 5) : records;
    }

    private Map<Long, String> getCategoryMap() {
        Map<Long, String> map = new HashMap<>();
        categoryMapper.selectByUserIdOrDefault(UserContext.requireUserId())
                .forEach(category -> map.put(category.getId(), category.getName()));
        return map;
    }
}
