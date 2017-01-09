package pl.edu.pw.elka.mbi.genomecoverage

/**
  * Created by rafalh on 09.01.17.
  */
class ChromoRegion(val chr: String, val start: Integer, val end: Integer) extends Serializable {
  override def toString: String = "ChromoRegion(chr: " + chr + ", start: " + start + ", end: " + end + ")"
  def size = end - start + 1
}
