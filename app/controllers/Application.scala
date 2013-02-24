package controllers

import play.api._
import libs.EventSource
import libs.iteratee.{Enumerator, Enumeratee}
import libs.json.{Json, JsValue}
import play.api.mvc._
import play.api.Play.current
import libs.json.Json._
import models.Coordinate
import models.GrowthStream._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  /**
   * convert a coordinate to JSON
   */
  val asJson: Enumeratee[Coordinate, JsValue] = Enumeratee.map[Coordinate] {
    coordinate => Json.obj(
      "latitude" -> coordinate.latitude.toString(),
      "longitude" -> coordinate.longitude.toString()
    )
  }

  /**
   * Stream of server send events
   */
  def stream() = Action {
    val jsonStream = fileLineStream(Play.getExistingFile("conf/coosbyid.txt").get) &> lineParser &> validCoordinate &> asJson
    val eventDataStream = jsonStream &> EventSource()
    Ok.stream(eventDataStream >>> Enumerator.eof).as("text/event-stream")
  }

}
