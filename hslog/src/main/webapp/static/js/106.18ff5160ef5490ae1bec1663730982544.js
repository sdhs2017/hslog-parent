webpackJsonp([106],{"o/rO":function(t,a,e){"use strict";Object.defineProperty(a,"__esModule",{value:!0});var i=e("sJCN"),r=e("58QH"),n=e("8+FI"),o=e("/sA3"),s={name:"urlGraph",data:function(){return{loading:!1,domain_url:"",busName:"",timepicker:[],nodeData:[],linkData:[]}},created:function(){var t=this;this.timepicker=[this.$route.query.starttime,this.$route.query.endtime],this.domain_url=this.$route.query.val,this.busName="urlGraph"+this.$route.query.val,n.default.$on(this.busName,function(a){t.timepicker=a,t.getUrlGraphData(t.timepicker)})},beforeDestroy:function(){n.default.$off(this.busName)},watch:{},methods:{getUrlGraphData:function(t){var a=this,e="/flow/getVisitCountGroupByHttpSourceIP.do",i={};"ip"===this.$route.query.type?(e="/flow/getDstIPCountGroupByHTTPSrcIP.do",i.ipv4_dst_addr=this.$route.query.val):i.domain_url=this.domain_url,i.starttime=t[0],i.endtime=t[1],this.loading=!0,this.$nextTick(function(){a.$axios.post(a.$baseUrl+e,a.$qs.stringify(i)).then(function(t){a.loading=!1;var e=[],i=[];"ip"===a.$route.query.type?e.push({name:t.data[0].ipv4_dst_addr.ipv4_dst_addr,count:t.data[0].ipv4_dst_addr.count}):e.push({name:t.data[0].domain.domain_url,count:t.data[0].domain.count}),t.data[0].source.forEach(function(r){var n={};n.name=r.source_ip,n.count=r.count,e.push(n);var o={};o.source=r.source_ip,"ip"===a.$route.query.type?o.target=t.data[0].ipv4_dst_addr.ipv4_dst_addr:o.target=t.data[0].domain.domain_url,o.count=r.count,i.push(o)}),a.nodeData=e,a.linkData=i}).catch(function(t){a.loading=!1,layer.msg("获取数据失败3",{icon:5})})})},nodeClick:function(t){var a={};a.val=t.target._private.data.id,a.clickType="node",a.starttime=this.timepicker[0],a.endtime=this.timepicker[1],"http"===a.val.substring(0,4)?(a.type="url",a.kname="domain_url"):(a.type="ip",a.kname="multifield_ip"),Object(o.l)("flowLogs"+a.val,"logsManage/flowLogs.vue",a,"日志")},linkClick:function(t){var a={};a.val=t.target._private.data.source+"-"+t.target._private.data.target,a.clickType="link",a.ipv4_src_addr=t.target._private.data.source,a.starttime=this.timepicker[0],a.endtime=this.timepicker[1],"ip"===this.$route.query.type?(a.type="ip",a.ipv4_dst_addr=t.target._private.data.target):(a.type="ip-url",a.targetVal=t.target._private.data.target,a.kname="domain_url"),Object(o.l)("flowLogs"+a.ipv4_src_addr+a.targetVal,"logsManage/flowLogs.vue",a,"日志")},timepickerChange:function(){null===this.timepicker&&(this.timepicker=["",""]),this.getUrlGraphData(this.timepicker)}},beforeRouteEnter:function(t,a,e){e(function(a){a.$options.name="urlGraph"+t.query.val,Object(o.m)(t.name,"logsManage/urlGraph.vue","业务流")})},components:{vBasegraph:i.default,baseDate:r.default}},u={render:function(){var t=this,a=t.$createElement,e=t._self._c||a;return e("div",{staticClass:"content-bg"},[e("div",{staticClass:"top-title"},[t._v(t._s(t.domain_url)+" 业务流\n        "),e("div",{staticClass:"datepicker-wapper",staticStyle:{"padding-left":"20px",float:"right","margin-right":"10px"}},[e("baseDate",{attrs:{type:"datetimerange",busName:this.busName,defaultVal:this.timepicker}})],1)]),t._v(" "),e("div",{directives:[{name:"loading",rawName:"v-loading",value:t.loading,expression:"loading"}],staticClass:"content",attrs:{"element-loading-background":"rgba(48, 62, 78, 0.5)"}},[e("v-basegraph",{attrs:{nodeData:t.nodeData,linkData:t.linkData,nodeClick:t.nodeClick,linkClick:t.linkClick}})],1)])},staticRenderFns:[]};var d=e("VU/8")(s,u,!1,function(t){e("uzIH")},"data-v-12ee4e11",null);a.default=d.exports},uzIH:function(t,a){}});