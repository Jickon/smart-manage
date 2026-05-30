import { Button, Empty, Upload } from '@arco-design/web-react';
import { IconUpload } from '@arco-design/web-react/icon';

interface AttachmentPanelProps {
  title?: string;
}

const AttachmentPanel = ({ title = '附件' }: AttachmentPanelProps) => {
  return (
    <section className="sm-attachment-panel">
      <header className="sm-attachment-panel-header">
        <span>{title}</span>
        <Upload showUploadList={false}>
          <Button size="small" icon={<IconUpload />}>
            上传文件
          </Button>
        </Upload>
      </header>
      <div className="sm-attachment-panel-body">
        <Empty description="暂无附件" />
      </div>
    </section>
  );
};

export default AttachmentPanel;
