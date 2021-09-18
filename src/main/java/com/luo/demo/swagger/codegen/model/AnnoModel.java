package com.luo.demo.swagger.codegen.model;

import com.luo.demo.swagger.codegen.utils.CommonUtils;
import lombok.Builder;
import lombok.Data;

import java.util.LinkedHashMap;

/**
 * 注解模型
 *
 * @author luohq
 * @date 2021-09-16 20:36
 */
@Data
@Builder
public class AnnoModel {
    private String type;
    private String typeClass;
    private LinkedHashMap<String, String> paramValMap = new LinkedHashMap<>();

    public static LinkedHashMap<String, String> buildParamValMap(String name, String val, String... kvs) {
        LinkedHashMap<String, String> pvMap = new LinkedHashMap<>();
        pvMap.put(name, val);
        if (null == kvs || 0 >= kvs.length) {
            return pvMap;
        }
        for (int i = 1; i < kvs.length; i += 2) {
            pvMap.put(kvs[i - 1], kvs[i]);
        }
        return pvMap;
    }
    public static final AnnoModel RequestBody = AnnoModel.builder()
            .type("RequestBody")
            .typeClass("org.springframework.web.bind.annotation.RequestBody")
            .build();
    public static final AnnoModel ResponseBody = AnnoModel.builder()
            .type("ResponseBody")
            .typeClass("org.springframework.web.bind.annotation.ResponseBody")
            .build();
    public static final AnnoModel Controller = AnnoModel.builder()
            .type("Controller")
            .typeClass("org.springframework.stereotype.Controller")
            .build();


    public static final AnnoModel RequestMapping = AnnoModel.builder()
            .type("RequestMapping")
            .typeClass("org.springframework.web.bind.annotation.RequestMapping")
            .paramValMap(AnnoModel.buildParamValMap("value", CommonUtils.strQuotes("/demo")))
            .build();


}
