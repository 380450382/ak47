package com.ak47.plugins.factory;

import com.ak47.plugins.exception.PluginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class AbstractAopPluginFactory implements AopPluginFactory {
    private final static Logger logger = LoggerFactory.getLogger(AbstractAopPluginFactory.class);

    @Value("${plugin.base_dir}")
    private String BASE_DIR = System.getProperty("user.dir") + "/.plugins";
    @Value("${plugin.plugin_cache}")
    protected String PLUGIN_CACHE = "plugin_cache.pc";
    @Value("${redis.remote-key}")
    protected String REMOTE_KEY = "remote_key";
    @Value("${redis.remote-expire}")
    protected Long REMOTE_EXPIRE = 50000L;
    @Value("${plugin.remote_resource}")
    protected String REMOTE_RESOURCE = "file://" + System.getProperty("user.dir") + "/resource/plugins.all";
    {
//        BASE_DIR = StringUtils.isBlank(environment.getProperty("plugin.base_dir"))?System.getProperty("user.dir") + "/.plugins":environment.getProperty("plugin.base_dir");
//        PLUGIN_CACHE = StringUtils.isBlank(environment.getProperty("plugin.plugin_cache"))?"plugin_cache.pc":environment.getProperty("plugin.plugin_cache");
//        REMOTE_RESOURCE = StringUtils.isBlank(environment.getProperty("plugin.remote_resource"))?"file://" + System.getProperty("user.dir") + "/resource/plugins.all":environment.getProperty("plugin.remote_resource");
    }

    protected void init(){
        printProperty();
    }

    private void printProperty(){
        logger.info("plugins基础属性配置");
        logger.info("base_dir:{}",BASE_DIR);
        logger.info("plugin_cache:{}",PLUGIN_CACHE);
        logger.info("remote_resource:{}",REMOTE_RESOURCE);
    }

    protected void enablePlugin(int pluginId) {
        enablePlugin(pluginId,true,null);
    }


    protected void disablePlugin(int pluginId) {
        disablePlugin(pluginId,true,null);
    }


    protected String fetchFile(String filePath){
        StringBuilder pluginDefinitionJson = new StringBuilder();
        File file = new File(filePath);
        if(file.exists()){
            try(InputStream inputStream = new FileInputStream(file);
                Reader reader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(reader)) {
                String line;
                while((line = bufferedReader.readLine()) != null){
                    pluginDefinitionJson.append(line);
                }
                return pluginDefinitionJson.toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected String fetchUrl(String resourceUrl){
        StringBuilder pluginDefinitionJson = new StringBuilder();
        try {
            URL url = new URL(resourceUrl);
            try(InputStream inputStream = url.openStream();
                Reader reader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(reader)) {
                String line;
                while((line = bufferedReader.readLine()) != null){
                    pluginDefinitionJson.append(line);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pluginDefinitionJson.toString();
    }

    public String getLoclFile(String file){
        return BASE_DIR + System.getProperty("file.separator") + file;
    }

}
