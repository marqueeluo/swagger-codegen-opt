package com.luo.demo.swagger.codegen.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * api path模型
 *
 * @author luohq
 * @date 2021-09-16 20:34
 */
@Data
@Builder
public class PathModel {
    private String basePath;
    private String name;
    private String desc;
    private String path;
    private String httpMethod;
    private List<String> consumes;
    private List<String> produces;
    private String returnBaseType;
    private String returnDataType;
    private List<ParamModel> params;

}
