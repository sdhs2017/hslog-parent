<template>
    <!--全局数据包个数--实时折线-->
    <div class="eb" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
        <div style="width: 100%;height: 100%;display: flex;justify-content: center;align-items: center;overflow: hidden" v-if="errState">{{errText}}</div>
        <v-echarts v-else echartType="timeline" :echartData = "this.timeLineData" :busName="busName" ></v-echarts>
    </div>
</template>

<script>
    import vEcharts from '../../../common/echarts_n'
    import {barDataFunc} from "../../common";
    import bus from '../../../common/bus';
    export default {
        name: "allPacketCount_timeline",
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
                        title:'全局数据包个数',
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
                errText:'此报表为实时报表，与此仪表盘性质不符',
                timeLineData:{
                    baseConfig:{
                        title:'全局数据包个数',
                        xAxisName:'时间',
                        yAxisName:'个数/个',
                        hoverText:'',
                    },
                    data:{}
                },
                //循环
                interval:'',
            }
        },
        created(){
            this.timeLineData.baseConfig.title = this.baseConProp.title
        },
        beforeDestroy(){
            clearInterval(this.interval)
        },
        watch:{
            params:{
                handler(newV,oldV) {
                    //判断值是否有变化
                    if(JSON.stringify(newV) !== '{}' && JSON.stringify(newV) !== JSON.stringify(oldV)){
                        //判断参数合理性
                        if(!this.setIntervalObj.state){
                            this.loading = true
                        }
                        this.getEchartData(this.params)

                    }
                },
                immediate: true,
                deep: true
            },
            setIntervalObj:{
                handler(newV,oldV) {
                    //判断是否启用轮询获取数据
                    if (this.setIntervalObj.state){
                        //判断参数是否合法(是否有刷新间隔时间)
                        if(this.setIntervalObj.interval){//合法
                            this.errState = false;
                            clearInterval(this.interval)
                            this.interval = setInterval(()=>{
                                this.getEchartData(this.params)
                            },this.setIntervalObj.interval)
                        }else{//不合法
                            this.errState = true;
                        }

                    }else {
                        clearInterval(this.interval)
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
                    this.$axios.post(this.$baseUrl+'/flow/getPacketCount_line.do',this.$qs.stringify(params))
                        .then((res) => {
                            this.loading = false;
                            let allObj = res.data;
                            if(allObj.success === 'true'){
                                this.errState = false;
                                let lineObj = allObj.data[0];
                                this.timeLineData.data = lineObj;
                            }else{
                                this.errState = true;
                                this.errText = allObj.message;
                                clearInterval(this.interval)
                            }
                        })
                        .catch((err) => {
                            this.loading = false;
                            layer.closeAll('loading');
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
