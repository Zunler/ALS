# 使用说明

使用前按照此[方法](https://blog.csdn.net/qq_36699423/article/details/92795821)打成jar包，放在spark安装目录的jars下

```shell
 	./bin/spark-submit --driver-class-path ./jars/mysql-connector-java-8.0.17.jar \--class neu.GetRecommend --master yarn --deploy-mode client --num-executors 100  --executor-memory 6G  --executor-cores 8 --driver-memory 1G --conf spark.default.parallelism=1000 --conf spark.storage.memoryFraction=0.5 --conf spark.shuffle.memoryFraction=0.3  ./jars/als.jar
```

## AlsEtl

从csv中读取数据，存parquet文件到hdfs

## AlsEtlFromMysql

从虚拟机数据库中读取数据，存parquet到hdfs

## AlsModel

使用hdfs的parquet文件，调用als算法，使用网格搜索，交叉验证保存最优模型到Hdfs

## AlsPredictor

从hdfs加载模型，为所有用户推荐艺术家存在虚拟机数据库

## GetRecommend

spark从数据库读取指定用户的推荐

## 遇到问题

```
class not found com.mysql.cj.jdbc.Driver
```

idea运行没有问题，集群运行报错。但是已经是maven工程并打成jar包，按理说不会出现这问题。

最后发现是配置了spark on yarn，在spark-default-conf中配置了默认从hdfs中读取jar

```xml
spark.master yarn-client
spark.yarn.jars=hdfs://master:9000/user/spark/spark-jars/*
```

把mysql-connector上传到hdfs指定目录即可