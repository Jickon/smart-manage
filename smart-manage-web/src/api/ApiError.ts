/** API 业务错误 — 保留后端完整错误信息，便于页面按 code 区分权限/校验/冲突等场景 */
export class ApiError extends Error {
  /** 后端业务状态码（对应 Result.code） */
  code: number;
  /** 请求追踪 ID，便于日志排查 */
  traceId: string;
  /** 后端返回的附加数据（校验错误详情等） */
  data?: unknown;

  constructor(code: number, msg: string, traceId: string, data?: unknown) {
    super(msg);
    this.name = 'ApiError';
    this.code = code;
    this.traceId = traceId;
    this.data = data;
  }
}
