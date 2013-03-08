package models

import org.specs2.mutable._
import play.api.libs.iteratee.{Iteratee, Enumerator}
import models.GrowthStream._
import concurrent.duration.Duration
import concurrent.Await
import io.Source

class LineParserSpec extends Specification {

  "lineEnumerator" should {
    "enumerates lines from a source" in {
      val source = Source.fromString(
        """688	-5.5	10.3
          |hello
          |690	12.51	-70.01
          |""".stripMargin)
      val input = lineEnumerator(source)
      val values = joinValues(input)
      values mustEqual List("688\t-5.5\t10.3", "hello", "690\t12.51\t-70.01")
    }
  }

  "A source line" should {
    "be parsed" in {
      val input = Enumerator("690\t12.51\t-70.01")
      val values = joinValues(input &> lineParser)
      values mustEqual List(Some(Coordinate(12.51, -70.01)))
    }
  }

  "An unparsable source line" should {
    "not break the parsing" in {
      val input = Enumerator("688\t-5.5\t10.3", "hello", "690\t12.51\t-70.01")
      val values = joinValues(input &> lineParser)
      values mustEqual List(Some(Coordinate(-5.5,10.3)), None, Some(Coordinate(12.51, -70.01)))
    }
  }

  "unvalid coordinates" should {
    "be filtered out" in {
      val input = Enumerator(Some(Coordinate(-5.5,10.3)), None, Some(Coordinate(12.51, -70.01)))
      val values = joinValues(input &> validCoordinate)
      values mustEqual List(Coordinate(-5.5,10.3), Coordinate(12.51, -70.01))
    }
  }

  "An unparsable source line" should {
    "be filtered out" in {
      val input = Enumerator("688\t-5.5\t10.3", "hello", "690\t12.51\t-70.01", "a\tb\tc")
      val values = joinValues(input &> lineParser &> validCoordinate)
      values mustEqual List(Coordinate(-5.5,10.3), Coordinate(12.51, -70.01))
    }
  }

  "a source from coordinates" should {
    "be parsed into coordinates" in {
      val source = Source.fromString(
        """688	-5.5	10.3
          |hello
          |690	12.51	-70.01
          |a	b	c""".stripMargin)
      val input = lineEnumerator(source)
      val values = joinValues(input &> lineParser &> validCoordinate)
      values mustEqual List(Coordinate(-5.5,10.3), Coordinate(12.51, -70.01))
    }
  }

  /**
   * @return list of enumerated values of type A
   */
  private def joinValues[A](values: Enumerator[A]): List[A] = {
    val join = Iteratee.fold[A, List[A]](Nil)((list, el) => el :: list)
    Await.result(Iteratee.flatten(values |>> join).run, Duration.Inf).reverse
  }

}
