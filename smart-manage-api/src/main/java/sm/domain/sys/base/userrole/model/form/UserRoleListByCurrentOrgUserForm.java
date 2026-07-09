package sm.domain.sys.base.userrole.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 按当前组织查询用户角色表单
 *
 * @author Chekfu
 */
@Data
@Schema(description = "按当前组织查询用户角色")
public class UserRoleListByCurrentOrgUserForm {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;
}
