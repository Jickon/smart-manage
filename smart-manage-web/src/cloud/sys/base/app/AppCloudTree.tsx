import { useMemo } from 'react';
import { Checkbox, Input, Spin, Tree } from '@arco-design/web-react';
import type { CloudSelectVO } from '../cloud/types';
import type { TreeDataType } from '@arco-design/web-react/es/Tree/interface';

const ALL_CLOUD_KEY = '__all_clouds__';

interface AppCloudTreeProps {
  clouds: CloudSelectVO[];
  loading: boolean;
  selectedCloudId?: string;
  includeChildren: boolean;
  showDisabled: boolean;
  keyword: string;
  onKeywordChange: (keyword: string) => void;
  onCloudChange: (cloudId?: string) => void;
  onIncludeChildrenChange: (checked: boolean) => void;
  onShowDisabledChange: (checked: boolean) => void;
}

const AppCloudTree = ({
  clouds,
  loading,
  selectedCloudId,
  includeChildren,
  showDisabled,
  keyword,
  onKeywordChange,
  onCloudChange,
  onIncludeChildrenChange,
  onShowDisabledChange,
}: AppCloudTreeProps) => {
  const treeData: TreeDataType[] = useMemo(
    () => [
      {
        key: ALL_CLOUD_KEY,
        title: '全部云',
        children: clouds.map((cloud) => ({
          key: cloud.id,
          title: `${cloud.name}（${cloud.number}）`,
        })),
      },
    ],
    [clouds],
  );

  return (
    <>
      <div className="sm-list-tree-search">
        <Input.Search
          allowClear
          placeholder="搜索云编码/名称"
          value={keyword}
          onChange={onKeywordChange}
          onSearch={onKeywordChange}
        />
      </div>
      <Spin className="sm-list-tree-body" loading={loading}>
        <Tree
          blockNode
          size="small"
          treeData={treeData}
          selectedKeys={[selectedCloudId ?? ALL_CLOUD_KEY]}
          defaultExpandedKeys={[ALL_CLOUD_KEY]}
          onSelect={(keys) => onCloudChange(keys[0] === ALL_CLOUD_KEY ? undefined : keys[0])}
        />
      </Spin>
      <div className="sm-list-tree-actions">
        <Checkbox checked={includeChildren} onChange={(checked) => onIncludeChildrenChange(Boolean(checked))}>
          包含下级
        </Checkbox>
        <Checkbox checked={showDisabled} onChange={(checked) => onShowDisabledChange(Boolean(checked))}>
          显示停用
        </Checkbox>
      </div>
    </>
  );
};

export default AppCloudTree;
