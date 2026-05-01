package com.money.cloud.note.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.money.cloud.common.context.UserContext;
import com.money.cloud.note.entity.ActivityLog;
import com.money.cloud.note.mapper.ActivityLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogMapper activityLogMapper;

    @Transactional
    public void log(String module, String action, Long targetId, String detail) {
        ActivityLog log = new ActivityLog();
        log.setUserId(UserContext.requireUserId());
        log.setModule(module);
        log.setAction(action);
        log.setTargetId(targetId);
        log.setDetail(detail);
        log.setCreatedAt(LocalDateTime.now());
        activityLogMapper.insert(log);
    }

    public List<ActivityLog> listRecent(int limit) {
        return activityLogMapper.selectList(new LambdaQueryWrapper<ActivityLog>()
                .eq(ActivityLog::getUserId, UserContext.requireUserId())
                .orderByDesc(ActivityLog::getCreatedAt)
                .last("limit " + limit));
    }

    public List<ActivityLog> listByModule(String module, int limit) {
        LambdaQueryWrapper<ActivityLog> wrapper = new LambdaQueryWrapper<ActivityLog>()
                .eq(ActivityLog::getUserId, UserContext.requireUserId())
                .orderByDesc(ActivityLog::getCreatedAt)
                .last("limit " + limit);
        if (StringUtils.hasText(module)) {
            wrapper.eq(ActivityLog::getModule, module);
        }
        return activityLogMapper.selectList(wrapper);
    }

    public Map<String, Long> countByModule() {
        Long userId = UserContext.requireUserId();
        List<ActivityLog> all = activityLogMapper.selectList(new LambdaQueryWrapper<ActivityLog>()
                .eq(ActivityLog::getUserId, userId));
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("note", all.stream().filter(a -> "note".equals(a.getModule())).count());
        result.put("vault", all.stream().filter(a -> "vault".equals(a.getModule())).count());
        result.put("todo", all.stream().filter(a -> "todo".equals(a.getModule())).count());
        return result;
    }

    public Map<String, Long> countByDay(int days) {
        Long userId = UserContext.requireUserId();
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<ActivityLog> all = activityLogMapper.selectList(new LambdaQueryWrapper<ActivityLog>()
                .eq(ActivityLog::getUserId, userId)
                .ge(ActivityLog::getCreatedAt, since));
        Map<String, Long> result = new LinkedHashMap<>();
        for (int i = days - 1; i >= 0; i--) {
            String date = LocalDateTime.now().minusDays(i).toLocalDate().toString();
            long count = all.stream()
                    .filter(a -> a.getCreatedAt().toLocalDate().toString().equals(date))
                    .count();
            result.put(date, count);
        }
        return result;
    }
}
