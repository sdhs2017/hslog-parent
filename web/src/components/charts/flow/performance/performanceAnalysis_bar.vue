<template>
    <!--应用平均响应时间统计--柱状图-->
    <div class="eb"  v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
<!--        <el-button type="primary" plain v-if="this.currentType !== 'all'" class="goBack" @click="goBack" size="mini">返回</el-button>-->
        <div style="width: 100%;height: 100%;display: flex;justify-content: center;align-items: center;overflow: hidden" v-if="errState">{{errText}}</div>
        <v-echarts v-else echartType="bar" :echartData = "this.barData" :busName="busName" ></v-echarts>
    </div>
</template>

<script>
    import vEcharts from '../../../common/echarts_n'
    import {barDataFunc} from "../../common";
    import {jumpHtml} from "../../../../../static/js/common";
    import bus from '../../../common/bus';
    export default {
        name: "performanceAnalysis_bar.vue",
        props:{
            params:{
                type:Object
            },
            busName:{
                type:Object,
                default(){
                    return{
                        clickName:'appAvgResTimeClick',
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
                loading:true,
                errState:false,
                errText:'此报表为实时报表，与此仪表盘性质不符',
                barData:{//柱状图数据
                    baseConfig:{
                        title:'',
                        xAxisName:'应用\n名称',
                        yAxisName:'时间/ms',
                        rotate:'15',
                        itemColor:[['rgba(68,47,148,0.5)','rgba(15,219,243,1)']]
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
            this.barData.baseConfig.title = this.baseConProp.title;
            /*监听点击事件*/
            bus.$on(this.busName.clickName,(data)=>{
                if(!this.setIntervalObj.state){
                    jumpHtml('performanceAnalysisUrl'+data.name,'flowManage/performanceAnalysisUrl.vue',{ url:data.name,starttime:this.params.starttime,endtime:this.params.endtime},'平均响应时间')
                }
            })
        },
        beforeDestroy(){
            bus.$off(this.busName.clickName)
            clearInterval(this.interval)
        },
        watch:{
            params:{
                handler(newV,oldV) {
                    //判断值是否有变化
                    if(JSON.stringify(newV) !== '{}' && JSON.stringify(newV) !== JSON.stringify(oldV)){
                        /*//判断当前图表的类型 （请求参数不同）
                        if (this.currentType === 'all'){

                        }else {
                            this.params.domain_url = this.domain_url
                            this.params.queryParam = JSON.stringify({'domain_url.raw':this.domain_url})
                        }*/
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
                    this.$axios.post(this.$baseUrl+'/flow/getDomaiUrlAvgResponsetime_barAndPie.do',this.$qs.stringify(params))
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
                            this.loading = false
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
    .goBack{
        margin-left: 30px;
    }
</style>
