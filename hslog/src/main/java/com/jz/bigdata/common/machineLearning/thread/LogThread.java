package com.jz.bigdata.common.machineLearning.thread;
import javax.websocket.Session;

import java.io.*;

/**
 * @Author: yiyang
 * @Date: 2021/3/12 10:18
 * @Description: 通过线程打印数据，防止websocket并发时出现异常
 */
public class LogThread extends Thread {
    private Session session;
    private BufferedReader reader;
    public LogThread( InputStream in,Session session) throws UnsupportedEncodingException {
        this.reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
        this.session = session;
    };


    @Override
    public void run() {
        String line;
        try {
            while((line = reader.readLine()) != null) {
                // 将实时日志通过WebSocket发送给客户端
                session.getBasicRemote().sendText(line );
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
