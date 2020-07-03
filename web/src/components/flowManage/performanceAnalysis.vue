<template>
    <div class="content-bg">
        <div class="top-title">应用性能分析
            <dateLayout class="from-wapper" busName="performanceAnalysis" :defaultVal = "defaultVal"></dateLayout>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">
                    {{this.chartTitle}}
                </p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="24">
                        <div class="chart-wapper ip-chart">
                            <performanceAnalysis_bar :params="param" :setIntervalObj="intervalObj" :busName="busName"></performanceAnalysis_bar>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>
    </div>

</template>

<script>
    import dateLayout from '../common/dateLayout'
    import performanceAnalysis_bar from '../charts/flow/performance/performanceAnalysis_bar'
    import bus from '../common/bus'
    import {dateFormat,setChartParam,jumpHtml} from '../../../static/js/common'
    export default {
        name: "performanceAnalysis",
        data() {
            return {
                chartTitle:'应用平均响应时间统计',
                busName:{
                    clickName:'appAvgResTimeClick',
                    exportName:''
                },
                //请求参数
                param:{
                    intervalValue:'',
                    intervalType:'',
                    starttime:'',
                    endtime:'',
                    last:''
                },
                //轮询参数
                intervalObj:{
                    state:false,
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
                    dateBlock:true,
                    //是否存在轮询框
                    isIntervalBox:true,
                    //轮询状态
                    intervalState:false,
                    //轮询数值间隔
                    intervalVal:'',
                    //轮询参数类型
                    intervalType:'',
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
            //初始时间
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 1);
            this.defaultVal.endtime= dateFormat('yyyy-mm-dd HH:MM:SS',end);
            this.defaultVal.starttime = dateFormat('yyyy-mm-dd HH:MM:SS',start);
            this.param.endtime= dateFormat('yyyy-mm-dd HH:MM:SS',end);
            this.param.starttime = dateFormat('yyyy-mm-dd HH:MM:SS',start);
            /*监听日期改变*/
            bus.$on('performanceAnalysis',(obj)=>{
                //设置参数对应
                let arr = setChartParam(obj);
                this.param = arr[0];
                this.intervalObj = arr[1]
            })
            /*监听点击事件*/
            bus.$on(this.busName.clickName,(data)=>{
                if(!this.intervalObj.state){
                    jumpHtml('performanceAnalysisUrl'+data.name,'flowManage/performanceAnalysisUrl.vue',{ url:data.name,starttime:this.param.starttime,endtime:this.param.endtime},'平均响应时间')
                }
            })
        },
        mounted(){},
        methods:{
        },
        beforeDestroy(){
            bus.$off('performanceAnalysis')
            bus.$off(this.busName.clickName)
        },
        components:{
            dateLayout,
            performanceAnalysis_bar
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
        text-align: center;
        font-weight: 600;
        font-size: 21px;
        color: #5bc0de;
        display: flex;
        justify-content: center;
        align-items: center;
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
        margin-top: 15px;
        color: #15c5e3;
        cursor: pointer;
    }
    .goBack:hover{
        text-decoration: underline;
        color: #fff;
    }
</style>
