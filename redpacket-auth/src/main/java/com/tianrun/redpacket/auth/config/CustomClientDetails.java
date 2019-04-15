package com.tianrun.redpacket.auth.config;

import com.tianrun.redpacket.auth.entity.SysOauthClientDetails;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.*;

/**
 * Created by dell on 2018/11/12.
 * @author dell
 */
@Data
public class CustomClientDetails implements ClientDetails {

    private SysOauthClientDetails sysOauthClientDetails;

    private static Set<String> toSet(String source) {
        Set<String> res = new HashSet<>();
        if (source != null) {
            res.addAll(Arrays.asList(source.split(",")));
        }
        return res;
    }

    private static boolean toBoolean(int source){
        return source == 1 ? true:false;
    }

    @Override
    public String getClientId() {
        return sysOauthClientDetails.getClientId();
    }

    @Override
    public Set<String> getResourceIds() {
        return toSet(sysOauthClientDetails.getResourceIds());
    }

    @Override
    public boolean isSecretRequired() {
        return toBoolean(sysOauthClientDetails.getIsSecretRequired());
    }

    @Override
    public String getClientSecret() {
        return sysOauthClientDetails.getClientSecret();
    }

    @Override
    public boolean isScoped() {
        return toBoolean(sysOauthClientDetails.getIsScoped());
    }

    @Override
    public Set<String> getScope() {
        return toSet(sysOauthClientDetails.getScope());
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return toSet(sysOauthClientDetails.getAuthorizedGrantTypes());
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return toSet(sysOauthClientDetails.getRegisteredRedirectUri());
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        String authorities = sysOauthClientDetails.getAuthorities();
        Set<GrantedAuthority> res = new HashSet<>();
        if (authorities != null) {
            toSet(authorities).forEach((auth) -> {
                GrantedAuthority gauth = new SimpleGrantedAuthority(auth);
                res.add(gauth);
            });

        }
        return res;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return sysOauthClientDetails.getAccessTokenValiditySeconds();
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return sysOauthClientDetails.getRefreshTokenValiditySeconds();
    }

    @Override
    public boolean isAutoApprove(String s) {
        return toBoolean(sysOauthClientDetails.getIsAutoApprove());
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return null;
    }
}
