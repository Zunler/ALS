package  neu

import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.recommendation.{ALS, ALSModel}
import org.apache.spark.ml.tuning.{ParamGridBuilder, TrainValidationSplit}
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}


object AlsModel {
  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(new SparkConf().setAppName("AlsModelSelect"))
    sc.setLogLevel("ERROR")
    val sqlContext = new SQLContext(sc)
    val rattingData = sqlContext.read.parquet("hdfs://172.16.29.107:9000/user/root/zun/music/out/usrArtist/rattingData");

    val evaluator = new RegressionEvaluator()
      .setMetricName("rmse")
      .setLabelCol("rating")
      .setPredictionCol("prediction")


    val als = new ALS()
      .setUserCol("userId")
      .setItemCol("artistId")
      .setRatingCol("rating")

    //参数网格
    val paramGrid = new ParamGridBuilder()
      .addGrid(als.regParam, Array(0.1, 0.01))
      .addGrid(als.rank, Array(10))
      .addGrid(als.maxIter, Array(5, 10))
      .build()
    //使用rmse进行评估


    val trainValidationSplit = new TrainValidationSplit()
      .setEstimator(als)
      .setEvaluator(evaluator)
      .setEstimatorParamMaps(paramGrid)
      .setTrainRatio(value = 0.8)

    val tvsModel = trainValidationSplit.fit(rattingData);
    //获取最优模型
    val alsModel = tvsModel.bestModel.asInstanceOf[ALSModel]

    alsModel.write.overwrite().save("hdfs://172.16.29.107:9000/user/root/zun/music/out/usrArtist/model")



  }
}
