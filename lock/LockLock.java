import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <pre>
 *    @author  : wangchun
 *    @time    : 2018/12/4 11:09
 *    desc    : Lock锁
 *    version : v1.0
 * </pre>
 */
public class LockLock {

    private int value;

    public int getValue(){
        lock.lock();
        value++;
        lock.unlock();
        return value;
    }

    private Lock lock  = new ReentrantLock();

    public static void main(String[] args){

        LockLock lock = new LockLock();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        //使用jdk8的函数表达方式 想当于 new Runanbel(new)
        for (int i = 0 ; i < 1000 ; i++){
            executorService.execute(()->{
                System.out.println(Thread.currentThread().getName()+"::::"+lock.getValue());
            });
        }
        executorService.shutdown();
    }

}
