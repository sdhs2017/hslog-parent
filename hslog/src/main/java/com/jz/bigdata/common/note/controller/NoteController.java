package com.jz.bigdata.common.note.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.note.entity.Note;
import com.jz.bigdata.common.note.service.INoteService;
import com.jz.bigdata.util.DescribeLog;

/**
 * @author shichengyu
 * @date 2017年12月8日 下午2:31:44
 * @description
 */
@Controller
@RequestMapping("/note")
public class NoteController {
	
	@Resource(name = "NoteService")
	private INoteService noteService;
	
	@ResponseBody
	@RequestMapping("/selectAll")
	@DescribeLog(describe="查询所有日志")
	public List<Note> selectAll(Note note){
		return noteService.selectAll(note);
	}

	
	/**
	 * @param request
	 * @return 根据id删除数据
	 */
	@ResponseBody
//	@RequestMapping("/deletes")
	@RequestMapping(value="/deletes",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="删除操作记录")
	public String delete(HttpServletRequest request) {
		int result = 0;
//		获取id数组
		String[] ids = request.getParameter("ids").split(",");
		if (ids.length > 0) {
			result = this.noteService.delete(ids);
		}
		return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
	}
	
	/**
	 * @param request
	 * @return 删除所有数据
	 */
	@ResponseBody
//	@RequestMapping("/deleteAll")
	@RequestMapping(value="/deleteAll",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="删除所有操作记录")
	public String deleteAll() {
		int result =this.noteService.deleteAll();
		return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
	}
	
	/**
	 * @param request
	 * @return 数据备份
	 */
	@ResponseBody
//	@RequestMapping("/deleteAll")
	@RequestMapping(value="/backup",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="数据备份")
	public String backup() {
		int result =this.noteService.backup();
		return result >= 1 ? Constant.successMessage() : Constant.failureMessage();
	}

	
	/**
	 * @param request
	 * @return 数据还原
	 */
	@ResponseBody
//	@RequestMapping("/deleteAll")
	@RequestMapping(value="/restore",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="数据还原")
	public String restore() {
		int result =this.noteService.restore();
		if(result>=1){
			return Constant.successMessage();
		}else{
			return Constant.restoreMessage();
		}
		
	}
	
	
	@ResponseBody
	@RequestMapping(value="/selectByPage",produces = "application/json; charset=utf-8")
	@DescribeLog(describe="分页查询审计日志")
	public String selectByPage(HttpServletRequest request) {
		//获取参数
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		String account=request.getParameter("account");
		String userName=request.getParameter("userName");
		String departmentName=request.getParameter("departmentName");
		String ip=request.getParameter("ip");
		//页码数
		int pageIndex=Integer.parseInt(request.getParameter("pageIndex"));
		//每页显示的数量
		int pageSize=Integer.parseInt(request.getParameter("pageSize"));
		
		return noteService.selectByPage(startTime, endTime, account, userName, departmentName, ip, pageIndex, pageSize);
	}
	
	
	
}
