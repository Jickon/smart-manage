package sm.domain.sys.base.role.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 角色 + 权限聚合保存表单 — 一次请求完成角色保存和权限分配，在事务内执行。
 *
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色+权限聚合保存表单")
public class RoleSaveWithPermsForm extends RoleSaveForm {

    @Schema(description = "权限ID列表，为空则清除所有权限")
    private List<Long> permissionIds;
}
