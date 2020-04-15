<template>
    <div class="echart-wapper" ref="mybox">

    </div>
</template>

<script>
    import bus from '../common/bus';
    const echarts = require('echarts');

    export default {
        name: "echarts",
        data(){
          return{
              clickEchartData:{}//点击图表获得的数据
          }
        },
        props: {
            //图表类型
            echartType: {
                type: String
            },
            //图表数据
            echartData: {
                type: Object,
                default(){
                    return{
                        baseConfig:{},
                        xAxisArr:[],
                        yAxisArr:[]
                    }
                }
            },
            /*
            *
                barData:{//柱状图数据
                    baseConfig:{
                        title:'日志级别数量统计',
                        xAxisName:'级别',
                        yAxisName:'数量/条',
                        hoverText:'数量'
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                pieData:{//饼状图数据
                    baseConfig:{
                        title:'日志级别数量统计',
                        hoverText:'百分比'
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                ringData:{
                    baseConfig:{
                        title:'日志级别数量统计',
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                lineData:{//折线图数据
                    baseConfig:{
                        title:'',
                        xAxisName:'时间/时',
                        yAxisName:'数量/条',
                        hoverText:'日志数量'
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                }
            *
            * */
            //是否开启图表点击
            clickState:{
                type: Boolean
            },
            //busname
            busName:{
                type:Object,
                default:()=>{
                    return {
                        exportName:'',
                        clickName:''
                    }
                }
            }
        },
        created() {

        },
        mounted() {
            this.judgeEchartType()
        },
        watch:{
            //监听props参数值的变化
            echartData:{
                handler() {
                    this.$nextTick( ()=> {
                        this.judgeEchartType()
                    })
                },
                immediate: true,
                deep: true
            }
        },
        methods:{
            //判断图表类型
            judgeEchartType(){
                switch (this.echartType) {
                    case 'bar':
                        this.barEchart();
                        break;
                    case 'morebar':
                        this.barEchart();
                        break;
                    case 'pie':
                        this.pieEchart();
                        break;
                    case 'ring':
                        this.ringEchart();
                        break;
                    case 'line':
                        this.lineEchart();
                        break;
                    case 'moreline':
                        this.moreLineEchart();
                        break;
                    case 'timeline':
                        this.timeLineEchart();
                        break;
                    case 'gauge':
                        this.gaugeEchart();
                        break;
                    default:
                        break;
                }
            },
            //柱状图
            barEchart(){
                if(this.echartData.baseConfig.dispose){
                    echarts.init(this.$refs.mybox).dispose();//销毁前一个实例
                }
                // 基于准备好的dom，初始化echarts实例
                // var myChart = echarts.init(document.getElementById('echart-wapper'));
               // let myChart1 = 'bar'+new Date();
                if(this.echartData.baseConfig.dispose){
                    echarts.init(this.$refs.mybox).dispose();//销毁前一个实例
                }
                let myChart = echarts.init(this.$refs.mybox);
                // 绘制图表
                myChart.setOption({
                    title: {
                        text: this.echartData.baseConfig.title ? this.echartData.baseConfig.title : '',
                        x:'center',
                        textStyle:{
                            color:'#5bc0de'
                        }
                    },
                    tooltip: {},
                    xAxis: {
                        name:this.echartData.baseConfig.xAxisName,
                        nameTextStyle:{
                            color:'#5bc0de'
                        },
                        axisLine:{
                            lineStyle:{
                                color:'#5bc0de'
                            }
                        },
                        axisLabel:{
                            borderColor:'#5bc0de',
                            interval:0,
                            rotate:this.echartData.baseConfig.rotate ? this.echartData.baseConfig.rotate : '0'
                        },
                        data: this.echartData.xAxisArr
                    },
                    grid:{
                        // show:true,
                        bottom:this.echartData.baseConfig.rotate ? '20%' : '60'
                    },
                    yAxis: {
                        name:this.echartData.baseConfig.yAxisName,
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
                        axisLabel: {
                            margin: 2,
                            formatter: function (value, index) {
                                if (value >= 10000 && value < 10000000) {
                                    value = value / 10000 + "万";
                                } else if (value >= 10000000) {
                                    value = value / 10000000 + "千万";
                                }
                                return value;
                            }
                        }
                    },
                    series: [{
                        name: this.echartData.baseConfig.hoverText,
                        type: 'bar',
                        itemStyle: {
                            normal: {
                                color: new echarts.graphic.LinearGradient(0, 1, 0, 0, [{
                                    offset: 0,
                                    color: this.echartData.baseConfig.itemColor ? this.echartData.baseConfig.itemColor[0] :'rgba(116,235,213,0.1)'
                                },{
                                    offset: 1,
                                    color: this.echartData.baseConfig.itemColor ? this.echartData.baseConfig.itemColor[1] : '#00EABD'
                                }])
                            }
                        },
                       /* itemStyle:{
                            normal:{
                                color:'#0EBF9C'
                            }
                        },*/
                        data: this.echartData.yAxisArr
                    }]
                });
                myChart.off('click')
                //图表点击事件
                myChart.on('click',(params)=>{
                    //调用父组件回调函数
                    this.$emit('callbackFucnc', params);
                    bus.$emit(this.busName.clickName,params)
                })
                bus.$emit(this.busName.exportName,myChart)
                window.addEventListener("resize",()=>{
                    myChart.resize();
                });
            },
            //折线图
            lineEchart(){
                let myChart = echarts.init(this.$refs.mybox);
                myChart.setOption({
                    title: {
                        text:this.echartData.baseConfig.title ? this.echartData.baseConfig.title : '',
                        x:'center',
                        textStyle:{
                            color:'#5bc0de'
                        }
                    },
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'cross',
                            label: {
                                backgroundColor: '#6a7985'
                            }
                        } ,
                        formatter: '{b} 时<br /> {a} : {c} 条'
                    },
                    legend: {
                        data:[],
                        left: 'left'
                    },
                    xAxis: {
                        name:this.echartData.baseConfig.xAxisName,
                        nameTextStyle:{
                            color:'#5bc0de'
                        },
                        axisLine:{
                            lineStyle:{
                                color:'#5bc0de'
                            }
                        },
                        boundaryGap: false,
                        data: this.echartData.xAxisArr
                    },
                    yAxis: {
                        name:this.echartData.baseConfig.yAxisName,
                        axisLine:{
                            lineStyle:{
                                color:'#5bc0de'
                            }
                        },
                        splitLine:{
                            lineStyle:{
                                color:'#303e4e'
                            }
                        },
                        nameTextStyle:{
                            color:'#5bc0de'
                        },
                        axisLabel: {
                            margin: 2,
                            formatter: function (value, index) {
                                if (value >= 10000 && value < 10000000) {
                                    value = value / 10000 + "万";
                                } else if (value >= 10000000) {
                                    value = value / 10000000 + "千万";
                                }
                                return value;
                            }
                        }
                    },
                    series: [{
                        name:this.echartData.baseConfig.hoverText,
                        type:'line',
                        smooth:true,
                        areaStyle: {
                            normal: {
                                //颜色渐变函数 前四个参数分别表示四个位置依次为左、下、右、上
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                                    offset: 0,
                                    color: '#00EABD'
                                }, {
                                    offset: 1,
                                    color: 'rgba(116,235,213,0.1)'
                                }])

                            }
                        },
                        itemStyle:{
                            normal:{
                                color:'#0EBF9C'
                            }
                        },
                        data:this.echartData.yAxisArr
                    }]
                });
                myChart.off('click')
                //图表点击事件
                myChart.on('click',(params)=>{
                    //调用父组件回调函数
                    //this.$emit('callbackFucnc', params);
                    bus.$emit(this.busName.clickName,params)
                })
                bus.$emit(this.busName.exportName,myChart)
                window.addEventListener("resize",function(){
                    myChart.resize();
                });
            },
            moreLineEchart(){
                if(this.echartData.baseConfig.dispose){
                    echarts.init(this.$refs.mybox).dispose();//销毁前一个实例
                }
                let myChart = echarts.init(this.$refs.mybox);
                let option = {
                    title: {
                        text:this.echartData.baseConfig.title ? this.echartData.baseConfig.title : '',
                        x:'center',
                        textStyle:{
                            color:'#5bc0de'
                        }
                    },
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'cross',
                            label: {
                                backgroundColor: '#6a7985'
                            }
                        }
                    },
                    legend: {
                        type:'scroll',
                        pageTextStyle:{
                            color:'#eee'
                        },
                        data:[],
                        width:'80%',
                        right:"5%",
                        top:"25px",
                        textStyle:{
                            color:'#eee'
                        }
                    },
                    xAxis: {
                        name:this.echartData.baseConfig.xAxisName,
                        nameTextStyle:{
                            color:'#5bc0de'
                        },
                        axisLine:{
                            lineStyle:{
                                color:'#5bc0de'
                            }
                        },
                        boundaryGap: false,
                        data: this.echartData.xAxisArr
                    },
                    yAxis: {
                        name:this.echartData.baseConfig.yAxisName,
                        axisLine:{
                            lineStyle:{
                                color:'#5bc0de'
                            }
                        },
                        splitLine:{
                            lineStyle:{
                                color:'#303e4e'
                            }
                        },
                        nameTextStyle:{
                            color:'#5bc0de'
                        },
                        axisLabel: {
                            margin: 2,
                            formatter: function (value, index) {
                                if (value >= 10000 && value < 10000000) {
                                    value = value / 10000 + "万";
                                } else if (value >= 10000000) {
                                    value = value / 10000000 + "千万";
                                }
                                return value;
                            }
                        }

                    },
                    series:[]
                };
                for(let i in this.echartData.yAxisArr ){
                    option.legend.data.push(this.echartData.yAxisArr[i].name);
                    option.series.push({
                        name:this.echartData.yAxisArr[i].name,
                        type: 'line',
                        smooth: true, //平滑性
                        areaStyle: {
                            normal: {
                                //颜色渐变函数 前四个参数分别表示四个位置依次为左、下、右、上
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                                    offset: 0,
                                    color: this.echartData.yAxisArr[i].color
                                }, {
                                    offset: 1,
                                    color: 'rgba(116,235,213,0.1)'
                                }])

                            }
                        },
                        itemStyle: {
                            normal: {
                                color: this.echartData.yAxisArr[i].color
                            }
                        },
                        data: this.echartData.yAxisArr[i].data
                    })
                }
                //option.series = this.echartData.yAxisArr ;
                myChart.setOption(option);
                //图表点击事件
                myChart.off('click')
                myChart.on('click',(params)=>{
                    //调用父组件回调函数
                    //this.$emit('callbackFucnc', params);
                    bus.$emit(this.busName.clickName,params)
                })
                bus.$emit(this.busName.exportName,myChart)
                window.addEventListener("resize",function(){
                    myChart.resize();
                });
            },
            //实时动态更新
            timeLineEchart(){
                if(this.echartData.baseConfig.dispose){
                    echarts.init(this.$refs.mybox).dispose();//销毁前一个实例
                }
                let myChart = echarts.init(this.$refs.mybox);
                let option = {
                    title: {
                        text:this.echartData.baseConfig.title ? this.echartData.baseConfig.title : '',
                        x:'center',
                        textStyle:{
                            color:'#5bc0de'
                        }
                    },
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'cross',
                            label: {
                                backgroundColor: '#6a7985'
                            }
                        }
                    },
                    legend: {
                        data:[],
                        right:"5%",
                        top:"25px",
                        textStyle:{
                            color:'#eee'
                        }
                    },
                    xAxis: {
                        name:this.echartData.baseConfig.xAxisName,
                        type: 'time',
                        nameTextStyle:{
                            color:'#5bc0de'
                        },
                        axisLine:{
                            lineStyle:{
                                color:'#5bc0de'
                            }
                        },
                        splitLine: {
                            show: false
                        }
                    },
                    yAxis: {
                        name:this.echartData.baseConfig.yAxisName,
                        type: 'value',
                        boundaryGap: [0, '100%'],
                        nameTextStyle:{
                            color:'#5bc0de'
                        },
                        axisLine:{
                            lineStyle:{
                                color:'#5bc0de'
                            }
                        },
                        splitLine: {
                            show: false
                        },
                        axisLabel: {
                            margin: 2,
                            formatter: function (value, index) {
                                if (value >= 10000 && value < 10000000) {
                                    value = value / 10000 + "万";
                                } else if (value >= 10000000) {
                                    value = value / 10000000 + "千万";
                                }
                                return value;
                            }
                        }
                    },
                    series: []
                };
                for(let i in this.echartData.yAxisArr ){
                    option.legend.data.push(this.echartData.yAxisArr[i].name);
                    option.series.push({
                        name:this.echartData.yAxisArr[i].name,
                        type: 'line',
                        smooth: true, //平滑性.
                        showSymbol: false,
                        hoverAnimation: false,
                        areaStyle: {
                            normal: {
                                //颜色渐变函数 前四个参数分别表示四个位置依次为左、下、右、上
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                                    offset: 0,
                                    color: this.echartData.yAxisArr[i].color
                                }, {
                                    offset: 1,
                                    color: 'rgba(116,235,213,0.1)'
                                }])

                            }
                        },
                        itemStyle: {
                            normal: {
                                color: this.echartData.yAxisArr[i].color
                            }
                        },
                        data: this.echartData.yAxisArr[i].data
                    })
                }
                //option.series = this.echartData.yAxisArr ;
                myChart.setOption(option);
                window.addEventListener("resize",function(){
                    myChart.resize();
                });
            },
            //饼图
            pieEchart(){
                //var myChart = echarts.init(document.getElementById('echart-wapper'));
                let myChart = echarts.init(this.$refs.mybox);
                myChart.setOption({
                    title : {
                        text: this.echartData.baseConfig.title ? this.echartData.baseConfig.title : '',
                        x:'center',
                        textStyle:{
                            color:'#5bc0de'
                        }
                    },
                    tooltip : {
                        trigger: 'item',
                        formatter: "{a} <br/>{b} : {c} ({d}%)"
                    },
                    legend: {
                        orient: 'vertical',
                        left: 'left',
                        data: []
                    },
                    // color:["#5bc0de","#1BB2D8","#99D2DD","#88B0BB","#1C7099","#038CC4","#75ABD0","#AFD6DD","#1790CFs"],
                    //color:['#48A2E3','#E95555','#E49831','#0FBF9C','#AE59E3','#5E6EE3','#8196BD','#88B0BB'],
                    color:['#1E73F0','#00D1CE','#33C3F7','#3952D3','#185BFF','#2455AD','#74EE9A','#253479'],
                    series : [
                        {
                            name: this.echartData.baseConfig.hoverText,
                            type: 'pie',
                            radius : '55%',
                            center: ['50%', '60%'],
                            /* data:[
                                {value:335, name:'alert multi control'},
                                {value:310, name:'alert_multi_verify'},
                                {value:234, name:'cur_yottabyte'},
                                {value:135, name:'share'},
                                {value:1548, name:'cj test'}
                            ], */
                            data:this.echartData.yAxisArr,
                            itemStyle: {
                                emphasis: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        }
                    ]
                });
                myChart.off('click')
                //图表点击事件
                myChart.on('click',(params)=>{
                    //调用父组件回调函数
                    //this.$emit('callbackFucnc', params);
                    bus.$emit(this.busName.clickName,params)
                })
                bus.$emit(this.busName.exportName,myChart)
                window.addEventListener("resize",function(){
                    myChart.resize();
                });
            },
            //圆环
            ringEchart(){
                let arr = this.echartData.yAxisArr;
                let arr2 = [];
                for (let i in arr){
                    let obj = {
                        value: arr[i].val,
                        name: arr[i].name,
                        itemStyle: {
                            normal: {
                                color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [{ //颜色渐变函数 前四个参数分别表示四个位置依次为左、下、右、上

                                    offset: 0,
                                    color: arr[i].color1
                                }, {
                                    offset: 1,
                                    color: arr[i].color2
                                }])
                            }
                        }
                    }
                    if(arr[i].label === true){
                        obj.label={
                            show: true,
                            fontSize: '18',
                            fontWeight: 'bold',
                            color: '#5EDDE3',
                            formatter: "{d}%"
                        }
                    }
                    arr2.push(obj);
                }
                let myChart = echarts.init(this.$refs.mybox);
                myChart.setOption({
                    tooltip: {
                        trigger: 'item',
                        formatter: "{a} <br/>{b}: {c} ({d}%)"
                    },
                    series: [{
                        name: this.echartData.baseConfig.title,
                        type: 'pie',
                        radius: ['55%', '70%'],
                        avoidLabelOverlap: false,
                        label: {
                            normal: {
                                show: false,
                                position: 'center'
                            },
                            emphasis: {
                                show: false,
                                textStyle: {
                                    fontSize: '20',
                                    fontWeight: 'bold',
                                    color: '#fff'
                                }
                            }
                        },
                        labelLine: {
                            normal: {
                                show: false
                            }
                        },
                        data: arr2
                    }]
                })
                window.addEventListener("resize",function(){
                    myChart.resize();
                });

            },
            //仪表盘
            gaugeEchart(){
                let myChart = echarts.init(this.$refs.mybox);
                myChart.setOption({
                    title : {
                        text: this.echartData.baseConfig.title ? this.echartData.baseConfig.title : '',
                        x:'center',
                        textStyle:{
                            color:'#5bc0de'
                        }
                    },
                    tooltip : {
                        formatter: "{a} <br/>{b} : {c}%"
                    },
                    series: [
                        {
                            name: this.echartData.baseConfig.hoverText,
                            type: 'gauge',
                            title:{
                                color:'#5bc0de'
                            },
                            detail: {formatter:'{value}%'},
                            //data: [{value: 10, name: '完成率'}]
                            data:this.echartData.yAxisArr[0].data,
                        }
                    ]
                })
                window.addEventListener("resize",function(){
                    myChart.resize();
                });
            }
        }
    }
</script>

<style scoped>
.echart-wapper{
    width: 100%;
    height: 100%;
}
</style>
