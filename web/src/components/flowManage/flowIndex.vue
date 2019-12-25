<template>
    <div class="flow-index-wapper" id="flow-index-wapper">
        <i class="el-icon-rank fullScreen"  @click="handleFullScreen"></i>
        <h2 class="index-tit">流量分析系统 </h2>
        <div class="world-wapper" id="world-wapper" ref="mybox"></div>
        <el-row :gutter="20" class="index-con">
            <el-col :span="7" class="item-col">
                <div class="item">
                    <div class="item-con">
                        <div class="con-tit">资产（IP）数据包个数</div>
                        <div class="con">
                            <v-echarts echartType="bar" :echartData = "this.chartData1" ></v-echarts>
                        </div>
                    </div>
                    <div class="item-con ranking">
                        <div class="con-tit">{{this.currentMouth }}月 URL 排行榜</div>
                        <div class="con">
                            <div class="ranking-head">
                                <span class="order">#</span>
                                <span class="val">IP</span>
                                <span class="num">数量</span>
                            </div>
                            <ul class="ranking-ul">
                                <li v-for="(i , index) in rankingData" :key="index" :class="index === 0 ? 'first-li' : (index === 1) ? 'second-li' : (index === 2) ? 'third-li' :''">
                                    <span class="order">{{index + 1}}</span>
                                    <span class="val">{{i.val}}</span>
                                    <span class="num">{{i.num}}</span>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </el-col>
            <el-col :span="10" class="item-col">
                <div class="item center">
                    <div class="current-date">
                        <div id="date">{{ date }}</div>
                        <ul>
                            <li id="hours"> {{ hour }}</li>
                            <li class="point">:</li>
                            <li id="min">{{ min }} </li>
                            <li class="point">:</li>
                            <li id="sec"> {{ sec }}</li>
                        </ul>
                    </div>

                    <div class="item-con flow-count-wapper">
                        <p class="flow-count-tit">已收集流量数</p>
                        <p class="flow-count-con">
                            <span v-for="(i,index) in flowCount" :key="index" v-if="i !== ','">{{i}}</span>
                            <b v-else>{{i}}</b>
                        </p>
                    </div>
                </div>
            </el-col>
            <el-col :span="7" class="item-col">
                <div class="item">
                    <div class="item-con">
                        <div class="con-tit">实时流量数据访问包大小</div>
                        <div class="con">
                            <v-echarts echartType="timeline" :echartData = "this.chartData2" ></v-echarts>
                        </div>
                    </div>
                    <div class="item-con">
                        <div class="con-tit">目的端口总流量</div>
                        <div class="con">
                            <v-echarts echartType="pie" :echartData = "this.chartData3" ></v-echarts>
                        </div>
                    </div>
                </div>
            </el-col>
        </el-row>
    </div>
    
</template>

<script>
    import echarts from "echarts";
    import 'echarts-gl'
    import '@/../node_modules/echarts/map/js/world.js'
    import vEcharts from '../common/echarts'
    import {dateFormat} from "../../../static/js/common";

    const MONTHNAME = [ "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" ];
    const DAYNAME = ["星期日","星期一","星期二","星期三","星期四","星期五","星期六"];
    const HOUR = ["00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"];

    export default {
        name: "flowIndex",
        data() {
            return {
                eData:[[{"name":"jinan","value":[120.3719,36.0986]},{"name":"jinan","value":[120.3719,36.0986]}],[{"name":"jinan","value":[120.3719,36.0986]},{"name":"Hangzhou","value":[120.1619,30.294]}],[{"name":"jinan","value":[120.3719,36.0986]},{"name":"Singapore","value":[103.8547,1.2929]}],[{"name":"jinan","value":[120.3719,36.0986]},{"name":"Beijing","value":[116.3889,39.9288]}],[{"name":"jinan","value":[120.3719,36.0986]},{"name":"Tokyo","value":[139.7532,35.6882]}],[{"name":"jinan","value":[120.3719,36.0986]},{"name":"Mountain View","value":[-122.0748,37.4043]}],[{"name":"jinan","value":[120.3719,36.0986]},{"name":"Central","value":[114.15,22.2909]}],[{"name":"jinan","value":[120.3719,36.0986]},{"name":"Seattle","value":[-122.3451,47.6348]}],[{"name":"jinan","value":[120.3719,36.0986]},{"name":"Qingdao","value":[120.3694,36.066]}],[{"name":"jinan","value":[120.3719,36.0986]},{"name":"Ashburn","value":[-77.4728,39.0481]}],[{"name":"Hangzhou","value":[120.1619,30.294]},{"name":"jinan","value":[120.3719,36.0986]}],[{"name":"Mountain View","value":[-122.0748,37.4043]},{"name":"jinan","value":[120.3719,36.0986]}],[{"name":"Beijing","value":[116.3889,39.9288]},{"name":"jinan","value":[120.3719,36.0986]}],[{"name":"Singapore","value":[103.8547,1.2929]},{"name":"jinan","value":[120.3719,36.0986]}],[{"name":"Central","value":[114.15,22.2909]},{"name":"jinan","value":[120.3719,36.0986]}],[{"name":"Tokyo","value":[139.7532,35.6882]},{"name":"jinan","value":[120.3719,36.0986]}],[{"name":"Seattle","value":[-122.3451,47.6348]},{"name":"jinan","value":[120.3719,36.0986]}],[{"name":"Ashburn","value":[-77.4728,39.0481]},{"name":"jinan","value":[120.3719,36.0986]}],[{"name":"San Francisco","value":[-122.4121,37.7506]},{"name":"jinan","value":[120.3719,36.0986]}]],
                interTime:'',
                timeInterval:5000,
                //数据日期间隔
                dataTime:3600,
                fullscreen:false,
                date:'',//日期
                hour:'',//时
                min:'',//分
                sec:'',//秒
                currentMouth:'',
                flowCount:'',
                chartData1:{
                    baseConfig:{
                        title:'',
                        xAxisName:'资产\nIP',
                        yAxisName:'数据包个数/个',
                        hoverText:'',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                rankingData:[],
                chartData2:{
                    baseConfig:{
                        title:'',
                        xAxisName:'时间',
                        yAxisName:'数据包长度/KB',
                        hoverText:'',
                    },
                    yAxisArr:[{
                        name:'',
                        color:'rgb(15,219,243)',
                        data:[]
                    }]
                },
                chartData3:{
                    baseConfig:{
                        title:'',
                        xAxisName:'',
                        yAxisName:'',
                        hoverText:'',
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                }
            }
        },
        created(){
            //循环创建时间 模拟时间
            setInterval(this.setDate,1000);
           // setInterval(this.getDataByTime,2000);
            //判断是否为手机端
            var ua = navigator.userAgent;
            var ipad = ua.match(/(iPad).*OS\s([\d_]+)/),
                isIphone = !ipad && ua.match(/(iPhone\sOS)\s([\d_]+)/),
                isAndroid = ua.match(/(Android)\s+([\d.]+)/),
                isMobile = isIphone || isAndroid;
            if(isMobile) {
                //显示左边栏开关
                window.location="mobile/index.html"
            }

        },
        mounted(){
            //设置地球
            this.setEarth();
            //获取流量数
            this.getFlowCount();
            //获取实时流量大小
            this.getDataByTime(this.timeInterval/1000);
            //获取资产ip数据
            this.getIpPacketsData(this.dataTime);
            //获取目的端口数据
            this.getTargetPortFlowData(this.dataTime);
            //获取应用画像排行榜
            let endTime = dateFormat('yyyy-mm-dd',new Date());
            let ta = endTime.split('-');
            let startTime = ta[0]+'-'+ta[1]+'-01';
            this.currentMouth = ta[1];
            this.getRanklingData(startTime,endTime)
            //定时器
            this.interTime = setInterval(()=>{
                this.getFlowCount();
                this.getDataByTime(this.timeInterval/1000);
                this.getIpPacketsData(this.dataTime);
                this.getTargetPortFlowData(this.dataTime);
            },this.timeInterval);
        },
        methods:{
            //创建时间
            setDate(){
                // 创建新的日期函数
                const newDate = new Date();
                // 设置日期
                newDate.setDate(newDate.getDate());
                this.date = newDate.getFullYear() + "/" + MONTHNAME[newDate.getMonth()] + '/' +  newDate.getDate() + ' ' +DAYNAME[newDate.getDay()];
                //设置小时
                const hours = new Date().getHours();
                this.hour = ( hours < 10 ? "0" : "" ) + hours;
                // 设置分钟
                const minutes = new Date().getMinutes();
                this.min = ( minutes < 10 ? "0" : "" ) + minutes;
                // 设置秒
                const seconds = new Date().getSeconds();
                this.sec =( seconds < 10 ? "0" : "" ) + seconds;
            },
            // 全屏事件
            handleFullScreen(){
                let element = document.getElementById('flow-index-wapper');
                if (this.fullscreen) {
                    if (document.exitFullscreen) {
                        document.exitFullscreen();
                    } else if (document.webkitCancelFullScreen) {
                        document.webkitCancelFullScreen();
                    } else if (document.mozCancelFullScreen) {
                        document.mozCancelFullScreen();
                    } else if (document.msExitFullscreen) {
                        document.msExitFullscreen();
                    }
                } else {
                    if (element.requestFullscreen) {
                        element.requestFullscreen();
                    } else if (element.webkitRequestFullScreen) {
                        element.webkitRequestFullScreen();
                    } else if (element.mozRequestFullScreen) {
                        element.mozRequestFullScreen();
                    } else if (element.msRequestFullscreen) {
                        // IE11
                        element.msRequestFullscreen();
                    }
                }
                this.fullscreen = !this.fullscreen;
            },
            /*设置地球*/
            setEarth(){
                let earthData = this.eData;
                /*
                 图中相关城市经纬度,根据你的需求添加数据
                 关于国家的经纬度，可以用首都的经纬度或者其他城市的经纬度
                 */
                var series = [];// 3D飞线
                var dser = [];  // 2D散点坐标
                earthData.forEach(function(item, i) {
                    dser.push({
                        type: 'effectScatter',
                        coordinateSystem: 'geo',
                        zlevel: 3,
                        rippleEffect: {
                            brushType: 'stroke'
                        },
                        label: {
                            fontSize:18,
                            show: true,
                            position: 'right',
                            formatter: '{b}'
                        },
                        itemStyle: {
                            normal: {
                                color: '#f5f802'
                            }
                        },
                        data:[{
                            name:item[0].name,
                            value:item[0].value,
                            symbolSize:10,
                            label: {
                                normal: {
                                    position: 'right'
                                }
                            }
                        }]
                    },{
                        type: 'effectScatter',
                        coordinateSystem: 'geo',
                        zlevel: 3,
                        rippleEffect: {
                            brushType: 'stroke'
                        },
                        label: {
                            normal: {
                                show: true,
                                position: 'left',
                                fontSize:18,
                                formatter: '{b}'
                            }
                        },
                        itemStyle: {
                            normal: {
                                /*color: '#ff0000'*/
                                color: '#f5f802'
                            }
                        },
                        data: [{
                            name:item[1].name,
                            value:item[1].value,
                            symbolSize:10,
                            label: {
                                normal: {
                                    position: 'right'
                                }
                            }
                        }]
                    })
                    series.push({
                        type: 'lines3D',
                        effect: {
                            show: true,
                            period: 3,//速度
                            trailLength: 0.1//尾部阴影
                        },
                        lineStyle: {//航线的视图效果
                            color: '#9ae5fc',
                            width: 1,
                            opacity: 0.6
                        },
                        // data: convertData(item[1])// 特效的起始、终点位置，一个二维数组，相当于coords: convertData(item[1])
                        data:[[item[0].value,item[1].value]]
                    })
                })
                var canvas = document.createElement('canvas');

                var myChart = echarts.init(canvas, null, {
                    width: 4096,
                    height: 2048
                });
                myChart.setOption({
                    backgroundColor: 'rgba(3,28,72,0.3)',
                    title: {
                        show:true
                    },
                    geo: {
                        type: 'map',
                        map: 'world',
                        left:0,
                        top:0,
                        right: 0,
                        bottom: 0,
                        boundingCoords: [[-180, 90], [180, -90]],
                        zoom:0,
                        roam: false,
                        itemStyle: {
                            borderColor:'#000d2d',
                            normal: {
                                areaColor: '#2455ad',
                                borderColor:'#000c2d'
                            },
                            emphasis: {

                                areaColor: '#357cf8'
                            }
                        },
                        emphasis:{
                            label:{
                                show:false
                            }
                        },
                        label:{
                            fontSize:24
                        }
                    },
                    series:dser
                })
                var option = {
                    backgroundColor: 'rgba(0,0,0,0)',//canvas的背景颜色
                    globe: {
                        baseTexture:myChart,
                        top: 'middle',
                        left: 'center',
                        displacementScale: 0,
                        environment:'none',
                        shading: 'color',
                        viewControl: {
                            distance:240,
                            autoRotate: false,
                            autoRotateAfterStill:5,
                            // 定位到北京
                            targetCoord: [116.46, 39.92]
                        }
                    },
                    series:series
                };

                var ee = echarts.init(this.$refs.mybox);
                ee.setOption(option, true);
                window.addEventListener("resize",()=>{
                    // myChart.resize();
                    ee.resize();
                });

            },
            setEarth2(){
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.get(this.$baseUrl+'/flow/getMap.do','')
                        .then(res=>{
                            layer.closeAll('loading');
                            /*let earthData = [
                                [{name:'南宁',value:[108.479, 23.1152]},{name:'广州',value:[108.479, 23.1152]}],
                                [{name:'重庆',value:[107.7539, 30.1904]},{name:'广州',value:[113.5107, 23.2196]}],
                                [{name:'美国',value:[-100.696295, 33.679979]},{name:'广州',value:[113.5107, 23.2196]}]
                            ]*/
                            let earthData = res.data;
                            /*
                             图中相关城市经纬度,根据你的需求添加数据
                             关于国家的经纬度，可以用首都的经纬度或者其他城市的经纬度
                             */
                            var series = [];// 3D飞线
                            var dser = [];  // 2D散点坐标
                            earthData.forEach(function(item, i) {
                                dser.push({
                                    type: 'effectScatter',
                                    coordinateSystem: 'geo',
                                    zlevel: 3,
                                    rippleEffect: {
                                        brushType: 'stroke'
                                    },
                                    label: {
                                        fontSize:18,
                                        show: true,
                                        position: 'right',
                                        formatter: '{b}'
                                    },
                                    itemStyle: {
                                        normal: {
                                            color: '#f5f802'
                                        }
                                    },
                                    data:[{
                                        name:item[0].name,
                                        value:item[0].value,
                                        symbolSize:10,
                                        label: {
                                            normal: {
                                                position: 'right'
                                            }
                                        }
                                    }]
                                },{
                                    type: 'effectScatter',
                                    coordinateSystem: 'geo',
                                    zlevel: 3,
                                    rippleEffect: {
                                        brushType: 'stroke'
                                    },
                                    label: {
                                        normal: {
                                            show: true,
                                            position: 'left',
                                            fontSize:18,
                                            formatter: '{b}'
                                        }
                                    },
                                    itemStyle: {
                                        normal: {
                                            /*color: '#ff0000'*/
                                            color: '#f5f802'
                                        }
                                    },
                                    data: [{
                                        name:item[1].name,
                                        value:item[1].value,
                                        symbolSize:10,
                                        label: {
                                            normal: {
                                                position: 'right'
                                            }
                                        }
                                    }]
                                })
                                series.push({
                                    type: 'lines3D',
                                    effect: {
                                        show: true,
                                        period: 3,//速度
                                        trailLength: 0.1//尾部阴影
                                    },
                                    lineStyle: {//航线的视图效果
                                        color: '#9ae5fc',
                                        width: 1,
                                        opacity: 0.6
                                    },
                                    // data: convertData(item[1])// 特效的起始、终点位置，一个二维数组，相当于coords: convertData(item[1])
                                    data:[[item[0].value,item[1].value]]
                                })
                            })
                            var canvas = document.createElement('canvas');

                            var myChart = echarts.init(canvas, null, {
                                width: 4096,
                                height: 2048
                            });
                            myChart.setOption({
                                backgroundColor: 'rgba(3,28,72,0.3)',
                                title: {
                                    show:true
                                },
                                geo: {
                                    type: 'map',
                                    map: 'world',
                                    left:0,
                                    top:0,
                                    right: 0,
                                    bottom: 0,
                                    boundingCoords: [[-180, 90], [180, -90]],
                                    zoom:0,
                                    roam: false,
                                    itemStyle: {
                                        borderColor:'#000d2d',
                                        normal: {
                                            areaColor: '#2455ad',
                                            borderColor:'#000c2d'
                                        },
                                        emphasis: {

                                            areaColor: '#357cf8'
                                        }
                                    },
                                    emphasis:{
                                        label:{
                                            show:false
                                        }
                                    },
                                    label:{
                                        fontSize:24
                                    }
                                },
                                series:dser
                            })
                            var option = {
                                backgroundColor: 'rgba(0,0,0,0)',//canvas的背景颜色
                                globe: {
                                    baseTexture:myChart,
                                    top: 'middle',
                                    left: 'center',
                                    displacementScale: 0,
                                    environment:'none',
                                    shading: 'color',
                                    viewControl: {
                                        distance:240,
                                        autoRotate: false,
                                        autoRotateAfterStill:5,
                                        // 定位到北京
                                        targetCoord: [116.46, 39.92]
                                    }
                                },
                                series:series
                            };

                            var ee = echarts.init(this.$refs.mybox);
                            ee.setOption(option, true);
                            window.addEventListener("resize",()=>{
                                // myChart.resize();
                                ee.resize();
                            });

                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })

            },
            /*获取流量数*/
            getFlowCount(){
                this.$nextTick( ()=> {
                    this.$axios.get(this.$baseUrl+'/log/getIndicesCountByType.do',{})
                        .then((res) => {
                            this.flowCount = parseInt(res.data[0].indices_defaultpacket).toLocaleString();
                        })
                        .catch((err) => {
                            console.log(err)
                        })
                })
            },
            /*获取流量数据-动态实时*/
            getDataByTime(timeInterval){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getPacketLengthPerSecond.do',this.$qs.stringify({
                        timeInterval:timeInterval
                    }))
                        .then(res=>{
                            if(this.chartData2.yAxisArr[0].data.length < 60){
                                this.chartData2.yAxisArr[0].data.push(res.data[0])
                            }else{
                                this.chartData2.yAxisArr[0].data.shift();
                                this.chartData2.yAxisArr[0].data.push(res.data[0])
                            }
                        })
                        .catch(err=>{

                        })
                })
               /* //获得当前时间
                let time = new Date();
                //console.log(time)
                let value = Math.random() * 21 + 100;
                if(this.chartData2.yAxisArr[0].data.length < 60){
                    this.chartData2.yAxisArr[0].data.push({
                        name:time,
                        value:[time, Math.round(value)]
                    })
                }else{
                    this.chartData2.yAxisArr[0].data.shift();
                    this.chartData2.yAxisArr[0].data.push({
                        name:time,
                        value:[time, Math.round(value)]
                    })
                }*/
            },
            /*获取资产ip数据*/
            getIpPacketsData(dataTime){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getDstIPPacketCount.do',this.$qs.stringify({
                        timeInterval:dataTime
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data){
                                let obj = res.data[i];
                                for (let j in obj){
                                    xns1.push(j);
                                    yvs1.push(obj[j])
                                    yvs2.push({
                                        name:j,
                                        value:obj[j]
                                    })
                                }
                            }
                            this.chartData1.xAxisArr = xns1;
                            this.chartData1.yAxisArr = yvs1;
                            //this.ipPacketsData2.yAxisArr = yvs2;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取目的端口总流量*/
            getTargetPortFlowData(dataTime){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getDstPortCount.do',this.$qs.stringify({
                        timeInterval:dataTime
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data){
                                let obj = res.data[i];
                                for (let j in obj){
                                    xns1.push(j);
                                    yvs1.push(obj[j]);
                                    yvs2.push({
                                        name:j,
                                        value:obj[j]
                                    })
                                }
                            }
                            /*this.targetPortFlowData.xAxisArr = xns1;
                            this.targetPortFlowData.yAxisArr = yvs1;*/
                            this.chartData3.yAxisArr = yvs2;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取应用画像数据*/
            getRanklingData(startTime,endTime){
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/flow/getCountGroupByUrl.do',this.$qs.stringify({
                        application_layer_protocol:'',
                        ipv4_dst_addr:'',
                        startTime:startTime,
                        endTime:endTime
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            let arr = [];
                            res.data.forEach((item)=>{
                                let obj = {};
                                obj.val=item.domain_url;
                                obj.num = item.count;
                                arr.push(obj);
                            })
                            this.rankingData = arr;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            }
        },
        beforeDestroy(){
          clearInterval(this.interTime)
        },
        components:{
            vEcharts
        }
    }
</script>

<style scoped>
    .flow-index-wapper{
        position: absolute;
        top: 0;
        left: 0;
        height: calc(100% - 40px);
        width: calc(100% - 40px);
        padding:20px;
        background: url("../../../static/img/flow-index-bg2.png");
        background-size: 100% 100%;
    }
    .fullScreen{
        position: absolute;
        right: 25px;
        top: 44px;
        z-index: 3;
        font-size: 26px;
        cursor: pointer;
        color: #1dd8fe;
    }
    .index-tit{
        text-align: center;
        position: absolute;
        width: 100%;
        z-index: 2;
        background-image: linear-gradient(to bottom, #4facfe 0%, #00f2fe 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
    }
    .index-con{
        height: 100%;
        background: url("../../../static/img/flow-index-bg.png");
        background-size: 100% 100%;
        position: relative;
        top: 10px;
        padding: 20px;
    }
    .item-col{
        height: 100%;
    }
    .item{
        height: 100%;
        position: relative;
        /*background: #0b4c86;*/
    }
    .world-wapper{
        width: 100%;
        height: calc(100% - 50px);
        position: absolute;
        z-index: 1;
    }
    .item-con{
        height: calc(50% - 20px);
        margin-top: 20px;
        box-sizing: border-box;
        width: 100%;
        /*background: rgba(11, 76, 134, 0.3);*/
        background: url("../../../static/img/bd2.png");
        background-size: 100% 100%;
        position: relative;
        z-index: 2;
    }
    .item-con .con-tit{
        height: 40px;
        line-height: 40px;
        text-indent: 30px;
        font-weight: 600;
    }
    .item-con .con{
        height: calc(100% - 40px);
        padding: 0 20px;
        position: relative;
        z-index: 2;
    }
    .current-date{
        position: absolute;
        top:20px;
        z-index: 2;
    }
    #date{
        /*font-family:'BebasNeueRegular', Arial, Helvetica, sans-serif;*/
        font-size:35px;
        text-align:center;
        text-shadow:0 0 5px #00c6ff;
        margin-bottom: 10px;
        font-family: 'electronicFont';
        background-image: linear-gradient(to right, #5bc0de 40%, #75EF99 73%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
    }
    .current-date ul{
        width: 100%;
        padding: 0px;
        list-style: none;
        text-align: center;
        font-family: 'electronicFont';
        background-image: linear-gradient(to right, #5bc0de 40%, #75EF99 73%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
    }
    .current-date ul li{
        display:inline;
        /* font-size:5em;  */
        text-align:center;
        /* font-family:'BebasNeueRegular', Arial, Helvetica, sans-serif;
         text-shadow:0 0 5px #00c6ff;*/
    }
    @media only screen and (min-width: 800px){
        .current-date ul li {
            font-size:2.5em;
        }

    }
    @media only screen and (min-width: 1500px){
        .current-date ul li {
            font-size:3.5em;
        }
        .current-number p b{
            font-size: 1rem;
        }
    }
    .center .flow-count-wapper{
        position: absolute;
        height: 120px;
        bottom:0;
        width: 100%;
        background: rgba(11, 76, 134, 0.35);
        z-index: 2;

    }
    .flow-count-tit{
        height:50px;
        text-align: center;
        line-height: 50px;
        font-size: 22px;
        color: #00F6FF;
    }
    .flow-count-con{
        display: flex;
        justify-content: center;
    }
    .flow-count-con span{
        display: inline-block;
        width: 30px;
        height: 45px;
        text-align: center;
        line-height: 45px;
        margin: 0 2px;
        font-family: 'electronicFont';
        /*color: #90EBFD;
        background: rgba(1,36,100,0.5);
        border: 2px solid #1272AE;*/
        background-image: linear-gradient(to bottom, #4facfe 0%, #00f2fe 100%);
        color:#072457;
        font-size: 38px;
    }
    .flow-count-con b{
        display: inline-block;
        height: 45px;
        line-height: 60px;
        margin: 0 5px;
        color:#4facfe;
    }
    .ranking .con{
        padding: 0 30px;
        font-size: 13px;
        color: #40b9fe;
    }
    .ranking .ranking-head{
        height: 36px;
        line-height: 36px;
    }
    .ranking .ranking-ul{
        height: calc(100% - 40px);
        overflow-y: auto;
    }
    .ranking .ranking-ul li{
        height: 36px;
        line-height: 36px;
        border-top: 1px solid #283261;
    }
    .ranking-ul li.first-li{
        color:#F54545;
    }
    .ranking-ul li.second-li{
        color:#FF8547;
    }
    .ranking-ul li.third-li{
        color:#FFAC39;
    }
    .ranking span{
        display: inline-block;
        text-align: center;
    }
    .ranking .order{
        width: 32px;
    }
    .ranking .val{
        width: calc(100% - 125px);
    }
    .ranking .num{
        width: 80px;
    }
</style>
