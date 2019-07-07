package com.ak47.plugins.model.VO;

import com.ak47.plugins.common.RequestCodeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BaseResult {
    private int code;
    private String msg;
    private boolean success;
    public static BaseResult success(){
        BaseResult baseResult = new BaseResult();
        baseResult.setCode(RequestCodeEnum.SUCCESS.getCode());
        baseResult.setMsg(RequestCodeEnum.SUCCESS.getDesc());
        baseResult.setSuccess(true);
        return baseResult;
    }

    public static BaseResult fail(RequestCodeEnum requestCodeEnum){
        BaseResult baseResult = new BaseResult();
        baseResult.setCode(requestCodeEnum.getCode());
        baseResult.setMsg(requestCodeEnum.getDesc());
        baseResult.setSuccess(false);
        return baseResult;
    }
}
