$(function(){
	var pageSize = 12;//每页显示条数
	//定义加载数据的类型  0-默认加载  1-精细加载
	var searchType = '0';
	//var getDeviceObj = sessionStorage.getItem("deviceObj");//获取本地存储的设备值 对象
	//获取存放在标签页标题中的数据
	var getDeviceObj = $('.page-tabs-content', parent.document).click().children("a.active").attr("data-obj");
	logStatus = true;//ture表明是搜索操作 false表示是过滤操作
	//将获取的本地sessionstorage字符串转换为obj对象格式
	var obj = JSON.parse(getDeviceObj.replace(/'/g,"\""));	
	//取出设备id
	var id = obj.deviceId;
	$(".top_title>span").html(obj.deviceName+"日志")
	var html = '';
	//定义循化 拼接设备列表信息
	for(var i in obj){
		html = '<tr>'  
             +      '<td class="device_name">'
             +      	'<span data-id="'+obj.deviceId+'">'+obj.deviceName+'</span>'
             +       '</td>'
             +       '<td class="device_hostname">'+obj.deviceHostname+'</td>'
             +       '<td class="device_type">'+obj.deviceType+'</td>'
             +       '<td class="device_logType">'+obj.deviceLogType+'</td>'
             +       '<td class="device_ip">'+obj.deviceIp+'</td>'
             +       '<td class="device_starttime">'+obj.deviceStarttime+'</td>' 
             +       '<td class="device_updatetime">'+obj.deviceUpdateTime+'</td>'
             +       '<td class="device_endtime">'+obj.deviceEndtime+'</td>'  
             +       '<td class="device_iswork">'+obj.deviceIsWorked+'</td>'                                                        	
             +   '</tr>'
	}
	//将设备列表添加到页面中
	$("#device_list>tbody").html(html);
	var url = "../../log/getLogListByEquipment.do";
	//获取日志
	//getLogs(url,id,'');
	getLogs(url,id,"",1,pageSize)
	//删除本地存储
	sessionStorage.removeItem('deviceObj');
	//搜索设备日志点击函数
	/* $("#searchLogs").click(function(){
		logStatus = true;
		//获得搜索关键词
		var searchWords = $(".searchWords").val();		
		//var url = "data2.json";
		//获取数据
		//getLogs(url,id,searchWords);
	}) */
	//检索历史li 绑定点击事件 点击查询
	$(".history_list").on("click","li",function(){
		logStatus = true;
		//获取当前点击的内容
		var searchWords = $(this).html();
		//var url = "data2.json";
		//获取数据
		//getLogs(url,id,searchWords);
	})
	
	//刷新按钮 点击事件
	$("#refreshLogs").click(function(){
		currentPage = 1;
		firstGet = true;//第一次请求标识
		getLogs(url,id,"",1,pageSize)
	})
	
	//时间控件
	$('#startTime').datetimepicker({  
	    format: 'yyyy-mm-dd hh:ii:ss',//定义时间格式  
	    autoclose: true,//选择好自动关闭  
//			    minView: 2,//只选择到小时  
	    language: 'zh-CN', //汉化   
	 });
	
	$('#endTime').datetimepicker({  
	    format: 'yyyy-mm-dd hh:ii:ss',//定义时间格式  
	    autoclose: true,//选择好自动关闭  
//			    minView: 2,//只选择到小时  
	    language: 'zh-CN', //汉化   
	 });
  
	//开始时间控件 值改变事件
     $('#startTime').datetimepicker().on('changeDate', function(ev){
     	
    	//删除结束时间控件
    	$('#endTime').datetimepicker('remove');
    	//获取开始时间
  		var startDate = $("#startTime").val();
    	//初始化结束时间控件数据
  		$("#endTime ").datetimepicker({
			format: 'yyyy-mm-dd hh:ii:ss',//定义时间格式  
		    autoclose: true,//选择好自动关闭  
//			    minView: 2,//只选择到小时  
			startDate: startDate,//开始时间
		    language: 'zh-CN', //汉化
   		});
  	}); 
   //结束时间控件 值改变事件
    $('#endTime').datetimepicker().on('changeDate', function(ev){
    	//删除开始时间控件
    	$('#startTime').datetimepicker('remove');
    	//获取结束时间
  		var endDate = $("#endTime .form-control").val();
    	$('#startTime').datetimepicker({  
		    format: 'yyyy-mm-dd hh:ii:ss',//定义时间格式  
		    autoclose: true,//选择好自动关闭  
//			    minView: 2,//只选择到小时  
			endDate:endDate,//结束时间
		    language: 'zh-CN', //汉化   
		 });			
  	});
   
   //日志类型  
   /* var typeArr = [
	   	['<option selected value=""></option>','<option value="emergency">emergency</option>','<option value="alert">alert</option>','<option value="critical">critical</option>','<option value="error">error</option>','<option value="warn">warn</option>','<option value="notice">notice</option>','<option value="info">info</option>','<option value="debug">debug</option>'],
   		['<option selected value=""></option>','<option value="error">error</option>','<option value="warn">warn</option>','<option value="info">info</option>','<option value="debug">debug</option>'],
   		['<option selected value=""></option>','<option value="error">error</option>']
	] */
	var typeArr = [
		['emergency','alert','critical','error','warn','notice','info','debug'],
		['error','warn','info','debug'],
		['error']
	]
	//获取日志类型
	var opt = obj.deviceLogType;
   //添加日志级别下拉框的内容列表
	if(opt == 'syslog' || opt == 'winlog' || opt == 'packetfilteringfirewall_log'){
    	$(".searchedLevelBox").val(typeArr[0].length+'个级别');
    	//往二级下拉框中循环添加内容 
	    for(var i in typeArr[0]){
	    	$(".levelListBox ul").append('<li><input type="checkBox" checked value="'+typeArr[0][i]+'"/>'+typeArr[0][i]+'</li>')
	    }			    
    }else if(opt == 'log4j'){
    	$(".searchedLevelBox").val(typeArr[1].length+'个级别');
    	//往二级下拉框中循环添加内容 
	    for(var i in typeArr[1]){
	    	$(".levelListBox ul").append('<li><input type="checkBox" checked value="'+typeArr[1][i]+'"/>'+typeArr[1][i]+'</li>')
	    }
    }else if(opt == 'mysql'){
    	$(".searchedLevelBox").val(typeArr[2].length+'个级别');
    	//往二级下拉框中循环添加内容 
	    for(var i in typeArr[2]){
	    	$(".levelListBox ul").append('<li><input type="checkBox" checked value="'+typeArr[2][i]+'"/>'+typeArr[2][i]+'</li>')
	    }
    }else{
    	$(".searchedLevelBox").val('');
    }
	//日志级别获得焦点事件
	$(".searchedLevelBox").focus(function(){
		$(".levelListBox").css("display","block")
		//创建遮罩
		$("body").append('<div class="zzDiv"></div>')
	})
	//日志级别失去焦点事件
	/* $(".searchedLevelBox").blur(function(event){
		$(".levelListBox").css("display","none")
	}) */
	//日志级别列表点击事件
	$(".levelListBox").on("click","li",function(){
		if($(this).children("input").prop("checked") == true){
			$(this).children("input").removeAttr("checked")
		}else{
			$(this).children("input").prop("checked","checked")
		}
	})
	var levels = '';//用于存储选中的日志类型
	//遮罩点击事件
	$("body").on("click",".zzDiv",function(){
		//获得选中的日级别
		levels = $(".levelListBox input:checkbox:checked").map(function(index,elem) {
	        return $(elem).val();
	    }).get().join(',');
		//填充选中的值
		if(levels.split(',') == ''){
			$(".searchedLevelBox").val()
		}else{
			$(".searchedLevelBox").val(levels.split(',').length+'个级别')	
		}
		//删除遮罩
		$(".zzDiv").remove();
		//隐藏级别列表
		$(".levelListBox").css("display","none")
	})
   
	//日期清除
	   $(".startTimeRemove").click(function(){
		   $("#startTime").val("")
	   })
	   $(".endTimeRemove").click(function(){
		   $("#endTime").val("")
	   })
	 //带条件的检索 参数对象集合  
	 var sendObj = {};  
	//点击检索  查询日志
	$("#searchLogs").click(function(){
		currentPage = 1;
		logStatus = true;			
		firstGet = true;//第一次请求标识
		//获得查询条件参数值
		//获取开始时间
		sendObj.starttime = $("#startTime").val();
		//获取结束时间
		sendObj.endtime = $("#endTime").val();
		//获取日志级别
		sendObj.operation_level= levels;
		//资产id
		sendObj.id = id;
		//查询日志
		getLogs("../../log/getLogListByEquipment.do","",sendObj,1,pageSize)
		searchType = '1';
		historySearch(sendObj)
	})
	//检索历史记录函数  obj-检索条件
	function historySearch(obj){
		//定义检索条件 用于页面显示
		var searchCon = '';
		//循环检验查询条件  不为空则添加
		for (var i in obj){
			if(obj[i] != '' && i != "id" && i != "param" && i != "order"&& i != "page"&& i != "size"){
				searchCon += obj[i]+';';
			}
		}
		//添加到检索历史列表 不为空且不存在
		if (searchCon != '' && historyArr.indexOf(searchCon) === -1){
			var searchHtml = '<li title="'+searchCon+'" data-startTime="'+obj.starttime+'" data-endTime="'+obj.endtime+'" data-level="'+obj.operation_level+'">'+searchCon+'<i class="glyphicon glyphicon-remove" title="删除"></i></li>';
			$(".history_list").prepend(searchHtml);
			historyArr.push(searchCon);
		}
	}
	
	//检索历史li 绑定点击事件 点击查询
	$(".history_list").on("click","li",function(){
		currentPage = 1;
		//定义第一次请求
		firstGet = true;
		//获取当前点击的内容
		var searchWords = $(this).text();
		var ev = arguments.callee.caller.arguments[0] || window.event;
	    var $this =$(ev.target); 
	   	if($this[0].nodeName == "LI" ){ 
	   		logStatus = true;	
	   		sendObj.starttime = $(this).attr("data-startTime");
	   		sendObj.endtime = $(this).attr("data-endTime");
	   		sendObj.operation_level = $(this).attr("data-level");
	   		//获取数据
	   		getLogs("../../log/getLogListByEquipment.do","",sendObj,1,pageSize)
	   	}
		
	})
	
	//删除按钮（批量）日志  点击事件 函数
	$("#removeLogs").click(function(){
		//获取被选择的复选框
		var checkboxs = $("#logs_list>tbody input[type=checkbox]:checked");
		var logsId = '';
		var logsType = $(".device_logType").html();
		//判断是否又被选中的  没有则提示 有则继续执行删除操作
		if(checkboxs.length == 0){
			layer.msg('未选中任何日志',{icon: 5});
		}else{
			//循环拼接id值
			for(var i = 0; i< checkboxs.length;i++){
				//拼接日志Id
				logsId += $(checkboxs[i]).parents("tr").attr("data-logid")+',';
			}	
			//调用删除函数
			removeLogs(logsId,logsType)
		}
	})
	//单个日志删除
	$("#logs_list").on("click",".removeLog",function(){
		var logId = $(this).parents("tr").attr("data-logid");
		var logtype = $(".device_logType").html();
		removeLogs(logId,logtype)
	})

	//删除日志
	function removeLogs(logsId,logsType){
		currentPage = 1;
		//参数
		var obj = {};
		obj.index = "estest";
		obj.type = logsType;
		obj.id = logsId;
		//询问框
		layer.confirm('您确定删除日志信息么？', {
		  btn: ['确定','取消'] //按钮
		}, function(index){
			layer.close(index);
			$.ajax({
				type:"post",
				url:"../../log/deleteById.do",
				data:obj,
				async:false,
				success:function(data){
					if(data == "DELETED"){
						layer.msg('删除成功',{icon: 1});
						firstGet = true;//第一次请求标识
						//加载日志数据列表
						if(searchType == "0"){//0-默认加载  1-精细加载
							//查询日志
							getLogs(url,id,'',1,pageSize)
						}else if(searchType == "1"){
							//查询日志
							getLogs("../../log/getLogListByEquipment.do","",sendObj,1,pageSize)
						}else{
							layer.msg('加载数据错误',{icon: 5});
						}
					}else{
						layer.msg('删除失败',{icon: 5});
					}	
					

				},
				error:function(err){
					layer.msg('删除失败',{icon: 5});
					console.log(err)
				}
			})
			//关闭弹窗
			layer.close();
		}, function(){
		  layer.close();
		});
	}
})
//数据最大显示下拉框改变事件
$(".maxShowData").change(function(){
	maxShowChange('数据量过大时，细化查询条件提高查询效率')
})

//点击开启拖选按钮
$(".openSelect").click(function(){
	//鼠标拖选文字功能
    $(".equipmentLogBody .logs_Mes").selectText({
		"sFunc":function(obj){
			/* var index = obj.eventDom.attr("data-index");
			var sendObj = {};
			sendObj.name = obj.inputText;
			sendObj.feature = obj.selectedText;
			sendObj.userId = JSON.parse(localStorage.getItem('LoginUser'))[0].id;
			sendObj.equipmentId = logDetailArr[index].equipmentid;
			sendObj.type = logDetailArr[index].type;
			sendObj.equipmentUserId = logDetailArr[index].userid;
			//console.log('选中文本：'+selectedText+' 输入文本：'+inputText)
			//成功回调函数
			var sfunc = function(data){
				if(data.success == "true"){
					layer.msg(data.message,{icon: 1});
					//重新加载数据
					getData();
				}else if(data.success == "false"){//失败
					layer.msg(data.message,{icon: 5});
				} 
			}(,)
			ajaxPost('../../action/insert.do',sendObj,sfunc); */
			console.log(logDetailArr[obj.eventDom.attr("data-index")])
			//创建特征到页面中
			$(".keywordsBox").append('<span>'+obj.selectedText+'<i class="glyphicon glyphicon-remove removeKeywords"></i></span>');
			$(".keywordsBox").attr("log-type",logDetailArr[obj.eventDom.attr("data-index")].type);
			//鼠标悬停在动作特征除 显示删除图表
			$(".keywordsBox span").hover(function(){
				$(this).children("i").css("display","block");
			},function(){
				$(this).children("i").css("display","none");
			})
			//点击删除动作特征
			$(".removeKeywords").click(function(){
				$(this).parent().remove();
			})
		}
	});
})
//添加动作提交按钮
$(".actionCommit").click(function(){
	var sendObj = {};
	var keywords = '';
	
	//获取动作名称
	sendObj.name = $(".actionName").val();
	//获取动作特征
	var keywordsArr = $(".keywordsBox span");
	for(var i=0;i < keywordsArr.length;i++){
		keywords += $(keywordsArr[i]).text()+'@#$' ;
	}
	sendObj.feature = keywords;
	//创建人
	sendObj.userId = JSON.parse(localStorage.getItem('LoginUser'))[0].id;
	//日志类型
	sendObj.type = $(".keywordsBox").attr("log-type");
	if(sendObj.name == ''){
		layer.msg('动作名称不能为空',{icon: 5});
	}else if(sendObj.feature == ''){
		layer.msg('动作特征不能为空',{icon: 5});
	}else{
		//成功回调函数
		var sfunc = function(data){
			if(data.success == "true"){
				layer.msg(data.message,{icon: 1});
				//隐藏添加动作弹窗
				//$(".actionWapper").css("display","none");
				//清空数据
				$(".actionName").html('');
				$(".keywordsBox").html('');
			}else if(data.success == "false"){//失败
				layer.msg(data.message,{icon: 5});
			} 
		}
		ajaxPost('../../action/insert.do',sendObj,sfunc);
	}
})
//取消添加动作弹窗
$(".actionCancle").click(function(){
	//隐藏添加动作弹窗
	$(".actionWapper").css("top","-340px");
	//改变开关状态
	$('[name="status"]').bootstrapSwitch('state', false);
	//清空数据
	$(".actionName").html('');
	$(".keywordsBox").html('');
	//关闭拖选
	$(".equipmentLogBody .logs_Mes").off('mouseup');
})

//拖选开关
$('[name="status"]').bootstrapSwitch({  
    onText:"开启",  
    offText:"关闭",  
    onColor:"success",  
    offColor:"warning",  
    size:"small",  
    onSwitchChange:function(event,state){  
	   if(state==true){  
		   $(".actionWapper").css("top","10px");
		   //鼠标拖选文字功能
		   $(".equipmentLogBody .logs_Mes").selectText({
				  "sFunc":function(obj){
					//创建特征到页面中
					$(".keywordsBox").append('<span>'+obj.selectedText+'<i class="glyphicon glyphicon-remove removeKeywords"></i></span>');
					$(".keywordsBox").attr("log-type",logDetailArr[obj.eventDom.next().attr("data-index")].type);
					//鼠标悬停在动作特征除 显示删除图表
					$(".keywordsBox span").hover(function(){
						$(this).children("i").css("display","block");
					},function(){
						$(this).children("i").css("display","none");
					})
					//点击删除动作特征
					$(".removeKeywords").click(function(){
						$(this).parent().remove();
					})
			   }
		   }); 
	    }else{  
	    	$(".actionWapper").css("top","-340px");
	    	//清空数据
	    	$(".actionName").html('');
	    	$(".keywordsBox").html('');
	    	//关闭拖选
	    	$(".equipmentLogBody .logs_Mes").off('mouseup');;
	    }
    }  
})  


