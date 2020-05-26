<template>
    <!--应用平均响应时间统计--柱状图-->
    <div class="eb"  v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
        <el-button type="primary" plain v-if="this.currentType !== 'all'" class="goBack" @click="goBack" size="mini">返回</el-button>
        <div style="width: 100%;height: 100%;display: flex;justify-content: center;align-items: center;overflow: hidden" v-if="errState">此报表为实时报表，与此仪表盘性质不符</div>
        <v-echarts v-else echartType="bar" :echartData = "this.barData" :busName="busName" ></v-echarts>
    </div>
</template>

<script>
    import vEcharts from '../../../common/echarts'
    import {barDataFunc} from "../../common";
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
                barData:{//柱状图数据
                    baseConfig:{
                        title:'',
                        xAxisName:'应用\n名称',
                        yAxisName:'时间/ms',
                        rotate:'15',
                        dispose:true,
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                //循环
                interval:'',
                currentType:'all',
                url:'/flow/getDomaiUrlAvgResponsetime.do',
                domain_url :'',
            }
        },
        created(){
            this.barData.baseConfig.title = this.baseConProp.title;
            /*监听点击事件*/
            bus.$on(this.busName.clickName,(data)=>{
                if(this.currentType === 'all'){
                    this.url = '/flow/getRequestUrlAvgResponsetime.do'
                    this.domain_url = data.name;
                    this.params.domain_url = this.domain_url;
                    this.currentType = 'one'
                    bus.$emit('titleChange', data.name + ' 功能URL平均响应时间统计')
                    this.getEchartData(this.params ,this.url)
                }
                //保存点击的x轴
                /*this.clickxAxis = data.name;
                let obj = {
                    starttime:this.allParams.starttime,
                    endtime:this.allParams.endtime,
                    domain_url:data.name
                }
                layer.load(1);
                this.getOneAppfuncResTimeData(obj);*/
            })
        },
        beforeDestroy(){
            bus.$off(this.busName.clickName)
        },
        watch:{
            params:{
                handler(newV,oldV) {
                    //判断值是否有变化
                    if(JSON.stringify(newV) !== '{}' && JSON.stringify(newV) !== JSON.stringify(oldV)){
                        //判断当前图表的类型 （请求参数不同）
                        if (this.currentType === 'all'){

                        }else {
                            this.params.domain_url = this.domain_url
                        }
                        this.getEchartData(this.params,this.url)
                    }
                },
                immediate: true,
                deep: true
            },
            setIntervalObj:{
                /*handler(newV,oldV) {
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
                deep: true*/
            }
        },
        methods:{
            //获取数据
            getEchartData(params){
                this.loading = true
                this.$nextTick( ()=> {
                    this.$axios.post(this.$baseUrl+'/flow/getDomaiUrlAvgResponsetime.do',this.$qs.stringify({
                        hsData:JSON.stringify(params)
                    }))
                        .then((res) => {
                            this.loading = false
                            let xns1 = [];
                            let yvs1 = [];
                            for(let i in res.data){
                                let obj = res.data[i];
                                xns1.push(obj.key);
                                yvs1.push(obj.value.toFixed(2))
                            }
                            this.barData.xAxisArr = xns1;
                            this.barData.yAxisArr = yvs1;
                        })
                        .catch((err) => {
                            this.loading = false
                            console.log(err)
                        })
                })
            },
            //返回操作
            goBack(){
                this.currentType = 'all';
                this.url = '/flow/getDomaiUrlAvgResponsetime.do';
                bus.$emit('titleChange', '应用平均响应时间统计')
                this.getEchartData({starttime:this.params.starttime,endtime:this.params.endtime},this.url)
            }
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
