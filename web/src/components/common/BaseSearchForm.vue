<template>
    <div>
        <el-form :inline="true" onsubmit="return false" class="demo-form-inline conditions-wapper" >
            <div v-if="formList.datetimeArr.length > 0" style="width: 384px;">
                <span class="input-lable">日期范围</span>
                <el-date-picker
                    v-model="formList.datetimeArr[0].model.model"
                    type="datetimerange"
                    range-separator="至"
                    size="mini"
                    start-placeholder="开始日期"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    :default-time="['00:00:00', '23:59:59']"
                    end-placeholder="结束日期">
                </el-date-picker>
            </div>
            <div v-if="formList.inputArr.length > 0 " >
                <div v-for="(item,index) in formList.inputArr" :key="index">
                    <span class="input-lable">{{item.label}}</span>
                    <span> <el-input size="mini" v-model="item.model.model" placeholder="请输入内容"></el-input></span>
                </div>
            </div>
            <div v-if="formList.selectArr.length > 0" >
                <div v-for="(item,index) in formList.selectArr" :key="index">
                    <span class="input-lable">{{item.label}}</span>
                    <el-select v-if="item.itemType === 'multiple'"  @change="logTypeChange(item.model.model,item.paramName)" class="multiple-width" multiple collapse-tags v-model="item.model.model" placeholder="请选择"  size="mini">
                        <el-option
                            v-for="op in item.options"
                            :key="op.value"
                            :label="op.label"
                            :value="op.value">
                        </el-option>
                    </el-select>
                    <el-select v-else v-model="item.model.model"  placeholder="请选择"  size="mini">
                        <el-option
                            v-for="op in item.options"
                            :key="op.value"
                            :label="op.label"
                            :value="op.value">
                        </el-option>
                    </el-select>
                </div>
            </div>
            <div v-if="formList.cascaderArr.length > 0" >
                <div v-for="(item,index) in formList.cascaderArr" :key="index">
                    <span class="input-lable">{{item.label}}</span>
                    <el-cascader
                        size="mini"
                        :options="item.options"
                        v-model="item.model.model"
                        clearable
                        style="width: 140px;position: relative;top: -1px;">
                    </el-cascader>
                </div>

            </div>
            <div v-if="btnArr.length>0">
                <el-button v-for="(item,index) in btnArr" :type="item.type"  :key="index" size="mini" @click="pushConditions">{{item.label}}</el-button>
            </div>
        </el-form>

    </div>
</template>

<script>
    import bus from '../common/bus';

    export default {
        /*name: "BaseSearchForm",*/
        props:{
            formItem:{
                type:Array
            },
            busName:{
                type:String
            }
        },
        data(){
            return{
                formList:{
                    datetimeArr:[
                      /*  {
                            label:'日期范围',
                            type:'datetimerange',
                            paramName:'time',
                            model:{
                                model:[]
                            }
                        }*/
                    ],
                    selectArr:[
                        /*{
                            label:'日志类型',
                            type:'',
                            paramName:'type',
                            model:{
                                model:''
                            },
                            options:[
                                {
                                    value:'syslog',
                                    label:'syslog'
                                },
                                {
                                    value:'winlog',
                                    label:'winlog'
                                },
                                {
                                    value:'snmp',
                                    label:'snmp'
                                }
                            ]
                        },
                        {
                            label:'日志级别',
                            type:'',
                            paramName:'level',
                            model:{
                                model:''
                            },
                            options:{}
                        }*/

                    ],
                    inputArr:[
                       /* {
                            label:'IP地址',
                            width:'',
                            paramName:'ip',
                            model:{
                                model:''
                            }
                        },
                        {
                            label:'资产名称',
                            width:'',
                            paramName:'equipmentname',
                            model:{
                                model:''
                            }
                        }*/
                    ],
                    cascaderArr:[/*{
                        itemType: "",
                        label: "资产类型",
                        model: {
                            model:''
                        },
                        options: [],
                        paramName: "type",
                        type: "cascader"
                    }*/]
                },
                btnArr:[
                    {
                        label:'检索',
                        type:'primary'
                    }
                ]

            }
        },
        created(){
            //处理数据 填充搜索条件框
            for(let i =0;i<this.formItem.length;i++){
                switch (this.formItem[i].type) {
                    case 'input':
                        this.formList.inputArr.push(this.formItem[i]);
                        break;
                    case 'select':
                        this.formList.selectArr.push(this.formItem[i]);
                        break;
                    case 'cascader':
                        this.formList.cascaderArr.push(this.formItem[i]);
                        break;
                    case 'datetimerange':
                        this.formList.datetimeArr.push(this.formItem[i]);
                        break;
                    default :
                        break;
                }
            }
        },
        methods:{
            //获得检索条件
            pushConditions(){
                //检索条件
                let searchObj = {};
                for(const i in this.formList){
                   for(let j in this.formList[i]){
                       //判断是否为时间格式的参数
                       if(this.formList[i][j].paramName === 'time'){
                           //定义开始时间/结束时间参数
                           if( this.formList[i][j].model.model === null || this.formList[i][j].model.model.length === 0 ){
                               searchObj.starttime = '';
                               searchObj.endtime = '';
                           }else{
                               searchObj.starttime = this.formList[i][j].model.model[0];
                               searchObj.endtime = this.formList[i][j].model.model[1];
                           }

                       }else{
                           //判断参数的值是否为数组类型， 如果是则进行循环拼接
                           if(typeof this.formList[i][j].model.model == 'object') {
                               let arr = this.formList[i][j].model.model;

                               //判断是否是联动的下拉框
                               if(this.formList[i][j].type === 'cascader'){
                                   searchObj[this.formList[i][j].paramName] = arr[1]
                               }else{
                                   let val = '';
                                   for (let m in arr) {
                                       val += arr[m] + ','
                                   }
                                   searchObj[this.formList[i][j].paramName] = val;
                               }

                           }else{
                               searchObj[this.formList[i][j].paramName] = this.formList[i][j].model.model;
                           }

                       }
                   }
                }
                //检测IP合法性
                let reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/
                let ip = searchObj['fields.ip'] ? searchObj['fields.ip'] : searchObj.ip ? searchObj.ip : undefined ;
                if(ip != undefined && ip != ''){
                    if(reg.test(ip)){
                        //返回查询条件
                        bus.$emit(this.busName,searchObj);
                    }else{
                        layer.msg('IP不合法',{icon:5})
                    }
                }else{
                    bus.$emit(this.busName,searchObj);
                }


            },
            //日志类型改变
            logTypeChange(val,type){
                if(type === 'agent.type'){//日志类型
                    for(let i =0;i<this.formList.selectArr.length;i++){
                        if(this.formList.selectArr[i].label == '日志级别'){
                            this.formList.selectArr[i].model.model=[];
                        }
                    }
                    bus.$emit('logTypeChange',val);
                }

            }
        }
    }
</script>

<style scoped>
    .conditions-wapper{
        display: flex;
        justify-content: center;
    }
    .conditions-wapper div{
        display: flex;
    }
    .input-lable{
        display: inline-block;
        font-size: 12px;
        height: 28px;
        background: #409eff;
        line-height: 28px;
        padding: 0 5px;
        color: #fff;
        word-break: keep-all;
    }
    .multiple-width{
        width: 127px;
    }
    .multiple-width input{
        width: 127px!important;
    }
    button{
        border-radius: 0!important;
    }
</style>
