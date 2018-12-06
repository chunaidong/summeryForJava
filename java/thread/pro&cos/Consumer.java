package consumer;

/**
 * <pre>
 *    @author  : wangchun
 *    @time    : 2018/12/6 9:08
 *    desc    : 消费者
 *    version : v1.0
 * </pre>
 */
public class Consumer implements Runnable {


    private Mac mac;

    public Consumer(Mac mac){
        this.mac = mac;
    }

    @Override
    public void run() {
        while (true){
            mac.take();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
