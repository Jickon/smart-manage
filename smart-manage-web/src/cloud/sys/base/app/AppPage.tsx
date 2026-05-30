import AppEditPage from './AppEditPage';
import AppListPage from './AppListPage';
import type { PageComponentProps } from '@/cloud/common/page';

const AppPage = (props: PageComponentProps) =>
  props.operationType || props.billId || props.temporary ? <AppEditPage {...props} /> : <AppListPage {...props} />;

export default AppPage;
