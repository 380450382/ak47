package com.ak47.plugins.factory;


import com.ak47.plugins.config.PluginDefinition;
import com.ak47.plugins.enums.PluginSourceEnum;

import java.util.List;

public interface AopPluginFactory {
    void enablePlugin(int pluginId);

    void disablePlugin(int pluginId);

    void installPlugin(PluginDefinition pluginDefinition);

    void uninstallPlugin(int pluginId);

    List<PluginDefinition> getPluginList(PluginSourceEnum pluginSourceEnum);
}
