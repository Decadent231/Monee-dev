# money-cloud 接口文档

## 1. 网关入口

统一网关地址：

```text
http://localhost:8080
```

服务前缀：

- 用户服务：`/user`
- 记账服务：`/monee`
- 笔记服务：`/note`

需要登录的接口统一请求头：

```text
Authorization: Bearer <token>
```

统一返回格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

## 2. user 模块

### 2.1 发送注册验证码

- 请求方式：`POST`
- 路径：`/user/auth/send-code`
- 请求参数：

```json
{
  "email": "demo@qq.com"
}
```

- 返回说明：向邮箱发送 6 位验证码，5 分钟有效

### 2.2 用户注册

- 请求方式：`POST`
- 路径：`/user/auth/register`
- 请求参数：

```json
{
  "email": "demo@qq.com",
  "password": "123456",
  "code": "123456",
  "nickname": "demo"
}
```

- 返回说明：注册成功后返回标准成功响应

### 2.3 用户登录

- 请求方式：`POST`
- 路径：`/user/auth/login`
- 请求参数：

```json
{
  "email": "demo@qq.com",
  "password": "123456"
}
```

- 返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "jwt-token",
    "userInfo": {
      "id": 1,
      "email": "demo@qq.com",
      "nickname": "demo"
    }
  }
}
```

### 2.4 获取当前登录用户

- 请求方式：`GET`
- 路径：`/user/users/me`
- 是否鉴权：`是`
- 返回说明：返回当前 JWT 对应用户信息

### 2.5 修改用户资料

- 请求方式：`PUT`
- 路径：`/user/users/profile`
- 是否鉴权：`是`
- 请求参数：

```json
{
  "nickname": "新的昵称"
}
```

### 2.6 修改密码

- 请求方式：`PUT`
- 路径：`/user/users/password`
- 是否鉴权：`是`
- 请求参数：

```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

### 2.7 验证密码

- 请求方式：`POST`
- 路径：`/user/users/verify-password`
- 是否鉴权：`是`
- 请求参数：

```json
{
  "password": "123456"
}
```

- 返回说明：返回 `true` 或 `false`，用于保险箱二次验证等场景

## 3. monee 模块

### 3.1 查询月预算

- 请求方式：`GET`
- 路径：`/monee/api/budget`
- 是否鉴权：`是`
- 查询参数：
  - `month`：可选，格式 `yyyy-MM`

### 3.2 设置月预算

- 请求方式：`POST`
- 路径：`/monee/api/budget`
- 是否鉴权：`是`
- 请求参数：

```json
{
  "amount": 5000.00,
  "month": "2026-04"
}
```

### 3.3 查询每日可用预算

- 请求方式：`GET`
- 路径：`/monee/api/budget/daily-available`
- 是否鉴权：`是`
- 查询参数：
  - `month`：可选，格式 `yyyy-MM`

### 3.4 查询全部分类

- 请求方式：`GET`
- 路径：`/monee/api/categories`
- 是否鉴权：`是`

### 3.5 查询分类详情

- 请求方式：`GET`
- 路径：`/monee/api/categories/{id}`
- 是否鉴权：`是`

### 3.6 新增分类

- 请求方式：`POST`
- 路径：`/monee/api/categories`
- 是否鉴权：`是`
- 请求参数：

```json
{
  "type": "expense",
  "icon": "food",
  "name": "餐饮",
  "description": "吃饭消费",
  "sort": 1
}
```

### 3.7 修改分类

- 请求方式：`PUT`
- 路径：`/monee/api/categories/{id}`
- 是否鉴权：`是`

### 3.8 删除分类

- 请求方式：`DELETE`
- 路径：`/monee/api/categories/{id}`
- 是否鉴权：`是`

### 3.9 分页查询记录

- 请求方式：`GET`
- 路径：`/monee/api/records`
- 是否鉴权：`是`
- 查询参数：
  - `page`：页码，默认 `1`
  - `size`：每页条数，默认 `10`
  - `startDate`：开始日期，可选，格式 `yyyy-MM-dd`
  - `endDate`：结束日期，可选，格式 `yyyy-MM-dd`
  - `type`：收支类型，可选
  - `categoryId`：分类 ID，可选
  - `keyword`：备注关键字，可选

### 3.10 查询记录详情

- 请求方式：`GET`
- 路径：`/monee/api/records/{id}`
- 是否鉴权：`是`

### 3.11 新增记录

- 请求方式：`POST`
- 路径：`/monee/api/records`
- 是否鉴权：`是`
- 请求参数：

```json
{
  "date": "2026-04-11",
  "type": "expense",
  "categoryId": 1,
  "amount": 30.50,
  "remark": "午餐"
}
```

### 3.12 修改记录

- 请求方式：`PUT`
- 路径：`/monee/api/records/{id}`
- 是否鉴权：`是`

### 3.13 删除记录

- 请求方式：`DELETE`
- 路径：`/monee/api/records/{id}`
- 是否鉴权：`是`

### 3.14 月度统计

- 请求方式：`GET`
- 路径：`/monee/api/statistics/monthly`
- 是否鉴权：`是`
- 查询参数：
  - `month`：可选，格式 `yyyy-MM`

### 3.15 分类统计

- 请求方式：`GET`
- 路径：`/monee/api/statistics/category`
- 是否鉴权：`是`
- 查询参数：
  - `month`：可选
  - `type`：可选，`expense` 或 `income`

### 3.16 月趋势统计

- 请求方式：`GET`
- 路径：`/monee/api/statistics/trend`
- 是否鉴权：`是`

### 3.17 年度统计

- 请求方式：`GET`
- 路径：`/monee/api/statistics/yearly`
- 是否鉴权：`是`
- 查询参数：
  - `year`：可选，例如 `2026`

### 3.18 年度分类统计

- 请求方式：`GET`
- 路径：`/monee/api/statistics/category/yearly`
- 是否鉴权：`是`

### 3.19 年度趋势统计

- 请求方式：`GET`
- 路径：`/monee/api/statistics/trend/yearly`
- 是否鉴权：`是`

## 4. note 模块

### 4.1 新增笔记

- 请求方式：`POST`
- 路径：`/note/notes`
- 是否鉴权：`是`
- 请求参数：

```json
{
  "title": "周报总结",
  "category": "项目管理",
  "tags": "周报,复盘",
  "summary": "本周迭代总结",
  "content": "<p>富文本内容</p>",
  "contentType": "html",
  "templateId": null
}
```

字段说明：

- `contentType`：内容类型，`html`（富文本，默认）/ `markdown`
- `templateId`：可选，选择模板时传入，新建笔记将自动填充模板内容

### 4.2 删除笔记（软删除，移入回收站）

- 请求方式：`DELETE`
- 路径：`/note/notes/{id}`
- 是否鉴权：`是`
- 返回说明：将笔记标记为已删除，移入回收站

### 4.3 修改笔记

- 请求方式：`PUT`
- 路径：`/note/notes/{id}`
- 是否鉴权：`是`
- 请求参数：

```json
{
  "title": "更新后的标题",
  "category": "分类",
  "tags": "标签1,标签2",
  "summary": "摘要",
  "content": "<p>更新后的内容</p>"
}
```

### 4.4 查询单条笔记

- 请求方式：`GET`
- 路径：`/note/notes/{id}`
- 是否鉴权：`是`

### 4.5 分页查询当前用户笔记

- 请求方式：`GET`
- 路径：`/note/notes`
- 是否鉴权：`是`
- 查询参数：
  - `current`：页码，默认 `1`
  - `size`：每页条数，默认 `10`
  - `keyword`：关键词，搜索标题/摘要/内容
  - `category`：分类筛选
  - `tag`：标签筛选

### 4.6 查询回收站

- 请求方式：`GET`
- 路径：`/note/notes/trash`
- 是否鉴权：`是`
- 返回说明：返回当前用户已软删除的笔记列表

### 4.7 恢复笔记

- 请求方式：`PUT`
- 路径：`/note/notes/{id}/restore`
- 是否鉴权：`是`
- 返回说明：将回收站中的笔记恢复

### 4.8 彻底删除笔记

- 请求方式：`DELETE`
- 路径：`/note/notes/{id}/permanent`
- 是否鉴权：`是`
- 返回说明：从数据库彻底删除，不可恢复

### 9. 清空回收站

- 请求方式：`DELETE`
- 路径：`/note/notes/trash/empty`

### 10. 置顶/取消置顶笔记

- 请求方式：`PUT`
- 路径：`/note/notes/{id}/pin`

### 11. 收藏/取消收藏笔记

- 请求方式：`PUT`
- 路径：`/note/notes/{id}/star`

### 12. 查询收藏笔记列表

- 请求方式：`GET`
- 路径：`/note/notes/starred`

## 3.1 note 模块 - 笔记模板

### 1. 查询模板列表

- 请求方式：`GET`
- 路径：`/note/templates`

返回说明：返回系统预置模板和当前用户自定义模板

### 2. 查询单个模板

- 请求方式：`GET`
- 路径：`/note/templates/{id}`

### 3. 创建模板

- 请求方式：`POST`
- 路径：`/note/templates`

请求参数：

```json
{
  "name": "我的模板",
  "contentType": "markdown",
  "content": "# 模板内容"
}
```

### 4. 修改模板

- 请求方式：`PUT`
- 路径：`/note/templates/{id}`

说明：系统预置模板不可修改/删除

### 5. 删除模板

- 请求方式：`DELETE`
- 路径：`/note/templates/{id}`

## 3.2 note 模块 - 全局搜索

### 1. 全局搜索

- 请求方式：`GET`
- 路径：`/note/search`

查询参数：

- `keyword`：搜索关键词

返回说明：按笔记、保险箱、待办三个分类返回匹配结果，每类最多 6 条

## 5. 密码保险箱 vault 模块

### 5.1 查询保险箱列表

- 请求方式：`GET`
- 路径：`/note/vault-items`
- 是否鉴权：`是`
- 查询参数：
  - `keyword`：关键词，搜索标题/账号/网址/备注
  - `category`：分类筛选

### 5.2 查询单条保险箱记录

- 请求方式：`GET`
- 路径：`/note/vault-items/{id}`
- 是否鉴权：`是`

### 5.3 新增保险箱记录

- 请求方式：`POST`
- 路径：`/note/vault-items`
- 是否鉴权：`是`
- 请求参数：

```json
{
  "title": "GitHub",
  "category": "开发平台",
  "username": "user@example.com",
  "password": "StrongPass123",
  "website": "https://github.com",
  "remark": "主工作账号"
}
```

### 5.4 修改保险箱记录

- 请求方式：`PUT`
- 路径：`/note/vault-items/{id}`
- 是否鉴权：`是`

### 5.5 删除保险箱记录

- 请求方式：`DELETE`
- 路径：`/note/vault-items/{id}`
- 是否鉴权：`是`

## 6. 待办事项 todo 模块

### 6.1 查询待办列表

- 请求方式：`GET`
- 路径：`/note/todos`
- 是否鉴权：`是`
- 查询参数：
  - `status`：状态筛选，`todo / doing / done`
  - `priority`：优先级筛选，`low / medium / high`
  - `keyword`：关键词，搜索标题和描述
  - `reminderFilter`：提醒状态筛选，`active`（未过期）/ `expired`（已过期）

### 6.2 查询单条待办

- 请求方式：`GET`
- 路径：`/note/todos/{id}`
- 是否鉴权：`是`

### 6.3 新增待办

- 请求方式：`POST`
- 路径：`/note/todos`
- 是否鉴权：`是`
- 请求参数：

```json
{
  "title": "整理接口文档",
  "description": "输出联调版本接口说明",
  "status": "todo",
  "priority": "high",
  "dueDate": "2026-04-20",
  "reminderEnabled": true,
  "reminderAt": "2026-04-20T09:00:00",
  "reminderEmail": "notify@example.com"
}
```

### 6.4 修改待办

- 请求方式：`PUT`
- 路径：`/note/todos/{id}`
- 是否鉴权：`是`
- 请求参数与新增待办一致

### 6.5 仅修改待办状态

- 请求方式：`PUT`
- 路径：`/note/todos/{id}/status`
- 是否鉴权：`是`
- 请求参数：

```json
{
  "status": "done"
}
```

### 6.6 删除待办

- 请求方式：`DELETE`
- 路径：`/note/todos/{id}`
- 是否鉴权：`是`

## 7. 活动日志 activity-log 模块

### 7.1 查询活动日志

- 请求方式：`GET`
- 路径：`/note/activity-logs`
- 是否鉴权：`是`
- 查询参数：
  - `module`：模块筛选，`note / vault / todo`，可选
  - `limit`：返回条数，默认 `30`

### 7.2 查询最近活动

- 请求方式：`GET`
- 路径：`/note/activity-logs/recent`
- 是否鉴权：`是`
- 查询参数：
  - `limit`：返回条数，默认 `10`

### 7.3 模块操作统计

- 请求方式：`GET`
- 路径：`/note/activity-logs/stats/modules`
- 是否鉴权：`是`
- 返回说明：返回各模块操作次数，格式 `{ "note": 12, "vault": 5, "todo": 8 }`

### 7.4 按天操作统计

- 请求方式：`GET`
- 路径：`/note/activity-logs/stats/daily`
- 是否鉴权：`是`
- 查询参数：
  - `days`：统计天数，默认 `14`
- 返回说明：返回按日期的操作次数，格式 `{ "2026-05-01": 5, "2026-04-30": 3 }`

## 8. 提醒发送说明

- 待办提醒由 `note` 服务定时任务每分钟扫描一次。
- 只有 `reminderEnabled = true`、`reminderSent = false` 且 `reminderAt <= 当前时间` 的待办会触发发送。
- 提醒邮件发送成功后，后端会把 `reminderSent` 更新为 `true`，避免重复发送。

## 9. 错误响应说明

常见错误码：

- `200`：成功
- `400`：参数错误或业务校验失败
- `401`：未登录、Token 失效或无权限
- `404`：资源不存在
- `500`：系统内部异常

## 10. 日历日程模块

### 10.1 新建日程

- 请求方式：`POST`
- 路径：`/note/calendar`
- 是否鉴权：`是`
- 请求参数：

```json
{
  "title": "团队周会",
  "description": "本周迭代回顾",
  "startTime": "2026-05-05T10:00:00",
  "endTime": "2026-05-05T11:00:00",
  "allDay": 0,
  "color": "#409eff",
  "location": "会议室A",
  "reminderMinutes": 15
}
```

### 10.2 修改日程

- 请求方式：`PUT`
- 路径：`/note/calendar/{id}`
- 是否鉴权：`是`

### 10.3 删除日程

- 请求方式：`DELETE`
- 路径：`/note/calendar/{id}`
- 是否鉴权：`是`

### 10.4 查询单个日程

- 请求方式：`GET`
- 路径：`/note/calendar/{id}`
- 是否鉴权：`是`

### 10.5 按月查询日程

- 请求方式：`GET`
- 路径：`/note/calendar`
- 是否鉴权：`是`
- 查询参数：
  - `year`：年份，必填
  - `month`：月份，必填

### 10.6 按日期范围查询

- 请求方式：`GET`
- 路径：`/note/calendar/range`
- 是否鉴权：`是`
- 查询参数：
  - `startDate`：开始日期，格式 `yyyy-MM-dd`
  - `endDate`：结束日期，格式 `yyyy-MM-dd`

### 10.7 查询今日日程

- 请求方式：`GET`
- 路径：`/note/calendar/today`
- 是否鉴权：`是`

## 11. 知识库 Wiki 模块

### 11.1 新建知识库

- 请求方式：`POST`
- 路径：`/note/wiki/spaces`
- 是否鉴权：`是`
- 请求参数：

```json
{
  "name": "技术笔记",
  "description": "日常开发知识积累",
  "icon": "💻"
}
```

### 11.2 修改知识库

- 请求方式：`PUT`
- 路径：`/note/wiki/spaces/{id}`
- 是否鉴权：`是`

### 11.3 删除知识库

- 请求方式：`DELETE`
- 路径：`/note/wiki/spaces/{id}`
- 是否鉴权：`是`
- 说明：删除知识库会同时删除其中所有页面

### 11.4 查询知识库列表

- 请求方式：`GET`
- 路径：`/note/wiki/spaces`
- 是否鉴权：`是`

### 11.5 查询单个知识库

- 请求方式：`GET`
- 路径：`/note/wiki/spaces/{id}`
- 是否鉴权：`是`

### 11.6 新建页面

- 请求方式：`POST`
- 路径：`/note/wiki/pages`
- 是否鉴权：`是`
- 请求参数：

```json
{
  "spaceId": 1,
  "parentId": null,
  "title": "Java 并发编程",
  "content": "# 并发编程\n\n## 线程池..."
}
```

### 11.7 修改页面

- 请求方式：`PUT`
- 路径：`/note/wiki/pages/{id}`
- 是否鉴权：`是`

### 11.8 删除页面

- 请求方式：`DELETE`
- 路径：`/note/wiki/pages/{id}`
- 是否鉴权：`是`
- 说明：删除页面会同时删除所有子页面

### 11.9 查询单个页面

- 请求方式：`GET`
- 路径：`/note/wiki/pages/{id}`
- 是否鉴权：`是`

### 11.10 查询知识库下所有页面（平铺）

- 请求方式：`GET`
- 路径：`/note/wiki/spaces/{spaceId}/pages`
- 是否鉴权：`是`

### 11.11 查询知识库页面树

- 请求方式：`GET`
- 路径：`/note/wiki/spaces/{spaceId}/tree`
- 是否鉴权：`是`
- 返回说明：返回树形结构，每个节点包含 `id`、`title`、`parentId`、`children` 数组

## 12. 文件库模块

### 12.1 上传文件

- 请求方式：`POST`
- 路径：`/note/files/upload`
- 是否鉴权：`是`
- 请求类型：`multipart/form-data`
- 请求参数：
  - `files`：文件（支持多文件）
  - `folder`：虚拟文件夹，可选
  - `remark`：备注，可选

### 12.2 分页查询文件

- 请求方式：`GET`
- 路径：`/note/files`
- 是否鉴权：`是`
- 查询参数：
  - `current`：页码，默认 `1`
  - `size`：每页条数，默认 `15`
  - `keyword`：关键词，搜索文件名/备注
  - `folder`：文件夹筛选

### 12.3 查询单条文件

- 请求方式：`GET`
- 路径：`/note/files/{id}`
- 是否鉴权：`是`

### 12.4 下载文件

- 请求方式：`GET`
- 路径：`/note/files/{id}/download`
- 是否鉴权：`是`（支持 `?token=` 参数鉴权）
- 返回说明：返回文件流，Content-Disposition 为附件

### 12.5 预览文件

- 请求方式：`GET`
- 路径：`/note/files/{id}/preview`
- 是否鉴权：`是`（支持 `?token=` 参数鉴权）
- 返回说明：支持 `image/*`、`application/pdf`、`text/*` 预览

### 12.6 修改文件信息

- 请求方式：`PUT`
- 路径：`/note/files/{id}`
- 是否鉴权：`是`
- 查询参数：
  - `folder`：新文件夹
  - `remark`：新备注

### 12.7 删除文件

- 请求方式：`DELETE`
- 路径：`/note/files/{id}`
- 是否鉴权：`是`
- 返回说明：同时删除磁盘文件和数据库记录

### 12.8 查询文件夹列表

- 请求方式：`GET`
- 路径：`/note/files/folders`
- 是否鉴权：`是`
- 返回说明：返回当前用户所有不重复的文件夹名称

### 12.9 存储统计

- 请求方式：`GET`
- 路径：`/note/files/stats`
- 是否鉴权：`是`
- 返回说明：返回 `{ "fileCount": 10, "totalSize": 1048576 }`

## 13. 工作台 Dashboard 模块

### 13.1 全模块数据总览

- 请求方式：`GET`
- 路径：`/note/dashboard/overview`
- 是否鉴权：`是`
- 返回说明：返回笔记数、wiki页面数、待办数、待办完成数、保险箱数、日程数、文件数、文件总大小

### 13.2 本周效率报告

- 请求方式：`GET`
- 路径：`/note/dashboard/weekly-report`
- 是否鉴权：`是`
- 返回说明：返回本周与上周的笔记新增数、待办完成数、操作次数对比

### 13.3 知识增长曲线

- 请求方式：`GET`
- 路径：`/note/dashboard/knowledge-growth`
- 是否鉴权：`是`
- 查询参数：
  - `days`：统计天数，默认 `30`
- 返回说明：返回按日期的笔记+wiki新增数

### 13.4 活动热力图

- 请求方式：`GET`
- 路径：`/note/dashboard/activity-heatmap`
- 是否鉴权：`是`
- 查询参数：
  - `days`：统计天数，默认 `90`
- 返回说明：返回按日期的操作次数，适配 ECharts Calendar Heatmap

### 13.5 优先待办列表

- 请求方式：`GET`
- 路径：`/note/dashboard/top-todos`
- 是否鉴权：`是`
- 查询参数：
  - `limit`：返回条数，默认 `5`
- 返回说明：返回未完成待办按优先级+创建时间排序
