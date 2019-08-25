package com.ak47.plugins.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

public final class FileUtil {
    private FileUtil(){
        super();
    }

    public static File ifAbsent(String path) throws IOException {
        if(StringUtils.isBlank(path)){
            return null;
        }
        File file = new File(path);
        if (!file.exists()){
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        return file;
    }

    public static boolean exists(String path) {
        if(StringUtils.isBlank(path)){
            return false;
        }
        File file = new File(path);
        return file.exists();
    }
}
