webpackJsonp([23],{"E+7/":function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var r=a("OLNH"),s=a("jKHB"),i=a("8+FI"),n=(a("/sA3"),{name:"userAgentInfo",data:function(){return{interTime:"",refreshIntTime:"5000",dataTime:3600,systemChartData:{baseConfig:{title:"",xAxisName:"操作系统",yAxisName:"访问次数/次",rotate:"25",itemColor:["rgba(68,47,148,0.5)","rgba(15,219,243,1)"]},xAxisArr:[],yAxisArr:[]},systemChartData2:{baseConfig:{title:"",hoverText:"百分比"},xAxisArr:[],yAxisArr:[]},browserChartData:{baseConfig:{title:"",xAxisName:"浏览器",yAxisName:"访问次数/次",rotate:"25",itemColor:["rgba(68,47,148,0.5)","rgba(15,219,243,1)"]},xAxisArr:[],yAxisArr:[]},browserChartData2:{baseConfig:{title:"",hoverText:"百分比"},xAxisArr:[],yAxisArr:[]}}},created:function(){var t=this;i.default.$on("userAgentInfoRefreshBus",function(e){t.refreshIntTime=e,clearInterval(t.interTime),t.setInterFun()}),i.default.$on("userAgentInfoDataBus",function(e){t.dataTime=e})},mounted:function(){this.getBrowserData(this.dataTime),this.getSystemData(this.dataTime)},methods:{getBrowserData:function(t){var e=this;this.$nextTick(function(){e.$post("/flow/getUserAgentBrowserGroupByTime.do",{timeInterval:t}).then(function(t){var a=[],r=[],s=[];for(var i in t[0])a.push(i),r.push(t[0][i]),s.push({name:i,value:t[0][i]});e.browserChartData.xAxisArr=a,e.browserChartData.yAxisArr=r,e.browserChartData2.yAxisArr=s}).catch(function(t){})})},getSystemData:function(t){var e=this;this.$nextTick(function(){e.$post("/flow/getUserAgentOSGroupByTime.do",{timeInterval:t}).then(function(t){var a=[],r=[],s=[];for(var i in t[0])a.push(i),r.push(t[0][i]),s.push({name:i,value:t[0][i]});e.systemChartData.xAxisArr=a,e.systemChartData.yAxisArr=r,e.systemChartData2.yAxisArr=s}).catch(function(t){})})},setInterFun:function(){var t=this;this.interTime=setInterval(function(){t.getBrowserData(t.dataTime),t.getSystemData(t.dataTime)},this.refreshIntTime)}},beforeRouteEnter:function(t,e,a){a(function(t){t.setInterFun()})},beforeRouteLeave:function(t,e,a){clearInterval(this.interTime),a()},components:{vFlowchartfrom:r.default,vEcharts:s.default}}),o={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"content-bg"},[a("div",{staticClass:"top-title"},[t._v("User-Agent信息\n      "),a("v-flowchartfrom",{staticClass:"from-wapper",attrs:{refreshBus:"userAgentInfoRefreshBus",dataBus:"userAgentInfoDataBus"}})],1),t._v(" "),a("div",{staticClass:"item-wapper"},[a("div",{staticClass:"item-title"},[t._v("业务访问用户统计-操作系统")]),t._v(" "),a("div",{staticClass:"item-content"},[a("v-echarts",{attrs:{echartType:"bar",echartData:this.systemChartData}})],1)]),t._v(" "),a("div",{staticClass:"item-wapper"},[a("div",{staticClass:"item-title"},[t._v("业务访问用户统计-操作系统")]),t._v(" "),a("div",{staticClass:"item-content"},[a("v-echarts",{attrs:{echartType:"pie",echartData:this.systemChartData2}})],1)]),t._v(" "),a("div",{staticClass:"item-wapper"},[a("div",{staticClass:"item-title"},[t._v("业务访问用户统计-浏览器")]),t._v(" "),a("div",{staticClass:"item-content"},[a("v-echarts",{attrs:{echartType:"bar",echartData:this.browserChartData}})],1)]),t._v(" "),a("div",{staticClass:"item-wapper"},[a("div",{staticClass:"item-title"},[t._v("业务访问用户统计-浏览器")]),t._v(" "),a("div",{staticClass:"item-content"},[a("v-echarts",{attrs:{echartType:"pie",echartData:this.browserChartData2}})],1)])])},staticRenderFns:[]};var c=a("VU/8")(n,o,!1,function(t){a("pC/Z")},"data-v-0f72bf5d",null);e.default=c.exports},"pC/Z":function(t,e){}});
//# sourceMappingURL=23.0b4309f32596e59434be.js.map