package ml.training

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SQLContext, SaveMode}
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by taos on 2017/7/4.
 * /usr/hdp/2.5.0.0-1245/spark/bin/spark-submit --class com.horizon.pt.AlsEtl --master yarn-client  --num-executors 2 --driver-memory 2g --executor-memory 2g --executor-cores 2 /root/algorithm/practicaltraining_2.10-1.0.jar hdfs://192.168.88.104:8020/out/usrArtist/
 */
object AlsEtl {

  //args(0) hdfs根目录
  //将csv，转换为parquet格式
  case class RatingD(userId: Int, artistId: Int, rating: Double)

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(new SparkConf().setAppName("AlsEtl"))
    sc.setLogLevel("ERROR")
    val rawUserArtistData = sc.textFile("hdfs://172.16.29.107:9000/user/root/zun/music/out/usrArtist/data.csv")
    val trainData = buildRatingDataFromRaw(rawUserArtistData)
    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._

    val resultDF = trainData.toDF()
    //本地没有权限写，在集群上运行是可以写的
   resultDF.write.parquet("hdfs://172.16.29.107:9000/user/root/zun/music/out/usrArtist/rattingData")

  }
  //将mysql已经处理好点的数据转换为RDD
  def buildRatings(rawUserArtistData:RDD[String]):RDD[RatingD]= {
    rawUserArtistData.map { line => {
      val array = line.split(",").map(_.toInt);
      RatingD(array(0).toInt, array(1), array(2));
    }
    }
  }
//将原始数据转换为RDD
  def buildRatingDataFromRaw(data: RDD[String]) = {
    val splitData = data.map(_.split(","))
    val countData = splitData.map(x => (x(2), x(3))).map(x => (x._1 + "," + x._2, 1)).reduceByKey(_ + _).map(x => {
      val Array(userId, artistID) = x._1.split(",")
      RatingD(userId.toInt, artistID.toInt, x._2.toDouble)
    })
    countData
  }

}
