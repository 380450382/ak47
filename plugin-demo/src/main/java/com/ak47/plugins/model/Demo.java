package com.ak47.plugins.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Demo extends BaseModel<Long>{
    private String name;
}
