webpackJsonp([116],{"p/3K":function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var i=a("/sA3"),s=a("8yks"),l=a("xqXs"),n=a("yEfi"),r=["01","02","03","04","05","06","07","08","09","10","11","12"],o=["Sun.","Mon.","Tues.","Wed.","Thur.","Fri.","Sat."],c={name:"index_m",data:function(){return{loading:!1,date:"",hour:"",min:"",sec:"",barPieParam:{intervalValue:"",intervalType:"",starttime:"",endtime:"",last:"7-day"},lineParam:{intervalValue:"",intervalType:"",starttime:"",endtime:"",last:""},allLogsTotle:0,errorLogsTotle:0,timeVal:"7-day",timeArr:[{value:"7-day",label:"最近7天"},{value:"30-day",label:"最近30天"},{value:"90-day",label:"最近90天"},{value:"",label:"全部"}]}},created:function(){setInterval(this.setDate,1e3),this.getLogsTotle();var t=new Date;this.lineParam.starttime=Object(i.e)("yyyy-mm-dd",t)+" 00:00:00",this.lineParam.endtime=Object(i.e)("yyyy-mm-dd",t)+" 23:59:59";var e=navigator.userAgent,a=!e.match(/(iPad).*OS\s([\d_]+)/)&&e.match(/(iPhone\sOS)\s([\d_]+)/),s=e.match(/(Android)\s+([\d.]+)/);a||s||this.$router.push("/index_n")},methods:{setDate:function(){var t=new Date;t.setDate(t.getDate()),this.date=t.getFullYear()+"/"+r[t.getMonth()]+"/"+t.getDate()+" "+o[t.getDay()];var e=(new Date).getHours();this.hour=(e<10?"0":"")+e;var a=(new Date).getMinutes();this.min=(a<10?"0":"")+a;var i=(new Date).getSeconds();this.sec=(i<10?"0":"")+i},getLogsTotle:function(){var t=this;this.$axios.post(this.$baseUrl+"/ecsCommon/getIndicesCount.do",{}).then(function(e){t.allLogsTotle=parseInt(e.data[0].indices).toLocaleString(),t.errorLogsTotle=parseInt(e.data[0].indiceserror).toLocaleString()}).catch(function(t){console.log(t)})},timeChange:function(){var t={intervalValue:"",intervalType:"",starttime:"",endtime:"",last:this.timeVal};this.barPieParam=t}},components:{logLevel_bar:s.default,logLevel_pie:l.default,hourlyLogCount_line:n.default}},d={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",[a("div",{staticClass:"time-wapper"},[t._v(t._s(t.date)+" "+t._s(t.hour)+":"+t._s(t.min)+":"+t._s(t.sec))]),t._v(" "),a("div",{staticClass:"item-wapper",staticStyle:{"margin-top":"0",height:"auto"}},[a("div",{staticClass:"item-title"},[t._v("统计")]),t._v(" "),a("ul",{staticClass:"item-content",staticStyle:{padding:"0.45rem 0.8rem",height:"105px"}},[a("li",[a("div",{staticClass:"li-tit"},[t._v("日志数：")]),t._v(" "),a("div",{staticClass:"li-val"},[t._v(t._s(t.allLogsTotle))])]),t._v(" "),a("li",[a("div",{staticClass:"li-tit"},[t._v("error数：")]),t._v(" "),a("div",{staticClass:"li-val",staticStyle:{color:"#e4956d"}},[t._v(t._s(t.errorLogsTotle))])])])]),t._v(" "),a("div",{staticClass:"item-wapper"},[a("div",{staticClass:"item-title"},[t._v(t._s(t.date.split(" ")[0])+" 数据量统计-折线图")]),t._v(" "),a("div",{staticClass:"item-content"},[a("hourlyLogCount_line",{attrs:{params:t.lineParam,baseConProp:{title:""}}})],1)]),t._v(" "),a("div",{staticClass:"item-wapper"},[a("div",{staticClass:"item-title"},[t._v("日志级别数量统计\n            "),a("el-select",{staticClass:"select-list",attrs:{placeholder:"请选择",size:"mini"},on:{change:t.timeChange},model:{value:t.timeVal,callback:function(e){t.timeVal=e},expression:"timeVal"}},t._l(t.timeArr,function(t){return a("el-option",{key:t.value,attrs:{label:t.label,value:t.value}})}),1)],1),t._v(" "),a("div",{staticClass:"item-content"},[a("logLevel_bar",{attrs:{params:t.barPieParam,baseConProp:{title:""}}})],1),t._v(" "),a("div",{staticClass:"item-content"},[a("logLevel_pie",{attrs:{params:t.barPieParam,baseConProp:{title:""}}})],1)])])},staticRenderFns:[]};var v=a("VU/8")(c,d,!1,function(t){a("qAdP")},"data-v-0537afcb",null);e.default=v.exports},qAdP:function(t,e){}});