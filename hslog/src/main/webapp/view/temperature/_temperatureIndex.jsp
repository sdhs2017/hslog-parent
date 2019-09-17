
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
                <h5>Echart图表</h5>
                <div align="center">
                <a href="#" class="btn btn-primary" onclick="PoorsighLine()">视力不良率曲线图</a>
                <a href="#" class="btn btn-primary" onclick="PoorsighBar()">视力不良率柱状图</a>
                <a href="#" class="btn btn-primary" onclick="PoorsighPie()">视力饼图</a>
                <a href="#" class="btn btn-primary" onclick="SicknessLine()">眼科发病率曲线图</a>
                </div>
                <div class="ibox-tools">
                    <a class="collapse-link">
                        <i class="fa fa-chevron-up"></i>
                    </a>
                </div>
            </div>
            <div class="ibox-content">
                <div class="row row-lg">
                    <div class="col-sm-12">
                        <div id="PoorsighLine" class="col-sm-12" style="height:500px"></div>
                        <div id="PoorsighPie" class="col-sm-12" style="height:500px;display:none"></div>
                        <div id="SicknessLine" class="col-sm-12" style="height:500px;display:none"></div>
                        <div id="PoorsighBar" class="col-sm-12" style="height:500px;display:none"></div>
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
			                        <form id="difftempForm" class="form-horizontal" role="form">
			                        	<div class="form-group" hidden="true" >
			                                <label class="col-sm-2 control-label">ID</label>
			                                <div class="col-sm-10">
			                                    <input id="TempId" type="text" class="form-control" name="id" value="${Temperature.id}">
			                                </div>
			                            </div>
			                            <div class="form-group">
			                                <label class="col-sm-2 control-label">城市</label>
			                                <div class="col-sm-10">
			                                    <input type="text" class="form-control" name="cityName" value="${Temperature.cityName}">
			                                </div>
			                            </div>
			                            <div class="form-group">
			                                <label class="col-sm-2 control-label">温度</label>
			                                <div class="col-sm-10">
			                                    <input type="text" class="form-control" name="temp" value="${Temperature.temp}">
			                                </div>
			                            </div>
			                            <div class="form-group">
			                                <label class="col-sm-2 control-label">高度</label>
			                                <div class="col-sm-10">
			                                    <input type="text" class="form-control" name="height" value="${Temperature.height}">
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
    <script src="<%=path%>js/china.js"></script>
    <script> 
    //通过ajax获取echart JSON 进而加载数据
    function loadBookEchartByAjax(){
		$.ajax({
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			url:'<%=path%>ophth/getPoorsighLine.do',
			data:{"name":""},
			success:function(data){
				loadEchart(data,'PoorsighLine');
			}
		});
	} 
    //点击视力不良曲线图
    function PoorsighLine(){
    	$("#PoorsighPie").hide();
    	$("#SicknessLine").hide();
    	$("#PoorsighBar").hide();
    	$.ajax({
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			url:'<%=path%>ophth/getPoorsighLine.do',
			success:function(data){
				loadEchart(data,'PoorsighLine');
			}
		});
    	$("#PoorsighLine").show();
    	 return false; //不刷新页面  
    }
   //点击视力不良柱状图
    function PoorsighBar(){
    	$("#PoorsighPie").hide();
    	$("#SicknessLine").hide();
    	$("#PoorsighLine").hide();
    	$.ajax({
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			url:'<%=path%>ophth/getPoorsighBar.do',
			success:function(data){
				loadEchart(data,'PoorsighBar');
			}
		});
    	$("#PoorsighBar").show();
    	 return false; //不刷新页面  
    }
    //点击视力不良饼图
    function PoorsighPie(){
    	$("#PoorsighBar").hide();
    	$("#SicknessLine").hide();
    	$("#PoorsighLine").hide();
    	$.ajax({
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			url:'<%=path%>ophth/getPoorsighPie.do',
			success:function(data){
				loadEchart(data,'PoorsighPie');
			}
		});
    	$("#PoorsighPie").show();
    	 return false; //不刷新页面  
    }
  //点击眼科发病率曲线图
    function SicknessLine(){
    	$("#PoorsighBar").hide();
    	$("#PoorsighPie").hide();
    	$("#PoorsighLine").hide();
    	$.ajax({
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			url:'<%=path%>ophth/getSicknessLine.do',
			success:function(data){
				loadEchart(data,'SicknessLine');
			}
		});
    	$("#SicknessLine").show();
    	 return false; //不刷新页面  
    }
    //Echart加载事件
	function loadEchart(option,id){
		var bookEchart = echarts.init(document.getElementById(id));
		bookEchart.setOption(option);
	}
	$(function() { 
	    //Form数据提交事件初始化
	    $("#difftempForm").submit(function(){  
	    	//提交数据
	        $(this).ajaxSubmit({  
	            type:"post",  //提交方式  
	            dataType:"json", //数据类型  
	            url:"<%=path%>temp/upsert.do", //请求url  
	            success:function(data){ //提交成功的回调函数  
	                if(data.success=="true"){
	                	//清空Form表单数据
	                	$('#difftempForm')[0].reset();
	                	//隐藏表单
	                	$('#modal-form').modal('hide');
	                	//刷新Grid数据
	                	$("#difftempTable").bootstrapTable('refresh', {url: '<%=path%>temp/selectTemp.do'});  
	                }  
	            }
	        });  
	        return false; //不刷新页面  
	    }); 
	    //加载Echart
	    loadBookEchartByAjax();
	    
	    //给table每一行添加onclick事件
	    $("#difftempTable").on("click", "tr", function () {
	    	//获取城市的值
	    	var name=$(this).children('td').eq(2).html();
	    	$.ajax({
				type:'post',
				dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
				url:'<%=path%>temp/getEchart.do',
				data:{"name":name},
				success:function(data){
					loadEchart(data,'PoorsighLine');
				}
			});
        });
	}); 
	//添加按钮
	function tempAdd(){
		//清空Form表单数据
    	$('#difftempForm')[0].reset();
    	//隐藏form
		$('#modal-form').modal({show:true});
	} 
	//模糊查询
	function blurselect(){
		var name=$("#blur").val();
		//往grid里放数据
		<%-- $('#difftempTable').bootstrapTable('refresh',{url:'<%=path%>temp/selectTempByName.do?name='+name}); 
		return false; //不刷新页面 
		--%>
		$.ajax({
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			url:'<%=path%>temp/selectTempByName.do',
			data:{"name":name},
			success:function(data){
				$("#difftempTable").bootstrapTable("load", data); 
			}
		});
	}
	//取消按钮
	function cancel(){
		//清空Form表单数据
    	$('#difftempForm')[0].reset();
		//隐藏form
		$('#modal-form').modal('hide');
	}
	//编辑Form
	function tempEdit(){
		//获取所有选择的行
		var selections = $("#difftempTable").bootstrapTable('getSelections');
		//如果行数为0或大于1
		if(selections.length==0||selections.length>1){
			layer.alert('请选择一条记录进行编辑');
		}else{
			//获取该行id
			var id = selections[0].id;
			//加载Form数据
			$('#difftempForm').form('load','<%=path%>temp/selectById.do?id='+id);
			//显示Form
			$('#modal-form').modal({show:true});
		}
	}
	//删除按钮
	function tempDelete(){
		//获取所有选择的行
		var selections = $("#difftempTable").bootstrapTable('getSelections');
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
				url:'<%=path%>temp/deleteTemp.do',
				data:{"ids":id},
				success:function(data){
					if(data.success=="true"){
						layer.alert('删除成功');
						//刷新Grid数据
	                	$("#difftempTable").bootstrapTable('refresh', {url: '<%=path%>temp/selectTemp.do'});  
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