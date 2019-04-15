package com.tianrun.redpacket.gateway.config;

import com.alibaba.fastjson.JSON;
import com.tianrun.redpacket.common.platform.RP;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by dell on 2018/11/8.
 * @author dell
 */
public class MyAuthenticationHandler implements AuthenticationEntryPoint{
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        RP<String> rp = RP.buildFailure("无效的token","无效的token");
        rp.setState(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(JSON.toJSONString(rp));
    }
}
