webpackJsonp([25],{ZEPR:function(e,t){},uhnX:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var i=a("ID1p"),o=a("qTy0"),n=a("8+FI"),s={name:"serviceList",data:function(){var e=this;return{loading:!1,busNames:{searchBusName:"serviceListSearch",selectionName:"serviceListSelection",reviseServiceListName:"serviceListRevise"},searchConditions:{name:"",ip:"",port:"",protocol:"",url:"",relativeUrl:"",state:"",complementState:""},delectEquipmentIds:"",complementStateArr:[{value:"",label:""},{value:"0",label:"未补全"},{value:"1",label:"已补全"}],stateArr:[{value:"",label:""},{value:"0",label:"否"},{value:"1",label:"是"}],tableHead:[{prop:"name",label:"名称",width:"80"},{prop:"ip",label:"IP",width:"120"},{prop:"port",label:"端口",width:"65"},{prop:"protocol",label:"协议",width:"65"},{prop:"url",label:"路径",width:""},{prop:"relativeUrl",label:"相对路径",width:""},{prop:"complementState",label:"补全状态",width:"75",formatData:function(e){return"1"==e?"已补全":'<span style="color:#e6a23c;">未补全</span>'}},{prop:"state",label:"是否启用",width:"75",formatData:function(e){return"1"==e?"是":"否"}},{prop:"describe",label:"描述",width:""},{prop:"createTime",label:"创建时间",width:""},{prop:"updateTime",label:"修改时间",width:""},{prop:"stopTime",label:"停用时间",width:""},{prop:"tools",label:"操作",width:"50",btns:[{icon:"el-icon-edit",text:"修改",btnType:"reviseService",clickFun:function(t){e.getServiceById(t.id)}}]}],tableData:[],formConditionsArr:[],allCounts:0,page:1,c_page:1,size:15,reviseWapper:!1,reviseCondition:{id:"",url:"",name:"",state:"",describe:"",complementState:""}}},created:function(){var e=this;this.formConditionsArr=[{label:"名称",paramName:"name",model:{model:""},itemType:"",type:"input"},{label:"IP",paramName:"ip",itemType:"",model:{model:""},type:"input"},{label:"端口",paramName:"port",model:{model:""},itemType:"",type:"input"},{label:"协议",paramName:"protocol",model:{model:""},itemType:"",type:"input"},{label:"路径",paramName:"url",model:{model:""},itemType:"",type:"input"},{label:"相对路径",paramName:"relativeUrl",model:{model:""},itemType:"",type:"input"},{label:"状态",paramName:"complementState",type:"select",itemType:"",model:{model:""},options:this.complementStateArr},{label:"启用",paramName:"state",type:"select",itemType:"",model:{model:""},options:this.stateArr}],this.getServiceListData(this.searchConditions,1),n.default.$on(this.busNames.searchBusName,function(t){e.searchConditions=t,e.getServiceListData(e.searchConditions,1),e.c_page=1}),n.default.$on(this.busNames.selectionName,function(t){for(var a in e.delectEquipmentIds="",t)e.delectEquipmentIds+=t[a].id+","})},beforeDestroy:function(){n.default.$off(this.busNames.searchBusName),n.default.$off(this.busNames.selectionName)},methods:{getServiceListData:function(e,t){var a=this;this.loading=!0;var i=e;i.pageIndex=t,i.pageSize=this.size,this.$nextTick(function(){a.$axios.post(a.$baseUrl+"/serviceInfo/selectPage.do",a.$qs.stringify(i)).then(function(e){a.loading=!1,a.tableData=e.data[0].serviceInfo,a.allCounts=JSON.parse(e.data[0].count.count)}).catch(function(e){a.loading=!1,console.log(e)})})},handleCurrentChange:function(e){this.getServiceListData(this.searchConditions,e)},removeService:function(){var e=this;""===this.delectEquipmentIds?layer.msg("未选中任何服务",{icon:5}):layer.confirm("您确定停用么？",{btn:["确定","取消"]},function(t){layer.close(t),e.$nextTick(function(){e.$axios.post(e.$baseUrl+"/serviceInfo/delete.do",e.$qs.stringify({id:e.delectEquipmentIds})).then(function(t){"true"==t.data.success?(layer.msg("停用成功",{icon:1}),e.getServiceListData(e.searchConditions,1),e.c_page=1):layer.msg("停用失败",{icon:5})}).catch(function(e){layer.msg("停用失败",{icon:5})})})},function(){layer.close()})},getServiceById:function(e){var t=this;this.$nextTick(function(){layer.load(1),t.$axios.post(t.$baseUrl+"/serviceInfo/selectServiceById.do",t.$qs.stringify({id:e})).then(function(e){layer.closeAll("loading"),t.reviseCondition.id=e.data.id,t.reviseCondition.url=e.data.url,t.reviseCondition.name=e.data.name,t.reviseCondition.state=e.data.state,t.reviseCondition.describe=e.data.describe,t.reviseCondition.complementState=e.data.complementState,t.reviseWapper=!0}).catch(function(e){layer.closeAll("loading"),console.log(e)})})},updateService:function(){var e=this;layer.load(1),this.$nextTick(function(){e.$axios.post(e.$baseUrl+"/serviceInfo/updateById.do",e.$qs.stringify(e.reviseCondition)).then(function(t){"true"==t.data.success?(layer.msg("修改成功",{icon:1}),e.getServiceListData(e.searchConditions,1),e.c_page=1,e.reviseWapper=!1):layer.msg("修改失败",{icon:5})}).catch(function(e){layer.msg("修改失败",{icon:5})})})}},components:{vSearchForm:i.default,vBasetable:o.default}},l={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"content-bg"},[a("div",{staticClass:"top-title"},[e._v("服务列表")]),e._v(" "),a("div",{staticClass:"search-wapper"},[a("v-search-form",{attrs:{formItem:e.formConditionsArr,busName:e.busNames.searchBusName}})],1),e._v(" "),a("div",{staticClass:"tools-wapper"},[a("el-button",{attrs:{type:"danger",size:"mini",plain:""},on:{click:e.removeService}},[e._v("停用")])],1),e._v(" "),a("div",{directives:[{name:"loading",rawName:"v-loading",value:e.loading,expression:"loading"}],staticClass:"table-wapper",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[a("v-basetable",{attrs:{selection:!0,tableHead:e.tableHead,tableData:e.tableData,busName:e.busNames}})],1),e._v(" "),a("div",{staticClass:"table-page"},[a("span",[e._v("共检索到资产数量为 "),a("b",[e._v(e._s(e.allCounts))]),e._v(" 台")]),e._v(" "),a("el-pagination",{attrs:{background:"",layout:"prev, pager, next","current-page":e.c_page,"page-size":e.size,total:e.allCounts},on:{"current-change":e.handleCurrentChange,"update:currentPage":function(t){e.c_page=t},"update:current-page":function(t){e.c_page=t}}})],1),e._v(" "),a("el-dialog",{attrs:{title:"修改服务列表",visible:e.reviseWapper,width:"440px"},on:{"update:visible":function(t){e.reviseWapper=t}}},[a("el-form",{attrs:{"label-width":"80px"}},[a("el-form-item",{attrs:{label:"路径:"}},[a("el-input",{attrs:{title:e.reviseCondition.url,disabled:""},model:{value:e.reviseCondition.url,callback:function(t){e.$set(e.reviseCondition,"url",t)},expression:"reviseCondition.url"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"名称:"}},[a("el-input",{model:{value:e.reviseCondition.name,callback:function(t){e.$set(e.reviseCondition,"name",t)},expression:"reviseCondition.name"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"是否启用:"}},[a("el-radio-group",{model:{value:e.reviseCondition.state,callback:function(t){e.$set(e.reviseCondition,"state",t)},expression:"reviseCondition.state"}},[a("el-radio",{attrs:{label:1}},[e._v("是")]),e._v(" "),a("el-radio",{attrs:{label:0}},[e._v("否")])],1)],1),e._v(" "),a("el-form-item",{attrs:{label:"描述:"}},[a("el-input",{attrs:{type:"textarea",autosize:{minRows:4,maxRows:6}},model:{value:e.reviseCondition.describe,callback:function(t){e.$set(e.reviseCondition,"describe",t)},expression:"reviseCondition.describe"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"是否补全:"}},[a("el-radio-group",{model:{value:e.reviseCondition.complementState,callback:function(t){e.$set(e.reviseCondition,"complementState",t)},expression:"reviseCondition.complementState"}},[a("el-radio",{attrs:{label:1}},[e._v("已补全")]),e._v(" "),a("el-radio",{attrs:{label:0}},[e._v("未补全")])],1)],1)],1),e._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{attrs:{type:"primary"},on:{click:e.updateService}},[e._v("确 定")]),e._v(" "),a("el-button",{on:{click:function(t){e.reviseWapper=!1}}},[e._v("取 消")])],1)],1)],1)},staticRenderFns:[]};var r=a("VU/8")(s,l,!1,function(e){a("ZEPR")},"data-v-e5e2518e",null);t.default=r.exports}});