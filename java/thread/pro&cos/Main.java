package consumer;

/**
 * <pre>
 *    @author  : wangchun
 *    @time    : 2018/12/6 9:10
 *    desc    : 输入描述
 *    version : v1.0
 * </pre>
 */
public class Main {

    public static void main(String[] args){
        Mac mac = new Mac();
        Producer producer = new Producer(mac);
        Consumer consumer = new Consumer(mac);
        Thread t1 = new Thread(producer);
        Thread t2 = new Thread(producer);
        Thread t3 = new Thread(producer);
        Thread t4 = new Thread(producer);
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        Thread t5 = new Thread(consumer);
        Thread t6 = new Thread(consumer);
        Thread t7 = new Thread(consumer);
        Thread t8 = new Thread(consumer);
        t5.start();
        t6.start();
        t7.start();
        t8.start();

    }

}
