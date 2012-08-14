package controllers

import play.api._
import libs.EventSource
import libs.iteratee.Enumeratee
import libs.json.JsValue
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
    coordinate => toJson ( Map (
      "latitude" -> toJson(coordinate.latitude.toString()),
      "longitude" -> toJson(coordinate.longitude.toString())
    ) )
  }

  /**
   * Stream of server send events
   */
  def stream() = Action {
    Ok.stream(fileLineStream(Play.getExistingFile("conf/coosbyid.txt").get) &> lineParser &> validCoordinate &> asJson ><> EventSource()).as("text/event-stream")
  }

}
