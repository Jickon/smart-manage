import {IconBulb, IconClose, IconFullscreen, IconHome, IconQuestionCircle} from '@arco-design/web-react/icon';
import {useAppWorkspaceStore} from '@/stores/appWorkspace';

interface Props {
  appNumber: string;
}

const ContentTabsBar = ({ appNumber }: Props) => {
  const ws = useAppWorkspaceStore((s) => s.workspaces[appNumber]);
  const activateContentTab = useAppWorkspaceStore((s) => s.activateContentTab);
  const removeContentTab = useAppWorkspaceStore((s) => s.removeContentTab);

  if (!ws) return null;

  const { contentTabs, activeContentTabKey } = ws;

  const handleRemove = (e: React.MouseEvent, key: string) => {
    e.stopPropagation();
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
                <>
                  <IconHome style={{fontSize: '18px'}}/>
                  {/*<span>{tab.label}</span>*/}
                </>
              ) : (
                <>
                  <span>{tab.label}</span>
                  {tab.closable && (
                    <IconClose className="sm-content-tab-close" onClick={(e) => handleRemove(e, tab.key)} />
                  )}
                </>
              )}
            </div>
          );
        })}
      </div>
      <div className="sm-content-tabs-actions">
        <span className="sm-content-tabs-action-btn">
          <IconBulb />
        </span>
        <span className="sm-content-tabs-action-btn">
          <IconQuestionCircle />
        </span>
        <span className="sm-content-tabs-action-btn">
          <IconFullscreen />
        </span>
      </div>
    </div>
  );
};

export default ContentTabsBar;
