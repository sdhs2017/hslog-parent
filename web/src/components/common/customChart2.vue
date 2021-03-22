<template>
    <div class="content-bg">
        <div id="chart"></div>
    </div>

</template>

<script>
    import {dateFormat,changeEchartsTooltip} from "../../../static/js/common";
    const echarts = require('echarts');
    export default {
        name:'customChart1',
        /*props:{
            chartData:{
                type:Object
            },
            duration:{
                type:Array
            }
        },*/
        data() {
            return {
                colorArr :['#00EABD','#20C1F3','#FC686F','#F9D124','#DE1AFB','#C0D7FC','#A9F4B7','#FF9E96','#75B568','#323A81'],
                categories:[],
                data:[],
                zoomH:0,
                duration:['2021-02-04 08:00:00','2021-02-04 18:15:25'],
                chartData:{"特殊登录-全局（logged-in-special）":[{"start":"2021-02-04 08:55:00","end":"2021-02-04 09:34:03","count":32,"title":"事件告警名称：特殊登录-全局（logged-in-special）<br>发生次数：32<br>告警条件：事件数>=5"},{"start":"2021-02-04 09:52:04","end":"2021-02-04 10:12:10","count":6,"title":"事件告警名称：特殊登录-全局（logged-in-special）<br>发生次数：6<br>告警条件：事件数>=5"},{"start":"2021-02-04 10:27:33","end":"2021-02-04 10:35:40","count":5,"title":"事件告警名称：特殊登录-全局（logged-in-special）<br>发生次数：5<br>告警条件：事件数>=5"},{"start":"2021-02-04 10:49:04","end":"2021-02-04 11:40:43","count":17,"title":"事件告警名称：特殊登录-全局（logged-in-special）<br>发生次数：17<br>告警条件：事件数>=5"},{"start":"2021-02-04 11:53:22","end":"2021-02-04 12:03:18","count":5,"title":"事件告警名称：特殊登录-全局（logged-in-special）<br>发生次数：5<br>告警条件：事件数>=5"},{"start":"2021-02-04 12:14:52","end":"2021-02-04 12:21:43","count":6,"title":"事件告警名称：特殊登录-全局（logged-in-special）<br>发生次数：6<br>告警条件：事件数>=5"},{"start":"2021-02-04 13:03:20","end":"2021-02-04 13:40:44","count":14,"title":"事件告警名称：特殊登录-全局（logged-in-special）<br>发生次数：14<br>告警条件：事件数>=5"},{"start":"2021-02-04 15:18:23","end":"2021-02-04 15:18:24","count":5,"title":"事件告警名称：特殊登录-全局（logged-in-special）<br>发生次数：5<br>告警条件：事件数>=5"},{"start":"2021-02-04 15:31:17","end":"2021-02-04 15:43:37","count":5,"title":"事件告警名称：特殊登录-全局（logged-in-special）<br>发生次数：5<br>告警条件：事件数>=5"},{"start":"2021-02-04 16:36:26","end":"2021-02-04 16:49:45","count":5,"title":"事件告警名称：特殊登录-全局（logged-in-special）<br>发生次数：5<br>告警条件：事件数>=5"},{"start":"2021-02-04 17:00:33","end":"2021-02-04 17:31:43","count":16,"title":"事件告警名称：特殊登录-全局（logged-in-special）<br>发生次数：16<br>告警条件：事件数>=5"}],"登录-全局（logged-in）":[{"start":"2021-02-04 08:55:00","end":"2021-02-04 09:34:03","count":34,"title":"事件告警名称：登录-全局（logged-in）<br>发生次数：34<br>告警条件：事件数>=5"},{"start":"2021-02-04 09:52:04","end":"2021-02-04 10:12:10","count":6,"title":"事件告警名称：登录-全局（logged-in）<br>发生次数：6<br>告警条件：事件数>=5"},{"start":"2021-02-04 10:27:33","end":"2021-02-04 10:35:40","count":5,"title":"事件告警名称：登录-全局（logged-in）<br>发生次数：5<br>告警条件：事件数>=5"},{"start":"2021-02-04 10:49:04","end":"2021-02-04 11:40:43","count":17,"title":"事件告警名称：登录-全局（logged-in）<br>发生次数：17<br>告警条件：事件数>=5"},{"start":"2021-02-04 11:53:22","end":"2021-02-04 12:03:18","count":5,"title":"事件告警名称：登录-全局（logged-in）<br>发生次数：5<br>告警条件：事件数>=5"},{"start":"2021-02-04 12:14:52","end":"2021-02-04 12:21:43","count":6,"title":"事件告警名称：登录-全局（logged-in）<br>发生次数：6<br>告警条件：事件数>=5"},{"start":"2021-02-04 13:03:20","end":"2021-02-04 13:40:44","count":14,"title":"事件告警名称：登录-全局（logged-in）<br>发生次数：14<br>告警条件：事件数>=5"},{"start":"2021-02-04 15:18:23","end":"2021-02-04 15:18:24","count":5,"title":"事件告警名称：登录-全局（logged-in）<br>发生次数：5<br>告警条件：事件数>=5"},{"start":"2021-02-04 15:31:17","end":"2021-02-04 15:43:37","count":5,"title":"事件告警名称：登录-全局（logged-in）<br>发生次数：5<br>告警条件：事件数>=5"},{"start":"2021-02-04 16:36:26","end":"2021-02-04 16:49:45","count":5,"title":"事件告警名称：登录-全局（logged-in）<br>发生次数：5<br>告警条件：事件数>=5"},{"start":"2021-02-04 17:00:33","end":"2021-02-04 17:31:43","count":17,"title":"事件告警名称：登录-全局（logged-in）<br>发生次数：17<br>告警条件：事件数>=5"}],"枚举组成员身份-资产（group-membership-enumerated）":[{"start":"2021-02-04 08:55:02","end":"2021-02-04 09:18:19","count":78,"title":"事件告警名称：枚举组成员身份-资产（group-membership-enumerated）<br>发生次数：78<br>告警条件：事件数>=20"},{"start":"2021-02-04 09:33:42","end":"2021-02-04 09:35:40","count":25,"title":"事件告警名称：枚举组成员身份-资产（group-membership-enumerated）<br>发生次数：25<br>告警条件：事件数>=20"},{"start":"2021-02-04 10:35:40","end":"2021-02-04 10:35:40","count":24,"title":"事件告警名称：枚举组成员身份-资产（group-membership-enumerated）<br>发生次数：24<br>告警条件：事件数>=20"},{"start":"2021-02-04 11:05:40","end":"2021-02-04 11:05:44","count":24,"title":"事件告警名称：枚举组成员身份-资产（group-membership-enumerated）<br>发生次数：24<br>告警条件：事件数>=20"},{"start":"2021-02-04 11:35:44","end":"2021-02-04 11:35:48","count":24,"title":"事件告警名称：枚举组成员身份-资产（group-membership-enumerated）<br>发生次数：24<br>告警条件：事件数>=20"},{"start":"2021-02-04 12:05:48","end":"2021-02-04 12:05:51","count":24,"title":"事件告警名称：枚举组成员身份-资产（group-membership-enumerated）<br>发生次数：24<br>告警条件：事件数>=20"},{"start":"2021-02-04 12:35:51","end":"2021-02-04 12:35:56","count":24,"title":"事件告警名称：枚举组成员身份-资产（group-membership-enumerated）<br>发生次数：24<br>告警条件：事件数>=20"},{"start":"2021-02-04 13:05:56","end":"2021-02-04 13:06:00","count":24,"title":"事件告警名称：枚举组成员身份-资产（group-membership-enumerated）<br>发生次数：24<br>告警条件：事件数>=20"},{"start":"2021-02-04 13:36:00","end":"2021-02-04 13:36:05","count":24,"title":"事件告警名称：枚举组成员身份-资产（group-membership-enumerated）<br>发生次数：24<br>告警条件：事件数>=20"},{"start":"2021-02-04 14:06:05","end":"2021-02-04 14:06:09","count":24,"title":"事件告警名称：枚举组成员身份-资产（group-membership-enumerated）<br>发生次数：24<br>告警条件：事件数>=20"},{"start":"2021-02-04 14:36:09","end":"2021-02-04 14:36:14","count":24,"title":"事件告警名称：枚举组成员身份-资产（group-membership-enumerated）<br>发生次数：24<br>告警条件：事件数>=20"},{"start":"2021-02-04 15:06:14","end":"2021-02-04 15:06:18","count":24,"title":"事件告警名称：枚举组成员身份-资产（group-membership-enumerated）<br>发生次数：24<br>告警条件：事件数>=20"},{"start":"2021-02-04 15:36:18","end":"2021-02-04 15:36:21","count":24,"title":"事件告警名称：枚举组成员身份-资产（group-membership-enumerated）<br>发生次数：24<br>告警条件：事件数>=20"},{"start":"2021-02-04 16:06:21","end":"2021-02-04 16:06:26","count":24,"title":"事件告警名称：枚举组成员身份-资产（group-membership-enumerated）<br>发生次数：24<br>告警条件：事件数>=20"},{"start":"2021-02-04 16:36:26","end":"2021-02-04 16:36:30","count":24,"title":"事件告警名称：枚举组成员身份-资产（group-membership-enumerated）<br>发生次数：24<br>告警条件：事件数>=20"},{"start":"2021-02-04 17:06:30","end":"2021-02-04 17:06:33","count":24,"title":"事件告警名称：枚举组成员身份-资产（group-membership-enumerated）<br>发生次数：24<br>告警条件：事件数>=20"}],"用户帐户管理-资产（User Account Management）":[{"start":"2021-02-04 08:55:00","end":"2021-02-04 09:11:12","count":911,"title":"事件告警名称：用户帐户管理-资产（User Account Management）<br>发生次数：911<br>告警条件：事件数>=15"},{"start":"2021-02-04 09:33:03","end":"2021-02-04 09:33:05","count":180,"title":"事件告警名称：用户帐户管理-资产（User Account Management）<br>发生次数：180<br>告警条件：事件数>=15"},{"start":"2021-02-04 11:33:04","end":"2021-02-04 11:33:05","count":124,"title":"事件告警名称：用户帐户管理-资产（User Account Management）<br>发生次数：124<br>告警条件：事件数>=15"},{"start":"2021-02-04 12:03:18","end":"2021-02-04 12:03:19","count":65,"title":"事件告警名称：用户帐户管理-资产（User Account Management）<br>发生次数：65<br>告警条件：事件数>=15"},{"start":"2021-02-04 13:36:00","end":"2021-02-04 13:38:27","count":16,"title":"事件告警名称：用户帐户管理-资产（User Account Management）<br>发生次数：16<br>告警条件：事件数>=15"},{"start":"2021-02-04 15:54:36","end":"2021-02-04 15:54:51","count":16,"title":"事件告警名称：用户帐户管理-资产（User Account Management）<br>发生次数：16<br>告警条件：事件数>=15"}],"登录-资产（logged-in）":[{"start":"2021-02-04 08:55:00","end":"2021-02-04 09:34:03","count":34,"title":"事件告警名称：登录-资产（logged-in）<br>发生次数：34<br>告警条件：事件数>=10"},{"start":"2021-02-04 09:52:04","end":"2021-02-04 13:40:44","count":57,"title":"事件告警名称：登录-资产（logged-in）<br>发生次数：57<br>告警条件：事件数>=10"},{"start":"2021-02-04 15:03:20","end":"2021-02-04 16:06:21","count":15,"title":"事件告警名称：登录-资产（logged-in）<br>发生次数：15<br>告警条件：事件数>=10"},{"start":"2021-02-04 16:36:26","end":"2021-02-04 17:31:43","count":22,"title":"事件告警名称：登录-资产（logged-in）<br>发生次数：22<br>告警条件：事件数>=10"}]},
            }
        },
        watch:{
            //监听props参数值的变化
            chartData:{
                handler() {
                    this.$nextTick( ()=> {
                        this.setData(this.chartData)
                    })
                },
                immediate: true,
                deep: true
            }
        },
        mounted() {
            //this.setData(this.chartData)
            // this.getData()
        },
        methods: {
            //生成随机颜色
            getColorRandom(){
                var c="#";
                var cArray=["0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"];
                for(var i=0;i<6;i++){
                    var  cIndex= Math.round(Math.random()*15);
                    c+=cArray[cIndex];
                }
                return c;
            },

            /* 获取数据 */
            getData() {
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/siem/getSIEMInfo.do',this.$qs.stringify({
                        equipment_id:'5ae3d773acb14e638c761056c4f9ffca',
                        last:'1-day'
                    }))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.chartData = obj.data
                                this.setData(this.chartData)
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.loading = false;
                        })
                })
            },
            setData(data){
                this.categories = [];
                this.data = [];
                /*data.forEach((item,i) =>{
                    this.categories.push(item.name)
                    let color = '#'+(Math.random()*0xffffff<<0).toString(16);
                    item.data.forEach( it =>{
                        let arr = [i,it.startTime,it.endTime]
                        this.data.push({
                            itemStyle:{color:color},
                            name:item.data.text,
                            value:arr
                        })
                    })
                })*/
                let length = 0
                for(let i in this.chartData){
                    let index = this.categories.length;
                    this.categories.push(i)
                    let color = ''
                    if(length < 10){
                        color = this.colorArr[length]
                    }else{
                        color = this.getColorRandom()
                    }
                    this.chartData[i].forEach( item =>{
                        let arr = [index,item.start,item.end]
                        this.data.push({
                            itemStyle:{color:color},
                            name:item.title,
                            value:arr
                        })
                    })
                    length ++;
                }
                this.zoomH = (100 * 35) / length
                this.setChart();
            },
            setChart(){
                let option = {
                    tooltip: {
                        formatter: function(params) {
                            //console.log(params)
                            return params.marker +params.name +  '<br>' + params.value[1] + '-' + params.value[2];
                        }
                    },
                    title: {
                        text: '',
                        left: 'center',
                        textStyle:{
                            color:'#5bc0de'
                        }
                    },
                    dataZoom: [{
                        type: 'slider',
                        xAxisIndex: 0,
                        filterMode: 'weakFilter',
                        height: 20,
                        bottom: 10,
                        handleIcon: 'path://M10.7,11.9H9.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4h1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
                        handleSize: '80%',
                        showDetail: false,
                    }, {
                        type: 'inside',
                        id: 'insideX',
                        xAxisIndex: 0,
                        filterMode: 'weakFilter',
                        zoomOnMouseWheel: false,
                        moveOnMouseMove: true
                    }, {
                        type: 'slider',
                        yAxisIndex: 0,
                        zoomLock: true,
                        width: 10,
                        right: 10,
                        top: 70,
                        bottom: 20,
                        start: this.zoomH,
                        end: 100,
                        handleSize: 0,
                        showDetail: false,
                    }, {
                        type: 'inside',
                        id: 'insideY',
                        yAxisIndex: 0,
                        start: this.zoomH,
                        end: 100,
                        zoomOnMouseWheel: false,
                        moveOnMouseMove: true,
                        moveOnMouseWheel: true
                    }],
                    grid: {
                        bottom:'80px'
                    },
                    xAxis: {
                        type: "time",
                        min: this.duration[0],
                        max:this.duration[1],
                        scale: true,
                        nameTextStyle:{
                            color:'#5bc0de'
                        },
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
                        axisLabel: {
                            borderColor:'#5bc0de',
                            interval:0,
                            formatter: function(val) {
                                let date = dateFormat('yyyy-mm-dd HH:MM:SS', new Date(val))
                                return date;
                            }
                        }
                    },
                    yAxis: {
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
                            borderColor:'#5bc0de',
                            interval:0,
                            formatter: function(val) {
                                return val.replace(/[^\x00-\xff]/g,"$&\x01").replace(/.{20}\x01?/g,"$&\n").replace(/\x01/g,"");

                            }
                        },
                        data: this.categories
                    },
                    series: [{
                        type: 'custom',
                        renderItem: this.renderItem,
                        itemStyle: {
                            opacity: 0.8
                        },
                        encode: {
                            x: [1, 2],
                            y: 0
                        },
                        data: this.data
                    }]
                };
                let myChart = echarts.init(document.getElementById("chart"));
                myChart.setOption(option);
                window.addEventListener("resize",()=>{
                    myChart.resize();
                });
            },
            renderItem(params, api) {
                var categoryIndex = api.value(0);
                var start = api.coord([api.value(1), categoryIndex]);
                var end = api.coord([api.value(2), categoryIndex]);
                var height = api.size([0, 1])[1] * 0.6;

                var rectShape = echarts.graphic.clipRectByRect({
                    x: start[0],
                    y: start[1] - height / 2,
                    width: end[0] - start[0],
                    height: height
                }, {
                    x: params.coordSys.x,
                    y: params.coordSys.y,
                    width: params.coordSys.width,
                    height: params.coordSys.height
                });

                return rectShape && {
                    type: 'rect',
                    transition: ['shape'],
                    shape: rectShape,
                    style: api.style()
                };
            }
        }
    }
</script>

<style scoped>
    .content-bg , #chart{
        width: 100%;
        height:100%;
    }
</style>
