<template>
    <div  class="content-bg" v-loading="loading" element-loading-background="rgba(48, 62, 78, 0.5)">
        <div class="top-title">资产组
            <div class="btn-wapper">
                <el-button type="primary" size="mini" plain @click="goToAddAssetGroup">添加</el-button>
                <el-button type="success" size="mini" plain  @click="refresh">刷新</el-button>
            </div>
        </div>
        <div class="equipemnt-tools-form">
            <v-search-form :formItem="formConditionsArr" :busName="busName"></v-search-form>
        </div>
        <div class="group-wapper" >
            <div class="asset-item" v-for="(item,i) in assetGroupList" :key="i">
                <div class="group-top">
                    <div class="group-name" :title="item.asset_group_name">{{item.asset_group_name}}</div>
                    <div class="group-btn">
                        <i class="el-icon-edit" @click="goToEditAssetGroup(item)"></i>
                        <i class="el-icon-s-data"></i>
                        <i class="el-icon-close" @click="removeAssetGroup(item.asset_group_id)"></i>
                    </div>
                </div>
                <div class="eq-tit"><span>所在资产</span><span>（{{item.asset_group_relations.length}}个）</span></div>
                <div class="eq-list">
                    <div title="点击查看资产信息" class="eq-item" v-for="(eq,ei) in item.asset_group_relations" :key="ei" @click="goToEq(eq.asset_id)">{{eq.asset_name}}</div>
                </div>
                <div class="create-time react"><b><i class="el-icon-time"></i> :</b> {{item.create_time}}</div>
                <div class="group-location react"><b><i class="el-icon-location-outline"></i> :</b> {{item.asset_group_area}}</div>
                <div class="group-user react"><b><i class="el-icon-user"></i> :</b> {{item.asset_group_person}}</div>
                <div class="group-desc" :title="item.asset_group_note"><b><i class="el-icon-notebook-2"></i> :</b> {{item.asset_group_note}}</div>
                <div class="circle-wapper"></div>
            </div>
        </div>
        <div class="equipment-table-page">
            <span>共检索到资产组数量为 <b>{{allCounts}}</b> 个</span>
            <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="size" :total="allCounts"></el-pagination>
        </div>
    </div>
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import bus from '../common/bus';
    import {jumpHtml} from "../../../static/js/common";
    export default {
        name: "assetGroup",
        data(){
            return{
                loading:false,
                busName:'assetGroupFrom',
                formConditionsArr:[
                    {
                        label:'资产组名称',
                        paramName:'asset_group_name',
                        model:{
                            model:''
                        },
                        itemType:'',
                        type:'input'
                    },
                    {
                        label:'区域',
                        paramName:'asset_group_area',
                        model:{
                            model:''
                        },
                        itemType:'',
                        type:'input'
                    },
                    {
                        label:'负责人',
                        paramName:'asset_group_person',
                        model:{
                            model:''
                        },
                        itemType:'',
                        type:'input'
                    },
                ],
                assetGroupList:[],
                conditionFrom:{
                    asset_group_area: "",
                    asset_group_name: "",
                    asset_group_person: "",
                },
                page:1,//页码
                c_page:1,//当前页码
                size:15,//每页的数量
                allCounts:0,//总数
            }
        },
        created(){
            this.getAssetGroup(this.conditionFrom,1)
            bus.$on(this.busName,(params)=>{
                this.conditionFrom = params;
                this.getAssetGroup(this.conditionFrom,1)
                this.c_page = 1;
            })
        },
        beforeDestroy(){
            //销毁
            bus.$off(this.busName)
        },
        methods:{
            //跳转添加页面
            goToAddAssetGroup(){
                this.$router.push({path:'addAssetGroup'});
            },
            /*跳转修改页面*/
            goToEditAssetGroup(item){
                jumpHtml('editAssetGroup'+item.asset_group_id,'assetGroup/editAssetGroup.vue',{ name:item.asset_group_name,id:item.asset_group_id },'资产组修改')
            },
            /*刷新*/
            refresh(){
                this.c_page = 1;
                this.getAssetGroup(this.conditionFrom,1);
            },
            /*获取资产组*/
            getAssetGroup(searchObj,page){
                this.loading = true;
                let obj = searchObj;
                obj.pageIndex = page;//当前页
                obj.pageSize = this.size;//页的条数
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/assetGroup/getAssetGroupInfoByCondition.do',this.$qs.stringify(obj))
                        .then(res=>{
                            this.loading = false;
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success === 'true'){
                                this.allCounts = Number(obj.data.count);
                                this.assetGroupList = obj.data.list;
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
            removeAssetGroup(id){
                //询问框
                layer.confirm('您确定删除么？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.close(index);
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/assetGroup/delete.do',this.$qs.stringify({asset_group_id:id}))
                            .then((res)=>{
                                if(res.data.success == "true"){
                                    layer.msg("删除成功",{icon:1});
                                    this.c_page = 1;
                                    this.getAssetGroup(this.conditionFrom,1)
                                }else{
                                    layer.msg(res.data.message,{icon:5});
                                }

                            })
                            .catch((err)=>{
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
                this.getAssetGroup(this.conditionFrom,page);
            },
            /*跳转单个资产*/
            goToEq(id){
                this.$router.push({name:'equipment2',params:{equipmentid: id}})
            }
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
        padding:20px 10px;
        overflow: hidden;
        width: 100%;
    }
    .asset-item{
        width: 230px;
        height: 323px;
        border: 1px solid #4884c4;
        /*background-image: linear-gradient(to right, #2f455c 0%, #386c9a 100%);*/
        background: #2f455c;
        position: relative;
        float: left;
        margin: 10px;
    }
    .asset-item>div{
        font-size: 12px;
        margin:5px 10px;
        color: #5bc0de;
    }
    .asset-item b{
        color: #8dc5fb
    }

    .asset-item .group-top{
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
    .asset-item .group-name{
        width: 170px;
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
        word-break: break-all;
    }
    .asset-item .group-btn{
        display: flex;
        font-size: 15px;
        width: 50px;
        margin: 0;
        justify-content: space-around;
        align-items: center;
        color: #fff;
    }
    .asset-item .group-btn i{
        cursor: pointer;
    }
    .group-btn i:hover{
        color: #8dc5fb;
    }
    .asset-item .group-desc{
        font-size: 12px;
        margin:0 10px;
        height: 34px;
        text-overflow: -o-ellipsis-lastline;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        -webkit-box-orient: vertical;
    }
    .asset-item .eq-tit{
        display: flex;
        justify-content: space-between;
        margin-bottom: 5px;
        font-weight: 600;
        margin-top: 10px;
    }
    .asset-item .eq-list{
        height: 90px;
        overflow: auto;
        padding: 10px;
        border: 1px dotted #5bc0de;
        margin:0 10px;
        box-shadow: 0px 0px 7px #2e7cc7 inset;
    }
    .asset-item .eq-list div{
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
    .asset-item .create-time{
        border-bottom: 1px dotted #2e7cc7;
        padding: 3px 0;
    }
    .asset-item .group-location{
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
        word-break: break-all;
        border-bottom: 1px dotted #2e7cc7;
        padding: 3px 0;
    }
    .asset-item .group-user{
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
</style>
