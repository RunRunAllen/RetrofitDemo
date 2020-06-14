package com.example.retrofitdemo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;

/*
 *  记录 类型 ，参数
 * */
public class ServiceMethod {

    private String httpMethod;
    private String httpUrl;
    private boolean hasBody;
    private FormBody.Builder formBuild;
    HttpUrl baseUrl;
    HttpUrl.Builder urlBuilder;
    private ParameterHandler[] parameterHandler;
    private Call.Factory callFactory;

    public ServiceMethod(Builder builder) {
        baseUrl = builder.retrofit.baseUrl;
        httpMethod = builder.httpMethod;
        httpUrl = builder.httpUrl;
        hasBody = builder.hasBody;
        parameterHandler = builder.parameterHandler;
        this.callFactory = builder.retrofit.callFactory;

        //如果是有请求体,创建一个okhttp的请求体对象
        if (hasBody) {
            formBuild = new FormBody.Builder();
        }
    }

    public void addQueryParameter(String key, String value) {

    }

    public void addFiledParameter(String key, String value) {

    }

    public Object invoke(Object[] args) {
        /**
         * 1  处理请求的地址与参数
         */
        for (int i = 0; i < parameterHandler.length; i++) {
            ParameterHandler handlers = parameterHandler[i];
            //handler内本来就记录了key,现在给到对应的value
            handlers.apply(this, args[i].toString());
        }

        //获取最终请求地址
        HttpUrl url;
        if (urlBuilder == null) {
            urlBuilder = baseUrl.newBuilder(httpUrl);
        }
        url = urlBuilder.build();

        //请求体
        FormBody formBody = null;
        if (formBuild != null) {
            formBody = formBuild.build();
        }

        Request request = new Request.Builder().url(url).method(httpMethod, formBody).build();
        return callFactory.newCall(request);
    }

    public static class Builder {

        private final CustomRetroift retrofit;
        private final Annotation[] methodAnnotations;
        private final Annotation[][] parameterAnnotations;
        private String httpMethod;
        private String httpUrl;
        private boolean hasBody;
        private FormBody.Builder formBuild;
        HttpUrl baseUrl;
        HttpUrl.Builder urlBuilder;
        private ParameterHandler[] parameterHandler;

        public Builder(CustomRetroift customRetroift, Method method) {
            this.retrofit = customRetroift;
            methodAnnotations = method.getAnnotations();
            parameterAnnotations = method.getParameterAnnotations();
        }

        public ServiceMethod build() {
            //1 解析方法上的注解
            for (Annotation annotation : methodAnnotations) {
                if (annotation instanceof POST) {
                    //post  类型
                    // 记录类型
                    this.httpMethod = "POST";
                    // 获取value
                    httpUrl = ((POST) annotation).value();
                    //是否有请求体
                    hasBody = true;


                } else {
                    //Get 或者其他类型

                }
            }

            /**
             * 2 解析方法参数的注解
             */
            int length = parameterAnnotations.length;
            parameterHandler = new ParameterHandler[length];
            for (int i = 0; i < length; i++) {
                // 一个参数上的所有的注解
                Annotation[] annotations = parameterAnnotations[i];
                // 处理参数上的每一个注解
                for (Annotation annotation : annotations) {
                    //todo 可以加一个判断:如果httpMethod是get请求,现在又解析到Filed注解,可以提示使用者使用Query注解
                    if (annotation instanceof Field) {
                        //得到注解上的value: 请求参数的key
                        String value = ((Field) annotation).value();
                        parameterHandler[i] = new ParameterHandler.FiledParameterHandler(value);
                    } else if (annotation instanceof Query) {
                        String value = ((Query) annotation).value();
                        parameterHandler[i] = new ParameterHandler.QueryParameterHandler(value);

                    }
                }
            }

            return new ServiceMethod(this);

        }

    }
}
