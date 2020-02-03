<template>
    <div class="content-bg">
        <div class="top-title">广播包/组播包
            <v-flowchartfrom class="from-wapper" refreshBus="mulAndBroRefreshBus" :refreshObj="refreshObj" switchBus="mulAndBroSwitchBus" timeBus="mulAndBroTimeBus"></v-flowchartfrom>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">全局-广播包、组播包</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="24">
                        <div class="chart-wapper ip-chart">
                            <v-echarts :echartType="this.chartType" :echartData = "this.chartData" ></v-echarts>
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
                echartChange:false,
                chartData:{
                    baseConfig:{
                        title:''
                    }
                },
                chartType:'timeline',
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
                },
                allMulAndBroData2:{
                    baseConfig:{
                        title:'',
                        xAxisName:'类型',
                        yAxisName:'数据包/个数',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:['组播包','广播包'],
                    yAxisArr:[]

                }
            }
        },
        created(){
            /*监听刷新时间改变*/
            bus.$on('mulAndBroRefreshBus',(val)=>{
                this.refreshIntTime = val*1000;
                clearInterval(this.interTime);
                //开启轮训
                this.setIntervalFun();
            })
            /*监听switch改变*/
            bus.$on('mulAndBroSwitchBus',(obj)=>{
                this.echartChange = true;
                //判断改变的值
                if(!obj.switchVal){//停止轮训
                    //停止轮训
                    clearInterval(this.interTime);
                }else{//开启轮训
                    this.chartType = 'timeline'
                    let paramObj={
                        timeInterval:this.refreshIntTime/1000
                    }
                    /*获取全局数据包类型个数*/
                    this.getAllMulAndBroData(paramObj);
                    //开启轮训
                    this.setIntervalFun();
                }
            })
            /*监听日期改变*/
            bus.$on('mulAndBroTimeBus',(obj)=>{
                //设置图表类型
                this.chartType = 'bar'
                //定义参数
                let paramObj = {
                    starttime:obj.dateArr[0],
                    endtime:obj.dateArr[1]
                }
                /*获取全局数据包类型个数*/
                layer.load(1);
                this.getAllMulAndBroData(paramObj);
            })
        },
        mounted(){
            let paramObj={
                timeInterval:this.refreshIntTime/1000
            }
            /*获取组播包广播包个数*/
            this.getAllMulAndBroData(paramObj);
            /*循环获取数据*/
            this.setIntervalFun();
        },
        methods:{
            /*获取广播包组播包个数*/
            getAllMulAndBroData(paramObj){
                this.$nextTick(()=> {
                    this.allMulAndBroData.baseConfig.dispose = this.echartChange;
                    this.allMulAndBroData2.baseConfig.dispose = this.echartChange;
                    this.$axios.post(this.$baseUrl + '/flow/getMulticastAndBroadcastPacketTypeCount.do', this.$qs.stringify(paramObj))
                        .then(res => {
                            layer.closeAll('loading');
                            if(this.chartType === 'timeline'){
                                if (this.allMulAndBroData.yAxisArr[0].data.length < 60) {
                                    this.allMulAndBroData.yAxisArr[0].data.push(res.data[0].multicast);
                                    this.allMulAndBroData.yAxisArr[1].data.push(res.data[0].broadcast);
                                    this.chartData = this.allMulAndBroData;
                                } else {
                                    this.allMulAndBroData.yAxisArr[0].data.shift();
                                    this.allMulAndBroData.yAxisArr[0].data.push(res.data[0].multicast);
                                    this.allMulAndBroData.yAxisArr[1].data.push(res.data[0].broadcast);
                                }
                            }else{
                                let arr = [];
                                arr.push(res.data[0].multicast.value[1]);
                                arr.push(res.data[0].broadcast.value[1]);
                                this.allMulAndBroData2.yAxisArr= arr;
                                this.chartData = this.allMulAndBroData2;
                            }
                            //改变状态（第一次切换类型）
                            this.echartChange = false;
                        })
                        .catch(err => {
                            layer.closeAll('loading');
                        })
                })
            },
            setIntervalFun(){
                this.interTime = setInterval(()=>{
                    let paramObj={
                        timeInterval:this.refreshIntTime/1000
                    }
                    /*获取组播包广播包个数*/
                    this.getAllMulAndBroData(paramObj);
                },this.refreshIntTime);
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
