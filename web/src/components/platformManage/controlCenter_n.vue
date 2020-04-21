<template>
    <div class="content-bg">
        <div class="top-title">控制中心</div>
        <div class="control-wapper">
            <div class="serviceConBox">
                <div class="beats-status" style="border-right: 1px dashed #40566d;">
                    <span>beats采集:</span>
                    <span class="serviceStatus" :style="beatState==='未开启'?{ color:'#d9534f'}:{ color:'#1ab394'}">{{beatState}}</span>
                </div>
                <div class="syslog-status">
                    <span>syslog采集:</span>
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
                                        <p class="btnTitle">开启beats采集服务</p>
                                        <p class="btnDescribe">开始收集各个beats发送过来的数据，范式化后入库</p>
                                    </div>
                                </div>
                            </div>
                            <div class="stop-beats" @click="stopBeats">
                                <div class="play-i">
                                    <i class="el-icon-circle-close"></i>
                                </div>
                                <div class="play-t ">
                                    <div>
                                        <p class="btnTitle">关闭beats采集服务</p>
                                        <p class="btnDescribe">停止收集各个beats发送过来的数据</p>
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
                                        <p class="btnTitle">开启syslog采集服务</p>
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
                                        <p class="btnTitle">关闭syslog采集服务</p>
                                        <p class="btnDescribe">开始收集各个资产发送过来的日志数据，范式化后入库</p>
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
                        <i class="el-icon-lx-refresh"></i>
                        <p class="btnTitle">初始化</p>
                        <p class="btnDescribe">对集群的数据结构进行初始化，保证日志数据正常采集到数据中心</p>
                    </div>
                    <div class="serviceBtn" id="backup" title="点击备份数据" @click="backup">
                        <i class="el-icon-printer"></i>
                        <p class="btnTitle">备份</p>
                        <p class="btnDescribe">对集群中的日志数据进行完整的备份操作</p>
                    </div>
                    <div class="serviceBtn" id="recover" title="点击恢复备份" @click="recover">
                        <i class="el-icon-refresh"></i>
                        <p class="btnTitle">恢复备份</p>
                        <p class="btnDescribe">对删除后的数据进行恢复操作（需对删除的数据先进行备份操作）</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
</template>

<script>
    export default {
        name: "controlCenter2",
        data() {
            return {
                beatState:'未开启',
                syslogState:'未开启',
                beatsLeft:'0px',
                syslogLeft:'0px',
                interval:''
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
                    this.$axios.post(this.$baseUrl+'/collector/stateKafkaOfBeatsCollector.do','')
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
                    this.$axios.post(this.$baseUrl+'/collector/stateKafkaCollector.do','')
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
            /*开启beats服务*/
            playBeats(){
                //询问框
                layer.confirm('是否开启beats采集？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.close(index);
                    layer.load(1)
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/collector/startKafkaOfBeatsCollector.do  ','')
                            .then(res=>{
                                layer.closeAll('loading');
                                if(res.data[0].state === true){
                                    layer.msg(res.data[0].msg,{icon: 1});
                                    this.beatState='已开启';
                                    this.beatsLeft = '-301px'
                                }else if(res.data[0].state === false){
                                    layer.msg(res.data[0].msg,{icon: 5});
                                }
                            })
                            .catch(err=>{
                                layer.closeAll('loading');
                                layer.msg('启动失败',{icon: 5});
                            })
                    })
                }, ()=>{
                    layer.close();
                });
            },
            /*停止beats服务*/
            stopBeats(){
                //询问框
                layer.confirm('是否beats采集服务？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.close(index);
                    layer.load(1)
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/collector/stopKafkaOfBeatsCollector.do','')
                            .then(res=>{
                                layer.closeAll('loading');
                                if(res.data[0].state === true){
                                    layer.msg(res.data[0].msg,{icon: 1});
                                    this.beatState='未开启';
                                    this.beatsLeft = '0px'
                                }else if(res.data[0].state === false){
                                    layer.msg(res.data[0].msg,{icon: 5});
                                }
                            })
                            .catch(err=>{
                                layer.closeAll('loading');
                                layer.msg('停止服务失败',{icon: 5});
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
                    layer.load(1)
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/collector/startCollectorState.do  ','')
                            .then(res=>{
                                layer.closeAll('loading');
                                if(res.data[0].state === true){
                                    layer.msg(res.data[0].msg,{icon: 1});
                                    this.syslogState='已开启';
                                    this.syslogLeft = '-301px'
                                }else if(res.data[0].state === false){
                                    layer.msg(res.data[0].msg,{icon: 5});
                                }
                            })
                            .catch(err=>{
                                layer.closeAll('loading');
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
                layer.confirm('是否beats采集服务？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.close(index);
                    layer.load(1)
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/collector/stopKafkaCollector.do','')
                            .then(res=>{
                                layer.closeAll('loading');
                                if(res.data[0].state === true){
                                    layer.msg(res.data[0].msg,{icon: 1});
                                    this.syslogState='未开启';
                                    this.syslogLeft = '0px'
                                }else if(res.data[0].state === false){
                                    layer.msg(res.data[0].msg,{icon: 5});
                                }
                            })
                            .catch(err=>{
                                layer.closeAll('loading');
                                layer.msg('停止服务失败',{icon: 5});
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
                    layer.load(1)
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/log/createIndexAndMapping4Beats.do','')
                            .then(res=>{
                                layer.closeAll('loading');
                                if(res.data[0].state === true){
                                    layer.msg(res.data[0].msg,{icon: 1});
                                }else if(res.data[0].state === false){
                                    layer.msg(res.data[0].msg,{icon: 5});
                                }
                            })
                            .catch(err=>{
                                layer.closeAll('loading');
                                layer.msg('初始化失败',{icon: 5});
                            })
                    })
                }, ()=>{
                    layer.close();
                });
            },
            /*备份*/
            backup(){
                //询问框
                layer.confirm('是否备份日志数据？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.close(index);
                    layer.load(1)
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/manage/createSnapshotByIndices.do','')
                            .then(res=>{
                                layer.closeAll('loading');
                                if(res.data[0].state === true){
                                    layer.msg(res.data[0].msg,{icon: 1});
                                }else if(res.data[0].state === false){
                                    layer.msg(res.data[0].msg,{icon: 5});
                                }
                            })
                            .catch(err=>{
                                layer.closeAll('loading');
                                layer.msg('备份失败',{icon: 5});
                            })
                    })
                }, ()=>{
                    layer.close();
                });
            },
            /*恢复*/
            recover(){
                //询问框
                layer.confirm('是否恢复日志数据？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.close(index);
                    layer.load(1)
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/manage/restore.do','')
                            .then(res=>{
                                layer.closeAll('loading');
                                if(res.data[0].state === true){
                                    layer.msg(res.data[0].msg,{icon: 1});
                                }else if(res.data[0].state === false){
                                    layer.msg(res.data[0].msg,{icon: 5});
                                }
                            })
                            .catch(err=>{
                                layer.closeAll('loading');
                                layer.msg('恢复失败',{icon: 5});
                            })
                    })
                }, ()=>{
                    layer.close();
                });
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
