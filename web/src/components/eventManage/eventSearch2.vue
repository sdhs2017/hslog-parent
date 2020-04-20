<template>
    <div class="content-bg">
        <div class="top-title">事件搜索</div>
        <div class="event-search-condition">
            <v-search-form :formItem="formConditionsArr" busName="eventSearch2"></v-search-form>
        </div>
        <div class="event-search-content">
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
        name: "eventSearch2",
        data(){
            return{
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
                page:1,
                size:15,
                c_page:1,
                allCounts:0,
                total:0,
                formConditionsArr:[
                    {
                        label:'时间范围',
                        type:'datetimerange',
                        itemType:'',
                        paramName:'time',
                        model:{
                            model:[]
                        },
                        val:''
                    },
                    {
                        label:'事件名称',
                        paramName:'event.action',
                        itemType:'',
                        model:{
                            model:''
                        },
                        type:'input'
                    },
                    {
                        label:'IP地址',
                        paramName:'fields.ip',
                        itemType:'',
                        model:{
                            model:''
                        },
                        type:'input'
                    },
                    {
                        label:'资产名称',
                        paramName:'host.hostname',
                        itemType:'',
                        model:{
                            model:''
                        },
                        type:'input'
                    }
                ],
                tableHead:[
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
                    // {
                    //     prop:'event.type',
                    //     label:'事件类型',
                    //     width:'120'
                    // },
                    {
                        prop:'fields.ip',
                        label:'IP地址',
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
                eventSearchCondition:{},
                saveCondition:{},
            }
        },
        created(){
            //定义七天时间范围
            let endTime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
            let startTime= new Date();
            startTime.setTime(startTime.getTime() - 3600 * 1000 * 24 * 7);
            startTime = dateFormat('yyyy-mm-dd HH:MM:SS',startTime);
            this.dateVal= [startTime,endTime];
            this.eventSearchCondition={
                'fields.ip':'',
                'host.hostname':'',
                'event.action':'',
                endtime: endTime,
                starttime: startTime
            }
            this.formConditionsArr[0].model.model= [startTime,endTime]
            //保存检索条件
            this.saveCondition = this.eventSearchCondition;
            //获取事件数据
            this.getEventsData(1,this.eventSearchCondition);
            //监听检索条件
            bus.$on('eventSearch2',(params)=>{
                this.getEventsData(1,params);
                this.c_page = 1;
                this.saveCondition = params;
            })
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
            /*获得事件列表数据*/
            getEventsData(page,params){
                let loading = layer.load(1);
                params.size = this.size;
                params.page = page;
                params.exists = 'event.action'
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/ecsCommon/getEventListByBlend.do',this.$qs.stringify({
                        hsData:JSON.stringify(params)
                    }))
                        .then((res)=>{
                            layer.close(loading);
                            this.tableData = res.data[0].list;
                            this.allCounts =  res.data[0].count;
                            if(this.allCounts >100000){
                                this.total = 100000;
                            }else{
                                this.total = this.allCounts;
                            }
                        })
                        .catch((err)=>{
                            layer.close(loading);
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
</style>
