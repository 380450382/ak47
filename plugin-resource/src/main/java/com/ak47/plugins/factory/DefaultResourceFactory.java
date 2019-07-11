package com.ak47.plugins.factory;

import com.ak47.plugins.config.ResourceDefinition;
import com.ak47.plugins.exception.ResourceException;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DefaultResourceFactory extends AbstractResourceFactory implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(DefaultResourceFactory.class);

    private Map<Integer, ResourceDefinition> definitionCache = new ConcurrentHashMap<>();


    @Override
    public void afterPropertiesSet() throws Exception {
        super.init();
        initDefinitionCache();
    }

    private void initDefinitionCache(){
        boolean isSuccess = getResourceHandler(RESOURCE_TYPE).downloadHandle(RESOURCE_NAME,getLoclFile(RESOURCE_NAME));
        File file = new File(getLoclFile(RESOURCE_NAME));
        if(!isSuccess){
            logger.info("远程资源库下载失败,尝试使用本地资源");
            if(!file.exists()){
                logger.info("本地资源不存在");
                logger.info("尝试创建一个资源文件");
                try {
                    if(!file.getParentFile().exists()){
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    } else {
                        file.createNewFile();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new ResourceException("初始化资源库失败");
                }
            }
        }
        StringBuilder resourceDefinitionJson = new StringBuilder();
        try(InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            while((line = bufferedReader.readLine()) != null){
                resourceDefinitionJson.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceException("读取资源库失败");
        }
        if(StringUtils.isNotBlank(resourceDefinitionJson.toString())){
            List<ResourceDefinition> resourceDefinitions = JSON.parseArray(resourceDefinitionJson.toString(),ResourceDefinition.class);
            resourceDefinitions.forEach(resourceDefinition -> definitionCache.put(resourceDefinition.getId(),resourceDefinition));
        }
        storedefinitionCache();
    }

    @Override
    public List<ResourceDefinition> getResourcesList() {
        return JSON.parseArray(JSON.toJSONString(definitionCache.values()),ResourceDefinition.class);
    }

    @Override
    public File getJar(int id,String temp,String type) {
        for (ResourceDefinition resourceDefinition : getResourcesList()) {
            if(resourceDefinition.getId() == id){
                return getResourceHandler(type).downloadJar(resourceDefinition.getJar(),temp);
            }
        }
        return null;
    }

    @Override
    public boolean uploadResources(ResourceDefinition resourceDefinition,InputStream inputStream, boolean isCover) {
        if (!isCover && definitionCache.containsKey(resourceDefinition.getId())) {
            throw new ResourceException("文件已存在");
        }
        String ftpUrl = getResourceHandler(resourceDefinition.getUrl()).uploadHandle(inputStream,resourceDefinition.getJar());
        if(StringUtils.isBlank(ftpUrl)){
            throw new ResourceException("上传失败");
        }
        String jarUrl = "http://127.0.0.1:8080/api/getJar.jar?id=" + resourceDefinition.getId();
        resourceDefinition.setUrl(jarUrl);
        definitionCache.put(resourceDefinition.getId(),resourceDefinition);
        storedefinitionCache();
        return true;
    }

    @Override
    public boolean deleteResources(int resourcesId, boolean delFile) {
        if (!definitionCache.containsKey(resourcesId)) {
            throw new ResourceException("文件不存在");
        }
        ResourceDefinition definition = definitionCache.get(resourcesId);
        if(!getResourceHandler(definition.getUrl()).deleteHandle(definition.getJar())){
            throw new ResourceException("删除失败");
        }
        definitionCache.remove(resourcesId);
        storedefinitionCache();
        return true;
    }

    @Override
    public String downloadResource() {
        return getResourceHandler(RESOURCE_TYPE).downloadResource(RESOURCE_NAME);
    }

    private void storedefinitionCache() {
        String definitions = JSON.toJSONString(definitionCache.values());
        File file = new File(getLoclFile(RESOURCE_NAME));
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } else if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceException("文件不存在，并创建失败");
        }
        try (OutputStream outputStream = new FileOutputStream(file);
             Writer writer = new OutputStreamWriter(outputStream);){
            writer.write(definitions);
            writer.flush();
            if(!getResourceHandler(RESOURCE_TYPE).storeResourceFileHandler(file)){
                throw new ResourceException("持久化失败");
            }
            return;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new ResourceException("持久化失败");
    }
}
