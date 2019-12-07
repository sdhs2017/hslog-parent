<template>
    <div class="content-bg">
        <div class="top-title">在线监控</div>
        <div class="monitoring-wapper">
           <div class="input-box">
               <label>数据接收协议</label>
               <el-input v-model="protocol" disabled></el-input>
           </div>
            <div class="input-box">
                <label>Rsyslog格式</label>
                <el-input v-model="rsyslog" disabled></el-input>
            </div>
            <div class="input-box">
                <label>数据存放路径 （排除本机的日志）</label>
                <el-input v-model="path" disabled></el-input>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "monitoring",
        data() {
            return {
                protocol:'UDP/TCP',
                rsyslog:'$template myformat,"<%PRI%> %$NOW% %TIMESTAMP:8:15% %HOSTNAME% %FROMHOST-IP% %syslogtag:1:32%%msg:::sp-if-no-1st-sp%%msg%\\n"',
                path:'fromhost-ip, !isequal, "127.0.0.1" /var/log/hostlog/test.log'
            }
        }
    }
</script>

<style scoped>
    .monitoring-wapper{
        padding: 10px 30px;
    }
    .input-box{
        margin: 30px 0;
    }
    .input-box label{
        color: #409eff;
        margin-bottom: 5px;
    }
</style>
