<template>
    <div class="content-bg" v-loading="loading" element-loading-background="rgba(48, 62, 78, 0.5)">
        <div class="top-title">{{this.alarmName}} 执行详情</div>
        <div class="execute-list-wapper">
            <vBasetable :tableHead="tableHead" :tableData="tableData"></vBasetable>
        </div>
        <div class="alarm-table-page">
            <span>共检索到告警数量为 <b>{{allCounts}}</b> 个</span>
            <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="size" :total="allCounts"></el-pagination>
        </div>
        <el-dialog :title="`快照详情  ${dialogTitle}`" :visible.sync="snapshotDialog" width="850px">
            <div class="snapshot-wapper">
                <jsonView :data="this.tableHead" theme="one-dark" :line-height="20" :deep="5" class="jsonView"></jsonView>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import vBasetable from '../common/Basetable2';
    import jsonView from 'vue-json-views'
    export default {
        name: "alarmExecuteDetail",
        data(){
            return{
                alarmId:'',
                alarmName:'',
                loading:false,
                snapshotDialog:false,
                dialogTitle:'',
                allCounts:0,
                page:1,//页码
                c_page:1,//当前页码
                size:15,//每页的数量
                snapshotJson:'',
                tableHead:[
                    {
                        prop:'time',
                        label:'执行时间',
                        width:''
                    },
                    {
                        prop:'state',
                        label:'状态',
                        width:''
                    },
                    {
                        prop:'readed',
                        label:'已读/未读',
                        width:''
                    },
                    {
                        prop:'tools',
                        label:'操作',
                        width:'',
                        btns:[
                            {
                                icon:'el-icon-camera',
                                text:'查看快照',
                                clickFun:(row,index)=>{
                                    this.dialogTitle = row.time;
                                    this.snapshotDialog = true
                                    // this.detailAlarm(row.alert_id)
                                }
                            },
                        ]
                    }
                ],
                tableData:[
                    {time:'2020-10-26 10:00:00',state:'运行',readed:'未读'}
                ]
            }
        },
        created(){
        },
        methods:{
            /*获取执行结果*/
            getAlarmExecute(searchObj,page){
                this.loading = true;
                let objParam = searchObj;
                objParam.pageIndex = page;//当前页
                objParam.pageSize = this.size;//页的条数
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/',this.$qs.stringify(objParam))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success === 'true'){
                                this.allCounts = Number(obj.data.count);
                                this.tableData = obj.data.list;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            /*分页页码改变*/
            handleCurrentChange(page){
                //获取数据
                this.getAlarmExecute(this.conditionFrom,page);
                //改变标识
                //this.firstGetData = false;
            },
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                vm.$options.name = 'editAlarm'+ to.query.id;
                //修改data参数
                vm.alarmName = to.query.name;
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'alarmExecuteDetail'+to.query.id,
                    component:'alarmManage/alarmExecuteDetail.vue',
                    title:'执行详情'
                }
                sessionStorage.setItem('/alarmExecuteDetail'+to.query.id,JSON.stringify(obj))
                if(vm.alarmId === '' || vm.alarmId !== to.query.id){
                    vm.alarmId = to.query.id;
                   // vm.getAlarmExecute();
                }

            })

        },
        components:{
            vBasetable,
            jsonView
        }
    }
</script>

<style scoped>
    .btn-wapper{
        float: right;
        margin-right: 10px;
    }
    .execute-list-wapper{
        padding: 10px;
    }
    .alarm-table-page{
        border-top: 1px solid #303e4e;
        height: 40px;
        display: flex;
        justify-content: flex-end;
        align-items:center;
    }
    .alarm-table-page b{
        color: #e4956d;
    }
    .snapshot-wapper{
        height: 500px;
        overflow: auto;
    }
</style>
