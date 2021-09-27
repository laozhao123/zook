package com.at.spring.redis.zook.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class WatchCallBack implements Watcher, AsyncCallback.StringCallback , AsyncCallback.Children2Callback, AsyncCallback.StatCallback {

    private static Logger log = Logger.getLogger("WatchCallBack");

    ZooKeeper zk;
    private String threadName;
    CountDownLatch cc = new CountDownLatch(1);

    public CountDownLatch getCc() {
        return cc;
    }

    public void setCc(CountDownLatch cc) {
        this.cc = cc;
    }

    String pathName;

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }


    public void tryLock(){
        try {
            //重入锁
//            byte[] data = zk.getData("/lock", false, new Stat());
//            if( pathName.equals(new String(data)) ){
//
//            }
            zk.create("/lock",threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,
                    this,"abc");
            cc.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unLock(){
        try {
            System.out.println("释放锁："+pathName);
            zk.delete(pathName,-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        if(name != null ){
            pathName = name;
            System.out.println(threadName+" create node :"+name);
            //找锁
            zk.getChildren("/",false,this,"sdf");
        }
    }

    //getChildren call back
    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        // 创建完了，且看到前面的节点
//        System.out.println(threadName+" lock locks ...");
//        for (String child : children) {
//            System.out.println(child);
//        }
        Collections.sort(children);
        int i = children.indexOf(pathName.substring(1));
        // 1 是不是第一个
        if(i == 0){
            System.out.println(threadName + " i am first ...");
            try {
//                zk.setData("/",threadName.getBytes(),-1); // 从入锁
                cc.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else{
            // 2 不是第一个
            System.out.println("我 "+pathName+" 监听 ："+"/"+children.get(i-1));
            zk.exists("/"+children.get(i-1),this,this,"sdf");
        }

    }

    //不是第一个的回调
    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        // 偷懒

    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                log.info("监听到 删除了 锁："+event.getPath());
                zk.getChildren("/",false,this,"sdf"); //重新找锁
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
        }
    }
}
