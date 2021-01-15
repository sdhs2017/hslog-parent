<template>
    <div class="content-bg" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
        <div class="top-title">文件类日志</div>
        <div class="content-wapper">
            <div class="left-wapper" v-loading="leftLoading"  element-loading-background="rgba(48, 62, 78, 0.5)">
                <div class="left-tit">模板</div>
                <div style="overflow-y: auto;height: calc(100% - 60px);">
                    <div :class="item.file_log_templateKey === currentLeftItem.file_log_templateKey ? 'left-item chooseClass' : 'left-item'"
                         v-for="(item,i) in leftData" :key="i"
                         @click="clickLeftItem(item)"
                    >{{item.file_log_templateName}}</div>
                </div>

            </div>
            <div class="right-wapper" v-loading="rightLoading"  element-loading-background="rgba(48, 62, 78, 0.5)">
                <div class="right-tit">
                    <el-input class="tit-input" size="large" v-model="currentLeftItem.file_log_templateName" @input="changeInput"></el-input>
                    <div class="btn-wapper"> <el-button type="warning" :disabled=" commitState ? false : 'disabled'" @click="saveData()">更新</el-button></div>
                </div>
                <el-tabs type="border-card" @tab-click="handleClick">
                    <el-tab-pane label="配置" >
                        <vBasetable2 :tableHead="tableHead" :tableData="tableData" :height="tableHeight"></vBasetable2>
                    </el-tab-pane>
                    <el-tab-pane label="源数据">
                        <vBasetable2 :tableHead="sourceHead" :tableData="sourceData" :height="tableHeight"></vBasetable2>
                    </el-tab-pane>
                </el-tabs>

            </div>
        </div>
        <el-dialog title="编辑" :visible.sync="editForm" width="440px">
            <el-form label-width="110px">
                <el-form-item label="字段代码:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input v-model="currentRightItem.data.file_log_field" class="item"></el-input>
                </el-form-item>
                <el-form-item label="字段名称:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-input v-model="currentRightItem.data.file_log_text" class="item"></el-input>
                </el-form-item>
                <el-form-item label="字段类型:">
                    <span style="color:red;position: absolute;left: -10px;">*</span>
                    <el-select v-model="currentRightItem.data.file_log_type" @change="typeChange" placeholder="请选择" style="width: 100%;">
                        <el-option
                            v-for="item in typeOpt"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="日期格式:">
                    <el-input v-model="currentRightItem.data.file_log_format" :disabled="currentRightItem.data.file_log_type === 'date' ? false : 'disabled'" class="item"></el-input>
                    <span style="color: #8a6226;">只在字段类型为date时生效，且不能为空</span>
                </el-form-item>
                <el-form-item label="设置为日期字段:" style="position: relative;top: -10px;">
                    <el-radio-group v-model="currentRightItem.data.file_log_is_timestamp" :disabled="currentRightItem.data.file_log_type === 'date' ? false : 'disabled'">
                        <el-radio label="true">是</el-radio>
                        <el-radio label="false">否</el-radio>
                    </el-radio-group>
                    <p style="color: #8a6226;position: relative;top: -10px;">只在字段类型为date时生效，且只存在一个</p>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="determine">确 定</el-button>
                <el-button @click="editForm = false">取 消</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import vBasetable2 from '../common/Basetable2'
    export default {
        name: "fileLogManage",
        data(){
            return{
                loading:false,
                leftLoading:false,
                rightLoading:false,
                //弹窗状态
                editForm:false,
                //更新按钮状态
                commitState:false,
                //数据列表
                leftData:[],
                //当前选中元素（左
                currentLeftItem:{},
                //用于比较改变
                compareObj:{},
                tableHeight:0,
                //表头（配置）
                tableHead:[
                    {
                        prop:'file_log_field',
                        label:'字段代码',
                        width:''
                    },{
                        prop:'file_log_text',
                        label:'字段名称',
                        width:''
                    },{
                        prop:'file_log_type',
                        label:'字段类型',
                        width:''
                    },{
                        prop:'file_log_format',
                        label:'日期格式',
                        width:''
                    },{
                        prop:'file_log_is_timestamp',
                        label:'设置为日期字段',
                        width:'',
                        formatData:(val)=>{
                            if(val == 'true'){
                                return "是"
                            }else{
                                return  "否"
                            }
                        }
                    }, {
                        prop:'tools',
                        label:'操作',
                        width:'60',
                        btns:[
                            {
                                icon:'el-icon-edit',
                                text:'修改',
                                btnType: 'editDetails',
                                /*formatData:(obj)=>{
                                    if(obj.file_log_order === 1){
                                        return false
                                    }else{
                                        return true
                                    }

                                },*/
                                clickFun:(row,index)=>{
                                    this.currentRightItem.index = index
                                    this.currentRightItem.data = JSON.parse(JSON.stringify(row));
                                    this.editForm = true
                                }
                            },
                        ]
                    }
                ],
                //表格数据（配置）
                tableData:[],
                //表头（源数据）
                sourceHead:[
                    {
                        prop:'file_log_field',
                        label:'field',
                        width:''
                    },{
                        prop:'file_log_text',
                        label:'text',
                        width:''
                    },{
                        prop:'file_log_type',
                        label:'type',
                        width:''
                    },{
                        prop:'file_log_format',
                        label:'format',
                        width:''
                    },{
                        prop:'file_log_is_timestamp',
                        label:'is_timestamp',
                        width:'',
                        formatData:(val)=>{
                            if(val == 'true'){
                                return "是"
                            }else{
                                return  "否"
                            }
                        }
                    },
                ],
                //表格数据（源数据）
                sourceData:[],
                //当前编辑行数据
                currentRightItem:{
                    index:0,
                    data:{}
                },
                //类型集合
                typeOpt:[{
                    value:'text',
                    label:'text'
                },{
                    value:'keyword',
                    label:'keyword'
                },{
                    value:'ip',
                    label:'ip'
                },{
                    value:'long',
                    label:'long'
                },{
                    value:'date',
                    label:'date'
                },],
                //标记name修改状态
                nameChangeState:false,
                //标记table修改状态
                tableChangeState:false,
            }
        },
        watch:{
            tableData:{
                handler(val, oldVal){

                },
                deep:true
            },
        },
        created(){
            this.tableHeight = document.body.clientHeight - 375 ;
            window.onresize = () => {
                this.tableHeight = document.body.clientHeight - 375 ;
            };
            //获取数据
            this.getList()
        },
        methods:{
            /*输入框输入时*/
            changeInput(value){
                if(value !== this.compareObj.file_log_templateName){
                    //更新按钮状态
                    this.commitState = true;
                    //name改变状态
                    //this.nameChangeState = true;

                }else{
                    this.commitState = false;
                    //this.nameChangeState = false;
                }
            },
            /*获取左侧列表*/
            getList(){
              this.$nextTick(()=>{
                  this.leftLoading = true;
                  this.$axios.post(this.$baseUrl+'/fileLog/getFileLogTemplateList.do','')
                      .then(res=>{
                          this.leftLoading = false;
                          let obj  = res.data;
                          if(obj.success == 'true'){
                              this.leftData = obj.data;
                              //判断是否存在值  有-取第一条  加载右边数据
                              if(this.leftData.length > 0){
                                  //获取第一条表格数据
                                  this.getTableData(this.leftData[0].file_log_templateKey)
                                  this.currentLeftItem = JSON.parse(JSON.stringify(this.leftData[0]));
                                  this.compareObj = JSON.parse(JSON.stringify(this.leftData[0]));
                              }
                          }else {
                              layer.msg(obj.message,{icon:5})
                          }

                      })
                      .catch(err=>{
                           this.leftLoading = false;
                      })
              })
            },
            /*获取表格数据(配置)*/
            getTableData(key){
                this.$nextTick(()=>{
                    this.rightLoading = true;
                    this.$axios.post(this.$baseUrl+'/fileLog/getFileLogTemplateFields.do',this.$qs.stringify({
                        file_log_templateKey:key
                    }))
                        .then(res=>{
                            this.rightLoading = false;
                            let obj  = res.data;
                            if(obj.success == 'true'){
                                this.tableData = obj.data
                            }else {
                                layer.msg(obj.message,{icon:5})
                            }

                        })
                        .catch(err=>{
                             this.rightLoading = false;
                        })
                })
            },
            /*点击左侧*/
            clickLeftItem(item){
                //判断是否存在更新
                if(this.commitState){//是
                    layer.confirm('当前表格未保存，继续，修改的数据将会丢失,确定继续么？', {
                        btn: ['确定','取消'] //按钮
                    }, (index)=>{
                        this.currentLeftItem = JSON.parse(JSON.stringify(item));
                        this.compareObj = JSON.parse(JSON.stringify(item));
                        this.commitState = false;
                        //this.nameChangeState = false;
                        //获取数据
                        this.getTableData(this.currentLeftItem.file_log_templateKey)
                        //清空源数据
                        this.sourceHead = [];
                        this.sourceData = [];
                        //关闭弹窗
                        layer.close(index);
                    }, function(){
                        layer.close();
                    });
                }else{//否
                    this.compareObj = JSON.parse(JSON.stringify(item));
                    this.currentLeftItem = JSON.parse(JSON.stringify(item));
                    this.commitState = false;
                    //this.nameChangeState = false;
                    //清空源数据
                    this.sourceHead = [];
                    this.sourceData = [];
                    //获取数据
                    this.getTableData(this.currentLeftItem.file_log_templateKey)
                }

            },
            /*tab 切换事件*/
            handleClick(tab, event) {
               if(tab.paneName == 1 && this.sourceHead.length === 0){
                   //获取源数据
                   this.$nextTick(()=>{
                       this.sourceloading = true;
                       this.$axios.post(this.$baseUrl+'/',this.$qs.stringify())
                           .then(res=>{
                               this.sourceloading = false;
                               let obj  = res.data;
                               if(obj.success == 'true'){
                                  // this.tableData = obj.data
                               }else {
                                   layer.msg(obj.message,{icon:5})
                               }
                           })
                           .catch(err=>{
                                this.sourceloading = false;
                           })
                   })
               }
            },
            /*type改变*/
            typeChange(){
                this.currentRightItem.data.file_log_format = ''
                this.currentRightItem.data.file_log_is_timestamp = 'false'
            },
            /*确定按钮*/
            determine(){
                //验证数据
                let index = this.currentRightItem.index
                let data = this.currentRightItem.data;
                if(data.file_log_field === ''){
                    layer.msg('字段代码不能为空',{icon:5})
                }else if(data.file_log_text === ''){
                    layer.msg('字段名称不能为空',{icon:5})
                }else if(data.file_log_type === 'date' && data.file_log_format === ''){
                    layer.msg('字段类型为date时，format不能为空',{icon:5})
                }else{
                    //赋值
                    for (let i in data){
                        this.tableData[index][i] = data[i]
                    }
                    //判断is_timestamp是否为true  更改其他为false
                    if(data.file_log_is_timestamp == 'true'){
                        console.log(data.file_log_is_timestamp)
                        for (let i=0;i<this.tableData.length;i++){
                            if(i !== index){
                                this.tableData[i].file_log_is_timestamp = 'false'
                            }
                        }
                    }
                    this.editForm = false;
                    this.commitState = true;
                    this.tableChangeState = true;
                }
            },
            /*保存*/
            saveData(){
                //判断名称是否合法
                if(this.currentLeftItem.file_log_templateName !== ''){
                    this.$nextTick(()=>{
                        this.loading = true;
                        this.$axios.post(this.$baseUrl+'/fileLog/updateTemplateInfo.do',this.$qs.stringify({
                            file_log_templateName:this.currentLeftItem.file_log_templateName,
                            file_log_templateKey:this.currentLeftItem.file_log_templateKey,
                            file_log_fields:JSON.stringify(this.tableData),
                            updateState:this.tableChangeState  ? 2 : 1
                        }))
                            .then(res=>{
                                this.loading = false;
                                let obj = res.data;
                                if(obj.success == 'true'){
                                    layer.msg(obj.message,{icon:1})
                                    //修改左侧名称
                                    for (let i in this.leftData){
                                        if(this.currentLeftItem.file_log_templateKey === this.leftData[i].file_log_templateKey){
                                            this.leftData[i].file_log_templateName = this.currentLeftItem.file_log_templateName;
                                            break;
                                        }
                                    }
                                    //更新用于比较的对象
                                    this.compareObj = JSON.parse(JSON.stringify(this.currentLeftItem));

                                    //更改 按钮状态
                                    this.commitState = false
                                    this.tableChangeState = false
                                }else {
                                    layer.msg(obj.message,{icon:5})
                                }
                            })
                            .catch(err=>{
                                this.loading = false;
                            })
                    })
                }else{
                    layer.msg('名称不能为空',{icon:5})
                }
            }
        },
        components:{
            vBasetable2
        }
    }
</script>

<style scoped>
    .content-wapper{
        margin: 10px;
        display: flex;
        height: calc(100vh - 238px);
    }
    .left-wapper{
        width: 250px;
        height: 100%;
        background: #303e4e;
        margin-right: 2px;
    }
    .left-tit{
        height: 60px;
        font-size: 20px;
        line-height: 60px;
        font-weight: 600;
        text-align: center;
        background: #2c76bd;
    }
    .left-item{
        height: 40px;
        text-align: center;
        position: relative;
        line-height: 40px;
        border-bottom: 1px solid #3f5167;
    }
    .left-item:hover{
        background: #36475a;
        cursor: pointer;
    }
    .left-item:hover  .edit{
        display: inline-block;
    }
    .chooseClass{
        background: #3f5167;
    }
    .edit{
        position: absolute;
        right: 10px;
        display: none;
        top: 14px;
        font-size: 12px;
    }
    .right-wapper{
        height: 100%;
        flex: 1;
        background: #303e4e;
        padding: 0 10px;
        /*padding-top: 0;*/

    }
    .right-tit{
        height: 60px;
        font-size: 20px;
        line-height: 60px;
        font-weight: 600;
        color: #2c76bd;
        border-bottom: 1px solid #5a7494;
        display: flex;
        justify-content: space-between;
    }
    .tit-input{
        width: 50%;
    }
    .tit-input /deep/ .el-input__inner{
        border-top: 0;
        border-left: 0;
        border-right: 0;
        font-size: 20px;
    }
    .btn-wapper{
        float: right;
        margin-right: 10px;
    }
    /deep/.el-input.is-disabled .el-input__inner {
        background-color: #303e4e;
        border-color: #909399;
    }
</style>
