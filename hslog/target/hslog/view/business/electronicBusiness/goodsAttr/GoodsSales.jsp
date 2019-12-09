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
    <div class="wrapper wrapper-content">
		<div class="ibox float-e-margins">
            <div class="ibox-content">
                <div class="row row-lg">
                    <div class="col-sm-12">
                        <div id="SalesByCategory" class="col-sm-12" style="height:500px"  ></div>
                    </div>
                </div>
            </div>
            <div class="ibox-content">
                <div class="row row-lg">
                    <div class="col-sm-12">
                        <div id="GoodsSalesByWeek" class="col-sm-12" style="height:500px"  ></div>
                    </div>
                </div>
            </div>
            
            
            <div class="ibox-content">
                <div class="row row-lg">
                    <div class="col-sm-12">
                        <div id="GoodsSalesByMonth" class="col-sm-12" style="height:500px"></div>
                    </div>
                </div>
            </div>
            <div class="ibox-content">
                <div class="row row-lg">
                    <div class="col-sm-12">
                        <div id="GoodsSalesRankByMonth" class="col-sm-12" style="height:500px"></div>
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
    function init(){
    	GoodsSalesByWeek();
    	GoodsSalesByMonth();
    	SalesByCategory();
    	GoodsSalesRankByMonth();
    }
    //商品销量排行-月
    function GoodsSalesRankByMonth(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/GoodsAttr/GoodsSalesRankByMonth.do';
    	 
     	//成功后回调函数
     	var sFunc = function(data){
     		var datas= [
     		           ["笔类","纸类","办公文具"],
     		           ["口罩","遮阳伞/雨伞","太阳镜"],
     		           ["风衣","牛仔裤","羊毛衫"],
     		           ["风衣","卫衣","休闲裤"],
     		           ["运动服","运动鞋","套装"],
     		           ["女靴","高跟鞋","内增高"],
     		           ["男靴","商务休闲鞋","休闲鞋"],
     		           ["浴室用品","洗晒/熨烫","雨伞雨具"],
     		           ["手机壳","数据线","手机饰品"],
     		           ["内存","SSD固态硬盘","硬盘"]];
     		data.series[1].label.normal.formatter = function(val){
                return datas[val.dataIndex][0];
            };
            data.series[2].label.normal.formatter = function(val){
                return datas[val.dataIndex][1];
            };
            data.series[3].label.normal.formatter = function(val){
                return datas[val.dataIndex][2];
            };
			loadEchart(data,'GoodsSalesRankByMonth');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
	};
	//商品不同种类销量
    function SalesByCategory(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/GoodsAttr/SalesByCategory.do';
    	 
     	//成功后回调函数
     	var sFunc = function(data){
     		data.tooltip.formatter=function (params) {
                var tar = params[1];
                return tar.name + '<br/>' + tar.seriesName + ' : ' + tar.value+'件';
            }
			loadEchart(data,'SalesByCategory');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
	};
     
     //过去一周每日销量
	function GoodsSalesByWeek(){
    	var url='<%=path%>/business/electronicBusiness/businessModelDemo/GoodsAttr/GoodsSalesByWeek.do';
    	
     	//成功后回调函数
     	var sFunc = function(data){
     		//平均值线，中间位置显示文字
     		data.series[0].markLine["label"]={
                normal:{
                    show:'true',
                    formatter:'{b}:{c}件',
                    position:'middle'
                }
            };
			loadEchart(data,'GoodsSalesByWeek');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,{},sFunc); 
	};
     //月销量
	function GoodsSalesByMonth(){
     	var url='<%=path%>/business/electronicBusiness/businessModelDemo/GoodsAttr/GoodsSalesByMonth.do';
     	
      	//成功后回调函数
      	var sFunc = function(data){
      		//平均值线，中间位置显示文字
     		/* data.series[1].markLine["label"]={
                normal:{
                    show:'true',
                    formatter:'{b}:{c}件',
                    position:'middle'
                }
            }; */
 			loadEchart(data,'GoodsSalesByMonth');
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