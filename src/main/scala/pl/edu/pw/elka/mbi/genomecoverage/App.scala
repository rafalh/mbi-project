package pl.edu.pw.elka.mbi.genomecoverage

import org.apache.hadoop.io.LongWritable
import org.apache.spark.{SparkConf, SparkContext}

object App {

  def main(args: Array[String]) {
    val bedFile = "local-data/20130108.exome.targets-edited.bed"
    val bamFile = "local-data/HG00096.chrom20.ILLUMINA.bwa.GBR.exome.20120522.bam"

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
}
