	var url = '../../event/insert.do';//添加事件链接
	//获取动作列表数据
	function getActionList(obj){
		$.ajax({
			url:'../../action/selectAllByType.do',
			data:obj,
			type:"post",  //提交方式  
	        dataType:"json", //数据类型  
			success:function(data){
				//填充数据
				var html = '';
				for(var i in data[0]){
					var obj = data[0][i];
					//拼接
					html += '<tr data-id="'+obj.id+'" class="actionTr">'
		                 +		'<td class="action_name">'+obj.name+'</td>'
		                 +		'<td class="action_user">'+obj.userName+'</td>'
		                 /* +		'<td class="action_eqName">'+obj.equipmentName+'</td>' */
		                 +		'<td class="action_logType">'+obj.type+'</td>'
		                 +		'<td class="action_keyWords">'+obj.feature+'</td>'
		                /*      +		'<td class="action_eqUser">'+obj.equipmentUserName+'</td>' */
		                 +	'</tr>'
				}
		        $(".actionBox tbody").html(html);
		        
		      //添加鼠标拖拽排序问题		 
				 var list = document.getElementById("acl");
				 var sort1 = new Sortable(list,{
					 group:{
						 name:"actionBox",
						 pull:"clone",
						 put:false
					 },
					 forceFallback:true,
					 sort:false,
					 animation: 150
				 })						 
			}
		})
	}
	//初始化 日志类型参数
	var logTypeObj = {};
	logTypeObj.type = 'syslog';
	//获取动作列表
	getActionList(logTypeObj);
	//日志框改变事件
	$(".selectLogType").change(function(){
		//改变参数 获取动作列表
		logTypeObj.type = $(this).val();
		getActionList(logTypeObj)
	})
	
	//获取设备列表
	$.ajax({
		url:'../../equipment/selectAll.do',
		data:'',
		type:"post",  //提交方式  
	    dataType:"json", //数据类型  
		success:function(data){
			//填充数据
			var html = '';
			for(var i in data[0]){
				var obj = data[0][i];
				//拼接
				html += '<tr data-id="'+obj.id+'" class="equipmentTr">'
	                 +		'<td class="equipment_name">'+obj.name+'</td>'
	                 +		'<td class="equipment_hostname">'+obj.hostName+'</td>'
	                 +		'<td class="equipment_type">'+obj.type+'</td>'
	                 +		'<td class="equipment_logType">'+obj.logType+'</td>'
	                 +		'<td class="equipment_ip">'+obj.ip+'</td>'
	                 +	'</tr>'
			}
	        $(".equipmentBox tbody").html(html);
	        
	      //添加鼠标拖拽排序问题		 
			 var eqlist = document.getElementById("eql");
			 var eqsort = new Sortable(eqlist,{
				 group:{
					 name:"equipmentBox",
					 pull:"clone",
					 put:false
				 },
				 forceFallback:true,
				 sort:false,
				 animation: 150
			 })						 
		}
	})
		
	 //资产框	 
	 var seqBox = document.getElementById("selectedEquipmentBox");
	 var seqsort = new Sortable(seqBox,{
		 group:{
			 name:"equipmentBox",
			 pull:false
		 },
		 animation: 150,
		 sort:false,
		 forceFallback:true,
		 //列表被添加元素事件
		 onAdd:function(evt){
			 //删除空结果提示
			 $(".selectedEquipmentBox .no-records-found").remove();
			 $(evt.item).append('<td><i class="glyphicon glyphicon-remove removeEquipment" title="删除"></i></td>');		
		 	 $(".layui-layer-setwin").remove();
		 } 
	 }); 
	//事件类型改变事件
	$(".eventType").change(function(){
		//关闭动作列表
		$(".closeActionBox").click();
		//判断事件类型 1-资产 0-系统
		if($(this).val() == '1'){
			//出现绑定资产框
			$(".selectEquipment").click();
		}else if($(this).val() == '0'){
			//移除绑定资产框
			$(".equipmentWarpper").css({"height":"0","padding":"0"});
			//移除资产列表
			$(".closeEquipmentBox").click();
		}
	})
	//选择资产按钮
	$(".selectEquipment").click(function(){
		//关闭动作列表
		$(".closeActionBox").click();
		//出现绑定资产框
		$(".equipmentWarpper").css({"height":"auto","padding":"10px"});
		//右侧滑出资产列表
		$(".equipmentBox").css("right","10px");
		$(".emptyBox").css("width","0");
		$(".selectedEquipmentBox").css("border","2px solid #e4956d");
	})
	//关闭资产列表
	$(".closeEquipmentBox").click(function(){
		//移除资产列表
		$(".equipmentBox").css("right","-52%");
		$(".emptyBox").css("width","20%");
		$(".selectedEquipmentBox").css("border","0");
	})
	//获得eventId（修改跳转的页面）
	var getEventId = sessionStorage.getItem("eventId");//获取本地存储的设备值 对象
	sessionStorage.removeItem("eventId");//删除本地缓存
	//如果getEventIdId == null 说明不是从资产列表跳转过来的
	if(getEventId != null){
		$(".secondA").html('修改事件');
		//成功回调函数
		var sfunc = function(data){//成功
			/* if(data.success == "true"){
				layer.msg(data.message,{icon: 1});
				//显示弹窗
				layHtml('edit',eventIndex,data);
				console.log(data)
			}else if(data.success == "false"){//失败
				layer.msg(data.message,{icon: 5});
			}  */
			//填充数据
			$(".top_title").html("修改事件 -‘"+data.event.name+"’");
			$(".eventName").val(data.event.name);//事件名
			$(".eventDes").val(data.event.message);//事件描述
			$(".eventType").val(data.event.event_classify);//事件类型
			$(".eventType").attr("disabled", true);//禁用事件类型
			if(data.event.event_classify == "1"){//资产事件
				$(".selectedEquipmentBox .no-records-found").remove();
				var ehtml = '';
				//出现绑定资产框
				$(".equipmentWarpper").css({"height":"auto","padding":"10px"});
				//拼接资产列表
				for(var i in data.equipment[0]){
					var obj = data.equipment[0][i];
					ehtml   = '<tr data-id="'+obj.id+'" class="equipmentTr">'
							 +	'<td class="equipment_name">'+obj.name+'</td>'
							 +	'<td class="equipment_hostName">'+obj.hostName+'</td>'
							 +	'<td class="equipment_type">'+obj.type+'</td>'
							 +	'<td class="equipment_logType">'+obj.logType+'</td>'
							 +	'<td class="equipment_ip">'+obj.ip+'</td>'
							 +	'<td><i class="glyphicon glyphicon-remove removeEquipment" title="删除"></i></td>'
							 + '</tr>'
					$("#selectedEquipmentBox").append(ehtml);
				}
			}
			if(data.event.state == 0){//状态为0-否  是否启用
				$(".yes[name='isUsed']").removeAttr("checked");
				$(".no[name='isUsed']").attr("checked","checked");
			}
			if(data.event.enabled == 0){//状态为0-否  是否告警						
				$(".alarmBox").css("height","0");
			}else{
				$(".no[name='isAlarm']").removeAttr("checked");
				$(".yes[name='isAlarm']").attr("checked","checked");
				//
				$(".alarmBox").css("height","152px");
			}
			$(".alarmName").val(data.event.safeStrategyName);//策略名
			$(".safe_month").val(data.event.month);//月
			$(".safe_day").val(data.event.day);//天
			$(".safe_hour").val(data.event.hour);//时
			$(".safe_min").val(data.event.minute);//分
			//动作列表
			//通过动作数组的长度 决定循环的次数
			for(var i = 0;i < data.action[0].length;i++){
				$(".no-records-found").remove();
				//通过遍历每一个动作对象 判断添加动作的位置
				for(var j in data.action[0]){
					//如果当前的动作对象与序列号相同则添加到页面中
					if(data.action[0][j].order == i){
						var actionLi = '';
						actionLi = '<tr data-id="'+data.action[0][j].id+'" class="actionTr">'
								 +	'<td class="action_name">'+data.action[0][j].name+'</td>'
								 +	'<td class="action_user">'+data.action[0][j].userName+'</td>'
								 +	'<td class="action_eqName">'+data.action[0][j].equipmentName+'</td>'
								 +	'<td class="action_logType">'+data.action[0][j].type+'</td>'
								 +	'<td class="action_keyWords">'+data.action[0][j].feature+'</td>'
								 +	'<td class="actionNum">'+data.action[0][j].number+'</td>'
								 +	'<td><i class="glyphicon glyphicon-edit edit" title="修改"></i><i class="glyphicon glyphicon-remove removeAction" title="删除"></i></td>'
								 + '</tr>'
						$("#selectedActionBox").append(actionLi);
					}
				}
				
			}
		}
		//失败回调函数
		var efunc = function(data){
			console.log(data)
			layer.msg('操作失败',{icon: 5});
		}
		//发送请求
		ajaxPost("../../event/selectEventAction.do ",{id:getEventId},sfunc,);
	}
	
	 //添加鼠标拖拽排序问题		 
	 var list = document.getElementById("selectedActionBox");
	 var sort2 = new Sortable(list,{
		 group:{
			 name:"actionBox",
			 pull:false
		 },
		 animation: 150,
		 forceFallback:true,
		 //列表被添加元素事件
		 onAdd:function(evt){
			 //删除空结果提示
			 $(".selectedActionBox .no-records-found").remove();
			 //定义初始次数
			 var actionNum = 1;		
			 //出现弹窗
			 layer.open({				
		 		type: 1,
		 		title:'动作次数',//标题
				area: ['250px', '170px'], //宽高
				btn: ['确定'], //按钮
				btn1:function(index){
					layer.close(index);
					actionNum = $(".layui-layer-input").val();
			    	$(evt.item).append('<td class="actionNum">'+actionNum+'</td><td><i class="glyphicon glyphicon-edit edit" title="修改"></i><i class="glyphicon glyphicon-remove removeAction" title="删除"></i></td>');								
				},
				content:'<input type="number" class="form-control layui-layer-input" style="margin-top: 10px;"  autofocus="autofocus" value="1">'
		 	})
		 	$(".layui-layer-setwin").remove();
		 } 
	 }); 
	 //修改动作次数按钮
	 $("#selectedActionBox").on("click",".edit",function(){
		 var $this = $(this);
		 var actionNum = $this.parent().siblings(".actionNum").html()
			//弹窗				 
		 	layer.open({				
		 		type: 1,
		 		title:'修改次数',//标题
				area: ['250px', '170px'], //宽高
				btn: ['确定'], //按钮
				btn1:function(index){
					layer.close(index);
					var num = $(".layui-layer-input").val();	
					$this.parent().siblings(".actionNum").html(num)
				},
				content:'<input type="number" class="form-control layui-layer-input" style="margin-top: 10px;"  autofocus="autofocus" value="'+actionNum+'">'
		 	})
	 })
	 //删除所选动作事件 - 删除所选资产事件 
	 $("#selectedActionBox,#selectedEquipmentBox").on("click",".removeAction,.removeEquipment",function(){
		var $this = $(this); 
		//询问框
		layer.confirm('确定删除？', {
		  btn: ['确定','取消'] //按钮
		}, function(index){
			layer.close(index);			
			if($this.parents('tbody').children('tr').length == 1){
				$this.parents('tbody').append('<tr class="no-records-found sortable-drag" draggable="false" style=""><td colspan="6">没有找到匹配的记录</td></tr>')
			}
			$this.parents('tr').remove();		
		}, function(){
			  layer.close();
		});
		  
	  })
	 
	//点击选择动作按钮 右边滑出动作列表
	$(".selectAction").click(function(){
		//移除事件列表
		$(".closeEquipmentBox").click();
		//滑出动作列表
		$(".actionBox").css("right","10px");
		$(".emptyBox").css("width","0");
		$(".selectedActionBox").css("border","2px solid #e4956d");
	})
	//关闭动作列表
	$(".closeActionBox").click(function(){
		$(".selectedActionBox").css("border","0");
		$(".actionBox").css("right","-52%");
		$(".emptyBox").css("width","20%");
	})
	
	//是否告警点击出现策略选项
	$("input[name='isAlarm']").click(function(){
		if($(this).val() == "1"){
			$(".alarmBox").css("height","152px");
		}else{
			$(".alarmBox").css("height","0");
		}
	})
	
	
	//添加月
	for(var i = 1;i <= 12;i++){
		$(".safe_month").append('<option value="'+i+'">'+i+'</option>');
	}
	//添加日
	for(var i = 1;i <= 30;i++){
		$(".safe_day").append('<option value="'+i+'">'+i+'</option>');
	}
	//添加时
	for(var i = 1;i <= 24;i++){
		$(".safe_hour").append('<option value="'+i+'">'+i+'</option>');
	}
	//添加分
	for(var i = 1;i <= 60;i++){
		$(".safe_min").append('<option value="'+i+'">'+i+'</option>');
	}
	
	//提交按钮 点击事件
	$(".submitParam").click(function(){
		//获取参数
		var acIds = "";//选中的动作Id
		//资产id
		var eqIds = "";
		//获得修改后的事件名
		var eventName = $(".eventName").val();
		//获得事件类型
		var eventType = $(".eventType").val();
		//是否启用状态
		var isUsedState = $("input[name='isUsed']:checked").val();
		//是否告警状态
		var isAlarmState = $("input[name='isAlarm']:checked").val();//enabled
		//获得事件描述
		var eventDes = $(".eventDes").val();
		//获得创建人
		var userId = JSON.parse(localStorage.getItem('LoginUser'))[0].id;
		//获得策略名称
		var alarmName = $(".alarmName").val();//safeStrategyName
		var month = $(".safe_month").val();
		//日
		var day = $(".safe_day").val();
		//时
		var hour = $(".safe_hour").val();
		//分
		var minute = $(".safe_min").val();
		//动作 拼接
		var actionList = $("#selectedActionBox .actionTr");
		for(var i = 0;i < actionList.length;i++){
			//id
			var id = $(actionList[i]).attr("data-id");
			//次数
			var num = $(actionList[i]).children(".actionNum").html()
			acIds += id+'-'+i+'-'+num+',';
		}
		//资产id拼接
		var equipmentList = $("#selectedEquipmentBox .equipmentTr");
		for(var i = 0;i < equipmentList.length;i++){
			//id
			var id = $(equipmentList[i]).attr("data-id");
			eqIds += id +',';
		}
		//判断值的合法性
		if(eventName == ''){//事件名值为空
			layer.msg('事件名称不能为空',{icon: 5});
		}else if(eventType == '1' && eqIds == ''){
			layer.msg('事件类型为资产事件，但未绑定资产',{icon: 5});
		}else if(acIds == ''){//动作不能为空
			layer.msg('未添加任何动作',{icon: 5});
		}else if(eventDes == ''){//动作不能为空
			layer.msg('事件描述为空',{icon: 5});
		}else{
			var obj = {};
			obj.name = eventName;
			obj.event_classify = eventType;
			obj.state = isUsedState;
			obj.enabled = isAlarmState;
			obj.message = eventDes;
			obj.userId = userId;
			obj.safeStrategyName = alarmName;
			obj.month = month;
			obj.day = day;
			obj.hour = hour;
			obj.minute = minute;
			obj.actionId = acIds;
			obj.equipmentIds = eqIds;
			if(getEventId != null){
				obj.id = getEventId;
				url = '../../event/updataById.do';
			}
			//成功函数
			var sFunc = function(data){
				if(data.success == "true"){//成功
					layer.msg(data.message,{icon: 1});
					//跳转到事件列表
					location.href="eventList.html"
					//关闭弹窗
					//layer.close(index);
				}else if(data.success == "false"){//失败
					layer.msg(data.message,{icon: 5});
				} 
			}
			ajaxPost(url,obj,sFunc);
		}
		
		
	})
	//返回按钮
	$(".goBack").click(function(){
		//跳转到事件列表
		location.href="eventList.html"
	})