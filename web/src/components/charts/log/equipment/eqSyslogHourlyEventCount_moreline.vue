<template>
    <!--单个资产(syslog)每小时事件数量统计--多条折线图-->
    <div class="eb" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
        <div style="width: 100%;height: 100%;display: flex;justify-content: center;align-items: center;overflow: hidden" v-if="errState">{{errText}}</div>
        <v-echarts echartType="line" :echartData = "this.moreLineData" :busName="busName" ></v-echarts>
    </div>
</template>

<script>
    import vEcharts from '../../../common/echarts_n'
    export default {
        name: "eqSyslogHourlyEventCount_moreline",
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
                        title:'每小时事件数量统计',
                    }
                }
            },
            setIntervalObj:{
                type:Object,
                default(){
                    return {
                        state:false,
                        interval:5000
                    }
                }
            }
        },
        data() {
            return {
                loading:false,
                errState:false,
                errText:'此为单个资产的报表,缺少必要条件。',
                moreLineData:{//折线图数据
                    baseConfig:{
                        title:'',
                        xAxisName:'时间/时',
                        yAxisName:'数量/条',
                        hoverText:'数量',
                        rotate:'25'
                    },
                    data:{
                        dimensions:[],
                        source:[]
                    }
                },
                interval:''
            }
        },
        created(){
            this.moreLineData.baseConfig.title = this.baseConProp.title
        },
        beforeDestroy(){
            clearInterval(this.interval)
        },
        watch:{
            params:{
                handler(newV,oldV) {
                    if(JSON.stringify(newV) != JSON.stringify(oldV) && JSON.stringify(newV) !== '{}'){
                        if(newV.queryParam && JSON.parse(newV.queryParam)['fields.equipmentid']){//合法 显示正常数据
                            if(!this.setIntervalObj.state){
                                this.loading = true
                            }
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
                    this.$axios.post(this.$baseUrl+'/log/getCountGroupByEventType_line.do',this.$qs.stringify(params))
                        .then((res) => {
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success === 'true'){
                                this.errState = false;
                                this.moreLineData.data = obj.data[0]
                            }else{
                                this.errState = true;
                                this.errText = obj.message;
                                clearInterval(this.interval)
                            }
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
