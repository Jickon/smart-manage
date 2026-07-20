package sm.domain.sys.base.basicdata.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础数据新增默认值
 *
 * @author Chekfu
 */
@Data
@Schema(title = "基础数据新增默认值")
public class BasicDataCreateNewDataVO {

    @Schema(description = "启用")
    private Boolean enabled;

    @Schema(description = "基础数据明细")
    private List<BasicDataEntryVO> entrys = new ArrayList<>();
}
