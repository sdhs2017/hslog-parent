<template>
    <div class="content-bg">
        <div class="top-title">DHCP 日志</div>
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
    import bus from '../common/bus';
    export default {
        name: "dhcpLogs",
        data() {
            return {
                layerObj:{
                    detailData:{},//弹窗数据
                    layerState:false//弹窗状态
                },
                busName:'dhcpLogs',
                searchUrl:'log/getDNSLogListByBlend.do',
                searchConditions:{
                    'type':'dhcp',
                    'starttime':'',
                    'endtime':'',
                    'dhcp_type':'',
                    'client_mac':'',
                    'relay_ip':'',
                    'client_ip':''
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
                        label:'dhcp类型',
                        paramName:'dhcp_type',
                        model:{
                            model:''
                        },
                        itemType:'',
                        type:'input'
                    },
                    {
                        label:'mac地址',
                        paramName:'client_mac',
                        model:{
                            model:''
                        },
                        itemType:'',
                        type:'input'
                    },
                    {
                        label:'中继设备',
                        paramName:'relay_ip',
                        model:{
                            model:''
                        },
                        itemType:'',
                        type:'input'
                    },
                    {
                        label:'客户端ip',
                        paramName:'client_ip',
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
                        prop:'dhcp_type',
                        label:'dhcp类型',
                        width:''
                    },
                    {
                        prop:'client_mac',
                        label:'mac地址',
                        width:''

                    },

                    {
                        prop:'relay_ip',
                        label:'中继设备地址',
                        width:''
                    },
                    {
                        prop:'client_ip',
                        label:'客户端IP',
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
            bus.$on('dhcpLogs',params =>{
                this.searchConditions = params;
                this.searchConditions.type = 'dhcp';
            })
        },
        beforeDestroy(){
            bus.$off('dhcpLogs')
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
