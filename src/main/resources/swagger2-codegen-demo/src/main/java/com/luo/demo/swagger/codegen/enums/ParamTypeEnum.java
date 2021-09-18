package com.luo.demo.swagger.codegen.enums;

import io.swagger.models.parameters.*;

import java.util.stream.Stream;

/**
 * 参数类型 - 枚举
 *
 * @author luo
 * @date 2021-09-16
 */
public enum ParamTypeEnum {
    QUERY("query", QueryParameter.class, null),
    FORM("formData", FormParameter.class, null),
    PATH("path", PathParameter.class, "spring.PathVariable"),
    HEADER("header", HeaderParameter.class, "spring.Header"),
    BODY("body", BodyParameter.class, "spring.RequestBody"),
    COOKIE("cookies", CookieParameter.class, "spring.Cookie");

    private String in;
    private Class swaggerParamType;
    private String annoType;

    ParamTypeEnum(String in, Class swaggerParamType, String annoType) {
        this.in = in;
        this.swaggerParamType = swaggerParamType;
        this.annoType = annoType;
    }

    public static ParamTypeEnum swaggerParamTypeOf(Parameter parameter) {
        return Stream.of(values())
                .filter(curEnum -> curEnum.getSwaggerParamType().equals(parameter.getClass()))
                .findFirst()
                .orElse(null);
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public Class getSwaggerParamType() {
        return swaggerParamType;
    }

    public void setSwaggerParamType(Class swaggerParamType) {
        this.swaggerParamType = swaggerParamType;
    }

    public String getAnnoType() {
        return annoType;
    }

    public void setAnnoType(String annoType) {
        this.annoType = annoType;
    }
}
