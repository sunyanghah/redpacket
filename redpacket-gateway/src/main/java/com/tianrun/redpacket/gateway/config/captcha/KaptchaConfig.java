
package com.tianrun.redpacket.gateway.config.captcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author dell
 */
@Configuration
public class KaptchaConfig {

    private static final String KAPTCHA_BORDER = "kaptcha.border";
    private static final String KAPTCHA_TEXTPRODUCER_FONT_COLOR = "kaptcha.textproducer.font.color";
    private static final String KAPTCHA_TEXTPRODUCER_CHAR_SPACE = "kaptcha.textproducer.char.space";
    private static final String KAPTCHA_IMAGE_WIDTH = "kaptcha.image.width";
    private static final String KAPTCHA_IMAGE_HEIGHT = "kaptcha.image.height";
    private static final String KAPTCHA_TEXTPRODUCER_CHAR_LENGTH = "kaptcha.textproducer.char.length";
    private static final Object KAPTCHA_IMAGE_FONT_SIZE = "kaptcha.textproducer.font.size";

    @Bean
    public DefaultKaptcha producer() {
        Properties properties = new Properties();
        properties.put(KAPTCHA_BORDER, KaptchaConstants.DEFAULT_IMAGE_BORDER);
        properties.put(KAPTCHA_TEXTPRODUCER_FONT_COLOR, KaptchaConstants.DEFAULT_COLOR_FONT);
        properties.put(KAPTCHA_TEXTPRODUCER_CHAR_SPACE, KaptchaConstants.DEFAULT_CHAR_SPACE);
        properties.put(KAPTCHA_IMAGE_WIDTH, KaptchaConstants.DEFAULT_IMAGE_WIDTH);
        properties.put(KAPTCHA_IMAGE_HEIGHT, KaptchaConstants.DEFAULT_IMAGE_HEIGHT);
        properties.put(KAPTCHA_IMAGE_FONT_SIZE, KaptchaConstants.DEFAULT_IMAGE_FONT_SIZE);
        properties.put(KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, KaptchaConstants.DEFAULT_IMAGE_LENGTH);
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
