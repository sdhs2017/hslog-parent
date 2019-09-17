var pageIndex = 1;//页码
var pageSize = 3;//每页显示的条数
var searchStutus = false;//用于判断是否是搜索
var num;//用于定义第一次请求
getPropertyData(1,'');
//获取 资产数据  pageIndex-- 分页  obj - 传的参数对象
function getPropertyData(pageIndex,obj){
	//定义传参对象
	var sendObj = {};
	//成功回调函数
	var sfunc = function(data){
		console.log(data)
		//资产数据
		var propertyData = data.result;
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
			jointPropertyList(propertyData);
			num = true;
		}else{
			//拼接到页面
			jointPropertyList(propertyData);
		}
		
	}
	//发送请求
	ajaxPost("data.json",sendObj,sfunc);
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

//拼接资产列表 函数
function jointPropertyList(data){
	var html = '';
	for(var i in data){
		var obj = data[i];		
		//替换系统类型
		var ganre = '';
		if(obj.ganre == 1){
			ganre = "Apache";
	 	}else if(obj.ganre == 2){
	 		ganre = "AIX5";
	 	}else if(obj.ganre == 3){
	 		ganre = "Big Iron";
	 	}else if(obj.ganre == 4){
	 		ganre = "Arcsight Express";
	 	}else if(obj.ganre == 5){
	 		ganre = "其他";
	 	}
		//替换设备类型
		var type = '';
		if(obj.type == 1){
	 		type = "服务器";
	 	}else if(obj.type == 2){
	 		type = "交换机";
	 	}else if(obj.type == 3){
	 		type = "防火墙";
	 	}else if(obj.type == 4){
	 		type = "路由器";
	 	}else if(obj.type == 5){
	 		type = "其他";
	 	}
		//替换风险状态
		var status = '';
		if(obj.status == 1){
			status = '<span class="label label-info">低</span>'
		}else if(obj.status == 2){
			status = '<span class="label label-warning">中</span>'
		}else if(obj.status == 3){
			status = '<span class="label label-danger">高</span>'
		}	
		html += '<tr>'   
             +   	'<td><input type="checkbox" data-id="'+obj.id+'"></td>'
             +      '<td class="property_name">'
             +        	'<span data-id="'+obj.id+'">'+obj.name+'</span>'
             +      '</td>'
             +      '<td class="property_ip">'+obj.ip+'</td>'
             +      '<td class="property_ganre" data-ganre="'+obj.ganre+'">'+ganre+'</td>'
             +      '<td class="property_type" data-type="'+obj.type+'">'+type+'</td>'                                            
             +		'<td class="property_createtime">'+obj.createTime+'</td>' 
             +      '<td class="property_status">'+status+'</td>'
             +       '<td class="td_tools">'
             +      	'<i class="glyphicon glyphicon-edit property_revise" title="修改"> </i> '
             +      	'<i class="glyphicon glyphicon-remove property_remove" title="删除"> </i>'                 
             +       '</td>'
             +	'</tr> '
	}
	//添加到页面中
	$("#property_list>tbody").html(html);
	
	//$("#tableHead").css("display","table");
//	$("#property_list>thead").css("display","none");
}

//点击添加资产按钮 事件函数
$("#add_property").click(function(){
	sessionStorage.setItem("propertyId", "addBtn");
	//跳转到 添加资产页面
	location.href="addProperty.html";
})
//表头中的复选框 点击全选或取消全选
$('#theadCheck').click(function(){	
	//如果checkbox_Status == 0 则未选中
	if($('#theadCheck')[0].checked == true){
		//更改状态
		$("#property_list>tbody input[type=checkbox]").prop("checked", "checked");
	}else if($('#theadCheck')[0].checked == false){
		//更改状态
		$("#property_list>tbody input[type=checkbox]").removeAttr("checked"); 
	}
})
//修改按钮 点击事件
$("#property_list").on("click",".property_revise",function(){
	//获得资产Id
	var id = $(this).parent().siblings(".property_name").children("span").attr("data-id");
	// 储存在本地
	sessionStorage.setItem("propertyId", id);
	//跳转到 添加资产页面
	location.href="addProperty.html";
	
})
//设备删除 按钮点击事件
$("#property_list").on("click",".property_remove",function(){
	//获得资产Id
	var id = $(this).parent().siblings(".property_name").children("span").attr("data-id");
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
			getPropertyData(1,"");
		}
		//失败回调函数
		var efunc = function(data){
			layer.msg("删除失败",{icon:5});
			console.log(data);
		}
		//发送请求
		ajaxPostWithOutLayer("",{id:id},sfunc,"",efunc)
		//关闭弹窗
		layer.close();
	}, function(){
	  layer.close();
	});
})

//点击批量删除按钮 执行函数
$("#remove_allProperty").click(function(){
	//获取被选择的复选框
	var checkboxs = $("#property_list>tbody input[type=checkbox]:checked");
	var id = '';
	//判断是否又被选中的  没有则提示 有则继续执行删除操作
	if(checkboxs.length == 0){
		layer.msg('未选中任何用户',{icon: 5});
	}else{
		//询问框
		layer.confirm('您确定删除么？', {
		  btn: ['确定','取消'] //按钮
		}, function(index){
			layer.close(index);
			//循环拼接id值
			for(var i = 0; i< checkboxs.length;i++){
				id += checkboxs[i].getAttribute("data-id")+',';
			}	
			 //发送数据到后台 进行删除
			 //成功回调函数
			var sfunc = function(data){
				layer.msg("删除成功",{icon:1});
				//重新加载谁被列表
				getPropertyData(1);
			}
			//失败回调函数
			var efunc = function(data){
				layer.msg("删除失败",{icon:5});
				console.log(data);
			}
			//发送请求
			ajaxPostWithOutLayer("",{id:id},sfunc,"",efunc)
			//关闭弹窗
			layer.close();
		}, function(){
		  layer.close();
		});
	}
	
})