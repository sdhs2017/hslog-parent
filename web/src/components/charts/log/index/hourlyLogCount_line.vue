<template>
    <!--今日日志数量统计--折线图-->
    <div class="eb"  v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
        <div style="width: 100%;height: 100%;display: flex;justify-content: center;align-items: center;overflow: hidden" v-if="errState">{{errText}}</div>
        <v-echarts echartType="line" :echartData = "this.lineData" :busName="busName" ></v-echarts>
    </div>
</template>

<script>
    import vEcharts from '../../../common/echarts_n'
    import {dateFormat} from "../../../../../static/js/common";
    import bus from '../../../common/bus';

    export default {
        name: "logLevel_bar",
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
            url:{
                type:String
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
                errText:'',
                errState:false,
                lineData:{//折线图数据
                    baseConfig:{
                        title:'',
                        xAxisName:'时间',
                        yAxisName:'数量/条',
                        hoverText:'日志数量',
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
            this.lineData.baseConfig.title = this.baseConProp.title
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
            //获取每小时日志数
            getEchartData(params){
                this.$nextTick( ()=> {
                    this.$axios.post(this.$baseUrl+'/log/getLogCountGroupByTime_line.do',this.$qs.stringify(params))
                        .then((res) => {
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success === 'true'){
                                this.errState = false;
                                this.lineData.data = obj.data[0]
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
