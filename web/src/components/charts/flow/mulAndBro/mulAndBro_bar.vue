<template>
    <!--广播包、组播包统计--柱状图-->
    <div class="eb" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
        <div style="width: 100%;height: 100%;display: flex;justify-content: center;align-items: center;overflow: hidden" v-if="errState">{{errText}}</div>
        <v-echarts v-else echartType="bar" :echartData = "this.barData" :busName="busName" ></v-echarts>
    </div>
</template>

<script>
    import vEcharts from '../../../common/echarts_n'
    import {barDataFunc} from "../../common";
    import bus from '../../../common/bus';
    export default {
        name: "mulAndBro_bar",
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
                barData:{//柱状图数据
                    baseConfig:{
                        title:'',
                        xAxisName:'类型',
                        yAxisName:'数据包/个数',
                        rotate:'20',
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
            this.barData.baseConfig.title = this.baseConProp.title
        },
        beforeDestroy(){
            clearInterval(this.interval)
        },
        watch:{
            params:{
                handler(newV,oldV) {
                    //判断值是否有变化
                    if(JSON.stringify(newV) !== '{}' && JSON.stringify(newV) !== JSON.stringify(oldV)){
                        this.loading = true;
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
                    this.$axios.post(this.$baseUrl+'/flow/getMulticastAndBroadcastPacketTypeCount.do',this.$qs.stringify(params))
                        .then((res) => {
                            this.loading = false;
                            //赋值
                            //this.barData.xAxisArr = barDataFunc(arr)[0];
                           // this.barData.yAxisArr = barDataFunc(arr)[1];
                            let arr = [];
                            arr.push(res.data[0].multicast.value[1]);
                            arr.push(res.data[0].broadcast.value[1]);
                            this.barData.yAxisArr= arr;
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
