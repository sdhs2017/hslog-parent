package com.jz.bigdata.common.manage.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

	@ResponseBody
	@RequestMapping("/getDiskUsage")
	@DescribeLog(describe = "获取服务器磁盘使用情况")
	public Map<String, String> getDiskUsage(HttpServletRequest request) {

		Map<String, String> map = iManageService.getDiskUsage(configProperty.getHost_user(),
				configProperty.getHost_passwd(), configProperty.getHost_ip());

		return map;
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

		Map<String, String> MessageResult = iManageService.doshell(
				"curl "+(user!=null?user:"")+" -X GET http://" + configProperty.getEs_path_snapshot()+"/_snapshot/EsBackup/snapshot",
				configProperty.getHost_user(), configProperty.getHost_passwd(), configProperty.getHost_ip());

		String resultSuccess = Pattern_Matcher.getMatchedContentByParentheses(
				MessageResult
						.get("curl "+(user!=null?user:"")+" -X GET http://" + configProperty.getEs_path_snapshot() + "/_snapshot/EsBackup/snapshot"),
				"\"state\":\"(.*?)\"");
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
