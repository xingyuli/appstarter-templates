
## 工具

### 数据模型生成器

为了演示整个项目的使用方式, 代码依赖于一个初始数据库表, 需要先导入data/init.sql。

```
$ cd data
$ gradle jooqCodegen
```

### javascript生成器

```
$ cd admin
$ gradle javascriptGenerator
```
- 该生成器将产生:
  - api_constants.js 包含api路径和所有错误编码
  - options.js 包含系统内所有枚举值
- 项目打包前无需手动调用该task, 在运行gradle bootRun和gradle build前均会先生成js。
- 为了了解生成的js的内容, 可以手动调用该task。


## 编码约定

### 枚举
- 凡是需要枚举类型的地方, 直接使用mysql的ENUM类型, jooqCodegen会自动生成枚举。
- 关于枚举的中文展示问题, 留给客户端根据options.js的枚举清单自行显示。

### dao 和 service
- jooqCodegen自动生成了各个表的dao, 包含各种的基本CRUD操作。
- `service`模块提供基础类`BaseDbService`, 包含类似`spring-data-jpa`的`JpaRepository`对应实现。编写service的时候自行决定使用dao还是`DSLContext`来完成复杂动态的sql。

### 错误编码
- 所有错误编码根据分组放在`Errors.kt`中, 请参照样例`UserErrorCode`编写, i18n信息通过对应app模块的messages.properties文件配置, 例如:
  ```
  UserErrorCode.MOBILE_ALREADY_EXISTED = 手机号码已存在
  ```
- `Errors.kt`中的错误编码会被javascriptGenerator处理, 可运行此task查看生产的内容。

### api测试

- 集成了swagger2 ui,访问路径/swagger-ui.html


## Docker Deploy

### Requirements

- Gradle

### Deploy

Follow usage guide by executing:

```bash
./compose-setup.sh
```


## Quick Experiencing

- Import `data/seed.sql` to your mysql server
- Browse to `http://localhost:9200` if you didn't change the port, sign in with the built-in account
  - username: admin
  - password: 11111111
