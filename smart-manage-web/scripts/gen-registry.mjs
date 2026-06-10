/**
 * 自动生成组件注册表导入文件。
 *
 * 扫描 src/cloud/**\/pageRegistration.ts，生成 src/cloud/common/registry/registry.gen.ts。
 * 检测重复 componentKey 时直接中止构建。
 *
 * 用法：node scripts/gen-registry.mjs
 */

import { readFileSync, writeFileSync } from 'node:fs';
import { readdir } from 'node:fs/promises';
import { join, relative, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = dirname(fileURLToPath(import.meta.url));
const ROOT = join(__dirname, '..');
const SRC = join(ROOT, 'src');
const OUTPUT = join(SRC, 'cloud', 'common', 'registry', 'registry.gen.ts');

const HEADER = `/**
 * 组件注册表导入文件 — 由 pnpm gen:registry 自动生成，禁止手动修改。
 * 每次构建前自动重新生成。
 */
`;

/** 递归扫描目录，收集所有 pageRegistration 文件 */
async function discoverRegistrations(dir) {
  const results = [];
  const entries = await readdir(dir, { withFileTypes: true });
  for (const entry of entries) {
    const fullPath = join(dir, entry.name);
    if (entry.isDirectory()) {
      // 跳过非业务目录
      if (entry.name === 'node_modules' || entry.name === 'common') continue;
      const sub = await discoverRegistrations(fullPath);
      results.push(...sub);
    } else if (entry.name === 'pageRegistration.ts' || entry.name === 'pageRegistration.tsx') {
      results.push(fullPath);
    }
  }
  return results;
}

/** 从 pageRegistration 文件中提取 componentKey */
function extractComponentKey(filePath, content) {
  // 匹配 definePageRegistration('componentKey', ...) 或 definePageRegistration("componentKey", ...)
  const match = content.match(
    /definePageRegistration\s*\(\s*['"]([^'"]+)['"]/,
  );
  return match ? match[1] : null;
}

async function main() {
  console.log('[gen:registry] 扫描 pageRegistration 文件...');

  const cloudDir = join(SRC, 'cloud');
  const files = await discoverRegistrations(cloudDir);
  files.sort();

  if (files.length === 0) {
    console.log('[gen:registry] 未发现 pageRegistration 文件，生成空注册表。');
    writeFileSync(OUTPUT, HEADER + '\n// 暂无注册页面\nexport {};\n', 'utf-8');
    return;
  }

  console.log(`[gen:registry] 发现 ${files.length} 个页面注册文件`);

  // 提取 componentKey 并检测重复
  const seen = new Map(); // componentKey → filePath
  const imports = [];

  for (const filePath of files) {
    const content = readFileSync(filePath, 'utf-8');
    const key = extractComponentKey(filePath, content);

    if (!key) {
      console.error(
        `[gen:registry] 错误：${relative(ROOT, filePath)} 中未找到有效的 componentKey`,
      );
      process.exit(1);
    }

    if (seen.has(key)) {
      console.error(
        `[gen:registry] 错误：componentKey "${key}" 重复！\n` +
          `  文件1: ${relative(ROOT, seen.get(key))}\n` +
          `  文件2: ${relative(ROOT, filePath)}`,
      );
      process.exit(1);
    }

    seen.set(key, filePath);

    const relativePath = relative(dirname(OUTPUT), filePath)
      .replace(/\\/g, '/')
      .replace(/\.tsx?$/, '');

    imports.push({
      key,
      path: relativePath,
      file: relative(ROOT, filePath),
    });
  }

  // 生成导入文件
  const lines = [HEADER];
  for (const imp of imports) {
    lines.push(`// ${imp.file} → ${imp.key}`);
    lines.push(`import '${imp.path}';`);
    lines.push('');
  }

  // 生成运行时去重校验代码
  lines.push('// 构建期已校验无重复 key，运行时二次确认');
  lines.push("import { validateRegistry } from './componentRegistry';");
  lines.push('validateRegistry();');

  const output = lines.join('\n') + '\n';
  writeFileSync(OUTPUT, output, 'utf-8');

  console.log(`[gen:registry] 已生成 ${relative(ROOT, OUTPUT)} (${imports.length} 个注册项)`);

  // 输出注册清单
  for (const imp of imports) {
    console.log(`  - ${imp.key} ← ${imp.file}`);
  }
}

main().catch((err) => {
  console.error('[gen:registry] 错误：', err);
  process.exit(1);
});
