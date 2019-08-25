package com.ak47.plugins.factory;

import com.ak47.plugins.common.SystemContent;
import com.ak47.plugins.exception.ResourceException;
import com.ak47.plugins.remote.RemoteResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractResourceFactory implements ResourceFactory {
    private static final Logger logger = LoggerFactory.getLogger(AbstractResourceFactory.class);

    @Autowired
    private ApplicationContext applicationContext;

    private List<RemoteResourceHandler> resourceHandlers = new ArrayList<>();
    private Map<String, File> uploadLocalFile = new ConcurrentHashMap<>();

    @Value("${resource.resource_name}")
    protected String RESOURCE_NAME = "resource.all";

    @Value("${resource.resource_type}")
    protected String RESOURCE_TYPE = "ftp";

    @Value("${resource.base_dir}")
    public String BASE_DIR = System.getProperty("user.dir") + "/.resource";

    protected void init(){
        printProperty();
        initResourceHandlers();
    }

    @Override
    public boolean addUploadFile(File file){
        if(uploadLocalFile.containsKey(file.getName())){
            return false;
        }
        uploadLocalFile.put(file.getName(),file);
        return true;
    }

    @Override
    public synchronized File getLocalFile(String filename){
        File file = uploadLocalFile.get(filename);
        uploadLocalFile.remove(filename);
        return file;
    }

    private void printProperty(){
        logger.info("plugins基础属性配置");
        logger.info("base_dir:{}",BASE_DIR);
        logger.info("resource_name:{}",RESOURCE_NAME);
        logger.info("resource_type:{}",RESOURCE_TYPE);
    }

    private void initResourceHandlers(){
        for (String beanDefinitionName : applicationContext.getBeanDefinitionNames()) {
            if(applicationContext.getBean(beanDefinitionName) instanceof RemoteResourceHandler){
                resourceHandlers.add((RemoteResourceHandler) applicationContext.getBean(beanDefinitionName));
            }
        }
    }


    protected RemoteResourceHandler getResourceHandler(String url){
        for (RemoteResourceHandler resourcesHandler : resourceHandlers) {
            if (resourcesHandler.support(url)) {
                return resourcesHandler;
            }
        }
        throw new ResourceException("没找到资源处理器");
    }

    protected String getLoclFile(String file) {
        return BASE_DIR + SystemContent.SEPARATOR + file;
    }

}
