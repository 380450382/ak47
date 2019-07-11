package com.ak47.plugins.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class ResourceDefinition implements Serializable {
    private int id;
    private String name;
    private String url;
    private String jar;
    private String className;
    private Boolean active;
    private String version;
    private String description;


    public int getId() {
        String id = name + jar + className + version;
        return id.hashCode();
    }
}
