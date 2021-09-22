package com.luo.demo.swagger.codegen.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 常量定义
 *
 * @author luohq
 * @date 2021-09-17
 */
public class Constants {

    private Constants() {}

    /** 无效数据配置 */
    public static final List<String> INVALID_PARAM_NAMES = Arrays.asList("root", "empty object");
    public static final List<String> INVALID_OBJ_DESCS = Arrays.asList("响应数据");
    /** 符号配置 */
    public static final String DEFAULT_API_NAME = "Api";
    public static final String PATH_SEPARATOR = "/";
    public static final String DESC_SEPARATOR = " - ";
    public static final String HYPHEN_SEPARATOR = "-";
    public static final String DOT = ".";
    public static final String COLON = ":";
    /** CommonResult默认配置 */
    public static final String RESULT_KEY_RESP_CODE = "respCode";
    public static final String RESULT_KEY_DATA = "data";
    public static final String RESULT_KEY_MSG = "msg";
    public static final String RESULT_KEY_ROWS = "rows";
    public static final String RESULT_KEY_TOTAL = "total";
    public static final String HTTP_STATUS_200 = "200";
    public static final String COMMON_RESULT_NAME = "CommonResult";
    public static final String COMMON_RESULT_PACKAGE = "com.luo.demo.result";
    /** 生成包名默认配置 */
    public static final String COMMON_RESULT_CLASS = "com.luo.demo.result.CommonResult";
    public static final String API_SUB_PACKAGE = ".api";
    public static final String MODEL_SUB_PACKAGE = ".model";
    public static final String CONTROLLER_SUB_PACKAGE = ".controller";
    /** maven模块后缀 */
    public static final String MVN_API_MODULE = "api";
    public static final String MVN_WEB_MODULE = "web";
    public static final String UNIFY_FILE_SEPARATOR = "/";
    public static final String SRC_DIR = "/src/main/java";
    /** 生成文件后缀 */
    public static final String JAVA_FILE_SUFFX = ".java";
    public static final String XML_FILE_SUFFX = ".xml";
    /** mustache模板名称 */
    public static final String API_MUSTACHE_NAME = "api.mustache";
    public static final String CONTROLLER_MUSTACHE_NAME = "controller.mustache";
    public static final String OBJ_MUSTACHE_NAME = "obj.mustache";




}
