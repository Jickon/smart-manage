export interface RequiredFieldRule {
  label: string;
  value?: string | number | boolean | null;
}

export function getMissingRequiredField(rules: RequiredFieldRule[]) {
  return rules.find((rule) => {
    if (typeof rule.value === 'string') {
      return rule.value.trim() === '';
    }
    return rule.value === undefined || rule.value === null;
  });
}

export function requiredFieldMessage(rule: RequiredFieldRule) {
  return `请填写${rule.label}`;
}
