package models

import java.io.File
import play.api.libs.iteratee.{Input, Enumeratee, Enumerator}
import play.api.libs.concurrent.Promise
import play.api.Logger

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

  /**
   * try to parse one line to extract a [[models.Coordinate]]
   */
  val lineParser: Enumeratee[String, Option[Coordinate]] = Enumeratee.map[String] {
    line =>
      val elements = line.split("\t")
      if (elements.length == 3) {
        try {
          Some(Coordinate(latitude  = elements(1).toDouble,
                          longitude = elements(2).toDouble))
        } catch {
          case e: Exception => Logger.error("error while parsing line: " + line, e); None
        }
      } else {
        None
      }
  }

  /**
   * keeps defined coordinates only
   */
  val validCoordinate: Enumeratee[Option[Coordinate], Coordinate] = Enumeratee.mapInput[Option[Coordinate]] {
    case Input.El(maybe) if maybe.isDefined => Input.El(maybe.get)
    case other => Input.Empty // ignore invalid coordinates
  }

}
