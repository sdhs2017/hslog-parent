webpackJsonp([54],{"6FcP":function(e,i){},lpgk:function(e,i,t){"use strict";Object.defineProperty(i,"__esModule",{value:!0});var n=t("ck3A"),s={name:"devTools",data:function(){return{json:{_index:"winlogbeat-7.6.0-2020.03.24",_type:"_doc",_id:"wcQICnEBznJxUXCIEOIv",_score:1,_source:{"@timestamp":"2020-03-24T00:55:20.469Z",winlog:{activity_id:"{b6f18387-0176-0004-e183-f1b67601d601}",channel:"Security",task:"Special Logon",keywords:["审核成功"],computer_name:"jyr-PC",provider_name:"Microsoft-Windows-Security-Auditing",api:"wineventlog",provider_guid:"{54849625-5478-4994-a5ba-3e3b0328c30d}",event_id:4672,record_id:490664,event_data:{PrivilegeList:["SeAssignPrimaryTokenPrivilege","SeTcbPrivilege","SeSecurityPrivilege","SeTakeOwnershipPrivilege","SeLoadDriverPrivilege","SeBackupPrivilege","SeRestorePrivilege","SeDebugPrivilege","SeAuditPrivilege","SeSystemEnvironmentPrivilege","SeImpersonatePrivilege","SeDelegateSessionUserImpersonatePrivilege"],SubjectUserSid:"S-1-5-18",SubjectUserName:"SYSTEM",SubjectDomainName:"NT AUTHORITY",SubjectLogonId:"0x3e7"},opcode:"信息",process:{thread:{id:860},pid:816},logon:{id:"0x3e7"}},event:{kind:"event",code:4672,provider:"Microsoft-Windows-Security-Auditing",action:"logged-in-special",created:"2020-03-24T00:55:21.502Z",module:"security"},log:{level:"信息"},message:"为新登录分配了特殊权限。",fields:{equipmentid:"1926e695d8284fd7b648fbe807522c36",ip:"192.168.200.44"},user:{id:"S-1-5-18",name:"SYSTEM",domain:"NT AUTHORITY"},ecs:{version:"1.4.0"},host:{name:"jyr-PC",architecture:"x86_64",os:{version:"10.0",family:"windows",name:"Windows 10 Home China",kernel:"10.0.18362.720 (WinBuild.160101.0800)",build:"18362.720",platform:"windows"},id:"f2b13a2b-3f72-48f4-bb44-0003818775e8",hostname:"jyr-PC"},agent:{ephemeral_id:"978bb564-6b2e-4608-aaa5-959f73f14ce0",hostname:"jyr-PC",id:"ebb8b367-cbea-477a-b645-8def3b1a1e65",version:"7.6.0",type:"winlogbeat"}}}}},components:{jsonView:t.n(n).a}},a={render:function(){var e=this.$createElement,i=this._self._c||e;return i("div",{staticClass:"content-bg"},[i("div",{staticClass:"top-title"},[this._v("调试")]),this._v(" "),i("div",{staticClass:"dev-wapper"},[i("div",{staticClass:"dev-tools-search"}),this._v(" "),i("div",{staticClass:"dev-tools-json"},[i("jsonView",{attrs:{data:this.json,theme:"one-dark","line-height":"20",deep:"5"}})],1)])])},staticRenderFns:[]};var o=t("VU/8")(s,a,!1,function(e){t("6FcP")},"data-v-6adbe84e",null);i.default=o.exports}});