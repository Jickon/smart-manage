package sm.domain.sys.base.user.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户 + 角色聚合保存表单 — 一次请求完成用户保存和角色分配，在事务内执行。
 *
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户+角色聚合保存表单")
public class UserSaveWithRolesForm extends UserSaveForm {

    @Schema(description = "角色ID列表，为空则清除所有角色")
    private List<Long> roleIds;
}
