webpackJsonp([39],{H6l1:function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var l={name:"actionManage",data:function(){return{tableHead:[{prop:"name",label:"动作名称",width:"150"},{prop:"userName",label:"创建人",width:"100"},{prop:"type",label:"日志类型",width:"100"},{prop:"feature",label:"关键字",width:""},{prop:"state",label:"是否启用",width:"125"},{prop:"tools",label:"操作",width:"50",btns:[{icon:"el-icon-edit",text:"查看详情",btnType:""},{icon:"el-icon-circle-close",text:"删除",btnType:""}]}],tableData:[]}},created:function(){this.getActionData()},methods:{getActionData:function(){var t=this;layer.load(1),this.$nextTick(function(){t.$axios.post("/jz/action/selectAll.do","").then(function(e){layer.closeAll(),e.data[0].forEach(function(t){"0"===t.state?t.state="否":t.state="是"}),t.tableData=e.data[0]}).catch(function(t){console.log(t),layer.closeAll()})})}},components:{vBasetable:a("qTy0").default}},n={render:function(){var t=this.$createElement,e=this._self._c||t;return e("div",{staticClass:"content-bg"},[e("div",{staticClass:"top-title"},[this._v("动作列表")]),this._v(" "),e("div",{staticClass:"action-table-wapper"},[e("v-basetable",{attrs:{tableHead:this.tableHead,tableData:this.tableData}})],1)])},staticRenderFns:[]};var i=a("C7Lr")(l,n,!1,function(t){a("L1uk")},"data-v-3b3cbb56",null);e.default=i.exports},L1uk:function(t,e){}});