<template>
    <div class="content-bg">
        <div class="top-title">DNS 日志</div>
        <div class="search-wapper">
            <v-search-form :formItem="formConditionsArr" :busName="busName"></v-search-form>
        </div>
        <div class="content-wapper">
            <v-logscontent :searchConditions="searchConditions" :tableHead="tableHead" :searchUrl="searchUrl" :layerObj="layerObj" ref="logContent" :moreDeleteBtn="true"></v-logscontent>
        </div>
    </div>
    
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import vLogscontent from '@/components/logsManage/logsContent';
    import {dateFormat} from "../../../static/js/common";
    import bus from '../common/bus';
    export default {
        name: "dnsLogs",
        data() {
            return {
                layerObj:{
                    detailData:{},//弹窗数据
                    layerState:false//弹窗状态
                },
                busName:'dnsLogs',
                searchUrl:'log/getDNSLogListByBlend.do',
                searchConditions:{
                    'type':'dns',
                    'starttime':'',
                    'endtime':'',
                    'dns_clientip':'',
                    'dns_view':'',
                    'dns_domain_name':'',
                    'dns_ana_type':'',
                    'dns_server':''
                },
                formConditionsArr:[
                    {
                        label:'事件范围',
                        type:'datetimerange',
                        itemType:'',
                        paramName:'time',
                        model:{
                            model:[]
                        },
                        val:''
                    },
                    {
                        label:'客户IP',
                        paramName:'dns_clientip',
                        model:{
                            model:''
                        },
                        itemType:'',
                        type:'input'
                    },
                    {
                        label:'访问域名',
                        paramName:'dns_domain_name',
                        model:{
                            model:''
                        },
                        itemType:'',
                        type:'input'
                    },
                    {
                        label:'解析类型',
                        paramName:'dns_ana_type',
                        model:{
                            model:''
                        },
                        itemType:'',
                        type:'input'
                    },
                    {
                        label:'服务器',
                        paramName:'dns_server',
                        model:{
                            model:''
                        },
                        itemType:'',
                        type:'input'
                    }
                    ,
                    {
                        label:'View',
                        paramName:'dns_view',
                        model:{
                            model:''
                        },
                        itemType:'',
                        type:'input'
                    }
                ],
                tableHead:[
                    {
                        prop:'logtime',
                        label:'时间',
                        width:''
                    },
                    {
                        prop:'equipmentname',
                        label:'资产名称',
                        width:''
                    },
                    {
                        prop:'dns_clientip',
                        label:'客户IP',
                        width:''
                    },
                    {
                        prop:'dns_ana_type',
                        label:'DNS解析类型',
                        width:''

                    },

                    {
                        prop:'dns_domain_name',
                        label:'访问域名',
                        width:''
                    },
                    {
                        prop:'dns_view',
                        label:'DNS view',
                        width:''
                    },
                    {
                        prop:'dns_server',
                        label:'DNS服务器',
                        width:''
                    },
                    {
                        prop:'tools',
                        label:'操作',
                        width:'60',
                        btns:[
                            {
                                icon:'el-icon-tickets',
                                text:'查看详情',
                                btnType: 'readDetails',
                                clickFun:(row,index)=>{
                                    //console.log(this.layerObj)
                                    this.layerObj.layerState = true;
                                    this.layerObj.detailData = row;
                                }
                            },
                            {
                                icon:'el-icon-circle-close',
                                text:'删除',
                                btnType: 'removeItem',
                                clickFun:(row,index)=>{
                                    this.$refs.logContent.removeParams([row])
                                }
                            }
                        ]
                    }
                ]
            }
        },
        created(){
            //定义七天时间范围
            let endTime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
            let startTime= new Date();
            startTime.setTime(startTime.getTime() - 3600 * 1000 * 24 * this.$store.state.beforeDay);
            startTime = dateFormat('yyyy-mm-dd HH:MM:SS',startTime);
            this.searchConditions = {
                'type':'dhcp',
                'dhcp_type':'',
                'client_mac':'',
                'relay_ip':'',
                'client_ip':'',
                endtime: endTime,
                starttime: startTime
            }
            this.formConditionsArr=[
                {
                    label:'事件范围',
                    type:'datetimerange',
                    itemType:'',
                    paramName:'time',
                    model:{
                        model:[startTime,endTime]
                    },
                    val:''
                },
                {
                    label:'客户IP',
                    paramName:'dns_clientip',
                    model:{
                        model:''
                    },
                    itemType:'',
                    type:'input'
                },
                {
                    label:'访问域名',
                    paramName:'dns_domain_name',
                    model:{
                        model:''
                    },
                    itemType:'',
                    type:'input'
                },
                {
                    label:'解析类型',
                    paramName:'dns_ana_type',
                    model:{
                        model:''
                    },
                    itemType:'',
                    type:'input'
                },
                {
                    label:'服务器',
                    paramName:'dns_server',
                    model:{
                        model:''
                    },
                    itemType:'',
                    type:'input'
                }
                ,
                {
                    label:'View',
                    paramName:'dns_view',
                    model:{
                        model:''
                    },
                    itemType:'',
                    type:'input'
                }
            ],
            bus.$on('dnsLogs',params =>{
                this.searchConditions = params;
                this.searchConditions.type = 'dns';
            })
        },
        beforeDestroy(){
            bus.$off('dnsLogs')
        },
        components:{
            vSearchForm,
            vLogscontent
        }
    }
</script>

<style scoped>
    .search-wapper{
        display: flex;
        justify-content: center;
        margin-bottom: 20px;
    }
    .content-wapper{
        padding: 10px;
    }
</style>
