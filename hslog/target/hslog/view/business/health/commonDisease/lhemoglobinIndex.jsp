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
            <div class="col-sm-12">
                <div class="ibox float-e-margins">
                    <div class="ibox-content">
                        <div class="row">
                            <div class="col-sm-12">
                                <div class="flot-chart" >
                                    <div id="yearx_lhemoglobiny" class="col-sm-12" style="height:500px"></div>
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
    <script src="<%=basePath%>hplus/js/plugins/flot/jquery.flot.js"></script>
    <script src="<%=basePath%>hplus/js/plugins/flot/jquery.flot.tooltip.min.js"></script>
    <script src="<%=basePath%>hplus/js/plugins/flot/jquery.flot.spline.js"></script>
    <script src="<%=basePath%>hplus/js/plugins/flot/jquery.flot.resize.js"></script>
    <script src="<%=basePath%>hplus/js/plugins/flot/jquery.flot.pie.js"></script>
    <script src="<%=basePath%>hplus/js/plugins/flot/jquery.flot.symbol.js"></script>
    <script src="<%=basePath%>hplus/js/plugins/peity/jquery.peity.min.js"></script>
    <script src="<%=basePath%>hplus/js/demo/peity-demo.min.js"></script>
    <script src="<%=basePath%>hplus/js/content.min.js?v=1.0.0"></script>
    <script src="<%=basePath%>hplus/js/plugins/jquery-ui/jquery-ui.min.js"></script>
    <script src="<%=basePath%>hplus/js/plugins/jvectormap/jquery-jvectormap-1.2.2.min.js"></script>
    <script src="<%=basePath%>hplus/js/plugins/jvectormap/jquery-jvectormap-world-mill-en.js"></script>
    <script src="<%=basePath%>hplus/js/plugins/easypiechart/jquery.easypiechart.js"></script>
    <script src="<%=basePath%>hplus/js/plugins/sparkline/jquery.sparkline.min.js"></script>
    <script src="<%=basePath%>hplus/js/demo/sparkline-demo.min.js"></script>
    <script>
        $(document).ready(function(){
        //通过ajax获取echart JSON 进而加载数据
        	$.ajax({//曲线图（X轴：年；Y轴：低血红蛋白；Z轴：性别）
        		type:'post',
        		dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
        		url:'<%=path%>/business/health/basicCharts/commonDisease/getCompletegraph.do',
        		success:function(data){
        			loadEchart(JSON.parse(data.getyearbylhemoglobinandsex),'yearx_lhemoglobiny');
        		}
        	});
            //Echart加载事件
        	function loadEchart(option,id){
        		var visitEchart = echarts.init(document.getElementById(id));
        		visitEchart.setOption(option);
        	}
        });
    </script>
    <script type="text/javascript" src="http://tajs.qq.com/stats?sId=9051096" charset="UTF-8"></script>
</body>

</html>