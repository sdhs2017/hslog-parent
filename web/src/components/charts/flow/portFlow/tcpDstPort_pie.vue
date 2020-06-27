<template>
    <!--TCP目的端口总流量统计--饼图-->
    <div class="eb" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
        <div style="width: 100%;height: 100%;display: flex;justify-content: center;align-items: center;overflow: hidden" v-if="errState">{{errText}}</div>
        <v-echarts v-else echartType="pie" :echartData = "this.pieData" :busName="busName" ></v-echarts>
    </div>
</template>

<script>
    import vEcharts from '../../../common/echarts_n'
    import bus from '../../../common/bus';
    export default {
        name: "tcpDstPort_pie",
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
                loading:false,
                errState:false,
                errText:'此报表为实时报表，与此仪表盘性质不符',
                pieData:{//柱状图数据
                    baseConfig:{
                        title:'',
                        hoverText:'百分比'
                    },
                    data:{
                        dimensions:[],
                        source:[]
                    }
                },
                //循环
                interval:'',
            }
        },
        created(){
            this.pieData.baseConfig.title = this.baseConProp.title
        },
        beforeDestroy(){
            clearInterval(this.interval)
        },
        watch:{
            params:{
                handler(newV,oldV) {
                    //判断值是否有变化
                    if(JSON.stringify(newV) !== '{}' && JSON.stringify(newV) !== JSON.stringify(oldV)){
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
                        if(this.setIntervalObj.interval){
                            clearInterval(this.interval)
                            this.interval = setInterval(()=>{
                                this.getEchartData(this.params)
                            },this.setIntervalObj.interval)
                        }else{
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
                    this.$axios.post(this.$baseUrl+'/flow/getTCPDstPortCount_barAndPie.do',this.$qs.stringify(params))
                        .then((res) => {
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success === 'true'){
                                this.errState = false;
                                this.pieData.data = obj.data[0]
                            }else{
                                this.errState = true;
                                this.errText = obj.message;
                                clearInterval(this.interval)
                            }
                        })
                        .catch((err) => {
                            this.loading = false;
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
