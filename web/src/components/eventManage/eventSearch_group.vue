<template>
    <div class="content-bg">
        <div class="top-title">事件关联查询
            <div class="equipemnt-tools-btns">
                <el-button type="success" size="mini" plain  @click="refreshEvent">刷新事件组</el-button>
            </div>

        </div>
        <div class="event-search-condition" v-loading="loadingFrom"  element-loading-background="rgba(48, 62, 78, 0.5)">
<!--            <v-search-form :formItem="formConditionsArr" busName="eventSearch_group"></v-search-form>-->
            <div style="width: 384px;">
                <span class="input-lable">日期范围</span>
                <el-date-picker
                    style="border-radius: 0;"
                    v-model="timeArr"
                    type="datetimerange"
                    range-separator="至"
                    size="mini"
                    @change="timeChanage"
                    start-placeholder="开始日期"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    :default-time="['00:00:00', '23:59:59']"
                    end-placeholder="结束日期">
                </el-date-picker>
            </div>
            <div>
                <span class="input-lable">日志类型</span>
                <el-select size="mini" style="border-radius: 0" @change="logtypeChange" v-model="eventSearchCondition['agent.type']">
                    <!--<el-option value=" ">全部</el-option>
                    <el-option value="syslog">syslog</el-option>
                    <el-option value="winlogbeat">window(日志)</el-option>-->
                    <el-option
                        v-for="item in logTypeOpt"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                    </el-option>
                </el-select>
            </div>
            <div>
                <span class="input-lable">事件名称</span>
                <el-select size="mini" v-model="eventSearchCondition['event.action']" clearable filterable>
                    <el-option
                        v-for="item in eventList"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                    </el-option>
                </el-select>
            </div>
            <div>
                <span class="input-lable">事件组</span>
                <el-select size="mini" v-model="eventSearchCondition.event_group_id" clearable>
                    <el-option
                        v-for="item in eventGroup"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                    </el-option>
                </el-select>
            </div>
            <div>
                <span class="input-lable">目的地址</span>
                <el-input size="mini" v-model="eventSearchCondition['fields.ip']" placeholder="" style="border-radius: 0"></el-input>
            </div>
            <div>
                <span class="input-lable">源地址</span>
                <el-input size="mini" v-model="eventSearchCondition['source.ip']" placeholder="" style="border-radius: 0"></el-input>
            </div>
            <div>
                <span class="input-lable">事件类型</span>
                <el-input size="mini" v-model="eventSearchCondition['winlog.task']" placeholder="" style="border-radius: 0"></el-input>
            </div>
            <el-button size="mini" type="primary" class="searchHttpLogs" @click="searchBtn">检索</el-button>
        </div>
        <div class="event-search-content" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
            <v-basetable2 :tableHead="tableHead" :tableData="tableData"></v-basetable2>
            <div class="event-table-page">
                共检索到事件数为 <b>{{allCounts}}</b>，最大展示
                <el-select v-model="selectVal" placeholder="请选择" size="mini" style="width: 84px;" @change="seletChange" class="maxSelect">
                    <el-option
                        v-for="(item,index) in selectOption"
                        :key="index"
                        :label="item.label"
                        :value="item.value">
                    </el-option>
                </el-select> 条
                <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="size" :total="total"></el-pagination>
            </div>
        </div>
        <div v-if="this.layerObj.layerState">
            <vListdetails2 :baseConfig="this.baseConfig" :detailsData="this.layerObj.detailData"></vListdetails2>
        </div>
    </div>
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import vBasetable2 from '../common/Basetable2';
    import vListdetails2 from '../common/Listdetails2';
    import bus from '../common/bus';
    import {dateFormat} from "../../../static/js/common";
    export default {
        name: "eventSearch_group",
        data(){
            return{
                loading:false,
                loadingFrom:false,
                baseConfig:{ //弹窗基础配置
                    type:'2',
                    title:'事件详情',
                    areaWidth:'620px',//宽度
                    areaHeight:'520px'//高度
                },
                layerObj:{
                    detailData:{},//弹窗数据
                    layerState:false//弹窗状态
                },
                timeArr:[],//时间参数数组
                page:1,
                size:15,
                c_page:1,
                allCounts:0,
                total:0,
                eventGroup:[],
                eventList:[],
                logTypeOpt:[{
                    label:'全部',
                    value:''
                },{
                    label:'syslog',
                    value:'syslog'
                },{
                    label:'window(日志)',
                    value:'winlogbeat'
                }],
                tableHead:[
                    {
                        prop:'@timestamp',
                        label:'时间',
                        width:'150'
                    },
                    {
                        prop:'event.action',
                        label:'名称',
                        width:'150'
                    },
                    {
                        prop:'winlog.task',
                        label:'类型',
                        width:'150'
                    },
                    {
                        prop:'winlog.event_data.SubjectUserName',
                        label:'主体',
                        width:'120',
                        formatData:(val)=>{
                            if(val){
                                return val
                            }else{
                                return  ''
                            }
                        }
                    },
                    {
                        prop:'fields.ip',
                        label:'客体/目的地址',
                        width:'125'
                    },
                    {
                        prop:'source.ip',
                        label:'源地址',
                        width:'125'
                    },
                    {
                        prop:'agent.type',
                        label:'日志类型',
                        width:'100'
                    },
                    {
                        prop:'fields.equipmentname',
                        label:'资产名称',
                        width:'120'
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
                                    // //console.log(this.layerObj)
                                    let obj = row;
                                    obj.type = 'eventDetail';
                                    this.layerObj.layerState = true;
                                    this.layerObj.detailData = obj;
                                }
                            }
                        ]
                    }

                ],
                tableData:[],
                selectVal:'10万',
                selectOption:[ //下拉框数据
                    {
                        value:100000,
                        label:'10万'
                    },
                    {
                        value:200000,
                        label:'20万'
                    },
                    {
                        value:500000,
                        label:'50万'
                    },
                    {
                        value:1000000,
                        label:'100万'
                    },
                    {
                        value:10000000,
                        label:'1000万'
                    },
                    {
                        value:100000000,
                        label:'1亿'
                    },
                ],
                eventSearchCondition:{
                    'winlog.task':'',
                    'event.action':'',
                    'agent.type':'',
                    event_group_id:'',
                    'fields.ip':'',
                    'source.ip':'',
                    endtime: '',
                    starttime: ''
                },
                saveCondition:{},
            }
        },
        created(){
            this.getEventGroup('');
            this.getEventList();
            //定义七天时间范围
            let endTime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
            let startTime= new Date();
            startTime.setTime(startTime.getTime() - 3600 * 1000 * 24 * this.$store.state.beforeDay);
            startTime = dateFormat('yyyy-mm-dd HH:MM:SS',startTime);
            this.timeArr= [startTime,endTime];
            this.eventSearchCondition={
                'winlog.task':'',
                'event.action':'',
                'agent.type':'',
                'fields.ip':'',
                'source.ip':'',
                event_group_id:'',
                endtime: endTime,
                starttime: startTime
            }
            //保存检索条件
            this.saveCondition = this.eventSearchCondition;
            //获取事件数据
            this.getEventsData(1,this.eventSearchCondition);
        },
        beforeDestroy(){
            bus.$off('eventSearch_group')
        },
        watch:{
        /*检测弹窗状态*/
        'layerObj.layerState': {
            handler(newV, oldV) {
                if (newV){
                    bus.$on('closeLayer',(params)=>{
                        //更改弹窗状态
                        this.layerObj.layerState = false;
                        //及时关闭检测
                        bus.$off('closeLayer')
                    })
                }
            },
            deep: true,
            immediate: true
        }
        },
        methods:{
            /*刷新*/
            refreshEvent(){
                this.getEventGroup();
            },
            /*时间改变事件*/
            timeChanage(val){
                if (val !== null){
                    this.eventSearchCondition.starttime = val[0];
                    this.eventSearchCondition.endtime = val[1];
                }else{
                    this.eventSearchCondition.starttime = '';
                    this.eventSearchCondition.endtime = '';
                }
            },
            /*日志类型改变事件*/
            logtypeChange(val){
                this.getEventList(val)
                this.eventSearchCondition['event.action'] = ''
            },
            /*获取事件列表*/
            getEventList(type){
                this.loadingFrom = true;
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/eventGroup/getEventListByType4Combobox_equal.do',this.$qs.stringify({
                        log_type:type
                    }))
                        .then(res=>{
                            this.loadingFrom = false;
                            let obj = res.data;
                            if(obj.success === 'true'){
                                this.eventList = obj.data
                                //this.formConditionsArr[this.formConditionsArr.length - 2].options = this.eventList;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.loadingFrom = false;
                        })
                })
            },
            /*获取事件组*/
            getEventGroup(){
                this.loadingFrom = true;
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/eventGroup/getEventGroupList.do',this.$qs.stringify())
                        .then(res=>{
                            let obj = res.data;
                            this.loadingFrom = false;
                            if(obj.success === 'true'){
                                this.eventGroup = obj.data;
                                //this.formConditionsArr[this.formConditionsArr.length - 1].options = this.eventGroup;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }

                        })
                        .catch(err=>{
                            this.loadingFrom = false;
                        })
                })
            },
            /*检索按钮*/
            searchBtn(){
                if(this.eventSearchCondition.starttime === '' || this.eventSearchCondition.endtime === ''){
                    layer.msg('查询时间条件不允许为空，时间跨度过大可能导致相应慢',{icon:5})
                }else{
                    this.getEventsData(1,this.eventSearchCondition);
                    this.c_page = 1;
                    this.saveCondition = this.eventSearchCondition;
                }

            },
            /*获得事件列表数据*/
            getEventsData(page,params){
                this.loading = true;
                params.size = this.size;
                params.page = page;
                params.exists = 'event.action'
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/ecsCommon/getEventListByBlend.do',this.$qs.stringify({
                        hsData:JSON.stringify(params)
                    }))
                        .then((res)=>{
                            this.loading = false;
                            /*this.tableData = res.data[0].list;*/
                           for (let i in res.data[0].list) {
                                if(res.data[0].list[i].winlog.event_data){
                                }else{
                                    res.data[0].list[i].winlog.event_data = {}
                                    res.data[0].list[i].winlog.event_data['SubjectUserName'] =''
                                }
                            }
                            this.tableData = res.data[0].list
                            this.allCounts =  res.data[0].count;
                            if(this.allCounts >100000){
                                this.total = 100000;
                            }else{
                                this.total = this.allCounts;
                            }
                        })
                        .catch((err)=>{
                            this.loading = false;
                            console.log(err)
                        })
                })
            },
            /*页码改变*/
            handleCurrentChange(page){
                this.getEventsData(page,this.saveCondition);
                //判断
                if((page * this.size) > this.allCounts){
                    layer.tips('这是最后一页', '.el-pager .active', {
                        tips: [1, '#3595CC'],
                        time: 4000
                    });
                }else{
                    if($('.el-pager .active').next().length < 1){
                        layer.tips('可通过更改最大显示数量，查看后面的数据', '.maxSelect', {
                            tips: [1, '#3595CC'],
                            time: 4000
                        });
                    }
                }
            },
            /*最大展示条数改变*/
            seletChange(selVal){
                //判断所选的展示条数
                if(selVal < this.allCounts){
                    this.total = selVal;
                }else{
                    this.total = this.allCounts;
                }
                //所选值过大时，进行提示
                const html = '数据量过大时，细化查询条件，提高查询效率'
                if(selVal >= 10000000){
                    layer.tips('提示: '+html, '.maxSelect', {
                        tips: [1, '#3595CC'],
                        time: 4000
                    });
                }
            }
        },
        components:{
            vSearchForm,
            vBasetable2,
            vListdetails2
        }
    }
</script>

<style scoped>
    .equipment-tools{
        height: 30px;
        padding: 0 20px;
        float: right;
        margin-right: 10px;
    }
    .equipemnt-tools-btns{
        float: right;
        margin-right: 10px;
    }
    .event-search-condition{
        display: flex;
        justify-content: center;
        margin-bottom: 30px;
    }
    .event-search-content{
        padding: 0 10px;
        overflow: auto;
    }
    .event-table-page{
        display: flex;
        justify-content: flex-end;
        align-items: center;
        margin: 10px 0;
    }
    .event-table-page b{
        color: #1ab394;
        margin:0 10px;
    }
    .event-search-condition{
        padding: 0 10px;
    }
    .event-search-condition>div{
        font-size: 12px;
        display: flex;
    }
    .input-lable{
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
    .event-search-condition /deep/ .el-input__inner{
        border-radius: 0!important;
    }
    .searchHttpLogs{
        -webkit-border-radius: 0;
        -moz-border-radius: 0;
        border-radius: 0;
    }
</style>
