
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
    <link href="<%=path%>hplus/css/style.min.css?v=4.0.0" rel="stylesheet">
	<link href="<%=path%>css/easyui.css" rel="stylesheet">
	<link href="<%=path%>js/bootstrap-multiselect/bootstrap-multiselect.css" rel="stylesheet">
	<style type="text/css">
		.fixed-table-header{
			height:110px;
		}
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
		<div class="ibox float-e-margins">
            <div class="ibox-title">
                <h5>眼科基础数据查询</h5>
                <div class="ibox-tools">
                    <a class="collapse-link">
                        <i class="fa fa-chevron-up"></i>
                    </a>
                </div>
            </div>
            <div class="ibox-content">
                <div class="row row-lg">
                    <div class="col-sm-12">
                        <div class="example">
                        <div class="row">
                        	<div class="col-sm-4 m-b-xs">
	                        	
                        	</div>
                        	<div class="col-sm-8">
	                        	<form class="form-inline" role="form">
		                                <input id="confines" class="easyui-combotree" style="width:150px;height:34px;"></input>
	                                    <select id="year"></select>
	                                    <select id="sex" multiple="multiple"></select>
	                                    <select id="count"></select>
				                	<!-- <button type="submit" class="btn btn-primary" style="margin-top:8px">搜索</button> -->
				                	 <a class="btn btn-primary" style=" margin-top:5px;" onclick="DataAll()"><i class="glyphicon glyphicon-check"></i>&nbsp;&nbsp;查询</a>
				            	</form>
                        	</div>
                        </div>
                        
                            <table id="OphthalmologyTable" data-toggle="table" data-height="579" data-pagination="true"  data-mobile-responsive="true">
                                <thead>
                                    <tr>
                                    	<th data-field="AGE_GRADE" rowspan="3" data-align="center" data-valign="middle" >年龄</th>
                                    	<th data-field="SEX" rowspan="3" data-align="center" data-valign="middle">性别</th>
                                    	<th data-field="INSPECTED" rowspan=3 data-align="center" data-valign="middle">受检人数</th>
                                        <th rowspan=2 colspan=2 data-align="center" data-valign="middle">视力正常</th>
                                        <th colspan=8 data-align="center" data-valign="middle">视力不良</th>
                                        <th rowspan=2 colspan=2 data-align="center" data-valign="middle">色盲色弱</th>
                                        <th rowspan=2 colspan=2 data-align="center" data-valign="middle">沙眼</th>
                                        <th colspan=2 rowspan=2 data-align="center" data-valign="middle">结膜炎</th>
                                    </tr>
                                    <tr>
                                    	<th  colspan=2 data-align="center" data-valign="middle">4.9</th>
                                    	<th  colspan=2 data-align="center" data-valign="middle">4.6-4.8</th>
                                    	<th  colspan=2 data-align="center" data-valign="middle">4.5以下</th>
                                    	<th  colspan=2 data-align="center" data-valign="middle">合计</th>
                                    </tr>
                                    <tr>
                                    	<th data-field="NORMAL" data-align="center">人数</th>
                                    	<th data-field="NORMALPERCENT" data-align="center">%</th>
                                    	<th data-field="V49" data-align="center">人数</th>
                                    	<th data-field="V49PERCENT" data-align="center">%</th>
                                    	<th data-field="V4648" data-align="center">人数</th>
                                    	<th data-field="V4648PERCENT" data-align="center">%</th>
                                    	<th data-field="V45" data-align="center">人数</th>
                                    	<th data-field="V45PERCENT" data-align="center">%</th>
                                    	<th data-field="POORSIGHT" data-align="center">人数</th>
                                    	<th data-field="POORSIGHTPERCENT" data-align="center">%</th>
                                    	<th data-field="BLINDHYPOCH" data-align="center">人数</th>
                                    	<th data-field="BLINDHYPOCHPERCENT" data-align="center">%</th>
                                    	<th data-field="TRACHOMA" data-align="center">人数</th>
                                    	<th data-field="TRACHOMAPERCENT" data-align="center">%</th>
                                    	<th data-field="CONJUNCTIVITIS" data-align="center">人数</th>
                                    	<th data-field="CONJUNCTIVITISPERCENT" data-align="center">%</th>
                                    </tr>
                                </thead>
                            </table>
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
    <script src="<%=path%>js/bootstrap-multiselect/bootstrap-multiselect.js"></script>
    <script src="<%=path%>util/ajax.js"></script>
    <script src="<%=path%>util/global.js"></script>
    <script> 
    //初始化加载事件
    function getYearOrZipcode(){
    	var url = '<%=path%>ophth/getYearOrZipcode.do';
    	//成功后回调函数
    	var sFunc = function(data){
   		//加载数据
   		//加载年
   		$('#year').multiselect('dataprovider',JSON.parse(data.mapYear));
   		//获取范围
		$('#confines').combotree("loadData",JSON.parse(data.mapProvinceCityDistrict));
		$('#confines').combotree('setValue',$('#confines').combotree('tree').tree('getRoots')[0].id);
		DataAll();
    	};
    	//获取数据并通过回调函数进行数据加载。
    	ajaxPost(url,{},sFunc);
    }
    
    //加载事件
    function DataAll(){
    	var url='<%=path%>ophth/selectDataAllByString.do';
   	   

    	var ZIPCODE=$("#confines").combotree('getValue');
     	
     	var SEX="";
    	$("#sex option:selected").each(function(){
    		SEX+=$(this).val()+',';
    	});
     	var YEAR=$("#year option:selected").val();
    	var COUNT=$("#count option:selected").val();
    	
    	if(COUNT=="AGE"){
    		$('#OphthalmologyTable').children('thead').eq(0).children('tr').eq(0).children('th').eq(0).children('.th-inner').text('年龄');
    	}
    	if(COUNT=="GRADE"){
    		$('#OphthalmologyTable').children('thead').eq(0).children('tr').eq(0).children('th').eq(0).children('.th-inner').text('年级');
    	}
     	//参数
     	var param={"ZIPCODE":ZIPCODE,"COUNT":COUNT,"SEX":SEX,"YEAR":YEAR};
     	//成功后回调函数
     	var sFunc = function(data){
     		$("#OphthalmologyTable").bootstrapTable("load", data);
     	}
     	//获取数据并通过回调函数进行数据加载。
     	ajaxPost(url,param,sFunc); 
    }
    //加载数据
	$(function(){ 
		//调整全选时，选项全部显示
		adjustButton('sex');
		//调整按钮大小
		$('#year').multiselect({
			buttonWidth:'150px'
			});
		$('#count').multiselect({
			buttonWidth:'150px'
			});
		//加载性别
		$('#sex').multiselect('dataprovider',sex);
		//加载统计方式
		$('#count').multiselect('dataprovider',countway);
		//初始化数据
		getYearOrZipcode();
	});
	</script>
</body>
</html>