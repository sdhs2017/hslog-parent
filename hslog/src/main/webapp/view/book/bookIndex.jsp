
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
                <h5>Form表单基本操作</h5>
                <div class="ibox-tools">
                    <a class="collapse-link">
                        <i class="fa fa-chevron-up"></i>
                    </a>
                </div>
            </div>
            <div class="ibox-content">
                <div class="row row-lg">
                    <div class="col-sm-12">
                        <h4 class="example-title">从URL加载</h4>
                        <div class="example">
                        <a  class="btn btn-primary" onclick="bookAdd()">添加</a>
                        <a class="btn btn-primary" onclick="bookEdit()">修改</a>
                        <a class="btn btn-primary" onclick="bookDelete()">删除</a>
                        <a class="btn btn-primary" onclick="exception()">异常测试</a>
                        <button class="btn btn-primary" id="redis" onclick="redis()">redis测试</button>
                        <input placeholder="请输入作者，书名" id="select" />
                        <button id="ss"  onclick="qualy()" >搜索</button>
                            <table id="BookTable" data-toggle="table" data-pagination="true" data-height="488" data-mobile-responsive="true">
                                <thead>
                                    <tr>
                                    	<th data-field="state" data-checkbox="true"></th>
                                    	<th data-field="id">ID</th>
                                        <th data-field="bookName">书名</th>
                                        <th data-field="writer">作者</th>
                                        <th data-field="date">时间</th>
                                    </tr>
                                </thead>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="wrapper wrapper-content">
		<div class="ibox float-e-margins">
            <div class="ibox-title">
                <h5>Echart图表</h5>
                <div class="ibox-tools">
                    <a class="collapse-link">
                        <i class="fa fa-chevron-up"></i>
                    </a>
                </div>
            </div>
            
        </div>
    </div>
    <div id="modal-form" class="modal fade" aria-hidden="true" >
    	<div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-body">
			    	<div class="row">
			            <div class="col-sm-12">
			                <div class="ibox float-e-margins">
			                    <div class="ibox-title">
			                        <h5>用户基本信息</h5>
								</div>
								<div class="ibox-content">
			                        <form id="userForm" class="form-horizontal" role="form">
			                        	<div class="form-group" hidden="true" >
			                                <label class="col-sm-2 control-label">ID</label>
			                                <div class="col-sm-10">
			                                    <input id="BookId" type="text" class="form-control" name="id" value="${Book.id}">
			                                </div>
			                            </div>
			                            <div class="form-group">
			                                <label class="col-sm-2 control-label">书名</label>
			                                <div class="col-sm-10">
			                                    <input type="text" class="form-control" name="bookName" value="${Book.bookName}">
			                                </div>
			                            </div>
			                            <div class="form-group">
			                                <label class="col-sm-2 control-label">作者</label>
			                                <div class="col-sm-10">
			                                    <input type="text" class="form-control" name="writer" value="${Book.writer}">
			                                </div>
			                            </div>
			                            <div class="form-group">
			                                <label class="col-sm-2 control-label">时间</label>
			                                <div class="col-sm-10">
			                                    <input type="text" class="form-control" name="date" value="${Book.date}">
			                                </div>
			                            </div>
			                        	<div class="hr-line-dashed"></div>
			                            <div class="form-group">
			                                <div class="col-sm-4 col-sm-offset-2">
			                                    <button id="uesrFormSubmit" class="btn btn-primary" type="submit">保存内容</button>
			                                    <button class="btn btn-white" type="button" onclick="cancel()">撤销</button>
			                                </div>
			                            </div>
			                        </form>
			                    </div>
							</div>
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
    <script src="<%=path%>hplus/js/plugins/layer/layer.min.js"></script>
    <script src="<%=path%>util/ajax.js"></script>
    <script> 
    //初始化加载事件
    function getMultiResult(){
    	var url = '<%=path%>book/getMapListResult.do';
    	//成功后回调函数
    	var sFunc = function(data){
    	
    		//加载数据
			$("#BookTable").bootstrapTable("load", data.r1); 
			//设置默认值
			//取值
			//构建ajax提交相关参数  url param successFunction...
			//ajaxPost(....)
    	};
    	//获取数据并通过回调函数进行数据加载。
    	ajaxPost(url,{},sFunc);
    }
  
	$(function() { 
	    //Form数据提交事件初始化
	    $("#userForm").submit(function(){  
	    	var url="<%=path%>book/upsert.do";
	    	var param={"state":"123"};
	    	var sfunc = function(){
	    		//清空Form表单数据
            	$('#userForm')[0].reset();
            	//隐藏表单
            	$('#modal-form').modal('hide');
            	//刷新Grid数据
            	$("#BookTable").bootstrapTable('refresh', {url: '<%=path%>book/selectList.do'});  
	    	};
	    	//提交
	    	ajaxFormSubmit(this,url,param,sfunc);
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
			layer.msg('请选择一条记录进行编辑',{icon:0});
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
			layer.msg('请选择至少一条记录进行删除',{icon:0});
		}else{
			var id = "";
			for(var i=0;i<selections.length;i++){
				id = id+selections[i].id+",";
			}
			var sFunc = function(){
				//刷新Grid数据
            	$("#BookTable").bootstrapTable('refresh', {url: '<%=path%>book/selectList.do'});
			};
			ajaxPost('<%=path%>book/delete.do',{"ids":id},sFunc);
		}
	}
	//异常测试
	function exception(){
		var func = function(){
			alert(123);
		};
		var param = {"state":"falsestate0"};
		ajaxPost('<%=path%>book/getException.do',param,func);
	}
	
	
	function qualy(){
		var url = '<%=path%>book/selectByquery.do';
		var query =$("#select").val();
		
    	//成功后回调函数
    	var sFunc = function(data){
    	
    		//加载数据
			$("#BookTable").bootstrapTable("load", data); 
			//设置默认值
			//取值
			//构建ajax提交相关参数  url param successFunction...
			//ajaxPost(....)
    	};
    	//获取数据并通过回调函数进行数据加载。
    	ajaxPost(url,{"query":query},sFunc);
	}
	  
	
	
	
	</script>
</body>
</html>