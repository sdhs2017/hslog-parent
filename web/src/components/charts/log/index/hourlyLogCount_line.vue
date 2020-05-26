<template>
    <!--今日日志数量统计--折线图-->
    <div class="eb">
        <v-echarts echartType="line" :echartData = "this.lineData" :busName="busName" ></v-echarts>
    </div>
</template>

<script>
    import vEcharts from '../../../common/echarts'
    import {dateFormat} from "../../../../../static/js/common";
    import bus from '../../../common/bus';

    export default {
        name: "logLevel_bar",
        props:{
            params:{
                type:Object
            },
            busName:{
                type:Object,
                default(){
                    return{
                        clickName:'',
                        exportName:''
                    }
                }
            },
            baseConProp:{
                type:Object,
                default(){
                    return{
                        title:'',
                    }
                }
            },
            url:{
                type:String
            }
        },
        data() {
            return {
                lineData:{//折线图数据
                    baseConfig:{
                        title:'',
                        xAxisName:'时间/时',
                        yAxisName:'数量/条',
                        hoverText:'日志数量'
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                }
            }
        },
        created(){
            this.lineData.baseConfig.title = this.baseConProp.title
        },
        watch:{
            params:{
                handler(newV,oldV) {
                    if(JSON.stringify(newV) != JSON.stringify(oldV)){
                       /* let todayDate = dateFormat('yyyy-mm-dd',new Date())
                        //this.params['@timestamp'] = todayDate;
                        this.getEchartData({'@timestamp':todayDate})*/
                        this.getEchartData(newV)
                    }

                },
                immediate: true,
                deep: true
            }
        },
        methods:{
            //获取每小时日志数
            getEchartData(params){
                this.$nextTick( ()=> {
                    this.$axios.post(this.$baseUrl+'/ecsCommon/getLogCountGroupByTime.do',this.$qs.stringify(params))
                        .then((res) => {
                            let xVal = [];
                            let yVal = [];
                            for(let i in res.data){
                                yVal.push(res.data[i].count)
                                xVal.push(res.data[i].hour)
                            }
                            //赋值
                            this.lineData.xAxisArr = xVal;
                            this.lineData.yAxisArr = yVal;
                        })
                        .catch((err) => {
                            console.log(err)
                        })
                })
            },
        },
        components:{
            vEcharts
        }
    }
</script>

<style scoped>
    .eb{
        position: relative;
        width: 100%;
        height: 100%;
    }
</style>
