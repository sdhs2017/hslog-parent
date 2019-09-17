//定义echart图表
var myChart03;
var myChart04;
var myChart05;
var myChart06;
var myChart07;
//分页条数
var pageSize = 10;
//每小时日志传参对象 总数 当前显示的页数
var hourLogObj = {},hourLogAllCount,hourLogCurPage;
//日志级别传参对象  总数 当前显示的页数
var levelLogObj = {},levelLogAllCount,levelLogCurPage;
//事件每小时传参对象  总数 当前显示的页数
var hourEventObj = {},hourEventAllCount,hourEventCurPage;
//事件类型传参对象   总数 当前显示的页数
var typeEventObj = {},typeEventAllCount,typeEventCurPage;
//获得今天的时间
var today = new Date();
//时间控件
laydate.render({
	elem: '#dataTime', //指定元素
    format: 'yyyy-MM-dd',
    max:"4073558400000",//最大日期为今天
    value: new Date(),
    showBottom: false,
    done: function(value, date, endDate){ //日期选择完毕事件
	      	sendObj.param = value;
	    	getEchartData(sendObj);
	}
});
laydate.render({
	elem: '#dataTime2', //指定元素
    format: 'yyyy-MM-dd',
    max:"4073558400000",//最大日期为今天
    value: new Date(),
    showBottom: false,
    done: function(value, date, endDate){ //日期选择完毕事件
	      	sendObj.param = value;
	    	getEchartData2(sendObj);
	}
});
laydate.render({
	elem: '#dataTime3', //指定元素
    format: 'yyyy-MM-dd',
    max:"4073558400000",//最大日期为今天
    value: new Date(),
    showBottom: false,
    done: function(value, date, endDate){ //日期选择完毕事件
	      	sendObj.param = value;
	    	getEchartData3(sendObj);
	}
});
laydate.render({
	elem: '#dataTime4', //指定元素
    format: 'yyyy-MM-dd',
    max:"4073558400000",//最大日期为今天
    value: new Date(),
    showBottom: false,
    done: function(value, date, endDate){ //日期选择完毕事件
	    	getEchartData4(value);
	}
});
//获取当前日期
var day1 = new Date();
var mon = day1.getMonth()+1;
var da = day1.getDate();
if( mon < 10){
	mon = "0"+(day1.getMonth()+1);
}
if(da < 10){
	da = "0"+ (day1.getDate());
}
var s1 = day1.getFullYear()+"-" + mon + "-" + da;
//var getDeviceId = sessionStorage.getItem("deviceObj1");//获取本地存储的设备值 对象
//获取存放在标签页标题中的数据
var getDeviceId = $('.page-tabs-content', parent.document).click().children("a.active").attr("data-obj");
if(getDeviceId != null){
	var str = JSON.parse(getDeviceId.replace(/'/g,"\""));	
	var title = str.deviceName;
	var logType = str.logType;
	$(".top_title>span").html(title+' 数据统计')
	var sendObj = {};
	sendObj.equipmentid =str.deviceId;
	sendObj.type = logType;
	sendObj.index = "estest";
	sendObj.param = s1;
	getEchartData(sendObj);	//日志数据函数	
	getEchartData2(sendObj);//潜在威胁函数
	getEchartData3(sendObj);//事件类型函数
	getEchartData4(s1);//日志级别函数
	//删除本地session
	sessionStorage.removeItem("deviceObj1");
}	
//日志报表		
function getEchartData(sendObj){
	$.ajax({
		type:"post",
		url:"../../log/getCountGroupByTime.do",
		data:sendObj,
		async:true,
		success:function(data){
			var obj = data[0];
			//var xVal = [];
			var yVal = [];
			/* for(var i in obj){
				yVal.push(obj[i])
			} */
			for(var i = 0;i<24;i++){
				if(i < 10){
					i = "0"+i;
				}
				yVal.push(obj[i]);
			}
			//yVal = obj;
			//折线图
        	//var myChart01 = echarts.init(document.getElementById('echartsBox'));
			myChart03 = echarts.init(document.getElementById('echartsBox'));
			myChart03.setOption({
				backgroundColor:'#293846',
        		title: {
    				text:" 每小时日志数据量统计",
		        	x:'center',
		        	textStyle:{
		        		color:'#5bc0de'
		        	}
	            },
	            tooltip: {
	            	trigger: 'axis',
			        axisPointer: {
			            type: 'cross',
			            label: {
			                backgroundColor: '#6a7985'
			            }
			        }
	            },
	            toolbox: {
			        feature: {
			           dataView: {
			        	   show: true, 
			        	   readOnly: false,
			        	   backgroundColor:'#3a6086',
			        	   textareaColor:'#3a6086',
			        	   textColor:'#fff',
			        	   textareaBorderColor:'#fff',
			        	   buttonColor:'#de946f',
			        	   optionToContent: function(opt) {
			        		    var axisData = opt.xAxis[0].data;
			        		    var series = opt.series;
			        		    var table = '<table style="width:100%;text-align:center"><tbody><tr>'
			        		                 + '<td>时间</td>'
			        		                 + '<td>' + series[0].name + '</td>'
			        		                 + '</tr>';
			        		    for (var i = 0, l = axisData.length; i < l; i++) {
			        		        table += '<tr>'
			        		                 + '<td>' + axisData[i] + '</td>'
			        		                 + '<td>' + series[0].data[i] + '</td>'
			        		                 + '</tr>';
			        		    }
			        		    table += '</tbody></table>';
			        		    return table;
			        		}
			           },
			          /*   restore: {show: true}, */
			            saveAsImage: {show: true}
			        }
				},
	            legend: {
			        data:[],
			        left: 'left'
				},
	            xAxis: {
	            	name:"小时",
	            	type: 'category',
	            	nameTextStyle:{
		        		color:'#5bc0de'
		        	},
		        	boundaryGap: false,
		        	axisLine:{
		        		lineStyle:{
		        			color:'#5bc0de'
		        		}
		        	},
	                data: ["00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"]
	            },
	            yAxis: {
	            	name:"数量",
	            	axisLine:{
		        		lineStyle:{
		        			color:'#5bc0de'
		        		}
		        	},
		        	splitLine:{
		        		lineStyle:{
		        			color:'#5bc0de'
		        		}
		        	},
	            	nameTextStyle:{
		        		color:'#5bc0de'
		        	},
		        	 type: 'value'
	            },
	            series: [ {
		            name:'日志数量',
		            type:'line',
		            smooth:true,//平滑性
		            areaStyle: {normal: {}},
		            itemStyle:{
                        normal:{
                            color:'#5bc0de'
                        }
                    },
		            data:yVal
		        }]
        	});
        	window.addEventListener("resize",function(){
                myChart03.resize();
            });
        	//每小时日志数据量统计 图表点击事件
        	myChart03.on('click', function (params) {
        		//定义第一次请求
        		var hourLogFirstGet = true;
        		//存储图表盒子标签
        		var $echartBox = $($(this).attr("_zr").dom);
        		//获得日期
        		var date = $echartBox.siblings().children("input").val();       		
        		//隐藏图表
        		$echartBox.parent().hide();
        		//显示列表
        		$echartBox.parent().siblings().show();
        		//更改列表标题
        		$echartBox.parent().siblings().children(".dataListTitle").html(date +' '+ params.name+'时日志列表<span class="goToChartBox">返回图表</span>')
        		//更改宽度
        		$echartBox.parents(".col-md-6").addClass("col-md-12");
        		$echartBox.parents(".col-md-6").removeClass("col-md-6");
        		//定义传参对象
        		//开始时间
        		hourLogObj.starttime = date+' '+params.name+':00:00';
        		//结束时间
        		hourLogObj.endtime = date+' '+params.name+':59:59';
        		//每页显示条数
        		hourLogObj.size = pageSize;
        		//资产id
        		hourLogObj.id = str.deviceId;
        		//发送请求 
        		getLogsData(hourLogObj,1,hourLogFirstGet,"#hourLogList");
        		
        		$(".goToChartBox").click(function(){
        			//隐藏列表
            		$(this).parents(".dataList").hide();
            		//显示图表
            		$(this).parents(".dataList").siblings().show();
            		//调整宽度
            		$(this).parents(".col-md-12").addClass("col-md-6");
            		$(this).parents(".col-md-12").removeClass("col-md-12");
            	})
        	})
        	
		},
		error:function(err){
			
		}
	});
	 				
} 
//创建分页函数 elem-分页容器(id)  allcount-总条数  limit-每一页显示的条数 currentPage-当前页
function createPage(elem,allCount,limit,currentPage){
	//分页插件
	layui.use('laypage', function(){
		var laypage = layui.laypage;	
	  	//执行一个laypage实例
	  	laypage.render({
		  elem: elem //分页容器 id
		  ,count: allCount //数据总数，从服务端得到
		  ,limit: limit  //每页显示的条数
		  ,curr:currentPage //默认显示页码
		  ,jump: function(obj, first){
		    //首次不执行
		    if(!first){
		    	//判断是哪个地方的分页  调用不同的请求
		    	if(obj.elem == 'hourLogPage'){
		    		hourLogCurPage = obj.curr;
		    		//请求数据
					getLogsData(hourLogObj,obj.curr,"","#hourLogList")
		    	}else if(obj.elem == 'levelLogPage'){
		    		levelLogCurPage = obj.curr;
		    		//请求数据
					getLogsData(levelLogObj,obj.curr,"","#levelLogList")
		    	}else if(obj.elem == 'hourEventPage'){
		    		hourEventCurPage = obj.curr;
		    		//请求数据
					getEventsData(hourEventObj,obj.curr,"","#hourEventList");
		    	}else if(obj.elem == 'typeEventPage'){
		    		typeEventCurPage = obj.curr;
		    		//请求数据
					getEventsData(typeEventObj,obj.curr,"","#typeEventList");
		    	}
		    	//获得分页盒子	
		    	var allC = $('#'+obj.elem).next().children("b").html(); 
		    	//判断是否显示的数据是最后一页
		    	if(obj.curr * obj.limit > allC){
					layer.tips('这是最后一页', '#'+obj.elem+' .layui-laypage-curr', {
						  tips: [1, '#3595CC'],
						  time: 4000
						});
				}else{
					if($("#"+obj.elem+" .layui-laypage-curr").next().html() == "下一页"){
						layer.tips('可通过更改最大显示数量，查看后面的数据', $('#'+obj.elem).next().children(".maxShowData"), {
							  tips: [1, '#3595CC'],
							  time: 4000
							});
					}
				}
		    }else{//首次
		    	firstGet = false;
		    	
		    }
		  }
		});
	});
}

//获得日志列表函数 sendObj-传参对象   page-当前页码  firstGet-判断第一次请求  listBox-存放日志列表的最外层盒子
function getLogsData(sendObj,page,firstGet,listBox){
	sendObj.page = page;	
	//获取事件总数与高危事件数量
	var sfunc = function(data){
		if(data[0].state == false){
			layer.msg(data[0].msg,{icon: 5});
			return false;
		}
		//将数据添加到页面中
		var html = '';
		for(var i in data[0].list){
			var obj = data[0].list[i];
			html += '<tr>'
				 +		'<td class="logs_time">'+obj.logtime+'</td>'
				 +		'<td class="logs_level">'+obj.operation_level+'</td>'
				 +		'<td class="logs_type">'+obj.type+'</td>'
				 +		'<td class="logs_ip">'+obj.ip+'</td>'
				 +		'<td class="logs_Mes">'+obj.operation_des+'</td>'
				 +		'<td class="logs_tools"><i class="glyphicon glyphicon-list-alt more" title="查看详情"></i></td>'
				 +  '</tr>'
			
		}
		//添加到页面
		$(listBox+" tbody").html(html);
		//绑定查看按钮
		$('.more').click(function(){
			var logTime =  $(this).parent().siblings('.logs_time').html();
			var logLevel =  $(this).parent().siblings('.logs_level').html();
			var logType =  $(this).parent().siblings('.logs_type').html();
			var logIp =  $(this).parent().siblings('.logs_ip').html();
			var logMes =  $(this).parent().siblings('.logs_Mes').html();
			var html = '<div class="layer_box">'
					+		'<div class="row" style="line-height:50px">'
					+			'<div class="col-xs-3">时间:</div>'
					+			'<div class="col-xs-9 layCen">'+logTime+'</div>'
					+		'</div>'
					+		'<div class="row" style="line-height:50px">'
					+			'<div class="col-xs-3">级别:</div>'
					+			'<div class="col-xs-9 layCen">'+logLevel+'</div>'
					+		'</div>'
					+		'<div class="row" style="line-height:50px">'
					+			'<div class="col-xs-3">类型:</div>'
					+			'<div class="col-xs-9 layCen">'+logType+'</div>'
					+		'</div>'
					+		'<div class="row" style="line-height:50px">'
					+			'<div class="col-xs-3">IP:</div>'
					+			'<div class="col-xs-9 layCen">'+logIp+'</div>'
					+		'</div>'
					+		'<div class="row" style="line-height:50px">'
					+			'<div class="col-xs-3">日志内容:</div>'
					+			'<div class="col-xs-9 layCen" style="line-height:22px;margin-bottom:20px;">'+logMes+'</div>'
					+		'</div>'
					+	'</div>'
					//弹窗
					layer.open({
				 		type: 1,
				 		title:"详细日志",//标题
						area: ['520px', 'auto'], //宽高
				 		content: html
					});
		})
		var allCount = data[0].count;
		//判断是否是第一次请求
		if(firstGet == true){
			//获得总条数
			var allCount = data[0].count;
			//获得存放分页的盒子
			var pageBoxId = $(listBox).parents(".dataListContent").next().attr("id");
			//获得总数盒子
			var allCountBox = $(listBox).parents(".dataListContent").siblings(".allCountBox");
			//创建分页函数
			//createPage(pageBoxId,allCount,10);
			//获得设置的最大显示条数
			var maxShowDataVal = $(allCountBox).children(".maxShowData").val();
			//获得默认的最大显示条数
			var defaultmaxVal = $(allCountBox).children(".maxShowData").children("option:first-child").val();
			if(maxShowDataVal != defaultmaxVal){
				maxShowDataVal = defaultmaxVal;
				$(allCountBox).children(".maxShowData").val(defaultmaxVal);
			}
			//判断总数与当前选择的最大显示的条数进行对比
			if(allCount > maxShowDataVal){
				//创建分页函数
				createPage(pageBoxId,$(allCountBox).children(".maxShowData").val(),10);
			}else{
				//创建分页函数
				createPage(pageBoxId,allCount,10);
			}
			//添加总条数到页面中
			$(allCountBox).children("b").html(allCount);
		}else{
			//显示数据
		}
	}
	var hsObj = {};
	hsObj.hsData = JSON.stringify(sendObj);
	//发送请求
	ajaxPost("../../log/getLogListByEquipment.do",hsObj,sfunc);
}
//获得事件列表函数 sendObj-传参对象   page-当前页码  firstGet-判断第一次请求  listBox-存放日志列表的最外层盒子
function getEventsData(sendObj,page,firstGet,listBox){
	sendObj.page = page;	
	//获取事件总数与高危事件数量
	var sfunc = function(data){
		if(data[0].state == false){
			layer.msg(data[0].msg,{icon: 5});
			return false;
		}
		//将数据添加到页面中
		var html = '';
		for(var i in data[0].list){
			var obj = data[0].list[i];
			html += '<tr>'
				 +		'<td class="logs_time">'+obj.logtime+'</td>'
				 +		'<td class="logs_event_des">'+obj.event_des+'</td>'
				 +		'<td class="logs_operation_level">'+obj.operation_level+'</td>'
				 +		'<td class="logs_event_type">'+obj.event_type+'</td>'
				 +		'<td class="logs_ip">'+obj.ip+'</td>'
				 +		'<td class="logs_type">'+obj.type+'</td>'
				 +		'<td class="logs_Mes">'+obj.operation_des+'</td>'
				 +		'<td class="logs_tools"><i class="glyphicon glyphicon-list-alt more" title="查看详情"></i></td>'
				 +  '</tr>'
			
		}
		//添加到页面
		$(listBox+" tbody").html(html);
		//绑定查看按钮
		$('.more').click(function(){
			var logTime =  $(this).parent().siblings('.logs_time').html();
			var logEventDes =  $(this).parent().siblings('.logs_event_des').html();
			var logOpLevel =  $(this).parent().siblings('.logs_operation_level').html();
			var logEventType =  $(this).parent().siblings('.logs_event_type').html();
			var logIp =  $(this).parent().siblings('.logs_ip').html();
			var logType =  $(this).parent().siblings('.logs_type').html();
			var logMes =  $(this).parent().siblings('.logs_Mes').html();
			var html = '<div class="layer_box">'
					+		'<div class="row" style="line-height:50px">'
					+			'<div class="col-xs-3">时间:</div>'
					+			'<div class="col-xs-9 layCen">'+logTime+'</div>'
					+		'</div>'
					+		'<div class="row" style="line-height:50px">'
					+			'<div class="col-xs-3">事件名称:</div>'
					+			'<div class="col-xs-9 layCen">'+logEventDes+'</div>'
					+		'</div>'
					+		'<div class="row" style="line-height:50px">'
					+			'<div class="col-xs-3">事件级别:</div>'
					+			'<div class="col-xs-9 layCen">'+logOpLevel+'</div>'
					+		'</div>'
					+		'<div class="row" style="line-height:50px">'
					+			'<div class="col-xs-3">事件类型:</div>'
					+			'<div class="col-xs-9 layCen">'+logEventType+'</div>'
					+		'</div>'
					+		'<div class="row" style="line-height:50px">'
					+			'<div class="col-xs-3">IP地址:</div>'
					+			'<div class="col-xs-9 layCen">'+logIp+'</div>'
					+		'</div>'
					+		'<div class="row" style="line-height:50px">'
					+			'<div class="col-xs-3">日志类型:</div>'
					+			'<div class="col-xs-9 layCen">'+logType+'</div>'
					+		'</div>'
					+		'<div class="row" style="line-height:50px">'
					+			'<div class="col-xs-3">日志内容:</div>'
					+			'<div class="col-xs-9 layCen" style="line-height:22px;margin-bottom:20px;">'+logMes+'</div>'
					+		'</div>'
					+	'</div>'
					//弹窗
					layer.open({
				 		type: 1,
				 		title:"详细日志",//标题
						area: ['520px', 'auto'], //宽高
				 		content: html
					});
		})
		var allCount = data[0].count;
		//判断是否是第一次请求
		if(firstGet == true){
			//获得总条数
			var allCount = data[0].count;
			//获得存放分页的盒子
			var pageBoxId = $(listBox).parents(".dataListContent").next().attr("id");
			//获得总数盒子
			var allCountBox = $(listBox).parents(".dataListContent").siblings(".allCountBox");
			//创建分页函数
			//createPage(pageBoxId,allCount,10);
			//获得设置的最大显示条数
			var maxShowDataVal = $(allCountBox).children(".maxShowData").val();
			//获得默认的最大显示条数
			var defaultmaxVal = $(allCountBox).children(".maxShowData").children("option:first-child").val();
			if(maxShowDataVal != defaultmaxVal){
				maxShowDataVal = defaultmaxVal;
				$(allCountBox).children(".maxShowData").val(defaultmaxVal);
			}
			//判断总数与当前选择的最大显示的条数进行对比
			if(allCount > maxShowDataVal){
				//创建分页函数
				createPage(pageBoxId,$(allCountBox).children(".maxShowData").val(),10);
			}else{
				//创建分页函数
				createPage(pageBoxId,allCount,10);
			}
			//添加总条数到页面中
			$(allCountBox).children("b").html(allCount);
		}else{
			//显示数据
		}
	}
	var hsObj = {};
	hsObj.hsData = JSON.stringify(sendObj);
	//发送请求
	ajaxPost("../../log/getEventListByBlend.do",hsObj,sfunc);
}
//潜在威胁分析
function getEchartData2(sendObj){
	$.ajax({
		type:"post",
		url:"../../log/getCountGroupByEventType.do",
		data:sendObj,
		async:true,
		success:function(data){
			var wholeArr = [];//全部 数据
			var highArr = [];//高危 数据
			var middleArr = [];//中危 数据
			var lowArr = [];//地位 数据
			for(var i = 0;i<24;i++){
				if(i < 10){
					i = "0"+i;
				}
				wholeArr.push(data[0][i]);
				highArr.push(data[1][i]);	
				middleArr.push(data[2][i]);	
				lowArr.push(data[3][i]);	
			}
			//折线图
        	//var myChart01 = echarts.init(document.getElementById('echartsBox'));
			myChart04 = echarts.init(document.getElementById('echartsBox2'));
			myChart04.setOption({
				backgroundColor:'#293846',
        		title: {
    				text:" 每小时事件数量统计",
		        	x:'center',
		        	textStyle:{
		        		color:'#5bc0de'
		        	}
	            },
	            tooltip: {
	                trigger: 'axis',
                	axisPointer: {
			            type: 'cross',
			            label: {
			                backgroundColor: '#6a7985'
			            }
			        }
	            },
	            legend: {
	            	right:"5%",
	            	top:"25px",
	            	textStyle:{
		        		color:'#eee'
		        	},
	                data:['全部','高危','中危','普通']
	            },
	            grid: {
	            	
	            },
	            toolbox: {
	                feature: {
	                	dataView: {
	                		show: true, 
				        	   readOnly: false,
				        	   backgroundColor:'#3a6086',
				        	   textareaColor:'#3a6086',
				        	   textColor:'#fff',
				        	   textareaBorderColor:'#fff',
				        	   buttonColor:'#de946f',
				        	   optionToContent: function(opt) {
				        		    var axisData = opt.xAxis[0].data;
				        		    var series = opt.series;
				        		    var table = '<table style="width:100%;text-align:center"><tbody><tr>'
				        		                 + '<td>时间</td>'
				        		                 + '<td>' + series[0].name + '</td>'
				        		                 + '<td>' + series[1].name + '</td>'
				        		                 + '<td>' + series[2].name + '</td>'
				        		                 + '<td>' + series[3].name + '</td>'
				        		                 + '</tr>';
				        		    for (var i = 0, l = axisData.length; i < l; i++) {
				        		        table += '<tr>'
				        		                 + '<td>' + axisData[i] + '</td>'
				        		                 + '<td>' + series[0].data[i] + '</td>'
				        		                 + '<td>' + series[1].data[i] + '</td>'
				        		                 + '<td>' + series[2].data[i] + '</td>'
				        		                 + '<td>' + series[3].data[i] + '</td>'
				        		                 + '</tr>';
				        		    }
				        		    table += '</tbody></table>';
				        		    return table;
				        		}
	                	},
	                    saveAsImage: {}
	                }
	            },
	            xAxis: {
	            	name:"小时",
	                type: 'category',
	                nameTextStyle:{
		        		color:'#5bc0de'
		        	},
		        	axisLine:{
		        		lineStyle:{
		        			color:'#5bc0de'
		        		}
		        	},
	                boundaryGap: false,
	                data: ["00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"]
	            },
	            yAxis: {
	            	name:"数量",
	            	axisLine:{
		        		lineStyle:{
		        			color:'#5bc0de'
		        		}
		        	},
		        	splitLine:{
		        		lineStyle:{
		        			color:'#5bc0de'
		        		}
		        	},
	            	nameTextStyle:{
		        		color:'#5bc0de'
		        	},
	                type: 'value'
	            },
	            series: [
	                {
	                    name:'全部',
	                    type:'line',
	                    smooth:true,//平滑性
	                    itemStyle:{
	                        normal:{
	                            color:'#61a0a8'
	                        }
	                    },
	                    data:wholeArr 
	                },
	                {
	                    name:'高危',
	                    type:'line',
	                    smooth:true,//平滑性
	                    itemStyle:{
	                        normal:{
	                            color:'#c64541'
	                        }
	                    },
	                    data:highArr
	                },
	                {
	                    name:'中危',
	                    type:'line',
	                    smooth:true,//平滑性
	                    itemStyle:{
	                        normal:{
	                            color:'#d48265'
	                        }
	                    },
	                    data:middleArr
	                },
	                {
	                    name:'普通',
	                    type:'line',
	                    smooth:true,//平滑性
	                    itemStyle:{
	                        normal:{
	                            color:'#95c9b1'
	                        }
	                    },
	                    data:lowArr
	                }
	            ]
        	});
        	window.addEventListener("resize",function(){
                myChart04.resize();
            });
        	//图表点击事件
        	myChart04.on('click', function (params) {
        		//定义第一次请求
        		var hourEventFirstGet = true;
        		//存储图表盒子标签
        		var $echartBox = $($(this).attr("_zr").dom);
        		//获得日期
        		var date = $echartBox.siblings().children("input").val();       		
        		//隐藏图表
        		$echartBox.parent().hide();
        		//显示列表
        		$echartBox.parent().siblings().show();
        		//更改列表标题
        		$echartBox.parent().siblings().children(".dataListTitle").html(date +' '+ params.name+'时	'+ params.seriesName+'事件列表<span class="goToChartBox">返回图表</span>')
        		//更改宽度
        		$echartBox.parents(".col-md-6").addClass("col-md-12");
        		$echartBox.parents(".col-md-6").removeClass("col-md-6");
        		//更改浏览器滚动条
        		var tt = $(document).scrollTop()
        		$(document).scrollTop(tt+329);
        		//定义传参对象
        		//开始时间
        		hourEventObj.starttime = date+' '+params.name+':00:00';
        		//结束时间
        		hourEventObj.endtime = date+' '+params.name+':59:59';
        		//每页显示条数
        		hourEventObj.size = pageSize;
        		//资产id
        		hourEventObj.equipmentid = str.deviceId;
        		//事件级别
        		hourEventObj.event_levels = params.seriesName; 
        		//发送请求 
        		getEventsData(hourEventObj,1,hourEventFirstGet,"#hourEventList");
        		
        		$(".goToChartBox").click(function(){
        			//隐藏列表
            		$(this).parents(".dataList").hide();
            		//显示图表
            		$(this).parents(".dataList").siblings().show();
            		//调整宽度
            		$(this).parents(".col-md-12").addClass("col-md-6");
            		$(this).parents(".col-md-12").removeClass("col-md-12");
            	})
        	})
        	
       
		},
		error:function(err){
			
		}
	});
	 				
} 
//事件类型函数
function getEchartData3(sendObj){
	$.ajax({
		type:"post",
		url:"../../log/getCountGroupByEvent.do",
		data:sendObj,
		async:true,
		success:function(data){
			var obj = data[0];
			var xVal = [];//x轴数据
			var yVal = [];//y轴数据						
			var pieVAl = [];//饼图数据
			for(var i in obj){
				var pieObj = {};
				xVal.push(i);
				yVal.push(obj[i])
				pieObj.value = obj[i];
				pieObj.name = i;
				pieVAl.push(pieObj)
			}
			//柱状图
        	myChart05 = echarts.init(document.getElementById('echartsBox3'));
        	myChart05.setOption({
        		backgroundColor:'#293846',
        		title: {
        				text: '事件类型数量-柱状图',
			        	x:'center',
		        		textStyle:{
			        		color:'#5bc0de'
			        	}
		            },
		            tooltip: {
		            	trigger: 'axis',
		    	        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
		    	            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
		    	        }
		            },
		            toolbox: {
				        feature: {
				           dataView: {
				        	   show: true, 
				        	   readOnly: false,
				        	   backgroundColor:'#3a6086',
				        	   textareaColor:'#3a6086',
				        	   textColor:'#fff',
				        	   textareaBorderColor:'#fff',
				        	   buttonColor:'#de946f',
				        	   optionToContent: function(opt) {
				        		    var axisData = opt.xAxis[0].data;
				        		    var series = opt.series;
				        		    var table = '<table style="width:100%;text-align:center"><tbody><tr>'
				        		                 + '<td>时间</td>'
				        		                 + '<td>' + series[0].name + '</td>'
				        		                 + '</tr>';
				        		    for (var i = 0, l = axisData.length; i < l; i++) {
				        		        table += '<tr>'
				        		                 + '<td>' + axisData[i] + '</td>'
				        		                 + '<td>' + series[0].data[i] + '</td>'
				        		                 + '</tr>';
				        		    }
				        		    table += '</tbody></table>';
				        		    return table;
				        		}
				           },
				           saveAsImage: {show: true}
				        }
					},
		            legend: {
				        data:[],
				        left: 'left'
				    },
		            xAxis: {
		            	name:"类型",
		            	nameTextStyle:{
			        		color:'#5bc0de'
			        	},
			        	axisLine:{
			        		lineStyle:{
			        			color:'#5bc0de'
			        		}
			        	},
			        	axisLabel:{  
			                interval:0,  
			                rotate:30,//倾斜度 -90 至 90 默认为0  
			                margin:8
			            },
//				                data: ["20/7","21/7","22/7","23/7","24/7","25/7","26/7"]
						data:xVal
		            },
		            yAxis: {
		            	name:"数量",
		            	axisLine:{
			        		lineStyle:{
			        			color:'#5bc0de'
			        		}
			        	},
			        	splitLine:{
			        		lineStyle:{
			        			color:'#5bc0de'
			        		}
			        	},
		            	nameTextStyle:{
			        		color:'#5bc0de'
			        	}
		            },
		            series: [ {
			            name:'数量',
			            type:'bar',
			            barWidth: '60%',
			            itemStyle:{
                            normal:{
                                color:'#5bc0de'
                            }
                        },
						data:yVal
			        }]
        	});       	
        	//饼图
        	myChart06 = echarts.init(document.getElementById('echartsBox4'));
        	 
        	myChart06.setOption({
        		noDataLoadingOption: {
                    text: '暂无数据',
                    effect: 'bubble',
                    effectOption: {
                        effect: {
                            n: 0
                        }
                    }
        		},
        		backgroundColor:'#293846',
        		title : {
			        text: '事件类型数量-饼图',
			        x:'center',
			        textStyle:{
		        		color:'#5bc0de'
		        	}
			    },
			    tooltip : {
			        trigger: 'item',
			        formatter: "{a} <br/>{b} : {c} ({d}%)"
			    },
			    toolbox: {
			        feature: {
			           dataView: {
			        	   show: true, 
			        	   readOnly: false,
			        	   backgroundColor:'#3a6086',
			        	   textareaColor:'#3a6086',
			        	   textColor:'#fff',
			        	   textareaBorderColor:'#fff',
			        	   buttonColor:'#de946f',
			        	   optionToContent: function(opt) {
			        		    var axisData = opt.xAxis[0].data;
			        		    var series = opt.series;
			        		    var table = '<table style="width:100%;text-align:center"><tbody><tr>'
			        		                 + '<td>时间</td>'
			        		                 + '<td>' + series[0].name + '</td>'
			        		                 + '</tr>';
			        		    for (var i = 0, l = axisData.length; i < l; i++) {
			        		        table += '<tr>'
			        		                 + '<td>' + axisData[i] + '</td>'
			        		                 + '<td>' + series[0].data[i] + '</td>'
			        		                 + '</tr>';
			        		    }
			        		    table += '</tbody></table>';
			        		    return table;
			        		}
			           },
			          /*   restore: {show: true}, */
			            saveAsImage: {show: true}
			        }
				},
			    /*legend: {
			        orient: 'vertical',
			        left: 'left',
			        data: xVal
			    },*/
			    color:["#1BB2D8","#5bc0de","#99D2DD","#88B0BB","#1C7099","#038CC4","#75ABD0","#AFD6DD","#1790CFs"],
			    series : [
			        {
			            name: '百分比',
			            type: 'pie',
			            radius : '55%',
			            center: ['50%', '60%'],
			            /*data:[
			                {value:335, name:'alert multi control'},
			                {value:310, name:'alert_multi_verify'},
			                {value:234, name:'cur_yottabyte'},
			                {value:135, name:'share'},
			                {value:1548, name:'cj test'}
			            ], */
			            data:pieVAl,
			            itemStyle: {
			                emphasis: {
			                    shadowBlur: 10,
			                    shadowOffsetX: 0,
			                    shadowColor: 'rgba(0, 0, 0, 0.5)'
			                }
			            }
			        }
			    ]
        	});
        	//如果数据为空 则提示暂无数据
        	if(yVal.length == 0){
        		$("#echartsBox3").append('<div class="noDataText">暂无数据</div>')
        	}
        	if(pieVAl.length == 0){
        		$("#echartsBox4").append('<div class="noDataText">暂无数据</div>')
        	}
        	window.addEventListener("resize",function(){
        		myChart05.resize();
        		myChart06.resize();
            });
        	//图表点击事件
        	myChart06.on('click', function (params) {
        		//定义第一次请求
        		var typeEventFirstGet = true;
        		//存储图表盒子标签
        		var $echartBox = $($(this).attr("_zr").dom);
        		//获得日期
        		var date = $echartBox.parent().siblings().children("input").val();       		
        		//隐藏图表
        		$echartBox.parent().parent().hide();
        		//显示列表
        		$echartBox.parent().parent().siblings().show();
        		//更改列表标题
        		$echartBox.parent().parent().siblings().children(".dataListTitle").html(date +' '+ params.name+'事件列表<span class="goToChartBox">返回图表</span>')
        		//更改宽度
        		$echartBox.parents(".col-md-7").addClass("col-md-12");
        		$echartBox.parents(".col-md-7").removeClass("col-md-7");
        		//更改浏览器滚动条
        		var tt = $(document).scrollTop()
        		$(document).scrollTop(tt+659);
        		//定义传参对象
        		//开始时间
        		typeEventObj.starttime = date+' 00:00:00';
        		//结束时间
        		typeEventObj.endtime = date+' 23:59:59';
        		//每页显示条数
        		typeEventObj.size = pageSize;
        		//资产id
        		typeEventObj.equipmentid = str.deviceId;
        		//事件级别
        		typeEventObj.event_type = params.name; 
        		//发送请求 
        		getEventsData(typeEventObj,1,typeEventFirstGet,"#typeEventList");
        		
        		$(".goToChartBox").click(function(){
        			//隐藏列表
            		$(this).parents(".dataList").hide();
            		//显示图表
            		$(this).parents(".dataList").siblings().show();
            		//调整宽度
            		$(this).parents(".col-md-12").addClass("col-md-7");
            		$(this).parents(".col-md-12").removeClass("col-md-12");
            	})
        	})
        	
        	myChart05.on('click', function (params) {
        		//定义第一次请求
        		var typeEventFirstGet = true;
        		//存储图表盒子标签
        		var $echartBox = $($(this).attr("_zr").dom);
        		//获得日期
        		var date = $echartBox.parent().siblings().children("input").val();       		
        		//隐藏图表
        		$echartBox.parent().parent().hide();
        		//显示列表
        		$echartBox.parent().parent().siblings().show();
        		//更改列表标题
        		$echartBox.parent().parent().siblings().children(".dataListTitle").html(date +' '+ params.name+'事件列表<span class="goToChartBox">返回图表</span>')
        		//更改宽度
        		$echartBox.parents(".col-md-7").addClass("col-md-12");
        		$echartBox.parents(".col-md-7").removeClass("col-md-7");
        		//更改浏览器滚动条
        		var tt = $(document).scrollTop()
        		$(document).scrollTop(tt+659);
        		//定义传参对象
        		//开始时间
        		typeEventObj.starttime = date+' 00:00:00';
        		//结束时间
        		typeEventObj.endtime = date+' 23:59:59';
        		//每页显示条数
        		typeEventObj.size = pageSize;
        		//资产id
        		typeEventObj.equipmentid = str.deviceId;
        		//事件级别
        		typeEventObj.event_type = params.name; 
        		//发送请求 
        		getEventsData(typeEventObj,1,typeEventFirstGet,"#typeEventList");
        		
        		$(".goToChartBox").click(function(){
        			//隐藏列表
            		$(this).parents(".dataList").hide();
            		//显示图表
            		$(this).parents(".dataList").siblings().show();
            		//调整宽度
            		$(this).parents(".col-md-12").addClass("col-md-7");
            		$(this).parents(".col-md-12").removeClass("col-md-12");
            	})
        	})
        	
		},
		error:function(err){
			
		}
	});
}
//日志级别报表
function getEchartData4(dateTime){
	$.ajax({
		type:"post",
		url:"../../log/getCountGroupByParam.do",
		data:{"index":"estest","type":"","param":"operation_level","equipmentid":str.deviceId,"time":dateTime},
		async:true,
		success:function(data){
			var obj = data[0];
			var xVal = [];//x轴数据
			var yVal = [];//y轴数据
			for(var i in obj){
				xVal.push(i);
				yVal.push(obj[i])
			}
			myChart07 = echarts.init(document.getElementById('echartsBox5'));
			myChart07.setOption({
				backgroundColor:'#293846',
				color: ['#5bc0de'],
				title : {
			        text: '日志级别数量统计',
			        x:'center',
			        textStyle:{
		        		color:'#5bc0de'
		        	}
			    },
			    tooltip : {
			        trigger: 'axis',
			        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
			            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
			        }
			    },
			    toolbox: {
			        feature: {
			           dataView: {
			        	   show: true, 
			        	   readOnly: false,
			        	   backgroundColor:'#3a6086',
			        	   textareaColor:'#3a6086',
			        	   textColor:'#fff',
			        	   textareaBorderColor:'#fff',
			        	   buttonColor:'#de946f',
			        	   optionToContent: function(opt) {
			        		    var axisData = opt.xAxis[0].data;
			        		    var series = opt.series;
			        		    var table = '<table style="width:100%;text-align:center"><tbody><tr>'
			        		                 + '<td>时间</td>'
			        		                 + '<td>' + series[0].name + '</td>'
			        		                 + '</tr>';
			        		    for (var i = 0, l = axisData.length; i < l; i++) {
			        		        table += '<tr>'
			        		                 + '<td>' + axisData[i] + '</td>'
			        		                 + '<td>' + series[0].data[i] + '</td>'
			        		                 + '</tr>';
			        		    }
			        		    table += '</tbody></table>';
			        		    return table;
			        		}
			           },
			          /*   restore: {show: true}, */
			            saveAsImage: {show: true}
			        }
				},
			    xAxis : [
			        {	
			        	name:'级别',
			            type : 'category',
			            /*data : ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],*/
			            data:xVal,
			            axisTick: {
			                alignWithLabel: true
			            },
			            nameTextStyle:{
			        		color:'#5bc0de'
			        	},
			        	axisLine:{
			        		lineStyle:{
			        			color:'#5bc0de'
			        		}
			        	},
			        }
			    ],
			    yAxis : [
			        {	
			        	name:'数量/条',
			            type : 'value',
			            axisLine:{
			        		lineStyle:{
			        			color:'#5bc0de'
			        		}
			        	},
			        	splitLine:{
			        		lineStyle:{
			        			color:'#5bc0de'
			        		}
			        	},
		            	nameTextStyle:{
			        		color:'#5bc0de'
			        	}
			        }
			    ],
			    series : [
			        {
			            name:'直接访问',
			            type:'bar',
			            barWidth: '60%',
			            /*data:[10, 52, 200, 334, 390, 330, 220]*/
			            data:yVal
			        }
			    ]
			})
			if(yVal.length == 0){
        		$("#echartsBox5").append('<div class="noDataText">暂无数据</div>')
        	}
			window.addEventListener("resize",function(){
				myChart07.resize();
		    });
			//图表点击事件
			//每小时日志数据量统计 图表点击事件
        	myChart07.on('click', function (params) {
        		//定义第一次请求
        		var levelLogFirstGet = true;
        		//存储图表盒子标签
        		var $echartBox = $($(this).attr("_zr").dom);
        		//获得日期
        		var date = $echartBox.siblings().children("input").val();       		
        		//隐藏图表
        		$echartBox.parent().hide();
        		//显示列表
        		$echartBox.parent().siblings().show();
        		//更改列表标题
        		$echartBox.parent().siblings().children(".dataListTitle").html(date +' '+ params.name+' 日志列表<span class="goToChartBox">返回图表</span>')
        		//更改宽度
        		$echartBox.parents(".col-md-5").addClass("col-md-12");
        		$echartBox.parents(".col-md-5").removeClass("col-md-5");
        		//定义传参对象
        		//开始时间
        		levelLogObj.starttime = date+' 00:00:00';
        		//结束时间
        		levelLogObj.endtime = date+' 23:59:59';
        		//日志级别
        		levelLogObj.operation_level = params.name;
        		//每页显示条数
        		levelLogObj.size = pageSize;
        		//资产id
        		levelLogObj.id = str.deviceId;
        		//发送请求 
        		getLogsData(levelLogObj,1,levelLogFirstGet,"#levelLogList");
        		
        		$(".goToChartBox").click(function(){
        			//隐藏列表
            		$(this).parents(".dataList").hide();
            		//显示图表
            		$(this).parents(".dataList").siblings().show();
            		//调整宽度
            		$(this).parents(".col-md-12").addClass("col-md-5");
            		$(this).parents(".col-md-12").removeClass("col-md-12");
            	})
        	})
			
		}
	})
	
}


//导出按钮点击事件
$(".dropdown-menu .downloadPDF").click(function(){
	 var imgBase64Arr = [];//用于存放图表Base64格式
	 //添加数据
  	imgBase64Arr.push(myChart03.getDataURL());
	imgBase64Arr.push(myChart04.getDataURL());
	imgBase64Arr.push(myChart05.getDataURL());
	imgBase64Arr.push(myChart06.getDataURL());
	imgBase64Arr.push(myChart07.getDataURL());
  	var canvas = $("canvas");
  	var contentWidth = canvas.width();
    var contentHeight = canvas.height();
	downloadToPDF(imgBase64Arr,40,contentWidth,contentHeight)
})

//获取日志总量与error的数量 函数
$.ajax({
	type:"post",
	url:"../../log/getIndicesCount.do",
	data:{equipmentid:str.deviceId},
	async:true,
	success:function(data){//[{"indices":643584,"indiceserror":3039}]
		$(".logsCountBox").html(data[0].indices);
		$(".errLogsCountBox").html(data[0].indiceserror);
	}
})
//获取事件总数与高危事件数量
var sfunc = function(data){
	$(".eventCountBox").html(data[0].events);
	$(".hignEventCountBox").html(data[0].eventserror);
}
//发送请求
ajaxPost("../../log/getEventsCount.do",{equipmentid:str.deviceId},sfunc);

//最大显示数据改变事件函数 $this-当前点击的下拉框
function maxShowChange($this){
	//获得总数
	var allC =  $this.siblings('b').html();
	//获得当前改变的值
	var changeVal = $this.val();
	//获得分页盒子的id
	var pageBoxId = $this.parents(".allCountBox").prev().attr("id");
	//当前页
	var curP;
	//判断是哪个分页盒子 选择不同的 当前页数
	if(pageBoxId == 'hourLogPage'){
		curP = hourLogCurPage ;
	}else if(pageBoxId == 'levelLogPage'){
		curP = levelLogCurPage;
	}else if(pageBoxId == 'hourEventPage'){
		curP = hourEventCurPage;
	}else if(pageBoxId == 'typeEventPage'){
		curP = typeEventCurPage;
	}
	//判断选的数值与检索出来的总数的大小   
	if(changeVal - allC < 0){
		//创建分页函数
		createPage(pageBoxId,changeVal,pageSize,curP);
	}else{
		//创建分页函数
		createPage(pageBoxId,allC,pageSize,curP);
	}
	if(changeVal >= 10000000){
		layer.tips('友情提示:数据量过大时,建议使用专项页面进行查看。 ',$this, {
			  tips: [1, '#3595CC'],
			  time: 4000
			});
	}
}	

$(".maxShowData").change(function(){
	maxShowChange($(this))
})