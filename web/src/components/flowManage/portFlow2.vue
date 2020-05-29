<template>
    <div class="content-bg">
        <div class="top-title">全局端口流量
            <v-basedate class="from-wapper" type="datetimerange" busName="portFlowTimeBus2"></v-basedate>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">目的端口总流量</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <dstPortAll_bar :params="dateArr"></dstPortAll_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <dstPortAll_pie :params="dateArr"></dstPortAll_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">TCP目的端口总流量</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <tcpDstPort_bar :params="dateArr"></tcpDstPort_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <tcpDstPort_pie :params="dateArr"></tcpDstPort_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">UDP目的端口总流量</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <udpDstPort_bar :params="dateArr"></udpDstPort_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <udpDstPort_pie :params="dateArr"></udpDstPort_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>
    </div>
    
</template>

<script>
    import vBasedate from '../common/baseDate'
    import dstPortAll_bar from '../charts/flow/portFlow/dstPortAll_bar'
    import dstPortAll_pie from '../charts/flow/portFlow/dstPortAll_pie'
    import tcpDstPort_bar from '../charts/flow/portFlow/tcpDstPort_bar'
    import tcpDstPort_pie from '../charts/flow/portFlow/tcpDstPort_pie'
    import udpDstPort_bar from '../charts/flow/portFlow/udpDstPort_bar'
    import udpDstPort_pie from '../charts/flow/portFlow/udpDstPort_pie'
    import bus from '../common/bus';
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "portFlow2",
        data() {
            return {
                dateArr:{},
            }
        },
        created(){
            /*监听日期改变*/
            bus.$on('portFlowTimeBus2',(arr)=>{
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
            bus.$off('portFlowTimeBus2')
        },
        components:{
            vBasedate,
            dstPortAll_bar,
            dstPortAll_pie,
            tcpDstPort_bar,
            tcpDstPort_pie,
            udpDstPort_bar,
            udpDstPort_pie
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
