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
    <link href="<%=basePath%>hplus/css/plugins/bootstrap-table/bootstrap-table.min.css" rel="stylesheet">
    <link href="<%=basePath%>hplus/css/animate.min.css" rel="stylesheet">
    <link href="<%=basePath%>hplus/css/style.min.css?v=4.0.0" rel="stylesheet">
</head>
<body class="gray-bg">
    <div class="row" style="margin-left:30px;margin-top:20px;">
		<div class="ibox float-e-margins">
			<div class="col-sm-6">
           		<div class="ibox float-e-margins">
               		<div class="ibox-title">
                   		<span id="yearPoorsigh" class="label label-info pull-right"></span>
                   		<h5>今日商品差评 / 今日商品好评 / 今日商品评价总数</h5>
               		</div>
               		<div class="ibox-content" align="center" style="height:100px;">
                   		<h2 id="Evaluate" class="no-margins">389 / 5643 / 6032</h2>
               		</div>
           		</div>
       		</div>
            <div class="col-sm-6">
           		<div class="ibox float-e-margins">
               		<div class="ibox-title">
                   		<span id="yearPoorsigh" class="label label-info pull-right"></span>
                   		<h5>今日商品差评率 / 今日商品好评率 / 本月商品平均好评率</h5>
               		</div>
               		<div class="ibox-content" align="center" style="height:100px;">
                   		<h2 id="Evaluate" class="no-margins">6.45% / 93.55% / 92.35%</h2>
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
	                            <div id="GoodEvalTop10" style="height:550px"></div>
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
	                            <div id="BadEvalTop10" style="height:550px"></div>
	                        </div>
	                    </div>
	                </div>
	            </div>
	        </div>
	    </div>
	</div>
	<div class="row" style="margin-left:30px">
    	<div class="col-sm-6">
			<div class="ibox-content">
	              	<div class="row row-lg">
	              		<div class="col-sm-12">
	                     		<div id="BadCommentReason" class="col-sm-12" style="height:500px"></div>
	                 		</div>
	              	</div>
			</div>
         </div>
    </div>
    <script src="<%=basePath%>hplus/js/jquery.min.js?v=2.1.4"></script>
    <script src="<%=basePath%>hplus/js/jquery.form.js"></script>
    <script src="<%=basePath%>js/echarts.min.js"></script>
    <script src="<%=basePath%>util/ajax.js"></script>
    <script src="<%=basePath%>hplus/js/plugins/layer/layer.js"></script>
    
  
    <script> 
    $(function() { 
    	init();
    });
    function init(){
    	BadCommentReason();
    	GoodEvalTop10();
    	BadEvalTop10();
    }
    //差评原因
	function BadCommentReason(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/GoodsAttr/BadCommentReason.do';
    	
     	//成功后回调函数
     	var sFunc = function(data){
     		data.series[0].avoidLabelOverlap= false;
			
			data.series[0].label.normal.position='center';
			
	            data.series[0].labelLine= {
	                normal: {
	                    show: false
	                }
	            };
			loadEchart(data,'BadCommentReason');
     	}
     	//获取数据并通过回调函数进行数据加载。
    	ajaxPost(url,{},sFunc); 
	};
     //商品好评Top10
	function GoodEvalTop10(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/GoodsAttr/GoodEvalTop10.do';
     	
      	//成功后回调函数
      	var sFunc = function(data){
      		
 			loadEchart(data,'GoodEvalTop10');
      	}
      	//获取数据并通过回调函数进行数据加载。
      	ajaxPost(url,{},sFunc); 
	};
      //商品差评Top10
	function BadEvalTop10(){
       	var url='<%=path%>/business/electronicBusiness/businessModelDemo/GoodsAttr/BadEvalTop10.do';
       	
        	//成功后回调函数
        	var sFunc = function(data){
        		
   			loadEchart(data,'BadEvalTop10');
        	}
        	//获取数据并通过回调函数进行数据加载。
        	ajaxPost(url,{},sFunc); 
	};
    //Echart加载事件
	function loadEchart(option,id){
		var bookEchart = echarts.init(document.getElementById(id));
		bookEchart.setOption(option);
	}
    
   
</script>
</body>
</html>