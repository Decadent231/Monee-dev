package com.money.cloud.note.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.money.cloud.common.context.LoginUser;
import com.money.cloud.common.context.UserContext;
import com.money.cloud.common.exception.BusinessException;
import com.money.cloud.common.util.MailUtil;
import com.money.cloud.note.dto.TodoItemRequest;
import com.money.cloud.note.entity.TodoItem;
import com.money.cloud.note.mapper.TodoItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final TodoItemMapper todoItemMapper;
    private final MailUtil mailUtil;

    @Transactional
    public TodoItem create(TodoItemRequest request) {
        TodoItem item = new TodoItem();
        item.setUserId(UserContext.requireUserId());
        applyRequest(item, request);
        item.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : "todo");
        item.setPriority(StringUtils.hasText(request.getPriority()) ? request.getPriority() : "medium");
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        item.setReminderSent(!Boolean.TRUE.equals(item.getReminderEnabled()));
        todoItemMapper.insert(item);
        return item;
    }

    @Transactional
    public TodoItem update(Long id, TodoItemRequest request) {
        TodoItem item = getOwnedItem(id);
        applyRequest(item, request);
        item.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : item.getStatus());
        item.setPriority(StringUtils.hasText(request.getPriority()) ? request.getPriority() : item.getPriority());
        item.setUpdatedAt(LocalDateTime.now());
        todoItemMapper.updateById(item);
        return item;
    }

    @Transactional
    public TodoItem updateStatus(Long id, String status) {
        TodoItem item = getOwnedItem(id);
        item.setStatus(status);
        item.setUpdatedAt(LocalDateTime.now());
        todoItemMapper.updateById(item);
        return item;
    }

    @Transactional
    public void delete(Long id) {
        todoItemMapper.deleteById(getOwnedItem(id).getId());
    }

    public List<TodoItem> list(String status, String priority) {
        LambdaQueryWrapper<TodoItem> wrapper = new LambdaQueryWrapper<TodoItem>()
                .eq(TodoItem::getUserId, UserContext.requireUserId())
                .orderByAsc(TodoItem::getStatus)
                .orderByAsc(TodoItem::getDueDate)
                .orderByDesc(TodoItem::getUpdatedAt, TodoItem::getId);
        if (StringUtils.hasText(status)) {
            wrapper.eq(TodoItem::getStatus, status);
        }
        if (StringUtils.hasText(priority)) {
            wrapper.eq(TodoItem::getPriority, priority);
        }
        return todoItemMapper.selectList(wrapper);
    }

    public TodoItem getById(Long id) {
        return getOwnedItem(id);
    }

    @Transactional
    @Scheduled(cron = "0 * * * * ?")
    public void sendDueReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<TodoItem> dueReminders = todoItemMapper.selectList(new LambdaQueryWrapper<TodoItem>()
                .eq(TodoItem::getReminderEnabled, true)
                .eq(TodoItem::getReminderSent, false)
                .isNotNull(TodoItem::getReminderAt)
                .le(TodoItem::getReminderAt, now)
                .orderByAsc(TodoItem::getReminderAt));

        for (TodoItem item : dueReminders) {
            String receiver = StringUtils.hasText(item.getReminderEmail()) ? item.getReminderEmail() : null;
            if (!StringUtils.hasText(receiver)) {
                item.setReminderSent(Boolean.TRUE);
                item.setUpdatedAt(LocalDateTime.now());
                todoItemMapper.updateById(item);
                continue;
            }

            String subject = "待办提醒：" + item.getTitle();
            String body = """
                    您有一条待办事项已到提醒时间。

                    标题：%s
                    状态：%s
                    优先级：%s
                    截止日期：%s
                    提醒时间：%s
                    描述：%s
                    """.formatted(
                    item.getTitle(),
                    item.getStatus(),
                    item.getPriority(),
                    item.getDueDate() == null ? "未设置" : item.getDueDate().toString(),
                    item.getReminderAt() == null ? "未设置" : item.getReminderAt().format(DATE_TIME_FORMATTER),
                    StringUtils.hasText(item.getDescription()) ? item.getDescription() : "无"
            );
            mailUtil.sendTextMail(receiver, subject, body);
            item.setReminderSent(Boolean.TRUE);
            item.setUpdatedAt(LocalDateTime.now());
            todoItemMapper.updateById(item);
        }
    }

    private void applyRequest(TodoItem item, TodoItemRequest request) {
        item.setTitle(request.getTitle());
        item.setDescription(trimToNull(request.getDescription()));
        item.setDueDate(request.getDueDate());

        boolean reminderEnabled = Boolean.TRUE.equals(request.getReminderEnabled());
        item.setReminderEnabled(reminderEnabled);
        item.setReminderAt(reminderEnabled ? request.getReminderAt() : null);
        item.setReminderEmail(reminderEnabled ? resolveReminderEmail(request) : null);
        item.setReminderSent(reminderEnabled ? Boolean.FALSE : Boolean.TRUE);

        if (reminderEnabled && request.getReminderAt() == null) {
            throw new BusinessException(400, "开启提醒后必须设置提醒时间");
        }
    }

    private String resolveReminderEmail(TodoItemRequest request) {
        if (StringUtils.hasText(request.getReminderEmail())) {
            return request.getReminderEmail().trim();
        }
        LoginUser loginUser = UserContext.get();
        return loginUser == null ? null : loginUser.getEmail();
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private TodoItem getOwnedItem(Long id) {
        TodoItem item = todoItemMapper.selectOne(new LambdaQueryWrapper<TodoItem>()
                .eq(TodoItem::getId, id)
                .eq(TodoItem::getUserId, UserContext.requireUserId())
                .last("limit 1"));
        if (item == null) {
            throw new BusinessException(404, "待办不存在");
        }
        return item;
    }
}
