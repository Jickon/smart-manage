# AGENTS.md

## 基本约定

- 你是一名架构师。
- 使用简体中文沟通。
- 如果有不明确的地方必须停下来问我。
- 代码改动应从全局架构考虑，不能偷懒而破坏项目架构设计。
- 系统处于架构搭建阶段，所有需求修改不要兼容旧逻辑，不要做冗余兜底代码，应让问题暴露出来。
- 关键代码或特殊处理的代码必须给出中文注释。
- 如果任务复杂时，自动启动计划模式。
- 如果你尝试了各种方式都访问不了网页或者获取不了想要的内容的时候，使用 /playwright-cli 打开浏览器获取数据。

## 技术栈

| 层级                        | 技术                                                                   |
|---------------------------|----------------------------------------------------------------------|
| **后端** (smart-manage-api) | Spring Boot 3, Java 21, MyBatis-Flex, Druid, Sa-Token, PostgreSQL    |
| **前端** (smart-manage-web) | Vite, React 19, TypeScript, Ant Design, TanStack Query, Zustand, Zod |

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
- Service 禁止用 `return null` 表达业务失败。资源不存在、状态非法、无权限、参数不合法等场景必须抛出明确异常，让 `GlobalExceptionHandler` 统一返回。
- JSON 反序列化、ID 转换等基础设施禁止静默吞错。比如 Long 解析失败不能返回 `null`，应暴露为参数异常。
- 标准业务接口语义：`listPage` 返回分页；`detail` 找不到应抛异常；`createNewData` 只返回新增默认值且不返回 id；`save` 负责新增和暂存修改；`submit` 负责提交并推进单据状态；`delete` 负责删除或作废，具体语义由单据类型明确。
- 修改后端代码后至少执行 `mvn compile`。如果涉及 APT 表对象、实体、Mapper 或配置变更，也需要确认 MyBatis-Flex 生成类可正常编译。

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

- 当前前端项目处于从零开始重构阶段，以下的说明是以前的，可能与已有的代码不匹配。

### 构建与运行

```bash
cd smart-manage-web

pnpm dev        # 开发（localhost:8000，代理 /smart-manage-api → localhost:8080）
pnpm build      # tsc 类型检查 + vite build
pnpm preview    # 预览构建产物
pnpm lint       # ESLint 检查（max-warnings 0）
pnpm lint:fix   # ESLint 自动修复
pnpm format     # Prettier 格式化
pnpm format:check # Prettier 格式校验
```

### 分层结构

```
src/
├── api/            # 接口层（axios 实例 + 拦截器）
├── assets/         # 静态文件
├── layouts/        # 布局组件（MainLayout → Sider + Header + Content）
├── cloud/          # 领域/应用/单据页面（如 cloud/sys/base/user）
│   └── common/     # 前端领域公共能力（页面框架、组件注册表等）
├── pages/          # 非业务页面组件
│   └── errors/     # 错误页面（404 等）
├── router/         # 路由配置（MemoryRouter，路由表）
├── stores/         # Zustand 状态（user：用户/token，app：侧边栏/当前模块）
├── styles/         # 样式（global.css：全局重置）
├── types/          # TypeScript 类型（Result<T>、PageResult<T> 等后端响应类型）
├── utils/          # 工具函数
├── App.tsx         # 根组件（ConfigProvider + QueryClient + RouterProvider）
└── main.tsx        # 入口（挂载 React + 全局样式）
```

### 核心设计模式

**路由方案：** 使用 React Router `MemoryRouter`。浏览器 URL 固定为 `/index.html?app=home`，不随页面内导航变化。`app` 参数决定当前显示的模块，默认 `home`。各应用模块路由通过 `routes` 配置表维护。
**ERP 双页签：** 前端参考金蝶苍穹/星瀚设计理念，采用两层 tabs。Header tabs 用于切换应用（app）；应用工作台内部 tabs 用于切换业务单据/页面。已打开 tab 不卸载 DOM，只通过显隐控制，保留查询条件、表单状态、滚动位置等现场。
**登录页：** `public/login.html` 为独立 HTML 页面，零框架依赖，加载速度快。页面初始化时调用 `/sys/base/captcha` 获取图文验证码，登录时 SM2 加密密码和验证码（C1C3C2 模式，cipherMode=1），调用 `/sys/base/login`。成功后存储 token 到 localStorage 并跳转目标页。支持 `?redirect=` 参数控制登录后跳转地址。
**请求拦截：** axios 实例（`src/api/request.ts`）统一处理：

- 请求拦截器：注入 `smtoken` header 从 localStorage
- 响应拦截器：`code !== 200` 视为业务错误；`code === 401` 跳转 `/login.html?redirect=...`
- 后端统一响应体对应 `types/api.ts` 中的 `Result<T>` / `PageResult<T>`

**状态管理：** `Zustand` 管理客户端状态（用户信息、token、侧边栏折叠、当前模块）；`TanStack Query` 管理服务端状态（请求缓存、loading/error 自动化）。

**主题定制：**
- `ConfigProvider`（Ant Design）— 运行时通过 `theme.token` 定制主题色、圆角等全局样式，不使用暗黑模式
- Ant Design 6 使用 CSS-in-JS（`@ant-design/cssinjs`），无需 Less 变量覆盖
- 入口 `main.tsx` 无需额外引入样式文件，antd 组件自动按需加载样式

**SM2 加密：** 前端仅涉及 SM2 加密（公钥加密密码和验证码），不引入 SM4。公钥硬编码在 `login.html` 中（与后端 application-dev.yml 的 `smart-manage.sm2.js.public-key` 一致）。加密库使用 npm 包 `sm-crypto`，复制 `dist/sm2.js` 到 `public/js/` 供 login.html 使用。React 应用内部需要 SM2 时可直接 `import` 该 npm 包。

### 页面架构

- 前端业务页面目录与后端保持一致，按 `src/cloud/{领域}/{应用}/{单据}` 分层，例如 `src/cloud/sys/base/user`。
- 菜单表中的 `component` 使用稳定业务键，例如 `sys/base/user/list`、`sys/base/file-config/custom`。后端只存菜单元数据，前端通过组件注册表把 `component` 映射为真实组件。
- 组件注册表是前端白名单，由 `pnpm gen:registry` 扫描 `src/cloud/**/pageRegistration.ts` 或 `.tsx` 生成。新增业务页面时在页面目录新增 `pageRegistration.ts` 并默认导出 `definePageRegistration(...)` 的结果，禁止根据后端字符串任意动态加载组件。
- 标准页面类型：
  - `ListPage`：列表页，负责查询、分页、过滤、行操作、打开单据页。
  - `EditPage`：单据新增、编辑、查看页，由 `OperationType` 控制页面能力。
  - `CustomPage`：非标准列表/编辑形态的业务页面兜底，例如文件存储配置、图表报表、监控页、SQL 控制台、复杂操作页。页面类型值使用 `CUSTOM`，避免与应用工作台概念混淆。
- `OperationType` 统一使用正确拼写，基础值为 `ADDNEW`、`EDIT`、`VIEW`。
- 列表页默认第一列为可点击列，优先使用 `number` 字段；日志等没有 `number` 的页面可使用 `id` 或更合适的业务标识。
- 列表页可按需启用左树右表形态：左侧树面板作为 `ListPage` 的 `treePanel` 传入，右侧继续使用过滤区、按钮区和表格区。左右分割可使用
  Ant Design 的 `Splitter` 组件。
- 列表页原则上不提供独立“编辑/查看”按钮。点击单据后，根据单据状态推导 `OperationType`：暂存状态进入 `EDIT`；已提交、审核通过等状态进入 `VIEW`。字段可见性、锁定性、顶部按钮显隐由单据状态和 `OperationType` 共同决定。
- 业务单据默认应具备单据状态字段，用于驱动编辑页能力控制。日志、监控记录、单条配置等非业务单据不强制加入单据状态，避免把所有表都套进单据模型。单据状态建议使用 `char(1)` 存储：`A` 暂存（`SAVED`）、`B` 已提交（`SUBMITTED`）、`C` 审核通过（`AUDITED`）、`D` 已关闭（`CLOSED`）。注意使用正确英文拼写 `SUBMITTED`，不要使用 `SUBMITED`。
- 前端 `EditPage` 需要区分“保存”和“提交”两个按钮。保存仅保存暂存数据，单据保持 `SAVED`，后续仍可修改；提交将单据状态改为 `SUBMITTED`，提交后不可再普通保存或编辑，后续可扩展消息通知、审批流程等业务动作。
- 后端 `save` 负责新增和修改暂存数据；前端可以只有一个保存按钮触发 `save`，但 Service 内部必须明确区分新增 insert 和修改 update 流程。新增由 `id == null` 判定，修改由 `id != null` 判定，不能相信前端传来的 id 或状态来决定数据库行为。
- 后端 `submit` 负责提交单据并推进状态。提交时必须以后端数据库当前状态为准校验，通常只允许 `SAVED` 状态提交为 `SUBMITTED`。
- 需要快速限制“只有新增或暂存单据才允许保存”的场景时，可设计注解式校验（如 `@BillSaveCheck`），但校验依据必须来自数据库当前状态；不是所有单据都强制使用该注解。
- tab key 规则：列表页单实例；编辑页按单据 id 多实例；新增页每次打开新的临时实例。编辑/查看同一单据必须共用同一个 tab key，不能出现同一单据同时打开编辑和查看两个 tab。
- 新增页使用前端生成的 uuid 作为临时 tab key。`createNewData` 不返回 id；保存成功后，后端返回真实 id，前端立即调用 `detail(id)` 回显完整数据，并把临时 tab key 替换为真实单据 tab key。保存成功后不关闭 `EditPage`，继续留在当前单据页面。
- 常规 `EditPage` 挂在业务单据 tab 中。字段很少的单据可采用居中 Modal 编辑形态：顶部左侧标题、右侧关闭，中间内容，底部取消和保存按钮。
- `ListPage`、`EditPage` 需要抽取通用界面。
- `t_sys_cloud`、`t_sys_app` 是当前标准列表/编辑页面的示例单据；其中 `t_sys_app` 用作左树右表示例。

### 技术规范

- 使用 pnpm 进行包管理
- 使用 `@/` 别名引用 `src`
- UI 组件仅使用 Ant Design，禁止引入其它 UI 库
- `id` 必须用字符串存储（后端 Long 雪花 ID 会丢失精度）
- `package.json` 声明 `"type": "module"`，ESLint 使用 ES module 格式
- 修改前端代码后至少执行 `pnpm lint`、`pnpm format:check`、`pnpm build`。只有需要自动修复格式或用户明确要求时，才执行 `pnpm lint:fix` 或 `pnpm format`。
- eslint报错时，只有在用户允许的情况下才可以使用注释跳过校验和修改eslint.config.js文件

### 样式规范

- 禁止在 tsx 文件中写 CSS 样式，禁止在 style 中写内联 CSS
- 自定义 CSS 类名以 `sm-` 开头（如 `sm-layout`、`sm-header`、`sm-content`）
- 优先使用 flex 布局

### 命名规范

- 禁止单字母变量名
- 页面组件目录：PascalCase（如 `pages/UserList.tsx`）
- 工具/Hook 文件：camelCase（如 `hooks/usePage.ts`）

### playwright-cli 测试

- 使用 playwright-cli 时加上 `--headed` 和 `--persistent` 参数以便观察测试过程
- 因为登录界面需要验证码，如果你需要登录，可以停下来让我输入验证码登录成功后再继续
