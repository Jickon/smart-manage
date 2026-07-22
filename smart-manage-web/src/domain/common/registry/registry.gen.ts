/**
 * 组件注册表导入文件 — 由 pnpm gen:registry 自动生成，禁止手动修改。
 * 每次构建前自动重新生成。
 */

// src\domain\scm\procurement\purchaseRequisition\pageRegistration.ts → scm/procurement/purchase-requisition
import '../../scm/procurement/purchaseRequisition/pageRegistration';

// src\domain\sys\app\pageRegistration.ts → sys/base/app
import '../../sys/app/pageRegistration';

// src\domain\sys\cloud\pageRegistration.ts → sys/base/cloud
import '../../sys/cloud/pageRegistration';

// src\domain\sys\menu\pageRegistration.ts → sys/base/menu
import '../../sys/menu/pageRegistration';

// src\domain\sys\permission\pageRegistration.ts → sys/base/permission
import '../../sys/permission/pageRegistration';

// src\domain\sys\role\pageRegistration.ts → sys/base/role
import '../../sys/role/pageRegistration';

// src\domain\sys\user\pageRegistration.ts → sys/base/user
import '../../sys/user/pageRegistration';

// 构建期已校验无重复 key，运行时二次确认
import { validateRegistry } from './componentRegistry';
validateRegistry();
