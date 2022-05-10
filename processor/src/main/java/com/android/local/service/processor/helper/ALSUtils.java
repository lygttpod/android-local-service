package com.android.local.service.processor.helper;

import java.util.Locale;

import javax.lang.model.type.TypeMirror;

public class ALSUtils {

    public static String getParamStatementByParamType(String paramKey, TypeMirror paramType) {
        String type = paramType.toString().toLowerCase(Locale.ROOT);
        if (type.contains("int")) {
            return "Integer.valueOf(" + paramKey + ")";
        } else if (type.contains("boolean")) {
            return "Boolean.valueOf(" + paramKey + ")";
        } else if (type.contains("string")) {
            return "String.valueOf(" + paramKey + ")";
        } else if (type.contains("long")) {
            return "Long.valueOf(" + paramKey + ")";
        } else if (type.contains("float")) {
            return "Float.valueOf(" + paramKey + ")";
        } else if (type.contains("double")) {
            return "Double.valueOf(" + paramKey + ")";
        } else {
            return paramKey;
        }
    }

    public static String getDefaultValueByParamTypeWhenNull(String paramValue, TypeMirror paramType) {
        String type = paramType.toString().toLowerCase(Locale.ROOT);
        if (type.contains("int")) {
            return "0";
        } else if (type.contains("boolean")) {
            return "false";
        } else if (type.contains("string")) {
            return "";
        } else if (type.contains("long")) {
            return "0";
        } else if (type.contains("float")) {
            return "0";
        } else if (type.contains("double")) {
            return "0";
        } else {
            return "";
        }
    }
}
