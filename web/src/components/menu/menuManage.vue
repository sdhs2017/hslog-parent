<template>
    <div class="content-bg">
        <div class="top-title">菜单管理</div>
        <div class="user-manage-content">
            <el-row :gutter="10">
                <el-col :span="6">
                    <div class="department-wapper" @click="removeClass">
                        <div class="department-top">
                            <h5>系统列表</h5>
                            <div class="department-btns">
                                <el-button type="primary" size="mini" plain title="添加系统" @click="addBtn"><i class="el-icon-plus"></i></el-button>
                                <el-button type="success" size="mini" plain title="修改系统" @click="editBtn"><i class="el-icon-edit"></i></el-button>
                                <el-button type="danger" size="mini" plain title="删除系统" @click="removeTree"><i class="el-icon-close"></i></el-button>
                            </div>
                        </div>
                        <div class="department-content treeBox">
                            <el-tree
                                :props="props"
                                :data="treeData"
                                :key="treeKey"
                                @node-click="nodeClick"
                                highlight-current
                                >

                            </el-tree>
                        </div>
                    </div>
                </el-col>
                <el-col :span="18">
                    <div class="user-wapper">
                        <div class="user-department-name">{{selectedTreeNode.sys_name}}</div>
                        <div class="user-tools-wapper">
                            <div class="user-btn">
                                <el-button type="primary"  size="mini" plain title="添加菜单" @click="addMenuBtn"><i class="el-icon-plus"></i>添加菜单</el-button>
                                <!--<el-button type="danger" size="mini" plain title="删除菜单" @click="removeUser"><i class="el-icon-close"></i>删除菜单</el-button>-->
                            </div>
                            <!--<div class="user-search">
                                <v-search-form :formItem="userSearchFormItem" busName="searchUserCon"></v-search-form>
                            </div>-->
                        </div>
                        <div class="user-table-wapper">
                            <v-basetable  :tableHead="menuTableHeadData" :tableData="menuTableData" :height="500"></v-basetable>
                        </div>
                        <!--<div class="user-page-wapper">
                            共检索到用户<b>{{allUserCounts}}</b>人
                            <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="pageSize" :total="allUserCounts"></el-pagination>
                        </div>-->
                    </div>
                </el-col>
            </el-row>
        </div>
        <!--添加与修改组织机构弹窗-->
        <el-dialog :title="treeBtnType === 'add' ? '添加系统':'修改系统'" :visible.sync="treeForm" width="440px">
            <el-form label-width="80px">
                <el-form-item label="名称:" >
                    <el-input v-model="systemParams.sys_name" placeholder="名称"  class="item"></el-input>
                </el-form-item>
                <el-form-item label="描述:" >
                    <el-input type="textarea"  :autosize="{ minRows:8, maxRows: 10}" v-model="systemParams.note"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="treeBtnType === 'add'?addTree():editTree()">确 定</el-button>
                <el-button @click="treeForm = false">取 消</el-button>
            </div>
        </el-dialog>
        <!--添加与修改用户弹窗-->
        <el-dialog :title="userBtnType === 'add' ? '添加菜单':'修改菜单'" :visible.sync="userForm" width="550px" :close-on-click-modal="clickModal">
            <el-tabs type="border-card" value="first">
                <el-tab-pane label="基本信息" name="first" class="pane-con">
                    <el-form label-width="100px">
                        <el-form-item label="菜单名称:">
                            <el-input v-model="menuParam.menuName"  class="item"></el-input>
                        </el-form-item>
                        <el-form-item label="父级菜单Id:">
                            <el-input v-model="menuParam.superiorId"  class="item"></el-input>
                        </el-form-item>
                        <el-form-item label="序号:">
                            <el-input v-model="menuParam.childId"  class="item"></el-input>
                        </el-form-item>
                        <el-form-item label="URL:">
                            <el-input v-model="menuParam.url"  class="item"></el-input>
                        </el-form-item>
                        <el-form-item label="icon:">
                            <el-input v-model="menuParam.icon"  class="item"></el-input>
                        </el-form-item>
                        <el-form-item label="状态:">
                            <el-radio-group v-model="menuParam.state">
                                <el-radio :label="1">启用</el-radio>
                                <el-radio :label="2">停用</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item label="所属系统:">
                            <!--<el-input v-model="menuParam.fk_system_id"  class="item"></el-input>-->
                            <el-select v-model="menuParam.fk_system_id" placeholder="请选择">
                                <el-option
                                    v-for="item in sysOptions"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
                <el-tab-pane label="按钮管理" name="second" class="pane-con">
                    <div style="display:flex;height: 40px;align-items: center;">
                        <div style="display:flex;align-items: center;overflow: hidden;width: 0;transition: all 0.2s linear;" :style="{width:menuFormState ? '393px' : '0'}">
                            <span class="menu-span" style="width: 45px;">名称：</span><el-input style="width:159px;" v-model="menuForm.name" placeholder=""></el-input>
                            <span class="menu-span" style="width: 36px;">id：</span><el-input style="width:165px;" v-model="menuForm.id" placeholder=""></el-input>
                        </div>
                        <el-button type="primary" class="menu-btn" plain size="mini" @click="menuBtn">{{menuBtnName}}</el-button>
                        <el-button type="warning" class="menu-btn" plain size="mini" v-if="menuFormState" @click="closeMenuBtn">取消</el-button>
                    </div>
                    <div style="border-top: 1px solid #5a7494;margin-top: 5px;">
                        <v-basetable :tableHead="menuBtnHead" :tableData="menuBtnData" ></v-basetable>
                    </div>
                </el-tab-pane>
            </el-tabs>

            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="userBtnType === 'add'?addMenu():reviseUser()">确 定</el-button>
                <el-button @click="userForm = false">取 消</el-button>
            </div>
        </el-dialog>
    </div>

</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import vBasetable from '../common/Basetable'
    import vSelectTree from '../common/selectTree'
    import {checkStrong,is_has} from  '../../../static/js/common.js'
    import bus from '../common/bus';
    export default {
        name: "userManage",
        data() {
            return {
                //树节点数据
                treeData:[
                    {
                        id:'0',
                        sys_name:'全部',
                        children:[]
                    }
                ],
                //树节点配置
                props: {//tree参数
                    label: 'sys_name',
                    children: 'children',
                    isLeaf: 'leaf'
                },
                //树节点参数
                systemParams:{
                    sys_name:'',
                    note:''
                },
                //选中的树节点
                selectedTreeNode:{
                    id: '',
                    note: '',
                    sys_name: ''
                },
                busName:{ //监听名称
                    selectionName:'userSelect',
                    reviseUserName:'reviseUserName'
                },
                //菜单按钮修改按钮索引
                resiveIndex:'',
                treeKey:'',//机构树key值
                treeForm:false,//机构树弹窗
                userForm:false,//用户弹窗
                clickModal:false,
                //菜单按钮表单参数
                menuForm:{
                    name:'',
                    id:''
                },
                //菜单按钮表单是否显示
                menuFormState:false,
                //菜单基本信息参数
                menuParam:{
                    menuName:'',
                    superiorId:'',
                    childId:'',
                    url:'',
                    icon:'',
                    state:'',
                    fk_system_id:'',
                    btn:''
                },
                //系统下拉框数据
                sysOptions:[],
                //菜单按钮表头
                menuBtnHead:[
                    {
                        prop:'name',
                        label:'名称',
                        width:''
                    },
                    {
                        prop:'id',
                        label:'id',
                        width:''
                    },
                    {
                        prop:'tools',
                        label:'操作',
                        width:'60px',
                        btns:[
                            {
                                icon:'el-icon-edit',
                                text:'修改',
                                btnType: 'reviseUser',
                                clickFun :(rowData,index)=>{
                                    if(this.menuFormState === true){
                                        layer.msg('此前操作未完成',{icon:5});
                                    }else{
                                        this.menuFormState = true;
                                        this.menuBtnName = '修改';
                                        this.menuForm = Object.assign({}, rowData);
                                        this.resiveIndex = index;
                                    }
                                }
                            },
                            {
                                icon:'el-icon-close',
                                text:'删除',
                                btnType: 'remove',
                                clickFun :(rowData,index)=>{
                                    this.menuBtnData.splice(index,1)
                                }
                            }
                        ]
                    }
                ],
                //菜单按钮表格数据
                menuBtnData:[
                    /*{
                        name:'添加用户',
                        id:'add'
                    },{
                        name:'修改用户',
                        id:'resive'
                    }*/
                ],
                menuBtnName:'添加',
                sysId:'',//系统id

                userParams:{//用户表单参数
                    name:'',
                    sex:1,
                    phone:'',
                    age:'',
                    password:'',
                    Email:'',
                    departmentId:0,
                    role:1,
                    state:1
                },
                userCheckboxState:true,//限制登录
                password2:'',

                selectedUserIds:'',//选中的表格中的用户
                reviseMenuId:'',//修改用户的id
                treeBtnType:'add',//树 按钮类型
                userBtnType:'add',//用户 按钮类型
                menuTableHeadData:[
                    {
                        prop:'menuName',
                        label:'菜单名称',
                        width:''
                    },
                    {
                        prop:'superiorId',
                        label:'父级菜单ID',
                        width:''
                    },
                    {
                        prop:'childId',
                        label:'序号',
                        width:'',
                        //formatData:(val)=>{return val == '1' ? '男' : '女'}
                    },
                    {
                        prop:'url',
                        label:'url',
                        width:''
                    },
                    {
                        prop:'icon',
                        label:'icon',
                        width:''
                    },
                    {
                        prop:'state',
                        label:'状态',
                        width:'150px',
                        formatData:(val)=>{return val == '1' ? '启用' : '停用'}
                    },
                    {
                        prop:'systemName',
                        label:'所属系统',
                        width:''
                    },
                    {
                        prop:'tools',
                        label:'操作',
                        width:'60px',
                        btns:[
                            {
                                icon:'el-icon-edit',
                                text:'修改菜单',
                                btnType: 'reviseMenu',
                                clickFun :(rowData)=>{
                                    this.reviseMenuBtn(rowData)
                                }
                            },{
                                icon:'el-icon-close',
                                text:'删除菜单',
                                btnType: 'reviseMenu',
                                clickFun :(rowData)=>{
                                    this.removeMenu(rowData.id)
                                }
                            }
                        ]
                    }
                ],
                //菜单列表数据
                menuTableData:[],
                /*userSearchFormItem:[
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
                    {
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

                ],*/
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
            /*/!*监听搜索用户事件*!/
            bus.$on('searchUserCon',params =>{
                //console.log(params)
                this.userSearchCondition = params;
                this.c_page=1;
                this.selectUser(1,params)
            });
            /!*监听用户多选*!/
            bus.$on(this.busName.selectionName,params =>{
                this.selectedUserIds = ''
                params.forEach(item=>{
                    this.selectedUserIds += item.id+','
                })
            })*/
            //加载用户部门
            //this.getDepartmentData();
            this.getTreeData();
        },
        mounted(){
        },
        watch:{
            /*'userForm'(){
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
            }*/
        },
        beforeDestroy(){
            //清除监听
            bus.$off('searchUserCon');
            bus.$off('getValue');
            bus.$off(this.busName.selectionName);
            bus.$off(this.busName.reviseUserName);
        },
        methods:{
/*            /!*获取部门列表*!/
            getDepartmentData(){
                this.$nextTick(()=>{
                    this.$axios.get(this.$baseUrl+'/department/selectAllDepartment.do',{})
                        .then(res =>{
                            this.options = res.data;
                        })
                        .catch(err =>{

                        })

                })
            },*/
            /*获取树节点*/
            getTreeData(){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/system/selectAllSystem.do','')
                        .then(res =>{
                            this.sysOptions = [];
                            this.treeData[0].children = res.data;
                            for(let i in res.data){
                                this.sysOptions.push({
                                    value:res.data[i].id,
                                    label:res.data[i].sys_name
                                })
                            }
                        })
                        .catch(err =>{

                        })
                })
            },
            /*树节点点击事件*/
            nodeClick(data){
                if(data.id != 0){
                    this.sysId = data.id;
                    this.selectedTreeNode = {
                        id: data.id,
                        note: data.note,
                        sys_name: data.sys_name
                    }
                }
                this.getMenuBySysId();
            },
            /*通过系统id获取菜单*/
            getMenuBySysId(){
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/menu/selectMenuBySysID.do',this.$qs.stringify({systemID:this.sysId}))
                        .then(res=>{
                            layer.closeAll('loading');
                            this.menuTableData = res.data[0];
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*添加按钮*/
            addBtn(){
                //修改树弹窗确定按钮类型
                this.treeBtnType = 'add';
                //出现弹窗
                this.treeForm = true;
                this.systemParams = {};
               /* this.departmentParams =  Object.assign({}, this.selectedTreeNode);
                //初始化参数
                this.departmentParams.name = '';
                this.departmentParams.comment = '';*/
            },
            /*添加组织机构*/
            addTree(){
                layer.load(1);
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/system/upsert.do',this.$qs.stringify(this.systemParams))
                        .then(res =>{
                            layer.closeAll('loading')
                            if(res.data.success ==='true'){
                                layer.msg('添加成功',{icon:1})
                                //关闭弹窗
                                this.treeForm = false;
                                //刷新树结构
                                this.getTreeData()

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
                    this.systemParams =  Object.assign({}, this.selectedTreeNode);
                }else{
                    layer.msg('未选中任何系统',{icon:5})
                }

            },
            /*修改组织机构*/
            editTree(){
                layer.load(1)
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/system/upsert.do',this.$qs.stringify(this.systemParams))
                        .then(res =>{
                            layer.closeAll('loading')
                            if(res.data.success ==='true'){
                                layer.msg('修改成功',{icon:1})
                                //关闭弹窗
                                this.treeForm = false;
                                //初始化选中的参数
                                this.selectedTreeNode = {};
                                //刷新树结构
                                // this.treeKey = +new Date();
                                this.getTreeData()
                            }else{
                                layer.msg(res.data.message,{icon:5})
                            }
                        })
                        .catch(err =>{
                            layer.closeAll('loading');
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
                            this.$axios.post(this.$baseUrl+'/system/delete.do',this.$qs.stringify({id:this.selectedTreeNode.id}))
                                .then(res =>{
                                    layer.closeAll('loading')
                                    if(res.data.success ==='true'){
                                        layer.msg('删除成功',{icon:1})
                                        //初始化选中的参数
                                        this.selectedTreeNode = {};
                                        //刷新树结构
                                        this.getTreeData()
                                    }else{
                                        layer.msg(res.data.message,{icon:5})
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
                            layer.closeAll()
                            this.changeUserShowData(res.data[0]);
                        })
                        .catch(err =>{
                            layer.closeAll()
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
                            layer.closeAll()
                            //this.userTableData = res.data[0];
                            this.allUserCounts = Number(res.data.count[0].count);
                            this.changeUserShowData(res.data.user[0])
                        })
                        .catch(err =>{
                            layer.closeAll()
                        })

                })
            },
            /*用户数据显示改变*/
            changeUserShowData(data){
                this.userTableData = data;
            },
            /*添加菜单按钮*/
            addMenuBtn(){
                this.userBtnType = 'add'
                //清空参数
                this.menuParam={
                    btn: '',
                    childId: '',
                    fk_system_id: '',
                    icon:'',
                    menuName:'',
                    state:'',
                    superiorId:'',
                    url: ''
                }
                this.menuBtnData = [];
                this.userForm=true;
            },
            /*添加菜单*/
            addMenu(){
                if(this.checkParams(this.menuParam)){
                    this.menuParam.btn = JSON.stringify(this.menuBtnData)
                    //封装按钮
                    layer.load(1);
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/menu/upsert.do',this.$qs.stringify(this.menuParam))
                            .then(res =>{
                                layer.closeAll('loading');
                                if(res.data.success === 'true'){
                                    layer.msg('添加成功',{icon: 1})
                                    //关闭弹窗
                                    this.userForm = false;
                                    //重新加载机构树
                                    //加载菜单列表
                                    this.getMenuBySysId()

                                }else{
                                    layer.msg(res.data.message,{icon:5})
                                }

                            })
                            .catch(err =>{
                                layer.closeAll();
                                layer.msg('添加失败',{icon: 5});
                            })

                    })
                }

            },
            /*删除用户*/
            removeMenu(id){
               /* if (this.selectedUserIds === ''){
                    layer.msg('未选中任何用户',{icon: 5});
                } else{

                }*/
                layer.confirm('您确定删除么？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.load(1)
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/menu/delete.do',this.$qs.stringify({id:id}))
                            .then(res =>{
                                layer.closeAll()
                                if(res.data.success === 'true'){
                                    layer.msg('删除成功',{icon:1})
                                    //刷新树结构
                                    //加载菜单列表
                                    this.getMenuBySysId()
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
            /*修改菜单按钮*/
            reviseMenuBtn(rowData){
                //清空参数
                this.menuParam={
                    btn: '',
                    childId: '',
                    fk_system_id: '',
                    icon:'',
                    menuName:'',
                    state:'',
                    superiorId:'',
                    url: ''
                }
                this.menuBtnData = [];
                this.userForm = true;
                this.userBtnType = 'revise';
                layer.load(1);
                //获取菜单数据
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/menu/getEntity.do',this.$qs.stringify({id:rowData.id}))
                        .then(res =>{
                            layer.closeAll('loading')
                            this.menuParam = {
                                btn:'',
                                childId:res.data.childId,
                                fk_system_id:res.data.fk_system_id,
                                icon:res.data.icon,
                                menuName:res.data.menuName,
                                state:JSON.parse(res.data.state),
                                superiorId:res.data.superiorId,
                                url:res.data.url
                            }
                            this.menuBtnData = [];
                            let btnArr = JSON.parse(res.data.btn)
                            for (let i in btnArr){
                                this.menuBtnData.push({
                                    id : btnArr[i].buttonID,
                                    name:btnArr[i].buttonName
                                })
                            }
                            this.reviseMenuId = rowData.id;
                        })
                        .catch(err =>{
                            layer.msg('获取菜单信息失败',{icon:5})
                            layer.closeAll('loading')
                        })

                })
            },
            /*修改菜单*/
            reviseUser(id){
                this.menuParam.btn = JSON.stringify(this.menuBtnData);
                this.menuParam.id = this.reviseMenuId;
                if(this.checkParams(this.menuParam)){
                    layer.load(1)
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/menu/upsert.do',this.$qs.stringify(this.menuParam))
                            .then(res =>{
                                layer.closeAll('loading');
                                if(res.data.success === 'true'){
                                    layer.msg('修改成功',{icon: 1})
                                    //关闭弹窗
                                    this.userForm = false;
                                    //清空参数
                                    this.menuParam={
                                        btn: '',
                                        childId: '',
                                        fk_system_id: '',
                                        icon:'',
                                        menuName:'',
                                        state:'',
                                        superiorId:'',
                                        url: '',
                                    }
                                    //加载菜单列表
                                    this.getMenuBySysId();

                                }else{
                                    layer.msg(res.data.message,{icon:5})
                                }

                            })
                            .catch(err =>{
                                layer.closeAll('loading');
                                layer.msg('修改失败',{icon: 5});
                            })

                    })
                }

            },
            /*页码改变*/
            handleCurrentChange(page){
                this.selectUser(page,this.userSearchCondition)
            },
            /*添加、修改菜单按钮方法*/
            menuBtn(){
                if(this.menuBtnName === '添加'){
                    this.menuFormState = true;
                    this.menuBtnName = '确定';
                }else if(this.menuBtnName === '确定'){
                    //判断数据合法性
                    if(this.menuForm.name !== '' && this.menuForm.id !== ''){
                        this.menuBtnData.push(this.menuForm);
                        console.log(this.menuBtnData)
                        this.menuForm={
                            name:'',
                            id:''
                        }
                        this.menuFormState = false;
                        this.menuBtnName = '添加';
                    }

                }else if(this.menuBtnName === '修改'){
                    //判断数据合法性
                    if(this.menuForm.name !== '' && this.menuForm.id !== ''){
                        this.menuBtnData.splice(this.resiveIndex,1,this.menuForm);
                        console.log(this.menuBtnData)
                        this.menuForm={
                            name:'',
                            id:''
                        }
                        this.menuFormState = false;
                        this.menuBtnName = '添加';
                    }
                }
            },
            /*取消添加、修改菜单按钮方法*/
            closeMenuBtn(){
                this.menuBtnName = '添加';
                this.menuFormState = false;
                this.menuForm={
                    name:'',
                    id:''
                }
            },
            /*检验参数合法性*/
            checkParams(params){
                /* menuName:'',
                    superiorId:'',
                    childId:'',
                    url:'',
                    icon:'',
                    state:'',
                    fk_system_id:'',
                    btn:''*/

                if(params.menuName === ''){
                    layer.msg('菜单名称不能为空',{icon: 5});
                    return false;
                }else if(params.childId === '' ){
                    layer.msg('序号不能为空',{icon: 5});
                    return false;
                }else if(params.fk_system_id === ''){
                    layer.msg('所属系统不能为空',{icon: 5});
                    return false;
                }else if(params.superiorId === '' && params.icon === ''){
                    layer.msg('一级菜单icon不能为空',{icon: 5});
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
    .pane-con{
        height: 360px;
        padding: 0 10px;
        overflow-y: auto;
    }
    .menu-span{
        display: inline-block;
        width: 43px;
        color: #fff;
        text-align: end;
    }
    .menu-btn{
        width: 32px;
        height: 32px;
        margin-left: 2px;
        text-align: center;
        padding: 0;
    }
</style>
