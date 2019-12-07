<template>
    <div class="content-bg">
        <div class="top-title">角色管理</div>
        <div class="role-btn">
            <el-button type="primary" size="mini" plain title="添加" @click="addRoleBtn"><i class="el-icon-lx-add"></i>添加</el-button>
        </div>
        <div class="role-wapper">
            <v-basetable  :tableHead="roleTableHead" :tableData="roleTableData" ></v-basetable>
        </div>

        <!--添加与修改角色-->
        <el-dialog :title="roleBtnType === 'add' ? '添加角色':'修改角色'" :visible.sync="roleState" width="440px">
            <el-form label-width="80px">
                <el-form-item label="名称:" >
                    <el-input v-model="roleParams.role_name" placeholder="名称"  class="item"></el-input>
                </el-form-item>
                <el-form-item label="描述:" >
                    <el-input type="textarea"  :autosize="{ minRows:8, maxRows: 10}" v-model="roleParams.note"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="roleBtnType === 'add'?addRole():editRole()">确 定</el-button>
                <el-button @click="roleState = false">取 消</el-button>
            </div>
        </el-dialog>

        <!--配置-->
        <el-dialog title="配置" :visible.sync="settingState" width="540px" height="600px">
            <el-steps :active="active" simple>
                <el-step title="添加菜单 1" icon="el-icon-edit"></el-step>
                <el-step title="添加按钮 2" icon="el-icon-edit"></el-step>
            </el-steps>
            <div class="seting-wapper" v-if="active == 0">
                <el-tree
                    ref="firstTree"
                    :data="firstTreeData"
                    default-expand-all
                    show-checkbox
                    node-key="id"
                    :props="firstProps">
                </el-tree>
            </div>
            <div class="seting-wapper" v-if="active == 1">
                <el-tree
                    ref="secondTree"
                    :data="secondTreeData"
                    default-expand-all
                    show-checkbox
                    node-key="id"
                    :props="firstProps">
                </el-tree>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button style="margin-top: 12px;" @click="prevSetting" v-if="active == 1">上一步</el-button>
                <el-button style="margin-top: 12px;" @click="nextSetting"  v-if="active == 0">下一步</el-button>
                <el-button type="primary" @click="pushSetting" v-if="active == 1">确 定</el-button>
                <el-button @click="settingState = false">取 消</el-button>
            </div>
        </el-dialog>
    </div>
    
</template>

<script>
    import vBasetable from '../common/Basetable'
    export default {
        name: "roleManage",
        data() {
            return {
                //角色弹窗状态
                roleState:false,
                settingState:false,
                //角色弹窗类型
                roleBtnType:'add',
                //角色弹窗表单参数
                roleParams:{
                    role_name:'',
                    note:''
                },
                //修改角色id
                resiveRoleId:'',
                //表头
                roleTableHead:[
                    {
                        prop:'role_name',
                        label:'角色名称',
                        width:''
                    },
                    {
                        prop:'note',
                        label:'描述',
                        width:''
                    },
                    {
                        prop:'tools',
                        label:'操作',
                        width:'100px',
                        btns:[
                            {
                                icon:'el-icon-edit',
                                text:'修改菜单',
                                btnType: 'reviseMenu',
                                clickFun :(rowData)=>{
                                    this.roleBtnType = 'resive';
                                    this.roleState = true;
                                    this.resiveRoleId = rowData.id;
                                    this.$nextTick(()=>{
                                        layer.load(1);
                                        this.$axios.post(this.$baseUrl+'/role/getEntity.do',this.$qs.stringify({id:this.resiveRoleId}))
                                            .then(res=>{
                                                layer.closeAll('loading');
                                                this.roleParams = {
                                                    role_name : res.data.role_name,
                                                    note:res.data.note
                                                }

                                            })
                                            .catch(err=>{
                                                layer.closeAll('loading');
                                                layer.msg('获取信息失败',{icon:5})
                                            })
                                    })
                                }
                            },{
                                icon:'el-icon-close',
                                text:'删除菜单',
                                btnType: 'reviseMenu',
                                clickFun :(rowData)=>{
                                    this.removeRole(rowData.id)
                                }
                            },{
                                icon:'el-icon-setting',
                                text:'配置',
                                btnType: 'reviseMenu',
                                clickFun :(rowData)=>{
                                    this.active = 0;
                                    this.firstTreeData = [];
                                    this.secondTreeData = [];
                                    this.settingState = true;
                                    this.resiveRoleId = rowData.id;
                                    this.$nextTick(()=>{
                                        layer.load(1);
                                        this.$axios.post(this.$baseUrl+'/menu/selectSystemMenu.do','')
                                            .then(res=>{
                                                layer.closeAll('loading');
                                                this.firstTreeData = res.data;
                                            })
                                            .catch(err=>{
                                                layer.closeAll('loading');
                                                layer.msg('获取信息失败',{icon:5})
                                            })
                                    })
                                }
                            }
                        ]
                    }
                ],
                //表格数据
                roleTableData:[
                   /* {
                        "role_name":'管理员',
                        "note":'管理用户权限'
                    },
                    {
                        "role_name":'操作员',
                        "note":'操作员权限'
                    },*/
                ],
                active:0,
                firstTreeData:[],
                firstProps:{
                    children: 'menus',
                    label: 'menuName'
                },
                secondTreeData:[]
            }
        },
        created(){
            this.getRoleData();
        },
        methods:{
            /*获取角色列表数据*/
            getRoleData(){
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/role/selectAllRole.do','')
                        .then(res=>{
                            layer.closeAll('loading');
                            this.roleTableData = res.data;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*添加按钮*/
            addRoleBtn(){
                this.roleBtnType = 'add';
                this.roleState = true;
                this.roleParams={
                    role_name:'',
                    note:''
                }
            },
            /*添加角色*/
            addRole(){
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/role/upsert.do',this.$qs.stringify(this.roleParams))
                        .then(res=>{
                            layer.closeAll('loading');
                            if(res.data.success ==='true'){
                                layer.msg('添加成功',{icon:1})
                                //关闭弹窗
                                this.roleState = false;
                                //刷新树结构
                                this.getRoleData();

                            }else{
                                layer.msg(res.data.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*修改角色*/
            editRole(){
                this.roleParams.id = this.resiveRoleId;
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/role/upsert.do',this.$qs.stringify(this.roleParams))
                        .then(res=>{
                            layer.closeAll('loading');
                            if(res.data.success ==='true'){
                                layer.msg('修改成功',{icon:1})
                                //关闭弹窗
                                this.roleState = false;
                                //刷新树结构
                                this.getRoleData();

                            }else{
                                layer.msg(res.data.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*删除角色*/
            removeRole(id){
                layer.confirm('您确定删除么？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.load(1)
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/role/delete.do',this.$qs.stringify({id:id}))
                            .then(res =>{
                                layer.closeAll()
                                if(res.data.success === 'true'){
                                    layer.msg('删除成功',{icon:1})
                                    //刷新树结构
                                    //加载菜单列表
                                    this.getRoleData()
                                }else{
                                    layer.msg('删除失败',{icon:5})
                                }
                            })
                            .catch(err =>{
                                layer.closeAll()
                            })

                    })
                })
            },
            /*下一步*/
            nextSetting(){
                //获取选中的节点
                let checkedArr = this.$refs.firstTree.getCheckedKeys();
                let halfCheckedArr = this.$refs.firstTree.getHalfCheckedKeys();
                console.log(checkedArr)
                console.log(halfCheckedArr)
                //判断合法性
                if(checkedArr.length > 0 || halfCheckedArr.length > 0){
                    let ids = '';
                    for (let i in checkedArr){
                        ids += checkedArr[i] +','
                    }
                    for (let i in halfCheckedArr){
                        ids += halfCheckedArr[i] +','
                    }

                    //发送请求 获取下一级数据
                    this.$nextTick(()=>{
                        layer.load(1);
                        this.$axios.post(this.$baseUrl+'/menu/selectSystemMenuByIDs.do',this.$qs.stringify({ids:ids}))
                            .then(res=>{
                                layer.closeAll('loading');
                                //填充数据
                                this.secondTreeData = res.data;
                                //跳到下一级
                                this.active = 1;
                            })
                            .catch(err=>{
                                layer.closeAll('loading');

                            })
                    })
                }
            },
            /*上一步*/
            prevSetting(){
                this.active = 0;
            },
            /*提交配置*/
            pushSetting(){
                let checkedArr = this.$refs.secondTree.getCheckedKeys();
                let halfCheckedArr = this.$refs.secondTree.getHalfCheckedKeys();
                console.log(checkedArr)
                console.log(halfCheckedArr)
                if(checkedArr.length > 0 || halfCheckedArr.length > 0){
                    let ids = '';
                    for (let i in checkedArr){
                        ids += checkedArr[i] +','
                    }
                    for (let i in halfCheckedArr){
                        ids += halfCheckedArr[i] +','
                    }
                    this.$nextTick(()=>{
                        layer.load(1);
                        this.$axios.post(this.$baseUrl+'/menu/upsertMenuButton.do',this.$qs.stringify({
                            roleID : this.resiveRoleId,
                            ids : ids
                        }))
                            .then(res=>{
                                this.settingState = false;
                                layer.closeAll('loading');
                                if(res.data.success === 'true'){
                                    layer.msg('配置成功',{icon:1});

                                }else{
                                    layer.msg(res.data.message,{icon:5})
                                }
                            })
                            .catch(err=>{
                                layer.closeAll('loading');

                            })
                    })
                }

            }

        },
        components:{
            vBasetable
        }
    }
</script>

<style scoped>
    .role-title{
        color: #e4956d;
    }
    .role-wapper{
        padding: 10px 20px;
    }
   .role-btn{
       padding-left: 20px;
   }
    .seting-wapper{
        height: 350px;
        background:#3a4a5d;
        margin-top: 1px;
        margin-left: 5px;
        overflow-y: scroll;
        padding: 20px;
    }
</style>
