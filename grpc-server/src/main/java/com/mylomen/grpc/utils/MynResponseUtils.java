package com.mylomen.grpc.utils;


import com.mylomen.gprc.client.domain.MynResponse;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

public class MynResponseUtils {

    /**
     * 是否ok
     *
     * @param response
     * @param <T>
     * @return
     */
    public static <T> boolean isOk(MynResponse<T> response) {
        return Objects.nonNull(response)
                && (response.getCode() == 0 || response.getCode() == 200);
    }

    public static <T> boolean notEmpty(MynResponse<T> response) {
        return Objects.nonNull(response)
                && (response.getCode() == 0 || response.getCode() == 200)
                && !ObjectUtils.isEmpty(response.getData());
    }

    public static <T> boolean isEmpty(MynResponse<T> response) {
        return Objects.isNull(response)
                || (response.getCode() != 0 && response.getCode() != 200)
                || ObjectUtils.isEmpty(response.getData());
    }
}
