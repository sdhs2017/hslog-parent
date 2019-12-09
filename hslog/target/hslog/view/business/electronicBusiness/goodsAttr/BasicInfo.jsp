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
	<div class="row" style="margin-top:20px">
		<div class="col-sm-4" >
       		<div class="ibox float-e-margins">
           		<div class="ibox-title">
               		<span id="yearPoorsigh" class="label label-info pull-right"></span>
               		<h5>上架商品种类（个）</h5>
           		</div>
           		<div class="ibox-content" align="center" style="height:100px;">
               		<h2 id="Evaluate" class="no-margins">1,321</h2>
           		</div>
       		</div>
   		</div>
   		<div class="col-sm-4">
       		<div class="ibox float-e-margins">
           		<div class="ibox-title">
               		<span id="yearPoorsigh" class="label label-info pull-right"></span>
               		<h5>今日销量（件）</h5>
           		</div>
           		<div class="ibox-content" align="center" style="height:100px;">
               		<h2 id="Evaluate" class="no-margins">451,782</h2>
           		</div>
       		</div>
   		</div>
   		<div class="col-sm-4">
       		<div class="ibox float-e-margins">
           		<div class="ibox-title">
               		<span id="yearPoorsigh" class="label label-info pull-right"></span>
               		<h5>商品库存量（件）</h5>
           		</div>
           		<div class="ibox-content" align="center" style="height:100px;">
               		<h2 id="Evaluate" class="no-margins">4,863,541</h2>
           		</div>
       		</div>
   		</div>
	</div>
    <div class="wrapper wrapper-content">
		<div class="ibox float-e-margins">
            <div class="ibox-content">
                <div class="row row-lg">
                    <div class="col-sm-12">
                        <div id="GoodsKindPriceDistribution" class="col-sm-12" style="height:500px"></div>
                    </div>
                </div>
            </div>
            <div class="ibox-content">
                <div class="row row-lg">
                    <div class="col-sm-12" style="height:700px">
                        <div id="GoodsStock" style="width:100%;height:600px"></div>
                    </div>
                </div>
            </div>
            <div class="ibox-content">
                <div class="row row-lg">
                    <div class="col-sm-12">
                        <div id="GoodsKind" style="height:600px"></div>
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
   /*  function init(){
    	GoodsStock();
    	GoodsKind();
    	GoodsKindPriceDistribution();
    } */
    //商品库存
    function GoodsStock(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/GoodsAttr/GoodsStock.do';
    	
     	//成功后回调函数
     	var sFunc = function(data){
			loadEchart(data,'GoodsStock');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
     };
     //商品品类
	function GoodsKind(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/GoodsAttr/GoodsKind.do';
     	
      	//成功后回调函数
      	var sFunc = function(data){
 			loadEchart(data,'GoodsKind');
      	}
      	//获取数据并通过回调函数进行数据加载。
      	ajaxPost(url,{},sFunc); 
      };
      //商品品类价格分布
	function GoodsKindPriceDistribution(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/GoodsAttr/GoodsKindPriceDistribution.do';
       	
        //成功后回调函数
        var sFunc = function(data){
   			loadEchart(data,'GoodsKindPriceDistribution');
        }
        //获取数据并通过回调函数进行数据加载。
        ajaxPost(url,{},sFunc); 
        };
    //Echart加载事件
	function loadEchart(option,id){
		var bookEchart = echarts.init(document.getElementById(id));
		bookEchart.setOption(option);
	}
    $(function() { 
    	init();
    });
   
</script>
</body>
</html>