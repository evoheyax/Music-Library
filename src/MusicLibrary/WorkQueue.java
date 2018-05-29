package MusicLibrary;
import java.util.LinkedList;

public class WorkQueue {
    private final int nThreads;
    private final PoolWorker[] threads;
    private final LinkedList queue;
    private volatile boolean shutdownRequested;

    public void shutdown() {
    	shutdownRequested = true;
    	synchronized(queue) {
    		queue.notifyAll();
    	}
    }
    
    public WorkQueue(int nThreads) {
        this.nThreads = nThreads;
        queue = new LinkedList();
        threads = new PoolWorker[nThreads];

        for (int i=0; i<nThreads; i++) {
            threads[i] = new PoolWorker();
            threads[i].start();
        }
    }

    public void execute(Runnable r) {
//TODO: only execute if not shutdown. ---- done ----
    	if(!shutdownRequested) {
	        synchronized(queue) {
	            queue.addLast(r);
	            queue.notifyAll();
	        }
    	}
    }
    
    public void awaitTermination() {
    	for (int i=0; i<nThreads; i++) {
	    	try {
				threads[i].join();
			} catch (InterruptedException e) {
				System.err.println("Error joining threads!");
			}
    	}
    }

    private class PoolWorker extends Thread {
        public void run() {
            Runnable r;

            while (true) {
            	
                synchronized(queue) {
                    while (queue.isEmpty() && !shutdownRequested) {
                        try
                        {
                            queue.wait();
                        }
                        catch (InterruptedException ignored)
                        {
                        }
                    }
                    
                    if(shutdownRequested && queue.isEmpty()) {
//TODO: this needs to be moved elsewhere ---- done ----              	
	                	//queue.notifyAll();
	                	break;
	                }

                    r = (Runnable) queue.removeFirst();
                }

                // If we don't catch RuntimeException, 
                // the pool could leak threads
                try {
                    r.run();
                }
                catch (RuntimeException e) {
                    // You might want to log something here
                }
            }
        }
    }
}
