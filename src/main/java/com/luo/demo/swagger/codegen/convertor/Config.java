package com.luo.demo.swagger.codegen.convertor;

import com.luo.demo.swagger.codegen.constant.Constants;
import com.luo.demo.swagger.codegen.model.ObjModel;
import com.luo.demo.swagger.codegen.utils.CommonUtils;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 基础配置
 *
 * @author luohq
 * @date 2021-09-17
 */
@Data
@Builder
public class Config {
    /**
     * 代码生成作者（默认codegn）
     */
    private String author;
    /**
     * 代码生成日期（默认当前日期）
     */
    private String date;
    /**
     * musatche模板所在目录（需至少包含api.mustache, controller.mustache, obj.mustache）
     */
    private String templateDirectory;
    /**
     * swagger api导出文件（可支持yaml和json格式）
     */
    private String swaggerApiFile;
    /**
     * 源码生成后输出目录
     */
    private String outputDirectory;
    /**
     * 源码基础包名
     */
    private String basePackage;
    /**
     * 源码api模块包名
     */
    private String apiPackage;
    /**
     * 源码model模块包名
     */
    private String modelPackage;
    /**
     * 源码controller模块包名
     */
    private String controllerPackage;
    /**
     * 源码maven基础module root名称
     */
    private String baseMvnModule;
    /**
     * 源码maven api module目录名
     */
    private String apiMvnModule;
    /**
     * 源码maven web module目录名
     */
    private String controllerMvnModule;
    /**
     * 源码src目录（默认src/main/java）
     */
    private String srcDir;
    /**
     * api源码文件import列表
     */
    private List<String> apiImports;
    /**
     * controller源码文件import
     */
    private List<String> controllerImports;
    /**
     * obj源码文件import
     */
    private List<String> objImports;
    /**
     * 是否需要生成CommonResult对象
     * true：生成（默认），则在modelPackage下生成CommonResult
     * false：不生成，则在api、controller导入通用CommonResult依赖
     */
    private Boolean generateCommonResult;
    /**
     * 通用commonResult包名
     */
    private String commonResultPackage;
    /**
     * 通用commonResult类名
     */
    private String commonResultName;


    public String getAuthor() {
        return Optional.ofNullable(author)
                .orElse(Constants.AUTHOR);
    }

    public String getDate() {
        return Optional.ofNullable(date)
                .orElse(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
    }

    public String getApiPackage() {
        return Optional.ofNullable(apiPackage)
                .orElse(CommonUtils.buildStr(basePackage, Constants.API_SUB_PACKAGE));
    }

    public String getModelPackage() {
        return Optional.ofNullable(modelPackage)
                .orElse(CommonUtils.buildStr(basePackage, Constants.MODEL_SUB_PACKAGE));
    }

    public String getControllerPackage() {
        return Optional.ofNullable(controllerPackage)
                .orElse(CommonUtils.buildStr(basePackage, Constants.CONTROLLER_SUB_PACKAGE));
    }

    public String getApiMvnModule() {
        return Optional.ofNullable(apiMvnModule)
                .orElse(Optional.ofNullable(baseMvnModule)
                        .map(baseMvnModule -> CommonUtils.buildStr(baseMvnModule, Constants.HYPHEN_SEPARATOR, Constants.MVN_API_MODULE))
                        .orElse(Constants.MVN_API_MODULE)
                );
    }

    public String getControllerMvnModule() {
        return Optional.ofNullable(controllerMvnModule)
                .orElse(Optional.ofNullable(baseMvnModule)
                        .map(baseMvnModule -> CommonUtils.buildStr(baseMvnModule, Constants.HYPHEN_SEPARATOR, Constants.MVN_WEB_MODULE))
                        .orElse(Constants.MVN_WEB_MODULE)
                );
    }

    public String getSrcDir() {
        return Optional.ofNullable(srcDir)
                .orElse(Constants.SRC_DIR);
    }

    public List<String> getApiImports() {
        return Optional.ofNullable(apiImports)
                .orElse(Constants.API_IMPORTS);
    }

    public List<String> getControllerImports() {
        return Optional.ofNullable(controllerImports)
                .orElse(Constants.CONTROLLER_IMPORTS);
    }

    public List<String> getObjImports() {
        return Optional.ofNullable(objImports)
                .orElse(Constants.OBJ_IMPORTS);
    }

    public Boolean getGenerateCommonResult() {
        return Optional.ofNullable(generateCommonResult)
                .orElse(true);
    }

    public String getCommonResultPackage() {
        if (this.getGenerateCommonResult()) {
            return this.getModelPackage();
        }
        return Optional.ofNullable(commonResultPackage)
                .orElse(Constants.COMMON_RESULT_PACKAGE);
    }

    public String getCommonResultName() {
        return Optional.ofNullable(commonResultName)
                .orElse(Constants.COMMON_RESULT_NAME);
    }

    /**
     * 构建完配置后，需执行此操作，
     * 根据当前配置进行适当调整
     */
    public Config refresh() {
        //重置CommonResult类名
        ObjModel.COMMON_RESULT_OBJ_MODEL.setName(this.getCommonResultName());
        //重置CommonResult包名
        ObjModel.COMMON_RESULT_OBJ_MODEL.setBasePackage(this.getCommonResultPackage());

        /** 若不生成commonResult，则需设置已经存在的通用commonResult类名、包名 */
        if (!this.generateCommonResult) {
            //到需导入共通commonResult
            String commonResultClass = CommonUtils.buildStr(this.getCommonResultPackage(), Constants.DOT, this.getCommonResultName());
            Constants.API_IMPORTS.add(commonResultClass);
            Constants.CONTROLLER_IMPORTS.add(commonResultClass);
        }
        return this;
    }


}
