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
	    <div class="col-sm-6" style="margin-top:20px;">
           	<div class="row">
           		<div class="col-sm-6">
             		<div class="ibox float-e-margins">
                		<div class="ibox-title">
                     		<span id="year" class="label label-success pull-right"></span>
                     		<h5>上架商品库存量</h5>
                 		</div>
                 		<div class="ibox-content" align="center" style="height:100px;">
                     		<h2 id="Stock" class="no-margins">0</h2>
                 		</div>
             		</div>
         		</div>
         		<div class="col-sm-6">
             		<div class="ibox float-e-margins">
                 		<div class="ibox-title">
                     		<span id="yearPoorsigh" class="label label-info pull-right"></span>
                     		<h5>上架商品总数量</h5>
                 		</div>
                 		<div class="ibox-content" align="center" style="height:100px;">
                     		<h2 id="GoodsCount" class="no-margins">0</h2>
                 		</div>
             		</div>
         		</div>
           </div>
           <div class="row">
          		<div class="col-sm-6">
             		<div class="ibox float-e-margins">
                		<div class="ibox-title">
                     		<span id="year" class="label label-success pull-right"></span>
                     		<h5>一级分类</h5>
                 		</div>
                 		<div class="ibox-content" align="center"  style="height:100px;">
                     		<h2 id="level1" class="no-margins">0</h2>
                 		</div>
             		</div>
         		</div>
         		<div class="col-sm-6">
             		<div class="ibox float-e-margins">
                 		<div class="ibox-title">
                     		<span id="yearPoorsigh" class="label label-info pull-right"></span>
                     		<h5>商品平均好评率</h5>
                 		</div>
                 		<div class="ibox-content" align="center" style="height:100px;">
                     		<h2 id="Evaluate" class="no-margins">0</h2>
                 		</div>
             		</div>
         		</div>
           </div>
           <div class="col-sm-12">
	            <div class="ibox-content">
	                <div class="row">
	                    <div class="col-sm-12">
	                        <div class="flot-chart" style="height:250px">
	                            <div id="GoodsPrice" style="height:250px"></div>
	                        </div>
	                    </div>
	                </div>
	            </div>
           </div>
	    </div>
	    <div class="col-sm-6" style="margin-top:20px;">
	        <div class="ibox float-e-margins">
	            <div class="ibox-content">
	                <div class="row">
	                    <div class="col-sm-12" style="height:600px">
	                        <div class="flot-chart" >
	                            <div id="GoodsSales" style="height:600px"></div>
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
		       		<div id="" style="width:1000px;height:500px;margin:auto auto"></div>
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
    	getGoodsAttr();
    	getGoodsSales();
    	getGoodsPrice();
    }
  	//商品价格分布
    function getGoodsPrice(){
    	var url='<%=path%>GoodsAttr/getGoodsPrice.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		loadEchart(data,"GoodsPrice");
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
  	//商品销量
    function getGoodsSales(){
    	var url='<%=path%>GoodsAttr/getGoodsSales.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
     		loadEchart(data,"GoodsSales");
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
    }
  	//商品基本属性
    function getGoodsAttr(){
    	var url='<%=path%>GoodsAttr/getGoodsAttrs.do';
   	   
     	//成功后回调函数
     	var sFunc = function(data){
			$("#Evaluate").html(data.Evaluate);
			$("#level1").html(data.level1);
			$("#GoodsCount").html(data.GoodsCount);
			$("#Stock").html(data.Stock);
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