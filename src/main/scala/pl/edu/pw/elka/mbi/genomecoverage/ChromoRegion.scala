package pl.edu.pw.elka.mbi.genomecoverage

/**
  * Created by rafalh on 09.01.17.
  */
class ChromoRegion(val chr: String, val start: Integer, val end: Integer) extends Serializable {
  override def toString: String = "ChromoRegion(chr: " + chr + ", start: " + start + ", end: " + end + ")"
  def size = end - start + 1

  def canEqual(other: Any): Boolean = other.isInstanceOf[ChromoRegion]

  override def equals(other: Any): Boolean = other match {
    case that: ChromoRegion =>
      (that canEqual this) &&
        chr == that.chr &&
        start == that.start &&
        end == that.end
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(chr, start, end)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
