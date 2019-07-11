package com.ak47.plugins.factory;

import com.ak47.plugins.config.ResourceDefinition;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface ResourceFactory {

    List<ResourceDefinition> getResourcesList();

    File getJar(int id,String temp,String type);

    boolean uploadResources(ResourceDefinition resourceDefinition, InputStream inputStream, boolean isCover);

    boolean deleteResources(int resourcesId, boolean delFile);

    String downloadResource();

    boolean addUploadFile(File file);

    File getLocalFile(String filename);
}
