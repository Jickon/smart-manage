package sm.system.helper;

/** 提供 SM4 Base64 密钥，具体来源由系统服务实现。 */
public interface Sm4KeyProvider {

    String getSm4KeyBase64();
}
