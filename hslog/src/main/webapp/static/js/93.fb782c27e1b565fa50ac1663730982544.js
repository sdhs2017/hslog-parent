webpackJsonp([93],{"9p84":function(t,a){},H6l1:function(t,a,e){"use strict";Object.defineProperty(a,"__esModule",{value:!0});var n={name:"actionManage",data:function(){var t=this;return{loading:!1,actionForm:!1,action:{id:"",name:"",state:"1"},tableHead:[{prop:"name",label:"动作名称",width:"150"},{prop:"userName",label:"创建人",width:"100"},{prop:"type",label:"日志类型",width:"100"},{prop:"feature",label:"关键字",width:""},{prop:"state",label:"是否启用",width:"125",formatData:function(t){return"1"==t?"是":"否"}},{prop:"tools",label:"操作",width:"80",btns:[{icon:"el-icon-edit",text:"修改",clickFun:function(a,e){t.reviseBtn(a,e)}},{icon:"el-icon-circle-close",text:"删除",clickFun:function(a,e){t.removeAction(a,e)}}]}],tableData:[]}},created:function(){this.getActionData()},watch:{actionForm:function(){this.actionForm||(this.action={id:"",name:"",state:"1"})}},methods:{getActionData:function(){var t=this;this.loading=!0,this.$nextTick(function(){t.$axios.post(t.$baseUrl+"/action/selectAll.do","").then(function(a){t.loading=!1,t.tableData=a.data[0]}).catch(function(a){console.log(a),t.loading=!1})})},reviseBtn:function(t){this.action={id:t.id,name:t.name,state:Number(t.state)},console.log(t),this.actionForm=!0},reviseAction:function(){var t=this;""===this.action.name?layer.msg("动作名称不符合",{icon:5}):this.$nextTick(function(){layer.load(1),t.$axios.post(t.$baseUrl+"/action/updateById.do",t.$qs.stringify(t.action)).then(function(a){layer.closeAll("loading"),"true"==a.data.success?(layer.msg(a.data.message,{icon:1}),t.actionForm=!1,t.getActionData()):"false"==a.data.success&&layer.msg(a.data.message,{icon:5})}).catch(function(t){layer.closeAll("loading")})})},removeAction:function(t){var a=this;layer.confirm("确定删除？",{btn:["确定","取消"]},function(e){layer.close(e),a.$nextTick(function(){layer.load(1),a.$axios.post(a.$baseUrl+"/action/delete.do",a.$qs.stringify({id:t.id})).then(function(t){layer.closeAll("loading"),"true"==t.data.success?(layer.msg(t.data.message,{icon:1}),a.getActionData()):"false"==t.data.success&&layer.msg(t.data.message,{icon:5})}).catch(function(t){layer.closeAll("loading")})})},function(){layer.close()})}},components:{vBasetable:e("qTy0").default}},i={render:function(){var t=this,a=t.$createElement,e=t._self._c||a;return e("div",{directives:[{name:"loading",rawName:"v-loading",value:t.loading,expression:"loading"}],staticClass:"content-bg",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.2)"}},[e("div",{staticClass:"top-title"},[t._v("动作列表")]),t._v(" "),e("div",{staticClass:"action-table-wapper"},[e("v-basetable",{attrs:{tableHead:t.tableHead,tableData:t.tableData}})],1),t._v(" "),e("el-dialog",{attrs:{title:"修改动作",visible:t.actionForm,width:"340px"},on:{"update:visible":function(a){t.actionForm=a}}},[e("el-form",{attrs:{"label-width":"80px"}},[e("el-form-item",{attrs:{label:"动作名称:"}},[e("el-input",{staticClass:"item",attrs:{placeholder:"名称"},model:{value:t.action.name,callback:function(a){t.$set(t.action,"name",a)},expression:"action.name"}})],1),t._v(" "),e("el-form-item",{attrs:{label:"是否启用:"}},[e("el-radio-group",{model:{value:t.action.state,callback:function(a){t.$set(t.action,"state",a)},expression:"action.state"}},[e("el-radio",{attrs:{label:1}},[t._v("是")]),t._v(" "),e("el-radio",{attrs:{label:0}},[t._v("否")])],1)],1)],1),t._v(" "),e("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[e("el-button",{attrs:{type:"primary"},on:{click:function(a){return t.reviseAction()}}},[t._v("确 定")]),t._v(" "),e("el-button",{on:{click:function(a){t.actionForm=!1}}},[t._v("取 消")])],1)],1)],1)},staticRenderFns:[]};var o=e("VU/8")(n,i,!1,function(t){e("9p84")},"data-v-278ada5e",null);a.default=o.exports}});