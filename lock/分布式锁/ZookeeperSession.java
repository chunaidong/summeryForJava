package net.jndzkj.supplier.service.utils;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * <pre>
 *    @author  : wangchun
 *    @time    : 2018/3/6 11:29
 *    desc    : 分布式锁帮助类
 *      我们通过去创建zk的一个临时node,来模拟给某一个商品id加锁
 *      zk会给你保证说,只会创建一个临时node,其他请求过来如果再要创建临时node,就会报错,NodeExistsException
 *      那么所以说,我们的所谓上锁,其实就是去创建某个商品对应的一个临时Node
 *      如果临时node创建成功了,那么说明我们成功加锁了，在操作对应业务数据库时，就不需要等待
 *      如果临时node创建失败了,说明有人已经拿到锁了,在继续操作业务时,就要不断的等待，等到拿到锁为止
 *
 *
 *    version : v1.0
 * </pre>
 */
public class ZookeeperSession {

    private Logger log = LoggerFactory.getLogger(ZookeeperSession.class);

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private ZooKeeper zooKeeper;
    /**zookeeper服务器地址
     * zkConnectAddress 一般通过配置获取
     */
    private String zkConnectAddress = "121.43.39.85:2181";
    /**连接超时时间
     * sessionTime 一般通过配置获取
     */
    private int sessionTime = 50000;

    public ZookeeperSession(){
        //去连接zookeeper server ,创建会话的时候，是异步去进行的
        //所以要给一个监听器,说告诉我们什么时候才是真正完成了跟zk server的连接
        try {
            this.zooKeeper = new ZooKeeper(zkConnectAddress, sessionTime,new ZookeeperWatcher());
            log.info(String.valueOf(zooKeeper.getState()));
            try {
                //CountDownLatch
                //java多线程并发同步的一个工具类
                //会传递一些数字，比如1，2，3
                //然后await(),如果数字不是0，那么就会卡住，等待

                //其他的线程可以调用countDown(),减1
                //如果数字减到0，那么之前所有再await的线程，都会逃出阻塞的状态
                //继续向下执行
                countDownLatch.await();
                System.out.println("--------成功连接---------");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取分布式锁
     * @param id
     */
    public void acquireDistributedLock(String id){
        String path = "/product-lock-"+id;

        try {
            System.out.println(Thread.currentThread().getName()+"--------》》获取分布式锁《《------- id=["+id+"]");
            zooKeeper.create(path,"".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println(Thread.currentThread().getName()+"--------》》获取分布式锁成功《《------- id=["+id+"]");
        }catch (Exception e){
            //如果该商品对应的锁的node,已经存在了,就是别人已经被别人加锁，那么就这里会报错
            //NodeExistsException
            int count = 0;
            while (true){
                try {
                    //1秒 尝试两次
                    Thread.sleep(2000);
                    zooKeeper.create(path,"".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                }catch (Exception e1){
                    count++;
                    continue;
                }
                System.out.println(Thread.currentThread().getName()+"重试"+count+"次之后，新的线程获取到分布式锁id：["+id+"]");
                break;
            }
        }
    }

    /**
     * 释放分布式锁
     * @param id
     */
   public void releaseDistributedLock(String id){
       String path = "/product-lock-"+id;
       try {
           zooKeeper.delete(path,-1);
           System.out.println(Thread.currentThread().getName()+"--------删除成功---------");
       }catch (Exception e){

       }
   }

    private  class ZookeeperWatcher implements Watcher{

        @Override
        public void process(WatchedEvent watchedEvent) {
            if (Event.KeeperState.SyncConnected == watchedEvent.getState()){
                countDownLatch.countDown();
                System.out.println("--------监测成功---------");
            }
        }
    }
    /**
     * 封装单例的静态内部类
     */
   private static class Singleton{

       private  static ZookeeperSession instance;

       static {
           instance = new ZookeeperSession();
       }

       public static ZookeeperSession getInstance(){
           return instance;
       }
   }
    /**
     * @author wangchun
     * @time 2018/3/6 11:34
     * @method getInstance
     * @return net.jndzkj.supplier.service.utils.ZookeeperSession
     * @version V1.0
     * @description 获取单例
     */
   public static ZookeeperSession getInstance(){
       return Singleton.getInstance();
   }
    /**
     * @author wangchun
     * @time 2018/3/6 11:34
     * @method init
     * @version V1.0
     * @description 初始化单例数据
     */
   public static void init(){
       getInstance();
   }

   public static void main(String[] args){
       //业务代码获取分布式锁
       final  ZookeeperSession zkSession = ZookeeperSession.getInstance();
       //业务Id
       final String id = "t11";
       for (int i =0;i<10000;i++){
           new Thread(new Runnable() {
               @Override
               public void run() {
                   zkSession.acquireDistributedLock(id);
                   try {
                       Thread.sleep(2000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   //释放锁
                   zkSession.releaseDistributedLock(id);
               }
           }
           ).start();
       }


      // zkSession.acquireDistributedLock(id);
       /**
        * 执行相应的业务操作
        */
       //释放锁
      // zkSession.releaseDistributedLock(id);
   }

}
