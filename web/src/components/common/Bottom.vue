<template>
    <div class="bottom">
        <div class="b-left"></div>
        <div class="b-right">
            <div class="threshold">{{thresholdText}} <span class="set-rang" @click="setRang">（阈值设置）</span> </div>
            <div class="systemIp">系统IP：<span style="color: #e4956d;">{{systemIp}}</span> <span @click="changIp"></span></div>
<!--            <div class="backupConfig">备份时间：<span  @click="backupWapper = true">{{this.backupObj2.backupDate === '' ? '未设置' :this.backupObj2.backupDate}}</span></div>-->
<!--            <div class="company"> 版权所有  © 2019-2020  山东九州信泰信息科技股份有限公司  </div>-->
            <div class="company"> 版权所有  © 2019-2020  山东汇数信息科技有限公司  </div>
        </div>
        <el-dialog title="阈值告警数值大小选择" :visible.sync="diskUsedState" width="440px">
            <el-form>
                <p style="color: #fff;">系统盘阈值大小：{{sysThresholdValue}}%</p>
                <input type="range" v-model="sysThresholdValue" id="myRange1" style="width: 100%;" min = "0" step="1" max="100">
                <p style="color: #fff;">数据盘阈值大小：{{dataThresholdValue}}%</p>
                <input type="range" v-model="dataThresholdValue" id="myRange2" style="width: 100%;" min = "0" step="1" max="100">
            </el-form>
            <p style="color: #fff;display: flex;justify-content: space-between;"><span>总空间 ：<b  style="color: #1ab394;">{{diskObj.sizeNum}}G</b></span> <span>已用空间 ： <b style="color: #e4956d;">{{diskObj.usedNum}}G</b></span></p>
            <div style="color: #fff;padding: 10px;background: #485b71;">
                <p>系统盘 : <span style="color: #1ab394;">{{diskObj.sysAllDisk}}</span>(总) <span style="color: #e4956d;margin-left: 20px;">{{diskObj.sysUsedDisk}}</span>(已用)</p>
                <p>数据盘 : <span style="color: #1ab394;">{{diskObj.dataAllDisk}}</span>(总) <span style="color: #e4956d;margin-left: 20px;">{{diskObj.dataUsedDisk}}</span>(已用)</p>
            </div>
            <p  style="color: #e4956d;">注：阈值修改会影响数据采集，请慎重选择。</p>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="okBtn">确 定</el-button>
                <el-button @click="diskUsedState = false">取 消</el-button>
            </div>
        </el-dialog>
        <el-dialog title="备份设置" :visible.sync="backupWapper" width="440px">
            <el-form label-width="120px" label-position="left">
                <el-form-item label="备份时间:" >
                    <el-date-picker
                        v-model="backupObj.backupDate"
                        type="datetime"
                        format="yyyy-MM-dd HH:mm:ss"
                        placeholder="选择日期时间">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="备份状态:" >
                    <el-switch
                        v-model="backupObj.backupState"
                        active-text="开启"
                        inactive-text="关闭">
                    </el-switch>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="saveBackup()">确 定</el-button>
                <el-button @click="backupWapper = false">取 消</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import {dateFormat} from "../../../static/js/common";

    export default {
        data(){
            return{
                thresholdText:'状态获取中',
                diskUsedState:false,//显示状态-磁盘空间
                systemIp:'获取中',//系统ip
                systemIpChangeState:false,
                sysThresholdValue :92,//系统盘阈值
                dataThresholdValue:85,//数据盘阈值
                diskObj:{
                    sizeNum	: '',//总容量
                    usedNum	: '',//已用容量
                    sysAllDisk	: '',//系统盘总容量
                    sysUsedDisk	: '',//系统盘已用容量
                    dataAllDisk	: '',//数据盘总容量
                    dataUsedDisk: ''//数据盘已用容量
                },
                backupWapper:false,
                backupObj:{
                    backupState:false,
                    backupDate:''
                },
                backupObj2:{
                    backupState:false,
                    backupDate:''
                }
            }
        },
        created() {
            this.getDiskUsed();
            //定时查看阈值情况  100分钟 查看一次
            setInterval(this.getDiskUsed,6000000);
            //获取系统ip
            this.getIp();
            let obj = localStorage.getItem('backupDate');

            if(obj){
                this.backupObj = JSON.parse(obj)
                this.backupObj2 = JSON.parse(obj)
            }
            /*setInterval(()=>{
                let date1 = new Date();
                let date2 = new Date(this.backupObj2.backupDate);
                if(date1 > date2 && this.backupObj2.backupState){
                    this.backup()
                    this.backupObj2.backupState = false;
                    this.backupObj.backupState = false;
                    localStorage.setItem('backupDate',JSON.stringify(this.backupObj))
                }
            },10000)*/
        },
        methods:{
            /*获取磁盘使用大小*/
            getDiskUsed(){
                this.$axios.get(this.$baseUrl+'/manage/getDiskUsage.do',{})
                    .then((res) => {
                        if(res.data.error !== undefined){
                            this.thresholdText=res.data.error;
                            // $(".threshold").css("color","#d9534f");
                        }else{
                            let sizeNum = Number(res.data.size);
                            let usedNum = Number(res.data.used);
                            let percentage = (usedNum/sizeNum)*100;
                            this.diskObj = {
                                sizeNum	: sizeNum,//总容量
                                usedNum	: usedNum,//已用容量
                                sysAllDisk	: res.data.sys_size,//系统盘总容量
                                sysUsedDisk	: res.data.sys_used,//系统盘已用容量
                                dataAllDisk	: res.data.data_size,//数据盘总容量
                                dataUsedDisk: res.data.data_used,//数据盘已用容量
                            };
                            this.thresholdText = '';
                            //判断阈值与实际使用大小
                            if(this.sysThresholdValue <= Number(res.data.sys_per.split('%')[0])){
                                this.thresholdText += `系统盘空间不足,剩余 ${res.data.sys_avail}`;
                                $(".threshold").css("color","#d9534f");
                            }
                            if(this.dataThresholdValue <= Number(res.data.data_per.split('%')[0])){
                                this.thresholdText +=` 数据盘空间不足,剩余 ${res.data.data_avail}`;
                                $(".threshold").css("color","#d9534f");
                            }
                            if(this.thresholdText === ""){
                                this.thresholdText = "磁盘空间充足";
                                $(".threshold").css("color","#1ab394");
                            }
                        }
                    })
                    .catch((err) => {
                        console.log(err)
                    })
            },
            /*设置阈值*/
            setRang(){
                this.diskUsedState = true;
            },
            okBtn(){
                this.thresholdText = '状态获取中';
                this.getDiskUsed();
                this.diskUsedState = false;
            },
            /*获取Ip*/
            getIp(){
                this.$axios.post(this.$baseUrl+'/ip/getIp.do',this.$qs.stringify({}))
                    .then(res=>{
                        if(res.data[0].success == "true"){
                            //填充ip数据
                           this.systemIp = res.data[0].message;
                        }else if(res.data[0].success == "false"){
                            this.systemIp = res.data[0].message;
                        }
                    })
                    .catch(err=>{
                        this.systemIp = '获取失败'
                    })
            },
            /*修改Ip*/
            changIp(){

            },
            /*备份*/
            saveBackup(){
                if(!this.backupObj.backupDate.length){
                    this.backupObj.backupDate = dateFormat('yyyy-mm-dd HH:MM:SS',this.backupObj.backupDate)
                }
                this.backupWapper = false;
                //this.backupObj2 =  JSON.parse(JSON.stringify(this.backupObj2))
                let str = JSON.stringify(this.backupObj);
                this.backupObj2 = JSON.parse(str)
                localStorage.setItem('backupDate',str)
            },
            /*备份方法*/
            backup(){
                console.log('sss')
                /*this.$nextTick(()=>{
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
                })*/
            }
        }
    }
</script>

<style scoped>
    .bottom{
        width:100%;
        height: 36px;
        line-height: 36px;
        display: flex;
        position: absolute;
        bottom: 0;
        background: rgb(38, 42, 55);
    }
    .b-left{
        flex: 0 0 170px;
        width: 200px;
    }
    .b-right{
        flex: 1px;
        border-left:2px solid rgb(25,28,40);
        font-size: 12px;
        padding:0 20px;
    }
    .threshold{
        color: rgb(26, 179, 148);
        font-weight: 600;
        float: left;
    }
    .company{
        float: right;
        color: #8193BC;
    }
    .set-rang{
        color: #eeeeee;
    }
    .set-rang:hover{
        cursor: pointer;
        color:rgb(26, 179, 148);
        text-decoration: underline;
    }
    .systemIp{
        float: left;
        margin-left: 20px;
    }
    .backupConfig{
        margin-left: 20px;
        float: left;
    }
    .backupConfig span{
        color:rgb(26, 179, 148);
        cursor: pointer;
    }
    .backupConfig span:hover{
        text-decoration: underline;
    }
</style>
