<template>
    <div class="content-bg">
        <div class="top-title">调试</div>
        <div class="dev-wapper">
            <div class="dev-tools-search">

            </div>
            <div class="dev-tools-json">
                <jsonView :data="json" theme="one-dark" line-height="20" deep="5"></jsonView>
            </div>
        </div>
    </div>
    
</template>

<script>
    import jsonView from 'vue-json-views'
    export default {
        name: "devTools",
        data() {
            return {
                json:{
                    "_index" : "winlogbeat-7.6.0-2020.03.24",
                    "_type" : "_doc",
                    "_id" : "wcQICnEBznJxUXCIEOIv",
                    "_score" : 1.0,
                    "_source" : {
                        "@timestamp" : "2020-03-24T00:55:20.469Z",
                        "winlog" : {
                            "activity_id" : "{b6f18387-0176-0004-e183-f1b67601d601}",
                            "channel" : "Security",
                            "task" : "Special Logon",
                            "keywords" : [
                                "审核成功"
                            ],
                            "computer_name" : "jyr-PC",
                            "provider_name" : "Microsoft-Windows-Security-Auditing",
                            "api" : "wineventlog",
                            "provider_guid" : "{54849625-5478-4994-a5ba-3e3b0328c30d}",
                            "event_id" : 4672,
                            "record_id" : 490664,
                            "event_data" : {
                                "PrivilegeList" : [
                                    "SeAssignPrimaryTokenPrivilege",
                                    "SeTcbPrivilege",
                                    "SeSecurityPrivilege",
                                    "SeTakeOwnershipPrivilege",
                                    "SeLoadDriverPrivilege",
                                    "SeBackupPrivilege",
                                    "SeRestorePrivilege",
                                    "SeDebugPrivilege",
                                    "SeAuditPrivilege",
                                    "SeSystemEnvironmentPrivilege",
                                    "SeImpersonatePrivilege",
                                    "SeDelegateSessionUserImpersonatePrivilege"
                                ],
                                "SubjectUserSid" : "S-1-5-18",
                                "SubjectUserName" : "SYSTEM",
                                "SubjectDomainName" : "NT AUTHORITY",
                                "SubjectLogonId" : "0x3e7"
                            },
                            "opcode" : "信息",
                            "process" : {
                                "thread" : {
                                    "id" : 860
                                },
                                "pid" : 816
                            },
                            "logon" : {
                                "id" : "0x3e7"
                            }
                        },
                        "event" : {
                            "kind" : "event",
                            "code" : 4672,
                            "provider" : "Microsoft-Windows-Security-Auditing",
                            "action" : "logged-in-special",
                            "created" : "2020-03-24T00:55:21.502Z",
                            "module" : "security"
                        },
                        "log" : {
                            "level" : "信息"
                        },
                        "message" : "为新登录分配了特殊权限。",
                    "fields" : {
                        "equipmentid" : "1926e695d8284fd7b648fbe807522c36",
                            "ip" : "192.168.200.44"
                    },
                    "user" : {
                        "id" : "S-1-5-18",
                            "name" : "SYSTEM",
                            "domain" : "NT AUTHORITY"
                    },
                    "ecs" : {
                        "version" : "1.4.0"
                    },
                    "host" : {
                        "name" : "jyr-PC",
                            "architecture" : "x86_64",
                            "os" : {
                            "version" : "10.0",
                                "family" : "windows",
                                "name" : "Windows 10 Home China",
                                "kernel" : "10.0.18362.720 (WinBuild.160101.0800)",
                                "build" : "18362.720",
                                "platform" : "windows"
                        },
                        "id" : "f2b13a2b-3f72-48f4-bb44-0003818775e8",
                            "hostname" : "jyr-PC"
                    },
                    "agent" : {
                        "ephemeral_id" : "978bb564-6b2e-4608-aaa5-959f73f14ce0",
                            "hostname" : "jyr-PC",
                            "id" : "ebb8b367-cbea-477a-b645-8def3b1a1e65",
                            "version" : "7.6.0",
                            "type" : "winlogbeat"
                    }
                }
                }
            }
        },
        components:{
            jsonView
        }
    }
</script>

<style scoped>
/deep/ .json-view-container.one-dark{
    background-color: #303e4e!important;
}
/deep/ .json-view-container.one-dark .json-view .json-body .base-line {
    border-left: 1px solid #5f7186!important;
}
.dev-wapper{
    display: flex;
    padding:10px;
    padding-top: 0;
}
.dev-tools-search{
    width: 300px;
    margin-right: 10px;
    background-color: #303e4e;
    height: calc(100vh - 237px);
}
.dev-tools-json{
    flex: 1;
    height: calc(100vh - 237px);
    overflow: auto;

}
</style>
