package com.money.cloud.note.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.money.cloud.common.context.UserContext;
import com.money.cloud.note.entity.*;
import com.money.cloud.note.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final NoteMapper noteMapper;
    private final TodoItemMapper todoItemMapper;
    private final VaultItemMapper vaultItemMapper;
    private final CalendarEventMapper calendarEventMapper;
    private final WikiPageMapper wikiPageMapper;
    private final ActivityLogMapper activityLogMapper;
    private final FileAssetMapper fileAssetMapper;

    public Map<String, Object> overview() {
        Long userId = UserContext.requireUserId();
        Map<String, Object> result = new HashMap<>();

        result.put("noteCount", noteMapper.selectCount(
                new LambdaQueryWrapper<Note>().eq(Note::getUserId, userId).eq(Note::getDeleted, 0)));
        result.put("vaultCount", vaultItemMapper.selectCount(
                new LambdaQueryWrapper<VaultItem>().eq(VaultItem::getUserId, userId)));
        result.put("todoTotal", todoItemMapper.selectCount(
                new LambdaQueryWrapper<TodoItem>().eq(TodoItem::getUserId, userId)));
        result.put("todoDone", todoItemMapper.selectCount(
                new LambdaQueryWrapper<TodoItem>().eq(TodoItem::getUserId, userId).eq(TodoItem::getStatus, "done")));
        result.put("calendarCount", calendarEventMapper.selectCount(
                new LambdaQueryWrapper<CalendarEvent>().eq(CalendarEvent::getUserId, userId)));
        result.put("wikiPageCount", wikiPageMapper.selectCount(
                new LambdaQueryWrapper<WikiPage>().eq(WikiPage::getUserId, userId)));
        result.put("fileCount", fileAssetMapper.selectCount(
                new LambdaQueryWrapper<FileAsset>().eq(FileAsset::getUserId, userId)));

        List<FileAsset> files = fileAssetMapper.selectList(
                new LambdaQueryWrapper<FileAsset>().eq(FileAsset::getUserId, userId)
                        .select(FileAsset::getFileSize));
        long totalSize = files.stream().mapToLong(f -> f.getFileSize() != null ? f.getFileSize() : 0).sum();
        result.put("fileTotalSize", totalSize);

        return result;
    }

    public Map<String, Object> weeklyReport() {
        Long userId = UserContext.requireUserId();
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate lastWeekStart = weekStart.minusWeeks(1);
        LocalDateTime thisWeekFrom = weekStart.atStartOfDay();
        LocalDateTime lastWeekFrom = lastWeekStart.atStartOfDay();
        LocalDateTime lastWeekTo = weekStart.atStartOfDay();

        Map<String, Object> result = new HashMap<>();

        long thisWeekNotes = noteMapper.selectCount(new LambdaQueryWrapper<Note>()
                .eq(Note::getUserId, userId).ge(Note::getCreatedAt, thisWeekFrom));
        long lastWeekNotes = noteMapper.selectCount(new LambdaQueryWrapper<Note>()
                .eq(Note::getUserId, userId).ge(Note::getCreatedAt, lastWeekFrom).lt(Note::getCreatedAt, lastWeekTo));

        long thisWeekDone = todoItemMapper.selectCount(new LambdaQueryWrapper<TodoItem>()
                .eq(TodoItem::getUserId, userId).eq(TodoItem::getStatus, "done").ge(TodoItem::getUpdatedAt, thisWeekFrom));
        long lastWeekDone = todoItemMapper.selectCount(new LambdaQueryWrapper<TodoItem>()
                .eq(TodoItem::getUserId, userId).eq(TodoItem::getStatus, "done")
                .ge(TodoItem::getUpdatedAt, lastWeekFrom).lt(TodoItem::getUpdatedAt, lastWeekTo));

        long thisWeekLogs = activityLogMapper.selectCount(new LambdaQueryWrapper<ActivityLog>()
                .eq(ActivityLog::getUserId, userId).ge(ActivityLog::getCreatedAt, thisWeekFrom));
        long lastWeekLogs = activityLogMapper.selectCount(new LambdaQueryWrapper<ActivityLog>()
                .eq(ActivityLog::getUserId, userId).ge(ActivityLog::getCreatedAt, lastWeekFrom)
                .lt(ActivityLog::getCreatedAt, lastWeekTo));

        result.put("thisWeekNotes", thisWeekNotes);
        result.put("lastWeekNotes", lastWeekNotes);
        result.put("thisWeekDone", thisWeekDone);
        result.put("lastWeekDone", lastWeekDone);
        result.put("thisWeekActivities", thisWeekLogs);
        result.put("lastWeekActivities", lastWeekLogs);

        return result;
    }

    public Map<String, Long> knowledgeGrowth(int days) {
        Long userId = UserContext.requireUserId();
        LocalDateTime since = LocalDate.now().minusDays(days - 1).atStartOfDay();

        List<Note> notes = noteMapper.selectList(new LambdaQueryWrapper<Note>()
                .eq(Note::getUserId, userId).eq(Note::getDeleted, 0).ge(Note::getCreatedAt, since)
                .select(Note::getCreatedAt));
        List<WikiPage> pages = wikiPageMapper.selectList(new LambdaQueryWrapper<WikiPage>()
                .eq(WikiPage::getUserId, userId).ge(WikiPage::getCreatedAt, since)
                .select(WikiPage::getCreatedAt));

        Map<String, Long> result = new LinkedHashMap<>();
        for (int i = days - 1; i >= 0; i--) {
            String date = LocalDate.now().minusDays(i).toString();
            result.put(date, 0L);
        }

        for (Note n : notes) {
            String date = n.getCreatedAt().toLocalDate().toString();
            result.merge(date, 1L, Long::sum);
        }
        for (WikiPage p : pages) {
            String date = p.getCreatedAt().toLocalDate().toString();
            result.merge(date, 1L, Long::sum);
        }

        return result;
    }

    public Map<String, Long> activityHeatmap(int days) {
        Long userId = UserContext.requireUserId();
        LocalDateTime since = LocalDate.now().minusDays(days - 1).atStartOfDay();

        List<ActivityLog> logs = activityLogMapper.selectList(new LambdaQueryWrapper<ActivityLog>()
                .eq(ActivityLog::getUserId, userId).ge(ActivityLog::getCreatedAt, since)
                .select(ActivityLog::getCreatedAt));

        Map<String, Long> result = new LinkedHashMap<>();
        for (int i = days - 1; i >= 0; i--) {
            String date = LocalDate.now().minusDays(i).toString();
            result.put(date, 0L);
        }

        for (ActivityLog log : logs) {
            String date = log.getCreatedAt().toLocalDate().toString();
            result.merge(date, 1L, Long::sum);
        }

        return result;
    }

    public List<Map<String, Object>> topTodos(int limit) {
        Long userId = UserContext.requireUserId();
        List<TodoItem> todos = todoItemMapper.selectList(new LambdaQueryWrapper<TodoItem>()
                .eq(TodoItem::getUserId, userId)
                .ne(TodoItem::getStatus, "done")
                .orderByAsc(TodoItem::getPriority, TodoItem::getCreatedAt)
                .last("limit " + limit));

        List<Map<String, Object>> result = new ArrayList<>();
        for (TodoItem t : todos) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", t.getId());
            map.put("title", t.getTitle());
            map.put("priority", t.getPriority());
            map.put("status", t.getStatus());
            map.put("dueDate", t.getDueDate());
            result.add(map);
        }
        return result;
    }
}
