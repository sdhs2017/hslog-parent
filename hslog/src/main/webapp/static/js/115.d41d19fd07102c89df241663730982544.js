webpackJsonp([115],{Nga0:function(t,a,e){"use strict";Object.defineProperty(a,"__esModule",{value:!0});var i=e("mvHQ"),r=e.n(i),s=e("5Om0"),n=e("cUU5"),l=e("MPrf"),o=e("8yks"),c=e("xqXs"),d=e("yEfi"),g=e("8+FI"),m=e("/sA3"),u=["01","02","03","04","05","06","07","08","09","10","11","12"],v=["星期日","星期一","星期二","星期三","星期四","星期五","星期六"],h={name:"index_n",data:function(){return{barloading:!1,pieloading:!1,defaultValBar:{lastVal:"",starttime:"",endtime:"",dateBlock:!0,isIntervalBox:!1,intervalState:!1,intervalVal:"",intervalType:"",dateUnit:"hour",dateCount:"1",commonlyVal:"",changeState:!0},defaultValPie:{lastVal:"",starttime:"",endtime:"",dateBlock:!0,isIntervalBox:!1,intervalState:!1,intervalVal:"",intervalType:"",dateUnit:"hour",dateCount:"1",commonlyVal:"",changeState:!0},defaultValLine:{lastVal:"",starttime:"",endtime:"",dateBlock:!0,isIntervalBox:!1,intervalState:!1,intervalVal:"",intervalType:"",dateUnit:"hour",dateCount:"1",commonlyVal:"",changeState:!0},defaultValErrorLog:{lastVal:"",starttime:"",endtime:"",dateBlock:!0,isIntervalBox:!1,intervalState:!1,intervalVal:"",intervalType:"",dateUnit:"hour",dateCount:"1",commonlyVal:"",changeState:!1},barParam:{intervalValue:"",intervalType:"",starttime:"",endtime:"",last:""},intervalObjBar:{state:!1,interval:"5000"},intervalObjPie:{state:!1,interval:"5000"},intervalObjLine:{state:!1,interval:"5000"},barBusName:{clickName:"indexLogLevelClick",exportName:""},pieParam:{intervalValue:"",intervalType:"",starttime:"",endtime:"",last:""},lineParam:{intervalValue:"",intervalType:"",starttime:"",endtime:"",last:""},errorLogParam:{intervalValue:"",intervalType:"",starttime:"",endtime:"",last:""},echartData:{barData:{baseConfig:{title:"日志级别数量统计",xAxisName:"级别",yAxisName:"数量/条",hoverText:"数量",itemColor:[["rgba(68,47,148,0.5)","rgba(15,219,243,1)"]]},data:{dimensions:[],source:[]}},pieData:{baseConfig:{title:"日志级别数量统计",xAxisName:"级别",yAxisName:"数量/条",hoverText:"数量",itemColor:[["rgba(68,47,148,0.5)","rgba(15,219,243,1)"]]},data:{dimensions:[],source:[]}}},loading:!1,date:"",hour:"",min:"",sec:"",starttime:"",todayDate:"",dateVal1:[],dateVal2:[],allLogsTotle:0,errorLogsTotle:0,flowTotle:0,errorLogsData:[],interval:"",activeIndex:0,baseConfig:{type:"2",title:"日志详情",areaWidth:"620px",areaHeight:"520px"},detailData:{},layerState:!1}},created:function(){var t=this,a=navigator.userAgent,e=!a.match(/(iPad).*OS\s([\d_]+)/)&&a.match(/(iPhone\sOS)\s([\d_]+)/),i=a.match(/(Android)\s+([\d.]+)/);(e||i)&&this.$router.push("/mobile/index_m");var r=new Date,s=new Date;s.setTime(s.getTime()-864e5*this.$store.state.beforeDay),this.dateVal1=[s,r],this.dateVal2=[s,r],this.todayDate=Object(m.e)("yyyy-mm-dd",r),this.starttime=Object(m.e)("yyyy-mm-dd",s),this.defaultValBar.endtime=Object(m.e)("yyyy-mm-dd HH:MM:SS",r),this.defaultValBar.starttime=Object(m.e)("yyyy-mm-dd",s)+" 00:00:00",this.defaultValPie.endtime=Object(m.e)("yyyy-mm-dd HH:MM:SS",r),this.defaultValPie.starttime=Object(m.e)("yyyy-mm-dd",s)+" 00:00:00",this.defaultValLine.starttime=Object(m.e)("yyyy-mm-dd",r)+" 00:00:00",this.defaultValLine.endtime=Object(m.e)("yyyy-mm-dd HH:MM:SS",r),this.defaultValErrorLog.endtime=Object(m.e)("yyyy-mm-dd HH:MM:SS",r),this.defaultValErrorLog.starttime=Object(m.e)("yyyy-mm-dd",s)+" 00:00:00",this.barParam.endtime=Object(m.e)("yyyy-mm-dd HH:MM:SS",r),this.barParam.starttime=Object(m.e)("yyyy-mm-dd",s)+" 00:00:00",this.pieParam.endtime=Object(m.e)("yyyy-mm-dd HH:MM:SS",r),this.pieParam.starttime=Object(m.e)("yyyy-mm-dd",s)+" 00:00:00",this.lineParam.starttime=Object(m.e)("yyyy-mm-dd",r)+" 00:00:00",this.lineParam.endtime=Object(m.e)("yyyy-mm-dd HH:MM:SS",r),this.errorLogParam.endtime=Object(m.e)("yyyy-mm-dd HH:MM:SS",r),this.errorLogParam.starttime=Object(m.e)("yyyy-mm-dd",s)+" 00:00:00",g.default.$on("changeDateBar",function(a){var e=Object(m.n)(a);t.barParam=e[0],t.intervalObjBar=e[1],t.getBarPieEchartData(t.barParam,!0,!1,"manual")}),g.default.$on("changeDatePie",function(a){var e=Object(m.n)(a);t.pieParam=e[0],t.intervalObjPie=e[1],t.getBarPieEchartData(t.barParam,!1,!0,"manual")}),g.default.$on("changeDateLine",function(a){var e=Object(m.n)(a);t.lineParam=e[0],t.intervalObjLine=e[1]}),g.default.$on("changeDateErrorLog",function(a){var e=Object(m.n)(a);t.errorLogParam=e[0],t.getErrorLogsData()}),setInterval(this.setDate,1e3),this.getLogsTotle(),this.getErrorLogTotle(),this.getBarPieEchartData(this.barParam,!0,!0,"auto"),this.getErrorLogsData(),g.default.$on(this.barBusName.clickName,function(a){t.$router.push({name:"accurateSearch2",params:{logLevel:a.name,dateArr:[t.barParam.starttime,t.barParam.endtime]}})})},mounted:function(){},watch:{layerState:function(t){t?this.stopLogsInterval():this.startLogsInterval()}},methods:{clickss:function(t){this.$router.push({name:"accurateSearch2",params:{logLevel:t.name,dateArr:this.dateVal1}})},goToSearchLogs:function(){0!=this.errorLogsTotle&&this.$router.push({name:"accurateSearch2",params:{logLevel:"error"}})},setDate:function(){var t=new Date;t.setDate(t.getDate()),this.date=t.getFullYear()+"/"+u[t.getMonth()]+"/"+t.getDate()+" "+v[t.getDay()];var a=(new Date).getHours();this.hour=(a<10?"0":"")+a;var e=(new Date).getMinutes();this.min=(e<10?"0":"")+e;var i=(new Date).getSeconds();this.sec=(i<10?"0":"")+i},getLogsTotle:function(){var t=this;this.$nextTick(function(){t.$axios.get(t.$baseUrl+"/ecsCommon/getIndicesCount.do",{}).then(function(a){t.allLogsTotle=parseInt(a.data[0].indices).toLocaleString()}).catch(function(t){console.log(t)})})},getErrorLogTotle:function(){var t=this;this.$nextTick(function(){t.$axios.get(t.$baseUrl+"/ecsCommon/getIndicesCountByLevel.do",{}).then(function(a){t.errorLogsTotle=parseInt(a.data[0].indiceserror).toLocaleString()}).catch(function(t){console.log(t)})})},getFlowTotle:function(){var t=this;this.$nextTick(function(){t.$axios.get(t.$baseUrl+"/ecsWinlog/getIndicesCountByType.do",{}).then(function(a){t.flowTotle=parseInt(a.data[0].indices_defaultpacket).toLocaleString()}).catch(function(t){console.log(t)})})},getErrorLogsData:function(){var t=this;this.loading=!0,this.$nextTick(function(){t.$axios.post(t.$baseUrl+"/ecsWinlog/getLogListByBlend.do",t.$qs.stringify({hsData:r()({starttime:t.errorLogParam.starttime,endtime:t.errorLogParam.endtime,"log.level":"error",page:1,size:20})})).then(function(a){t.loading=!1,t.errorLogsData=a.data[0].list.slice(0,30),t.errorLogsData.length<3?setTimeout(function(){t.startLogsInterval()},5e3):t.startLogsInterval()}).catch(function(a){t.loading=!1,console.log(a)})})},getBarPieEchartData:function(t,a,e,i){var r=this;a&&(this.barloading=!0),e&&(this.pieloading=!0),t.refresh=i,this.$nextTick(function(){r.$axios.post(r.$baseUrl+"/log/getCountGroupByLogLevel_barAndPie.do",r.$qs.stringify(t)).then(function(t){var i=t.data;"true"===i.success?(a&&(r.barloading=!1,r.echartData.barData.data=i.data[0]),e&&(r.pieloading=!1,r.echartData.pieData.data=i.data[0])):(a&&(r.barloading=!1),e&&(r.pieloading=!1))}).catch(function(t){console.log(t)})})},errorLogsClick:function(t){var a=this;this.detailData=t,this.layerState=!0,g.default.$on("closeLayer",function(t){a.layerState=!1})},errorLogsListScroll:function(){this.activeIndex<this.errorLogsData.length-3?this.activeIndex+=1:(this.stopLogsInterval(),this.activeIndex=0,this.getErrorLogsData())},stopLogsInterval:function(){clearInterval(this.interval)},startLogsInterval:function(){var t=this;this.layerState||(clearInterval(this.interval),this.interval=setInterval(function(){t.errorLogsListScroll()},4e3))}},computed:{top:function(){return 112.66*-this.activeIndex+"px"}},beforeDestroy:function(){g.default.$off(this.barBusName.clickName),g.default.$off("changeDateBar"),g.default.$off("changeDatePie"),g.default.$off("changeDateLine"),clearInterval(this.interval)},components:{vEcharts:s.default,dateLayout:n.default,vListdetails2:l.default,logLevel_bar:o.default,logLevel_pie:c.default,hourlyLogCount_line:d.default}},p={render:function(){var t=this,a=t.$createElement,e=t._self._c||a;return e("div",{staticClass:"wapper"},[e("el-row",{staticClass:"wapper-top",attrs:{gutter:20}},[e("el-col",{attrs:{span:7}},[e("div",{staticClass:"grid-content bg-purple wapper-content count-wapper"},[e("p",{staticClass:"content-title"}),t._v(" "),e("div",{staticClass:"content-infom"},[e("div",{staticClass:"current-date"},[e("div",{attrs:{id:"date"}},[t._v(t._s(t.date))]),t._v(" "),e("ul",[e("li",{attrs:{id:"hours"}},[t._v(" "+t._s(t.hour))]),t._v(" "),e("li",{staticClass:"point"},[t._v(":")]),t._v(" "),e("li",{attrs:{id:"min"}},[t._v(t._s(t.min)+" ")]),t._v(" "),e("li",{staticClass:"point"},[t._v(":")]),t._v(" "),e("li",{attrs:{id:"sec"}},[t._v(" "+t._s(t.sec))])])]),t._v(" "),e("div",{staticClass:"current-number"},[e("div",{staticClass:"num-left"},[e("p",{staticClass:"all-totle-wapper"},[e("span",[t._v("日志数：")]),t._v(" "),e("b",[t._v(" "+t._s(t.allLogsTotle)+" ")])])]),t._v(" "),e("div",{staticClass:"num-right"},[e("p",{staticClass:"flow-totle-wapper"},[e("span",[t._v("ERROR日志数：")]),t._v(" "),e("b",{staticClass:"errorLogsCounts",attrs:{title:"点击查看错误日志"},on:{click:function(a){return t.goToSearchLogs()}}},[t._v(" "+t._s(t.errorLogsTotle)+" ")])])])])])])]),t._v(" "),e("el-col",{attrs:{span:10}},[e("div",{staticClass:"grid-content bg-purple wapper-content"},[e("p",{staticClass:"content-title"},[e("dateLayout",{staticClass:"date-wapper",attrs:{busName:"changeDateBar",defaultVal:t.defaultValBar}})],1),t._v(" "),e("div",{directives:[{name:"loading",rawName:"v-loading",value:t.barloading,expression:"barloading"}],staticClass:"content-infom",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[e("v-echarts",{attrs:{echartType:"bar",echartData:t.echartData.barData,busName:t.barBusName}})],1)])]),t._v(" "),e("el-col",{attrs:{span:7}},[e("div",{staticClass:"grid-content bg-purple wapper-content"},[e("p",{staticClass:"content-title",staticStyle:{display:"flex","justify-content":"flex-end"}},[e("dateLayout",{staticClass:"date-wapper",attrs:{busName:"changeDatePie",defaultVal:t.defaultValPie}})],1),t._v(" "),e("div",{directives:[{name:"loading",rawName:"v-loading",value:t.pieloading,expression:"pieloading"}],staticClass:"content-infom",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[e("v-echarts",{attrs:{echartType:"pie",echartData:this.echartData.pieData}})],1)])])],1),t._v(" "),e("el-row",{staticClass:"wapper-bottom",attrs:{gutter:20}},[e("el-col",{attrs:{span:10}},[e("div",{staticClass:"grid-content bg-purple wapper-content",staticStyle:{height:"400px"}},[e("p",{staticClass:"content-title"},[t._v("异常检索\n                        "),e("dateLayout",{staticClass:"date-wapper",staticStyle:{"margin-top":"9px"},attrs:{busName:"changeDateErrorLog",defaultVal:t.defaultValErrorLog}})],1),t._v(" "),e("div",{directives:[{name:"loading",rawName:"v-loading",value:t.loading,expression:"loading"}],staticClass:"content-infom",staticStyle:{height:"350px"},attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[0==t.errorLogsData.length?e("p",{staticStyle:{padding:"19px",color:"#7593bc"}},[t._v("暂无异常")]):e("ul",{ref:"errorLogsList",staticClass:"errorLogsList",style:{top:t.top},on:{mouseenter:function(a){return t.stopLogsInterval()},mouseleave:function(a){return t.startLogsInterval()}}},t._l(t.errorLogsData,function(a,i){return e("li",{key:i,staticClass:"error-logs-item",attrs:{title:"点击查看详情"},on:{click:function(e){return t.errorLogsClick(a)}}},[e("p",{staticClass:"index-list-top"},[e("span",{staticClass:"index-log-level"},[t._v("#"+t._s(a.log.level)+"#")]),t._v(" "),e("span",{staticClass:"index-log-ip"},[t._v("#"+t._s(a.fields.ip)+"#")]),t._v(" "),e("span",{staticClass:"index-log-eqname"},[t._v("#"+t._s(a.fields.equipmentname)+"#")])]),t._v(" "),e("p",{staticClass:"index-list-content"},[t._v(t._s(a.message))]),t._v(" "),e("p",{staticClass:"index-list-time"},[e("small",[e("i",{staticClass:"el-icon-time"}),t._v(" "+t._s(a["@timestamp"]))])])])}),0)])])]),t._v(" "),e("el-col",{staticStyle:{height:"400px"},attrs:{span:14}},[e("div",{staticClass:"grid-content bg-purple wapper-content",staticStyle:{height:"400px"}},[e("p",{staticClass:"content-title"},[t._v("数据量统计\n                        "),e("dateLayout",{staticClass:"date-wapper",staticStyle:{"margin-top":"9px"},attrs:{busName:"changeDateLine",defaultVal:t.defaultValLine}})],1),t._v(" "),e("div",{staticClass:"content-infom",staticStyle:{height:"350px"}},[e("div",{staticClass:"content-infom",staticStyle:{height:"350px"}},[e("hourlyLogCount_line",{attrs:{params:t.lineParam,setIntervalObj:t.intervalObjLine}})],1)])])])],1),t._v(" "),t.layerState?e("div",[e("vListdetails2",{attrs:{baseConfig:this.baseConfig,detailsData:this.detailData}})],1):t._e()],1)},staticRenderFns:[]};var y=e("VU/8")(h,p,!1,function(t){e("iD9q")},"data-v-0604019c",null);a.default=y.exports},iD9q:function(t,a){}});