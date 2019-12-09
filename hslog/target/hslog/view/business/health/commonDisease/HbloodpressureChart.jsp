
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
    <link href="<%=basePath%>hplus/css/style.css?v=4.0.0" rel="stylesheet">
	<link href="<%=basePath%>css/easyui.css" rel="stylesheet">
	<link href="<%=basePath%>js/bootstrap-multiselect/bootstrap-multiselect.css" rel="stylesheet">
	<style type="text/css">
		.textbox .textbox-text{
			font-size:16px;
			text-align:center;
		}
		.textbox{
			border-radius:3.5px 3.5px 3.5px 3.5px;
		}
		.tree-title{
			font-size:16px;
		}
		.btn-group{
			padding-top:5px;
		}
	</style>
</head>

<body class="gray-bg">
    
     <div class="wrapper wrapper-content">
        <div class="row">
            <div class="col-sm-12">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
				       	  <input id="Line_confines" class="easyui-combotree" style="width:150px;height:34px;"></input>
					     <select id="Line_Grade" multiple="multiple"></select>
					     <a id='Line' class="btn btn-primary" style=" margin-top:5px;" onclick="PoorsighLine()"><i class="glyphicon glyphicon-check"></i>&nbsp;&nbsp;查询</a>
                    </div>
                    <div class="ibox-content">
                        <div class="row">
	                        <div id="PoorsighLine" class="col-sm-12" style="height:250px"></div>
                    	</div>
                    </div>
                </div>
            </div>
        </div>
        
		<div class="row">
                <div class="col-sm-12">
                	<div class="ibox float-e-margins">
                		 <div class="ibox-title">
                        	 <input id="age_confines" class="easyui-combotree" style="width:150px;height:34px;"></input>
					     	  <select id="age_year"></select>
					  		   <select id="age_count"></select>
					  		  <a id='age' class="btn btn-primary" style=" margin-top:5px;" onclick="PoorsighLineage()"><i class="glyphicon glyphicon-check"></i>&nbsp;&nbsp;查询</a>
                    	</div>
                    	<div class="ibox-content" style="height:250px">
                       		<div id="PoorsighLineage" class="col-sm-12" style="height:250px"></div>
                        </div>
                 	</div>
                </div>
         </div> 
          <div class="row">
                <div class="col-sm-12">
                <div class="ibox float-e-margins">
                	 <div class="ibox-title">
                	 <input id="bar_confines" class="easyui-combotree" style="width:150px;height:34px;"></input>
					     <select id="bar_Grade" multiple="multiple"></select>
					     <select id="bar_year"></select>
					     <a class="btn btn-primary" style=" margin-top:5px;" onclick="PoorsighBar()"><i class="glyphicon glyphicon-check"></i>&nbsp;&nbsp;查询</a>
                    </div>
                	<div class="ibox float-e-margins">
                    	<div class="ibox-content" style="height:250px">
                       		<div id="PoorsighBar" class="col-sm-12" style="height:250px"></div>
                        </div>
                 	</div>
                </div>
                </div>
         </div> 
        </div>
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
    <script src="<%=basePath%>js/china.js"></script>
    <script src="<%=basePath%>js/bootstrap-multiselect/bootstrap-multiselect.js"></script>
    <script src="<%=basePath%>util/ajax.js"></script>
    <script src="<%=basePath%>util/global.js"></script> 
    <script> 
    
    //点击第一个曲线图
    function PoorsighLine(){
    	var url='<%=path%>/business/health/basicCharts/commonDisease/selectYearHbloodpressureByString.do';
    	var ZIPCODE=$("#Line_confines").combotree('getValue');
    	var GRADE="";
    	$("#Line_Grade option:selected").each(function(){
    		GRADE+=$(this).val()+',';
    	});
    	//参数
    	var param={"ZIPCODE":ZIPCODE,"GRADE":GRADE};
    	//成功后回调函数
    	var sFunc = function(data){
    		loadEchart(data,'PoorsighLine');
    	};
    	//获取数据并通过回调函数进行数据加载。
    	ajaxPost(url,param,sFunc); 
    };
    //点击第二个曲线图
    function PoorsighLineage(){
		var url='<%=path%>/business/health/basicCharts/commonDisease/selectAgeOrGradeHbloodpressureByString.do';
    	
    	var ZIPCODE=$("#age_confines").combotree('getValue');
    	var YEAR=$("#age_year option:selected").val();
    	var COUNT=$("#age_count option:selected").val();
    	//参数
    	var param={"ZIPCODE":ZIPCODE,"YEAR":YEAR,"COUNT":COUNT};
    	//成功后回调函数
    	var sFunc = function(data){
    		loadEchart(data,'PoorsighLineage');
    	}
    	//获取数据并通过回调函数进行数据加载。
    	ajaxPost(url,param,sFunc);
    };
  //点击柱状图
    function PoorsighBar(){
    	var url='<%=path%>/business/health/basicCharts/commonDisease/selectZipcodeHbloodpressureByString.do';
 	   
    	var ZIPCODE=$("#bar_confines").combotree('getValue');
    	var GRADE="";
     	$("#bar_Grade option:selected").each(function(){
     		GRADE+=$(this).val()+',';
     	});
     	var YEAR=$("#bar_year option:selected").val();
     	//参数
     	var param={"ZIPCODE":ZIPCODE,"GRADE":GRADE,"YEAR":YEAR};
     	//成功后回调函数
     	var sFunc = function(data){
     		loadEchart(data,'PoorsighBar');
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,param,sFunc);
     };
    //Echart加载事件
	function loadEchart(option,id){
		var bookEchart = echarts.init(document.getElementById(id));
		bookEchart.setOption(option);
	};
	//初始化加载事件
    function getYearOrZipcode(){
    	var url = '<%=path%>/ophth/getYearOrZipcode.do';
    	//成功后回调函数
    	var sFunc = function(data){
   		//加载数据
   		//加载年第二个曲线图
   		$('#age_year').multiselect('dataprovider',JSON.parse(data.mapYear));
   		//加载年柱状图
   		$('#bar_year').multiselect('dataprovider',JSON.parse(data.mapYear));
   		//第一个曲线图获取范围
		$('#Line_confines').combotree("loadData",JSON.parse(data.mapProvinceCityDistrict));
		$('#Line_confines').combotree('setValue',$('#Line_confines').combotree('tree').tree('getRoots')[0].id);
		//第二个曲线图获取范围
		$('#age_confines').combotree("loadData",JSON.parse(data.mapProvinceCityDistrict));
		$('#age_confines').combotree('setValue',$('#age_confines').combotree('tree').tree('getRoots')[0].id);
		//第三个柱状图获取范围
		$('#bar_confines').combotree("loadData",JSON.parse(data.mapProvinceCity));
		$('#bar_confines').combotree('setValue',$('#bar_confines').combotree('tree').tree('getRoots')[0].id);
		
		//设置默认值
		var ZIPCODE=$("#bar_confines").combotree('getValue');
     	var GRADE="";
     	$("#bar_Grade option:selected").each(function(){
     		GRADE+=$(this).val()+',';
     	});
     	var YEAR=$("#bar_year option:selected").val();
     	
     	var COUNT=$("#age_count option:selected").val();
    	
    	
    	var url='<%=path%>/business/health/basicCharts/commonDisease/getHbloodpressureAllChart.do';
    	var param={"ZIPCODE":ZIPCODE,"GRADE":GRADE,"YEAR":YEAR,"COUNT":COUNT};
    	//取值
    	var sFunc=function(data){
    		loadEchart(JSON.parse(data.YearHbloodpressureByString),'PoorsighLine');
    		loadEchart(JSON.parse(data.AgeOrGradeHbloodpressureByString),'PoorsighLineage');
    		loadEchart(JSON.parse(data.ZipcodeHbloodpressureByString),'PoorsighBar');
    	}
		//构建ajax提交相关参数  url param successFunction...
		//ajaxPost(....)
    	ajaxPost(url,param,sFunc);
    	};
    	//获取数据并通过回调函数进行数据加载。
    	ajaxPost(url,{},sFunc);
    }
    //加载图表数据
	$(function() {
		//调整全选时，选项全部显示
		adjustButton('Line_Grade');
		adjustButton('bar_Grade');
		//调整按钮大小
		$('#age_year').multiselect({
			buttonWidth:'150px'
		});
		$('#age_count').multiselect({
			buttonWidth:'150px'
		});
		$('#bar_year').multiselect({
			buttonWidth:'150px'
		});
		//第一个曲线图加载年级
		$('#Line_Grade').multiselect('dataprovider',grade);
		//第二个曲线图加载统计方式
		$('#age_count').multiselect('dataprovider',countway);
		//柱状图加载年级
		$('#bar_Grade').multiselect('dataprovider',grade);
		
		//页面加载
		getYearOrZipcode();
	}); 
	</script>
</body>
</html>