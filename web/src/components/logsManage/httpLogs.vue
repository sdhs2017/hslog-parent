<template>
    <div class="content-bg http-content">
        <div class="top-title">HTTP 日志</div>
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
                        <span class="input-lable" style="width: 86px;">请求/响应</span>
                        <el-select size="mini" v-model="formConditions.requestorresponse" @change="reChange">
                            <el-option
                                v-for="item in options"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                    </div>
                </div>
                <div class="condition-bottom" v-if="formConditions.requestorresponse === 'request'">
                    <div style="width: 50%;">
                        <span class="input-lable">请求方法</span>
                        <el-input size="mini" v-model="formConditions.request_type" placeholder=""></el-input>
                    </div>
                    <div style="width: 50%;">
                        <span class="input-lable">请求URL</span>
                        <el-input size="mini" v-model="formConditions.request_url" placeholder=""></el-input>
                    </div>
                </div>
                <div class="condition-bottom" v-if="formConditions.requestorresponse === 'response'">
                    <div style="width: 100%;">
                        <span class="input-lable">响应状态</span>
                        <el-input size="mini" v-model="formConditions.response_state" placeholder=""></el-input>
                    </div>
                </div>
            </el-form>
            <el-button size="mini" type="primary" class="searchHttpLogs" @click="searchHttpLogs">检索</el-button>
        </div>
        <div class="content-wapper">
            <v-logscontent :searchConditions="searchConditions" :tableHead="tableHead" :searchUrl="searchUrl"  :layerObj="layerObj"  ref="logContent" :moreDeleteBtn="true"></v-logscontent>
        </div>
    </div>
    
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import vLogscontent from '@/components/logsManage/logsContent';
    import bus from '../common/bus';
    export default {
        name: "httpLogs",
        data() {
            return {
                layerObj:{
                    detailData:{},//弹窗数据
                    layerState:false//弹窗状态
                },
                busName:'dnsLogs',
                searchUrl:'flow/getHttpLogListByBlend.do',   //查询方法地址
                timeArr:[],//时间参数数组
                options:[{//请求响应数据
                    value: '',
                    label: ''
                },{
                    value: 'request',
                    label: 'request'
                },{
                    value: 'response',
                    label: 'response'
                }],
                formConditions:{ //查询条件框
                    type:'defaultpacket',//日志类型
                    application_layer_protocol:'http',
                    starttime:'', //开始时间
                    endtime:'', //结束时间
                    ipv4_src_addr:'',//源地址
                    ipv4_dst_addr:'',//目的地址
                    l4_src_port:'',//源端口
                    l4_dst_port:'',//目的端口
                    requestorresponse:'',//请求或响应
                    request_type:'',//请求方法
                    request_url:'',//请求URL
                    response_state:''//请求状态
                },
                searchConditions:{}, //查询条件集合
                tableHead:[                                 //表头列
                    {
                        prop:'logtime',
                        label:'时间',
                        width:'120'
                    },
                    {
                        prop:'ipv4_src_addr',
                        label:'源地址',
                        width:'110'
                    },
                    {
                        prop:'ipv4_dst_addr',
                        label:'目的地址',
                        width:'110'
                    },
                    {
                        prop:'l4_src_port',
                        label:'源端口',
                        width:'66'

                    },

                    {
                        prop:'l4_dst_port',
                        label:'目的端口',
                        width:'70'
                    },
                    {
                        prop:'requestorresponse',
                        label:'请求/响应',
                        width:'75'
                    },
                    {
                        prop:'request_type',
                        label:'请求方法',
                        width:'75'
                    },
                    {
                        prop:'request_url',
                        label:'请求url',
                        width:'90'
                    },
                    {
                        prop:'response_state',
                        label:'响应状态',
                        width:'95'
                    },
                    {
                        prop:'operation_des',
                        label:'内容',
                        width:'',
                        formatData:(val)=>{
                            return '<script type="text/html" style="display:block">'+val+'</scrip'+'t>'
                        }
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
            //赋值查询条件
            this.searchConditions = Object.assign({}, this.formConditions);//复制对象 避免指针
        },
        methods:{
            /*请求/响应改变事件*/
            reChange(val){
                //清空数据值
                this.formConditions.request_type = '';
                this.formConditions.request_url = '';
                this.formConditions.request_tresponse_stateype = '';
                //减少高度
                $(".search-wapper").height("28px")
                //判断值
                setTimeout(()=>{
                    if(val !== '') {
                        $(".search-wapper").height("61px")
                    }
                },200)

            },
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
        components:{
            vLogscontent,
            vSearchForm
        }
    }
</script>

<style>
    .http-content .search-wapper{
        /*max-width: 1150px;*/
        display: flex;
        justify-content: center;
        margin-bottom:20px ;
        transition: all .2s linear;
        height: 28px;
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
