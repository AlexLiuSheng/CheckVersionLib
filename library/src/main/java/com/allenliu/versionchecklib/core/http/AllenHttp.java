package com.allenliu.versionchecklib.core.http;

import com.allenliu.versionchecklib.core.AllenChecker;
import com.allenliu.versionchecklib.core.VersionParams;
import com.allenliu.versionchecklib.utils.ALog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by allenliu on 2017/8/31.
 */

public class AllenHttp {
    private static OkHttpClient client;

    public static OkHttpClient getHttpClient() {
        if (client == null)
            client = new OkHttpClient();
        return client;
    }

    private static <T extends Request.Builder> T assembleHeader(T builder, VersionParams versionParams) {
        com.allenliu.versionchecklib.core.http.HttpHeaders headers = versionParams.getHttpHeaders();
        if (headers != null) {
            ALog.e("header:");
            for (Map.Entry<String, String> stringStringEntry : headers.entrySet()) {
                String key = stringStringEntry.getKey();
                String value = stringStringEntry.getValue();
                ALog.e(key+"="+value+"\n");
                builder.addHeader(key, value);
            }
        }
        return builder;
    }

    private static String assembleUrl(String url, HttpParams params) {

        StringBuffer urlBuilder = new StringBuffer(url);
        if (params != null) {
            urlBuilder.append("?");
            for (Map.Entry<String, Object> stringObjectEntry : params.entrySet()) {
                String key = stringObjectEntry.getKey();
                String value = stringObjectEntry.getValue() + "";
                urlBuilder.append(key).append("=").append(value).append("&");
            }
            url = urlBuilder.substring(0, urlBuilder.length() - 1);
        }
        ALog.e("url:"+url);
        return url;
    }

    public static Request.Builder get(VersionParams versionParams) {
        Request.Builder builder = new Request.Builder();
        builder = assembleHeader(builder, versionParams);
        builder.url(assembleUrl(versionParams.getRequestUrl(), versionParams.getRequestParams()));

        return builder;
    }

    public static Request.Builder post(VersionParams versionParams) {
        FormBody formBody = getRequestParams(versionParams);
        Request.Builder builder = new Request.Builder();
        builder = assembleHeader(builder, versionParams);
        builder.post(formBody).url(versionParams.getRequestUrl());
        return builder;
    }

    public static Request.Builder postJson(VersionParams versionParams) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = getRequestParamsJson(versionParams.getRequestParams());
        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder builder = new Request.Builder();
        builder = assembleHeader(builder, versionParams);
        builder.post(body).url(versionParams.getRequestUrl());
        return builder;
    }

    private static FormBody getRequestParams(VersionParams versionParams) {
        FormBody.Builder builder = new FormBody.Builder();
        HttpParams params = versionParams.getRequestParams();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue() + "");
            ALog.e("params key:"+entry.getKey()+"-----value:"+entry.getValue());
        }
        return builder.build();
    }

    private  static String getRequestParamsJson(HttpParams params) {
        String json;
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            try {
                jsonObject.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        json = jsonObject.toString();
        ALog.e("json:"+json);
        return json;
    }
}
