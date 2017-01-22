package pl.edu.pw.elka.mbi.genomecoverage

import org.scalatest.FunSuite

/**
  * Created by rafalh on 22.01.17.
  */
class ChromoRegionTest extends FunSuite {

  test("testSize") {
    val region = new ChromoRegion(chr = "chr4", start = 5, end = 7)
    assert(region.size == 3)
  }

}
