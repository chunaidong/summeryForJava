package com.example.demo.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.CountDownLatch;

public class ZookeeperClient {

    @Autowired
    private CuratorFramework client;

    private final static String LOCK_PROJECT = "wang-lock";

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Value("${lock.path}")
    private String lockPath;

    public void init(){
        client = client.usingNamespace("wangchun-test");

        try {
            if(client.checkExists().forPath("/" + LOCK_PROJECT) == null){
                client.create()
                      .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                      .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                      .forPath("/" + LOCK_PROJECT+"/"+lockPath);
            }
            addWatcherToLock("/" +LOCK_PROJECT+"/"+lockPath);
        } catch (Exception e) {
            System.out.println("连接错误");
            e.printStackTrace();
        }
    }

    public  void addWatcherToLock(String s) throws Exception {
        final PathChildrenCache childrenCache = new PathChildrenCache(client,s,true);
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                if(pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)){
                    String path = pathChildrenCacheEvent.getData().getPath();
                    System.out.println("======"+path+"==========");
                    if(path.contains(lockPath)){
                        System.out.println("======[][][][]==========");
                        countDownLatch.countDown();
                    }
                }
            }
        });
    }

    public void getLock(String id){
      while (true){
          try {
              client.create().creatingParentsIfNeeded()
                      .withMode(CreateMode.EPHEMERAL)
                      .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                      .forPath("/" + LOCK_PROJECT +"/" +lockPath +"/" +id);
              return;
          }catch (Exception e){
              System.out.println("获取锁失败!!!!!");
              try {
                  if(countDownLatch.getCount() <=0){
                      countDownLatch = new CountDownLatch(1);
                  }
                  countDownLatch.await();
              }catch (Exception ee){

              }
          }
      }


    }
    public boolean releaseLock(String s){
        try {
            if(client.checkExists().forPath("/"+ LOCK_PROJECT +"/" +lockPath+"/"+s) !=null){
                client.delete().forPath("/"+ LOCK_PROJECT +"/" +lockPath+"/"+s);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
