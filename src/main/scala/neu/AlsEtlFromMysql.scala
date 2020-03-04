package neu

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

object AlsEtlFromMysql {
  def main(args: Array[String]) = {

    val sc = new SparkContext(new SparkConf().setAppName("GetRecommend").setMaster("yarn"))
    val url = "jdbc:mysql://172.16.29.107:3306/music?characterEncoding=UTF-8"
    sc.setLogLevel("ERROR")
    //args(0)
    val table = "TB_SYS_USER_MUSIC"
    val prop = new java.util.Properties
    prop.setProperty("user", "hive")
    prop.setProperty("driverClass", "com.mysql.cj.jdbc.Driver") //args(1)
    prop.setProperty("password", "NEU@pzj123456") //args(2)
    val sqlContext = new SQLContext(sc)

    sqlContext.read.jdbc(url, table, prop).createOrReplaceTempView("data") //将dataframe转换为临时视图

    val data = sqlContext.sql("SELECT USER_ID , ARTIST_ID , COUNT(ARTIST_ID) AS COUNT FROM data GROUP BY ARTIST_ID , USER_ID ORDER BY USER_ID,ARTIST_ID")
    data.show()
    //本地没有权限写，在集群上运行是可以写的
    data.write.mode("overwrite").parquet("hdfs://172.16.29.107:9000/user/root/zun/music/out/usrArtist/rattingData")
  }

}
