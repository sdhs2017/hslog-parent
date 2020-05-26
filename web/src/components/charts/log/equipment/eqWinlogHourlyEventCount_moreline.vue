<template>
    <!--单个资产(winlog)每小时事件数量统计--多条折线图-->
    <div class="eb">
        <div style="width: 100%;height: 100%;display: flex;justify-content: center;align-items: center;overflow: hidden" v-if="errState">此为单个资产的报表,缺少必要条件。</div>
        <v-echarts echartType="moreline" :echartData = "this.moreLineData" :busName="busName" ></v-echarts>
    </div>
</template>

<script>
    import vEcharts from '../../../common/echarts'
    export default {
        name: "eqWinlogHourlyEventCount_moreline",
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
                errState:false,
                moreLineData:{//折线图数据
                    baseConfig:{
                        title:'',
                        xAxisName:'时间/时',
                        yAxisName:'数量/条',
                        hoverText:'数量',
                        dispose:true
                    },
                    xAxisArr:["00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"],
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
                        if(newV.hsData != {} && JSON.parse(newV.hsData)['fields.equipmentid']){//合法 显示正常数据
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
                    this.$axios.post(this.$baseUrl+'/ecsCommon/getCountGroupByTimeAndEvent.do',this.$qs.stringify(params))
                        .then((res) => {
                            this.moreLineData.yAxisArr = [];
                            let color = ['#00EABD','#20C1F3','#FC686F','#F9D124','#DE1AFB','#C0D7FC','#A9F4B7','#FF9E96','#75B568','#323A81'];
                            let k = 0;
                            for(let i in res.data[0]){
                                let item = res.data[0][i];
                                let data=[];
                                for (let j in item){
                                    data.push(item[j].count)
                                }
                                let obj = {
                                    name:i,
                                    color:color[k],
                                    data:data
                                }
                                this.moreLineData.yAxisArr.push(obj);
                                k++;
                                if(k === color.length){
                                    k = 0;
                                }
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
