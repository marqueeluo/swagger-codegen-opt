package com.luo.demo.swagger.codegen.convertor;

import com.luo.demo.swagger.codegen.constant.Constants;
import com.luo.demo.swagger.codegen.utils.CommonUtils;
import lombok.Builder;
import lombok.Data;

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
    private String basePackage = "com.luo.demo";
    private String apiPackage;
    private String modelPackage;
    private String controllerPackage;
    private String baseMvnModule;
    private String apiMvnModule;
    private String controllerMvnModule;

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

}
