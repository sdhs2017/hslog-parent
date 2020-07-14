<template>
    <div class="content-bg">
        <div  id="chart"></div>
    </div>

</template>

<script>
    import {dateFormat} from "../../../static/js/common";
    const echarts = require('echarts');
    export default {
        name:'customChart',
        data() {
            return {}
        },
        mounted() {
            let  data = [
                {
                    machineName:'主机-帐户被注销',
                    machineId:'968545',
                    startTime:'2020-07-07 20:08:00',
                    endTime:'2020-07-07 22:00:00',
                },
                {
                    machineName:'主机-成功登录帐户',
                    machineId:'1q1q1q0',
                    startTime:'2020-07-06 08:50:00',
                    endTime:'2020-07-06 10:00:00',
                },
                {
                    machineName:'主机-分配给新的登录特权',
                    machineId:'1q1q1q',
                    startTime:'2020-07-07 00:00:00',
                    endTime:'2020-07-07 10:00:00',
                },
                {
                    machineName:'主机-开启新的会话窗口',
                    machineId:'1q1q1q3',
                    startTime:'2020-07-06 10:10:00',
                    endTime:'2020-07-07 20:40:10',
                },
                {
                    machineName:'流量-http错误访问，code：404',
                    machineId:'1q1q1q120',
                    startTime:'2020-07-06 08:08:00',
                    endTime:'2020-07-06 12:00:00',
                },
                {
                    machineName:'流量-数据库高危操作-delete',
                    machineId:'289463',
                    startTime:'2020-07-07 08:08:00',
                    endTime:'2020-07-07 12:00:00',
                },
                {
                    machineName:'流量-数据库高危操作-drop',
                    machineId:'8958958',
                    startTime:'2020-07-06 13:08:00',
                    endTime:'2020-07-06 15:00:00',
                },
                {
                    machineName:'流量-数据库中危操作-update',
                    machineId:'176862',
                    startTime:'2020-07-06 20:08:00',
                    endTime:'2020-07-06 24:00:00',
                },
                {
                    machineName:'指标-CPU使用率超过91%',
                    machineId:'475868',
                    startTime:'2020-07-06 02:08:00',
                    endTime:'2020-07-06 04:00:00',
                },
                {
                    machineName:'流量-带宽访问量异常',
                    machineId:'238110',
                    startTime:'2020-07-06 00:08:00',
                    endTime:'2020-07-06 10:00:00',
                },
                {
                    machineName:'主机-暴力破解：failed password',
                    machineId:'7819545',
                    startTime:'2020-07-06 01:08:00',
                    endTime:'2020-07-06 04:00:00',
                },
                {
                    machineName:'主机-通过ssh方式进行操作',
                    machineId:'1q1q1q2',
                    startTime:'2020-07-06 04:00:00',
                    endTime:'2020-07-06 08:00:00',
                },
            ]

            var o = this.calculateDataChart(data);//将后台数据处理成echart需要的数据
            console.log(o)
            this.loadChart(o,new Date('2020/07/06'),new Date('2020/07/08'),o.max)//生成图表
        },
        methods: {
            /* 获取数据 */
            getData() {

            },
            /* 处理数据 */
            calculateDataChart(list) {
                var data = [];
                var machineMap = {};
                var machines = [];
                var colorList = ["#588d81",  "#1871c6", "#e18213", "#28309e"];//颜色列表，前五个机器固定颜色
                list.forEach((item, n) => {
                    var color = "";
                    if (machineMap.hasOwnProperty(item.machineId)) {
                        color = machineMap[item.machineId];
                    } else {
                        if (colorList.length > 0) {
                            color = colorList.shift();
                        } else {
                            color = this.getRandomColor();//颜色用完后，后面的机器随机颜色
                        }
                        machines.push({name: item.machineName, color: color});
                        machineMap[item.machineId] = color;
                    }
                    var index = this.dealOverlay(data, item);//获得色块的y轴起始位置，核心代码
                    data.push({
                        name: item.machineName,
                        machineId: item.machineId,
                        startTime: item.startTime,
                        endTime: item.endTime,
                        value: [
                            index,
                            item.startTime,
                            item.endTime,
                        ],
                        itemStyle: {
                            normal: {
                                color: color
                            }
                        },
                    });
                })
                return {
                    machines: machines,
                    data: data,
                };
            },
            //oldObject 已经被计算过确定位置的色块
            //n 新加入的色块
            dealOverlay(oldObject, n) {
                let data = $.grep(oldObject, function (o, i) {//首先过滤掉时间轴不会重复的老色块，因为不会影响新色块的纵坐标位置
                    if (new Date(n.startTime) > new Date(o.value[2]) || new Date(n.endTime) < new Date(o.value[1]))
                        return false;
                    return true;
                });
                var index = 0.2;
                index = this.overlay(data, n, index);//重叠算法计算新色块的高度
                //console.log(index)
                return index;
            },
            overlay(data, n, index) {//重叠算法
                let k = 1;
                $.each(data,  (i, o)=> {//o 某色块对象
                    var value = o.value;
                    //index起始位置大于该色块的最大高度，或者新的色块最大高度小于该色块的起始位置，也就是不重复
                    if (index > value[0] + 0.2 || index + 0.2 < value[0]) {
                        return true;
                    } else {//如果重复了，那么将index改成该色块的最大高度加上一个固定高度，然后再重新遍历计算
                        //注意，index改变后必须重新遍历一遍，因为之前index不重叠的色块，index改变后有可能会重叠
                        index = this.overlay(data, n, value[0] + 0.2 + 0.2);
                        return false;
                    }
                    /* 	index = index+ 0.1*k;
				k++; */
                });

                return index;
            },
            getRandomColor(){

                return '#'+(Math.random()*0xffffff<<0).toString(16);

            },
            //加载echart数据
            loadChart(obj, startTime, endTime, max) {
                var machines = obj.machines;
                let data = obj.data;
                var duration = (endTime - startTime);
                var series = [{
                    type: 'custom',//类型，自定义
                    renderItem: this.renderItem,//自定义图形设置，关键代码
                    itemStyle: {
                        normal: {
                            opacity: 1
                        }
                    },
                    label: {
                        normal: {
                            show: true,
                            position: 'inside',
                            formatter: function (a) {
                                return a.name;
                            },
                            color: "white"
                        }
                    },
                    encode: {
                        x: [1, 2],
                        y: 0
                    },
                    data: data
                },
                ];
                //echart 结构配置项
                var option = {
                    tooltip: {
                        formatter: function (params) {
                            let str = `${params.name}</br>${params.value[1]} - ${params.value[2]}`
                            return str;
                        }
                    },
                    legend: {
                        data: machines,
                        x: 'left',
                        y: 'top',
                    },
                    grid: {
                        left: 50,
                        right: 50
                    },
                    xAxis: {
                        type: "time",
                        boundaryGap: false,
                        min: startTime,
                        max: endTime,
                        scale: true,
                        position: "top",
                        nameTextStyle:{
                            color:'#5bc0de'
                        },
                        splitLine:{
                            lineStyle:{
                                color:'#303e4e'
                            }
                        },
                        axisLine:{
                            lineStyle:{
                                color:'#5bc0de'
                            }
                        },
                        //interval:3600000,
                        /* splitLine:{
						show:false
					}, */
                        axisLabel: {
                            borderColor:'#5bc0de',
                            //	rotate:'20',
                            formatter:  (val)=> {
                                let date = dateFormat('yyyy-mm-dd HH:MM:SS', new Date(val))
                                return date;
                            }
                        }
                    },
                    yAxis: {
                        type: "value",
                        inverse: true,
                        show: false,
                        scale: true,
                        minInterval: 1,
                    },
                    dataZoom: [{
                        type: 'slider',
                        filterMode: 'weakFilter',
                        showDataShadow: false,
                        borderColor: 'transparent',
                        backgroundColor: '#374a5f',
                        handleIcon: 'M10.7,11.9H9.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4h1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7v-1.2h6.6z M13.3,22H6.7v-1.2h6.6z M13.3,19.6H6.7v-1.2h6.6z', // jshint ignore:line
                        handleSize: 20,
                        handleStyle: {
                            shadowBlur: 6,
                            shadowOffsetX: 1,
                            shadowOffsetY: 2,
                            shadowColor: '#aaa'
                        },
                        labelFormatter: ''
                    }, {
                        type: 'inside',
                        filterMode: 'weakFilter'
                    }],
                    series: series
                };

                var myChart = echarts.init(document.getElementById("chart"));
                window.onresize = function () {//适应屏幕的变化，重新计算图形
                    myChart.resize();
                }
                myChart.setOption(option);
                myChart.dispatchAction({
                    type: 'dataZoom',
                    // 可选，dataZoom 组件的 index，多个 dataZoom 组件时有用，默认为 0
                    dataZoomIndex: [0, 1, 2, 3],
                    // 开始位置的百分比，0 - 100
                    start: 0,
                    // 结束位置的百分比，0 - 100
                    end: 100,
                })
                /* myChart.on('datazoom', function (params) {//忘了有什么用。。
				var opt = myChart.getOption();
				var dz = opt.dataZoom[0];
				duration = dz.endValue - dz.startValue;
			}); */
                /* myChart.on('legendselectchanged',function(params){//点击图例可以隐藏对应的机器色块
				console.log(params);
				var selected = params.selected;
				var opt = myChart.getOption();
				opt.series[0].data = $.grep(data,function(n,i){
					if(selected[n.name]){
						return true;
					}else{
						return false;
					}
				});
				myChart.setOption(opt,true);
			}); */
                myChart.on('click', function (params) {//色块点击事件
                    console.log(params.data);
                });
            },
            renderItem(params, api) {
                var categoryIndex = api.value(0);
                var count = api.value(4);
                var start = api.coord([api.value(1), 0]);//
                var end = api.coord([api.value(2), 0]);//
                var yheight = api.size([0, categoryIndex * 0.35])[1];//色块y轴起始
               // var height = api.size([0, getMax(count)])[1];//色块长度
                var rectShape = echarts.graphic.clipRectByRect({
                    x: start[0],
                    y: start[1] + yheight,
                    width: end[0] - start[0],
                    height: 30
                }, {
                    x: params.coordSys.x,
                    y: params.coordSys.y,
                    width: params.coordSys.width,
                    height: params.coordSys.height
                });
                var style = api.style();
                var renderItem = rectShape && {
                    type: 'rect',
                    shape: rectShape,
                    style: style
                };

                return renderItem;
            }
        }
    }
</script>

<style>
	#chart{
		width: 100%;
		height:700px;
	}
</style>
