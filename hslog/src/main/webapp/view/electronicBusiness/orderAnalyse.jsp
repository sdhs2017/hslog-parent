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
	                    <div class="col-sm-12" style="height:500px">
	                        <div class="flot-chart" >
	                            <div id="Payment" style="height:500px"></div>
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
	                            <div id="GoodsEvaluation" style="height:500px"></div>
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
	                            <div id="MaxOrder" style="height:500px"></div>
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
	                            <div id="SalesVolume" style="height:500px"></div>
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
	                            <div id="OrderCancelReason" style="height:500px"></div>
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
	                            <div id="SalesVolumeByDifCategory1" style="height:500px"></div>
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
	            <div class="ibox-content">
	                <div class="row">
	                    <div class="col-sm-12" style="height:500px">
	                        <div class="flot-chart" >
	                            <div id="SalesVolumeByDifCategory" style="height:500px"></div>
	                        </div>
	                    </div>
	                </div>
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
    	getPayment();
    	getGoodsEvaluation();
    	getMaxOrder();
    	getSalesVolume();
    	getOrderCancelReason();
    	getSalesVolumeByDifCategory();
    }
  	//不同品类的月销量
    function getSalesVolumeByDifCategory(){
    	var url='<%=path%>OrderAnalyse/getSalesVolumeByDifCategory.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		data.tooltip.formatter=function (params) {
                var tar = params[1];
                return tar.name + '<br/>' + tar.seriesName + ' : ' + tar.value;
            }
			loadEchart(data,'SalesVolumeByDifCategory');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
  	//订单取消原因分布
    function getOrderCancelReason(){
    	var url='<%=path%>OrderAnalyse/getOrderCancelReason.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		data.series[0].areaStyle={normal: {}};
			loadEchart(data,'OrderCancelReason');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
  	//每日产品销量
    function getSalesVolume(){
    	var url='<%=path%>OrderAnalyse/getSalesVolume.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
			loadEchart(data,'SalesVolume');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
  	//过去一周订单最大金额
    function getMaxOrder(){
    	var url='<%=path%>OrderAnalyse/getMaxOrder.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
			loadEchart(data,'MaxOrder');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
  	//差评原因  饼图
    function getGoodsEvaluation(){
    	var url='<%=path%>OrderAnalyse/getGoodsEvaluation.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		data.series[0].label.normal.position='center';
     		data.series[0].avoidLabelOverlap= false;
			
			loadEchart(data,'GoodsEvaluation');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
  	//支付方式雷达图
    function getPayment(){
    	var url='<%=path%>OrderAnalyse/getPayment.do';
   	   
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
			loadEchart(data,'Payment');
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