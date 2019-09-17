package com.jz.bigdata.common.action.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.springframework.stereotype.Service;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.action.dao.IActionDao;
import com.jz.bigdata.common.action.entity.Action;
import com.jz.bigdata.common.action.service.IActionService;
import com.jz.bigdata.common.action_event.dao.IAction_eventDao;
import com.jz.bigdata.common.ansj_dic.dao.IAnsj_dicDao;
import com.jz.bigdata.common.ansj_dic.entity.Dic;
import com.jz.bigdata.common.manage.service.IManageService;
import com.jz.bigdata.util.Uuid;


/**
 * @ClassName ActionServiceImpl
 * @Description 
 * @Author shi cheng yu
 * @Date 2018年9月28日 下午2:05:27
 */
@Service(value = "ActionService")
public class ActionServiceImpl implements IActionService{
	
	@Resource
	private IActionDao actionDao;
	@Resource
	private IAction_eventDao action_eventDao;
	@Resource
	private IAnsj_dicDao ansj_dicDao;
	@Resource(name="manageService")
	private IManageService iManageService;

	/**
	 * @param action
	 * @return
	 * @description
	 * 添加数据
	 */
	@Override
	public int insert(Action action) {
		action.setId(Uuid.getUUID());
		action.setState(1);
		action.setDeleteState(1);
		int result =actionDao.insert(action);
		if(result>0) {
			Result NLP =  NlpAnalysis.parse(action.getName());
			for(Term term : NLP.getTerms()){
				//System.out.println(term.getName()+"\t"+term.getNatureStr()+"\t"+term.getOffe());
				Dic dics=ansj_dicDao.selectByName(term.getName());
				if(null==dics) {
					Dic dic =new Dic();
					dic.setFreq(String.valueOf(term.getOffe()));
					dic.setNature(term.getNatureStr());
					dic.setName(term.getName());
					ansj_dicDao.insert(dic);
				}
			}
			Dic dics=ansj_dicDao.selectByName(action.getName());
			if(null==dics) {
				Dic dic =new Dic();
				dic.setFreq("1000");
				dic.setNature("userDefine");
				dic.setName(action.getName());
				ansj_dicDao.insert(dic);
			}
			
		}
		
		iManageService.doCutl("", "http://192.168.2.181:9200/_ansj/flush/dic?key=dic");
		
		return result;
	}

	/**
	 * @param action
	 * @return
	 * @description
	 * 查询数据
	 */
	@Override
	public List<Action> selectAll(HttpSession session) {
		String role =(String) session.getAttribute(Constant.SESSION_USERROLE);
		String userId=(String) session.getAttribute(Constant.SESSION_USERID);
		return actionDao.selectAll(role,userId);
	}

	/**
	 * @param action
	 * @return
	 * @description
	 * 修改数据
	 */
	@Override
	public int updateById(Action action) {
		return actionDao.updateById(action);
	}

	/**
	 * @param id
	 * @return
	 * @description
	 * 删除数据
	 */
	@Override
	public int delete(String id) {
		action_eventDao.deleteByActionId(id);
		return actionDao.delete(id);
	}

	/**
	 * @param eventId
	 * @return
	 * @description
	 * 根据eventid查询数据
	 */
	@Override
	public List<Action> selectActionByEventId(String eventId) {
		return actionDao.selectActionByEventId(eventId);
	}

	@Override
	public List<Action> selectAllByType(String type) {
		return actionDao.selectAllByType(type);
	}

}
