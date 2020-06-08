### OneKeyApk

Android可以进行一键打包，上传，提交修改记录的黑科技。  
CSDN原文链接:<https://demon.blog.csdn.net/article/details/106626221>


### 说明
OneKeyPack：测试打包的Android程序  

BuildApk：打包上传到fir.im的Java程序  


### 直接使用

1. 下载[BuildApk.jar](https://github.com/DeMonLiu623/OneKeyApk/blob/master/OneKeyPack/BuildApk.jar)  

2. 配置你的Module的bulid.gradle如下：

```
task aaUploadApk(type: Exec) {
    def applicationId = project.android.defaultConfig.applicationId //包名
    def api_token = "fir.im的api_token" //fir.im的api_token
    def filePath = "build/outputs/apk/release/app-release.apk" //apk打包的原始的路径
    def name = "OneKey" //App名
    def versionName = project.android.defaultConfig.versionName //版本号
    def versionCode = project.android.defaultConfig.versionCode //版本Code
    def jarPath = project.rootDir.toString() + "/BuildApk.jar" //jar的路径
    commandLine "java", "-jar", "$jarPath", "$applicationId", "$api_token", "$filePath", "$name", "$versionName", "$versionCode"
}

aaBuildApk.dependsOn('assembleRelease')
aaUploadApk.dependsOn('clean', 'aaBuildApk')
aaBuildApk.mustRunAfter('clean')
```


3. 双击运行```Gradle---Tasks---other---aaUploadApk```