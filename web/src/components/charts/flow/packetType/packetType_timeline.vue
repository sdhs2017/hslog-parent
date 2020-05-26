<template>
    <!--数据包类型统计--实时折线-->
    <div class="eb">
        <div style="width: 100%;height: 100%;display: flex;justify-content: center;align-items: center;overflow: hidden" v-if="errState">此报表为实时报表，与此仪表盘性质不符</div>
        <v-echarts v-else echartType="timeline" :echartData = "this.timeLineData" :busName="busName" ></v-echarts>
    </div>
</template>

<script>
    import vEcharts from '../../../common/echarts'
    import {barDataFunc} from "../../common";
    import bus from '../../../common/bus';
    export default {
        name: "packetType_timeline",
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
            }
        },
        data() {
            return {
                errState:false,
                timeLineData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'时间',
                        yAxisName:'数据包/个数',
                        hoverText:'',
                    },
                    yAxisArr:[{
                        name:'碎片包',
                        color:'rgb(15,219,243)',
                        data:[]
                    },{
                        name:'正常包',
                        color:'rgb(9,243,105)',
                        data:[]
                    },{
                        name:'特大包',
                        color:'rgb(243,220,15)',
                        data:[]
                    }]
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
                    this.$axios.post(this.$baseUrl+'/flow/getPacketTypeCount.do',this.$qs.stringify(params))
                        .then((res) => {
                            layer.closeAll('loading');
                            if(this.timeLineData.yAxisArr[0].data.length < 60){
                                this.timeLineData.yAxisArr[0].data.push(res.data[0].small);
                                this.timeLineData.yAxisArr[1].data.push(res.data[0].normal);
                                this.timeLineData.yAxisArr[2].data.push(res.data[0].big);
                            }else{
                                this.timeLineData.yAxisArr[0].data.shift();
                                this.timeLineData.yAxisArr[1].data.shift();
                                this.timeLineData.yAxisArr[2].data.shift();
                                this.timeLineData.yAxisArr[0].data.push(res.data[0].small);
                                this.timeLineData.yAxisArr[1].data.push(res.data[0].normal);
                                this.timeLineData.yAxisArr[2].data.push(res.data[0].big);
                            }
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
