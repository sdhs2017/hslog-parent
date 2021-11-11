package com.jz.bigdata.common.note.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.note.dao.INoteDao;
import com.jz.bigdata.common.note.entity.Note;
import com.jz.bigdata.common.note.service.INoteService;

import net.sf.json.JSONArray;

/**
 * @author shichengyu
 * @date 2017年11月30日 下午2:39:58
 * @description
 */
@Service(value = "NoteService")
public class NoteService implements INoteService {

	@Resource
	private INoteDao noteDao;
	
	
	
	/**
	 * @param note
	 * @return
	 * @description
	 * 添加数据
	 */
	@Override
	public int insert(Note note) {
		note.setState(1);
		return noteDao.insert(note);
	}



	/**
	 * @param account
	 * @param startTime
	 * @param endTime
	 * @return
	 * @description
	 * 数据个数
	 */
	@Override
	public List<String> count(String account, String startTime, String endTime) {
		return noteDao.count(account, startTime, endTime);
	}



	/**
	 * @return
	 * @description
	 * 查询所有数据
	 */
	@Override
	public List<Note> selectAll(Note note) {
		return noteDao.selectAll(note);
	}



	/**
	 * @param ids
	 * @return
	 * @description
	 * 删除数据
	 */
	@Override
	public int delete(String[] ids) {
		return noteDao.delete(ids);
	}



	/**
	 * @return
	 * @description
	 * 删除所有数据
	 */
	@Override
	public int deleteAll() {
		return noteDao.deleteAll();
	}



	/**
	 * @return
	 * @description
	 * 数据备份
	 */
	@Override
	public int backup() {
		List<Note> name=noteDao.tableName();
		
//		获取总数集合
//		listName=(List) name.get(0);
//		System.out.println(listName.get(0));
	
		if(name.size()>0){
			noteDao.dropTable();
		}
		
		return noteDao.backup();
	}



	/**
	 * @return
	 * @description
	 * 数据还原
	 */
	@Override
	public int restore() {
		
		List<Note> name=noteDao.tableName();
		int num=0;
		if(name.size()>0){
			//涉密
			//将备份后的表
			noteDao.restore_security();
			num=1;
			//noteDao.dropTableNote();
			//num=noteDao.restore();
		}
		
		return num;
	}




	/**
	 * @param startTime
	 * @param endTime
	 * @param account
	 * @param userName
	 * @param departmentName
	 * @param ip
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @description
	 * 分页查询
	 */
	@Override
	public String selectByPage(String startTime,String endTime,String account,String userName,String departmentName,String ip,int pageIndex,int pageSize) {
//		获取起始数
		int startRecord =(pageSize*(pageIndex-1));
//		获取总数
		List count=noteDao.countByPage(startTime, endTime, account, userName, departmentName, ip);
		List listCount=new ArrayList<>();
//		获取总数集合
		listCount=(List) count.get(0);
		
		Map<String,Object> map =new HashMap<String,Object>();
//		总数添加到map
		map.put("count", (listCount.get(0)));
//		查询所有数据
		List<Note> listEquipment= noteDao.selectByPage(startTime, endTime, account, userName, departmentName, ip, startRecord, pageSize);
//		System.err.println(listEquipment.get(0).getCreateTime());
//		数据添加到map
		map.put("note", listEquipment);
		return JSONArray.fromObject(map).toString();
	}

	@Override
	public String selectByPage_Security(String startTime, String endTime, String account, String userName, String departmentName, String ip, int pageIndex, int pageSize, String[] roleids) {
		//获取起始数
		int startRecord =(pageSize*(pageIndex-1));
		//获取总数
		List count=noteDao.countByPage_Security(startTime, endTime, account, userName, departmentName, ip,roleids);
		List listCount=new ArrayList<>();
		//获取总数集合
		listCount=(List) count.get(0);

		Map<String,Object> map =new HashMap<String,Object>();
		//总数添加到map
		map.put("count", (listCount.get(0)));
		List<Note> listEquipment= noteDao.selectByPage_Security(startTime, endTime, account, userName, departmentName, ip, startRecord, pageSize,roleids);
		map.put("note", listEquipment);
		return JSONArray.fromObject(map).toString();
	}


}
