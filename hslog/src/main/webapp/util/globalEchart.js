//echart数组全局变量
var resizeEcharts = [];
function GlobalEchartsLoad(option,id){
	var visitEchart = echarts.init(document.getElementById(id));
	
	//判断是否为手机端
	var ua = navigator.userAgent;
	var ipad = ua.match(/(iPad).*OS\s([\d_]+)/),
	    isIphone = !ipad && ua.match(/(iPhone\sOS)\s([\d_]+)/),
	    isAndroid = ua.match(/(Android)\s+([\d.]+)/),
	    isMobile = isIphone || isAndroid;
    if(isMobile) {
		//设置图例标题大小
		option.title.textStyle={};
		option.title.textStyle.fontSize = 12;
		
		//设置图例字体大小
		var len = option.legend.data.length;
		for (var i = 0; i < len; i++){
			option.legend.data[i].textStyle = {
					"font-size":'8px'
			};
		}
		//设置图例上边距
		option.legend.top = 20;
    }
	    
	//生成报表
	visitEchart.setOption(option);
	//每加载一个echart，将echart对象放入全局中
	resizeEcharts.push(visitEchart);
	//设置 ECharts 报表自适应
	window.onresize = function(){
		//遍历echart对象
		$.each(resizeEcharts,function(index,chart){
			chart.resize();
		})
	};
	
}