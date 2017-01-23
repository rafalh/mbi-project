package pl.edu.pw.elka.mbi.genomecoverage

import org.apache.hadoop.io.LongWritable
import org.apache.spark.{SparkConf, SparkContext}

object App {

  def main(args: Array[String]) {
    if(args.length > 1){
      val bedFile = args(0)
      val bamFile = args(1)

      val conf = new SparkConf()
        .setAppName("Genome Coverage Application")
        .setMaster("local[*]")
        .registerKryoClasses(Array(classOf[LongWritable]))
      val sc = new SparkContext(conf)

      val results = ChromoCoverageCalc.calc(bamFile, bedFile, sc)

      results
        .foreach(pair => {
          println(pair._1 + ": " + pair._2)
          println("length: " + pair._2.length)
          println("sum: " + pair._2.sum)
          println("min: " + pair._2.min)
          println("max: " + pair._2.max)
          println("mean: " + pair._2.mean)
          println("stdDev: " + pair._2.stdDev)
        })

      sc.stop()
    }
    else{
      println("usage: ./run.sh <path-to-bed-file> <path-to-bam-file>")
    }
  }
}
