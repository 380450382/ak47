package com.ak47.plugins.controller;


import com.ak47.plugins.config.PluginDefinition;
import com.ak47.plugins.enums.PluginSourceEnum;
import com.ak47.plugins.factory.AopPluginFactory;
import com.ak47.plugins.model.DTO.DemoDTO;
import com.ak47.plugins.model.VO.DemoVO;
import com.ak47.plugins.service.DemoService;
import com.ak47.plugins.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("demo")
public class DemoController {

    @Autowired
    private DemoService demoService;
    @Autowired
    private AopPluginFactory aopPluginFactory;

    @GetMapping("say")
    public DemoVO say(String name){
        DemoDTO demoDTO = demoService.say(name);
        demoService.hello(name);
        return  ObjectUtils.setTbyObj(DemoVO.class,demoDTO);
    }

    @GetMapping("getList")
    public List<PluginDefinition> getList(int code ) {
        for (PluginSourceEnum pluginSourceEnum : PluginSourceEnum.values()) {
            if(pluginSourceEnum.getCode() == code){
                return aopPluginFactory.getPluginList(pluginSourceEnum);
            }
        }
        return null;
    }

    @GetMapping("install")
    public void install(int pluginsId) {
        for (PluginDefinition pluginDefinition : aopPluginFactory.getPluginList(PluginSourceEnum.ALL_PLUGIN)) {
            if(pluginDefinition.getId() == pluginsId){
                aopPluginFactory.installPlugin(pluginDefinition);
                break;
            }
        }
    }

    @GetMapping("enablePlugin")
    public void enablePlugin(int pluginsId) {
        aopPluginFactory.enablePlugin(pluginsId);
    }

    @GetMapping("disablePlugin")
    public void disablePlugin(int pluginsId) {
        aopPluginFactory.disablePlugin(pluginsId);
    }

    @GetMapping("uninstallPlugin")
    public void uninstallPlugin(int pluginsId) {
        aopPluginFactory.uninstallPlugin(pluginsId);
    }

    @GetMapping("addAdvise")
    public void addAdvise(){

//        for (String beanDefinitionName : applicationContext.getBeanDefinitionNames()) {
//            if(applicationContext.getBean(beanDefinitionName) instanceof Advised){
////                ((Advised)applicationContext.getBean(beanDefinitionName)).addAdvice(new ImportAop());
////                System.out.println(beanDefinitionName);
//                AspectJExpressionPointcutAdvisor advisor1 = new AspectJExpressionPointcutAdvisor();
//                advisor1.setExpression("execution(* com.ak47.plugins.service.impl..*.a*(..))");
//                advisor1.setAdvice(new ImportAop());
//                ((Advised)applicationContext.getBean(beanDefinitionName)).addAdvisor(advisor1);
//            }
//        }
    }

    @GetMapping("removeAdvise")
    public void removeAdvise(){
//        for (String beanDefinitionName : applicationContext.getBeanDefinitionNames()) {
//            if(applicationContext.getBean(beanDefinitionName) instanceof Advised){
//                Advised advised = ((Advised) applicationContext.getBean(beanDefinitionName));
//                for (Advisor advisor : advised.getAdvisors()) {
//                    if(advisor.getAdvice() instanceof ImportAop){
//                        advised.removeAdvisor(advisor);
//                    }
//                }
//                System.out.println(beanDefinitionName);
//            }
//        }
    }
}
