<template>
    <div class="content-bg http-content">
        <div class="top-title">{{htmlTitle}} 日志</div>
        <div class="search-wapper">
            <el-form :inline="true" onsubmit="return false" class="demo-form-inline http-conditions-wapper">
                <div class="condition-top">
                    <div style="width: 384px;">
                        <span class="input-lable">日期范围</span>
                        <el-date-picker
                            v-model="timeArr"
                            type="datetimerange"
                            range-separator="至"
                            size="mini"
                            @change="timeChanage"
                            start-placeholder="开始日期"
                            value-format="yyyy-MM-dd HH:mm:ss"
                            end-placeholder="结束日期">
                        </el-date-picker>
                    </div>
                    <div>
                        <span class="input-lable">源地址</span>
                        <el-input size="mini" v-model="formConditions.ipv4_src_addr" placeholder="" style="border-radius: 0"></el-input>
                    </div>
                    <div>
                        <span class="input-lable">目的地址</span>
                        <el-input size="mini" v-model="formConditions.ipv4_dst_addr" placeholder=""></el-input>
                    </div>
                    <div>
                        <span class="input-lable">源端口</span>
                        <el-input size="mini" v-model="formConditions.l4_src_port" style="width: 66px;" placeholder=""></el-input>
                    </div>
                    <div>
                        <span class="input-lable">目的端口</span>
                        <el-input size="mini" v-model="formConditions.l4_dst_port" style="width: 66px;" placeholder=""></el-input>
                    </div>
                    <div>
                        <span class="input-lable" style="width: 86px;">数据来源</span>
                        <el-select size="mini" v-model="formConditions.packet_source" >
                            <el-option
                                v-for="item in options"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                    </div>
                </div>
                <div class="condition-bottom">
                    <div style="width: 33.333%;">
                        <span class="input-lable">传输层协议</span>
                        <el-input size="mini" v-model="formConditions.protocol_name" placeholder=""></el-input>
                    </div>
                    <div style="width: 33.333%;">
                        <span class="input-lable">应用层协议</span>
                        <el-input size="mini" v-model="formConditions.application_layer_protocol" placeholder=""></el-input>
                    </div>
                    <div style="width: 33.333%;">
                        <span class="input-lable">加密协议</span>
                        <el-input size="mini" v-model="formConditions.encryption_based_protection_protocol" placeholder=""></el-input>
                    </div>
                </div>
            </el-form>
            <el-button size="mini" type="primary" class="searchHttpLogs" @click="searchHttpLogs">检索</el-button>
        </div>
        <div class="content-wapper">
            <v-logscontent :searchConditions="searchConditions" :tableHead="tableHead" :searchUrl="searchUrl" :layerObj="layerObj"  ref="logContent" :moreDeleteBtn="true"></v-logscontent>
        </div>
    </div>
    
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import vLogscontent from '@/components/logsManage/logsContent';
    import {savePath,dateFormat} from "../../../static/js/common";
    import bus from '../common/bus';
    export default {
        name: "flowLogs",
        data() {
            return {
                layerObj:{
                    detailData:{},//弹窗数据
                    layerState:false//弹窗状态
                },
                htmlTitle:'流量',
                busName:'dnsLogs',
                //searchUrl:'log/getLogListByBlend.do',   //查询方法地址
                searchUrl:'flow/getFlowListByBlend.do',
                timeArr:[],//时间参数数组
                options:[{//请求响应数据
                    value: '',
                    label: ''
                },{
                    value: 'libpcap',
                    label: 'libpcap'
                },{
                    value: 'netflow',
                    label: 'netflow'
                }],
                formConditions:{ //查询条件框
                    type:'defaultpacket',//日志类型
                    starttime:'', //开始时间
                    endtime:'', //结束时间
                    ipv4_src_addr:'',//源地址
                    ipv4_dst_addr:'',//目的地址
                    l4_src_port:'',//源端口
                    l4_dst_port:'',//目的端口
                    protocol_name:'',//传输层协议
                    application_layer_protocol:'',//应用层协议
                    encryption_based_protection_protocol:'',//加密协议
                    packet_source:''//数据来源
                },
                searchConditions:{
                    id:''
                }, //查询条件集合
                tableHead:[                                 //表头列
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
                ],
            }
        },
        created(){
            //定义七天时间范围
            let endTime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
            let startTime= new Date();
            startTime.setTime(startTime.getTime() - 3600 * 1000 * 24 * 7);
            startTime = dateFormat('yyyy-mm-dd HH:MM:SS',startTime);
            this.timeArr=[startTime,endTime]
            this.formConditions.endtime= endTime;
            this.formConditions.starttime= startTime;
        },
        watch:{
            'htmlTitle'(){
                this.searchConditions = Object.assign({}, this.formConditions);//复制对象 避免指针
            }
        },
        methods:{
            /*时间改变事件*/
            timeChanage(val){
                if (val !== null){
                    this.formConditions.starttime = val[0];
                    this.formConditions.endtime = val[1];
                }else{
                    this.formConditions.starttime = '';
                    this.formConditions.endtime = '';
                }
            },
            /*检索按钮点击事件*/
            searchHttpLogs(){
                this.searchConditions = Object.assign({}, this.formConditions);//复制对象 避免指针
            }
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                vm.$options.name = to.name;
                //判断参数
                if(!$.isEmptyObject(to.query)){
                    //将路由存放在本地 用来刷新页面时添加路由
                    savePath(to.name,'logsManage/flowLogs.vue','日志')
                    //判断参数的类型
                    if(to.query.clickType === 'node'){
                        if(to.query.type === 'ip'){
                            vm.formConditions.multifield_ip = to.query.val;
                        }else if(to.query.type === 'url'){
                            vm.formConditions[to.query.kname] = to.query.val;
                        }
                        //修改标题
                        vm.htmlTitle = to.query.val;
                    }else if(to.query.clickType === 'link'){
                        if(to.query.type === "ip"){
                            vm.formConditions.ipv4_src_addr = to.query.ipv4_src_addr;
                            vm.formConditions.ipv4_dst_addr = to.query.ipv4_dst_addr;
                            //修改标题
                            vm.htmlTitle = to.query.ipv4_src_addr +' - '+to.query.ipv4_dst_addr;

                        }else if(to.query.type === "ip-url"){
                            vm.formConditions.ipv4_src_addr = to.query.ipv4_src_addr;
                            vm.formConditions[to.query.kname] = to.query.targetVal;
                            //修改标题
                            vm.htmlTitle = to.query.ipv4_src_addr +' - '+to.query.targetVal;
                        }
                    }
                }else{
                    //调用查询日志方法，模拟检索按钮
                    vm.searchHttpLogs();
                }


            })

        },
        components:{
            vLogscontent,
            vSearchForm
        }
    }
</script>

<style>
    .http-content .search-wapper{
        /*max-width: 1150px;*/
        height:61px;
        display: flex;
        justify-content: center;
        margin-bottom:20px ;
        transition: all .2s linear;
        overflow: hidden;
    }
    .http-conditions-wapper .el-input--mini .el-input__inner{
        -webkit-border-radius: 0;
        -moz-border-radius: 0;
        border-radius: 0;
    }
    .http-conditions-wapper .el-range-editor--mini.el-input__inner{
        -webkit-border-radius: 0;
        -moz-border-radius: 0;
        border-radius: 0;
    }
    .http-conditions-wapper div{
        font-size: 12px;
        display: flex;
    }
    .http-conditions-wapper div .input-lable{
        display: inline-block;
        font-size: 12px;
        height: 28px;
        background: #409eff;
        line-height: 28px;
        padding: 0 5px;
        color: #fff;
        word-break: keep-all;
        text-align: center;
    }
    .http-conditions-wapper div input{
        border-radius: 0!important;
    }
    .condition-top{
        margin-bottom: 5px;
    }
    .http-content .searchHttpLogs{
        -webkit-border-radius: 0;
        -moz-border-radius: 0;
        border-radius: 0;
    }
</style>
