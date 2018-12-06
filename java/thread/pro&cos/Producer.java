package consumer;

/**
 * <pre>
 *    @author  : wangchun
 *    @time    : 2018/12/6 9:07
 *    desc    : 生产者
 *    version : v1.0
 * </pre>
 */
public class Producer implements Runnable {


    private Mac mac;

    public Producer (Mac mac){
        this.mac = mac;
    }

    @Override
    public void run() {
        while (true){
            mac.make();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
