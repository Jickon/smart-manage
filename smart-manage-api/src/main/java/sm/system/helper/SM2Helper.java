package sm.system.helper;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.BCUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import jakarta.annotation.PostConstruct;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sm.system.util.StringUtil;

import java.security.KeyPair;


/**
 * @author Chekfu
 */
@Component
public class SM2Helper {
	@Value("${smart-manage.sm2.js.private-key}")
	private String privateKey;

	@Value("${smart-manage.sm2.js.public-key}")
	private String publicKey;

	private static String staticPrivateKey;
	private static String staticPublicKey;

	@PostConstruct
	public void init() {
		staticPrivateKey = this.privateKey;
		staticPublicKey = this.publicKey;
	}

	public static KeyPair generateKeyPair() {
		return SecureUtil.generateKeyPair("SM2");
	}

	/**
	 * 加密
	 */
	public static String encrypt(String data) {
		try {
			if (checkKeyIsEmpty(staticPrivateKey, staticPublicKey)) {
				SM2 sm2 = new SM2(staticPrivateKey, staticPublicKey);
				sm2.setMode(SM2Engine.Mode.C1C3C2);
				return sm2.encryptHex(data, KeyType.PublicKey);
			}
			return data;
		} catch (Exception e) {
			throw new RuntimeException("sm2加密失败" + e);
		}
	}

	/**
	 * 解密
	 */
	public static String decrypt(String data) {
		try {
			if (checkKeyIsEmpty(staticPrivateKey, staticPublicKey)) {
				SM2 sm2 = new SM2(staticPrivateKey, staticPublicKey);
				sm2.setMode(SM2Engine.Mode.C1C3C2);
				return sm2.decryptStr(data, KeyType.PrivateKey);
			}
			return data;
		} catch (Exception e) {
			throw new RuntimeException("sm2解密失败" + e);
		}
	}

	private static boolean checkKeyIsEmpty(String privateKey, String publicKey) {
		return !StringUtil.isEmpty(privateKey) && !StringUtil.isEmpty(publicKey);
	}

	/**
	 * 服务端密钥对
	 */
	private static void createServerKey() {
		// 生成密钥对
		KeyPair keyPair = SM2Helper.generateKeyPair();
		// 生成私钥
		String privateKey = HexUtil.encodeHexStr(keyPair.getPrivate().getEncoded());
		staticPrivateKey = privateKey;
		System.out.println("服务端privateKey：" + privateKey);
		// 生成公钥
		String publicKey = HexUtil.encodeHexStr(keyPair.getPublic().getEncoded());
		staticPublicKey = publicKey;
		System.out.println("服务端publicKey：" + publicKey);

		//测试
		String encrypt = SM2Helper.encrypt("测试" + System.currentTimeMillis());
		System.out.println("服务端加密：" + encrypt);
		String decrypt = SM2Helper.decrypt(encrypt);
		System.out.println("服务端解密：" + decrypt);
	}

	/**
	 * 前端密钥对
	 */
	private static void createClientKey() {
		// 生成密钥对
		KeyPair keyPair = SM2Helper.generateKeyPair();
		// 生成公钥 Q，以Q值做为js端的加密公钥
		String publicKeyQ = HexUtil.encodeHexStr(((BCECPublicKey) keyPair.getPublic()).getQ().getEncoded(false));
		staticPublicKey = publicKeyQ;
		System.out.println("前端公钥Q：" + publicKeyQ);
		// 生成私钥 D，以D值做为js端的解密私钥
		String privateKeyD = HexUtil.encodeHexStr(BCUtil.encodeECPrivateKey(keyPair.getPrivate()));
		staticPrivateKey = privateKeyD;
		System.out.println("前端私钥D：" + privateKeyD);

		//测试
		String encrypt = SM2Helper.encrypt("测试" + System.currentTimeMillis());
		System.out.println("前端加密：" + encrypt);
		String decrypt = SM2Helper.decrypt(encrypt);
		System.out.println("前端解密：" + decrypt);
	}

	public static void main(String[] args) {
		createServerKey();
		createClientKey();
	}

}