import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 *    @author  : wangchun
 *    @time    : 2018/12/4 11:14
 *    desc    : Aotimic原子类操作
 *    version : v1.0
 * </pre>
 */
public class AotimicLock {

    private AtomicInteger value = new AtomicInteger();

    public int getValue() {
        return value.getAndIncrement();
    }

    public static void main(String[] args){

        AotimicLock aotimicLock = new AotimicLock();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        //使用jdk8的函数表达方式 想当于 new Runanbel(new)
        for (int i = 0 ; i < 1000 ; i++){
            executorService.execute(()->{
                System.out.println(Thread.currentThread().getName()+"::::"+aotimicLock.getValue());
            });
        }
        executorService.shutdown();
    }
}
