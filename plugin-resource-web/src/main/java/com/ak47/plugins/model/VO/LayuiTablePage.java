package com.ak47.plugins.model.VO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class LayuiTablePage<T> {
    private int code ;
    private String msg;
    private Long count;
    private List<T> data;
}
