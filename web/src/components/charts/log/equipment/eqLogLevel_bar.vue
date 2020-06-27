<template>
    <!--单个资产日志级别数量统计--柱状图-->
    <div class="eb" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
        <div style="width: 100%;height: 100%;display: flex;justify-content: center;align-items: center;overflow: hidden" v-if="errState">{{errText}}</div>
        <v-echarts v-else echartType="bar" :echartData = "this.barData" :busName="busName" ></v-echarts>
    </div>
</template>

<script>
    import vEcharts from '../../../common/echarts_n'
    import bus from '../../../common/bus';
    export default {
        name: "eqLogLevel_bar",
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
                barData:{//柱状图数据
                    baseConfig:{
                        title:'日志级别数量统计',
                        xAxisName:'级别',
                        yAxisName:'数量/条',
                        hoverText:'数量',
                        itemColor:[['rgba(68,47,148,0.5)','rgba(15,219,243,1)']]
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
            this.barData.baseConfig.title = this.baseConProp.title
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
                    this.$axios.post(this.$baseUrl+'/log/getCountGroupByLogLevel_barAndPie.do',this.$qs.stringify(params))
                        .then((res) => {
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success === 'true'){
                                this.errState = false;
                                this.barData.data = obj.data[0]
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
