<html>
<head>
	<meta charset="utf-8">
    <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %> 
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<%
	String path = request.getContextPath(); 
	// 获得本项目的地址(例如: http://localhost:8080/MyApp/)赋值给basePath变量 
	String basePath = request.getScheme()+"://"+request.getServerName()
	+":"+request.getServerPort()+path+"/"; 
	// 将 "项目路径basePath" 放入pageContext中，待以后用EL表达式读出。 
	pageContext.setAttribute("basePath",basePath); 
	%>
    <link rel="shortcut icon" href="favicon.ico"> 
    <link href="<%=basePath%>hplus/css/bootstrap.min.css?v=3.3.5" rel="stylesheet">
    <link href="<%=basePath%>hplus/css/font-awesome.min.css?v=4.4.0" rel="stylesheet">
    <!-- Morris -->
    <link href="<%=basePath%>hplus/css/plugins/morris/morris-0.4.3.min.css" rel="stylesheet">
    <!-- Gritter -->
    <link href="<%=basePath%>hplus/js/plugins/gritter/jquery.gritter.css" rel="stylesheet">
    <link href="<%=basePath%>hplus/css/animate.min.css" rel="stylesheet">
    <link href="<%=basePath%>hplus/css/style.min.css?v=4.0.0" rel="stylesheet">
    
    <base target="_blank">
</head>
<body class="gray-bg">
	<div class="row">
		<div class="col-sm-3" style="margin-top:20px">
       		<div class="ibox float-e-margins">
           		<div class="ibox-title">
               		<span id="yearPoorsigh" class="label label-info pull-right"></span>
               		<h5>注册总人数</h5>
           		</div>
           		<div class="ibox-content" align="center" style="height:100px;">
               		<h2 id="Evaluate" class="no-margins">128,754</h2>
           		</div>
       		</div>
   		</div>
   		<div class="col-sm-3" style="margin-top:20px">
       		<div class="ibox float-e-margins">
           		<div class="ibox-title">
               		<span id="yearPoorsigh" class="label label-info pull-right"></span>
               		<h5>今日新增注册人数</h5>
           		</div>
           		<div class="ibox-content" align="center" style="height:100px;">
               		<h2 id="Evaluate" class="no-margins">354</h2>
           		</div>
       		</div>
   		</div>
   		<div class="col-sm-6" style="margin-top:20px">
       		<div class="ibox float-e-margins">
           		<div class="ibox-title">
               		<span id="yearPoorsigh" class="label label-info pull-right"></span>
               		<h5>今日访客人数</h5>
           		</div>
           		<div class="ibox-content" align="center" style="height:100px;">
               		<h2 id="Evaluate" class="no-margins">128,547</h2>
           		</div>
       		</div>
   		</div>
	</div>
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
	                            <div id="UserRegNew" style="height:500px"></div>
	                        </div>
	                    </div>
	                </div>
	            </div>
	        </div>
	    </div>
	</div>
	
    <div class="row">
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
	<script src="<%=basePath%>hplus/js/jquery.min.js?v=2.1.4"></script>
    <script src="<%=basePath%>js/echarts.min.js"></script>
    <script src="<%=basePath%>js/china.js"></script>
    <script src="<%=basePath%>hplus/js/plugins/layer/layer.js"></script>
    <script src="<%=basePath%>hplus/js/bootstrap.min.js?v=3.3.5"></script>
    <script src="<%=basePath%>util/ajax.js"></script>
    <script src="<%=basePath%>util/global.js"></script>
    <script>
    
	//初始化加载
    function init(){
    	getUserReg();
    	getUserSex();
    	getUserRegNew();
    	
    }
    

  	//注册用户男女比例
    function getUserSex(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/UserAttr/UserSex.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		
			loadEchart(data,'UserSex');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
  	//年度用户出册曲线，按月
  	function getUserRegNew(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/UserAttr/UserRegByMonth.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		//平均值线，中间位置显示文字
     		data.series[0].markLine["label"]={
                normal:{
                    show:'true',
                    formatter:'{b}[{a}]:{c}',
                    position:'middle'
                }
            };
     		data.series[1].markLine["label"]={
                    normal:{
                        show:'true',
                        formatter:'{b}[{a}]:{c}',
                        position:'middle'
                    }
                };
			loadEchart(data,'UserRegNew');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
	//过去一周用户注册数曲线图
    function getUserReg(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/UserAttr/UserReg.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		//平均值线，中间位置显示文字
     		data.series[0].markLine["label"]={
                normal:{
                    show:'true',
                    formatter:'{b}[{a}]:{c}',
                    position:'middle'
                }
            };
     		data.series[1].markLine["label"]={
                normal:{
                    show:'true',
                    formatter:'{b}[{a}]:{c}',
                    position:'middle'
                }
            };
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