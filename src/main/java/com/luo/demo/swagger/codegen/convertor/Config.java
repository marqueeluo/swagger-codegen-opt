package com.luo.demo.swagger.codegen.convertor;

import com.luo.demo.swagger.codegen.constant.Constants;
import com.luo.demo.swagger.codegen.utils.CommonUtils;
import lombok.Builder;
import lombok.Data;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private String author;
    private String date;
    private String templateDirectory;
    private String swaggerApiFile;
    private String outputDirectory;
    private String basePackage;
    private String apiPackage;
    private String modelPackage;
    private String controllerPackage;
    private String baseMvnModule;
    private String apiMvnModule;
    private String controllerMvnModule;
    private String srcDir;


    public String getAuthor() {
        return Optional.ofNullable(author).orElse("codegen");
    }

    public String getDate() {
        return Optional.ofNullable(date).orElse(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
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
}
