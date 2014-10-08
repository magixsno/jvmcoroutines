import scala.concurrent._
import scala.util.continuations._
import ExecutionContext.Implicits.global

object CoRoutDemo {
	def main(args: Array[String]) {
		Thread.currentThread().setName("MAIN")
	  
	  	///////////DEMO SINGLE THREAD
	  	var ch = new Channel[Int]
	   
	  
		Thread.currentThread().setName("MAIN")
	  	Go co {
	      println("begin receive")
	      ch.receive()
	      println("end receive")	      
	    }
	    
	  	Go co {
	      println("begin send")
	      ch.send(1)
	      println("end send")
	    }
	    
	    Scheduler.waitYield
	    
	    Thread.sleep(2000)
	}
	
}