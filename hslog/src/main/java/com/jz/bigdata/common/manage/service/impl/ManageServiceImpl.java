package com.jz.bigdata.common.manage.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import com.jz.bigdata.business.logAnalysis.log.service.impl.LogServiceImpl;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.equipment.service.IEquipmentService;
import com.jz.bigdata.common.manage.service.IManageService;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.Pattern_Matcher;
import com.jz.bigdata.util.ResourceUsage;

import net.sf.json.JSONArray;

@Service(value="manageService")
public class ManageServiceImpl extends QuartzJobBean implements IManageService {

    public static final String FILES_SHELL = "df -hl";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Resource(name ="configProperty")
    private ConfigProperty configProperty;

    @Resource(name = "EquipmentService")
    private IEquipmentService equipmentService;

    @Resource(name = "logService")
    private LogServiceImpl logService;

    @Override
    public Map<String, String> getDiskUsage(String user,String passwd,String host) {

        Map<String, String> result = ResourceUsage.runDistanceShell(FILES_SHELL, user, passwd, host);
        Map<String, String> diskinfo = new HashMap<String, String>();
        if (result.get("error")!=null&&!result.get("error").equals("")) {
            return result;
        }
        String commandResult = result.get(FILES_SHELL);
        String[] strings = commandResult.split(LINE_SEPARATOR);

        Pattern TPattern = Pattern.compile("[0-9][T]");
        Pattern GPattern = Pattern.compile("[0-9][G]");
        Pattern MPattern = Pattern.compile("[0-9][M]");
        Pattern KPattern = Pattern.compile("[0-9][K]");
        float size = 0;
        float used = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) continue;

            int temp = 0;
            Matcher Tmatcher = TPattern.matcher(strings[i]);
            Matcher Gmatcher = GPattern.matcher(strings[i]);
            Matcher Mmatcher = MPattern.matcher(strings[i]);
            Matcher Kmatcher = KPattern.matcher(strings[i]);

            if (Gmatcher.find()||Mmatcher.find()||Kmatcher.find()||Tmatcher.find()) {
                Pattern systemPattern = Pattern.compile("/$");
                Pattern dataPattern = Pattern.compile("/home");
                Matcher systemmatcher = systemPattern.matcher(strings[i]);
                Matcher datamatcher = dataPattern.matcher(strings[i]);
                //System.out.println(strings[i]);
                if (systemmatcher.find()) {
                    String[] content = strings[i].toString().split("\\s+");
            		/*for(String content : strings[i].toString().split("\\s+")) {
            			System.out.println("--- "+content);
            		};*/
                    diskinfo.put("sys_size", content[0]);
                    diskinfo.put("sys_used", content[1]);
                    diskinfo.put("sys_avail", content[2]);
                    diskinfo.put("sys_per", content[3]);
                    diskinfo.put("sys", content[4]);

                }
                if (datamatcher.find()) {
                    String[] content = strings[i].toString().split("\\s+");
                    diskinfo.put("data_size", content[0]);
                    diskinfo.put("data_used", content[1]);
                    diskinfo.put("data_avail", content[2]);
                    diskinfo.put("data_per", content[3]);
                    diskinfo.put("data", content[4]);
                }
                if (stringBuilder.length()<1) {
                    for (String s : strings[i].split("\\s+")) {

                        if (temp == 0) {
                            temp++;
                            continue;
                        }
                        if (!s.trim().isEmpty()) {
                            if (temp == 1) {
                                size += disposeUnit(s);
                                temp++;
                            } else {
                                used += disposeUnit(s);
                                temp = 0;
                            }
                        }
                    }
                }else{
                    stringBuilder.append("   "+strings[i]);
                    //System.out.println(stringBuilder.toString());
                    for (String s : stringBuilder.toString().split("\\s+")) {
                        if (temp == 0) {
                            temp++;
                            continue;
                        }
                        if (!s.trim().isEmpty()) {
                            if (temp == 1) {
                                size += disposeUnit(s);
                                temp++;
                            } else {
                                used += disposeUnit(s);
                                temp = 0;
                            }
                        }
                    }
                    stringBuilder.delete(0, stringBuilder.length());
                }

            }else{
                stringBuilder.append(strings[i]);
            }

        }
        DecimalFormat decimalFormat=new DecimalFormat(".00");

        diskinfo.put("size", decimalFormat.format(size));
        diskinfo.put("used", decimalFormat.format(used));
        return diskinfo;
    }

    /**
     * 处理单位转换
     * K/KB/M/T 最终转换为G 处理
     *
     * @param s 带单位的数据字符串
     * @return 以G 为单位处理后的数值
     */
    private static float disposeUnit(String s) {

        try {
            s = s.toUpperCase();
            String lastIndex = s.substring(s.length() - 1);
            String num = s.substring(0, s.length() - 1);
            float parseInt = Float.parseFloat(num);
            if (lastIndex.equals("G")) {
                return parseInt;
            } else if (lastIndex.equals("T")) {
                return parseInt * 1024;
            } else if (lastIndex.equals("M")) {
                return parseInt / 1024;
            } else if (lastIndex.equals("K") || lastIndex.equals("KB")) {
                return parseInt / (1024 * 1024);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    /**
     * 通过java执行curl
     * @param type
     * @param url
     */
    public String doCurl(String type,String url) {
        String[] cmds = {"curl", type, url};
        ProcessBuilder processBuilder = new ProcessBuilder(cmds);
        processBuilder.redirectErrorStream(true);
        Process process;
        StringBuilder result = new StringBuilder();
        try {
            process = processBuilder.start();
            BufferedReader br = null;
            String line = null;
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = br.readLine()) != null) {
                System.out.println("\t" + line);
                result.append(line.trim()).append(System.getProperty("line.separator"));
            }
            br.close();
            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 通过java执行shell
     * @param url,user,passwd,host
     */
    public Map<String, String> doshell(String url,String user,String passwd,String host) {
        Map<String, String> result = ResourceUsage.runDistanceShell(url, user, passwd, host);
        System.out.println(result);
        return result;
    }


    public String createSnapshotByIndices() {

        String user = null;
        // 判断配置文件中是否配置elasticsearch的用户名和密码信息，如果配置拼接curl的用户设置
        if ((configProperty.getEs_user()!=null&&!configProperty.getEs_user().equals(""))&&(configProperty.getEs_password()!=null&&!configProperty.getEs_password().equals(""))){
            user = " --user "+configProperty.getEs_user()+":"+configProperty.getEs_password();
        }

        Map<String, String> MessageResult=doshell("curl "+(user!=null?user:"")+" -X GET http://"+configProperty.getEs_path_snapshot()+"/_snapshot/EsBackup/snapshot", configProperty.getHost_user(), configProperty.getHost_passwd(), configProperty.getHost_ip());

        String resultSuccess=Pattern_Matcher.getMatchedContentByParentheses(MessageResult.get("curl "+(user!=null?user:"")+" -X GET http://"+configProperty.getEs_path_snapshot()+"/_snapshot/EsBackup/snapshot"), "\"state\":\"(.*?)\"");
        Map<String, Object> map= new HashMap<>();
        if(resultSuccess.equals("SUCCESS")){
            // 创建备份仓库
            //String url = "curl -XPUT http://"+configProperty.getEs_path_snapshot()+"/_snapshot/EsBackup -d '{\"type\":\"fs\",\"settings\":{\"location\":\"/home/elsearch/es_backups/my_backup/\"}}'";
            //String url = "curl -XPUT http://"+configProperty.getEs_path_snapshot()+"/_snapshot/EsBackup -d '{\"type\":\"fs\",\"settings\":{\"location\":\"/mnt/disk1/elsearch/es_backups/\"}}'";
            //doshell(url,configProperty.getHost_user(), configProperty.getHost_passwd(), configProperty.getHost_ip());
            // 删除快照
            String deleteUrl = "curl "+(user!=null?user:"")+" -XDELETE http://"+configProperty.getEs_path_snapshot()+"/_snapshot/EsBackup/snapshot";
            doshell(deleteUrl,configProperty.getHost_user(), configProperty.getHost_passwd(), configProperty.getHost_ip());
            // 创建快照并指定索引
            String snapshotUrlByIndices = "curl "+(user!=null?user:"")+" -XPUT http://"+configProperty.getEs_path_snapshot()+"/_snapshot/EsBackup/snapshot -H 'Content-Type:application/json' -d \'{\"indices\":\""+configProperty.getEs_index()+"\",\"wait_for_completion\":true}\'";
            doshell(snapshotUrlByIndices,configProperty.getHost_user(), configProperty.getHost_passwd(), configProperty.getHost_ip());

            //System.out.println("自动备份成功！----时间----:"+format.format(new Date()));
            map.put("state", true);
            map.put("msg", "日志数据备份成功！");
            return JSONArray.fromObject(map).toString();
        }else{
            map.put("state", true);
            map.put("msg", "日志数据备份未结束！");
            return JSONArray.fromObject(map).toString();
        }
    }

    /**
     * 定时任务统计客户自定义的安全策略数据
     * @return
     */
    public String updateRisk() throws Exception {

        List<Equipment> list = equipmentService.selectAllEquipmentByRisk();
        String index = configProperty.getEs_index();
        String [] types = null;
        Date enddate = new Date();
        for(Equipment equipment : list) {
            logService.getEventstypeCountByEquipmentid(index, types, equipment.getId(), enddate);
        }

        Map<String, Object> map= new HashMap<>();
        map.put("state", true);
        map.put("msg", "定时任务执行完成！");
        return JSONArray.fromObject(map).toString();
    }

    @Override
    protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
        // TODO Auto-generated method stub

    }

}
