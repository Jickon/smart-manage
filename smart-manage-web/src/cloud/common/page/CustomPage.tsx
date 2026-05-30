import { Empty } from '@arco-design/web-react';
import type { CommonPageProps } from '@/cloud/common/page/types';

const CustomPage = ({ title, actions, children }: CommonPageProps) => {
  return (
    <section className="sm-common-page sm-custom-page">
      <header className="sm-common-page-header">
        <h2 className="sm-common-page-title">{title}</h2>
        {actions && <div className="sm-common-page-actions">{actions}</div>}
      </header>
      <div className="sm-custom-page-body">{children ?? <Empty description="暂无页面配置" />}</div>
    </section>
  );
};

export default CustomPage;
