package pl.edu.pw.elka.mbi.genomecoverage

import com.holdenkarau.spark.testing.SharedSparkContext
import org.scalatest.FunSuite

/**
  * Created by rafalh on 22.01.17.
  */
class ChromoCoverageCalc$Test extends FunSuite with SharedSparkContext {

  test("testLoadBed") {
    val regions = ChromoCoverageCalc.loadBed("src/test/resources/test.bed", sc)
    assert(regions.count() == 1)
    assert(regions.take(1)(0) == new ChromoRegion("1", 655490, 655610))
  }

  test("testLoadBam") {
    val records = ChromoCoverageCalc.loadBam("local-data/HG00096.chrom20.ILLUMINA.bwa.GBR.exome.20120522.bam", sc)
    assert(records.count() == 1933817)
  }

  test("testParseChromo") {
    assert(ChromoCoverageCalc.parseChromo("chr1") == "1")
    assert(ChromoCoverageCalc.parseChromo("chr20") == "20")
    assert(ChromoCoverageCalc.parseChromo("chrX") == "X")
    intercept[UnsupportedOperationException] {
      ChromoCoverageCalc.parseChromo("foobar")
    }
  }

  test("testFilterBam") {
    val records = ChromoCoverageCalc.filterBam(
      ChromoCoverageCalc.loadBam("local-data/HG00096.chrom20.ILLUMINA.bwa.GBR.exome.20120522.bam", sc))
    assert(records.count() == 1923237)
  }

  test("testCalc") {

    val results = ChromoCoverageCalc.calc(
      "local-data/HG00096.chrom20.ILLUMINA.bwa.GBR.exome.20120522.bam",
      "src/test/resources/test.bed", sc)

    assert(results.count() == 1)
    val pair = results.take(1)(0)
    assert(pair._2 == new RegionResults(
      length = 122,
      sum = 200,
      min = 0,
      max = 3,
      mean = 1.639344262295082,
      stdDev = 0.5442030384491091
    ))
  }

}
