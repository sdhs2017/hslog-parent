<template>
    <!--单个资产每小时日志数量统计--折线图-->
    <div class="eb">
        <div style="width: 100%;height: 100%;display: flex;justify-content: center;align-items: center;overflow: hidden" v-if="errState">此为单个资产的报表,缺少必要条件。</div>
        <v-echarts echartType="line" :echartData = "this.lineData" :busName="busName" ></v-echarts>
    </div>
</template>

<script>
    import vEcharts from '../../../common/echarts'
    import {dateFormat} from "../../../../../static/js/common";
    import bus from '../../../common/bus';

    export default {
        name: "eqHourlyLogLevel_line",
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
                        title:'每小时日志数量统计',
                    }
                }
            }
        },
        data() {
            return {
                errState:false,
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
                    if(JSON.stringify(newV) != JSON.stringify(oldV) && JSON.stringify(newV) !== '{}'){
                        //判断条件合法性
                        if(newV.hsData && JSON.parse(newV.hsData)['fields.equipmentid']){//合法 显示正常数据
                            this.getEchartData(this.params)
                        }else{//显示错误提示
                            this.errState = true
                        }
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
