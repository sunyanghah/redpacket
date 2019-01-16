package com.tianrun.redpacket.companyred.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tianrun.redpacket.companyred.dto.ActivityPlaceDto;
import com.tianrun.redpacket.companyred.entity.RedActivityPlace;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by dell on 2019/1/16.
 * @author dell
 */
@Mapper
public interface RedActivityPlaceMapper extends BaseMapper<RedActivityPlace>{

    /**
     * 根据红包id查询场景集合
     * @param activityId
     * @return
     */
    @Select(
            "select place_code as placeCode,place_name as placeName " +
                    "from tb_red_activity_place where activity_id = #{activityId}"
    )
    List<ActivityPlaceDto> getPlaceListByRedId(@Param("activityId")Long activityId);

    /**
     * 根据红包id删除场景
     * @param redActivityId
     */
    @Delete("delete from tb_red_activity_place where activity_id = #{activityId}")
    void deletePlaceOfRed(@Param("activityId") long redActivityId);
}
