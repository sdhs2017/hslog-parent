webpackJsonp([39],{MkHZ:function(t,s,a){"use strict";Object.defineProperty(s,"__esModule",{value:!0});var e={name:"controlCenter2",data:function(){return{loading:!1,beatState:"未开启",syslogState:"未开启",beatsLeft:"0px",syslogLeft:"0px",interval:""}},created:function(){var t=this;this.getBeatsState(),this.getSyslogState(),this.interval=setInterval(function(){t.getBeatsState(),t.getSyslogState()},5e3)},methods:{getBeatsState:function(){var t=this;this.$nextTick(function(){t.$axios.post(t.$baseUrl+"/collector/getAgentKafkaListenerState.do","").then(function(s){!0===s.data[0].state?(t.beatState="已开启",t.beatsLeft="-301px"):!1===s.data[0].state?(t.beatState="未开启",t.beatsLeft="0px"):console.log(data)}).catch(function(t){})})},getSyslogState:function(){var t=this;this.$nextTick(function(){t.$axios.post(t.$baseUrl+"/collector/getSyslogKafkaListenerState.do","").then(function(s){!0===s.data[0].state?(t.syslogState="已开启",t.syslogLeft="-301px"):!1===s.data[0].state?(t.syslogState="未开启",t.syslogLeft="0px"):console.log(data)}).catch(function(t){})})},playBeats:function(){var t=this;layer.confirm("是否开启Agent采集？",{btn:["确定","取消"]},function(s){layer.close(s),t.loading=!0,t.$nextTick(function(){t.$axios.post(t.$baseUrl+"/collector/startAgentKafkaListener.do","").then(function(s){t.loading=!1,"true"===s.data.success?(layer.msg(s.data.message,{icon:1}),t.beatState="已开启",t.beatsLeft="-301px"):"false"===s.data.success&&layer.msg(s.data.message,{icon:5})}).catch(function(s){t.loading=!1,layer.msg("启动失败",{icon:5})})})},function(){layer.close()})},stopBeats:function(){var t=this;layer.confirm("是否关闭Agent采集服务？",{btn:["确定","取消"]},function(s){clearInterval(t.interval),layer.close(s),t.loading=!0,t.$nextTick(function(){t.$axios.post(t.$baseUrl+"/collector/stopAgentKafkaListener.do","").then(function(s){t.loading=!1,"true"===s.data.success?(layer.msg(s.data.message,{icon:1}),t.beatState="未开启",t.beatsLeft="0px"):"false"===s.data.success&&layer.msg(s.data.message,{icon:5}),t.interval=setInterval(function(){t.getBeatsState(),t.getSyslogState()},5e3)}).catch(function(s){t.loading=!1,layer.msg("停止服务失败",{icon:5}),t.interval=setInterval(function(){t.getBeatsState(),t.getSyslogState()},5e3)})})},function(){layer.close()})},playSyslog:function(){var t=this;layer.confirm("是否开启syslog采集？",{btn:["确定","取消"]},function(s){layer.close(s),t.loading=!0,t.$nextTick(function(){t.$axios.post(t.$baseUrl+"/collector/startSyslogKafkaListener.do  ","").then(function(s){t.loading=!1,"true"===s.data.success?(layer.msg(s.data.message,{icon:1}),t.syslogState="已开启",t.syslogLeft="-301px"):"false"===s.data.success&&layer.msg(s.data.message,{icon:5})}).catch(function(s){t.loading=!1,layer.msg("启动失败",{icon:5})})})},function(){layer.close()})},stopSyslog:function(){var t=this;layer.confirm("是否关闭syslog采集服务？",{btn:["确定","取消"]},function(s){clearInterval(t.interval),layer.close(s),t.loading=!0,t.$nextTick(function(){t.$axios.post(t.$baseUrl+"/collector/stopSyslogKafkaListener.do","").then(function(s){t.loading=!1,"true"===s.data.success?(layer.msg(s.data.message,{icon:1}),t.syslogState="未开启",t.syslogLeft="0px"):"false"===s.data.success&&layer.msg(s.data.message,{icon:5}),t.interval=setInterval(function(){t.getBeatsState(),t.getSyslogState()},5e3)}).catch(function(s){t.loading=!1,layer.msg("停止服务失败",{icon:5}),t.interval=setInterval(function(){t.getBeatsState(),t.getSyslogState()},5e3)})})},function(){layer.close()})},initialize:function(){var t=this;layer.confirm("是否初始化？",{btn:["确定","取消"]},function(s){layer.close(s),t.loading=!0,t.$nextTick(function(){t.$axios.post(t.$baseUrl+"/log/createIndexAndMapping4Beats.do","").then(function(s){t.loading=!1,!0===s.data[0].state?layer.msg(s.data[0].msg,{icon:1}):!1===s.data[0].state&&layer.msg(s.data[0].msg,{icon:5})}).catch(function(s){t.loading=!1,layer.msg("初始化失败",{icon:5})})})},function(){layer.close()})},backup:function(){var t=this;layer.confirm("是否备份日志数据？",{btn:["确定","取消"]},function(s){layer.close(s),t.loading=!0,t.$nextTick(function(){t.$axios.post(t.$baseUrl+"/manage/createSnapshotByIndices.do","").then(function(s){t.loading=!1,!0===s.data[0].state?layer.msg(s.data[0].msg,{icon:1}):!1===s.data[0].state&&layer.msg(s.data[0].msg,{icon:5})}).catch(function(s){t.loading=!1,layer.msg("备份失败",{icon:5})})})},function(){layer.close()})},recover:function(){var t=this;layer.confirm("是否恢复日志数据？",{btn:["确定","取消"]},function(s){layer.close(s),t.loading=!0,t.$nextTick(function(){t.$axios.post(t.$baseUrl+"/manage/restore.do","").then(function(s){t.loading=!1,!0===s.data[0].state?layer.msg(s.data[0].msg,{icon:1}):!1===s.data[0].state&&layer.msg(s.data[0].msg,{icon:5})}).catch(function(s){t.loading=!1,layer.msg("恢复失败",{icon:5})})})},function(){layer.close()})}},beforeDestroy:function(){clearInterval(this.interval)}},i={render:function(){var t=this,s=t.$createElement,a=t._self._c||s;return a("div",{directives:[{name:"loading",rawName:"v-loading",value:t.loading,expression:"loading"}],staticClass:"content-bg",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[a("div",{staticClass:"top-title"},[t._v("控制中心")]),t._v(" "),a("div",{staticClass:"control-wapper"},[a("div",{staticClass:"serviceConBox"},[a("div",{staticClass:"beats-status",staticStyle:{"border-right":"1px dashed #40566d"}},[a("span",[t._v("Agent采集:")]),t._v(" "),a("span",{staticClass:"serviceStatus",style:"未开启"===t.beatState?{color:"#d9534f"}:{color:"#1ab394"}},[t._v(t._s(t.beatState))])]),t._v(" "),a("div",{staticClass:"syslog-status"},[a("span",[t._v("Syslog采集:")]),t._v(" "),a("span",{staticClass:"serviceStatus",style:"未开启"===t.syslogState?{color:"#d9534f"}:{color:"#1ab394"}},[t._v(t._s(t.syslogState))])])]),t._v(" "),a("div",{staticClass:"btnConBox"},[a("div",{staticClass:"btnConBoxTop"},[a("div",{staticClass:"left-wapper serviceBtn"},[a("div",{staticClass:"scroll-wapper",style:{left:t.beatsLeft}},[a("div",{staticClass:"play-beats",on:{click:t.playBeats}},[t._m(0),t._v(" "),t._m(1)]),t._v(" "),a("div",{staticClass:"stop-beats",on:{click:t.stopBeats}},[t._m(2),t._v(" "),t._m(3)])])]),t._v(" "),a("div",{staticClass:"right-wapper serviceBtn"},[a("div",{staticClass:"scroll-wapper",style:{left:t.syslogLeft}},[a("div",{staticClass:"play-syslog",on:{click:t.playSyslog}},[t._m(4),t._v(" "),t._m(5)]),t._v(" "),a("div",{staticClass:"stop-syslog",on:{click:t.stopSyslog}},[t._m(6),t._v(" "),t._m(7)])])])]),t._v(" "),a("div",{staticClass:"btnConBoxBottom"},[a("div",{staticClass:"serviceBtn",attrs:{id:"initialize",title:"点击初始化"},on:{click:t.initialize}},[a("i",{staticClass:"el-icon-refresh-right"}),t._v(" "),a("p",{staticClass:"btnTitle"},[t._v("初始化")]),t._v(" "),a("p",{staticClass:"btnDescribe"},[t._v("对集群的数据结构进行初始化，保证日志数据正常采集到数据中心")])]),t._v(" "),a("div",{staticClass:"serviceBtn",attrs:{id:"backup",title:"点击备份数据"},on:{click:t.backup}},[a("i",{staticClass:"el-icon-printer"}),t._v(" "),a("p",{staticClass:"btnTitle"},[t._v("备份")]),t._v(" "),a("p",{staticClass:"btnDescribe"},[t._v("对集群中的日志数据进行完整的备份操作")])]),t._v(" "),a("div",{staticClass:"serviceBtn",attrs:{id:"recover",title:"点击恢复备份"},on:{click:t.recover}},[a("i",{staticClass:"el-icon-refresh"}),t._v(" "),a("p",{staticClass:"btnTitle"},[t._v("恢复备份")]),t._v(" "),a("p",{staticClass:"btnDescribe"},[t._v("对删除后的数据进行恢复操作（需对删除的数据先进行备份操作）")])])])])])])},staticRenderFns:[function(){var t=this.$createElement,s=this._self._c||t;return s("div",{staticClass:"play-i"},[s("i",{staticClass:"el-icon-caret-right"})])},function(){var t=this.$createElement,s=this._self._c||t;return s("div",{staticClass:"play-t "},[s("div",[s("p",{staticClass:"btnTitle"},[this._v("开启Agent采集服务")]),this._v(" "),s("p",{staticClass:"btnDescribe"},[this._v("开始收集各个Agent发送过来的数据，范式化后入库")])])])},function(){var t=this.$createElement,s=this._self._c||t;return s("div",{staticClass:"play-i"},[s("i",{staticClass:"el-icon-circle-close"})])},function(){var t=this.$createElement,s=this._self._c||t;return s("div",{staticClass:"play-t "},[s("div",[s("p",{staticClass:"btnTitle"},[this._v("关闭Agent采集服务")]),this._v(" "),s("p",{staticClass:"btnDescribe"},[this._v("停止收集各个Agent发送过来的数据")])])])},function(){var t=this.$createElement,s=this._self._c||t;return s("div",{staticClass:"play-i"},[s("i",{staticClass:"el-icon-caret-right"})])},function(){var t=this.$createElement,s=this._self._c||t;return s("div",{staticClass:"play-t "},[s("div",[s("p",{staticClass:"btnTitle"},[this._v("开启Syslog采集服务")]),this._v(" "),s("p",{staticClass:"btnDescribe"},[this._v("开始收集各个资产发送过来的日志数据，范式化后入库")])])])},function(){var t=this.$createElement,s=this._self._c||t;return s("div",{staticClass:"play-i"},[s("i",{staticClass:"el-icon-circle-close"})])},function(){var t=this.$createElement,s=this._self._c||t;return s("div",{staticClass:"play-t "},[s("div",[s("p",{staticClass:"btnTitle"},[this._v("关闭Syslog采集服务")]),this._v(" "),s("p",{staticClass:"btnDescribe"},[this._v("停止收集各个资产发送过来的数据")])])])}]};var n=a("VU/8")(e,i,!1,function(t){a("Zuf7")},"data-v-a4eba544",null);s.default=n.exports},Zuf7:function(t,s){}});