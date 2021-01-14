package com.demo.bigdata.program.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class SparkFirst {

    public void sparkTest(){
        //配置执行
        SparkConf conf = new SparkConf().setAppName("Java_WordCount");
        // 创建SparkContext对象:  JavaSparkContext
        JavaSparkContext context = new JavaSparkContext(conf);
        //读入数据
        JavaRDD<String> lines = context.textFile("");
        //分词
        JavaRDD<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterator<String> call(String line) throws Exception {
                return Arrays.asList(line.split(" ")).iterator();
            }

        });
        //每个单词记一次数
        JavaPairRDD<String, Integer> wordOne = words.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String word) throws Exception {
                return new Tuple2<String,Integer>(word,1);
            }
        });

        //执行reduceByKey的操作
        JavaPairRDD<String, Integer> count = wordOne.reduceByKey(new Function2<Integer, Integer, Integer>() {

                   @Override
            public Integer call(Integer i1, Integer i2) throws Exception {
                return i1 + i2;
            }
        });


        //执行计算，执行action操作: 把结果打印在屏幕上
        List<Tuple2<String, Integer>> result = count.collect();

        //输出
        for(Tuple2<String, Integer> tuple: result){
            System.out.println(tuple._1+"\t"+tuple._2);
        }

        //停止SparkContext对象
        context.stop();
    }


    public void readData1(){
        SparkSession session = SparkSession.builder()
                .appName("SQLJava")
                .config("spark.master", "local[2]")
                .getOrCreate();
        /**
         *json(paths: String)
         *json(jsonRDD: JavaRDD[String])
         *json(RDD: RDD[String])
         *json(jsonDataset: Dataset[String])
         */
        Dataset<Row> df1 = session.read().json("d:/json.json");

        df1.createOrReplaceTempView("stu");
        df1 = session.sql("select * from stu");
        df1.show();

        Dataset<Row> df2 = session.sql("select * from stu where age > 20");
        df2.show();
        System.out.println("=============================");

        //聚合查询
        Dataset<Row> dfCount = session.sql("select count(*) from stu");
        dfCount.show();

        /*
         * DataFrame 转换为 RDD
         * */
        JavaRDD<Row> rdd = df1.toJavaRDD();
        rdd.collect().forEach(new Consumer<Row>() {
            @Override
            public void accept(Row row) {
                Long id = row.getAs("id");
                String name = row.getAs("name");
                Long age = row.getAs("age");
                System.out.println(id + "-" + name + "-" + age);
            }
        });

    }

    /**
     *json(paths: String)
     *json(jsonRDD: JavaRDD[String])
     *json(RDD: RDD[String])
     *json(jsonDataset: Dataset[String])
     */
    public void readData2(){
        //配置执行
        SparkConf conf = new SparkConf().setAppName("Java_WordCount");
        // 创建SparkContext对象:  JavaSparkContext
        JavaSparkContext context = new JavaSparkContext(conf);
        /**
         * new SQLContext(param)
         * param:sparkContext/javaSparkContext/SparkSession
         */
        SQLContext sqlContext = new SQLContext(context);
        Dataset<Row> json = sqlContext.read().json();

    }
}
