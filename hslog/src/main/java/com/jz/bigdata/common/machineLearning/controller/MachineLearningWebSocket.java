package com.jz.bigdata.common.machineLearning.controller;

import com.jz.bigdata.common.machineLearning.thread.LogThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.InputStream;

/**
 * @Author: yiyang
 * @Date: 2021/3/15 10:28
 * @Description: AI websocket 服务端
 */

@Slf4j
@Component
@ServerEndpoint("/websocket/{execute}")
public class MachineLearningWebSocket {

    /**
     * websocket 实时进行信息打印
     * @param session
     * @return
     */
    @OnOpen
    public void getWebSocketPrint(@PathParam("execute") String execute,Session session){
        try {
            String commond;
            if("train".equals(execute)){
                //训练数据-linux
                //commond = "sh /opt/ml/train.sh";
                //windows
                commond = "D:\\Anaconda3\\Scripts\\activate.bat && conda activate detection_anomalies_env && python -u E:\\work\\hs\\MachineLearning\\ADLTI_py\\detectAnomalies.py E:/work/hs/MachineLearning/files/review.yaml train";
            }else{
                //检测数据-linux
                //commond = "sh /opt/ml/detect.sh";
                //windows
                commond = "D:\\Anaconda3\\Scripts\\activate.bat && conda activate detection_anomalies_env && python -u E:\\work\\hs\\MachineLearning\\ADLTI_py\\detectAnomalies.py E:/work/hs/MachineLearning/files/review.yaml detect";
            }
            System.out.println("-------Shell ："+commond);

            Process process = Runtime.getRuntime().exec(commond);
            InputStream inputStream = process.getInputStream();
            // 一定要启动新的线程，防止InputStream阻塞处理WebSocket的线程
            LogThread thread = new LogThread(inputStream,session);
            thread.start();
        } catch (Exception e) {
            log.error("shell脚本执行异常！");
            e.printStackTrace();
        }
    }
    /**
     * WebSocket请求关闭
     */
    @OnClose
    public void onClose() {

       //TODO
    }

    @OnError
    public void onError(Throwable thr) {
        thr.printStackTrace();
    }

}
