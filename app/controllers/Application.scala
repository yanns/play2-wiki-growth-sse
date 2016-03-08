package controllers

import play.api._
import libs.EventSource
import libs.iteratee.Enumeratee
import libs.json.{Json, JsValue}
import play.api.mvc._
import play.api.Play.current
import libs.json.Json._
import models.Coordinate
import models.GrowthStream._
import play.api.libs.concurrent.Execution.Implicits._


class Application(env: Environment) extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  /**
   * convert a coordinate to JSON
   */
  val asJson: Enumeratee[Coordinate, JsValue] = Enumeratee.map[Coordinate] { coordinate =>
    Json.obj(
      "latitude" -> coordinate.latitude,
      "longitude" -> coordinate.longitude
    )
  }

  /**
   * Stream of server send events
   */
  def stream() = Action {
    val source = scala.io.Source.fromFile(env.getExistingFile("conf/coosbyid.txt").get)
    val jsonStream = lineEnumerator(source) &> lineParser &> validCoordinate &> asJson
    val eventDataStream = jsonStream &> EventSource()
    Ok.chunked(eventDataStream).as("text/event-stream")
  }

}
