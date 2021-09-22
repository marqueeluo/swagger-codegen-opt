package com.luo.demo.swagger.codegen;

import com.luo.demo.swagger.codegen.convertor.Config;
import com.luo.demo.swagger.codegen.convertor.MustacheEngine;
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
        Config config = Config.builder()
                .swaggerApiFile("swaggerApi-new.json")
                .basePackage("com.luo.feign")
                .baseMvnModule("luo-openfeign-demo")
                //.apiPackage("com.luo.demo.api")
                //.modelPackage("com.luo.demo.model")
                //.controllerPackage("com.luo.demo.controller")
                .author("luohq")
                .templateDirectory("openfeign")
                .outputDirectory("D:/codegen/swagger")
                .generateCommonResult(false)
                .build()
                .refresh();
        CodegenModel codegenModel = new Swagger2ModelConvertor(config).convert();
        System.out.println(codegenModel);
        new MustacheEngine().renderTemplate(codegenModel);
    }
}
