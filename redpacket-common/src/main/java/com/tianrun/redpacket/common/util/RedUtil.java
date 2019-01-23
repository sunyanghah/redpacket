package com.tianrun.redpacket.common.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2019/1/23.
 * @author dell
 */
public class RedUtil {


    /**
     * 计算红包金额分发
     * @param size
     * @param money
     * @param minPrice
     * @param maxPrice
     * @return
     * @throws Exception
     */
    public static <T> List<T> getRandomRed(Integer size, Integer money, Integer minPrice, Integer maxPrice,Class t) throws Exception{
        List redMoneyList = new ArrayList<>();
        for (int i = size;i>0;i--){
            int randomMoney = getRandomMoney(i,money,minPrice,maxPrice);
            if (t == String.class) {
                redMoneyList.add(String.valueOf(randomMoney));
            }
            if (t == Long.class) {
                redMoneyList.add(randomMoney);
            }
            money -= randomMoney;
        }
        return redMoneyList;
    }

    /**
     * 计算单个红包
     * @param rs
     * @param rm
     * @param minP
     * @param maxP
     * @return
     * @throws Exception
     */
    public static int getRandomMoney(Integer rs,Integer rm,Integer minP,Integer maxP) throws Exception{

        if(rs <= 0 || rm <= 0){
            throw new Exception();
        }

        /**
         * 默认每个红包最小一分
         */
        if(minP == null || minP == 0 ){
            minP = 1;
        }
        /**
         * 默认红包最大为全部金额
         * 并不会出现全部金额的情况，下面做了处理
         */
        if(maxP == null || maxP == 0 ){
            maxP = rm;
        }
        BigInteger remainSize = new BigInteger(rs.toString());
        BigInteger remainMoney = new BigInteger(rm.toString());
        BigInteger minPrice = new BigInteger(minP.toString());
        BigInteger maxPrice = new BigInteger(maxP.toString());
        /**
         * 计算当剩余红包全部装满时，当前红包最少可接受的金额
         */
        BigInteger minTemp = remainMoney.subtract(maxPrice.multiply(remainSize.subtract(BigInteger.ONE)));
        BigInteger randomMin = minTemp.compareTo(minPrice) == -1 ? minPrice : minTemp;

        /**
         * 计算当剩余红包全部塞入最少金额时，当前红包最多可装多少金额
         */
        BigInteger maxTemp = remainMoney.subtract(minPrice.multiply(remainSize.subtract(BigInteger.ONE)));
        BigInteger randomMax = maxTemp.compareTo(maxPrice) == 1 ? maxPrice : maxTemp;

        /**
         * 以下两句代码是为了使每个红包的金额尽量均匀，
         * 不然会出现总是第一个红包特别大的情况
         * 想进一步干预的话 可以改进一下: 做出一个种子 可以人工配置，或动态计算出这个种子值
         * 种子用来控制红包金额的波动情况。
         */
        BigInteger beatMax = remainMoney.divide(remainSize).multiply(new BigInteger("2"));
        randomMax = randomMax.compareTo(beatMax) == 1 ? beatMax : randomMax;

        BigInteger beatMin = remainMoney.divide(remainSize).divide(new BigInteger("2"));
        randomMin = randomMin.compareTo(beatMin) == -1 ? beatMin : randomMin;

        return (int)Math.round((Math.random()*(randomMax.subtract(randomMin).doubleValue()) + randomMin.doubleValue()));

    }
}
