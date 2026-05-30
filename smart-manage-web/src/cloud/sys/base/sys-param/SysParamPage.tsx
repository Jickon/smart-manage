import SysParamEditPage from './SysParamEditPage';
import SysParamListPage from './SysParamListPage';
import type { PageComponentProps } from '@/cloud/common/page';

const SysParamPage = (props: PageComponentProps) =>
  props.operationType || props.billId || props.temporary ? (
    <SysParamEditPage {...props} />
  ) : (
    <SysParamListPage {...props} />
  );

export default SysParamPage;
