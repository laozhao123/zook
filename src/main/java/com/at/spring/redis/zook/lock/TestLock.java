package com.at.spring.redis.zook.lock;

import com.at.spring.redis.zook.config.ZkUtils;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLock {

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
    public void lock() throws Exception{

        for (int i = 0; i <10 ; i++) {
            new Thread(){
                @Override
                public void run() {
                    // 每个线程

                    WatchCallBack watchCallBack = new WatchCallBack();
                    watchCallBack.setZk(zk);
                    String name = Thread.currentThread().getName();
                    watchCallBack.setThreadName(name);
                    //枪锁
                    watchCallBack.tryLock();

                    //干活
                    System.out.println(name+ " working ,,,");
                    //释放锁
                    watchCallBack.unLock();
                }
            }.start();
            Thread.sleep(100
            );
        }



        while (true){

        }

    }



}
