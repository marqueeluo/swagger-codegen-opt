package com.luo.demo.swagger.codegen.convertor;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.luo.demo.swagger.codegen.constant.Constants;
import com.luo.demo.swagger.codegen.model.CodegenModel;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.stream.Stream;

import static com.luo.demo.swagger.codegen.utils.CommonUtils.buildStr;
import static com.luo.demo.swagger.codegen.utils.CommonUtils.convertDrectoryPath;

/**
 * Mustache模版引擎
 *
 * @author luohq
 * @date 2021-09-19 15:33
 */
public class MustacheEngine {

    /**
     * 渲染模板
     *
     * @param codegenModel
     */
    public void renderTemplate(CodegenModel codegenModel) {
        //创建目录
        this.mkdirs(codegenModel);
        //遍历CodegenModel.objs -> 生成对象定义
        this.renderObjs(codegenModel);
        //遍历CodegenModel.apis -> 生成api接口定义（方法、参数、返回值）
        this.renderApis(codegenModel);
        //遍历CodegenModel.apis -> 生成controller接口实现（方法、参数、返回值，空实现return null）
        this.renderControllers(codegenModel);
    }

    /**
     * 创建目录（api.module, web.module, api.package, controller.package, model.package）
     *
     * @param codegenModel
     */
    private void mkdirs(CodegenModel codegenModel) {
        //创建目录（api.module, web.module, api.package, controller.package, model.package）
        Config config = codegenModel.getConfig();
        Stream.of(
                convertDrectoryPath(config.getOutputDirectory(), config.getApiMvnModule(), config.getSrcDir(), config.getApiPackage()),
                convertDrectoryPath(config.getOutputDirectory(), config.getApiMvnModule(), config.getSrcDir(), config.getModelPackage()),
                convertDrectoryPath(config.getOutputDirectory(), config.getControllerMvnModule(), config.getSrcDir(), config.getControllerPackage())
        ).forEach(dir -> {
            try {
                FileUtils.forceMkdir(new File(dir));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 生成obj文件
     *
     * @param codegenModel
     */
    private void renderObjs(CodegenModel codegenModel) {
        //遍历CodegenModel.objs -> 生成对象定义
        String objTemplatePath = buildStr(
                convertDrectoryPath(codegenModel.getConfig().getTemplateDirectory()),
                Constants.UNIFY_FILE_SEPARATOR,
                Constants.OBJ_MUSTACHE_NAME
        );
        codegenModel.getObjs().stream().forEach(objModel -> {
            Config config = objModel.getConfig();
            String objOutputDir = buildStr(
                    convertDrectoryPath(
                            config.getOutputDirectory(),
                            config.getApiMvnModule(),
                            config.getSrcDir(),
                            config.getModelPackage()
                    ),
                    buildStr(Constants.UNIFY_FILE_SEPARATOR, objModel.getName(), Constants.JAVA_FILE_SUFFX)
            );
            this.renderMustacheToFile(objTemplatePath, objOutputDir, objModel);
        });
    }

    /**
     * 生成api文件
     *
     * @param codegenModel
     */
    private void renderApis(CodegenModel codegenModel) {
        //遍历CodegenModel.apis -> 生成api定义
        String apiTemplatePath = buildStr(
                convertDrectoryPath(codegenModel.getConfig().getTemplateDirectory()),
                Constants.UNIFY_FILE_SEPARATOR,
                Constants.API_MUSTACHE_NAME
        );
        codegenModel.getApis().stream().forEach(apiModel -> {
            Config config = apiModel.getConfig();
            String apiOutputDir = buildStr(
                    convertDrectoryPath(
                            config.getOutputDirectory(),
                            config.getApiMvnModule(),
                            config.getSrcDir(),
                            config.getApiPackage()
                    ),
                    buildStr(Constants.UNIFY_FILE_SEPARATOR, apiModel.getName().concat("Api"), Constants.JAVA_FILE_SUFFX)
            );
            this.renderMustacheToFile(apiTemplatePath, apiOutputDir, apiModel);
        });
    }

    /**
     * 生成controller文件
     *
     * @param codegenModel
     */
    private void renderControllers(CodegenModel codegenModel) {
        //遍历CodegenModel.apis -> 生成controller定义
        String controllerTemplatePath = buildStr(
                convertDrectoryPath(codegenModel.getConfig().getTemplateDirectory()),
                Constants.UNIFY_FILE_SEPARATOR,
                Constants.CONTROLLER_MUSTACHE_NAME
        );
        codegenModel.getApis().stream().forEach(apiModel -> {
            Config config = apiModel.getConfig();
            String controllerOutputDir = buildStr(
                    convertDrectoryPath(
                            config.getOutputDirectory(),
                            config.getControllerMvnModule(),
                            config.getSrcDir(),
                            config.getControllerPackage()
                    ),
                    buildStr(Constants.UNIFY_FILE_SEPARATOR, apiModel.getName().concat("Controller"), Constants.JAVA_FILE_SUFFX)
            );
            this.renderMustacheToFile(controllerTemplatePath, controllerOutputDir, apiModel);
        });
    }

    /**
     * 根据mustache模板渲染文件
     *
     * @param templatePath
     * @param outputFilePath
     * @param templateData
     */
    @SneakyThrows
    private void renderMustacheToFile(String templatePath, String outputFilePath, Object templateData) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(templatePath);
        Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath), "UTF-8"));
        mustache.execute(fileWriter, templateData).flush();
    }


}
