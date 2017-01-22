package pl.edu.pw.elka.mbi.genomecoverage

/**
  * Created by rafalh on 09.01.17.
  */
class RegionResults(val length: Integer, val sum: Integer,
                    val min: Integer, val max: Integer,
                    val mean: Double, val stdDev: Double) extends Serializable {

  override def toString = s"RegionResults(length=$length, sum=$sum, min=$min, max=$max, mean=$mean, stdDev=$stdDev)"

  def canEqual(other: Any): Boolean = other.isInstanceOf[RegionResults]

  override def equals(other: Any): Boolean = other match {
    case that: RegionResults =>
      (that canEqual this) &&
        length == that.length &&
        sum == that.sum &&
        min == that.min &&
        max == that.max &&
        mean == that.mean &&
        stdDev == that.stdDev
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(length, sum, min, max, mean, stdDev)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
