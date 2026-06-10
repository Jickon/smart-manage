/**
 * 组件注册表导入文件 — 由 pnpm gen:registry 自动生成，禁止手动修改。
 * 每次构建前自动重新生成。
 */

// src\cloud\sys\cloud\pageRegistration.ts → sys/base/cloud
import '../../sys/cloud/pageRegistration';

// 构建期已校验无重复 key，运行时二次确认
import { validateRegistry } from './componentRegistry';
validateRegistry();
