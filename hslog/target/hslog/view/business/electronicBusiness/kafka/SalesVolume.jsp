<html>
<head>
<meta charset="utf-8">
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
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
<link href="<%=basePath%>hplus/css/bootstrap.min.css?v=3.3.5"
	rel="stylesheet">
<link href="<%=basePath%>hplus/css/font-awesome.min.css?v=4.4.0"
	rel="stylesheet">
<link
	href="<%=basePath%>hplus/css/plugins/bootstrap-table/bootstrap-table.min.css"
	rel="stylesheet">
<link href="<%=basePath%>hplus/css/animate.min.css" rel="stylesheet">
<link href="<%=basePath%>hplus/css/style.min.css?v=4.0.0" rel="stylesheet">
</head>
<body class="gray-bg">

	<div class="ibox-content">
		<div class="row row-lg">
			<div class="col-sm-12">
				<div id="radar" class="col-sm-12" style="height: 500px"></div>
			</div>
		</div>
	</div>



	<script src="<%=basePath%>hplus/js/jquery.min.js?v=2.1.4"></script>
	<script src="<%=basePath%>hplus/js/jquery.form.js"></script>
	<script src="<%=basePath%>js/echarts.min.js"></script>
	<script src="<%=basePath%>hplus/js/jquery.min.js?v=2.1.4"></script>
	<script src="<%=basePath%>hplus/js/jquery.form.js"></script>
	<script src="<%=basePath%>hplus/js/bootstrap.min.js?v=3.3.5"></script>
	<script src="<%=basePath%>hplus/js/content.min.js?v=1.0.0"></script>
	<script
		src="<%=basePath%>hplus/js/plugins/bootstrap-table/bootstrap-table.min.js"></script>
	<script
		src="<%=basePath%>hplus/js/plugins/bootstrap-table/bootstrap-table-mobile.min.js"></script>
	<script
		src="<%=basePath%>hplus/js/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
	<script src="<%=basePath%>hplus/js/demo/bootstrap-table-demo.min.js"></script>
	<script src="http://tajs.qq.com/stats?sId=9051096" charset="UTF-8"></script>
	<script src="<%=basePath%>hplus/js/plugins/layer/layer.js"></script>
	<script src="<%=basePath%>js/jquery.easyui.min.js"></script>
	<script src="<%=basePath%>js/echarts.min.js"></script>
	<script src="<%=basePath%>js/bootstrap-multiselect/bootstrap-multiselect.js"></script>
	<script src="<%=basePath%>util/ajax.js"></script>
	<script src="<%=basePath%>util/global.js"></script>
	<script type="text/javascript"> 
       
	//扩展Date的format方法 
	Date.prototype.format = function(format) {
		var o = {
			"M+" : this.getMonth() + 1,
			"d+" : this.getDate(),
			"h+" : this.getHours(),
			"m+" : this.getMinutes(),
			"s+" : this.getSeconds(),
			"q+" : Math.floor((this.getMonth() + 3) / 3),
			"S" : this.getMilliseconds()
		};
		if (/(y+)/.test(format)) {
			format = format.replace(RegExp.$1, (this.getFullYear() + "")
					.substr(4 - RegExp.$1.length));
		}
		for ( var k in o) {
			if (new RegExp("(" + k + ")").test(format)) {
				format = format.replace(RegExp.$1,
						RegExp.$1.length == 1 ? o[k] : ("00" + o[k])
								.substr(("" + o[k]).length));
			}
		}
		return format;
	}
	/** 
	 *转换日期对象为日期字符串 
	 * @param date 日期对象 
	 * @param isFull 是否为完整的日期数据, 
	 * 为true时, 格式如"2000-03-05 01:05:04" 
	 * 为false时, 格式如 "2000-03-05" 
	 * @return 符合要求的日期字符串 
	 */
	function getSmpFormatDate(date, isFull) {
		var pattern = "";
		if (isFull == true || isFull == undefined) {
			pattern = "yyyy-MM-dd hh:mm:ss";
		} else {
			pattern = "hh:mm:ss";
		}
		return getFormatDate(date, pattern);
	}
	/** 
	 *转换当前日期对象为日期字符串 
	 * @param date 日期对象 
	 * @param isFull 是否为完整的日期数据, 
	 * 为true时, 格式如"2000-03-05 01:05:04" 
	 * 为false时, 格式如 "2000-03-05" 
	 * @return 符合要求的日期字符串 
	 */
	function getSmpFormatNowDate(isFull) {
		return getSmpFormatDate(new Date(), isFull);
	}
	/** 
	 *转换long值为日期字符串 
	 * @param l long值 
	 * @param isFull 是否为完整的日期数据, 
	 * 为true时, 格式如"2000-03-05 01:05:04" 
	 * 为false时, 格式如 "2000-03-05" 
	 * @return 符合要求的日期字符串 
	 */
	function getSmpFormatDateByLong(l, isFull) {
		return getSmpFormatDate(new Date(l), isFull);
	}
	/** 
	 *转换long值为日期字符串 
	 * @param l long值 
	 * @param pattern 格式字符串,例如：yyyy-MM-dd hh:mm:ss 
	 * @return 符合要求的日期字符串 
	 */
	function getFormatDateByLong(l, pattern) {
		return getFormatDate(new Date(l), pattern);
	}
	/** 
	 *转换日期对象为日期字符串 
	 * @param l long值 
	 * @param pattern 格式字符串,例如：yyyy-MM-dd hh:mm:ss 
	 * @return 符合要求的日期字符串 
	 */
	function getFormatDate(date, pattern) {
		if (date == undefined) {
			date = new Date();
		}
		if (pattern == undefined) {
			pattern = "yyyy-MM-dd hh:mm:ss";
		}
		return date.format(pattern);
	}
	
    //获取div id
	var dom = document.getElementById("radar");
    //添加添加图表数据
    var myChart = echarts.init(dom);
    var app = {};
    //图标初始化加载路径
    var url='<%=path%>/salesVolume/SalesVolumLine.do';
 	//定义时间间隔参数
 	var number = 10;
 	var param={"number":number};
 	//成功时的回调函数
 	var sFunc = function(option){
		//图表信息转对象
 		var opt = JSON.parse(option.r2); 
 		//初始化加载时间（10个）
 		opt.xAxis[0].data=(function (){
        	var res = [];
        	var len = 10;
         	var num=0;
            while (len--) {
            	res.unshift((option.r3[num++]));//添加数据
            }
         	return res;
         })();
 		//初始化加载数据
		opt.series[0].data= (function (){
   	 		var res = [];
            var len = 10;
            while (len --) {
            	res.push(option.r1[len]);//添加数据
            }
            return res;
         })();
		//显示每个点的数值
 		opt.series[0].itemStyle={
 			normal: {label : {show: true}}
 		};
 		//向div添加数据
 		myChart.setOption(opt);
        clearInterval(app.timeTicket);
	    //循环获取数据
	    app.timeTicket = setInterval(function (){
	    	//请求路径
	    	var url1='<%=path%>/salesVolume/selectByDate.do';
	    	//请求参数 （图表最新时间）
		 	var param1={"startDate":opt.xAxis[0].data[9]};
	    	//回调函数
		    var sFunc1 = function(value){
	    		//获取series。data
			    var data0 = opt.series[0].data;
		        data0.shift();
	            //对返回数据进行判断并且赋值（数值）
	            if(value.sum==null){
	        		data0.push(0);
	             }else{
	                data0.push(value.sum);
		         }
	        
             	opt.xAxis[0].data.shift();//数据不累加
             	opt.xAxis[0].data.push(value.date);//添加时间
            	//向 div中添加数据
	        	myChart.setOption(opt);
	         };
	 		 ajaxPostWithOutLayer(url1,param1,sFunc1);
   		 },10000);
	 };
    
 	ajaxPostWithOutLayer(url,param,sFunc);

	</script>
</body>
</html>