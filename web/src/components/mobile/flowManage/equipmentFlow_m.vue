<template>
    <div class="content-bg">
        <div class="title">资产流量</div>
        <div class="timer"><dateLayout class="from-wapper" busName="equipmentFlowTimeBus" :defaultVal = "defaultVal"></dateLayout></div>
        <div class="item-wapper">
            <div class="item-title">资产（IP）数据包个数</div>
            <div class="item-content">
                <eqippacketbar :params="param" :setIntervalObj="intervalObj" :baseConProp="{title:''}"></eqippacketbar>
            </div>
        </div>
        <div class="item-wapper">
            <div class="item-title">资产（IP）数据包个数</div>
            <div class="item-content">
                <eqippacketpie :params="param" :setIntervalObj="intervalObj" :baseConProp="{title:''}"></eqippacketpie>
            </div>
        </div>
        <div class="item-wapper">
            <div class="item-title">资产（服务）数据包个数</div>
            <div class="item-content">
                <eqserverpacketbar :params="param" :setIntervalObj="intervalObj" :baseConProp="{title:''}"></eqserverpacketbar>
            </div>
        </div>
        <div class="item-wapper">
            <div class="item-title">资产（服务）数据包个数</div>
            <div class="item-content">
                <eqserverpacketpie :params="param" :setIntervalObj="intervalObj" :baseConProp="{title:''}"></eqserverpacketpie>
            </div>
        </div>
    </div>

</template>

<script>
    import dateLayout from '../../common/dateLayout'
    import vEcharts from '../../common/echarts'
    import eqippacketbar from '../../charts/flow/equipmentFlow/eqIpPacket_bar'
    import eqippacketpie from '../../charts/flow/equipmentFlow/eqIpPacket_pie'
    import eqserverpacketbar from '../../charts/flow/equipmentFlow/eqServerPacket_bar'
    import eqserverpacketpie from '../../charts/flow/equipmentFlow/eqServerPacket_pie'
    import bus from '../../common/bus';
    import {dateFormat,setChartParam} from '../../../../static/js/common'
    export default {
        name: "equipmentFlow_m",
        data() {
            return {
                //请求参数
                param:{
                    intervalValue:'',
                    intervalType:'',
                    starttime:'',
                    endtime:'',
                    last:'7-day'
                },
                //轮询参数
                intervalObj:{
                    state:false,
                    interval:'5000'
                },
                //时间控件参数
                defaultVal:{
                    //具体时间参数
                    lastVal:'7-day',
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
                    dateUnit:'day',
                    //‘快速选择’功能参数数值
                    dateCount:'7',
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
