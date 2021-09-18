package com.luo.demo.swagger.codegen.enums;

/**
 * 对象name、desc后缀 - 枚举
 *
 * @ahthor luohq
 * @date 2021-09-18
 */
public enum ObjSuffixEnum {
    PARAM("Param", " - 参数"),
    RESULT("Result", " - 结果");

    private String nameSuffix;
    private String descSuffix;

    ObjSuffixEnum(String nameSuffix, String descSuffix) {
        this.nameSuffix = nameSuffix;
        this.descSuffix = descSuffix;
    }

    public String getNameSuffix() {
        return nameSuffix;
    }

    public String getDescSuffix() {
        return descSuffix;
    }
}
