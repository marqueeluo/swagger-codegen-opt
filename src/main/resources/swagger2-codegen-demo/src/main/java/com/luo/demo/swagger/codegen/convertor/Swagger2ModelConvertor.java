package com.luo.demo.swagger.codegen.convertor;

import com.luo.demo.swagger.codegen.constant.Constants;
import com.luo.demo.swagger.codegen.enums.FieldTypeEnum;
import com.luo.demo.swagger.codegen.enums.ObjSuffixEnum;
import com.luo.demo.swagger.codegen.model.*;
import io.swagger.models.*;
import io.swagger.models.parameters.*;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.parser.SwaggerParser;
import io.swagger.parser.util.ParseOptions;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.luo.demo.swagger.codegen.utils.CommonUtils.*;

/**
 * 转换swagger配置为模板数据
 *
 * @author luohq
 * @date 2021-09-17
 */
public class Swagger2ModelConvertor {

    /**
     * 全局配置
     */
    private Config config;
    /**
     * swagger解析结果
     */
    private Swagger swagger;


    public Swagger2ModelConvertor(String swaggerFileLocation, Config config) {
        this.config = config;
        this.swagger = loadSwagger(swaggerFileLocation);
    }

    /**
     * 从swagger.json|yaml加载swagger对象（通过swagger api）
     *
     * @param inputSpec
     * @return
     */
    private Swagger loadSwagger(String inputSpec) {
        ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolve(true);
        //此选项设置为false，避免swagger帮忙解析，后续自己解析
        parseOptions.setFlatten(false);
        Swagger swagger = new SwaggerParser().read(inputSpec, null, parseOptions);
        return swagger;
    }

    /**
     * 根据swagger文件解析模板数据
     *
     * @return
     */
    public CodegenModel convert() {
        /** 构建最外层的codegenModel */
        CodegenModel codegenModel = this.buildCodegenModel();
        /** 构建api model */
        this.parseApiModel(codegenModel);
        return codegenModel;

    }

    /**
     * 构建CodegenModel对象
     *
     * @return
     */
    private CodegenModel buildCodegenModel() {
        return CodegenModel.builder()
                .basePackage(this.config.getBasePackage())
                .objs(new HashSet<>())
                .build();
    }

    /**
     * 解析tag对应ApiModel
     *
     * @param codegenModel
     */
    private void parseApiModel(CodegenModel codegenModel) {
        if (isEmptyMap(this.swagger.getPaths())) {
            return;
        }

        /** 解析tag分类，用于区分API */
        List<Tag> tagList = this.swagger.getTags();
        List<ApiModel> apiModelList = tagList.stream().map(tag -> {
            return ApiModel.builder()
                    //TODO name待重构为应为名
                    .name(tag.getName())
                    .desc(buildStrWithDelimiter(Constants.DESC_SEPARATOR, tag.getName(), tag.getDescription()))
                    .basePackage(this.config.getApiPackage())
                    .paths(new ArrayList<>())
                    //TODO basePath待重构
                    .basePath("/")
                    .build();
        }).collect(Collectors.toList());
        Map<String, ApiModel> apiModelMap = apiModelList.stream()
                .collect(Collectors.toMap(ApiModel::getName, Function.identity()));

        /** 解析所有path */
        Map<String, Path> pathMap = this.swagger.getPaths();

        pathMap.entrySet().stream()
                .forEach(pathEntry -> {
                    //构建path模型
                    this.parsePathModel(pathEntry.getKey(), pathEntry.getValue(), apiModelMap, codegenModel);
                });

        /** 注册api */
        //重置api basePath, name
        AtomicInteger autoIndex = new AtomicInteger(0);
        apiModelList.stream().forEach(apiModel -> this.resetApiModel(apiModel, autoIndex.addAndGet(1)));
        codegenModel.setApis(apiModelList);
    }

    /**
     * 1. 重置ApiModel的baseBath
     * 2. 重置所包含的PathModel.path|basePath
     * 3. 根据basePath重置ApiModel.name，若不存在BasePath则为Api{index}形式
     *
     * @param apiModel
     * @param index
     */
    private void resetApiModel(ApiModel apiModel, Integer index) {
        List<String> allApiPathList = apiModel.getPaths().stream().map(PathModel::getPath).collect(Collectors.toList());
        /** 提取共通的basePath前缀 */
        //检查首个path
        String[] firstPathItems = splitPathItems(allApiPathList.get(0));
        //为空（"/"）则直接返回
        if (null == firstPathItems) {
            return;
        }

        //依次对比其他path，提取共通的path前缀
        int basePathItemEndIndex = Integer.MAX_VALUE;
        for (int i = 1; i < allApiPathList.size(); i++) {
            String[] nextPathItems = splitPathItems(allApiPathList.get(i));
            //对比相同前缀的endIndex
            int samePathItemEndIndex = -1;
            for (int j = 0; j < firstPathItems.length && j < nextPathItems.length; j++) {
                //依次对比，若不相同，则跳出比较
                if (!firstPathItems[j].equals(nextPathItems[j])) {
                    break;
                }
                //记录相同前缀的index
                samePathItemEndIndex = j;
            }
            //没有相同的path前缀，则直接结束其他path的比较，则无共同basePath
            if (0 > samePathItemEndIndex) {
                basePathItemEndIndex = -1;
                break;
            }
            //若有共通path前缀，则记录最短匹配的前缀endIndex
            basePathItemEndIndex = Math.min(samePathItemEndIndex, basePathItemEndIndex);
        }

        /** 无共通path前缀，设置apiModel.name为Api{Index} */
        if (0 > basePathItemEndIndex) {
            apiModel.setName(buildStr(Constants.DEFAULT_API_NAME, ++index));
            return;
        }

        /** 存在共同的basePath，则重置PathModel */
        String basePath = buildStr(
                Constants.PATH_SEPARATOR,
                buildTypeWithTemplateParam(Constants.PATH_SEPARATOR, Arrays.copyOf(firstPathItems, basePathItemEndIndex + 1))
        );
        //所有path删除前缀basePath
        apiModel.getPaths().stream().forEach(pathModel -> {
            //设置basePath
            pathModel.setBasePath(basePath);
            //删除basePath前缀
            pathModel.setPath(pathModel.getPath().replace(basePath, ""));
            //若为空，则设置path为"/"
            if (isBlankStr(pathModel.getPath())) {
                pathModel.setPath(Constants.PATH_SEPARATOR);
            }
        });
        /** 根据basePath设置apiModel.name */
        apiModel.setName(firstCharUpper(path2Camel(basePath)));

    }

    /**
     * 解析swagger.path对应ApiModel.PathModel
     *
     * @param pathKey
     * @param curPath
     * @param apiModelMap
     * @param codegenModel
     */
    private void parsePathModel(String pathKey, Path curPath, Map<String, ApiModel> apiModelMap, CodegenModel codegenModel) {
        //定义httpMethod和获取对应operation的映射
        Map<String, Function<Path, Operation>> httpMethod2OpFuncMap = new HashMap<>();
        httpMethod2OpFuncMap.put(HttpMethod.GET.name(), Path::getGet);
        httpMethod2OpFuncMap.put(HttpMethod.POST.name(), Path::getPost);
        httpMethod2OpFuncMap.put(HttpMethod.PUT.name(), Path::getPut);
        httpMethod2OpFuncMap.put(HttpMethod.DELETE.name(), Path::getDelete);
        //options, head, patch暂未考虑

        //遍历所有存在的operation
        httpMethod2OpFuncMap.entrySet().stream()
                .forEach(httpMethod2OpFuncEntry -> {
                    String httpMethod = httpMethod2OpFuncEntry.getKey();
                    Operation curOperation = httpMethod2OpFuncEntry.getValue().apply(curPath);
                    if (null == curOperation) {
                        return;
                    }
                    /** 根据path下的具体operation（对应get|post|put...）构建path请求 */
                    PathModel pathModel = PathModel.builder()
                            .name(curOperation.getOperationId())
                            .path(pathKey)
                            .httpMethod(httpMethod)
                            .desc(firstNotBlank(curOperation.getDescription(), curOperation.getSummary()))
                            .consumes(curOperation.getConsumes())
                            .produces(curOperation.getProduces())
                            .params(new ArrayList<>())
                            .build();
                    //根据pathKey转换
                    resetPathName(pathModel);
                    //根据operation所属的tag归属到指定的apiModel
                    if (!isEmptyCollection(curOperation.getTags())) {
                        ApiModel curApiModel = apiModelMap.get(curOperation.getTags().get(0));
                        curApiModel.getPaths().add(pathModel);
                        //设置path的basePath
                        pathModel.setBasePath(curApiModel.getBasePath());
                    }
                    /** 解析参数 */
                    List<Parameter> opParamList = curOperation.getParameters();
                    if (!isEmptyCollection(opParamList)) {
                        this.parseOnlyOneQueryAndFormParam(opParamList, pathModel);
                        this.parseQueryAndFormParam(opParamList, pathModel, codegenModel);
                        this.parsePathAndHeaderParam(opParamList, pathModel);
                        this.parseBodyParam(opParamList, pathModel, codegenModel);
                    }
                    /** 解析返回值 */
                    this.parseResponse200(curOperation, pathModel, codegenModel);
                });
    }


    /**
     * 仅一个swagger.path.operation.parameters queryOrForm参数，解析为单独参数ParamModel
     *
     * @param parameterList
     * @param pathModel
     */
    private void parseOnlyOneQueryAndFormParam(List<Parameter> parameterList, PathModel pathModel) {
        /** 仅一个参数，且不是BodyParameter */
        Boolean isOnlyParam = (1 == parameterList.size() && (parameterList.get(0) instanceof QueryParameter || parameterList.get(0) instanceof FormParameter));
        if (!isOnlyParam) {
            return;
        }
        Parameter curParam = parameterList.get(0);
        FieldTypeEnum curFieldTypeEnum = FieldTypeEnum.paramTypeOf(((AbstractSerializableParameter) curParam).getType());
        ParamModel paramModel = ParamModel.builder()
                .isQueryParam(curParam instanceof QueryParameter)
                .isFormParam(curParam instanceof FormParameter)
                .name(curParam.getName())
                .desc(curParam.getDescription())
                .required(curParam.getRequired())
                .type(curFieldTypeEnum.getFieldTypeName())
                .typeClass(curFieldTypeEnum.getFieldTypeClass())
                .build();
        //添加参数
        pathModel.getParams().add(paramModel);
    }

    /**
     * 多个swagger.path.operation.parameters query|form参数解析为一个ParamModel和一个ObjModel
     * 注：
     * 原swagger-codegen解析为多个@RequestParam参数，多个@RequestParam兼容原FeignClient，
     * 但是我在优化OpenFeign.contract后可以支持对象的形式，故才如此转换，这也是我决定重写swagger-codegen的初心
     *
     * @param parameterList
     * @param pathModel
     * @param codegenModel
     */
    private void parseQueryAndFormParam(List<Parameter> parameterList, PathModel pathModel, CodegenModel codegenModel) {
        Predicate<Parameter> isQueryOrFormParamPredicate = parameter -> parameter instanceof QueryParameter || parameter instanceof FormParameter;
        Long paramCount = parameterList.stream().filter(isQueryOrFormParamPredicate).count();
        if (1 >= paramCount) {
            return;
        }
        /** 将多个参数聚合成一个参数对象ObjModel */
        ObjModel objModel = ObjModel.builder()
                .name(convertParamName(pathModel, null))
                .desc(convertParamObjDesc(pathModel, null))
                .basePackage(this.config.getModelPackage())
                .build();
        //生成属性
        List<FieldModel> fieldModelList = parameterList.stream()
                .filter(isQueryOrFormParamPredicate)
                .map(curParam -> {
                    FieldTypeEnum curFieldTypeEnum = FieldTypeEnum.paramTypeOf(((AbstractSerializableParameter) curParam).getType());
                    return FieldModel.builder()
                            .name(curParam.getName())
                            .type(curFieldTypeEnum.getFieldTypeName())
                            .typeClass(curFieldTypeEnum.getFieldTypeClass())
                            .desc(curParam.getDescription())
                            .required(curParam.getRequired())
                            .build();
                })
                .collect(Collectors.toList());
        //添加属性到对象
        objModel.setFields(fieldModelList);
        /** 注册对象 */
        objModel = codegenModel.registerObjModel(objModel);

        /** 生成参数 */
        ParamModel paramModel = ParamModel.builder()
                .isFormObjParam(true)
                .name(firstCharLower(objModel.getName()))
                .desc(objModel.getDesc())
                .type(objModel.getName())
                .typeClass(objModel.getTypeClass())
                .build();
        //添加参数到path中
        pathModel.getParams().add(paramModel);
    }


    /**
     * 多个swagger.path.operation.parameters header、path参数为解析为多个单独参数ParamModel
     *
     * @param parameterList
     * @param pathModel
     */
    private void parsePathAndHeaderParam(List<Parameter> parameterList, PathModel pathModel) {
        Predicate<Parameter> isPathOrHeaderParamPredicate = parameter -> parameter instanceof PathParameter || parameter instanceof HeaderParameter;
        parameterList.stream().filter(isPathOrHeaderParamPredicate).forEach(curParam -> {
            FieldTypeEnum curFieldTypeEnum = FieldTypeEnum.paramTypeOf(((AbstractSerializableParameter) curParam).getType());
            ParamModel paramModel = ParamModel.builder()
                    .isHeaderParam(curParam instanceof HeaderParameter)
                    .isPathParam(curParam instanceof PathParameter)
                    .name(curParam.getName())
                    .desc(curParam.getDescription())
                    .required(curParam.getRequired())
                    .type(curFieldTypeEnum.getFieldTypeName())
                    .typeClass(curFieldTypeEnum.getFieldTypeClass())
                    .build();
            //添加参数
            pathModel.getParams().add(paramModel);
        });
    }

    /**
     * 多个swagger.path.operation.parameters body参数为解析为多个单独参数ParamModel
     *
     * @param parameterList
     * @param pathModel
     * @param codegenModel
     */
    private void parseBodyParam(List<Parameter> parameterList, PathModel pathModel, CodegenModel codegenModel) {
        Predicate<Parameter> isBodyParamPredicate = parameter -> parameter instanceof BodyParameter;

        parameterList.stream().filter(isBodyParamPredicate).forEach(curParam -> {
            BodyParameter bodyParam = (BodyParameter) curParam;
            /** 参数对象ObjModel */
            ObjModel objModel = ObjModel.builder()
                    .name(convertParamName(pathModel, bodyParam.getName()))
                    .desc(convertParamObjDesc(pathModel, bodyParam.getDescription()))
                    .basePackage(this.config.getModelPackage())
                    .build();
            //生成属性
            List<FieldModel> fieldModelList = this.parseProperties(bodyParam.getSchema().getProperties(), pathModel, codegenModel, ObjSuffixEnum.PARAM);
            //添加属性到对象
            objModel.setFields(fieldModelList);
            /** 注册对象 */
            objModel = codegenModel.registerObjModel(objModel);

            /** 生成参数 */
            ParamModel paramModel = ParamModel.builder()
                    .isBodyParam(true)
                    .name(firstCharLower(objModel.getName()))
                    .desc(objModel.getDesc())
                    .type(objModel.getName())
                    .typeClass(objModel.getTypeClass())
                    .build();
            //添加参数到path中
            pathModel.getParams().add(paramModel);
        });


    }


    /**
     * 解析status 200对应的response为ApiModel.returnType
     *
     * @param curOperation
     * @param pathModel
     * @param codegenModel
     */
    private void parseResponse200(Operation curOperation, PathModel pathModel, CodegenModel codegenModel) {
        //设置默认path.returnType = CommonResult
        pathModel.setReturnBaseType(Constants.COMMON_RESULT_NAME);
        /** 解析返回值 */
        Optional.ofNullable(curOperation.getResponses())
                .map(statusRespMap -> statusRespMap.get(Constants.HTTP_STATUS_200))
                .map(response -> response.getResponseSchema())
                .map(model -> model.getProperties())
                .ifPresent(respPropMap -> {
                    ObjModel resultObjModel = null;
                    /** CommonResult（包含respCode, msg, data, rows, total）模式 */
                    if (respPropMap.containsKey(Constants.RESULT_KEY_RESP_CODE)) {
                        //提前注册CommonResult
                        codegenModel.registerObjModel(ObjModel.CommonResultObjModel);
                        Property dataProp = respPropMap.get(Constants.RESULT_KEY_DATA);
                        Property rowsProp = respPropMap.get(Constants.RESULT_KEY_ROWS);

                        //解析data ObjectProperty
                        if (null != dataProp && dataProp instanceof ObjectProperty) {
                            resultObjModel = this.parseObjectProperty((ObjectProperty) dataProp, pathModel, codegenModel, ObjSuffixEnum.RESULT);
                            //设置path.returnType = CommonResult<resultObj>
                            pathModel.setReturnBaseType(buildTypeWithTemplateParam(Constants.COMMON_RESULT_NAME, resultObjModel.getName()));
                        }
                        //解析rows ArrayProperty
                        else if (null != rowsProp && rowsProp instanceof ArrayProperty) {
                            resultObjModel = this.parseArrayProperty((ArrayProperty) rowsProp, pathModel, codegenModel, ObjSuffixEnum.RESULT);
                            //设置path.returnType = CommonResult<array.itemType>
                            pathModel.setReturnBaseType(buildTypeWithTemplateParam(Constants.COMMON_RESULT_NAME, resultObjModel.getName()));
                        }
                    }
                    /** 其他返回结果 */
                    else {
                        resultObjModel = ObjModel.builder()
                                .name(convertResultName(pathModel, null))
                                .desc(convertResultObjDesc(pathModel, pathModel.getDesc()))
                                .basePackage(this.config.getModelPackage())
                                .build();

                        List<FieldModel> fieldModelList = this.parseProperties(respPropMap, pathModel, codegenModel, ObjSuffixEnum.RESULT);
                        resultObjModel.setFields(fieldModelList);
                        /** 注册对象 */
                        resultObjModel = codegenModel.registerObjModel(resultObjModel);
                        /** 设置path.returnType */
                        pathModel.setReturnBaseType(resultObjModel.getName());
                    }
                });
    }


    /**
     * 解析ObjectProperty为ObjModel
     * <p>
     * Swagger.Path.Operation.BodyParameter.getSchema().getProperties(),
     * Swagger.Path.Operation.getResponse().get("200").getResponseSchema().getProperties()
     * <p>
     * 其中schema对应ModelImpl
     *
     * @param objectProperty
     * @param pathModel
     * @param codegenModel
     * @param objSuffixEnum
     */
    private ObjModel parseObjectProperty(ObjectProperty objectProperty, PathModel pathModel, CodegenModel codegenModel, ObjSuffixEnum objSuffixEnum) {
        /** 参数对象ObjModel */
        ObjModel objModel = ObjModel.builder()
                .name(convertObjName(pathModel, objectProperty.getTitle(), objSuffixEnum))
                .desc(convertObjDesc(pathModel, objectProperty.getDescription(), objSuffixEnum))
                .basePackage(this.config.getModelPackage())
                .build();

        List<FieldModel> fieldModelList = this.parseProperties(objectProperty.getProperties(), pathModel, codegenModel, objSuffixEnum);
        //添加属性到对象
        objModel.setFields(fieldModelList);
        /** 注册对象 */
        objModel = codegenModel.registerObjModel(objModel);
        return objModel;
    }

    /**
     * 解析ArrayProperty.item为ObjModel
     *
     * @param arrayProperty
     * @param pathModel
     * @param codegenModel
     * @param objSuffixEnum
     * @return
     */
    private ObjModel parseArrayProperty(ArrayProperty arrayProperty, PathModel pathModel, CodegenModel codegenModel, ObjSuffixEnum objSuffixEnum) {
        Property itemProp = arrayProperty.getItems();
        FieldTypeEnum itemPropTypeEnum = FieldTypeEnum.paramTypeOf(itemProp.getType());
        //arrayProperty.item为简单类型，则直接List<T>
        if (itemPropTypeEnum.isPrimitiveType()) {
            return ObjModel.builder()
                    .name(itemPropTypeEnum.getFieldTypeName())
                    .typeClass(itemPropTypeEnum.getFieldTypeClass())
                    .build();
        } else {
            ObjModel itemObjModel = this.parseObjectProperty((ObjectProperty) itemProp, pathModel, codegenModel, objSuffixEnum);
            return itemObjModel;
        }
    }


    /**
     * 解析property列表为FieldModel列表
     * 注：支持object、array嵌套生成逻辑
     *
     * @param propertyMap
     * @param pathModel
     * @param codegenModel
     * @param objSuffixEnum
     * @return
     */
    private List<FieldModel> parseProperties(Map<String, Property> propertyMap, PathModel pathModel, CodegenModel codegenModel, ObjSuffixEnum objSuffixEnum) {
        //生成属性
        List<FieldModel> fieldModelList = propertyMap.entrySet()
                .stream()
                .map(propEntry -> {
                    String propKey = propEntry.getKey();
                    Property propVal = propEntry.getValue();
                    FieldTypeEnum curFieldTypeEnum = FieldTypeEnum.paramTypeOf(propVal.getType());
                    //ObjectProperty
                    if (propVal instanceof ObjectProperty) {
                        ObjModel propObjModel = this.parseObjectProperty((ObjectProperty) propVal, pathModel, codegenModel, objSuffixEnum);
                        return FieldModel.builder()
                                .name(propKey)
                                .type(propObjModel.getName())
                                .typeClass(propObjModel.getTypeClass())
                                .desc(propVal.getDescription())
                                //TODO required
//                                .required(((ModelImpl) model.getSchema()).getRequired().contains(propKey))
                                .build();

                    }
                    //ArrayProperty
                    if (propVal instanceof ArrayProperty) {
                        ArrayProperty arrayProperty = (ArrayProperty) propVal;
                        ObjModel itemObjModel = this.parseArrayProperty(arrayProperty, pathModel, codegenModel, objSuffixEnum);
                        return FieldModel.builder()
                                .name(propKey)
                                .type(buildStr("List<", itemObjModel.getName(), ">"))
                                .typeClass(itemObjModel.getTypeClass())
                                .desc(propVal.getDescription())
                                //TODO required
//                                .required(((ModelImpl) model.getSchema()).getRequired().contains(propKey))
                                .build();
                    }
                    return FieldModel.builder()
                            .name(propKey)
                            .type(curFieldTypeEnum.getFieldTypeName())
                            .typeClass(curFieldTypeEnum.getFieldTypeClass())
                            .desc(propVal.getDescription())
                            //TODO required
//                            .required(((ModelImpl) bodyParam.getSchema()).getRequired().contains(propKey))
                            .build();
                }).collect(Collectors.toList());
        return fieldModelList;
    }


}
