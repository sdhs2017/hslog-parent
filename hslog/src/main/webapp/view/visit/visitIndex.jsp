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
                        <a class="btn btn-primary" onclick="searchs()">搜索</a>
                        <a data-toggle="modal" class="btn btn-primary" href="MyIndex.html#modal-form">添加</a>
                        <a class="btn btn-primary" onclick="visitEdit()">修改</a>
                        <a class="btn btn-primary" onclick="visitDelete()">删除</a>
                        <div class="col-sm-5">
			        	 		<input type="text" class="form-control" id="typ"  name="typ"  placeholder="">
			      			</div>
                            <table id="VisitTable" data-toggle="table" data-url="<%=path%>visit/selectListl.do" data-pagination="true" data-height="488" data-mobile-responsive="true">
                                <thead>
                                    <tr>
                                    	<th data-field="state" data-checkbox="true"></th>
                                    	<th data-field="id">ID</th>
                                        <th data-field="uamark">用户编号</th>
                                        <th data-field="gender">性别</th>
                                        <th data-field="age">年龄</th>
                                        <th data-field="types">访问途径</th>
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
            <div class="ibox-content">
                <div class="row row-lg">
                    <div class="col-sm-12">
                        <div id="visitEchart" class="col-sm-12" style="height:500px"></div>
                    </div>
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
			                                    <input id="BookId" type="text" class="form-control" name="id" value="${Visit.id}">
			                                </div>
			                            </div>
			                            <div class="form-group">
			                                <label class="col-sm-2 control-label">用户号</label>
			                                <div class="col-sm-10">
			                                    <input type="text" class="form-control" name="uamark" value="${Visit.uamark}">
			                                </div>
			                            </div>
			                            <div class="form-group">
			                                <label class="col-sm-2 control-label">性别</label>
			                                <div class="col-sm-10">
			                                    <input type="text" class="form-control" name="gender" value="${Visit.gender}">
			                                </div>
			                            </div>
			                            <div class="form-group">
			                                <label class="col-sm-2 control-label">年龄</label>
			                                <div class="col-sm-10">
			                                    <input type="text" class="form-control" name="age" value="${Visit.age}">
			                                </div>
			                            </div>
			                            <div class="form-group">
			                                <label class="col-sm-2 control-label">访问途径</label>
			                                <div class="col-sm-10">
			                                    <input type="text" class="form-control" name="types" value="${Visit.types}">
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
    <script> 
    //通过ajax获取echart JSON 进而加载数据
    function loadVisitEchartByAjax(){
		$.ajax({
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			url:'<%=path%>visit/getEchart.do',
			success:function(data){
				loadEchart(data,'visitEchart');
			}
		});
	}
    //Echart加载事件
	function loadEchart(option,id){
		var visitEchart = echarts.init(document.getElementById(id));
		visitEchart.setOption(option);
	}
	$(function() { 
	    //Form数据提交事件初始化
	    $("#userForm").submit(function(){  
	    	//提交数据
	        $(this).ajaxSubmit({  
	            type:"post",  //提交方式  
	            dataType:"json", //数据类型  
	            url:"<%=path%>visit/upsertl.do", //请求url  
	            success:function(data){ //提交成功的回调函数  
	                if(data.success=="true"){
	                	//清空Form表单数据
	                	$('#userForm')[0].reset();
	                	//隐藏表单
	                	 $('#myModal').modal('hide');
	                	//刷新Grid数据
	                	$("#VisitTable").bootstrapTable('refresh', {url: '<%=path%>visit/selectListl.do'});  
	                }  
	            }
	        });  
	        return false; //不刷新页面  
	    }); 
	    //加载Echart
	    loadVisitEchartByAjax();
	}); 
	//取消按钮
	function cancel(){
		//清空Form表单数据
    	$('#userForm')[0].reset();
		//隐藏form
		$('#modal-form').modal('hide');
	}
	//编辑Form
	function visitEdit(){
		//获取所有选择的行
		var selections = $("#VisitTable").bootstrapTable('getSelections');
		//如果行数为0或大于1
		if(selections.length==0||selections.length>1){
			layer.alert('请选择一条记录进行编辑');
		}else{
			//获取该行id
			var id = selections[0].id;
			//加载Form数据
			$('#userForm').form('load','<%=path%>visit/selectByIdl.do?id='+id);
			//显示Form
			$('#modal-form').modal({show:true});
		}
	}
	//搜索事件
	function searchs(){
		//获取输入的值
		var types = $("#typ").val();
		$("#VisitTable").bootstrapTable('refresh',{url: encodeURI('<%=path%>visit/selform.do?types='+types)});  
		return false; //不刷新页面 
	}
	//给每一行tr添加事件 点击显示此类型的图形
	$("#VisitTable").on("click", "tr", function () {
    	//获取id
    	var types=$(this).children('td').eq(5).html();
    	$.ajax({
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			data:{"types":types},
			url:'<%=path%>visit/getEchart.do',
			success:function(data){
				loadEchart(data,'visitEchart');
			}
		});
    });
	//删除按钮
	function visitDelete(){
		//获取所有选择的行
		var selections = $("#VisitTable").bootstrapTable('getSelections');
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
				url:'<%=path%>visit/deletel.do',
				data:{"ids":id},
				success:function(data){
					if(data.success=="true"){
						layer.alert('删除成功');
						//刷新Grid数据
	                	$("#VisitTable").bootstrapTable('refresh', {url: '<%=path%>visit/selectListl.do'});  
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