<template>
    <!--单个资产(syslog)每小时事件数量统计--多条折线图-->
    <div class="eb" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
        <div style="width: 100%;height: 100%;display: flex;justify-content: center;align-items: center;overflow: hidden" v-if="errState">此为单个资产的报表,缺少必要条件。</div>
        <v-echarts echartType="moreline" :echartData = "this.moreLineData" :busName="busName" ></v-echarts>
    </div>
</template>

<script>
    import vEcharts from '../../../common/echarts'
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
            }
        },
        data() {
            return {
                loading:false,
                errState:false,
                moreLineData:{//折线图数据
                    baseConfig:{
                        title:'',
                        xAxisName:'时间/时',
                        yAxisName:'数量/条',
                        hoverText:'数量',
                        dispose:true
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                }
            }
        },
        created(){
            this.moreLineData.baseConfig.title = this.baseConProp.title
        },
        watch:{
            params:{
                handler(newV,oldV) {
                    if(JSON.stringify(newV) != JSON.stringify(oldV) && JSON.stringify(newV) !== '{}'){
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
                this.loading = true;
                this.$nextTick( ()=> {
                    this.$axios.post(this.$baseUrl+'/ecsSyslog/getCountGroupByEventType.do',this.$qs.stringify(params))
                        .then((res) => {
                            this.loading = false;
                            let color = ['#00EABD','#20C1F3','#FC686F','#F9D124','#DE1AFB','#C0D7FC','#A9F4B7','#FF9E96','#75B568','#323A81'];
                            this.moreLineData.yAxisArr = [];
                            this.moreLineData.xAxisArr = [];
                            let whole = {
                                name:'全部',
                                color:'#61a0a8',
                                data:[]
                            }
                            let high = {
                                name:'高危',
                                color:'#c64541',
                                data:[]
                            }
                            let middle = {
                                name:'中危',
                                color:'#d48265',
                                data:[]
                            }
                            let low = {
                                name:'普通',
                                color:'#95c9b1',
                                data:[]
                            }
                            for(let i = 0;i<24;i++){
                                whole.data.push(res.data[0][i].count);
                                high.data.push(res.data[1][i].count);
                                middle.data.push(res.data[2][i].count);
                                low.data.push(res.data[3][i].count);
                                this.moreLineData.xAxisArr.push(i)
                            }
                            this.moreLineData.yAxisArr.push(whole)
                            this.moreLineData.yAxisArr.push(high)
                            this.moreLineData.yAxisArr.push(middle)
                            this.moreLineData.yAxisArr.push(low)
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
