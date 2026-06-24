package sm.domain.sys.monitor.cache.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * Redis key 批量删除表单
 *
 * @author Chekfu
 */
@Data
@Schema(description = "Redis key 批量删除")
public class RedisDeleteForm {
    @NotEmpty(message = "keys 不能为空")
    @Schema(description = "要删除的 key 列表")
    private List<String> keys;
}
