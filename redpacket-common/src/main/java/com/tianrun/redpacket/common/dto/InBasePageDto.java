package com.tianrun.redpacket.common.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 页面查询的page基类
 * @author dell
 */
@Data
public class InBasePageDto {
	@NotNull(message = "每页显示的记录数不能为空")
	protected Integer size = 10;
	@NotNull(message = "当前页数不能为空")
	protected Integer current = 1;

	protected Integer dataIndex;

	public Integer getDataIndex(){
		return (current-1)* size;
	}
}
