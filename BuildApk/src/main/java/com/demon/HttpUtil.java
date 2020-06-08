package com.demon;

import javafx.util.Pair;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author DeMon
 * Created on 2020/6/4.
 * E-mail 757454343@qq.com
 * Desc:
 */
public class HttpUtil {
    private final String charset = "UTF-8";
    private static HttpUtil instance;

    private HttpUtil() {
    }

    public static HttpUtil getInstance() {
        if (instance == null) {
            instance = new HttpUtil();
        }
        return instance;
    }

    public JSONObject getTokenInfo(String url, Map<String, Object> params) {
        try {
            HttpPost httpPost = new HttpPost(url.trim());
            httpPost.setEntity(new UrlEncodedFormEntity(map2NameValuePairList(params), charset));
            Pair<Integer, String> body = execute(httpPost);
            System.out.println(body.getValue());
            if (body.getKey() == 201) {
                System.out.println("Successfully obtained the upload information, start uploading Apk!");
                JSONObject jsonObject = JSONObject.fromObject(body.getValue());
                return jsonObject;
            } else {
                System.out.println("Failed to obtain upload information! Please check the parameters!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean upLoadApk(String url, Map<String, Object> params) {
        try {
            // Post请求
            HttpPost httpPost = new HttpPost(url.trim());
            // 设置参数
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            entityBuilder.setCharset(Charset.forName("UTF-8"));
            if (params != null && !params.isEmpty()) {
                Iterator<String> it = params.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    Object value = params.get(key);
                    if (value instanceof File) {
                        FileBody fileBody = new FileBody((File) value);
                        entityBuilder.addPart(key, fileBody);
                    } else {
                        entityBuilder.addPart(key, new StringBody(String.valueOf(value), ContentType.DEFAULT_TEXT.withCharset(charset)));
                    }
                }
            }
            HttpEntity entity = entityBuilder.build();
            httpPost.setEntity(entity);
            // 发送请求,获取返回数据
            Pair<Integer, String> body = execute(httpPost);
            System.out.println(body.getValue());
            if (body.getKey() == 200) { //fir.im文档是201，实际上是200
                return true;
            } else {
                System.out.println("Apk upload failed! Please check the upload parameters!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    private Pair<Integer, String> execute(HttpRequestBase requestBase) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String body = null;
        int code = 0;
        try {
            CloseableHttpResponse response = httpclient.execute(requestBase);
            try {
                code = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    body = EntityUtils.toString(entity, charset);
                }
                EntityUtils.consume(entity);
            } catch (Exception e) {
                throw e;
            } finally {
                response.close();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            httpclient.close();
        }
        return new Pair<Integer, String>(code, body);
    }


    private List<NameValuePair> map2NameValuePairList(Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                if (params.get(key) != null) {
                    String value = String.valueOf(params.get(key));
                    list.add(new BasicNameValuePair(key, value));
                }
            }
            return list;
        }
        return null;
    }
}
