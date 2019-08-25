package com.ak47.plugins.remote;

import java.io.File;
import java.io.InputStream;

public interface RemoteResourceHandler {

    boolean support(String url);

    String uploadHandle(InputStream inputStream,String fileName);

    boolean deleteHandle(String jar);

    boolean downloadResourceHandler(String path);

    File downloadJar(String filename,String temp);

    String fetchResourceContent(String path);

    boolean storeResourceFileHandler(File file);

}
