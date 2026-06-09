import { Avatar, Dropdown } from 'antd';
import type { MenuProps } from 'antd';
import { UserOutlined, LogoutOutlined } from '@ant-design/icons';
import { useHeaderTabsStore } from '@/stores/headerTabs';
import { useWorkbenchStore } from '@/stores/workbench';
import { useUserStore } from '@/stores/user';
import './Header.css';

const Header = () => {
  const tabs = useHeaderTabsStore((s) => s.tabs);
  const activeKey = useHeaderTabsStore((s) => s.activeKey);
  const activate = useHeaderTabsStore((s) => s.activate);
  const removeAppTab = useHeaderTabsStore((s) => s.removeAppTab);
  const destroyWorkspace = useWorkbenchStore((s) => s.destroyWorkspace);
  const userInfo = useUserStore((s) => s.userInfo);
  const clearUser = useUserStore((s) => s.clearUser);

  const handleRemove = (event: React.MouseEvent, key: string) => {
    event.stopPropagation();
    removeAppTab(key);
    destroyWorkspace(key);
  };

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

  return (
    <header className="sm-header">
      <div className="sm-header-logo">Smart Manage</div>

      {/* Header Tabs */}
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
                <span
                  className="sm-header-tab-operate-close"
                  onClick={(event) => handleRemove(event, tab.key)}
                >
                  ✕
                </span>
              </div>
            )}
          </div>
        ))}
      </div>

      {/* 右侧操作区 */}
      <div className="sm-header-actions">
        <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
          <Avatar size={32} className="sm-header-avatar" icon={<UserOutlined />}>
            {userInfo?.nickname?.[0]}
          </Avatar>
        </Dropdown>
      </div>
    </header>
  );
};

export default Header;
