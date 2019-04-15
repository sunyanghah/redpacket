package com.tianrun.redpacket.gateway.dto;

import lombok.Data;

/**
 * Created by dell on 2018/11/8.
 * @author dell
 */
@Data
public class OutResourcesDto {

    private String resourceMethod;

    private String resourceUrl;

    private String serviceId;
}
