package com.jz.bigdata.common.equipment.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.alibaba.excel.EasyExcel;
import com.jz.bigdata.business.logAnalysis.ecs.service.IecsService;
import com.jz.bigdata.roleauthority.user.dao.IUserDao;
import com.jz.bigdata.roleauthority.user.entity.User;
import com.jz.bigdata.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.jz.bigdata.business.logAnalysis.log.service.IlogService;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.equipment.dao.IEquipmentDao;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.equipment.service.IEquipmentService;

import net.sf.json.JSONArray;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * @author shichengyu
 * @date 2017年8月16日 下午2:39:47
 * @description
 */
@Slf4j
@Service(value = "EquipmentService")
public class EquipmentServiceImpl implements IEquipmentService {
	//日期时间格式
	private static final DateTimeFormatter dtf_time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final DateTimeFormatter dtf_time_zero = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");
	//生成文件时的日期格式
	private static final DateTimeFormatter dtf_time_file = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS");
	//资产导出excel模板 名称
	private static final String EquipmentTemplateFileName = "资产导出模板.xls";
	@Resource
	private IEquipmentDao equipmentDao;

	@Resource
	private IUserDao userDao;

	@Resource(name = "configProperty")
	private ConfigProperty configProperty;

	@Resource(name="logService")
	private IlogService logService;

	@Resource(name="ecsService")
	private IecsService ecsService;

	/**
	 * @param equipment
	 * @return 添加数据
	 */
	@Override
	public int insert(Equipment equipment, HttpSession session) {

		// 获取总数
		List<Object> count = equipmentDao.count_Number();
		// 获取总数集合6

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		resultList = (List<Map<String, Object>>) count.get(0);

		BASE64Util base64Util = new BASE64Util();
		// 判断资产数是否超过限定
		if (Integer.valueOf((String) resultList.get(0).get("count")) < Integer
				.valueOf(base64Util.decode(configProperty.getNumber()).trim())) {


			Equipment equ= equipmentDao.selectByNameIp(equipment);
			//判断资产是否存在
			if(equ ==null){
				// 获取uuid
				equipment.setId(Uuid.getUUID());
				User user = userDao.selectById(session.getAttribute(Constant.SESSION_USERID).toString());
				equipment.setDepartmentId(user.getDepartmentId());
				equipment.setUserId(session.getAttribute(Constant.SESSION_USERID).toString());
				equipment.setDepartmentNodeId((int) session.getAttribute(Constant.SESSION_DEPARTMENTNODEID));
				// 获取日期
				equipment.setCreateTime(LocalDateTime.now().format(dtf_time));
//				equipment.setState(1);
				equipmentDao.insert(equipment);
				return 2;
			}
			return 1;
		} else {
			return 0;
		}

	}

	/**
	 * 添加/修改资产
	 * @param equipment
	 * @param session
	 * @param isAssetGroup 是否关联资产组
	 * @return
	 */
	@Override
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	public int upsert(Equipment equipment, HttpSession session,boolean isAssetGroup) throws Exception {
		// 获取总数
		List<Object> count = equipmentDao.count_Number();
		// 获取总数集合6

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		resultList = (List<Map<String, Object>>) count.get(0);

		BASE64Util base64Util = new BASE64Util();
		// 判断资产数是否超过限定
		if (Integer.valueOf((String) resultList.get(0).get("count")) < Integer.valueOf(base64Util.decode(configProperty.getNumber()).trim())) {
			int result = 0;
			//id不为空，说明是资产的update
			if(equipment.getId()!=null&&!"".equals(equipment.getId())){
				// 获取日期
				equipment.setUpDateTime(LocalDateTime.now().format(dtf_time));
				equipment.setDepartmentNodeId((int) session.getAttribute(Constant.SESSION_DEPARTMENTNODEID));
				result = equipmentDao.updateById(equipment);
				if(isAssetGroup){
					//更新资产与资产组中间表
					equipmentDao.updateAssetGroupRelationsById(equipment);
				}
			}else{
				equipment.setId(Uuid.getUUID());
				//id为空，为新增资产
				User user = userDao.selectById(session.getAttribute(Constant.SESSION_USERID).toString());
				equipment.setDepartmentId(user.getDepartmentId());
				equipment.setUserId(session.getAttribute(Constant.SESSION_USERID).toString());
				equipment.setDepartmentNodeId((int) session.getAttribute(Constant.SESSION_DEPARTMENTNODEID));
				// 获取日期
				equipment.setCreateTime(LocalDateTime.now().format(dtf_time));
				result = equipmentDao.insert(equipment);
			}
			return result;
		} else {
			return 2;
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public int insertBatch(Equipment equipment, HttpSession session){
		// 获取总数
		List<Object> count = equipmentDao.count_Number();
		// 默认返回值
		int result = 0;
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		resultList = (List<Map<String, Object>>) count.get(0);

		BASE64Util base64Util = new BASE64Util();
		// 判断资产数是否超过限定
		if (Integer.valueOf((String) resultList.get(0).get("count")) < Integer.valueOf(base64Util.decode(configProperty.getNumber()).trim())) {

			//id不为空，说明是资产的update
			if(equipment.getId()!=null&&!"".equals(equipment.getId())){
				// 获取日期
				equipment.setUpDateTime(LocalDateTime.now().format(dtf_time));
				equipment.setDepartmentNodeId((int) session.getAttribute(Constant.SESSION_DEPARTMENTNODEID));
				return equipmentDao.updateById(equipment);
			}else{
				equipment.setId(Uuid.getUUID());
				//id为空，为新增资产
				User user = userDao.selectById(session.getAttribute(Constant.SESSION_USERID).toString());
				equipment.setDepartmentId(user.getDepartmentId());
				equipment.setUserId(session.getAttribute(Constant.SESSION_USERID).toString());
				equipment.setDepartmentNodeId((int) session.getAttribute(Constant.SESSION_DEPARTMENTNODEID));
				// 获取日期
				equipment.setCreateTime(LocalDateTime.now().format(dtf_time));
				return equipmentDao.insert(equipment);
			}
		} else {
			return 3;
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public String insertBatch(List<Equipment> equipmentList, HttpSession session){
		// 获取总数
		List<Object> count = equipmentDao.count_Number();
		// 默认返回值
		int result = 0;
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		resultList = (List<Map<String, Object>>) count.get(0);

		BASE64Util base64Util = new BASE64Util();
		for (Equipment equipment : equipmentList){
			// 判断资产数是否超过限定
			if (Integer.valueOf((String) resultList.get(0).get("count")) < Integer.valueOf(base64Util.decode(configProperty.getNumber()).trim())) {

				equipment.setId(Uuid.getUUID());
				//id为空，为新增资产
				User user = userDao.selectById(session.getAttribute(Constant.SESSION_USERID).toString());
				equipment.setDepartmentId(user.getDepartmentId());
				equipment.setUserId(session.getAttribute(Constant.SESSION_USERID).toString());
				equipment.setDepartmentNodeId((int) session.getAttribute(Constant.SESSION_DEPARTMENTNODEID));
				// 获取日期
				equipment.setCreateTime(LocalDateTime.now().format(dtf_time));
				try {
					switch (equipmentDao.insert(equipment)){
						case 1:
							break;
						case 0:
							return Constant.failureMessage("insert 结果 0！");
					}
				} catch (DataAccessException e) {
					e.printStackTrace();
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					//异常类型
					if(e.getMessage().indexOf("MySQLIntegrityConstraintViolationException")>=0){
						//根据异常信息判定是否存在唯一索引重复
						if(e.getMessage().indexOf("nameUnique")>=0){
							return Constant.failureMessage("资产名称重复，请重新编辑该资产："+equipment.getName()+" "+equipment.getIp()+" "+equipment.getLogType());
						}else if(e.getMessage().indexOf("ipLogTypeUnique")>=0){
							return Constant.failureMessage("资产“IP+日志类型”重复，请重新编辑该资产："+equipment.getName()+" "+equipment.getIp()+" "+equipment.getLogType());
						}else{
							log.error(e.getMessage());
							return Constant.failureMessage("资产导入batchInsert MySQLIntegrityConstraintViolationException的其他情况: "+equipment.getName()+" "+equipment.getIp()+" "+equipment.getLogType());
						}
					}else{
						log.error(e.getMessage());
						return Constant.failureMessage("资产导入batchInsert 其他异常情况: "+equipment.getName()+" "+equipment.getIp()+" "+equipment.getLogType());
					}
				}catch (Exception e){
					e.printStackTrace();
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return Constant.failureMessage("其他异常情况！");
				}
			} else {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				// 超过300条报3
				return Constant.failureMessage("资产达到上限300，无法添加！");
			}
		}
		return Constant.successMessage("资产导入成功！");
	}

	/**
	 * @param equipment
	 * @return 查询数据
	 */
	@Override
	public String selectAll(Equipment equipment,HttpSession session) {
		String userId=session.getAttribute(Constant.SESSION_USERID).toString();
		String role=session.getAttribute(Constant.SESSION_USERROLE).toString();
		List<Equipment> list = equipmentDao.selectAll(equipment,role,userId);

		// list转json
		return JSONArray.fromObject(list).toString();
	}

	/**
	 * @param equipment
	 * @return 修改数据
	 */
	@Override
	public int updateById(Equipment equipment, HttpSession session) {
		// 获取日期
		equipment.setUpDateTime(LocalDateTime.now().format(dtf_time));
		equipment.setDepartmentNodeId((int) session.getAttribute(Constant.SESSION_DEPARTMENTNODEID));
		// equipment.setDepartmentId(Integer.valueOf(session.getAttribute(Constant.SESSION_DEPARTMENTID).toString()));
		// equipment.setUserId(session.getAttribute(Constant.SESSION_USERID).toString());
		// if(equipment)
		return equipmentDao.updateById(equipment);
	}

	/**
	 * @param ids
	 * @return 删除数据
	 */
	@Override
	public int delete(String[] ids) {
		int result=0;
		result=equipmentDao.delete(ids);
		equipmentDao.deleteEvent(ids);
		//
		return result;
	}
	/**
	 * @param ids
	 * @return 删除数据.包含资产/资产组关系表中信息的删除
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public int deleteWithAssetGroup(String[] ids) {
		int result=0;
		result=equipmentDao.delete(ids);
		equipmentDao.deleteEvent(ids);
		equipmentDao.deleteAssetGroupRelationsByEquipmentId(ids);
		return result;
	}
	/**
	 * @param equipment
	 * @return
	 * @description 根据id查询单一资产
	 */
	@Override
	public List<Equipment> selectEquipment(Equipment equipment) throws Exception {
		//List<Equipment> list = equipmentDao.selectEquipment(equipment);
		List<Equipment> list = equipmentDao.selectEquipmentWithRole(equipment);
		List<Equipment> listEquipment = (List<Equipment>) list.get(0);

		List<Equipment> myList = new ArrayList<>();

		if (listEquipment.size()<1){
			return  myList;
		}
		// equipment=listEquipment.get(0);
		// System.out.println(equipment.getConfidentiality());

		// 遍历资产，通过资产id查询该资产下当天的日志条数，时间范围当天的00:00:00到当天的查询时间
		String starttime = LocalDateTime.now().format(dtf_time_zero);
		String endtime = LocalDateTime.now().format(dtf_time);
		Map<String, String> esMap = new HashMap<>();
		Map<String, Object> equipmentmap = (Map<String, Object>) listEquipment.get(0);

		/*for (Entry<String,Object> ssSet : equipmentmap.entrySet()) {
			System.out.println(ssSet.getKey()+":"+ssSet.getValue());
		}*/

		String equipmentid = equipmentmap.get("id").toString();
		/*esMap.put("equipmentid", equipmentid);
		equipmentmap.put("log_count", logService.getCount(esMap,starttime,endtime,null,configProperty.getEs_index())+"");*/
		/**
		 * 新版本查询ecsService
		 */
		esMap.put("fields.equipmentid", equipmentid);
		//esMap.put("fields.failure","false");
		equipmentmap.put("log_count", ecsService.getCount(esMap,starttime,endtime,configProperty.getEs_index())+"");

		equipment = JavaBeanUtil.convertMapToBean(Equipment.class, equipmentmap);

		myList.add(equipment);
		return myList;
	}

	@Override
	public List<Equipment> selectEquipmentByLog(Equipment equipment) throws Exception {
		// 查询所有数据
		List<Equipment> listEquipment = equipmentDao.searchAllByPage(equipment.getId(),"", "", "", "", "", "","", 0,15,"");
		// System.err.println(listEquipment.get(0).getCreateTime());

		// 遍历资产，通过资产id查询该资产下当天的日志条数，时间范围当天的00:00:00到当天的查询时间
		String starttime = LocalDateTime.now().format(dtf_time_zero);
		String endtime = LocalDateTime.now().format(dtf_time);
		for(Equipment equipment_tmp : listEquipment) {
			Map<String, String> esMap = new HashMap<>();
			//esMap.put("equipmentid", equipment.getId());
			//equipment.setLog_count(logService.getCount(esMap,starttime,endtime,null,configProperty.getEs_index())+"");
			/**
			 * ECS的资产id改成fields.equipmentid
			 */
			esMap.put("fields.equipmentid", equipment_tmp.getId());
			//esMap.put("fields.failure","true");//资产显示页面也需要显示未范式化的资产
			equipment_tmp.setLog_count(getEquipmentLogCount(equipment_tmp.getLogType(),equipment_tmp.getType(),esMap,starttime,endtime)+"");

		}
		return listEquipment;
	}

	/**
	 * 分页查询数据
	 * @param hostName
	 * @param name
	 * @param ip
	 * @param logType
	 * @param pageIndex
	 * @param pageSize
	 * @param session
	 * @param asset_group_id 资产组id
	 * @return
	 */
	@Override
	public String selectAllByPage(String hostName, String name, String ip, String logType,String type, int pageIndex, int pageSize,HttpSession session,String asset_group_id) throws Exception {
		// 获取起始数
		int startRecord = (pageSize * (pageIndex - 1));
		// 获取总数
		List count = equipmentDao.count(hostName, name, ip, logType,type,session.getAttribute(Constant.SESSION_USERROLE).toString(),session.getAttribute(Constant.SESSION_USERID).toString(),asset_group_id);
		List listCount = new ArrayList<>();
		// 获取总数集合
		listCount = (List) count.get(0);

		Map<String, Object> map = new HashMap<String, Object>();
		// 总数添加到map
		map.put("count", (listCount.get(0)));
		// 查询所有数据
		List<Equipment> listEquipment = equipmentDao.searchAllByPage("",hostName, name, ip, logType, type, session.getAttribute(Constant.SESSION_USERROLE).toString(),session.getAttribute(Constant.SESSION_USERID).toString(), startRecord,pageSize,asset_group_id);
		// System.err.println(listEquipment.get(0).getCreateTime());

		// 遍历资产，通过资产id查询该资产下当天的日志条数，时间范围当天的00:00:00到当天的查询时间
		String starttime = LocalDateTime.now().format(dtf_time_zero);
		String endtime = LocalDateTime.now().format(dtf_time);
		for(Equipment equipment : listEquipment) {
			Map<String, String> esMap = new HashMap<>();
			//esMap.put("equipmentid", equipment.getId());
			//equipment.setLog_count(logService.getCount(esMap,starttime,endtime,null,configProperty.getEs_index())+"");
			/**
			 * ECS的资产id改成fields.equipmentid
 			 */
			esMap.put("fields.equipmentid", equipment.getId());
			//esMap.put("fields.failure","true");//资产显示页面也需要显示未范式化的资产
			equipment.setLog_count(getEquipmentLogCount(equipment.getLogType(),equipment.getType(),esMap,starttime,endtime)+"");
		}
		// 数据添加到map
		map.put("equipment", listEquipment);
		return JSONArray.fromObject(map).toString();
	}

	@Override
	public String selectAllByPageWithRole(String hostName, String name, String ip, String logType, String type, int pageIndex, int pageSize, HttpSession session, String asset_group_id) throws Exception {
		//返回结果
		Map<String, Object> map = new HashMap<String, Object>();
		// 获取起始数
		int startRecord = (pageSize * (pageIndex - 1));
		//获取用户角色信息
		String userRole = session.getAttribute(Constant.SESSION_USERROLE).toString();
		List<List<Map<String,String>>> listEquipment;
		//如果角色仅仅是操作管理员，则只查询该管理员的资产
		if(Constant.USER_ROLE_OPERATION_MANAGER.equals(userRole)){
			// 获取总数
			List<List<Map<String,String>>> count = equipmentDao.countWithRole(hostName, name, ip, logType,type,session.getAttribute(Constant.SESSION_USERROLE).toString(),session.getAttribute(Constant.SESSION_USERID).toString(),asset_group_id);
			// 总数添加到map
			map.put("count", (count.get(0).get(0)));
			listEquipment = equipmentDao.searchAllByPageWithRole("",hostName, name, ip, logType, type, session.getAttribute(Constant.SESSION_USERROLE).toString(),session.getAttribute(Constant.SESSION_USERID).toString(), startRecord,pageSize,asset_group_id);
		}else{
			// 获取总数
			List count = equipmentDao.count(hostName, name, ip, logType,type,session.getAttribute(Constant.SESSION_USERROLE).toString(),session.getAttribute(Constant.SESSION_USERID).toString(),asset_group_id);
			List listCount = new ArrayList<>();
			// 获取总数集合
			listCount = (List) count.get(0);
			// 总数添加到map
			map.put("count", (listCount.get(0)));
			// 查询所有数据
			listEquipment = equipmentDao.searchAllByPageWithRole("",hostName, name, ip, logType, type, session.getAttribute(Constant.SESSION_USERROLE).toString(),null, startRecord,pageSize,asset_group_id);
		}

		// 遍历资产，通过资产id查询该资产下当天的日志条数，时间范围当天的00:00:00到当天的查询时间
		String starttime = LocalDateTime.now().format(dtf_time_zero);
		String endtime = LocalDateTime.now().format(dtf_time);
		for(Map<String,String> equipment : listEquipment.get(0)) {
			Map<String, String> esMap = new HashMap<>();
			/**
			 * ECS的资产id改成fields.equipmentid
			 */
			esMap.put("fields.equipmentid", equipment.get("id"));
			//esMap.put("fields.failure","true");//资产显示页面也需要显示未范式化的资产
			equipment.put("log_count",getEquipmentLogCount(equipment.get("logType"),equipment.get("type"),esMap,starttime,endtime)+"");
		}
		// 数据添加到map
		map.put("equipment", listEquipment.get(0));
		return JSONArray.fromObject(map).toString();
	}

	/**
	 * 查询日志count
	 * @param logType 日志类型
	 * @param esMap 查询条件
	 * @param starttime 起始时间
	 * @param endtime 截止时间
	 * @return 日志数
	 * @throws Exception
	 */
	private long getEquipmentLogCount(String logType,String equipment_type,Map<String, String> esMap,String starttime,String endtime) throws Exception {
		String index;
		//根据日志类型设置要查询的index
		switch (logType){
			case "file":
				index = configProperty.getEs_file_index();
				break;
			case "winlog":
			case "syslog":
				//syslog类型  对于防火墙和IPS类型资产进行单独处理
				if(equipment_type.equals(Constant.EQUIPMENT_TYPE_IPS_CODE)){
					//IPS
					index = "hs_syslog_ips-*";
				}else if(equipment_type.equals(Constant.EQUIPMENT_TYPE_FIREWALL_CODE)){
					//防火墙
					index = "hs_syslog_firewall-*";
				}else{
					index = configProperty.getEs_index();
				}
				break;
			case "metric":
				index = configProperty.getEs_metric_index();
				break;
			case "packet":
				index = configProperty.getEs_packet_index();
				break;
			case "mysql":
				index = Constant.MYSQL_AUDIT_INDEX_NAME;
				break;
			default:
				//其他类型，日志量为0
				return 0;
		}
		return ecsService.getCount(esMap,starttime,endtime,index);

	}
	/**
	 * @param equipment
	 * @return
	 * @description 查询单个数据
	 */
	@Override
	public Equipment selectOneEquipment(Equipment equipment) {
		return equipmentDao.selectOneEquipment(equipment);
	}

	@Override
	public List<Equipment> selectAllHostName() {
		return equipmentDao.selectAllHostName();
	}

	/**
	 * @return
	 * @description 查询所有数据map
	 *
	 */
	@Override
	public Map<String, Equipment> selectAllEquipment() {

		Map<String, Equipment> map = new HashMap<String, Equipment>();
		List<Equipment> list = equipmentDao.selectAllHostName();
		Equipment e;
		String logType = "syslog";
		//key ip和日志类型为组合主键，用于日志资产匹配
		for (int i = 0; i < list.size(); i++) {
			map.put(list.get(i).getIp() + list.get(i).getLogType(), list.get(i));
		}
		return map;
	}

	@Override
	public Map<String, Equipment> selectAllEquipment_key_id() {
		Map<String, Equipment> map = new HashMap<String, Equipment>();
		List<Equipment> list = equipmentDao.selectAllHostName();
		Equipment e;
		String logType = "syslog";
		//key ip和日志类型为组合主键，用于日志资产匹配
		for (int i = 0; i < list.size(); i++) {
			map.put(list.get(i).getId(), list.get(i));
		}
		return map;
	}

	@Override
	public Set<String> selectAllIPAdress() {

		Set<String> set = new HashSet<String>();
		List<Equipment> list = equipmentDao.selectAllHostName();
		for (int i = 0; i < list.size(); i++) {
			set.add(list.get(i).getIp());
		}
		return set;
	}

	@Override
	public Map<String, String> selectLog_level() {

		Map<String, String> map = new HashMap<String, String>();
		List<Equipment> list = equipmentDao.selectAllHostName();
		for (Equipment equipment : list) {
			map.put(equipment.getId(), equipment.getLog_level());
		}
		return map;
	}

	/**
	 * @param high_risk
	 * @param moderate_risk
	 * @param low_risk
	 * @return
	 * @description 修改高中低危的数据
	 */
	@Override
	public int upRiskById(String id, int high_risk, int moderate_risk, int low_risk) {
		return equipmentDao.upRiskById(id, high_risk, moderate_risk, low_risk);
	}

	/**
	 * @return
	 * @description 查询所有数据，用于安全策略
	 */
	@Override
	public List<Equipment> selectAllEquipmentByRisk() {
		return equipmentDao.selectAllEquipmentByRisk();
	}

	/**
	 * @param list
	 * @return
	 * @description
	 * 批量修改数据
	 */
	@Override
	public int batchUpdate(List<Equipment> list) {
		return equipmentDao.batchUpdate(list);
	}

	/**
	 * 验证资产名称重复
	 * @param equipment
	 * @return  true：存在    false ：不存在
	 */
	@Override
	public boolean checkNameUnique(Equipment equipment) {
		List<List<Map<String,String>>> nameCount = equipmentDao.checkNameUnique(equipment);
		if(Integer.parseInt(nameCount.get(0).get(0).get("count"))>0){
			return true;
		}
		return false;
	}
	/**
	 * 验证IP+日志类型重复性
	 * @param equipment
	 * @return  true：存在  false ：不存在
	 */
	@Override
	public boolean checkIpAndLogTypeUnique(Equipment equipment) {
		List<List<Map<String,String>>> ipAndLogTypeCount = equipmentDao.checkIpAndLogTypeUnique(equipment);
		if(Integer.parseInt(ipAndLogTypeCount.get(0).get(0).get("count"))>0){
			return true;
		}
		return false;
	}

	@Override
	public String selectRisk() {
		List<Equipment> list = equipmentDao.selectRisk();
		return JSONArray.fromObject(list).toString();
	}

	@Override
	public List<Map<String, String>> getAssetList4Checkbox(String asset_group_ids) {
		List<Map<String, String>> result = new ArrayList<>();
		//根据资产组条件
		List<Equipment> equipmentList = equipmentDao.getEquipmentListByAssetGroupIds(asset_group_ids.split(","));
		for(Equipment equipment:equipmentList){
			Map<String,String> map = new HashMap<>();
			map.put(Constant.COMBOBOX_VALUE,equipment.getId());
			map.put(Constant.COMBOBOX_LABEL,equipment.getName());
			result.add(map);
		}
		return result;
	}

	@Override
	public List<Equipment> getEquipmentListByDashboardSet(String[] asset_group_ids, String[] asset_ids) {
		List<Equipment> equipmentList = equipmentDao.getEquipmentListByDashboardSet(asset_group_ids,asset_ids);
		return equipmentList;
	}

	@Override
	public List<Map<String, String>> getDashboardsInfo(String logType) {
		//最终要返回的菜单
		List<Map<String,String>> result = new ArrayList<>();
		result.addAll(DashboardConfig.SIEM);//菜单第一项都是SIEM
		if("metric".equals(logType)){
			result.addAll(DashboardConfig.METRIC_DASHBOARDS_ASSET);
		}else if("packet".equals(logType)){
			result.addAll(DashboardConfig.PACKET_DASHBOARDS_ASSET);
		}else if("syslog".equals(logType)){
			result.addAll(DashboardConfig.SYSLOG);
		}else if("winlog".equals(logType)){
			result.addAll(DashboardConfig.WINLOG);
		}else{
			//其他类型无报表
			result.clear();
		}
		return result;
	}

	@Override
	public String createEquipmentExportFile(String path){
		String fileName = "资产导出"+LocalDateTime.now().format(dtf_time_file) + ".xls";
		List<List<Map<String,String>>> equipmentList = equipmentDao.selectAllEquipmentExport();
		//多表关联数据返回后，结果放在最外层list的第一项中，具体可看拦截器部分代码
		List<Map<String,String>> list = equipmentList.get(0);
		//生成excel可接收数据
		List<List<Object>> excelDataList = new ArrayList<List<Object>>();
		for(Map<String,String> map:list){
			List<Object> row = new ArrayList<>();
			row.add(map.get("name"));//资产名称
			row.add(map.get("ip"));//资产IP
			row.add(map.get("hostName"));//主机名
			row.add(map.get("logType"));//日志类型
			row.add(map.get("log_level"));//日志级别
			row.add(Constant.EQUIPMENT_TYPE_EN.get(map.get("type")));//资产类型？
			row.add("1".equals(map.get("startUp"))?"启用":"未启用");//是否启用
			row.add(map.get("domain"));//根域名
			row.add(map.get("port"));//端口
			row.add(map.get("system"));//系统
			row.add(map.get("systemVersion"));//系统版本号
			row.add(map.get("assetNum"));//资产编号
			row.add(map.get("serialNum"));//序列好
			row.add(map.get("macAdress"));//MAC地址
			row.add(map.get("describe"));//描述
			row.add(map.get("valuation"));//资产价值
			row.add(map.get("userName"));//负责人
			row.add(map.get("phone"));//联系方式
			excelDataList.add(row);
		}
		//生成结果文件
		EasyExcel.write(path+fileName).withTemplate(path+EquipmentTemplateFileName).sheet().doWrite(excelDataList);
		return fileName;
	}


}
