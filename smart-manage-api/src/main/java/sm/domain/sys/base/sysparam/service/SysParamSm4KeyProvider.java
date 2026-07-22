package sm.domain.sys.base.sysparam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sm.system.helper.Sm4KeyProvider;

/** 从系统参数读取 SM4 密钥。 */
@Component
@RequiredArgsConstructor
public class SysParamSm4KeyProvider implements Sm4KeyProvider {

    private static final String PARAM_SM4_KEY = "SM4_KEY";

    private final SysParamService sysParamService;

    @Override
    public String getSm4KeyBase64() {
        return sysParamService.getString(PARAM_SM4_KEY);
    }
}
