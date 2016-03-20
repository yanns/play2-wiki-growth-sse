package models

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Framing, Source}
import akka.util.ByteString

import scala.util.Try

case class Coordinate(latitude: BigDecimal, longitude: BigDecimal)

object GrowthStream {

  val flow: Flow[ByteString, Coordinate, NotUsed] =
    Framing.delimiter(ByteString("\n"), maximumFrameLength = 100, allowTruncation = true)
      .map(_.utf8String)
      .map(lineParser)
      .collect { case Some(c) ⇒ c }


  /**
   * try to parse one line to extract a [[models.Coordinate]]
   */
  def lineParser(line: String): Option[Coordinate] =
    line.split("\t") match {
      case Array(_, IsDouble(latitude), IsDouble(longitude)) => Some(Coordinate(latitude, longitude))
      case _ => None
    }

  private object IsDouble {
    def unapply(string: String): Option[Double] =
      Try(string.toDouble).toOption
  }

}
