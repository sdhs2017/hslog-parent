webpackJsonp([21],{Ha2g:function(t,e){},OJEZ:function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var n=a("mvHQ"),i=a.n(n),o=a("jKHB"),s=a("/sA3"),r={name:"equipmentCharts",data:function(){return{loading1:!1,loading2:!1,loading3:!1,loading4:!1,editable:!1,currentDateVal1:"",currentDateVal2:"",currentDateVal3:"",currentDateVal4:"",equipmentId:"",equipmentName:"",logsCount:"",errorLogsCount:"",eventCount:"",dangerEventCount:"",hourLogsCountData:{baseConfig:{title:"",xAxisName:"时间",yAxisName:"数量",hoverText:""},xAxisArr:[],yAxisArr:[]},hourEventCountData:{baseConfig:{title:"",xAxisName:"时间",yAxisName:"数量",hoverText:""},xAxisArr:["00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"],yAxisArr:[]},logTypeData:{baseConfig:{title:"",xAxisName:"日志\n类型",yAxisName:"数量",rotate:"25",hoverText:""},xAxisArr:[],yAxisArr:[]},eventTypeData:{baseConfig:{title:"",xAxisName:"事件\n类型",rotate:"25",yAxisName:"数量",hoverText:""},xAxisArr:[],yAxisArr:[]},eventTypeData2:{baseConfig:{title:"",hoverText:"百分比"},xAxisArr:[],yAxisArr:[]}}},mounted:function(){},methods:{goBack:function(){this.$router.push("/equipmentList")},getLogCount:function(t){var e=this;this.$nextTick(function(){e.$post("/ecsCommon/getIndicesCount.do",{hsData:i()({"fields.equipmentid":t})}).then(function(t){e.logsCount=t[0].indices,e.errorLogsCount=t[0].indiceserror}).catch(function(t){console.log(t)})})},getEventCount:function(t){var e=this;this.$nextTick(function(){e.$post("/ecsSyslog/getEventsCount.do",{hsData:i()({"fields.equipmentid":t})}).then(function(t){e.eventCount=t[0].events,e.dangerEventCount=t[0].eventserror}).catch(function(t){console.log(t)})})},getHourLogsCount:function(t){var e=this;this.loading1=!0,this.$nextTick(function(){e.$post("/ecsCommon/getLogCountGroupByTime.do",{hsData:i()({"fields.equipmentid":e.$route.query.id}),starttime:t+" 00:00:00",endtime:t+" 23:59:59"}).then(function(t){e.loading1=!1;var a=[],n=[];for(var i in t)a.push(t[i].hour),n.push(t[i].count);e.hourLogsCountData.xAxisArr=a,e.hourLogsCountData.yAxisArr=n}).catch(function(t){e.loading1=!1,console.log(t)})})},getHourEventsCount:function(t){var e=this;this.loading2=!0,this.$nextTick(function(){e.$post("/ecsSyslog/getCountGroupByEventType.do",{hsData:i()({"fields.equipmentid":e.$route.query.id}),starttime:t+" 00:00:00",endtime:t+" 23:59:59"}).then(function(t){e.loading2=!1;for(var a={name:"全部",color:"#61a0a8",data:[]},n={name:"高危",color:"#c64541",data:[]},i={name:"中危",color:"#d48265",data:[]},o={name:"普通",color:"#95c9b1",data:[]},s=0;s<24;s++)a.data.push(t[0][s].count),n.data.push(t[1][s].count),i.data.push(t[2][s].count),o.data.push(t[3][s].count);e.hourEventCountData.yAxisArr=[],e.hourEventCountData.yAxisArr.push(a),e.hourEventCountData.yAxisArr.push(n),e.hourEventCountData.yAxisArr.push(i),e.hourEventCountData.yAxisArr.push(o)}).catch(function(t){e.loading2=!1,console.log(t)})})},getlogTypeData:function(t){var e=this;this.loading3=!0,this.$nextTick(function(){e.$post("/ecsCommon/getCountGroupByParam.do",{hsData:i()({"fields.equipmentid":e.$route.query.id}),starttime:t+" 00:00:00",endtime:t+" 23:59:59",groupField:"log.level"}).then(function(t){e.loading3=!1;var a=[],n=[];for(var i in t[0])a.push(i),n.push(t[0][i]);e.logTypeData.xAxisArr=a,e.logTypeData.yAxisArr=n}).catch(function(t){e.loading3=!1,console.log(t)})})},getEventTypeData:function(t){var e=this;this.loading4=!0,this.$nextTick(function(){e.$post("/ecsCommon/getCountGroupByParam.do",{hsData:i()({"fields.equipmentid":e.$route.query.id}),starttime:t+" 00:00:00",endtime:t+" 23:59:59",groupField:"event.action"}).then(function(t){e.loading4=!1;var a=[],n=[],i=[];for(var o in t[0]){a.push(o),n.push(t[0][o]);var s={};s.value=t[0][o],s.name=o,i.push(s)}e.eventTypeData.xAxisArr=a,e.eventTypeData.yAxisArr=n,e.eventTypeData2.yAxisArr=i}).catch(function(t){e.loading4=!1,console.log(t)})})},dateChange1:function(){this.getHourLogsCount(this.currentDateVal1)},dateChange2:function(){this.getHourEventsCount(this.currentDateVal2)},dateChange3:function(){this.getlogTypeData(this.currentDateVal3)},dateChange4:function(){this.getEventTypeData(this.currentDateVal4)}},watch:{equipmentId:{handler:function(t,e){var a=Object(s.a)("yyyy-mm-dd",new Date);this.currentDateVal1=a,this.currentDateVal2=a,this.currentDateVal3=a,this.currentDateVal4=a,this.getLogCount(this.equipmentId),this.getEventCount(this.equipmentId),this.getHourLogsCount(a),this.getHourEventsCount(a),this.getlogTypeData(a),this.getEventTypeData(a)}}},beforeRouteEnter:function(t,e,a){a(function(e){e.$options.name="equipmentCharts"+t.query.id,e.equipmentId=t.query.id,e.equipmentName=t.query.name;var a={path:"equipmentCharts"+t.query.id,component:"equipment/equipmentCharts.vue",title:"统计"};sessionStorage.setItem("/equipmentCharts"+t.query.id,i()(a))})},components:{vEcharts:o.default}},u={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"content-bg"},[a("div",{staticClass:"top-title"},[a("span",{staticClass:"goBack",on:{click:function(e){return t.goBack()}}},[t._v("返回")]),t._v(" "),a("b",[t._v(t._s(t.equipmentName)+"统计")]),t._v(" "),a("span")]),t._v(" "),a("ul",{staticClass:"count-wapper"},[a("li",[a("div",{staticClass:"count-tit"},[t._v("日志总数")]),t._v(" "),a("div",{staticClass:"count-val"},[t._v(t._s(t.logsCount))])]),t._v(" "),a("li",[a("div",{staticClass:"count-tit"},[t._v("ERROR日志")]),t._v(" "),a("div",{staticClass:"count-val err-val"},[t._v(t._s(t.errorLogsCount))])]),t._v(" "),a("li",[a("div",{staticClass:"count-tit"},[t._v("事件总数")]),t._v(" "),a("div",{staticClass:"count-val"},[t._v(t._s(t.eventCount))])]),t._v(" "),a("li",[a("div",{staticClass:"count-tit"},[t._v("高危事件")]),t._v(" "),a("div",{staticClass:"count-val err-val"},[t._v(t._s(t.dangerEventCount))])])]),t._v(" "),a("div",{staticClass:"item-wapper"},[a("div",{staticClass:"item-title"},[t._v("每小时日志数量统计\n      "),a("el-date-picker",{staticClass:"time-wapper",attrs:{"value-format":"yyyy-MM-dd",size:"mini",type:"date",editable:t.editable,placeholder:"选择日期"},on:{change:t.dateChange1},model:{value:t.currentDateVal1,callback:function(e){t.currentDateVal1=e},expression:"currentDateVal1"}})],1),t._v(" "),a("div",{directives:[{name:"loading",rawName:"v-loading",value:t.loading1,expression:"loading1"}],staticClass:"item-content",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[a("v-echarts",{attrs:{echartType:"line",echartData:this.hourLogsCountData}})],1)]),t._v(" "),a("div",{staticClass:"item-wapper"},[a("div",{staticClass:"item-title"},[t._v("每小时事件数量统计\n      "),a("el-date-picker",{staticClass:"time-wapper",attrs:{"value-format":"yyyy-MM-dd",size:"mini",editable:t.editable,type:"date",placeholder:"选择日期"},on:{change:t.dateChange2},model:{value:t.currentDateVal2,callback:function(e){t.currentDateVal2=e},expression:"currentDateVal2"}})],1),t._v(" "),a("div",{directives:[{name:"loading",rawName:"v-loading",value:t.loading2,expression:"loading2"}],staticClass:"item-content",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[a("v-echarts",{attrs:{echartType:"moreline",echartData:this.hourEventCountData}})],1)]),t._v(" "),a("div",{staticClass:"item-wapper"},[a("div",{staticClass:"item-title"},[t._v("日志级别数量统计\n      "),a("el-date-picker",{staticClass:"time-wapper",attrs:{"value-format":"yyyy-MM-dd",size:"mini",editable:t.editable,type:"date",placeholder:"选择日期"},on:{change:t.dateChange3},model:{value:t.currentDateVal3,callback:function(e){t.currentDateVal3=e},expression:"currentDateVal3"}})],1),t._v(" "),a("div",{directives:[{name:"loading",rawName:"v-loading",value:t.loading3,expression:"loading3"}],staticClass:"item-content",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[a("v-echarts",{attrs:{echartType:"bar",echartData:this.logTypeData}})],1)]),t._v(" "),a("div",{directives:[{name:"loading",rawName:"v-loading",value:t.loading4,expression:"loading4"}],staticClass:"item-wapper",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[a("div",{staticClass:"item-title"},[t._v("事件类型数量统计\n      "),a("el-date-picker",{staticClass:"time-wapper",attrs:{"value-format":"yyyy-MM-dd",size:"mini",editable:t.editable,type:"date",placeholder:"选择日期"},on:{change:t.dateChange4},model:{value:t.currentDateVal4,callback:function(e){t.currentDateVal4=e},expression:"currentDateVal4"}})],1),t._v(" "),a("div",{staticClass:"item-content"},[a("v-echarts",{attrs:{echartType:"bar",echartData:this.eventTypeData}})],1),t._v(" "),a("div",{staticClass:"item-content"},[a("v-echarts",{attrs:{echartType:"pie",echartData:this.eventTypeData2}})],1)])])},staticRenderFns:[]};var c=a("VU/8")(r,u,!1,function(t){a("Ha2g")},"data-v-1a655dcc",null);e.default=c.exports}});
//# sourceMappingURL=21.c3726a25851ee502dc30.js.map