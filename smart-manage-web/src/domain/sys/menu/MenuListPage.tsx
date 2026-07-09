import { useState, useMemo, useCallback } from 'react';
import { Button, Tag, Tree, Modal, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import type { DataNode } from 'antd/es/tree';
import { useQuery } from '@tanstack/react-query';
import ListPage from '@/domain/common/page/ListPage';
import { useListPageQuery } from '@/domain/common/page/useListPageQuery';
import { useWorkbenchStore } from '@/stores/workbench';
import { OperationType } from '@/domain/common/page/types';
import { fetchAppsAll } from '@/domain/sys/app/api';
import { menuApi } from './api';
import type { MenuListVO } from './types';
import type { PageComponentProps } from '@/domain/common/page/types';

/** 菜单编辑页 componentKey */
const MENU_EDIT_KEY = 'sys/base/menu/edit';

/** 树节点 key 格式：app:{appId} | cat:{id} | page:{id} */
function nodeKey(prefix: string, id: string | number) {
  return `${prefix}:${id}`;
}

/**
 * 将 MenuTreeVO 列表构建为 2 级树（CATEGORY → PAGE），前端按 parentId 组装。
 */
interface TreeMenuNode {
  id: string;
  number: string;
  name: string;
  level: number;
  parentId: string;
  children: TreeMenuNode[];
}

function buildMenuTree(
  menus: { id: string; number: string; name: string; level: number; parentId: string }[],
): TreeMenuNode[] {
  const categories = menus.filter((m) => m.level === 2);
  const pages = menus.filter((m) => m.level === 3);
  return categories.map((cat) => ({
    ...cat,
    children: pages
      .filter((p) => p.parentId === cat.id)
      .map((p) => ({ ...p, children: [] as TreeMenuNode[] })),
  }));
}

/** 菜单列表页 — 左树右表 */
const MenuListPage = (props: PageComponentProps) => {
  const [selectedNodeKey, setSelectedNodeKey] = useState<string | undefined>(undefined);
  // 当前选中节点信息：{ type: 'app'|'cat', id }
  const [selectedFilter, setSelectedFilter] = useState<{ type: string; id: string } | null>(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const openBillTab = useWorkbenchStore((s) => s.openBillTab);
  const openAddNewTab = useWorkbenchStore((s) => s.openAddNewTab);

  // 加载应用列表 + 每个应用的菜单树
  const appsQuery = useQuery({
    queryKey: ['cloud-apps-all'],
    queryFn: fetchAppsAll,
    staleTime: 5 * 60 * 1000,
  });

  // 按需加载菜单（需展开应用时触发：默认先加载第一个应用）
  const menuTreesQuery = useQuery({
    queryKey: ['menu-trees'],
    queryFn: async () => {
      if (!appsQuery.data) return new Map<string, TreeMenuNode[]>();
      const map = new Map<string, TreeMenuNode[]>();
      for (const cloud of appsQuery.data) {
        for (const app of cloud.appList) {
          const menus = await menuApi.listByApp(app.id);
          map.set(app.id, buildMenuTree(menus));
        }
      }
      return map;
    },
    enabled: !!appsQuery.data,
    staleTime: 5 * 60 * 1000,
  });

  // 右侧列表
  const { records, total, pageNum, pageSize, keyword, query, onSearch, onPageChange, onRefresh } =
    useListPageQuery({
      queryKey: ['menu-list', selectedFilter?.type ?? '', selectedFilter?.id ?? ''],
      queryFn: (params) => {
        const filter: { appId?: string; parentId?: string } = {};
        if (selectedFilter?.type === 'app') filter.appId = selectedFilter.id;
        if (selectedFilter?.type === 'cat') filter.parentId = selectedFilter.id;
        return menuApi.listPage({ ...params, ...filter });
      },
    });

  // 构建左侧树
  const treeData: DataNode[] = useMemo(() => {
    if (!appsQuery.data || !menuTreesQuery.data) return [];
    const nodes: DataNode[] = [];
    for (const cloud of appsQuery.data) {
      const cloudChildren: DataNode[] = [];
      for (const app of cloud.appList) {
        const menuTree = menuTreesQuery.data.get(app.id) ?? [];
        const appNode: DataNode = {
          key: nodeKey('app', app.id),
          title: app.name,
          children: menuTree.map((cat) => ({
            key: nodeKey('cat', cat.id),
            title: `${cat.name} (${cat.children.length})`,
            isLeaf: false,
            children: cat.children.map((page) => ({
              key: nodeKey('page', page.id),
              title: page.name,
              isLeaf: true,
            })),
          })),
        };
        cloudChildren.push(appNode);
      }
      if (cloudChildren.length > 0) {
        nodes.push({
          key: nodeKey('cloud', cloud.id),
          title: cloud.name,
          children: cloudChildren,
        });
      }
    }
    return nodes;
  }, [appsQuery.data, menuTreesQuery.data]);

  const handleTreeSelect = useCallback((keys: React.Key[]) => {
    if (keys.length === 0) {
      setSelectedFilter(null);
      setSelectedNodeKey(undefined);
      return;
    }
    const key = String(keys[0]);
    setSelectedNodeKey(key);

    const [type, id] = key.split(':') as [string, string];
    if (type === 'app') {
      setSelectedFilter({ type: 'app', id });
    } else if (type === 'cat') {
      setSelectedFilter({ type: 'cat', id });
    } else if (type === 'page') {
      setSelectedFilter({ type: 'page', id });
    } else {
      // cloud level — show nothing specific
      setSelectedFilter(null);
    }
  }, []);

  const handleOpenEdit = useCallback(
    (id: string) => {
      openBillTab(props.appNumber, MENU_EDIT_KEY, '编辑菜单', id, OperationType.EDIT);
    },
    [props.appNumber, openBillTab],
  );

  const handleOpenAdd = useCallback(() => {
    openAddNewTab(props.appNumber, MENU_EDIT_KEY, '新增菜单');
  }, [props.appNumber, openAddNewTab]);

  const handleDelete = useCallback(() => {
    if (selectedRowKeys.length === 0) return;
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除选中的 ${selectedRowKeys.length} 条记录吗？`,
      okText: '删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        await Promise.all(selectedRowKeys.map((id) => menuApi.delete(String(id))));
        message.success('删除成功');
        setSelectedRowKeys([]);
        query.refetch();
      },
    });
  }, [selectedRowKeys, query]);

  const columns: ColumnsType<MenuListVO> = [
    {
      title: '编码',
      dataIndex: 'number',
      width: 120,
      render: (text, record) => (
        <Button type="link" size="small" onClick={() => handleOpenEdit(record.id)}>
          {text || '-'}
        </Button>
      ),
    },
    { title: '名称', dataIndex: 'name', width: 180 },
    {
      title: '层级',
      dataIndex: 'level',
      width: 80,
      render: (val) => (val === 2 ? <Tag color="blue">分组</Tag> : <Tag color="green">页面</Tag>),
    },
    { title: '路径', dataIndex: 'path', width: 180, ellipsis: true },
    { title: '组件', dataIndex: 'component', width: 200, ellipsis: true },
    { title: '排序', dataIndex: 'sort', width: 60 },
  ];

  const loading =
    query.isLoading ||
    appsQuery.isLoading ||
    (menuTreesQuery.isLoading && menuTreesQuery.fetchStatus !== 'idle');
  const error = query.error || appsQuery.error || menuTreesQuery.error;

  const treePanel = (
    <div className="sm-list-tree-panel-inner">
      <Tree
        treeData={treeData}
        showLine={false}
        blockNode
        defaultExpandedKeys={[]}
        selectedKeys={selectedNodeKey ? [selectedNodeKey] : []}
        onSelect={handleTreeSelect}
      />
    </div>
  );

  return (
    <ListPage<MenuListVO>
      {...props}
      title="菜单管理"
      loading={loading ? true : false}
      error={error as Error | null}
      onRetry={() => {
        query.refetch();
        appsQuery.refetch();
        menuTreesQuery.refetch();
      }}
      total={total}
      pageNum={pageNum}
      pageSize={pageSize}
      quickSearchPlaceholder="搜索名称/路径"
      filterSummary={keyword ? `关键字：${keyword}` : undefined}
      treePanel={treePanel}
      onAddNew={handleOpenAdd}
      onDelete={handleDelete}
      onRefresh={onRefresh}
      onQuickSearch={onSearch}
      onPageChange={onPageChange}
      rowKey="id"
      columns={columns}
      dataSource={records}
      selectMode="checkbox"
      selectedRowKeys={selectedRowKeys}
      onSelectChange={(keys) => setSelectedRowKeys(keys)}
    />
  );
};

export default MenuListPage;
