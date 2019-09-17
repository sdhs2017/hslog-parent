
<html>

<head>
    <meta charset="utf-8">
    <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %> 
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<%String path = "../../";%>
    <link rel="shortcut icon" href="favicon.ico"> 
    <link href="<%=path%>hplus/css/bootstrap.min.css?v=3.3.5" rel="stylesheet">
    <link href="<%=path%>hplus/css/font-awesome.min.css?v=4.4.0" rel="stylesheet">
    <link href="<%=path%>hplus/css/animate.min.css" rel="stylesheet">
    <link href="<%=path%>hplus/css/style.min.css?v=4.0.0" rel="stylesheet">
	<link href="<%=path%>css/easyui.css" rel="stylesheet">
	<link href="<%=path%>js/bootstrap-multiselect/bootstrap-multiselect.css" rel="stylesheet">
	<link rel="stylesheet" href="<%=path%>js/jqwidgets/styles/jqx.base.css" type="text/css" />
	<style type="text/css">
		.tasklog .jqx-grid-content .jqx-grid-cell {
			border-style: solid;
			border-width: 0px 1px 1px 0px;
			margin-right: 1px;
		 	margin-bottom: 1px;
		 	border-color: transparent;  
		    background: #fff;
		    white-space: nowrap;
		    font-weight: normal; 
		    font-size: inherit;
		    overflow: hidden; 
		    position: absolute !important; 
		    height: 100%; 
		 }
		 .tasklog .jqx-grid-cell.jqx-item{
		   background-color: transparent;
		   border-color: #aaa;
		}
	</style>
</head>

<body class="gray-bg">
    <div class="wrapper wrapper-content">
		<div class="ibox float-e-margins">
            <div class="ibox-title">
                <h5>任务调度</h5>
                <div class="ibox-tools">
                    <form style="margin-top:-12px;" class="form-inline" role="form">
                    		 <a class="btn btn-primary" onclick="setData(id)" style=" margin-top:4px;" >增加</a>
                    		 <a id="updeta" class="btn btn-primary" style=" margin-top:4px;background-color: dimgray;">编辑</a>
                    		 <a id="start" class="btn btn-primary" style=" margin-top:4px;background-color: dimgray;">执行</a>
                    		 <a id="delete" class="btn btn-primary" style=" margin-top:4px;background-color: dimgray;">删除</a>
                    		 <a id="open" class="btn btn-primary" style=" margin-top:4px;background-color: dimgray;">启用</a>
                    		 <a id="close" class="btn btn-primary" style=" margin-top:4px;background-color: dimgray;">停用</a>
                    		 <input type="text" id="selectByname" value="" placeholder="请输入任务名称" style="height: 34px;">
	                		 <a class="btn btn-primary" style=" margin-top:4px;" onclick="selectTask()"><i class="glyphicon glyphicon-check"></i>&nbsp;&nbsp;查询</a>
	            	</form>
                </div>
            </div>
            <div class="ibox-content">
                <div class="row row-lg">
                    <div class="col-sm-12">
                        <div class="example">
                        <div class="row">
                        	<div class="col-sm-4 m-b-xs">
                        		
                        	</div>
                        	<div class="col-sm-8">
	                        	
                        	</div>
                        </div>
                        	<div id="treeGrid">
    						</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 编辑页面 -->
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
			<form id="addform" class="form-horizontal" role="form">
	 		<div class="modal-header">
         		<h4 class="modal-title" id="addtask" align="center">新增任务</h4>
         		<h4 class="modal-title" id="updatetask" style="display: none" align="center">修改任务</h4>
         		<input type="hidden" id="Id" name="ID" value="${Task.ID}"/>
    		 </div>
			<div class="modal-body">
   				<div class="form-group">
      				<label for="firstname" class="col-sm-4 control-label">任务目录:</label>
      			<div class="col-sm-5">
        	 		<input type="text" class="form-control" id="fName" readonly="readonly" name="FName" value="" placeholder="任务管理目录">
      			</div>
      			<div class="col-sm-3"></div>
   				</div> 
   				<div class="form-group">
      				<label for="firstname" class="col-sm-4 control-label">任务名称:</label>
      			<div class="col-sm-5">
        	 		<input type="text" class="form-control" id="taskname"  name="TASKNAME" value="${Task.TASKNAME}" placeholder="请输入任务名称">
      			</div>
      			<div class="col-sm-3"></div>
   				</div> 
   				<div class="form-group">
      				<label for="firstname" class="col-sm-4 control-label">任务说明:</label>
      			<div class="col-sm-5">
        	 		<input type="text" class="form-control" id="taskexplain" name="TASKEXPLAIN" value="${Task.TASKEXPLAIN}" placeholder="请输入任务说明">
      			</div>
      			<div class="col-sm-3"></div>
   				</div> 
   				<div class="form-group">
      				<label for="firstname" class="col-sm-4 control-label">
      				<input type="checkbox" onclick="updatecycle()" id="iscycle" name="iscycle">
      				任务周期:</label>
      			<div class="col-sm-5">
        	 		<input type="text" class="form-control" id="taskcycle"  name="TASKCYCLE" value="${Task.TASKCYCLE}" placeholder="请输入任务周期(Linux crontab格式)">
      			</div>
      			<div class="col-sm-3"></div>
   				</div> 
   				<div class="form-group">
      				<label for="firstname" class="col-sm-4 control-label">任务内容:</label>
      			<div class="col-sm-5">
        	 		<textarea class="form-control"  id="taskcontent"  name="TASKCONTENT" placeholder="请输入任务内容"></textarea>
      			</div>
      			<div class="col-sm-3"></div>
   				</div>  
   				<div class="form-group">
      				<label for="firstname" class="col-sm-4 control-label">
      				<input type="checkbox" id="taskstate" name="taskstate">
      				启用</label>
      			<div class="col-sm-5">
      			</div>
      			<div class="col-sm-3"></div>
   				</div> 
			</div>
   			<div class="modal-footer" >
      			<div class="col-sm-offset-2 col-sm-10">
         			<button id="button" type="submit"  class="btn btn-primary btn-sm">保存</button>&nbsp;&nbsp;&nbsp;&nbsp;
         			<button type="button" class="btn btn-info btn-sm" onclick="$('#myModal').modal('hide');">返回</button>
      			</div>
   			</div>
			</form>
			</div>
		</div>
	</div>
    <script src="<%=path%>hplus/js/jquery.min.js?v=2.1.4"></script>
    <script type="text/javascript" src="<%=path%>js/jqwidgets/jqx-all.js"></script>
    <script src="<%=path%>hplus/js/jquery.form.js"></script>
    <script src="<%=path%>hplus/js/bootstrap.min.js?v=3.3.5"></script>
    <script src="<%=path%>hplus/js/content.min.js?v=1.0.0"></script>
    <script src="http://tajs.qq.com/stats?sId=9051096" charset="UTF-8"></script>
    <script src="<%=path%>hplus/js/plugins/layer/layer.js"></script>
    <script src="<%=path%>js/jquery.easyui.min.js"></script>
    <script src="<%=path%>js/echarts.min.js"></script>
    <script src="<%=path%>util/ajax.js"></script>
	<script type="text/javascript">
		//设置全局的表示付
		var identifiers=0;
		//设置全局的json
    	var employees ="";
    	//设置全局变量id
        var GlobalID="";
    	//初始化加载数据
        function selectTask(){
        	var url = '<%=path%>tasks/selectTask.do';
        	//成功后回调函数
        	var byname=$("#selectByname").val().replace(/[ ]/g,"");
        	var param={"tname":""};
        	if(byname!=""){
        		param={"tname":byname};
        	}
        	var sFunc = function(data){
        		employees=data;
        		// prepare the data
                var source =
                {
                    dataType: "json",
                    dataFields: [
                        { name: 'ID', type: 'string' },
                        { name: 'FID', type: 'string' },
                        { name: 'TASKNAME', type: 'string' },
                        { name: 'TASKCONTENT', type: 'string' },
                        { name: 'TASKEXPLAIN', type: 'string' },
                        { name: 'TASKCYCLE', type: 'string' },
                        { name: 'TASKSTATE', type: 'string' },
                        { name: 'EXECSTATE_SNAPSHOT', type: 'string' }
                    ],
                    hierarchy:
                    {
                        keyDataField: { name: 'ID' },
                        //设置子节点
                        parentDataField: { name: 'FID' }
                    },
                    id: 'ID',
                    localData: employees
                };
                var dataAdapter = new $.jqx.dataAdapter(source, {
                    beforeLoadComplete: function (records) {
                        return records;
                    }
                }
                );
                // create Tree Grid
                $("#treeGrid").jqxTreeGrid({
                	width: 1065,
                    //是否分页
                    pageable: true,
                    //一页显示条数
                    pageSize: 3,
                    //数据源
                    source: dataAdapter,
                    rowDetails: true,
                    rowDetailsRenderer: function (rowKey, row) {
                    	//来区分有值
                    	identifiers=identifiers+1;;
        	          	var details = "<div class='container-fluid' style='height: 90px;'><div class='row-fluid'>"
        	          		+"<div class='col-xs-2'><div style='margin-top: 5px;margin-left: 20px;'><input style='margin-top: 5px;background-color: dimgray;color:white;border-radius:5px;border:none;height:30px;outline:none;' id='"+rowKey+"plain' type='button' onclick='selectexplain(\""+rowKey+"\")' value='任务说明'><br/><input style='background-color: #1ab394;margin-top: 10px;color:white;border-radius:5px;border:none;height:30px;outline:none;' id='"+rowKey+"log' type='button' onclick='selectgrid(\""+rowKey+"\")' value='任务日志'></div></div>"
        	          		+"<div class='col-xs-10' style='height:85px;'><div id='"+rowKey+"sm' style='border-radius:3px;margin-top: 0.5px;margin-left: -35px;height:85px;width:728px;border:1px solid #CCC;'><div style='margin: 10px;'><b>任务描述:</b><span id='"+rowKey+"'>"+row.TASKEXPLAIN+"</span></div><div style='margin: 10px;'><b>任务内容:</b><span id='"+rowKey+1+"'>"+row.TASKCONTENT+"</span></div></div>"
        	          		+"<div id='"+rowKey+"rz' style='display:none;margin-left: -35px'><div class='tasklog' id='"+rowKey+"grid'></div></div></div></div></div>";
                        return details;
                    },
                    rendered: function () {
                    	//点击grid行时
        	          	$(".tasklog").on("rowclick", function (event){
        			    	var args = event.args;
        			        // 行的可见索引
       			          	var visibleIndex = args.visibleindex;
       			          	//开始查询数据
       			          	var url = '<%=path%>tasks/selectTaskLogByTaskId.do';
       			            var param={"ID":args.row.bounddata.TASKID};
       			            // 最初的事件
       			            var ev = args.originalEvent;
       		    	        //成功后回调函数设置
       		    	        var sFunc = function(data){
       		    	          	//返回这行数据的内容
       		    	          	layer.open({
       								type: 1,
       								title: '返回结果',
       								closeBtn:1,
       								area: ['400px','200px'],
       								//skin: 'layui-layer-nobg', //没有背景色
       								shadeClose: true,
       								content: data[visibleIndex].RETURNINFO
       								}); 
       		    	         };
       		    	         ajaxPost(url,param,sFunc);
       	          		}); 
                    },
                    ready: function () {
                        $("#treeGrid").jqxTreeGrid('expandRow', "2");
                    },
                    columns: [
                      { text: '任务名称', dataField: 'TASKNAME', width: 230 },
                      { text: '任务说明', dataField: 'TASKEXPLAIN', width: 200 },
                      { text: '任务内容', dataField: 'TASKCONTENT', width: 200 },
                      { text: '执行状态', dataField: 'EXECSTATE_SNAPSHOT' },
                      { text: '任务状态', dataField: 'TASKSTATE' }
                    ]
                }); 
              	//接触绑定的onclick事件
       			$("#start").unbind('click');
    			$("#delete").unbind('click');
    			$("#updeta").unbind('click');
    			$("#open").unbind('click');
    			$("#close").unbind('click');
    			document.getElementById('open').style.backgroundColor="dimgray"; 
           		document.getElementById('close').style.backgroundColor="dimgray"; 
    			document.getElementById('start').style.backgroundColor="dimgray"; 
           		document.getElementById('updeta').style.backgroundColor="dimgray"; 
    			document.getElementById('delete').style.backgroundColor="dimgray";
    			GlobalID="";
        		};
        		//获取数据并通过回调函数进行数据加载。
        		ajaxPost(url,param,sFunc);
        };
      	//添加grid行点击事件
        function rowclick(){
        	//当点击一行时
            $('#treeGrid').on('rowClick',function (event){
            	// 这一行的对象
            	var args = event.args;
            	// 这一行的id
            	var key = args.key;
            	GlobalID=key;
            	/*//这行的复选框选中
            	$("#treeGrid").jqxTreeGrid('checkRow',key); */
            	// 点击这一行列名
            	var dataField = args.dataField;
            	//如果不为空的话
            	if(GlobalID!=""){
            		var url = '<%=path%>tasks/selectTaskById.do';
                    var param={"ID":GlobalID,"updateid":"0"};
            	    //成功后回调函数
            	    var sFunc = function(data){
            	    //当执行状态不为执行中时
            	    if(data.EXECSTATE_SNAPSHOT!="1"){
            	    	//接触绑定的onclick事件
                    	$("#updeta").unbind('click');
		                $("#start").unbind('click');
		           	    $("#delete").unbind('click');
               		    //绑定onclick事件
               			$("#updeta").click(function(){
               				setData(1);
               			}); 
      	          		$("#start").click(function(){
               				Start();
               			});
       	          		$("#delete").click(function(){
         					deleteData();
         				});
               		    //去掉颜色
	          			document.getElementById('delete').style.backgroundColor="";
          			 	document.getElementById('start').style.backgroundColor=""; 
                        document.getElementById('updeta').style.backgroundColor="";
                        	//当任务状态为停用时,切周期不为空
     	          			if(data.TASKSTATE=="0"){
     	          				$("#open").unbind('click');
      	          				$("#open").click(function(){
               						OpenState();
               					});
      	          				$("#close").unbind('click');
      	          				document.getElementById('open').style.backgroundColor="";
      	          				document.getElementById('close').style.backgroundColor="dimgray";
       	          			}else{
       	          				$("#close").unbind('click');
       	          				$("#close").click(function(){
                					CloseState();
                				});
         	          			document.getElementById('close').style.backgroundColor=""; 
         	          			$("#open").unbind('click');
         	          			document.getElementById('open').style.backgroundColor="dimgray"; 
       	          			}	
      	          		}else{
      	          			//接触绑定的onclick事件
      		          		$("#start").unbind('click');
      		 		    	$("#delete").unbind('click');
      		 		    	$("#updeta").unbind('click');
      		 		    	$("#open").unbind('click');
      		 		    	$("#close").unbind('click');
      		 		    	document.getElementById('open').style.backgroundColor="dimgray"; 
      		              	document.getElementById('close').style.backgroundColor="dimgray"; 
      		 		    	document.getElementById('start').style.backgroundColor="dimgray"; 
      		              	document.getElementById('updeta').style.backgroundColor="dimgray"; 
      		 		    	document.getElementById('delete').style.backgroundColor="dimgray";
      	          		}
      	          	}
      	          	//获取数据并通过回调函数进行数据加载。
      	        	ajaxPost(url,param,sFunc);
      		    }
      		});
        }
    	$(document).ready(function () {
    		//加载数据
    	 	selectTask();
    		//添加grid行点击事件
    	 	rowclick();
    	 	//Form数据提交事件初始化
 	    	$("#addform").submit(function(){  
 	    	var url="<%=path%>tasks/updateoraddTask.do";
 	    	var param="";
 	    	//查询复选框是否选中
 	    	if(document.getElementById('taskstate').checked == true){
 	    		if(document.getElementById('iscycle').checked == true){
     	    		param={"state":"1","id":GlobalID,"iscycle":"1"};
     	    	}else{
     	    		param={"state":"1","id":GlobalID,"iscycle":"0"};
     	    	}
 	    	}else{
 	    		if(document.getElementById('iscycle').checked == true){
     	    		param={"state":"0","id":GlobalID,"iscycle":"1"};
     	    	}else{
     	    		param={"state":"0","id":GlobalID,"iscycle":"0"};
     	    	}
 	    	}
 	    	//表单验证
 	    	//设置标识符
 	    	var name=false;
 	    	var plain=false;
 	    	var cycle=false;
 	    	var content=false;
 	    	var points="";
 	    	//判断是否填充值,把空格去掉
 	    	if($("#taskname").val().replace(/[ ]/g,"")!=""){
 	    		name=true;
 	    	}else{
 	    		points=points+"请补全[任务名称]!<br/>";
 	    	}
 	    	if($("#taskexplain").val().replace(/[ ]/g,"")!=""){
 	    		plain=true;
 	    	}else{
 	    		points=points+"请补全[任务说明]!<br/>";
 	    	}
 	    	if(document.getElementById('iscycle').checked == true){
 	    		var cyclechar=/^((?:[1-9]?\d|\*)\s*(?:(?:[\/-][1-9]?\d)|(?:,[1-9]?\d)+)?\s*){5}\?$/;
 	    		if($("#taskcycle").val().replace(/[ ]/g,"")==""){
     	    		points=points+"请补全[任务周期]!<br/>";
     	    	}else if(!cyclechar.test($("#taskcycle").val())){
     	    		points=points+"请按正确格式填写[任务周期]!<br/>";
     	    	}else{
     	    		cycle=true;
     	    	}
 	    	}else{
 	    		cycle=true;
 	    	}
 	    	if($("#taskcontent").val().replace(/[ ]/g,"")!=""){
 	    		content=true;
 	    	}else{
 	    		points=points+"请补全[任务内容]!<br/>";
 	    	}
 	    	if(name&&plain&&cycle&&content){
  	    		//提交
 	    		var sfunc = function(){
                	//隐藏表单
                	$('#myModal').modal('hide');
                	//从新加载
	          		selectTask();
     	    	};
	     		ajaxFormSubmit(this,url,param,sfunc);
 	    	}else{
 	    		layer.msg(points,{icon:0});
 	    	}
 	        return false; //不刷新页面  
 	    	});
    	});
    	//加载任务说明
    	function selectexplain(rowid){
    		//点击任务说明时，任务日志隐藏
    		document.getElementById(rowid+"rz").style.display="none";
    		document.getElementById(rowid+"sm").style.display="";
    		//按钮颜色变化
    		document.getElementById(rowid+'plain').style.backgroundColor="dimgray"; 
    		document.getElementById(rowid+'log').style.backgroundColor="#1ab394"; 
    	}
    	//加载grid
    	function selectgrid(rowid){
      	//定义加载grid事件的标识符
     	var url = '<%=path%>tasks/selectTaskLogByTaskId.do';
       	var param={"ID":rowid};
       	//成功后回调函数设置
		var sFunc = function(data){
			var par=data; 
			var source ={
				datatype: "json",
	            localdata: par,
	            datafields:[	
					{name: 'TASKID', type: 'string' },
	            	{ name: 'STARTTIME', type: 'string' },
	             	{ name: 'ENDTIME', type: 'string' },
	            	{ name: 'RETURNINFO', type: 'string' },
	            	{ name: 'EXECSTATE', type: 'string' }
	            ],  
	            id: 'ID',
	        };  
			var dataAdapter = new $.jqx.dataAdapter(source);
	     	// initialize jqxGrid
         	$("#"+rowid+"grid").jqxGrid({
            	width: 728,
             	height:85,
             	source: dataAdapter,
             	columnsresize: true,
             	columnsreorder: true,
             	columns: [
               		{ text: '开始时间', datafield: 'STARTTIME'},
               		{ text: '结束时间', datafield: 'ENDTIME'},
               		{ text: '执行结果', datafield: 'EXECSTATE'}
             	] 
         	});
	       	//点击任务日志时，任务说明隐藏，并加载日志grid
	     	document.getElementById(rowid+"sm").style.display="none";
	     	document.getElementById(rowid+"rz").style.display="";
	     	document.getElementById(rowid+'plain').style.backgroundColor="#1ab394"; 
	     	document.getElementById(rowid+'log').style.backgroundColor="dimgray"; 
	       	};
	       	//获取数据并通过回调函数进行数据加载。
	       	ajaxPost(url,param,sFunc);
    	};
    	/*删除选项*/
    	function deleteData(){
    		layer.open({
    			content: '是否删除？',
    			btn: ['确认', '取消'],
    			shadeClose: false,
    			yes: function(){
    				var url = '<%=path%>tasks/deleteTask.do';
	          		var param={"ID":GlobalID};
	          		//成功后回调函数
	          		var sFunc = function(data){
	          			//从新加载
	          			selectTask();
	          		};
	          		//获取数据并通过回调函数进行数据加载。
	          		ajaxPost(url,param,sFunc);
	          		return false; //不刷新页面  
    				layer.closeAll('dialog');
    			}
    		});
    	};
    	/*改变周期填写状态*/
    	function updatecycle(){
    		if(document.getElementById('iscycle').checked == true){
  				document.getElementById("taskcycle").removeAttribute("readOnly");
  			}else{
  				document.getElementById("taskcycle").setAttribute("readOnly",'true');
  				$("#taskcycle").val("");
  			}
    	}
    	/*手动触发*/
    	function Start(){
    		layer.open({
	    		content: '是否执行任务？',
	    		btn: ['确认', '取消'],
	    		shadeClose: false,
	    		yes: function(){
	    			var url = '<%=path%>tasks/StartOn.do';
		          	var param={"ID":GlobalID,"updateid":"0"};
		          	//成功后回调函数
		          	var sFunc = function(data){
		          		//从新加载
		          		selectTask();
		          	};
		          	//获取数据并通过回调函数进行数据加载。
		          	ajaxPost(url,param,sFunc);
		          	return false; //不刷新页面  
	    			layer.closeAll('dialog');
	    		}
	    	});
	    }
	    /*停止任务状态*/
	    function CloseState(){
	    	//初始化加载数据
	       	var url = '<%=path%>tasks/selectTaskById.do';
	       	var param={"ID":GlobalID,"updateid":"0"};
	       	//成功后回调函数
	       	var sFunc = function(data){
	       		var name=data.TASKNAME;
	       		layer.open({
	        		content: '是否停止'+name+'任务？',
	        		btn: ['确认', '取消'],
	        		shadeClose: false,
	        		yes: function(){
	        			var url = '<%=path%>tasks/UpdateState.do';
	    	          	var param={"ID":GlobalID,"StateId":"0"};
	    	          	//成功后回调函数
	    	          	var sFunc = function(data){
	    	          		//从新加载
	    	          		selectTask();
	    	          	};
	    	          	//获取数据并通过回调函数进行数据加载。
	    	          	ajaxPost(url,param,sFunc);
	    	          	return false; //不刷新页面  
	        			layer.closeAll('dialog');
	        		}
	        	});
	       	};
	       	//获取数据并通过回调函数进行数据加载。
	       	ajaxPost(url,param,sFunc);
	    }
	    /*启动任务状态*/
	    function OpenState(){
				//初始化加载数据
	           	var url = '<%=path%>tasks/selectTaskById.do';
	           	var param={"ID":GlobalID,"updateid":"0"};
	           	//成功后回调函数
	           	var sFunc = function(data){
	           		var name=data.TASKNAME;
	           		layer.open({
	            		content: '是否启动'+name+'任务？',
	            		btn: ['确认', '取消'],
	            		shadeClose: false,
	            		yes: function(){
	            			var url = '<%=path%>tasks/UpdateState.do';
	        	          	var param={"ID":GlobalID,"StateId":"1"};
	        	          	//成功后回调函数
	        	          	var sFunc = function(data){
	        	          		//从新加载
	        	          		selectTask();
	        	          	};
	        	          	//获取数据并通过回调函数进行数据加载。
	        	          	ajaxPost(url,param,sFunc);
	        	          	return false; //不刷新页面  
	            			layer.closeAll('dialog');
	            		}
	            	});
	           	};
	           	//获取数据并通过回调函数进行数据加载。
	           	ajaxPost(url,param,sFunc);
	    }
	    /*编辑页面*/
		function setData(id){
			//如果id等于0则就是新增
			if(id!=0){
				$('#addtask').hide();
				$('#updatetask').show();
				//给修改页面添加数据
				//初始化加载数据
	           	var url = '<%=path%>tasks/selectTaskById.do';
	           	var param={"ID":GlobalID,"updateid":"1"};
	           	//成功后回调函数
	           	var sFunc = function(data){
	           		$('#Id').val(data.ID);
	           		if("0"!=data.FID){
	           			$("#fName").val(data.FID);
	           		}
	       			$("#taskname").val(data.TASKNAME);
	       			$("#taskexplain").val(data.TASKEXPLAIN);
	       			$("#taskcycle").val(data.TASKCYCLE);
	       			$("#taskcontent").val(data.TASKCONTENT);
	       			if(data.TASKSTATE=="0"){
	       				document.getElementById('taskstate').checked =false;
	       			}else{
	       				document.getElementById('taskstate').checked =true;
	       			}
	       			document.getElementById("taskname").setAttribute("readOnly",'true');
	       			document.getElementById("taskcontent").setAttribute("readOnly",'true');
	       			if(data.TASKCYCLE!=""){
	       				document.getElementById('iscycle').checked = true;
	       				document.getElementById("taskcycle").removeAttribute("readOnly");
	       			}else{
	       				document.getElementById('iscycle').checked = false;
	       			document.getElementById("taskcycle").setAttribute("readOnly",'true');
	       			}
	       			$('#myModal').modal();
	           	};
	           	//获取数据并通过回调函数进行数据加载。
	           	ajaxPost(url,param,sFunc);
			}else{
				//把数据清空，打开添加页面
				$('#updatetask').hide();
				$('#addtask').show();
				$('#Id').val("0");
				$("#taskname").val("");
				$("#taskexplain").val("");
				$("#taskcycle").val("");
				$("#taskcontent").val("");
				if(GlobalID!=""){
					//初始化加载数据
	               	var url = '<%=path%>tasks/selectTaskById.do';
	               	var param={"ID":GlobalID,"updateid":"0"};
	               	//成功后回调函数
	               	var sFunc = function(data){
	               		$("#fName").val(data.TASKNAME);
	               		$('#myModal').modal();
	               	};
	               	//获取数据并通过回调函数进行数据加载。
	               	ajaxPost(url,param,sFunc);
				}else{
					$("#fName").val("");
					$('#myModal').modal();
				}
				document.getElementById("taskcycle").setAttribute("readOnly",'true');
				document.getElementById("taskname").removeAttribute("readOnly");
				document.getElementById("taskcontent").removeAttribute("readOnly");
				document.getElementById('iscycle').checked = false;
				document.getElementById('taskstate').checked =true;
			}
		};
	</script>
</body>
</html>