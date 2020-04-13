<template>
    <div v-if="equipment.id !== ''">
        <div v-show="echartsStatus" class="echarts-content">
            <div class="echarts-time">
                <el-date-picker class="equipment-echart-time" value-format="yyyy-MM-dd" v-model="timeVal" @change="dateChage" type="date" placeholder="选择日期" size="mini"></el-date-picker>
            </div>
            <div class="echarts-data" v-if="!eventTypeEchart">
                <v-echarts :echartType="this.echartType" :echartData="this.echartData[this.echartDataType]" :busName="busName"></v-echarts>
            </div>
            <div class="echarts-data" v-else style="display: flex">
                <v-echarts echartType="bar" :echartData="this.echartData.barData" :busName="busName"></v-echarts>
                <v-echarts echartType="pie" :echartData="this.echartData.pieData" :busName="busName"></v-echarts>
            </div>
        </div>
        <div v-show="!echartsStatus" class="echarts-content">
            <div class="equipment-table-title">{{tableTitle}} <b @click="()=>{this.echartsStatus = true}">返回图表</b></div>
            <div class="equipment-table-content">
                <v-basetable2 :tableHead="tableHead" :tableData="tableData"></v-basetable2>
            </div>
            <div class="equipment-table-page">
                <span>总数为 <b style="color:#56b3cf;">{{allCount}}</b> 条</span>
                <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="pageSize" :total="allCount"></el-pagination>
            </div>
        </div>
    </div>
</template>

<script>
    import vEcharts from '../common/echarts'
    import vBasetable2 from '../common/Basetable2';
    import bus from '../common/bus';
    export default {
        // name: "echartsTable",
        props:{
            echartsConfig:{
                type: Object,
            },
            eventTypeEchart:{
                type: Boolean,
                default(){
                    return false
                }
            },
            equipment:{
                type: Object,
                default(){
                    return{
                        id:'',
                        type:''
                    }
                }
            },
            urls:{
                type:Object,
                default(){
                    return{
                        echartUrl:'',
                        tableUrl:''
                    }
                }
            },
            echartType:{
                type:String
            },
            exportName:{
                type:String
            },
            busName:{
                type:Object,
                default(){
                    return{
                        clickName:'',
                        exportName:''
                    }
                }
            }
        },
        data(){
            return{
                echartsStatus:true,
                timeVal:'',//时间
                echartDataType:'',//用于区分图表数据
                echartData:{
                    barData:{//柱状图数据
                        baseConfig:{
                        },
                        xAxisArr:[],
                        yAxisArr:[]
                    },
                    pieData:{//饼状图数据
                        baseConfig:{
                        },
                        xAxisArr:[],
                        yAxisArr:[]
                    },
                    lineData:{//折线图数据
                        baseConfig:{
                        },
                        xAxisArr:["00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"],
                        yAxisArr:[]
                    }
                },
                echartsConditions:{
                    equipmentid:'',
                    type:'',
                    param:'',
                    index:'estest'
                },//图表参数
                tableCondition:{},//表格数据参数
                /*busName:{//监听名称
                    clickName:'',
                    exportName:''
                },*/
                tableHead:[],
                logTableHead:[//表头
                    {
                        prop:'@timestamp',
                        label:'时间',
                        width:'150'
                    },
                    {
                        prop:'log.level',
                        label:'级别',
                        width:'100'
                    },
                    {
                        prop:'agent.type',
                        label:'日志类型',
                        width:'100'
                    },
                    {
                        prop:'message',
                        label:'日志内容',
                        width:''
                    },
                ],
                eventTableHead:[
                    {
                        prop:'@timestamp',
                        label:'时间',
                        width:'150'
                    },
                    {
                        prop:'event.action',
                        label:'事件名称',
                        width:'150'
                    },
                    {
                        prop:'agent.type',
                        label:'日志类型',
                        width:'100'
                    },
                    {
                        prop:'winlog',
                        label:'事件原因',
                        width:'100'
                    },
                    {
                        prop:'message',
                        label:'日志内容',
                        width:''
                    }
                ],
                tableData:[],//表格数据
                pageSize:10,//每页条数
                c_page:1,//当前页
                allCount:0,//总条数
                clickXVal:'',//点击图表的x轴数据
                tableTitle:'',//列表头文字
                echartdata:{},
                exportEchartdata:false
            }
        },
        created(){
            //获取当前日期
            let day1 = new Date();
            let mon = day1.getMonth()+1;
            let da = day1.getDate();
            if( mon < 10){
                mon = "0"+(day1.getMonth()+1);
            }
            if(da < 10){
                da = "0"+ (day1.getDate());
            }
            this.timeVal = day1.getFullYear()+"-" + mon + "-" + da;

            //监听点击事件

        },
        beforeDestroy(){
            bus.$off(this.busName.clickName)
        },
        watch:{
            /*检测资产id的变动*/
            'equipment.id'(){
               this.$nextTick(()=>{
                   //请求参数
                   //this.echartsConditions.equipmentid = this.equipment.id;
                   // this.echartsConditions.type = this.equipment.type;
                   // this.echartsConditions.param = this.timeVal;
                   // this.echartsConditions.index = 'estest';
                   this.echartsConditions = {
                       '@timestamp' : this.timeVal,
                       'fields.equipmentid':this.equipment.id
                   }

                   //配置图表基本设置
                    if(this.echartType === 'line' || this.echartType === 'moreline'){
                        this.echartDataType = 'lineData';
                        this.echartData.lineData.baseConfig = this.echartsConfig;
                    }else if(this.echartType === 'bar'){
                        this.echartDataType = 'barData';
                        this.echartData.barData.baseConfig = this.echartsConfig;
                   }else {
                        this.echartData.barData.baseConfig = {
                            title:'事件类型数量-柱状图',
                            xAxisName:'类型',
                            yAxisName:'数量/条',
                            hoverText:'数量',
                            rotate:'20'
                        }
                        this.echartData.pieData.baseConfig = {
                            title:'事件类型数量-饼图',
                            hoverText:'百分比'
                        }
                    }

                   // //日志级别参数
                   // if(this.echartsConfig.title === '日志级别数量统计'){
                   //     this.echartsConditions.type='';
                   //     this.echartsConditions.param='operation_level';
                   //     this.echartsConditions.time = this.timeVal;
                   // }
                   //请求数据
                   this.getEchartsData(this.echartsConditions);
                   //绑定监听事件
                   // this.busName.clickName = this.equipment.id+'_'+this.echartType;
                   //this.busName.exportName ='export'+this.equipment.id+'_'+this.echartType;
                   //监听导出事件
                   bus.$on(this.busName.exportName,(params)=>{
                       this.exportEchartdata = true;
                       //向父组件传递参数
                       this.echartdata = params;
                       bus.$emit(this.exportName,params)
                   })
                   //监听点击事件
                   bus.$on(this.busName.clickName,(params)=>{
                       this.c_page = 1;
                       this.clickXVal = params.name;
                       if(params.name.length < 2){
                           params.name = '0'+params.name
                       }
                       this.tableCondition = {
                           starttime : this.timeVal+' '+ params.name+':00:00',
                           endtime : this.timeVal+' '+ params.name+':59:59',
                           'fields.equipmentid':this.equipment.id,
                       }
                       //隐藏图表
                       this.echartsStatus = false;
                       //判断当前的图表类型
                       if(this.echartType === 'line'){//日志总数
                            this.tableTitle = this.timeVal+' '+this.clickXVal+' 日志列表'
                            this.tableHead = this.logTableHead;
                       }else if(this.echartType === 'moreline'){//事件每小时
                           this.tableTitle = this.timeVal+' '+this.clickXVal+' '+params.seriesName+'事件列表'
                           this.clickXVal = params.name;
                           console.log(params.name)
                           this.tableCondition = {
                               starttime : this.timeVal+' '+ params.name+':00:00',
                               endtime : this.timeVal+' '+ params.name+':59:59',
                               'fields.equipmentid':this.equipment.id
                           }
                           this.tableCondition['event.action'] = params.seriesName;
                           this.tableHead = this.eventTableHead;
                       }else if(this.echartType === 'bar'){//日志类型
                           this.tableTitle = this.timeVal+' '+this.clickXVal+' 日志列表'
                           this.tableCondition = {
                               starttime : this.timeVal+' 00:00:00',
                               endtime : this.timeVal+' 23:59:59',
                               'fields.equipmentid':this.equipment.id,
                           }
                           this.tableCondition['log.level'] = params.name;
                           this.tableHead = this.logTableHead;
                       }else{//事件类型
                           this.tableTitle = this.timeVal+' '+this.clickXVal+' 事件列表';
                           this.tableCondition = {
                               starttime : this.timeVal+' 00:00:00',
                               endtime : this.timeVal+' 23:59:59',
                               'fields.equipmentid':this.equipment.id,
                           }
                           this.tableCondition['event.action'] = params.name;
                           this.tableHead = this.eventTableHead;
                       }
                       this.getLogsListData(1)
                   })
               })
            },
            'exportEchartData'(){
                console.log('eeeee')
                bus.$emit('dd',this.echartdata)
            }
        },
        methods:{
            /*获取图表数据*/
            getEchartsData(obj){
                let loading = layer.load(1)
                //请求数据
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/'+this.urls.echartUrl,this.$qs.stringify({
                        hsData : JSON.stringify(obj)
                    }))
                        .then((res)=>{
                            layer.close(loading);
                            //判断图表的类型
                            if(this.echartType === 'line'){
                                let yVal = [];
                                let xVal = [];
                                for(let i in res.data){
                                    xVal.push(res.data[i].hour);
                                    yVal.push(res.data[i].count)
                                }
                                this.echartData.lineData.yAxisArr = yVal;
                                this.echartData.lineData.xAxisArr = xVal;
                            }else if(this.echartType === 'moreline'){
                                this.echartData.lineData.yAxisArr = [];
                                let color = ['#00EABD','#20C1F3','#FC686F','#F9D124','#DE1AFB','#C0D7FC','#A9F4B7','#FF9E96','#75B568','#323A81'];
                                let k = 0;
                                for(let i in res.data[0]){
                                    let item = res.data[0][i];
                                    let data=[];
                                    for (let j in item){
                                        data.push(item[j].count)
                                    }
                                    let obj = {
                                        name:i,
                                        color:color[k],
                                        data:data
                                    }
                                    this.echartData.lineData.yAxisArr.push(obj);
                                    k++;
                                    if(k === color.length){
                                        k = 0;
                                    }
                                }

                            }else if(this.echartType === 'bar'){
                                this.echartData.barData.xAxisArr = [];
                                this.echartData.barData.yAxisArr = [];

                                for(let i in res.data[0]){
                                    this.echartData.barData.xAxisArr.push(i);
                                    this.echartData.barData.yAxisArr.push(res.data[0][i]);
                                }

                            }else{
                                //事件柱状图
                                this.echartData.barData.xAxisArr = [];
                                this.echartData.barData.yAxisArr = [];
                                //事件饼图
                                this.echartData.pieData.yAxisArr = [];
                                for(let i in res.data[0]){
                                    this.echartData.barData.xAxisArr.push(i);
                                    this.echartData.barData.yAxisArr.push(res.data[0][i]);
                                    this.echartData.pieData.yAxisArr.push({name:i,value:res.data[0][i]});
                                }

                            }

                        })
                        .catch((err)=>{
                            layer.close(loading);
                        })
                })
            },
            /*获取日志列表数据*/
            getLogsListData(page){
                let loading = layer.load(1)
                this.tableCondition.page = page;
                this.tableCondition.size = this.pageSize;
                let hsObj = {};
                hsObj.hsData = this.tableCondition;
                this.$nextTick(()=>{
                    this.$axios.get(this.$baseUrl+'/'+this.urls.tableUrl,{params:hsObj})
                        .then((res)=>{
                            layer.close(loading);
                            //console.log(res.data)
                            this.allCount = res.data[0].count;
                            this.tableData = res.data[0].list;
                        })
                        .catch((err)=>{
                            layer.close(loading);
                        })
                })
            },
            /*日期改变*/
            dateChage(){
                // if (this.echartsConfig.title === '日志级别数量统计'){
                //     this.echartsConditions.time = this.timeVal;
                // } else{
                //     this.echartsConditions.param = this.timeVal;
                // }
                this.echartsConditions['@timestamp'] = this.timeVal;
                this.getEchartsData(this.echartsConditions);
            },
            /*页码改变*/
            handleCurrentChange(page){
                this.getLogsListData(page)
            }
        },
        components:{
            vEcharts,
            vBasetable2
        }
    }
</script>

<style scoped>
    .echarts-content{
        height: 38vh;
        min-height: 280px;
        background: #303e4e;
    }
    .echarts-time{
        height: 40px;
        line-height: 40px;
        padding-left: 20px;
    }
    .equipment-echart-time{
        width: 126px;
    }
    .echarts-data{
        height: calc(100% - 40px);
    }
    .equipment-table-title{
        height: 40px;
        line-height: 40px;
        text-align: center;
        color: #d3946f;
        font-weight: 600;
        border-bottom: 1px solid #5a7494;
    }
    .equipment-table-title b{
        float: right;
        font-size: 13px;
        margin-right: 10px;
        cursor: pointer;
        color: #56b3cf;
    }
    .equipment-table-content{
        height: calc(100% - 70px);
        overflow: auto;
    }
    .equipment-table-page{
        border-top: 1px solid #5a7494;
        height: 30px;
        font-size: 13px;
        display: flex;
        justify-content: flex-end;
        align-items: center;
    }
</style>
