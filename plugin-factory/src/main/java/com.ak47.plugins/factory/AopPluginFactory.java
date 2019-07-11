package com.ak47.plugins.factory;


import com.ak47.plugins.config.PluginDefinition;
import com.ak47.plugins.enums.PluginSourceEnum;

import java.util.List;

public interface AopPluginFactory {
    void enablePlugin(int pluginId,boolean isCover,String expression);

    void disablePlugin(int pluginId,boolean isClear,String expression);

    void installPlugin(PluginDefinition pluginDefinition);

    void updatePluginExpression(int pluginId,String expression);

    void uninstallPlugin(int pluginId);

    List<PluginDefinition> getPluginList(PluginSourceEnum pluginSourceEnum);
}
