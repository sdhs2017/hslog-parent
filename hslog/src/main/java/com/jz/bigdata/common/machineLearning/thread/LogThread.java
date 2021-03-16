package com.jz.bigdata.common.machineLearning.thread;
import javax.websocket.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Author: yiyang
 * @Date: 2021/3/12 10:18
 * @Description:
 */
public class LogThread extends Thread {
    private Session session;
    private BufferedReader reader;
    public LogThread( InputStream in,Session session){
        this.reader = new BufferedReader(new InputStreamReader(in));
        this.session = session;
    };


    @Override
    public void run() {
        String line;
        try {
            while((line = reader.readLine()) != null) {
                // 将实时日志通过WebSocket发送给客户端，给每一行添加一个HTML换行
                session.getBasicRemote().sendText(line + "<br>");
                System.out.println(line + "<br>");
            }
        } catch ( IOException e) {
            e.printStackTrace();
        }
//        String line;
//        try {
//            for(int i=0;i<50;i++){
//                session.getBasicRemote().sendText("-----数据持续打印----"+i + "<br>");
//                sleep(500);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
