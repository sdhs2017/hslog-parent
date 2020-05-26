<template>
    <!--日志级别数量统计--饼图-->
    <div class="eb" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
       <v-echarts echartType="pie" :echartData = "this.pieData" :busName="busName" ></v-echarts>
   </div>
</template>

<script>
    import vEcharts from '../../../common/echarts'
    import bus from '../../../common/bus';
    export default {
        name: "logLevel_pie",
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
                        title:'日志级别数量统计',
                    }
                }
            }
        },
        data() {
            return {
                loading:false,
                pieData:{//饼状图数据
                    baseConfig:{
                        title:'日志级别数量统计',
                        hoverText:'百分比'
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
            }
        },
        created(){
            this.pieData.baseConfig.title = this.baseConProp.title
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
            //获取饼图 数据
            getEchartData(params){
                this.loading = true;
                this.$nextTick( ()=> {
                    this.$axios.post(this.$baseUrl+'/ecsCommon/getCountGroupByParam.do',this.$qs.stringify(params))
                        .then((res) => {
                            this.loading = false;
                            const obj = res.data[0];
                            const pieVAl = [];//饼图数据
                            for(const i in obj){
                                const pieObj = {};
                                pieObj.value = obj[i];
                                pieObj.name = i;
                                pieVAl.push(pieObj)
                            }
                            this.pieData.yAxisArr = pieVAl;
                        })
                        .catch((err) => {
                            console.log(err)
                            this.loading = false;
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
