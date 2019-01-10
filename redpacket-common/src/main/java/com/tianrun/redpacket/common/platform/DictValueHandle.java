package com.tianrun.redpacket.common.platform;

import java.lang.annotation.*;

/**
 * Created by dell on 2019/1/10.
 * @author dell
 */
@Target({ ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictValueHandle {

    String dictType();
}
