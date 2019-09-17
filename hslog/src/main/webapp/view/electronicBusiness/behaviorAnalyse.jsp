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
	    <div class="col-sm-7">
			<div class="ibox float-e-margins">
		    	<div class="ibox-content" style="height:500px">
		       		<div id="SalesMap" style="height:500px"></div>
		        </div>
		 	</div>
		</div>
		<div class="col-sm-5">
			<div class="ibox float-e-margins">
		    	<div class="ibox-content" style="height:500px">
		       		<div id="ReturnGoods" style="height:500px"></div>
		        </div>
		 	</div>
		</div>
	</div>
	<div class="row">
		<div class="col-sm-12">
			<div class="ibox float-e-margins">
		    	<div class="ibox-content" style="height:400px">
		       		<div id="BehaviorFunnel" style="width:1000px;height:400px"></div>
		        </div>
		 	</div>
		</div>
    </div> 
    <div class="row">
		<div class="col-sm-6">
			<div class="ibox float-e-margins">
		    	<div class="ibox-content" style="height:610px">
		       		<div id="UserValue" style="height:600px"></div>
		        </div>
		 	</div>
		</div>
		<div class="col-sm-6">
			<div class="ibox float-e-margins">
		    	<div class="ibox-content" style="height:610px">
		       		<div id="VisitTime" style="height:600px"></div>
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
    	getSalesMap();
    	getReturnGoods();
    	getBehaviorFunnel();
    	getUserValue();
    	getVisitTime();
    }
  	//用户平均访问时常柱状图
    function getVisitTime(){
    	var url='<%=path%>BehaviorAnalys/getVisitTime.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
			loadEchart(data,'VisitTime');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
	//用户消费能力&忠诚度聚类图
	function getUserValue(){
		var url='<%=path%>BehaviorAnalys/getUserValue.do';
	   	   
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
			loadEchart(data,'UserValue');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc);
	}
  	//订单退换货情况
    function getReturnGoods(){
    	var url='<%=path%>BehaviorAnalys/getReturnGoods.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
			loadEchart(data,'ReturnGoods');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
	//销售额分布
    function getSalesMap(){
    	var url='<%=path%>BehaviorAnalys/getSalesMap.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
			loadEchart(data,'SalesMap');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
    //行为漏斗图
    function getBehaviorFunnel(){
    	var url='<%=path%>BehaviorAnalys/getBehaviorFunnel.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		data.series[0].label = {
                    normal: {
                        show: true,
                        formatter:'{b} : {c}',
                        textStyle:{
                            color:'#000'
                        },
                        position: 'inside'
                    }
                };
     		data.series[0].labelLine = {
                normal: {
                    show:true,
                    length: 100,
                    lineStyle: {
                        width: 1,
                        type: 'solid'
                    }
                }
            };
			loadEchart(data,'BehaviorFunnel');
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