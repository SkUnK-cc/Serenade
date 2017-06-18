package com.example.serenade.serenade.retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Serenade on 17/6/16.
 */

public class BaseResponse<T> {

    @SerializedName("showapi_res_body")
    public T data;

    @SerializedName("showapi_res_code")
    public int code;

    @SerializedName("showapi_res_error")
    public String message;
}
