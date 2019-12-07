<template>
    <div class="content-bg">
        <div class="top-title">事件搜索</div>
        <div class="event-search-condition">
            <v-search-form :formItem="formConditionsArr" busName="eventSearch"></v-search-form>
        </div>
        <div class="event-search-content">
            <v-basetable :tableHead="tableHead" :tableData="tableData"></v-basetable>
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
    </div>
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import vBasetable from '../common/Basetable';
    import bus from '../common/bus';

    export default {
        name: "eventSearch",
        data(){
            return{
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
                        label:'事件级别',
                        paramName:'event_level',
                        type:'select',
                        itemType:'',
                        model:{
                            model:''
                        },
                        options:[
                            {
                                value:'',
                                label:'全部'
                            },
                            {
                                value:'0',
                                label:'Emergency'
                            },
                            {
                                value:'1',
                                label:'Alert'
                            },
                            {
                                value:'2',
                                label:'Critical'
                            },
                            {
                                value:'3',
                                label:'Error'
                            },
                            {
                                value:'4',
                                label:'Warn'
                            },
                            {
                                value:'5',
                                label:'Notice'
                            },
                            {
                                value:'6',
                                label:'Info'
                            },
                            {
                                value:'7',
                                label:'Debug'
                            }
                        ]
                    },
                    {
                        label:'事件类型',
                        paramName:'event_type',
                        itemType:'',
                        model:{
                            model:''
                        },
                        type:'input'
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
                        itemType:'',
                        model:{
                            model:''
                        },
                        type:'input'
                    }
                ],
                tableHead:[
                    {
                        prop:'logtime',
                        label:'时间',
                        width:'150'
                    },
                    {
                        prop:'event_des',
                        label:'事件名称',
                        width:'150'
                    },
                    {
                        prop:'operation_level',
                        label:'事件级别',
                        width:'100'
                    },
                    {
                        prop:'event_type',
                        label:'事件类型',
                        width:'120'
                    },
                    {
                        prop:'ip',
                        label:'IP地址',
                        width:'125'
                    },
                    {
                        prop:'type',
                        label:'日志类型',
                        width:'100'
                    },
                    {
                        prop:'equipmentname',
                        label:'资产名称',
                        width:'120'
                    },
                    {
                        prop:'operation_des',
                        label:'日志内容',
                        width:''
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
                    starttime:'',
                    endtime:'',
                    ip:'',
                    hostname:'',
                    event_type:'',
                    event_level:'',
                },
                saveCondition:{},
            }
        },
        created(){
            //保存检索条件
            this.saveCondition = this.eventSearchCondition;
            //获取事件数据
            this.getEventsData(1,this.eventSearchCondition);
            //监听检索条件
            bus.$on('eventSearch',(params)=>{
                this.getEventsData(1,params);
                this.c_page = 1;
                this.saveCondition = params;
            })
        },
        methods:{
            /*获得事件列表数据*/
            getEventsData(page,params){
                let loading = layer.load(1);
                params.size = this.size;
                params.page = page;
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/log/getEventListByBlend.do',this.$qs.stringify({
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
            vBasetable
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
