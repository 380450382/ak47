package com.ak47.plugins.controller;


import com.ak47.plugins.common.RequestCodeEnum;
import com.ak47.plugins.config.PluginDefinition;
import com.ak47.plugins.enums.PluginSourceEnum;
import com.ak47.plugins.exception.PluginException;
import com.ak47.plugins.factory.AopPluginFactory;
import com.ak47.plugins.model.VO.BaseResult;
import com.ak47.plugins.model.VO.LayuiTablePage;
import com.ak47.plugins.model.VO.LayuiTablePageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class ApiController {

    @Autowired
    private AopPluginFactory aopPluginFactory;

    @GetMapping("getList")
    public LayuiTablePage<PluginDefinition> getList(int code ) {
        for (PluginSourceEnum pluginSourceEnum : PluginSourceEnum.values()) {
            if(pluginSourceEnum.getCode() == code){
                return LayuiTablePageFactory.success(aopPluginFactory.getPluginList(pluginSourceEnum));
            }
        }
        return null;
    }

    @GetMapping("installPlugin")
    public BaseResult installPlugin(int pluginsId, String expression) {
        for (PluginDefinition pluginDefinition : aopPluginFactory.getPluginList(PluginSourceEnum.ALL_PLUGIN)) {
            if(pluginDefinition.getId() == pluginsId){
                pluginDefinition.setExpression(expression);
                try {
                    aopPluginFactory.installPlugin(pluginDefinition);
                } catch (PluginException e) {
                    return BaseResult.fail(RequestCodeEnum.ERROR);
                }
                break;
            }
        }
        return BaseResult.success();
    }

    @GetMapping("updatePluginExpression")
    public BaseResult updatePluginExpression(int pluginsId,String expression) {
        aopPluginFactory.updatePluginExpression(pluginsId,expression);
        return BaseResult.success();
    }

    @GetMapping("enablePlugin")
    public BaseResult enablePlugin(int pluginsId,boolean isCover) {
        aopPluginFactory.enablePlugin(pluginsId,isCover);
        return BaseResult.success();
    }

    @GetMapping("disablePlugin")
    public BaseResult disablePlugin(int pluginsId,boolean isClear) {
        aopPluginFactory.disablePlugin(pluginsId,isClear);
        return BaseResult.success();
    }

    @GetMapping("uninstallPlugin")
    public BaseResult uninstallPlugin(int pluginsId) {
        aopPluginFactory.uninstallPlugin(pluginsId);
        return BaseResult.success();
    }
}
