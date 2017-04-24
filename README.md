# autoApi-common 接口自动化测试框架
* 所有的自动化测试用例项目统一约束如下:<br>
groupId: `com.lj.test`<br>
artifactId: `autocase-XXX`<br>
如:<br>
```
    <groupId>com.lj.test</groupId>
    <artifactId>autocase-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
```

## 接口自动化用例设计的一些原则
1. 每个接口单独一个类，文件名、方法名清晰
2. 各个接口之间尽量减少相互依赖，最好完全独立可以运行
3. 保证对数据库的强兼容性，增强自动化用例运行的健壮性, 自动化测试数据程序结束时及时清理


## 使用中的一些约束
1. 数据库使用 com.lj.test.busines.DB, 采用单例模式, 防止重复建立数据库开销
2. json文件形式适合固定参数、参数变化范围不大等情况
3. 自动化环境目前和功能环境是同一套, 自动化的所有操作全部采用特定的一批自动化专属用户(参见com.lj.test.busines.Users), 生成的自动化数据test结束后要在代码中及时清理


## TODO 待后续完善点
1. 每个接口的并发测试
2. 

## json文件标准模板及用例生成规则:
### 1. 用例生成逻辑如下:

* 生成用例总数:tAll
* ENUM的用例数为:t-Enum
假设:url中 N 个ENUM变量,每个变量的ENUM个数分别为Eu(n), 则 
```
t—urlEnum = Eu1 * Eu2 * Eu3 ... * EuN
```
同理:headers, params中亦如是:
```
t-headerEnum = Eh1 * Eh2 * Eh3 ... * EhN
t-paramEnum = Ep1 * Ep2 * Ep3 ... * EpN
```
ENUM穷举生成的用例总数为: 
```
t-Enum = t—urlEnum * t-headerEnum * t-paramEnum
```

* 异常字符串的用例数为: t-Exec
假设url 的 query中有 M-url 个参数, config.properties 中有 S-url 个字符串, 则 针对url path异常字符串生成的总用例数为: 
```
t-urlExce = M-url * S-url
```
同理, headers , body 中亦如是: 
```
t-headersExce = M-headers * S-exce,
t-paramsExce = M-body * S-exce
```
ps: headers\body 的异常字符串集合为同一个参数配置
异常字符串生成的总用例数为: 
```
t-Exec = t-urlExce + t-headersExce + t-paramsExce
```
(//TODO: 此处处理逻辑是否需要相乘,还是相加,待考虑。
初衷: ENUM中每个组合都要遍历, 异常字符串代码中几乎不会组合处理)

* 综上,总用例数为:
```
tAll = t-Enum + t-Exec
```


### 2. json模板如下:
```
{
  "description": "用户进行一次操作 /api/v1/resources/{userType}",
  "sqlBeforeClass": [
    "select * from mydata.T_U_Ext order by Id  limit 1;"
  ],
  "sqlAfterClass": [
    "delete from mydata.T_User where Id=150004360;",
    "delete from mydata.T_CouUser where Id=150004360;"
  ],
  "sqlBeforeMethod": [
    
  ],
  "sqlAfterMethod": [
  ],
  "apis": [
    {
      "desc": "测试接口模板",
      "url": "http://ops.test:8080/api/v1/resources/{userType}?platform={platform}",
      "method": "post",
      "headers": {
        "Content-Type": "application/json",
        "Accept": "*/*",
        "X-Tracking-ID": "X-Tracking-ID",
        "userId": "<%user_all_over.UserId%>",
        "Authorization": "encrypt <%user_all_over.Token%>"
      },
      "body": {
      },
      "isNeedException": true,
      "EnumURL": {
        "userType": [
          "FreshUser",
          "NewUser",
          "OldUser",
          "LosingUser",
          "All"
        ],
        "platform": [
          "baidu",
          "sougou",
          "all"
        ]
      },
      "EnumHeaders": {
        "Authorization": [
          "encrypt <%user_all_over.Token%>",
          "<%user_all_over.Token%>",
          "encrypt <%user_all_over.Token%>123"
        ]
      },
      "EnumParams": {}
    }
  ]
}
```
