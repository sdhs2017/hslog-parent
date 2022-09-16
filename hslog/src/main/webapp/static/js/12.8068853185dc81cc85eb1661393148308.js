webpackJsonp([12],{"6tVx":function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var o=n("mvHQ"),s=n.n(o),i={name:"assetGroupFrom",props:{formType:{type:String,default:function(){return""}},groupId:{type:String,default:function(){return""}},url:{type:String,default:function(){return""}},defaultFrom:{type:Object}},data:function(){return{loading:!1,winlogEventList:[],syslogEventList:[],form:{event_group_name:"",event_group_note:"",syslog_event_ids:[],winlog_event_ids:[]},id:""}},watch:{defaultFrom:function(){this.form.event_group_name=this.defaultFrom.event_group_name,this.form.event_group_note=this.defaultFrom.event_group_note,this.form.syslog_event_ids=this.defaultFrom.syslog_event_ids,this.form.winlog_event_ids=this.defaultFrom.winlog_event_ids,this.form.event_group_id=this.defaultFrom.event_group_id}},created:function(){this.getEventList("winlogbeat"),this.getEventList("syslog")},methods:{getEventList:function(t){var e=this;this.$nextTick(function(){e.loading=!0,e.$axios.post(e.$baseUrl+"/eventGroup/getEventListByType.do",e.$qs.stringify({log_type:t})).then(function(n){e.loading=!1;var o=n.data;"true"===o.success?"syslog"===t?e.syslogEventList=o.data:"winlogbeat"===t&&(e.winlogEventList=o.data):layer.msg(o.message,{icon:5})}).catch(function(t){e.loading=!1})})},submitFrom:function(){var t=this;""===this.form.event_group_name?layer.msg("事件组名称不能为空",{icon:5}):0===this.form.syslog_event_ids.length&&0===this.form.winlog_event_ids.length?layer.msg("事件组中未选中任何事件",{icon:5}):(""!==this.groupId&&(this.form.event_group_id=this.groupId),this.$nextTick(function(){var e=JSON.parse(s()(t.form));0===e.winlog_event_ids.length&&(e.winlog_event_ids=""),0===e.syslog_event_ids.length&&(e.syslog_event_ids=""),t.loading=!0,t.$axios.post(t.$baseUrl+t.url,t.$qs.stringify(e)).then(function(e){t.loading=!1;var n=e.data;"true"===n.success?(layer.msg(n.message,{icon:1}),""===t.groupId&&t.clearFrom()):layer.msg(n.message,{icon:5})}).catch(function(e){t.loading=!1})}))},clearFrom:function(){this.form={event_group_name:"",event_group_note:"",syslog_event_ids:[],winlog_event_ids:[]}}}},r={render:function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{directives:[{name:"loading",rawName:"v-loading",value:t.loading,expression:"loading"}],attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[n("el-form",{ref:"form",staticClass:"from-wapper",attrs:{model:t.form,"label-width":"100px"}},[n("el-form-item",{attrs:{label:"名称："}},[n("span",{staticClass:"mustWrite"},[t._v("*")]),t._v(" "),n("el-input",{model:{value:t.form.event_group_name,callback:function(e){t.$set(t.form,"event_group_name",e)},expression:"form.event_group_name"}})],1),t._v(" "),n("el-form-item",{attrs:{label:"linux："}},[n("el-transfer",{attrs:{filterable:"",titles:["全部事件","已选事件"],"button-texts":["到左边","到右边"],data:t.syslogEventList},model:{value:t.form.syslog_event_ids,callback:function(e){t.$set(t.form,"syslog_event_ids",e)},expression:"form.syslog_event_ids"}})],1),t._v(" "),n("el-form-item",{attrs:{label:"windows："}},[n("el-transfer",{attrs:{filterable:"",titles:["全部事件","已选事件"],"button-texts":["到左边","到右边"],data:t.winlogEventList},model:{value:t.form.winlog_event_ids,callback:function(e){t.$set(t.form,"winlog_event_ids",e)},expression:"form.winlog_event_ids"}})],1),t._v(" "),n("el-form-item",{attrs:{label:"说明："}},[n("el-input",{attrs:{type:"textarea"},model:{value:t.form.event_group_note,callback:function(e){t.$set(t.form,"event_group_note",e)},expression:"form.event_group_note"}})],1),t._v(" "),n("el-form-item",{staticStyle:{display:"flex","justify-content":"space-evenly"}},[n("el-button",{attrs:{type:"primary"},on:{click:t.submitFrom}},[t._v("确定")]),t._v(" "),n("el-button",{on:{click:t.clearFrom}},[t._v("清空")])],1)],1)],1)},staticRenderFns:[]};var a=n("VU/8")(i,r,!1,function(t){n("Cn8k")},"data-v-2bd0158e",null);e.default=a.exports},Cn8k:function(t,e){},"MZc/":function(t,e){},fdhz:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var o={name:"addEventGroup",data:function(){return{}},components:{assetFrom:n("6tVx").default}},s={render:function(){var t=this.$createElement,e=this._self._c||t;return e("div",{staticClass:"content-bg"},[e("div",{staticClass:"top-title"},[this._v("添加事件组")]),this._v(" "),e("div",{staticClass:"form-wapper"},[e("assetFrom",{attrs:{url:"/eventGroup/insert.do"}})],1)])},staticRenderFns:[]};var i=n("VU/8")(o,s,!1,function(t){n("MZc/")},"data-v-48107d96",null);e.default=i.exports}});