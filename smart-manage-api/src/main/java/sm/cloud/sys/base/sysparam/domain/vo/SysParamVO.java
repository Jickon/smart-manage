package sm.cloud.sys.base.sysparam.domain.vo;

import lombok.Data;

/**
 * 系统参数 VO
 *
 * @author Chekfu
 */
@Data
public class SysParamVO {

    private Long id;

    private String number;

    private String name;

    private String value;

    private String remark;

    private Boolean isSystem;
}
