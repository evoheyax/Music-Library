package MusicLibrary;
import java.util.HashMap;

/**
 * A read/write lock that allows multiple readers, disallows multiple writers, and allows a writer to 
 * acquire a read lock while holding the write lock. 
 * 
 */
public class ReentrantLock {

	//Declare data members here!
//TODO: instance variables should be private
	private HashMap<Long, Integer> reads;
	private HashMap<Long, Integer> writes;
	
	/**
	 * Construct a new ReentrantLock.
	 */
	public ReentrantLock() {
		this.reads = new HashMap<>();
		this.writes = new HashMap<>();
	}

	/**
	 * Returns true if the invoking thread holds a read lock.
	 * @return
	 */
	public synchronized boolean hasRead() {
		return this.reads.containsKey(Thread.currentThread().getId());
	}

	/**
	 * Returns true if the invoking thread holds a write lock.
	 * @return
	 */
	public synchronized boolean hasWrite() {
		return this.writes.containsKey(Thread.currentThread().getId());
	}

	/**
	 * Non-blocking method that attempts to acquire the read lock.
	 * Returns true if successful.
	 * @return
	 */
	public synchronized boolean tryLockRead() {
		if(this.writes.size() == 0 || this.hasWrite()) {
			int newCount = 1;
			if(this.hasRead()) {
				newCount = this.reads.get(Thread.currentThread().getId())+1;
			}
			this.reads.put(Thread.currentThread().getId(), newCount);
			//System.out.println(Thread.currentThread().getId()+" has gained the read lock!");
			return true;
		}
		return false;
	}

	/**
	 * Non-blocking method that attempts to acquire the write lock.
	 * Returns true if successful.
	 * @return
	 */	
	public synchronized boolean tryLockWrite() {
		if((this.writes.size() == 0 && this.reads.size() == 0) || this.hasWrite()) {
			int newCount = 1;
			if(this.hasWrite()) {
				newCount = this.writes.get(Thread.currentThread().getId())+1;
			}
			this.writes.put(Thread.currentThread().getId(), newCount);
			//System.out.println(Thread.currentThread().getId()+" has gained the write lock!");
			return true;
		}
		return false;
	}

	/**
	 * Blocking method that will return only when the read lock has been 
	 * acquired.
	 */	 
	public synchronized void lockRead() {
		while(!tryLockRead()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Releases the read lock held by the calling thread. Other threads may continue
	 * to hold a read lock.
	 */
	public synchronized void unlockRead() {
		if(this.hasRead()) {
			if(this.reads.get(Thread.currentThread().getId()) > 1) {
				int newCount = this.reads.get(Thread.currentThread().getId())-1;
				this.reads.put(Thread.currentThread().getId(), newCount);
			} else {
				this.reads.remove(Thread.currentThread().getId());
			}
			//System.out.println(Thread.currentThread().getId()+" has given up the read lock!");
		}
		notifyAll();
	}

	/**
	 * Blocking method that will return only when the write lock has been 
	 * acquired.
	 */
	public synchronized void lockWrite() {
		while(!tryLockWrite()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Releases the write lock held by the calling thread. The calling thread may continue to hold
	 * a read lock.
	 */
	public synchronized void unlockWrite() {
		if(this.hasWrite()) {
			if(this.writes.get(Thread.currentThread().getId()) > 1) {
				int newCount = this.writes.get(Thread.currentThread().getId())-1;
				this.writes.put(Thread.currentThread().getId(), newCount);
			} else {
				this.writes.remove(Thread.currentThread().getId());
			}
			//System.out.println(Thread.currentThread().getId()+" has given up the write lock!");
		}
		notifyAll();
	}
}
