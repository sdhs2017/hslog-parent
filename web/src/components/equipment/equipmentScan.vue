<template>
    <div class="content-bg">
        <div class="top-title">资产配置管理
            <div v-if="scanState" style="float:right;margin-right: 10px;color: mediumspringgreen;"><i v-if="scanText ==='资产扫描中'" class="el-icon-loading">{{scanText}}</i></div>
        </div>
        <div class="scan-wapper">
            <div ref="form" class="scan-condition" >
                <div>
                    <label>起始IP</label>
                    <el-input size="mini" v-model="form.startip" class="item"></el-input>
                </div>
                <div>
                    <label>结束IP</label>
                    <el-input size="mini" v-model="form.endip" class="item"></el-input>
                </div>
                <el-button size="mini" type="primary" @click="scanEquipment"><i class="el-icon-search"></i>扫描</el-button>
            </div>
        </div>
        <div class="scan-table-wapper" v-loading="loading"  element-loading-background="rgba(26,36,47, 0.2)">
            <el-table :data="tableData" stripe fit style="width: 100%" >
                <el-table-column prop="ip" label="IP地址" ></el-table-column>
                <el-table-column prop="name" label="资产名称" ></el-table-column>
                <el-table-column prop="hostName" label="主机名" ></el-table-column>
                <el-table-column prop="type" label="资产类型" ></el-table-column>
                <el-table-column prop="logType" label="日志类型" ></el-table-column>
                <el-table-column prop="createTime" label="进入时间" >
                    <template slot-scope="scope">
                        {{scope.row.createTime !== null ?scope.row.createTime.split(".")[0]:'-'}}
                    </template>
                </el-table-column>
                <el-table-column prop="upDateTime" label="更新时间" >
                    <template slot-scope="scope">
                        {{scope.row.upDateTime !== null ?scope.row.upDateTime.split(".")[0]:'-'}}
                    </template>
                </el-table-column>
                <el-table-column prop="endTime" label="结束时间" >
                    <template slot-scope="scope">
                        {{scope.row.endTime !== null ?scope.row.endTime.split(".")[0]:'-'}}
                    </template>
                </el-table-column>
                <el-table-column prop="startUp" label="是否启用" >
                    <template slot-scope="scope">
                        {{scope.row.startUp === 0 ?'否':'是'}}
                    </template>
                </el-table-column>
                <el-table-column prop="responseDate" label="响应时间" >
                    <template slot-scope="scope">
                        {{(Number((new Date() - new Date(scope.row.responseDate))/1000/60)).toFixed(2)}} min
                    </template>
                </el-table-column>
                <el-table-column prop="state" label="状态" >
                    <template slot-scope="scope">
                        <!--<el-tag
                            :type="scope.row.state === '正常' ? 'success' : 'warning'"
                            disable-transitions>{{scope.row.state}}</el-tag>-->
                        <el-tag v-if="scope.row.state === '正常'" type="success" disable-transitions>正常</el-tag>
                        <el-tag v-else type="warning" disable-transitions>超时</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="50">
                    <template slot-scope="scope">
                        <i class="el-icon-edit" title="修改" @click="goToUpdate(scope.row)"></i>
                    </template>
                </el-table-column>
            </el-table>
        </div>
    </div>
    
</template>

<script>
    export default {
        name: "equipmentScan",
        data() {
            return {
                loading:false,
                form:{
                    startip:'',
                    endip:''
                },
                typeArr:[],//资产类型
                tableData:[],//表格数据
                scanState:false,//扫描状态  是否正在扫描
                scanText:'资产扫描中',
                getEquipmentState:'',
                getScanEquipmentInterval:'',
                getScanStateInterval:''
            }
        },
        created(){
            //获取资产类型数据
            this.getEquipmentType();
            //获取扫描的资产
            this.getEquipmentScan();
            if(localStorage.getItem('scanState')){//true 为有任务
                this.scanState = true;
                //定时器 循环获取扫描出来的资产
                this.getScanEquipmentInterval = setInterval(()=>{this.getScanEquipment()},3000);
                //定时器 循环获取扫描状态
                this.getScanStateInterval = setInterval(()=>{this.getScanState()},3000);
            }
        },
        watch:{
            'scanState'(){
                if(this.scanState){
                    clearInterval(this.getEquipmentState)
                }else{
                    this.getEquipmentState = setInterval(()=>{},120000)
                }
            }
        },
        methods:{
            /*获得资产类型数据*/
            getEquipmentType(){
                this.$nextTick(()=>{
                    this.$axios.get('static/filejson/equiptype.json',{})
                        .then((res)=>{
                            this.typeArr = res.data;
                        })
                        .catch((err)=>{
                            console.log(err)
                        })
                })
            },
            //获取资产数据
            getEquipmentScan(){
                this.loading = true;
                this.tableData = [];
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/assets/selectAll.do','')
                        .then(res =>{
                            this.loading = false;
                            res.data.forEach(item =>{
                                if(item.type !== null){
                                    let type = '';
                                    const str = item.type.substring(0,2);
                                    for (let n in this.typeArr){
                                        let obj = this.typeArr[n];
                                        if(obj.value == str){
                                            type += obj.label +'-';
                                            for(let j in obj.children){
                                                if(obj.children[j].value == item.type){
                                                    type += obj.children[j].label;
                                                    break;
                                                }
                                            }
                                            break;
                                        }
                                    }
                                    item.type = type;
                                }
                                this.tableData = res.data;
                            })


                        })
                        .catch(err=>{
                            this.loading = false;
                        })
                })
            },
            /*获取扫描出来的资产*/
            getScanEquipment(){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/assets/selectByIncrement.do','')
                        .then(res =>{
                            res.data.forEach(item =>{
                                if(item.type !== null){
                                    let type = '';
                                    const str = item.type.substring(0,2);
                                    for (let n in this.typeArr){
                                        let obj = this.typeArr[n];
                                        if(obj.value == str){
                                            type += obj.label +'-';
                                            for(let j in obj.children){
                                                if(obj.children[j].value == item.type){
                                                    type += obj.children[j].label;
                                                    break;
                                                }
                                            }
                                            break;
                                        }
                                    }
                                    item.type = type;
                                }
                                this.tableData.prepend(res.data);
                            })


                        })
                        .catch(err=>{
                        })
                })
            },
            /*扫描按钮点击事件*/
            scanEquipment(){
                //判断是否有任务在进行
                if(!this.scanState){
                    //判断参数合理性
                    let isIp2 =/^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
                    if(!isIp2.test(this.form.startip)){
                        layer.msg("起始ip地址不能为空或格式不正确",{icon:5});
                    }else if(!isIp2.test(this.form.endip)){
                        layer.msg("结束ip地址不能为空或格式不正确",{icon:5});
                    }else{
                        this.$nextTick(()=>{
                            this.$axios.post(this.$baseUrl+'/collector/startMasscanCollector.do',this.$qs.stringify(this.form))
                                .then(res=>{
                                    this.loading = false;
                                    if(res.data[0].success == true){
                                        //弹窗提示
                                        layer.msg(res.data[0].msg,{icon:1});
                                        this.scanState = true;
                                        this.scanText = '资产扫描中';
                                        //定时器 循环获取扫描出来的资产
                                        this.getScanEquipmentInterval = setInterval(()=>{this.getScanEquipment()},3000);
                                        //定时器 循环获取扫描状态
                                        this.getScanStateInterval = setInterval(()=>{this.getScanState()},3000);
                                        //将任务存放在本地
                                        localStorage.setItem('scanState','true');
                                    }else{
                                        layer.msg(res.data[0].msg,{icon:5});
                                    }
                                })
                                .catch(err=>{
                                    this.loading = false;
                                })
                        })
                    }
                }else{
                    layer.msg('有扫描任务正在进行',{icon:5})
                }
            },
            /*获取扫描状态*/
            getScanState(){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'collector/stateMasscanCollector.do','')
                        .then(res=>{
                            if(res.data[0].state == true){
                                this.scanText = '扫描完成';
                                //关闭计时器
                                clearInterval(this.getScanEquipmentInterval);
                                clearInterval(this.getScanStateInterval);
                                this.getScanEquipment();
                                //删除本地缓存任务
                                localStorage.removeItem('scanState');
                            }
                        })
                        .then(err=>{

                        })
                })
            },
            /*跳转到更新资产页面*/
            goToUpdate(rowData){
                let newRouters = [{
                    path:'/',
                    component: resolve => require(['../common/Home.vue'], resolve),
                    meta: { title: '自述文件' },
                    children:[
                        {
                            path: '/updateEquipment'+rowData.id,
                            name:'updateEquipment'+rowData.id,
                            component: resolve => require(['../equipment/updateEquipment.vue'], resolve),
                            meta: { title: '资产更新' }
                        }
                    ]
                }]
                this.$router.addRoutes(newRouters);
                this.$router.push({path:'/updateEquipment'+rowData.id,query: { ip:rowData.ip,id: rowData.id }});
            }
        }
    }
</script>

<style scoped>
    .scan-condition{
        margin: 10px auto;
        display: flex;
        justify-content: center;
    }
    .scan-condition>div{
        display: flex;
        justify-content: center;
    }
    .scan-condition>div label{
        width: 48px;
        background: #409eff;
        padding: 0 5px;
        height: 28px;
        line-height: 28px;
        font-size: 14px;
        text-align: center;
        -webkit-border-radius: 5px;
        -moz-border-radius: 5px;
        border-radius: 5px;
    }
    .scan-condition>div .item{
        width: 180px;
    }
    .scan-table-wapper{
        margin-top: 20px;
        padding: 0 10px;
    }
    i{
        cursor: pointer;
    }
</style>
