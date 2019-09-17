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
</head>
<body class="gray-bg">
	
    <div class="wrapper wrapper-content">
		<div class="ibox float-e-margins">
            <div class="ibox-title">
                <h5>曲线图（X轴：年（2009-2015）；Y轴：营养不良率（%）；Z轴：性别）</h5>
                <div class="ibox-tools">
                    <a class="collapse-link">
                        <i class="fa fa-chevron-up"></i>
                    </a>
                </div>
            </div>
            <div class="ibox-content">
                <div class="row row-lg">
                    <div class="col-sm-12">
                        <div id="selectyearx_dystrophiay" class="col-sm-12" style="height:500px"></div>
                        
                    </div>
                </div>
            </div>
        </div>
    </div>
     <div class="wrapper wrapper-content">
		<div class="ibox float-e-margins">
            <div class="ibox-title">
                <h5>饼图（正常、超重、肥胖、营养不良）</h5>
                <div class="ibox-tools">
                    <a class="collapse-link">
                        <i class="fa fa-chevron-up"></i>
                    </a>
                </div>
            </div>
            <div class="ibox-content">
                <div class="row row-lg">
                    <div class="col-sm-12">
                        
                        <div id="selectcake" class="col-sm-12" style="height:500px"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="wrapper wrapper-content">
		<div class="ibox float-e-margins">
            <div class="ibox-title">
                <h5>曲线图（X轴：年龄；Y轴：营养不良率（%），Z轴：性别）</h5>
                <div class="ibox-tools">
                    <a class="collapse-link">
                        <i class="fa fa-chevron-up"></i>
                    </a>
                </div>
            </div>
            <div class="ibox-content">
                <div class="row row-lg">
                    <div class="col-sm-12">
                        <div id="selectagex_dystrophiay" class="col-sm-12" style="height:500px"></div>
                        
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="wrapper wrapper-content">
		<div class="ibox float-e-margins">
            <div class="ibox-title">
                <h5>柱状图（X轴：17地市，Y轴:比率，Z轴：营养不良/肥胖）</h5>
                <div class="ibox-tools">
                    <a class="collapse-link">
                        <i class="fa fa-chevron-up"></i>
                    </a>
                </div>
            </div>
            <div class="ibox-content">
                <div class="row row-lg">
                    <div class="col-sm-12">
                        <div id="selectcityx_thany" class="col-sm-12" style="height:500px"></div>
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
    <script> 
    //通过ajax获取echart JSON 进而加载数据
$(document).ready(function(){
		$.ajax({//曲线图（X轴：年（2009-2015）；Y轴：营养不良率（%）；Z轴：性别
			type:'post',
			dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
			url:'<%=path%>nutritional/selectyearx_dystrophiay.do',
			success:function(data){
				loadEchart(data,'selectyearx_dystrophiay');
			}
		});
	$.ajax({// 饼图（正常、超重、肥胖、营养不良）
		type:'post',
		dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
		url:'<%=path%>nutritional/selectcake.do',
		success:function(data){
			loadEchart(data,'selectcake');
		}
	});
	$.ajax({//曲线图（X轴：年龄；Y轴：营养不良率（%），Z轴：性别）
		type:'post',
		dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
		url:'<%=path%>nutritional/selectagex_dystrophiay.do',
		success:function(data){
			loadEchart(data,'selectagex_dystrophiay');
		}
	});
	$.ajax({//柱状图（X轴：17地市，Y轴:比率，Z轴：营养不良/肥胖）
		type:'post',
		dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
		url:'<%=path%>nutritional/selectcityx_thany.do',
		success:function(data){
			loadEchart(data,'selectcityx_thany');
		}
	});

    //Echart加载事件
	function loadEchart(option,id){
		var visitEchart = echarts.init(document.getElementById(id));
		visitEchart.setOption(option);
	}
});
	</script>
</body>
</html>