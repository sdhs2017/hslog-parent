<template>
    <div class="content-bg">
        <div class="top-title">审计日志</div>
        <div class="audit-search-wapper">
            <v-search-form :formItem="formConditionsArr" :busName="busName.formBusName"></v-search-form>
        </div>
        <div class="audit-btn-wapper">
            <el-button type="primary" plain size="mini" title="备份数据" @click="backupLogs"><i class="el-icon-printer"></i>备份</el-button>
            <el-button type="success" plain size="mini" title="还原备份数据" @click="recoverLogs"><i class="el-icon-sort"></i>还原</el-button>
            <el-button type="info" plain size="mini" title="清空" @click="emptyLogs"><i class="el-icon-delete"></i>清空</el-button>
            <el-button type="danger" plain size="mini" title="删除" @click="removeLogs"><i class="el-icon-close"></i>删除</el-button>
            <el-button type="warning" plain size="mini" title="刷新" @click="repeatLogs"><i class="el-icon-refresh"></i>刷新</el-button>
        </div>
        <div class="audit-table-wapper">
            <v-basetable :selection="selection" :tableHead="tableHead" :tableData="tableData" :busName="busName"></v-basetable>
        </div>
        <div class="user-page-wapper">
            共检索到日志<b>{{allCounts}}</b>条
            <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="pageSize" :total="allCounts"></el-pagination>
        </div>
    </div>
    
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import vBasetable from '../common/Basetable'
    import {dateFormat} from "../../../static/js/common";
    import bus from '../common/bus';
    export default {
        name: "auditLog",
        data() {
            return {
                selection:true,
                busName:{
                    formBusName:'auditLog',
                    selectionName:'selectedAuditLogs'
                },
                formConditionsArr:[
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
                        label:'部门',
                        paramName:'departmentName',
                        model:{
                            model:''
                        },
                        itemType:'',
                        type:'input'
                    },
                    {
                        label:'用户',
                        paramName:'userName',
                        model:{
                            model:''
                        },
                        itemType:'',
                        type:'input'
                    },
                    {
                        label:'账号',
                        paramName:'account',
                        model:{
                            model:''
                        },
                        itemType:'',
                        type:'input'
                    },
                    {
                        label:'IP',
                        paramName:'ip',
                        model:{
                            model:''
                        },
                        itemType:'',
                        type:'input'
                    }
                ],
                tableHead:[
                    {
                        prop:'time',
                        label:'操作时间',
                        width:'150'
                    },
                    {
                        prop:'userName',
                        label:'用户',
                        width:'100'
                    },
                    {
                        prop:'account',
                        label:'账号',
                        width:'100'
                    },
                    {
                        prop:'departmentName',
                        label:'部门',
                        width:'80'
                    },
                    {
                        prop:'ip',
                        label:'IP',
                        width:'125'
                    },
                    {
                        prop:'describe',
                        label:'内容',
                        width:''
                    }
                ],
                tableData:[],
                searchConditions:{
                    userName:'',
                    departmentName:'',
                    startTime:'',
                    endTime:'',
                    ip:'',
                    account:''
                },
                page:1,
                pageSize:15,
                c_page:1,
                allCounts:0,
                selectedIds:''
            }
        },
        created(){
            //定义七天时间范围
            let endTime = dateFormat('yyyy-mm-dd HH:MM:SS',new Date());
            let startTime= new Date();
            startTime.setTime(startTime.getTime() - 3600 * 1000 * 24 * 7);
            startTime = dateFormat('yyyy-mm-dd HH:MM:SS',startTime);
            this.searchConditions = {
                endTime: endTime,
                userName:'',
                departmentName:'',
                ip:'',
                account:'',
                startTime: startTime
            }
            this.formConditionsArr[0].model.model=[startTime,endTime]
            //监听检索按钮
            bus.$on(this.busName.formBusName,params=>{
                let obj = {
                    userName:params.userName,
                    departmentName:params.departmentName,
                    startTime:params.starttime,
                    endTime:params.endtime,
                    ip:params.ip,
                    account:params.account
                }
                this.searchConditions = obj;
                this.c_page = 1;
                //获取数据
                this.getAuditLogData(1,this.searchConditions)
            })
            //监听选中的日志
            bus.$on(this.busName.selectionName,params=>{
                params.forEach(item =>{
                    this.selectedIds += item.id +',';
                })
            })
            //获取数据
            this.getAuditLogData(1,this.searchConditions);
        },
        //销毁组件之前
        beforeDestroy(){
            bus.$off(this.busName.formBusName)
            bus.$off(this.busName.selectionName)
        },
        methods:{
            /*获取日志信息*/
            getAuditLogData(page,obj){
                layer.load(1)
                obj.pageIndex = page;
                obj.pageSize= this.pageSize;
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/note/selectByPage.do',this.$qs.stringify(obj))
                        .then(res=>{
                            layer.closeAll()
                            this.tableData = res.data[0].note[0];
                            this.allCounts = Number(res.data[0].count.count);
                        })
                        .catch(err=>{
                            layer.closeAll()
                            layer.msg('获取信息失败',{icon: 5});
                        })
                })
            },
            /*页码改变*/
            handleCurrentChange(page){
                //获取数据
                this.getAuditLogData(page,this.searchConditions)
            },
            /*备份*/
            backupLogs(){
                //询问框
                layer.confirm('您确定备份数据么？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.close(index);
                    layer.load(1)
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/note/backup.do','')
                            .then(res=>{
                                if(res.data.success === "true"){
                                    layer.msg(res.data.message,{icon: 1});
                                }else if(res.data.success === "false"){//失败
                                    layer.msg(res.data.message,{icon: 5});
                                }
                            })
                            .catch(err=>{
                                layer.closeAll()
                                layer.msg('备份失败',{icon: 5});
                            })
                    })
                }, ()=>{
                    layer.close();
                });
            },
            /*还原*/
            recoverLogs(){
                //询问框
                layer.confirm('您确定还原数据么？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.close(index);
                    layer.load(1)
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/note/restore.do','')
                            .then(res=>{
                                if(res.data.success === "true"){
                                    layer.msg(res.data.message,{icon: 1});
                                }else if(res.data.success === "false"){//失败
                                    layer.msg(res.data.message,{icon: 5});
                                }
                            })
                            .catch(err=>{
                                layer.closeAll()
                                layer.msg('备份失败',{icon: 5});
                            })
                    })
                }, ()=>{
                    layer.close();
                });
            },
            /*删除*/
            removeLogs(){
                if(this.selectedIds !== ''){
                    //询问框
                    layer.confirm('您确定删除选中的日志么？', {
                        btn: ['确定','取消'] //按钮
                    }, (index)=>{
                        layer.close(index);
                        layer.load(1)
                        this.$nextTick(()=>{
                            this.$axios.post(this.$baseUrl+'/note/deletes.do',this.$qs.stringify({ids:this.selectedIds}))
                                .then(res=>{
                                    if(res.data.success === "true"){
                                        layer.msg(res.data.message,{icon: 1});
                                        //获取数据
                                        this.getAuditLogData(1,this.searchConditions)
                                    }else if(res.data.success === "false"){//失败
                                        layer.msg(res.data.message,{icon: 5});
                                    }
                                })
                                .catch(err=>{
                                    layer.closeAll();
                                    layer.msg('删除失败',{icon: 5});
                                })
                        })
                    }, ()=>{
                        layer.close();
                    });
                }else{
                    layer.msg('未选中任何日志',{icon: 5});
                }
            },
            /*清空*/
            emptyLogs(){
                //询问框
                layer.confirm('您确定清空数据么？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.close(index);
                    layer.load(1)
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/note/deleteAll.do','')
                            .then(res=>{
                                if(res.data.success === "true"){
                                    layer.msg(res.data.message,{icon: 1});
                                    //获取数据
                                    this.getAuditLogData(1,this.searchConditions)
                                }else if(res.data.success === "false"){//失败
                                    layer.msg(res.data.message,{icon: 5});
                                }
                            })
                            .catch(err=>{
                                layer.closeAll()
                                layer.msg('清空数据',{icon: 5});
                            })
                    })
                }, ()=>{
                    layer.close();
                });
            },
            /*刷新*/
            repeatLogs(){
                //条件初始化
                this.searchConditions = {
                    userName:'',
                    departmentName:'',
                    startTime:'',
                    endTime:'',
                    ip:'',
                    account:''
                }
                //获取数据
                this.getAuditLogData(1,this.searchConditions);
                this.c_page = 1;
            }
        },
        components:{
            vBasetable,
            vSearchForm
        }
    }
</script>

<style scoped>
    .audit-search-wapper{
        display: flex;
        justify-content: center;
    }
    .audit-btn-wapper{
        margin-top: 20px;
        margin-bottom: 10px;
        padding: 0 20px;
    }
    .audit-btn-wapper button{
        background: 0;
        margin: 0;
    }
    .audit-table-wapper{
        padding: 0 20px;
    }
    .user-page-wapper{
        display: flex;
        justify-content: flex-end;
        align-items: center;
        padding:20px;
    }
    .user-page-wapper b{
        color: #409eff;
        margin: 0 5px;
    }
</style>
