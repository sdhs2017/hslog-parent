<template>
    <div class="content-bg">
        <div class="top-title">数据包类型
<!--            <v-flowchartfrom class="from-wapper" refreshBus="realTimeFlowRefreshBus" dataBus="realTimeFlowDataBus"></v-flowchartfrom>-->
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">全局-数据包类型个数</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="24">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="timeline" :echartData = "this.allPacketsTypeData" ></v-echarts>
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
        name: "packetType",
        data() {
            return {
                interTime:'',
                //刷新时间间隔
                refreshIntTime :'10000',
                //数据日期间隔
                dataTime:1,
                //全局数据包类型个数
                allPacketsTypeData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'时间',
                        yAxisName:'数据包/个数',
                        hoverText:'',
                    },
                    yAxisArr:[{
                        name:'碎片包',
                        color:'rgb(15,219,243)',
                        data:[]
                    },{
                        name:'正常包',
                        color:'rgb(9,243,105)',
                        data:[]
                    },{
                        name:'特大包',
                        color:'rgb(243,220,15)',
                        data:[]
                    }]
                },
            }
        },
        created(){
            /*监听刷新时间改变*/
           /* bus.$on('realTimeFlowRefreshBus',(val)=>{
                    this.refreshIntTime = val;
                    clearInterval(this.interTime);
                    this.interTime = setInterval(()=>{
                        let endtime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
                        let st = new Date(new Date(endtime).valueOf() - this.dataTime * 60 * 60 * 1000);
                        let starttime = dateFormat('yyyy-mm-dd HH:MM:SS',st);
                        /!*获取业务系统统计- 浏览器*!/
                        this.getBrowserData(starttime,endtime);
                        /!*获取业务系统统计- 系统*!/
                        this.getSystemData(starttime,endtime);
                    },this.refreshIntTime);
            })
            /!*监听数据时间改变*!/
            bus.$on('realTimeFlowDataBus',(val)=>{
                this.dataTime = val;
            })*/

        },
        mounted(){
            //第一次获取数据
            let endtime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
            let st = new Date(new Date(endtime).valueOf() - this.dataTime * 60 * 60 * 1000);
            let starttime = dateFormat('yyyy-mm-dd HH:MM:SS',st);
            /*获取全局数据包类型个数*/
            this.getAllPacketsTypeData(endtime);
            /*循环获取数据*/
            this.interTime = setInterval(()=>{
                    let endtime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
                    let st = new Date(new Date(endtime).valueOf() - this.dataTime * 60 * 60 * 1000);
                    let starttime = dateFormat('yyyy-mm-dd HH:MM:SS',st);
                    /*获取全局数据包类型个数*/
                    this.getAllPacketsTypeData(endtime);
            },2000);
        },
        methods:{
            /*获取全局数据包类型个数*/
            getAllPacketsTypeData(time){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getPacketTypeCount.do',this.$qs.stringify({
                        endtime:time
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            if(this.allPacketsTypeData.yAxisArr[0].data.length < 60){
                                this.allPacketsTypeData.yAxisArr[0].data.push({
                                    name:time,
                                    value:[time,res.data[0].small]
                                })
                                this.allPacketsTypeData.yAxisArr[1].data.push({
                                    name:time,
                                    value:[time,res.data[0].normal]
                                })
                                this.allPacketsTypeData.yAxisArr[2].data.push({
                                    name:time,
                                    value:[time,res.data[0].big]
                                })
                            }else{
                                this.allPacketsTypeData.yAxisArr[0].data.shift();
                                this.allPacketsTypeData.yAxisArr[1].data.shift();
                                this.allPacketsTypeData.yAxisArr[2].data.shift();
                                this.allPacketsTypeData.yAxisArr[0].data.push({
                                    name:time,
                                    value:[time,res.data[0].small]
                                })
                                this.allPacketsTypeData.yAxisArr[1].data.push({
                                    name:time,
                                    value:[time,res.data[0].normal]
                                })
                                this.allPacketsTypeData.yAxisArr[2].data.push({
                                    name:time,
                                    value:[time,res.data[0].big]
                                })
                            }
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
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
