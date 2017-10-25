package com.github.shokohara.titanic

import org.apache.spark.SparkConf
import org.apache.spark.sql.{Dataset, SparkSession}

/**
  * def name_classifier(name_df):
  * name_class_df = pd.DataFrame(columns={'miss', 'mrs', 'master', 'mr'})
  * for name in name_df:
  * if 'Miss' in name:
  * ldf = pd.DataFrame([[1, 0, 0, 0]], columns={'miss', 'mrs', 'master', 'mr'})
  * elif 'Mrs' in name:
  * ldf = pd.DataFrame([[0, 1, 0, 0]], columns={'miss', 'mrs', 'master', 'mr'})
  * elif 'Master' in name:
  * ldf = pd.DataFrame([[0, 0, 1, 0]], columns={'miss', 'mrs', 'master', 'mr'})
  * elif 'Mr' in name:
  * ldf = pd.DataFrame([[0, 0, 0, 1]], columns={'miss', 'mrs', 'master', 'mr'})
  * else:
  * ldf = pd.DataFrame([[0, 0, 0, 0]], columns={'miss', 'mrs', 'master', 'mr'})
  * name_class_df = name_class_df.append(ldf, ignore_index=True)
  * name_class_df["miss"] = name_class_df["miss"].astype(int)
  * name_class_df["mrs"] = name_class_df["mrs"].astype(int)
  * name_class_df["master"] = name_class_df["master"].astype(int)
  * name_class_df["mr"] = name_class_df["mr"].astype(int)
  * return name_class_df
  */
//PassengerId,Survived,Pclass,Name,Sex,Age,SibSp,Parch,Ticket,Fare,Cabin,Embarked
case class Raw(passengerId: Int,
               survived: Int,
               pClass: Int,
               name: String,
               sex: String,
               age: Double,
               sibSp: Int,
               parch: String,
               ticket: String,
               fare: Double,
               cabin: String,
               embarked: String)

case class Processed(passengerId: Int,
                     survived: Int,
                     pClass: Int,
                     sex: Int,
                     age: Int,
                     sibSp: Int,
                     parch: String,
                     ticket: String,
                     fare: Double,
                     cabin: String,
                     embarked: String,
                     miss: Int,
                     mrs: Int,
                     master: Int,
                     mr: Int)

import shapeless._
import record._
import shapeless.syntax.std.product._

object Main {

  def b(raw: Raw) =
    if (raw.name contains "Miss") (1, 0, 0, 0)
    else if (raw.name contains "Mrs") (0, 1, 0, 0)
    else if (raw.name contains "Master") (0, 0, 1, 0)
    else if (raw.name contains "Mr") (0, 0, 0, 1)
    else (0, 0, 0, 0)

  def sex(sex: String) = if (sex == "male") 1 else if (sex == "female") 0 else 0

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]")
    //    val conf = new SparkContext()
    //      .setAppName("my app")
    //      .set("spark.executor.memory", "1g")
    val spark: SparkSession = SparkSession.builder.config(conf).appName("spark session example").getOrCreate
    import spark.implicits._
    //    spark.sparkContext.setLogLevel("ERROR")
    val df: Dataset[Raw] =
      spark.read.format("csv").option("header", "true").option("inferSchema", "true").load("train.csv").as[Raw]
    def nameClassifier(df: Dataset[Raw]): Dataset[Processed] =
      df.map { (raw: Raw) =>
        val rec2 = LabelledGeneric[Raw].to(raw)
        Generic[Processed].from(rec2.remove('name)._2.updateWith('age)(_.toInt).updateWith('sex)(sex) ++ b(raw).toHList)
      }
    nameClassifier(df).printSchema
    spark.stop()
  }
}

object PrintUtiltity {
  def p(data: String) =
    println(data)
}
