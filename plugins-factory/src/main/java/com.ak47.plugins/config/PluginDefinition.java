package com.ak47.plugins.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class PluginDefinition implements Serializable {
    private int id;
    private String name;
    private String url;
    private String jar;
    private String className;
    private Boolean active;
    private String version;
    private String expression;


    public int getId() {
        String id = name + url + jar + className;
        return id.hashCode();
    }
}
