import { Avatar, Badge, Dropdown, Layout, Menu } from '@arco-design/web-react';
import { IconClose, IconLock, IconNotification, IconSearch, IconUser } from '@arco-design/web-react/icon';
import { useHeaderTabsStore } from '@/stores/headerTabs';
import { useAppWorkspaceStore } from '@/stores/appWorkspace';
import { useUserStore } from '@/stores/user';

const ShellHeader = () => {
  const tabs = useHeaderTabsStore((store) => store.tabs);
  const activeKey = useHeaderTabsStore((store) => store.activeKey);
  const activate = useHeaderTabsStore((store) => store.activate);
  const removeAppTab = useHeaderTabsStore((store) => store.removeAppTab);
  const destroyWorkspace = useAppWorkspaceStore((store) => store.destroyWorkspace);
  const userInfo = useUserStore((store) => store.userInfo);
  const logout = useUserStore((store) => store.logout);

  const handleRemove = (event: React.MouseEvent, key: string) => {
    event.stopPropagation();
    removeAppTab(key);
    destroyWorkspace(key);
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
                <IconClose className="sm-header-tab-operate-close" onClick={(event) => handleRemove(event, tab.key)} />
                <IconLock className="sm-header-tab-operate-lock" />
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
