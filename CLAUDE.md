# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 基本约定

- 你是一名架构师。
- 使用简体中文沟通。
- 如果有不明确的地方必须停下来问我。
- 代码改动应从全局架构考虑，不能偷懒而破坏项目架构设计。
- 系统处于架构搭建阶段，所有需求修改不要兼容旧逻辑，不要做冗余兜底代码，应让问题暴露出来。
- 关键代码或特殊处理的代码必须给出中文注释。
- 如果你尝试了各种方式都访问不了网页或者获取不了想要的内容的时候，使用 /playwright-cli 打开浏览器获取数据。

## 技术栈

| 层级                        | 技术                                                                |
| --------------------------- |-------------------------------------------------------------------|
| **后端** (smart-manage-api) | Spring Boot 3, Java 17, MyBatis-Flex, Druid, Sa-Token, PostgreSQL |
| **前端** (smart-manage-web) | Vite 8, React 19, TypeScript, Arco Design                         |

## 后端约定 (smart-manage-api)

### 分层架构
- 根包：`sm`
- `sm.framework` — 第三方 jar 配置（CORS, Sa-Token, JSON, Redis, MyBatis-Flex, 请求加解密, TraceId）
- `sm.system` — 本系统公共类（自定义注解、全局异常处理、实体基类、拦截器、监听器、工具类）
- `sm.cloud.{领域}` — 领域模块（如 `sm.cloud.sys` 系统服务云）
- `sm.cloud.{领域}.{应用}` — 应用模块（如 `sm.cloud.sys.monitor` 系统监控）
- `sm.cloud.{领域}.{应用}.{单据}` — 单据/子模块
- 公共能力放在 `sm.cloud.{领域}.common`或`sm.cloud.{领域}.{应用}.common`

### 核心设计模式
**请求流程：** CorsFilter → EncryptApiFilter（SM4 解密） → SaServletFilter（登录校验 + 权限校验） → TraceIdInterceptor → BizLogAspect → Controller → Service → Mapper
**统一响应格式：** 所有接口返回 `Result<T>`，包含 `code` / `msg` / `data` / `traceId`。成功用 `Result.success(data)`；异常由 `GlobalExceptionHandler` 统一处理。
**分页：** 入参继承 `PageForm`（pageNum, pageSize），返回 `PageResult<T>`（total, records）。Service 中使用 MyBatis-Flex 的 `Page.of()` + `mapper.paginate()`。
**权限鉴权：** Controller 方法上用 `@SaCheckPermission("sys:base:user:listPage")` 声明权限码；`StpInterfaceImpl` 实现权限/角色加载接口，统一从数据库加载并按 token 缓存到 Redis。
**登录验证码：** 登录接口需要验证码。`/captcha` 生成图文验证码存 Redis（key 为 `captcha:{uuid}`），登录时 SM2 解密前端密码和验证码后校验。
**请求加密（可选）：** `@EncryptApi` / `@DecryptApi` 标注 Controller 或方法后，`EncryptApiFilter` 通过 SM4/CBC 加解密请求/响应体。
**操作日志：** `@BizLog("创建用户")` 标注 Service 方法，AOP 自动记录操作人、IP、请求参数、响应体、耗时，通过 `LogWriteService` 异步落库。
**实体自动填充：** `MyBatisFlexInsertListener` / `MyBatisFlexUpdateListener` 自动设置 `createTime` / `createUser` / `updateTime` / `updateUser`。
**文件存储：** `FileStorageService` 接口抽象，工厂 `FileStorageServiceFactory` 根据 `FileConfigEntity` 的配置选择 Local / FTP 实现。


### 关键约定

- 项目处于架构搭建阶段，不写测试，不考虑向后兼容
- admin 用户（`superadmin`）拥有 `*` 通配权限，跳过权限校验
- 主键使用雪花 ID（MyBatis-Flex 的 `snowFlakeId` 生成器），乐观锁字段 `mutex`
- **禁止**在查询中使用裸表名/字段名字符串（如 `"t_sys_user"`、`"username"`），必须使用MyBatis-Flex的 APT 生成的 `*Table` 类

### 命名规范

- 禁止单字母变量名
- 所有实体**统一使用 `number` 作为业务编码字段**（非 `code`）
- **主从单据**：`detail` 和 `save` 均一次请求完成，明细统一用数组字段传递。主明细默认名为 `entrys`
  ，如有多个明细表可自定义名称（前后端一致即可），后端 `@Transactional` 更新数据。**即使无明细数据，`entrys`
  也必须返回空数组 `[]`**
- `*Util` — 纯静态工具类，不依赖 Spring 容器（如 `ServletUtil`、`StringUtil`）
- `*Helper` — `@Component` 组件，依赖 Spring 注入或配置（如 `CacheHelper`、`Argon2Helper`）

### SQL执行
- 由于本项目处于框架搭建阶段，随时可能修改数据库表结构，所以要以数据库实际数据为准
- 如果你需要执行SQL，可以使用psql命令直接执行，但是要附带密码，不然命令会卡在让你输入密码，psql命令在
  `D:\Program Files\PostgreSQL\16\bin`
- 根据以往经验，查询数据要指定字符集为GBK，插入或更新数据数据要指定字符集为UTF8，具体原因不知
- **创建表时必须加上表备注和字段备注**，使用 PostgreSQL `COMMENT ON` 语法。字段注释规范：
  - `id` → `ID`
  - `number` → `编码`
  - `name` → `名称`
  - `create_time` → `创建时间`
  - `update_time` → `更新时间`
  - `create_user` → `创建人`
  - `update_user` → `修改人`
  - 其他字段根据业务含义填写中文备注

## 前端约定 (smart-manage-web)

### 构建与运行

```bash
cd smart-manage-web

pnpm dev        # 开发（localhost:5173，代理 /smart-manage-api → localhost:8080）
pnpm build      # 生产构建（tsc 类型检查 + vite build），默认 base=/，部署到 /ierp 时加 --base=/ierp/
pnpm lint       # ESLint 检查（含 Prettier 格式校验，max-warnings 0）
pnpm format     # Prettier 格式化
```

### 分层结构

```
src/
├── api/            # 接口层（axios 实例 + 拦截器）
├── components/     # 公共组件
├── hooks/          # 自定义 hooks
├── layouts/        # 布局组件（MainLayout → Sider + Header + Content）
├── pages/          # 页面组件
│   └── errors/     # 错误页面（404 等）
├── router/         # 路由配置（MemoryRouter，路由表）
├── stores/         # Zustand 状态（user：用户/token，app：侧边栏/当前模块）
├── styles/         # 样式（theme.less：Arco Less 变量覆盖，global.less：全局重置）
├── types/          # TypeScript 类型（Result<T>、PageResult<T> 等后端响应类型）
├── utils/          # 工具函数
├── App.tsx         # 根组件（ConfigProvider + QueryClient + MemoryRouter）
└── main.tsx        # 入口（挂载 React + Arco Less + 全局样式）
```

### 核心设计模式

**路由方案：** 使用 React Router `MemoryRouter`。浏览器 URL 固定为 `/index.html?app=home`，不随页面内导航变化。`app` 参数决定当前显示的模块，默认 `home`。各应用模块路由通过 `routes` 配置表维护。
**登录页：** `public/login.html` 为独立 HTML 页面，零框架依赖，加载速度快。页面初始化时调用 `/sys/base/captcha` 获取图文验证码，登录时 SM2 加密密码和验证码（C1C3C2 模式，cipherMode=1），调用 `/sys/base/login`。成功后存储 token 到 localStorage 并跳转目标页。支持 `?redirect=` 参数控制登录后跳转地址。
**请求拦截：** axios 实例（`src/api/request.ts`）统一处理：
- 请求拦截器：注入 `satoken` header 从 localStorage
- 响应拦截器：`code !== '0'` 视为业务错误；`code === '401'` 跳转 `/login.html?redirect=...`
- 后端统一响应体对应 `types/api.ts` 中的 `Result<T>` / `PageResult<T>`

**状态管理：** `Zustand` 管理客户端状态（用户信息、token、侧边栏折叠、当前模块）；`TanStack Query` 管理服务端状态（请求缓存、loading/error 自动化）。

**主题定制：**
- `ConfigProvider`（Arco Design）— 运行时主题色切换，不使用暗黑模式
- Less `modifyVars` — 编译时组件级样式定制。vite.config.ts 中 `additionalData` 全局注入 `theme.less`，覆盖 Arco Less 变量即可影响所有组件样式
- 入口 `main.tsx` 引入 `@arco-design/web-react/dist/css/index.less`（非 arco.css），确保 Less 变量覆盖生效

**SM2 加密：** 前端仅涉及 SM2 加密（公钥加密密码和验证码），不引入 SM4。公钥硬编码在 `login.html` 中（与后端 application-dev.yml 的 `smart-manage.sm2.js.public-key` 一致）。加密库使用 npm 包 `sm-crypto`，复制 `dist/sm2.js` 到 `public/js/` 供 login.html 使用。React 应用内部需要 SM2 时可直接 `import` 该 npm 包。

### 技术规范

- 使用 pnpm 进行包管理
- 使用 `@/` 别名引用 `src`
- UI 组件仅使用 Arco Design，禁止引入其它 UI 库
- `id` 必须用字符串存储（后端 Long 雪花 ID 会丢失精度）
- 列表页第一列（number 字段）可点击进入查看详情
- `package.json` 声明 `"type": "module"`，ESLint 使用 ES module 格式

### 样式规范

- 禁止在 tsx 文件中写 CSS 样式，禁止在 style 中写内联 CSS
- 自定义 CSS 类名以 `sm-` 开头（如 `sm-layout`、`sm-header`、`sm-content`）
- 优先使用 flex 布局

### 命名规范

- 禁止单字母变量名
- 页面组件目录：PascalCase（如 `pages/UserList.tsx`）
- 工具/Hook 文件：camelCase（如 `hooks/usePage.ts`）

### Arco Design 参考基线

- Arco Design 官网：https://arco.design/react/docs/start
- Arco Design 主题定制：https://arco.design/react/docs/theme
- VChart 图表库：https://www.visactor.io/vchart/guide/tutorial_docs/VChart_Website_Guide

### playwright-cli 测试

- 使用 playwright-cli 时加上 `--headed` 和 `--persistent` 参数以便观察测试过程
- 因为登录界面需要验证码，如果你需要登录，可以停下来让我输入验证码登录成功后再继续

