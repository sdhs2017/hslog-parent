<template>
    <div class="content-bg">
        <div class="top-title">{{this.urlName}}
            <dateLayout class="from-wapper" :busName="busName" :defaultVal="defaultVal"></dateLayout>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">
                    {{this.chartTitle}}
                </p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="24">
                        <div class="chart-wapper ip-chart">
                            <performanceAnalysisUrl_bar :params="param" :setIntervalObj="intervalObj"></performanceAnalysisUrl_bar>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>
    </div>

</template>

<script>
    import dateLayout from '../common/dateLayout'
    import performanceAnalysisUrl_bar from '../charts/flow/performance/performanceAnalysisUrl_bar'
    import bus from '../common/bus'
    import {dateFormat,setChartParam,savePath} from '../../../static/js/common'
    export default {
        name: "performanceAnalysisUrl",
        data() {
            return {
                chartTitle:'功能URL平均响应时间统计',
                urlName:'',
                busName:'',
                //请求参数
                param:{

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
            this.defaultVal.endtime= this.$route.query.endtime;
            this.defaultVal.starttime =  this.$route.query.starttime;
            this.param={
                intervalValue:'',
                intervalType:'',
                starttime:this.$route.query.starttime,
                endtime:this.$route.query.endtime,
                last:'',
                queryParam:JSON.stringify({'domain_url.raw':this.$route.query.url})
            }
        },
        watch:{
            'urlName'(){
                if(this.urlName !== ''){
                    bus.$on(this.busName,(obj)=>{
                        let arr = setChartParam(obj);
                        this.param = arr[0];
                        this.param.queryParam = JSON.stringify({'domain_url.raw':this.urlName})
                        console.log(this.param)
                        this.intervalObj = arr[1]
                    })
                }
            }
        },
        beforeDestroy(){
            bus.$off(this.busName)
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                vm.$options.name = 'performanceAnalysisUrl'+ to.query.url;
                vm.busName = 'performanceAnalysisUrl'+to.query.url;
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'performanceAnalysisUrl'+to.query.url,
                    component:'flowManage/performanceAnalysisUrl.vue',
                    title:'平均响应时间'
                }
                //sessionStorage.setItem('/performanceAnalysisUrl'+to.query.url,JSON.stringify(obj))
                savePath('performanceAnalysisUrl'+to.query.url,'flowManage/performanceAnalysisUrl.vue','平均响应时间')
                if(vm.urlName === '' || vm.urlName !== to.query.url){
                    vm.urlName = to.query.url;
                }

            })

        },
        components:{
            dateLayout,
            performanceAnalysisUrl_bar
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
