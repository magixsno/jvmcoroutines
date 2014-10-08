import scala.concurrent._
import scala.util.continuations._
import ExecutionContext.Implicits.global

object Go {
  
	/*
	 * Keyword that spawns the number of threads to run coroutines on
	 */
	def spawn(n: Int) = {
	    var c = 0
	    for(c<- 1 to n) {
	      var thread = new Thread(new Runnable {
	        def run() {
	          Scheduler.waitYield
	        }
	      })
	      thread.start()
	    }
	  }
	  
	/*
	 * Keyword that schedules the coroutine on the next available resource
	 */
	  def co(f: => Any @cps[Any]) = {
	    Scheduler.schedule(() => reset{f})
	  }
}