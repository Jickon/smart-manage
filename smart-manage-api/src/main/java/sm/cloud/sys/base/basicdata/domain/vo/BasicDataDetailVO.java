package sm.cloud.sys.base.basicdata.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import sm.cloud.sys.base.basicdataitem.domain.vo.BasicDataItemListVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 基础数据详情（含明细 entrys）
 *
 * @author Chekfu
 */
@Data
@Schema(description = "基础数据详情")
public class BasicDataDetailVO {

    private Long id;

    private String number;

    private String name;

    private String remark;

    private Boolean enableFlag;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @Schema(description = "基础数据项明细")
    private List<BasicDataItemListVO> entrys;
}
