package com.jz.bigdata.common.machineLearning.controller;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.data_source.entity.DataSource;
import com.jz.bigdata.common.data_source.service.IDataSourceService;
import com.jz.bigdata.common.machineLearning.thread.LogThread;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DescribeLog;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @Author: yiyang
 * @Date: 2021/2/23 10:28
 * @Description: 数据源
 */

@Slf4j
@Component
@ServerEndpoint("/websocket")
public class MachineLearningController {

    /**
     * 获取事件类型
     * @param session
     * @return
     */
    @OnOpen
    public void getWebSocketPrint(Session session){
        try {
            //String commond = "/opt/ml/ADLTI_py/dist/detectAnomalies /opt/ml/files_linux/review.yaml ";
            String commond = "sh /opt/ml/run.sh";
//            String query = session.getQueryString();
//            String[] param = query.split("=");
//            if(param.length==2){
//                if(param[0].equals("commond")){
//                    commond = param[1];
//                }
//            }
            System.out.println(commond);
            //System.out.println("---------"+run_mode);
            // 执行tail -f命令
            Process process = Runtime.getRuntime().exec(commond);
            InputStream inputStream = process.getInputStream();
            // 一定要启动新的线程，防止InputStream阻塞处理WebSocket的线程
            LogThread thread = new LogThread(inputStream,session);
            thread.start();
        } catch (Exception e) {
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
