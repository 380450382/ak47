package com.ak47.plugins.common;

import lombok.Getter;

@Getter
public enum RequestCodeEnum {
    SUCCESS(0,"成功"),
    ERROR(500,"服务端错误"),
    NOT_FOUND(404,"找不到"),
    ;
    private int code;
    private String desc;

    RequestCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
