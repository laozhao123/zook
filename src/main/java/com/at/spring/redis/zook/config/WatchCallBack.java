package com.at.spring.redis.zook.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class WatchCallBack implements Watcher , AsyncCallback.StatCallback, AsyncCallback.DataCallback {

    ZooKeeper zk;
    MyConf mf;
    CountDownLatch cc = new CountDownLatch(1);

    public MyConf getMf() {
        return mf;
    }

    public void setMf(MyConf mf) {
        this.mf = mf;
    }

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }


    public void await(){
        zk.exists("/AppConf",this,this,"begin ");
        try {
            cc.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        if(stat != null){
            zk.getData("/AppConf",this,this,"begin s");
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        if(data !=null){
            System.out.println("===="+ctx.toString());
            String s = new String(data);
            mf.setConf(s);
            cc.countDown();
        }
    }

    @Override
    // 监控别人 修改
    public void process(WatchedEvent event) {
//        if
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                //
                zk.getData("/AppConf",this,this,"create");
                break;
            case NodeDeleted:
                // 容忍性
                mf.setConf("");
                cc = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                //
                zk.getData("/AppConf",this,this,"change");
                break;
            case NodeChildrenChanged:
                break;
        }
    }

}
