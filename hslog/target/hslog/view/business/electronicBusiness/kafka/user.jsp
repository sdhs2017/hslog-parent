<html>
<head>
<meta charset="utf-8">
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
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
<link href="<%=basePath%>hplus/css/plugins/bootstrap-table/bootstrap-table.min.css" rel="stylesheet">
<link href="<%=basePath%>hplus/css/animate.min.css" rel="stylesheet">
<link href="<%=basePath%>hplus/css/style.min.css?v=4.0.0" rel="stylesheet">
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content">
		<div class="row" >
            <div class="col-sm-3">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <span id="year" style="font-size:20;font-weight:bold">用户自然属性</span>
                    </div>
                    <div class="ibox-content" style="background-color: #c2c2c2;">
                        <span id="year"  style="font-size:16;margin-left:100px;font-weight:bold">姓名：李XX</span>
                    </div>
                    <div class="ibox-content" style="background-color: #c2c2c2;">
                        <span id="year"  style="font-size:16;margin-left:100px;font-weight:bold;">性别：女</span>
                    </div>
                    <div class="ibox-content" style="background-color: #c2c2c2;">
                        <span id="year"  style="font-size:16;margin-left:100px;font-weight:bold">年龄：25</span>
                    </div>
                    <div class="ibox-content" style="background-color: #c2c2c2;">
                        <span id="year"  style="font-size:16;margin-left:100px;font-weight:bold">婚否：未婚</span>
                    </div>
                    <div class="ibox-content" style="background-color: #c2c2c2;">
                        <span id="year"  style="font-size:16;margin-left:100px;font-weight:bold">住址：山东省济南市历下区</span>
                    </div>
                </div>
            </div>
            
            <div class="col-sm-9">
                <div class="ibox float-e-margins">
                     <div class="ibox-title">
                        <span id="year" style="font-size:20;font-weight:bold">商品推荐</span>
                    </div>
                    <div class="ibox-content" style="height:290px">
	                    <div class="row">
	                    	<div class="col-sm-4 ">
	                    		<div class="alert alert-success" style="height:260px">
	                            	<ul style="line-height:30px;">
			                            <li>
			                                <p><strong>一级分类</strong> - <span id="cf1_1"></span></p>
			                            </li>
			                            <li>
			                                <p><strong>二级分类</strong> - <span id="cf2_1"></span></p>
			                            </li>
			                            <li>
			                                <p><strong>三级分类</strong> - <span id="cf3_1"></span></p>
			                            </li>
			                            <li>
			                                <p><strong>商品名称</strong> - <span id="goods_1"></span></p>
			                            </li>
			                            <li>
			                                <p><strong>商品价格</strong> - <span id="goodsprice_1"></span></p>
			                            </li>
		                            </ul>
	                        	</div>
	                    	</div>
	                    	<div class="col-sm-4 ">
	                    		<div class="alert alert-info" style="height:260px">
	                            	<ul style="line-height:30px;">
			                            <li>
			                                <p><strong>一级分类</strong> - <span id="cf1_2"></span></p>
			                            </li>
			                            <li>
			                                <p><strong>二级分类</strong> - <span id="cf2_2"></span></p>
			                            </li>
			                            <li>
			                                <p><strong>三级分类</strong> - <span id="cf3_2"></span></p>
			                            </li>
			                            <li>
			                                <p><strong>商品名称</strong> - <span id="goods_2"></span></p>
			                            </li>
			                            <li>
			                                <p><strong>商品价格</strong> - <span id="goodsprice_2"></span></p>
			                            </li>
		                            </ul>
	                        	</div>
	                    	</div>
	                    	<div class="col-sm-4 ">
	                    		<div class="alert alert-warning" style="height:260px">
	                            	<ul style="line-height:30px;">
			                            <li>
			                                <p><strong>一级分类</strong> - <span id="cf1_3"></span></p>
			                            </li>
			                            <li>
			                                <p><strong>二级分类</strong> - <span id="cf2_3"></span></p>
			                            </li>
			                            <li>
			                                <p><strong>三级分类</strong> - <span id="cf3_3"></span></p>
			                            </li>
			                            <li>
			                                <p><strong>商品名称</strong> - <span id="goods_3"></span></p>
			                            </li>
			                            <li>
			                                <p><strong>商品价格</strong> - <span id="goodsprice_3"></span></p>
			                            </li>
		                            </ul>
	                        	</div>
	                    	</div>
	                    </div>
                    </div>
                </div>
            </div>
          	
        </div>
        <div class="row" >
        	<div class="col-sm-12" >
				<div class="ibox-content">
					<div class="row row-lg">
						<div class="col-sm-4">
							<div id="cf1Pie" class="col-sm-12" style="height: 500px"></div>
						</div>
						<div class="col-sm-4">
							<div id="cf2Pie" class="col-sm-12" style="height: 500px"></div>
						</div>
						<div class="col-sm-4">
							<div id="cf3Pie" class="col-sm-12" style="height: 500px"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row" style="margin-top:20px">
			<div class="col-sm-12">
				<div class="ibox-content">
					<div class="row row-lg">
						<div class="col-sm-12">
							<div id="goodsTop5" class="col-sm-12" style="height: 500px"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<script src="<%=basePath%>hplus/js/jquery.min.js?v=2.1.4"></script>
	<script src="<%=basePath%>hplus/js/jquery.form.js"></script>
	<script src="<%=basePath%>js/echarts.min.js"></script>
	<script src="<%=basePath%>hplus/js/jquery.min.js?v=2.1.4"></script>
	<script src="<%=basePath%>hplus/js/jquery.form.js"></script>
	<script src="<%=basePath%>hplus/js/bootstrap.min.js?v=3.3.5"></script>
	<script src="<%=basePath%>hplus/js/content.min.js?v=1.0.0"></script>
	<script src="<%=basePath%>hplus/js/plugins/bootstrap-table/bootstrap-table.min.js"></script>
	<script src="<%=basePath%>hplus/js/plugins/bootstrap-table/bootstrap-table-mobile.min.js"></script>
	<script src="<%=basePath%>hplus/js/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
	<script src="<%=basePath%>hplus/js/demo/bootstrap-table-demo.min.js"></script>
	<script src="http://tajs.qq.com/stats?sId=9051096" charset="UTF-8"></script>
	<script src="<%=basePath%>hplus/js/plugins/layer/layer.js"></script>
	<script src="<%=basePath%>js/jquery.easyui.min.js"></script>
	<script src="<%=basePath%>js/echarts.min.js"></script>
	<script src="<%=basePath%>js/bootstrap-multiselect/bootstrap-multiselect.js"></script>
	<script src="<%=basePath%>util/ajax.js"></script>
	<script src="<%=basePath%>util/global.js"></script>
	<!--  
	<script type="text/javascript"
		src="http://echarts.baidu.com/gallery/vendors/echarts/echarts-all-3.js"></script>
	<script type="text/javascript"
		src="http://echarts.baidu.com/gallery/vendors/echarts/extension/dataTool.min.js"></script>
	<script type="text/javascript"
		src="http://echarts.baidu.com/gallery/vendors/echarts/map/js/china.js"></script>
	<script type="text/javascript"
		src="http://echarts.baidu.com/gallery/vendors/echarts/map/js/world.js"></script>
	<script type="text/javascript"
		src="http://api.map.baidu.com/api?v=2.0&ak=ZUONbpqGBsYGXNIYHicvbAbM"></script>
	<script type="text/javascript"
		src="http://echarts.baidu.com/gallery/vendors/echarts/extension/bmap.min.js"></script>
	-->
	<script type="text/javascript"> 
       
        //cf1动态图  
        //获取div Id
		var dom = document.getElementById("cf1Pie");
        //向div中添加数据
	    var myChart = echarts.init(dom);
	    var app = {};
	    clearInterval(app.timeTicket);
	    //循环加载数据
	    app.timeTicket = setInterval(function (){
	    	//请求路径
	    	var url='<%=path%>/pvuv/cf1Pie.do';
	    	//成功后回调函
	   		var sFunc = function(option){
	    		//向div添加数据
				myChart.setOption(option);
	    	};
	    	//请求方法
	        ajaxPostWithOutLayer(url, {}, sFunc);
	    //请求时间间隔
		}, 2000);
	    
	    
	    //cf2 top5 饼图
		//获取div Id
		var dom1 = document.getElementById("cf2Pie");
	    //向div中添加数据
		var myChart1 = echarts.init(dom1);
		var app1 = {};
		clearInterval(app1.timeTicket);
		//混循环获取数据
		app1.timeTicket = setInterval(function (){
			//请求路径
		 	var url3='<%=path%>/pvuv/cf2PieWithOther.do';
		 	//成功后回调函数
		 	var sFunc3 = function(option1){
		 		//向div添加数据
				myChart1.setOption(option1);
		 	};
		 	//求情方法
		    ajaxPostWithOutLayer(url3, {}, sFunc3);
		 //请求时间间隔
		 }, 2000);
		  
		
		//cf2 top5 饼图
		var dom2 = document.getElementById("cf3Pie");
		var myChart2 = echarts.init(dom2);
		var app2 = {};
		clearInterval(app2.timeTicket);
			app2.timeTicket = setInterval(function (){
		    	var url5='<%=path%>/pvuv/cf3PieWithOther.do';
		    	//成功后回调函数
		 		var sFunc5 = function(option2){
		    		//向div中添加数据
					myChart2.setOption(option2);
		 		 };
		    //请求方法	 
			ajaxPostWithOutLayer(url5, {}, sFunc5);
		    //请求时间间隔
		}, 2000);
		
		//商品 top5 饼图
		//获取div id
		var dom3 = document.getElementById("goodsTop5");
		//向div中添加数据
		var myChart3 = echarts.init(dom3);
		var app3 = {};
		clearInterval(app3.timeTicket);
		//循环获取数据
		app3.timeTicket = setInterval(function (){
			//请求方法路径
		 	var url6='<%=path%>/pvuv/goodsTop5.do';
		 	//成功后回调函数
		 	var sFunc6 = function(option3){
		 		//div中添加显示内容
				myChart3.setOption(option3);
		 		};
		 	//请求方法
			ajaxPostWithOutLayer(url6, {}, sFunc6);
		 }, 2000);
		 
		 
		//商品 推荐
		var app4 = {};
		clearInterval(app4.timeTicket);
		app4.timeTicket = setInterval(function (){
			var goodsRecommendUrl='<%=path%>/pvuv/goodsRecommendByCf1.do';
			//成功后回调函数
			var sFunc7 = function(option4){
			if(option4.length>0){
		    	if(option4[0]!=undefined){
		    		$('#cf1_1').text(option4[0].cf1);
					$('#cf2_1').text(option4[0].cf2);
					$('#cf3_1').text(option4[0].cf3);
					$('#goods_1').text(option4[0].goodsName);
					$('#goodsprice_1').text(option4[0].price);
		    	}
		    	if(option4[1]!=undefined){
		    		$('#cf1_2').text(option4[1].cf1);
					$('#cf2_2').text(option4[1].cf2);
					$('#cf3_2').text(option4[1].cf3);
					$('#goods_2').text(option4[1].goodsName);
					$('#goodsprice_2').text(option4[1].price);
		    	}
		    	if(option4[2]!=undefined){
		    		$('#cf1_3').text(option4[2].cf1);
					$('#cf2_3').text(option4[2].cf2);
					$('#cf3_3').text(option4[2].cf3);
					$('#goods_3').text(option4[2].goodsName);
					$('#goodsprice_3').text(option4[2].price);
		    		}
		    	}
			};
			ajaxPostWithOutLayer(goodsRecommendUrl, {}, sFunc7);
		}, 2000);
		
	</script>
</body>
</html>