package com.ak47.plugins.model.VO;

import com.ak47.plugins.common.RequestCodeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApiResult<T> extends BaseResult {
    private T t;

    public static<T> ApiResult<T> success(T t){
        ApiResult apiResult = new ApiResult();
        apiResult.setCode(RequestCodeEnum.SUCCESS.getCode());
        apiResult.setMsg(RequestCodeEnum.SUCCESS.getDesc());
        apiResult.setSuccess(true);
        apiResult.setT(t);
        return apiResult;
    }
}
