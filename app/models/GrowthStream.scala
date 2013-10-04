package models

import play.api.libs.iteratee.{Input, Enumeratee, Enumerator}
import concurrent.Future
import io.Source
import play.api.libs.concurrent.Execution.Implicits._
import scala.util.Try

case class Coordinate(latitude: BigDecimal, longitude: BigDecimal)

object GrowthStream {

  /**
   * Enumerates the lines of a file
   */
  def lineEnumerator(source: Source) : Enumerator[String] = {
    val lines = source.getLines()

    Enumerator.fromCallback1[String] ( _ => {
      val line = if (lines.hasNext) {
        Some(lines.next())
      } else {
        None
      }
      Future.successful(line)
    }, source.close)
  }

  object IsDouble {
    def unapply(string: String): Option[Double] =
      Try(string.toDouble).toOption
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
