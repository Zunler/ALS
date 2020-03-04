package neu

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

object GetRecommend {
def main(args:Array[String])={
  val sc = new SparkContext(new SparkConf().setAppName("GetRecommend").setMaster("yarn"))
  val url = "jdbc:mysql://172.16.29.107:3306/music?characterEncoding=UTF-8"
  //args(0)
  val table="RECOMMENDATIONS"
  val prop = new java.util.Properties
  prop.setProperty("user","hive")
  prop.setProperty("driverClass","com.mysql.cj.jdbc.Driver")//args(1)
  prop.setProperty("password","NEU@pzj123456")//args(2)

  val sqlContext = new SQLContext(sc)
  val result=sqlContext.read.jdbc(url,table,prop)
  //获取 最新一次的推荐结果
  val userID=6
  result.where("USER_ID="+userID).orderBy(-result("ID")).limit(1).show()//args(3):UserID
  }
}
