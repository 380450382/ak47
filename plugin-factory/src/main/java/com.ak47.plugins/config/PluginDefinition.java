package com.ak47.plugins.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class PluginDefinition implements Serializable {
    private static final long serialVersionUID = -4864158944621073956L;
    private int id;
    private String name;
    private String url;
    private String jar;
    private String className;
    private Boolean active;
    private String version;
    private String expression;
    private String description;
    private int count;


    public int getId() {
        String id = name + url + jar + className;
        return id.hashCode();
    }


    public synchronized void increaseCount(){
        this.setCount(this.getCount()+1);
    }

    public synchronized void decrementCount(){
        this.setCount(this.getCount()-1);
        if(this.getCount()<=0){
            this.setCount(0);
        }
        if(this.getCount() == 0) {
            this.setActive(false);
        }
    }

    public synchronized void clearCount(){
        this.setCount(0);
        this.setActive(false);
    }
}
