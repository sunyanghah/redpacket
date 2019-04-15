package com.tianrun.redpacket.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by dell on 2018/11/12.
 * @author dell
 */
@TableName("oauth_client_details")
@Data
public class SysOauthClientDetails {

    @TableId(value = "client_id",type = IdType.INPUT)
    private String clientId;

    @TableField(value = "resource_ids")
    private String resourceIds;

    @TableField(value = "is_secret_required")
    private int isSecretRequired;

    @TableField(value = "client_secret")
    private String clientSecret;

    @TableField(value = "is_scoped")
    private int isScoped;

    @TableField(value = "scope")
    private String scope;

    @TableField(value = "authorized_grant_types")
    private String authorizedGrantTypes;

    @TableField(value = "registered_redirect_uri")
    private String registeredRedirectUri;

    @TableField(value = "authorities")
    private String authorities;

    @TableField(value = "access_token_validity_seconds")
    private int accessTokenValiditySeconds;

    @TableField(value = "refresh_token_validity_seconds")
    private int refreshTokenValiditySeconds;

    @TableField(value = "is_auto_approve")
    private int isAutoApprove;

    @TableField(value = "additional_information")
    private String additionalInformation;
}
