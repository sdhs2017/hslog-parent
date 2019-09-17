
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

    <title>H+ 后台主题UI框架 - 首页示例二</title>
    <meta name="keywords" content="H+后台主题,后台bootstrap框架,会员中心主题,后台HTML,响应式后台">
    <meta name="description" content="H+是一个完全响应式，基于Bootstrap3最新版本开发的扁平化主题，她采用了主流的左右两栏式布局，使用了Html5+CSS3等现代技术">
	<%String path = "../../";%>
    <link rel="shortcut icon" href="favicon.ico"> <link href="<%=path%>hplus/css/bootstrap.min.css?v=3.3.5" rel="stylesheet">
    <link href="<%=path%>hplus/css/font-awesome.min.css?v=4.4.0" rel="stylesheet">

    <!-- Morris -->
    <link href="<%=path%>hplus/css/plugins/morris/morris-0.4.3.min.css" rel="stylesheet">

    <!-- Gritter -->
    <link href="<%=path%>hplus/js/plugins/gritter/jquery.gritter.css" rel="stylesheet">

    <link href="<%=path%>hplus/css/animate.min.css" rel="stylesheet">
    <link href="<%=path%>hplus/css/style.min.css?v=4.0.0" rel="stylesheet"><base target="_blank">
</head>
<body class="gray-bg">
    <div class="wrapper wrapper-content">
        <div class="row">
            <div class="col-sm-4">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <span id="year" class="label label-success pull-right"></span>
                        <h5>受检人数</h5>
                    </div>
                    <div class="ibox-content" align="center">
                        <h2 id="inspected" class="no-margins"></h2>
                    </div>
                </div>
            </div>
            <div class="col-sm-4">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <span id="yearsymmetry" class="label label-info pull-right"></span>
                        <h5>体型匀称率</h5>
                    </div>
                    <div class="ibox-content" align="center">
                        <h2 id="symmetry" class="no-margins"></h2>
                    </div>
                </div>
            </div>
            <div class="col-sm-4">
                  <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <span id="yearharmful" class="label label-info pull-right"></span>
                        <h5>发育不良率</h5>
                    </div>
                    <div class="ibox-content" align="center">
                        <h2 id="harmful" class="no-margins"></h2>
                    </div>
                </div>
            </div>
          
        </div>
        <div class="row">
            <div class="col-sm-12">
                <div class="ibox float-e-margins">
                    <div class="ibox-content" style="height:250px">
                    	<div class="row">
                    		<div class="col-sm-7">
                    			<div class="flot-chart">
                        			<div id="physiqueLine" class="col-sm-12" style="height:250px"></div>
                    			</div>
                    		</div>
                    		<div class="col-sm-5">
                    			<div class="flot-chart">
                    				<div id="physiquePie" class="col-sm-12" style="height:250px"></div>
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
                    <div class="ibox-content" style="height:250px">
                    	<div class="row">
                    		<div class="col-sm-7">
                    			<div class="flot-chart">
                        			<div id="developmentLine" class="col-sm-12" style="height:250px"></div>
                    			</div>
                    		</div>
                    		<div class="col-sm-5">
                    			<div class="flot-chart">
                    				<div id="developmentPie" class="col-sm-12" style="height:250px"></div>
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
                    	<div class="ibox-content" style="height:250px">
                       		<div id="developmentBar" class="col-sm-12" style="height:250px"></div>
                        </div>
                 	</div>
                </div>
            </div>
        </div>
   
	
    <script src="<%=path%>hplus/js/jquery.min.js?v=2.1.4"></script>
    <script src="<%=path%>hplus/js/jquery.form.js"></script>
    <script src="<%=path%>hplus/js/bootstrap.min.js?v=3.3.5"></script>
    <script src="<%=path%>hplus/js/content.min.js?v=1.0.0"></script>
    <script src="<%=path%>hplus/js/plugins/bootstrap-table/bootstrap-table.min.js"></script>
    <script src="<%=path%>hplus/js/plugins/bootstrap-table/bootstrap-table-mobile.min.js"></script>
    <script src="<%=path%>hplus/js/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
    <script src="<%=path%>hplus/js/demo/bootstrap-table-demo.min.js"></script>
    <script src="http://tajs.qq.com/stats?sId=9051096" charset="UTF-8"></script>
    <script src="<%=path%>hplus/js/plugins/layer/layer.js"></script>
    <script src="<%=path%>js/jquery.easyui.min.js"></script>
    <script src="<%=path%>js/echarts.min.js"></script>
    <script src="<%=path%>js/china.js"></script>
    <script src="<%=path%>util/ajax.js"></script>
    <script src="<%=path%>util/global.js"></script>
    <script>
	//初始化加载
    function getPhysiqueDevelopmentAllChart(){
    	var url='<%=path%>physiquedevelopment/getPhysiqueDevelopmentAllChart.do';
     	//参数
     	var param={"YEAR":"2015"};
     	//成功后回调函数
     	var sFunc = function(data){
     		$("#year").html(data.mapAll.YEAR+"年");
			$("#yearsymmetry").html(data.mapAll.YEAR+"年");
			$("#yearharmful").html(data.mapAll.YEAR+"年");
			$("#inspected").html(data.mapAll.INSPECTED);
			$("#symmetry").html(data.mapAll.SYMMETRY+"%");
			$("#harmful").html(data.mapAll.HARMFUL+"%");
			loadEchart(JSON.parse(data.YearPhysiqueLine),'physiqueLine');
			loadEchart(JSON.parse(data.selectYearDevelopmentLine),'developmentLine');
			loadEchart(JSON.parse(data.selectDevelopmentBar),'developmentBar');
			loadEchart(JSON.parse(data.selectPhysiquePie),'physiquePie');
			loadEchart(JSON.parse(data.selectDevelopmentPie),'developmentPie');
     	};
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,param,sFunc); 
     };
	
    //Echart加载事件
	function loadEchart(option,id){
		var bookEchart = echarts.init(document.getElementById(id));
		bookEchart.setOption(option);
	}
    $(function() { 
    	getPhysiqueDevelopmentAllChart();
    });
    </script>
</body>
</html>