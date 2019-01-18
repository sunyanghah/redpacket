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

    /**
     * 已拆红包人员
     */
    public static String HB_USER = "hbUser";

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
