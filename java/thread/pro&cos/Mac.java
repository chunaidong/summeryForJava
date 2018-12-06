package consumer;

/**
 * <pre>
 *    @author  : wangchun
 *    @time    : 2018/12/6 8:57
 *    desc    : 目标产品
 *    version : v1.0
 * </pre>
 */
public class Mac {
    /**
     * 货品数量
     */
    private  int count;
    /**
     * 生产最大数量
     */
    private final int MAX_COUNT = 10;

    /**
     * 生产
     */
    public synchronized void make() {
        if(count > MAX_COUNT){
            System.out.println("库存数量已经达到最大值了: "+Thread.currentThread().getName()+" 停止生成");
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("库存数量为: "+count+" ==="+Thread.currentThread().getName()+" 开始生成");
            count++;
            notifyAll();
        }
    }

    /**
     * 消费
     */
    public synchronized void take(){
        if (count <=0){
            System.out.println("库存数量已经消耗完: "+Thread.currentThread().getName()+" 停止消费");
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("库存数量为: "+count+" ==="+Thread.currentThread().getName()+" 开始消费");
            count--;
            notifyAll();
        }
    }
}
