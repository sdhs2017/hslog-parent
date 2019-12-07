<template>
    <div class="content-bg">
        <div class="top-title"></div>
        <div class="content">
            <div class="service-state-wapper">
                <span>服务状态：</span>
                <span class="service-state" :style="state==='未开启'?{ color:'#d9534f'}:{ color:'#1ab394'}">{{state}}</span>
            </div>
            <div class="btn-wapper">
                <div class="service-btn" @click="playService">
                    <i class="el-icon-caret-right"></i>
                    <p class="btn-title">开启服务</p>
                    <p class="btn-describe">开始收集网络流量数据包，范式化后入库</p>
                </div>
                <div class="service-btn" @click="stopService">
                    <i class="el-icon-circle-close"></i>
                    <p class="btn-title">停止服务</p>
                    <p class="btn-describe">停止收集网络流浪数据包</p>
                </div>
            </div>
        </div>
    </div>
    
</template>

<script>
    export default {
        name: "flowServiceManage",
        data() {
            return {
                state:'未开启'
            }
        },
        created(){
            //查看服务状态
            setInterval(this.getServiceStatus,5000);
        },
        methods:{
            /*查看服务状态*/
            getServiceStatus(){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/collector/statePcap4jCollector.do','')
                        .then(res =>{
                            if(res.data[0].state === true){
                                this.state='已开启';
                            }else if(res.data[0].state === false){
                                this.state='未开启';
                            }else{
                                console.log(data)
                            }
                        })
                        .catch(err =>{

                        })
                })
            },/*启动服务*/
            playService(){
                //询问框
                layer.confirm('是否开启服务？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.close(index);
                    layer.load(1)
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/collector/startPcap4jCollector.do','')
                            .then(res=>{
                                layer.closeAll('loading');
                                if(res.data[0].state === true){
                                    layer.msg(res.data[0].msg,{icon: 1});
                                    this.state = '已开启'
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
            /*停止服务*/
            stopService(){
                //询问框
                layer.confirm('是否停止服务？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.close(index);
                    layer.load(1)
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/collector/stopPcap4jCollector.do','')
                            .then(res=>{
                                layer.closeAll('loading');
                                if(res.data[0].state === true){
                                    layer.msg(res.data[0].msg,{icon: 1});
                                    this.state = '未开启'
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
            }
        }
    }
</script>

<style scoped>
    .service-state-wapper{
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
    }
    .btn-wapper{
        width:638px;
        height:432px;
        margin:20px auto;
    }
    .service-btn{
        width: 305px;
        height: 300px;
        float: left;
        text-align: center;
        color: #2c76bd;
        cursor: pointer;
        margin-right: 10px;
        margin-bottom: 10px;
        border: 0;
        border: 2px solid #4781bb;
        background: 0;
        transition: all .2s linear;
    }
    .service-btn:hover{
        transform: translate3d(0 ,-1px ,0);
        box-shadow: 0 10px 30px #4781bb;
    }
    .service-btn i {
         font-size: 100px;
         line-height: 177px;
         color: #2c76bd;
     }
    .service-btn .btn-title {
        color: #3298fb;
        font-size: 24px;
        font-weight: 600;
        margin-bottom: 20px;
    }
    .service-btn .btn-describe {
        padding: 0 10px;
        color: #6792bd;
    }
</style>
