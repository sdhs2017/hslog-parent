webpackJsonp([108],{"4NO3":function(t,a){},FPyv:function(t,a,e){"use strict";Object.defineProperty(a,"__esModule",{value:!0});var s={name:"flowServiceManage",data:function(){return{loading:!1,state:"未开启",interval:""}},created:function(){this.getServiceStatus(),this.interval=setInterval(this.getServiceStatus,5e3)},methods:{getServiceStatus:function(){var t=this;this.$nextTick(function(){t.$axios.post(t.$baseUrl+"/collector/statePcap4jCollector.do","").then(function(a){!0===a.data[0].state?t.state="已开启":!1===a.data[0].state?t.state="未开启":console.log(data)}).catch(function(t){})})},playService:function(){var t=this;layer.confirm("是否开启服务？",{btn:["确定","取消"]},function(a){layer.close(a),t.loading=!0,t.$nextTick(function(){t.$axios.post(t.$baseUrl+"/collector/startPcap4jCollector.do","").then(function(a){t.loading=!1,!0===a.data[0].state?(layer.msg(a.data[0].msg,{icon:1}),t.state="已开启"):!1===a.data[0].state&&layer.msg(a.data[0].msg,{icon:5})}).catch(function(a){t.loading=!1,layer.msg("启动失败",{icon:5})})})},function(){layer.close()})},stopService:function(){var t=this;layer.confirm("是否停止服务？",{btn:["确定","取消"]},function(a){layer.close(a),t.loading=!0,t.$nextTick(function(){t.$axios.post(t.$baseUrl+"/collector/stopPcap4jCollector.do","").then(function(a){t.loading=!1,!0===a.data[0].state?(layer.msg(a.data[0].msg,{icon:1}),t.state="未开启"):!1===a.data[0].state&&layer.msg(a.data[0].msg,{icon:5})}).catch(function(a){t.loading=!1,layer.msg("停止服务失败",{icon:5})})})},function(){layer.close()})}},beforeDestroy:function(){clearInterval(this.interval)}},i={render:function(){var t=this,a=t.$createElement,e=t._self._c||a;return e("div",{directives:[{name:"loading",rawName:"v-loading",value:t.loading,expression:"loading"}],staticClass:"content-bg",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[e("div",{staticClass:"top-title"},[t._v("流量控制中心")]),t._v(" "),e("div",{staticClass:"content"},[e("div",{staticClass:"service-state-wapper"},[e("span",[t._v("服务状态：")]),t._v(" "),e("span",{staticClass:"service-state",style:"未开启"===t.state?{color:"#d9534f"}:{color:"#1ab394"}},[t._v(t._s(t.state))])]),t._v(" "),e("div",{staticClass:"btn-wapper"},[e("div",{staticClass:"service-btn",on:{click:t.playService}},[e("i",{staticClass:"el-icon-caret-right"}),t._v(" "),e("p",{staticClass:"btn-title"},[t._v("开启服务")]),t._v(" "),e("p",{staticClass:"btn-describe"},[t._v("开始收集网络流量数据包，范式化后入库")])]),t._v(" "),e("div",{staticClass:"service-btn",on:{click:t.stopService}},[e("i",{staticClass:"el-icon-circle-close"}),t._v(" "),e("p",{staticClass:"btn-title"},[t._v("停止服务")]),t._v(" "),e("p",{staticClass:"btn-describe"},[t._v("停止收集网络流浪数据包")])])])])])},staticRenderFns:[]};var n=e("VU/8")(s,i,!1,function(t){e("4NO3")},"data-v-12823a08",null);a.default=n.exports}});