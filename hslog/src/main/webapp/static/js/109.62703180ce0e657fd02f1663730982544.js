webpackJsonp([109],{LC7L:function(e,t){},ui9M:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var i=a("ID1p"),l=a("AIWI"),n=a("/sA3"),o=a("8+FI"),r={name:"dhcpLogs",data:function(){var e=this;return{layerObj:{detailData:{},layerState:!1},busName:"dhcpLogs",searchUrl:"log/getDNSLogListByBlend.do",searchConditions:{type:"dhcp",starttime:"",endtime:"",dhcp_type:"",client_mac:"",relay_ip:"",client_ip:""},formConditionsArr:[],tableHead:[{prop:"logtime",label:"时间",width:""},{prop:"equipmentname",label:"资产名称",width:""},{prop:"dhcp_type",label:"dhcp类型",width:""},{prop:"client_mac",label:"mac地址",width:""},{prop:"relay_ip",label:"中继设备地址",width:""},{prop:"client_ip",label:"客户端IP",width:""},{prop:"tools",label:"操作",width:"60",btns:[{icon:"el-icon-tickets",text:"查看详情",btnType:"readDetails",clickFun:function(t,a){e.layerObj.layerState=!0,e.layerObj.detailData=t}},{icon:"el-icon-circle-close",text:"删除",btnType:"removeItem",clickFun:function(t,a){e.$refs.logContent.removeParams([t])}}]}]}},created:function(){var e=this,t=Object(n.e)("yyyy-mm-dd HH:MM:SS",new Date),a=new Date;a.setTime(a.getTime()-864e5*this.$store.state.beforeDay),a=Object(n.e)("yyyy-mm-dd HH:MM:SS",a),this.searchConditions={type:"dhcp",dhcp_type:"",client_mac:"",relay_ip:"",client_ip:"",endtime:t,starttime:a},this.formConditionsArr=[{label:"事件范围",type:"datetimerange",itemType:"",paramName:"time",model:{model:[a,t]},val:""},{label:"dhcp类型",paramName:"dhcp_type",model:{model:""},itemType:"",type:"input"},{label:"mac地址",paramName:"client_mac",model:{model:""},itemType:"",type:"input"},{label:"中继设备",paramName:"relay_ip",model:{model:""},itemType:"",type:"input"},{label:"客户端ip",paramName:"client_ip",model:{model:""},itemType:"",type:"input"}],o.default.$on("dhcpLogs",function(t){e.searchConditions=t,e.searchConditions.type="dhcp"})},beforeDestroy:function(){o.default.$off("dhcpLogs")},components:{vSearchForm:i.default,vLogscontent:l.default}},c={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"content-bg"},[a("div",{staticClass:"top-title"},[e._v("DHCP 日志")]),e._v(" "),a("div",{staticClass:"search-wapper"},[a("v-search-form",{attrs:{formItem:e.formConditionsArr,busName:e.busName}})],1),e._v(" "),a("div",{staticClass:"content-wapper"},[a("v-logscontent",{ref:"logContent",attrs:{searchConditions:e.searchConditions,tableHead:e.tableHead,searchUrl:e.searchUrl,layerObj:e.layerObj,moreDeleteBtn:!0}})],1)])},staticRenderFns:[]};var p=a("VU/8")(r,c,!1,function(e){a("LC7L")},"data-v-0e83bde0",null);t.default=p.exports}});