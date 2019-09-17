package com.jz.bigdata.common.junitTest;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jz.bigdata.common.action.entity.Action;
import com.jz.bigdata.common.action.service.IActionService;
import com.jz.bigdata.common.event.entity.Event;
import com.jz.bigdata.common.event.service.IEventService;
import com.jz.bigdata.util.Uuid;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring-mybatis.xml")
public class TestClass {
	
	@Resource(name = "EventService")
	private IEventService eventService;
	
	@Resource(name="ActionService")
	private IActionService actionService;
	
	@Test
	public void test01(){
		Event event =new Event();
		event.setId(Uuid.getUUID());
		event.setName("test");
		event.setUserId(Uuid.getUUID());
		event.setState(0);
		event.setMessage("描述");
		event.setEnabled(1);
		eventService.insert(event);
		System.out.println("11");
	}

	@Test
	public void test02(){
		Event event =new Event();
		event.setId(Uuid.getUUID());
		event.setName("test");
		event.setUserId(Uuid.getUUID());
		event.setState(0);
		event.setMessage("描述");
		event.setEnabled(1);
		eventService.insert(event);
		System.out.println("11");
	}
	
	@Test
	public void testAction(){
		List<Action> list=actionService.selectAllByType("syslog");
		System.out.println(list.size()+"    "+list.toString());
	}
}
