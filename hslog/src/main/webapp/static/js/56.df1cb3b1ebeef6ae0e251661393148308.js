webpackJsonp([56],{"3fQY":function(t,e){},i9TG:function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=a("mvHQ"),s=a.n(i),o=a("62NR"),l=a("8+FI"),n=a("/sA3"),r={name:"index",data:function(){var t=this;return{falseB:!1,loading:!1,conditionFrom:{},page:1,c_page:1,size:15,allCounts:0,checkList:[],tableBusName:{selectionName:"dataSourceDelect"},delectIds:"",tableHead:[{prop:"data_source_name",label:"数据源名称"},{prop:"data_source_item_type",label:"数据库类型",width:"100"},{prop:"description",label:"说明"},{prop:"data_source_sample_num",label:"样本数",width:"60"},{prop:"data_source_is_initialized_label",label:"是否初始化",width:"100"},{prop:"data_source_init_time",label:"初始化时间"},{prop:"data_source_discovery_state_label",label:"敏感数据发现状态"},{prop:"tools",label:"操作",width:"60",btns:[{icon:"el-icon-edit",text:"修改",clickFun:function(e,a){t.formState=!0,t.passwordState=!0,t.editId=e.data_source_id,t.getDataSourceInfo()}},{icon:"el-icon-coin",text:"数据库详情",clickFun:function(e,a){t.detailState=!0,t.editId=e.data_source_id,t.getLeftList()}}]}],tableData:[],editId:"",formState:!1,formLoading:!1,form:{data_source_name:"",data_source_item_type:"",data_source_ip:"",data_source_port:"",data_source_username:"",data_source_password:"",data_source_dbname:"",data_source_sample_num:1},passwordState:!1,dataSourceType:[{label:"关系型数据库",value:"1"},{label:"大数据平台",value:"2"}],childrenType:[],linkObj:{state:!0,text:"连接成功"},detailState:!1,detailLoading:!1,detailLeftLoading:!1,detailRightLoading:!1,leftList:[],fieldTableHead:[{prop:"COLUMN_NAME",label:"字段名称"},{prop:"DATA_TYPE",label:"数据类型"},{prop:"LENGTH",label:"数据类型长度"},{prop:"IS_NULLABLE",label:"是否为空"},{prop:"COLUMN_COMMENT",label:"注释"}],fieldTableData:[],tableHeight:0,currentNode:{},tableDialogState:!1,dataDetailLoading:!1,tableDialogHeight:0,tableDialogHead:[],tableDialogData:[],errorLinkState:!1}},created:function(){var t=this;this.getDataList(1,{}),this.clearForm(),l.default.$on(this.tableBusName.selectionName,function(e){for(var a in t.delectIds="",e)t.delectIds+=e[a].data_source_id+","}),this.tableHeight=document.body.clientHeight-402,window.onresize=function(){var e=document.body.clientHeight-402;t.tableHeight=e<320?320:e;var a=document.body.clientHeight-370;t.tableDialogHeight=a<320?320:a}},watch:{formState:function(){this.formState?(this.getDBType(),this.linkObj={state:!0,text:""}):(this.editId="",this.clearForm())},detailState:function(){this.detailState||(this.currentNode={},this.editId="",this.leftList=[],this.fieldTableData=[])}},methods:{clearForm:function(){this.passwordState=!1,this.form={data_source_name:"",data_source_item_type:"",data_source_ip:"",data_source_port:"",data_source_username:"",data_source_password:"",data_source_dbname:"",data_source_sample_num:1}},passwordBlur:function(){""!==this.form.data_source_password&&(this.passwordState=!0)},passwordFocus:function(){this.passwordState=!1},initialize:function(){var t=this;layer.confirm("您确定初始化么？",{btn:["确定","取消"]},function(e){layer.close(e),t.$nextTick(function(){t.loading=!0,t.$axios.post(t.$baseUrl+"/dataSource/dataSourceInit.do",t.$qs.stringify({data_source_ids:t.delectIds})).then(function(e){t.loading=!1;var a=e.data;"true"==a.success?(layer.msg(a.message,{icon:1}),t.getDataList(1,t.conditionFrom),t.delectIds=""):layer.msg(a.message,{icon:5})}).catch(function(e){t.loading=!1})})},function(){layer.close()})},discoveryData:function(){var t=this;layer.confirm("您确定执行么？",{btn:["确定","取消"]},function(e){layer.close(e),t.$nextTick(function(){t.loading=!0,t.$axios.post(t.$baseUrl+"/dataSource/dataDiscovery.do",t.$qs.stringify({data_source_ids:t.delectIds})).then(function(e){t.loading=!1;var a=e.data;"true"==a.success?(layer.msg(a.message,{icon:1}),t.getDataList(1,t.conditionFrom),t.delectIds=""):layer.msg(a.message,{icon:5})}).catch(function(e){t.loading=!1})})},function(){layer.close()})},refresh:function(){this.getDataList(1,this.conditionFrom)},getDataList:function(t,e){var a=this;this.c_page=t;var i=e;i.pageIndex=t,i.pageSize=this.size,this.$nextTick(function(){a.loading=!0,a.$axios.post(a.$baseUrl+"/dataSource/getDataSourceList.do",a.$qs.stringify(i)).then(function(t){a.loading=!1;var e=t.data;"true"==e.success?(a.allCounts=Number(e.data[0].count),a.tableData=e.data[0].list[0]):layer.msg(e.message,{icon:5})}).catch(function(t){a.loading=!1})})},handleCurrentChange:function(t){this.getDataList(t,this.conditionFrom)},getDataSourceInfo:function(){var t=this;this.$nextTick(function(){t.loading=!0,t.$axios.post(t.$baseUrl+"/dataSource/getDataSourceInfo.do",t.$qs.stringify({data_source_id:t.editId})).then(function(e){t.loading=!1;var a=e.data;"true"==a.success?(a.data.data_source_username=Object(n.f)(a.data.data_source_username),a.data.data_source_password=Object(n.f)(a.data.data_source_password),t.form=a.data):layer.msg(a.message,{icon:5})}).catch(function(e){t.loading=!1})})},getDBType:function(){var t=this;this.$nextTick(function(){t.loading=!0,t.$axios.post(t.$baseUrl+"/dataSource/getDataSourceItemType.do",t.$qs.stringify("")).then(function(e){t.loading=!1;var a=e.data;"true"==a.success?t.childrenType=a.data:layer.msg(a.message,{icon:5})}).catch(function(e){t.loading=!1})})},addDataSource:function(){var t=this;this.checkParam()&&this.$nextTick(function(){t.formLoading=!0;var e=JSON.parse(s()(t.form));e.data_source_username=Object(n.h)(e.data_source_username),e.data_source_password=Object(n.h)(e.data_source_password),t.$axios.post(t.$baseUrl+"/dataSource/save.do",t.$qs.stringify(e)).then(function(e){t.formLoading=!1;var a=e.data;"true"==a.success?(layer.msg(a.message,{icon:1}),t.formState=!1,t.getDataList(1,{})):layer.msg(a.message,{icon:5})}).catch(function(e){t.formLoading=!1})})},reviseDataSource:function(){var t=this;this.checkParam()&&this.$nextTick(function(){t.formLoading=!0;var e=JSON.parse(s()(t.form));e.data_source_username=Object(n.h)(e.data_source_username),e.data_source_password=Object(n.h)(e.data_source_password),t.$axios.post(t.$baseUrl+"/dataSource/update.do",t.$qs.stringify(e)).then(function(e){t.formLoading=!1;var a=e.data;"true"==a.success?(layer.msg(a.message,{icon:1}),t.formState=!1,t.editId="",t.getDataList(1,{})):layer.msg(a.message,{icon:5})}).catch(function(e){t.formLoading=!1})})},remove:function(){var t=this;layer.confirm("您确定删除么？",{btn:["确定","取消"]},function(e){layer.close(e),t.$nextTick(function(){t.loading=!0,t.$axios.post(t.$baseUrl+"/dataSource/delete.do",t.$qs.stringify({data_source_ids:t.delectIds})).then(function(e){t.loading=!1;var a=e.data;"true"==a.success?(layer.msg(a.message,{icon:1}),t.getDataList(1,t.conditionFrom),t.delectIds=""):layer.msg(a.message,{icon:5})}).catch(function(e){t.loading=!1})})},function(){layer.close()})},testLink:function(){var t=this;this.checkParam()&&this.$nextTick(function(){t.formLoading=!0;var e=JSON.parse(s()(t.form));e.data_source_username=Object(n.h)(e.data_source_username),e.data_source_password=Object(n.h)(e.data_source_password),t.$axios.post(t.$baseUrl+"/dataSource/testConnection.do",t.$qs.stringify(e)).then(function(e){t.formLoading=!1;var a=e.data;"true"==a.success?t.linkObj={state:!0,text:a.message}:(t.linkObj={state:!1,text:a.message,alertInfo:a.alertInfo},t.errorLinkState=!0)}).catch(function(e){t.formLoading=!1})})},getLeftList:function(){var t=this;this.leftList=[],this.$nextTick(function(){t.detailLeftLoading=!0,t.$axios.post(t.$baseUrl+"/dataSource/getDataBase.do",t.$qs.stringify({data_source_id:t.editId})).then(function(e){t.detailLeftLoading=!1;var a=e.data;"true"==a.success?t.leftList=a.data:layer.msg(a.message,{icon:5})}).catch(function(e){t.detailLeftLoading=!1})})},titleClick:function(t,e){for(var a="",i=0;i<this.leftList.length;i++)if(t===this.leftList[i].id){a=i;break}this.getChildren(a)},getChildren:function(t){var e=this;this.$nextTick(function(){e.detailLeftLoading=!0,e.$axios.post(e.$baseUrl+"/dataSource/getTablesByDatabase.do",e.$qs.stringify({data_source_id:e.editId,database:e.leftList[t].id})).then(function(a){e.detailLeftLoading=!1;var i=a.data;"true"==i.success?(e.leftList[t].child=i.data,0==i.data.length&&(e.currentNode={},e.fieldTableData=[],console.log($(".left-wapper li.el-menu-item.is-active").css("color")),e.$nextTick(function(){$(".left-wapper li.el-menu-item.is-active").css("color","rgb(192,203,217)")}))):layer.msg(i.message,{icon:5})}).catch(function(t){e.detailLeftLoading=!1})})},childClick:function(t){var e=this;this.currentNode=t,this.$nextTick(function(){e.detailRightLoading=!0,e.$axios.post(e.$baseUrl+"/dataSource/getTableFields.do",e.$qs.stringify({data_source_id:e.editId,database:t.database,table:t.id})).then(function(t){e.detailRightLoading=!1;var a=t.data;"true"==a.success?e.fieldTableData=a.data:layer.msg(a.message,{icon:5})}).catch(function(t){e.detailRightLoading=!1})})},checkParam:function(){return""===this.form.data_source_name?(layer.msg("数据源名称不能为空",{icon:5}),!1):""===this.form.data_source_item_type?(layer.msg("数据源类型不能为空",{icon:5}),!1):""===this.form.data_source_ip?(layer.msg("IP不能为空",{icon:5}),!1):""===this.form.data_source_port?(layer.msg("端口不能为空",{icon:5}),!1):""===this.form.data_source_username?(layer.msg("用户名不能为空",{icon:5}),!1):""===this.form.data_source_password?(layer.msg("密码不能为空",{icon:5}),!1):"MySQL"!==this.form.data_source_item_type&&""===this.form.data_source_dbname?(layer.msg("数据库类型为"+this.form.data_source_item_type+"时，数据库/实例不能为空",{icon:5}),!1):!(this.form.data_source_sample_num<=0)||(layer.msg("样本数需大于0",{icon:5}),!1)},dataBtn:function(){this.tableDialogState=!0,this.getFieldData()},getFieldData:function(){var t=this;this.tableDialogHead=[],this.tableDialogData=[],this.$nextTick(function(){t.tableDialogHeight=document.body.clientHeight-370,t.dataDetailLoading=!0,t.$axios.post(t.$baseUrl+"/dataSourceMetadata/getDataPreview.do",t.$qs.stringify({database:t.currentNode.database,table:t.currentNode.id,data_source_id:t.editId})).then(function(e){t.dataDetailLoading=!1;var a=e.data;"true"==a.success?(t.tableDialogHead=a.data[0].fields,t.tableDialogData=a.data[0].data):layer.msg(a.message,{icon:5})}).catch(function(e){t.dataDetailLoading=!1})})}},components:{basetable:o.default}},c={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{directives:[{name:"loading",rawName:"v-loading",value:t.loading,expression:"loading"}],staticClass:"content-bg",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[a("div",{staticClass:"top-title"},[t._v("数据源管理\n            "),a("div",{staticClass:"btn-wapper"},[a("el-button",{attrs:{type:"primary",size:"mini",plain:""},on:{click:function(e){t.formState=!0}}},[t._v("添加")]),t._v(" "),a("el-button",{attrs:{title:"初始化选中项",type:"warning",size:"mini",plain:"",disabled:!(t.delectIds.length>0)},on:{click:t.initialize}},[t._v("初始化")]),t._v(" "),a("el-button",{attrs:{type:"success",plain:"",size:"mini",disabled:!(t.delectIds.length>0)},on:{click:t.discoveryData}},[t._v("敏感数据发现")]),t._v(" "),a("el-button",{attrs:{title:"删除选中项",type:"danger",size:"mini",plain:"",disabled:!(t.delectIds.length>0)},on:{click:t.remove}},[t._v("删除")]),t._v(" "),a("el-button",{attrs:{type:"success",size:"mini",plain:""},on:{click:t.refresh}},[t._v("刷新")])],1)]),t._v(" "),a("div",{staticClass:"table-wapper"},[a("basetable",{attrs:{selection:!0,"table-head":t.tableHead,"table-data":t.tableData,busName:t.tableBusName}})],1),t._v(" "),a("div",{staticClass:"table-page"},[a("span",[t._v("共检索到数据源 "),a("b",[t._v(t._s(t.allCounts))]),t._v(" 个")]),t._v(" "),a("el-pagination",{attrs:{background:"",layout:"prev, pager, next","current-page":t.c_page,"page-size":t.size,total:t.allCounts},on:{"current-change":t.handleCurrentChange,"update:currentPage":function(e){t.c_page=e},"update:current-page":function(e){t.c_page=e}}})],1),t._v(" "),a("el-dialog",{directives:[{name:"loading",rawName:"v-loading",value:t.formLoading,expression:"formLoading"}],attrs:{title:""===t.editId?"添加":"修改",visible:t.formState,"destroy-on-close":"",width:"500px","element-loading-background":"rgba(48, 62, 78, 0.5)","close-on-click-modal":t.falseB},on:{"update:visible":function(e){t.formState=e}}},[a("el-form",{attrs:{"label-width":"100px"}},[a("el-form-item",{attrs:{label:"数据源名称:"}},[a("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[t._v("*")]),t._v(" "),a("el-input",{staticClass:"item",staticStyle:{width:"90%"},attrs:{size:"mini",placeholder:""},model:{value:t.form.data_source_name,callback:function(e){t.$set(t.form,"data_source_name",e)},expression:"form.data_source_name"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"数据源类型:"}},[a("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[t._v("*")]),t._v(" "),a("el-select",{staticStyle:{width:"90%"},attrs:{size:"mini",placeholder:"请选择"},model:{value:t.form.data_source_item_type,callback:function(e){t.$set(t.form,"data_source_item_type",e)},expression:"form.data_source_item_type"}},t._l(t.childrenType,function(t){return a("el-option",{key:t.value,attrs:{label:t.label,value:t.value}})}),1)],1),t._v(" "),a("el-form-item",{attrs:{label:"IP地址:"}},[a("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[t._v("*")]),t._v(" "),a("el-input",{staticClass:"item",staticStyle:{width:"90%"},attrs:{size:"mini",placeholder:""},model:{value:t.form.data_source_ip,callback:function(e){t.$set(t.form,"data_source_ip",e)},expression:"form.data_source_ip"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"端口:"}},[a("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[t._v("*")]),t._v(" "),a("el-input",{staticClass:"item",staticStyle:{width:"90%"},attrs:{size:"mini",placeholder:""},model:{value:t.form.data_source_port,callback:function(e){t.$set(t.form,"data_source_port",e)},expression:"form.data_source_port"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"用户名:"}},[a("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[t._v("*")]),t._v(" "),a("el-input",{staticClass:"item",staticStyle:{width:"90%"},attrs:{size:"mini",placeholder:""},model:{value:t.form.data_source_username,callback:function(e){t.$set(t.form,"data_source_username",e)},expression:"form.data_source_username"}}),t._v(" "),a("el-input",{staticClass:"item",staticStyle:{width:"0",height:"0",overflow:"hidden"},attrs:{size:"mini"}})],1),t._v(" "),t.passwordState&&""!=t.form.data_source_password?t._e():a("el-form-item",{attrs:{label:"密码:"}},[a("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[t._v("*")]),t._v(" "),a("el-input",{staticClass:"item",staticStyle:{width:"0",height:"0",overflow:"hidden"},attrs:{type:"password","auto-complete":"new-password",size:"mini"}}),t._v(" "),a("el-input",{staticClass:"item",staticStyle:{width:"90%",position:"relative",left:"-3px"},attrs:{type:"password","auto-complete":"new-password",size:"mini"},on:{blur:t.passwordBlur},model:{value:t.form.data_source_password,callback:function(e){t.$set(t.form,"data_source_password",e)},expression:"form.data_source_password"}})],1),t._v(" "),""!==t.form.data_source_password&&t.passwordState?a("el-form-item",{attrs:{label:"密码:"}},[a("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[t._v("*")]),t._v(" "),a("el-input",{staticClass:"item",staticStyle:{width:"0",height:"0",overflow:"hidden"},attrs:{type:"password","auto-complete":"new-password",size:"mini"}}),t._v(" "),a("el-input",{staticClass:"item",staticStyle:{width:"90%",position:"relative",left:"-3px"},attrs:{type:"password","auto-complete":"new-password",value:"11111111111111111",size:"mini",placeholder:""},on:{focus:t.passwordFocus}})],1):t._e(),t._v(" "),a("el-form-item",{attrs:{label:"数据库/实例:"}},["MySQL"!==t.form.data_source_item_type?a("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[t._v("*")]):t._e(),t._v(" "),a("el-input",{staticClass:"item",staticStyle:{width:"90%"},attrs:{size:"mini",placeholder:""},model:{value:t.form.data_source_dbname,callback:function(e){t.$set(t.form,"data_source_dbname",e)},expression:"form.data_source_dbname"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"样本数:"}},[a("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[t._v("*")]),t._v(" "),a("el-input",{staticClass:"item",staticStyle:{width:"90%"},attrs:{min:"1",type:"number",size:"mini"},model:{value:t.form.data_source_sample_num,callback:function(e){t.$set(t.form,"data_source_sample_num",e)},expression:"form.data_source_sample_num"}})],1)],1),t._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("div",{staticStyle:{float:"left"}},[a("el-button",{attrs:{type:"primary",plain:""},on:{click:t.testLink}},[t._v("测试连接")]),t._v(" "),t.linkObj.state?a("span",{staticStyle:{"font-size":"14px",color:"#1ab394"}},[t._v(t._s(t.linkObj.text))]):a("span",{staticStyle:{"font-size":"14px",color:"#e4956d"}},[t._v(t._s(t.linkObj.text)+"\n                        "),a("span",{staticStyle:{cursor:"pointer"},on:{click:function(e){t.errorLinkState=!0}}},[t._v("(详情)")])])],1),t._v(" "),a("el-button",{on:{click:function(e){t.formState=!1}}},[t._v("取消")]),t._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:function(e){""===t.editId?t.addDataSource():t.reviseDataSource()}}},[t._v("确 定")])],1)],1),t._v(" "),a("el-dialog",{directives:[{name:"loading",rawName:"v-loading",value:t.detailLoading,expression:"detailLoading"}],attrs:{title:"数据库详情",visible:t.detailState,width:"80vw",height:"80vh","destroy-on-close":"","element-loading-background":"rgba(48, 62, 78, 0.5)","close-on-click-modal":t.falseB},on:{"update:visible":function(e){t.detailState=e}}},[a("div",{staticClass:"detail-wapper"},[a("div",{directives:[{name:"loading",rawName:"v-loading",value:t.detailLeftLoading,expression:"detailLeftLoading"}],staticClass:"left-wapper",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[a("el-menu",{staticClass:"sidebar-el-menu",attrs:{"default-active":"1","background-color":"#405267","text-color":"#bfcbd9","active-text-color":"#20a0ff"},on:{open:t.titleClick}},[t._l(t.leftList,function(e,i){return["true"===e.isExpand?a("el-submenu",{key:i,attrs:{index:e.id}},[a("template",{slot:"title"},[a("span",{staticClass:"overflowClass",attrs:{slot:"title",title:e.label},slot:"title"},[t._v(t._s(e.label))])]),t._v(" "),t._l(e.child,function(e){return[a("el-menu-item",{key:e.id,staticStyle:{"font-size":"10px"},attrs:{index:e.id,title:e.label},on:{click:function(a){return t.childClick(e)}}},[t._v("\n                                        "+t._s(e.label)+"\n                                    ")])]})],2):a("el-menu-item",{key:e.id,attrs:{index:e.id,title:e.label},on:{click:function(a){return t.childClick(e)}}},[a("span",{attrs:{slot:"title"},slot:"title"},[t._v(t._s(e.label))])])]})],2)],1),t._v(" "),a("div",{directives:[{name:"loading",rawName:"v-loading",value:t.detailRightLoading,expression:"detailRightLoading"}],staticClass:"right-wapper",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[a("div",{staticClass:"table-tit-wapper"},[a("h3",[t._v(t._s(t.currentNode.label))]),t._v(" "),"{}"!==JSON.stringify(this.currentNode)?a("el-button",{staticStyle:{float:"right",margin:"10px"},attrs:{type:"primary",size:"mini",plain:""},on:{click:function(e){return t.dataBtn()}}},[t._v("数据预览")]):t._e()],1),t._v(" "),a("basetable",{attrs:{height:this.tableHeight,"table-head":t.fieldTableHead,"table-data":t.fieldTableData}})],1)])]),t._v(" "),a("el-dialog",{directives:[{name:"loading",rawName:"v-loading",value:t.dataDetailLoading,expression:"dataDetailLoading"}],attrs:{title:"数据预览",visible:t.tableDialogState,width:"85vw",height:"80vh","destroy-on-close":"","element-loading-background":"rgba(48, 62, 78, 0.5)","close-on-click-modal":t.falseB},on:{"update:visible":function(e){t.tableDialogState=e}}},[a("div",{staticClass:"detail-wapper2"},[a("basetable",{staticStyle:{width:"100%"},attrs:{tableHead:t.tableDialogHead,tableData:t.tableDialogData,height:t.tableDialogHeight}})],1)]),t._v(" "),a("el-dialog",{staticClass:"error-wapper",attrs:{title:"失败信息",visible:t.errorLinkState,width:"350px",top:"40vh","destroy-on-close":"","close-on-click-modal":t.falseB},on:{"update:visible":function(e){t.errorLinkState=e}}},[a("div",{staticStyle:{color:"#f56c6c"}},[t._v(t._s(t.linkObj.alertInfo))])])],1)},staticRenderFns:[]};var d=a("VU/8")(r,c,!1,function(t){a("3fQY")},"data-v-67c468dc",null);e.default=d.exports}});