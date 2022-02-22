<template>
    <div class="content-bg"  v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
        <div class="top-title">控制中心</div>
        <div class="control-wapper">
            <div class="serviceConBox">
                <div class="beats-status" style="border-right: 1px dashed #40566d;">
                    <span>Agent采集:</span>
                    <span class="serviceStatus" :style="beatState==='未开启'?{ color:'#d9534f'}:{ color:'#1ab394'}">{{beatState}}</span>
                </div>
                <div class="syslog-status">
                    <span>Syslog采集:</span>
                    <span class="serviceStatus" :style="syslogState==='未开启'?{ color:'#d9534f'}:{ color:'#1ab394'}">{{syslogState}}</span>
                </div>
            </div>
            <div class="btnConBox">
                <div class="btnConBoxTop">
                    <div class="left-wapper serviceBtn">
                        <div class="scroll-wapper" :style="{left:beatsLeft}">
                            <div class="play-beats" @click="playBeats">
                                <div class="play-i">
                                    <i class="el-icon-caret-right"></i>
                                </div>
                                <div class="play-t ">
                                    <div>
                                        <p class="btnTitle">开启Agent采集服务</p>
                                        <p class="btnDescribe">开始收集各个Agent发送过来的数据，范式化后入库</p>
                                    </div>
                                </div>
                            </div>
                            <div class="stop-beats" @click="stopBeats">
                                <div class="play-i">
                                    <i class="el-icon-circle-close"></i>
                                </div>
                                <div class="play-t ">
                                    <div>
                                        <p class="btnTitle">关闭Agent采集服务</p>
                                        <p class="btnDescribe">停止收集各个Agent发送过来的数据</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                    <div class="right-wapper serviceBtn">
                        <div class="scroll-wapper" :style="{left:syslogLeft}">
                            <div class="play-syslog" @click="playSyslog">
                                <div class="play-i">
                                    <i class="el-icon-caret-right"></i>
                                </div>
                                <div class="play-t ">
                                    <div>
                                        <p class="btnTitle">开启Syslog采集服务</p>
                                        <p class="btnDescribe">开始收集各个资产发送过来的日志数据，范式化后入库</p>
                                    </div>
                                </div>
                            </div>
                            <div class="stop-syslog" @click="stopSyslog">
                                <div class="play-i">
                                    <i class="el-icon-circle-close"></i>
                                </div>
                                <div class="play-t ">
                                    <div>
                                        <p class="btnTitle">关闭Syslog采集服务</p>
                                        <p class="btnDescribe">停止收集各个资产发送过来的数据</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!--<div class="serviceBtn" id="play_service" title="点击开启服务" @click="playService">
                        <div>
                            <i class="el-icon-caret-right"></i>
                        </div>
                        <div>
                            <div>
                                <p class="btnTitle">开启服务</p>
                                <p class="btnDescribe">开始收集各个资产发送过来的日志数据，范式化后入库</p>
                            </div>
                        </div>
                    </div>-->
                </div>
                <div  class="btnConBoxBottom">
                   <!-- <div class="serviceBtn" id="stop_service" title="点击停止服务" @click="stopService">
                        <i class="el-icon-circle-close"></i>
                        <p class="btnTitle">停止服务</p>
                        <p class="btnDescribe">停止收集各个资产发送过来的日志数据</p>
                    </div>-->
                    <div class="serviceBtn" id="initialize" title="点击初始化" @click="initialize">
                        <i class="el-icon-refresh-right"></i>
                        <p class="btnTitle">初始化</p>
                        <p class="btnDescribe">对集群的数据结构进行初始化，保证日志数据正常采集到数据中心</p>
                    </div>
                    <div class="serviceBtn" id="backup" title="点击备份数据" @click="getBackupConfig()">
                        <i class="el-icon-printer"></i>
                        <p class="btnTitle">备份</p>
                        <p class="btnDescribe">对集群中的日志数据进行完整的备份操作</p>
                    </div>
                    <div class="serviceBtn" id="recover" title="点击恢复备份" @click="getRecoverList()">
                        <i class="el-icon-refresh"></i>
                        <p class="btnTitle">恢复备份</p>
                        <p class="btnDescribe">对删除后的数据进行恢复操作（需对删除的数据先进行备份操作）</p>
                    </div>
                </div>
            </div>
        </div>
        <el-dialog
            title="备份设置"
            :visible.sync="backupDialogVisible"
            width="400px"
            >
            <div>
                <el-form ref="form"  label-width="50px">
                    <el-form-item label="周期">
                        <el-input v-model="period"></el-input>
                    </el-form-item>
                    <p style="color: #e4956d; padding-left: 10px;font-size: 10px;">注：填写cron表达式</p>
                    <p style="color: #e4956d; padding-left: 10px;font-size: 10px;">例：0*/10***? (每10分钟的0秒执行一次)</p>
                </el-form>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="backupDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="backup()">确 定</el-button>
            </span>
        </el-dialog>
        <el-dialog
            title="还原备份"
            :visible.sync="recoverDialogVisible"
            width="600px"
        >
            <div style="height: 400px;overflow: auto">
                <el-radio-group v-model="recoverChecked">
                    <el-radio v-for="(item,i) in listArr" :label="item.label" :key="item.label" style="margin-bottom: 10px"></el-radio>
                </el-radio-group>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="recoverDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="recover()">确 定</el-button>
            </span>
        </el-dialog>
    </div>
    
</template>

<script>
    export default {
        name: "controlCenter2",
        data() {
            return {
                loading:false,
                beatState:'未开启',
                syslogState:'未开启',
                beatsLeft:'0px',
                syslogLeft:'0px',
                interval:'',
                backupDialogVisible:false,//备份弹窗
                recoverDialogVisible:false,//还原弹窗
                period:'0 */1 * * * ?',//周期
                listArr:[],
                recoverChecked:'',
            }
        },
        created(){
            this.getBeatsState();
            this.getSyslogState();
            //查看服务状态
             this.interval = setInterval(()=>{
                 this.getBeatsState();
                 this.getSyslogState();
             },5000);

        },
        methods:{
            /*获取beats服务状态*/
            getBeatsState(){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/collector/getAgentKafkaListenerState.do','')
                        .then(res =>{
                            if(res.data[0].state === true){
                                this.beatState='已开启';
                                this.beatsLeft = '-301px'
                            }else if(res.data[0].state === false){
                                this.beatState='未开启';
                                this.beatsLeft = '0px'
                            }else{
                                console.log(data)
                            }
                        })
                        .catch(err =>{

                        })
                })
            },
            /*获取syslog服务状态*/
            getSyslogState(){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/collector/getSyslogKafkaListenerState.do','')
                        .then(res =>{
                            if(res.data[0].state === true){
                                this.syslogState='已开启';
                                this.syslogLeft = '-301px'
                            }else if(res.data[0].state === false){
                                this.syslogState='未开启';
                                this.syslogLeft = '0px'
                            }else{
                                console.log(data)
                            }
                        })
                        .catch(err =>{

                        })
                })
            },
            /*开启Agent服务*/
            playBeats(){
                //询问框
                layer.confirm('是否开启Agent采集？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.close(index);
                    this.loading = true;
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/collector/startAgentKafkaListener.do','')
                            .then(res=>{
                                this.loading = false;
                                if(res.data.success === 'true'){
                                    layer.msg(res.data.message,{icon: 1});
                                    this.beatState='已开启';
                                    this.beatsLeft = '-301px'
                                }else if(res.data.success === 'false'){
                                    layer.msg(res.data.message,{icon: 5});
                                }
                            })
                            .catch(err=>{
                                this.loading = false;
                                layer.msg('启动失败',{icon: 5});
                            })
                    })
                }, ()=>{
                    layer.close();
                });
            },
            /*停止Agent服务*/
            stopBeats(){
                //询问框
                layer.confirm('是否关闭Agent采集服务？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    //关闭轮训
                    clearInterval(this.interval)

                    layer.close(index);
                    this.loading = true;
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/collector/stopAgentKafkaListener.do','')
                            .then(res=>{
                                this.loading = false;
                                if(res.data.success === 'true'){
                                    layer.msg(res.data.message,{icon: 1});
                                    this.beatState='未开启';
                                    this.beatsLeft = '0px'
                                }else if(res.data.success === 'false'){
                                    layer.msg(res.data.message,{icon: 5});
                                }
                                //开启轮训
                                this.interval = setInterval(()=>{
                                    this.getBeatsState();
                                    this.getSyslogState();
                                },5000);
                            })
                            .catch(err=>{
                                this.loading = false;
                                layer.msg('停止服务失败',{icon: 5});
                                //开启轮训
                                this.interval = setInterval(()=>{
                                    this.getBeatsState();
                                    this.getSyslogState();
                                },5000);
                            })
                    })
                }, ()=>{
                    layer.close();
                });
            },
            /*开启syslog服务*/
            playSyslog(){
                //询问框
                layer.confirm('是否开启syslog采集？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.close(index);
                    this.loading = true;
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/collector/startSyslogKafkaListener.do  ','')
                            .then(res=>{
                                this.loading = false;
                                if(res.data.success === 'true'){
                                    layer.msg(res.data.message,{icon: 1});
                                    this.syslogState='已开启';
                                    this.syslogLeft = '-301px'
                                }else if(res.data.success === 'false'){
                                    layer.msg(res.data.message,{icon: 5});
                                }
                            })
                            .catch(err=>{
                                this.loading = false;
                                layer.msg('启动失败',{icon: 5});
                            })
                    })
                }, ()=>{
                    layer.close();
                });
            },
            /*停止syslog服务*/
            stopSyslog(){
                //询问框
                layer.confirm('是否关闭syslog采集服务？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    //关闭轮训
                    clearInterval(this.interval)

                    layer.close(index);
                    this.loading = true;
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/collector/stopSyslogKafkaListener.do','')
                            .then(res=>{
                                this.loading = false;
                                if(res.data.success === 'true'){
                                    layer.msg(res.data.message,{icon: 1});
                                    this.syslogState='未开启';
                                    this.syslogLeft = '0px'
                                }else if(res.data.success === 'false'){
                                    layer.msg(res.data.message,{icon: 5});
                                }
                                //开启轮训
                                this.interval = setInterval(()=>{
                                    this.getBeatsState();
                                    this.getSyslogState();
                                },5000);
                            })
                            .catch(err=>{
                                this.loading = false;
                                layer.msg('停止服务失败',{icon: 5});
                                //开启轮训
                                this.interval = setInterval(()=>{
                                    this.getBeatsState();
                                    this.getSyslogState();
                                },5000);
                            })
                    })
                }, ()=>{
                    layer.close();
                });
            },
            /*初始化*/
            initialize(){
                //询问框
                layer.confirm('是否初始化？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.close(index);
                    this.loading = true;
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/log/createIndexAndMapping4Beats.do','')
                            .then(res=>{
                                this.loading = false;
                                if(res.data[0].state === true){
                                    layer.msg(res.data[0].msg,{icon: 1});
                                }else if(res.data[0].state === false){
                                    layer.msg(res.data[0].msg,{icon: 5});
                                }
                            })
                            .catch(err=>{
                                this.loading = false;
                                layer.msg('初始化失败',{icon: 5});
                            })
                    })
                }, ()=>{
                    layer.close();
                });
            },
            getBackupConfig(){
                /*this.loading = true;
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/',this.$qs.stringify())
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){

                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })*/
                //获取本地存储的备份周期设置
                let  localPeriod = localStorage.getItem('backupPeriod');
                if(localPeriod){
                    this.period = localPeriod
                }
                console.log(localPeriod)
                this.backupDialogVisible = true;
            },
            /*备份*/
            backup(){
                /*//询问框
                layer.confirm('是否备份日志数据？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.close(index);
                    this.loading = true;
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/manage/createSnapshotByIndices.do','')
                            .then(res=>{
                                this.loading = false;
                                if(res.data[0].state === true){
                                    layer.msg(res.data[0].msg,{icon: 1});
                                }else if(res.data[0].state === false){
                                    layer.msg(res.data[0].msg,{icon: 5});
                                }
                            })
                            .catch(err=>{
                                this.loading = false;
                                layer.msg('备份失败',{icon: 5});
                            })
                    })
                }, ()=>{
                    layer.close();
                });*/
                if(this.period == ''){
                    layer.msg('请填写备份周期',{icon: 5});
                }else{
                    this.$nextTick(()=>{
                        this.loading = true;
                        this.$axios.post(this.$baseUrl+'/manage/createPolicySnapshotByIndices.do',this.$qs.stringify({
                            cron:this.period
                        }))
                            .then(res=>{
                                this.loading = false;
                                let obj = res.data;
                                if(obj.success == 'true'){
                                    localStorage.setItem('backupPeriod',this.period);
                                    layer.msg(obj.message,{icon:1})
                                    this.backupDialogVisible = false
                                }else{
                                    layer.msg(obj.message,{icon:5})
                                }
                            })
                            .catch(err=>{
                                 this.loading = false;
                            })
                    })
                }
               /* this.backupDialogVisible = true;*/
            },
            //获取备份列表
            getRecoverList(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/manage/getSnapshotList.do',this.$qs.stringify())
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                this.listArr = [];
                                obj.data.forEach((item)=>{
                                    let obj = {
                                        value:item,
                                        label:item
                                    }
                                    this.listArr.push(obj)

                                })
                                console.log(this.listArr)
                                this.recoverDialogVisible = true;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            /*恢复*/
            recover(){
                /*//询问框
                layer.confirm('是否恢复日志数据？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.close(index);
                    this.loading = true;
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/manage/restore.do','')
                            .then(res=>{
                                this.loading = false;
                                if(res.data[0].state === true){
                                    layer.msg(res.data[0].msg,{icon: 1});
                                }else if(res.data[0].state === false){
                                    layer.msg(res.data[0].msg,{icon: 5});
                                }
                            })
                            .catch(err=>{
                                this.loading = false;
                                layer.msg('恢复失败',{icon: 5});
                            })
                    })
                }, ()=>{
                    layer.close();
                });*/
                if(this.recoverChecked == ''){
                    layer.msg('请选择恢复项',{icon: 5});
                }else{
                    this.$nextTick(()=>{
                        this.loading = true;
                        this.$axios.post(this.$baseUrl+'/manage/restoreBySnapshot.do',this.$qs.stringify({
                            snapshotName:this.recoverChecked
                        }))
                            .then(res=>{
                                this.loading = false;
                                let obj = res.data;
                                if(obj.success == 'true'){
                                    layer.msg(obj.message,{icon:1})
                                    this.recoverDialogVisible = false;
                                }else{
                                    layer.msg(obj.message,{icon:5})
                                }
                            })
                            .catch(err=>{
                                 this.loading = false;
                            })
                    })
                }
                // this.recoverDialogVisible = true;
            }
        },
        beforeDestroy() {
            clearInterval(this.interval)
        }
    }
</script>

<style scoped>
    .control-wapper{
        padding: 10px;
        font-size: 14px;
    }
    .btnConBox{
        width:630px;
        height:432px;
        margin:20px auto;
    }
    .serviceConBox{
        width:620px;
        height:80px;
        border:2px solid #4781bb;
        margin-bottom:20px;
        margin:0 auto;
        position: relative;
        right: 5px;
        text-align: center;
        line-height:80px;
        font-size:26px;
        font-weight:600;
        display: flex;
    }
    .serviceConBox>div{
        text-align: center;
        width: 50%;
    }
    .btnConBoxTop,.btnConBoxBottom{
        height:220px;
        display: flex;
    }
    .btnConBoxTop>div{
        width: 301px;
        margin-bottom: 10px;
        border:2px solid #4781bb;
        float: left;
        text-align: center;
        color: #2c76bd;
        cursor: pointer;
        background: 0;
        transition: all .2s linear;
        overflow: hidden;
    }
    .btnConBoxTop .left-wapper{
        margin-right: 10px;

    }
    .serviceBtn{
        width: 200px;
        height: 210px;
        float: left;
        text-align: center;
        color: #2c76bd;
        cursor: pointer;
        margin-right: 10px;
        margin-bottom: 10px;
        border: 0;
        border:2px solid #4781bb;
        /*  border-radius: 5px;
         box-shadow: 0 3px 15px #4781bb; */
        background: 0;
        transition: all .2s linear;
    }
    .serviceBtn:hover{
        transform: translate3d(0 ,-1px ,0);
        box-shadow: 0 10px 30px #4781bb;
    }
    .serviceBtn i{
        font-size: 52px;
        line-height: 95px;
        color: #2c76bd;
    }
    .scroll-wapper{
        height: 210px;
        position: relative;
        transition: all 0.3s linear;
        left: 0;
        width: 600px;
    }
    .scroll-wapper>div{
        width: 300px;
        height: 210px;
        float: left;
        box-sizing: border-box;
        padding: 20px;
        padding-top: 10px;
    }
    .play-i i{
        font-size: 72px;
    }
    #play_service{
        width:410px;
    }
    #play_service i{
        font-size:72px;
    }
    #play_service>div{
        width:190px;
        float:left;
        height:210px;
        display: flex;
        justify-content: center;
        align-items: center;
    }
    .btnTitle{
        color: #3298fb;
        font-size: 24px;
        font-weight:600;
        margin-bottom: 10px;
    }
    .btnDescribe{
        padding:0 10px;
        color:#6792bd;
    }
</style>
