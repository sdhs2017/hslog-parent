package com.jz.bigdata.business;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.joda.time.DateTime;

import java.util.Date;

public enum  FlagTest {
    INSTANCE;
    //定义全局变量，开关，默认关闭状态
    private Boolean flag = false;

    public synchronized Boolean getFlag() {
        return flag;
    }

    public synchronized void setFlag(Boolean flag) {
        this.flag = flag;
    }
    //点击开启,参数mark作为一个标记，定位是哪个线程进行的操作
    public synchronized void startFlag(String mark) throws InterruptedException {
        System.out.println(DateTime.now().toString()+"---"+mark+"---开始开启");
        //使用synchronized修饰变量，可以为变量加锁，执行完成后开锁
        synchronized (flag){
            System.out.println(DateTime.now().toString()+"---"+mark+"---开关flag已锁定---");
            //设置延迟时间，这步可以代替开启kafka的操作，保证开启kafka和设开关赋值有明显停顿，方便测试
            Thread.sleep(5000);
            if(flag==true){
                System.out.println(DateTime.now().toString()+"---"+mark+"---flag已开启，不需要重复开启");
            }else{
                setFlag(true);
                System.out.println(DateTime.now().toString()+"---"+mark+"---开关flag释放锁定---");
                System.out.println(DateTime.now().toString()+"---"+mark+"---开启成功");
            }
        }
    }
    //点击关闭
    public void stopFlag() throws InterruptedException {

        System.out.println(DateTime.now().toString()+"---开始关闭");
        synchronized (flag){
            Thread.sleep(5000);
            if(flag==false){
                System.out.println(DateTime.now().toString()+"---flag已关闭");
            }else{
                setFlag(true);
            }
        }
        System.out.println(DateTime.now().toString()+"---关闭成功");
    }

    public static void main(String[] args){
        //开启两个线程测试
        Thread t1 =new Thread(new FlagRunable("线程1")) ;
        t1.start();
        Thread t2 =new Thread(new FlagRunable("线程2")) ;
        t2.start();
    }
}
