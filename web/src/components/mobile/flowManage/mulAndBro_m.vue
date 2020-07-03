<template>
    <div class="content-bg">
        <div class="title">广播包/组播包</div>
        <div class="timer"><dateLayout class="from-wapper" busName="mulAndBroRefreshBus_n" :defaultVal = "defaultVal"></dateLayout></div>
        <div class="item-wapper">
            <div class="item-title">全局-广播包、组播包</div>
            <div class="item-content">
                <mulAndBro_timeline :params="param" :setIntervalObj="intervalObj"> </mulAndBro_timeline>
            </div>
        </div>
    </div>

</template>

<script>
    import dateLayout from '../../common/dateLayout'
    import vEcharts from '../../common/echarts'
    import bus from '../../common/bus';
    import mulAndBro_bar from '../../charts/flow/mulAndBro/mulAndBro_bar'
    import mulAndBro_timeline from '../../charts/flow/mulAndBro/mulAndBro_timeline'
    import {dateFormat,setChartParam} from '../../../../static/js/common'
    export default {
        name: "mulAndBro_m",
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
