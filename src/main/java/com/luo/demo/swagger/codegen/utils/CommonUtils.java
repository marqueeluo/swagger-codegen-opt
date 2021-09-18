package com.luo.demo.swagger.codegen.utils;

import com.luo.demo.swagger.codegen.constant.Constants;
import com.luo.demo.swagger.codegen.enums.ObjSuffixEnum;
import com.luo.demo.swagger.codegen.model.PathModel;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 通用工具类
 *
 * @author luohq
 * @date 2021-09-17
 */
public class CommonUtils {

    private CommonUtils() {
    }

    /**
     * 字符串两边加双引号
     *
     * @param str
     * @return
     */
    public static String strQuotes(String str) {
        return String.format("\"%s\"", str);
    }

    /**
     * 拼接字符串
     *
     * @param objs
     * @return
     */
    public static String buildStr(Object... objs) {
        return buildStrWithDelimiter("", objs);
    }

    /**
     * 拼接字符串（用指定分隔符拼接）
     *
     * @param delimiter
     * @param objs
     * @return
     */
    public static String buildStrWithDelimiter(String delimiter, Object... objs) {
        return Stream.of(objs)
                .filter(obj -> null != obj && "null" != obj)
                .map(String::valueOf)
                .collect(Collectors.joining(delimiter));
    }

//    public static void main(String[] args) {
//        System.out.println(buildStrWithDelimiter("-", "user", null));
//    }

    /**
     * 是否为空白字符串
     *
     * @param str
     * @return
     */
    public static Boolean isBlankStr(String str) {
        return null == str || "".equals(str.trim()) ;
    }

    /**
     * 是否为空集合
     *
     * @param collection
     * @return
     */
    public static Boolean isEmptyCollection(Collection collection) {
        return null == collection || collection.isEmpty();
    }

    /**
     * 集合size是否相等
     *
     * @param collection1
     * @param collection2
     * @return
     */
    public static Boolean isSameSizeCollection(Collection collection1, Collection collection2) {
        if (isEmptyCollection(collection1) && isEmptyCollection(collection2)) {
            return true;
        }
        if (isEmptyCollection(collection1) || isEmptyCollection(collection2)) {
            return false;
        }
        return collection1.size() == collection2.size();
    }

    /**
     * 是否为空map
     *
     * @param map
     * @return
     */
    public static Boolean isEmptyMap(Map map) {
        return null == map || map.isEmpty();
    }

    /**
     * 取第一个非null对象
     *
     * @param objs
     * @return
     */
    public static Object firstNotNull(Object... objs) {
        return Stream.of(objs)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    /**
     * 取第一个非空白字符串
     *
     * @param strs
     * @return
     */
    public static String firstNotBlank(String... strs) {
        return Stream.of(strs)
                .filter(str -> !isBlankStr(str))
                .findFirst()
                .orElse(null);
    }


    /**
     * 重置path.name（转换path为方法名）
     *
     * @param pathModel
     * @return
     */
    public static String resetPathName(PathModel pathModel) {
//        //非空 且 不是无效名称（root, empty object）
//        if (!isBlankStr(pathModel.getName()) && Constants.INVALID_PARAM_NAMES.contains(pathModel.getName())) {
//            return pathModel.getName();
//        }
        String path2CamelStr = firstCharLower(path2Camel(pathModel.getPath()));
        pathModel.setName(path2CamelStr);
        //TODO 考虑是否加上http method作为后缀
        return path2CamelStr;
    }


    /**
     * 根据path生成参数名
     *
     * @param pathModel
     * @param paramName
     * @return
     */
    public static String convertParamName(PathModel pathModel, String paramName) {
        return convertObjName(pathModel, paramName, ObjSuffixEnum.PARAM);
    }

    /**
     * 根据path生成结果名
     *
     * @param pathModel
     * @param paramName
     * @return
     */
    public static String convertResultName(PathModel pathModel, String paramName) {
        return convertObjName(pathModel, paramName, ObjSuffixEnum.RESULT);
    }

    /**
     * Map(objName, objOccurCount)
     */
    private static final Map<String, Integer> NAME_COUNT_MAP = new HashMap<>();

    /**
     * 根据path生成对象名称（需保证对象名唯一，多次出现依次累加后缀如：obj, obj1, obj2,...）
     *
     * @param pathModel
     * @param objName
     * @param objSuffixEnum
     * @return
     */
    public static String convertObjName(PathModel pathModel, String objName, ObjSuffixEnum objSuffixEnum) {
        String orginName = null;
        //非空 且 不是无效名称（root, empty object）
        if (!isBlankStr(objName) && !Constants.INVALID_PARAM_NAMES.contains(objName)) {
            orginName = firstCharUpper(objName);
        } else {
            orginName = buildStr(
                    firstCharUpper(path2Camel(pathModel.getBasePath())),
                    firstCharUpper(path2Camel(pathModel.getPath())),
                    firstCharUpper(pathModel.getHttpMethod().toLowerCase()),
                    objSuffixEnum.getNameSuffix()
            );
        }
        Integer nameCount = NAME_COUNT_MAP.get(orginName);
        String uniqueName = null;
        if (null == nameCount) {
            nameCount = 1;
            uniqueName = orginName;
        } else {
            uniqueName = orginName + (nameCount++);
        }
        NAME_COUNT_MAP.put(orginName, nameCount);
        return uniqueName;
    }


    /**
     * 根据path生成参数对象描述
     *
     * @param pathModel
     * @param objDesc
     * @return
     */
    public static String convertParamObjDesc(PathModel pathModel, String objDesc) {
        return convertObjDesc(pathModel, objDesc, ObjSuffixEnum.PARAM);
    }

    /**
     * 根据path生成结果对象描述
     *
     * @param pathModel
     * @param objDesc
     * @return
     */
    public static String convertResultObjDesc(PathModel pathModel, String objDesc) {
        return convertObjDesc(pathModel, objDesc, ObjSuffixEnum.RESULT);
    }


    /**
     * 根据path生成对象描述
     *
     * @param pathModel
     * @param objDesc
     * @param objSuffixEnum
     * @return
     */
    public static String convertObjDesc(PathModel pathModel, String objDesc, ObjSuffixEnum objSuffixEnum) {
        if (!isBlankStr(objDesc) && !Constants.INVALID_OBJ_DESCS.contains(objDesc)) {
            return buildStr(objDesc, objSuffixEnum.getDescSuffix());
        }
        return buildStr(path2Camel(pathModel.getPath()), objSuffixEnum.getDescSuffix());
    }

    /**
     * path转驼峰字符串（且移除path参数，如{...}形式）
     *
     * @param path
     * @return
     */
    public static String path2Camel(String path) {
        path = path.replaceAll("/\\{.*?\\}", "");
        String[] pathItems = path.split(Constants.PATH_SEPARATOR);
        if (null == pathItems || 0 >= pathItems.length) {
            return null;
        }
        StringBuilder sb = new StringBuilder(pathItems[0]);
        for (int i = 1; i < pathItems.length; i++) {
            sb.append(firstCharUpper(pathItems[i]));
        }
        return sb.toString();
    }

    /**
     * 拆分path
     *
     * @param path
     * @return
     */
    public static String[] splitPathItems(String path) {
        return Stream.of(path.split(Constants.PATH_SEPARATOR))
                .filter(pathItem -> !isBlankStr(pathItem))
                .toArray(String[]::new);
    }

//    public static void main(String[] args) {
////        System.out.println(Arrays.asList(splitPathItems("/user/detail")));
////        System.out.println(isBlankStr(""));
//
//        String[] items = "/user/detail".split("/");
//        for (String item : items) {
//            System.out.println("[" + item + "]:" + isBlankStr(item));
//        }
//    }

    /**
     * 字符串首字母大写
     *
     * @param str
     * @return
     */
    public static String firstCharUpper(String str) {
        if (isBlankStr(str)) {
            return str;
        }
        return buildStr(Character.toUpperCase(str.charAt(0)), str.substring(1));
    }

    /**
     * 字符串首字母小写
     *
     * @param str
     * @return
     */
    public static String firstCharLower(String str) {
        if (isBlankStr(str)) {
            return str;
        }
        return buildStr(Character.toLowerCase(str.charAt(0)), str.substring(1));
    }

    /**
     * 构建模板参数类型
     *
     * @param type
     * @param templateParams
     * @return
     */
    public static String buildTypeWithTemplateParam(String type, String... templateParams) {
        if (null == templateParams || 0 >= templateParams.length) {
            return type;
        }
        String templateParamsStr = Stream.of(templateParams).collect(Collectors.joining(","));
        return buildStr(type, "<", templateParamsStr, ">");
    }
}
