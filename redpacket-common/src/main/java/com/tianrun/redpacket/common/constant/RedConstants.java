package com.tianrun.redpacket.common.constant;

/**
 * Created by dell on 2018/12/25.
 * @author dell
 */
public class RedConstants {
    /**
     * 红包基本信息
     */
    public static String HB_INFO = "hbInfo";
    public static String HB_TYPE = "type";
    public static String HB_SIZE = "size";
    public static String HB_MONEY = "money";
    public static String HB_MAX_PRICE = "maxPrice";
    public static String HB_MIN_PRICE = "minPrice";
    public static String HB_DEADLINE = "deadline";
    public static String HB_PRICE = "price";
    public static String HB_STATUS = "status";

//    /**
//     * 已拆红包人员
//     */
//    public static String HB_USER = "hbUser";


    /**
     * 已拆红包人员 ，应对redis集群
     * 集群有一个槽的概念，不同的key根据redis的hash算法会分布在不同的槽，
     * 集群是不支持多key查询的，解决办法是使用redis的hash tag，存入的时候把要多key查询的 key用{}括起来
     * 只有用{}括起来的部分才会参与hash计算
     */
    public static String HB_INFO_USER = "{hbInfo}User";

    /**
     * 红包权限情况
     */
    public static String HB_AUTH = "hbAuth";

    /**
     * 红包任务完成情况
     */
    public static String HB_TASK = "hbTask";

    /**
     * 红包过期时间，单位分钟
     */
    public static long HB_DEADLINE_MILLISECOND = 1000*60*60*24;

}
