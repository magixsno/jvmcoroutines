import scala.concurrent._
import scala.util.continuations._
import ExecutionContext.Implicits.global

object RecBlockDemo {
	def main(args: Array[String]) {
		var ch = new Channel[Int]
		Go co {
		  println("RECEIVING")
		  var value = ch.receive()
		  println("RECEIVED: " + value)
		  println("SENDING")
		  ch.send(1)
		  println("SENT")
		}
		
		//ELSEWHERE
		Go co {
		  println("SENDING FROM ELSEWHERE")
		  ch.send(2)
		  println("SENT FROM ELSEWHERE")
		}
		Scheduler.waitYield
		Thread.sleep(2000)
	}
}