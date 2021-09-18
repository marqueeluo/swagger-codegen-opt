package com.luo.demo.swagger.codegen.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * JSON工具类
 *
 * @author luohq
 * @date 2018/7/11
 */
public final class JsonUtils {

    private static final Gson gson = new GsonBuilder()
            //.excludeFieldsWithoutExposeAnnotation() //不对没有用@Expose注解的属性进行操作
            .enableComplexMapKeySerialization() //当Map的key为复杂对象时,需要开启该方法
            //.serializeNulls() //当字段值为空或null时，依然对该字段进行转换
            .setDateFormat("yyyy-MM-dd HH:mm:ss") //时间转化为特定格式
            //.setPrettyPrinting() //对结果进行格式化，增加换行
            .disableHtmlEscaping() //防止特殊字符出现乱码
            .create();

    private JsonUtils() {
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> targetClass) {
        return gson.fromJson(json, targetClass);
    }

    public static <T> T fromJson(String json, TypeToken<T> typeToken) {
        return gson.fromJson(json, typeToken.getType());
    }
}