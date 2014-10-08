import scala.concurrent._
import scala.util.continuations._
import ExecutionContext.Implicits.global

object InterThreadDemo2 {
	def main(args: Array[String]) {
	  
	  Thread.currentThread().setName("MAIN")
	  
	  ////////////DEMO ON 2 THREADS
	   var ch = new Channel[Int]
	    Go spawn(6)
	    
	    var count = 0
	    for(count <- 1 to 10) {
	      reset{
	       println("SENDING FROM " + Thread.currentThread().getName())
	       ch.send(count)
	       println("SENT COMPLETED ON " + Thread.currentThread().getName())
	      }
	    }
	    
	    
	     var count2 = 0
	     while(count2 < 10) {
	         count2 = count2 + 1
	         Go co {
	    	  ch.receive()
	    	  println("RECEIVED ON " + Thread.currentThread().getName())
	         }
	      
	      }
	     Thread.sleep(1)
	     Scheduler.waitYield
	     
	     Thread.sleep(2000)
	}
}