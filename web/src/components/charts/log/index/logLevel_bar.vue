<template>
    <!--日志级别数量统计--柱状图-->
    <div class="eb" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
       <v-echarts echartType="bar" :echartData = "this.barData" :busName="busName1" ></v-echarts>
    </div>
</template>

<script>
    import vEcharts from '../../../common/echarts'
    import bus from '../../../common/bus';
    export default {
        name: "logLevel_bar",
        props:{
            params:{
                type:Object
            },
            busName1:{
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
                        title:'日志级别数量统计',
                    }
                }
            }
        },
        data() {
            return {
                loading:false,
                barData:{//柱状图数据
                    baseConfig:{
                        title:'日志级别数量统计',
                        xAxisName:'级别',
                        yAxisName:'数量/条',
                        hoverText:'数量'
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
            }
        },
        created(){
           this.barData.baseConfig.title = this.baseConProp.title
        },
        watch:{
            params:{
                handler(newV,oldV) {
                    if(JSON.stringify(newV) != JSON.stringify(oldV)){
                        this.params.groupField='log.level'
                        this.getEchartData(this.params)
                    }
                },
                immediate: true,
                deep: true
            }
        },
        methods:{
            //获取数据
            getEchartData(params){
                this.loading = true
                this.$nextTick( ()=> {
                    this.$axios.post(this.$baseUrl+'/ecsCommon/getCountGroupByParam.do',this.$qs.stringify(params))
                        .then((res) => {
                            this.loading = false
                            const obj = res.data[0];
                            const xVal = [];//x轴数据
                            const yVal = [];//y轴数据
                            for(const i in obj){
                                const pieObj = {};
                                xVal.push(i);
                                yVal.push(obj[i])
                            }
                            //赋值
                            this.barData.xAxisArr = xVal;
                            this.barData.yAxisArr = yVal;

                        })
                        .catch((err) => {
                            console.log(err);
                            this.loading = false
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
