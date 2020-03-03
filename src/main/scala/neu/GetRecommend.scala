package neu

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

object GetRecommend {
def main(args:Array[String])={
  val sc = new SparkContext(new SparkConf().setAppName("GetRecommend"))
  val url = "jdbc:mysql://172.16.29.107:3306/music?characterEncoding=UTF-8"
  //args(0)
  val table="RECOMMENDATIONS"
  val prop = new java.util.Properties
  prop.setProperty("user","hive")//args(1)
  prop.setProperty("password","NEU@pzj123456")//args(2)
  prop.setProperty("driverClass","com.mysql.cj.jdbc.Driver")
  val sqlContext =  new SQLContext(sc)
  val result=sqlContext.read.jdbc(url,table,prop)
  //获取 最新一次的推荐结果
  result.where("USER_ID=6").orderBy(-result("ID")).limit(1).show()//args(3):UserID
  }
}
