package sm.cloud.sys.base.uiconfig.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.entity.BaseEntity;

/**
 * 界面配置实体
 *
 * @author Chekfu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_sys_ui_config")
public class UiConfigEntity extends BaseEntity {

    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /** 页面标题 */
    private String pageTitle;

    /** 登录页 banner 图片路径 */
    private String loginBanner;

    /** 登录页 logo 路径 */
    private String loginLogo;

    /** 系统名称 */
    private String systemName;

    /** 首页 header logo 路径 */
    private String headerLogo;
}
