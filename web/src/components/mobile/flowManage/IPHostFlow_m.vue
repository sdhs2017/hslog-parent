<template>
    <div class="content-bg">
        <div class="title">IP主机流量</div>
        <div class="timer"><dateLayout class="from-wapper" busName="IPHostFlowTimeBus" :defaultVal = "defaultVal"></dateLayout></div>
        <div class="item-wapper">
            <div class="item-title">源IP地址流量排序</div>
            <div class="item-content">
                <srcIp_bar :params="param" :setIntervalObj="intervalObj" :baseConProp="{title:''}"></srcIp_bar>
            </div>
        </div>
        <div class="item-wapper">
            <div class="item-title">源IP地址流量排序</div>
            <div class="item-content">
                <srcIp_pie :params="param" :setIntervalObj="intervalObj" :baseConProp="{title:''}"></srcIp_pie>
            </div>
        </div>
        <div class="item-wapper">
            <div class="item-title">目的IP地址流量排序</div>
            <div class="item-content">
                <dstIp_bar :params="param" :setIntervalObj="intervalObj" :baseConProp="{title:''}"></dstIp_bar>
            </div>
        </div>
        <div class="item-wapper">
            <div class="item-title">目的IP地址流量排序</div>
            <div class="item-content">
                <dstIp_pie :params="param" :setIntervalObj="intervalObj" :baseConProp="{title:''}"></dstIp_pie>
            </div>
        </div>
    </div>

</template>

<script>
    import dateLayout from '../../common/dateLayout'
    import bus from '../../common/bus';
    import dstIp_bar from '../../charts/flow/ipHostFlow/dstIp_bar'
    import dstIp_pie from '../../charts/flow/ipHostFlow/dstIp_pie'
    import srcIp_bar from '../../charts/flow/ipHostFlow/srcIp_bar'
    import srcIp_pie from '../../charts/flow/ipHostFlow/srcIp_pie'
    import {dateFormat,setChartParam} from '../../../../static/js/common'
    export default {
        name: "IPHostFlow_m",
        data() {
            return {
                //请求参数
                param:{
                    intervalValue:'',
                    intervalType:'',
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
            bus.$on('IPHostFlowTimeBus',(obj)=>{
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
            bus.$off('IPHostFlowTimeBus')
        },
        components:{
            dateLayout,
            dstIp_bar,
            dstIp_pie,
            srcIp_bar,
            srcIp_pie
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
