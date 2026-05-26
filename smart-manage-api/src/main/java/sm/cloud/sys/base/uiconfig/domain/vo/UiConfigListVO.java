package sm.cloud.sys.base.uiconfig.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 界面配置列表 VO
 *
 * @author Chekfu
 */
@Data
public class UiConfigListVO {

    private Long id;

    private String pageTitle;

    private String systemName;

    private String loginBanner;

    private String loginLogo;

    private String headerLogo;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
