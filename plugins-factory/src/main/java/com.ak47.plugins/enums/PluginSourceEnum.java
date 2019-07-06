package com.ak47.plugins.enums;

import lombok.Getter;

@Getter
public enum PluginSourceEnum {
    REMOTE_PLUGIN(1,"缓存框架列表"),
    LOCAL_FILE_PLUGIN(2,"本地文件列表"),
    LOCAL_CACHE_PLUGIN(3,"本地缓存列表"),
    ALL_PLUGIN(4,"所有列表"),
    ;

    PluginSourceEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int code;
    private String desc;
}
