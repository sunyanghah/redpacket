package com.tianrun.redpacket.common.dict;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by dell on 2019/1/7.
 * @author dell
 *
 * 将dto或dto集合中的字典code转化为字典value
 *
 */
@Component
public class DictHandle {

    @Autowired
    private RedDictMapper redDictMapper;

    /**
     * dto中字段不用添加注解，适合dto中只有一两个需要处理的字典字段
     * 多次调用此方法会造成多次查询数据库
     * @param collection
     * @param fieldName
     * @param type
     * @param <T>
     * @throws Exception
     */
    public <T> void handleDict(Collection<T> collection,String fieldName,String type) throws Exception{
        if (collection != null && collection.size() > 0 && StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(type)){
            Set<String> codeList = new HashSet<>();

            for (T t : collection){
                codeList.add(getDictCode(t,fieldName));
            }

            List<Map<String,String>> valueList = redDictMapper.getValueByTypeAndCodes(type,new ArrayList<>(codeList));

            if (valueList != null && valueList.size() > 0){
                for (T t : collection){
                    String code = getDictCode(t,fieldName);
                    if (null != code){
                        for (Map<String,String> valueMap : valueList){
                            if (code.equals(valueMap.get("dictCode"))){
                                setDictValue(t,fieldName,valueMap.get("dictValue"));
                            }
                        }
                    }

                }
            }
        }
    }

    /**
     * dto中字段不用添加注解，适合dto中只有一两个需要处理的字典字段
     * 多次调用此方法会造成多次查询数据库
     * @param t
     * @param fieldName
     * @param type
     * @param <T>
     * @throws Exception
     */
    public <T> void handleDict(T t,String fieldName,String type) throws Exception{
        if (t != null && StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(type)) {
            String code = getDictCode(t, fieldName);
            String dictValue = redDictMapper.getValueByTypeAndCode(type, code);
            setDictValue(t, fieldName, dictValue);
        }
    }



    /**
     * dto中需要处理的字段上需要添加注解，适合dto中有多个需要处理的字典字段
     * @param collection
     * @param <T>
     * @throws Exception
     */
    public <T> void handleDict(Collection<T> collection) throws Exception{
        if (collection != null && collection.size() > 0){
            Set<String> types = new HashSet<>();
            Set<String> codes = new HashSet<>();
            for (T t : collection){
                getTypesAndCodes(t,types,codes);
            }
            List<Map<String, String>> maps = redDictMapper.getValueByTypesAndCodes(new ArrayList<>(types), new ArrayList<>(codes));
            for (T t : collection){
                setDictValue(maps,t);
            }
        }
    }

    /**
     * dto中需要处理的字段上需要添加注解，适合dto中有多个需要处理的字典字段
     * @param t
     * @param <T>
     * @throws Exception
     */
    public <T> void handleDict(T t) throws Exception{
        if (t != null) {
            Set<String> types = new HashSet<>();
            Set<String> codes = new HashSet<>();
            getTypesAndCodes(t,types,codes);
            List<Map<String, String>> maps = redDictMapper.getValueByTypesAndCodes(new ArrayList<>(types), new ArrayList<>(codes));
            setDictValue(maps,t);
        }
    }


    private <T> void getTypesAndCodes(T t,Set<String> types,Set<String> codes) throws Exception{
        Class c = t.getClass();
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            DictValueHandle dictValueHandle = field.getAnnotation(DictValueHandle.class);
            if (dictValueHandle != null) {
                String code = getDictCode(t, field.getName());
                codes.add(code);
                types.add(dictValueHandle.dictType());
            }
        }
    }

    private <T> void setDictValue(List<Map<String,String>> maps,T t) throws Exception{
        Class c = t.getClass();
        Field[] fields = c.getDeclaredFields();
        if (maps != null && maps.size() > 0) {
            for (Field field : fields) {
                DictValueHandle dictValueHandle = field.getAnnotation(DictValueHandle.class);
                if (dictValueHandle != null) {
                    for (Map<String, String> map : maps) {
                        if (dictValueHandle.dictType().equals(map.get("dictType"))) {
                            String code = getDictCode(t, field.getName());
                            if (code.equals(map.get("dictCode"))) {
                                setDictValue(t, field.getName(), map.get("dictValue"));
                            }
                        }
                    }
                }
            }
        }
    }

    private <T> String getDictCode(T t,String fieldName) throws Exception{
        Method getMethod = getMethod(t,fieldName,"get");
        if (null != getMethod){
            Object value = getMethod.invoke(t);
            if (null != value) {
                return value.toString();
            }
        }
        return null;
    }

    private <T> void setDictValue(T t,String fieldName,String value) throws Exception {
        Method setMethod = getMethod(t,fieldName,"set",String.class);
        if (null != setMethod){
            setMethod.invoke(t,value);
        }
    }

    private <T> Method getMethod(T t,String fieldName,String getSet,Class... args) throws Exception{
        Class c = t.getClass();
        String fieldFormat = fieldName.substring(0,1).toUpperCase()+fieldName.substring(1,fieldName.length());
        Method method = c.getMethod(getSet+fieldFormat,args);
        return method;
    }


}
