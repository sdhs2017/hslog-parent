<template>
    <!--全局利用率--仪表-->
    <div class="eb">
        <div style="width: 100%;height: 100%;display: flex;justify-content: center;align-items: center;overflow: hidden" v-if="errState">此报表为实时报表，与此仪表盘性质不符</div>
        <v-echarts v-else echartType="gauge" :echartData = "this.timeGuageData" :busName="busName" ></v-echarts>
    </div>
</template>

<script>
    import vEcharts from '../../../common/echarts'
    import {barDataFunc} from "../../common";
    import bus from '../../../common/bus';
    export default {
        name: "allUsedPer_gauge",
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
            setIntervalObj:{
                type:Object,
                default(){
                    return {
                        state:false,
                        interval:5000
                    }
                }
            },
            otherParams:{
                type:Object,
                default(){
                    return {
                        bandwidth:10
                    }
                }
            }
        },
        data() {
            return {
                errState:false,
                timeGuageData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'',
                        yAxisName:'',
                        hoverText:'全局',
                    },
                    yAxisArr:[{
                        name:'利用率',
                        data:[/*{value: 10, name: '利用率'}*/]
                    }]
                },
                //循环
                interval:'',
            }
        },
        created(){
            this.timeGuageData.baseConfig.title = this.baseConProp.title
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
                        if(newV.timeInterval){
                            this.getEchartData(this.params)
                        }else{
                            this.errState = true;
                        }

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
                    this.$axios.post(this.$baseUrl+'/flow/getPacketLengthPerSecond.do',this.$qs.stringify(params))
                        .then((res) => {
                            layer.closeAll('loading');
                            res.data[0].value[1] = res.data[0].value[1] * 8 * 1000 * 100 / this.setIntervalObj.interval / this.otherParams.bandwidth / 1024;
                            res.data[0].value[1] = res.data[0].value[1].toFixed(2);
                            this.timeGuageData.yAxisArr[0].data=[{
                                value:res.data[0].value[1],
                                name:'利用率'
                            }]
                        })
                        .catch((err) => {
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
