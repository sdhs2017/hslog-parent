<template>
    <div class="content-bg">
        <div class="top-title">协议流量
            <dateLayout class="from-wapper" busName="protocolFlowTimeBus" :defaultVal = "defaultVal"></dateLayout>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">传输层协议长度统计</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <transport_bar :params="param" :setIntervalObj="intervalObj"></transport_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <transport_pie :params="param" :setIntervalObj="intervalObj"></transport_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">应用层协议长度统计</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <application_bar :params="param" :setIntervalObj="intervalObj"></application_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <application_pie :params="param" :setIntervalObj="intervalObj"></application_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">协议长度综合统计</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <multiple_bar :params="param" :setIntervalObj="intervalObj"></multiple_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <multiple_pie :params="param" :setIntervalObj="intervalObj"></multiple_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>
    </div>

</template>

<script>
    import dateLayout from '../common/dateLayout'
    import application_bar from '../charts/flow/protocoFlow/application_bar'
    import application_pie from '../charts/flow/protocoFlow/application_pie'
    import multiple_bar from '../charts/flow/protocoFlow/multiple_bar'
    import multiple_pie from '../charts/flow/protocoFlow/multiple_pie'
    import transport_bar from '../charts/flow/protocoFlow/transport_bar'
    import transport_pie from '../charts/flow/protocoFlow/transport_pie'
    import bus from '../common/bus';
    import {dateFormat,setChartParam} from '../../../static/js/common'
    export default {
        name: "protocolFlow",
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
                    state:false,
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
                    intervalState:false,
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
            bus.$on('protocolFlowTimeBus',(obj)=>{
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
            bus.$off('protocolFlowTimeBus')
        },
        components:{
            dateLayout,
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
