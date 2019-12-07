<template>
    <div class="content-bg">
        <div class="top-title">'{{equipmentName}}' 安全策略</div>
        <div class="equipment-safe-btns">
            <el-button type="primary" size="mini" plain @click="addBtn()">添加</el-button>
            <el-button type="danger" size="mini" plain @click="delectEquipmentSafe()">删除</el-button>
        </div>
        <div class="equipment-safe-table">
            <v-basetable :selection="true" :tableHead="tableHead" :tableData="tableData" :busName="busName"></v-basetable>
        </div>
        <!--弹出表单-->
        <el-dialog title="添加安全策略" :visible.sync="dialogFormVisible" width="440px">
            <el-form label-width="80px">
                <el-form-item label="策略名称:" >
                    <el-input v-model="safeFormItem.safe_strategy_name" placeholder="策略名称"  class="item"></el-input>
                </el-form-item>
                <el-form-item label="事件类型:">
                    <el-select v-model="safeFormItem.event_type" placeholder="请选择" style="width: 100%;">
                        <el-option
                            v-for="item in eventTypeArr"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="阈值:" >
                    <el-input v-model="safeFormItem.number"  class="item" placeholder="阈值(数值类型)"></el-input>
                </el-form-item>
                <el-form-item label="是否启用:">
                    <el-radio-group v-model="safeFormItem.state">
                        <el-radio :label="1">是</el-radio>
                        <el-radio :label="0">否</el-radio>
                    </el-radio-group>
                </el-form-item>
                <div class="safe-time">
                    <span>时间：</span>
                    <div class="timeSelect">
                        <el-form-item label="月" label-width="0px">
                            <el-select v-model="safeFormItem.month" placeholder="请选择" >
                                <el-option label="" value="00"></el-option>
                                <el-option label="1" value="1"></el-option>
                                <el-option label="2" value="2"></el-option>
                                <el-option label="3" value="3"></el-option>
                                <el-option label="4" value="4"></el-option>
                                <el-option label="5" value="5"></el-option>
                                <el-option label="6" value="6"></el-option>
                                <el-option label="7" value="7"></el-option>
                                <el-option label="8" value="8"></el-option>
                                <el-option label="9" value="9"></el-option>
                                <el-option label="10" value="10"></el-option>
                                <el-option label="11" value="11"></el-option>
                                <el-option label="12" value="12"></el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="日" label-width="0px">
                            <el-select v-model="safeFormItem.day" placeholder="请选择" >
                                <el-option
                                    v-for="item in dayArr"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="时" label-width="0px">
                            <el-select v-model="safeFormItem.hour" placeholder="请选择" >
                                <el-option
                                    v-for="item in hourArr"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="分" label-width="0px">
                            <el-select v-model="safeFormItem.minute" placeholder="请选择" >
                                <el-option
                                    v-for="item in minArr"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </div>
                </div>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click=" addBtnType ? addEquipmentSafe():editEquipmentSafe(editId)">确 定</el-button>
                <el-button @click="dialogFormVisible = false">取 消</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import vBasetable from '../common/Basetable';
    import bus from '../common/bus';
    export default {
        name: "equipmentSafe",
        data(){
            return{
                busName:{
                    selectionName:'',
                    editSafeName: ''
                },
                equipmentName:'',//资产名称
                equipmentId:'',//资产id
                logType:'',
                tableHead:[
                    {
                        prop:'safe_strategy_name',
                        label:'策略名',
                        width:''
                    },
                    {
                        prop:'event_type',
                        label:'事件类型',
                        width:'',
                        formatData:(val)=>{
                            //事件类型
                            if(this.logType === "syslog"){
                                return this.syslogEventObj[val]
                            }else if(this.logType === "winlog"){
                                return  this.winlogEventObj[val]
                            }
                        }
                    },
                    {
                        prop:'number',
                        label:'阈值',
                        width:''
                    },
                    {
                        prop:'time_interval',
                        label:'时间',
                        width:'',
                        formatData:(val)=>{
                            //时间
                            let time = '';
                            let timearr = val.split("-");
                            if(timearr[0] !== "00"){
                                time += timearr[0]+"月";
                            }
                            if(timearr[1] !== "00"){
                                time += timearr[1]+"日";
                            }
                            if(timearr[2] !== "00"){
                                time += timearr[2]+"时";
                            }
                            if(timearr[3] !== "00"){
                                time += timearr[3]+"分";
                            }
                            return time;
                        }
                    },
                    {
                        prop:'state',
                        label:'是否启用',
                        width:'',
                        formatData:(val)=>{return val == '1' ? '是' : '否'}
                    },
                    {
                        prop:'tools',
                        label:'操作',
                        width:'50',
                        btns:[
                            {
                                icon:'el-icon-edit',
                                text:'修改',
                                btnType: 'editEquipmentSafe',
                                clickFun:(params)=>{
                                    this.editId = params.id;
                                    //获取安全策略信息
                                    this.$nextTick(()=> {
                                        layer.load(1);
                                        this.$axios.post(this.$baseUrl+'/safeStrategy/selectById.do', this.$qs.stringify({id:params.id}))
                                            .then(res => {
                                                layer.closeAll();
                                                this.safeFormItem.safe_strategy_name = res.data[0].safe_strategy_name;
                                                this.safeFormItem.event_type = res.data[0].event_type;
                                                this.safeFormItem.state = res.data[0].state;
                                                this.safeFormItem.number = res.data[0].number;
                                                this.safeFormItem.month = res.data[0].month;
                                                this.safeFormItem.day = res.data[0].day;
                                                this.safeFormItem.hour = res.data[0].hour;
                                                this.safeFormItem.minute = res.data[0].minute;
                                                this.dialogFormVisible = true;
                                                this.addBtnType = false;
                                            })
                                            .catch(err =>{
                                                layer.closeAll();
                                                layer.msg('获取信息失败',{icon: 5});
                                            })
                                    })
                                }
                            }
                        ]
                    }
                ],//表格头
                tableData:[],//表格数据
                safeFormItem:{
                    safe_strategy_name:'',//策略名
                    event_type:'',//事件类型
                    number:'',//阈值
                    state:1,//状态
                    month:'00',//月
                    day:'00',//日
                    hour:'00',//时
                    minute:'00'//分
                },
                dialogFormVisible:false,//弹窗开关
                eventTypeArr:[],//事件类型
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
                dayArr:[
                    {
                        value:"00",
                        label:""
                    }
                ],
                hourArr:[
                    {
                        value:"00",
                        label:""
                    }
                ],
                minArr:[
                    {
                        value:"00",
                        label:""
                    }
                ],
                delectIds:'',//删除安全策略的id集合
                editId:'',//修改策略id
                addBtnType:true //弹窗确定按钮类型（修改/添加）
            }
        },
        created(){
            for(let i=1;i<=30;i++){
                let obj = {};
                obj.value=i;
                obj.label=i;
                this.dayArr.push(obj)
            }
            for(let i=1;i<=24;i++){
                let obj = {};
                obj.value=i;
                obj.label=i;
                this.hourArr.push(obj)
            }
            for(let i=1;i<=60;i++){
                let obj = {};
                obj.value=i;
                obj.label=i;
                this.minArr.push(obj)
            }

        },
        methods:{
           /*获取设备的安全策略数据*/
           getEquipmentSafeData(){
               layer.load(1)
               this.$nextTick(()=>{
                   this.$axios.post(this.$baseUrl+'/safeStrategy/selectByEquipmentId.do',this.$qs.stringify({equipmentId:this.equipmentId}))
                       .then(res =>{
                           layer.closeAll();
                           this.tableData = res.data;
                       })
                       .catch(err =>{
                           layer.closeAll()
                       })
               })
           },
            /*添加按钮*/
            addBtn(){
                //重置参数
                this.safeFormItem.safe_strategy_name = '';
                this.safeFormItem.event_type = '';
                this.safeFormItem.state = 1;
                this.safeFormItem.number = '';
                this.safeFormItem.month = '00';
                this.safeFormItem.day = '00';
                this.safeFormItem.hour = '00';
                this.safeFormItem.minute = '00';
                this.safeFormItem.id = '';
                this.dialogFormVisible = true ;
                this.addBtnType = true
            },
            /*t添加安全策略*/
            addEquipmentSafe(){
                this.$nextTick(()=> {
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/safeStrategy/insert.do', this.$qs.stringify(this.safeFormItem))
                        .then(res => {
                            layer.closeAll();
                            console.log(res.data.success)
                            if(res.data.success === 'true'){
                                layer.msg('添加成功',{icon: 1});
                                this.dialogFormVisible = false;
                                this.getEquipmentSafeData();
                            }else{
                                layer.msg(res.data.message,{icon: 5});
                            }


                        })
                        .catch(err =>{
                            layer.closeAll();
                            layer.msg('添加失败',{icon: 5});
                        })
                })
            },
            /*修改安全策略*/
            editEquipmentSafe(id){
                let obj = Object.assign({}, obj);
                obj = this.safeFormItem;
                obj.id = id;
                this.$nextTick(()=> {
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/safeStrategy/insert.do', this.$qs.stringify(obj))
                        .then(res => {
                            layer.closeAll();
                            if(res.data.success === 'true'){
                                layer.msg('修改成功',{icon: 1});
                                this.dialogFormVisible = false;
                                this.getEquipmentSafeData();
                            }else{
                                layer.msg(res.data.message,{icon: 5});
                            }


                        })
                        .catch(err =>{
                            layer.closeAll();
                            layer.msg('添加失败',{icon: 5});
                        })
                })
            },
            /*删除安全策略*/
            delectEquipmentSafe(){
                if(this.delectIds === ''){
                    layer.msg('未选中任何安全策略',{icon: 5});
                }else{
                    //询问框
                    layer.confirm('您确定删除么？', {
                        btn: ['确定','取消'] //按钮
                    }, (index)=>{
                        layer.close(index);
                        let safeObj = {};
                        //策略id
                        safeObj.ids = this.delectIds;
                        //资产id
                        safeObj.id = this.equipmentId;
                        //发送数据到后台 进行删除
                        this.$nextTick(()=> {
                            layer.load(1);
                            this.$axios.post(this.$baseUrl+'/safeStrategy/deletes.do', this.$qs.stringify(safeObj))
                                .then(res => {
                                    layer.closeAll();
                                    if(res.data.success === 'true'){
                                        layer.msg('删除成功',{icon: 1});
                                        this.dialogFormVisible = false;
                                        this.getEquipmentSafeData();
                                    }else{
                                        layer.msg(res.data.message,{icon: 5});
                                    }
                                })
                                .catch(err =>{
                                    layer.closeAll();
                                    layer.msg('删除失败',{icon: 5});
                                })
                        })
                        //关闭弹窗
                        layer.close();
                    }, function(){
                        layer.close();
                    });
                }
            }
        },
        watch:{
            'equipmentId'(){
                //获取数据
                this.getEquipmentSafeData();
                this.safeFormItem.equipmentId = this.equipmentId;
                //监听多选操作
                bus.$on(this.busName.selectionName,params =>{
                    this.delectIds = '';
                    for(let i in params){
                        this.delectIds += params[i].id + ',';
                    }
                })

            },
            'logType'(){
                if(this.logType === 'syslog'){
                    for(let i in this.syslogEventObj){
                        let obj = {};
                        obj.value = i;
                        obj.label = this.syslogEventObj[i];
                        this.eventTypeArr.push(obj);
                    }
                }else if(this.logType === 'winlog'){
                    for(let i in this.winlogEventObj){
                        let obj = {};
                        obj.value = i;
                        obj.label = this.syslogEventObj[i];
                        this.eventTypeArr.push(obj);
                    }
                }
            }
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                vm.$options.name = 'equipmentSafe'+ to.query.id;
                //修改data参数
                vm.equipmentName = to.query.name;
                vm.busName.selectionName = 'equipmentSafe'+to.query.id;
                vm.busName.editSafeName = 'editSafeName'+to.query.id;
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'equipmentSafe'+to.query.id,
                    component:'equipment/equipmentSafe.vue',
                    title:'安全策略'
                }
                sessionStorage.setItem('/equipmentSafe'+to.query.id,JSON.stringify(obj))
                if(vm.equipmentId === '' || vm.equipmentId !== to.query.id){
                    vm.equipmentId = to.query.id;
                    vm.logType = to.query.logType;
                   // vm.getEquipmentData();
                }

            })

        },
        beforeDestroy(){
            bus.$off(this.busName.selectionName);
            bus.$off(this.busName.editSafeName);
        },
        destroyed(){
        },
        components:{
            vBasetable
        }
    }
</script>

<style scoped>
    .equipment-safe-btns{
        margin-bottom: 20px;
        padding: 0 10px;
    }
    .equipment-safe-btns button{
        background: 0;
        margin: 0;
    }
    .item{
        flex: 1;
    }
    .equipment-safe-table{
        padding: 0 10px;
    }
    .safe-time{
        display: flex;
    }
    .safe-time span{
        display: inline-block;
        width: 80px;
        text-align: end;
        align-items: center;
        line-height: 80px;
        color: #D6DFEB;
    }
    .timeSelect{
        display: flex;
        flex: 1;
    }
    lable{
        color: #fff!important;
    }
</style>
