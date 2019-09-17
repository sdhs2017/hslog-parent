var pageIndex = 1;//页码
var pageSize = 3;//每页显示的条数
var searchStutus = false;//用于判断是否是搜索
var num;//用于定义第一次请求
getLogsData(1,'');
//获取 资产数据  pageIndex-- 分页  obj - 传的参数对象
function getLogsData(pageIndex,obj){
	//定义传参对象
	var sendObj = {};
	//成功回调函数
	var sfunc = function(data){
		console.log(data)
		//资产数据
		var logsData = data.result;
		//判断是否是第一次发起的请求   第一次需要创建分页
		if(pageIndex == 1){
			//定义第一次请求
			num = false;
			//总条数
			var count = data.count;
			//总页数
			var pageCount = Math.ceil(count/pageSize);
			//创建分页
			creatPage(pageCount);
			//拼接到页面
			jointLogsList(logsData);
			num = true;
		}else{
			//拼接到页面
			jointPropertyList(propertyData);
		}
		
	}
	//发送请求
	ajaxPost("logsdata.json",sendObj,sfunc);
}
//创建分页函数   pageCount-总页数
function creatPage(pageCount){
	$(".tools_page").pagination1(pageCount, {
			num_edge_entries: 1, //边缘页数
			num_display_entries: 4, //主体页数
			callback: pageselectCallback,
			items_per_page: 1, //每页显示1项
			prev_text: "&laquo;",
			next_text: "&raquo;"
		});
}
//分页回调函数
function pageselectCallback(page_index){
	
	$("#theadCheck").removeAttr("checked"); 
	if(num == true){
		//判断是否是搜索产生的分页  false-不是
		if(searchStutus == false){
			getPropertyData(page_index+1);
		}else{
			//调用函数显示数据
			searchDevice(page_index+1);
		}
		
	}
	
}
//拼接日志列表 到页面
function jointLogsList(data){
	var html = '';
	for(var i in data){
		var obj = data[i];
		//替换风险状态
		var level = '';
		if(obj.level == "INFO"){
			level = '<span class="label label-info">INFO</span>'
		}else if(obj.level == "ERROR"){
			level = '<span class="label label-danger">ERROR</span>'
		}	
		html += '<tr>'   
             +  	'<td class="" data-id="'+obj.id+'"></td>'
             +      '<td class="logs_time">'+obj.logtime+'</td>'
             +      '<td class="logs_level">'+level+'</td>'
             +      '<td class="logs_con">'+obj.operation_des+'</td>'
             +      '<td class="td_tools">'
             +      	'<i class="glyphicon glyphicon-list-alt" title="查看详情"></i>'
             +      	'<i class="glyphicon glyphicon-remove" title="删除"> </i>'                 
             +      '</td>'
             +	'</tr>'
	}
	//添加到页面中
	$("#logs_list tbody").html(html);
}