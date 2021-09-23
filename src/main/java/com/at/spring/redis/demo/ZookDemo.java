package com.at.spring.redis.demo;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class ZookDemo {

    public static void main(String[] args) throws Exception{
        tt();

    }



    public static void tt() throws Exception{

        CountDownLatch cd = new CountDownLatch(1);

        // watch 观察回调
        // 1： new Zookeeper 传入的watch session级别 跟path node 没关系
        ZooKeeper zk = new ZooKeeper("192.168.175.102:2181,192.168.175.103:2181,192.168.175.104:2181"
                , 3000,
                new Watcher() {
                    //回调方法
                    @Override
                    public void process(WatchedEvent event) {
                        Event.KeeperState state = event.getState();
                        Event.EventType type = event.getType();
                        String path = event.getPath();
                        System.out.println("new zk  "+event.toString());
                        switch (state) {
                            case Unknown:
                                break;
                            case Disconnected:
                                break;
                            case NoSyncConnected:
                                break;
                            case SyncConnected:
                                System.out.println("车工");
                                cd.countDown();
                                break;
                            case AuthFailed:
                                break;
                            case ConnectedReadOnly:
                                break;
                            case SaslAuthenticated:
                                break;
                            case Expired:
                                break;
                        }
                        switch (type) {
                            case None:
                                break;
                            case NodeCreated:
                                break;
                            case NodeDeleted:
                                break;
                            case NodeDataChanged:
                                break;
                            case NodeChildrenChanged:
                                break;
                        }

                    }
                });

        cd.await();

        ZooKeeper.States state = zk.getState();
        switch (state) {
            case CONNECTING:
                System.out.println("ing.....");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("ed.....");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }

        String pathName = zk.create("/ooxx", "olddata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        Stat stat = new Stat();
        byte[] node = zk.getData("/ooxx", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("get data"+event.toString());
                try {
                    // true default watch 被重新注册  new zk 那个watch
                    // this // 使用当前watch
                    byte[] data = zk.getData("/ooxx", this, stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, stat);
        System.out.println(new String(node));

        //触发回调
        Stat stat1 = zk.setData("/ooxx", "newData".getBytes(), 0);
        Stat stat2 = zk.setData("/ooxx", "newData01".getBytes(), stat1.getVersion());


        // 异步回调
        System.out.println("----async start  -----");
        zk.getData("/ooxx", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println("----async call back  -----");
                System.out.println(ctx.toString());
                System.out.println(new String(data));
            }
        },"abc");

        System.out.println("----async over -----");



        Thread.sleep(222222222);

    }
}
