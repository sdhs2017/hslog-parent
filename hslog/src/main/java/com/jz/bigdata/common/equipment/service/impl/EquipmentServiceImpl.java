package com.jz.bigdata.common.equipment.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.jz.bigdata.business.logAnalysis.ecs.service.IecsService;
import com.jz.bigdata.roleauthority.user.dao.IUserDao;
import com.jz.bigdata.roleauthority.user.entity.User;
import org.springframework.stereotype.Service;

import com.jz.bigdata.business.logAnalysis.log.service.IlogService;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.equipment.dao.IEquipmentDao;
import com.jz.bigdata.common.equipment.entity.Equipment;
import com.jz.bigdata.common.equipment.service.IEquipmentService;

import com.jz.bigdata.util.BASE64Util;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.JavaBeanUtil;
import com.jz.bigdata.util.Uuid;

import net.sf.json.JSONArray;

/**
 * @author shichengyu
 * @date 2017年8月16日 下午2:39:47
 * @description
 */
@Service(value = "EquipmentService")
public class EquipmentServiceImpl implements IEquipmentService {

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
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
				// 获取日期
				equipment.setCreateTime(df.format(new Date()));
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
		// 设置日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取日期
		equipment.setUpDateTime(df.format(new Date()));
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
		return result;
	}

	/**
	 * @param equipment
	 * @return
	 * @description 根据id查询单一资产
	 */
	@Override
	public List<Equipment> selectEquipment(Equipment equipment) throws Exception {
		List<Equipment> list = equipmentDao.selectEquipment(equipment);
		List<Equipment> listEquipment = (List<Equipment>) list.get(0);

		List<Equipment> myList = new ArrayList<>();

		if (listEquipment.size()<1){
			return  myList;
		}
		// equipment=listEquipment.get(0);
		// System.out.println(equipment.getConfidentiality());

		// 遍历资产，通过资产id查询该资产下当天的日志条数，时间范围当天的00:00:00到当天的查询时间
		// 设置日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat startdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String starttime = startdf.format(new Date());
		String endtime = df.format(new Date());
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
		esMap.put("fields.failure","false");
		equipmentmap.put("log_count", ecsService.getCount(esMap,starttime,endtime,configProperty.getEs_index())+"");

		equipment = JavaBeanUtil.convertMapToBean(Equipment.class, equipmentmap);

		myList.add(equipment);
		return myList;
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
	 * @return
	 */
	@Override
	public String selectAllByPage(String hostName, String name, String ip, String logType,String type, int pageIndex, int pageSize,HttpSession session) throws Exception {
		// 获取起始数
		int startRecord = (pageSize * (pageIndex - 1));
		// 获取总数
		List count = equipmentDao.count(hostName, name, ip, logType,type,session.getAttribute(Constant.SESSION_USERROLE).toString(),session.getAttribute(Constant.SESSION_USERID).toString());
		List listCount = new ArrayList<>();
		// 获取总数集合
		listCount = (List) count.get(0);

		Map<String, Object> map = new HashMap<String, Object>();
		// 总数添加到map
		map.put("count", (listCount.get(0)));
		// 查询所有数据
		List<Equipment> listEquipment = equipmentDao.selectAllByPage(hostName, name, ip, logType, type, session.getAttribute(Constant.SESSION_USERROLE).toString(),session.getAttribute(Constant.SESSION_USERID).toString(), startRecord,pageSize);
		// System.err.println(listEquipment.get(0).getCreateTime());

		// 遍历资产，通过资产id查询该资产下当天的日志条数，时间范围当天的00:00:00到当天的查询时间
		// 设置日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat startdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String starttime = startdf.format(new Date());
		String endtime = df.format(new Date());
		for(Equipment equipment : listEquipment) {
			Map<String, String> esMap = new HashMap<>();
			//esMap.put("equipmentid", equipment.getId());
			//equipment.setLog_count(logService.getCount(esMap,starttime,endtime,null,configProperty.getEs_index())+"");
			/**
			 * ECS的资产id改成fields.equipmentid
 			 */
			esMap.put("fields.equipmentid", equipment.getId());
			//esMap.put("fields.failure","true");//资产显示页面也需要显示未范式化的资产
			equipment.setLog_count(ecsService.getCount(esMap,starttime,endtime,configProperty.getEs_index())+"");
		}
		// 数据添加到map
		map.put("equipment", listEquipment);
		return JSONArray.fromObject(map).toString();
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

}
