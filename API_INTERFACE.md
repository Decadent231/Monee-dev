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
  "title": "今天的总结",
  "content": "完成了记账和微服务改造"
}
```

### 4.2 删除笔记

- 请求方式：`DELETE`
- 路径：`/note/notes/{id}`
- 是否鉴权：`是`

### 4.3 修改笔记

- 请求方式：`PUT`
- 路径：`/note/notes/{id}`
- 是否鉴权：`是`
- 请求参数：

```json
{
  "title": "更新后的标题",
  "content": "更新后的内容"
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

## 5. 错误响应说明

常见错误码：

- `200`：成功
- `400`：参数错误或业务校验失败
- `401`：未登录、Token 失效或无权限
- `404`：资源不存在
- `500`：系统内部异常
