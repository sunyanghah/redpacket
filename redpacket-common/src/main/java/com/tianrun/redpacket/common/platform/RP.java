package com.tianrun.redpacket.common.platform;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by dell on 2018/10/27.
 * @author dell
 */
public class RP<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final int SUCCESS = 0;
    public static final int FAIL = 1;

    @JSONField(
            ordinal = 1
    )
    private int code = 0;
    @JSONField(
            ordinal = 2
    )
    private int state = 200;
    @JSONField(
            ordinal = 3
    )
    private String msg = "success";
    @JSONField(
            ordinal = 4
    )
    private String timestamp = String.valueOf(System.currentTimeMillis());
    @JSONField(
            ordinal = 5
    )
    private T data;

    public RP() {
    }

    public RP(T data) {
        this.data = data;
    }

    public RP(T data, String msg) {
        this.data = data;
        this.msg = msg;
    }

    public RP(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public RP(Throwable e) {
        this.msg = e.getMessage();
        this.code = 1;
        this.state = 500;
    }

    public RP(Throwable e, int state) {
        this.msg = e.getMessage();
        this.code = 1;
        this.state = state;
    }

    public RP(Boolean success, String msg, T data) {
        if(success.booleanValue()) {
            this.code = 0;
            this.state = 200;
        } else {
            this.code = 1;
            this.state = 500;
        }

        this.msg = msg;
        this.data = data;
    }

    public boolean isSuccess() {
        return this.code == 0;
    }

    public static <T> RP<T> build(Boolean success, String msg, T data) {
        return new RP(success, msg, data);
    }

    public static <T> RP<T> buildSuccess(String msg, T data) {
        return build(Boolean.TRUE, msg, data);
    }

    public static <T> RP<T> buildFailure(String msg, T data) {
        return build(Boolean.FALSE, msg, data);
    }

    public static <T> RP<T> buildSuccess(String msg) {
        return buildSuccess(msg, null);
    }

    public static <T> RP<T> buildSuccess(T data) {
        return buildSuccess(null, data);
    }

    public static <T> RP<T> buildFailure(String msg) {
        return buildFailure(msg, null);
    }

    public static <T> RP<T> buildFailure(T data) {
        return buildFailure("", data);
    }

    public int getCode() {
        return this.code;
    }

    public int getState() {
        return this.state;
    }

    public String getMsg() {
        return this.msg;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public T getData() {
        return this.data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        } else if(!(o instanceof RP)) {
            return false;
        } else {
            RP other = (RP)o;
            if(!other.canEqual(this)) {
                return false;
            } else if(this.getCode() != other.getCode()) {
                return false;
            } else if(this.getState() != other.getState()) {
                return false;
            } else {
                label52: {
                    String this$msg = this.getMsg();
                    String other$msg = other.getMsg();
                    if(this$msg == null) {
                        if(other$msg == null) {
                            break label52;
                        }
                    } else if(this$msg.equals(other$msg)) {
                        break label52;
                    }

                    return false;
                }

                String this$timestamp = this.getTimestamp();
                String other$timestamp = other.getTimestamp();
                if(this$timestamp == null) {
                    if(other$timestamp != null) {
                        return false;
                    }
                } else if(!this$timestamp.equals(other$timestamp)) {
                    return false;
                }

                Object this$data = this.getData();
                Object other$data = other.getData();
                if(this$data == null) {
                    if(other$data != null) {
                        return false;
                    }
                } else if(!this$data.equals(other$data)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof RP;
    }

    @Override
    public int hashCode() {
        boolean PRIME = true;
        byte result = 1;
        int result1 = result * 59 + this.getCode();
        result1 = result1 * 59 + this.getState();
        String $msg = this.getMsg();
        result1 = result1 * 59 + ($msg == null?43:$msg.hashCode());
        String $timestamp = this.getTimestamp();
        result1 = result1 * 59 + ($timestamp == null?43:$timestamp.hashCode());
        Object $data = this.getData();
        result1 = result1 * 59 + ($data == null?43:$data.hashCode());
        return result1;
    }

    @Override
    public String toString() {
        return "RP(code=" + this.getCode() + ", state=" + this.getState() + ", msg=" + this.getMsg() + ", timestamp=" + this.getTimestamp() + ", data=" + this.getData() + ")";
    }
}
