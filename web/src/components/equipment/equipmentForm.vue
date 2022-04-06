<template>
    <div v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
        <el-form ref="form" label-width="100px" class="equipment-form">
            <el-row>
                <el-col :span="12">
                    <div class="grid-content bg-purple">
                        <span class="mustWrite">*</span>
                        <el-form-item label="资产名称:">
                            <el-input v-model="form.name" placeholder="资产名称" ></el-input>
                        </el-form-item>
                    </div>
                </el-col>
                <el-col :span="12">
                    <div class="grid-content bg-purple-light">
                        <span class="mustWrite">*</span>
                        <el-form-item label="资产ip:">
                            <el-input v-model="form.ip" placeholder="资产ip" ></el-input>
                        </el-form-item>
                    </div>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <div class="grid-content bg-purple">
                        <span class="mustWrite">*</span>
                        <el-form-item label="主机名:">
                            <el-input v-model="form.hostName" placeholder="主机名"></el-input>
                        </el-form-item>
                    </div>
                </el-col>
                <el-col :span="12">
                    <div class="grid-content bg-purple-light">
                        <span class="mustWrite">*</span>
                        <el-form-item label="日志类型:">
                            <el-select v-model="form.logType" placeholder="请选择" style="width: 100%;">
                                <el-option
                                    v-for="item in logTypeArr"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </div>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <div class="grid-content bg-purple">
                        <span class="mustWrite">*</span>
                        <el-form-item label="日志级别:">
                            <el-checkbox-group v-model="form.log_level">
                                <el-checkbox label="emergency"></el-checkbox>
                                <el-checkbox label="alert"></el-checkbox>
                                <el-checkbox label="critical"></el-checkbox>
                                <el-checkbox label="error"></el-checkbox>
                                <el-checkbox label="warning"></el-checkbox>
                                <el-checkbox label="notice"></el-checkbox>
                                <el-checkbox label="information"></el-checkbox>
                                <el-checkbox label="debug"></el-checkbox>
                            </el-checkbox-group>
                        </el-form-item>
                    </div>
                </el-col>
                <el-col :span="12">
                    <div class="grid-content bg-purple-light">
                        <span class="mustWrite">*</span>
                        <el-form-item label="资产类型:">
                            <el-cascader
                                :options="typeArr"
                                v-model="form.type"
                                @change="eqTypeChange"
                                style="width: 100%;">
                            </el-cascader>
                        </el-form-item>
                    </div>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <div class="grid-content bg-purple">
                        <el-form-item label="根域名:">
                            <el-input v-model="form.domain" placeholder="根域名"></el-input>
                        </el-form-item>
                    </div>
                </el-col>
                <el-col :span="12">
                    <div class="grid-content bg-purple-light">
                        <el-form-item label="端口:">
                            <el-input v-model="form.port" placeholder="端口"></el-input>
                        </el-form-item>
                    </div>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <div class="grid-content bg-purple">
                        <el-form-item label="系统:">
                            <el-input v-model="form.system" placeholder="系统"></el-input>
                        </el-form-item>
                    </div>
                </el-col>
                <el-col :span="12">
                    <div class="grid-content bg-purple-light">
                        <el-form-item label="系统版本号:">
                            <el-input v-model="form.systemVersion" placeholder="系统版本号"></el-input>
                        </el-form-item>
                    </div>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <div class="grid-content bg-purple">
                        <el-form-item label="资产编号:">
                            <el-input v-model="form.assetNum" placeholder="资产编号"></el-input>
                        </el-form-item>
                    </div>
                </el-col>
                <el-col :span="12">
                    <div class="grid-content bg-purple-light">
                        <el-form-item label="序列号:">
                            <el-input v-model="form.serialNum" placeholder="序列号"></el-input>
                        </el-form-item>
                    </div>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <div class="grid-content bg-purple">
                        <span class="mustWrite">*</span>
                        <el-form-item label="是否启用:">
                            <el-radio-group v-model="form.startUp">
                                <el-radio :label="1">是</el-radio>
                                <el-radio :label="0">否</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </div>
                </el-col>
                <el-col :span="12">
                    <div class="grid-content bg-purple-light">
                        <el-form-item label="MAC地址:">
                            <el-input v-model="form.macAdress" placeholder="MAC地址"></el-input>
                        </el-form-item>
                    </div>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <div class="grid-content bg-purple">
                        <el-form-item label="创建时间:">
                            <el-input v-model="form.createTime" disabled></el-input>
                        </el-form-item>
                    </div>
                </el-col>
                <el-col :span="12">
                    <div class="grid-content bg-purple-light">
                        <el-form-item label="更新时间:">
                            <el-input v-model="form.updateTime" disabled></el-input>
                        </el-form-item>
                    </div>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <div class="grid-content bg-purple">
                        <el-form-item label="截止时间:">
                            <el-input v-model="form.endTime" disabled></el-input>
                        </el-form-item>
                    </div>
                </el-col>
                <el-col :span="12">
                    <div class="grid-content bg-purple-light">
                        <el-form-item label="描述:">
                            <el-input type="textarea"  :autosize="{ minRows:3, maxRows: 3}" v-model="form.describe"></el-input>
                        </el-form-item>
                    </div>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <div class="grid-content bg-purple">
                        <el-form-item label="责任人:">
                            <el-input v-model="form.userName" disabled></el-input>
                        </el-form-item>
                    </div>
                </el-col>
                <el-col :span="12">
                    <div class="grid-content bg-purple-light">
                        <el-form-item label="资产价值:">
                            <el-input v-model="form.valuation" placeholder="资产价值"></el-input>
                        </el-form-item>
                    </div>
                </el-col>
            </el-row>
        </el-form>
        <div class="equipment-form-btns">
            <el-button type="primary" @click="saveEquipment">保存</el-button>
<!--            <el-button type="primary" @click="()=>{this.$router.push({path:routerUrl})}">返回</el-button>-->
            <el-button type="info" @click="emptyData">清空</el-button>
        </div>

        <el-dialog title="提示" :visible.sync="dialogState" width="460px">
            <div style="color: #fff;">
                防火墙、IPS要求日志内容格式满足“key=value 且以空格隔开”的格式。
                例如：time="2021-06-17 11:17:45" IP=127.0.0.1
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogState = false">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import bus from '../common/bus';
    import {checkIP} from '../../../static/js/common'
    export default {
        name: "equipmentForm",
        props:{
            //表单数据
            formData:{
                type:Object
            },
            //监听事件名称
            busName:{
                type:String
            },
            routerUrl:{
                type:String
            }
        },
        data(){
            return{
                loading:false,
                dialogState:false,//提示框状态
                nameState:false,//name合法性
                ipOrTypeState:false,//ip与日志类型合法性
                saveState:false,
                logBaseJson:{},//日志配置信息
                form:{
                    name:'',//资产名称
                    ip:'',//资产Ip
                    hostName:'',//主机名
                    logType:'',//日志类型
                    //log_level:'',//日志级别
                    log_level:["emergency","alert","critical","error","warning","notice","information","debug"],
                    type:'',//资产类型
                    startUp:1,//是否启用
                    macAdress:'',//MAC地址
                    system:'',//系统
                    domain:'',//根域名
                    port:'',//端口
                    systemVersion:'',//系统版本号
                    assetNum:'',//资产编号
                    serialNum:'',//序列号
                    describe:'',//描述
                    valuation:'',//资产价值
                    userName:'',//资产责任人
                    createTime:'',//创建时间
                    updateTime:'',//更新时间
                    endTime:''//截止时间
                },
                defaultForm:'',
                logTypeArr:[],//日志类型数据
                typeArr:[], //资产类型
                equipmentData:{}
            }
        },
        created(){
            this.getLogType();
            this.getEquipmentType();
            this.defaultForm = JSON.stringify(this.form);
        },
        mounted(){
            //console.log(this.formData)
        },
        watch:{
          'formData'(newV,oldV){
              this.equipmentData = newV;
              //console.log( this.equipmentData);
              //对数据进行处理
              //日志级别
              if(this.equipmentData.log_level){
                  this.equipmentData.log_level = this.equipmentData.log_level.split(',').filter((s)=>{
                      return s && s.trim();
                  });
              }

              //资产类型
              const type = this.equipmentData.type;
              this.equipmentData.type = [];
              if(type){
                  this.equipmentData.type.push(type.substring(0,2))
                  this.equipmentData.type.push(type);
              }
              //赋值
              for(let i in this.form){
                  this.form[i] = this.equipmentData[i]
              }
              //this.form = this.equipmentData;
          }
        },
        methods:{
            /*资产名称失去焦点事件*/
            equipmentNameBlur(){
                //值不为空验证合法性
                if(this.form.name != ''){
                    let params = {};
                    if (this.formData){
                        params.id = this.formData.id
                    }
                    params.name = this.form.name;
                    this.$nextTick(()=>{
                        this.loading = true;
                        this.$axios.post(this.$baseUrl+'/equipment/checkNameUnique.do',this.$qs.stringify(params))
                            .then(res=>{
                                this.loading = false;
                                let obj = res.data;
                                if(obj.success !== 'true'){
                                    layer.msg(obj.message,{icon:5})
                                    this.nameState = false;
                                }else{
                                    this.nameState = true;
                                }
                            })
                            .catch(err=>{
                                this.loading = false;
                            })
                    })
                }
            },
            /*资产ip与日志类型失去焦点事件*/
            ipOrLogtypeBlur(){
                if(this.form.ip != '' && this.form.logType !=''){
                    let params = {}
                    if (this.formData){
                        params.id = this.formData.id
                    }
                    params.ip = this.form.ip;
                    params.logType = this.form.logType;
                    this.$nextTick(()=>{
                        this.loading = true;
                        this.$axios.post(this.$baseUrl+'/equipment/checkIpAndLogTypeUnique.do',this.$qs.stringify(params))
                            .then(res=>{
                                this.loading = false;
                                let obj = res.data;
                                if(obj.success !== 'true'){
                                    layer.msg(obj.message,{icon:5})
                                    this.ipOrTypeState = false;
                                }else{
                                    this.ipOrTypeState = true;
                                }
                            })
                            .catch(err=>{
                                this.loading = false;
                            })
                    })
                }
            },
            /*获得日志类型*/
            getLogType(){
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/log/getLogTypeComboxByPage.do',this.$qs.stringify({
                        pageType:'equipment'
                    }))
                        .then((res)=>{
                            for (let i in  res.data.data) {
                                this.logTypeArr.push( res.data.data[i]);
                            }
                        })
                        .catch((err)=>{
                            console.log(err)
                        })
                })
            },
            /*getLogType(){
                this.$nextTick(()=>{
                    this.$axios.get('static/filejson/logTypeLevel.json',{})
                        .then((res)=>{
                            //this.logBaseJson = res.data;
                            //this.logType.push({value:'',label:'请选择'});
                            for(let i=0;i<res.data.length; i++){
                                let obj = {
                                    value:res.data[i].type,
                                    label:res.data[i].label
                                };
                                this.logTypeArr.push(obj);
                            }
                        })
                        .catch((err)=>{
                            console.log(err)
                        })
                })
            },*/
            /*获得资产类型数据*/
            getEquipmentType(){
                this.$nextTick(()=>{
                    this.$axios.get('static/filejson/equiptype.json',{})
                        .then((res)=>{
                            this.typeArr = res.data
                        })
                        .catch((err)=>{
                            console.log(err)
                        })
                })
            },
            /*保存资产数据*/
            saveEquipment(){
                this.saveState = true;
                //判断合法性
                if(this.form.name === ''){
                    layer.msg("资产名称不能为空",{icon:5});
                }else if(this.form.hostName === ''){
                    layer.msg("主机名称不能为空",{icon:5});
                }else if(this.form.ip === ''){
                    layer.msg("IP地址不能为空",{icon:5});
                }else if(!checkIP(this.form.ip)){
                    layer.msg("IP不合法",{icon:5});
                }else if(this.form.log_level.length == 0){
                    layer.msg("日志级别不能为空",{icon:5});
                }else if(this.form.logType.length == 0){
                    layer.msg("日志类型不能为空",{icon:5});
                }else if(this.form.type.length == 0){
                    layer.msg("资产类型不能为空",{icon:5});
                }else{
                    let formData2 = Object.assign({}, this.form);
                    //处理日志级别参数
                    let length = formData2.log_level.length;
                    let str = '';
                    for (let i =0;i<length;i++) {
                        str += formData2.log_level[i]+',';
                    }
                    formData2.log_level = str;
                    //处理资产类型
                    formData2.type = formData2.type[1];
                    bus.$emit(this.busName,formData2)

                }


            },
            /*清空数据*/
            emptyData(){
                this.form = JSON.parse(this.defaultForm);
            },
            //资产类型改变
            eqTypeChange(val){
                if(val[1] == '0201' || val[1] == '0204'){//IPS 、 防火墙
                    this.dialogState = true
                }
            }
        }
    }
</script>

<style scoped>
    .equipment-form-item{
        /*!**!padding: 0 20px;*/
    }
    .grid-content{
        padding: 0 20px;
        margin-bottom: 30px;
    }
    .grid-content lable{
        color: #fff!important;
        position: relative;
    }
    .mustWrite{
        color: red;
        position: absolute;
        top: 5px;
    }
    .equipment-form-btns{
        border-top: 1px solid #303e4e;
        text-align: center;
        height: 50px;
        line-height: 50px;
    }
</style>
