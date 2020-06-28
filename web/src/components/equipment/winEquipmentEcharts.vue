<template>
    <div class="content-bg">
        <div class="top-title">‘{{equipmentName}}’ 数据统计
            <el-dropdown class="exportEchartsMenu">
                  <span class="el-dropdown-link">
                    导出<i class="el-icon-arrow-down el-icon--right"></i>
                  </span>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item @click.native="exportEcharts()">PDF格式</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
        <div class="equipment-echarts-wapper">
            <el-row :gutter="10" class="wapper-row">
                <el-col :span="8">
                    <div class="grid-content con-wapper msg" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
                        <h2 class="con-tit">资产信息</h2>
                        <div class="con">
                            <h3>一般信息</h3>
<!--                            <p><span class="p-t">启动时间：</span><span class="p-c">2019-06-12 10:10:10</span></p>-->
<!--                            <p><span class="p-t">本地时间：</span><span class="p-c">2019-06-12 10:10:10</span></p>-->
                            <p><span class="p-t">主机名：</span><span class="p-c">{{equipmentInfo.host.hostname}}</span></p>
                            <h3>OS</h3>
                            <p><span class="p-t">系统组：</span><span class="p-c">{{equipmentInfo.host.os.family}}</span></p>
                            <p><span class="p-t">系统版本：</span><span class="p-c">{{equipmentInfo.host.os.version}}</span></p>
                            <p><span class="p-t">内核：</span><span class="p-c">{{equipmentInfo.host.os.kernel}}</span></p>
                            <p><span class="p-t">内部版本：</span><span class="p-c">{{equipmentInfo.host.os.build}}</span></p>
                            <p><span class="p-t">系统类型：</span><span class="p-c">{{equipmentInfo.host.architecture}}</span></p>
                            <h3>agent</h3>
                            <p><span class="p-t">名称：</span><span class="p-c"> {{equipmentInfo.agent.type}}</span></p>
                            <p><span class="p-t">版本：</span><span class="p-c">{{equipmentInfo.agent.version}}</span></p>
<!--                            <p><span class="p-t">ping：</span><span class="p-c">Up(1)</span></p>-->
                        </div>
                    </div>
                </el-col>
                <el-col :span="16">
                    <el-row  :gutter="10" class="wapper-row">
                        <el-col :span="12">
                            <div class="statistics-wapper">
                                <h4>日志总数</h4>
                                <div class="statistics-value">{{logsCount}}</div>
                            </div>
                        </el-col>
                        <el-col :span="12">
                            <div class="statistics-wapper">
                                <h4>ERROR 日志总数</h4>
                                <div class="statistics-value" style="color: #f55446;">{{errorLogsCount}}</div>
                            </div>
                        </el-col>
                    </el-row>
                    <el-row  :gutter="10" >
                        <el-col :span="24">
                            <v-echarts-Table2
                                exportName="e1"
                                :busName="{clickName:equipmentId+'_line',exportName:'export'+equipmentId+'_line'}"
                                chartName="eqHourlyLogCount_line"
                                echartType="line"
                                :equipment="{id:equipmentId,type:logType}"
                                :urls="{tableUrl:'ecsCommon/getLogListByEquipment.do'}"
                            ></v-echarts-Table2>
                        </el-col>
                    </el-row>
                </el-col>
            </el-row>
            <el-row :gutter="10" class="wapper-row">
                <el-col :span="15">
                    <v-echarts-Table2
                        exportName="e4-e5"
                        :busName="{clickName:equipmentId+'_bar-pie',exportName:'export'+equipmentId+'_bar-pie'}"
                        chartName="eqEventType_bar"
                        echartType="bar-pie"
                        :eventTypeEchart = "true"
                        :equipment="{id:equipmentId,type:logType}"
                        :urls="{tableUrl:'ecsCommon/getListByBlend.do'}"
                    ></v-echarts-Table2>
                </el-col>
                <el-col :span="9">
                    <v-echarts-Table2
                        exportName="e3"
                        :busName="{clickName:equipmentId+'_bar',exportName:'export'+equipmentId+'_bar'}"
                        chartName="eqLogLevel_bar"
                        echartType="bar"
                        :equipment="{id:equipmentId,type:logType}"
                        :urls="{tableUrl:'ecsCommon/getLogListByEquipment.do'}"
                    ></v-echarts-Table2>
                </el-col>

            </el-row>
            <el-row :gutter="10" class="wapper-row">
                <el-col :span="24">
                    <v-echarts-Table2
                        exportName="e2"
                        :busName="{clickName:equipmentId+'_moreline',exportName:'export'+equipmentId+'_moreline'}"
                        chartName="eqWinlogHourlyEventCount_moreline"
                        echartType="moreline"
                        :equipment="{id:equipmentId,type:logType}"
                        :urls="{tableUrl:'ecsCommon/getListByBlend.do'}"
                    ></v-echarts-Table2>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script>
    import vEchartsTable2 from '../equipment/winEchartsTable';
    import bus from '../common/bus';
    import {downloadToPDF} from  '../../../static/js/common.js'
    export default {
        name: "winEquipmentEcharts",
        data(){
            return{
                loading:false,
                equipmentId:'',//资产id
                equipmentName:'',//资产名称
                equipmentInfo:{ //资产的基本信息
                    host:{
                        name:'',
                        hostname:'',
                        os:{
                            build: '',
                            family: '',
                            kernel:'',
                            name:'',
                            platform: '',
                            version: '',
                        }
                    },
                    agent:{
                        type:'',
                        version:''
                    }
                },
                logType:'',
                logsCount:0,//日志总数
                errorLogsCount:0,//错误日志总数
                eventCount:0,//事件总数
                dangerEventCount:0,//高危事件总数
                exportEchartsArr:[]//导出的图表集合
            }
        },
        created(){
            bus.$on('e1',(params)=>{
                this.exportEchartsArr[0]=params;
            })
            bus.$on('e2',(params)=>{
                this.exportEchartsArr[1]=params;
            })
            bus.$on('e3',(params)=>{
                this.exportEchartsArr[2]=params;
            })
            let t = 0;
            bus.$on('e4-e5',(params)=>{
                if(t === 0){
                    this.exportEchartsArr[3]=params;
                    t = 1;
                }else{
                    this.exportEchartsArr[4]=params;
                }

            })
        },
        methods:{
            /*获取日志总数于错误日志数*/
            getLogCount(id){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/ecsCommon/getIndicesCount.do',this.$qs.stringify({
                        hsData:JSON.stringify({'fields.equipmentid':id})
                    }))
                        .then((res)=>{
                            this.logsCount = res.data[0].indices;
                            this.errorLogsCount = res.data[0].indiceserror;
                        })
                        .catch((err)=>{
                            console.log(err)
                        })
                })
            },
            /*获取事件总数于高危事件数*/
            getEventCount(id){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/log/getEventsCount.do',this.$qs.stringify({equipmentid: id}))
                        .then((res)=>{
                            this.eventCount = res.data[0].events;
                            this.dangerEventCount = res.data[0].eventserror;
                        })
                        .catch((err)=>{
                            console.log(err)
                        })
                })
            },
            /*导出报表*/
            exportEcharts(){
                let imgBase64Arr = [];//用于存放图表Base64格式
                for(let i in this.exportEchartsArr){
                    //添加数据
                    imgBase64Arr.push(this.exportEchartsArr[i].getDataURL());
                }
                let canvas = $("canvas");
                let contentWidth = canvas.width();
                let contentHeight = canvas.height();
                downloadToPDF(imgBase64Arr,40,contentWidth,contentHeight)
            },
            /*获取资产的基本信息*/
            getEquipmentInfo(id){
                let obj = {
                    'fields.equipmentid':id
                }
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/ecsWinlog/getEquipmentInfo.do',this.$qs.stringify({
                        hsData:JSON.stringify(obj)
                    }))
                        .then(res=>{
                            this.loading = false;
                            this.equipmentInfo = res.data[0]
                        })
                        .catch(err=>{
                            this.loading = false;
                        })
                })
            }
        },
        watch:{
            'equipmentId'(newV,oldV){
                this.getLogCount(newV);
                //this.getEventCount(newV);
                this.getEquipmentInfo(newV)
            }
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                vm.$options.name = 'winEquipmentEcharts'+ to.query.id;
                //修改data参数
                vm.equipmentName = to.query.name;
                vm.busName = 'winEquipmentEcharts'+to.query.id;
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'winEquipmentEcharts'+to.query.id,
                    component:'equipment/winEquipmentEcharts.vue',
                    title:'统计'
                }
                sessionStorage.setItem('/winEquipmentEcharts'+to.query.id,JSON.stringify(obj))
                if(vm.equipmentId === '' || vm.equipmentId !== to.query.id){
                    vm.equipmentId = to.query.id;
                    vm.logType = to.query.logType;
                }

            })

        },
        components:{
            vEchartsTable2
        }
    }
</script>

<style scoped>
    .equipment-echarts-wapper{
        height: 100%;
        padding: 20px 40px;
    }
    .wapper-row{
        margin-bottom: 10px;
    }
    .statistics-wapper{
        height: 150px;
        background: #303e4e;
    }
    .statistics-wapper h4{
        color: #de946f;
        padding-left: 10px;
        height: 46px;
        line-height: 46px;
    }
    .statistics-value{
        height: 103px;
        line-height: 92px;
        text-align: center;
        color: #5bc0de;
        font-weight: 600;
        font-size: 32px;
    }
    .exportEchartsMenu{
        float: right;
        color: #fff;
        margin-right: 20px;
        cursor: pointer;
    }
    .msg{
        min-height: 440px;
        height: calc(38vh + 160px);
        font-size: 13px;
        background: #303e4e;
        padding: 20px;
        box-sizing: border-box;
        padding-top: 5px;
    }
    .msg h2{
        height: 50px;
        line-height: 50px;
        color: #e4956d;
        font-size: 20px;
    }
    .msg .con h3{
        height: 34px;
        line-height: 34px;
        padding-left: 30px;
        background: #386c9a;
    }
    .msg .con p{
        display: flex;
        height: 33px;
        line-height: 33px;
        /* padding-left: 60px; */
        background-color: #2f455c;
        border-bottom: 1px solid #365778;
    }
    .msg .con p .p-t{
        display: inline-block;
        width: 120px;
        text-align: end;
        margin-right: 20px;
    }
    .msg .con p .p-c{
        display: inline-block;
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
        word-break: break-all;
    }
</style>
