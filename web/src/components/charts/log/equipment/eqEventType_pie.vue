<template>
    <!--单个资产事件级别数量统计--柱状图-->
    <div class="eb">
        <div style="width: 100%;height: 100%;display: flex;justify-content: center;align-items: center;overflow: hidden" v-if="errState">此为单个资产的报表,缺少必要条件。</div>
        <v-echarts echartType="pie" :echartData = "this.pieData" :busName="busName" ></v-echarts>
    </div>
</template>

<script>
    import vEcharts from '../../../common/echarts'
    import bus from '../../../common/bus';
    export default {
        name: "eqEventType_pie",
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
                        title:'事件类型数量统计',
                    }
                }
            }
        },
        data() {
            return {
                errState:false,
                pieData:{//柱状图数据
                    baseConfig:{
                        title:'',
                        xAxisName:'类型',
                        yAxisName:'数量/条',
                        hoverText:'数量',
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
            //获取数据
            getEchartData(params){
                this.$nextTick( ()=> {
                    this.$axios.post(this.$baseUrl+'/ecsSyslog/getCountGroupByParam.do',this.$qs.stringify(params))
                        .then((res) => {
                            //事件饼图
                            this.pieData.yAxisArr = [];
                            for(let i in res.data[0]){
                                this.pieData.yAxisArr.push({name:i,value:res.data[0][i]});
                            }
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
