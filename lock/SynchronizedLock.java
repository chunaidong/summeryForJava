import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <pre>
 *    @author  : wangchun
 *    @time    : 2018/12/4 10:58
 *    desc    : Synchronized同步锁 ,Synchronized为对象锁
 *    version : v1.0
 * </pre>
 */
public class SynchronizedLock {

    private int value;

    public synchronized int getValue(){
        return value++;
    }


    public static void main(String[] args){

        SynchronizedLock synchronizedLock = new SynchronizedLock();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        //使用jdk8的函数表达方式 想当于 new Runanbel(new)
        for (int i = 0 ; i < 1000 ; i++){
            executorService.execute(()->{
                System.out.println(Thread.currentThread().getName()+"::::"+synchronizedLock.getValue());
            });
        }
        executorService.shutdown();
    }


}
