package sm.cloud.sys.base.app.domain.vo;

import lombok.Data;

/**
 * 云与应用联表查询的扁平结果，仅用于服务层组装。
 */
@Data
public class CloudAppRowVO {
    private Long cloudId;
    private String cloudName;
    private String cloudNumber;
    private Integer cloudSeq;
    private Long appId;
    private String appName;
    private String appNumber;
    private String appIcon;
    private String appIconColor;
    private Integer appSeq;
    private String appDescription;
}
