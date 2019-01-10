package com.tianrun.redpacket.companyred.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by dell on 2019/1/9.
 * @author dell
 */
@Data
public class InRedAuthDto {

    private boolean hasAuth;

    private List<String> userAccounts;

}
