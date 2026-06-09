import { HomeOutlined } from '@ant-design/icons';
import { useWorkbenchStore } from '@/stores/workbench';

interface Props {
  appNumber: string;
}

const ContentTabsBar = ({ appNumber }: Props) => {
  const ws = useWorkbenchStore((s) => s.workspaces[appNumber]);
  const activateContentTab = useWorkbenchStore((s) => s.activateContentTab);
  const removeContentTab = useWorkbenchStore((s) => s.removeContentTab);

  if (!ws) return null;

  const { contentTabs, activeContentTabKey } = ws;

  const handleRemove = (event: React.MouseEvent, key: string) => {
    event.stopPropagation();
    removeContentTab(appNumber, key);
  };

  return (
    <div className="sm-content-tabs-bar">
      <div className="sm-content-tabs">
        {contentTabs.map((tab) => {
          const isHome = tab.key === '__home__';
          return (
            <div
              key={tab.key}
              className={`sm-content-tab ${isHome ? 'sm-content-tab-home' : ''} ${activeContentTabKey === tab.key ? 'sm-content-tab--active' : ''}`}
              onClick={() => activateContentTab(appNumber, tab.key)}
            >
              {isHome ? (
                <HomeOutlined style={{ fontSize: '18px' }} />
              ) : (
                <>
                  <span>{tab.label}</span>
                  {tab.closable && (
                    <span
                      className="sm-content-tab-close"
                      onClick={(event) => handleRemove(event, tab.key)}
                    >
                      ✕
                    </span>
                  )}
                </>
              )}
            </div>
          );
        })}
      </div>
      <div className="sm-content-tabs-actions">
        <span className="sm-content-tabs-action-btn">⚡</span>
        <span className="sm-content-tabs-action-btn">?</span>
      </div>
    </div>
  );
};

export default ContentTabsBar;
