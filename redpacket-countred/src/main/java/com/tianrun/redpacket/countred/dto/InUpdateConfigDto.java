package com.tianrun.redpacket.countred.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created by dell on 2019/4/8.
 * @author dell
 */
@Data
public class InUpdateConfigDto {

    @NotNull
    private String configCode;

    @NotBlank
    private String configValue;

    private String updateUser;

}
