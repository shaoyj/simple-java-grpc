package com.mylomen.gprc.client.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

@Data
public class MynResponse<T> implements Serializable {

    private Integer code;

    private String msg;

    /**
     * data 异常给 null。 正常返回必须要有数值
     */
    private T data;

    /**
     * 扩展信息
     */
    private Map<String, Object> ext;


    public Map<String, Object> getExt() {
        return ext == null ? Collections.emptyMap() : ext;
    }

    public void setExt(Map<String, Object> ext) {
        this.ext = ext;
    }


    public static <T> MynResponse<T> ok(T data) {
        MynResponse<T> response = new MynResponse<>();
        response.code = 0;
        response.data = data;
        return response;
    }

    public static <T> MynResponse<T> success(T data) {
        MynResponse<T> response = new MynResponse<>();
        response.code = 0;
        response.data = data;
        return response;
    }

    public static <T> MynResponse<T> failed(String msg) {
        MynResponse<T> response = new MynResponse<>();
        response.code = -1;
        response.msg = msg;
        return response;
    }

    /**
     * 未登录
     *
     * @param msg
     * @return
     */
    public static <T> MynResponse<T> noLogin(String msg) {
        MynResponse<T> response = new MynResponse<>();
        response.code = 401;
        response.msg = msg;
        return response;
    }

    public static MynResponse<?> failed(Integer code, String msg) {
        MynResponse<?> response = new MynResponse<>();
        response.code = code;
        response.msg = msg;
        return response;
    }
}
