package tutorialspoint.javaBasic;

class ThreadA extends Thread {
	private Thread t;
	private String threadName;
	
	public ThreadA(String name){
		threadName = name;
	}
	
	// Override, need to set public
	public void run(){
		System.out.println("run thread " + threadName);
		
		
		try {
			// sleep to see other thread
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.out.println("exit thread " + threadName);
		}
		
	}
	
	// Override, need to set public
	public void start(){
		System.out.println("Starting thread " + threadName);
		// if Override this method, we need to start thread t.
		// if not, run method won't run
		// 3. need to indicate "this" to execute run method of this class
		if(t==null){
			t = new Thread(this, threadName);
			t.start();
		}
	}
}


public class Test{
	
	public static void main(String arg[]){
		ThreadA a = new ThreadA("A");
		a.start();
		
		ThreadA b = new ThreadA("B");
		b.start();
	}
}
