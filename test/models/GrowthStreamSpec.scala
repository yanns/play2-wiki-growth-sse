package models

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import akka.util.ByteString
import models.GrowthStream._
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues, WordSpec}

class GrowthStreamSpec extends WordSpec with Matchers with OptionValues with BeforeAndAfterAll {

  implicit val system = ActorSystem("GrowthStreamSpec")
  implicit val mat = ActorMaterializer()

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  "flow" should {
    "enumerates lines from a source" in {
      val source = Source.single(
        """688	-5.5	10.3
          |hello
          |690	12.51	-70.01
          |""".stripMargin)
        .map(ByteString.apply)
      val result = source via flow
      result
        .runWith(TestSink.probe[Coordinate])
        .request(3)
        .expectNext(Coordinate(-5.5, 10.3), Coordinate(12.51, -70.01))
        .expectComplete()
    }
  }

  "A source line" should {
    "be parsed" in {
      val input = "690\t12.51\t-70.01"
      val result = lineParser(input)
      result.value shouldEqual Coordinate(12.51, -70.01)
    }
  }

  "An unparsable source line" should {
    "not break the parsing" in {
      val input = Seq("688\t-5.5\t10.3", "hello", "690\t12.51\t-70.01")
      val values = input.map(lineParser)
      values shouldEqual Seq(Some(Coordinate(-5.5,10.3)), None, Some(Coordinate(12.51, -70.01)))
    }
  }
}
