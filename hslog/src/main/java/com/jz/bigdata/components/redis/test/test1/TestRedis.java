package com.jz.bigdata.components.redis.test.test1;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jz.bigdata.business.health.test.book.entity.Book;


/**
 * redis spring 简单例子
 * @author hk
 *
 * 2012-12-22 上午10:40:15
 */
public class TestRedis {
	public static ApplicationContext app = new ClassPathXmlApplicationContext("classpath:redis-context1.xml");

    public static void main(String[] args) throws InterruptedException {
//    	ApplicationContext app = new ClassPathXmlApplicationContext("classpath:redis-context1.xml");
    	  //这里已经配置好,属于一个redis的服务接口
	    RedisService redisService = (RedisService) app.getBean("redisService");
   
        String ping = redisService.ping();//测试是否连接成功,连接成功输出PONG
        System.out.println(ping);

        //首先,我们看下redis服务里是否有数据
        long dbSizeStart = redisService.dbSize();
//        System.out.println(dbSizeStart);
//        
        String id = new Random().nextInt(10000) + "";  
        Map<String, String> map = new HashMap<String, String>();  
        map.put("id","2876");  
        map.put("age", new Random().nextInt(70) + "");  
        map.put("name", "张三");  
              
        redisService.rpush("personid", "2876");        // 保存用户id  
              
        redisService.hmset("person:" + "2876", map);   // 保存用户信息  
        
        redisService.hgetAll("person:" + id);
        System.out.println(redisService.hgetAll("person:" + id));
        
       
//        redisService.del("personid:" +2876);  
//        System.err.println(redisService.lpush("personid",id));
        System.err.println(redisService.get("personid"));
//        System.err.println(redisService.llen("personid"));
        
        List<String> idList = redisService.lrange("personid", 0, 10);  
        for(String ids : idList){  
            System.out.println(redisService.hgetAll("person:" + ids));  
        }  

//        redisService.set("username", "oyhk");//设值(查看了源代码,默认存活时间30分钟)
//        Object username = redisService.get("username");//取值 
//        System.out.println(username.toString());
//        redisService.set("username1", "oyhk1", 1);//设值,并且设置数据的存活时间(这里以秒为单位)
//        Object username1 = redisService.get("username1");
//        System.out.println(username1);
//        Thread.sleep(2000);//我睡眠一会,再去取,这个时间超过了,他的存活时间
//        Object liveUsername1 = redisService.get("username1");
//        System.out.println(liveUsername1);//输出null
//
//        //是否存在
//        boolean exist = redisService.exists("username");
//        System.out.println(exist);
//
//        
//
//        //删除
//        redisService.set("username2", "oyhk2");
//        Object username2 = redisService.get("username2");
//        System.out.println(username2);
//        redisService.del("username2");
//        Object username2_2 = redisService.get("username2");
//        System.out.println(username2_2);//如果为null,那么就是删除数据了
//        
//        Book book =new Book();
//        book.setId("1");
//        book.setDate("1922-03-02");
//        book.setBookName("qqqq");
//        book.setWriter("sss");
//        redisService.set(book.getId().getBytes(), serialize(book));
//        byte[] byt=redisService.get(book.getId().getBytes());
//        Object obj=unserizlize(byt);
//        if(obj instanceof Book){
//            System.out.println(((Book) obj).getId());
//        }
        //查看keys
        Set<String> keys = redisService.keys("*");//这里查看所有的keys
//        String[] aa=keys.toString().split(",");
//        Iterator<String> it = keys.iterator();
//        while(it.hasNext()){
//        System.out.println(it.next());
//    }
////        for(int i=0;i<aa.length;i++){
////        	System.err.println(aa[i]+"-------------");
////        }
        System.out.println(keys);//只有username username1(已经清空了)
//
//        //dbsize
//        long dbSizeEnd = redisService.dbSize();
//        System.out.println(dbSizeEnd);

        //清空reids所有数据
//        redisService.flushDB();
    }

    //序列化 
    public static byte [] serialize(Object obj){
        ObjectOutputStream obi=null;
        ByteArrayOutputStream bai=null;
        try {
            bai=new ByteArrayOutputStream();
            obi=new ObjectOutputStream(bai);
            obi.writeObject(obj);
            byte[] byt=bai.toByteArray();
            return byt;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //反序列化
    public static Object unserizlize(byte[] byt){
        ObjectInputStream oii=null;
        ByteArrayInputStream bis=null;
        bis=new ByteArrayInputStream(byt);
        try {
            oii=new ObjectInputStream(bis);
            Object obj=oii.readObject();
            return obj;
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        return null;
    }
}