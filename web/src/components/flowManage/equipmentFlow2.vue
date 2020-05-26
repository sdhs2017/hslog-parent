<template>
    <div class="content-bg">
        <div class="top-title">资产流量
            <v-basedate class="from-wapper" type="datetimerange" busName="equipmentFlowTimeBus2_n"></v-basedate>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">资产（IP）数据包个数</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <eqippacketbar :params="dateArr"></eqippacketbar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <eqippacketpie :params="dateArr"></eqippacketpie>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">资产（服务）数据包个数</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <eqserverpacketbar :params="dateArr"></eqserverpacketbar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <eqserverpacketpie :params="dateArr"></eqserverpacketpie>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>
    </div>

</template>

<script>
    import vBasedate from '../common/baseDate'
    import vEcharts from '../common/echarts'
    import eqippacketbar from '../charts/flow/equipmentFlow/eqIpPacket_bar'
    import eqippacketpie from '../charts/flow/equipmentFlow/eqIpPacket_pie'
    import eqserverpacketbar from '../charts/flow/equipmentFlow/eqServerPacket_bar'
    import eqserverpacketpie from '../charts/flow/equipmentFlow/eqServerPacket_pie'
    import bus from '../common/bus';
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "equipmentFlow2",
        data() {
            return {
                dateArr:{},
            }
        },
        created(){
            /*监听日期改变*/
            bus.$on('equipmentFlowTimeBus2_n',(arr)=>{
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
            bus.$off('equipmentFlowTimeBus2_n')
        },
        components:{
            vBasedate,
            vEcharts,
            eqippacketbar,
            eqippacketpie,
            eqserverpacketbar,
            eqserverpacketpie
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
