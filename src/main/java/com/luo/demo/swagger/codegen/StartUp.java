package com.luo.demo.swagger.codegen;

import com.luo.demo.swagger.codegen.convertor.Config;
import com.luo.demo.swagger.codegen.convertor.Swagger2ModelConvertor;
import com.luo.demo.swagger.codegen.model.CodegenModel;

/**
 * 启动类
 *
 * @author luohq
 * @date 2021-09-16
 */
public class StartUp {

    public static void main(String[] args) {
        String inputSpec = "swaggerApi-new.json";
        Config config = Config.builder()
                .basePackage("com.luo.demo")
//                .apiPackage("com.luo.demo.api")
//                .modelPackage("com.luo.demo.model")
//                .controllerPackage("com.luo.demo.controller")
                .build();
        CodegenModel codegenModel = new Swagger2ModelConvertor(inputSpec, config).convert();
        System.out.println(codegenModel);
    }
}
