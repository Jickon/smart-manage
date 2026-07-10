package sm.domain.sys.base.basicdata.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "基础数据明细")
public class BasicDataEntryVO {

    private Long id;

    private String number;

    private String name;

    private Integer sort;

    private Boolean enableFlag;
}
