<template>
    <div class="content-bg"  v-loading="loading"  element-loading-background="rgba(26,36,47, 0.2)">
        <div class="top-title">图表列表</div>
        <div class="tools-wapper">
            <el-button  type="primary" plain @click="addChartsState = true" >添加</el-button>
            <el-button  type="primary" plain @click="getChartsList" >刷新</el-button>
        </div>
        <div class="table-wapper">
            <v-basetable :tableHead="tableHead" :tableData="tableData" :busName="busNames"></v-basetable>
        </div>
        <el-dialog title="选择图表类型" :visible.sync="addChartsState" width="440px">
            <ul class="chart-type-list">
                <li @click="addChartsState = false">
                    <router-link :to="{name:'barChart'}">
                        <p class="li-top-i"><i class="el-icon-s-data"></i></p>
                        <p class="li-bottom-tit">柱状图</p>
                    </router-link>
                </li>
                <li @click="addChartsState = false">
                    <router-link :to="{name:'lineChart'}">
                        <p class="li-top-i"><i class="el-icon-share"></i></p>
                        <p class="li-bottom-tit">折线图</p>
                    </router-link>
                </li>
                <li @click="addChartsState = false">
                    <router-link :to="{name:'pieChart'}">
                        <p class="li-top-i"><i class="el-icon-pie-chart"></i></p>
                        <p class="li-bottom-tit">饼图</p>
                    </router-link>
                </li>
                <li @click="addChartsState = false">
                    <router-link :to="{name:'metricChart'}">
                        <p class="li-top-i" style="font-style: italic;font-size: 27px;font-family: 'electronicFont';">123</p>
                        <p class="li-bottom-tit">指标</p>
                    </router-link>
                </li>
            </ul>
        </el-dialog>
    </div>
    
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import vBasetable from '../common/Basetable';
    import {jumpHtml} from "../../../static/js/common";
    import bus from '../common/bus';
    export default {
        name: "chartsList",
        data() {
            return {
                loading:false,
                addChartsState:false,
                busNames:{},
                tableHead:[
                    {
                        prop:'title',
                        label:'标题',
                        width:'',
                    },
                    {
                        prop:'type',
                        label:'类型',
                        width:'100',
                        formatData:(val)=>{
                           switch (val) {
                               case 'bar':
                                   return '柱状图';
                               case 'line':
                                   return '折线图';
                               case 'pie' :
                                   return '饼图'
                               default :
                                   return val;
                           }
                        }
                    },
                    /*{
                        prop:'createTime',
                        label:'创建时间',
                        width:'',
                        formatData:(val)=>{return val.split('.')[0]}
                    },*/
                    {
                        prop:'description',
                        label:'描述',
                        width:'',
                    },
                    {
                        prop: 'tools',
                        label: '操作',
                        width: '100',
                        btns: [
                            {
                                icon: 'el-icon-view',
                                text: '查看',
                                clickFun: (row, index) => {
                                    this.seeChart(row,index)
                                }
                            },
                            {
                                icon: 'el-icon-edit',
                                text: '编辑',
                                formatData:(obj)=>{
                                    return obj.editable
                                },
                                clickFun: (row, index) => {
                                   this.reviseChart(row,index)
                                }
                            },
                            {
                                icon: 'el-icon-error',
                                text: '删除',
                                formatData:(obj)=>{
                                    return obj.deletable
                                },
                                clickFun: (row, index) => {
                                    this.removeChart(row,index)
                                }
                            }
                        ]
                    },
                    {
                        prop:'',
                        label:'状态',
                        width:'60',
                        formatData:(val,row)=>{
                            if(!row.deletable && !row.editable){
                                return '<i class="el-icon-lock" style="color:#e4956d"></i>'
                            }
                        }
                    }
                ],
                tableData:[
                    // {id:'aaa',name:'日志级别数量统计',type:'bar',createTime:'2020-02-13 12:10:10'},
                    // {id:'bbb',name:'事件级别数量统计',type:'line',createTime:'2020-02-13 12:10:10'}
                ]
            }
        },
        created(){
            this.getChartsList();
        },
        methods:{
            /*获取图表列表*/
            getChartsList(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/BI/getVisualizations.do','')
                        .then(res=>{
                            this.loading = false;
                            let obj =res.data
                            if (obj.success == 'true'){
                                this.tableData = obj.data;
                            } else {
                                layer.msg(res.data.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.loading = false;
                            layer.msg('获取数据失败',{icon:5})
                        })
                })
            },
            /*跳转页面*/
            goToHtml(chartsType,chartsId){
                switch (chartsType) {
                    case 'bar':
                        jumpHtml('barChart','dashboard/barChart.vue',{},'创建柱状图');
                        this.addChartsState = false;
                        break;
                    case 'pie':
                        jumpHtml('barChart','dashboard/pieChart.vue',{},'创建饼图');
                        this.addChartsState = false;
                        break;
                    case 'line':
                        jumpHtml('barChart','dashboard/lineChart.vue',{},'创建折线图');
                        this.addChartsState = false;
                        break;
                    case 'metric':
                        jumpHtml('metricChart','dashboard/metricChart.vue',{},'创建指标');
                        this.addChartsState = false;
                        break;
                }
            },
            /*查看图表*/
            seeChart(rowData,index){
                switch (rowData.type) {
                    case 'bar':
                        jumpHtml('seeBarChart'+rowData.id,'dashboard/barChart.vue',{name:rowData.title,id:rowData.id,type:'see'},' 查看');
                        break;
                    case 'pie':
                        jumpHtml('seePieChart'+rowData.id,'dashboard/pieChart.vue',{name:rowData.title,id:rowData.id,type:'see'},' 查看');
                        break;
                    case 'line':
                        jumpHtml('seeLineChart'+rowData.id,'dashboard/lineChart.vue',{name:rowData.title,id:rowData.id,type:'see'},' 查看');
                        break;
                    case 'metric':
                        jumpHtml('seeMetricChart'+rowData.id,'dashboard/metricChart.vue',{name:rowData.title,id:rowData.id,type:'see'},' 查看');
                        break;
                }
            },
            /*修改按钮*/
            reviseChart(rowData,index){
                switch (rowData.type) {
                    case 'bar':
                        jumpHtml('editBarChart'+rowData.id,'dashboard/barChart.vue',{name:rowData.title,id:rowData.id,type:'edit'},' 编辑');
                        break;
                    case 'pie':
                        jumpHtml('editPieChart'+rowData.id,'dashboard/pieChart.vue',{name:rowData.title,id:rowData.id,type:'edit'},' 编辑');
                        break;
                    case 'line':
                        jumpHtml('editLineChart'+rowData.id,'dashboard/lineChart.vue',{name:rowData.title,id:rowData.id,type:'edit'},' 编辑');
                        break;
                    case 'metric':
                        jumpHtml('editMetricChart'+rowData.id,'dashboard/metricChart.vue',{name:rowData.title,id:rowData.id,type:'edit'},' 编辑');
                        break;
                }
            },
            /*删除*/
            removeChart(row,index){
                layer.confirm('您确定删除么？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=> {
                    this.$nextTick(()=>{
                        this.loading = true;
                        this.$axios.post(this.$baseUrl+'/BI/deleteVisualizationById.do',this.$qs.stringify({
                            id:row.id
                        }))
                            .then(res=>{
                                this.loading = false;
                                let obj =res.data
                                if (obj.success == 'true'){
                                    layer.msg(res.data.message,{icon:1})
                                    setTimeout(()=>{
                                        this.getChartsList();
                                    },1000)

                                } else {
                                    layer.msg(res.data.message,{icon:5})
                                }
                            })
                            .catch(err=>{
                                this.loading = false;

                            })
                    })
                })

            }
        },
        components:{
            vSearchForm,
            vBasetable
        }
    }
</script>

<style scoped>
    .tools-wapper{
        padding: 0 10px;
        margin: 10px 0;
    }
    .table-wapper{
        padding: 0 10px;
    }
    .chart-type-list{
        overflow: hidden;
    }
    .chart-type-list li{
        float: left;
        margin: 15px;
        width: 100px;
        height: 100px;
        background: #3a4a5d;
        color: #fff;
        box-sizing: border-box;
    }
    .chart-type-list li a{
        color: #fff;
    }
    .chart-type-list li:hover{
        cursor: pointer;
        border: 1px solid #1dd8fe;
    }
    .chart-type-list li p{
        text-align: center;
    }
    .li-top-i{
        height: 40px;
        line-height: 55px;
        font-size: 34px;
    }
    .li-bottom-tit{
        height: 60px;
        line-height: 60px;
        font-size: 16px;
        font-weight: 600;
    }
</style>
