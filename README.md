# money-cloud

`money-cloud` 是 `Monee` 系统的 Spring Cloud 微服务后端，基于 `Spring Boot 3.2.x + Spring Cloud 2023.0.x` 构建，将原单体记账项目拆分为网关、用户、记账、笔记和公共能力模块。

## 模块说明

- `gateway`：统一入口，负责路由转发、跨域处理
- `user`：用户注册、登录、邮箱验证码、JWT 身份认证、用户资料维护
- `monee`：原 `money` 单体业务迁移模块，负责记账、预算、分类、统计
- `note`：工作笔记、密码保险箱、待办事项、待办邮件提醒
- `common`：统一返回、全局异常、JWT 工具、过滤器、邮件工具、通用配置

## 技术栈

- Spring Boot 3.2.12
- Spring Cloud 2023.0.6
- Spring Cloud Gateway
- Spring Security 6
- JWT
- Spring Mail
- MyBatis-Plus 3.5.7
- MySQL 8
- Maven Multi Module
- Lombok

## 架构图

```mermaid
flowchart LR
    A["Web / Android Client"] --> B["Gateway :8080"]
    B --> C["user :8081"]
    B --> D["monee :8082"]
    B --> E["note :8083"]
    C --> F["money_cloud_user"]
    D --> G["money_cloud_monee"]
    E --> H["money_cloud_note"]
```

## 项目结构

```text
money-cloud
├─ common
├─ gateway
├─ user
├─ monee
├─ note
├─ sql
├─ pom.xml
├─ README.md
├─ PROJECT_INTRODUCE.md
└─ API_INTERFACE.md
```

## 核心能力

### 用户模块

- 邮箱验证码注册
- 邮箱密码登录
- BCrypt 密码加密
- JWT 登录态，默认有效期 2 年
- 获取当前用户信息
- 修改昵称与密码

### 记账模块

- 分类增删改查
- 记录增删改查
- 预算设置与预算剩余
- 月统计、年统计、分类统计、趋势统计
- 所有数据按登录用户隔离

### 笔记模块

- 工作笔记 CRUD
- 笔记分类、标签、摘要、富文本内容
- 密码保险箱，后端加密存储
- 待办事项 CRUD
- 待办邮件提醒：支持提醒时间、提醒邮箱，默认发到当前账号邮箱

## 环境要求

- JDK 17
- Maven 3.9+
- MySQL 8

## 数据库

默认使用三个库：

- `money_cloud_user`
- `money_cloud_monee`
- `money_cloud_note`

初始化脚本位于：

- [sql/user.sql](./sql/user.sql)
- [sql/monee.sql](./sql/monee.sql)
- [sql/note.sql](./sql/note.sql)

增量脚本：

- [sql/note_migration_20260412.sql](./sql/note_migration_20260412.sql)
- [sql/todo-reminder-upgrade.sql](./sql/todo-reminder-upgrade.sql)

## 配置说明

仓库中的 `application.yml` 已改为环境变量占位形式，不再直接提交生产密码。

### 常用环境变量

```text
MYSQL_HOST
MYSQL_PORT
MYSQL_USERNAME
MYSQL_PASSWORD
MAIL_HOST
MAIL_PORT
MAIL_USERNAME
MAIL_PASSWORD
MAIL_PROTOCOL
MAIL_SMTP_AUTH
MAIL_SMTP_STARTTLS_ENABLE
MAIL_SMTP_SSL_TRUST
JWT_SECRET
JWT_EXPIRE_SECONDS
VAULT_SECRET
```

### 推荐示例

```bash
set MYSQL_HOST=127.0.0.1
set MYSQL_PORT=3306
set MYSQL_USERNAME=root
set MYSQL_PASSWORD=your-password
set MAIL_USERNAME=your-qq@qq.com
set MAIL_PASSWORD=your-mail-auth-code
set JWT_SECRET=your-jwt-secret
set JWT_EXPIRE_SECONDS=63072000
set VAULT_SECRET=your-vault-secret
```

Linux:

```bash
export MYSQL_HOST=127.0.0.1
export MYSQL_PORT=3306
export MYSQL_USERNAME=root
export MYSQL_PASSWORD=your-password
export MAIL_USERNAME=your-qq@qq.com
export MAIL_PASSWORD=your-mail-auth-code
export JWT_SECRET=your-jwt-secret
export JWT_EXPIRE_SECONDS=63072000
export VAULT_SECRET=your-vault-secret
```

## 启动方式

### 1. 构建

```bash
mvn clean package
```

### 2. 分模块启动

```bash
mvn -pl user spring-boot:run
mvn -pl monee spring-boot:run
mvn -pl note spring-boot:run
mvn -pl gateway spring-boot:run
```

### 3. 访问入口

网关默认端口：

```text
http://localhost:8080
```

路由规则：

- `/user/**` -> `user`
- `/monee/**` -> `monee`
- `/note/**` -> `note`

## 鉴权说明

- 注册和登录在 `user` 模块完成
- 登录成功后返回 JWT Token
- 访问 `monee` 和 `note` 业务接口时，请在请求头中携带：

```text
Authorization: Bearer <token>
```

- 网关当前为基础转发版，不额外做统一鉴权拦截
- 实际鉴权由各业务服务内部的 Spring Security + JWT Filter 完成

## 文档

- 项目介绍：[PROJECT_INTRODUCE.md](./PROJECT_INTRODUCE.md)
- 接口文档：[API_INTERFACE.md](./API_INTERFACE.md)

## 关联前端

- Android 客户端：`Monee-app`
- 笔记 Web 前端：`Note-web`
