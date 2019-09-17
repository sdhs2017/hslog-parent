
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
				       	  <input id="Line_confines" class="easyui-combotree" style="width:150px;height:34px;"></input>
				       	  <select id="Line_year"></select>
					     <select id="Line_sex" multiple="multiple"></select>
					     <select id="Line_count"></select>
					     <a class="btn btn-primary" style=" margin-top:5px;" onclick="PoorsighLine()"><i class="glyphicon glyphicon-check"></i>&nbsp;&nbsp;查询</a>
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
					  		   <select id="age_sex" multiple="multiple"></select>
					  		    <select id="age_tooth"></select>
					  		  <a class="btn btn-primary" style=" margin-top:5px;" onclick="PoorsighLineage()"><i class="glyphicon glyphicon-check"></i>&nbsp;&nbsp;查询</a>
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
					     <select id="bar_sex" multiple="multiple"></select>
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
    <script> 
    
    //点第一个曲线图
    function PoorsighLine(){
    	var ZIPCODE=$("#Line_confines").combotree('getValue');
    	var YEAR="";
    	$("#Line_year option:selected").each(function(){
    		YEAR+=$(this).val();
    	});
    	var COUNT="";
    	$("#Line_count option:selected").each(function(){
    		COUNT+=$(this).val();
    	});
    	var SEX="";
    	$("#Line_sex option:selected").each(function(){
    		SEX+=$(this).val()+',';
    	});
    	 $.ajax({
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			data:{"ZIPCODE":ZIPCODE,"SEX":SEX,"YEAR":YEAR,"COUNT":COUNT},
			url:'<%=path%>tooth/selectPeriodontosisCariesByString.do',
			success:function(data){
				loadEchart(data,'PoorsighLine');
			}
		});
    	
    };
    //点击第二个曲线图
    function PoorsighLineage(){
    	var ZIPCODE=$("#age_confines").combotree('getValue');
    	var YEAR="";
    	$("#age_year option:selected").each(function(){
    		YEAR+=$(this).val();
    	});
    	var SEX="";
    	$("#age_sex option:selected").each(function(){
    		SEX+=$(this).val()+',';
    	});
    	var TOOTH="";
    	$("#age_tooth option:selected").each(function(){
    		TOOTH+=$(this).val();
    	});
    	 $.ajax({
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			data:{"ZIPCODE":ZIPCODE,"YEAR":YEAR,"TOOTH":TOOTH,"SEX":SEX},
			url:'<%=path%>tooth/selectPerDecPieByString.do',
			success:function(data){
				loadEchart(data,'PoorsighLineage');
			}
		});
    	
    };
  //点击柱状图
    function PoorsighBar(){
     	var ZIPCODE=$("#bar_confines").combotree('getValue');
     	var GRADE="";
     	$("#bar_Grade option:selected").each(function(){
     		GRADE+=$(this).val()+',';
     	});
     	var YEAR="";
     	$("#bar_year option:selected").each(function(){
     		YEAR+=$(this).val();
     	});
     	var SEX="";
    	$("#bar_sex option:selected").each(function(){
    		SEX+=$(this).val()+',';
    	});
     	 $.ajax({
 			type:'post',
 			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
 			data:{"ZIPCODE":ZIPCODE,"GRADE":GRADE,"YEAR":YEAR,"SEX":SEX},
 			url:'<%=path%>tooth/selectPerDecBarByString.do',
 			success:function(data){
 				loadEchart(data,'PoorsighBar');
 			}
 		});
     };
    //Echart加载事件
	function loadEchart(option,id){
		var bookEchart = echarts.init(document.getElementById(id));
		bookEchart.setOption(option);
	};
	function getCombocox(){
		$.ajax({
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			url:'<%=path%>ophth/getCombocox.do',
			success:function(data){
				$('#Line_Grade').combobox("loadData",data);
			}
		});
	}
	//第一个曲线图获取范围
	function getMoreCity(){
		$.ajax({
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			url:'<%=path%>ophth/getProvinceCityDistrict.do',
			success:function(data){
				$('#Line_confines').combotree("loadData",JSON.parse(data.mapBesides));
				var t = $('#Line_confines').combotree('tree');//获取tree对象
				roots=t.tree('getRoots');//获取根节点
				$('#Line_confines').combotree('setValue',roots[0].id);
			}
		});
	}
	 //第二个曲线图获取范围
	function getMoreCityage(){
		$.ajax({
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			url:'<%=path%>ophth/getProvinceCityDistrict.do',
			success:function(data){
				$('#age_confines').combotree("loadData",JSON.parse(data.mapBesides));
				var t = $('#age_confines').combotree('tree');//获取tree对象
				roots=t.tree('getRoots');//获取根节点
				$('#age_confines').combotree('setValue',roots[0].id);
			}
		});
	}
	 //第三个柱状图获取范围
	 function getMoreCitybar(){
		$.ajax({
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			url:'<%=path%>ophth/getProvinceCity.do',
			success:function(data){
				$('#bar_confines').combotree("loadData",JSON.parse(data.mapBesides));
				var t = $('#bar_confines').combotree('tree');//获取tree对象
				roots=t.tree('getRoots');//获取根节点
				$('#bar_confines').combotree('setValue',roots[0].id);
			}
		});
	}
	 //加载年柱状图
	function getBarYearAll(){
		$.ajax({
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			url:'<%=path%>ophth/getYearAll.do',
			success:function(data){
				$('#bar_year').multiselect('dataprovider',data);
			}
		});
	}
	 //加载年第二个曲线图
	function getBarYearAllage(){
		$.ajax({
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			url:'<%=path%>ophth/getYearAll.do',
			success:function(data){
				$('#age_year').multiselect('dataprovider',data);
			}
		});
	}
	 //加载年第一个曲线图
	function getLineYearAllage(){
		$.ajax({
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			url:'<%=path%>ophth/getYearAll.do',
			success:function(data){
				$('#Line_year').multiselect('dataprovider',data);
			}
		});
	}
    //加载图表数据
	$(function() {
		
		//第一个曲线图获取范围
		getMoreCity();
		//第二个饼图获取范围
		getMoreCityage();
		//第三个柱状图获取范围
		getMoreCitybar();
		//加载年柱状图
		getBarYearAll();
		//加载年曲线图
		getLineYearAllage();
		//加载年饼图
		getBarYearAllage();
		//调整按钮大小
		$('#Line_Grade').multiselect({
			buttonWidth:'150px',
		    buttonText: function(options) {
                if (options.length === 0) {
                    return 'None selected';
                }
                else {
                    var selected = '';
                    options.each(function() {
                    	  var label = ($(this).attr('label') !== undefined) ? $(this).attr('label') : $(this).html();
                    	   selected += label + ', ';
                    	});
                    return selected.substr(0, selected.length -2);
                }
            }
        });
		$('#Line_year').multiselect({
			buttonWidth:'150px'
		});
		$('#Line_sex').multiselect({
			buttonWidth:'150px',
			buttonText: function(options) {
	                if (options.length === 0) {
	                    return 'None selected';
	                }
	                else {
	                    var selected = '';
	                    options.each(function() {
	                    	  var label = ($(this).attr('label') !== undefined) ? $(this).attr('label') : $(this).html();
	                    	   selected += label + ', ';
	                    	});
	                    return selected.substr(0, selected.length -2);
	                }
	            }
		});
		$('#Line_count').multiselect({
			buttonWidth:'150px'
		});
		$('#age_year').multiselect({
			buttonWidth:'150px'
		});
		$('#age_tooth').multiselect({
			buttonWidth:'150px'
		});
		$('#age_sex').multiselect({
			buttonWidth:'150px',
			buttonText: function(options) {
	                if (options.length === 0) {
	                    return 'None selected';
	                }
	                else {
	                    var selected = '';
	                    options.each(function() {
	                    	  var label = ($(this).attr('label') !== undefined) ? $(this).attr('label') : $(this).html();
	                    	   selected += label + ', ';
	                    	});
	                    return selected.substr(0, selected.length -2);
	                }
	            }
		});
		$('#bar_Grade').multiselect({
			buttonWidth:'150px',
			 buttonText: function(options) {
	                if (options.length === 0) {
	                    return 'None selected';
	                }
	                else {
	                    var selected = '';
	                    options.each(function() {
	                    	  var label = ($(this).attr('label') !== undefined) ? $(this).attr('label') : $(this).html();
	                    	   selected += label + ', ';
	                    	});
	                    return selected.substr(0, selected.length -2);
	                }
	            }
		});
		$('#bar_year').multiselect({
			buttonWidth:'150px'
		});
		$('#bar_sex').multiselect({
			buttonWidth:'150px',
			buttonText: function(options) {
	                if (options.length === 0) {
	                    return 'None selected';
	                }
	                else {
	                    var selected = '';
	                    options.each(function() {
	                    	  var label = ($(this).attr('label') !== undefined) ? $(this).attr('label') : $(this).html();
	                    	   selected += label + ', ';
	                    	});
	                    return selected.substr(0, selected.length -2);
	                }
	            }
		});
		//年级
		var option = [
		               {label: '小学',title: '小学',value: '1',selected: true},
		               {label: '初中', title: '初中',value: '2',selected: true},
		               {label: '高中',title: '高中',value: '3',selected: true}
		    	            ];
		//性别
		var optionsex  = [
		               {label: '男', title: '男',value: '1',selected: true},
		               {label: '女', title: '女',value: '0',selected: true}
		    	            ];
		//统计方式
		var optioncount  = [
		               {label: '年龄', title: '年龄',value: 'AGE',selected: true},
		               {label: '年级', title: '年级',value: 'GRADE'}
		    	            ];
		//恒牙乳牙
		var optiontooth  = [
		               {label: '恒牙龋齿', title: '恒牙龋齿',value: 'PER',selected: true},
		               {label: '乳牙龋齿', title: '乳牙龋齿',value: 'DEC'}
		    	            ];
		
		//第一个曲线图加载统计方式
		$('#Line_count').multiselect('dataprovider',optioncount);
		//第一个曲线图加载性别
		$('#Line_sex').multiselect('dataprovider',optionsex);
		//第二个曲线图加载统计方式
		$('#age_sex').multiselect('dataprovider',optionsex);
		//第二个曲线图加载恒牙乳牙
		$('#age_tooth').multiselect('dataprovider',optiontooth);
		//柱状图加载年级
		$('#bar_Grade').multiselect('dataprovider',option);
		//柱状图加载性别
		$('#bar_sex').multiselect('dataprovider',optionsex);
		//页面加载
		PoorsighLine();
		PoorsighLineage();
		PoorsighBar();
	}); 
	</script>
</body>
</html>