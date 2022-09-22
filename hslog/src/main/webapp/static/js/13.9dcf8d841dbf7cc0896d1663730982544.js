webpackJsonp([13],{U3r2:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var s=a("woOf"),l=a.n(s),r=a("mvHQ"),i=a.n(r),o=a("8+FI"),n=a("/sA3"),c={name:"assetOverviewUserForm",props:{formData:{type:Object},busName:{type:String},routerUrl:{type:String}},data:function(){return{nameState:!1,ipOrTypeState:!1,saveState:!1,form:{name:"",ip:"",hostName:"",logType:"",log_level:["emergency","alert","critical","error","warning","notice","information","debug"],type:"",startUp:1,macAdress:"",system:"",domain:"",port:"",systemVersion:"",assetNum:"",serialNum:"",describe:"",valuation:"",userName:"",phone:"",roleNames:"",createTime:"",updateTime:"",endTime:""},defaultForm:"",logTypeArr:[],typeArr:[],equipmentData:{}}},created:function(){this.getLogType(),this.getEquipmentType(),this.defaultForm=i()(this.form)},mounted:function(){},watch:{formData:function(e,t){this.equipmentData=e,this.equipmentData.log_level&&(this.equipmentData.log_level=this.equipmentData.log_level.split(",").filter(function(e){return e&&e.trim()}));var a=this.equipmentData.type;for(var s in this.equipmentData.type=[],a&&(this.equipmentData.type.push(a.substring(0,2)),this.equipmentData.type.push(a)),this.form)this.form[s]=this.equipmentData[s]}},methods:{equipmentNameBlur:function(){var e=this;if(""!=this.form.name){var t={};this.formData&&(t.id=this.formData.id),t.name=this.form.name,this.$nextTick(function(){layer.load(1),e.$axios.post(e.$baseUrl+"/equipment/checkNameUnique.do",e.$qs.stringify(t)).then(function(t){layer.closeAll("loading");var a=t.data;"true"!==a.success?(layer.msg(a.message,{icon:5}),e.nameState=!1):e.nameState=!0}).catch(function(e){layer.closeAll("loading")})})}},ipOrLogtypeBlur:function(){var e=this;if(""!=this.form.ip&&""!=this.form.logType){var t={};this.formData&&(t.id=this.formData.id),t.ip=this.form.ip,t.logType=this.form.logType,this.$nextTick(function(){layer.load(1),e.$axios.post(e.$baseUrl+"/equipment/checkIpAndLogTypeUnique.do",e.$qs.stringify(t)).then(function(t){layer.closeAll("loading");var a=t.data;"true"!==a.success?(layer.msg(a.message,{icon:5}),e.ipOrTypeState=!1):e.ipOrTypeState=!0}).catch(function(e){layer.closeAll("loading")})})}},getLogType:function(){var e=this;this.$nextTick(function(){e.$axios.get("static/filejson/logTypeLevel.json",{}).then(function(t){for(var a=0;a<t.data.length;a++){var s={value:t.data[a].type,label:t.data[a].label};e.logTypeArr.push(s)}}).catch(function(e){console.log(e)})})},getEquipmentType:function(){var e=this;this.$nextTick(function(){e.$axios.get("static/filejson/equiptype.json",{}).then(function(t){e.typeArr=t.data}).catch(function(e){console.log(e)})})},saveEquipment:function(){if(this.saveState=!0,""===this.form.name)layer.msg("资产名称不能为空",{icon:5});else if(""===this.form.hostName)layer.msg("主机名称不能为空",{icon:5});else if(""===this.form.ip)layer.msg("IP地址不能为空",{icon:5});else if(Object(n.c)(this.form.ip))if(0==this.form.log_level.length)layer.msg("日志级别不能为空",{icon:5});else if(0==this.form.logType.length)layer.msg("日志类型不能为空",{icon:5});else if(0==this.form.type.length)layer.msg("资产类型不能为空",{icon:5});else{for(var e=l()({},this.form),t=e.log_level.length,a="",s=0;s<t;s++)a+=e.log_level[s]+",";e.log_level=a,e.type=e.type[1],o.default.$emit(this.busName,e)}else layer.msg("IP不合法",{icon:5})},emptyData:function(){this.form=JSON.parse(this.defaultForm)}}},m={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",[a("el-form",{ref:"form",staticClass:"equipment-form",attrs:{"label-width":"100px"}},[a("el-row",[a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple"},[a("span",{staticClass:"mustWrite"},[e._v("*")]),e._v(" "),a("el-form-item",{attrs:{label:"资产名称:"}},[a("el-input",{attrs:{placeholder:"资产名称"},model:{value:e.form.name,callback:function(t){e.$set(e.form,"name",t)},expression:"form.name"}})],1)],1)]),e._v(" "),a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple-light"},[a("span",{staticClass:"mustWrite"},[e._v("*")]),e._v(" "),a("el-form-item",{attrs:{label:"资产ip:"}},[a("el-input",{attrs:{placeholder:"资产ip"},model:{value:e.form.ip,callback:function(t){e.$set(e.form,"ip",t)},expression:"form.ip"}})],1)],1)])],1),e._v(" "),a("el-row",[a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple"},[a("span",{staticClass:"mustWrite"},[e._v("*")]),e._v(" "),a("el-form-item",{attrs:{label:"主机名:"}},[a("el-input",{attrs:{placeholder:"主机名"},model:{value:e.form.hostName,callback:function(t){e.$set(e.form,"hostName",t)},expression:"form.hostName"}})],1)],1)]),e._v(" "),a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple-light"},[a("span",{staticClass:"mustWrite"},[e._v("*")]),e._v(" "),a("el-form-item",{attrs:{label:"日志类型:"}},[a("el-select",{staticStyle:{width:"100%"},attrs:{placeholder:"请选择"},model:{value:e.form.logType,callback:function(t){e.$set(e.form,"logType",t)},expression:"form.logType"}},e._l(e.logTypeArr,function(e){return a("el-option",{key:e.value,attrs:{label:e.label,value:e.value}})}),1)],1)],1)])],1),e._v(" "),a("el-row",[a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple"},[a("span",{staticClass:"mustWrite"},[e._v("*")]),e._v(" "),a("el-form-item",{attrs:{label:"日志级别:"}},[a("el-checkbox-group",{model:{value:e.form.log_level,callback:function(t){e.$set(e.form,"log_level",t)},expression:"form.log_level"}},[a("el-checkbox",{attrs:{label:"emergency"}}),e._v(" "),a("el-checkbox",{attrs:{label:"alert"}}),e._v(" "),a("el-checkbox",{attrs:{label:"critical"}}),e._v(" "),a("el-checkbox",{attrs:{label:"error"}}),e._v(" "),a("el-checkbox",{attrs:{label:"warning"}}),e._v(" "),a("el-checkbox",{attrs:{label:"notice"}}),e._v(" "),a("el-checkbox",{attrs:{label:"information"}}),e._v(" "),a("el-checkbox",{attrs:{label:"debug"}})],1)],1)],1)]),e._v(" "),a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple-light"},[a("span",{staticClass:"mustWrite"},[e._v("*")]),e._v(" "),a("el-form-item",{attrs:{label:"资产类型:"}},[a("el-cascader",{staticStyle:{width:"100%"},attrs:{options:e.typeArr},model:{value:e.form.type,callback:function(t){e.$set(e.form,"type",t)},expression:"form.type"}})],1)],1)])],1),e._v(" "),a("el-row",[a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple"},[a("el-form-item",{attrs:{label:"根域名:"}},[a("el-input",{attrs:{placeholder:"根域名"},model:{value:e.form.domain,callback:function(t){e.$set(e.form,"domain",t)},expression:"form.domain"}})],1)],1)]),e._v(" "),a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple-light"},[a("el-form-item",{attrs:{label:"端口:"}},[a("el-input",{attrs:{placeholder:"端口"},model:{value:e.form.port,callback:function(t){e.$set(e.form,"port",t)},expression:"form.port"}})],1)],1)])],1),e._v(" "),a("el-row",[a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple"},[a("el-form-item",{attrs:{label:"系统:"}},[a("el-input",{attrs:{placeholder:"系统"},model:{value:e.form.system,callback:function(t){e.$set(e.form,"system",t)},expression:"form.system"}})],1)],1)]),e._v(" "),a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple-light"},[a("el-form-item",{attrs:{label:"系统版本号:"}},[a("el-input",{attrs:{placeholder:"系统版本号"},model:{value:e.form.systemVersion,callback:function(t){e.$set(e.form,"systemVersion",t)},expression:"form.systemVersion"}})],1)],1)])],1),e._v(" "),a("el-row",[a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple"},[a("el-form-item",{attrs:{label:"资产编号:"}},[a("el-input",{attrs:{placeholder:"资产编号"},model:{value:e.form.assetNum,callback:function(t){e.$set(e.form,"assetNum",t)},expression:"form.assetNum"}})],1)],1)]),e._v(" "),a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple-light"},[a("el-form-item",{attrs:{label:"序列号:"}},[a("el-input",{attrs:{placeholder:"序列号"},model:{value:e.form.serialNum,callback:function(t){e.$set(e.form,"serialNum",t)},expression:"form.serialNum"}})],1)],1)])],1),e._v(" "),a("el-row",[a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple"},[a("span",{staticClass:"mustWrite"},[e._v("*")]),e._v(" "),a("el-form-item",{attrs:{label:"是否启用:"}},[a("el-radio-group",{model:{value:e.form.startUp,callback:function(t){e.$set(e.form,"startUp",t)},expression:"form.startUp"}},[a("el-radio",{attrs:{label:1}},[e._v("是")]),e._v(" "),a("el-radio",{attrs:{label:0}},[e._v("否")])],1)],1)],1)]),e._v(" "),a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple-light"},[a("el-form-item",{attrs:{label:"MAC地址:"}},[a("el-input",{attrs:{placeholder:"MAC地址"},model:{value:e.form.macAdress,callback:function(t){e.$set(e.form,"macAdress",t)},expression:"form.macAdress"}})],1)],1)])],1),e._v(" "),a("el-row",[a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple-light"},[a("el-form-item",{attrs:{label:"资产价值:"}},[a("el-input",{attrs:{placeholder:"资产价值"},model:{value:e.form.valuation,callback:function(t){e.$set(e.form,"valuation",t)},expression:"form.valuation"}})],1)],1)]),e._v(" "),a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple-light"},[a("el-form-item",{attrs:{label:"描述:"}},[a("el-input",{attrs:{type:"textarea",autosize:{minRows:3,maxRows:3}},model:{value:e.form.describe,callback:function(t){e.$set(e.form,"describe",t)},expression:"form.describe"}})],1)],1)])],1),e._v(" "),a("el-row",[a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple"},[a("el-form-item",{attrs:{label:"创建时间:"}},[a("el-input",{attrs:{disabled:""},model:{value:e.form.createTime,callback:function(t){e.$set(e.form,"createTime",t)},expression:"form.createTime"}})],1)],1)]),e._v(" "),a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple-light"},[a("el-form-item",{attrs:{label:"更新时间:"}},[a("el-input",{attrs:{disabled:""},model:{value:e.form.updateTime,callback:function(t){e.$set(e.form,"updateTime",t)},expression:"form.updateTime"}})],1)],1)])],1),e._v(" "),a("el-row",[a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple"},[a("el-form-item",{attrs:{label:"截止时间:"}},[a("el-input",{attrs:{disabled:""},model:{value:e.form.endTime,callback:function(t){e.$set(e.form,"endTime",t)},expression:"form.endTime"}})],1)],1)]),e._v(" "),a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple"},[a("el-form-item",{attrs:{label:"责任人:"}},[a("el-input",{attrs:{disabled:""},model:{value:e.form.userName,callback:function(t){e.$set(e.form,"userName",t)},expression:"form.userName"}})],1)],1)])],1),e._v(" "),a("el-row",[a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple"},[a("el-form-item",{attrs:{label:"责任人电话:"}},[a("el-input",{attrs:{disabled:""},model:{value:e.form.phone,callback:function(t){e.$set(e.form,"phone",t)},expression:"form.phone"}})],1)],1)]),e._v(" "),a("el-col",{attrs:{span:12}},[a("div",{staticClass:"grid-content bg-purple-light"},[a("el-form-item",{attrs:{label:"责任人角色:"}},[a("el-input",{attrs:{disabled:""},model:{value:e.form.roleNames,callback:function(t){e.$set(e.form,"roleNames",t)},expression:"form.roleNames"}})],1)],1)])],1)],1),e._v(" "),a("div",{staticClass:"equipment-form-btns"},[a("el-button",{attrs:{type:"primary"},on:{click:e.saveEquipment}},[e._v("保存")]),e._v(" "),a("el-button",{attrs:{type:"info"},on:{click:e.emptyData}},[e._v("清空")])],1)],1)},staticRenderFns:[]};var p=a("VU/8")(c,m,!1,function(e){a("Xt8S")},"data-v-2ae69eb5",null);t.default=p.exports},Xt8S:function(e,t){},hT5o:function(e,t){},ueP0:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var s=a("8+FI"),l={name:"addAssetOverviewUser",data:function(){return{loading:!1,routerUrl:"/assetOverviewUser",busName:"addAssetOverviewUser",formData:{}}},created:function(){var e=this;s.default.$on(this.busName,function(t){e.formData=t,e.addEquipment(e.formData)})},methods:{addEquipment:function(e){var t=this;this.loading=!0,this.$nextTick(function(){t.$axios.post(t.$baseUrl+"/equipment/upsert.do",t.$qs.stringify(e)).then(function(e){t.loading=!1,"true"==e.data.success?(layer.msg(e.data.message,{icon:1}),t.$router.push({path:"/assetOverviewUser"})):layer.msg(e.data.message,{icon:5})}).catch(function(e){t.loading=!1})})}},beforeDestroy:function(){s.default.$off(this.busName)},components:{vEquipmentForm:a("U3r2").default}},r={render:function(){var e=this.$createElement,t=this._self._c||e;return t("div",{staticClass:"content-bg"},[t("div",{staticClass:"top-title"},[this._v("添加资产")]),this._v(" "),t("div",{staticClass:"equipment-path"}),this._v(" "),t("div",{directives:[{name:"loading",rawName:"v-loading",value:this.loading,expression:"loading"}],staticClass:"form-wapper",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[t("v-equipment-form",{attrs:{busName:this.busName,routerUrl:this.routerUrl}})],1)])},staticRenderFns:[]};var i=a("VU/8")(l,r,!1,function(e){a("hT5o")},"data-v-c7a5a964",null);t.default=i.exports}});