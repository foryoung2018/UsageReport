package com.htc.lib1.dm.exception;

import java.util.Map;

/**
 * Created by Joe_Wu on 3/8/15.
 */
public class DMUnauthorizedException extends DMException {
    private Integer code;
    private Map<String,Object> data;

    public DMUnauthorizedException(Integer code, Map<String,Object> data) {
        super("DM Unauthorized code:["+code+"]");
        this.code = code;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
