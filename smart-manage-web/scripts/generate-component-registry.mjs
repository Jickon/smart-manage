import { readdir, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const projectRoot = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const cloudRoot = path.join(projectRoot, 'src', 'cloud');
const outputFile = path.join(cloudRoot, 'common', 'registry', 'componentRegistry.generated.tsx');

async function collectRegistrationFiles(directory) {
  const entries = await readdir(directory, { withFileTypes: true });
  const result = [];

  for (const entry of entries) {
    const absolutePath = path.join(directory, entry.name);
    if (entry.isDirectory()) {
      if (entry.name === 'common') {
        continue;
      }
      result.push(...(await collectRegistrationFiles(absolutePath)));
      continue;
    }
    if (entry.isFile() && (entry.name === 'pageRegistration.ts' || entry.name === 'pageRegistration.tsx')) {
      result.push(absolutePath);
    }
  }

  return result;
}

function toImportPath(absolutePath) {
  const relativePath = path.relative(path.join(projectRoot, 'src'), absolutePath);
  const withoutExtension = relativePath.replace(/\.(ts|tsx)$/, '');
  return `@/${withoutExtension.split(path.sep).join('/')}`;
}

function renderGeneratedFile(registrationFiles) {
  const imports = registrationFiles
    .map((file, index) => `import registration${index} from '${toImportPath(file)}';`)
    .join('\n');
  const registrationNames = registrationFiles.map((_, index) => `registration${index}`);

  return `${imports ? `${imports}\n` : ''}import type { PageRegistration } from '@/cloud/common/page/types';

export const generatedPageRegistrations: PageRegistration[] = [
${registrationNames.map((name) => `  ${name},`).join('\n')}
];
`;
}

const registrationFiles = (await collectRegistrationFiles(cloudRoot)).sort();
await writeFile(outputFile, renderGeneratedFile(registrationFiles), 'utf8');
console.log(`Generated ${path.relative(projectRoot, outputFile)} with ${registrationFiles.length} registration(s).`);
