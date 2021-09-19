package com.luo.demo.swagger.codegen.model;

import com.luo.demo.swagger.codegen.constant.Constants;
import com.luo.demo.swagger.codegen.utils.CommonUtils;
import lombok.Builder;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 对象模型
 *
 * @author luohq
 * @date 2021-09-16 19:54
 */
@Data
@Builder
public class ObjModel {
    private List<String> templateTypes;
    private String basePackage;
    private String name;
    private String desc;
    private List<FieldModel> fields;
    private String typeClass;


    public String getTypeClass() {
        return Optional.ofNullable(typeClass)
                .orElse(CommonUtils.buildStr(basePackage, Constants.DOT, name));
    }

    @Override
    public int hashCode() {
        return Objects.hash(CommonUtils.buildStr(basePackage, this.convertFieldsSortKey()));
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof ObjModel)) {
            return false;
        }
        ObjModel objModel = (ObjModel) obj;
        if (!Objects.equals(this.basePackage, objModel.getBasePackage())
                || !CommonUtils.isSameSizeCollection(this.templateTypes, objModel.getTemplateTypes())
                || !CommonUtils.isSameSizeCollection(this.fields, objModel.getFields())
        ) {
            return false;
        }
        return Objects.equals(this.convertFieldsSortKey(), objModel.convertFieldsSortKey());
    }


    private String convertFieldsSortKey() {
        return fields.stream()
                //按name字母序升序排列
                .sorted(Comparator.comparing(FieldModel::getName))
                //name1:type1,name2:type2
                .map(fieldModel -> CommonUtils.buildStr(fieldModel.getName(), ":", fieldModel.getType()))
                .collect(Collectors.joining(","));
    }

    public static final ObjModel CommonResultObjModel = ObjModel.builder()
            .basePackage(Constants.COMMON_RESULT_PACKAGE)
            .desc("通用结果")
            .name(Constants.COMMON_RESULT_NAME)
            .templateTypes(Arrays.asList("T"))
            .fields(Arrays.asList(
                    FieldModel.builder().name(Constants.RESULT_KEY_RESP_CODE).type("Integer").desc("响应码").typeClass("java.lang.Integer").required(true).build(),
                    FieldModel.builder().name(Constants.RESULT_KEY_MSG).type("String").desc("消息提示").typeClass("java.lang.String").build(),
                    FieldModel.builder().name(Constants.RESULT_KEY_DATA).type("T").desc("响应数据").build(),
                    FieldModel.builder().name(Constants.RESULT_KEY_ROWS).type("List<T>").desc("响应数据列表").typeClass("java.util.list").build(),
                    FieldModel.builder().name(Constants.RESULT_KEY_TOTAL).type("Integer").desc("结果总数").typeClass("java.lang.Integer").required(true).build()

            )).build();
}
