import scala.collection.mutable.SynchronizedQueue
import scala.collection._
import scala.concurrent._
import scala.util.continuations._

class Mutex() {
 private var buffer = new SynchronizedQueue[Unit => Unit];
 private var locked = false

 /*
 *If this critical section is locked, then the rest of the code gets packaged up 
 * as a continuation and placed in the queue. Otherwise, it gets packaged up and
 * run immediately. 
 */
 def lock: Unit @cps[Unit] = {
   var lockedCode: Unit => Unit = (Unit) => Unit
	shift {
     locker: (Unit => Unit) => {
        synchronized {
    	   if(locked) {
    	      buffer.enqueue(locker)
    	   } else {
    	      locked = true
              lockedCode = locker
           }
    	}
        lockedCode()
      }
    }	
 }
 
 
 /*
  * If the critical section ends, then the next blocked continuation is ran with 
  * the lock locked.
  */
 def unlock {
	   var nextLockedCode: Unit => Unit = (Unit) => Unit;
	   synchronized {
	       locked = false
	       if(!buffer.isEmpty) {
	    	  nextLockedCode = buffer.dequeue()
	    	  locked = true
	       }
	   }
	   nextLockedCode()
 }  
}