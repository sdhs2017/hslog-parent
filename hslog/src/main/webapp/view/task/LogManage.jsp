
<html>

<head>

    <meta charset="utf-8">
    <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %> 
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<%String path = "../../";%>
    <link rel="shortcut icon" href="favicon.ico"> 
    <link href="<%=path%>hplus/css/bootstrap.min.css?v=3.3.5" rel="stylesheet" media="screen">
	<link href="<%=path%>js/bootstrap/css/bootstrap-datetimepicker.min.css" rel="stylesheet" media="screen">
    <link href="<%=path%>hplus/css/font-awesome.min.css?v=4.4.0" rel="stylesheet">
    <link href="<%=path%>hplus/css/plugins/bootstrap-table/bootstrap-table.min.css" rel="stylesheet">
    <link href="<%=path%>hplus/css/animate.min.css" rel="stylesheet">
    <link href="<%=path%>hplus/css/style.min.css?v=4.0.0" rel="stylesheet">
	<link href="<%=path%>css/easyui.css" rel="stylesheet">
	<link href="<%=path%>js/bootstrap-multiselect/bootstrap-multiselect.css" rel="stylesheet">
	<style type="text/css">
		.fixed-table-header{
			height:36px;
		}
		.textbox .textbox-text{
			font-size:16px;
			text-align:center;
		}
		
		.textbox{
			border-radius:3.5px 3.5px 3.5px 3.5px;
		}
		.tree-title{
			font-size:16px;
		}
		.btn-group{
			padding-top:5px;
		}
		.dropdown-menu{
			font-size:15px;
		}
	</style>
</head>

<body class="gray-bg">
    <div class="wrapper wrapper-content">
		<div class="ibox float-e-margins">
            <div class="ibox-title">
                <h5>日志基础数据查询</h5>
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
                        	<div class="col-sm-1">
	                        	
                        	</div>
                        	<div class="col-sm-11">
	                        	<form class="form-inline" role="form">
		                                <input id="confines" class="easyui-combotree" style="width:150px;height:34px;"></input>
	                                    <div class="input-group date form_date" data-date-format="dd MM yyyy">
		                                    <input class="form-control" size="16" type="text" value="" placeholder="开始时间" readonly>
		                                    <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span>
											<span class="input-group-addon"><span class="glyphicon glyphicon-th"></span></span>
	                                    </div>
	                                    <div class="input-group date form_datetime" data-date-format="dd MM yyyy">
	                                    <input class="form-control" size="16" type="text" value="" placeholder="结束时间" readonly>
	                                    <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span>
										<span class="input-group-addon"><span class="glyphicon glyphicon-th"></span></span>
	                                    </div>
	                                    <select id="state"></select>
				                	<!-- <button type="submit" class="btn btn-primary" style="margin-top:8px">搜索</button> -->
				                	 <a class="btn btn-primary" style="margin-top:5px;" onclick="DataAll()"><i class="glyphicon glyphicon-check"></i>&nbsp;&nbsp;查询</a>
				            	</form>
                        	</div>
                        </div>
                        
                            <table id="OphthalmologyTable" data-toggle="table" data-height="450" data-pagination="true" >
                                <thead>
                                    <tr>
                                    	<th data-field="TASKNAME"  data-align="center" data-valign="middle" >任务名称</th>
                                    	<th data-field="NODENAME"  data-align="center" data-valign="middle" >目录名称</th>
                                    	<th data-field="TASKCONTENT"  data-align="center" data-valign="middle">任务内容</th>
                                    	<th data-field="STARTTIME"  data-align="center" data-valign="middle">开始时间</th>
                                        <th data-field="ENDTIME"  data-align="center" data-valign="middle">结束时间</th>
                                    	<th data-field="EXECSTATE"  data-align="center" data-valign="middle">执行结果</th>
                                    	<th data-field="EXECLOG" data-formatter="MoreLog" data-align="left" data-valign="left">执行日志</th>
                                    	
                                    </tr>
                                </thead>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
   
    <script src="<%=path%>hplus/js/jquery.min.js?v=2.1.4" charset="UTF-8"></script>
    <script src="<%=path%>hplus/js/jquery.form.js"></script>
    <script src="<%=path%>hplus/js/bootstrap.min.js?v=3.3.5"></script>
    <script src="<%=path%>hplus/js/content.min.js?v=1.0.0"></script>
    <script src="<%=path%>js/bootstrap/js/bootstrap-datetimepicker.min.js" charset="UTF-8"></script>
    <script src="<%=path%>js/bootstrap/js/locales/bootstrap-datetimepicker.fr.js" charset="UTF-8"></script>
    <script src="<%=path%>js/bootstrap/js/locales/bootstrap-datetimepicker.zh-CN.js"></script>
    <script src="<%=path%>hplus/js/plugins/bootstrap-table/bootstrap-table.min.js"></script>
    <script src="<%=path%>hplus/js/plugins/bootstrap-table/bootstrap-table-mobile.min.js"></script>
    <script src="<%=path%>hplus/js/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
    <script src="<%=path%>hplus/js/demo/bootstrap-table-demo.min.js"></script>
    <script src="http://tajs.qq.com/stats?sId=9051096" charset="UTF-8"></script>
    <script src="<%=path%>hplus/js/plugins/layer/layer.js"></script>
    <script src="<%=path%>js/jquery.easyui.min.js"></script>
    <script src="<%=path%>js/echarts.min.js"></script>
    <script src="<%=path%>js/bootstrap-multiselect/bootstrap-multiselect.js"></script>
    <script src="<%=path%>util/ajax.js"></script>
    <script src="<%=path%>util/global.js"></script>
    <script> 
    //初始化加载事件
	function getYearOrZipcode(){
    	var url = '<%=path%>tasktree/selectTree4Combo.do';
    	//成功后回调函数
    	var sFunc = function(data){
	   		//获取范围
			$('#confines').combotree("loadData",data);
			$('#confines').combotree('setValue',$('#confines').combotree('tree').tree('getRoots')[0].id);
			DataAll();
    	};
    	//获取数据并通过回调函数进行数据加载。
    	ajaxPost(url,{},sFunc);
    } 
    function MoreLog(a1,row,col){
    	if(row.RETURNINFO!=undefined&&row.RETURNINFO!=""){
    		var r = row.RETURNINFO.split("<br/>");
    		if(r.length>=3){
    			if(r[2].length>21){
    				return r[2].substring(21,36)+"<a onclick='_getReturnInfo(\"\",\""+row.ID+"\",\"\")'>>>更多</a>";
    			}
    		}
    	}
    	return "";
    }
    //加载事件
    function DataAll(){
  	
	  	var url='<%=path%>taskLog/selectTasklogByAll.do';
	
	  	var ZIPCODE=$("#confines").combotree('getValue');
	   	
	  	var MIN=$(".form_date .form-control").val();
	  	var MAX=$(".form_datetime .form-control").val();
	  	
	   	var STATE=$("#state option:selected").val();
	   	var flag=true;
	   	//判断时间
	   	if(""!=MIN&&""!=MAX&&null!=MIN&&null!=MAX){
	   		if(MIN>MAX){
	   			flag=false;
	   		}
	   	}
	   	//参数
	   	var param={"ZIPCODE":ZIPCODE,"MIN":MIN,"STATE":STATE,"MAX":MAX};
	   	if(flag){
	    	//成功后回调函数
	    	var sFunc = function(data){
	    		$("#OphthalmologyTable").bootstrapTable("load", data);
	    	};
	    	//获取数据并通过回调函数进行数据加载。
	    	ajaxPost(url,param,sFunc); 
	   	}else{
	   		layer.msg('开始时间不能大于结束时间！');
	   	}
    } 
    //获取日志信息并弹窗
    function _getReturnInfo(e, arg1, arg2){
    	//开始查询数据
  		var url = '<%=path%>taskLog/selectTaskLogById.do';
      	var param={"ID":arg1.ID==undefined?arg1:arg1.ID};
      	//成功后回调函数设置
      	var sFunc = function(data){
      		//返回这行数据的内容
      		 layer.open({
				  type: 1,
				  title: '返回结果',
				  closeBtn:1,
				  area: ['700px','400px'],
				  //skin: 'layui-layer-nobg', //没有背景色
				  shadeClose: true,
				  content: '\<\div style="margin-left:30px;">'+(data[0].RETURNINFO==undefined?"无返回信息":data[0].RETURNINFO)+'\<\/div>'
				}); 
      	};
      	ajaxPost(url,param,sFunc);
    }
    //鼠标点击及行双击的实现模式不同
    //该方法兼容鼠标双击行事件
    var getReturnInfo = function(e, arg1, arg2) {
    	_getReturnInfo(e, arg1, arg2);
	}
    //加载数据
	$(function(){ 
		DataAll();
		//加载时间控件
		$('.form_date').datetimepicker({
			format: "yyyy-mm-dd",  
			language:  'CN',
	        weekStart: 1,
	        todayBtn:  1,
			autoclose: 1,
			todayHighlight: 1,
			startView: 2,
			minView: 2,
			forceParse: 0
	    });
		$(".form_datetime").datetimepicker({
			format: "yyyy-mm-dd",  
			language:  'CN',
	        weekStart: 1,
	        todayBtn:  1,
			autoclose: 1,
			todayHighlight: 1,
			startView: 2,
			minView: 2,
			forceParse: 0
	    });
		//调整全选时，选项全部显示
		adjustButton('state');
		//调整按钮大小
		//加载执行状态
		$('#state').multiselect('dataprovider',missionstate);
		//初始化数据 
		getYearOrZipcode();
		//添加点击事件
		$('#OphthalmologyTable').on('dbl-click-row.bs.table', getReturnInfo); 
		
	});

	</script>
</body>
</html>