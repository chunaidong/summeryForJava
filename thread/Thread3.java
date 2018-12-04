import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * <pre>
 *    @author  : wangchun
 *    @time    : 2018/12/4 14:21
 *    desc    : 输入描述
 *    version : v1.0
 * </pre>
 */
public class Thread3 {


    public static void main(String[] args){

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
            }
        });

        executorService.shutdown();
    }

}
