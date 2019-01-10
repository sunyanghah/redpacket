package com.tianrun.redpacket.common.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Created by dell on 2018/8/24.
 * @author dell
 */
@Data
public class InBatchIdDto<T> {

    @NotEmpty
    private List<T> idList;

}
