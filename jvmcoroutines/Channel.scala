import scala.collection.mutable.SynchronizedQueue
import scala.collection.mutable.Queue
import scala.collection._
import scala.concurrent._
import scala.util.continuations._
import ExecutionContext.Implicits.global

class Channel[T] {
  
  private var receivers = new Queue[T => Any]
  private var senders = new Queue[(T => Any) => Any]
  private var m: Mutex = new Mutex 
  
  def nop(): Unit = {}
  
  /*
   * Places a value in the buffer and blocks if necessary
   */
  def send(item: T): Any @cps[Any] = {
	    m.lock
	    var receiver = shift { 
	    	sender: ((T => Any) => Any) => {
		    	  //if there is someone listening and we are sending, then run the 
	    		  //receivers code and the senders code
		    	  //else the rest of the senders code in the queue
	    	  
		    	  if(!receivers.isEmpty) {
		    		  	var received = receivers.dequeue()
		    		  	m.unlock
		    		    sender(received)
		    	  } else {
		    		    senders.enqueue(sender)
		    		    m.unlock
		    	  }
		    	  nop()
	    	}
	    }
    
	    // is the code that is in the sender queue and everything after that is in senders code
	    Scheduler.schedule(() => receiver(item))
	    
	    return
  }
  
  /*
   * Pulls the first value from the buffer
   */
  def receive(): T @cps[Any]= {
	   m.lock
	   var sender = shift { 
		      receiver: (T => Any) => {
			    	//if we are receiving and someone is talking, then run the senders 
		    	  	//code --> which is going to run the receivers code
				    if(!senders.isEmpty) {
				      var sent = senders.dequeue()
				      m.unlock
				      sent(receiver)
				    } else {
				      receivers.enqueue(receiver)
				      m.unlock
				    }
				    nop()
		      }
	   }
	   //everything that happens after the receive is in the receive queue
	   return sender
  }
}
