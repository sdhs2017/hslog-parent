webpackJsonp([93],{ObLP:function(e,a){},ar3I:function(e,a,t){"use strict";Object.defineProperty(a,"__esModule",{value:!0});var i=t("62NR"),o=t("ID1p"),l=t("8+FI"),n={name:"tabManage",data:function(){var e=this;return{falseB:!1,loading:!1,conditionsArr:[{label:"规则名称",paramName:"label_name",model:{model:""},itemType:"",type:"input"}],busName:"tabManageSearch",conditionFrom:{label_name:""},page:1,c_page:1,size:15,allCounts:0,tableBusName:{selectionName:"tabManageDelect"},delectIds:"",tableData:[],tableHead:[{prop:"label_name",label:"规则名称",width:""},{prop:"label_discover_way",label:"发现方式",width:"",formatData:function(a,t){var i="";return e.discoverWay.forEach(function(e){e.value==a&&(i=e.label)}),i}},{prop:"label_discover_percent",label:"确认比例(%)",width:""},{prop:"label_discover_regex",label:"正则表达式",width:""},{prop:"label_remark",label:"规则描述",width:""},{prop:"tools",label:"操作",width:"60",btns:[{icon:"el-icon-edit",text:"修改",clickFun:function(a,t){e.formState=!0,e.editId=a.label_id,e.getTabInfo()}}]}],editId:"",formState:!1,formLoading:!1,form:{label_name:"",label_discover_way:1,label_discover_percent:"",label_discover_regex:"",label_remark:""},discoverWay:[{value:"1",label:"按正则发现"},{value:"0",label:"无规则"}]}},watch:{formState:function(e,a){e||(this.form={label_name:"",label_discover_way:1,label_discover_percent:"",label_discover_regex:"",label_remark:""},this.editId="")}},created:function(){var e=this;this.getDataList(1,this.conditionFrom),this.getDiscoverWay(),l.default.$on(this.busName,function(a){e.conditionFrom=a,e.getDataList(1,e.conditionFrom)}),l.default.$on(this.tableBusName.selectionName,function(a){for(var t in e.delectIds="",a)e.delectIds+=a[t].label_id+","}),this.tableHeight=document.body.clientHeight-402,window.onresize=function(){var a=document.body.clientHeight-402;e.tableHeight=a<320?320:a;var t=document.body.clientHeight-370;e.tableDialogHeight=t<320?320:t}},methods:{getDiscoverWay:function(){var e=this;this.$nextTick(function(){e.loading=!0,e.$axios.post(e.$baseUrl+"/DSGLabel/getLabelDiscoverWay.do",e.$qs.stringify()).then(function(a){e.loading=!1;var t=a.data;"true"==t.success?e.discoverWay=t.data:layer.msg(t.message,{icon:5})}).catch(function(a){e.loading=!1})})},discoverWayChange:function(){this.form.label_discover_percent="",this.form.label_discover_regex=""},refresh:function(){this.getDataList(1,this.conditionFrom)},getDataList:function(e,a){var t=this;this.c_page=e;var i=a;i.pageIndex=e,i.pageSize=this.size,this.$nextTick(function(){t.loading=!0,t.$axios.post(t.$baseUrl+"/DSGLabel/searchLabel.do",t.$qs.stringify(i)).then(function(e){t.loading=!1;var a=e.data;"true"==a.success?(t.allCounts=Number(a.data[0].count),t.tableData=a.data[0].list):layer.msg(a.message,{icon:5})}).catch(function(e){t.loading=!1})})},handleCurrentChange:function(e){this.getDataList(e,this.conditionFrom)},getTabInfo:function(){var e=this;this.$nextTick(function(){e.loading=!0,e.$axios.post(e.$baseUrl+"/DSGLabel/getLabelInfoById.do",e.$qs.stringify({label_id:e.editId})).then(function(a){e.loading=!1;var t=a.data;"true"==t.success?e.form=t.data:layer.msg(t.message,{icon:5})}).catch(function(a){e.loading=!1})})},addTab:function(){var e=this;this.checkParam()&&this.$nextTick(function(){e.formLoading=!0,e.$axios.post(e.$baseUrl+"/DSGLabel/insertLabel.do",e.$qs.stringify(e.form)).then(function(a){e.formLoading=!1;var t=a.data;"true"==t.success?(layer.msg(t.message,{icon:1}),e.formState=!1,e.getDataList(1,{})):layer.msg(t.message,{icon:5})}).catch(function(a){e.formLoading=!1})})},reviseTab:function(){var e=this;this.checkParam()&&this.$nextTick(function(){e.formLoading=!0,e.$axios.post(e.$baseUrl+"/DSGLabel/updateLabel.do",e.$qs.stringify(e.form)).then(function(a){e.formLoading=!1;var t=a.data;"true"==t.success?(layer.msg(t.message,{icon:1}),e.formState=!1,e.editId="",e.getDataList(1,{})):layer.msg(t.message,{icon:5})}).catch(function(a){e.formLoading=!1})})},remove:function(){var e=this;layer.confirm("您确定删除么？",{btn:["确定","取消"]},function(a){layer.close(a),e.$nextTick(function(){e.loading=!0,e.$axios.post(e.$baseUrl+"/DSGLabel/deleteLabel.do",e.$qs.stringify({ids:e.delectIds})).then(function(a){e.loading=!1;var t=a.data;"true"==t.success?(layer.msg(t.message,{icon:1}),e.getDataList(1,e.conditionFrom),e.delectIds=""):layer.msg(t.message,{icon:5})}).catch(function(a){e.loading=!1})})},function(){layer.close()})},testLink:function(){var e=this;this.checkParam()&&this.$nextTick(function(){e.formLoading=!0,e.$axios.post(e.$baseUrl+"/dataSource/testConnection.do",e.$qs.stringify(e.form)).then(function(a){e.formLoading=!1;var t=a.data;"true"==t.success?e.linkObj={state:!0,text:t.message}:(e.linkObj={state:!1,text:t.message,alertInfo:t.alertInfo},e.errorLinkState=!0)}).catch(function(a){e.formLoading=!1})})},checkParam:function(){return""===this.form.label_name?(layer.msg("名称不能为空",{icon:5}),!1):1===this.form.label_discover_way&&""===this.form.label_discover_regex?(layer.msg("发现方式为正则时，正则表达式不能为空",{icon:5}),!1):1===this.form.label_discover_way&&this.form.label_discover_percent>100?(layer.msg("确认比例值为1-100",{icon:5}),!1):!(1===this.form.label_discover_way&&this.form.label_discover_percent<1)||(layer.msg("确认比例值为1-100",{icon:5}),!1)}},components:{basetable:i.default,vSearchForm:o.default}},s={render:function(){var e=this,a=e.$createElement,t=e._self._c||a;return t("div",{directives:[{name:"loading",rawName:"v-loading",value:e.loading,expression:"loading"}],staticClass:"content-bg",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[t("div",{staticClass:"top-title"},[e._v("发现规则列表\n        "),t("div",{staticClass:"btn-wapper"},[t("el-button",{attrs:{type:"primary",size:"mini",plain:""},on:{click:function(a){e.formState=!0}}},[e._v("添加")]),e._v(" "),t("el-button",{attrs:{title:"删除选中项",type:"danger",size:"mini",plain:"",disabled:!(e.delectIds.length>0)},on:{click:e.remove}},[e._v("删除")]),e._v(" "),t("el-button",{attrs:{type:"success",size:"mini",plain:""},on:{click:e.refresh}},[e._v("刷新")])],1)]),e._v(" "),t("div",{staticClass:"search-wapper"},[t("v-search-form",{attrs:{formItem:e.conditionsArr,busName:e.busName}})],1),e._v(" "),t("div",{staticClass:"table-wapper"},[t("basetable",{attrs:{selection:!0,"table-head":e.tableHead,"table-data":e.tableData,busName:e.tableBusName}})],1),e._v(" "),t("div",{staticClass:"table-page"},[t("span",[e._v("共检索到规则 "),t("b",[e._v(e._s(e.allCounts))]),e._v(" 个")]),e._v(" "),t("el-pagination",{attrs:{background:"",layout:"prev, pager, next","current-page":e.c_page,"page-size":e.size,total:e.allCounts},on:{"current-change":e.handleCurrentChange,"update:currentPage":function(a){e.c_page=a},"update:current-page":function(a){e.c_page=a}}})],1),e._v(" "),t("el-dialog",{directives:[{name:"loading",rawName:"v-loading",value:e.formLoading,expression:"formLoading"}],attrs:{title:""===e.editId?"添加":"修改",visible:e.formState,"destroy-on-close":"",width:"500px","element-loading-background":"rgba(48, 62, 78, 0.5)","close-on-click-modal":e.falseB},on:{"update:visible":function(a){e.formState=a}}},[t("el-form",{attrs:{"label-width":"100px"}},[t("el-form-item",{attrs:{label:"规则名称:"}},[t("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[e._v("*")]),e._v(" "),t("el-input",{staticClass:"item",attrs:{size:"mini"},model:{value:e.form.label_name,callback:function(a){e.$set(e.form,"label_name",a)},expression:"form.label_name"}})],1),e._v(" "),t("el-form-item",{attrs:{label:"发现方式:"}},[t("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[e._v("*")]),e._v(" "),t("el-select",{staticStyle:{width:"100%"},attrs:{size:"mini",clearable:"",placeholder:"请选择"},on:{change:e.discoverWayChange},model:{value:e.form.label_discover_way,callback:function(a){e.$set(e.form,"label_discover_way",a)},expression:"form.label_discover_way"}},e._l(e.discoverWay,function(e){return t("el-option",{key:e.value,attrs:{label:e.label,value:e.value}})}),1)],1),e._v(" "),"1"==e.form.label_discover_way?t("el-form-item",{attrs:{label:"确认比例(%):"}},[t("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[e._v("*")]),e._v(" "),t("el-input",{staticClass:"item",attrs:{size:"mini",type:"number",min:"1",max:"100",maxlength:"3",placeholder:"1-100"},model:{value:e.form.label_discover_percent,callback:function(a){e.$set(e.form,"label_discover_percent",a)},expression:"form.label_discover_percent"}})],1):e._e(),e._v(" "),"1"==e.form.label_discover_way?t("el-form-item",{attrs:{label:"正则表达式:"}},[t("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[e._v("*")]),e._v(" "),t("el-input",{attrs:{type:"textarea",autosize:{minRows:4,maxRows:10}},model:{value:e.form.label_discover_regex,callback:function(a){e.$set(e.form,"label_discover_regex",a)},expression:"form.label_discover_regex"}})],1):e._e(),e._v(" "),t("el-form-item",{attrs:{label:"规则描述:"}},[t("el-input",{attrs:{type:"textarea",autosize:{minRows:4,maxRows:10}},model:{value:e.form.label_remark,callback:function(a){e.$set(e.form,"label_remark",a)},expression:"form.label_remark"}})],1)],1),e._v(" "),t("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[t("el-button",{on:{click:function(a){e.formState=!1}}},[e._v("取消")]),e._v(" "),t("el-button",{attrs:{type:"primary"},on:{click:function(a){""===e.editId?e.addTab():e.reviseTab()}}},[e._v("确 定")])],1)],1)],1)},staticRenderFns:[]};var r=t("VU/8")(n,s,!1,function(e){t("ObLP")},"data-v-278d3e1d",null);a.default=r.exports}});