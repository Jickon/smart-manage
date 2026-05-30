import { useState } from 'react';
import { Button, Empty } from '@arco-design/web-react';
import { IconDown, IconUp } from '@arco-design/web-react/icon';
import type { EditPageProps } from '@/cloud/common/page/types';

const EditPage = ({ sections, attachment, toolbarActions, onSave, onSubmit, onCancel, children }: EditPageProps) => {
  const initialCollapsed = () =>
    Object.fromEntries((sections ?? []).map((section) => [section.key, Boolean(section.defaultCollapsed)]));
  const [collapsedMap, setCollapsedMap] = useState<Record<string, boolean>>(initialCollapsed);

  const toggleSection = (key: string) => {
    setCollapsedMap((current) => ({ ...current, [key]: !current[key] }));
  };

  return (
    <section className="sm-common-page sm-edit-page">
      <header className="sm-edit-toolbar">
        <div className="sm-edit-toolbar-actions">
          <Button type="primary" onClick={onSave}>
            保存
          </Button>
          <Button type="primary" onClick={onSubmit}>
            提交
          </Button>
          {toolbarActions}
          <Button type="primary" onClick={onCancel}>
            退出
          </Button>
        </div>
      </header>
      <div className="sm-edit-page-body">
        {sections?.length
          ? sections.map((section) => {
              const collapsed = collapsedMap[section.key];
              return (
                <section
                  key={section.key}
                  className={`sm-edit-section ${collapsed ? 'sm-edit-section--collapsed' : ''}`}
                >
                  <button className="sm-edit-section-header" type="button" onClick={() => toggleSection(section.key)}>
                    <span className="sm-edit-section-title">
                      {collapsed ? <IconDown /> : <IconUp />}
                      <span>{section.title}</span>
                    </span>
                    {!collapsed && section.extra && (
                      <span className="sm-edit-section-header-extra">{section.extra}</span>
                    )}
                  </button>
                  {!collapsed && <div className="sm-edit-section-body">{section.content}</div>}
                </section>
              );
            })
          : (children ?? <Empty description="暂无表单配置" />)}
        {attachment}
      </div>
    </section>
  );
};

export default EditPage;
