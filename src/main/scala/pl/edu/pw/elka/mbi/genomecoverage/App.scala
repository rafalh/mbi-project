package pl.edu.pw.elka.mbi.genomecoverage

import org.apache.hadoop.io.LongWritable
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.seqdoop.hadoop_bam.{BAMInputFormat, SAMRecordWritable}

object App {

  def parseChromo(str: String): String = {
    if (str.startsWith("chr")) {
      str.substring(3)
    } else {
      throw new RuntimeException(s"unknown chromosome string ${str}")
    }
  }

  def buildResult(depths: RDD[Int]): RegionResults = {
    new RegionResults(min = depths.min, max = depths.max, mean = depths.mean, stdDev = depths.stdev)
  }

  def main(args: Array[String]) {
    val bamFile = "local-data/HG00096.chrom20.ILLUMINA.bwa.GBR.exome.20120522.bam"

    val conf = new SparkConf()
      .setAppName("Genome Coverage Application")
      .setMaster("local[*]")
      .registerKryoClasses(Array(classOf[LongWritable]))
    val sc = new SparkContext(conf)
    val bamData = sc.newAPIHadoopFile[LongWritable, SAMRecordWritable, BAMInputFormat](bamFile)
    val records = bamData
      .map(pair => pair._2.get())
      .filter(rec => rec.getMappingQuality > 15)
      .filter(rec => !rec.getReadUnmappedFlag && !rec.getNotPrimaryAlignmentFlag)

//    records
//      .take(5)
//      .foreach(rec => {
//        println(rec)
//        println(s"${rec.getReferenceName}: ${rec.getAlignmentStart} - ${rec.getAlignmentEnd} - ${rec.getReadString}")
//      })

    val bedFile = sc.textFile("local-data/20130108.exome.targets.bed")
    val regions = bedFile
      .map(line => line.split("\t"))
      .map(fields => new ChromoRegion(parseChromo(fields(0)), Integer.valueOf(fields(1)), Integer.valueOf(fields(2))))

    regions
      .cartesian(records)
      .filter(pair => { // find overlapping pairs (region and read align)
        val reg = pair._1
        val rec = pair._2
        rec.getAlignmentStart <= reg.end && rec.getAlignmentEnd <= reg.start
      })
      .map(pair => { // map to array of region length which value is 1 if position is contained in record, 0 otherwise
        val reg = pair._1
        val rec = pair._2
        val temp = Array(0, reg.size) // optimization?
        for (i <- 0 to reg.size)
          if (reg.start + i >= rec.getAlignmentStart && reg.start + i <= rec.getAlignmentEnd)
            temp(i) = 1
        (reg, temp)
      })
      .reduceByKey((x, y) => x.zip(y).map(p => p._1 + p._2)) // sum arrays for the same region
      .map(pair => { // calculate interesting results
        val depths = pair._2
        val sum = depths.sum
        val mean = sum / depths.length
        (pair._1, new RegionResults(
          // Can we do it better? RDD has needed methods...
          min = depths.min,
          max = depths.max,
          mean = mean,
          stdDev = Math.sqrt(depths.map( _ - mean).map(t => t*t).sum / depths.length)))
      })
      .take(5)
      .foreach(pair => {
        println(pair._1 + ": " + pair._2)
      })
    sc.stop()
  }
}