package com.luo.demo.swagger.codegen.convertor;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.luo.demo.swagger.codegen.model.CodegenModel;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Mustache模版引擎
 *
 * @author luohq
 * @date 2021-09-19 15:33
 */
public class MustacheEngine {
//    public static void main(String[] args) throws IOException {
//        MustacheFactory mf = new DefaultMustacheFactory();
//        //main.mustache和familiesDesc.mustache需放在同一目录，如均在resources/mustache目录下
//        Mustache mustache = mf.compile("mustache/main.mustache");
//        mustache.execute(new PrintWriter(System.out), buildIntro()).flush();
//    }

    public void renderTemplate(String templatePath, CodegenModel codegenModel) {
        //创建目录（api.module, web.module, api.package, controller.package, model.package）
        //遍历CodegenModel.apis -> 生成api接口定义（方法、参数、返回值）
        //遍历CodegenModel.apis -> 生成controller接口实现（方法、参数、返回值，空实现return null）
        //遍历CodegenModel.objs -> 生成对象定义
    }
}
