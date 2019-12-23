<template>
    <div class="content-bg">
        <div class="top-title">全局实时流量
            <v-flowchartfrom class="from-wapper" refreshBus="realTimeFlowRefreshBus" :refreshObj="refreshObj"></v-flowchartfrom>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">全局-利用率百分比</p>
                <div class="chart-from">
                    <span>带宽：</span>
                    <el-select v-model="bandwidth" placeholder="请选择" size="mini" >
                        <el-option
                            v-for="item in bandwidthArr"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </div>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="timeline" :echartData = "this.allUsedData" ></v-echarts>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="gauge" :echartData = "this.allUsedData2" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <el-row :gutter="20" class="flow-row">
                <el-col :span="12">
                    <div class="echarts-item chart-wapper" style="padding-top: 30px">
                        <v-echarts echartType="timeline" :echartData = "this.allPacketsData" ></v-echarts>
                    </div>
                </el-col>
                <el-col :span="12">
                    <div class="echarts-item chart-wapper" style="padding-top: 30px">
                        <v-echarts echartType="timeline" :echartData = "this.chartData2" ></v-echarts>
                    </div>
                </el-col>
            </el-row>
        </div>
    </div>
    
</template>

<script>
    import vFlowchartfrom from '../common/flowChartsFrom'
    import vEcharts from '../common/echarts'
    import bus from '../common/bus';
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "realTimeFlow",
        data() {
            return {
                refreshObj:{
                    defaultVal:'5',
                    opt:[{
                        label:'5s',
                        value:'5'
                    },
                    {
                        label:'10s',
                        value:'10'
                    },
                    {
                        label:'20s',
                        value:'20'
                    }
                    ]
                },
                interTime:'',
                //刷新时间间隔
                refreshIntTime :5000,
                //数据日期间隔
                dataTime:1,
                //全局利用率百分比
                bandwidth:10,
                bandwidthArr:[
                    {
                        label:'10M',
                        value:10
                    },
                    {
                        label:'100M',
                        value:100
                    },
                    {
                        label:'1000M',
                        value:1000
                    }
                ],
                allUsedData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'时间',
                        yAxisName:'百分比/%',
                        hoverText:'',
                    },
                    yAxisArr:[{
                        name:'',
                        color:'rgb(15,219,243)',
                        data:[]
                    }]
                },
                allUsedData2:{
                    baseConfig:{
                        title:'',
                        xAxisName:'',
                        yAxisName:'',
                        hoverText:'全局',
                    },
                    yAxisArr:[{
                        name:'利用率',
                        data:[/*{value: 10, name: '利用率'}*/]
                    }]
                },
                //全局数据包个数
                allPacketsData:{
                    baseConfig:{
                        title:'全局数据包个数',
                        xAxisName:'时间',
                        yAxisName:'个数',
                        hoverText:'',
                    },
                    yAxisArr:[{
                        name:'',
                        color:'rgb(15,219,243)',
                        data:[]
                    }]
                },
                //全局实时流量
                chartData2:{
                    baseConfig:{
                        title:'全局实时流量',
                        xAxisName:'时间',
                        yAxisName:'大小/b',
                        hoverText:'',
                    },
                    yAxisArr:[{
                        name:'',
                        color:'rgb(15,219,243)',
                        data:[]
                    }]
                },
            }
        },
        created(){
            /*监听刷新时间改变*/
            bus.$on('realTimeFlowRefreshBus',(val)=>{
                    this.refreshIntTime = val*1000;
                    clearInterval(this.interTime);
                    this.interTime = setInterval(()=>{
                        /*获取全局利用率百分比*/
                        this.getAllUsedData(this.refreshIntTime/1000);
                        /*获取全局数据包个数*/
                        this.getAllPacketsData(this.refreshIntTime/1000);
                        /*获取数据-动态实时*/
                        this.getDataByTime(this.refreshIntTime/1000);

                    },this.refreshIntTime);
            })

        },
        mounted(){
            //第一次获取数据
            /*获取全局利用率百分比*/
            this.getAllUsedData(this.refreshIntTime/1000);
            /*获取全局数据包个数*/
            this.getAllPacketsData(this.refreshIntTime/1000);
            /*获取数据-动态实时*/
            this.getDataByTime(this.refreshIntTime/1000);
            /*循环获取数据*/
            this.interTime = setInterval(()=>{
                    /*获取全局利用率百分比*/
                    this.getAllUsedData(this.refreshIntTime/1000);
                    /*获取全局数据包个数*/
                    this.getAllPacketsData(this.refreshIntTime/1000);
                    /*获取数据-动态实时*/
                    this.getDataByTime(this.refreshIntTime/1000)
            },this.refreshIntTime);
        },
        methods:{
            /*获取全局利用率百分比*/
            getAllUsedData(timeInterval){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getPacketLengthPerSecond.do',this.$qs.stringify({
                        timeInterval:timeInterval
                    }))
                        .then(res=>{
                            res.data[0].value[1] = res.data[0].value[1] / this.refreshIntTime / this.bandwidth;
                            res.data[0].value[1] = res.data[0].value[1].toFixed(2);
                            if(this.allUsedData.yAxisArr[0].data.length < 60){
                                this.allUsedData.yAxisArr[0].data.push(res.data[0])
                            }else{
                                this.allUsedData.yAxisArr[0].data.shift();
                                this.allUsedData.yAxisArr[0].data.push(res.data[0])
                            }
                            this.allUsedData2.yAxisArr[0].data=[{
                                value:res.data[0].value[1],
                                name:'利用率'
                            }]
                        })
                        .catch(err=>{

                        })
                })
            },
            /*获取全局数据包个数*/
            getAllPacketsData(timeInterval){
                this.$nextTick(()=> {
                    this.$axios.post(this.$baseUrl + '/flow/getPacketCount.do', this.$qs.stringify({
                        timeInterval:timeInterval
                    }))
                        .then(res => {
                            layer.closeAll('loading');
                            if (this.allPacketsData.yAxisArr[0].data.length < 60) {
                                this.allPacketsData.yAxisArr[0].data.push(res.data[0]);
                            } else {
                                this.allPacketsData.yAxisArr[0].data.shift();
                                this.allPacketsData.yAxisArr[0].data.push(res.data[0]);
                            }
                        })
                        .catch(err => {
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取数据-动态实时*/
            getDataByTime(timeInterval){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getPacketLengthPerSecond.do',this.$qs.stringify({
                        timeInterval:timeInterval
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            if(this.chartData2.yAxisArr[0].data.length < 60){
                                this.chartData2.yAxisArr[0].data.push(res.data[0])
                            }else{
                                this.chartData2.yAxisArr[0].data.shift();
                                this.chartData2.yAxisArr[0].data.push(res.data[0])
                            }
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

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
            }
        },
        beforeDestroy(){
            clearInterval(this.interTime);
        },
        components:{
            vFlowchartfrom,
            vEcharts
        }
    }
</script>

<style scoped>
    .from-wapper{
        float: right;
        margin-right: 10px;
        margin-top: 5px;
    }
    .flow-echarts-con{
        padding: 20px;
        /*padding-bottom: 80px;*/
        /*height: calc(100% - 100px);*/
    }
    .echarts-item{
        background: #303e4e;
        margin: 20px 0;

    }
    .echarts-tit{
        height: 80px;
        line-height: 80px;
        text-align: center;
        font-weight: 600;
        font-size: 21px;
        color: #5bc0de;
    }
    .flow-row{
        padding-bottom: 80px;
        margin: 0 20px;
    }
    .chart-wapper{
        height: 60vh;
        min-height: 420px;
        /*padding-top: 50px;*/
        /*background: #303e4e;*/
    }
    .date-wapper{
        margin-bottom: 20px;
        padding-left: 50px;
    }
    .chart-from{
        padding-left: 50px;
    }
</style>
