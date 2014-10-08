import scala.collection.mutable.SynchronizedQueue
import scala.collection._
import scala.concurrent._
import scala.util.continuations._
import java.util.concurrent.Semaphore


object Scheduler {
	private var jobs = new SynchronizedQueue[() => Unit]
	private var sema = new Semaphore(0)
	
	/*
	 * Places a coroutine in the buffer
	 */
	def schedule(job: () => Unit): Unit = {
	    jobs.enqueue(job)
	    sema.release()
	    return
	}
	
	/*
	 * Schedules the next job on the available resource
	 */
	def waitYield: Unit = {
	  while(true) {
	    sema.acquire()
		var job = jobs.dequeue()
		job()
	  }
	}
}