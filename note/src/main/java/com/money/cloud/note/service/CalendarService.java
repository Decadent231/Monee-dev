package com.money.cloud.note.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.money.cloud.common.context.UserContext;
import com.money.cloud.common.exception.BusinessException;
import com.money.cloud.note.entity.CalendarEvent;
import com.money.cloud.note.mapper.CalendarEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarEventMapper calendarEventMapper;
    private final ActivityLogService activityLogService;

    @Transactional
    public CalendarEvent create(CalendarEvent event) {
        event.setId(null);
        event.setUserId(UserContext.requireUserId());
        if (event.getAllDay() == null) event.setAllDay(0);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        calendarEventMapper.insert(event);
        activityLogService.log("calendar", "create", event.getId(), event.getTitle());
        return event;
    }

    @Transactional
    public CalendarEvent update(Long id, CalendarEvent event) {
        CalendarEvent existing = getOwnedEvent(id);
        existing.setTitle(event.getTitle());
        existing.setDescription(event.getDescription());
        existing.setStartTime(event.getStartTime());
        existing.setEndTime(event.getEndTime());
        existing.setAllDay(event.getAllDay() != null ? event.getAllDay() : 0);
        existing.setColor(event.getColor());
        existing.setLocation(event.getLocation());
        existing.setReminderMinutes(event.getReminderMinutes());
        existing.setUpdatedAt(LocalDateTime.now());
        calendarEventMapper.updateById(existing);
        activityLogService.log("calendar", "update", id, existing.getTitle());
        return existing;
    }

    @Transactional
    public void delete(Long id) {
        CalendarEvent event = getOwnedEvent(id);
        calendarEventMapper.deleteById(id);
        activityLogService.log("calendar", "delete", id, event.getTitle());
    }

    public CalendarEvent getById(Long id) {
        return getOwnedEvent(id);
    }

    public List<CalendarEvent> listByMonth(int year, int month) {
        Long userId = UserContext.requireUserId();
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.plusMonths(1).atDay(1).atStartOfDay();

        return calendarEventMapper.selectList(new LambdaQueryWrapper<CalendarEvent>()
                .eq(CalendarEvent::getUserId, userId)
                .and(q -> q
                        .between(CalendarEvent::getStartTime, start, end.minusSeconds(1))
                        .or()
                        .le(CalendarEvent::getStartTime, end.minusSeconds(1))
                        .ge(CalendarEvent::getEndTime, start))
                .orderByAsc(CalendarEvent::getStartTime));
    }

    public List<CalendarEvent> listByDateRange(String startDate, String endDate) {
        Long userId = UserContext.requireUserId();
        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate).plusDays(1).atStartOfDay();

        return calendarEventMapper.selectList(new LambdaQueryWrapper<CalendarEvent>()
                .eq(CalendarEvent::getUserId, userId)
                .and(q -> q
                        .between(CalendarEvent::getStartTime, start, end.minusSeconds(1))
                        .or()
                        .le(CalendarEvent::getStartTime, end.minusSeconds(1))
                        .ge(CalendarEvent::getEndTime, start))
                .orderByAsc(CalendarEvent::getStartTime));
    }

    public List<CalendarEvent> listToday() {
        Long userId = UserContext.requireUserId();
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        return calendarEventMapper.selectList(new LambdaQueryWrapper<CalendarEvent>()
                .eq(CalendarEvent::getUserId, userId)
                .and(q -> q
                        .between(CalendarEvent::getStartTime, start, end.minusSeconds(1))
                        .or()
                        .le(CalendarEvent::getStartTime, end.minusSeconds(1))
                        .ge(CalendarEvent::getEndTime, start))
                .orderByAsc(CalendarEvent::getStartTime));
    }

    private CalendarEvent getOwnedEvent(Long id) {
        CalendarEvent event = calendarEventMapper.selectById(id);
        if (event == null || !event.getUserId().equals(UserContext.requireUserId())) {
            throw new BusinessException(404, "日程不存在");
        }
        return event;
    }
}
