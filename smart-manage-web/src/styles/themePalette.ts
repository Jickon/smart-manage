export interface ThemeColorOption {
  key: string;
  label: string;
  value: string;
}

/** 基础色板第 6 阶。主题切换只替换品牌色，功能色与中性色保持固定。 */
export const THEME_COLOR_OPTIONS: readonly ThemeColorOption[] = [
  { key: 'pink', label: '粉红色', value: '#F90D58' },
  { key: 'red', label: '红色', value: '#FB2323' },
  { key: 'orange', label: '橙色', value: '#FF5F1F' },
  { key: 'yellow', label: '黄色', value: '#FF991C' },
  { key: 'cyan', label: '明青色', value: '#16B8B1' },
  { key: 'green', label: '绿色', value: '#1BA854' },
  { key: 'lime', label: '青柠色', value: '#77C404' },
  { key: 'gold', label: '亮黄色', value: '#FDC200' },
  { key: 'lightBlue', label: '浅蓝色', value: '#16B0F1' },
  { key: 'blue', label: '蓝色', value: '#276FF5' },
  { key: 'deepBlue', label: '深蓝色', value: '#0E5FD8' },
  { key: 'purple', label: '紫色', value: '#701DF0' },
] as const;

export const DEFAULT_THEME_COLOR = '#276FF5';
export const SM_SAFE_LINK_COLOR = '#0E5FD8';

const supportedThemeColors = new Set(THEME_COLOR_OPTIONS.map((option) => option.value));

export function normalizeThemeColor(themeColor?: string | null): string {
  const normalized = themeColor?.trim().toUpperCase();
  return normalized && supportedThemeColors.has(normalized) ? normalized : DEFAULT_THEME_COLOR;
}
