import { useState } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import { Layout, Menu, Tabs, Button, Dropdown, Avatar, Space } from 'antd';
import type { MenuProps } from 'antd';
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  UserOutlined,
  LogoutOutlined,
} from '@ant-design/icons';
import { useUserStore } from '@/stores/user';
import { useAppStore } from '@/stores/app';
import type { TabItem } from '@/stores/app';
import './MainLayout.css';

const { Header, Sider, Content } = Layout;

export default function MainLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const userInfo = useUserStore((s) => s.userInfo);
  const clearUser = useUserStore((s) => s.clearUser);
  const tabs = useAppStore((s) => s.tabs);
  const activeTabKey = useAppStore((s) => s.activeTabKey);
  const openTab = useAppStore((s) => s.openTab);
  const closeTab = useAppStore((s) => s.closeTab);
  const setActiveTab = useAppStore((s) => s.setActiveTab);
  const navigate = useNavigate();

  const handleLogout = () => {
    clearUser();
    window.location.href = '/login.html';
  };

  const userMenuItems: MenuProps['items'] = [
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: handleLogout,
    },
  ];

  /** 处理 tab 切换 */
  const handleTabChange = (key: string) => {
    setActiveTab(key);
  };

  /** 处理 tab 关闭 */
  const handleTabEdit = (
    targetKey: React.MouseEvent | React.KeyboardEvent | string,
    action: 'add' | 'remove',
  ) => {
    if (action === 'remove' && typeof targetKey === 'string') {
      closeTab(targetKey);
    }
  };

  /** 左侧菜单点击 — 打开新 tab */
  const handleMenuClick: MenuProps['onClick'] = (info) => {
    const tab: TabItem = {
      key: info.key,
      title: info.domEvent.currentTarget?.textContent ?? info.key,
      component: info.key,
      closable: true,
    };
    openTab(tab);
    navigate(`/${info.key}`);
  };

  return (
    <Layout className="sm-main-layout">
      {/* 左侧导航 */}
      <Sider trigger={null} collapsible collapsed={collapsed} theme="dark" width={220}>
        <div className={collapsed ? 'sm-layout-logo sm-layout-logo-collapsed' : 'sm-layout-logo'}>
          {collapsed ? 'SM' : 'Smart Manage'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[activeTabKey]}
          onClick={handleMenuClick}
          items={[
            { key: 'home', icon: <UserOutlined />, label: '首页' },
            // 业务菜单由后端动态加载后合并
          ]}
        />
      </Sider>

      <Layout>
        {/* 顶部 Header */}
        <Header className="sm-layout-header">
          <Button
            type="text"
            icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={() => setCollapsed(!collapsed)}
          />
          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
            <Space className="sm-layout-user">
              <Avatar size="small" icon={<UserOutlined />} />
              <span>{userInfo?.nickname ?? userInfo?.username ?? '用户'}</span>
            </Space>
          </Dropdown>
        </Header>

        {/* 工作台 Tabs */}
        {tabs.length > 0 && (
          <Tabs
            type="editable-card"
            hideAdd
            activeKey={activeTabKey}
            onChange={handleTabChange}
            onEdit={handleTabEdit}
            items={tabs.map((tab) => ({
              key: tab.key,
              label: tab.title,
              closable: tab.closable,
            }))}
            className="sm-layout-tabs"
          />
        )}

        {/* 内容区 */}
        <Content className="sm-layout-content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
