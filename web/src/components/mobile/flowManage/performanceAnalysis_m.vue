<template>
    <div class="content-bg">
        <div class="title">应用性能分析</div>
        <div class="timer"><dateLayout class="from-wapper" busName="performanceAnalysis" :defaultVal = "defaultVal"></dateLayout></div>
        <div class="item-wapper">
            <div class="item-title">应用平均响应时间统计</div>
            <div class="item-content">
                <performanceAnalysis_bar :params="param" :setIntervalObj="intervalObj"></performanceAnalysis_bar>
            </div>
        </div>
    </div>

</template>

<script>
    import dateLayout from '../../common/dateLayout'
    import performanceAnalysis_bar from '../../charts/flow/performance/performanceAnalysis_bar'
    import bus from '../../common/bus'
    import {dateFormat,setChartParam} from '../../../../static/js/common'
    export default {
        name: "performanceAnalysis_m",
        data() {
            return {
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
                    // jumpHtml('performanceAnalysisUrl'+data.name,'flowManage/performanceAnalysisUrl.vue',{ url:data.name,starttime:this.param.starttime,endtime:this.param.endtime},'平均响应时间')
                    let sss = ('performanceAnalysisUrl_m'+data.name).replace(/\//g,"&-")
                    let newRouters = [{
                        path:'/mobile',
                        component: resolve => require(['../../../components/mobile/common/home'], resolve),
                        children:[
                            {
                                path:'/mobile/'+sss,
                                name:'performanceAnalysisUrl_m'+data.name,
                                component: resolve => require(['@/components/mobile/flowManage/performanceAnalysisUrl_m.vue'], resolve),
                                meta: { title: '' }
                            }
                        ]
                    }]
                    this.$router.addRoutes(newRouters);
                    this.$router.push({path:'/mobile/'+sss,query: { url:data.name,starttime:this.param.starttime,endtime:this.param.endtime}})
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
    .content-bg{
        /*height: 1000px;*/
        font-size: 1rem;
        background: url("../../../../static/img/flow-index-bg2.png");
        background-size: 100% 100%;
    }
    .title{
        font-size: 1.3rem;
        font-weight: 600;
        color: #185bff;
        padding: 10px;
        text-align: center;
        text-shadow: none;
    }
    .item-wapper{
        height: auto;
        /*background: rgb(26,36,47);*/
        background: url("../../../../static/img/bd2.png");
        background-size: 100% 100%;
        margin:0.5rem 0.2rem;
    }
    .timer{
        display: flex;
        justify-content: center;
    }
    .item-title{
        height: 2.5rem;
        line-height: 2.5rem;
        font-size: 1rem;
        text-align: left;
        padding-left: 25px;
        /*background: url("../../../static/img/title-bg.png");*/
        background-size: 100% 100%;
    }
    .item-content{
        /*height: calc(100% - 1rem);*/
        height: 230px;
        box-sizing: border-box;
        padding: 0.5rem;
        padding-top: 0;
    }
</style>
