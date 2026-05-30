import { Button, Empty } from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import type { ReactNode } from 'react';

interface EditableEntryTableProps {
  table?: ReactNode;
  summary?: ReactNode;
  onAddRow?: () => void;
  onDeleteRow?: () => void;
}

const EditableEntryTable = ({ table, summary, onAddRow, onDeleteRow }: EditableEntryTableProps) => {
  return (
    <div className="sm-entry-table">
      <div className="sm-entry-table-toolbar">
        <div className="sm-entry-table-summary">{summary}</div>
        <div className="sm-entry-table-actions">
          <Button size="small" icon={<IconPlus />} onClick={onAddRow}>
            增行
          </Button>
          <Button size="small" icon={<IconDelete />} onClick={onDeleteRow}>
            删行
          </Button>
        </div>
      </div>
      <div className="sm-entry-table-body">{table ?? <Empty description="暂无明细配置" />}</div>
    </div>
  );
};

export default EditableEntryTable;
