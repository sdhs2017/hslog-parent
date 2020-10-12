<template>
    <div  class="content-bg" v-loading="loading" element-loading-background="rgba(48, 62, 78, 0.5)">
        <div class="top-title">关联事件组
            <div class="btn-wapper">
                <el-button type="primary" size="mini" plain @click="goToAddEventGroup">添加</el-button>
                <el-button type="success" size="mini" plain  @click="refresh">刷新</el-button>
            </div>
        </div>
        <div class="equipemnt-tools-form">
            <v-search-form :formItem="formConditionsArr" :busName="busName"></v-search-form>
        </div>
        <div class="group-wapper" >
            <div class="event-item" v-for="(item,i) in eventGroupList" :key="i">
                <div class="group-top">
                    <div class="group-name" :title="item.event_group_name">{{item.event_group_name}}</div>
                    <div class="group-btn">
                        <i class="el-icon-edit" @click="goToEditEventGroup(item)"></i>
                        <i class="el-icon-close" @click="removeEventGroup(item.event_group_id)"></i>
                    </div>
                </div>
                <div class="eq-tit"><span>事件</span><span class="goToEqGroupSpan">({{item.event_group_events.length}})</span></div>
                <div class="eq-list">
                    <div class="eq-item" v-for="(eq,ei) in item.event_group_events" :key="ei" >{{eq.event_name_en}}</div>
                </div>
                <div class="group-desc" :title="item.event_group_note"><b><i class="el-icon-notebook-2"></i> :</b> {{item.event_group_note}}</div>
                <div class="circle-wapper"></div>
            </div>
        </div>
        <div class="equipment-table-page">
            <span>共检索到事件组数量为 <b>{{allCounts}}</b> 个</span>
            <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="size" :total="allCounts"></el-pagination>
        </div>
    </div>
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import bus from '../common/bus';
    import {jumpHtml} from "../../../static/js/common";
    export default {
        name: "eventGroup",
        data(){
            return{
                loading:false,
                busName:'eventGroupFrom',
                eventList:[],
                formConditionsArr:[],
                eventGroupList:[],
                conditionFrom:{
                    event_group_name: "",
                    event_id: "",
                },
                page:1,//页码
                c_page:1,//当前页码
                size:15,//每页的数量
                allCounts:0,//总数
            }
        },
        created(){
            this.getEventList();
            this.formConditionsArr=[
                {
                    label:'事件组名称',
                    paramName:'event_group_name',
                    model:{
                        model:''
                    },
                    itemType:'',
                    type:'input'
                },
                {
                    label:'事件名称',
                    paramName:'event_id',
                    type:'select',
                    itemType:'',
                    model:{
                        model:''
                    },
                    options:this.eventList
                },
            ]
            this.getEventGroup(this.conditionFrom,1)
            bus.$on(this.busName,(params)=>{
                this.conditionFrom = params;
                this.getEventGroup(this.conditionFrom,1)
                this.c_page = 1;
            })
        },
        beforeDestroy(){
            //销毁
            bus.$off(this.busName)
        },
        methods:{
            //跳转添加页面
            goToAddEventGroup(){
                this.$router.push({path:'addEventGroup'});
            },
            /*跳转修改页面*/
            goToEditEventGroup(item){
                jumpHtml('editEventGroup'+item.event_group_id,'eventGroup/editEventGroup.vue',{ name:item.event_group_name,id:item.event_group_id },'事件组修改')
            },
            /*刷新*/
            refresh(){
                this.c_page = 1;
                this.getEventGroup(this.conditionFrom,1);
            },
            /*获取事件*/
            getEventList(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/eventGroup/getEventListByType4Combobox.do',this.$qs.stringify())
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success === 'true'){
                               this.eventList = obj.data
                                this.formConditionsArr[this.formConditionsArr.length - 1].options = this.eventList;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.loading = false;
                        })
                })
            },
            /*获取资产组*/
            getEventGroup(searchObj,page){
                this.loading = true;
                let obj = searchObj;
                obj.pageIndex = page;//当前页
                obj.pageSize = this.size;//页的条数
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/eventGroup/getEventGroupInfoByCondition.do',this.$qs.stringify(obj))
                        .then(res=>{
                            this.loading = false;
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success === 'true'){
                                this.allCounts = Number(obj.data.count);
                                this.eventGroupList = obj.data.list;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            /*删除资产组*/
            removeEventGroup(id){
                //询问框
                layer.confirm('您确定删除么？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    this.loading = true
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/eventGroup/delete.do',this.$qs.stringify({event_group_id:id}))
                            .then((res)=>{
                                this.loading = false
                                if(res.data.success == "true"){
                                    layer.msg("删除成功",{icon:1});
                                    this.c_page = 1;
                                    this.getEventGroup(this.conditionFrom,1)
                                }else{
                                    layer.msg(res.data.message,{icon:5});
                                }

                            })
                            .catch((err)=>{
                                this.loading = false;
                                layer.msg("删除失败",{icon:5});
                            })
                    })
                }, function(){
                    layer.close();
                })
            },
            /*分页*/
            handleCurrentChange(page){
                //获取数据
                this.getEventGroup(this.conditionFrom,page);
            },
        },
        components:{
            vSearchForm,
        }

    }
</script>

<style scoped>
    .btn-wapper{
        float: right;
        margin-right: 10px;
    }
    .group-wapper{
        min-height: 600px;
        padding:40px 10px;
        overflow: hidden;
        width: 100%;
        display: flex;
        justify-content: center;
        flex-wrap: wrap;
    }
    .event-item{
        width: 230px;
        height: 260px;
        border: 1px solid #4884c4;
        /*background-image: linear-gradient(to right, #2f455c 0%, #386c9a 100%);*/
        background: #2f455c;
        position: relative;
        float: left;
        margin: 10px;
    }
    .event-item>div{
        font-size: 12px;
        margin:5px 10px;
        color: #5bc0de;
    }
    .event-item b{
        color: #8dc5fb
    }

    .event-item .group-top{
        padding: 0 10px;
        background-image: linear-gradient(to right, #2f455c 0%, #386c9a 100%);
        height: 50px;
        line-height: 50px;
        /*text-align: center;*/
        font-size: 19px;
        font-weight: 600;
        color: #e4956d;
        margin: 0;
        border-bottom: 1px solid #4884c4;
        display: flex;
        justify-content: space-between;
        padding-right:0;
    }
    .event-item .group-name{
        width: 170px;
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
        word-break: break-all;
    }
    .event-item .group-btn{
        display: flex;
        font-size: 15px;
        width: 50px;
        margin: 0;
        justify-content: space-around;
        align-items: center;
        color: #fff;
    }
    .event-item .group-btn i{
        cursor: pointer;
    }
    .group-btn i:hover{
        color: #8dc5fb;
    }
    .event-item .group-desc{
        font-size: 12px;
        margin:0 10px;
        margin-top: 5px;
        height: 34px;
        text-overflow: -o-ellipsis-lastline;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        -webkit-box-orient: vertical;
    }
    .event-item .eq-tit{
        display: flex;
        justify-content: space-between;
        margin-bottom: 5px;
        font-weight: 600;
        margin-top: 10px;
    }
    .event-item .eq-list{
        height: 90px;
        overflow: auto;
        padding: 10px;
        border: 1px dotted #5bc0de;
        margin:0 10px;
        box-shadow: 0px 0px 7px #2e7cc7 inset;
    }
    .event-item .eq-list div{
        padding: 3px 5px;
        margin: 5px;
        font-size: 12px;
        float: left;
        margin-left: 0;
        /*background: #2e7cc7;*/
        color: #e5f9ff;
        background: #4876a2;
        cursor: pointer;
    }
    .eq-list .eq-item:hover{
        color: #e4956d;
    }
    .event-item .create-time{
        border-bottom: 1px dotted #2e7cc7;
        padding: 3px 0;
    }
    .event-item .group-location{
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
        word-break: break-all;
        border-bottom: 1px dotted #2e7cc7;
        padding: 3px 0;
    }
    .event-item .group-user{
        padding: 3px 0;
        border-bottom: 1px dotted #2e7cc7;
    }
    .equipment-table-page{
        border-top: 1px solid #303e4e;
        height: 40px;
        display: flex;
        justify-content: flex-end;
        align-items:center;
    }
    .equipment-table-page b{
        color: #e4956d;
    }
    .react{
        overflow: hidden;
        position: relative;
    }
    .react:after{
        content: "";
        position: absolute;
        top: 0;
        left: 11px;
        width: 500%;
        height: 1000%;
        background: #5bc0de;
        /* z-index: 0; */
        opacity: 0.5;
        -webkit-transform-origin: 0% 0%;
        -ms-transform-origin: 0% 0%;
        transform-origin: 0% 0%;
        -webkit-transform: translateX(calc(20% - 25px)) translateY(10%) rotate(-45deg);
        -ms-transform: translateX(calc(20% - 25px)) translateY(10%) rotate(-45deg);
        transform: translateX(calc(20% - 25px)) translateY(10%) rotate(-45deg);
    }
    .circle-wapper{
        position: absolute;
        width: 11px;
        height: 11px;
        border-radius: 100%;
        background: #1a242f;
        bottom: 0px;
        right: -6px;
    }
    .goToEqGroupSpan{
        text-decoration: underline;
        color: #409eff;
    }
    .goToEqGroupSpan:hover{
        color: #e4956d;
        cursor: pointer;
    }
</style>
