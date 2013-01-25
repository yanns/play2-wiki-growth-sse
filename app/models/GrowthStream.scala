package models

import java.io.File
import play.api.libs.iteratee.{Input, Enumeratee, Enumerator}
import play.api.libs.concurrent.Promise
import org.apache.commons.lang3.math.NumberUtils

case class Coordinate(latitude: BigDecimal, longitude: BigDecimal)

object GrowthStream {

  /**
   * Enumerates the lines of a file
   */
  def fileLineStream(file: File) : Enumerator[String] = {
    val source = scala.io.Source.fromFile(file)
    val lines = source.getLines()

    Enumerator.fromCallback[String] (() => {
      val line = if (lines.hasNext) {
        Some(lines.next())
      } else {
        None
      }
      Promise.pure(line)
    }, source.close)
  }

  object IsDouble {
    def unapply(string: String): Option[Double] = {
      if (NumberUtils.isNumber(string)) {
        Some(string.toDouble)
      } else {
        None
      }
    }
  }

  /**
   * try to parse one line to extract a [[models.Coordinate]]
   */
  val lineParser: Enumeratee[String, Option[Coordinate]] = Enumeratee.map[String] { line =>
    line.split("\t") match {
      case Array(_, IsDouble(latitude), IsDouble(longitude)) => Some(Coordinate(latitude, longitude))
      case _ => None
    }
  }

  /**
   * keeps defined coordinates only
   */
  val validCoordinate: Enumeratee[Option[Coordinate], Coordinate] = Enumeratee.mapInput[Option[Coordinate]] {
    case Input.El(Some(coordinate)) => Input.El(coordinate)
    case other => Input.Empty // ignore invalid coordinates
  }

}
