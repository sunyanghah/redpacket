package com.tianrun.redpacket.gateway.config;

import com.tianrun.redpacket.gateway.dto.OutResourcesDto;
import com.tianrun.redpacket.gateway.mapper.ResourceMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by dell on 2018/11/23.
 * @author dell
 */
@Component("permissionService")
public class PermissionService {

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private RouteLocator routeLocator;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 统一鉴权。
     * 如果各服务有自己的权限体系。则这里改为调用其他服务接口。其他服务实现具体鉴权。
     * @param request
     * @param authentication
     * @return
     * @throws Exception
     */
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) throws Exception{

        String requestUrl = request.getRequestURI();
        String requestService = null;
        // 获取匹配到的路由对象
        Route route = routeLocator.getMatchingRoute(requestUrl);
        if (null != route){
            // 返回资源实际url
            requestUrl = route.getPath();
            requestService = route.getId();
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roleList = new ArrayList<>();
        if (null != authorities && authorities.size() > 0) {
            authorities.forEach(grantedAuthority -> roleList.add(grantedAuthority.getAuthority()));
            List<OutResourcesDto> resourceList = resourceMapper.getResourcesByRoles(roleList);
            if (null != resourceList && resourceList.size() > 0){
                for (OutResourcesDto resource : resourceList){
                    if (checkService(resource.getServiceId(),requestService)
                            && checkMethod(resource.getResourceMethod(),request.getMethod())
                            && checkUrl(resource.getResourceUrl(),requestUrl)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 核对服务是否对应
     * @param resourceService
     * @param requestService
     * @return
     * @throws Exception
     */
    private boolean checkService(String resourceService,String requestService) throws Exception{
        if (StringUtils.isBlank(resourceService)){
            return true;
        }
        return resourceService.equals(requestService);
    }

    /**
     * 核对接口method是否对应
     * @param resourceMethod
     * @param requestMethod
     * @return
     * @throws Exception
     */
    private boolean checkMethod(String resourceMethod,String requestMethod) throws Exception{
        if (StringUtils.isBlank(resourceMethod)){
            return true;
        }
        return resourceMethod.equals(requestMethod);
    }

    /**
     * 核对接口url是否对应
     * @param resourceUrl
     * @param requestUrl
     * @return
     * @throws Exception
     */
    private boolean checkUrl(String resourceUrl, String requestUrl) throws Exception{
        return antPathMatcher.match(resourceUrl,requestUrl);
    }

}
