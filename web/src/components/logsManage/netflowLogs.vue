<template>
    <div class="content-bg">
        <div class="top-title">日志
            <div class="datepicker-wapper" style="padding-left: 20px;float: right;margin-right: 10px;">
                <baseDate type="datetimerange" :busName="this.busName" :defaultVal="this.timepicker"></baseDate>
            </div>
        </div>
        <div class="content-wapper">
            <v-logscontent :searchConditions="searchConditions" :tableHead="tableHead" :searchUrl="searchUrl" :layerObj="layerObj"  ref="logContent" ></v-logscontent>
        </div>
    </div>
    
</template>

<script>
    import vLogscontent from '@/components/logsManage/logsContent';
    import baseDate from '../common/baseDate'
    import bus from '../common/bus';
    import {savePath} from "../../../static/js/common";

    export default {
        name: "netflowLogs",
        data() {
            return {
                busName:'',
                timepicker:[],//日期值
                layerObj:{
                    detailData:{},//弹窗数据
                    layerState:false//弹窗状态
                },
                conditions:{
                    id:''
                },
                tableHead:[
                    {
                        prop:'logtime',
                        label:'时间',
                        width:''
                    },
                    {
                        prop:'ipv4_src_addr',
                        label:'源地址',
                        width:''
                    },
                    {
                        prop:'ipv4_dst_addr',
                        label:'目的地址',
                        width:''
                    },
                    {
                        prop:'l4_src_port',
                        label:'源端口',
                        width:''

                    },
                    {
                        prop:'l4_dst_port',
                        label:'目的端口',
                        width:''
                    },
                    {
                        prop:'protocol_name',
                        label:'传输层协议',
                        width:''
                    },
                    {
                        prop:'application_layer_protocol',
                        label:'应用层协议',
                        width:''
                    },
                    {
                        prop:'encryption_based_protection_protocol',
                        label:'加密协议',
                        width:''
                    },
                    {
                        prop:'packet_source',
                        label:'数据来源',
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
                                clickFun:(row)=>{
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
                ],
                searchUrl:'flow/getFlowListByBlend.do'
            }
        },
        computed:{
            searchConditions(){
                return  this.conditions;
            }
        },
        created(){
            this.busName = 'netflowLogs' + this.$route.query.iporport;
            this.timepicker = [this.$route.query.starttime,this.$route.query.endtime]
            bus.$on(this.busName,(arr)=>{
                this.timepicker = arr;
                this.conditions.starttime =  this.timepicker[0]
                this.conditions.endtime =  this.timepicker[1]
            })
        },
        beforeDestroy(){
            bus.$off(this.busName)
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                vm.$options.name = 'netflowLogs'+ to.query.iporport;
                vm.conditions = {
                    type:'defaultpacket',
                    ipv4_src_addr:to.query.ipv4_src_addr,
                    ipv4_dst_addr:to.query.ipv4_dst_addr,
                    l4_src_port:to.query.l4_src_port,
                    l4_dst_port:to.query.l4_dst_port,
                    starttime:to.query.starttime,
                    endtime:to.query.endtime
                }
                savePath(to.name,'logsManage/netflowLogs.vue','日志');
            })

        },
        components:{
            vLogscontent,
            baseDate
        }
    }
</script>

<style scoped>
    .content-wapper{
        padding: 10px;
    }
</style>
