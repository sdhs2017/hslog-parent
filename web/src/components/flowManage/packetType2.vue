<template>
    <div class="content-bg">
        <div class="top-title">数据包类型
            <v-basedate class="from-wapper" type="datetimerange" busName="packetTypeTimeBus2"></v-basedate>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">全局-数据包类型个数</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="24">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.allPacketsTypeData2" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>
    </div>
    
</template>

<script>
    import vBasedate from '../common/baseDate'
    import vEcharts from '../common/echarts'
    import bus from '../common/bus';
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "packetType",
        data() {
            return {
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
                allPacketsTypeData2:{
                    baseConfig:{
                        title:'',
                        xAxisName:'类型',
                        yAxisName:'数据包/个数',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:['碎片包','正常包','特大包'],
                    yAxisArr:[]

                },
            }
        },
        created(){
            /*监听日期改变*/
            bus.$on('packetTypeTimeBus2',(arr)=>{
                //定义参数
                let paramObj = {
                    starttime:arr[0],
                    endtime:arr[1]
                }
                /*获取全局数据包类型个数*/
                layer.load(1);
                this.getAllPacketsTypeData(paramObj);
            })
        },
        mounted(){},
        methods:{
            /*获取全局数据包类型个数*/
            getAllPacketsTypeData(paramObj){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getPacketTypeCount.do',this.$qs.stringify(paramObj))
                        .then(res=>{
                            layer.closeAll('loading');
                            let arr = [];
                            arr.push(res.data[0].small.value[1]);
                            arr.push(res.data[0].normal.value[1]);
                            arr.push(res.data[0].big.value[1]);
                            this.allPacketsTypeData2.yAxisArr= arr;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            }
        },
        beforeDestroy(){
            //clearInterval(this.interTime);
        },
        components:{
            vBasedate,
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
