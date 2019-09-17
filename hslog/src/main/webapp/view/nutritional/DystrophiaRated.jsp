
<html>

<head>

    <meta charset="utf-8">
    <%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %> 
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<%String path = "../../";%>
    <link rel="shortcut icon" href="favicon.ico"> 
    <link href="<%=path%>hplus/css/bootstrap.min.css?v=3.3.5" rel="stylesheet">
    <link href="<%=path%>hplus/css/font-awesome.min.css?v=4.4.0" rel="stylesheet">
    <link href="<%=path%>hplus/css/plugins/bootstrap-table/bootstrap-table.min.css" rel="stylesheet">
    <link href="<%=path%>hplus/css/animate.min.css" rel="stylesheet">
    <link href="<%=path%>hplus/css/style.css?v=4.0.0" rel="stylesheet">
	<link href="<%=path%>css/easyui.css" rel="stylesheet">
	<link href="<%=path%>js/bootstrap-multiselect/bootstrap-multiselect.css" rel="stylesheet">
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
				       	  <input id="pie_confines" class="easyui-combotree" style="width:150px;height:34px;"></input>
					     <select id="pie_Grade" multiple="multiple"></select>
					   <select id="pie_sex" multiple="multiple"></select>
					     <select id="pie_year"></select>
					     <a class="btn btn-primary" style=" margin-top:5px;" onclick="PoorsighPie()"><i class="glyphicon glyphicon-check"></i>&nbsp;&nbsp;查询</a>
                    </div>
                    <div class="ibox-content">
                    	<div class="row">
                        	<div id="PoorsighPie" class="col-sm-12" style="height:450px"></div>
                    	</div>
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
    <script src="<%=path%>js/bootstrap-multiselect/bootstrap-multiselect.js"></script>
    <script src="<%=path%>util/ajax.js"></script>
    <script src="<%=path%>util/global.js"></script>
    <script> 
    
   //点击饼图
   function PoorsighPie(){
	    var url='<%=path%>nutritional/selectDystrophiayPieByString.do';
	    var ZIPCODE=$("#pie_confines").combotree('getValue');
    	var GRADE="";
    	$("#pie_Grade option:selected").each(function(){
    		GRADE+=$(this).val()+',';
    	});
    	var SEX="";
    	$("#pie_sex option:selected").each(function(){
    		SEX+=$(this).val()+',';
    	});
    	var YEAR=$("#pie_year option:selected").val();
	   	//参数
	   	var param={"ZIPCODE":ZIPCODE,"GRADE":GRADE,"SEX":SEX,"YEAR":YEAR};
	   	//成功后回调函数
	   	var sFunc = function(data){
	   		loadEchart(data,'PoorsighPie');
	   	};
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
    	var url = '<%=path%>ophth/getYearOrZipcode.do';
    	//成功后回调函数
    	var sFunc = function(data){
   		//加载数据
   		//加载年饼图
   		$('#pie_year').multiselect('dataprovider',JSON.parse(data.mapYear));
		//饼图获取范围
		$('#pie_confines').combotree("loadData",JSON.parse(data.mapProvinceCityDistrict));
		$('#pie_confines').combotree('setValue',$('#pie_confines').combotree('tree').tree('getRoots')[0].id);
		
		//设置默认值
		var url='<%=path%>nutritional/selectDystrophiayPieByString.do';
	    var ZIPCODE=$("#pie_confines").combotree('getValue');
    	var GRADE="";
    	$("#pie_Grade option:selected").each(function(){
    		GRADE+=$(this).val()+',';
    	});
    	var SEX="";
    	$("#pie_sex option:selected").each(function(){
    		SEX+=$(this).val()+',';
    	});
    	var YEAR=$("#pie_year option:selected").val();
	   	//参数
	   	var param={"ZIPCODE":ZIPCODE,"GRADE":GRADE,"SEX":SEX,"YEAR":YEAR};
	   	//成功后回调函数
	   	var sFunc = function(data){
	   		loadEchart(data,'PoorsighPie');
	   	};
	   	//获取数据并通过回调函数进行数据加载。
	   	ajaxPost(url,param,sFunc); 
    	};
    	//获取数据并通过回调函数进行数据加载。
    	ajaxPost(url,{},sFunc);
    }
    //加载图表数据
	$(function() {
		//调整全选时，选项全部显示
		adjustButton('pie_Grade');
		adjustButton('pie_sex');
		//调整按钮大小
		$('#pie_year').multiselect({
			buttonWidth:'100px'
		});
		//饼图加载年级
		$('#pie_Grade').multiselect('dataprovider',grade);
		//饼图加载性别
		$('#pie_sex').multiselect('dataprovider',sex);
		
		
		//页面加载
		getYearOrZipcode();
	
	}); 
	</script>
</body>
</html>