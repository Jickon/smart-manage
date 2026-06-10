import { Avatar, Dropdown, Modal } from 'antd';
import type { MenuProps } from 'antd';
import { UserOutlined, LogoutOutlined } from '@ant-design/icons';
import { useHeaderTabsStore } from '@/stores/headerTabs';
import { useWorkbenchStore } from '@/stores/workbench';
import { useUserStore } from '@/stores/user';
import { openApp, closeAppAndRemove } from '@/services/navigationService';
import './Header.css';

const Header = () => {
  const tabs = useHeaderTabsStore((s) => s.tabs);
  const activeKey = useHeaderTabsStore((s) => s.activeKey);
  const userInfo = useUserStore((s) => s.userInfo);
  const clearUser = useUserStore((s) => s.clearUser);

  const handleTabClick = (key: string) => {
    openApp(key);
  };

  const handleRemove = async (event: React.MouseEvent, key: string) => {
    event.stopPropagation();
    await closeAppAndRemove(key);
  };

  const handleLogout = async () => {
    const allowed = await useWorkbenchStore.getState().checkAllDirty();
    if (!allowed) {
      Modal.confirm({
        title: '有未保存的数据',
        content: '部分页面存在未保存的修改，退出登录将丢失这些数据。确定退出吗？',
        okText: '确定退出',
        cancelText: '取消',
        okButtonProps: { danger: true },
        onOk: () => {
          clearUser();
          window.location.href = '/login.html';
        },
      });
      return;
    }
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
      <nav className="sm-header-tabs" role="tablist" aria-label="应用切换">
        {tabs.map((tab) => (
          <div
            key={tab.key}
            role="tab"
            tabIndex={0}
            aria-selected={activeKey === tab.key}
            className={`sm-header-tab ${activeKey === tab.key ? 'sm-header-tab--active' : ''}`}
            onClick={() => handleTabClick(tab.key)}
            onKeyDown={(e) => {
              if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                handleTabClick(tab.key);
              }
            }}
          >
            <span>{tab.label}</span>
            {tab.closable && (
              <div className="sm-header-tab-operate">
                <button
                  className="sm-header-tab-operate-close"
                  onClick={(event) => handleRemove(event, tab.key)}
                  aria-label={`关闭 ${tab.label}`}
                >
                  ✕
                </button>
              </div>
            )}
          </div>
        ))}
      </nav>

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
