<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<%String path = "../../";%>
<link rel="shortcut icon" href="favicon.ico"> 
<link href="<%=path%>hplus/css/bootstrap.min.css?v=3.3.5" rel="stylesheet">
<link rel="stylesheet" href="<%=path%>/css/ztree/demo.css" type="text/css">
<link rel="stylesheet" href="<%=path%>/css/ztree/metroStyle/metroStyle.css" type="text/css">
<link href="<%=path%>hplus/css/font-awesome.min.css?v=4.4.0" rel="stylesheet">
<link href="<%=path%>hplus/css/plugins/bootstrap-table/bootstrap-table.min.css" rel="stylesheet">
<link href="<%=path%>hplus/css/animate.min.css" rel="stylesheet">
<link href="<%=path%>hplus/css/style.min.css?v=4.0.0" rel="stylesheet">
<link rel="stylesheet" href="<%=path%>js/bootstrap-addtabs/bootstrap-addtabs.css" type="text/css" media="screen" />
<title>Insert title here</title>
<style type="text/css">
	#treeDemo{
		min-height:500px;margin-left:-20px;margin-top:3px;
	}
	div#rMenu {position:absolute; visibility:hidden; top:0; background-color: #555;text-align: left;padding: 2px;}
	div#rMenu ul li{
		margin: 1px 0;
		padding: 0 5px;
		cursor: pointer;
		list-style: none outside none;
		background-color: #DFDFDF;
	}
</style>
</head>
<body>
	<div style="padding-top:10px;margin-bottom:10px;margin-left:20px">
		<form class="form-inline">
            		 <a id="addtaskstree" class="btn btn-primary"  >增加目录</a>
            		 <a id="addtask" class="btn btn-primary" >增加任务</a>
            		 <a id="updeta" class="btn btn-primary"  >编辑</a>
            		 <a id="start" class="btn btn-primary" >执行</a>
            		 <a id="delete" class="btn btn-primary"  >删除</a>
            		 <a id="open" class="btn btn-primary"  >启用</a>
            		 <a id="close" class="btn btn-primary"  >停用</a>
            		 <a id="cancel" class="btn btn-primary" >取消</a>
       	</form>
	</div>
 	<div class="container-fluid">
      <div class="row-fluid">
      	<div style="width: 25px;height:100px;float: left;"></div>
		<div style="margin-top:-3px;margin-left:-5px;height:750px;width: 200px;float: left;">
			<div>
				<ul id="treeDemo" class="ztree" style="height:750px;"></ul>
			</div>
			<div id="rMenu">
				<ul>
					<li id="m_addParent">增加父节点</li>
					<li id="m_add">增加子节点</li>
					<li id="m_del">删除节点</li>
					<li id="m_reset">修改名字</li>
				</ul>
			</div>
		</div>
		<div style="height:750px;">
			<div style="margin-top:3px;height:750px;width:1500px; border: 1px solid #888888;">
		        <div class="col-md-12" style="margin-top:4px;width: 1250px;height:750px;">
		            <div id="tabs">
		                <!-- Nav tabs -->
		                <ul class="nav nav-tabs" role="tablist">
		                    <li role="presentation" class="active"><a href="#home" aria-controls="home" role="tab" data-toggle="tab">执行日志</a></li>                    
		                </ul>
		                <div class="tab-content" style="margin-top: 10px;">
		                    <div role="tabpanel" class="tab-pane active" id="home" >
			                	<iframe class="J_iframe" name="iframe0" width="100%" height="680px" src="../task/LogManage.jsp" frameborder="0" data-id="business/health/nutritional/nutritionalsurface.jsp" seamless></iframe>
			                </div>
		                </div>
		                <!-- Tab panes -->
		                <!--
		                <div class="tab-content" style="margin-top: 30px;">
		                    <div role="tabpanel" class="tab-pane active" id="home" >
		                        <table id="tasklogTable" data-toggle="table" data-height="383" data-pagination="true"  data-mobile-responsive="true">
	                                <thead>
	                                    <tr>
	                                    	<th data-field="TASKNAME"  data-align="center" data-valign="middle" >任务名称</th>
	                                    	<th data-field="NODENAME"  data-align="center" data-valign="middle" >目录名称</th>
	                                    	<th data-field="TASKCONTENT"  data-align="center" data-valign="middle">任务内容</th>
	                                    	<th data-field="STARTTIME"  data-align="center" data-valign="middle">开始时间</th>
	                                        <th data-field="ENDTIME"  data-align="center" data-valign="middle">结束时间</th>
	                                    	<th data-field="EXECSTATE"  data-align="center" data-valign="middle">执行结果</th>
	                                    </tr>
	                                </thead>
	                            </table>
		                    </div>                    
		                </div>
						-->
		            </div>
				</div>
			</div>
	</div>
	</div>  
	<!-- 任务目录编辑页面 -->
	<div class="modal fade" id="TasktreeModal" >
		<div class="modal-dialog" >
			<div class="modal-content">
			<form id="addTasktreeform" class="form-horizontal" >
	 		<div class="modal-header">
         		<h4 class="modal-title" id="addtasktree" align="center">新增目录</h4>
         		<h4 class="modal-title" id="updatetasktree" style="display: none" align="center">修改目录</h4>
         		<input type="hidden" id="Id" name="id"/>
    		 </div>
			<div class="modal-body">
   				<div class="form-group">
      				<label for="firstname" class="col-sm-4 control-label">父节点:</label>
      			<div class="col-sm-5">
        	 		<input type="text" class="form-control" id="fName" readonly="readonly" name="FName" value="" placeholder="任务目录">
      			</div>
      			<div class="col-sm-3"></div>
   				</div> 
   				<div class="form-group">
      				<label for="firstname" class="col-sm-4 control-label">节点名称:</label>
      			<div class="col-sm-5">
        	 		<input type="text" class="form-control" id="tasktreename"  name="NodeName"  placeholder="请输入任务名称">
      			</div>
      			<div class="col-sm-3"></div>
   				</div> 
   				<div class="form-group">
      				<label for="firstname" class="col-sm-4 control-label">备注:</label>
      			<div class="col-sm-5">
        	 		<input type="text" class="form-control" id="tasktreenode"  name="note"  placeholder="请输入备注">
      			</div>
      			<div class="col-sm-3"></div>
   				</div> 
			</div>
   			<div class="modal-footer" >
      			<div class="col-sm-offset-2 col-sm-10">
         			<button id="button" type="submit"  class="btn btn-primary btn-sm">保存</button>&nbsp;&nbsp;&nbsp;&nbsp;
         			<button type="button" class="btn btn-info btn-sm" onclick="$('#TasktreeModal').modal('hide');">返回</button>
      			</div>
   			</div>
			</form>
			</div>
		</div>
	</div>
	<!-- 任务编辑页面 -->
	<div class="modal fade" id="myModal" >
		<div class="modal-dialog">
			<div class="modal-content">
			<form id="addform" class="form-horizontal" >
	 		<div class="modal-header">
         		<h4 class="modal-title" id="addtasks" align="center">新增任务</h4>
         		<h4 class="modal-title" id="updatetasks" style="display: none" align="center">修改任务</h4>
         		<input type="hidden" id="taskid" name="id" />
    		 </div>
			<div class="modal-body">
   				<div class="form-group">
      				<label for="firstname" class="col-sm-4 control-label">任务目录:</label>
      			<div class="col-sm-5">
        	 		<input type="text" class="form-control" id="taskName" readonly="readonly" name="FName" value="" placeholder="任务管理目录">
      			</div>
      			<div class="col-sm-3"></div>
   				</div> 
   				<div class="form-group">
      				<label for="firstname" class="col-sm-4 control-label">任务名称:</label>
      			<div class="col-sm-5">
        	 		<input type="text" class="form-control" id="taskname"  name="taskname" value="${Task.taskname}" placeholder="请输入任务名称">
      			</div>
      			<div class="col-sm-3"></div>
   				</div> 
   				<div class="form-group">
      				<label for="firstname" class="col-sm-4 control-label">任务说明:</label>
      			<div class="col-sm-5">
        	 		<input type="text" class="form-control" id="taskexplain" name="taskexplain" value="${Task.taskexplain}" placeholder="请输入任务说明">
      			</div>
      			<div class="col-sm-3"></div>
   				</div> 
   				<div class="form-group">
      				<label for="firstname" class="col-sm-4 control-label">
      				<input type="checkbox" onclick="updatecycle()" id="iscycle" name="iscycle">
      				任务周期:</label>
      			<div class="col-sm-5">
        	 		<input type="text" class="form-control" id="taskcycle"  name="taskcycle" value="${Task.taskcycle}" placeholder="请输入任务周期(Linux crontab格式)">
      			</div>
      			<div class="col-sm-3"></div>
   				</div> 
   				<div class="form-group">
      				<label for="firstname" class="col-sm-4 control-label">任务内容:</label>
      			<div class="col-sm-5">
        	 		<textarea class="form-control"  id="taskcontent"  name="taskcontent" placeholder="请输入任务内容"></textarea>
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
</div>
<!--  
<script src="<%=path%>hplus/js/jquery.form.js"></script>
<script type="text/javascript" src="<%=path%>/js/ztree/jquery.ztree.exedit.js"></script>
<script src="<%=path%>hplus/js/content.min.js?v=1.0.0"></script>
-->	
<script src="<%=path%>hplus/js/jquery.min.js?v=2.1.4"></script>
<script type="text/javascript" src="<%=path%>/js/ztree/jquery.ztree.core.js"></script>
<script src="<%=path%>hplus/js/bootstrap.min.js?v=3.3.5"></script>
<script src="<%=path%>hplus/js/plugins/bootstrap-table/bootstrap-table.min.js"></script>
<script src="<%=path%>hplus/js/plugins/layer/layer.min.js"></script>
<script src="<%=path%>util/ajax.js"></script>	
<script  src="<%=path%>js/bootstrap-addtabs/bootstrap-addtabs.js" ></script>
<script src="<%=path%>hplus/js/jquery.form.js"></script>
<script type="text/javascript">
    $(function(){
        $('#tabs').addtabs({monitor:'.topbar'});
    })
</script>
<script type="text/javascript">
		
		
		var setting = {
			view: {
				dblClickExpand: false,
				selectedMulti: false,
				nameIsHTML: true
			},
			edit: {
				enable: true,
				showRemoveBtn: false,
				showRenameBtn: false
			},
			data: {
				keep: {
					parent:true,
					leaf:true
				},
				simpleData: {
					enable: true
				}
			},
			callback: {
				beforeDrag: beforeDrag,
				beforeDrop:beforeDrop,
				beforeRemove: beforeRemove,
				beforeRename: beforeRename,
				onRightClick: OnRightClick,
				onClick: zTreeOnClick
			}
		};
		//单击事件
		function zTreeOnClick(event, treeId, treeNode) {
		    //右侧tab加载
		    var type = treeNode.isParent;
		    //如果是父节点
		    if(type){
		    	Addtabs.add({
	               id: treeNode.id,
	               title: treeNode.name,
	               content:"<div id=\"note"+treeNode.id+"\" style=\"margin-top:15px\" class=\"alert alert-success\">备注：这里是目录备注</div><div><table id=\"table"+treeNode.id+"\" data-height=\"300\"></table></div>"
	            });
		    	var columns = [
    	               	{title:'任务名称',field:'TASKNAME',width:100},
    	               	{title:'任务说明',field:'TASKEXPLAIN'},
    	               	{title:'任务内容',field:'TASKCONTENT'},
    	               	{title:'执行状态',field:'EXECSTATEVALUE',width:80},
    	               	{title:'任务状态',field:'TASKSTATEVALUE',width:50},
    	               	{title:'操作',formatter:function (value, row, index){
    	               		return "<a style='color:#00F' onclick='selectdetails(\""+row.TASKTREEID+"\",\""+row.TASKNAME+"\")'>查看详情</a>"
    	               	},width:80}];
		    	$('#table'+treeNode.id).bootstrapTable('destroy').bootstrapTable({
                    columns: columns,
                    data: []
                });
		    	//加载节点备注信息
		    	var _url = "../../tasktree/selectTasktreeByid.do";
		    	var _param = {"ID":treeNode.id};
		    	var _sFunc = function(data){
		    		$("#note"+treeNode.id).html("备注："+data.note);
		    	};
		    	ajaxPost(_url,_param,_sFunc);
		    	//加载任务grid
		    	var url = "../../task/getTaskListByTreeFnode.do";
		    	var param = {"treeid":treeNode.id};
		    	var sFunc = function(data){
		    		$('#table'+treeNode.id).bootstrapTable('load', data);
		    	};
		    	ajaxPost(url,param,sFunc);
		    	
		    	//如果是父节点的话
		    	//接触绑定的onclick事件
            	$("#addtaskstree").unbind('click');
                $("#addtask").unbind('click');
           	    $("#delete").unbind('click');
           	 	$("#update").unbind('click');
       		    //绑定onclick事件
       			$("#addtaskstree").click(function(){
       				addtasktree();
       			}); 
	          	$("#addtask").click(function(){
	          		addtask();
       			});
	          	$("#delete").click(function(){
	          		deletetasktreeortask();
 				});
	          	$("#updeta").click(function(){
	          		settasktreeortask();
 				});
       		    //去掉颜色
      			$('#addtaskstree').attr('disabled',false);
  			 	$('#addtask').attr('disabled',false); 
                $('#updeta').attr('disabled',false);
                $('#delete').attr('disabled',false);
                //不能点击的按钮
                $('#start').attr('disabled',true);
                $('#open').attr('disabled',true);
                $('#close').attr('disabled',true);
                $('#cancel').attr('disabled',true);
		    }else{
		    	//子节点
		    	//去掉颜色
		    	$("#addtaskstree").unbind('click');
                $("#addtask").unbind('click');
      			$('#addtaskstree').attr('disabled',true);
  			 	$('#addtask').attr('disabled',true); 
		    	var url = '<%=path%>tasktree/selectTaskById.do';
                var param={"ID":treeNode.id};
        	    //成功后回调函数
        	    var sFunc = function(data){
	        	    //当执行状态不为执行中时
	        	    if(data[0].EXECSTATE_SNAPSHOT!="1"){
	        	    	//接触绑定的onclick事件
	                	$("#updeta").unbind('click');
		                $("#start").unbind('click');
		           	    $("#delete").unbind('click');
		           	 	$("#cancel").unbind('click');
	           		    //绑定onclick事件
	           			$("#updeta").click(function(){
	           				settasktreeortask();
	           			}); 
	  	          		$("#start").click(function(){
	  	          			StartOn();
	           			});
	   	          		$("#delete").click(function(){
	   	          			deletetasktreeortask();
	     				});
	           		    //去掉颜色
	          			$('#delete').attr('disabled',false);
	      			 	$('#start').attr('disabled',false); 
	                    $('#updeta').attr('disabled',false);
	                    $('#cancel').attr('disabled',true);
	                   	//当任务状态为停用时,切周期不为空
	        			if(data[0].TASKSTATE=="0"){
	        				$("#open").unbind('click');
	         				$("#open").click(function(){
	       						OpenState();
	       					});
	         				$("#close").unbind('click');
	         				$('#open').attr('disabled',false);
	         				$('#close').attr('disabled',true);
	          			}else{
	          				$("#close").unbind('click');
	          				$("#close").click(function(){
	        					CloseState();
	        				});
	 	          			$("#open").unbind('click');
	 	          			$('#close').attr('disabled',false); 
	 	          			$('#open').attr('disabled',true); 
	          			}
	                   	
	  	          	}else{
	          			//接触绑定的onclick事件
		          		$("#start").unbind('click');
		 		    	$("#delete").unbind('click');
		 		    	$("#updeta").unbind('click');
		 		    	$("#open").unbind('click');
		 		    	$("#close").unbind('click');
		 		    	$('#open').attr('disabled',true); 
		              	$('#close').attr('disabled',true); 
		 		    	$('#start').attr('disabled',true); 
		              	$('#updeta').attr('disabled',true); 
		 		    	$('#delete').attr('disabled',true);
		 		    	//绑定取消按钮
		 		    	$("#cancel").click(function(){
       						cancelTask();
       					});
		 		    	$('#cancel').attr('disabled',false); 
		 		    	//将任务信息附着在该任务节点上
		 		    	var selectedNodes = $.fn.zTree.getZTreeObj("treeDemo").getSelectedNodes();
		 		    	selectedNodes[0].taskInfo = data[0];
		 		    	//$('#'+selectedNodes[0].id).taskInfo = data[0];
	          		}
  	          	};
  	          	//获取数据并通过回调函数进行数据加载。
  	        	ajaxPost(url,param,sFunc);
  	          	//加载tab以及加载grid数据
  	        	selectdetails(treeNode.id,treeNode.name);
  	          	<%-- var temp = false;
  	          	if($('#table'+treeNode.id).length==0){
  	          		temp=true;
  	          	}
		    	Addtabs.add({
	               id: treeNode.id,
	               title: treeNode.name,
	               content:"<div class=\"row show-grid\">"
	               			+"<div class=\"col-md-2\" style=\"font-weight:bold\">任务说明</div><div class=\"col-md-2\" id=\"taskexplain"+treeNode.id+"\">说明</div>"
	               			+"<div class=\"col-md-2\" style=\"font-weight:bold\">任务状态</div><div class=\"col-md-2\" id=\"taskstate"+treeNode.id+"\">开启</div>"
	               			+"</div>"
	               			+"<div class=\"row show-grid\">"
	               			+"<div class=\"col-md-2\" style=\"font-weight:bold\">任务周期</div><div class=\"col-md-2\" id=\"taskcycle"+treeNode.id+"\">*/10 * * * * ?</div>"
	               			+"<div class=\"col-md-2\" style=\"font-weight:bold\">执行状态</div><div class=\"col-md-2\" id=\"execstate_snapshot"+treeNode.id+"\">执行成功</div>"
	               			+"</div>"
	               			+"<div class=\"row show-grid\">"
	               			+"<div class=\"col-md-2\" style=\"font-weight:bold\">任务内容</div><div class=\"col-md-4\" id=\"taskcontent"+treeNode.id+"\">linux命令</div>"
	               			+"</div>"
	               			+"<div><table id=\"table"+treeNode.id+"\" data-height=\"210\"></table></div>"
	            });
		    	var columns = [
    	               	{title:'任务名称',field:'TASKNAME',width:50},
    	               	{title:'任务内容',field:'TASKCONTENT',width:100},
    	               	{title:'开始时间',field:'STARTTIME',width:80},
    	               	{title:'结束时间',field:'ENDTIME',width:80},
    	               	{title:'执行状态',field:'EXECSTATEVALUE',width:30}];
		    	$('#table'+treeNode.id).bootstrapTable('destroy').bootstrapTable({
                    columns: columns,
                    data: []
                });
		    	//加载节点备注信息
		    	var _url = "../../task/getTaskByTreeid.do";
		    	var _param = {"treeid":treeNode.id};
		    	var _sFunc = function(data){
		    		$("#taskexplain"+treeNode.id).html(data.TASKEXPLAIN);
		    		$("#taskstate"+treeNode.id).html(data.TASKSTATEVALUE);
		    		$("#taskcycle"+treeNode.id).html(data.TASKCYCLE==undefined?"&nbsp":data.TASKCYCLE+"&nbsp");
		    		$("#execstate_snapshot"+treeNode.id).html(data.EXECSTATEVALUE==undefined?"&nbsp":data.EXECSTATEVALUE+"&nbsp");
		    		$("#taskcontent"+treeNode.id).html(data.TASKCONTENT);
		    	};
		    	ajaxPost(_url,_param,_sFunc);
		    	//加载任务grid
		    	var url = "../../taskLog/selectTaskLogByTreeId.do";
		    	var param = {"treeid":treeNode.id};
		    	var sFunc = function(data){
		    		$('#table'+treeNode.id).bootstrapTable('load', data);
		    	};
		    	ajaxPost(url,param,sFunc);
		    	if(temp){
		    		//添加点击事件
					$('#table'+treeNode.id).on('click-row.bs.table', function (e, arg1, arg2) {
					  //开始查询数据
			      		var url = '<%=path%>taskLog/selectTaskLogByTaskId.do';
			          	var param={"ID":arg1.TASKID};
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
								  content: '\<\div style="margin-left:30px;">'+data[0].RETURNINFO+'\<\/div>'
								}); 
			          	};
			          	ajaxPost(url,param,sFunc);
					});
		    	} --%>
		    	
		    }
		    
		};

		//右击事件
		function OnRightClick(event, treeId, treeNode) {
			if (!treeNode && event.target.tagName.toLowerCase() != "button" && $(event.target).parents("a").length == 0) {
				zTree.cancelSelectedNode();
				showRMenu("root",event.clientX , event.clientY);
			} else if (treeNode && !treeNode.noR) {
				zTree.selectNode(treeNode);
				showRMenu("node",event.clientX , event.clientY);
				if(treeNode.isParent){
					$("#m_addParent").show();
					$("#m_add").show();
				}else{
					$("#m_addParent").hide();
					$("#m_add").hide();
				} 
			}
		}
		//显示菜单
		function showRMenu(type, x, y) {
			$("#rMenu ul").show();
			if (type=="root") {
				$("#m_addParent").show();
				$("#m_add").show();
				$("#m_del").show();
				$("#m_reset").show();
			} 
			rMenu.css({"top":y+"px", "left":(x-14)+"px", "visibility":"visible"});
		
			$("body").bind("mousedown", onBodyMouseDown);
		}
		//隐藏菜单
		function hideRMenu() {
			if (rMenu) rMenu.css({"visibility": "hidden"});
			$("body").unbind("mousedown", onBodyMouseDown);
		}
		//在body隐藏菜单
		function onBodyMouseDown(event){
			if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length>0)) {
				rMenu.css({"visibility" : "hidden"});
			}
		}
		//允许拖拽
		function beforeDrag(treeId, treeNode) {
			return true;
		}
		//拖拽前
		function beforeDrop(treeId, treeNodes, targetNode, moveType, isCopy) {
			if(confirm("是否挪到 '" +targetNode.name + "'这里?")){
				//不允许父节点以下的节点拖拽
				if(targetNode.pId==treeNodes[0].pId){
					alert("不允许在同父节点之下拖拽");
					return false;
				}else{
					//是否是父节点 targetNode是要拖拽到的地方的对象   treeNodes[0]拖拽的对象
					if(targetNode.isParent){
						//如果拖拽到的地方是父节点,pid就是父节点的id
						dragTasktree(targetNode.id,treeNodes[0].id);
					}else{
						//如果拖拽到的地方是子节点,pid就是子节点的pid
						dragTasktree(targetNode.pId,treeNodes[0].id);
					}
					return true;
				}
				
			}else{
				return false;
			}
			
		}
		//删除前
		function beforeRemove(treeId, treeNode) {
			if(confirm("确定删除节点'" + treeNode.name + "' 吗?")){
				deleteTasktree(treeNode.id,treeNode.isParent);
				return true;
			}else{
				return false;
			}
		}
		//修改后
		function beforeRename(treeId, treeNode, newName) {
			if (newName.length == 0) {
				alert("请选择一个节点");
				setTimeout(function(){zTree.editName(treeNode);}, 10);
				return false;
			}
			//修改名字
			updateTasktree(newName,treeNode.id);
			return true;
		}
		//命名
		var newCount = 1;
		//增加
		function add(e) {
			hideRMenu();
			var zTree = $.fn.zTree.getZTreeObj("treeDemo"),
			isParent = e.data.isParent,
			nodes = zTree.getSelectedNodes(),
			treeNode = nodes[0];
			var newname="newnode" + (newCount++);
			if (treeNode) {
				var func = function(data){
					treeNode = zTree.addNodes(treeNode, {id:data.id, pId:data.pId, isParent:data.nodeType=="0"?true:false, name:data.nodeName});
					if (treeNode) {
						zTree.editName(treeNode[0],true);
					} else {
						alert("子节点被锁定无法添加父节点.");
					}
				};
				//当添加在属性菜单中
				//当isParent为true时就是添加父节点反之就是添加子节点
				insertTasktree(newname,treeNode.id,isParent,func);
				
			} else {
				var func = function(data){
					treeNode = zTree.addNodes(null, {id:data.id, pId:data.pId==null?"0":"0", isParent:data.nodeType=="0"?true:false, name:data.nodeName});
					if (treeNode) {
						zTree.editName(treeNode[0],true);
					} else {
						alert("子节点被锁定无法添加父节点.");
					}
				};
				//当点击在树形菜单外围
				insertTasktree(newname,null,isParent,func);
			}
		};
		//修改
		function edit() {
			hideRMenu();
			nodes = zTree.getSelectedNodes(),
			treeNode = nodes[0];
			if (nodes.length == 0) {
				alert("请选择一个节点");
				return;
			}
			zTree.editName(treeNode,true);
		};
		//删除
		function remove(e) {
			hideRMenu();
			nodes = zTree.getSelectedNodes(),
			treeNode = nodes[0];
			if (nodes.length == 0) {
				alert("请选择一个节点");
				return;
			}
			zTree.removeNode(treeNode,true);
		};
		//全局变量
		var zTree, rMenu;
		//加载数据
		function getZtreeData(){
			//var url = '<%=path%>tasktree/selectTasktree.do';
			var url = '<%=path%>tasktree/selectTasktreeAndState.do';
	    	//成功后回调函数
	    	var sFunc = function(data){
	    		$.fn.zTree.init($("#treeDemo"), setting, data);
	    		zTree = $.fn.zTree.getZTreeObj("treeDemo");
	    		//接触绑定的onclick事件
          		$("#start").unbind('click');
 		    	$("#delete").unbind('click');
 		    	$("#updeta").unbind('click');
 		    	$("#open").unbind('click');
 		    	$("#close").unbind('click');
 		    	$("#addtaskstree").unbind('click');
                $("#addtask").unbind('click');
              	//绑定onclick事件
       			$("#addtaskstree").click(function(){
       				addtasktree();
       			}); 
	          	$("#addtask").click(function(){
	          		addtask();
       			});
      			$('#addtaskstree').attr('disabled',false);
  			 	$('#addtask').attr('disabled',false); 
 		    	$('#open').attr('disabled',true); 
              	$('#close').attr('disabled',true); 
 		    	$('#start').attr('disabled',true); 
              	$('#updeta').attr('disabled',true); 
 		    	$('#delete').attr('disabled',true);
	    	};
	    	//获取数据并通过回调函数进行数据加载。
	    	ajaxPost(url,{},sFunc);
		}
		//增加数据
	    function insertTasktree(name,pid,type,fun){
	    	var url = '<%=path%>tasktree/insertTasktree.do';
	    	//获取参数
	    	var NODENAME=name;
	    	var FNODE=pid;
	    	var NODETYPE="";
	    	if(type){
	    		NODETYPE="0";
	    	}else{
	    		NODETYPE="1";
	    	}
	    	var param={"NODENAME":NODENAME,"FNODE":FNODE,"NODETYPE":NODETYPE};
	    	//成功后回调函数
	    	var sFunc = fun;
	    	
	    	//获取数据并通过回调函数进行数据加载。
	    	ajaxPost(url,param,sFunc);
	    }
	  	//修改数据
	    function updateTasktree(name,id){
	    	var url = '<%=path%>tasktree/updateTasktree.do';
	    	//获取参数
	    	var NODENAME=name;
	    	var UUID=id;
	    	var param={"NODENAME":NODENAME,"UUID":UUID};
	    	//成功后回调函数
	    	var sFunc = function(data){
	    		return true;
	    	};
	    	//获取数据并通过回调函数进行数据加载。
	    	ajaxPost(url,param,sFunc);
	    }
	  	//拖拽数据
	    function dragTasktree(pid,id){
	    	var url = '<%=path%>tasktree/dragTasktree.do';
	    	//获取参数
	    	var FNODE=pid;
	    	var UUID=id;
	    	var param={"FNODE":FNODE,"UUID":UUID};
	    	//成功后回调函数
	    	var sFunc = function(data){
	    		return true;
	    	};
	    	//获取数据并通过回调函数进行数据加载。
	    	ajaxPost(url,param,sFunc);
	    }
	  	//删除数据
	    function deleteTasktree(id,parent){
	    	var url = '<%=path%>tasktree/deleteTasktree.do';
	    	//获取参数
	    	var UUID=id;
	    	var PARENT=parent;
	    	var param={"UUID":UUID,"PARENT":PARENT};
	    	//成功后回调函数
	    	var sFunc = function(data){
	    		return true;
	    	};
	    	//获取数据并通过回调函数进行数据加载。
	    	ajaxPost(url,param,sFunc);
	    }
		$(document).ready(function(){
			//加载菜单
			getZtreeData();
			rMenu = $("#rMenu");
			$("#m_addParent").bind("click", {isParent:true}, add);
			$("#m_add").bind("click", {isParent:false}, add);
			$("#m_reset").bind("click", edit);
			$("#m_del").bind("click", remove);
			//编辑任务目录数据提交事件初始化
 	    	$("#addTasktreeform").submit(function(){  
	 	    	var url="<%=path%>tasktree/updateoraddTasktree.do";
	 	    	//设置参数
	 	    	var param="";
	 	    	//获取当前光标点击的节点数组
				var selectedNodes = $.fn.zTree.getZTreeObj("treeDemo").getSelectedNodes();
				//如果数组长度不为0则就是有节点选中状态
				if(selectedNodes.length>0){
					param={"FNODE":selectedNodes[0].id};
				}else{
					param={"FNODE":""};
				}
	 	    	//表单验证
	 	    	//设置标识符
	 	    	var name=false;
	 	    	var points="";
	 	    	//判断是否填充值,把空格去掉
	 	    	if($("#tasktreename").val().replace(/[ ]/g,"")!=""){
	 	    		name=true;
	 	    	}else{
	 	    		points=points+"请补全[节点名称]!";
	 	    	}
	 	    	//如果name为true
	 	    	if(name){
	  	    		//提交
	 	    		var sfunc = function(){
	                	//隐藏表单
	                	$('#TasktreeModal').modal('hide');
	                	//加载树形菜单
	        			getZtreeData();
	     	    	};
		     		ajaxFormSubmit(this,url,param,sfunc);
	 	    	}else{
	 	    		layer.msg(points,{icon:0});
	 	    	}
	 	        return false; //不刷新页面  
	 	    });
 	   	 	//任务节点数据提交事件初始化
 	    	$("#addform").submit(function(){ 
		    	//获取当前光标点击的节点数组
				var selectedNodes = $.fn.zTree.getZTreeObj("treeDemo").getSelectedNodes();
	 	    	var url="<%=path%>tasktree/updateoraddTask.do";
	 	    	var param="";
	 	    	//查询复选框是否选中
	 	    	if(document.getElementById('taskstate').checked == true){
	 	    		if(document.getElementById('iscycle').checked == true){
	     	    		//如果数组长度不为0则就是有节点选中状态
	    				if(selectedNodes.length>0){
	     	    			param={"state":"1","tid":selectedNodes[0].id,"iscycle":"1"};
	    				}else{
	    					param={"state":"1","tid":"","iscycle":"1"};
	    				}
	     	    	}else{
	     	    		//如果数组长度不为0则就是有节点选中状态
	     	    		if(selectedNodes.length>0){
	     	    			param={"state":"1","tid":selectedNodes[0].id,"iscycle":"0"};
	    				}else{
	    					param={"state":"1","tid":"","iscycle":"0"};
	    				}
	     	    	}
	 	    	}else{
	 	    		if(document.getElementById('iscycle').checked == true){
	 	    			//如果数组长度不为0则就是有节点选中状态
	     	    		if(selectedNodes.length>0){
	     	    			param={"state":"0","tid":selectedNodes[0].id,"iscycle":"1"};
	    				}else{
	    					param={"state":"0","tid":"","iscycle":"1"};
	    				}
	     	    	}else{
	     	    		//如果数组长度不为0则就是有节点选中状态
	     	    		if(selectedNodes.length>0){
	     	    			param={"state":"0","tid":selectedNodes[0].id,"iscycle":"0"};
	    				}else{
	    					param={"state":"0","tid":"","iscycle":"0"};
	    				}
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
	                	//加载树形菜单
	        			getZtreeData();
	     	    	};
		     		ajaxFormSubmit(this,url,param,sfunc);
	 	    	}else{
	 	    		layer.msg(points,{icon:0});
	 	    	}
	 	        return false; //不刷新页面  
 	    	});
 	   	 	//加载执行日志
 	    	tasklogAll();
 	   	 	//加载执行日志的行点击事件
 	   	 	addtrclick();
		});
		//新增任务目录
		function addtasktree(){
			//获取当前光标点击的节点数组
			var selectedNodes = $.fn.zTree.getZTreeObj("treeDemo").getSelectedNodes();
			//如果数组长度不为0则就是有节点选中状态
			if(selectedNodes.length>0){
				$("#fName").val(selectedNodes[0].name);
			}else{
				$("#fName").val("");
			}
			//把数据清空，打开添加页面
			$('#updatetasktree').hide();
			$('#addtasktree').show();
			$('#Id').val("0");
			$("#tasktreename").val("");
			$("#tasktreenode").val("");
			$('#TasktreeModal').modal();
		}
		/*任务添加页面*/
		function addtask(){
			//把数据清空，打开添加页面
			$('#updatetasks').hide();
			$('#addtasks').show();
			//获取当前光标点击的节点数组
			var selectedNodes = $.fn.zTree.getZTreeObj("treeDemo").getSelectedNodes();
			//如果数组长度不为0则就是有节点选中状态
			if(selectedNodes.length>0){
				$("#taskName").val(selectedNodes[0].name);
			}else{
				$("#taskName").val("");
			}
			$('#taskid').val("0");
			$("#taskname").val("");
			$("#taskexplain").val("");
			$("#taskcycle").val("");
			$("#taskcontent").val("");
			$('#myModal').modal();
			document.getElementById("taskcycle").setAttribute("readOnly",'true');
			document.getElementById("taskname").removeAttribute("readOnly");
			document.getElementById("taskcontent").removeAttribute("readOnly");
			$('#iscycle').checked = false;
			$('#taskstate').checked =true;
		};
		//编辑页面
		function settasktreeortask(){
			//获取当前光标点击的节点数组
			var selectedNodes = $.fn.zTree.getZTreeObj("treeDemo").getSelectedNodes();
			//如果为父节点则就是目录，不为父节点就是任务，selectedNodes[0].isParent为true则是目录
			if(selectedNodes[0].isParent){
				//修改任务目录编辑from的修改状态
				$('#addtasktree').hide();
				$('#updatetasktree').show();
				//给修改页面添加数据
				//初始化加载数据
	           	var url = '<%=path%>tasktree/selectTasktreeByid.do';
				//传参数为任务目录的id
	           	var param={"ID":selectedNodes[0].id};
	           	//成功后回调函数
	           	var sFunc = function(data){
	           		//from表单赋值
	           		$('#Id').val(data.id);
					$("#tasktreename").val(data.nodeName);
					$("#tasktreenode").val(data.note);
	       			$('#TasktreeModal').modal();
	           	};
	           	//获取数据并通过回调函数进行数据加载。
	           	ajaxPost(url,param,sFunc);
			}else{
				$('#addtasks').hide();
				$('#updatetasks').show();
				//给修改页面添加数据
				//初始化加载数据
	           	var url = '<%=path%>tasktree/selectTaskById.do';
	           	var param={"ID":selectedNodes[0].id};
	           	//成功后回调函数
	           	var sFunc = function(data){
	           		$('#taskid').val(data[0].ID);
	           		if("0"!=data[0].TASKTREEID){
	           			$("#fName").val(data[0].TASKTREEID);
	           		}
	       			$("#taskname").val(data[0].TASKNAME);
	       			$("#taskexplain").val(data[0].TASKEXPLAIN);
	       			$("#taskcycle").val(data[0].TASKCYCLE);
	       			$("#taskcontent").val(data[0].TASKCONTENT);
	       			if(data[0].TASKSTATE=="0"){
	       				document.getElementById('taskstate').checked =false;
	       			}else{
	       				document.getElementById('taskstate').checked =true;
	       			}
	       			document.getElementById("taskcontent").setAttribute("readOnly",'true');
	       			if(data[0].TASKCYCLE!=""){
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
			}
		}
		//删除节点
		function deletetasktreeortask(){
			layer.open({
    			content: '是否删除？',
    			btn: ['确认', '取消'],
    			shadeClose: false,
    			yes: function(){
    				//获取当前光标点击的节点数组
    				var selectedNodes = $.fn.zTree.getZTreeObj("treeDemo").getSelectedNodes();
   					var url = '<%=path%>tasktree/deleteTasktree.do';
   			    	//获取参数
   			    	var UUID=selectedNodes[0].id;
   			    	var PARENT=selectedNodes[0].isParent;
   			    	var param={"UUID":UUID,"PARENT":PARENT};
   			    	//成功后回调函数
   			    	var sFunc = function(data){
   			    		//加载树形菜单
   	        			getZtreeData();
   			    	};
   			    	//获取数据并通过回调函数进行数据加载。
   			    	ajaxPost(url,param,sFunc);
    				
	          		return false; //不刷新页面  
    				layer.closeAll('dialog');
    			}
    		});
			
		}
		/*改变周期填写状态*/
    	function updatecycle(){
    		if(document.getElementById('iscycle').checked == true){
  				document.getElementById("taskcycle").removeAttribute("readOnly");
  			}else{
  				document.getElementById("taskcycle").setAttribute("readOnly",'true');
  				$("#taskcycle").val("");
  			}
    	}
    	//执行一次任务
    	function StartOn(){
    		//获取当前光标点击的节点数组
			var selectedNodes = $.fn.zTree.getZTreeObj("treeDemo").getSelectedNodes();
    		layer.open({
	    		content: '是否执行任务？',
	    		btn: ['确认', '取消'],
	    		shadeClose: false,
	    		yes: function(){
	    			var url = '<%=path%>tasktree/StartOn.do';
		          	var param={"ID":selectedNodes[0].id};
		          	//成功后回调函数
		          	var sFunc = function(data){
		          		//加载树形菜单
   	        			getZtreeData();
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
	    	//获取当前光标点击的节点数组
			var selectedNodes = $.fn.zTree.getZTreeObj("treeDemo").getSelectedNodes();
       		var name=selectedNodes[0].name;
       		layer.open({
        		content: '是否停止'+name+'任务？',
        		btn: ['确认', '取消'],
        		shadeClose: false,
        		yes: function(){
        			var url = '<%=path%>tasktree/UpdateState.do';
    	          	var param={"ID":selectedNodes[0].id,"StateId":"0"};
    	          	//成功后回调函数
    	          	var sFunc = function(data){
    	          		//加载树形菜单
	        			getZtreeData();
    	          	};
    	          	//获取数据并通过回调函数进行数据加载。
    	          	ajaxPost(url,param,sFunc);
    	          	return false; //不刷新页面  
        			layer.closeAll('dialog');
        		}
        	});
	    }
	    /*启动任务状态*/
	    function OpenState(){
	    	//获取当前光标点击的节点数组
			var selectedNodes = $.fn.zTree.getZTreeObj("treeDemo").getSelectedNodes();
      		var name=selectedNodes[0].name;
      		layer.open({
	       		content: '是否启动'+name+'任务？',
	       		btn: ['确认', '取消'],
	       		shadeClose: false,
	       		yes: function(){
	       			var url = '<%=path%>tasktree/UpdateState.do';
	   	          	var param={"ID":selectedNodes[0].id,"StateId":"1"};
	   	          	//成功后回调函数
	   	          	var sFunc = function(data){
	   	          	//加载树形菜单
	        			getZtreeData();
	   	          	};
	   	          	//获取数据并通过回调函数进行数据加载。
	   	          	ajaxPost(url,param,sFunc);
	   	          	return false; //不刷新页面  
	       			layer.closeAll('dialog');
	       		}
       		});
	    }
	    //任务取消功能
	    function cancelTask(){
	    	//获取当前光标点击的节点数组
			var selectedNodes = $.fn.zTree.getZTreeObj("treeDemo").getSelectedNodes();
      		var taskcontent=selectedNodes[0].taskInfo.TASKCONTENT;//获取任务执行语句
      		var re = /\/.*.jar/;
      		var r = taskcontent.match(re); 
      		if(r.length>0){
      			//获取jar包名称，发送给后台
      			var jarname = r[0].substring(r[0].lastIndexOf("/")+1,r[0].lastIndexOf(".jar")+4);
      			var taskid = selectedNodes[0].taskInfo.ID;//任务id
      			layer.open({
    	       		content: '是否取消'+jarname+'任务？',
    	       		btn: ['确认', '取消'],
    	       		shadeClose: false,
    	       		yes: function(){
    	       			var url = '<%=path%>tasktree/cancelTask.do';
    	   	          	var param={"TaskJarName":jarname,"TaskId":taskid};
    	   	          	//成功后回调函数
    	   	          	var sFunc = function(data){
    	   	          		//加载树形菜单
    	        			getZtreeData();
    	   	          	};
    	   	          	//获取数据并通过回调函数进行数据加载。
    	   	          	ajaxPost(url,param,sFunc);
    	   	         	layer.closeAll('dialog');
    	   	          	return false; //不刷新页面  
    	       			
    	       		}
           		});
      		}else{
      			layer.alert("该任务无法取消！");
      		}
      		
	    }
	    //加载执行日志
	    function tasklogAll(){
		  	var url='<%=path%>taskLog/selectTasklogByAll.do';
		   	//参数
		   	var param={"ZIPCODE":"","MIN":"","STATE":"","MAX":""};
	    	//成功后回调函数
	    	 var sFunc = function(data){
	    		$("#tasklogTable").bootstrapTable("load", data);
	    	}
	    	//获取数据并通过回调函数进行数据加载。
	    	ajaxPost(url,param,sFunc); 
	    }
	    //添加行点击事件
	    function addtrclick(){
			//添加点击事件
			$('#tasklogTable').on('dbl-click-row.bs.table', function (e, arg1, arg2) {
			  	//开始查询数据
	      		var url = '<%=path%>taskLog/selectTaskLogById.do';
	          	var param={"ID":arg1.ID};
	          	//成功后回调函数设置
	          	var sFunc = function(data){
	          		//返回这行数据的内容
	          		layer.open({
						 type: 1,
						 title: '返回结果',
						 closeBtn:1,
						 area: ['700px','400px'],
						 //skin: 'layui-layer-demo', //没有背景色
						 shadeClose: true,
						 content:'\<\div style="margin-left:30px;">'+(data[0].RETURNINFO==undefined?"无返回信息":data[0].RETURNINFO)+'\<\/div>'
					}); 
	          	};
	          	ajaxPost(url,param,sFunc);
			});
	    }
	    //查看详情
	    function selectdetails(Id,name){
          	var temp = false;
 	          	if($('#table'+Id).length==0){
 	          		temp=true;
 	          	}
	    	Addtabs.add({
               id: Id,
               title: name,
               content:"<div class=\"row show-grid\">"
               			+"<div class=\"col-md-2\" style=\"font-weight:bold\">任务说明</div><div class=\"col-md-4\" id=\"taskexplain"+Id+"\">说明</div>"
               			+"<div class=\"col-md-2\" style=\"font-weight:bold\">任务状态</div><div class=\"col-md-4\" id=\"taskstate"+Id+"\">开启</div>"
               			+"</div>"
               			+"<div class=\"row show-grid\">"
               			+"<div class=\"col-md-2\" style=\"font-weight:bold\">任务周期</div><div class=\"col-md-4\" id=\"taskcycle"+Id+"\">*/10 * * * * ?</div>"
               			+"<div class=\"col-md-2\" style=\"font-weight:bold\">执行状态</div><div class=\"col-md-4\" id=\"execstate_snapshot"+Id+"\">执行成功</div>"
               			+"</div>"
               			+"<div class=\"row show-grid\">"
               			+"<div class=\"col-md-2\" style=\"font-weight:bold\">任务内容</div><div class=\"col-md-10\" id=\"taskcontent"+Id+"\">linux命令</div>"
               			+"</div>"
               			+"<div><table id=\"table"+Id+"\" data-height=\"210\"></table></div>"
            });
	    	var columns = [
   	               	{title:'任务名称',field:'TASKNAME',width:50},
   	               	{title:'任务内容',field:'TASKCONTENT',width:100},
   	               	{title:'开始时间',field:'STARTTIME',width:80},
   	               	{title:'结束时间',field:'ENDTIME',width:80},
   	               	{title:'执行状态',field:'EXECSTATEVALUE',width:30}];
	    	$('#table'+Id).bootstrapTable('destroy').bootstrapTable({
                   columns: columns,
                   data: []
               });
	    	//加载节点备注信息
	    	var _url = "../../task/getTaskByTreeid.do";
	    	var _param = {"treeid":Id};
	    	var _sFunc = function(data){
	    		$("#taskexplain"+Id).html(data.TASKEXPLAIN);
	    		$("#taskstate"+Id).html(data.TASKSTATEVALUE);
	    		$("#taskcycle"+Id).html(data.TASKCYCLE==undefined?"&nbsp":data.TASKCYCLE+"&nbsp");
	    		$("#execstate_snapshot"+Id).html(data.EXECSTATEVALUE==undefined?"&nbsp":data.EXECSTATEVALUE+"&nbsp");
	    		$("#taskcontent"+Id).html(data.TASKCONTENT);
	    	};
	    	ajaxPost(_url,_param,_sFunc);
	    	//加载任务grid
	    	var url = "../../taskLog/selectTaskLogByTreeId.do";
	    	var param = {"treeid":Id};
	    	var sFunc = function(data){
	    		$('#table'+Id).bootstrapTable('load', data);
	    	};
	    	ajaxPost(url,param,sFunc);
	    	if(temp){
	    		//添加点击事件
				$('#table'+Id).on('dbl-click-row.bs.table', function (e, arg1, arg2) {
				  //开始查询数据
		      		var url = '<%=path%>taskLog/selectTaskLogByTaskId.do';
		          	var param={"ID":arg1.TASKID};
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
							  content:'\<\div style="margin-left:30px;">'+(data[0].RETURNINFO==undefined?"无返回信息":data[0].RETURNINFO)+'\<\/div>'
							}); 
		          	};
		          	ajaxPost(url,param,sFunc);
				});
	    	}
	    }
</script>
</body>

</html>