package nodescala

import scala.language.postfixOps
import scala.util.{Try, Success, Failure}
import scala.collection._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.async.Async.{async, await}
import org.scalatest._
import NodeScala._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class NodeScalaSuite extends FunSuite {

  /**
   * Additional tests based on first response to submission
   */
  
test("A Future.all should be completed with all the results of successful futures List(1, 2, 3, 4, 5).map(x => Future { x })") {
    assert(false)
  }
// [Observed Error] an implementation is missing
//   [exception was thrown] detailed error message in debug output section below
// [Lost Points] 4

test("A Future should be completed with any of the results") {
    assert(false)
  }
// [Observed Error] an implementation is missing
//   [exception was thrown] detailed error message in debug output section below
// [Lost Points] 4

test("A Future should fail with one of the exceptions when calling any") {
    assert(false)
  }
// [Observed Error] an implementation is missing
//   [exception was thrown] detailed error message in debug output section below
// [Lost Points] 4

test("A Future should complete after 3s when using a delay of 1s") {
    assert(false)
  }
// [Observed Error] an implementation is missing
//   [exception was thrown] detailed error message in debug output section below
// [Lost Points] 2

test("A Future should not complete after 1s when using a delay of 3s") {
    assert(false)
  }
// [Observed Error] an implementation is missing
//   [exception was thrown] detailed error message in debug output section below
// [Lost Points] 2

test("A Future should run until cancelled when using Future.run") {
    assert(false)
  }
// [Observed Error] an implementation is missing
//   [exception was thrown] detailed error message in debug output section below
// [Lost Points] 4

test("Future.now should return the result when completed") {
    assert(false)
  }
// [Observed Error] an implementation is missing
//   [exception was thrown] detailed error message in debug output section below
// [Lost Points] 2

test("Future.now should throw a NoSuchElementException when not completed") {
    assert(false)
  }
// [Observed Error] an implementation is missing
//   [exception was thrown] detailed error message in debug output section below
// [Lost Points] 2

test("Future.continueWith should contain the continued value") {
    assert(false)
  }
// [Observed Error] an implementation is missing
//   [exception was thrown] detailed error message in debug output section below
// [Lost Points] 2

test("Future.continueWith should handle exceptions thrown by the user specified continuation function") {
    assert(false)
  }
// [Observed Error] an implementation is missing
//   [exception was thrown] detailed error message in debug output section below
// [Lost Points] 2

test("Future.continue should contain the continued value") {
    assert(false)
  }
// [Observed Error] an implementation is missing
//   [exception was thrown] detailed error message in debug output section below
// [Lost Points] 2

test("Future.continue should handle exceptions thrown by the user specified continuation function") {
    assert(false)
  }
// [Observed Error] an implementation is missing
//   [exception was thrown] detailed error message in debug output section below
// [Lost Points] 2

test("Server should serve requests properly by using the exchange object") {
    assert(false)
  }
// [Observed Error] an implementation is missing
//   [exception was thrown] detailed error message in debug output section below
// [Lost Points] 12

test("Server should cancel a long-running or infinite response") {
    assert(false)
  }
// [Observed Error] an implementation is missing
//   [exception was thrown] detailed error message in debug output section below
// [Lost Points] 12



/**
 * Original provided tests
 */
  
  
  
  test("A Future should always be completed") {
    val always = Future.always(517)

    assert(Await.result(always, 0 nanos) == 517)
  }
  test("A Future should never be completed") {
    val never = Future.never[Int]

    try {
      Await.result(never, 1 second)
      assert(false)
    } catch {
      case t: TimeoutException => // ok!
    }
  }

  
  
  class DummyExchange(val request: Request) extends Exchange {
    @volatile var response = ""
    val loaded = Promise[String]()
    def write(s: String) {
      response += s
    }
    def close() {
      loaded.success(response)
    }
  }

  class DummyListener(val port: Int, val relativePath: String) extends NodeScala.Listener {
    self =>

    @volatile private var started = false
    var handler: Exchange => Unit = null

    def createContext(h: Exchange => Unit) = this.synchronized {
      assert(started, "is server started?")
      handler = h
    }

    def removeContext() = this.synchronized {
      assert(started, "is server started?")
      handler = null
    }

    def start() = self.synchronized {
      started = true
      new Subscription {
        def unsubscribe() = self.synchronized {
          started = false
        }
      }
    }

    def emit(req: Request) = {
      val exchange = new DummyExchange(req)
      if (handler != null) handler(exchange)
      exchange
    }
  }

  class DummyServer(val port: Int) extends NodeScala {
    self =>
    val listeners = mutable.Map[String, DummyListener]()

    def createListener(relativePath: String) = {
      val l = new DummyListener(port, relativePath)
      listeners(relativePath) = l
      l
    }

    def emit(relativePath: String, req: Request) = this.synchronized {
      val l = listeners(relativePath)
      l.emit(req)
    }
  }
  test("Server should serve requests") {
    val dummy = new DummyServer(8191)
    val dummySubscription = dummy.start("/testDir") {
      request => for (kv <- request.iterator) yield (kv + "\n").toString
    }

    // wait until server is really installed
    Thread.sleep(500)

    def test(req: Request) {
      val webpage = dummy.emit("/testDir", req)
      val content = Await.result(webpage.loaded.future, 1 second)
      val expected = (for (kv <- req.iterator) yield (kv + "\n").toString).mkString
      assert(content == expected, s"'$content' vs. '$expected'")
    }

    test(immutable.Map("StrangeRequest" -> List("Does it work?")))
    test(immutable.Map("StrangeRequest" -> List("It works!")))
    test(immutable.Map("WorksForThree" -> List("Always works. Trust me.")))

    dummySubscription.unsubscribe()
  }

}




