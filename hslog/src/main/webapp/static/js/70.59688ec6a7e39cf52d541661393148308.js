webpackJsonp([70],{"5JPJ":function(e,t){},LH64:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var s=a("woOf"),r=a.n(s),i=a("mvHQ"),l=a.n(i),o=a("ID1p"),n=a("qTy0"),c=a("6EjU"),d=a("/sA3"),u=a("8+FI"),p={name:"userManage_SR",data:function(){var e=this;return{type:"",userRoleDialog:!1,testRoleVal:[],falseB:!1,trueB:!0,loading:!1,rightLoading:!1,leftLoading:!1,expireDateForm:!1,expireDateForm1_7:!1,expireLoading:!1,expireDateVal:1,busName:{selectionName:"userSelect",reviseUserName:"reviseUserName"},expireDate:1,mode:0,middleCheck:{background:"#ffaf56"},hideCheck:{background:"#6ba001"},treeKey:"",treeForm:!1,userForm:!1,clickModal:!1,props:{label:"name",isLeaf:"leaf"},fatherName:"",departmentParams:{level:1,subordinate:0,departmentNodeId:0,name:"",comment:""},userParams:{name:"",sex:1,phone:"",age:"",password:"",Email:"",departmentId:0,role:"",state:1},roleVal:[],userCheckboxState:!0,password2:"",selectedTreeNode:{},selectedUserIds:"",reviseUserId:"",treeBtnType:"add",userBtnType:"add",userTableHeadData:[{prop:"phone",label:"账号",width:""},{prop:"name",label:"姓名",width:""},{prop:"sex",label:"性别",width:"",formatData:function(e){return"1"==e?"男":"女"}},{prop:"age",label:"年龄",width:""},{prop:"departmentName",label:"部门",width:""},{prop:"role",label:"角色",width:""},{prop:"email",label:"邮箱",width:"150px"},{prop:"state",label:"状态",width:"",formatData:function(e){switch(e){case"0":return"已锁定";case"1":return"正常";case"2":return"停用";default:return""}}},{prop:"tools",label:"操作",width:"60px",btns:[{icon:"el-icon-edit",text:"修改用户",btnType:"reviseUser",clickFun:function(t){e.reviseUserBtn(t)}},{icon:"el-icon-key",text:"重置密码",btnType:"reviseUser",clickFun:function(t){e.resetPassword(t)}}]}],userTableData:[],userSearchFormItem:[{label:"姓名",paramName:"name",itemType:"",model:{model:""},type:"input"},{label:"账号",paramName:"phone",itemType:"",model:{model:""},type:"input"}],allUserCounts:0,pageSize:10,c_page:1,userSearchCondition:{name:"",phone:""},roleArr:[],options:[],paw_placeholder:""}},computed:{disabled:function(){return 5!==JSON.parse(localStorage.getItem("LoginUser")).role}},created:function(){var e=this;Object(d.i)(),u.default.$on("searchUserCon",function(t){e.userSearchCondition=t,e.c_page=1,e.selectUser(1,t)}),u.default.$on(this.busName.selectionName,function(t){e.selectedUserIds="",t.forEach(function(t){e.selectedUserIds+=t.id+","})}),this.getDepartmentData(),this.getRoleList(),this.getExpireDate()},mounted:function(){},watch:{userForm:function(){var e=this;this.userForm?(this.getDepartmentData(),u.default.$on("getValue",function(t){e.userParams.departmentId=t})):u.default.$off("getValue")},"userParams.password":function(e){e&&(this.mode=Object(d.d)(e))},"userParams.role":function(e){5===e&&this.roleArr.push({value:5,label:"master"})},expireDateForm:function(e){e&&(this.expireDateVal=this.expireDate)},userRoleDialog:function(e){e||(this.testRoleVal=[])}},beforeDestroy:function(){u.default.$off("searchUserCon"),u.default.$off("getValue"),u.default.$off(this.busName.selectionName),u.default.$off(this.busName.reviseUserName)},methods:{editRoleBtn:function(){""===this.selectedUserIds?layer.msg("未选中任何用户",{icon:5}):this.userRoleDialog=!0},editRole:function(){var e=this,t="";for(var a in this.testRoleVal)t+=this.testRoleVal[a]+",";this.$nextTick(function(){layer.load(1),e.$axios.post(e.$baseUrl+"/user/setUserRole.do",e.$qs.stringify({user_ids:e.selectedUserIds,role_ids:t})).then(function(t){layer.closeAll("loading");var a=t.data;"true"==a.success?(e.userRoleDialog=!1,layer.msg(a.message,{icon:1}),e.treeKey=+new Date,e.c_page=1,e.selectUser(1,e.userSearchCondition)):layer.msg(a.message,{icon:5})}).catch(function(e){layer.closeAll("loading")})})},addUserBtn2:function(){this.paw_placeholder=sessionStorage.getItem("pawComplex"),this.roleVal=[],this.userBtnType="add",this.type="test",this.userParams={name:"",sex:1,phone:"",age:"",password:"",Email:"",departmentId:0,role:""},this.password2="",this.userForm=!0},getExpireDate:function(){var e=this;this.$nextTick(function(){e.$axios.post(e.$baseUrl+"/configuration/selectPwdExpireDay.do",e.$qs.stringify("")).then(function(t){var a=t.data;"true"==a.success?e.expireDate=a.data:layer.msg(a.message,{icon:5})}).catch(function(e){})})},setExpireDate:function(){var e=this;this.$nextTick(function(){e.expireLoading=!0,e.$axios.post(e.$baseUrl+"/configuration/update.do",e.$qs.stringify({pwd_expire_day:e.expireDateVal})).then(function(t){e.expireLoading=!1;var a=t.data;"true"==a.success?(layer.msg(a.message,{icon:1}),e.expireDate=e.expireDateVal,e.expireDateForm=!1,e.expireDateForm1_7=!1):layer.msg(a.message,{icon:5})}).catch(function(t){e.expireLoading=!1})})},getRoleList:function(){var e=this;this.$nextTick(function(){e.$axios.post(e.$baseUrl+"/role/selectAllRole.do","").then(function(t){for(var a in t.data)e.roleArr.push({value:t.data[a].id,label:t.data[a].role_name})}).catch(function(e){})})},getDepartmentData:function(){var e=this;this.leftLoading=!0,this.$nextTick(function(){e.$axios.get(e.$baseUrl+"/department/selectAllDepartment.do",{}).then(function(t){e.leftLoading=!1,e.options=t.data}).catch(function(t){e.leftLoading=!1})})},getTreeData:function(e,t){var a=this;this.depData=[],this.$nextTick(function(){a.$axios.post(a.$baseUrl+"/department/selectAll_Security.do",a.$qs.stringify(e)).then(function(e){""!==e.data&&(e.data.department.forEach(function(e){a.depData.push(e)}),void 0!==e.data.user&&e.data.user[0].forEach(function(e){a.depData.push(e)}),t(a.depData),void 0!==e.data.user&&(a.changeUserShowData(e.data.user[0]),a.allUserCounts=e.data.user[0].length))}).catch(function(e){})})},loadNode:function(e,t){0===e.level?this.getTreeData({level:1},t):l()(e.data.id).length<10?this.getTreeData({id:e.data.id},t):t([])},nodeClick:function(e){e.departmentName?this.selectTreeUser(e.id):(this.fatherName=".. / "+e.name,this.selectedTreeNode={level:e.level,subordinate:e.subordinate,departmentNodeId:e.departmentNodeId,superiorId:e.superiorId,id:e.id,name:e.name,comment:e.comment})},addBtn:function(){this.treeBtnType="add",this.treeForm=!0,this.departmentParams={name:"",comment:"",level:"1"},"{}"!==l()(this.selectedTreeNode)&&(this.departmentParams=r()({},this.selectedTreeNode),this.departmentParams.name="",this.departmentParams.comment=""),console.log(this.departmentParams)},addTree:function(){var e=this;layer.load(1),this.$nextTick(function(){e.$axios.post(e.$baseUrl+"/department/insert.do",e.$qs.stringify(e.departmentParams)).then(function(t){layer.closeAll("loading"),"true"===t.data.success?(layer.msg("添加成功",{icon:1}),e.treeForm=!1,e.treeKey=+new Date):layer.msg(t.data.message,{icon:5})}).catch(function(e){layer.closeAll("loading")})})},editBtn:function(){this.selectedTreeNode.id?(this.treeBtnType="edit",this.treeForm=!0,this.departmentParams=r()({},this.selectedTreeNode)):layer.msg("未选中任何组织",{icon:5})},editTree:function(){var e=this;layer.load(1),this.$nextTick(function(){e.$axios.post(e.$baseUrl+"/department/updateById.do",e.$qs.stringify(e.departmentParams)).then(function(t){layer.closeAll("loading"),"true"===t.data.success?(layer.msg("修改成功",{icon:1}),e.treeForm=!1,e.treeKey=+new Date):layer.msg(t.data.message,{icon:5})}).catch(function(e){layer.closeAll("loading")})})},removeTree:function(){var e=this;this.selectedTreeNode.id?layer.confirm("您确定删除么？",{btn:["确定","取消"]},function(t){layer.load(1),e.$nextTick(function(){e.$axios.post(e.$baseUrl+"/department/delete.do",e.$qs.stringify({id:e.selectedTreeNode.id})).then(function(t){layer.closeAll("loading"),t.data?(layer.msg("删除成功",{icon:1}),e.selectedTreeNode={},e.treeKey=+new Date):layer.msg("删除失败，该目录存在子集",{icon:5})}).catch(function(e){layer.closeAll("loading")})})}):layer.msg("未选中任何组织",{icon:5})},removeClass:function(e){"department-wapper"===$(e.target)[0].className&&(this.selectedTreeNode={},$(".el-tree--highlight-current").removeClass("el-tree--highlight-current"))},selectTreeUser:function(e){var t=this;this.rightLoading=!0,this.$nextTick(function(){t.$axios.get(t.$baseUrl+"/user/selectUser.do",{params:{id:e}}).then(function(e){t.rightLoading=!1,t.changeUserShowData(e.data[0])}).catch(function(e){t.rightLoading=!1})})},selectUser:function(e,t){var a=this;this.fatherName="",t.pageIndex=e,t.pageSize=this.pageSize,this.rightLoading=!0,this.$nextTick(function(){a.$axios.post(a.$baseUrl+"/user/selectAll_Security.do",a.$qs.stringify(t)).then(function(e){a.rightLoading=!1,a.allUserCounts=Number(e.data.count[0].count),a.changeUserShowData(e.data.user[0])}).catch(function(e){a.rightLoading=!1})})},changeUserShowData:function(e){this.userTableData=e},addUserBtn:function(){this.roleVal=[],this.userBtnType="add",this.userParams={name:"",sex:1,phone:"",age:"",password:"",Email:"",departmentId:0,role:""},this.password2="",this.userForm=!0},addUser:function(){var e=this;for(var t in this.userParams.role="",this.roleVal)this.userParams.role+=this.roleVal[t]+",";this.checkUserParams(this.userParams)&&(layer.load(1),this.$nextTick(function(){e.$axios.post(e.$baseUrl+"/user/insert_Security.do",e.$qs.stringify(e.userParams)).then(function(t){layer.closeAll("loading"),"true"===t.data.success?(layer.msg("添加成功",{icon:1}),e.userForm=!1,e.userParams={name:"",sex:1,phone:"",age:"",password:"",Email:"",departmentId:0,role:""},e.treeKey=+new Date,e.c_page=1,e.selectUser(1,e.userSearchCondition)):layer.msg(t.data.message,{icon:5})}).catch(function(e){layer.closeAll("loading"),layer.msg("添加失败",{icon:5})})}))},removeUser:function(){var e=this;""===this.selectedUserIds?layer.msg("未选中任何用户",{icon:5}):layer.confirm("您确定删除么？",{btn:["确定","取消"]},function(t){layer.load(1),e.$nextTick(function(){e.$axios.post(e.$baseUrl+"/user/deletes.do",e.$qs.stringify({ids:e.selectedUserIds})).then(function(t){layer.closeAll("loading"),"true"===t.data.success?(layer.msg("删除成功",{icon:1}),e.treeKey=+new Date,e.selectUser(1,e.userSearchCondition)):layer.msg("删除失败",{icon:5})}).catch(function(e){layer.closeAll("loading")})})})},reviseUserBtn:function(e){var t=this;this.userForm=!0,this.userBtnType="revise",this.rightLoading=!0,this.$nextTick(function(){t.$axios.post(t.$baseUrl+"/user/selectById.do",t.$qs.stringify({id:e.id})).then(function(a){t.rightLoading=!1,t.userParams={name:a.data.name,sex:a.data.sex,phone:a.data.phone,age:a.data.age,Email:a.data.email,departmentId:a.data.departmentId,role:a.data.role},""!==t.roleVal&&(t.roleVal=a.data.role.split(",")),1===a.data.state?t.userCheckboxState=!1:t.userCheckboxState=!0,t.reviseUserId=e.id}).catch(function(e){layer.msg("获取用户信息失败",{icon:5}),t.rightLoading=!1})})},reviseUser:function(e){var t=this;for(var a in this.userParams.role="",this.roleVal)this.userParams.role+=this.roleVal[a]+",";this.userParams.id=this.reviseUserId,!0===this.userCheckboxState?this.userParams.state=2:this.userParams.state=1,this.checkUserParams(this.userParams)&&(layer.load(1),this.$nextTick(function(){t.$axios.post(t.$baseUrl+"/user/inserts.do",t.$qs.stringify(t.userParams)).then(function(e){layer.closeAll("loading"),"true"===e.data.success?(layer.msg("修改成功",{icon:1}),t.userForm=!1,t.userParams={name:"",sex:1,phone:"",age:"",password:"",Email:"",departmentId:0,role:""},t.treeKey=+new Date,t.c_page=1,t.selectUser(1,t.userSearchCondition)):layer.msg(e.data.message,{icon:5})}).catch(function(e){layer.closeAll("loading"),layer.msg("修改失败",{icon:5})})}))},handleCurrentChange:function(e){this.selectUser(e,this.userSearchCondition)},resetPassword:function(e){var t=this;layer.confirm("您确定重置此用户密码么？",{btn:["确定","取消"]},function(a){layer.close(a),t.$nextTick(function(){t.loading=!0,t.$axios.post(t.$baseUrl+"/user/resetPasswordById.do",t.$qs.stringify({id:e.id})).then(function(e){t.loading=!1;var a=e.data;if("true"==a.success){var s=a.message;t.$alert(s,"密码重置为",{confirmButtonText:"确定"})}else layer.msg(a.message,{icon:5})}).catch(function(e){t.loading=!1})})})},checkUserParams:function(e){if("add"===this.userBtnType&&e.password!==this.password2)return layer.msg("两次密码不正确",{icon:5}),!1;return""!==e.phone&&11===e.phone.length&&$.isNumeric(e.phone)?0===e.departmentId?(layer.msg("未选中任何部门",{icon:5}),!1):""===e.name?(layer.msg("姓名不能为空",{icon:5}),!1):/^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/.test(e.Email)?""!==e.role||"test"===this.type||(layer.msg("未选中角色",{icon:5}),!1):(layer.msg("邮箱格式不正确",{icon:5}),!1):(layer.msg("电话（账号）不能为空或格式不正确",{icon:5}),!1)}},components:{vBasetable:n.default,vSearchForm:o.default,vSelectTree:c.default}},m={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{directives:[{name:"loading",rawName:"v-loading",value:e.loading,expression:"loading"}],staticClass:"content-bg",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[a("div",{staticClass:"top-title"},[e._v("用户管理")]),e._v(" "),a("div",{staticClass:"user-manage-content"},[a("el-row",{attrs:{gutter:10}},[a("el-col",{attrs:{span:6}},[a("div",{directives:[{name:"loading",rawName:"v-loading",value:e.leftLoading,expression:"leftLoading"}],staticClass:"department-wapper",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"},on:{click:e.removeClass}},[a("div",{staticClass:"department-top"},[a("h5",[e._v("机构列表")]),e._v(" "),e.$is_has("userManage_departmentBtns")?a("div",{staticClass:"department-btns"},[a("el-button",{attrs:{type:"primary",size:"mini",plain:"",title:"添加组织机构"},on:{click:e.addBtn}},[a("i",{staticClass:"el-icon-plus"})]),e._v(" "),a("el-button",{attrs:{type:"success",size:"mini",plain:"",title:"修改组织机构"},on:{click:e.editBtn}},[a("i",{staticClass:"el-icon-edit"})]),e._v(" "),a("el-button",{attrs:{type:"danger",size:"mini",plain:"",title:"删除组织机构"},on:{click:e.removeTree}},[a("i",{staticClass:"el-icon-close"})])],1):e._e()]),e._v(" "),a("div",{staticClass:"department-content treeBox"},[a("el-tree",{key:e.treeKey,attrs:{props:e.props,load:e.loadNode,"highlight-current":"",lazy:""},on:{"node-click":e.nodeClick},scopedSlots:e._u([{key:"default",fn:function(t){var s=t.node;return t.data,a("span",{staticClass:"custom-tree-node"},[s.data.departmentName?a("span",{staticClass:"tree-label"},[a("span",{staticClass:"people-icon el-icon-user-solid"}),e._v(e._s(s.label)+"\n                                ")]):a("span",{staticClass:"tree-label"},[a("span",{staticClass:"tree-icon el-icon-s-home"}),e._v(e._s(s.label)+"\n                                ")])])}}])})],1)])]),e._v(" "),a("el-col",{attrs:{span:18}},[a("div",{directives:[{name:"loading",rawName:"v-loading",value:e.rightLoading,expression:"rightLoading"}],staticClass:"user-wapper",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[a("div",{staticClass:"user-department-name"},[e._v(e._s(e.fatherName.split("/")[1])+"\n                        "),e.$is_has("userManage_expireDate")?a("span",{staticClass:"expire-wapper"},[e._v("密码过期时间:"),a("b",[e._v(e._s(this.expireDate))]),e._v("天 "),a("i",{staticClass:"el-icon-edit editExpire",on:{click:function(t){e.expireDateForm=!0}}})]):e._e(),e._v(" "),e.$is_has("userManage_expireDate_1_7")?a("span",{staticClass:"expire-wapper"},[e._v("密码过期时间:"),a("b",[e._v(e._s(this.expireDate))]),e._v("天 "),a("i",{staticClass:"el-icon-edit editExpire",on:{click:function(t){e.expireDateForm1_7=!0}}})]):e._e()]),e._v(" "),a("div",{staticClass:"user-tools-wapper"},[a("div",{staticClass:"user-btn"},[e.$is_has("userManage_addUser")?a("el-button",{attrs:{type:"primary",size:"mini",plain:"",title:"添加用户"},on:{click:e.addUserBtn}},[a("i",{staticClass:"el-icon-plus"}),e._v("添加用户")]):e._e(),e._v(" "),e.$is_has("userManage_addUser2")?a("el-button",{attrs:{type:"primary",size:"mini",plain:"",title:"添加用户"},on:{click:e.addUserBtn2}},[a("i",{staticClass:"el-icon-plus"}),e._v("添加用户")]):e._e(),e._v(" "),a("el-button",{attrs:{type:"danger",size:"mini",plain:"",title:"删除用户"},on:{click:e.removeUser}},[a("i",{staticClass:"el-icon-close"}),e._v("删除用户")]),e._v(" "),e.$is_has("userManage_setRole")?a("el-button",{attrs:{type:"primary",size:"mini",plain:"",title:"权限修改"},on:{click:e.editRoleBtn}},[a("i",{staticClass:"el-icon-edit"}),e._v("修改权限")]):e._e()],1),e._v(" "),a("div",{staticClass:"user-search"},[a("v-search-form",{attrs:{formItem:e.userSearchFormItem,busName:"searchUserCon"}})],1)]),e._v(" "),a("div",{staticClass:"user-table-wapper"},[a("v-basetable",{attrs:{selection:!0,tableHead:e.userTableHeadData,tableData:e.userTableData,busName:e.busName}})],1),e._v(" "),a("div",{staticClass:"user-page-wapper"},[e._v("\n                        共检索到用户"),a("b",[e._v(e._s(e.allUserCounts))]),e._v("人\n                        "),a("el-pagination",{attrs:{background:"",layout:"prev, pager, next","current-page":e.c_page,"page-size":e.pageSize,total:e.allUserCounts},on:{"current-change":e.handleCurrentChange,"update:currentPage":function(t){e.c_page=t},"update:current-page":function(t){e.c_page=t}}})],1)])])],1)],1),e._v(" "),a("el-dialog",{attrs:{title:"add"===e.treeBtnType?"添加组织机构":"修改组织机构",visible:e.treeForm,width:"440px"},on:{"update:visible":function(t){e.treeForm=t}}},[a("el-form",{attrs:{"label-width":"80px"}},["add"===e.treeBtnType&&"{}"!=JSON.stringify(e.selectedTreeNode)?a("el-form-item",{attrs:{label:"父级目录:"}},[a("el-input",{staticClass:"item",attrs:{placeholder:"",disabled:""},model:{value:e.fatherName,callback:function(t){e.fatherName=t},expression:"fatherName"}})],1):e._e(),e._v(" "),a("el-form-item",{attrs:{label:"名称:"}},[a("el-input",{staticClass:"item",attrs:{placeholder:"名称"},model:{value:e.departmentParams.name,callback:function(t){e.$set(e.departmentParams,"name",t)},expression:"departmentParams.name"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"描述:"}},[a("el-input",{attrs:{type:"textarea",autosize:{minRows:8,maxRows:10}},model:{value:e.departmentParams.comment,callback:function(t){e.$set(e.departmentParams,"comment",t)},expression:"departmentParams.comment"}})],1)],1),e._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{attrs:{type:"primary"},on:{click:function(t){"add"===e.treeBtnType?e.addTree():e.editTree()}}},[e._v("确 定")]),e._v(" "),a("el-button",{on:{click:function(t){e.treeForm=!1}}},[e._v("取 消")])],1)],1),e._v(" "),a("el-dialog",{attrs:{title:"add"===e.userBtnType?"添加用户":"修改用户",visible:e.userForm,width:"500px","close-on-click-modal":e.clickModal},on:{"update:visible":function(t){e.userForm=t}}},[a("el-form",{attrs:{"label-width":"100px"}},[a("el-form-item",{attrs:{label:"账号(电话):"}},[a("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[e._v("*")]),e._v(" "),a("el-input",{staticClass:"item",attrs:{placeholder:"11位有效数字",maxlength:"11"},model:{value:e.userParams.phone,callback:function(t){e.$set(e.userParams,"phone",t)},expression:"userParams.phone"}})],1),e._v(" "),"add"===e.userBtnType?a("el-form-item",{attrs:{label:"密码:"}},[a("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[e._v("*")]),e._v(" "),a("el-input",{staticClass:"item",attrs:{type:"password",placeholder:e.paw_placeholder,maxlength:"18"},model:{value:e.userParams.password,callback:function(t){e.$set(e.userParams,"password",t)},expression:"userParams.password"}})],1):e._e(),e._v(" "),""!==e.userParams.password&&void 0!==e.userParams.password?a("ul",{staticClass:"pass_set"},[a("li",{staticStyle:{"background-color":"rgb(237, 62, 13)"},attrs:{id:"strength_L"}},[e._v("弱")]),e._v(" "),a("li",{style:e.mode>=2?{background:"#ffaf56"}:{},attrs:{id:"strength_M"}},[e._v("中")]),e._v(" "),a("li",{style:e.mode>2?{background:"#6ba001"}:{},attrs:{id:"strength_H"}},[e._v("强")])]):e._e(),e._v(" "),"add"===e.userBtnType?a("el-form-item",{attrs:{label:"确认密码:"}},[a("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[e._v("*")]),e._v(" "),a("el-input",{staticClass:"item",attrs:{type:"password",placeholder:"确认密码",maxlength:"18"},model:{value:e.password2,callback:function(t){e.password2=t},expression:"password2"}})],1):e._e(),e._v(" "),a("el-form-item",{attrs:{label:"姓名:"}},[a("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[e._v("*")]),e._v(" "),a("el-input",{staticClass:"item",attrs:{placeholder:"姓名"},model:{value:e.userParams.name,callback:function(t){e.$set(e.userParams,"name",t)},expression:"userParams.name"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"部门:"}},[a("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[e._v("*")]),e._v(" "),a("v-select-tree",{attrs:{options:e.options,value:e.userParams.departmentId}})],1),e._v(" "),"test"!==e.type?a("el-form-item",{attrs:{label:"角色:"}},[a("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[e._v("*")]),e._v(" "),a("el-select",{staticStyle:{width:"100%"},attrs:{multiple:"",placeholder:"请选择"},model:{value:e.roleVal,callback:function(t){e.roleVal=t},expression:"roleVal"}},e._l(e.roleArr,function(e){return a("el-option",{key:e.value,attrs:{label:e.label,value:e.value}})}),1)],1):e._e(),e._v(" "),a("el-form-item",{attrs:{label:"邮箱:"}},[a("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[e._v("*")]),e._v(" "),a("el-input",{staticClass:"item",attrs:{placeholder:"邮箱"},model:{value:e.userParams.Email,callback:function(t){e.$set(e.userParams,"Email",t)},expression:"userParams.Email"}})],1),e._v(" "),"add"!==e.userBtnType?a("el-form-item",{attrs:{label:"状态:"}},[a("el-checkbox",{model:{value:e.userCheckboxState,callback:function(t){e.userCheckboxState=t},expression:"userCheckboxState"}},[e._v("限制登录")])],1):e._e(),e._v(" "),a("el-form-item",{attrs:{label:"性别:"}},[a("el-radio-group",{model:{value:e.userParams.sex,callback:function(t){e.$set(e.userParams,"sex",t)},expression:"userParams.sex"}},[a("el-radio",{attrs:{label:1}},[e._v("男")]),e._v(" "),a("el-radio",{attrs:{label:0}},[e._v("女")])],1)],1),e._v(" "),a("el-form-item",{attrs:{label:"年龄:"}},[a("el-input",{staticClass:"item",attrs:{type:"number",placeholder:"年龄"},model:{value:e.userParams.age,callback:function(t){e.$set(e.userParams,"age",t)},expression:"userParams.age"}})],1)],1),e._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{attrs:{type:"primary"},on:{click:function(t){"add"===e.userBtnType?e.addUser():e.reviseUser()}}},[e._v("确 定")]),e._v(" "),a("el-button",{on:{click:function(t){e.userForm=!1}}},[e._v("取 消")])],1)],1),e._v(" "),a("el-dialog",{directives:[{name:"loading",rawName:"v-loading",value:e.expireLoading,expression:"expireLoading"}],attrs:{title:"设置",visible:e.expireDateForm1_7,width:"400px","close-on-click-modal":e.falseB,"destroy-on-close":e.trueB,"element-loading-background":"rgba(48, 62, 78, 0.5)"},on:{"update:visible":function(t){e.expireDateForm1_7=t}}},[a("el-form",{attrs:{"label-width":"100px"}},[a("el-form-item",{attrs:{label:"过期时间:"}},[a("el-input",{staticClass:"item",attrs:{type:"number",placeholder:"1-7 数字",maxlength:"1"},model:{value:e.expireDateVal,callback:function(t){e.expireDateVal=t},expression:"expireDateVal"}})],1)],1),e._v(" "),a("p",{staticStyle:{color:"#e4956d","padding-left":"30px","font-size":"12px"}},[e._v("提示：过期时间值为 数字 1-7")]),e._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{attrs:{type:"primary",disabled:(e.expireDateVal>7||e.expireDateVal<1)&&"disabled"},on:{click:e.setExpireDate}},[e._v("确 定")]),e._v(" "),a("el-button",{on:{click:function(t){e.expireDateForm1_7=!1}}},[e._v("取 消")])],1)],1),e._v(" "),a("el-dialog",{directives:[{name:"loading",rawName:"v-loading",value:e.expireLoading,expression:"expireLoading"}],attrs:{title:"设置",visible:e.expireDateForm,width:"400px","close-on-click-modal":e.falseB,"destroy-on-close":e.trueB,"element-loading-background":"rgba(48, 62, 78, 0.5)"},on:{"update:visible":function(t){e.expireDateForm=t}}},[a("el-form",{attrs:{"label-width":"100px"}},[a("el-form-item",{attrs:{label:"过期时间:"}},[a("el-input",{staticClass:"item",attrs:{type:"number"},model:{value:e.expireDateVal,callback:function(t){e.expireDateVal=t},expression:"expireDateVal"}})],1)],1),e._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{attrs:{type:"primary",disabled:e.expireDateVal<0&&"disabled"},on:{click:e.setExpireDate}},[e._v("确 定")]),e._v(" "),a("el-button",{on:{click:function(t){e.expireDateForm=!1}}},[e._v("取 消")])],1)],1),e._v(" "),a("el-dialog",{directives:[{name:"loading",rawName:"v-loading",value:e.expireLoading,expression:"expireLoading"}],attrs:{title:"设置用户角色",visible:e.userRoleDialog,width:"400px","close-on-click-modal":e.falseB,"destroy-on-close":e.trueB,"element-loading-background":"rgba(48, 62, 78, 0.5)"},on:{"update:visible":function(t){e.userRoleDialog=t}}},[a("el-form",{attrs:{"label-width":"50px"}},[a("el-form-item",{attrs:{label:"角色:"}},[a("el-select",{staticStyle:{width:"100%"},attrs:{multiple:"",placeholder:"请选择"},model:{value:e.testRoleVal,callback:function(t){e.testRoleVal=t},expression:"testRoleVal"}},e._l(e.roleArr,function(e){return a("el-option",{key:e.value,attrs:{label:e.label,value:e.value}})}),1)],1)],1),e._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{attrs:{type:"primary",disabled:e.testRoleVal.length<1&&"disabled"},on:{click:e.editRole}},[e._v("确 定")]),e._v(" "),a("el-button",{on:{click:function(t){e.userRoleDialog=!1}}},[e._v("取 消")])],1)],1)],1)},staticRenderFns:[]};var h=a("VU/8")(p,m,!1,function(e){a("5JPJ")},null,null);t.default=h.exports}});