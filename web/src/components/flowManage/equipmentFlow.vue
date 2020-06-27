<template>
    <div class="content-bg">
        <div class="top-title">资产流量
            <dateLayout class="from-wapper" busName="equipmentFlowTimeBus" :defaultVal = "defaultVal"></dateLayout>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">资产（IP）数据包个数</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <eqippacketbar :params="param" :setIntervalObj="intervalObj"></eqippacketbar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <eqippacketpie :params="param" :setIntervalObj="intervalObj"></eqippacketpie>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">资产（服务）数据包个数</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <eqserverpacketbar :params="param" :setIntervalObj="intervalObj"></eqserverpacketbar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <eqserverpacketpie :params="param" :setIntervalObj="intervalObj"></eqserverpacketpie>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>
    </div>

</template>

<script>
    import dateLayout from '../common/dateLayout'
    import vEcharts from '../common/echarts'
    import eqippacketbar from '../charts/flow/equipmentFlow/eqIpPacket_bar'
    import eqippacketpie from '../charts/flow/equipmentFlow/eqIpPacket_pie'
    import eqserverpacketbar from '../charts/flow/equipmentFlow/eqServerPacket_bar'
    import eqserverpacketpie from '../charts/flow/equipmentFlow/eqServerPacket_pie'
    import bus from '../common/bus';
    import {dateFormat,setChartParam} from '../../../static/js/common'
    export default {
        name: "equipmentFlow2",
        data() {
            return {
                //请求参数
                param:{
                    intervalValue:'5',
                    intervalType:'second',
                    starttime:'',
                    endtime:'',
                    last:'1-hour'
                },
                //轮询参数
                intervalObj:{
                    state:true,
                    interval:'5000'
                },
                //时间控件参数
                defaultVal:{
                    //具体时间参数
                    lastVal:'1-hour',
                    //起始时间
                    starttime:'',
                    //结束时间
                    endtime:'',
                    //具体时间 类型状态
                    dateBlock:false,
                    //是否存在轮询框
                    isIntervalBox:true,
                    //轮询状态
                    intervalState:true,
                    //轮询数值间隔
                    intervalVal:'5',
                    //轮询参数类型
                    intervalType:'second',
                    //‘快速选择’功能参数类型
                    dateUnit:'hour',
                    //‘快速选择’功能参数数值
                    dateCount:'1',
                    //‘常用’ 时间值
                    commonlyVal:'',
                    //是否可以切换精确日期
                    changeState:true
                }
            }
        },
        created(){
            /*监听日期改变*/
            bus.$on('equipmentFlowTimeBus',(obj)=>{
                //设置参数对应
                let arr = setChartParam(obj);
                this.param = arr[0];
                this.intervalObj = arr[1]
            })
        },
        mounted(){},
        methods:{
        },
        beforeDestroy(){
            bus.$off('equipmentFlowTimeBus')
        },
        components:{
            dateLayout,
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
        margin-top: 10px;
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
