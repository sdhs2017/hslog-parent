<template>
    <div class="content-bg">
        <div class="top-title">应用性能分析
            <v-basedate class="from-wapper" type="datetimerange" busName="performanceAnalysis"></v-basedate>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">
                    {{this.chartTitle}}
                    <span v-if="this.chartDataType !== 'all'" class="goBack" @click="goBack">返回</span>
                </p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="24">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.appAvgResTimeData" :busName="this.busName"></v-echarts>
                        </div>
                    </el-col>
                   <!-- <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="pie" :echartData = "this.ipPacketsData2" ></v-echarts>
                        </div>
                    </el-col>-->
                </el-row>
            </div>
           <!-- <div class="echarts-item">
                <p class="echarts-tit">资产（服务）数据包个数</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.domainPacketsData" ></v-echarts>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="pie" :echartData = "this.domainPacketsData2" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>-->
        </div>
    </div>

</template>

<script>
    import vBasedate from '../common/baseDate'
    import vEcharts from '../common/echarts'
    import bus from '../common/bus'
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "performanceAnalysis",
        data() {
            return {
                interTime:'',
                //刷新时间间隔
                refreshIntTime :'5000',
                //数据日期间隔
                dataTime:3600,
                busName:{
                    exportName:'',
                    clickName:'appAvgResTimeClick'
                },
                //图表类型
                chartDataType:'all',
                //图表标题
                chartTitle:'应用平均响应时间统计',
                allParams:{},
                oneParams:{},
                clickxAxis:'',
                //应用平均响应时间统计数据
                appAvgResTimeData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'应用名称',
                        yAxisName:'时间/ms',
                        rotate:'15',
                        dispose:true,
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                appAvgResTimeData2:{
                    baseConfig:{
                        title:'',
                        hoverText:'百分比'
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                }
            }
        },
        created(){
            /*监听日期改变*/
            bus.$on('performanceAnalysis',(arr)=>{
                //判断图表数据类型
                if(this.chartDataType === 'all'){
                    let paramObj = {
                        starttime:arr[0],
                        endtime:arr[1]
                    }
                    layer.load(1);
                    /*获取应用平均响应时间数据*/
                    this.getAppAvgResTimeData(paramObj);
                }else{
                    let obj = {
                        domain_url:this.clickxAxis,
                        starttime:arr[0],
                        endtime:arr[1]
                    }
                    layer.load(1);
                    this.getOneAppfuncResTimeData(obj);
                }
            })
            /*监听点击事件*/
            bus.$on('appAvgResTimeClick',(data)=>{
                //保存点击的x轴
                this.clickxAxis = data.name;
                let obj = {
                    starttime:this.allParams.starttime,
                    endtime:this.allParams.endtime,
                    domain_url:data.name
                }
                layer.load(1);
                this.getOneAppfuncResTimeData(obj);
            })
        },
        mounted(){},
        methods:{
            /*获取应用平均响应时间数据*/
            getAppAvgResTimeData(obj){
                let paramObj = JSON.stringify(obj)
                this.chartDataType = 'all'
                this.allParams = obj;
                this.chartTitle = "应用平均响应时间统计"
                this.busName.clickName = 'appAvgResTimeClick';
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getDomaiUrlAvgResponsetime.do',this.$qs.stringify({
                        hsData:paramObj
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data){
                                let obj = res.data[i];
                                xns1.push(obj.key);
                                yvs1.push(obj.value.toFixed(2))
                                /*yvs2.push({
                                    name:j,
                                    value:obj[j]
                                })*/
                            }
                            this.appAvgResTimeData.xAxisArr = xns1;
                            this.appAvgResTimeData.yAxisArr = yvs1;
                            //this.ipPacketsData2.yAxisArr = yvs2;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取单个应用功能URL平均响应时间*/
            getOneAppfuncResTimeData(obj){
                let paramObj = JSON.stringify(obj)
                this.chartDataType = 'one'
                this.chartTitle = this.clickxAxis + " 功能URL平均响应时间统计";
                this.busName.clickName = '';
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getRequestUrlAvgResponsetime.do',this.$qs.stringify({
                        hsData:paramObj
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data){
                                let obj = res.data[i];
                                xns1.push(obj.key);
                                yvs1.push(obj.value.toFixed(2))
                                /*yvs2.push({
                                    name:j,
                                    value:obj[j]
                                })*/
                            }
                            this.appAvgResTimeData.xAxisArr = xns1;
                            this.appAvgResTimeData.yAxisArr = yvs1;
                            //this.ipPacketsData2.yAxisArr = yvs2;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*返回*/
            goBack(){
                this.chartTitle = "应用平均响应时间统计"
                layer.load(1);
                /*获取应用平均响应时间数据*/
                this.getAppAvgResTimeData(this.allParams);
            }
        },
        beforeDestroy(){
        },
        components:{
            vBasedate,
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
    .goBack{
        float: left;
        margin-left: 20px;
        font-size: 15px;
        color: #15c5e3;
        cursor: pointer;
    }
    .goBack:hover{
        text-decoration: underline;
        color: #fff;
    }
</style>
