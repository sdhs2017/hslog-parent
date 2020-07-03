<template>
    <div class="content-bg">
        <div class="title">{{this.urlName}}</div>
        <div class="timer"><dateLayout class="from-wapper" :busName="busName" :defaultVal = "defaultVal"></dateLayout></div>
        <div class="item-wapper">
            <div class="item-title"> {{this.chartTitle}}</div>
            <div class="item-content">
                <performanceAnalysisUrl_bar :params="param" :setIntervalObj="intervalObj"></performanceAnalysisUrl_bar>
            </div>
        </div>
    </div>

</template>

<script>
    import dateLayout from '../../common/dateLayout'
    import performanceAnalysisUrl_bar from '../../charts/flow/performance/performanceAnalysisUrl_bar'
    import bus from '../../common/bus'
    import {dateFormat,setChartParam,savePath} from '../../../../static/js/common'
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

        },
        watch:{
            'urlName'(){
                if(this.urlName !== ''){
                    bus.$on(this.busName,(obj)=>{
                        let arr = setChartParam(obj);
                        this.param = arr[0];
                        this.param.queryParam = JSON.stringify({'domain_url.raw':this.urlName})
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
                vm.busName = 'performanceAnalysisUrl'+ to.query.url;
                vm.param={
                    intervalValue:'',
                    intervalType:'',
                    starttime:to.query.starttime,
                    endtime:to.query.endtime,
                    last:'',
                    queryParam:JSON.stringify({'domain_url.raw':to.query.url})
                }
                //将路由存放在本地 用来刷新页面时添加路由
                let sss ='/mobile/performanceAnalysisUrl_m'+to.query.url.replace(/\//g,"&-")
                let obj = {
                    path:sss,
                    component:'mobile/flowManage/performanceAnalysisUrl_m.vue',
                    title:''
                }
                sessionStorage.setItem(sss, JSON.stringify(obj))
               // savePath('/mobile/performanceAnalysisUrl'+to.query.url,'mobile/flowManage/performanceAnalysisUrl_m.vue')
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
