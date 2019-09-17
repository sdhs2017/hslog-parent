
<html>

<head>

    <meta charset="utf-8">
    <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %> 
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<%String path = "../../";%>
    <link rel="shortcut icon" href="favicon.ico"> 
    <link href="<%=path%>hplus/css/bootstrap.min.css?v=3.3.5" rel="stylesheet">
    <link href="<%=path%>hplus/css/font-awesome.min.css?v=4.4.0" rel="stylesheet">
    <link href="<%=path%>hplus/css/plugins/bootstrap-table/bootstrap-table.min.css" rel="stylesheet">
    <link href="<%=path%>hplus/css/animate.min.css" rel="stylesheet">
    <link href="<%=path%>hplus/css/style.min.css?v=4.0.0" rel="stylesheet">

</head>

<body class="gray-bg">
    <div class="wrapper wrapper-content">
		<div class="ibox float-e-margins">
            <div class="ibox-title">
                <h5>营养评价数据统计</h5>
                <div class="ibox-tools">
                    <a class="collapse-link">
                        <i class="fa fa-chevron-up"></i>
                    </a>
                </div>
            </div>
            <div class="ibox-content">
                <div class="row row-lg">
                    <div class="col-sm-12">
                        <div class="example">
                        <div class="row">
                        	<div class="col-sm-5 m-b-xs">
	                        	
                        	</div>
                        	
   <h2>ComboTree</h2>
	<div class="demo-info">
		<div class="demo-tip icon-tip"></div>
		<div>Click the right arrow button to show the tree.</div>
	</div>
	
	<div style="margin:10px 0;">
		<a href="#" class="easyui-linkbutton" onclick="reload()">Reload</a>
		<a href="#" class="easyui-linkbutton" onclick="setValue()">SetValue</a>
		<a href="#" class="easyui-linkbutton" onclick="getValue()">GetValue</a>
		<a href="#" class="easyui-linkbutton" onclick="disable()">Disable</a>
		<a href="#" class="easyui-linkbutton" onclick="enable()">Enable</a>
	</div>
	<p>Single Select</p>
	<input id="cc" class="easyui-combotree" value="2" data-options="url:'tree_data.json',required:true" style="width:200px;">
	<p>Multiple Select</p>
	<select class="easyui-combotree" name="language" data-options="url:'tree_data.json',cascadeCheck:false" multiple style="width:200px;"></select>
                        	
                        	<div class="col-sm-7">
	                        	<form class="form-inline" role="form">
		                        	<div class="form-group draggable" >
		                                <div class="col-sm-12">
		                                    <select class="form-control" name="" >
		                                        <option>2015学年</option>
		                                        <option>2016学年</option>
		                                        <option>2017学年</option>
		                                        <option>2018学年</option>
		                                    </select>
		                                </div>
		                            </div>
		                            <div class="form-group draggable" >
		                                <div class="col-sm-12">
		                                    <select class="form-control" name="" >
		                                        <option>男</option>
		                                        <option>女</option>
		                                        <option>全部</option>
		                                    </select>
		                                </div>
		                            </div>
					                <div class="form-group">
					                    <input class="form-control" placeholder="请输入您需要查找的内容 …">
					                </div>
				                	<button type="submit" class="btn btn-primary" style="margin-top:8px">搜索</button>
				            	</form>
                        	</div>
                        </div>
                        
                            <table id="OphthalmologyTable" data-toggle="table"  data-pagination="true"  data-mobile-responsive="true">
                                <thead>
                                    <tr>
                                    	<th data-field="" rowspan="3" data-align="center" data-valign="middle">统计方式</th>
                                    	<th data-field="" rowspan="3" data-align="center" data-valign="middle">性别</th>
                                    	<th data-field="" rowspan=3 data-align="center" data-valign="middle">受检人数</th>
                                        <th rowspan=2 colspan=2 data-align="center" data-valign="middle">视力正常</th>
                                        <th rowspan=2 colspan=2 data-align="center" data-valign="middle">色盲色弱</th>
                                        <th rowspan=2 colspan=2 data-align="center" data-valign="middle">沙眼</th>
                                        <th colspan=2 rowspan=2 data-align="center" data-valign="middle">结膜炎</th>
                                    </tr>
                                    <tr>
                                    </tr>
                                    <tr>
                                    	
                                    	<th data-field="" data-align="center">人数</th>
                                    	<th data-field="" data-align="center">%</th>
                                    	<th data-field="" data-align="center">人数</th>
                                    	<th data-field="" data-align="center">%</th>
                                    	<th data-field="" data-align="center">人数</th>
                                    	<th data-field="" data-align="center">%</th>
                                    	<th data-field="" data-align="center">人数</th>
                                    	<th data-field="" data-align="center">%</th>
                                    </tr>
                                </thead>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
   
    <script src="<%=path%>hplus/js/jquery.min.js?v=2.1.4"></script>
    <script src="<%=path%>hplus/js/jquery.form.js"></script>
    <script src="<%=path%>hplus/js/bootstrap.min.js?v=3.3.5"></script>
    <script src="<%=path%>hplus/js/content.min.js?v=1.0.0"></script>
    <script src="<%=path%>hplus/js/plugins/bootstrap-table/bootstrap-table.min.js"></script>
    <script src="<%=path%>hplus/js/plugins/bootstrap-table/bootstrap-table-mobile.min.js"></script>
    <script src="<%=path%>hplus/js/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
    <script src="<%=path%>hplus/js/demo/bootstrap-table-demo.min.js"></script>
    <script src="http://tajs.qq.com/stats?sId=9051096" charset="UTF-8"></script>
    <script src="<%=path%>hplus/js/plugins/layer/layer.js"></script>
    <script src="<%=path%>js/jquery.easyui.min.js"></script>
    <script src="<%=path%>js/echarts.min.js"></script>
    <script> 
    //通过ajax获取echart JSON 进而加载数据
    function loadBookEchartByAjax(){
		$.ajax({
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			url:'<%=path%>book/getEchart.do',
			success:function(data){
				loadEchart(data,'bookEchart');
			}
		});
	}
    //初始化加载事件
    function getMultiResult(){
    	$.ajax({
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			url:'<%=path%>ophth/getOphthalmologyData.do',
			success:function(data){
				$("#OphthalmologyTable").bootstrapTable("load", data); 
			}
		});
    }
    //Echart加载事件
	function loadEchart(option,id){
		var bookEchart = echarts.init(document.getElementById(id));
		bookEchart.setOption(option);
	}
	$(function() { 
	    //Form数据提交事件初始化
	    $("#userForm").submit(function(){  
	    	//提交数据
	        $(this).ajaxSubmit({  
	            type:"post",  //提交方式  
	            dataType:"json", //数据类型  
	            url:"<%=path%>book/upsert.do", //请求url  
	            success:function(data){ //提交成功的回调函数  
	                if(data.success=="true"){
	                	//清空Form表单数据
	                	$('#userForm')[0].reset();
	                	//隐藏表单
	                	$('#modal-form').modal('hide');
	                	//刷新Grid数据
	                	$("#BookTable").bootstrapTable('refresh', {url: '<%=path%>book/selectList.do'});  
	                }  
	            }
	        });  
	        return false; //不刷新页面  
	    }); 
	    //加载Echart
	    //loadBookEchartByAjax();
	    //初始化数据加载
	    getMultiResult();
	}); 
	//取消按钮
	function cancel(){
		//清空Form表单数据
    	$('#userForm')[0].reset();
		//隐藏form
		$('#modal-form').modal('hide');
	}
	//添加
	function bookAdd(){
		//清空Form表单数据
    	$('#userForm')[0].reset();
    	//显示Form
		$('#modal-form').modal({show:true});
	}
	//编辑Form
	function bookEdit(){
		//获取所有选择的行
		var selections = $("#BookTable").bootstrapTable('getSelections');
		//如果行数为0或大于1
		if(selections.length==0||selections.length>1){
			layer.alert('请选择一条记录进行编辑');
		}else{
			//获取该行id
			var id = selections[0].id;
			//加载Form数据
			$('#userForm').form('load',encodeURI('<%=path%>book/selectById.do?id='+id));
			//显示Form
			$('#modal-form').modal({show:true});
		}
	}
	//删除按钮
	function bookDelete(){
		//获取所有选择的行
		var selections = $("#BookTable").bootstrapTable('getSelections');
		//如果行数为0
		if(selections.length==0){
			layer.alert('请选择至少一条记录进行删除');
		}else{
			var id = "";
			for(var i=0;i<selections.length;i++){
				id = id+selections[i].id+",";
			}
			$.ajax({
				type:'post',
				dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
				url:'<%=path%>book/delete.do',
				data:{"ids":id},
				success:function(data){
					if(data.success=="true"){
						layer.alert('删除成功');
						//刷新Grid数据
	                	$("#BookTable").bootstrapTable('refresh', {url: '<%=path%>book/selectList.do'});  
					}else{
						layer.alert('删除失败');
					}
					
				}
			});
		}
	}
	</script>
</body>
</html>