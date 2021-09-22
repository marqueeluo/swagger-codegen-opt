package com.luo.demo.swagger.codegen.model;

import com.luo.demo.swagger.codegen.convertor.Config;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Api模型
 *
 * @author luohq
 * @date 2021-09-16 19:53
 */
@Data
@Builder
public class ApiModel {
    private Config config;
    private String basePackage;
    private String name;
    private String desc;
    private String basePath;
    private List<PathModel> paths;
}
