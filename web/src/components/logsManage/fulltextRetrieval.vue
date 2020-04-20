<template>
    <div class="content-bg">
        <div class="top-title">全文检索
            <el-date-picker
                class="date-wapper"
                v-model="dateVal"
                type="datetimerange"
                range-separator="至"
                size="mini"
                start-placeholder="开始日期"
                value-format="yyyy-MM-dd HH:mm:ss"
                :default-time="['00:00:00', '23:59:59']"
                @change="timeChange"
                end-placeholder="结束日期"
                :picker-options="pickerOptions">
            </el-date-picker>
        </div>
        <div class="search-wapper">
            <el-form :inline="true" onsubmit="return false" class="demo-form-inline">
                <el-form-item>
                    <el-input placeholder="输入条件检索日志" class="input-searchword" v-model="words"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="searchLogs()"><i class="el-icon-search" ></i>查询</el-button>
                </el-form-item>
            </el-form>
        </div>
        <div class="content-wapper">
            <v-logscontent2 :searchConditions="searchConditions" :tableHead="tableHead" :searchUrl="searchUrl" :layerObj="layerObj"></v-logscontent2>
        </div>

    </div>
</template>

<script>
    import vLogscontent2 from '@/components/logsManage/logsContent2';
    import {dateFormat} from "../../../static/js/common";
    export default {
        name: "fulltextRetrieval",
        data(){
            return{
                dateVal:[],
                pickerOptions: {
                    shortcuts: [{
                        text: '最近一周',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '最近一个月',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '最近三个月',
                        onClick(picker) {
                            const end = new Date();
                            const start = new Date();
                            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                            picker.$emit('pick', [start, end]);
                        }
                    }, {
                        text: '全部',
                        onClick(picker) {
                            picker.$emit('pick', ['', '']);
                        }
                    }]
                },
                layerObj:{
                    detailData:{},//弹窗数据
                    layerState:false//弹窗状态
                },
                words:'',//检索条件
                searchConditions:{
                    words:''
                },
                searchUrl:'ecsWinlog/getLogListByContent.do',//数据地址
                tableHead:[],//表头列
                firstSearch:true//第一次查询标识（创建分页）
            }
        },
        created(){
            //七天数据
            //定义七天时间范围
            let endTime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
            let startTime= new Date();
            startTime.setTime(startTime.getTime() - 3600 * 1000 * 24 * 7);
            startTime = dateFormat('yyyy-mm-dd HH:MM:SS',startTime);
            this.dateVal= [startTime,endTime];
            this.searchConditions={
                words:'',
                endtime: endTime,
                starttime: startTime
            },
            //表头
            this.tableHead = [
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
                    prop:'fields.equipmentname',
                    label:'资产名称',
                    width:'125',
                    clickFun:(item)=>{
                        if(item.equipmentid ==='unknown'){
                            layer.msg("资产未知",{icon:5})
                        }else{
                            //跳转到资产列表页面
                            this.$router.push({name:'equipment2',params:{equipmentid: item.fields.equipmentid}})
                        }
                    }
                },
                {
                    prop:'fields.ip',
                    label:'IP',
                    width:'125'
                },
                {
                    prop:'message',
                    label:'日志内容',
                    width:''
                },
                {
                    prop:'tools',
                    label:'操作',
                    width:'50',
                    btns:[{
                        icon:'el-icon-tickets',
                        text:'查看详情',
                        btnType:'readDetails',
                        clickFun:(row,index)=>{
                            //console.log(this.layerObj)
                            this.layerObj.layerState = true;
                            this.layerObj.detailData = row;
                        }
                    }]
                }
            ]
            if(this.$route.params.words !== undefined){
                this.words=this.$route.params.words;
                this.searchConditions=this.$route.params;
            }
        },
        watch: {
            '$route' (to, from) { //监听路由是否变化
                if (this.$route.params.words) {
                    this.words=this.$route.params.words;
                    this.searchConditions=this.$route.params;
                }
            }
        },
        methods:{
            /*查询按钮点击事件*/
            searchLogs(){
                this.searchConditions = {
                    words : this.words,
                    starttime: this.dateVal[0],
                    endtime: this.dateVal[1]
                }
            },
            /*日期改变*/
            timeChange(){
                //查询日志
                this.searchLogs();
            },
            goToEquipment(){
                console.log('ddd')
            }
        },
        components:{
            vLogscontent2
        }
    }
</script>

<style scoped>
    .date-wapper{
        float: right;
        top: 10px;
        right: 10px;
    }
    .search-wapper{
        display: flex;
        justify-content: center;
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
</style>
