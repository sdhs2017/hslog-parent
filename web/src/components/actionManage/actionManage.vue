<template>
    <div class="content-bg">
        <div class="top-title">动作列表</div>
        <div class="action-table-wapper">
            <v-basetable :tableHead = tableHead :tableData = tableData></v-basetable>
        </div>
    </div>
    
</template>

<script>
    import vBasetable from '../common/Basetable'
    export default {
        name: "actionManage",
        data() {
            return {
                tableHead:[
                    {
                        prop:'name',
                        label:'动作名称',
                        width:'150'
                    },
                    {
                        prop:'userName',
                        label:'创建人',
                        width:'100'
                    },
                    {
                        prop:'type',
                        label:'日志类型',
                        width:'100'

                    },
                    {
                        prop:'feature',
                        label:'关键字',
                        width:''
                    },
                    {
                        prop:'state',
                        label:'是否启用',
                        width:'125'
                    },
                    {
                        prop:'tools',
                        label:'操作',
                        width:'50',
                        btns:[
                            {
                                icon:'el-icon-edit',
                                text:'查看详情',
                                btnType: ''
                            },
                            {
                                icon:'el-icon-circle-close',
                                text:'删除',
                                btnType: ''
                            }
                        ]
                    }
                ],
                tableData:[]
            }
        },
        created(){
            this.getActionData();
        },
        methods:{
            //获取数据
            getActionData(){
                layer.load(1);
                this.$nextTick(()=>{
                    this.$axios.post('/jz/action/selectAll.do','')
                        .then(res =>{
                            layer.closeAll();
                            res.data[0].forEach(item =>{
                                if(item.state === '0'){
                                    item.state = '否'
                                }else{
                                    item.state = '是'
                                }
                            })
                            this.tableData = res.data[0];
                        })
                        .catch(error => {
                            console.log(error)
                            layer.closeAll()
                        })
                })
            }
        },
        components:{
            vBasetable
        }
    }
</script>

<style scoped>
    .action-table-wapper{
        padding: 0 20px;
    }
</style>
