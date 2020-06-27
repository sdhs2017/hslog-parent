<template>
    <div class="content-bg">
        <div class="top-title">广播包/组播包
<!--            <v-flowchartfrom class="from-wapper" refreshBus="mulAndBroRefreshBus_n" :refreshObj="refreshObj" switchBus="mulAndBroSwitchBus_n" timeBus="mulAndBroTimeBus_n"></v-flowchartfrom>-->
            <dateLayout class="from-wapper" busName="mulAndBroRefreshBus_n" :defaultVal = "defaultVal"></dateLayout>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">全局-广播包、组播包</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="24">
                        <div class="chart-wapper ip-chart" v-if="chartType === 'timeline'">
                            <mulAndBro_timeline :params="param" :setIntervalObj="intervalObj"> </mulAndBro_timeline>
                        </div>
                        <div class="chart-wapper ip-chart" v-else>
                            <mulAndBro_bar :params="param" > </mulAndBro_bar>
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
    import bus from '../common/bus';
    import mulAndBro_bar from '../charts/flow/mulAndBro/mulAndBro_bar'
    import mulAndBro_timeline from '../charts/flow/mulAndBro/mulAndBro_timeline'
    import {dateFormat,setChartParam} from '../../../static/js/common'
    export default {
        name: "mulAndBro_n",
        data() {
            return {
                chartType:'timeline',
                //请求参数
                param:{
                    intervalValue:'5',
                    intervalType:'second',
                    starttime:'',
                    endtime:'',
                    last:'5-min'
                },
                //轮询参数
                intervalObj:{
                    state:true,
                    interval:'5000'
                },
                //时间控件参数
                defaultVal:{
                    //具体时间参数
                    lastVal:'',
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
                    dateUnit:'min',
                    //‘快速选择’功能参数数值
                    dateCount:'5',
                    //‘常用’ 时间值
                    commonlyVal:'',
                    changeState:true,
                }
            }
        },
        created(){
            /*监听刷新时间改变*/
            /*bus.$on('mulAndBroRefreshBus_n',(val)=>{
                this.intervalObj.interval = val*1000
                this.refreshIntTime = this.intervalObj.interval
            })
            /!*监听switch改变*!/
            bus.$on('mulAndBroSwitchBus_n',(obj)=>{
                this.intervalObj.state = obj.switchVal;
                if(obj.switchVal){
                    this.chartType = 'timeline'
                }
            })
            /!*监听日期改变*!/
            bus.$on('mulAndBroTimeBus_n',(obj)=>{
                this.chartType = 'bar'
                this.barParam = {
                    starttime:obj.dateArr[0],
                    endtime:obj.dateArr[1]
                }
            })*/
            /*监听日期改变*/
            bus.$on('mulAndBroRefreshBus_n',(obj)=>{
                //设置参数对应
                let arr = setChartParam(obj);
                this.param = arr[0];
                this.intervalObj = arr[1]
            })
        },
        mounted(){
        },
        methods:{
        },
        beforeDestroy(){
            bus.$off('mulAndBroTimeBus_n');
            bus.$off('mulAndBroSwitchBus_n');
            bus.$off('mulAndBroRefreshBus_n');
        },
        components:{
            dateLayout,
            vEcharts,
            mulAndBro_timeline,
            mulAndBro_bar
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
