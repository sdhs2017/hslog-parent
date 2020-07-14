<template>
    <div class="content-bg">
        <div class="top-title">User-Agent信息
            <dateLayout class="from-wapper" busName="userAgentInfoDataBus" :defaultVal = "defaultVal"></dateLayout>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">业务访问用户统计-操作系统</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper system-chart">
                            <agentSystem_bar :params="param" :setIntervalObj="intervalObj"></agentSystem_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper system-chart">
                            <agentSystem_pie :params="param" :setIntervalObj="intervalObj"></agentSystem_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>

            <div class="echarts-item">
                <p class="echarts-tit">业务访问用户统计-浏览器</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper browser-chart">
                            <agentBrowser_bar :params="param" :setIntervalObj="intervalObj"></agentBrowser_bar>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper browser-chart">
                            <agentBrowser_pie :params="param" :setIntervalObj="intervalObj"></agentBrowser_pie>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>
    </div>
    
</template>

<script>
    import dateLayout from '../common/dateLayout'
    import bus from '../common/bus';
    import agentBrowser_bar from '../charts/flow/userAgentInfo/agentBrowser_bar'
    import agentBrowser_pie from '../charts/flow/userAgentInfo/agentBrowser_pie'
    import agentSystem_bar from '../charts/flow/userAgentInfo/agentSystem_bar'
    import agentSystem_pie from '../charts/flow/userAgentInfo/agentSystem_pie'
    import {dateFormat,setChartParam} from '../../../static/js/common'
    export default {
        name: "userAgentInfo",
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
            bus.$on('userAgentInfoDataBus',(obj)=>{
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
            bus.$off('userAgentInfoDataBus');
        },
        components:{
            dateLayout,
            agentBrowser_bar,
            agentBrowser_pie,
            agentSystem_bar,
            agentSystem_pie
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
