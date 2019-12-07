<template>
    <div class="content-bg">
        <div class="top-title">角色管理</div>
        <div class="role-wapper">
            <el-tabs type="border-card">
                <el-tab-pane label="管理员">
                    <h4>功能描述:</h4>
                    <div class="role-content">
                        <div v-for="(item,index) in administratorsArr" v-if="item.children.length > 0">
                            <p class="role-title">{{item.title}}</p>
                            <div>
                                <span  class="role-item" v-for="(i,index) in item.children">{{i.describes}}</span>
                            </div>
                        </div>
                    </div>
                </el-tab-pane>
                <el-tab-pane label="操作员">
                    <h4>功能描述:</h4>
                    <div class="role-content">
                        <div v-for="(item,index) in operatorArr" v-if="item.children.length > 0">
                            <p class="role-title">{{item.title}}</p>
                            <div>
                                <span  class="role-item" v-for="(i,index) in item.children">{{i.describes}}</span>
                            </div>
                        </div>
                    </div>
                </el-tab-pane>
                <el-tab-pane label="审查员">
                    <h4>功能描述:</h4>
                    <div class="role-content">
                        <div v-for="(item,index) in examinerArr" v-if="item.children.length > 0">
                            <p class="role-title">{{item.title}}</p>
                            <div>
                                <span  class="role-item" v-for="(i,index) in item.children">{{i.describes}}</span>
                            </div>
                        </div>
                    </div>
                </el-tab-pane>
                <el-tab-pane label="游客">
                    <h4>功能描述:</h4>
                    <div class="role-content">
                        <div v-for="(item,index) in visitorArr" v-if="item.children.length > 0">
                            <p class="role-title">{{item.title}}</p>
                            <div>
                                <span  class="role-item" v-for="(i,index) in item.children">{{i.describes}}</span>
                            </div>
                        </div>
                    </div>
                </el-tab-pane>
            </el-tabs>
        </div>
    </div>

</template>

<script>
    export default {
        name: "roleExplain",
        data() {
            return {

                administratorsArr: {},
                operatorArr:{},
                examinerArr:{},
                visitorArr:{}
            }
        },
        created(){
            this.getRoleData();
        },
        methods:{
            /*获取数据*/
            getRoleData(){
                layer.load(1);
                this.$nextTick(()=>{
                    this.$axios.get(this.$baseUrl+'/function/selectAll.do',{})
                        .then(res =>{
                            layer.closeAll();
                            this.administratorsArr = this.roleItemClassify(res.data.administrators);
                            this.operatorArr = this.roleItemClassify(res.data.operator);
                            this.examinerArr = this.roleItemClassify(res.data.examiner);
                            this.visitorArr = this.roleItemClassify(res.data.visitor);
                        })
                        .catch(err =>{
                            console.log('eee')
                            layer.closeAll();
                            //layer.msg('获取数据失败',{icon:5})
                        })

                })
            },
            /*分类*/
            /*
            * [
            *   {
            *       title:'',
            *       children:[]
            *   }
            * ]
            *
            *
            * */
            roleItemClassify(arr){
                /* let roleObj = Object.assign({}, obj);*/
                let roleObj = {
                    logCollectorObj : {
                        title: '日志、事件管理模块',
                        children: []
                    },
                    usersObj : {
                        title: '用户模块',
                        children: []
                    },
                    equipmentObj : {
                        title: '资产模块',
                        children: []
                    },
                    departmentObj : {
                        title: '组织机构模块',
                        children: []
                    },
                    functionObj : {
                        title: '角色管理模块',
                        children: []
                    },
                    noteObj : {
                        title: '平台管理模块',
                        children: []
                    },
                    manageObj : {
                        title: '存储模块',
                        children: []
                    },
                    otherObj:{
                        title: '其他',
                        children: []
                    }
                };
                arr.forEach(item =>{

                    let title = item.resource.split("/")[2];
                    switch (title) {
                        case 'collector' : case 'log':
                        roleObj.logCollectorObj.children.push(item);
                        break;
                        case 'users':
                            roleObj.usersObj.children.push(item);
                            break;
                        case 'equipment':
                            roleObj.equipmentObj.children.push(item);
                            break;
                        case 'department':
                            roleObj.departmentObj.children.push(item);
                            break;
                        case 'function':
                            roleObj.functionObj.children.push(item);
                            break;
                        case 'note':
                            roleObj.noteObj.children.push(item);
                            break;
                        case 'manage':
                            roleObj.manageObj.children.push(item);
                            break;
                        default:
                            roleObj.otherObj.children.push(item);
                            break;
                    }

                });
                return roleObj;
            }
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
    .role-item{
        display: inline-block;
        padding: 3px 7px;
        border: 1px solid #ccc;
        border-radius: 5px;
        margin: 6px 6px;
        font-size: 12px;
        cursor: pointer;
    }
    h4{
        font-size: 24px;
        margin: 20px 0;
    }
    .role-content{
        padding:0 20px;
    }
    .role-content>div{
        margin: 10px 0;
    }
</style>
