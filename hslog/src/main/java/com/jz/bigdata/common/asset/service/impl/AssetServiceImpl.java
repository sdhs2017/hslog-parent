package com.jz.bigdata.common.asset.service.impl;

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
import com.jz.bigdata.common.asset.dao.IAssetDao;
import com.jz.bigdata.common.asset.entity.Asset;
import com.jz.bigdata.common.asset.service.IAssetService;
import com.jz.bigdata.roleauthority.user.dao.IUserDao;
import com.jz.bigdata.roleauthority.user.entity.User;
import org.springframework.stereotype.Service;

import com.jz.bigdata.business.logAnalysis.log.service.IlogService;
import com.jz.bigdata.common.Constant;

import com.jz.bigdata.util.BASE64Util;
import com.jz.bigdata.util.ConfigProperty;
import com.jz.bigdata.util.JavaBeanUtil;
import com.jz.bigdata.util.Uuid;

import net.sf.json.JSONArray;

/**

 * @description
 */
@Service(value = "AssetService")
public class AssetServiceImpl implements IAssetService {

	@Resource
	private IAssetDao assetDao;

	@Resource
	private IUserDao userDao;

	@Resource(name = "configProperty")
	private ConfigProperty configProperty;

	@Resource(name="logService")
	private IlogService logService;

	@Resource(name="ecsService")
	private IecsService ecsService;

	/**
	 * @param asset
	 * @return 添加数据
	 */
	@Override
	public int insert(Asset asset, HttpSession session) {

		// 获取总数
		List<Object> count = assetDao.count_Number();
		// 获取总数集合6

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		resultList = (List<Map<String, Object>>) count.get(0);

		BASE64Util base64Util = new BASE64Util();
		// 判断资产数是否超过限定
		if (Integer.valueOf((String) resultList.get(0).get("count")) < Integer
				.valueOf(base64Util.decode(configProperty.getNumber()).trim())) {


			Asset equ= assetDao.selectByNameIp(asset);
			//判断资产是否存在
			if(equ ==null){
				// 获取uuid
				asset.setId(Uuid.getUUID());
				User user = userDao.selectById(session.getAttribute(Constant.SESSION_USERID).toString());
				asset.setDepartmentId(user.getDepartmentId());
				asset.setUserId(session.getAttribute(Constant.SESSION_USERID).toString());
				asset.setDepartmentNodeId((int) session.getAttribute(Constant.SESSION_DEPARTMENTNODEID));
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
				// 获取日期
				asset.setCreateTime(df.format(new Date()));
//				asset.setState(1);
				assetDao.insert(asset);
				return 2;
			}
			return 1;
		} else {
			return 0;
		}

	}

	/**
	 * @param asset
	 * @return 查询数据
	 */
	@Override
	public String selectAll(Asset asset,HttpSession session) {
		String userId=session.getAttribute(Constant.SESSION_USERID).toString();
		String role=session.getAttribute(Constant.SESSION_USERROLE).toString();
		List<Asset> list = assetDao.selectAll(asset,role,userId);

		// list转json
		return JSONArray.fromObject(list).toString();
	}

	/**
	 * @param asset
	 * @return 修改数据
	 */
	@Override
	public int updateById(Asset asset, HttpSession session) {
		// 设置日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取日期
		asset.setUpDateTime(df.format(new Date()));
		asset.setDepartmentNodeId((int) session.getAttribute(Constant.SESSION_DEPARTMENTNODEID));
		// asset.setDepartmentId(Integer.valueOf(session.getAttribute(Constant.SESSION_DEPARTMENTID).toString()));
		// asset.setUserId(session.getAttribute(Constant.SESSION_USERID).toString());
		// if(asset)
		return assetDao.updateById(asset);
	}

	/**
	 * @param ids
	 * @return 删除数据
	 */
	@Override
	public int delete(String[] ids) {
		int result=0;
		result=assetDao.delete(ids);
		//assetDao.deleteEvent(ids);
		return result;
	}

	/**
	 * @param asset
	 * @return
	 * @description 根据id查询单一资产
	 */
	@Override
	public List<Asset> selectAsset(Asset asset) throws Exception {
		List<Asset> list = assetDao.selectAsset(asset);
		List<Asset> listAsset = (List<Asset>) list.get(0);

		List<Asset> myList = new ArrayList<>();

		if (listAsset.size()<1){
			return  myList;
		}
		// asset=listAsset.get(0);
		// System.out.println(asset.getConfidentiality());

		// 遍历资产，通过资产id查询该资产下当天的日志条数，时间范围当天的00:00:00到当天的查询时间
		// 设置日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat startdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String starttime = startdf.format(new Date());
		String endtime = df.format(new Date());
		Map<String, String> esMap = new HashMap<>();
		Map<String, Object> assetmap = (Map<String, Object>) listAsset.get(0);

		/*for (Entry<String,Object> ssSet : assetmap.entrySet()) {
			System.out.println(ssSet.getKey()+":"+ssSet.getValue());
		}*/

		String assetid = assetmap.get("id").toString();
		/*esMap.put("assetid", assetid);
		assetmap.put("log_count", logService.getCount(esMap,starttime,endtime,null,configProperty.getEs_index())+"");*/
		/**
		 * 新版本查询ecsService
		 */
		esMap.put("fields.assetid", assetid);
		esMap.put("fields.failure","false");
		assetmap.put("log_count", ecsService.getCount(esMap,starttime,endtime,configProperty.getEs_index())+"");

		asset = JavaBeanUtil.convertMapToBean(Asset.class, assetmap);

		myList.add(asset);
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
		List count = assetDao.count(hostName, name, ip, logType,type,session.getAttribute(Constant.SESSION_USERROLE).toString(),session.getAttribute(Constant.SESSION_USERID).toString());
		List listCount = new ArrayList<>();
		// 获取总数集合
		listCount = (List) count.get(0);

		Map<String, Object> map = new HashMap<String, Object>();
		// 总数添加到map
		map.put("count", (listCount.get(0)));
		// 查询所有数据
		List<Asset> listAsset = assetDao.selectAllByPage(hostName, name, ip, logType, type, session.getAttribute(Constant.SESSION_USERROLE).toString(),session.getAttribute(Constant.SESSION_USERID).toString(), startRecord,pageSize);
		// System.err.println(listAsset.get(0).getCreateTime());

		// 遍历资产，通过资产id查询该资产下当天的日志条数，时间范围当天的00:00:00到当天的查询时间
		// 设置日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat startdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String starttime = startdf.format(new Date());
		String endtime = df.format(new Date());
		for(Asset asset : listAsset) {
			Map<String, String> esMap = new HashMap<>();
			//esMap.put("assetid", asset.getId());
			//asset.setLog_count(logService.getCount(esMap,starttime,endtime,null,configProperty.getEs_index())+"");
			/**
			 * ECS的资产id改成fields.assetid
			 */
			esMap.put("fields.assetid", asset.getId());
			esMap.put("fields.failure","false");
			asset.setLog_count(ecsService.getCount(esMap,starttime,endtime,configProperty.getEs_index())+"");
		}
		// 数据添加到map
		map.put("asset", listAsset);
		return JSONArray.fromObject(map).toString();
	}

	/**
	 * @param asset
	 * @return
	 * @description 查询单个数据
	 */
	@Override
	public Asset selectOneAsset(Asset asset) {
		return assetDao.selectOneAsset(asset);
	}

	@Override
	public List<Asset> selectAllHostName() {
		return assetDao.selectAllHostName();
	}

	/**
	 * @return
	 * @description 查询所有数据map
	 *
	 */
	@Override
	public Map<String, Asset> selectAllAsset() {

		Map<String, Asset> map = new HashMap<String, Asset>();
		List<Asset> list = assetDao.selectAllHostName();
		Asset e;
		//String logType = "syslog";
		//key ip 逻辑资产 ip是唯一标识
		//TODO
		for (int i = 0; i < list.size(); i++) {
			map.put(list.get(i).getIp() , list.get(i));
		}
		return map;
	}

	@Override
	public Set<String> selectAllIPAdress() {

		Set<String> set = new HashSet<String>();
		List<Asset> list = assetDao.selectAllHostName();
		for (int i = 0; i < list.size(); i++) {
			set.add(list.get(i).getIp());
		}
		return set;
	}

	@Override
	public Map<String, String> selectLog_level() {

		Map<String, String> map = new HashMap<String, String>();
		List<Asset> list = assetDao.selectAllHostName();
		for (Asset asset : list) {
			map.put(asset.getId(), asset.getLog_level());
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
		return assetDao.upRiskById(id, high_risk, moderate_risk, low_risk);
	}

	/**
	 * @return
	 * @description 查询所有数据，用于安全策略
	 */
	@Override
	public List<Asset> selectAllAssetByRisk() {
		return assetDao.selectAllAssetByRisk();
	}

	/**
	 * @param list
	 * @return
	 * @description
	 * 批量修改数据
	 */
	@Override
	public int batchUpdate(List<Asset> list) {
		return assetDao.batchUpdate(list);
	}

	@Override
	public void initEquipmentCache() {

	}

}
