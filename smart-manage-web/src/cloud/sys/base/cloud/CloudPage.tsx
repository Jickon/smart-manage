import CloudEditPage from './CloudEditPage';
import CloudListPage from './CloudListPage';
import type { PageComponentProps } from '@/cloud/common/page';

const CloudPage = (props: PageComponentProps) =>
  props.operationType || props.billId || props.temporary ? <CloudEditPage {...props} /> : <CloudListPage {...props} />;

export default CloudPage;
