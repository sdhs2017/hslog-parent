package com.jz.bigdata.common.manage.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.jz.bigdata.common.Constant;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.common.manage.service.IManageService;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.DescribeLog;
import com.jz.bigdata.util.Pattern_Matcher;

import net.sf.json.JSONArray;

@Controller
@RequestMapping("/manage")
public class ManageController {

	@Resource(name = "configProperty")
	private ConfigProperty configProperty;

	@Resource(name = "manageService")
	private IManageService iManageService;
	//创建快照策略的ID
	private final String SNAPSHOT_ID = "snapshot";
	@ResponseBody
	@RequestMapping("/getDiskUsage")
	@DescribeLog(describe = "获取服务器磁盘使用情况")
	public Map<String, String> getDiskUsage(HttpServletRequest request) {

		Map<String, String> map = iManageService.getDiskUsage(configProperty.getHost_user(),
				configProperty.getHost_passwd(), configProperty.getHost_ip());

		return map;
	}

	@ResponseBody
	@RequestMapping("/modifyServerIP")
	@DescribeLog(describe = "修改服务器IP地址")
	public Map<String, String> modifyServerIP(HttpServletRequest request) {

		/**
		 * 修改信息
		 * ipaddr  ip地址
		 * gateway 网关
		 * netmask 子网掩码
		 */
		String ipaddr = request.getParameter("ipaddr");
		String gateway = request.getParameter("gateway");
		String netmask = request.getParameter("netmask");

		return iManageService.doshell("sh "+configProperty.getModifyip_shell()+" "+ipaddr+" "+netmask+" "+gateway,configProperty.getModifyip_path());
	}

	@ResponseBody
	@RequestMapping(value = "/createRepertory", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "创建备份仓库")
	public String createRepertory() {

		// 创建备份仓库
		// String url = "curl -XPUT
		// http://"+configProperty.getEs_path_snapshot()+"/_snapshot/EsBackup -d
		// '{\"type\":\"fs\",\"settings\":{\"location\":\"/home/elsearch/es_backups/my_backup/\"}}'";
		String url = "curl -XPUT http://" + configProperty.getEs_path_snapshot()
				+ "/_snapshot/EsBackup -d '{\"type\":\"fs\",\"settings\":{\"location\":\"/mnt/disk1/elsearch/es_backups/\"}}'";

		// iManageService.doCutl("-XPUT", url);
		iManageService.doshell(url, configProperty.getHost_user(), configProperty.getHost_passwd(),
				configProperty.getHost_ip());
		Map<String, Object> map = new HashMap<>();
		map.put("state", true);
		map.put("msg", "创建备份仓库成功！");
		return JSONArray.fromObject(map).toString();
	}

	@ResponseBody
	@RequestMapping(value = "/createSnapshotByIndices", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "创建快照")
	public String createSnapshotByIndices() {


		String user = null;
		// 判断配置文件中是否配置elasticsearch的用户名和密码信息，如果配置拼接curl的用户设置
		if ((configProperty.getEs_user()!=null&&!configProperty.getEs_user().equals(""))&&(configProperty.getEs_password()!=null&&!configProperty.getEs_password().equals(""))){
			user = " --user "+configProperty.getEs_user()+":"+configProperty.getEs_password();
		}
		//创建快照
		Map<String, String> MessageResult = iManageService.doshell(
				"curl "+(user!=null?user:"")+" -X GET http://" + configProperty.getEs_path_snapshot()+"/_snapshot/EsBackup/snapshot",
				configProperty.getHost_user(), configProperty.getHost_passwd(), configProperty.getHost_ip());
		//查看快照状态
		String resultSuccess = Pattern_Matcher.getMatchedContentByParentheses(
				MessageResult
						.get("curl "+(user!=null?user:"")+" -X GET http://" + configProperty.getEs_path_snapshot() + "/_snapshot/EsBackup/snapshot"),
				"\"state\":\"(.*?)\"");
		//查看快照创建结果，是否有异常信息
		String resultMissing = Pattern_Matcher.getMatchedContent(
				MessageResult
						.get("curl "+(user!=null?user:"")+" -X GET http://" + configProperty.getEs_path_snapshot() + "/_snapshot/EsBackup/snapshot"),
				"\"type\":\"snapshot_missing_exception\"");

		Map<String, Object> map = new HashMap<>();
		// createRepertory();
		// 判断备份快照状态，成功
		if (resultSuccess.equals("SUCCESS")) {
			// 删除快照
			String deleteUrl = "curl "+(user!=null?user:"")+" -XDELETE http://" + configProperty.getEs_path_snapshot()
					+ "/_snapshot/EsBackup/snapshot";
			// iManageService.doCutl("-XDELETE ", deleteUrl);
			iManageService.doshell(deleteUrl, configProperty.getHost_user(), configProperty.getHost_passwd(),
					configProperty.getHost_ip());
			// 创建快照并指定索引
			String snapshotUrlByIndices = "curl "+(user!=null?user:"")+" -XPUT http://" + configProperty.getEs_path_snapshot()
					+ "/_snapshot/EsBackup/snapshot -H 'Content-Type:application/json' -d \'{\"indices\":\"" + configProperty.getEs_index()
					+ "\",\"wait_for_completion\":true}\'";
			// iManageService.doCutl("-XPUT", snapshotUrlByIndices);
			Map<String, String> result = iManageService.doshell(snapshotUrlByIndices, configProperty.getHost_user(),
					configProperty.getHost_passwd(), configProperty.getHost_ip());
			map.put("state", true);
			map.put("msg", "日志数据备份启动成功！");
			return JSONArray.fromObject(map).toString();
			// 判断快照状态提示快照missing，没有创建过快照
		} else if(resultMissing.contains("missing")){
			// 初次创建快照
			// 删除快照
			String deleteUrl = "curl "+(user!=null?user:"")+" -XDELETE http://" + configProperty.getEs_path_snapshot()
					+ "/_snapshot/EsBackup/snapshot";
			iManageService.doshell(deleteUrl, configProperty.getHost_user(), configProperty.getHost_passwd(),
					configProperty.getHost_ip());
			// 创建快照并指定索引
			String snapshotUrlByIndices = "curl "+(user!=null?user:"")+" -XPUT http://" + configProperty.getEs_path_snapshot()
					+ "/_snapshot/EsBackup/snapshot -H 'Content-Type:application/json' -d \'{\"indices\":\"" + configProperty.getEs_index()
					+ "\",\"wait_for_completion\":true}\'";
			// iManageService.doCutl("-XPUT", snapshotUrlByIndices);
			Map<String, String> result = iManageService.doshell(snapshotUrlByIndices, configProperty.getHost_user(),
					configProperty.getHost_passwd(), configProperty.getHost_ip());
			map.put("state", true);
			map.put("msg", "日志数据备份启动成功！");
			return JSONArray.fromObject(map).toString();
		} else if (resultSuccess.equals("IN_PROGRESS")) {
			map.put("state", false);
			map.put("msg", "日志数据备份未结束！");
			return JSONArray.fromObject(map).toString();
		} else if (resultSuccess.equals("FAILED")) {
			// 删除失败快照
			String deleteUrl = "curl "+(user!=null?user:"")+" -XDELETE http://" + configProperty.getEs_path_snapshot()
					+ "/_snapshot/EsBackup/snapshot";
			iManageService.doshell(deleteUrl, configProperty.getHost_user(), configProperty.getHost_passwd(),
					configProperty.getHost_ip());
			// 创建快照并指定索引
			String snapshotUrlByIndices = "curl "+(user!=null?user:"")+" -XPUT http://" + configProperty.getEs_path_snapshot()
					+ "/_snapshot/EsBackup/snapshot -H 'Content-Type:application/json' -d \'{\"indices\":\"" + configProperty.getEs_index()
					+ "\",\"wait_for_completion\":true}\'";
			// iManageService.doCutl("-XPUT", snapshotUrlByIndices);
			Map<String, String> result = iManageService.doshell(snapshotUrlByIndices, configProperty.getHost_user(),
					configProperty.getHost_passwd(), configProperty.getHost_ip());
			map.put("state", true);
			map.put("msg", "日志数据备份启动成功！");
			return JSONArray.fromObject(map).toString();
		}else {
			// 其他任何异常情况
			// 删除快照
			String deleteUrl = "curl "+(user!=null?user:"")+" -XDELETE http://" + configProperty.getEs_path_snapshot()
					+ "/_snapshot/EsBackup/snapshot";
			iManageService.doshell(deleteUrl, configProperty.getHost_user(), configProperty.getHost_passwd(),
					configProperty.getHost_ip());
			// 创建快照并指定索引
			String snapshotUrlByIndices = "curl "+(user!=null?user:"")+" -XPUT http://" + configProperty.getEs_path_snapshot()
					+ "/_snapshot/EsBackup/snapshot -H 'Content-Type:application/json' -d \'{\"indices\":\"" + configProperty.getEs_index()
					+ "\",\"wait_for_completion\":true}\'";
			// iManageService.doCutl("-XPUT", snapshotUrlByIndices);
			Map<String, String> result = iManageService.doshell(snapshotUrlByIndices, configProperty.getHost_user(),
					configProperty.getHost_passwd(), configProperty.getHost_ip());
			map.put("state", true);
			map.put("msg", "日志数据备份启动成功！");
			return JSONArray.fromObject(map).toString();
		}

	}

	/**
	 * 暂时服务于2022年销售许可证策略备份要求检测
	 * 创建快照策略，并执行
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/createPolicySnapshotByIndices", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "创建策略快照")
	public String createPolicySnapshotByIndices(HttpServletRequest request) {
		//策略cron表达式
		String cron = request.getParameter("cron");
		//先删除原来的策略
		iManageService.deleteSLMPolicy(SNAPSHOT_ID);
		//name 为快照名称，该表达式会使快照生成的格式变为：snapshot-2022.02.17-17.10.00xxxxx
		boolean result = iManageService.createSLMPolicy(configProperty.getEs_index(),SNAPSHOT_ID,"<snapshot-winlogbeat-{now/s{yyyy.MM.dd-HH.mm.ss|UTC+08:00}}>",cron,"EsBackup");
		if(result){
			boolean execResult = iManageService.executeSLMPolicy(SNAPSHOT_ID);
			if(execResult){
				return Constant.successMessage("操作成功！");
			}else{
				return Constant.failureMessage("操作失败");
			}
		}else{
			return Constant.failureMessage("操作失败");
		}
	}

	/**
	 * 暂时服务于2022年销售许可证策略备份要求检测
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getSnapshotList", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "获取快照列表")
	public String getSnapshotList(){
		List<Map<String,String>> list = iManageService.getSnapListByPolicyId("EsBackup");
		return Constant.successData(JSONArray.fromObject(list).toString());
	}

	/**
	 * 暂时服务于2022年销售许可证策略备份要求检测
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/restoreBySnapshot", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "恢复快照")
	public String restoreBySnapshot(HttpServletRequest request){
		String snapshotName = request.getParameter("snapshotName");
		//先关闭索引，否则无法恢复快照
		if(iManageService.closeIndex(configProperty.getEs_index())){
			boolean result = iManageService.restoreSnapshot("EsBackup",snapshotName);
			//再开启
			iManageService.openIndex(configProperty.getEs_index());
			if(result){
				return Constant.successMessage("操作成功");
			}else{
				return Constant.failureMessage("操作失败");
			}
		}else{
			return Constant.failureMessage("操作失败");
		}
	}
	@ResponseBody
	@RequestMapping(value = "/restore", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "恢复索引")
	public String restore() {

		String user = null;
		// 判断配置文件中是否配置elasticsearch的用户名和密码信息，如果配置拼接curl的用户设置
		if ((configProperty.getEs_user()!=null&&!configProperty.getEs_user().equals(""))&&(configProperty.getEs_password()!=null&&!configProperty.getEs_password().equals(""))){
			user = " --user "+configProperty.getEs_user()+":"+configProperty.getEs_password();
		}

		// 首先关闭需要恢复的索引
		String closeUrl = "curl "+(user!=null?user:"")+" -XPOST http://" + configProperty.getEs_path_snapshot() + "/"
				+ configProperty.getEs_index() + "/_close";
		// iManageService.doCutl("-XPOST", closeUrl);
		iManageService.doshell(closeUrl, configProperty.getHost_user(), configProperty.getHost_passwd(),
				configProperty.getHost_ip());
		// 回复快照
		String restoreUrl = "curl "+(user!=null?user:"")+" -XPOST http://" + configProperty.getEs_path_snapshot()
				+ "/_snapshot/EsBackup/snapshot/_restore";
		// iManageService.doCutl("-XPOST", restoreUrl);
		iManageService.doshell(restoreUrl, configProperty.getHost_user(), configProperty.getHost_passwd(),
				configProperty.getHost_ip());
		// 恢复完成之后打开索引
		String openUrl = "curl "+(user!=null?user:"")+" -XPOST http://" + configProperty.getEs_path_snapshot() + "/"
				+ configProperty.getEs_index() + "/_open";
		// iManageService.doCutl("-XPOST", openUrl);
		iManageService.doshell(openUrl, configProperty.getHost_user(), configProperty.getHost_passwd(),
				configProperty.getHost_ip());

		Map<String, Object> map = new HashMap<>();
		map.put("state", true);
		map.put("msg", "恢复备份数据成功！");
		return JSONArray.fromObject(map).toString();

	}

	@ResponseBody
	@RequestMapping(value = "/doCurl", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "执行curl命令")
	public String doCurl(HttpServletRequest request) {

		/**
		 * 接收前端返回的elasticsearch查询参数
		 */
		String indices = request.getParameter("indices");
		String method = request.getParameter("method");
		String parameter = request.getParameter("parameter");
		String content = request.getParameter("content");
		//String searchCmd = request.getParameter("cmd");
		String searchCmd = "GET winlogbeat-7.6.0-2020.03.26/_search?size=0 {\"aggs\": {\"NAME\": {\"terms\": {\"field\":\"fields.equipmentid\", \"size\":10}}}\n" +
				"}";

		StringBuilder urlbuilder = new StringBuilder();
		urlbuilder.append("http://192.168.2.181:9201/");
		urlbuilder.append(indices);
		if (parameter!=null&&!parameter.equals("")){
			urlbuilder.append(parameter);
		}
		if (content!=null&&!content.equals("")){
			urlbuilder.append("-H 'Content-Type: application/json' ");
			urlbuilder.append("-d '");
			urlbuilder.append(content);
			urlbuilder.append("'");
		}


		//String result = "curl -XGET \"http://192.168.2.181:9201/winlogbeat-7.6.0-2020.03.26/_search?size=0\" -H 'Content-Type: application/json' -d'{\"aggs\": {\"NAME\": {\"terms\": {\"field\":\"fields.equipmentid\", \"size\":10}}}}'";

		String result = iManageService.doCurl(method, urlbuilder.toString());


		Map<String, Object> map = new HashMap<>();
		map.put("state", true);
		map.put("msg", result);
		return JSONArray.fromObject(map).toString();

	}

	@ResponseBody
	@RequestMapping(value = "/getIndex", produces = "application/json; charset=utf-8")
	@DescribeLog(describe = "index")
	public String getIndex(HttpServletRequest request) {

		iManageService.indexForceMerge();

		Map<String, Object> map = new HashMap<>();
		map.put("state", true);
		map.put("msg", "wan");
		return JSONArray.fromObject(map).toString();

	}
}
