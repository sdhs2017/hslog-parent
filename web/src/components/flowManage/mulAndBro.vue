<template>
    <div class="content-bg">
        <div class="top-title">广播包/组播包
            <v-flowchartfrom class="from-wapper" refreshBus="mulAndBroRefreshBus" :refreshObj="refreshObj"></v-flowchartfrom>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">全局-广播包、组播包</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="24">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="timeline" :echartData = "this.allMulAndBroData" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>
    </div>
    
</template>

<script>
    import vFlowchartfrom from '../common/flowChartsFrom'
    import vEcharts from '../common/echarts'
    import bus from '../common/bus';
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "mulAndBro",
        data() {
            return {
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
                interTime:'',
                //刷新时间间隔
                refreshIntTime :'5000',
                //数据日期间隔
                dataTime:1,
                //全局广播包组播包个数
                allMulAndBroData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'时间',
                        yAxisName:'个数',
                        hoverText:'',
                    },
                    yAxisArr:[{
                        name:'组播包',
                        color:'rgb(15,219,243)',
                        data:[]
                    },{
                        name:'广播包',
                        color:'rgb(9,243,105)',
                        data:[]
                    }]
                }
            }
        },
        created(){
            /*监听刷新时间改变*/
            bus.$on('mulAndBroRefreshBus',(val)=>{
                this.refreshIntTime = val*1000;
                clearInterval(this.interTime);
                this.interTime = setInterval(()=>{
                    /*获取组播包广播包个数*/
                    this.getAllMulAndBroData(this.refreshIntTime/1000);
                },this.refreshIntTime);
            })
        },
        mounted(){
            /*获取组播包广播包个数*/
            this.getAllMulAndBroData(this.refreshIntTime/1000);
            /*循环获取数据*/
            this.interTime = setInterval(()=>{
                    /*获取组播包广播包个数*/
                    this.getAllMulAndBroData(this.refreshIntTime/1000);
            },this.refreshIntTime);
        },
        methods:{
            /*获取广播包组播包个数*/
            getAllMulAndBroData(timeInterval){
                this.$nextTick(()=> {

                    this.$axios.post(this.$baseUrl + '/flow/getMulticastAndBroadcastPacketTypeCount.do', this.$qs.stringify({
                        timeInterval:timeInterval
                    }))
                        .then(res => {
                            layer.closeAll('loading');
                            if (this.allMulAndBroData.yAxisArr[0].data.length < 60) {
                                this.allMulAndBroData.yAxisArr[0].data.push(res.data[0]);
                                this.allMulAndBroData.yAxisArr[1].data.push(res.data[1]);
                            } else {
                                this.allMulAndBroData.yAxisArr[0].data.shift();
                                this.allMulAndBroData.yAxisArr[0].data.push(res.data[0]);
                                this.allMulAndBroData.yAxisArr[1].data.push(res.data[1]);
                            }
                        })
                        .catch(err => {
                            layer.closeAll('loading');
                        })
                })
            }
        },
        beforeDestroy(){
            clearInterval(this.interTime);
        },
        components:{
            vFlowchartfrom,
            vEcharts
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
