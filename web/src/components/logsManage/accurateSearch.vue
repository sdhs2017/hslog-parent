<template>
    <div class="content-bg">
        <div class="top-title">精确查询</div>
        <div class="search-wapper">
            <v-search-form :formItem="formConditionsArr" :busName="busName"></v-search-form>
        </div>
        <div class="content-wapper">
            <v-logscontent :searchConditions="searchConditions" :tableHead="tableHead" :searchUrl="searchUrl" :exportBtn="true" :layerObj="layerObj" ref="logContent" :moreDeleteBtn="true"></v-logscontent>
        </div>
        <div class="export-progress-box"  :style="progressRight">
            <div class="export-progress-wapper">
                <div class="progress-name" v-html="progressTitle"></div>
                <div class="progress-val">{{progressVal}}</div>
            </div>
        </div>
    </div>
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import vLogscontent from '@/components/logsManage/logsContent';
    import bus from '../common/bus';

    export default {
        name: "accurateSearch",
        data(){
            return{
                layerObj:{
                    detailData:{},//弹窗数据
                    layerState:false//弹窗状态
                },
                busName:'accurateSeatch',
                words:'',//检索条件
                searchConditions:{
                },
                searchUrl:'log/getLogListByBlend.do',//数据地址
                formConditionsArr:[],//查询条件
                tableHead:[],//表头列
                firstSearch:true,//第一次查询标识（创建分页）
                logBaseJson:[],
                logType:[],//日志类型
                logLevel:[],//日志级别
                levelVal:'',//级别内容
                progressRight:'right:-90px;',//导出日志进度框右偏移量
                progressTitle:'导出中<br/><i class="el-icon-loading"></i>',
                progressVal:'',
                exportInternal:''
            }
        },
        created(){
            //表头
            this.tableHead = [
                {
                    prop:'logtime',
                    label:'时间',
                    width:'150'
                },
                {
                    prop:'operation_level',
                    label:'级别',
                    width:'100'
                },
                {
                    prop:'type',
                    label:'日志类型',
                    width:'100'

                },
                {
                    prop:'equipmentname',
                    label:'资产名称',
                    width:'125',
                    clickFun:(item)=>{
                        if(item.equipmentid ==='unknown'){
                            layer.msg("资产未知",{icon:5})
                        }else{
                            //跳转到资产列表页面
                            this.$router.push({name:'equipment',params:{equipmentid: item.equipmentid}})
                        }
                    }
                },
                {
                    prop:'ip',
                    label:'IP',
                    width:'125'
                },
                {
                    prop:'operation_des',
                    label:'日志内容',
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
            this.getLogType();
            //检索条件
            this.formConditionsArr = [
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
                    label:'日志类型',
                    paramName:'type',
                    type:'select',
                    itemType:'',
                    model:{
                        model:''
                    },
                    options:this.logType
                },
                {
                    label:'日志级别',
                    paramName:'operation_level',
                    type:'select',
                    itemType:'multiple',
                    model:{
                        model:this.levelVal
                    },
                    options:this.logLevel
                },
                {
                    label:'IP地址',
                    paramName:'ip',
                    itemType:'',
                    model:{
                        model:''
                    },
                    type:'input'
                },
                {
                    label:'资产名称',
                    paramName:'hostname',
                    model:{
                        model:''
                    },
                    itemType:'',
                    type:'input'
                }

            ]
            //检测搜索条件
            bus.$on(this.busName,(params)=>{
                console.log(params)
                this.searchConditions = params;
            })
            //检测日志类型改变
            bus.$on('logTypeChange',(params)=>{
                this.logLevel.length = 0;
                this.levelVal = '';
                for(let i =0;i<this.logBaseJson.length;i++){
                    if(this.logBaseJson[i].type == params){
                       for(let j=0;j< this.logBaseJson[i].level.length;j++){
                           let obj = {
                               value:this.logBaseJson[i].level[j],
                               label:this.logBaseJson[i].level[j]
                           };
                           this.logLevel.push(obj);
                       }
                    }
                }
            })
            //获取本地是否有导出任务
            //有
            if(localStorage.getItem('exportLogs')){
                this.progressRight = 'right:5px;';
                this.progressTitle = '导出中<br/>...';
                //开启定时器 查看导出进度
                this.exportInternal = setInterval(this.updataProgressValue,1000);
            };
            //检测导出日志进度
            bus.$on('exportStart',(params)=>{
                this.progressRight = 'right:5px;';
                //开启定时器 查看导出进度
                this.exportInternal = setInterval(this.updataProgressValue,1000);
            })
            //检测导出失败
            bus.$on('exportFail',(params)=>{
                this.progressRight = 'right:-90px;'
                layer.msg('导出失败',{icon: 5});
                clearInterval(this.exportInternal);
                //删除本地保存的导出任务
                localStorage.removeItem('exportLogs');
            })
        },
        watch:{
            '$route' (to, from) {
               // console.log(to)
            }
        },
        methods:{
            //获得日志类型
            getLogType(){
                this.$nextTick(()=>{
                    this.$axios.get('static/filejson/logTypeLevel.json',{})
                        .then((res)=>{
                            this.logBaseJson = res.data;
                            this.logType.push({value:'',label:'全部'});
                            for(let i=0;i<res.data.length; i++){
                                let obj = {
                                    value:res.data[i].type,
                                    label:res.data[i].type
                                };
                                this.logType.push(obj);

                            }
                        })
                        .catch((err)=>{
                            console.log(err)
                        })
                })
            },
            /*获取导出日志进度*/
            updataProgressValue(){
                this.$nextTick(()=>{
                    this.$axios.get(this.$baseUrl+'/log/getExportProcess.do',{})
                        .then((res)=>{
                            //导出完成
                            if(res.data[0].state === 'finished'){
                                //清楚定时器
                                clearInterval(this.exportInternal);
                                //更改显示状态
                                this.progressTitle = '导出<br/>完成';
                                this.progressVal = res.data[0].value;
                                //延时隐藏进度条
                                setTimeout(()=>{
                                    this.progressRight = 'right:-90px;'
                                },5000)
                                //延时清空进度条数据
                                setTimeout(()=>{
                                    this.progressTitle = '导出中<br/>...';
                                    this.progressVal = '';
                                },5300)
                                //导出成功弹窗
                                layer.msg('导出成功',{icon:1});
                                //删除本地保存的导出任务
                                localStorage.removeItem('exportLogs');
                            }else{ //导出未完成 更新进度
                                this.progressVal = res.data[0].value;
                            }
                        })
                        .catch((err)=>{
                            console.log(err)
                        })
                })
            }
        },
        components:{
            vSearchForm,
            vLogscontent
        }
    }
</script>

<style scoped>
    .top-title{
        font-size: 18px;
        font-weight: 600;
        padding-left: 20px;
        height: 50px;
        line-height: 50px;
        margin-bottom: 20px;
        color: #e4956d;
        text-shadow: 3px 4px 3px #4e7ba9;
        border-bottom: 1px solid #303e4e;
    }
    .search-wapper{
        display: flex;
        justify-content: center;
        margin-bottom: 20px;
    }
    .input-searchword{
        width: 300px;
    }
    .content-wapper{
        height: 600px;
        padding: 0 10px 20px 10px;
        /*border:2px solid #303e4e;*/
    }
    .content-wapper>div{
        height: 100%;
    }
    .export-progress-box{
        position:absolute;
        top:35px;
        /*right:5px;*/
        display: flex;
        transition: all .2s;
        font-size: 13px;
    }
    .export-progress-wapper{
        width:50px;
        height:55px;
        border:2px solid #fff;
        border-bottom:0;
        color:#fff;
        background: #00bcd4;
        border-top:0;
        margin:0 30px;
    }
    .progress-name{
        width:46px;
        height:30px;
        text-align:center;
        /* line-height:30px; */
        line-height: 14px;
        word-break: break-all;
    }
    .progress-val{
        width:50px;
        height:50px;
        position:relative;
        left:-2px;
        text-align:center;
        line-height:50px;
        border-radius: 100%;
        border:2px solid #fff;
        font-weight:600;
        font-size:14px;
        background: #00bcd4;

    }
</style>
