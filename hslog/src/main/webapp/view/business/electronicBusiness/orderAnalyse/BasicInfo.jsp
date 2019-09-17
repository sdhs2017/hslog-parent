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
    	<div class="col-sm-4">
      		<div class="ibox float-e-margins">
         		<div class="ibox-title">
              		<span id="year" class="label label-success pull-right"></span>
              		<h5>昨日成功支付用户数</h5>
          		</div>
          		<div class="ibox-content" align="center" style="height:100px;">
              		<h2 id="Stock" class="no-margins">6,999</h2>
          		</div>
      		</div>
  		</div>
   		<div class="col-sm-4">
       		<div class="ibox float-e-margins">
           		<div class="ibox-title">
               		<span id="yearPoorsigh" class="label label-info pull-right"></span>
               		<h5>昨日成功订单数</h5>
           		</div>
           		<div class="ibox-content" align="center" style="height:100px;">
               		<h2 id="GoodsCount" class="no-margins">7,635</h2>
           		</div>
       		</div>
   		</div>
   		<div class="col-sm-4">
       		<div class="ibox float-e-margins">
           		<div class="ibox-title">
               		<span id="yearPoorsigh" class="label label-info pull-right"></span>
               		<h5>昨日成功下单金额（元）</h5>
           		</div>
           		<div class="ibox-content" align="center" style="height:100px;">
               		<h2 id="GoodsCount" class="no-margins">3,139,718.62</h2>
           		</div>
       		</div>
   		</div>
    </div>
	<div class="row">
	    <div class="col-sm-6">
	        <div class="ibox float-e-margins">
	            <div class="ibox-content">
	                <div class="row">
	                    <div class="col-sm-12" style="height:550px">
	                        <div class="flot-chart" >
	                            <div id="TargetSales" style="height:550px"></div>
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
	                    <div class="col-sm-12" style="height:550px">
	                        <div class="flot-chart" >
	                            <div id="PaymentMethod" style="height:550px"></div>
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
	                    <div class="col-sm-12" style="height:550px">
	                        <div class="flot-chart" >
	                            <div id="PaymentMethodPie" style="height:600px"></div>
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
    	Payment();
    	GoodsSales();
    	PaymentMethodPie()
    }
 
  	
  	
  	//目标销售额完成情况 仪表盘
    function GoodsSales(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/OrderAnalyse/TargetSales.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		loadEchart(data,"TargetSales");
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
  	
  
  	//支付方式雷达图
    function Payment(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/OrderAnalyse/PaymentMethod.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		data.series[0].type='radar';
			data.tooltip={};
			
			data.radar={
			        indicator: [
			           { name: '支付宝', max: 16000},
			           { name: '找人代付', max: 16000},
			           { name: '银联在线', max: 16000},
			           { name: '银行卡', max: 16000},
			           { name: '信用卡', max: 16000},
			           { name: '微信', max: 16000},
			           { name: '网上银行', max: 16000},
			           { name: 'QQ钱包', max: 16000},
			           { name: '货到付款', max: 16000},
			           { name: '分期付款', max: 16000},
			           { name: '充值卡', max: 16000}
			        ],
			        splitNumber: 4
			    };
			loadEchart(data,'PaymentMethod');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
  	
	//目标销售额完成情况 仪表盘
    function PaymentMethodPie(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/OrderAnalyse/PaymentMethodPie.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		loadEchart(data,"PaymentMethodPie");
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