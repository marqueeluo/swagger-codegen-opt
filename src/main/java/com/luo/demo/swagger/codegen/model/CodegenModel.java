package com.luo.demo.swagger.codegen.model;

import com.luo.demo.swagger.codegen.enums.FieldTypeEnum;
import com.luo.demo.swagger.codegen.utils.CommonUtils;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 代码生成 - 数据
 *
 * @author luohq
 * @date 2021-09-17
 */
@Data
@Builder
public class CodegenModel extends HashMap {
    private String basePackage;
    private List<ApiModel> apis;
    private Set<ObjModel> objs;

    public synchronized ObjModel registerObjModel(ObjModel objModel) {
        //简单类型，则跳过组册直接返回
        if( FieldTypeEnum.fieldTypeOf(objModel.getName()).isPrimitiveType()) {
            return objModel;
        }
        //为空则直接注册
        if (CommonUtils.isEmptyCollection(this.objs)) {
            this.objs = new HashSet<>();
            this.objs.add(objModel);
            return objModel;
        }
        //若不包含，则注册并返回当前注册对象
        if (!this.objs.contains(objModel)) {
            this.objs.add(objModel);
            return objModel;
        }
        //若包含，则已之前注册过的对象为准，不再重复组册对象（对象复用）
        return this.objs.stream()
                .filter(objModelInner -> objModelInner.equals(objModel))
                .findFirst()
                .orElse(null);

    }
}
