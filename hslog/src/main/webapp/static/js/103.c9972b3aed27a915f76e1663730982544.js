webpackJsonp([103],{"5iWg":function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var n={name:"eventList",data:function(){return{tableHead:[{prop:"name",label:"事件名称"},{prop:"userName",label:"创建人"},{prop:"actionName",label:"动作名称",formatData:function(t){return t.substring(0,t.length-1)}},{prop:"message",label:"描述"},{prop:"actionState",label:"是否启用",formatData:function(t){return"1"==t?"是":"否"}},{prop:"tools",label:"操作",width:"60",btns:[{icon:"el-icon-edit",text:"修改",clickFun:function(t,e){}},{icon:"el-icon-circle-close",text:"删除",btnType:"removeItem",clickFun:function(t,e){}}]}],tableData:[]}},created:function(){this.getEventListData()},methods:{getEventListData:function(){var t=this;this.$nextTick(function(){layer.load(1),t.$axios.post(t.$baseUrl+"/event/selectAll.do","").then(function(e){layer.closeAll("loading"),t.tableData=e.data}).catch(function(t){layer.closeAll("loading")})})}},components:{vBasetable:a("qTy0").default}},i={render:function(){var t=this.$createElement,e=this._self._c||t;return e("div",{staticClass:"content-bg"},[e("div",{staticClass:"top-title"},[this._v("事件列表")]),this._v(" "),e("div",{staticClass:"btn-wapper"},[e("el-button",{attrs:{type:"primary",size:"mini",plain:""}},[this._v("添加")])],1),this._v(" "),e("div",{staticClass:"event-list-content"},[e("v-basetable",{attrs:{tableHead:this.tableHead,tableData:this.tableData}})],1)])},staticRenderFns:[]};var l=a("VU/8")(n,i,!1,function(t){a("HBDb")},"data-v-1a770ad7",null);e.default=l.exports},HBDb:function(t,e){}});