package com.jz.bigdata.common.assets_old.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.jz.bigdata.common.assets_old.dao.IAssetsDao;
import com.jz.bigdata.common.assets_old.entity.Assets;
import com.jz.bigdata.common.assets_old.service.IAssetsService;
import net.sf.json.JSONArray;


@Service(value = "assetsService")
public class AssetsServiceimpl implements IAssetsService{
	
	@Resource
	private IAssetsDao assetsDao;
	
	private String oldDate;
	
	/**
	 * @param assets
	 * @return
	 * @description
	 * 添加数据
	 */
	@Override
	public int insert(Assets assets) {
		return assetsDao.insert(assets);
	}

	/**
	 * @return
	 * @description
	 * 查询所有数据
	 */
	@Override
	public List<Assets> selectAll() {
		return assetsDao.selectAll();
	}

	/**
	 * @param assets
	 * @return
	 * @description
	 * 更新数据
	 */
	@Override
	public int updateById(Assets assets) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取日期
		assets.setUpDateTime(df.format(new Date()));
		return assetsDao.updateById(assets);
	}

	/**
	 * @param ids
	 * @return
	 * @description
	 * 删除数据
	 */
	@Override
	public int delete(String[] ids) {
		return assetsDao.delete(ids);
	}

	/**
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @description
	 * 分页查询
	 */
	@Override
	public String selectAllByPage(int pageIndex, int pageSize,Assets assets) {
		// 获取起始数
		int startRecord = (pageSize * (pageIndex - 1));
		// 获取总数
		List count = assetsDao.count(assets);
		List listCount = new ArrayList<>();
		// 获取总数集合
		listCount = (List) count.get(0);

		Map<String, Object> map = new HashMap<String, Object>();
		// 总数添加到map
		map.put("count", (listCount.get(0)));
		// 查询所有数据
		List<Assets> listAssets = assetsDao.selectAllByPage(startRecord,pageSize,assets);
		// System.err.println(listEquipment.get(0).getCreateTime());
		// 数据添加到map
		map.put("assets", listAssets);
		return JSONArray.fromObject(map).toString();
	}



	/**
	 * @param assets
	 * @return
	 * @description
	 * 查询单个数据
	 */
	@Override
	public Assets selectOneAssets(Assets assets) {
		return assetsDao.selectOneAssets(assets);
	}

	/**
	 * @return
	 * @description
	 * 扫描增量数据 
	 */
	@Override
	public List<Assets> selectByIncrement() {
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date =format.format(new Date());
		
		if(oldDate==null||("").equals(oldDate)){
			oldDate=date;
		}
		String oldTime=oldDate;
		List<Assets> list  = assetsDao.selectByIncrement(oldTime, date);
		oldDate=date;
		return list;
	}

	/**
	 * @param id
	 * @param state
	 * @param upDateTime
	 * @return
	 * @description
	 * 修改状态
	 */
	@Override
	public int updateState(String id, String state, String upDateTime) {
		return assetsDao.updateState(id, state, upDateTime);
	}
	


}
