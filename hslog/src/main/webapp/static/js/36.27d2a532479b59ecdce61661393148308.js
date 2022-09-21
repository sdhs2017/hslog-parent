webpackJsonp([36],{Jxs9:function(e,t,o){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var a=o("ID1p"),r=o("576z"),s=o("8+FI"),i=o("/sA3"),l={name:"complexSearch",data:function(){return{layerObj:{detailData:{},layerState:!1},busName:"complexSearch",words:"",searchConditions:{},searchUrl:"ecsWinlog/getLogListByBlend.do",formConditionsArr:[],tableHead:[],firstSearch:!0,logBaseJson:[],logType:[],logLevel:[],levelVal:"",normalizationVal:"true",normalizationOpt:[{label:"全部",value:""},{label:"是",value:"true"},{label:"否",value:"false"}],progressRight:"right:-90px;",progressTitle:'导出中<br/><i class="el-icon-loading"></i>',progressVal:"",exportInternal:""}},created:function(){var e=this;this.tableHead=[{prop:"@timestamp",label:"时间",width:"150"},{prop:"log.level",label:"级别",width:"100"},{prop:"agent.type",label:"日志类型",width:"100"},{prop:"fields.equipmentname",label:"资产名称",width:"125",clickFun:function(t){"unknown"===t.equipmentid?layer.msg("资产未知",{icon:5}):e.$router.push({name:"equipment2",params:{equipmentid:t.fields.equipmentid}})}},{prop:"fields.ip",label:"IP",width:"125"},{prop:"message",label:"日志内容",width:""},{prop:"tools",label:"操作",width:"60",btns:[{icon:"el-icon-tickets",text:"查看详情",btnType:"readDetails",clickFun:function(t,o){e.layerObj.layerState=!0,e.layerObj.detailData=t}},{icon:"el-icon-circle-close",text:"删除",btnType:"removeItem",clickFun:function(t,o){e.$refs.logContent.removeParams([t])}}]}],this.getLogType(),this.setLogLevel("");var t=Object(i.e)("yyyy-mm-dd HH:MM:SS",new Date),o=new Date;if(o.setTime(o.getTime()-864e5*this.$store.state.beforeDay),o=Object(i.e)("yyyy-mm-dd HH:MM:SS",o),this.searchConditions={normalization:"true","agent.type":"",endtime:t,"fields.ip":"",message:"","fields.equipmentname":"","log.level":"",starttime:o},this.formConditionsArr=[{label:"时间范围",type:"datetimerange",itemType:"",paramName:"time",model:{model:[o,t]},val:""},{label:"类型",paramName:"agent.type",type:"select",itemType:"multiple",model:{model:""},options:this.logType},{label:"级别",paramName:"log.level",type:"select",itemType:"multiple",model:{model:this.levelVal},options:this.logLevel},{label:"是否完整范式化",paramName:"normalization",width:"70",type:"select",itemType:"",model:{model:this.normalizationVal},options:this.normalizationOpt},{label:"IP地址",paramName:"fields.ip",itemType:"",model:{model:""},type:"input"},{label:"资产名称",paramName:"fields.equipmentname",model:{model:""},itemType:"",type:"input"},{label:"内容",paramName:"message",itemType:"",model:{model:""},type:"input"}],this.$route.params.logLevel){var a="",r="";this.$route.params.dateArr&&this.$route.params.dateArr[0].length&&(a=this.$route.params.dateArr[0],r=this.$route.params.dateArr[1]),this.formConditionsArr[0].model.model=[a,r],this.formConditionsArr[2].model.model=[this.$route.params.logLevel],this.searchConditions["log.level"]=this.$route.params.logLevel,this.searchConditions.starttime=a,this.searchConditions.endtime=r,this.formConditionsArr[1].model.model="",this.searchConditions["agent.type"]=""}s.default.$on(this.busName,function(t){e.searchConditions=t}),s.default.$on("logTypeChange",function(t){var o;o=t.join(","),e.setLogLevel(o)}),localStorage.getItem("exportLogs")&&(this.progressRight="right:5px;",this.progressTitle="导出中<br/>...",this.exportInternal=setInterval(this.updataProgressValue,1e3)),s.default.$on("exportStart",function(t){e.progressRight="right:5px;",e.exportInternal=setInterval(e.updataProgressValue,1e3)}),s.default.$on("exportFail",function(t){e.progressRight="right:-90px;",layer.msg("导出失败",{icon:5}),clearInterval(e.exportInternal),localStorage.removeItem("exportLogs")})},beforeDestroy:function(){s.default.$off(this.busName),s.default.$off("logTypeChange"),s.default.$off("exportStart"),s.default.$off("exportFail")},watch:{$route:function(e,t){if(this.$route.params.logLevel){var o="",a="";this.$route.params.dateArr&&(this.$route.params.dateArr[0].length?(o=this.$route.params.dateArr[0]+" 00:00:00",a=this.$route.params.dateArr[1]+" 23:59:59"):(o=Object(i.e)("yyyy-mm-dd",this.$route.params.dateArr[0])+" 00:00:00",a=Object(i.e)("yyyy-mm-dd",this.$route.params.dateArr[1])+" 23:59:59")),this.formConditionsArr[2].model.model=[this.$route.params.logLevel],this.formConditionsArr[0].model.model=[o,a],this.searchConditions["log.level"]=this.$route.params.logLevel,this.searchConditions.starttime=o,this.searchConditions.endtime=a,this.formConditionsArr[1].model.model="",this.searchConditions["agent.type"]=""}}},methods:{getLogType:function(){var e=this;this.$nextTick(function(){e.$axios.post(e.$baseUrl+"/log/getLogTypeComboxByPage.do",e.$qs.stringify({pageType:"search"})).then(function(t){for(var o in t.data.data)e.logType.push(t.data.data[o])}).catch(function(e){console.log(e)})})},setLogLevel:function(e){var t=this;this.$nextTick(function(){t.$axios.post(t.$baseUrl+"/log/getLogLevelByLogType.do",t.$qs.stringify({logType:e})).then(function(e){var o=e.data;if("true"==o.success){t.logLevel=[];for(var a=0;a<o.data.length;a++)t.$set(t.logLevel,a,o.data[a]);t.formConditionsArr[2].options=t.logLevel}else layer.msg(o.message,{icon:5})}).catch(function(e){t.loading=!1})})},updataProgressValue:function(){var e=this;this.$nextTick(function(){e.$axios.get(e.$baseUrl+"/log/getExportProcess.do",{}).then(function(t){"finished"===t.data[0].state?(clearInterval(e.exportInternal),e.progressTitle="导出<br/>完成",e.progressVal=t.data[0].value,setTimeout(function(){e.progressRight="right:-90px;"},5e3),setTimeout(function(){e.progressTitle="导出中<br/>...",e.progressVal=""},5300),layer.msg("导出成功",{icon:1}),localStorage.removeItem("exportLogs")):e.progressVal=t.data[0].value}).catch(function(e){console.log(e)})})}},components:{vSearchForm:a.default,vLogscontent2:r.default}},n={render:function(){var e=this,t=e.$createElement,o=e._self._c||t;return o("div",{staticClass:"content-bg"},[o("div",{staticClass:"top-title"},[e._v("复合查询")]),e._v(" "),o("div",{staticClass:"search-wapper"},[o("v-search-form",{attrs:{formItem:e.formConditionsArr,busName:e.busName}})],1),e._v(" "),o("div",{staticClass:"content-wapper"},[o("v-logscontent2",{ref:"logContent",attrs:{searchConditions:e.searchConditions,tableHead:e.tableHead,searchUrl:e.searchUrl,exportBtn:!0,layerObj:e.layerObj,moreDeleteBtn:!0}})],1),e._v(" "),o("div",{staticClass:"export-progress-box",style:e.progressRight},[o("div",{staticClass:"export-progress-wapper"},[o("div",{staticClass:"progress-name",domProps:{innerHTML:e._s(e.progressTitle)}}),e._v(" "),o("div",{staticClass:"progress-val"},[e._v(e._s(e.progressVal))])])])])},staticRenderFns:[]};var p=o("VU/8")(l,n,!1,function(e){o("qgtL")},"data-v-b9a13d72",null);t.default=p.exports},qgtL:function(e,t){}});