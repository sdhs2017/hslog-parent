webpackJsonp([34],{AN1a:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var l=a("OLNH"),i=a("cUU5"),s=a("/sA3"),n=a("KxiM"),r=a("5FmZ"),d=a("C1Em"),v=a("HTTF"),o=a("8+FI"),c={name:"realTimeFlow",data:function(){return{param:{intervalValue:"5",intervalType:"second",starttime:"",endtime:"",last:"5-min"},barParam:{},intervalObj:{state:!0,interval:"5000"},refreshObj:{defaultVal:"5",opt:[{label:"5s",value:"5"},{label:"10s",value:"10"},{label:"20s",value:"20"}]},refreshIntTime:"5000",chartType:"timeline",bandwidth:100,bandwidthArr:[{label:"1M",value:1},{label:"2M",value:2},{label:"5M",value:5},{label:"10M",value:10},{label:"100M",value:100},{label:"1000M",value:1e3},{label:"10000M",value:1e4}],defaultVal:{lastVal:"",starttime:"",endtime:"",dateBlock:!1,isIntervalBox:!0,intervalState:!0,intervalVal:"5",intervalType:"second",dateUnit:"min",dateCount:"5",commonlyVal:"",changeState:!0}}},created:function(){var e=this;o.default.$on("realTimeFlowRefreshBus",function(t){var a=Object(s.n)(t);e.param=a[0],e.intervalObj=a[1]})},mounted:function(){},methods:{},beforeDestroy:function(){o.default.$off("realTimeFlowRefreshBus")},components:{vFlowchartfrom:l.default,dateLayout:i.default,allflow_timeline:n.default,allPacketCount_timeline:r.default,allUsedPer_gauge:d.default,allUsedPer_timeline:v.default}},u={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"content-bg"},[a("div",{staticClass:"title"},[e._v("全局实时流量")]),e._v(" "),a("div",{staticClass:"timer"},[a("dateLayout",{staticClass:"from-wapper",attrs:{busName:"realTimeFlowRefreshBus",defaultVal:e.defaultVal}})],1),e._v(" "),a("div",{staticClass:"item-wapper"},[a("div",{staticClass:"item-title"},[e._v("全局-利用率百分比\n            "),a("div",{staticClass:"chart-from"},[a("span",[e._v("带宽：")]),e._v(" "),a("el-select",{staticStyle:{width:"80px"},attrs:{placeholder:"请选择",size:"mini"},model:{value:e.bandwidth,callback:function(t){e.bandwidth=t},expression:"bandwidth"}},e._l(e.bandwidthArr,function(e){return a("el-option",{key:e.value,attrs:{label:e.label,value:e.value}})}),1)],1)]),e._v(" "),a("div",{staticClass:"item-content"},[a("allUsedPer_timeline",{attrs:{params:e.param,setIntervalObj:e.intervalObj,otherParams:{bandwidth:e.bandwidth}}})],1),e._v(" "),a("div",{staticClass:"item-content"},[a("allUsedPer_gauge",{attrs:{params:e.param,setIntervalObj:e.intervalObj,otherParams:{bandwidth:e.bandwidth}}})],1)]),e._v(" "),a("div",{staticClass:"item-wapper"},[a("div",{staticClass:"item-title"},[e._v("全局数据包个数")]),e._v(" "),a("div",{staticClass:"item-content"},[a("allPacketCount_timeline",{attrs:{params:e.param,setIntervalObj:e.intervalObj,baseConProp:{title:""}}})],1)]),e._v(" "),a("div",{staticClass:"item-wapper"},[a("div",{staticClass:"item-title"},[e._v("全局实时流量")]),e._v(" "),a("div",{staticClass:"item-content"},[a("allflow_timeline",{attrs:{params:e.param,setIntervalObj:e.intervalObj,baseConProp:{title:""}}})],1)])])},staticRenderFns:[]};var m=a("VU/8")(c,u,!1,function(e){a("YYP/")},"data-v-c4df2eac",null);t.default=m.exports},"YYP/":function(e,t){}});