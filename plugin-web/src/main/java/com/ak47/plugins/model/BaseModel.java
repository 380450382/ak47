package com.ak47.plugins.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class BaseModel<T> implements Serializable {
    private T id;
    private Date modifiedDate;
    private Date createdDate;
}
