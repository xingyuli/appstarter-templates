package com.mobisist.springbootseed.appstarter.db.ext;

import com.mobisist.springbootseed.appstarter.common.JSONKt;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KClass;
import org.jooq.Converter;
import org.jooq.tools.StringUtils;
import org.jooq.tools.json.JSONObject;

import java.util.HashMap;

class MysqlJSONJooqJSONObjectConverter implements Converter<Object, JSONObject> {

    @Override
    public JSONObject from(Object databaseObject) {
        if (databaseObject == null) {
            return null;
        }

        String stringRepresentation = databaseObject.toString();
        if (StringUtils.isBlank(stringRepresentation)) {
            return null;
        }

        @SuppressWarnings("unchecked")
        KClass<HashMap> toType = (KClass<HashMap>) Reflection.createKotlinClass(HashMap.class);
        return new JSONObject(JSONKt.jsonFrom(toType, stringRepresentation));
    }

    @Override
    public Object to(JSONObject userObject) {
        if (userObject == null) {
            return null;
        }
        return JSONKt.jsonStringify(userObject, false);
    }

    @Override
    public Class<Object> fromType() {
        return Object.class;
    }

    @Override
    public Class<JSONObject> toType() {
        return JSONObject.class;
    }

}
