<template>
    <div class="content-bg">
        <div class="top-title">数据源添加</div>
        <div class="source-content">
            <div class="add-type">
                <div class="add-item upload" @click="goToUploadFile">
                    <i class="el-icon-lx-file"></i>
                    <p class="title">本地上传</p>
                    <p>上传本地日志文件</p>
                    <p class="last">本地日志文件 <br/>本地结构化文件</p>
                </div>
                <div class="add-item monitoring" @click="goToMonitoring">
                    <i class="el-icon-lx-attention"></i>
                    <p class="title">在线监控</p>
                    <p>在此服务上的文件和端口</p>
                    <p class="last">syslog-wmi-snmp-tcp/udp-脚本外部数据源输入</p>
                </div>
            </div>
            <div class="instruction-wapper">
                <h3>通用数据来源：</h3>
                <div class="instruction-ul-wapper">
                    <ul>
                        <h4><i class="el-icon-printer"></i>网络设备</h4>
                        <li>交换机</li>
                        <li>路由器</li>
                    </ul>
                    <ul>
                        <h4><i class="el-icon-bell"></i> 安全</h4>
                        <li>IPS</li>
                        <li>IDS</li>
                        <li>抗DOS</li>
                        <li>防火墙</li>
                    </ul>
                    <ul>
                        <h4><i class="el-icon-mobile-phone"></i>主机</h4>
                        <li>Windows</li>
                        <li>Linux</li>
                        <li>虚拟机</li>
                    </ul>
                    <ul>
                        <h4><i class="el-icon-lx-cascades"></i>应用程序服务</h4>
                        <li>Tomcat</li>
                        <li>Apache</li>
                        <li>IIS</li>
                        <li>Weblogic</li>
                        <li>Mysql</li>
                        <li>Oracle</li>
                        <li>Sqlserver</li>
                        <li>Db2</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
    
</template>

<script>
    export default {
        name: "sourceFile",
        data() {
            return {}
        },
        methods:{
            /*跳转到uploadFile页面*/
            goToUploadFile(){
                this.$router.push({path:'/uploadFile'})
            },
            /*跳转到Monitoring页面*/
            goToMonitoring(){
                this.$router.push({path:'/monitoring'})
            }
        }
    }
</script>

<style scoped>
    .source-content{
        padding: 10px;
    }
    .add-type{
        display: flex;
        justify-content: center;
        margin: 30px 0;
    }
    .add-item{
        width: 200px;
        height: 210px;
        border: 1px solid #409eff;
        text-align: center;
        color: #2c76bd;
        background: #303e4e;
        cursor: pointer;
        margin-left: 30px;
        margin-right: 30px;

    }
    .add-item:hover{
        background: #3f5267;
    }
    .add-item i{
        font-size: 52px;
        line-height: 95px;
        color: #2c76bd;
    }
    .add-item .title{
        font-size: 24px;
        font-weight: 600px;
        margin: 0;
    }
    .add-item p{
        font-size: 14px;
        margin: 8px 0;
    }
    .add-item .last{
        color: #bfcbd9;
        font-size: 14px;
        padding:0 5px;
    }
    .instruction-wapper{
        background: #303e4e;
    }
    .instruction-wapper h3{
        padding-top: 30px;
        padding-left: 50px;
        margin-top: 30px;
        font-size: 21px;
    }
    .instruction-ul-wapper{
        padding: 10px 20px;
        padding-top: 0;
        overflow: hidden;
        display: flex;
        justify-content: center;
    }
    .instruction-ul-wapper ul{
        margin: 20px 40px;
    }
    .instruction-wapper h4{
        color: #56a4ef;
        margin-bottom: 5px;
    }
    .instruction-wapper h4 i{
        font-size: 18px;
        margin-right: 8px;
    }
    .instruction-wapper li{
        font-size:13px;
        padding-left: 30px;
    }
</style>
