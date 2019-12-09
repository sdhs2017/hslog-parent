$(function(){
	//加载日志类型数据下拉框
	$.ajax({
		url:'../../staticdata/staticData.json',
		type:"get",  //提交方式  
        dataType:"json", //数据类型  
		success:function(data){
			//$(".eLogType")
			var logTypeArr = data.logType;
			for(var i in logTypeArr){
				var logTypOpt = '';
				logTypOpt = '<option value="'+logTypeArr[i]+'">'+logTypeArr[i]+'</option>'
				$(".logType").append(logTypOpt)
			}
		}
	});
	
	//资产类型改变事件
	//二级下拉框 联动 数据源 
	var dTypeArr = [
		['<option value="0101">交换机</option>','<option value="0102">路由器</option>'],
		['<option value="0201">IPS</option>','<option value="0202">IDS</option>','<option value="0203">抗DDOS</option>','<option value="0204">防火墙</option>'],
		['<option value="0301">Windows</option>','<option value="0302">Linux</option>','<option value="0303">虚拟化</option>'],
		['<option value="0401">Tomcat</option>','<option value="0402">Apache</option>','<option value="0403">IIS</option>','<option value="0404">Weblogic</option>','<option value="0405">Mysql</option>','<option value="0406">Oracle</option>','<option value="0407">Sqlserver</option>','<option value="0408">Db2</option>']
	]
	//二级下拉框 函数
	$(".eBigType").change(function(){
		//获取一级下拉框中的值
	    var opt=$(".eBigType").val();
	    //清空二级下拉框中的内容
	    $(".eType").html('');
	    if(opt != ''){
	    	//往二级下拉框中循环添加内容 
		    for(var i in dTypeArr[opt]){
		    	$(".eType").append(dTypeArr[opt][i])
		    }		   
	    }
	    
	});
});

var pageIndex = 1;//页码
var pageSize = 15;//每页显示的条数
var searchStutus = false;//用于判断是否是搜索
var eId =  localStorage.getItem("equipmentid");
if(eId != null){
	var sfunc = function(data){
		if(data.success == 'false'){
			layer.msg(data.message,{icon:5});
		}else{
			//拼接到页面
			jointDeviceList(data.equipment);
			//更改检索总数
			$(".allCountBox>b").html(1);
		}	
	}
	ajaxPost("../../equipment/selectEquipmentByLog.do",{id:eId},sfunc);
	//删除本地缓存日志
	localStorage.removeItem("equipmentid");
}else{
	//页面加载时  加载谁被列表
	getDeviceData(pageIndex);
}

//获取设备数据函数 
function getDeviceData(pageIndex,obj,url){
	
	var sendObj = {};//传参的参数集合	
	//初始值为空
	sendObj.name = '';
	sendObj.hostName = '';
	sendObj.ip = '';
	sendObj.logType = '';
	searchStutus = false;//用于表明是否是搜索发起的请求
	var obj = obj;
	//判断是否传obj参数  是则说明是搜索查询  不是则是默认加载
	if(obj != undefined){
		//赋值
		sendObj.name = obj.name;
		sendObj.hostName = obj.hostName;
		sendObj.ip = obj.ip;
		sendObj.logType = obj.logType;
	}
	
	sendObj.pageIndex = pageIndex;//
	sendObj.pageSize = pageSize;
	var sfunc = function(data){
		//设备数据
		var deviceData = data[0].equipment;
		if(pageIndex == 1){
			//定义第一次请求
			num = false;
			//总条数
			var count = data[0].count.count;
			//添加到页面中
			$(".allCountBox>b").html(count);
			//总页数
			var pageCount = Math.ceil(count/pageSize);
			//创建分页
			creatPage(pageCount);
			//拼接到页面
			jointDeviceList(deviceData);
			num = true;
		}else{
			//拼接到页面
			jointDeviceList(deviceData);
		}
		
	}
	//发送请求
	ajaxPost("../../equipment/selectPage.do",sendObj,sfunc);
}

//创建分页函数   pageCount-总页数
function creatPage(pageCount){
	$(".tools_page").pagination1(pageCount, {
			num_edge_entries: 1, //边缘页数
			num_display_entries: 4, //主体页数
			callback: pageselectCallback,
			items_per_page: 1, //每页显示1项
			/*prev_text: "&laquo;",
			next_text: "&raquo;"*/
			prev_text: "上一页",
			next_text: "下一页"
		});
}
//分页回调函数
function pageselectCallback(page_index){
	
	$("#theadCheck").removeAttr("checked"); 
	if(num == true){
		//判断是否是搜索产生的分页  false-不是
		if(searchStutus == false){
			getDeviceData(page_index+1);
		}else{
			//调用函数显示数据
			searchDevice(page_index+1);
		}
		
	}
	
}
//点击添加设备按钮 函数
$("#add_device").click(function(){
	//deviceLayerHtml();
	//跳转到资产添加页面
	location.href="addProperty.html";
})
//点击添加设备按钮 函数
$("#snmp_device").click(function(){
	//deviceLayerHtml();
	//跳转到资产添加页面
	location.href="deviceSnmp.html";
})
//设备修改 点击事件 函数 list_con
$(".list_con").on("click",".device_revise",function(){
	//定义按钮类型
	var btnType = "revise";
	//定义对象 要修改的数据
	var obj = {};
	//获取设备id
	var id = $(this).parent().attr("data-id");
	// 储存在本地
	sessionStorage.setItem("propertyId", id);
	//跳转到 添加资产页面
	location.href="addProperty.html";
})

//设备删除 按钮点击事件
$(".list_con").on("click",".device_remove",function(){
	//获取设备id
	var deviceId = $(this).parent().attr("data-id");
	var deviceObj = {};
	deviceObj.id = deviceId;
	//询问框
	layer.confirm('您确定删除么？', {
	  btn: ['确定','取消'] //按钮
	}, function(index){
		layer.close(index);
		 //发送数据到后台 进行删除
		 //成功回调函数
		var sfunc = function(data){
			layer.msg("删除成功",{icon:1});
			//重新加载谁被列表
			getDeviceData(1);
		}
		//失败回调函数
		var efunc = function(data){
			layer.msg("删除失败",{icon:5});
			console.log(data);
		}
		//发送请求
		ajaxPostWithOutLayer("../../equipment/delete.do",deviceObj,sfunc,"",efunc)
		//关闭弹窗
		layer.close();
	}, function(){
	  layer.close();
	});
})
//设备日志索引 用于计数
var htmlNum = 0;
//设备查看日志 按钮点击事件
$(".list_con").on("click",".device_logs",function(){
	//将设备信息存放到本地sessionStorage中
	var obj = {};
	obj.deviceId = $(this).parent().attr("data-id");
	obj.deviceName = $(this).parents('.li-content').siblings(".li-title").children(".device_name").html();
	obj.deviceHostname = $(this).parents('.li-content').find(".device_hostname").children('.li-value').html();
	obj.deviceIp = $(this).parents('.li-content').find(".device_ip").children('.li-value').html();
	obj.deviceType = $(this).parents('.li-content').find(".device_type").children('.li-value').html();
	obj.deviceLogType = $(this).parents('.li-content').find(".device_logType").children('.li-value').html();
	obj.deviceStarttime = $(this).parents('.li-content').find(".device_starttime").children('.li-value').html();
	obj.deviceUpdateTime = $(this).parents('.li-content').find(".device_updatetime").children('.li-value').html();
	obj.deviceEndtime = $(this).parents('.li-content').find(".device_endtime").children('.li-value').html();
	obj.deviceIsWorked = $(this).parents('.li-content').find(".device_iswork").children('.li-value').html();
	//将对象转换为字符串
	var objstr = JSON.stringify(obj);
	// 储存在本地
	//sessionStorage.setItem("deviceObj", objstr);
	//将  " 替换成 '
	objstr = objstr.replace(/"/g,"'");
	//拼接导航
	var html ='<a href="javascript:;" class="active J_menuTab" data-obj="'+objstr+'" data-id="equipmentLogs'+htmlNum+'">'+obj.deviceName+'日志<i class="fa fa-times-circle"></i></a>'
	//移除导航菜单选中属性
	$('.page-tabs-content', parent.document).click().children("a").removeClass("active");
	//添加导航菜单
	$('.page-tabs-content', parent.document).click().append(html);
	var iframe = '<iframe class="J_iframe" width="100%" height="100%" src="logPro/equipmentLogs.html" frameborder="0" data-id="equipmentLogs'+htmlNum+'" seamless="" style="display: inline;"></iframe>'
	//移除其他页面
	$('#content-main', parent.document).click().children("iframe").hide();
	$('#content-main', parent.document).click().append(iframe);
	//location.href="../logPro/searchLogs.html";
	htmlNum++;	
})
//设备报表  计数
var htmlNum1 = 0;
//单个设备  查询报表按钮
$(".list_con").on("click",".device_echarts",function(){
	//将设备信息存放到本地sessionStorage中
	var obj = {};
	obj.deviceId = $(this).parent().attr("data-id");
	obj.deviceName = $(this).parents('.li-content').siblings(".li-title").children(".device_name").html();
	obj.logType = $(this).parents('.li-content').find(".device_logType").children('.li-value').html();
	//将对象转换为字符串
	var objstr = JSON.stringify(obj);
	// 储存在本地
	//sessionStorage.setItem("deviceObj1", objstr);
	//将  " 替换成 '
	objstr = objstr.replace(/"/g,"'");
	//拼接导航
	var html ='<a href="javascript:;" class="active J_menuTab" data-obj="'+objstr+'" data-id="echart'+htmlNum1+'">'+obj.deviceName+'报表<i class="fa fa-times-circle"></i></a>'
	//移除导航菜单选中属性
	$('.page-tabs-content', parent.document).click().children("a").removeClass("active");
	//添加导航菜单
	$('.page-tabs-content', parent.document).click().append(html);
	var iframe = '<iframe class="J_iframe" width="100%" height="100%" src="device/oneDeviceChart.html" frameborder="0" data-id="echart'+htmlNum1+'" seamless="" style="display: inline;"></iframe>'
	//移除其他页面
	$('#content-main', parent.document).click().children("iframe").hide();
	$('#content-main', parent.document).click().append(iframe);
	//location.href="../logPro/searchLogs.html";
	htmlNum1++;	
})
var htmlNum2 = 0;
//资产安全策略按钮点击事件
$(".list_con").on("click",".device_safe",function(){
	//将设备信息存放到本地sessionStorage中
	var obj = {};
	obj.deviceId = $(this).parent().attr("data-id");
	obj.deviceName = $(this).parents('.li-content').siblings(".li-title").children(".device_name").html();
	obj.logType = $(this).parents('.li-content').find(".device_logType").children('.li-value').html();
	//将对象转换为字符串
	var objstr = JSON.stringify(obj);
	// 储存在本地
	sessionStorage.setItem("deviceObj2", objstr);
	//拼接导航
	var html ='<a href="javascript:;" class="active J_menuTab" data-id="safeStrategy'+htmlNum2+'">'+obj.deviceName+'安全策略<i class="fa fa-times-circle"></i></a>'
	//移除导航菜单选中属性
	$('.page-tabs-content', parent.document).click().children("a").removeClass("active");
	//添加导航菜单
	$('.page-tabs-content', parent.document).click().append(html);
	var iframe = '<iframe class="J_iframe" width="100%" height="100%" src="device/safeStrategy.html" frameborder="0" data-id="safeStrategy'+htmlNum2+'" seamless="" style="display: inline;"></iframe>'
	//移除其他页面
	$('#content-main', parent.document).click().children("iframe").hide();
	$('#content-main', parent.document).click().append(iframe);
	//location.href="../logPro/searchLogs.html";
	htmlNum2++;	
})

var htmlNum3 = 0;
//潜在威胁分析按钮点击事件
$(".list_con").on("click",".device_analyse",function(){
	//将设备信息存放到本地sessionStorage中
	var obj = {};
	obj.deviceId = $(this).parent().attr("data-id");
	obj.deviceName = $(this).parents('.li-content').siblings(".li-title").children(".device_name").html();
	obj.logType = $(this).parents('.li-content').find(".device_logType").children('.li-value').html();
	//将对象转换为字符串
	var objstr = JSON.stringify(obj);
	// 储存在本地
	sessionStorage.setItem("deviceObj3", objstr);
	//拼接导航
	var html ='<a href="javascript:;" class="active J_menuTab" data-id="threatAnalysis'+htmlNum3+'">'+obj.deviceName+'潜在威胁分析<i class="fa fa-times-circle"></i></a>'
	//移除导航菜单选中属性
	$('.page-tabs-content', parent.document).click().children("a").removeClass("active");
	//添加导航菜单
	$('.page-tabs-content', parent.document).click().append(html);
	var iframe = '<iframe class="J_iframe" width="100%" height="100%" src="device/threatAnalysis.html" frameborder="0" data-id="threatAnalysis'+htmlNum3+'" seamless="" style="display: inline;"></iframe>'
	//移除其他页面
	$('#content-main', parent.document).click().children("iframe").hide();
	$('#content-main', parent.document).click().append(iframe);
	//location.href="../logPro/searchLogs.html";
	htmlNum3++;	
})
var htmlNum4 = 0;
//查询资产事件列表按钮点击事件
$(".list_con").on("click",".device_eventlist",function(){
	//将设备信息存放到本地sessionStorage中
	var obj = {};
	obj.deviceId = $(this).parent().attr("data-id");
	obj.deviceName = $(this).parents('.li-content').siblings(".li-title").children(".device_name").html();
	obj.deviceHostname = $(this).parents('.li-content').find(".device_hostname").children('.li-value').html();
	obj.deviceIp = $(this).parents('.li-content').find(".device_ip").children('.li-value').html();
	obj.deviceType = $(this).parents('.li-content').find(".device_type").children('.li-value').html();
	obj.deviceLogType = $(this).parents('.li-content').find(".device_logType").children('.li-value').html();
	obj.deviceStarttime = $(this).parents('.li-content').find(".device_starttime").children('.li-value').html();
	obj.deviceUpdateTime = $(this).parents('.li-content').find(".device_updatetime").children('.li-value').html();
	obj.deviceEndtime = $(this).parents('.li-content').find(".device_endtime").children('.li-value').html();
	obj.deviceIsWorked = $(this).parents('.li-content').find(".device_iswork").children('.li-value').html();
	//将对象转换为字符串
	var objstr = JSON.stringify(obj);
	// 储存在本地
	sessionStorage.setItem("deviceObj4", objstr);
	//拼接导航
	var html ='<a href="javascript:;" class="active J_menuTab" data-id="oneDeviceEvents'+htmlNum4+'">'+obj.deviceName+'事件列表 <i class="fa fa-times-circle"></i></a>'
	//移除导航菜单选中属性
	$('.page-tabs-content', parent.document).click().children("a").removeClass("active");
	//添加导航菜单
	$('.page-tabs-content', parent.document).click().append(html);
	var iframe = '<iframe class="J_iframe" width="100%" height="100%" src="device/oneDeviceEvents.html" frameborder="0" data-id="oneDeviceEvents'+htmlNum4+'" seamless="" style="display: inline;"></iframe>'
	//移除其他页面
	$('#content-main', parent.document).click().children("iframe").hide();
	$('#content-main', parent.document).click().append(iframe);
	//location.href="../logPro/searchLogs.html";
	htmlNum4++;	
})
//查看更多资产信息
$(".list_con").on("click",".more-information",function(){
	if($(this).children().hasClass('glyphicon-chevron-down')){
		$(this).children().attr('title',"收起");
		$(this).children().attr('class','glyphicon glyphicon-chevron-up')
		$(this).siblings('ul').height(0);
		$(this).parent().siblings(".li-key-box").height('217px');
		$(this).parent().siblings(".li-key-box").css('overflow-y','auto')
	}else{
		$(this).children().attr('title',"点击查看更多资产信息");
		$(this).children().attr('class','glyphicon glyphicon-chevron-down')
		$(this).siblings('ul').height('120px');
		$(this).parent().siblings(".li-key-box").height('100px');
		$(this).parent().siblings(".li-key-box").animate({scrollTop:0},200);
		$(this).parent().siblings(".li-key-box").css('overflow-y','hidden')
	}
})

//设备列表加载拼串   data - 数据
function jointDeviceList(data){
	var html = '';
	//清空页面数据
	$(".device-list").html("")
	//循环取值  拼串
	for(var i in data){
	 	var obj = filterObj(data[i]);
	 	var logType = '';//日志类型
	 	if(obj.startUp == 0){
	 		obj.startUp = "否";
	 	}else if(obj.startUp == 1){
	 		obj.startUp = "是";
	 	}
	 	/*//日志类型转换
	 	if(obj.logType == 1){
	 		logType = "syslog";
	 	}else if(obj.logType == 2){
	 		logType = "winlog";
	 	}else if(obj.logType == 3){
	 		logType = "snmp";
	 	}else if(obj.logType == 4){
	 		logType = "log4j";
	 	}else if(obj.logType == 5){
	 		logType = "mysql";
	 	}else if(obj.logType == 6){
	 		logType = "packetfilteringfirewall_log";
	 	}	 	*/
	 	
	 	//设备类型
	 	var typeArr2 = ["网络","安全","主机","应用"]
	 	var typeArr = [
	 	     ["交换机","路由器"],
	 	     ["IPS","IDS","抗DDOS","防火墙"],
	 	     ["Windows","Linux","虚拟化"],
	 	     ["tomcat","apache","IIS","weblogic","mysql","Oracle","sqlserver","db2"]
	 	]
	 	var bigType = "";
	 	var type = "";//设备类型
	 	//获取设备type值；
		var deType =  obj.type;
		//截取type前两位字符
		var str = Number(deType.substring(0,2));
		//截取type后两位
		var str2 =  Number(deType.substring(2));
		bigType = typeArr2[str - 1]
		type = typeArr[str - 1][str2 - 1];
	 	//时间分割  传来得时间数据 末尾有.0 将其分割出去
	 	obj.createTime = obj.createTime.split(".")[0];
	 	obj.upDateTime = obj.upDateTime.split(".")[0];
	 	obj.endTime = obj.endTime.split(".")[0];
	 	
	 	/*html = '<tr>'   
			+		 '<td> <input type="checkbox" name="'+obj.id+'"></td>'
            +        '<td class="device_name">'
            +        	'<span data-id="'+obj.id+'" data-userid="'+obj.userId+'" data-depid="'+obj.departmentId+'">'+obj.name+'</a>'
            +        '</td>'
            +        '<td class="device_hostname">'+obj.hostName+'</td>'
            +        '<td class="device_type" data-type="'+obj.type+'">'+bigType+'-'+type+'</td>'
            +        '<td class="device_logType" data-logType="'+obj.logType+'">'+obj.logType+'</td>'
            +        '<td class="device_ip">'+obj.ip+'</td>'
            +        '<td class="device_domain">'+obj.domain+'</td>'
            +        '<td class="device_port">'+obj.port+'</td>'
            +        '<td class="device_iswork">'+obj.startUp+'</td>' 
            +        '<td class="device_starttime">'+obj.createTime+'</td>'   
            +        '<td class="device_updatetime">'+obj.upDateTime+'</td>' 
            +        '<td class="device_endtime">'+obj.endTime+'</td>'
            +       	'<td class="td_tools">'
            +       		'<i class="glyphicon glyphicon-edit device_revise" title="修改"> </i>'
            +       		'<i class="glyphicon glyphicon-list-alt device_logs" title="查询资产日志"> </i>'
            +       		'<i class="glyphicon glyphicon-stats device_echarts" title="查询资产图表"> </i>'
            +       		'<i class="glyphicon glyphicon-th-list device_eventlist" title="查询资产事件列表"> </i>'
            +       		'<i class="glyphicon glyphicon-bell device_safe" title="设置安全策略"> </i>'
            +       		'<i class="glyphicon glyphicon-screenshot device_analyse" title="潜在威胁分析"> </i>'
            +       	'</td>'
            +		'<td class="device_threat" threat-id="'+obj.id+'"></td>'
            +    '</tr>'
          
          //添加到页面中
          $(".device_list tbody").append(html);*/
	 	
	 	html += '<li class="device-item">'
			 +	   '<div class="li-title">'
			 +          '<div class="device_name"><span>'+obj.name+'</span></div>'
			 +		    '<input type="checkbox" name="'+obj.id+'">'
			 +		    '<div class="tit-ball">'+bigType+'</div>'
			 +			'<div class="device-information">'
			 +				'<span class="device_logtype">'+obj.logType+'</span>'
			 +				'<span class="device_ip">'+obj.ip+'</span>'
			 +				'<span class="more-infor"><i class="glyphicon glyphicon-chevron-right"></i></span>'	
			 +				'<div class="li-key-box">'
			 +					'<div class="device_logType">'
			 +						'<div class="li-key">日志类型 :</div>'
			 +						'<div class="li-value">'+obj.logType+'</div>'
			 +					'</div>'
			 +					'<div class="device_ip">'
			 +						'<div class="li-key">IP地址 :</div>'
			 +						'<div class="li-value">'+obj.ip+'</div>'
			 +					'</div>'
			 +					'<div class="device_hostname">'
			 +						'<div class="li-key">主机名 :</div>'
			 +						'<div class="li-value">'+obj.hostName+'</div>'
			 +					'</div>'
			 +					'<div class="device_type">'
			 +						'<div class="li-key">资产类型 :</div>'
			 +						'<div class="li-value">'+bigType+'-'+type+'</div>'
			 +					'</div>'
			 +					'<div class="device_domain">'
			 +						'<div class="li-key">根域名 :</div>'
			 +						'<div class="li-value">'+obj.domain+'</div>'
			 +					'</div>'
			 +					'<div class="device_port">'
			 +						'<div class="li-key">端口 :</div>'
			 +						'<div class="li-value">'+obj.port+'</div>'
			 +					'</div>'
			 +					'<div class="device_iswork">'
			 +						'<div class="li-key">是否启用 :</div>'
			 +						'<div class="li-value">'+obj.startUp+'</div>'
			 +					'</div>'
			 +					'<div class="device_starttime">'
			 +						'<div class="li-key">创建时间 :</div>'
			 +						'<div class="li-value">'+obj.createTime+'</div>'
			 +					'</div>'
			 +					'<div class="device_updatetime">'
			 +						'<div class="li-key">更新时间 :</div>'
			 +						'<div class="li-value">'+obj.createTime+'</div>'
			 +					'</div>'
			 +					'<div class="device_endtime">'
			 +						'<div class="li-key">停用时间 :</div>'
			 +						'<div class="li-value">'+obj.createTime+'</div>'
			 +					'</div>'
			 +				'</div>'
			 +	  		'</div>'
			 +			 '<div class="device-tools">'
			 +				'<a href="" class="tools-a"><i class="glyphicon glyphicon-chevron-down"></i></a>'
			 +			'</div>'
			 +			'<ul class="li-tools-ul" data-angle="[0,215]">'
			 +				'<li class="item"><i class="glyphicon glyphicon-edit device_revise" title="修改"> </i></li>'
			 +				'<li class="item"><i class="glyphicon glyphicon-list-alt device_logs" title="查询资产日志"> </i></li>'
			 +				'<li class="item"><i class="glyphicon glyphicon-stats device_echarts" title="查询资产图表"></i></li>'
			 +				'<li class="item"><i class="glyphicon glyphicon-th-list device_eventlist" title="查询资产事件列表"> </i></li>'
			 +				'<li class="item"><i class="glyphicon glyphicon-bell device_safe" title="设置安全策略"> </i></li>'
			 +				'<li class="item"><i class="glyphicon glyphicon-screenshot device_analyse" title="潜在威胁分析"> </i></li>'
			 +			'</ul>'
			 +		'</div>'
			 +'</li>'
	 		
	
	 	 /* //判断有没有高危事件
	 	 if(obj.high_risk != 0 && obj.high_risk != "-"){//高危事件
	 		 $("td[threat-id="+obj.id+"]").append('<span class="higeThreat" title="高危事件数量">'+obj.high_risk+'</span>')
	 	 }
	 	//判断有没有中危事件
	 	 if(obj.moderate_risk != 0 && obj.moderate_risk != "-"){//中危事件
	 		 $("td[threat-id="+obj.id+"]").append('<span class="middleThreat" title="中危事件数量">'+obj.moderate_risk+'</span>')
	 	 }*/
	}
	
	//添加到页面中
	//$(".device_list tbody").html(html);
	$(".device-list").html(html);
 	$(".tools-a").wheelmenu({
		trigger: "hover",
		animation: "fly",
		animationSpeed: "fast",
		angle: "all",
	});
	//资产信息详情
	$(".more-infor").hover(function(){
		$(this).css('zIndex','11');
		$(this).siblings(".li-key-box").css({"zIndex":"4","opacity":"1","right":"-194px"});
	},function(){
		$(this).css('zIndex','3');
		$(this).siblings(".li-key-box").css({"zIndex":"-1","opacity":"0","right":"-240px"});
	})
	//遮罩
	$(".tools-a,.li-tools-ul,.more-infor").hover(function() {
		$(".zz").css("display", "block")
	}, function() {
		$(".zz").css("display", "none")
	})
}

//表头中的复选框 点击全选或取消全选
$('#theadCheck').click(function(){	
	//如果checkbox_Status == 0 则未选中
	if($('#theadCheck')[0].checked == true){
		//更改状态
		$("#device_list>tbody input[type=checkbox]").prop("checked", "checked");
	}else if($('#theadCheck')[0].checked == false){
		//更改状态
		$("#device_list>tbody input[type=checkbox]").removeAttr("checked"); 
	}
})

//点击批量删除按钮 执行函数
$("#remove_device").click(function(){
	//获取被选择的复选框
	var checkboxs = $("#device_list>tbody input[type=checkbox]:checked");
	var deviceId = '';
	//判断是否又被选中的  没有则提示 有则继续执行删除操作
	if(checkboxs.length == 0){
		layer.msg('未选中任何资产',{icon: 5});
	}else{
		//询问框
		layer.confirm('您确定删除么？', {
		  btn: ['确定','取消'] //按钮
		}, function(index){
			layer.close(index);
			//循环拼接id值
			for(var i = 0; i< checkboxs.length;i++){
				deviceId += checkboxs[i].getAttribute("name")+',';
			}	
			var deviceObj = {};
			deviceObj.id = deviceId;
			 //发送数据到后台 进行删除
			 //成功回调函数
			var sfunc = function(data){
				layer.msg("删除成功",{icon:1});
				//重新加载谁被列表
				getDeviceData(1);
			}
			//失败回调函数
			var efunc = function(data){
				layer.msg("删除失败",{icon:5});
				console.log(data);
			}
			//发送请求
			ajaxPostWithOutLayer("../../equipment/delete.do",deviceObj,sfunc,"",efunc)
			//关闭弹窗
			layer.close();
		}, function(){
		  layer.close();
		});
	}
	
})
//点击刷新按钮 事件
$("#refresh_device").click(function(){
	//页面加载时  加载谁被列表
	getDeviceData(pageIndex);
})



var htmlNum2 = 0;
//查看所有设备报表按钮
$("#echart_device").click(function(){
	//拼接导航
	var html ='<a href="javascript:;" class="active J_menuTab" data-id="allDevEchart'+htmlNum2+'">所有设备报表<i class="fa fa-times-circle"></i></a>'
	//移除导航菜单选中属性
	$('.page-tabs-content', parent.document).click().children("a").removeClass("active");
	//添加导航菜单
	$('.page-tabs-content', parent.document).click().append(html);
	var iframe = '<iframe class="J_iframe" width="100%" height="100%" src="device/allDeviceChart.html?v=4.0" frameborder="0" data-id="allDevEchart'+htmlNum2+'" seamless="" style="display: inline;"></iframe>'
	//移除其他页面
	$('#content-main', parent.document).click().children("iframe").hide();
	$('#content-main', parent.document).click().append(iframe);
	htmlNum2++;
})
//搜索设备点击事件 
$(".search_device").click(function(){
	//调用函数
	searchDevice(1);
	//更改状态
	searchStutus = true;
})

function searchDevice(pageIndex){
	//获取输入框中的搜索条件的值
	var search_deviceName = $(".search_deviceName").val();
	var search_hostname = $(".search_hostname").val();
	var search_deviceIp = $(".search_deviceIp").val();
	var search_logType = $(".logType").val();
	var search_type = $(".eType").val();
	var obj = {};
	//赋值 参数
	obj.name = search_deviceName;
	obj.hostName = search_hostname;
	obj.ip = search_deviceIp;
	obj.logType = search_logType;
	if(search_type == null){
		search_type = "";
	}
	obj.type = search_type;
	//调用函数 获取数据
	getDeviceData(pageIndex,obj);
}

//数据对象过滤  null/undefined  转为'-';
function filterObj(obj){
	for(var i in obj){
		if(obj[i] == undefined || obj[i] == null || obj[i] == ""){
			obj[i] = '-';
		}
	}
	return obj;
}









