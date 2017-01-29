package pl.edu.pw.elka.mbi.genomecoverage

import htsjdk.samtools.SAMRecord
import org.apache.hadoop.io.LongWritable
import org.apache.log4j.Logger
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.seqdoop.hadoop_bam.{BAMInputFormat, SAMRecordWritable}

object ChromoCoverageCalc {

  private[this] val logger = Logger.getLogger(getClass)

  private[this] val BED_PARTITIONS = 8

  def parseChromo(str: String): String = {
    if (str.startsWith("chr")) {
      str.substring(3)
    } else {
      throw new UnsupportedOperationException(s"unknown chromosome string $str")
    }
  }

  def loadBed(path: String, sc: SparkContext): RDD[ChromoRegion] = {
    sc.textFile(path, BED_PARTITIONS)
      .map(line => line.split("\t"))
      .map(fields => new ChromoRegion(parseChromo(fields(0)), Integer.valueOf(fields(1)), Integer.valueOf(fields(2))))
  }

  def loadBam(path: String, sc: SparkContext): RDD[SAMRecord] = {
    sc.newAPIHadoopFile[LongWritable, SAMRecordWritable, BAMInputFormat](path)
      .map(pair => pair._2.get())
  }

  def filterBam(records: RDD[SAMRecord]): RDD[SAMRecord] = {
    records.filter(rec => rec.getMappingQuality > 15)
      .filter(rec => !rec.getReadUnmappedFlag && !rec.getNotPrimaryAlignmentFlag)
  }

  def dumpSamRecords(records: RDD[SAMRecord], limit: Int) = {
    records
      .take(limit)
      .foreach(rec => {
        logger.info(rec)
        logger.info(s"${rec.getReferenceName}: ${rec.getAlignmentStart} - ${rec.getAlignmentEnd} - ${rec.getReadString}")
      })
  }

  def calc(records: RDD[SAMRecord], regions: RDD[ChromoRegion]): RDD[(ChromoRegion, RegionResults)] = {
    regions
      .cartesian(records)
      .filter(pair => {
        // find overlapping pairs (region and read align)
        val reg = pair._1
        val rec = pair._2
        rec.getAlignmentStart <= reg.end && rec.getAlignmentEnd >= reg.start
      })
      .map(pair => {
        // map to array of region length which value is 1 if position is contained in record, 0 otherwise
        logger.info(pair._1 + ": " + pair._2)
        logger.info(s"${pair._2.getReferenceName}: ${pair._2.getAlignmentStart} - ${pair._2.getAlignmentEnd} - ${pair._2.getReadString}")

        val reg = pair._1
        val rec = pair._2
        val temp = Array.fill[Int](reg.size + 1)(0) // optimization?
        for (i <- 0 to reg.size)
          if (reg.start + i >= rec.getAlignmentStart && reg.start + i <= rec.getAlignmentEnd) {
            temp(i) = 1
          }
        (reg, temp)
      })
      .reduceByKey((x, y) => x.zip(y).map(p => p._1 + p._2))
      .map(pair => {
        // calculate interesting results
        val depths = pair._2
        val sum = depths.sum
        val mean: Double = (sum.toDouble / depths.length)
        val stdDev: Double = Math.sqrt(depths.map(_ - mean).map(t => t * t).sum / depths.length)
        (pair._1, new RegionResults(
          // Can we do it better? RDD has needed methods...
          length = depths.length,
          sum = sum,
          min = depths.min,
          max = depths.max,
          mean = mean,
          stdDev = stdDev))
      })
  }

  def calc(bamPath: String, bedPath: String, sc: SparkContext): RDD[(ChromoRegion, RegionResults)] = {
    val records = filterBam(loadBam(bamPath, sc))
    val regions = loadBed(bedPath, sc)
    logger.info("bam count - " + records.count())
    logger.info("bed count - " + regions.count())
    calc(records, regions)
  }
}
