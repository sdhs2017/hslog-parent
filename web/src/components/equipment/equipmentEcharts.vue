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
            <el-row class="wapper-row" :gutter="10">
                <el-col :span="6">
                    <div class="statistics-wapper">
                        <h4>日志总数</h4>
                        <div class="statistics-value">{{logsCount}}</div>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="statistics-wapper">
                        <h4>ERROR 日志总数</h4>
                        <div class="statistics-value" style="color: #f55446;">{{errorLogsCount}}</div>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="statistics-wapper">
                        <h4>事件总数</h4>
                        <div class="statistics-value">{{eventCount}}</div>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="statistics-wapper">
                        <h4>高危事件总数</h4>
                        <div class="statistics-value" style="color: #f55446;">{{dangerEventCount}}</div>
                    </div>
                </el-col>
            </el-row>
            <el-row :gutter="10">
                <el-col :span="12">
                    <v-echarts-Table
                        exportName="e1"
                        :busName="{clickName:equipmentId+'_line',exportName:'export'+equipmentId+'_line'}"
                        :echartsConfig="{title:'每小时日志数量统计', xAxisName:'小时', yAxisName:'数量/条',hoverText:'数量'}"
                        echartType="line"
                        :equipment="{id:equipmentId,type:logType}"
                        :urls="{echartUrl:'log/getCountGroupByTime.do',tableUrl:'log/getLogListByEquipment.do'}"
                    ></v-echarts-Table>
                </el-col>
                <el-col class="wapper-row" :span="12">
                    <v-echarts-Table
                        exportName="e2"
                        :busName="{clickName:equipmentId+'_moreline',exportName:'export'+equipmentId+'_moreline'}"
                        :echartsConfig="{title:'每小时事件数量统计', xAxisName:'小时', yAxisName:'数量/条',hoverText:'数量'}"
                        echartType="moreline"
                        :equipment="{id:equipmentId,type:logType}"
                        :urls="{echartUrl:'log/getCountGroupByEventType.do',tableUrl:'log/getEventListByBlend.do'}"
                    ></v-echarts-Table>
                </el-col>
            </el-row>
            <el-row :gutter="10">
                <el-col :span="10">
                    <v-echarts-Table
                        exportName="e3"
                        :busName="{clickName:equipmentId+'_bar',exportName:'export'+equipmentId+'_bar'}"
                        :echartsConfig="{title:'日志级别数量统计', xAxisName:'级别', yAxisName:'数量/条',hoverText:'数量'}"
                        echartType="bar"
                        :equipment="{id:equipmentId,type:logType}"
                        :urls="{echartUrl:'log/getCountGroupByParam.do',tableUrl:'log/getLogListByEquipment.do'}"
                    ></v-echarts-Table>
                </el-col>
                <el-col class="wapper-row" :span="14">
                    <v-echarts-Table
                        exportName="e4-e5"
                        :busName="{clickName:equipmentId+'_bar-pie',exportName:'export'+equipmentId+'_bar-pie'}"
                        :echartsConfig="{title:'事件类型数量统计',hoverText:'百分比'}"
                        echartType="bar-pie"
                        :eventTypeEchart = "true"
                        :equipment="{id:equipmentId,type:logType}"
                        :urls="{echartUrl:'log/getCountGroupByEvent.do',tableUrl:'log/getEventListByBlend.do'}"
                    ></v-echarts-Table>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script>
    import vEchartsTable from '../equipment/echartsTable';
    import bus from '../common/bus';
    import {downloadToPDF} from  '../../../static/js/common.js'
    export default {
        name: "equipmentEcharts",
        data(){
            return{
                equipmentId:'',//资产id
                equipmentName:'',//资产名称
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
                    this.$axios.post(this.$baseUrl+'/log/getIndicesCount.do',this.$qs.stringify({equipmentid: id}))
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
            }
        },
        watch:{
            'equipmentId'(newV,oldV){
                this.getLogCount(newV);
                this.getEventCount(newV);
            }
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                vm.$options.name = 'equipmentEcharts'+ to.query.id;
                //修改data参数
                vm.equipmentName = to.query.name;
                vm.busName = 'equipmentEcharts'+to.query.id;
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'equipmentEcharts'+to.query.id,
                    component:'equipment/equipmentEcharts.vue',
                    title:'统计'
                }
                sessionStorage.setItem('/equipmentEcharts'+to.query.id,JSON.stringify(obj))
                if(vm.equipmentId === '' || vm.equipmentId !== to.query.id){
                    vm.equipmentId = to.query.id;
                    vm.logType = to.query.logType;
                }

            })

        },
        components:{
            vEchartsTable
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
</style>
