package sm.domain.sys.base.basicdataitem.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 基础数据项列表项
 *
 * @author Chekfu
 */
@Data
@Schema(description = "基础数据项列表项")
public class BasicDataItemListVO {

    private Long id;

    private String typeNumber;

    private String itemCode;

    private String itemLabel;

    private Integer sort;

    private Boolean enableFlag;
}
