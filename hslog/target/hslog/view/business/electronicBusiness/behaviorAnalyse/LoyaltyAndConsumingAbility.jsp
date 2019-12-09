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
		<div class="col-sm-12">
			<div class="ibox float-e-margins" >
		    	<div class="ibox-content" style="height:700px;">
		       		<div id="LoyaltyAndConsumingAbility" style="height:650px;width:750px;margin:auto auto"></div>
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
    
    	getUserValue();
    	
    }
  
	//用户消费能力&忠诚度聚类图
	function getUserValue(){
		var url='<%=path%>/business/electronicBusiness/businessModelDemo/BehaviorAnalys/LoyaltyAndConsumingAbility.do';
	   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		data.tooltip.formatter=function (obj) {
                var value = obj.value;
                return '忠诚度 ：' + value[0] + '<br>'
                     + '消费能力 ：' + value[1] + '<br>'
                     + '人数 ：' + value[2] + '<br>';
            };
     		data.visualMap[0].inRange={
                symbolSize: [5, 70]
            };
     		data.visualMap[0].outOfRange= {
                symbolSize: [10, 70],
                color: ['rgba(255,255,255,.2)']
            };
     		data.visualMap[0].controller={
                inRange: {
                    color: ['#c23531']
                },
                outOfRange: {
                    color: ['#444']
                }
            };
     		data.visualMap[0].textGap=30;
			loadEchart(data,'LoyaltyAndConsumingAbility');
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