package com.luo.demo.swagger.codegen.model;

import lombok.Builder;
import lombok.Data;

/**
 * 参数模型
 *
 * @author luohq
 * @date 2021-09-16 19:53
 */
@Data
@Builder
public class ParamModel {
    private String name;
    private String type;
    private String typeClass;
    private String desc;
    private Boolean required;
    private Boolean isFormObjParam;
    private Boolean isQueryParam;
    private Boolean isPathParam;
    private Boolean isHeaderParam;
    private Boolean isBodyParam;
    private Boolean isFormParam;
    private Boolean isFile;
    private Boolean isLast;
}
