package com.ak47.plugins.model.VO;

import com.ak47.plugins.common.RequestCodeEnum;

import java.util.List;

public final class LayuiTablePageFactory {
    private LayuiTablePageFactory() {
    }

    public static<T> LayuiTablePage<T> success(List<T> data){
        LayuiTablePage<T> layuiTablePage = new LayuiTablePage<>();
        layuiTablePage.setCode(RequestCodeEnum.SUCCESS.getCode());
        layuiTablePage.setMsg(RequestCodeEnum.SUCCESS.getDesc());
        layuiTablePage.setCount(data.size()+0L);
        layuiTablePage.setData(data);
        return layuiTablePage;
    }

    public static<T> LayuiTablePage<T> fail(RequestCodeEnum requestCodeEnum){
        LayuiTablePage<T> layuiTablePage = new LayuiTablePage<>();
        layuiTablePage.setCode(requestCodeEnum.getCode());
        layuiTablePage.setMsg(requestCodeEnum.getDesc());
        return layuiTablePage;
    }
}
