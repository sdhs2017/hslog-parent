<template>
    <div class="content-bg">
        <div class="top-title">流量报表</div>
        <div class="flow-echarts-con">
            <div class="date-wapper"> <v-basedate busName="e1" :defaultVal=this.dateVal></v-basedate></div>
            <el-row :gutter="20" class="flow-row">
                <el-col :span="12">
                    <div class="chart-wapper system-chart">
                        <v-echarts echartType="bar" :echartData = "this.systemChartData" ></v-echarts>
                    </div>
                </el-col>
                <el-col :span="12">
                    <div class="chart-wapper system-chart">
                        <v-echarts echartType="pie" :echartData = "this.systemChartData2" ></v-echarts>
                    </div>
                </el-col>
            </el-row>

            <div class="date-wapper"> <v-basedate busName="e2" :defaultVal=this.dateVal></v-basedate></div>
            <el-row :gutter="20" class="flow-row">
                <el-col :span="12">
                    <div class="chart-wapper browser-chart">
                        <v-echarts echartType="bar" :echartData = "this.browserChartData" ></v-echarts>
                    </div>
                </el-col>
                <el-col :span="12">
                    <div class="chart-wapper browser-chart">
                        <v-echarts echartType="pie" :echartData = "this.browserChartData2" ></v-echarts>
                    </div>
                </el-col>
            </el-row>
            <el-row :gutter="20" class="flow-row">
                <el-col :span="12">
                    <div class="chart-wapper ip-chart">
                        <div class="date-wapper"> <v-basedate busName="e3" :defaultVal=this.dateVal></v-basedate></div>
                        <v-echarts echartType="bar" :echartData = "this.ipChartData" ></v-echarts>
                    </div>
                </el-col>
            </el-row>
        </div>
    </div>
    
</template>

<script>
    import vEcharts from '../common/echarts'
    import vBasedate from '../common/baseDate'
    import bus from '../common/bus';
    export default {
        name: "flowEcharts",
        data() {
            return {
                dateVal:[],
                data1:[{"Windows 10":135,"WSDAPI":109,"Windows XP":21,"SOGOU_POPUP_NEWS":2,"Windows 8":2,"SogouIMEMiniSetup_skinrecommend":2,"SogouIMEMiniSetup_imepopup":2,"Windows Vista":2,"SogouIMEMiniSetup_RandSkin":2}],
                data2:[{"Firefox 7":113,"WSDAPI":109,"Internet Explorer 6":21,"Internet Explorer 11":14,"Chrome 63":6,"SOGOU_POPUP_NEWS":2,"Chrome":2,"SogouIMEMiniSetup_skinrecommend":2,"Chrome 43":2,"SogouIMEMiniSetup_RandSkin":2}],
                data3:[{"10.28.82.188":64908,"10.28.82.69":14385,"10.28.83.73":5376,"220.194.95.147":3588,"111.202.100.56":2292,"10.28.83.215":2250,"117.18.237.29":2082,"111.206.79.114":1912,"123.126.51.104":1596,"139.215.137.13":1234}],
                //操作系统 柱状图数据
                systemChartData:{
                    baseConfig:{
                        title:'业务访问用户统计-操作系统',
                        xAxisName:'操作系统',
                        yAxisName:'访问次数/次',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]

                },
                //操作系统 饼图数据
                systemChartData2:{
                    baseConfig:{
                        title:'',
                        hoverText:'百分比'
                    },
                    xAxisArr:[],
                    yAxisArr:[]

                },
                browserChartData:{
                    baseConfig:{
                        title:'业务访问用户统计-浏览器',
                        xAxisName:'浏览器',
                        yAxisName:'访问次数/次',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]

                },
                browserChartData2:{
                    baseConfig:{
                        title:'',
                        hoverText:'百分比'
                    },
                    xAxisArr:[],
                    yAxisArr:[]

                },
                ipChartData:{
                    baseConfig:{
                        title:'访问业务服务器的流量大小统计',
                        xAxisName:'业务服务器',
                        yAxisName:'数据包长度/kb',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]

                }
            }
        },
        created(){
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            this.dateVal=[start,end];

            var mon = end.getMonth()+1;
            var da = end.getDate();
            if( mon < 10){
                mon = "0"+(end.getMonth()+1);
            }
            if(da < 10){
                da = "0"+ (end.getDate());
            }
            let endtime = end.getFullYear()+"-" + mon + "-" + da;
            //定义七天前时间
            var date2 = new Date(end);
            date2.setDate(end.getDate()-7);
            let sd = date2.getDate();
            if(sd < 10){
                sd = "0"+ sd;
            }
            let starttime = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+sd;
            //获取数据
            /*this.getBrowserData(starttime,endtime);
            this.getSystemData(starttime,endtime);
            this.getIpData(starttime,endtime);*/

        },
        mounted(){
            let xns1 = [];
            let yvs1 = [];
            let yvs11 = [];
            let xns2 = [];
            let yvs2 = [];
            let yvs22 = [];
            let xns3 = [];
            let yvs3 = [];
            for(let i in this.data1[0]){
                xns1.push(i);
                yvs1.push(this.data1[0][i]);
                yvs11.push({
                    name:i,
                    value:this.data1[0][i]
                })
            }
            for(let i in this.data2[0]){
                xns2.push(i);
                yvs2.push(this.data2[0][i]);
                yvs22.push({
                    name:i,
                    value:this.data2[0][i]
                })
            }
            for(let i in this.data3[0]){
                xns3.push(i);
                yvs3.push(this.data3[0][i]);
            }
            this.systemChartData.xAxisArr = xns1;
            this.systemChartData.yAxisArr = yvs1;
            this.systemChartData2.yAxisArr = yvs11;
            this.browserChartData.xAxisArr = xns2;
            this.browserChartData.yAxisArr = yvs2;
            this.browserChartData2.yAxisArr = yvs22;
            this.ipChartData.xAxisArr = xns3;
            this.ipChartData.yAxisArr = yvs3;

            //绑定监听时间改变事件
            bus.$on('e1',(params)=>{
                this.getBrowserData(params[0],params[1]);
            })
            bus.$on('e2',(params)=>{
                this.getSystemData(params[0],params[1]);
            })
            bus.$on('e3',(params)=>{
                this.getIpData(params[0],params[1]);
            })
        },
        methods:{
            /*获取业务系统统计- 浏览器*/
            getBrowserData(starttime,endtime){
              this.$nextTick(()=>{
                  console.log(starttime)
                  layer.load(1);
                  this.$axios.post(this.$baseUrl+'/flow/getUserAgentBrowserGroupByTime.do',this.$qs.stringify({
                      starttime:starttime,
                      endtime:endtime
                  }))
                      .then(res=>{
                          layer.closeAll('loading');
                          let xns1 = [];
                          let yvs1 = [];
                          for(let i in res.data[0]){
                              xns1.push(i);
                              yvs1.push(res.data[0][i]);
                          }
                          this.systemChartData.xAxisArr = xns1;
                          this.systemChartData.yAxisArr = yvs1;
                      })
                      .catch(err=>{
                          layer.closeAll('loading');

                      })
              })
            },
            /*获取业务系统统计- 系统*/
            getSystemData(starttime,endtime){
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/flow/getUserAgentOSGroupByTime.do',this.$qs.stringify({
                        starttime:starttime,
                        endtime:endtime
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            for(let i in res.data[0]){
                                xns1.push(i);
                                yvs1.push(res.data[0][i]);
                            }
                            this.browserChartData.xAxisArr = xns1;
                            this.browserChartData.yAxisArr = yvs1;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取业务系统统计- 浏览器*/
            getIpData(starttime,endtime){
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/flow/getRequestPacketOfDstIP.do',this.$qs.stringify({
                        starttime:starttime,
                        endtime:endtime
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            for(let i in res.data[0]){
                                xns1.push(i);
                                yvs1.push(res.data[0][i]);
                            }
                            this.ipChartData.xAxisArr = xns1;
                            this.ipChartData.yAxisArr = yvs1;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            }
        },
        components:{
            vEcharts,
            vBasedate
        }
    }
</script>

<style scoped>
    .flow-echarts-con{
        padding: 20px;
        /*padding-bottom: 80px;*/
        /*height: calc(100% - 100px);*/
    }
    .flow-row{
        padding-bottom: 80px;
        margin: 0 20px;
    }
    .chart-wapper{
        height: 60vh;
        min-height: 420px;
        /*padding-top: 50px;*/
        /*background: #303e4e;*/
    }
    .date-wapper{
        margin-bottom: 20px;
        padding-left: 50px;
    }
</style>
