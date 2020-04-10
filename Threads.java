import java.util.concurrent.locks.ReentrantLock;
import static java.lang.Thread.*;

/*
Operating Systems CISC 3320 EM6  HW#3 Kevin Lin ID#23429248
*/

/**
 * We must implement Runnable class in order for multiple threads to run.
 * Runnable class has an abstract method called run, that we must inherit.
 */
public class Threads implements Runnable {
    /**
     * threadTime is a random number initialized by the Math.random method.
     * pid is instantiated in order to call methods in the Pid class
     * masterLock is an instantiated ReentrantLock object that only allows one thread to run;
     * it unlocks the lock for other threads to use, once the lock is released.
     */
    private int threadTime;
    private Pid pid;
    private ReentrantLock masterLock;

    /**
     * The constructor accepts a certain time for the thread to sleep and instantiates a ReentrantLock object.
     * @param t the user inputs on the amount of time the thread will sleep
     * @param p accepts a Pid object so that multiple threads can allocate memory
     */
    public Threads(int t, Pid p){
        threadTime=t;
        this.pid=p;
        masterLock=new ReentrantLock();//instantiate ReentrantLock class
    }

    /**
     * The run method allows a thread to allocate a pid and locks other threads.
     * Once a thread acquires a lock, no other thread can allocate a pid and they will have to wait.
     * It only allows one thread to run because of the ReentrantLock class. We can instantiate multiple
     * ReentrantLock objects to allow multiple threads to run.
     * It would catch any exceptions, unlock the current thread for other threads to run,
     * and then sleep(based on user input or a random number) for a while to release the pid.
     */
    @Override
    public void run() {
        int Pid = 0;//sets pid to zero and will turn into a different number once pid is allocated
        System.out.println(Thread.currentThread().getName());
        try{//try catch and finally block, allocates pid and locks other threads. Then releases it in finally block once pid is allocated
            masterLock.lock();// locks until pid is allocated
            Pid=pid.allocate_pid();
            System.out.println("Master Lock acquired.");
            while(Pid==-1){
                System.out.println("All pids are occupied");
                Pid=pid.allocate_pid();
            }
        }
        catch (Exception e){
            e.toString();
        }
        finally {//finally block will always run whether there is an exception or not.
            masterLock.unlock();//unlocks thread so that other threads can allocate memory
        }
        Thread.currentThread().setName(""+Pid);//Thread sets name to the pid number, in order to release later.

        try{
            Thread.sleep(threadTime);// thread sleeps for a while based on random number or user input
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName()+" is interrupted.");
        }
        int pidrelease =Integer.valueOf(Thread.currentThread().getName());//Thread returns the current name of the thread(which is pid number), in order to release the pid.
        pid.release_pid(pidrelease);
        System.out.println("Pid " + Thread.currentThread().getName() + " exiting.");
    }

    /**
     * The main method instantiates a pid and then allocates a map. The Thread class instantiates 100 threads
     * and sleeps at a random moment. It will accept a Pid object inorder to allocate memory.
     */
    public static void main(String[] args){
        Pid pid=new Pid();
        pid.allocate_map();
        for (int i=0;i<=100;i++) {
            Thread thread = new Thread(new Threads( (int) Math.random() * 100 + 1, pid));//initializes a certain time and amount of locks
            thread.start();
        }
    }
}
