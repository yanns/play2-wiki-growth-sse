package controllers

import akka.stream.scaladsl.{FileIO, Source}
import models.Coordinate
import models.GrowthStream._
import play.api._
import play.api.http.ContentTypes
import play.api.libs.EventSource
import play.api.libs.json.Json._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._


class Application(env: Environment) extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  /**
   * convert a coordinate to JSON
   */
  def asJson(coordinate: Coordinate): JsValue =
    Json.obj(
      "latitude" -> coordinate.latitude,
      "longitude" -> coordinate.longitude
    )

  /**
   * Stream of server send events
   */
  def stream() = Action {

    val source = FileIO.fromFile(env.getExistingFile("conf/coosbyid.txt").get, 300)
    val jsonStream: Source[JsValue, _] = source.via(flow).map(asJson)

    Ok.chunked(jsonStream via EventSource.flow).as(ContentTypes.EVENT_STREAM)
  }

}
