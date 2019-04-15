
package com.tianrun.redpacket.gateway.config.dynamicroute;

import com.tianrun.redpacket.gateway.entity.SysZuulRoute;
import com.tianrun.redpacket.gateway.mapper.RouteMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

/**
 * @author dell
 * 动态路由实现
 */
@Slf4j
public class DynamicRouteLocator extends DiscoveryClientRouteLocator {
    private ZuulProperties properties;
    private RedisTemplate redisTemplate;

    @Autowired
    private RouteMapper routeMapper;

    public DynamicRouteLocator(String servletPath, DiscoveryClient discovery, ZuulProperties properties,
                               ServiceInstance localServiceInstance, RedisTemplate redisTemplate) {
        super(servletPath, discovery, properties, localServiceInstance);
        this.properties = properties;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 重写路由配置
     * <p>
     * 1. properties 配置。
     * 2. eureka 默认配置。
     * 3. DB数据库配置。
     *
     * @return 路由表
     */
    @Override
    protected LinkedHashMap<String, ZuulProperties.ZuulRoute> locateRoutes() {

        LinkedHashMap<String, ZuulProperties.ZuulRoute> routesMap = new LinkedHashMap<>();
        //读取properties配置、eureka默认配置
        routesMap.putAll(super.locateRoutes());
        routesMap.putAll(locateRoutesFromDb());

        return routesMap;
    }

    /**
     * Redis中保存的，没有从upms拉去，避免启动链路依赖问题（取舍），网关依赖业务模块的问题
     *
     * @return
     */
    private Map<String, ZuulProperties.ZuulRoute> locateRoutesFromDb() {
        Map<String, ZuulProperties.ZuulRoute> routes = new LinkedHashMap<>();
        List<SysZuulRoute> results;
        // 测试阶段暂不放入redis，放入redis要处理 redis与数据库数据一致性问题
//        Object redisRoute = redisTemplate.opsForValue().get("_ROUTE_KEY");
        Object redisRoute = null;
        if (redisRoute == null) {
            results = routeMapper.selectList(null);
            if (results != null) {
//                redisTemplate.opsForValue().set("_ROUTE_KEY", results);
            }else{
                results = new ArrayList<>();
            }
        }else {
            results = (List<SysZuulRoute>) redisRoute;
        }

        for (SysZuulRoute result : results) {
            if (StringUtils.isBlank(result.getPath())) {
                continue;
            }
            ZuulProperties.ZuulRoute zuulRoute = new ZuulProperties.ZuulRoute();
            try {
                zuulRoute.setId(result.getServiceId());
                zuulRoute.setPath(result.getPath());
                zuulRoute.setServiceId(result.getServiceId());
                zuulRoute.setRetryable(StringUtils.equals(result.getRetryable(), "0") ? Boolean.FALSE : Boolean.TRUE);
                zuulRoute.setStripPrefix(StringUtils.equals(result.getStripPrefix(), "0") ? Boolean.FALSE : Boolean.TRUE);
                zuulRoute.setUrl(result.getUrl());
                List<String> sensitiveHeadersList = StringUtils.isBlank(result.getSensitiveheadersList())?
                        null:Arrays.asList(result.getSensitiveheadersList().split(","));
                if (sensitiveHeadersList != null) {
                    Set<String> sensitiveHeaderSet = new HashSet<>();
                    sensitiveHeadersList.stream().filter(sensitiveHeader -> StringUtils.isNotBlank(sensitiveHeader))
                            .forEach(sensitiveHeader -> sensitiveHeaderSet.add(sensitiveHeader));
                    zuulRoute.setSensitiveHeaders(sensitiveHeaderSet);
                }
            } catch (Exception e) {
                log.error("从数据库加载路由配置异常", e);
            }
            routes.put(zuulRoute.getPath(), zuulRoute);
        }
        return routes;
    }
}
