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
    <title>H+ 后台主题UI框架 - 首页示例二</title>
    <meta name="keywords" content="H+后台主题,后台bootstrap框架,会员中心主题,后台HTML,响应式后台">
    <meta name="description" content="H+是一个完全响应式，基于Bootstrap3最新版本开发的扁平化主题，她采用了主流的左右两栏式布局，使用了Html5+CSS3等现代技术">
    <link rel="shortcut icon" href="favicon.ico"> 
    <link href="<%=basePath%>hplus/css/bootstrap.min.css?v=3.3.5" rel="stylesheet">
    <link href="<%=basePath%>hplus/css/font-awesome.min.css?v=4.4.0" rel="stylesheet">
    <!-- Morris -->
    <link href="<%=basePath%>hplus/css/plugins/morris/morris-0.4.3.min.css" rel="stylesheet">
    <!-- Gritter -->
    <link href="<%=basePath%>hplus/js/plugins/gritter/jquery.gritter.css" rel="stylesheet">
    <link href="<%=basePath%>hplus/css/animate.min.css" rel="stylesheet">
    <link href="<%=basePath%>hplus/css/style.min.css?v=4.0.0" rel="stylesheet"><base target="_blank">
</head>

<body class="gray-bg">
    <div class="wrapper wrapper-content">
        <div class="row">
            <div class="col-sm-4">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <span class="label label-success pull-right">2015年</span>
                        <h5>受检人数</h5>
                    </div>
                    <div class="ibox-content" align="center" >
                        <h2 id="INSPECTED" class="no-margins" ></h2>
                    </div>
                </div>
            </div>
            <div class="col-sm-4">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <span class="label label-info pull-right">2015年</span>
                        <h5>营养不良率</h5>
                    </div>
                    <div class="ibox-content" align="center">
                        <h2  id="DYSTROPHIA"  class="no-margins"></h2>
                    </div>
                </div>
            </div>
            <div class="col-sm-4">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <span class="label label-primary pull-right">2015年</span>
                        <h5>肥胖率</h5>
                    </div>
                    <div class="ibox-content" align="center">
                        <h2 id="FAT"  class="no-margins"></h2>
                    </div>
                </div>
            </div>
        </div>
        <!--  
        <div class="row">
            <div class="col-sm-12">
                <div class="ibox float-e-margins">
                    <div class="ibox-content">
                        <div class="row">
                            <div class="col-sm-6">
                                <div class="flot-chart" >
                                    <div id="selectyearx_dystrophiay" class="col-sm-12" style="height:200px"></div>
                                </div>
                            </div>
                            <div class="col-sm-6">
                                <div class="flot-chart">
                                    <div class="flot-chart-content" id="selectyearx_faty"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        -->
	<div class="row">
	    <div class="col-sm-6">
	        <div class="ibox float-e-margins">
	            <div class="ibox-content">
	                <div class="row">
	                    <div class="col-sm-12" style="height:200px">
	                        <div class="flot-chart" >
	                            <div id="selectyearx_dystrophiay" style="height:200px"></div>
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
	                    <div class="col-sm-12" style="height:200px">
	                        <div class="flot-chart" >
	                            <div id="selectyearx_faty" style="height:200px"></div>
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
                            
                            <div class="col-sm-12">
                                <div class="flot-chart">
                                    <div class="flot-chart-content" id="selectcityx_thany"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="<%=basePath%>hplus/js/jquery.min.js?v=2.1.4"></script>
    <script src="<%=basePath%>hplus/js/jquery.form.js"></script>
    <script src="<%=basePath%>js/jquery.easyui.min.js"></script>
    <script src="<%=basePath%>js/echarts.min.js"></script>
    <script src="<%=basePath%>hplus/js/plugins/layer/layer.js"></script>
    <script src="<%=basePath%>hplus/js/bootstrap.min.js?v=3.3.5"></script>
    <script src="<%=basePath%>hplus/js/content.min.js?v=1.0.0"></script>
    <script src="<%=basePath%>hplus/js/plugins/jquery-ui/jquery-ui.min.js"></script>
    <script src="<%=basePath%>util/ajax.js"></script>
    <script src="<%=basePath%>util/global.js"></script>
    <script src="<%=basePath%>util/globalEchart.js"></script>
    <script>
            
        	//初始化加载
            function getDystrophiayFatAllChart(){
            	var url='<%=path%>/business/health/basicCharts/nutritional/getDystrophiayFatAllChart.do';
          	   
             	//参数
             	var param={"YEAR":"2015"};
             	//成功后回调函数
             	var sFunc = function(data){
             		$("#INSPECTED").html(data.mapAll.INSPECTED);
        			$("#DYSTROPHIA").html(data.mapAll.DYSTROPHIA+"%");
        			$("#FAT").html(data.mapAll.FAT+"%");
        			GlobalEchartsLoad(JSON.parse(data.dystrophiay),'selectyearx_dystrophiay');
        			GlobalEchartsLoad(JSON.parse(data.faty),'selectyearx_faty');
        			GlobalEchartsLoad(JSON.parse(data.thany),'selectcityx_thany');
             	}
             	//获取数据并通过回调函数进行数据加载。
             	ajaxPost(url,param,sFunc); 
             };
             $(function() { 
            	 getDystrophiayFatAllChart();
             });  
    </script>
    <script type="text/javascript" src="http://tajs.qq.com/stats?sId=9051096" charset="UTF-8"></script>
</body>

</html>