# android local service 安卓本地微服务架构

### [**Demo下载体验**](https://www.pgyer.com/MLpo)

1. 添加依赖

```groovy
    implementation 'io.github.lygttpod.android-local-service:core:0.0.1'
    implementation 'io.github.lygttpod.android-local-service:annotation:0.0.1'
    kapt 'io.github.lygttpod.android-local-service:processor:0.0.1'
```

2. 创建本地服务（具体效果可以看项目demo） 定义如下类

```kotlin
   //@Service标记这是一个服务，端口号是服务器的端口号，注意端口号唯一
   @Service(port = 2222)
   abstract class AndroidService {
   
       //@Page标注页面类，打开指定h5页面
       @Page("index")
       fun getIndexFileName() = "test_page.html"
       
       //@Get注解在方法上边
       @Get("query")
        fun query(
            aaa: Boolean,
            bbb: Double,
            ccc: Float,
            ddd: String,
            eee: Int,
        ): List<String> {
            return listOf("$aaa", "$bbb", "$ccc", "$ddd", "$eee")
        }
    
       @Get("saveData")
       fun saveData(content: String) {
           LiveDataHelper.saveDataLiveData.postValue(content + UUID.randomUUID());
       }
   
       @Get("queryAppInfo")
       fun getAppInfo(): HashMap<String, Any> {
           return hashMapOf(
               "applicationId" to BuildConfig.APPLICATION_ID,
               "versionName" to BuildConfig.VERSION_NAME,
               "versionCode" to BuildConfig.VERSION_CODE,
               "uuid" to UUID.randomUUID(),
           )
       }
   }
```

3、初始化服务

```kotlin
        ①、初始化（建议在application中初始化）
        ALSHelper.init(this)
        
        ②、启动服务 
        启动单个服务：
        ALSHelper.startService(ServiceConfig(AndroidService::class.java))

        启动多个服务：
        ALSHelper.startServices(
            listOf(
                ServiceConfig(PCService::class.java),
                ServiceConfig(OtherService::class.java, 9527)
            )
        )
        
        ③、如需修改服务端口号可以在启动服务时候传入新的端口号
        //第一个参数是创建的服务类，第二个参数是 端口号，不传默认是AndroidService类中@Service注解中的端口号
        ServiceConfig(AndroidService::class.java, 9527)
```

4、局域网内浏览器通过一下方式即可看到效果

```
    192.168.31.157 是本机IP地址(每台设备都不一样)

    http://192.168.31.157:2222/index 
    http://192.168.31.157:2222/queryAppInfo 
    http://192.168.31.157:2222/saveData?content=我是浏览器发送的内容
```

5、demo是最好的老师，赶紧去体验一下demo吧！
