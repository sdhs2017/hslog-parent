<html>
<head>
	<meta charset="utf-8">
    <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %> 
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<%String path = "../../";%>
    <link rel="shortcut icon" href="favicon.ico"> 
    <link href="<%=path%>hplus/css/bootstrap.min.css?v=3.3.5" rel="stylesheet">
    <link href="<%=path%>hplus/css/font-awesome.min.css?v=4.4.0" rel="stylesheet">
    <!-- Morris -->
    <link href="<%=path%>hplus/css/plugins/morris/morris-0.4.3.min.css" rel="stylesheet">
    <!-- Gritter -->
    <link href="<%=path%>hplus/js/plugins/gritter/jquery.gritter.css" rel="stylesheet">
    <link href="<%=path%>hplus/css/animate.min.css" rel="stylesheet">
    <link href="<%=path%>hplus/css/style.min.css?v=4.0.0" rel="stylesheet">
    <base target="_blank">
</head>
<body class="gray-bg">
	<div class="row">
	    <div class="col-sm-6">
	        <div class="ibox float-e-margins">
	            <div class="ibox-content">
	                <div class="row">
	                    <div class="col-sm-12" style="height:500px">
	                        <div class="flot-chart" >
	                            <div id="UserReg" style="height:500px"></div>
	                        </div>
	                    </div>
	                </div>
	            </div>
	        </div>
	    </div>
	    <div class="col-sm-6">
	        <div class="ibox float-e-margins">
	            <div class="ibox-content">
	                <div class="row">
	                    <div class="col-sm-12" style="height:500px">
	                        <div class="flot-chart" >
	                            <div id="UserSex" style="height:500px"></div>
	                        </div>
	                    </div>
	                </div>
	            </div>
	        </div>
	    </div>
	</div>
	<div class="row">
		<div class="col-sm-12">
			<div class="ibox float-e-margins">
		    	<div class="ibox-content" style="height:500px">
		       		<div id="UserMap" style="width:1000px;height:500px;margin:auto auto"></div>
		        </div>
		 	</div>
		</div>
    </div> 
	<script src="<%=path%>hplus/js/jquery.min.js?v=2.1.4"></script>
    <script src="<%=path%>js/echarts.min.js"></script>
    <script src="<%=path%>js/china.js"></script>
    <script src="<%=path%>hplus/js/plugins/layer/layer.js"></script>
    <script src="<%=path%>hplus/js/bootstrap.min.js?v=3.3.5"></script>
    <script src="<%=path%>util/ajax.js"></script>
    <script src="<%=path%>util/global.js"></script>
    <script>
	//初始化加载
    function init(){
    	getUserReg();
    	getUserSex();
    	getUserMap();
    }
  	//注册用户分布图
    function getUserMap(){
    	var url='<%=path%>UserAttr/getUserMap.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
			loadEchart(data,'UserMap');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
  	//注册用户男女比例
    function getUserSex(){
    	var url='<%=path%>UserAttr/getUserSex.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
			loadEchart(data,'UserSex');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
	//用户注册数曲线图
    function getUserReg(){
    	var url='<%=path%>UserAttr/getUserReg.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
			loadEchart(data,'UserReg');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
    
	//Echart加载事件
   	function loadEchart(option,id){
   		var visitEchart = echarts.init(document.getElementById(id));
   		visitEchart.setOption(option);
   	}
    $(function() { 
    	init();
    });
    </script>
</body>