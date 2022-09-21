webpackJsonp([46],{"6vP3":function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var l={name:"roleManage",data:function(){var e=this;return{roleState:!1,settingState:!1,roleBtnType:"add",roleParams:{role_name:"",note:""},resiveRoleId:"",roleTableHead:[{prop:"role_name",label:"角色名称",width:""},{prop:"note",label:"描述",width:""},{prop:"tools",label:"操作",width:"100px",btns:[{icon:"el-icon-edit",text:"修改菜单",btnType:"reviseMenu",clickFun:function(t){e.roleBtnType="resive",e.roleState=!0,e.resiveRoleId=t.id,e.$nextTick(function(){layer.load(1),e.$axios.post(e.$baseUrl+"/role/getEntity.do",e.$qs.stringify({id:e.resiveRoleId})).then(function(t){layer.closeAll("loading"),e.roleParams={role_name:t.data.role_name,note:t.data.note}}).catch(function(e){layer.closeAll("loading"),layer.msg("获取信息失败",{icon:5})})})}},{icon:"el-icon-close",text:"删除菜单",btnType:"reviseMenu",clickFun:function(t){e.removeRole(t.id)}},{icon:"el-icon-setting",text:"配置",btnType:"reviseMenu",clickFun:function(t){e.active=0,e.firstTreeData=[],e.secondTreeData=[],e.settingState=!0,e.resiveRoleId=t.id,e.$nextTick(function(){layer.load(1),e.$axios.post(e.$baseUrl+"/menu/selectSystemMenu.do","").then(function(t){layer.closeAll("loading"),e.firstTreeData=t.data}).catch(function(e){layer.closeAll("loading"),layer.msg("获取信息失败",{icon:5})})})}}]}],roleTableData:[],active:0,firstTreeData:[],firstProps:{children:"menus",label:"menuName"},secondTreeData:[],menuIds:""}},created:function(){this.getRoleData()},methods:{getRoleData:function(){var e=this;this.$nextTick(function(){layer.load(1),e.$axios.post(e.$baseUrl+"/role/selectAllRole.do","").then(function(t){layer.closeAll("loading"),e.roleTableData=t.data}).catch(function(e){layer.closeAll("loading")})})},addRoleBtn:function(){this.roleBtnType="add",this.roleState=!0,this.roleParams={role_name:"",note:""}},addRole:function(){var e=this;this.$nextTick(function(){layer.load(1),e.$axios.post(e.$baseUrl+"/role/upsert.do",e.$qs.stringify(e.roleParams)).then(function(t){layer.closeAll("loading"),"true"===t.data.success?(layer.msg("添加成功",{icon:1}),e.roleState=!1,e.getRoleData()):layer.msg(t.data.message,{icon:5})}).catch(function(e){layer.closeAll("loading")})})},editRole:function(){var e=this;this.roleParams.id=this.resiveRoleId,this.$nextTick(function(){layer.load(1),e.$axios.post(e.$baseUrl+"/role/upsert.do",e.$qs.stringify(e.roleParams)).then(function(t){layer.closeAll("loading"),"true"===t.data.success?(layer.msg("修改成功",{icon:1}),e.roleState=!1,e.getRoleData()):layer.msg(t.data.message,{icon:5})}).catch(function(e){layer.closeAll("loading")})})},removeRole:function(e){var t=this;layer.confirm("您确定删除么？",{btn:["确定","取消"]},function(a){layer.load(1),t.$nextTick(function(){t.$axios.post(t.$baseUrl+"/role/delete.do",t.$qs.stringify({id:e})).then(function(e){layer.closeAll(),"true"===e.data.success?(layer.msg("删除成功",{icon:1}),t.getRoleData()):layer.msg("删除失败",{icon:5})}).catch(function(e){layer.closeAll()})})})},nextSetting:function(){var e=this;this.menuIds="";var t=this.$refs.firstTree.getCheckedKeys(),a=this.$refs.firstTree.getHalfCheckedKeys();if(t.length>0||a.length>0){var l="";for(var o in t)l+=t[o]+",";for(var i in a)l+=a[i]+",";this.menuIds=l,this.$nextTick(function(){layer.load(1),e.$axios.post(e.$baseUrl+"/menu/selectSystemMenuByIDs.do",e.$qs.stringify({ids:l})).then(function(t){layer.closeAll("loading"),e.secondTreeData=t.data,e.active=1}).catch(function(e){layer.closeAll("loading")})})}},prevSetting:function(){this.active=0},pushSetting:function(){var e=this,t=this.$refs.secondTree.getCheckedKeys(),a=this.$refs.secondTree.getHalfCheckedKeys(),l="";for(var o in t)l+=t[o]+",";for(var i in a)l+=a[i]+",";this.$nextTick(function(){layer.load(1),e.$axios.post(e.$baseUrl+"/menu/upsertMenuButton.do",e.$qs.stringify({roleID:e.resiveRoleId,buttonIds:l,menuIds:e.menuIds})).then(function(t){e.settingState=!1,layer.closeAll("loading"),"true"===t.data.success?layer.msg("配置成功",{icon:1}):layer.msg(t.data.message,{icon:5})}).catch(function(e){layer.closeAll("loading")})})}},components:{vBasetable:a("qTy0").default}},o={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"content-bg"},[a("div",{staticClass:"top-title"},[e._v("角色管理")]),e._v(" "),a("div",{staticClass:"role-btn"},[a("el-button",{attrs:{type:"primary",size:"mini",plain:"",title:"添加"},on:{click:e.addRoleBtn}},[a("i",{staticClass:"el-icon-lx-add"}),e._v("添加")])],1),e._v(" "),a("div",{staticClass:"role-wapper"},[a("v-basetable",{attrs:{tableHead:e.roleTableHead,tableData:e.roleTableData}})],1),e._v(" "),a("el-dialog",{attrs:{title:"add"===e.roleBtnType?"添加角色":"修改角色",visible:e.roleState,width:"440px"},on:{"update:visible":function(t){e.roleState=t}}},[a("el-form",{attrs:{"label-width":"80px"}},[a("el-form-item",{attrs:{label:"名称:"}},[a("el-input",{staticClass:"item",attrs:{placeholder:"名称"},model:{value:e.roleParams.role_name,callback:function(t){e.$set(e.roleParams,"role_name",t)},expression:"roleParams.role_name"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"描述:"}},[a("el-input",{attrs:{type:"textarea",autosize:{minRows:8,maxRows:10}},model:{value:e.roleParams.note,callback:function(t){e.$set(e.roleParams,"note",t)},expression:"roleParams.note"}})],1)],1),e._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{attrs:{type:"primary"},on:{click:function(t){"add"===e.roleBtnType?e.addRole():e.editRole()}}},[e._v("确 定")]),e._v(" "),a("el-button",{on:{click:function(t){e.roleState=!1}}},[e._v("取 消")])],1)],1),e._v(" "),a("el-dialog",{attrs:{title:"配置",visible:e.settingState,width:"540px",height:"600px"},on:{"update:visible":function(t){e.settingState=t}}},[a("el-steps",{attrs:{active:e.active,simple:""}},[a("el-step",{attrs:{title:"添加菜单 1",icon:"el-icon-edit"}}),e._v(" "),a("el-step",{attrs:{title:"添加按钮 2",icon:"el-icon-edit"}})],1),e._v(" "),0==e.active?a("div",{staticClass:"seting-wapper"},[a("el-tree",{ref:"firstTree",attrs:{data:e.firstTreeData,"default-expand-all":"","show-checkbox":"","node-key":"id",props:e.firstProps}})],1):e._e(),e._v(" "),1==e.active?a("div",{staticClass:"seting-wapper"},[a("el-tree",{ref:"secondTree",attrs:{data:e.secondTreeData,"default-expand-all":"","show-checkbox":"","node-key":"id",props:e.firstProps}})],1):e._e(),e._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[1==e.active?a("el-button",{staticStyle:{"margin-top":"12px"},on:{click:e.prevSetting}},[e._v("上一步")]):e._e(),e._v(" "),0==e.active?a("el-button",{staticStyle:{"margin-top":"12px"},on:{click:e.nextSetting}},[e._v("下一步")]):e._e(),e._v(" "),1==e.active?a("el-button",{attrs:{type:"primary"},on:{click:e.pushSetting}},[e._v("确 定")]):e._e(),e._v(" "),a("el-button",{on:{click:function(t){e.settingState=!1}}},[e._v("取 消")])],1)],1)],1)},staticRenderFns:[]};var i=a("VU/8")(l,o,!1,function(e){a("v7Fp")},"data-v-7d38d7b7",null);t.default=i.exports},v7Fp:function(e,t){}});