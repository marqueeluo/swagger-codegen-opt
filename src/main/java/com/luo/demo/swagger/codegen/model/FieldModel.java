package com.luo.demo.swagger.codegen.model;

import com.luo.demo.swagger.codegen.utils.CommonUtils;
import lombok.Builder;
import lombok.Data;

/**
 * 属性模型
 *
 * @author luohq
 * @date 2021-09-16 19:54
 */
@Data
@Builder
public class FieldModel {
    private String name;
    private String type;
    private String typeClass;
    private String desc;
    private Boolean required;
    private Boolean isLast;
    public String getNameFirstUpper() {
        return CommonUtils.firstCharUpper(name);
    }
}
