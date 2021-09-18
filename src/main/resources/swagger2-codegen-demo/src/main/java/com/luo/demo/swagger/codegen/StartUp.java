package com.luo.demo.swagger.codegen;

import com.luo.demo.swagger.codegen.convertor.Config;
import com.luo.demo.swagger.codegen.convertor.Swagger2ModelConvertor;
import com.luo.demo.swagger.codegen.model.CodegenModel;
import com.luo.demo.swagger.codegen.utils.JsonUtils;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import io.swagger.parser.util.ParseOptions;

import java.util.LinkedHashMap;

/**
 * 启动类
 *
 * @author luohq
 * @date 2021-09-16
 */
public class StartUp {

    public static void main(String[] args) {
//        String inputSpec = "D:/idea_workspace/swagger/swaggerApi.yaml";
        String inputSpec = "D:/idea_workspace/swagger/swaggerApi-new.json";
//        ParseOptions parseOptions = new ParseOptions();
//        parseOptions.setResolve(true);
//        parseOptions.setFlatten(false);
//        Swagger swagger = new SwaggerParser().read(inputSpec, null, parseOptions);
//        System.out.println(JsonUtils.toJson(swagger));

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
