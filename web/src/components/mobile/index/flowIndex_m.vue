<template>
    <div>
        <div class="time-wapper">{{date}} {{hour}}:{{min}}:{{sec}}</div>
        <div class="world-wapper" id="world-wapper" ref="mybox"></div>
        <div class="flow-count-wapper">
            <p class="flow-count-tit">已收集流量数</p>
            <p class="flow-count-con">
                <span v-for="(i,index) in flowTotle" :key="index" v-if="i !== ','">{{i}}</span>
                <b v-else>{{i}}</b>
            </p>
        </div>
        <div class="item-wapper">
            <div class="item-title">实时流量</div>
            <div class="item-content">
                <allflow_timeline :params="param2" :setIntervalObj="intervalObj" :baseConProp="{title:''}"></allflow_timeline>
            </div>
        </div>
        <div class="item-wapper">
            <div class="item-title">资产（IP）数据包个数</div>
            <div class="item-content">
                <eqIpPacket_bar :params="params" :setIntervalObj="intervalObj"></eqIpPacket_bar>
            </div>
        </div>
        <div class="item-wapper">
            <div class="item-title">目的端口总流量</div>
            <div class="item-content">
                <dstPortAll_pie :params="params" :setIntervalObj="intervalObj"></dstPortAll_pie>
            </div>
        </div>
    </div>
</template>

<script>
    import echarts from "echarts";
    import 'echarts-gl'
    import '@/../node_modules/echarts/map/js/world.js'
    import eqIpPacket_bar from '../../charts/flow/equipmentFlow/eqIpPacket_bar'
    import dstPortAll_pie from '../../charts/flow/portFlow/dstPortAll_pie'
    import allflow_timeline from '../../charts/flow/realTime/allflow_timeline'
    import {dateFormat} from "../../../../static/js/common";

    const MONTHNAME = [ "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" ];
    const DAYNAME = ["Sun.","Mon.","Tues.","Wed.","Thur.","Fri.","Sat."];
    export default {
        name: "flowIndex",
        data() {
            return {
                interTime:'',
                timeInterval:5000,
                dataTime:3600,
                date:'',//日期
                hour:'',//时
                min:'',//分
                sec:'',//秒
                flowTotle:'',
                //目的地址、资产ip参数
                params:{
                    intervalValue:'5',
                    intervalType:'second',
                    starttime:'',
                    endtime:'',
                    last:'1-hour'
                },
                //实时流量参数
                param2:{
                    intervalValue:'5',
                    intervalType:'second',
                    starttime:'',
                    endtime:'',
                    last:'5-min'
                },
                intervalObj:{
                    state:true,
                    interval:'5000'
                },
            }
        },
        created(){
            this.setDate();
            this.getFlowTotle();
            //判断是否为手机端
            var ua = navigator.userAgent;
            var ipad = ua.match(/(iPad).*OS\s([\d_]+)/),
                isIphone = !ipad && ua.match(/(iPhone\sOS)\s([\d_]+)/),
                isAndroid = ua.match(/(Android)\s+([\d.]+)/),
                isMobile = isIphone || isAndroid;
            if(!isMobile) {
                this.$router.push('/flowIndex');
            }
        },
        mounted(){
            this.setEarth2();
            setInterval(this.getFlowTotle,5000);
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
            /*获取流量条数*/
            getFlowTotle(){
                this.$axios.post(this.$baseUrl+'/log/getIndicesCountByType.do',{})
                    .then(res=>{
                        this.flowTotle = parseInt(res.data[0].indices_defaultpacket).toLocaleString();
                    })
                    .catch(err=>{
                        this.$Message.error(err)
                    })
            },
            /*设置地球*/
            setEarth2(){
                this.$axios.post(this.$baseUrl+'/flow/getMap.do',this.$qs.stringify({
                    timeInterval:259200
                }))
                    .then(res=>{
                        /*
                     图中相关城市经纬度,根据你的需求添加数据
                     关于国家的经纬度，可以用首都的经纬度或者其他城市的经纬度
                     */
                        let earthData = res.data;
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
                        this.$Message.error(err)
                    })
            },

            /*计时器*/
            setInterFun(){
                this.interTime = setInterval(()=>{
                    //let time = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
                   /* this.getDataByTime(this.timeInterval/1000)
                    this.getIpPacketsData(this.dataTime);
                    this.getTargetPortFlowData(this.dataTime);*/
                },this.timeInterval);
            }
        },
        beforeRouteEnter(to, from, next){
            next(vm => {
                vm.setInterFun();
            });
        },
        beforeRouteLeave(to, from, next) {
            clearInterval(this.interTime);
            next();
        },
        components:{
            eqIpPacket_bar,
            dstPortAll_pie,
            allflow_timeline
        }
    }
</script>

<style scoped>
    .content-bg{
        /*height: 1000px;*/
        font-size: 1rem;
        background: url("../../../../static/img/flow-index-bg2.png");
        background-size: 100% 100%;
    }
    .time-wapper{
        height: 2rem;
        line-height: 2rem;
        text-align: center;
        font-size: 1.3rem;
        font-family: 'electronicFont';
        background-image: linear-gradient(to right, #5bc0de 40%, #75EF99 73%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
    }
    .world-wapper{
        width: 100%;
        height:320px;
    }
    .item-wapper{
        height: auto;
        /*background: rgb(26,36,47);*/
        background: url("../../../../static/img/bd2.png");
        background-size: 100% 100%;
        margin:0.5rem 0.2rem;
    }
    .item-title{
        height: 2.5rem;
        line-height: 2.5rem;
        font-size: 1rem;
        text-align: left;
        padding-left: 25px;
        /*background: url("../../../static/img/title-bg.png");*/
        background-size: 100% 100%;
    }
    .item-content{
        /*height: calc(100% - 1rem);*/
        height: 230px;
        box-sizing: border-box;
        padding: 0.5rem;
        padding-top: 0;
    }
    .flow-count-tit{
        height:50px;
        text-align: center;
        line-height: 50px;
        font-size: 22px;
        color: #00F6FF;
        margin: 0;
    }
    .flow-count-con{
        display: flex;
        justify-content: center;
        margin: 0;
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
</style>
