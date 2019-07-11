package com.ak47.plugins.remote;

import java.io.File;
import java.io.InputStream;

public interface RemoteResourceHandler {

    boolean support(String url);

    String uploadHandle(InputStream inputStream,String fileName);

    boolean deleteHandle(String jar);

    boolean downloadHandle(String filename,String path);

    File downloadJar(String filename,String temp);

    String downloadResource(String filename);

    boolean storeResourceFileHandler(File file);

}
