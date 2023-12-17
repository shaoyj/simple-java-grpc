package com.mylomen.gprc.client.domain;

import lombok.Getter;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;


@Getter
public class MynPageResult<T> implements Serializable {

    /**
     * 锚点
     */
    private String anchor;

    /**
     * 是否还有数据
     */
    private boolean end;

    /**
     * 数据集合
     */
    private List<T> list;

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public static <T> MynPageResult<T> empty(String anchor) {
        MynPageResult<T> result = new MynPageResult<>();
        result.setList(Collections.emptyList());
        result.setEnd(true);
        result.setAnchor(anchor);
        return result;
    }
}
