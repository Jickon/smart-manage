package sm.domain.sys.base.basicdata.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "基础数据选项")
public class BasicDataOptionVO {

    @Schema(description = "编码")
    private String number;

    @Schema(description = "名称")
    private String name;
}
