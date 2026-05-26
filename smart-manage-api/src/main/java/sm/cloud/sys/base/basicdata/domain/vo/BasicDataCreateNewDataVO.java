package sm.cloud.sys.base.basicdata.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 基础数据新增默认值
 *
 * @author Chekfu
 */
@Data
@Schema(title = "基础数据新增默认值")
public class BasicDataCreateNewDataVO {

    @Schema(description = "启用")
    private Boolean enableFlag;
}
