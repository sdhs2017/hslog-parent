<template>
    <div class="content-bg">
        <div class="top-title">'{{equipmentName}}' 潜在威胁分析</div>
        <div class="equipment-threat-content">
            <div class="threat-wapper hige-threat-wapper">
                <div class="threat-top">
                    <h5>高危事件</h5>
                </div>
                <div class="threat-content">
                    <div class="threat-item" v-for="(item,index) in higeThreatArr" :key="index">
                        <h4 class="threat-item-title">{{currentEventObj[item.event_type]}}</h4>
                        <div class="threat-item-content">
                            <p class="per">实时/阈值：{{Number(item.per)}}%</p>
                            <p class="time">
                                <span>起始：</span>
                                <span>{{item.starttime}}</span>
                            </p>
                            <p class="time">结束：当前时间</p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="threat-wapper middle-threat-wapper">
                <div class="threat-top">
                    <h5>中危事件</h5>
                </div>
                <div class="threat-content">
                    <div class="threat-item" v-for="(item,index) in middleThreatArr" :key="index">
                        <h4 class="threat-item-title">{{currentEventObj[item.event_type]}}</h4>
                        <div class="threat-item-content">
                            <p class="per">实时/阈值：{{Number(item.per)}}%</p>
                            <p class="time">
                                <span>起始：</span>
                                <span>{{item.starttime}}</span>
                            </p>
                            <p class="time">结束：当前时间</p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="threat-wapper low-threat-wapper">
                <div class="threat-top">
                    <h5>低危事件</h5>
                </div>
                <div class="threat-content">
                    <div class="threat-item" v-for="(item,index) in lowThreatArr" :key="index">
                        <h4 class="threat-item-title">{{currentEventObj[item.event_type]}}</h4>
                        <div class="threat-item-content">
                            <p class="per">实时/阈值：{{Number(item.per)}}%</p>
                            <p class="time">
                                <span>起始：</span>
                                <span>{{item.starttime}}</span>
                            </p>
                            <p class="time">结束：当前时间</p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="threat-wapper all-threat-wapper">
                <div class="threat-top">
                    <h5>未设置告警的事件</h5>
                </div>
                <div class="threat-content">
                    <div class="threat-item" v-for="(item,index) in allThreatArr" :key="index">
                        <h4 class="threat-item-title">{{currentEventObj[item.event_type]}}</h4>
                        <div class="threat-item-content">
                            <p>未设置告警</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
</template>

<script>
    export default {
        name: "equipmentThreat",
        data() {
            return {
                equipmentName:'',//资产名称
                equipmentId:'',//资产id
                logType:'',//日志类型
                higeThreatArr:[],
                middleThreatArr:[],
                lowThreatArr:[],
                allThreatArr:[],
                currentEventObj:{},
                syslogEventObj:{
                    poweroff:"主机关机",
                    NetworkManager:"网络服务",
                    usb:"usb外接",
                    sshd:"通过ssh方式进行操作",
                    login:"用户登录",
                    su:"通过su方式登录",
                    session:"开启新的会话窗口",
                    rsyslogd:"rsyslog自身日志",
                    pci:"pci日志",
                    pci_bus:"pci_bus日志",
                    ACPI:"ACPI日志",
                    PM:"PM日志",
                    SRAT:"SRAT日志",
                    crond:"定时任务"

                },
                winlogEventObj:{
                    login_successful:"登录成功",
                    mstsc_successful:"远程登录成功",
                    mstsc_interrupt:"远程连接中断",
                    log_off:"用户注销"
                },
            }
        },
        methods:{
            /*获取数据*/
            getEquipmentThreat(){
                this.$nextTick(()=> {
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/log/getCountGroupByEventstype.do', this.$qs.stringify({equipmentid:this.equipmentId,type:this.logType}))
                        .then(res => {
                            layer.closeAll();
                            for(let i in res.data){
                                //判断比值大小
                                let pieVal = Number(res.data[i].per);
                                if(pieVal <= 50){//普通事件
                                   this.lowThreatArr.push(res.data[i])
                                }else if(pieVal > 50 && pieVal <= 80){//中危事件
                                    this.middleThreatArr.push(res.data[i])
                                }else if(pieVal > 80){//高危事件
                                    this.higeThreatArr.push(res.data[i])
                                }
                                //在未设置告警事件数组中删除
                                for(let j in this.allThreatArr){
                                    if(this.allThreatArr[j].event_type === res.data[i].event_type){
                                        this.allThreatArr.splice(j,1)
                                    }
                                }
                            }

                        })
                        .catch(err =>{
                            layer.closeAll();
                            layer.msg('获取信息失败',{icon: 5});
                        })
                })
            }
        },
        watch:{
            'equipmentId'(){
                //获取数据
                this.getEquipmentThreat();
            },
            'logType'(){
                if(this.logType === 'syslog' ){
                    this.currentEventObj = this.syslogEventObj;
                }else if(this.logType === 'winlog'){
                    this.currentEventObj = this.winlogEventObj;
                }
                for(let i in this.currentEventObj){
                    let eventObj = {};
                    eventObj.event_type = i;
                    this.allThreatArr.push(eventObj)
                }
            }
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                vm.$options.name = 'equipmentThreat'+ to.query.id;
                //修改data参数
                vm.equipmentName = to.query.name;
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'equipmentThreat'+to.query.id,
                    component:'equipment/equipmentThreat.vue',
                    title:'威胁分析'
                }
                sessionStorage.setItem('/equipmentThreat'+to.query.id,JSON.stringify(obj))
                if(vm.equipmentId === '' || vm.equipmentId !== to.query.id){
                    vm.equipmentId = to.query.id;
                    vm.logType = to.query.logType;
                }

            })

        }
    }
</script>

<style scoped>
    .equipment-threat-content{
        padding: 20px;
    }
    .threat-wapper{
        background: #303e4e;
        margin-bottom: 20px;
    }
    .threat-top{
        height: 50px;
        line-height: 50px;
        padding: 0 10px;
        border-bottom: 1px solid #4d657f;

    }
    .threat-top h5{
        font-size: 14px;
        font-weight: 600;
    }
    .threat-content{
        min-height: 40px;
        background: #3a4a5d;
        overflow: hidden;
    }
    .threat-item{
        width: 150px;
        height: 150px;
        margin: 20px;
        background: #ccc;
        border-radius: 5px;
        float: left;
        color: #fff;
        font-weight: 600;
        font-size: 13px;
    }
    .threat-item:hover{
        cursor: pointer;
        box-shadow: 0 15px 30px rgba(0,0,0,0.1);
        transform: translate3d(0,-2px,0);
    }
    .threat-item-title{
        height: 50px;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-top: 6px;
        margin-bottom: 0;
        font-size: 16px;
        text-align: center;
        font-weight: 600;
        overflow: hidden;
        background: rgba(0,0,0,0.1);
    }
    .threat-item-content{
        height: 87px;
        /* line-height: 60px; */
        text-align: center;
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
        align-items: center;
    }
    .hige-threat-wapper .threat-item{
        background: #f55446;
    }
    .middle-threat-wapper .threat-item{
        background: #f1ae09;
    }
    .low-threat-wapper .threat-item{
        background: #55c45d;
    }
    .all-threat-wapper .threat-item{
        background: #5e7896;
    }
</style>
