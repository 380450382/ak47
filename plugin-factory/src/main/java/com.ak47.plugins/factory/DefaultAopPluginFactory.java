package com.ak47.plugins.factory;

import com.ak47.plugins.config.PluginDefinition;
import com.ak47.plugins.enums.PluginSourceEnum;
import com.ak47.plugins.exception.PluginException;
import com.ak47.plugins.redis.RedisService;
import com.alibaba.fastjson.JSON;
import org.aopalliance.aop.Advice;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DefaultAopPluginFactory implements AopPluginFactory,InitializingBean {
    private final static Logger logger = LoggerFactory.getLogger(DefaultAopPluginFactory.class);
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private Environment environment;
    @Autowired
    private RedisService redisService;
    private Map<Integer,PluginDefinition> definitionCache = new ConcurrentHashMap<>();
    private Map<String,Advice> adviceCache = new ConcurrentHashMap<>();
    @Value("${plugin.base_dir}")
    private String BASE_DIR = System.getProperty("user.dir") + "/.plugins";
    @Value("${plugin.plugin_cache}")
    private String PLUGIN_CACHE = "plugin_cache.pc";
    @Value("${redis.remote-key}")
    private String REMOTE_KEY = "remote_key";
    @Value("${redis.remote-expire}")
    private Long REMOTE_EXPIRE = 50000L;
    @Value("${plugin.remote_resource}")
    private String REMOTE_RESOURCE = "file://" + System.getProperty("user.dir") + "/resource/plugins.all";
    {
//        BASE_DIR = StringUtils.isBlank(environment.getProperty("plugin.base_dir"))?System.getProperty("user.dir") + "/.plugins":environment.getProperty("plugin.base_dir");
//        PLUGIN_CACHE = StringUtils.isBlank(environment.getProperty("plugin.plugin_cache"))?"plugin_cache.pc":environment.getProperty("plugin.plugin_cache");
//        REMOTE_RESOURCE = StringUtils.isBlank(environment.getProperty("plugin.remote_resource"))?"file://" + System.getProperty("user.dir") + "/resource/plugins.all":environment.getProperty("plugin.remote_resource");
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    public void init(){
        printProperty();
        initDefinitionCache();
        initAdviceCache();
    }

    public void printProperty(){
        logger.info("plugins基础属性配置");
        logger.info("base_dir:{}",BASE_DIR);
        logger.info("plugin_cache:{}",PLUGIN_CACHE);
        logger.info("remote_resource:{}",REMOTE_RESOURCE);
    }

    public void initAdviceCache(){
        definitionCache.keySet().forEach(key -> {
            PluginDefinition pluginDefinition = definitionCache.get(key);
            if(pluginDefinition.getActive()){
                int count = pluginDefinition.getCount();
                pluginDefinition.clearCount();
                for (int i = 0; i < count; i++) {
                    enablePlugin(pluginDefinition.getId(),false);
                }
            }
        });
    }

    public void initDefinitionCache(){
        List<PluginDefinition> pluginDefinitions = redisService.getList(REMOTE_KEY,PluginDefinition.class);
        if(CollectionUtils.isEmpty(pluginDefinitions)){
            pluginDefinitions = getPluginList(PluginSourceEnum.LOCAL_FILE_PLUGIN);
        }
        pluginDefinitions.forEach(pluginDefinition -> definitionCache.put(pluginDefinition.getId(),pluginDefinition));
    }

    public void enablePlugin(int pluginId) {
        enablePlugin(pluginId,true);
    }

    @Override
    public void enablePlugin(int pluginId,boolean isCover) {
        PluginDefinition pluginDefinition = definitionCache.get(pluginId);
        if(pluginDefinition == null) {
            throw new PluginException("启动失败,可能还未安装");
        }
        Advice advice = adviceCache.get(pluginDefinition.getClassName());
        if(advice == null){
            advice = buildPlugin(pluginDefinition);
            if(advice == null){
                throw new PluginException("启动失败,构建插件失败");
            }
            pluginDefinition.increaseCount();
        } else {
            if(isCover) {
                disablePlugin(pluginId);
            }
            pluginDefinition.increaseCount();
        }
        for (String beanDefinitionName : applicationContext.getBeanDefinitionNames()) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            if(bean instanceof Advised){
                if(StringUtils.isBlank(pluginDefinition.getExpression())){
                    ((Advised)bean).addAdvice(advice);
                } else {
                    AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
                    advisor.setExpression(pluginDefinition.getExpression());
                    advisor.setAdvice(advice);
                    ((Advised)bean).addAdvisor(advisor);
                }
            }
        }
        pluginDefinition.setActive(true);
        storeDefinitionCache();
    }

    public void disablePlugin(int pluginId) {
        disablePlugin(pluginId,true);
    }

    @Override
    public void disablePlugin(int pluginId, boolean isClear) {
        PluginDefinition pluginDefinition = definitionCache.get(pluginId);
        if(pluginDefinition == null) {
            throw new PluginException("禁用失败,可能还未安装");
        }
        Advice advice = adviceCache.get(pluginDefinition.getClassName());
        if(advice == null){
            throw new PluginException("禁用失败,未找到插件");
        }
        for (String beanDefinitionName : applicationContext.getBeanDefinitionNames()) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            if(bean instanceof Advised){
                if(isClear) {
                    int count = pluginDefinition.getCount();
                    for (int i = 0; i < count; i++) {
                        ((Advised) bean).removeAdvice(advice);
                    }
                } else {
                    ((Advised) bean).removeAdvice(advice);
                }
            }
        }
        if(isClear) {
            pluginDefinition.clearCount();
        } else {
            pluginDefinition.decrementCount();
        }
        storeDefinitionCache();
    }

    @Override
    public void updatePluginExpression(int pluginId, String expression) {
        PluginDefinition pluginDefinition = definitionCache.get(pluginId);
        if(pluginDefinition == null) {
            throw new PluginException("修改失败,可能还未安装");
        }
        disablePlugin(pluginId);
        pluginDefinition.setExpression(expression);
        storeDefinitionCache();
        enablePlugin(pluginId);
    }

    @Override
    public void installPlugin(PluginDefinition pluginDefinition) {
        if(definitionCache.containsKey(pluginDefinition.getId())){
            logger.warn("{}:已存在插件",pluginDefinition.getId());
            throw new PluginException(String.format("%s:已存在插件",pluginDefinition.getId()));
        }

        definitionCache.put(pluginDefinition.getId(),pluginDefinition);

        try {
            buildPlugin(pluginDefinition);
            storeDefinitionCache();
        } catch (Exception e) {
            definitionCache.remove(pluginDefinition.getId());
            logger.error("安装失败: {}",e.getMessage());
            throw new PluginException(e.getMessage());
        }

        if(pluginDefinition.getActive()){
            enablePlugin(pluginDefinition.getId());
        }
    }

    @Override
    public void uninstallPlugin(int pluginId) {
        if(!definitionCache.containsKey(pluginId)){
            logger.warn("{}:不存在插件",pluginId);
            throw new PluginException(String.format("%s:不存在插件",pluginId));
        }
        disablePlugin(pluginId);
        definitionCache.remove(pluginId);
        storeDefinitionCache();
    }

    public String fetchFile(String filePath){
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pluginDefinitionJson.toString();
    }

    public String fetchUrl(String resourceUrl){
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

    @Override
    public List<PluginDefinition> getPluginList(PluginSourceEnum pluginSourceEnum) {
        String pluginDefinitionJson = null;
        switch (pluginSourceEnum){
            case LOCAL_FILE_PLUGIN:
                pluginDefinitionJson = fetchFile(getLoclFile(PLUGIN_CACHE));
                break;
            case LOCAL_CACHE_PLUGIN:
                pluginDefinitionJson = JSON.toJSONString(definitionCache.values());
                break;
            case ALL_PLUGIN:
                pluginDefinitionJson = fetchUrl(REMOTE_RESOURCE);
                break;
            case REMOTE_PLUGIN:

                break;
        }
        if(StringUtils.isNotBlank(pluginDefinitionJson)){
            return JSON.parseArray(pluginDefinitionJson,PluginDefinition.class);
        }
        return new ArrayList<>();
    }

    public Advice buildPlugin(PluginDefinition pluginDefinition){
        if(adviceCache.containsKey(pluginDefinition.getClassName())){
            return adviceCache.get(pluginDefinition.getClassName());
        }
        File file = new File(getLoclFile(pluginDefinition.getJar()));
        if(!file.exists()){
            try {
                URL url = new URL(pluginDefinition.getUrl());
                InputStream inputStream = url.openStream();
                if(!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                Files.copy(inputStream,file.toPath());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        URLClassLoader urlClassLoader = (URLClassLoader) getClass().getClassLoader();
        try {
            URL jarUrl = file.toURI().toURL();
            boolean exist = false;
            for (URL url : urlClassLoader.getURLs()) {
                if(url.equals(jarUrl)){
                    exist = true;
                    break;
                }
            }
            if(!exist){
                try {
                   Method addURLMethod = URLClassLoader.class.getDeclaredMethod("addURL",URL.class);
                   addURLMethod.setAccessible(true);
                   addURLMethod.invoke(urlClassLoader,jarUrl);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            Advice advice = (Advice) Class.forName(pluginDefinition.getClassName()).getConstructor().newInstance();
            adviceCache.put(pluginDefinition.getClassName(), advice );
            return advice;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new PluginException("构建失败");
    }

    public String getLoclFile(String file){
        return BASE_DIR + "/" + file;
    }

    public void storeDefinitionCache(){
        String definitions = JSON.toJSONString(definitionCache.values());
        File file = new File(getLoclFile(PLUGIN_CACHE));
        try {
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
                file.createNewFile();
            } else if(!file.exists()){
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileOutputStream outputStream = new FileOutputStream(file);
             Writer writer = new OutputStreamWriter(outputStream)){
            writer.write(definitions);
            writer.flush();
            redisService.set(REMOTE_KEY,definitionCache.values(),REMOTE_EXPIRE);
            return;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new PluginException("持久化失败");
    }
}
