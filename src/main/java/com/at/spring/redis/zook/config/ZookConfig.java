package com.at.spring.redis.zook.config;


import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ZookConfig {

    ZooKeeper zk;


    @Before
    public void conn(){
        zk = ZkUtils.getZK();
    }

    @After
    public void close(){
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tt() throws Exception{

        WatchCallBack watchCallBack = new WatchCallBack();

//        zk.exists("/AppConf", new Watcher() {
////            @Override
////            public void process(WatchedEvent event) {
////
////            }
////        }, new AsyncCallback.StatCallback(){
////            @Override
////            public void processResult(int rc, String path, Object ctx, Stat stat) {
////                if(stat != null){
////                    zk.getData()
////                }
////            }
////        },"ABC");

        watchCallBack.setZk(zk);
        MyConf myConf = new MyConf();
        watchCallBack.setMf(myConf);

        //
        watchCallBack.await();

        // 1 节点不存在
        // 2 节点存在

        while (true){
            if("".equals(myConf.getConf())){
                System.out.println("conf diu le ---");
                watchCallBack.await();
            }
            else {
                System.out.println(myConf.getConf());
            }
            Thread.sleep(300);
        }






    }





}
