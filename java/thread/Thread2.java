/**
 * <pre>
 *    @author  : wangchun
 *    @time    : 2018/12/4 14:17
 *    desc    : 输入描述
 *    version : v1.0
 * </pre>
 */
public class Thread2 implements Runnable {

    @Override
    public void run() {
         System.out.println(Thread.currentThread().getName());
    }

    public static void main(String[] args){
        Thread2 thread = new Thread2();
        Thread thread1 = new Thread(thread);
        Thread thread2 = new Thread(thread);
        Thread thread3 = new Thread(thread);
        Thread thread4= new Thread(thread);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }
}
