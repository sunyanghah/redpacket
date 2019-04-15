package com.tianrun.redpacket.gateway.web;

import com.google.code.kaptcha.Producer;
import com.tianrun.redpacket.gateway.config.captcha.KaptchaConstants;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

/**
 * Created by dell on 2019/4/11.
 * @author dell
 */
@RestController
@RequestMapping("validCode")
public class ValidCodeController {
    @Autowired
    private Producer producer;
    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping("/{randomStr}")
    public void getValidCode(@PathVariable String randomStr, HttpServletRequest request, HttpServletResponse response) throws Exception{
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        //生成文字验证码
        String text = producer.createText();
        //生成图片验证码
        BufferedImage image = producer.createImage(text);
        redisTemplate.opsForValue().set(KaptchaConstants.DEFAULT_CODE_KEY + randomStr, text, KaptchaConstants.DEFAULT_IMAGE_EXPIRE, TimeUnit.SECONDS);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "JPEG", out);
        IOUtils.closeQuietly(out);
    }
}
