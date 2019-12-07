<template>
    <div class="content-bg">
        <el-row>
            <el-button type="warning"  class="admin-btn" v-if="is_Has('user','add')" @click="adGetData()">管理员加载数据</el-button>
            <el-button type="success" class="vis-btn" v-if="is_Has('user','123')" @click="viGetData()">游客加载数据</el-button>
        </el-row>
        <el-row class="btn-wapper">
            <el-button type="warning" plain class="h-btn" data-attr="table:admin1">管理员按钮</el-button>
            <el-button type="warning" plain class="h-btn" data-attr="table:admin2">管理员按钮</el-button>
            <el-button type="warning" plain class="h-btn" data-attr="table:admin3">管理员按钮</el-button>
            <el-button type="warning" plain class="h-btn" data-attr="table:admin4">管理员按钮</el-button>
            <el-button type="success" plain class="h-btn" data-attr="table:visitor1">游客按钮</el-button>
            <el-button type="success" plain class="h-btn" data-attr="table:visitor2">游客按钮</el-button>
        </el-row>
        <div class="table-wapper">
            <tb :tableData="tableData"  :tableHeader="tableHeader"></tb>
        </div>

    </div>
    
</template>

<script>
    import tb from '../common/tb'
	export default {
		name: "test",
		data() {
			return {
                tableHeader:[ // 表头数据
                    { prop: 'name', label: '姓名'},
                    { prop: 'sex', label: '性别',formatData:(val)=>{return val === '0'? '男' : '女'}},
                    { prop: 'age', label: '年龄'},
                    { prop: 'btn', label: '操作', fixed: 'right', minWidth: '140px',
                        btn: [
                           /* { name: '查看',attr:'table:visitor3',clickFun: (row)=>{this.haClick(row)}},
                            { name: '编辑',attr:'table:admin5', clickFun: 'editClick()' }*/
                        ]
                    }
                ],
                tableData:[],
                permissionArr:[]
            }
		},
        methods:{
		    is_Has(html,type){
		        let obj = {
		            user:{
		                btns: ['delete','add']
                    }
                }
                if(obj[html].btns.includes(type)){
                    console.log('ssss')
                    return true;
                }else{
                    return false
                }

            },

            haClick(row){
                console.log(row.dhm)
            },
            editClick(){
                console.log("eee")
            },
            adGetData(){
                //更改表头
                this.tableHeader[this.tableHeader.length - 1].btn.push(
                    { name: '编辑',attr:'table:admin5', clickFun: 'editClick()' }
                )
                console.log(this.tableHeader)
                //加载数据
                this.tableData = [
                    {'name':'张三','sex':'0','age':'11'},
                    {'name':'李四','sex':'1','age':'22'},
                    {'name':'王五','sex':'1','age':'33'},
                    {'name':'赵六','sex':'1','age':'44'},
                    {'name':'孙七','sex':'0','age':'55'}
                ]
                this.permissionArr = ['table:admin1','table:admin2','table:admin3','table:admin4','table:admin5']
                //控制显示
                //console.log($('button').length)
                for(let i in this.permissionArr){
                   // console.log($('button[data-attr = "'+this.permissionArr[i]+'"]'))
                    $('button[data-attr = "'+this.permissionArr[i]+'"]').css("display","inline-block");
                }
            },
            viGetData(){
                //更改表头
                this.tableHeader[this.tableHeader.length - 1].btn.push(
                    {name: '查看',attr:'table:visitor3',clickFun: (row)=>{this.haClick(row)}}
                )
                this.tableData = [
                    {'name':'张三','sex':'0','age':'11'},
                    {'name':'李四','sex':'1','age':'22'},
                    {'name':'王五','sex':'1','age':'33'},
                    {'name':'赵六','sex':'1','age':'44'},
                    {'name':'孙七','sex':'0','age':'55'}
                ]
                this.permissionArr = ['table:visitor1','table:visitor2','table:visitor3']
                //控制显示
                for(let i in this.permissionArr){
                    $('button[data-attr = "'+this.permissionArr[i]+'"]').css("display","inline-block");
                }
            }
        },
        components:{
		    tb
        }
	}
</script>

<style scoped>
    .btn-wapper{
        padding: 20px;
    }
    .table-wapper{
        background: 0;
    }
    button{
        /*display: none;*/
    }
    .admin-btn,.vis-btn{
        display: inline-block;
    }
</style>
