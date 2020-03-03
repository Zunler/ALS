package ml.training

import java.util.Properties

import org.apache.spark.ml.feature.{VectorAssembler, VectorSlicer}
import org.apache.spark.ml.recommendation.ALSModel
import org.apache.spark.ml.recommendation.ALSModel
import org.apache.spark.ml.tuning.TrainValidationSplitModel
import org.apache.spark.ml.util.MLReader
import org.apache.spark.sql.{DataFrame, Row, SQLContext}
import org.apache.spark.sql.functions._
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by taos on 2017/7/4.
  */
object AlsPredictor {
  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(new SparkConf().setAppName("AlsPredictor"))
    val sqlContext =  new SQLContext(sc)
    import sqlContext.implicits._

    val als =ALSModel.read.asInstanceOf[MLReader[ALSModel]].load("hdfs://172.16.29.107:9000/user/root/zun/music/out/usrArtist/model")


    val userRecs:DataFrame = als.recommendForAllUsers(10)
//这里的推荐结果 recommendations 是WrappedArray 我不知道怎么从dataframe里提取出来，只好曲线救国
    //TODO 把数组提取出来
    var users=new ArrayBuffer[Int]
    var aritsts=new ArrayBuffer[String]
    //把数据分别提取到users，和artists中
    var userArray:Array[Row] = userRecs.collect()
    userArray.map(row => {
      users.append(row.get(0).toString().toInt)
      val arrayPredict : Seq[Row] = row.getSeq(1)
      val temp=new ArrayBuffer[Int]()
      arrayPredict.map(rowPredict =>{
        temp.append(rowPredict(0).toString.toInt)
      })
      aritsts.append(temp.mkString(","))

    })

val ud=users.toDF("USER_ID")
val ad=aritsts.toDF("ARTISTS")

   val recommendData= ud.crossJoin(ad)
    //TODO 写到数据库，把预测结果整理出来

    val url = "jdbc:mysql://172.16.29.107:3306/music?characterEncoding=UTF-8"//args(1)
    //TODO 表要事先建好 ID USER_ID ARTISTS
    val table="RECOMMENDATIONS"
    val prop = new java.util.Properties
    prop.setProperty("user","hive")//args(2)
    prop.setProperty("password","NEU@pzj123456")//args(3)
    prop.setProperty("driverClass","com.mysql.cj.jdbc.Driver")
    recommendData.write.mode("append").jdbc(url, table, prop)


  }




}
