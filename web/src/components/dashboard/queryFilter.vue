<template>
    <div>
    <!--   <div>
           <el-input v-model="queryVal" placeholder="请输入内容" size="mini"></el-input>
       </div>-->
        <div class="filter-wapper">
            <ul class="filter-ul">
                <li v-for="(item ,i) in filterArr" :key="i" :class="{noview:!item.enable}">
                    <span v-if="item.label_status" @click="resiveFilter(i)">{{item.label}}</span>
                    <span v-else>
                        <span @click="resiveFilter(i)" v-if="item.operator === 'is match' || item.operator === 'is not match' || item.operator === 'is term' || item.operator === 'is not term'">{{item.field}} : {{item.operator}} <b>{{item.value}}</b></span>
                        <span @click="resiveFilter(i)" v-if="item.operator === 'is between' || item.operator === 'is not between' ">{{item.field}} : {{item.operator}} <b>{{item.start}}-{{item.end}}</b></span>
                        <span @click="resiveFilter(i)" v-if="item.operator === 'is one of match' || item.operator === 'is not one of match' || item.operator === 'is one of term' || item.operator === 'is not one of term'">{{item.field}} : {{item.operator}} <b>{{item.values}}</b></span>
                        <span @click="resiveFilter(i)" v-if="item.operator === 'exists' || item.operator === 'does not exists' ">{{item.field}} : {{item.operator}} </span>
                    </span>

                    <i class="el-icon-view" @click="item.enable = false" v-if="item.enable" title="关闭过滤"></i>
                    <del v-else @click="item.enable = true" title="开启过滤">
                        <span class="i-del"></span>
                        <i class="el-icon-view" title="关闭过滤"></i>
                    </del>
                    <i class="el-icon-close" @click="filterArr.splice(i,1)" ></i>
                </li>
                <span class="addFilter" @click="addFilters()"> <i class="el-icon-plus"></i>添加筛选</span>
            </ul>
        </div>
        <el-dialog title="编辑筛选" :visible.sync="filterDialog" width="400px">
            <div class="filter-form-wapper" v-loading="loading"  element-loading-background="rgba(26,36,47, 0.2)">
                <div class="filter-form">
                    <el-form  inline label-width="80px" label-position="top" style="display: flex;" v-if="useObject === 'dashboard'">
                        <el-form-item label="Index Pattern" style="width:100%;">
                            <el-select v-model="form.template_name" style="width:100%;"  @change="indexPatternChange"  placeholder="请选择" filterable default-first-option  class="chooseClass iss"   size="mini">
                                <el-option
                                    v-for="item in indexPatternOpt"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-form>
                    <el-form  inline label-width="80px" label-position="top" style="display: flex;">
                        <el-form-item label="Field" style="width:70%;">
                            <el-select v-model="form.field" @change="fieldChange"  placeholder="请选择" filterable default-first-option  class="chooseClass iss"   size="mini">
                                <el-option
                                    v-for="item in fieldOpt"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="Operator">
                            <el-select v-model="form.operator" @change="operatorChange()" filterable allow-create default-first-option  class="chooseClass iss"   size="mini">
                                <el-option
                                    v-for="item in operatorOpt"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-form>
                    <!--初始显示-->
                    <el-form ref="form" label-width="80px" label-position="top" v-if="form.operator === '' || form.field === '' ">
                        <el-form-item label="Value">
                            <el-input v-model="form.value" size="mini"></el-input>
                        </el-form-item>
                    </el-form>
                    <!--单个值-->
                    <el-form ref="form" label-width="80px" label-position="top" v-if="form.field !== '' && (form.operator === 'is match' || form.operator === 'is not match' || form.operator === 'is term' || form.operator === 'is not term')">
                        <!--string-->
                        <el-form-item label="Value" v-if="fieldType[form.field] === 'string'">
                            <el-input v-model="form.value" size="mini"></el-input>
                        </el-form-item>
                        <!--number-->
                        <el-form-item label="Value" v-if="fieldType[form.field] === 'number'">
                            <el-input v-model="form.value" type="number" size="mini"></el-input>
                        </el-form-item>
                        <!--ip-->
                        <el-form-item label="Value" v-if="fieldType[form.field] === 'ip'">
                            <el-input v-model="form.value" size="mini"></el-input>
                        </el-form-item>
                        <!--date-->
                        <el-form-item label="Value" v-if="fieldType[form.field] === 'date'">
                            <el-date-picker
                                style="width: 100%;"
                                v-model="form.value"
                                type="datetime"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                placeholder="选择日期时间">
                            </el-date-picker>
                        </el-form-item>
                        <!--Boolean-->
                        <el-form-item label="Value" v-if="fieldType[form.field] === 'boolean'">
                            <el-select v-model="form.value" placeholder="请选择" style="width: 100%">
                                <el-option label="true" value="true"></el-option>
                                <el-option label="false" value="false"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-form>
                    <!--两个值-->
                    <el-form ref="form" label-width="80px" label-position="top" v-if="form.field !== '' && (form.operator === 'is between' || form.operator === 'is not between') ">
                        <!--string-->
                        <el-form-item label="Value" v-if="fieldType[form.field] === 'string'" class="between-wapper">
                            <el-input v-model="form.start" size="mini"></el-input>
                            <span> - </span>
                            <el-input v-model="form.end" size="mini"></el-input>
                        </el-form-item>
                        <!--number-->
                        <el-form-item label="Value" v-if="fieldType[form.field] === 'number'"  class="between-wapper">
                            <el-input v-model="form.start" type="number" size="mini"></el-input>
                            <span> - </span>
                            <el-input v-model="form.end" type="number" size="mini"></el-input>
                        </el-form-item>
                        <!--ip-->
                        <el-form-item label="Value" v-if="fieldType[form.field] === 'ip'"  class="between-wapper">
                            <el-input v-model="form.start" size="mini"></el-input>
                            <span> - </span>
                            <el-input v-model="form.end" size="mini"></el-input>
                        </el-form-item>
                        <!--date-->
                        <el-form-item label="Value" v-if="fieldType[form.field] === 'date'"  class="between-wapper">
                            <el-date-picker
                                v-model="form.start"
                                type="datetime"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                placeholder="选择日期时间">
                            </el-date-picker>
                            <span> - </span>
                            <el-date-picker
                                v-model="form.end"
                                type="datetime"
                                size="mini"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                placeholder="选择日期时间">
                            </el-date-picker>
                        </el-form-item>
                    </el-form>
                    <!--多个值-->
                    <div v-if="form.field !== '' && ( form.operator === 'is one of match' || form.operator === 'is not one of match' || form.operator === 'is one of term' || form.operator === 'is not one of term') ">
                        <p class="values-tit">Value</p>
                        <div class="values-wapper">
                            <span v-for="(item ,n) in form.values" :key="n">{{item}}<i class="el-icon-close" title="删除" @click="form.values.splice(n,1)"></i></span>
                        </div>
                        <div class="morevalue-form">
                            <el-form style="height: 32px;width: 300px;">
                                <!--string-->
                                <el-form-item v-if="fieldType[form.field] === 'string'">
                                    <el-input v-model="addFromValue" size="mini"></el-input>
                                </el-form-item>
                                <!--number-->
                                <el-form-item v-if="fieldType[form.field] === 'number'">
                                    <el-input v-model="addFromValue" type="number" size="mini"></el-input>
                                </el-form-item>
                                <!--ip-->
                                <el-form-item  v-if="fieldType[form.field] === 'ip'">
                                    <el-input v-model="addFromValue" size="mini"></el-input>
                                </el-form-item>
                                <!--date-->
                                <el-form-item  v-if="fieldType[form.field] === 'date'">
                                    <el-date-picker
                                        style="width: 300px;"
                                        v-model="addFromValue"
                                        type="datetime"
                                        size="mini"
                                        value-format="yyyy-MM-dd HH:mm:ss"
                                        placeholder="选择日期时间">
                                    </el-date-picker>
                                </el-form-item>
                            </el-form>
                            <el-button type="primary" style="background: 0;border-radius:0;" size="mini" @click="addValue()">添加</el-button>
                        </div>

                    </div>

                </div>
                <div class="custom-laber-wapper">
                    <el-form  label-width="82px" style="margin-bottom: 10px;">
                        <el-form-item label="自定义标签" style="margin-bottom: 0">
                            <el-switch v-model="form.label_status"></el-switch>
                        </el-form-item>
                    </el-form>
                    <el-form  label-width="82px" v-if="form.label_status" label-position="left">
                        <el-form-item label="标签名称">
                            <el-input v-model="form.label" size="mini"></el-input>
                        </el-form-item>
                    </el-form>
                </div>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="filterDialog = false">取 消</el-button>
                    <el-button  type="primary" v-if="form.operator === 'exists' || form.operator === 'does not exists'" @click="saveFilter()">确 定</el-button>
                    <el-button v-else  type="primary" @click="saveFilter()" :disabled="(form.field === '' || form.operator === '' || (form.value === '' && form.start === '' && form.values.length === 0 && form.end === '') || (form.label_status && form.label === '')) ? 'disabled' : false">确 定</el-button>
                </div>
            </div>
        </el-dialog>
    </div>
    
</template>

<script>
    import bus from '../common/bus';
    export default {
        name: "queryFilter",
        props:{
            //使用类型  查看/修改、添加
            useType:{
                type:String,
                default(){
                    return 'see'
                }
            },
            //使用对象  chart/dashboard
            useObject:{
                type:String
            },
            //监听事件名称
            busName:{
                type:String
            },
            //初始值
            defaultArr:{
                type: String,
                default(){
                    return ''
                }
            },
            filterArr:{
                type: Array,
                default(){
                    return []
                }
            },
            templateName:{
                type:String,
                default(){
                    return ''
                }
            },
            preIndexName:{
                type:String,
                default(){
                    return ''
                }
            },
            suffixIndexName:{
                type:String,
                default(){
                    return ''
                }
            },
            ids:{
                type:Array,
                default(){
                    return []
                }
            }
        },
        data() {
            return {
                loading:false,
                dialogType:'add',
                filterDialog:false,
                addFromValue:'',
                form:{
                    template_name:'',
                    field:'',
                    fieldType:'',
                    operator:'',
                    value:'',
                    start:'',
                    end:'',
                    values:[],
                    enable:true,
                    label_status:false,
                    label:''
                },
                currentIndex:'',
                queryVal:'',
                //indexPattern集合
                indexPatternOpt:[],
                //filterArr:[],
                //field类型   string、number、ip、date
                fieldType:{ },
                //field值集合
                fieldOpt:[],
                //operator值集合
                operatorOpt:[],

            }
        },
        created() {
            /*if(this.useObject === 'dashboard'){
                this.getIndexPattern()
            }*/
        },
        watch:{
            /*'defaultArr'(){
                //加载初始值
                if(this.defaultArr !== ''){
                    console.log('ddd')
                    this.filterArr = JSON.parse(this.defaultArr);
                    //判断是否是chart使用的
                    if(this.useObject !== 'dashboard'){
                        //获取field数据集合
                        this.getFieldData();
                    }

                }
            },*/
            'filterDialog'(){
                if(!this.filterDialog){
                    this.dialogType = 'add';
                    this.form={
                        template_name:'',
                        field:'',
                        operator:'',
                        fieldType:'',
                        value:'',
                        start:'',
                        end:'',
                        values:[],
                        enable:true,
                        label_status:false,
                        label:''
                    }
                }
            },
            //筛选值 集合
            filterArr:{
                handler(newV,oldV) {
                    if(JSON.stringify(oldV) == JSON.stringify(newV)){
                        //提交到父级页面
                        let str = JSON.stringify(this.filterArr)
                        bus.$emit(this.busName,str)
                    }else{
                        //判断是否是chart使用的
                        if(this.useObject !== 'dashboard'){
                            //获取field数据集合
                            this.getFieldData();
                        }
                    }
                },
                immediate: false,
                deep: true
            },
            /*监听数据源 索引*/
            suffixIndexName(newVal,oldVal){
                //判断是否有默认值  oldVal为空表明没有初始的filter值
                if(oldVal !== ''){//
                    //重置
                    this.filterArr = [];
                }
                this.form = {
                    field:'',
                    fieldType:'',
                    operator:'',
                    value:'',
                    start:'',
                    end:'',
                    values:[],
                    enable:true,
                    label_status:false,
                    label:''
                }
            }
        },
        methods:{
            /*添加过滤*/
            addFilters(){
                //判断是否已经选择数据源
                if(this.suffixIndexName !== '' || this.useObject === 'dashboard'){
                    this.filterDialog = true;
                    if(this.useObject === 'dashboard'){
                        this.getIndexPattern();
                    }else{
                        //获取fields数据
                        this.getFieldData();
                    }
                }else{
                    layer.msg('请先选择数据源',{icon:5})
                }
            },
            /*获取indexPattern*/
            getIndexPattern(){
                let chartIds = '';
                for (let i in this.ids){
                    chartIds += this.ids[i]+','
                }
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/BI/getDashboardTemplates.do',this.$qs.stringify({
                        ids:chartIds
                    }))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success === 'true'){
                                this.indexPatternOpt = [];
                                obj.data.forEach(item=>{
                                    this.indexPatternOpt.push({
                                        value:item,
                                        label:item
                                    })
                                })
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            /*获取field数据*/
            getFieldData(val){
                this.$nextTick(()=>{
                    this.loading = true;
                    let params = {};
                    //判断使用对象 chart dashboard
                    if(this.useObject === 'chart'){
                        params = {
                            suffix_index_name:this.suffixIndexName,
                            pre_index_name:this.preIndexName,
                            template_name:this.templateName
                        }
                    }else if(this.useObject === 'dashboard'){
                        params.template_name = val
                    }
                    this.$axios.post(this.$baseUrl+'/BI/getFieldByFilter.do',this.$qs.stringify(params))
                        .then(res=>{
                            this.loading = false;
                            //清空数据
                            this.fieldOpt = [];
                            this.fieldType = {};
                            //循环push
                            let arr = res.data;
                            arr.forEach(item =>{
                                this.fieldOpt.push({
                                    value:item.fieldName,
                                    label:item.fieldName
                                })
                                //判断类型
                                if(item.fieldType === 'long' || item.fieldType === 'integer' || item.fieldType === 'short' || item.fieldType === 'byte' || item.fieldType === 'double' || item.fieldType === 'float' || item.fieldType === 'half_float' || item.fieldType === 'scaled_float'){
                                    this.fieldType[item.fieldName] = 'number'
                                }else if(item.fieldType === 'boolean'){
                                    this.fieldType[item.fieldName] = 'boolean'
                                }else if(item.fieldType === 'text' || item.fieldType === 'keyword'){
                                    this.fieldType[item.fieldName] = 'string'
                                }else if(item.fieldType === 'ip'){
                                    this.fieldType[item.fieldName] = 'ip'
                                }else if(item.fieldType === 'date'){
                                    this.fieldType[item.fieldName] = 'date'
                                }
                            })
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            /*获取operator数据*/
            getOperatorData(field){
                console.log(field)
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/BI/getOperatorByFiledType.do',this.$qs.stringify({
                        fieldType:this.fieldType[field]
                    }))
                        .then(res=>{
                            this.loading = false;
                            this.operatorOpt = [];
                            res.data.forEach(item =>{
                                this.operatorOpt.push({
                                    value:item,
                                    label:item
                                })
                            })
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            /*indexPattern改变事件*/
            indexPatternChange(val){
                //清空值
                this.form.field = '';
                this.form.value = '';
                this.form.start = '';
                this.form.end = '';
                this.form.values = [];
                this.addFromValue = '';
                this.form.operator = '';
                this.form.fieldType = '';
                //查询field
                this.getFieldData(val);
            },
            /*field改变事件*/
            fieldChange(val){
                //清空值
                this.form.value = '';
                this.form.start = '';
                this.form.end = '';
                this.form.values = [];
                this.addFromValue = '';
                this.form.operator = '';
                this.form.fieldType = this.fieldType[val];
                //获取operator集合
                this.getOperatorData(val)
            },
            /*operator改变事件*/
            operatorChange(){
                //清空值
                this.form.value = '';
                this.form.start = '';
                this.form.end = '';
                this.form.values = [];
                this.addFromValue = '';
            },
            /*检测ip合法性*/
            checkIp(ip){
                //检测IP合法性
                let reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
                if(reg.test(ip)){
                    return true;
                }else{
                    return false;
                }
            },
            /*添加按钮*/
            addValue(){
                //判断值是否存在/是否合法
                if(this.addFromValue && !this.form.values.includes(this.addFromValue)){
                    //判断ip类型
                    if(this.fieldType[this.form.field] === 'ip'){
                        //检查ip合法性
                        if(!this.checkIp(this.addFromValue)){
                            layer.msg('IP不合法',{icon:5});
                            return;
                        }
                    }
                    this.form.values.push(this.addFromValue)
                    this.addFromValue = '';
                }
            },
            /*修改过滤条件*/
            resiveFilter(i){
                this.currentIndex = i;
                this.form =JSON.parse(JSON.stringify(this.filterArr[i]));
                console.log(this.form.field)
                this.dialogType = 'resive';
                if(this.useObject === 'dashboard'){
                    this.getIndexPattern()
                    this.getFieldData(this.form.template_name)
                }
                //获取operator数据集合
                this.getOperatorData(this.form.field);
                this.filterDialog = true;
            },
            /*保存过滤条件*/
            saveFilter(){
                if(this.dialogType === 'resive'){
                   // this.filterArr[this.currentIndex] = this.form;
                    this.$set(this.filterArr, this.currentIndex, this.form);
                }else{
                    //判断ip合法性
                    if(this.fieldType[this.form.field] === 'ip'){
                        if (this.form.value !== ''){
                            if(!this.checkIp(this.form.value)){
                                layer.msg('IP不合法',{icon:5});
                                return;
                            }
                        }
                        if (this.form.start !== ''){
                            if(!this.checkIp(this.form.start)){
                                layer.msg('起始IP不合法',{icon:5});
                                return;
                            }
                        }
                        if (this.form.end !== ''){
                            if(!this.checkIp(this.form.end)){
                                layer.msg('截止IP不合法',{icon:5});
                                return;
                            }
                        }
                    }
                    let obj = {};
                    let str = JSON.stringify(this.form);
                    obj = JSON.parse(str);
                    this.filterArr.push(obj);
                }
                this.filterDialog = false;
            }
        }
    }
</script>

<style scoped>
    .filter-wapper{
        display: flex;
        min-height: 24px;
        font-size: 12px;
        align-items: center;
    }
    .filter-wapper .filter-ul{
        display: flex;
        flex-wrap: wrap;
    }
    .addFilter{
        display: flex;
        align-items: center;
    }
    .filter-ul li{
        padding:3px 5px;
        border: 1px solid #48617d;
        font-size: 12px;
        margin: 5px;
        margin-left: 0;
        color: #a0cfff;
    }
    .filter-ul .noview{
        color: #ccc;
        text-decoration: line-through;
    }
    .filter-ul li span:hover{
        cursor: pointer;
        text-decoration: underline;
    }
    .filter-ul li .el-icon-view{
        margin-left: 3px;
    }
    .filter-ul li .el-icon-view:hover{
        color: #32ff1d;
    }
    .filter-ul li i:hover{
        cursor: pointer;
    }
    del{
        position: relative;
        cursor: pointer;
    }
    del:hover{
        color: #32ff1d;
    }
    del .i-del{
        position: absolute;
        height: 1px;
        width: 14px;
        background: #ccc;
        top: 8px;
        left: 2px;
        transform: rotate(-45deg);
    }
    .addFilter:hover{
        cursor: pointer;
        text-decoration: underline;
        color: #409eff;
    }
    .filter-form{
        padding: 10px 0;
        border-top: 1px solid #3c5d80;
        border-bottom: 1px solid #3c5d80;
    }
    .filter-btn{
        height: 50px;
        display: flex;
        justify-content: flex-end;
        align-items: center;
    }
    .between-wapper /deep/ .el-form-item__content{
        display: flex;
    }
    .between-wapper span{
        margin: 0 5px;
        color: #fff;
    }
    .values-tit{
        color: #e4956d;
        font-weight: 600;
        padding: 0;
    }
    .morevalue-form{
        display: flex;
        align-items: center;
        justify-content: space-between;
    }
    .values-wapper{
        margin: 5px 0;
        display: flex;
        flex-wrap: wrap;
        /*border: 1px solid #409eff;*/
    }
    .values-wapper span{
        border: 1px dashed #3c5d80;
        padding: 4px 5px;
        color: #409eff;
        font-size: 10px;
        margin: 5px;
        border-left: 2px solid #409eff;
    }
    .values-wapper span i{
        margin-left: 3px;
        cursor: pointer;
    }
    .values-wapper span i:hover{
        color: #e4956d;
    }
    .dialog-footer{
        margin-top: 10px;
        /* text-align: end; */
        display: flex;
        justify-content: space-around;
    }
    .custom-laber-wapper{
        border-bottom: 1px solid #3c5d80;
        padding: 10px 0;
    }
    .custom-laber-wapper /deep/.el-form--label-top .el-form-item__label{
        color: #fff!important;
    }
</style>
