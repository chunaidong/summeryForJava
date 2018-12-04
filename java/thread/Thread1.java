import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <pre>
 *    @author  : wangchun
 *    @time    : 2018/12/4 14:06
 *    desc    : 通过继承方式实现线程
 *    version : v1.0
 * </pre>
 */
public class Thread1 extends Thread{

    @Override
    public void run() {
       System.out.println(Thread.currentThread().getName());
    }

    public static void main(String[] args){

        Thread1 thread1 = new Thread1();
        Thread1 thread2 = new Thread1();
        Thread1 thread3 = new Thread1();
        Thread1 thread4 = new Thread1();
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }

}
