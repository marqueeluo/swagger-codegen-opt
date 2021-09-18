package com.luo.demo.swagger.codegen.enums;

import java.util.stream.Stream;

/**
 * 属性类型映射 - 枚举
 *
 * @author luohq
 * @date 2021-09-16 20:02
 */
public enum FieldTypeEnum {
    STRING("string", "String", "java.lang.String"),
    NUMBER("number", "Double", "java.lang.Double"),
    INT("integer", "Integer", "java.lang.Integer"),
    LONG("long", "Long", "java.lang.Long"),
    REF("ref", "", "java.lang.Object"),
    FILE("file", "MultipartFile", "org.springframework.web.multipart.MultipartFile"),
    OBJECT("object", "Object", "java.lang.Object"),
    ARRAY("array", "List", "java.util.List");

    private String paramType;
    private String fieldTypeName;
    private String fieldTypeClass;

    FieldTypeEnum(String paramType, String fieldTypeName, String fieldTypeClass) {
        this.paramType = paramType;
        this.fieldTypeName = fieldTypeName;
        this.fieldTypeClass = fieldTypeClass;
    }

    public static FieldTypeEnum paramTypeOf(String paramType) {
        return Stream.of(values())
                .filter(curEnum -> curEnum.getParamType().equals(paramType))
                .findFirst()
                .orElse(OBJECT);
    }

    public static FieldTypeEnum fieldTypeOf(String fieldType) {
        return Stream.of(values())
                .filter(curEnum -> curEnum.getFieldTypeName().equals(fieldType))
                .findFirst()
                .orElse(OBJECT);
    }

    public Boolean isPrimitiveType() {
        return !OBJECT.equals(this) && !ARRAY.equals(this);
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getFieldTypeName() {
        return fieldTypeName;
    }

    public void setFieldTypeName(String fieldTypeName) {
        this.fieldTypeName = fieldTypeName;
    }

    public String getFieldTypeClass() {
        return fieldTypeClass;
    }

    public void setFieldTypeClass(String fieldTypeClass) {
        this.fieldTypeClass = fieldTypeClass;
    }
}
