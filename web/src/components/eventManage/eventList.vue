<template>
    <div class="content-bg">
        <div class="top-title">事件列表</div>
        <div class="btn-wapper">
            <el-button type="primary" size="mini" plain >添加</el-button>
        </div>
        <div class="event-list-content">
            <v-basetable :tableHead="tableHead" :tableData="tableData"></v-basetable>
        </div>
    </div>
    
</template>

<script>
    import vBasetable from '../common/Basetable';
	export default {
		name: "eventList",
		data() {
			return {
			    tableHead:[
                    {
                        prop:'name',
                        label:'事件名称'
                    },
                    {
                        prop:'userName',
                        label:'创建人'
                    },
                    {
                        prop:'actionName',
                        label:'动作名称',
                        formatData:(val)=>{return val.substring(0,val.length - 1);}
                    },
                    {
                        prop:'message',
                        label:'描述'
                    },
                    {
                        prop:'actionState',
                        label:'是否启用',
                        formatData:(val)=>{return val == '1' ? '是' : '否'}
                    },
                    {
                        prop:'tools',
                        label:'操作',
                        width:'60',
                        btns:[
                            {
                                icon:'el-icon-edit',
                                text:'修改',
                                clickFun:(row,index)=>{

                                }
                            },
                            {
                                icon:'el-icon-circle-close',
                                text:'删除',
                                btnType: 'removeItem',
                                clickFun:(row,index)=>{
                                    //this.$refs.logContent.removeParams([row])
                                }
                            }
                        ]
                    }

                ],
                tableData:[]
            }
		},
        created(){
		    //获取事件列表数据
            this.getEventListData();
        },
        methods:{
		    /*获取数据*/
            getEventListData(){
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/event/selectAll.do','')
                        .then(res=>{
                            layer.closeAll('loading');
                            this.tableData = res.data;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

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
    .btn-wapper{
        margin: 10px;
    }
    .event-list-content{
        padding: 10px;
    }
</style>
