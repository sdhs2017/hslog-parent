webpackJsonp([64],{TivN:function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=a("mvHQ"),l=a.n(i),o=a("62NR"),n=a("ID1p"),r=a("/sA3"),s=a("8+FI"),c={name:"fileLogManage",data:function(){var t=this;return{loading:!1,leftLoading:!1,rightLoading:!1,editForm:!1,commitState:!1,leftData:[],currentLeftItem:{},compareObj:{},tableHeight:0,sourceTableHeight:0,activeName:"0",tableHead:[{prop:"file_log_field",label:"字段代码",width:""},{prop:"file_log_text",label:"字段名称",width:""},{prop:"file_log_type",label:"字段类型",width:""},{prop:"file_log_format",label:"日期格式",width:""},{prop:"file_log_is_timestamp",label:"设置为日期字段",width:"",formatData:function(t){return"true"==t?"是":"否"}},{prop:"tools",label:"操作",width:"60",btns:[{icon:"el-icon-edit",text:"修改",btnType:"editDetails",formatData:function(t){return 0!==t.file_log_order},clickFun:function(e,a){t.currentRightItem.index=a,t.currentRightItem.data=JSON.parse(l()(e)),t.editForm=!0}}]}],tableData:[],searchConditions:{},fromConditionsArr:[],busName:"fileLogManageSearch",size:15,c_page:1,allCounts:0,sourceHead:[],sourceData:[],currentRightItem:{index:0,data:{}},typeOpt:[{value:"text",label:"text"},{value:"keyword",label:"keyword"},{value:"ip",label:"ip"},{value:"long",label:"long"},{value:"date",label:"date"}],nameChangeState:!1,tableChangeState:!1}},watch:{tableData:{handler:function(t,e){},deep:!0}},created:function(){var t=this;this.tableHeight=document.body.clientHeight-375,this.sourceTableHeight=document.body.clientHeight-450,window.onresize=function(){t.tableHeight=document.body.clientHeight-375,t.sourceTableHeight=document.body.clientHeight-450},this.getList();var e=Object(r.e)("yyyy-mm-dd HH:MM:SS",new Date),a=new Date;a.setTime(a.getTime()-864e5*this.$store.state.beforeDay),a=Object(r.e)("yyyy-mm-dd HH:MM:SS",a),this.searchConditions={endtime:e,starttime:a},this.formConditionsArr=[{label:"时间范围",type:"datetimerange",itemType:"",paramName:"time",model:{model:[a,e]},val:""}],s.default.$on(this.busName,function(e){t.searchConditions=e,t.getSourceData(1),t.c_page=1})},methods:{changeInput:function(t){t!==this.compareObj.file_log_templateName?this.commitState=!0:this.commitState=!1},getList:function(){var t=this;this.$nextTick(function(){t.leftLoading=!0,t.$axios.post(t.$baseUrl+"/fileLog/getFileLogTemplateList.do","").then(function(e){t.leftLoading=!1;var a=e.data;"true"==a.success?(t.leftData=a.data,t.leftData.length>0&&(t.getTableData(t.leftData[0].file_log_templateKey),t.currentLeftItem=JSON.parse(l()(t.leftData[0])),t.compareObj=JSON.parse(l()(t.leftData[0])))):layer.msg(a.message,{icon:5})}).catch(function(e){t.leftLoading=!1})})},getTableData:function(t){var e=this;this.$nextTick(function(){e.rightLoading=!0,e.$axios.post(e.$baseUrl+"/fileLog/getFileLogTemplateFields.do",e.$qs.stringify({file_log_templateKey:t})).then(function(t){e.rightLoading=!1;var a=t.data;"true"==a.success?e.tableData=a.data:layer.msg(a.message,{icon:5})}).catch(function(t){e.rightLoading=!1})})},clickLeftItem:function(t){var e=this;this.commitState?layer.confirm("当前表格未保存，继续，修改的数据将会丢失,确定继续么？",{btn:["确定","取消"]},function(a){e.activeName="0",e.currentLeftItem=JSON.parse(l()(t)),e.compareObj=JSON.parse(l()(t)),e.commitState=!1,e.getTableData(e.currentLeftItem.file_log_templateKey),e.sourceHead=[],e.sourceData=[],layer.close(a)},function(){layer.close()}):(this.activeName="0",this.compareObj=JSON.parse(l()(t)),this.currentLeftItem=JSON.parse(l()(t)),this.commitState=!1,this.sourceHead=[],this.sourceData=[],this.getTableData(this.currentLeftItem.file_log_templateKey))},handleClick:function(t,e){this.getSourceHead(this.currentLeftItem.file_log_templateKey),this.getSourceData(1)},getSourceHead:function(t){var e=this;this.c_page=1,this.$nextTick(function(){e.rightLoading=!0,e.$axios.post(e.$baseUrl+"/fileLog/getTemplateFields.do",e.$qs.stringify({file_log_templateKey:t})).then(function(t){e.rightLoading=!1;var a=t.data;"true"==a.success?e.sourceHead=a.data:layer.msg(a.message,{icon:5})}).catch(function(t){e.rightLoading=!1})})},getSourceData:function(t){var e=this;this.$nextTick(function(){e.rightLoading=!0,e.$axios.post(e.$baseUrl+"/fileLog/getTemplateData.do",e.$qs.stringify({file_log_templateKey:e.currentLeftItem.file_log_templateKey,starttime:e.searchConditions.starttime,endtime:e.searchConditions.endtime,page:t,size:e.size})).then(function(t){e.rightLoading=!1;var a=t.data;"true"==a.success?(e.allCounts=a.data[0].count,e.sourceData=a.data[0].list):layer.msg(a.message,{icon:5})}).catch(function(t){e.rightLoading=!1})})},handleCurrentChange:function(t){this.getSourceData(t)},typeChange:function(){this.currentRightItem.data.file_log_format="",this.currentRightItem.data.file_log_is_timestamp="false"},determine:function(){var t=this.currentRightItem.index,e=this.currentRightItem.data;if(""===e.file_log_field)layer.msg("字段代码不能为空",{icon:5});else if(/^[a-zA-Z0-9]+$/.test(e.file_log_field))if(""===e.file_log_text)layer.msg("字段名称不能为空",{icon:5});else if("date"===e.file_log_type&&""===e.file_log_format)layer.msg("字段类型为date时，format不能为空",{icon:5});else{for(var a in e)this.tableData[t][a]=e[a];if("true"==e.file_log_is_timestamp){console.log(e.file_log_is_timestamp);for(var i=0;i<this.tableData.length;i++)i!==t&&(this.tableData[i].file_log_is_timestamp="false")}this.editForm=!1,this.commitState=!0,this.tableChangeState=!0}else layer.msg("字段代码只允许为数字/英文",{icon:5})},saveData:function(){var t=this;""!==this.currentLeftItem.file_log_templateName?this.$nextTick(function(){t.loading=!0,t.$axios.post(t.$baseUrl+"/fileLog/updateTemplateInfo.do",t.$qs.stringify({file_log_templateName:t.currentLeftItem.file_log_templateName,file_log_templateKey:t.currentLeftItem.file_log_templateKey,file_log_fields:l()(t.tableData),updateState:t.tableChangeState?2:1})).then(function(e){t.loading=!1;var a=e.data;if("true"==a.success){for(var i in layer.msg(a.message,{icon:1}),t.leftData)if(t.currentLeftItem.file_log_templateKey===t.leftData[i].file_log_templateKey){t.leftData[i].file_log_templateName=t.currentLeftItem.file_log_templateName;break}t.compareObj=JSON.parse(l()(t.currentLeftItem)),t.commitState=!1,t.tableChangeState=!1}else layer.msg(a.message,{icon:5})}).catch(function(e){t.loading=!1})}):layer.msg("名称不能为空",{icon:5})}},components:{vBasetable2:o.default,vSearchForm:n.default}},g={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{directives:[{name:"loading",rawName:"v-loading",value:t.loading,expression:"loading"}],staticClass:"content-bg",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[a("div",{staticClass:"top-title"},[t._v("文件类日志")]),t._v(" "),a("div",{staticClass:"content-wapper"},[a("div",{directives:[{name:"loading",rawName:"v-loading",value:t.leftLoading,expression:"leftLoading"}],staticClass:"left-wapper",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[a("div",{staticClass:"left-tit"},[t._v("模板")]),t._v(" "),a("div",{staticStyle:{"overflow-y":"auto",height:"calc(100% - 60px)"}},t._l(t.leftData,function(e,i){return a("div",{key:i,class:e.file_log_templateKey===t.currentLeftItem.file_log_templateKey?"left-item chooseClass":"left-item",on:{click:function(a){return t.clickLeftItem(e)}}},[t._v(t._s(e.file_log_templateName))])}),0)]),t._v(" "),a("div",{directives:[{name:"loading",rawName:"v-loading",value:t.rightLoading,expression:"rightLoading"}],staticClass:"right-wapper",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[a("div",{staticClass:"right-tit"},[a("el-input",{staticClass:"tit-input",attrs:{size:"large"},on:{input:t.changeInput},model:{value:t.currentLeftItem.file_log_templateName,callback:function(e){t.$set(t.currentLeftItem,"file_log_templateName",e)},expression:"currentLeftItem.file_log_templateName"}}),t._v(" "),a("div",{staticClass:"btn-wapper"},[a("el-button",{attrs:{type:"warning",disabled:!t.commitState&&"disabled"},on:{click:function(e){return t.saveData()}}},[t._v("更新")])],1)],1),t._v(" "),a("el-tabs",{attrs:{type:"border-card"},on:{"tab-click":t.handleClick},model:{value:t.activeName,callback:function(e){t.activeName=e},expression:"activeName"}},[a("el-tab-pane",{attrs:{label:"配置",name:"0"}},[a("vBasetable2",{attrs:{tableHead:t.tableHead,tableData:t.tableData,height:t.tableHeight}})],1),t._v(" "),a("el-tab-pane",{attrs:{label:"源数据",name:"1"}},[a("div",{staticClass:"search-wapper"},[a("v-search-form",{staticStyle:{float:"right"},attrs:{formItem:t.formConditionsArr,busName:t.busName}})],1),t._v(" "),a("vBasetable2",{attrs:{tableHead:t.sourceHead,tableData:t.sourceData,height:t.sourceTableHeight}}),t._v(" "),a("div",{staticClass:"table-page"},[a("span",[t._v("数据条数为 "),a("b",[t._v(t._s(t.allCounts))]),t._v(" 条")]),t._v(" "),a("el-pagination",{attrs:{background:"",layout:"prev, pager, next","current-page":t.c_page,"page-size":t.size,total:t.allCounts},on:{"current-change":t.handleCurrentChange,"update:currentPage":function(e){t.c_page=e},"update:current-page":function(e){t.c_page=e}}})],1)],1)],1)],1)]),t._v(" "),a("el-dialog",{attrs:{title:"编辑",visible:t.editForm,width:"440px"},on:{"update:visible":function(e){t.editForm=e}}},[a("el-form",{attrs:{"label-width":"110px"}},[a("el-form-item",{attrs:{label:"字段代码:"}},[a("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[t._v("*")]),t._v(" "),a("el-input",{staticClass:"item",model:{value:t.currentRightItem.data.file_log_field,callback:function(e){t.$set(t.currentRightItem.data,"file_log_field",e)},expression:"currentRightItem.data.file_log_field"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"字段名称:"}},[a("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[t._v("*")]),t._v(" "),a("el-input",{staticClass:"item",model:{value:t.currentRightItem.data.file_log_text,callback:function(e){t.$set(t.currentRightItem.data,"file_log_text",e)},expression:"currentRightItem.data.file_log_text"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"字段类型:"}},[a("span",{staticStyle:{color:"red",position:"absolute",left:"-10px"}},[t._v("*")]),t._v(" "),a("el-select",{staticStyle:{width:"100%"},attrs:{placeholder:"请选择"},on:{change:t.typeChange},model:{value:t.currentRightItem.data.file_log_type,callback:function(e){t.$set(t.currentRightItem.data,"file_log_type",e)},expression:"currentRightItem.data.file_log_type"}},t._l(t.typeOpt,function(t){return a("el-option",{key:t.value,attrs:{label:t.label,value:t.value}})}),1)],1),t._v(" "),a("el-form-item",{attrs:{label:"日期格式:"}},[a("el-input",{staticClass:"item",attrs:{disabled:"date"!==t.currentRightItem.data.file_log_type&&"disabled"},model:{value:t.currentRightItem.data.file_log_format,callback:function(e){t.$set(t.currentRightItem.data,"file_log_format",e)},expression:"currentRightItem.data.file_log_format"}}),t._v(" "),a("span",{staticStyle:{color:"#8a6226"}},[t._v("只在字段类型为date时生效，且不能为空")])],1),t._v(" "),a("el-form-item",{staticStyle:{position:"relative",top:"-10px"},attrs:{label:"设置为日期字段:"}},[a("el-radio-group",{attrs:{disabled:"date"!==t.currentRightItem.data.file_log_type&&"disabled"},model:{value:t.currentRightItem.data.file_log_is_timestamp,callback:function(e){t.$set(t.currentRightItem.data,"file_log_is_timestamp",e)},expression:"currentRightItem.data.file_log_is_timestamp"}},[a("el-radio",{attrs:{label:"true"}},[t._v("是")]),t._v(" "),a("el-radio",{attrs:{label:"false"}},[t._v("否")])],1),t._v(" "),a("p",{staticStyle:{color:"#8a6226",position:"relative",top:"-10px"}},[t._v("只在字段类型为date时生效，且只存在一个")])],1)],1),t._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{attrs:{type:"primary"},on:{click:t.determine}},[t._v("确 定")]),t._v(" "),a("el-button",{on:{click:function(e){t.editForm=!1}}},[t._v("取 消")])],1)],1)],1)},staticRenderFns:[]};var m=a("VU/8")(c,g,!1,function(t){a("nXjh")},"data-v-5b4dfdfe",null);e.default=m.exports},nXjh:function(t,e){}});