<template>
    <div class="content-bg">
        <div class="top-title">协议流量
            <v-basedate class="from-wapper" type="datetimerange" busName="protocolFlowTimeBus2"></v-basedate>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">传输层协议长度统计</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <transport_bar :params="dateArr"></transport_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <transport_pie :params="dateArr"></transport_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">应用层协议长度统计</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <application_bar :params="dateArr"></application_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <application_pie :params="dateArr"></application_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">协议长度综合统计</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <multiple_bar :params="dateArr"></multiple_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <multiple_pie :params="dateArr"></multiple_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>
    </div>
    
</template>

<script>
    import vBasedate from '../common/baseDate'
    import application_bar from '../charts/flow/protocoFlow/application_bar'
    import application_pie from '../charts/flow/protocoFlow/application_pie'
    import multiple_bar from '../charts/flow/protocoFlow/multiple_bar'
    import multiple_pie from '../charts/flow/protocoFlow/multiple_pie'
    import transport_bar from '../charts/flow/protocoFlow/transport_bar'
    import transport_pie from '../charts/flow/protocoFlow/transport_pie'
    import bus from '../common/bus';
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "protocolFlow2",
        data() {
            return {
                dateArr:{},
            }
        },
        created(){
            /*监听日期改变*/
            bus.$on('protocolFlowTimeBus2',(arr)=>{
                layer.load(1);
                let paramObj = {
                    starttime:arr[0],
                    endtime:arr[1]
                }
                this.dateArr = paramObj;
            })
        },
        mounted(){},
        methods:{
        },
        beforeDestroy(){
            bus.$off('protocolFlowTimeBus2')
        },
        components:{
            vBasedate,
            application_bar,
            application_pie,
            multiple_bar,
            multiple_pie,
            transport_bar,
            transport_pie
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
