package sm.cloud.sys.base.common.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author Chekfu
 */
public class CaptchaUtil {
	private static final Random RANDOM = new Random();

	public static String generateCharCaptcha(int length) {
		String chars = "23456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ";
		StringBuilder captcha = new StringBuilder();
		for (int i = 0; i < length; i++) {
			captcha.append(chars.charAt(RANDOM.nextInt(chars.length())));
		}
		return captcha.toString();
	}

	public static BufferedImage generateCaptchaImage(String captcha, int width, int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		// 设置背景色
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);

		// 设置字体
		g.setFont(new Font("Arial", Font.BOLD, 30));

		// 绘制干扰线
		for (int i = 0; i < 5; i++) {
			g.setColor(new Color(RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255)));
			g.drawLine(RANDOM.nextInt(width), RANDOM.nextInt(height),
					RANDOM.nextInt(width), RANDOM.nextInt(height));
		}

		// 绘制验证码
		for (int i = 0; i < captcha.length(); i++) {
			g.setColor(new Color(RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255)));
			g.drawString(String.valueOf(captcha.charAt(i)), 20 + i * 30, 35);
		}

		g.dispose();
		return image;
	}
}