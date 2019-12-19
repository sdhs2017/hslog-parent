<template>
    <div class="content-bg">
        <div class="top-title">流量报表
            <v-flowchartfrom class="from-wapper" refreshBus="refreshBus" dataBus="dataBus"></v-flowchartfrom>
        </div>
        <div class="flow-echarts-con">
            <div class="echarts-item">
                <p class="echarts-tit">业务访问用户统计-操作系统</p>
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
            </div>

            <div class="echarts-item">
                <p class="echarts-tit">业务访问用户统计-浏览器</p>
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
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">访问业务服务器的流量大小统计</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.ipChartData" ></v-echarts>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="pie" :echartData = "this.ipChartData2" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">全局-利用率百分比</p>
                <div class="chart-from">
                    <span>带宽：</span>
                    <el-select v-model="bandwidth" placeholder="请选择" size="mini" >
                        <el-option
                            v-for="item in bandwidthArr"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </div>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="timeline" :echartData = "this.allUsedData" ></v-echarts>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="gauge" :echartData = "this.allUsedData2" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">源IP地址流量排序</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.sourceIpData" ></v-echarts>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
<!--                            <v-echarts echartType="pie" :echartData = "this.sourceIpData" ></v-echarts>-->
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">目的IP地址流量排序</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.targetIpData" ></v-echarts>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <!--                            <v-echarts echartType="pie" :echartData = "this.sourceIpData" ></v-echarts>-->
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">传输层协议长度统计</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="24">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="pie" :echartData = "this.TUData" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">应用层协议长度统计</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="pie" :echartData = "this.appData" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">协议长度综合统计</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="pie" :echartData = "this.agreementData" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">全局-数据包类型个数</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="timeline" :echartData = "this.allPacketsTypeData" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">资产（IP）数据包个数</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.ipPacketsData" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">资产（域名）数据包个数</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="24">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.domainPacketsData" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">目的端口总流量</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="24">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.targetPortFlowData" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">TCP目的端口总流量</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="24">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.TCPPortFlowData" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">UDP目的端口总流量</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="24">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="bar" :echartData = "this.UDPPortFlowData" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">全局数据包个数</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="timeline" :echartData = "this.allPacketsData" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="echarts-item">
                <p class="echarts-tit">全局-广播包、组播包</p>
                <el-row :gutter="20" class="flow-row">
                    <el-col :span="12">
                        <div class="chart-wapper ip-chart">
                            <v-echarts echartType="timeline" :echartData = "this.allMulAndBroData" ></v-echarts>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>
    </div>
    
</template>

<script>
    import vFlowchartfrom from '../common/flowChartsFrom'
    import vEcharts from '../common/echarts'
    import vBasedate from '../common/baseDate'
    import bus from '../common/bus';
    import {dateFormat} from '../../../static/js/common'
    export default {
        name: "flowEcharts",
        data() {
            return {
                dateVal:[],
                data1:[{"Windows 10":135,"WSDAPI":109,"Windows XP":21,"SOGOU_POPUP_NEWS":2,"Windows 8":2,"SogouIMEMiniSetup_skinrecommend":2,"SogouIMEMiniSetup_imepopup":2,"Windows Vista":2,"SogouIMEMiniSetup_RandSkin":2}],
                data2:[{"Firefox 7":113,"WSDAPI":109,"Internet Explorer 6":21,"Internet Explorer 11":14,"Chrome 63":6,"SOGOU_POPUP_NEWS":2,"Chrome":2,"SogouIMEMiniSetup_skinrecommend":2,"Chrome 43":2,"SogouIMEMiniSetup_RandSkin":2}],
                data3:[{"10.28.82.188":64908,"10.28.82.69":14385,"10.28.83.73":5376,"220.194.95.147":3588,"111.202.100.56":2292,"10.28.83.215":2250,"117.18.237.29":2082,"111.206.79.114":1912,"123.126.51.104":1596,"139.215.137.13":1234}],
                data4:[{"http://10.28.82.69:5357/f2b13a2b-3f72-48f4-bb44-0003818775e8/":728},{"http://10.28.82.188:5601/api/":301},{"http://10.28.83.73:5357/15a4fa48-aa76-46fa-a636-29622103d6a3/":177},{"http://111.202.100.40:80/q":36},{"http://118.184.170.54:80/imewis/":22},{"http://10.28.83.44:5357/da5790cd-9f57-43ed-937a-526a46c78a89/":22},{"http://119.188.150.178:443/download":20},{"http://220.194.95.148:14000/cgi-bin/":18},{"http://119.188.150.227:443/download":14},{"http://223.167.84.144:80/mmtls/":12}],
                interTime:'',
                //刷新时间间隔
                refreshIntTime :'10000',
                //数据日期间隔
                dataTime:1,
                //操作系统 柱状图数据
                systemChartData:{
                    baseConfig:{
                        title:'',
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
                //浏览器
                browserChartData:{
                    baseConfig:{
                        title:'',
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
                //业务系统
                ipChartData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'业务服务器',
                        yAxisName:'数据包长度/kb',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]

                },
                ipChartData2:{
                    baseConfig:{
                        title:'',
                        hoverText:'百分比'
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                //全局利用率百分比
                bandwidth:1000,
                bandwidthArr:[
                    {
                        label:'1000M',
                        value:1000
                    }
                ],
                allUsedData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'时间',
                        yAxisName:'百分比/%',
                        hoverText:'',
                    },
                    yAxisArr:[{
                        name:'',
                        color:'rgb(15,219,243)',
                        data:[]
                    }]
                },
                allUsedData2:{
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
                //源ip地址流量排行
                sourceIpData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'源IP',
                        yAxisName:'访问次数/次',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                //目的ip地址流量排行
                targetIpData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'目的IP',
                        yAxisName:'访问次数/次',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                //传输层协议长度统计
                TUData:{
                    baseConfig:{
                        title:'',
                        hoverText:'百分比'
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                //应用层协议长度统计
                appData:{
                    baseConfig:{
                        title:'',
                        hoverText:'百分比'
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                //协议长度综合统计
                agreementData:{
                    baseConfig:{
                        title:'',
                        hoverText:'百分比'
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                //全局数据包类型个数
                allPacketsTypeData:{
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
                //资产（ip）数据包个数
                ipPacketsData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'资产(IP)',
                        yAxisName:'数据包/个',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                //资产（域名）数据包个数
                domainPacketsData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'资产(域名)',
                        yAxisName:'数据包/个',
                        rotate:'10',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                //目的端口总流量
                targetPortFlowData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'端口',
                        yAxisName:'数据包/个',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                //TCP端口总流量
                TCPPortFlowData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'端口',
                        yAxisName:'数据包/个',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                //TCP端口总流量
                UDPPortFlowData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'端口',
                        yAxisName:'数据包/个',
                        rotate:'20',
                        itemColor:['rgba(68,47,148,0.5)','rgba(15,219,243,1)']
                    },
                    xAxisArr:[],
                    yAxisArr:[]
                },
                //全局数据包个数
                allPacketsData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'时间',
                        yAxisName:'个数',
                        hoverText:'',
                    },
                    yAxisArr:[{
                        name:'',
                        color:'rgb(15,219,243)',
                        data:[]
                    }]
                },
                //全局广播包组播包个数
                allMulAndBroData:{
                    baseConfig:{
                        title:'',
                        xAxisName:'时间',
                        yAxisName:'个数',
                        hoverText:'',
                    },
                    yAxisArr:[{
                        name:'广播包',
                        color:'rgb(15,219,243)',
                        data:[]
                    },{
                        name:'组播包',
                        color:'rgb(9,243,105)',
                        data:[]
                    }]
                }
            }
        },
        created(){
            //获取数据
            /*this.getBrowserData(starttime,endtime);
            this.getSystemData(starttime,endtime);
            this.getIpData(starttime,endtime);*/

            /*监听刷新时间改变*/
            bus.$on('refreshBus',(val)=>{
                this.refreshIntTime = val;
                clearInterval(this.interTime);
                this.interTime = setInterval(()=>{
                    let endtime = dateFormat('yyyy-MM-dd hh:mm:ss',new Date());
                    let st = new Date(new Date(endtime).valueOf() - this.dataTime * 60 * 60 * 1000);
                    let starttime = dateFormat('yyyy-MM-dd hh:mm:ss',st);
                    /*获取业务系统统计- 浏览器*/
                    this.getBrowserData(starttime,endtime);
                    /*获取业务系统统计- 系统*/
                    this.getSystemData(starttime,endtime);
                    /*获取访问业务服务器统计- */
                    this.getIpData(starttime,endtime);
                    /*获取源IP地址流量排行*/
                    this.getSourceIpData(starttime,endtime);
                    /*获取目的IP地址流量排行*/
                    this.getTargetIpData(starttime,endtime);
                    /*获取传输层协议长度统计*/
                    this.getTUData(starttime,endtime);
                    /*获取应用层协议长度统计*/
                    this.getAppData(starttime,endtime);
                    /*获取协议长度综合统计*/
                    this.getAgreementData(starttime,endtime);
                    /*获取资产（ip）数据包个数*/
                    this.getIpPacketsData(starttime,endtime);
                    /*获取资产（域名）数据包个数*/
                    this.getDomainPacketsData(starttime,endtime);
                    /*获取目的端口总流量*/
                    this.getTargetPortFlowData(starttime,endtime);
                    /*获取TCP端口总流量*/
                    this.getTCPPortFlowData(starttime,endtime);
                    /*获取UDP端口总流量*/
                    this.getUDPPortFlowData(starttime,endtime);
                },this.refreshIntTime);
            })
            /*监听数据时间改变*/
            bus.$on('dataBus',(val)=>{
                this.dataTime = val;
            })


        },
        mounted(){
            /*let xns1 = [];
            let yvs1 = [];
            let yvs11 = [];
            let xns2 = [];
            let yvs2 = [];
            let yvs22 = [];
            let xns3 = [];
            let yvs3 = [];
            let yvs33 = [];
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
                yvs33.push({
                    name:i,
                    value:this.data3[0][i]
                })
            }
            this.systemChartData.xAxisArr = xns1;
            this.systemChartData.yAxisArr = yvs1;
            this.systemChartData2.yAxisArr = yvs11;
            this.browserChartData.xAxisArr = xns2;
            this.browserChartData.yAxisArr = yvs2;
            this.browserChartData2.yAxisArr = yvs22;
            this.ipChartData.xAxisArr = xns3;
            this.ipChartData.yAxisArr = yvs3;
            this.ipChartData2.yAxisArr = yvs33;*/


            //获取全局利用率百分比
            /*let ctime = getCurrentDate('yyyy-MM-dd hh:mm:ss');
            this.getAllUsedData(ctime);
            setInterval(()=>{
                let ctime = getCurrentDate('yyyy-MM-dd hh:mm:ss');
                this.getAllUsedData(ctime);
            },10000)*/
            //第一次获取数据
            let endtime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
            let st = new Date(new Date(endtime).valueOf() - this.dataTime * 60 * 60 * 1000);
            let starttime = dateFormat('yyyy-mm-dd HH:MM:SS',st);
            /*获取业务系统统计- 浏览器*/
            this.getBrowserData(starttime,endtime);
            /*获取业务系统统计- 系统*/
            this.getSystemData(starttime,endtime);
            /*获取访问业务服务器统计- */
            this.getIpData(starttime,endtime);
            /*获取全局利用率百分比*/
            this.getAllUsedData(endtime);
            /*获取源IP地址流量排行*/
            this.getSourceIpData(starttime,endtime);
            /*获取目的IP地址流量排行*/
            this.getTargetIpData(starttime,endtime);
            /*获取传输层协议长度统计*/
            this.getTUData(starttime,endtime);
            /*获取应用层协议长度统计*/
            this.getAppData(starttime,endtime);
            /*获取协议长度综合统计*/
            this.getAgreementData(starttime,endtime);
            /*获取全局数据包类型个数*/
            this.getAllPacketsTypeData(endtime);
            /*获取资产（ip）数据包个数*/
            this.getIpPacketsData(starttime,endtime);
            /*获取资产（域名）数据包个数*/
            this.getDomainPacketsData(starttime,endtime);
            /*获取目的端口总流量*/
            this.getTargetPortFlowData(starttime,endtime);
            /*获取TCP端口总流量*/
            this.getTCPPortFlowData(starttime,endtime);
            /*获取UDP端口总流量*/
            this.getUDPPortFlowData(starttime,endtime);
            /*获取全局数据包个数*/
            this.getAllPacketsData(endtime);
            /*获取组播包广播包个数*/
            this.getAllMulAndBroData(endtime);
            /*循环获取数据*/
            this.interTime = setInterval(()=>{
                let endtime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
                let st = new Date(new Date(endtime).valueOf() - this.dataTime * 60 * 60 * 1000);
                let starttime = dateFormat('yyyy-mm-dd HH:MM:SS',st);
                /*获取业务系统统计- 浏览器*/
                this.getBrowserData(starttime,endtime);
                /*获取业务系统统计- 系统*/
                this.getSystemData(starttime,endtime);
                /*获取访问业务服务器统计- */
                this.getIpData(starttime,endtime);
                /*获取源IP地址流量排行*/
                this.getSourceIpData(starttime,endtime);
                /*获取目的IP地址流量排行*/
                this.getTargetIpData(starttime,endtime);
                /*获取传输层协议长度统计*/
                this.getTUData(starttime,endtime);
                /*获取应用层协议长度统计*/
                this.getAppData(starttime,endtime);
                /*获取协议长度综合统计*/
                this.getAgreementData(starttime,endtime);
                /*获取资产（ip）数据包个数*/
                this.getIpPacketsData(starttime,endtime);
                /*获取资产（域名）数据包个数*/
                this.getDomainPacketsData(starttime,endtime);
                /*获取目的端口总流量*/
                this.getTargetPortFlowData(starttime,endtime);
                /*获取TCP端口总流量*/
                this.getTCPPortFlowData(starttime,endtime);
                /*获取UDP端口总流量*/
                this.getUDPPortFlowData(starttime,endtime);
            },this.refreshIntTime);

            setInterval(()=>{
                let endtime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
                let st = new Date(new Date(endtime).valueOf() - this.dataTime * 60 * 60 * 1000);
                let starttime = dateFormat('yyyy-mm-dd HH:MM:SS',st);
                /*获取全局利用率百分比*/
                this.getAllUsedData(endtime);
                /*获取全局数据包类型个数*/
                this.getAllPacketsTypeData(endtime);
                /*获取全局数据包个数*/
                this.getAllPacketsData(endtime);
                /*获取组播包广播包个数*/
                this.getAllMulAndBroData(endtime);
            },2000)
        },
        methods:{
            /*获取业务系统统计- 浏览器*/
            getBrowserData(starttime,endtime){
              this.$nextTick(()=>{
                  this.$axios.post(this.$baseUrl+'/flow/getUserAgentBrowserGroupByTime.do',this.$qs.stringify({
                      starttime:starttime,
                      endtime:endtime
                  }))
                      .then(res=>{
                          let xns1 = [];
                          let yvs1 = [];
                          let yvs2 = [];
                          for(let i in res.data[0]){
                              xns1.push(i);
                              yvs1.push(res.data[0][i]);
                              yvs2.push({
                                  name:i,
                                  value:res.data[0][i]
                              })
                          }
                          this.systemChartData.xAxisArr = xns1;
                          this.systemChartData.yAxisArr = yvs1;
                          this.systemChartData2.yAxisArr = yvs2;
                      })
                      .catch(err=>{

                      })
              })
            },
            /*获取业务系统统计- 系统*/
            getSystemData(starttime,endtime){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getUserAgentOSGroupByTime.do',this.$qs.stringify({
                        starttime:starttime,
                        endtime:endtime
                    }))
                        .then(res=>{
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data[0]){
                                xns1.push(i);
                                yvs1.push(res.data[0][i]);
                                yvs2.push({
                                    name:i,
                                    value:res.data[0][i]
                                })
                            }
                            this.browserChartData.xAxisArr = xns1;
                            this.browserChartData.yAxisArr = yvs1;
                            this.browserChartData2.yAxisArr = yvs2;
                        })
                        .catch(err=>{

                        })
                })
            },
            /*获取访问业务服务器统计- */
            getIpData(starttime,endtime){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getRequestPacketOfDstIP.do',this.$qs.stringify({
                        starttime:starttime,
                        endtime:endtime
                    }))
                        .then(res=>{
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data[0]){
                                xns1.push(i);
                                yvs1.push(res.data[0][i]);
                                yvs2.push({
                                    name:i,
                                    value:res.data[0][i]
                                })
                            }
                            this.ipChartData.xAxisArr = xns1;
                            this.ipChartData.yAxisArr = yvs1;
                            this.ipChartData2.yAxisArr = yvs2;
                        })
                        .catch(err=>{

                        })
                })
            },
            /*获取全局利用率百分比*/
            getAllUsedData(time){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getPacketLengthPerSecond.do',this.$qs.stringify({
                        endtime:time
                    }))
                        .then(res=>{
                            res.data[0].value[1] = res.data[0].value[1] / this.refreshIntTime / this.bandwidth
                            if(this.allUsedData.yAxisArr[0].data.length < 60){
                                this.allUsedData.yAxisArr[0].data.push(res.data[0])
                            }else{
                                this.allUsedData.yAxisArr[0].data.shift();
                                this.allUsedData.yAxisArr[0].data.push(res.data[0])
                            }
                            this.allUsedData2.yAxisArr[0].data.push({
                                value:res.data[0].value[1],
                                name:'利用率'
                            })
                        })
                        .catch(err=>{

                        })
                })
            },
            /*获取源IP地址流量排行*/
            getSourceIpData(starttime,endtime){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/flow/getSrcIPFlow.do',this.$qs.stringify({
                        starttime:starttime,
                        endtime:endtime
                    }))
                        .then(res=>{
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data[0]){
                                xns1.push(i);
                                yvs1.push(res.data[0][i]);
                                yvs2.push({
                                    name:i,
                                    value:res.data[0][i]
                                })
                            }
                            this.sourceIpData.xAxisArr = xns1;
                            this.sourceIpData.yAxisArr = yvs1;
                            // this.systemChartData2.yAxisArr = yvs2;
                        })
                        .catch(err=>{

                        })
                })
            },
            /*获取目的IP地址流量排行*/
            getTargetIpData(starttime,endtime){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getDstIPFlow.do',this.$qs.stringify({
                        starttime:starttime,
                        endtime:endtime
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data[0]){
                                xns1.push(i);
                                yvs1.push(res.data[0][i]);
                                yvs2.push({
                                    name:i,
                                    value:res.data[0][i]
                                })
                            }
                            this.targetIpData.xAxisArr = xns1;
                            this.targetIpData.yAxisArr = yvs1;
                            // this.systemChartData2.yAxisArr = yvs2;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取传输层协议长度统计*/
            getTUData(starttime,endtime){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getTransportLength.do',this.$qs.stringify({
                        starttime:starttime,
                        endtime:endtime
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data[0]){
                                xns1.push(i);
                                yvs1.push(res.data[0][i]);
                                yvs2.push({
                                    name:i,
                                    value:res.data[0][i]
                                })
                            }
                            /* this.targetIpData.xAxisArr = xns1;
                             this.targetIpData.yAxisArr = yvs1;*/
                            this.TUData.yAxisArr = yvs2;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取应用层协议长度统计*/
            getAppData(starttime,endtime){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getApplicationLength.do',this.$qs.stringify({
                        starttime:starttime,
                        endtime:endtime
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data[0]){
                                xns1.push(i);
                                yvs1.push(res.data[0][i]);
                                yvs2.push({
                                    name:i,
                                    value:res.data[0][i]
                                })
                            }
                            /* this.targetIpData.xAxisArr = xns1;
                             this.targetIpData.yAxisArr = yvs1;*/
                            this.appData.yAxisArr = yvs2;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取协议长度综合统计*/
            getAgreementData(starttime,endtime){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getMultipleLength.do',this.$qs.stringify({
                        starttime:starttime,
                        endtime:endtime
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data){
                                /*yvs2.push({
                                    name:i,
                                    value:res.data[0][i]
                                })*/
                                for(let j in res.data[i]){
                                    yvs2.push({
                                        name:j,
                                        value:res.data[i][j]
                                    })
                                }
                            }
                            /* this.targetIpData.xAxisArr = xns1;
                             this.targetIpData.yAxisArr = yvs1;*/
                            this.agreementData.yAxisArr = yvs2;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取全局数据包类型个数*/
            getAllPacketsTypeData(time){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getMultipleLength.do',this.$qs.stringify({
                        endtime:time
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            if(this.allPacketsTypeData.yAxisArr[0].data.length < 60){
                                this.allPacketsTypeData.yAxisArr[0].data.push({
                                    name:time,
                                    value:[time,res.data[0].small]
                                })
                                this.allPacketsTypeData.yAxisArr[1].data.push({
                                    name:time,
                                    value:[time,res.data[1].normal]
                                })
                                this.allPacketsTypeData.yAxisArr[2].data.push({
                                    name:time,
                                    value:[time,res.data[2].big]
                                })
                            }else{
                                this.allPacketsTypeData.yAxisArr[0].data.shift();
                                this.allPacketsTypeData.yAxisArr[1].data.shift();
                                this.allPacketsTypeData.yAxisArr[2].data.shift();
                                this.allPacketsTypeData.yAxisArr[0].data.push({
                                    name:time,
                                    value:[time,res.data[0].small]
                                })
                                this.allPacketsTypeData.yAxisArr[1].data.push({
                                    name:time,
                                    value:[time,res.data[1].normal]
                                })
                                this.allPacketsTypeData.yAxisArr[2].data.push({
                                    name:time,
                                    value:[time,res.data[2].big]
                                })
                            }
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取资产（ip）数据包个数*/
            getIpPacketsData(starttime,endtime){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getDstIPPacketCount.do',this.$qs.stringify({
                        starttime:starttime,
                        endtime:endtime
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data){
                                let obj = res.data[i];
                                for (let j in obj){
                                    xns1.push(j);
                                    yvs1.push(obj[j])
                                }
                            }
                            this.ipPacketsData.xAxisArr = xns1;
                            this.ipPacketsData.yAxisArr = yvs1;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取资产（域名）数据包个数*/
            getDomainPacketsData(starttime,endtime){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getDstUrlPacketCount.do',this.$qs.stringify({
                        starttime:starttime,
                        endtime:endtime
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data){
                                let obj = res.data[i];
                                for (let j in obj){
                                    xns1.push(j);
                                    yvs1.push(obj[j])
                                }
                            }
                            this.domainPacketsData.xAxisArr = xns1;
                            this.domainPacketsData.yAxisArr = yvs1;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取目的端口总流量*/
            getTargetPortFlowData(starttime,endtime){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getDstPortCount.do',this.$qs.stringify({
                        starttime:starttime,
                        endtime:endtime
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data[0]){
                                xns1.push(i);
                                yvs1.push(res.data[0][i])
                            }
                            this.targetPortFlowData.xAxisArr = xns1;
                            this.targetPortFlowData.yAxisArr = yvs1;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取TCP目的端口总流量*/
            getTCPPortFlowData(starttime,endtime){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getTCPDstPortCount.do',this.$qs.stringify({
                        starttime:starttime,
                        endtime:endtime
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data[0]){
                                xns1.push(i);
                                yvs1.push(res.data[0][i])
                            }
                            this.TCPPortFlowData.xAxisArr = xns1;
                            this.TCPPortFlowData.yAxisArr = yvs1;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取UDP端口总流量*/
            getUDPPortFlowData(starttime,endtime){
                this.$nextTick(()=>{

                    this.$axios.post(this.$baseUrl+'/flow/getUDPDstPortCount.do',this.$qs.stringify({
                        starttime:starttime,
                        endtime:endtime
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            let xns1 = [];
                            let yvs1 = [];
                            let yvs2 = [];
                            for(let i in res.data[0]){
                                xns1.push(i);
                                yvs1.push(res.data[0][i])
                            }
                            this.UDPPortFlowData.xAxisArr = xns1;
                            this.UDPPortFlowData.yAxisArr = yvs1;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取全局数据包个数*/
            getAllPacketsData(time){
                this.$nextTick(()=> {

                    this.$axios.post(this.$baseUrl + '/flow/getPacketCount.do', this.$qs.stringify({
                        endtime:time
                    }))
                        .then(res => {
                            layer.closeAll('loading');
                            if (this.allPacketsData.yAxisArr[0].data.length < 60) {
                                this.allPacketsData.yAxisArr[0].data.push(res.data[0]);
                            } else {
                                this.allPacketsData.yAxisArr[0].data.shift();
                                this.allPacketsData.yAxisArr[0].data.push(res.data[0]);
                            }
                        })
                        .catch(err => {
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取广播包组播包个数*/
            getAllMulAndBroData(time){
                this.$nextTick(()=> {

                    this.$axios.post(this.$baseUrl + '/flow/getMulticastAndBroadcastPacketTypeCount.do', this.$qs.stringify({
                        endtime:time
                    }))
                        .then(res => {
                            layer.closeAll('loading');
                            if (this.allMulAndBroData.yAxisArr[0].data.length < 60) {
                                this.allMulAndBroData.yAxisArr[0].data.push(res.data[0]);
                            } else {
                                this.allMulAndBroData.yAxisArr[0].data.shift();
                                this.allMulAndBroData.yAxisArr[0].data.push(res.data[0]);
                            }
                        })
                        .catch(err => {
                            layer.closeAll('loading');

                        })
                })
            }
        },
        components:{
            vEcharts,
            vBasedate,
            vFlowchartfrom
        }
    }
</script>

<style scoped>
    .from-wapper{
        float: right;
        margin-right: 10px;
        margin-top: 5px;
    }
    .flow-echarts-con{
        padding: 20px;
        /*padding-bottom: 80px;*/
        /*height: calc(100% - 100px);*/
    }
    .echarts-item{
        background: #303e4e;
        margin: 20px 0;

    }
    .echarts-tit{
        height: 80px;
        line-height: 80px;
        text-align: center;
        font-weight: 600;
        font-size: 21px;
        color: #5bc0de;
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
    .chart-from{
        padding-left: 50px;
    }
</style>
