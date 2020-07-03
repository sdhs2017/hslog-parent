<template>
    <div class="content-bg">
        <div class="title">全局实时流量</div>
        <div class="timer"><dateLayout class="from-wapper" busName="realTimeFlowRefreshBus" :defaultVal = "defaultVal"></dateLayout></div>
        <div class="item-wapper">
            <div class="item-title">全局-利用率百分比
                <div class="chart-from">
                    <span>带宽：</span>
                    <el-select v-model="bandwidth" placeholder="请选择" size="mini" style="width: 80px;">
                        <el-option
                            v-for="item in bandwidthArr"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </div>
            </div>
            <div class="item-content">
                <allUsedPer_timeline :params="param" :setIntervalObj="intervalObj" :otherParams="{bandwidth:bandwidth}"></allUsedPer_timeline>
            </div>
            <div class="item-content">
                <allUsedPer_gauge :params="param" :setIntervalObj="intervalObj"  :otherParams="{bandwidth:bandwidth}"></allUsedPer_gauge>
            </div>
        </div>
        <div class="item-wapper">
            <div class="item-title">全局数据包个数</div>
            <div class="item-content">
                <allPacketCount_timeline :params="param" :setIntervalObj="intervalObj" :baseConProp="{title:''}"></allPacketCount_timeline>
            </div>
        </div>
        <div class="item-wapper">
            <div class="item-title">全局实时流量</div>
            <div class="item-content">
                <allflow_timeline :params="param" :setIntervalObj="intervalObj" :baseConProp="{title:''}"></allflow_timeline>
            </div>
        </div>
    </div>
    
</template>

<script>
    import vFlowchartfrom from '../../common/flowChartsFrom'
    import dateLayout from '../../common/dateLayout'
    import {dateFormat,setChartParam} from '../../../../static/js/common'
    import allflow_timeline from '../../charts/flow/realTime/allflow_timeline'
    import allPacketCount_timeline from '../../charts/flow/realTime/allPacketCount_timeline'
    import allUsedPer_gauge from '../../charts/flow/realTime/allUsedPer_gauge'
    import allUsedPer_timeline from '../../charts/flow/realTime/allUsedPer_timeline'
    import bus from '../../common/bus';
    export default {
        name: "realTimeFlow",
        data() {
            return {
                param:{
                    //timeInterval:5
                    intervalValue:'5',
                    intervalType:'second',
                    starttime:'',
                    endtime:'',
                    last:'5-min'
                },
                barParam:{},
                intervalObj:{
                    state:true,
                    interval:'5000'
                },
                refreshObj:{
                    defaultVal:'5',
                    opt:[{
                        label:'5s',
                        value:'5'
                    },
                        {
                            label:'10s',
                            value:'10'
                        },
                        {
                            label:'20s',
                            value:'20'
                        }
                    ]
                },
                //刷新时间间隔
                refreshIntTime :'5000',
                chartType:'timeline',
                //全局利用率百分比
                bandwidth:100,
                bandwidthArr:[
                    {
                        label:'1M',
                        value:1
                    },
                    {
                        label:'2M',
                        value:2
                    },
                    {
                        label:'5M',
                        value:5
                    },
                    {
                        label:'10M',
                        value:10
                    },
                    {
                        label:'100M',
                        value:100
                    },
                    {
                        label:'1000M',
                        value:1000
                    },{
                        label:'10000M',
                        value:10000
                    }
                ],
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
            bus.$on('realTimeFlowRefreshBus',(obj)=>{
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
            bus.$off('realTimeFlowRefreshBus')
        },
        components:{
            vFlowchartfrom,
            dateLayout,
            allflow_timeline,
            allPacketCount_timeline,
            allUsedPer_gauge,
            allUsedPer_timeline
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
    .chart-from{
        float:right;
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
        position: relative;
    }
    .item-content{
        /*height: calc(100% - 1rem);*/
        height: 230px;
        box-sizing: border-box;
        padding: 0.5rem;
        padding-top: 0;
    }
    .chart-from{
        position: absolute;
        right: 26px;
        top: 3px;
        height: 35px;
        line-height: 35px;
        display: flex;
        align-items: center;
    }
    .chart-from span{
        font-size: 0.35rem;
    }
    .chart-from /deep/ .el-input--mini .el-input__inner{
        border: 1px solid #409eff;
        color: #409eff;
        background: 0;
        border-top: 0;
        border-left: 0;
        border-right: 0;
    }
</style>
