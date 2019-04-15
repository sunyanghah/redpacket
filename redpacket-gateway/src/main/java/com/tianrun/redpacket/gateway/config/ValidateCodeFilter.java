package com.tianrun.redpacket.gateway.config;

import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.tianrun.redpacket.common.exception.BusinessException;
import com.tianrun.redpacket.common.platform.RP;
import com.tianrun.redpacket.gateway.config.captcha.KaptchaConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author dell
 */
@Slf4j
@RefreshScope
@Configuration("validateCodeFilter")
public class ValidateCodeFilter extends ZuulFilter {
    private static final String EXPIRED_CAPTCHA_ERROR = "验证码已过期，请重新获取";
    private static final String GRANT_TYPE = "grant_type";
    private static final String OAUTH_TOKEN_URL = "/oauth/token";
    private static final String MOBILE_TOKEN_URL = "/mobile/token";
    private static final String REFRESH_TOKEN = "refresh_token";

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_ERROR_FILTER_ORDER + 1;
    }

    /**
     * 是否校验验证码
     * 1. 判断验证码开关是否开启
     * 2. 判断请求是否登录请求
     * 2.1 判断是不是刷新请求(不用单独在建立刷新客户端)
     * 3. 判断终端是否支持
     *
     * @return true/false
     */
    @Override
    public boolean shouldFilter() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();

        if (!(request.getRequestURI().contains(OAUTH_TOKEN_URL)||
                request.getRequestURI().contains(MOBILE_TOKEN_URL))) {
            return false;
        }

        if (REFRESH_TOKEN.equals(request.getParameter(GRANT_TYPE))) {
            return false;
        }

        return true;
    }

    @Override
    public Object run() {
        try {
            checkCode();
        } catch (BusinessException e) {
            RequestContext ctx = RequestContext.getCurrentContext();
            RP<String> result = new RP<>();
            result.setCode(478);
            result.setData(e.getMessage());
            ctx.setResponseStatusCode(478);
            ctx.setSendZuulResponse(false);
            ctx.getResponse().setContentType("application/json;charset=UTF-8");
            ctx.setResponseBody(JSONObject.toJSONString(result));
        }
        return null;
    }

    /**
     * 检查code
     *
     * @throws com.tianrun.redpacket.common.exception.BusinessException 验证码校验异常
     */
    private void checkCode() throws BusinessException {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        String code = httpServletRequest.getParameter("validCode");
        if (StringUtils.isBlank(code)) {
            throw new BusinessException("请输入验证码");
        }

        String randomStr = httpServletRequest.getParameter("randomStr");
        if (StringUtils.isBlank(randomStr)) {
            randomStr = httpServletRequest.getParameter("mobile");
        }

        String key = KaptchaConstants.DEFAULT_CODE_KEY + randomStr;
        if (!redisTemplate.hasKey(key)) {
            throw new BusinessException(EXPIRED_CAPTCHA_ERROR);
        }

        Object codeObj = redisTemplate.opsForValue().get(key);

        if (codeObj == null) {
            throw new BusinessException(EXPIRED_CAPTCHA_ERROR);
        }

        String saveCode = codeObj.toString();
        if (StringUtils.isBlank(saveCode)) {
            redisTemplate.delete(key);
            throw new BusinessException(EXPIRED_CAPTCHA_ERROR);
        }

        if (!StringUtils.equals(saveCode, code)) {
            redisTemplate.delete(key);
            throw new BusinessException("验证码错误，请重新输入");
        }

        redisTemplate.delete(key);
    }
}
