<template>
    <div class="content-bg">
        <div class="top-title">用户管理</div>
        <div class="user-manage-content">
            <el-row :gutter="10">
                <el-col :span="6">
                    <div class="department-wapper" @click="removeClass">
                        <div class="department-top">
                            <h5>机构列表</h5>
                            <div class="department-btns">
                                <el-button type="primary" size="mini" plain title="添加组织机构" @click="addBtn" v-if="this.$is_has('userManage_addDepartment')"><i class="el-icon-lx-add"></i></el-button>
                                <el-button type="success" size="mini" plain title="修改组织机构" @click="editBtn" v-if="this.$is_has('userManage_reviseDepartment')"><i class="el-icon-edit"></i></el-button>
                                <el-button type="danger" size="mini" plain title="删除组织机构" @click="removeTree" v-if="this.$is_has('userManage_deleteDepartment')"><i class="el-icon-close"></i></el-button>
                            </div>
                        </div>
                        <div class="department-content treeBox">
                            <el-tree
                                :props="props"
                                :load="loadNode"
                                :key="treeKey"
                                @node-click="nodeClick"
                                highlight-current
                                lazy>
                                <span class="custom-tree-node" slot-scope="{ node, data }">
                                    <span v-if="node.data.departmentName" class="tree-label">
                                        <span class="people-icon el-icon-lx-peoplefill"></span>{{ node.label }}
                                    </span>
                                    <span v-else class="tree-label">
                                        <span class="tree-icon el-icon-lx-homefill"></span>{{ node.label }}
                                    </span>
                                </span>
                            </el-tree>
                        </div>
                    </div>
                </el-col>
                <el-col :span="18">
                    <div class="user-wapper">
                        <div class="user-department-name">{{fatherName.split('/')[1]}}</div>
                        <div class="user-tools-wapper">
                            <div class="user-btn">
                                <el-button type="primary" size="mini" plain title="添加用户" @click="addUserBtn"><i class="el-icon-lx-add"></i>添加用户</el-button>
                                <el-button type="danger" size="mini" plain title="删除用户" @click="removeUser"><i class="el-icon-close"></i>删除用户</el-button>
                            </div>
                            <div class="user-search">
                                <v-search-form :formItem="userSearchFormItem" busName="searchUserCon"></v-search-form>
                            </div>
                        </div>
                        <div class="user-table-wapper">
                            <v-basetable :selection="true" :tableHead="userTableHeadData" :tableData="userTableData" :busName="busName"></v-basetable>
                        </div>
                        <div class="user-page-wapper">
                            共检索到用户<b>{{allUserCounts}}</b>人
                            <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="pageSize" :total="allUserCounts"></el-pagination>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>
        <!--添加与修改组织机构弹窗-->
        <el-dialog :title="treeBtnType === 'add' ? '添加组织机构':'修改组织机构'" :visible.sync="treeForm" width="440px">
            <el-form label-width="80px">
                <el-form-item label="父级目录:" v-if="treeBtnType === 'add'">
                    <el-input v-model="fatherName" placeholder="" disabled class="item"></el-input>
                </el-form-item>
                <el-form-item label="名称:" >
                    <el-input v-model="departmentParams.name" placeholder="名称"  class="item"></el-input>
                </el-form-item>
                <el-form-item label="描述:" >
                    <el-input type="textarea"  :autosize="{ minRows:8, maxRows: 10}" v-model="departmentParams.comment"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="treeBtnType === 'add'?addTree():editTree()">确 定</el-button>
                <el-button @click="treeForm = false">取 消</el-button>
            </div>
        </el-dialog>
        <!--添加与修改用户弹窗-->
        <el-dialog :title="userBtnType === 'add' ? '添加用户':'修改用户'" :visible.sync="userForm" width="500px" :close-on-click-modal="clickModal">
            <el-form label-width="100px">
                <el-form-item label="账号(电话):">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input v-model="userParams.phone" placeholder="11位有效数字" maxlength="11"  class="item"></el-input>
                </el-form-item>
                <el-form-item label="密码:" v-if="userBtnType === 'add'">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input v-model="userParams.password" type="password" placeholder="6-18位数字、字母、字符"  maxlength="18"  class="item"></el-input>
                </el-form-item>
                <ul class="pass_set" v-if="userParams.password !== '' && userParams.password !== undefined">
                    <li id="strength_L" style="background-color: rgb(237, 62, 13);">弱</li>
                    <li id="strength_M" :style="mode >=2 ? {background:'#ffaf56'}:{}">中</li>
                    <li id="strength_H" :style="mode > 2 ? {background:'#6ba001'}:{}">强</li>
                </ul>
                <el-form-item label="确认密码:" v-if="userBtnType === 'add'">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input type="password" v-model="password2" placeholder="确认密码"  class="item" maxlength="18"></el-input>
                </el-form-item>
                <el-form-item label="姓名:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input v-model="userParams.name" placeholder="姓名"  class="item"></el-input>
                </el-form-item>
                <el-form-item label="部门:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <v-select-tree :options="options" :value="userParams.departmentId"></v-select-tree>
                </el-form-item>
                <el-form-item label="角色:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-select v-model="roleVal" multiple placeholder="请选择" style="width: 100%;">
                        <el-option
                            v-for="item in roleArr"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="邮箱:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input v-model="userParams.Email" placeholder="邮箱"  class="item"></el-input>
                </el-form-item>
                <el-form-item label="状态:"  v-if="userBtnType !== 'add'">
                    <el-checkbox v-model="userCheckboxState" :disabled="disabled">限制登录</el-checkbox>
                </el-form-item>
                <el-form-item label="性别:">
                    <el-radio-group v-model="userParams.sex">
                        <el-radio :label="1">男</el-radio>
                        <el-radio :label="0">女</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="年龄:">
                    <el-input v-model="userParams.age" type="number" placeholder="年龄"  class="item"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="userBtnType === 'add'?addUser():reviseUser()">确 定</el-button>
                <el-button @click="userForm = false">取 消</el-button>
            </div>
        </el-dialog>
    </div>
    
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import vBasetable from '../common/Basetable'
    import vSelectTree from '../common/selectTree'
    import {checkStrong} from  '../../../static/js/common.js'
    import bus from '../common/bus';
    export default {
        name: "userManage",
        data() {
            return {
                busName:{ //监听名称
                    selectionName:'userSelect',
                    reviseUserName:'reviseUserName'
                },
                mode:0,//密码复杂度验证
                middleCheck:{
                    background: '#ffaf56'
                },
                hideCheck:{
                    background: '#6ba001'
                },
                treeKey:'',//机构树key值
                treeForm:false,//机构树弹窗
                userForm:false,//用户弹窗
                clickModal:false,
                props: {//tree参数
                    label: 'name',
                    // children: 'zones',
                    isLeaf: 'leaf'
                },
                fatherName:'',//机构数父级名称
                departmentParams:{//树表单参数
                    level: 1,//层级
                    subordinate: 0,//是否有下级
                    departmentNodeId: 0,//最外层父级节点ID
                    name: '',//部门名称
                    comment:''//部门描述
                },
                userParams:{//用户表单参数
                    name:'',
                    sex:1,
                    phone:'',
                    age:'',
                    password:'',
                    Email:'',
                    departmentId:0,
                    role:'',
                    state:1
                },
                roleVal:[],//角色
                userCheckboxState:true,//限制登录
                password2:'',
                selectedTreeNode:{},//选中的树节点
                selectedUserIds:'',//选中的表格中的用户
                reviseUserId:'',//修改用户的id
                treeBtnType:'add',//树 按钮类型
                userBtnType:'add',//用户 按钮类型
                userTableHeadData:[
                    {
                        prop:'phone',
                        label:'账号',
                        width:''
                    },
                    {
                        prop:'name',
                        label:'姓名',
                        width:''
                    },
                    {
                        prop:'sex',
                        label:'性别',
                        width:'',
                        formatData:(val)=>{return val == '1' ? '男' : '女'}
                    },
                    {
                        prop:'age',
                        label:'年龄',
                        width:''
                    },
                    {
                        prop:'departmentName',
                        label:'部门',
                        width:''
                    },
                    {
                        prop:'role',
                        label:'角色',
                        width:''
                    },
                    {
                        prop:'email',
                        label:'邮箱',
                        width:'150px'
                    },
                    {
                        prop:'state',
                        label:'状态',
                        width:'',
                        formatData:(val)=>{return val == '1' ? '正常' : '受限'}
                    },
                    {
                        prop:'tools',
                        label:'操作',
                        width:'50px',
                        btns:[
                            {
                                icon:'el-icon-edit',
                                text:'修改用户',
                                btnType: 'reviseUser',
                                clickFun :(rowData)=>{
                                    this.reviseUserBtn(rowData)
                                }
                            }
                        ]
                    }
                ],
                userTableData:[],//用户列表数据
                userSearchFormItem:[
                    {
                        label:'姓名',
                        paramName:'name',
                        itemType:'',
                        model:{
                            model:''
                        },
                        type:'input'
                    },
                    {
                        label:'账号',
                        paramName:'phone',
                        itemType:'',
                        model:{
                            model:''
                        },
                        type:'input'
                    },
                ],//检索用户条件项
                allUserCounts:0,//用户总数
                pageSize:10,//每页显示的条数
                c_page:1,//当前页码
                userSearchCondition:{
                    name:'',
                    phone:''
                },
                roleArr:[
                    /*{
                        value:1,
                        label:"管理员"
                    },
                    {
                        value:2,
                        label:"操作员"
                    },
                    {
                        value:3,
                        label:"审查员"
                    },
                    {
                        value:4,
                        label:"游客"
                    }
*/
                ],
                options:[]

            }
        },
        computed:{
            disabled(){
                //获取登录用户的角色
                let user = JSON.parse(localStorage.getItem('LoginUser'));
                if(user.role === 5){
                    return false;
                }else {
                    return true;
                }
            }
        },
        created(){
            /*监听搜索用户事件*/
            bus.$on('searchUserCon',params =>{
                //console.log(params)
                this.userSearchCondition = params;
                this.c_page=1;
                this.selectUser(1,params)
            });
            /*监听用户多选*/
            bus.$on(this.busName.selectionName,params =>{
                this.selectedUserIds = ''
                params.forEach(item=>{
                    this.selectedUserIds += item.id+','
                })
            })
            //加载用户部门
            this.getDepartmentData();
            //
            this.getRoleList();
        },
        mounted(){
        },
        watch:{
            'userForm'(){
                if(this.userForm){
                    //获取部门列表
                    this.getDepartmentData();
                    //监听部门列表参数
                    bus.$on('getValue',params=>{
                        this.userParams.departmentId = params;
                    })
                }else{
                    bus.$off('getValue');
                }
            },
            'userParams.password'(val){
                if(val){
                    this.mode =  checkStrong(val);
                }

            },
            'userParams.role'(val){
                if(val === 5){
                    this.roleArr.push({
                        value:5,
                        label:"master"
                    })
                }
            }
        },
        beforeDestroy(){
            //清除监听
            bus.$off('searchUserCon');
            bus.$off('getValue');
            bus.$off(this.busName.selectionName);
            bus.$off(this.busName.reviseUserName);
        },
        methods:{
            /*获取角色列表*/
            getRoleList(){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/role/selectAllRole.do','')
                        .then(res=>{
                            for(let i in res.data){
                                this.roleArr.push({
                                    value:res.data[i].id,
                                    label:res.data[i].role_name
                                })
                            }

                        })
                        .catch(err=>{

                        })
                })
            },
            /*获取部门列表*/
            getDepartmentData(){
                this.$nextTick(()=>{
                    this.$axios.get(this.$baseUrl+'/department/selectAllDepartment.do',{})
                        .then(res =>{
                            this.options = res.data;
                        })
                        .catch(err =>{

                        })

                })
            },
            /*获取树节点*/
            getTreeData(params,resolve){
                this.depData=[]
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/department/selectAll.do',this.$qs.stringify(params))
                        .then(res =>{
                            //判断数据是否为空
                            if(res.data !== ''){
                                //加载组织机构列表
                                res.data.department.forEach(item =>{
                                    this.depData.push(item)
                                })
                                if(res.data.user !== undefined){
                                    res.data.user[0].forEach(item =>{
                                        this.depData.push(item)
                                    })
                                }
                                resolve(this.depData)

                                //判断是否有用户数据
                                if(res.data.user !== undefined){
                                    this.changeUserShowData(res.data.user[0]);
                                    this.allUserCounts = res.data.user[0].length;
                                }
                            }
                        })
                        .catch(err =>{

                        })
                })
            },
            /*加载节点数据*/
            loadNode(node, resolve) {
                if (node.level === 0) {
                    this.getTreeData({level:1},resolve);
                } else {
                    //this.getTreeChild(node.data.id, resolve)
                    if(JSON.stringify(node.data.id).length < 10){
                        this.getTreeData({id:node.data.id},resolve);
                    }else{
                        resolve([])
                    }

                }
            },
            /*树节点点击事件*/
            nodeClick(data){
                if(data.departmentName){//点击的是用户
                    //查询用户
                    this.selectTreeUser(data.id)
                }else{//点击的是部门
                    // console.log(data)
                    this.fatherName = '.. / '+data.name;
                    //数据填充
                    this.selectedTreeNode = {
                        level: data.level,//层级
                        subordinate: data.subordinate, //是否有child
                        departmentNodeId: data.departmentNodeId,//最外层id
                        superiorId: data.superiorId,
                        id: data.id,//父id
                        name: data.name,//名称
                        comment: data.comment//描述
                    }
                }
            },
            /*添加按钮*/
            addBtn(){
                //修改树弹窗确定按钮类型
                this.treeBtnType = 'add';
                //出现弹窗
                this.treeForm = true;
                this.departmentParams =  Object.assign({}, this.selectedTreeNode);
                //初始化参数
                this.departmentParams.name = '';
                this.departmentParams.comment = '';
            },
            /*添加组织机构*/
            addTree(){
                layer.load(1);
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/department/insert.do',this.$qs.stringify(this.departmentParams))
                        .then(res =>{
                            layer.closeAll('loading')
                            if(res.data.success ==='true'){
                                layer.msg('添加成功',{icon:1})
                                //关闭弹窗
                                this.treeForm = false;
                                //刷新树结构
                                this.treeKey = +new Date();

                            }else{
                                layer.msg(res.data.message,{icon:5})
                            }
                        })
                        .catch(err =>{
                            layer.closeAll('loading')
                        })

                })
            },
            /*修改树按钮*/
            editBtn(){
                //判断是否选中组织机构
                if(this.selectedTreeNode.id){
                    //修改树弹窗确定按钮类型
                    this.treeBtnType = 'edit';
                    //出现弹窗
                    this.treeForm = true;
                    this.departmentParams =  Object.assign({}, this.selectedTreeNode);
                }else{
                    layer.msg('未选中任何组织',{icon:5})
                }

            },
            /*修改组织机构*/
            editTree(){
                layer.load(1)
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/department/updateById.do',this.$qs.stringify(this.departmentParams))
                        .then(res =>{
                            layer.closeAll('loading')
                            if(res.data.success ==='true'){
                                layer.msg('修改成功',{icon:1})
                                //关闭弹窗
                                this.treeForm = false;
                                //刷新树结构
                                this.treeKey = +new Date();

                            }else{
                                layer.msg(res.data.message,{icon:5})
                            }
                        })
                        .catch(err =>{
                            layer.closeAll('loading')
                        })

                })
            },
            /*删除组织机构*/
            removeTree(){
                //判断是否选中组织机构
                if(this.selectedTreeNode.id){
                    layer.confirm('您确定删除么？', {
                        btn: ['确定','取消'] //按钮
                    }, (index)=>{
                        layer.load(1)
                        this.$nextTick(()=>{
                            this.$axios.post(this.$baseUrl+'/department/delete.do',this.$qs.stringify({id:this.selectedTreeNode.id}))
                                .then(res =>{
                                    layer.closeAll('loading')
                                    if(res.data){
                                        layer.msg('删除成功',{icon:1})
                                        //初始化选中的参数
                                        this.selectedTreeNode = {};
                                        //刷新树结构
                                        this.treeKey = +new Date();
                                    }else{
                                        layer.msg('删除失败，该目录存在子集',{icon:5})
                                    }
                                })
                                .catch(err =>{
                                    layer.closeAll('loading')
                                })

                        })
                    })

                }else{
                    layer.msg('未选中任何组织',{icon:5})
                }
            },
            /*清楚选中的树节点*/
            removeClass(ev){

                if($(ev.target)[0].className === 'department-wapper'){
                    //初始化选中节点
                    this.selectedTreeNode = {};
                    $('.el-tree--highlight-current').removeClass('el-tree--highlight-current')
                }

               // console.log(this.selectedTreeNode.id)
            },
            /*机构树查询用户*/
            selectTreeUser(id){
                layer.load(1)
                this.$nextTick(()=>{
                    this.$axios.get(this.$baseUrl+'/user/selectUser.do',{
                        params:{
                            id:id
                        }
                    })
                        .then(res =>{
                            layer.closeAll('loading')
                            this.changeUserShowData(res.data[0]);
                        })
                        .catch(err =>{
                            layer.closeAll('loading')
                        })

                })
            },
            /*搜索框查询用户*/
            selectUser(page,obj){
                this.fatherName = '';
                obj.pageIndex = page;
                obj.pageSize = this.pageSize;
                layer.load(1)
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/user/selectAlls.do',this.$qs.stringify(obj))
                        .then(res =>{
                            layer.closeAll('loading')
                            //this.userTableData = res.data[0];
                            this.allUserCounts = Number(res.data.count[0].count);
                            this.changeUserShowData(res.data.user[0])
                        })
                        .catch(err =>{
                            layer.closeAll('loading')
                        })

                })
            },
            /*用户数据显示改变*/
            changeUserShowData(data){
                this.userTableData = data;
            },
            /*添加用户按钮*/
            addUserBtn(){
                this.roleVal = [];
                this.userBtnType = 'add'
                //清空参数
                this.userParams={
                    name:'',
                    sex:1,
                    phone:'',
                    age:'',
                    password:'',
                    Email:'',
                    departmentId:0,
                    role:''
                }
                this.userForm=true;
            },
            /*添加用户*/
            addUser(){
                //拼接角色
                for(let i in this.roleVal){
                    this.userParams.role += this.roleVal[i]+',';
                }
                if(this.checkUserParams(this.userParams)){
                    layer.load(1)
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/user/inserts.do',this.$qs.stringify(this.userParams))
                            .then(res =>{
                                layer.closeAll('loading')
                                if(res.data.success === 'true'){
                                    layer.msg('添加成功',{icon: 1})
                                    //关闭弹窗
                                    this.userForm = false;
                                    //清空参数
                                    this.userParams={
                                        name:'',
                                        sex:1,
                                        phone:'',
                                        age:'',
                                        password:'',
                                        Email:'',
                                        departmentId:0,
                                        role:''
                                    }
                                    //重新加载机构树
                                    this.treeKey = +new Date();
                                    this.c_page=1;
                                    //加载用户列表
                                    this.selectUser(1,this.userSearchCondition)

                                }else{
                                    layer.msg(res.data.message,{icon:5})
                                }

                            })
                            .catch(err =>{
                                layer.closeAll('loading')
                                layer.msg('添加失败',{icon: 5});
                            })

                    })
                }

            },
            /*删除用户*/
            removeUser(){
                if (this.selectedUserIds === ''){
                    layer.msg('未选中任何用户',{icon: 5});
                } else{
                    layer.confirm('您确定删除么？', {
                        btn: ['确定','取消'] //按钮
                    }, (index)=>{
                        layer.load(1)
                        this.$nextTick(()=>{
                            this.$axios.post(this.$baseUrl+'/user/deletes.do',this.$qs.stringify({ids:this.selectedUserIds}))
                                .then(res =>{
                                    layer.closeAll('loading')
                                    if(res.data.success === 'true'){
                                        layer.msg('删除成功',{icon:1})
                                        //刷新树结构
                                        this.treeKey = +new Date();
                                        //加载用户列表
                                        this.selectUser(1,this.userSearchCondition)
                                    }else{
                                        layer.msg('删除失败',{icon:5})
                                    }
                                })
                                .catch(err =>{
                                    layer.closeAll('loading')
                                })

                        })
                    })
                }
            },
            /*修改用户按钮*/
            reviseUserBtn(rowData){
                this.userForm = true;
                this.userBtnType = 'revise';
                layer.load(1)
                //获取用户数据
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/user/selectById.do',this.$qs.stringify({id:rowData.id}))
                        .then(res =>{
                            layer.closeAll('loading')
                            this.userParams = {
                                name:res.data.name,
                                sex:res.data.sex,
                                phone:res.data.phone,
                                age:res.data.age,
                                Email:res.data.email,
                                departmentId:res.data.departmentId,
                                role:res.data.role,
                            }
                            if(this.roleVal !== ''){
                                this.roleVal = res.data.role.split(',');
                            }
                            if(res.data.state === 1){
                                this.userCheckboxState = false;
                            }else{
                                this.userCheckboxState = true;
                            }
                            this.reviseUserId = rowData.id;
                        })
                        .catch(err =>{
                            layer.msg('获取用户信息失败',{icon:5})
                            layer.closeAll('loading')
                        })

                })
            },
            /*修改用户*/
            reviseUser(id){
                this.userParams.role = '';
                //拼接角色
                for(let i in this.roleVal){
                    this.userParams.role += this.roleVal[i]+',';
                }
                this.userParams.id = this.reviseUserId;
                if(this.userCheckboxState === true){
                    this.userParams.state = 0;
                }else{
                    this.userParams.state = 1;
                }
                if(this.checkUserParams(this.userParams)){
                    layer.load(1)
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/user/inserts.do',this.$qs.stringify(this.userParams))
                            .then(res =>{
                                layer.closeAll('loading')
                                if(res.data.success === 'true'){
                                    layer.msg('修改成功',{icon: 1})
                                    //关闭弹窗
                                    this.userForm = false;
                                    //清空参数
                                    this.userParams={
                                        name:'',
                                        sex:1,
                                        phone:'',
                                        age:'',
                                        password:'',
                                        Email:'',
                                        departmentId:0,
                                        role:''
                                    }
                                    //重新加载机构树
                                    this.treeKey = +new Date();
                                    this.c_page=1;
                                    //加载用户列表
                                    this.selectUser(1,this.userSearchCondition)

                                }else{
                                    layer.msg(res.data.message,{icon:5})
                                }

                            })
                            .catch(err =>{
                                layer.closeAll('loading')
                                layer.msg('修改失败',{icon: 5});
                            })

                    })
                }

            },
            /*页码改变*/
            handleCurrentChange(page){
                this.selectUser(page,this.userSearchCondition)
            },
            /*检测用户参数合法性*/
            checkUserParams(params){
                if(this.userBtnType === 'add'){
                    if(params.password.length < 6 || params.password.length >18){
                        layer.msg('密码长度不正确（6-18）',{icon: 5});
                        return false;
                    }else if(params.password !== this.password2){
                        layer.msg('两次密码不正确',{icon: 5});
                        return false;
                    }
                }
                //邮箱验证 正则表达式
                let reg = /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/;
                if(params.phone === '' || params.phone.length !== 11 || !$.isNumeric(params.phone)){
                    layer.msg('电话（账号）不能为空或格式不正确',{icon: 5});
                    return false;
                }else if(params.departmentId === 0){
                    layer.msg('未选中任何部门',{icon: 5});
                    return false;
                }else if(params.name === ''){
                    layer.msg('姓名不能为空',{icon: 5});
                    return false;
                }else if(!reg.test(params.Email)){
                    layer.msg('邮箱格式不正确',{icon: 5});
                    return false;
                }else if(params.role === ''){
                    layer.msg('未选中角色',{icon: 5});
                    return false;
                }else{
                    return true;
                }
            }
        },
        components:{
            vBasetable,
            vSearchForm,
            vSelectTree

        }
    }
</script>

<style>
    .el-tree {
        position: relative;
        cursor: default;
        background: 0;
        color: #ffffff;
    }
    .el-tree-node__content:hover{
        background-color: #3a4a5d;
    }
    .el-tree--highlight-current .el-tree-node.is-current>.el-tree-node__content {
        background-color: #5a7494;
    }
    .el-tree-node:focus>.el-tree-node__content {
        background-color: #5a7494;
    }
  /*  .el-tree-node__content::before, .el-tree-node__content::after{
        content:'';
        left:-21px;
        position:absolute;
        right:auto
    }
    .el-tree-node__content::before {
        border-left:1px solid #999;
        bottom:50px;
        height:100%;
        top:0;
        width:1px
    }
    .el-tree-node__content::after {
        border-top:1px solid #999;
        height:20px;
        top:25px;
        width:25px
    }*/
    .user-manage-content{
        height: 100%;
        padding: 10px 20px;

    }
    .department-wapper,.user-wapper{
        height: 100%;
        min-height:70vh;
        background: #303e4e;
    }
    .department-top{
        height: 48px;
        line-height: 48px;
        padding: 0 10px;
        border-bottom: 1px solid #5a7494;
    }
    .department-top h5{
        font-size: 16px;
        float: left;
    }
    .department-btns{
        float: right;
    }
    .department-btns button{
        background: 0!important;
        margin: 0;
        font-size: 13px;
        font-weight: 600;
        padding: 5px 10px;
    }
    .treeBox {
        /* border-bottom:1px solid #e7eaec; */
        min-height:20px;
        padding:19px;
        margin-bottom:20px;
        /*background-color:#fbfbfb;*/
        /*border:1px solid #999;*/
        -webkit-border-radius:4px;
        -moz-border-radius:4px;
        border-radius:4px;
        -webkit-box-shadow:inset 0 1px 1px rgba(0, 0, 0, 0.05);
        -moz-box-shadow:inset 0 1px 1px rgba(0, 0, 0, 0.05);
        box-shadow:inset 0 1px 1px rgba(0, 0, 0, 0.05)
    }
    .tree-label{
        font-size: 13px;
        /*color: #3095e0;*/
    }
    .tree-icon{
        color: #3095e0;
        margin-right: 5px;
    }
    .people-icon{
        color: #fff;
        margin-right: 5px;
    }
    .user-wapper{
        padding: 0 10px;
        height: calc(100%- 100px);
    }
    .user-department-name{
        height: 50px;
        line-height: 50px;
        width: 100%;
        border-bottom: 1px solid #5a7494;
        font-size: 28px;
        margin-bottom: 10px;
        padding-left: 10px;
    }
    .user-tools-wapper{
        min-height: 40px;
        line-height: 40px;
        margin-bottom: 10px;
        padding-left: 15px;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
    .user-table-wapper{
        border: 1px solid #5a7494;
    }
    .user-page-wapper{
        display: flex;
        justify-content: flex-end;
        align-items: center;
    }
    .user-page-wapper b{
        margin: 0 5px;
        color: #409EFF;
    }
    .pass_set{
        overflow: hidden;
        margin-bottom: 20px;
        padding-left: 100px;
    }
    .pass_set li {
        float: left;
        text-align: center;
        width: 50px;
        border-right: 2px solid #fff;
        background: #667e9a;
        color: #fff;
        list-style-type: none;
    }
</style>
