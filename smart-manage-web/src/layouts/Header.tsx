import { Layout, Badge, Avatar, Dropdown, Menu } from '@arco-design/web-react';
import {IconSearch, IconNotification, IconClose, IconUser, IconLock} from '@arco-design/web-react/icon';
import { useHeaderTabsStore } from '@/stores/headerTabs';
import { useUserStore } from '@/stores/user';

const ShellHeader = () => {
  const tabs = useHeaderTabsStore((s) => s.tabs);
  const activeKey = useHeaderTabsStore((s) => s.activeKey);
  const activate = useHeaderTabsStore((s) => s.activate);
  const removeAppTab = useHeaderTabsStore((s) => s.removeAppTab);
  const userInfo = useUserStore((s) => s.userInfo);
  const logout = useUserStore((s) => s.logout);

  const handleRemove = (e: React.MouseEvent, key: string) => {
    e.stopPropagation();
    removeAppTab(key);
  };

  const handleLogout = () => {
    logout();
    window.location.href = '/login.html';
  };

  return (
    <Layout.Header className="sm-header">
      <div className="sm-header-logo">Smart Manage</div>
      <div className="sm-header-tabs">
        {tabs.map((tab) => (
          <div
            key={tab.key}
            className={`sm-header-tab ${activeKey === tab.key ? 'sm-header-tab--active' : ''}`}
            onClick={() => activate(tab.key)}
          >
            <span>{tab.label}</span>
            {tab.closable && (
                <div className="sm-header-tab-operate">
                    <IconClose className="sm-header-tab-operate-close" onClick={(e) => handleRemove(e, tab.key)} />
                    <IconLock className="sm-header-tab-operate-lock"/>
                </div>
            )}
          </div>
        ))}
      </div>
      <div className="sm-header-actions">
        <IconSearch className="sm-header-action-btn" />
        <Badge count={0}>
          <IconNotification className="sm-header-action-btn" />
        </Badge>
        <Dropdown
          trigger="click"
          droplist={
            <Menu onClickMenuItem={handleLogout}>
              <Menu.Item key="logout">退出登录</Menu.Item>
            </Menu>
          }
        >
          <Avatar size={32} className="sm-header-avatar">
            {userInfo?.nickname?.[0] || <IconUser />}
          </Avatar>
        </Dropdown>
      </div>
    </Layout.Header>
  );
};

export default ShellHeader;
