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
	        <div class="ibox float-e-margins">
	            <div class="ibox-content">
	                <div class="row">
	                    <div class="col-sm-12" style="height:450px">
	                        <div class="flot-chart" >
	                            <div id="MaxOrderByWeek" style="height:450px"></div>
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
	                            <div id="payIndentNumber" style="height:550px"></div>
	                        </div>
	                    </div>
	                </div>
	            </div>
	        </div>
	    </div>
	    <div class="col-sm-6">
			<div class="ibox float-e-margins">
		    	<div class="ibox-content" style="height:550px">
		       		<div id="PaymentOrderContrast" style="height:550px;"></div>
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
	                            <div id="PaymentOrderRate" style="height:550px"></div>
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
	                            <div id="OrderRate" style="height:550px"></div>
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
		    	<div class="ibox-content" style="height:550px">
		       		<div id="WeekOfSales" style="height:550px;"></div>
		        </div>
		 	</div>
		</div>
		<div class="col-sm-6">
			<div class="ibox float-e-margins">
		    	<div class="ibox-content" style="height:550px">
		       		<div id="OrderSaleRate" style="height:550px;"></div>
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
    	PaymentOrderRate();
    	payIndentNumber();
    	MaxOrder();
    	PaymentOrderContrast();
    	OrderRate();
    	WeekOfSales();
    	OrderSaleRate();
    }
  	//过去一周订单最大金额
    function MaxOrder(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/OrderAnalyse/MaxOrderByWeek.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		//平均值线，中间位置显示文字
     		data.series[0].markLine["label"]={
                normal:{
                    show:'true',
                    formatter:'{b}:{c}元',
                    position:'middle'
                }
            };
			loadEchart(data,'MaxOrderByWeek');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
  	
  	//过去一周已支付订单数量（曲线图）
    function payIndentNumber(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/OrderAnalyse/payIndentNumber.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		data.series[0].markLine["label"]={
                    normal:{
                        show:'true',
                        formatter:'{b}:{c}元',
                        position:'middle'
                    }
                };
     		loadEchart(data,"payIndentNumber");
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
    
  	//2016年已支付订单比率
    function OrderRate(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/OrderAnalyse/OrderRate.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		
			loadEchart(data,'OrderRate');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
  	//过去一周订单支付率
    function PaymentOrderRate(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/OrderAnalyse/PaymentOrderRate.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		//平均值线，中间位置显示文字
     		data.series[0].markLine["label"]={
                normal:{
                    show:'true',
                    formatter:'{b}:{c}%',
                    position:'middle'
                }
            };
			loadEchart(data,'PaymentOrderRate');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
    
  	//过去一周销售额
    function WeekOfSales(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/OrderAnalyse/WeekOfSales.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		//平均值线，中间位置显示文字
     		data.series[0].markLine["label"]={
                normal:{
                    show:'true',
                    formatter:'{b}:{c}元',
                    position:'middle'
                }
            };
     		loadEchart(data,"WeekOfSales");
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
	
    //2016年销售额
    function OrderSaleRate(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/OrderAnalyse/OrderSaleRate.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		loadEchart(data,"OrderSaleRate");
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
    
    //2016年已支付订单数量（订单数为柱状图，同比环比为曲线图）
    function PaymentOrderContrast(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/OrderAnalyse/PaymentOrderContrast.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		loadEchart(data,"PaymentOrderContrast");
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