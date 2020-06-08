package com.demon;

import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DeMon
 * Created on 2020/6/4.
 * E-mail 757454343@qq.com
 * Desc:
 */
public class Main {
    public static void main(String[] args) {
        String bundle_id = args[0]; //包名
        String api_token = args[1];//fir.im的api_token
        String filePath = args[2]; //apk走到打包的路径
        String name = args[3]; //App名
        String version = args[4]; //版本号
        String build = args[5]; //版本Code
        String logTag = exeCmd("git rev-list --tags --max-count=1");
        String tagName = exeCmd("git describe --tags " + logTag);
        String tagTime = exeCmd("git log -1 --format=%ai " + tagName);
        String changelog = exeCmd("git log --no-merges --pretty=\"- (%an,%cr) - %s\" --since=\"" + tagTime + "\"");
        System.out.println(changelog);
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Apk does not exist, please check the path and naming!");
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", "android");
        map.put("bundle_id", bundle_id);
        map.put("api_token", api_token);
        //获取上传凭证
        JSONObject jsonObject = HttpUtil.getInstance().getTokenInfo("http://api.bq04.com/apps", map);
        if (jsonObject != null) {
            JSONObject json = jsonObject.getJSONObject("cert").getJSONObject("binary");
            Map<String, Object> upLoadMap = new HashMap<String, Object>();
            upLoadMap.put("key", json.getString("key"));
            upLoadMap.put("token", json.getString("token"));
            upLoadMap.put("file", file);
            upLoadMap.put("x:name", name);
            upLoadMap.put("x:version", version);
            upLoadMap.put("x:build", build);
            upLoadMap.put("x:changelog", changelog);
            //上传文件
            if (HttpUtil.getInstance().upLoadApk(json.getString("upload_url"), upLoadMap)) {
                //因为AndroidStudio控制台输出中文会乱码，所以这里输出日志都改成英文
                System.out.println("Apk Uploaded successfully! Please visit the link below to view:");
                String url = "http://" + jsonObject.getString("download_domain") + "/" + jsonObject.getString("short");
                System.out.println(url);
                openUrl(url);
            }
        } else {
            System.out.println("Failed to obtain upload information! Please check the parameters!");
        }
    }


    public static String exeCmd(String commandStr) {
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream(),"UTF-8"));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }


    public static void openUrl(String url) {
        String cmd = "rundll32 url.dll,FileProtocolHandler " + url;
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
