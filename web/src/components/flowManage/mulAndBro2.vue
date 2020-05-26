<template>
    <div class="content-bg">
        <div class="top-title">广播包/组播包
            <v-basedate class="from-wapper" type="datetimerange" busName="mulAndBroTimeBus2"></v-basedate>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">全局-广播包、组播包</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="24">
                        <div class="chart-wapper ip-chart">
                            <mulAndBro_bar :params="dateArr" ></mulAndBro_bar>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>
    </div>
    
</template>

<script>
    import vBasedate from '../common/baseDate'
    import mulAndBro_bar from '../charts/flow/mulAndBro/mulAndBro_bar'
    import bus from '../common/bus';
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "mulAndBro2",
        data() {
            return {
                dateArr:{}
            }
        },
        created(){
            /*监听日期改变*/
            bus.$on('mulAndBroTimeBus2',(arr)=>{
                layer.load(1)
                let paramObj = {
                    starttime:arr[0],
                    endtime:arr[1]
                }
                this.dateArr = paramObj;
            })
        },
        mounted(){
        },
        methods:{
        },
        beforeDestroy(){
            bus.$off('mulAndBroTimeBus2')
        },
        components:{
            vBasedate,
            mulAndBro_bar
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
