<template>
    <div class="choose-wapper">
        当前数据源:
<!--        <el-select v-model="index" filterable  placeholder="请选择" @change="indexBlur"  size="mini" class="index-sel">-->
        <!--<el-cascader
            ref="refHandle"
            class="index-sel"
            v-model="index"
            placeholder=""
            allow-create="true"
            :options="dataArr"
            :props="{checkStrictly: true, label:'menuName' ,value:'id',children:'menus'}"
            :show-all-levels="false"
            filterable
            popper-class="cascaderPopper"
            @change="changeCascader"
            @blur="cascaderBlur"
            @focus="cascaderFocus"
        >
        </el-cascader>
        <el-input class="input-ss" size="mini" v-model="inputVal" ></el-input>-->
        <el-select v-model="templateVal"  placeholder="请选择" @change="templateValChange" style="margin-right: 25px;" class="chooseClass ts"  size="mini">
            <el-option
                v-for="item in templateOpt"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
        </el-select>
        <el-select v-model="indexFirstVal"  placeholder="请选择" @change="indexFirstValChange"  class="chooseClass ifs"   size="mini">
            <el-option
                v-for="item in indexFirstOpt"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
        </el-select>
        <el-select v-model="indexSecondVal"  placeholder="输入/选择" @change="indexSecondValChange" filterable allow-create default-first-option  class="chooseClass iss"   size="mini">
            <el-option
                v-for="item in indexSecondOpt"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
        </el-select>
    </div>
    
</template>

<script>
    import bus from '../common/bus';
    export default {
        name: "chooseIndex",
        props:{
            arr:{
                type:Array
            },
            busName:{
                type:String
            }
        },
        data() {
            return {
                /*index:'',
                inputVal:'',
                dataArr:[]*/
                templateVal:'',
                templateOpt:[],
                indexFirstVal:'',
                indexFirstOpt:[],
                indexSecondVal:'',
                indexSecondOpt:[]
            }
        },
        created(){
            /*//获取索引数据
            this.getIndex();*/
            //获取template
            this.getTemplateVal();
        },
        computed:{

        },
        watch:{
            arr:{
                handler() {
                    //获取数据集合
                    //this.getTemplateVal();
                    this.getIndexFirstOpt(this.arr[0]);
                    this.getIndexSecondOpt(this.arr[1]);
                    //赋值
                    this.$nextTick( ()=> {
                        this.templateVal = this.arr[0];
                        this.indexFirstVal = this.arr[1];
                        this.indexSecondVal = this.arr[2];
                    })
                },
                deep: true
            }
        },
        methods:{
/*            //获取index
            getIndex(){
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/metadata/getIndexTree.do',this.$qs.stringify())
                        .then(res=>{
                            layer.closeAll('loading');
                            this.dataArr = res.data;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            //层级下拉框值改变事件
            changeCascader(val){
                let obj = this.$refs.refHandle.getCheckedNodes();
                if (this.index.length == 2 ){
                    this.inputVal = this.index[1]
                }else{
                    this.inputVal = this.index[0]
                }
                //绑定事件
                bus.$emit(this.busName,this.index);
                //隐藏下拉框
                this.$refs.refHandle.dropDownVisible = false; //监听值发生变化就关闭它
            },
            //下拉框失去焦点
            cascaderBlur(){
                this.inputVal = $(".index-sel input").val();
                this.index = [$(".index-sel input").val()];
                //绑定事件
                bus.$emit(this.busName,this.index);
            },
            /!*下拉框获得焦点事件*!/
            cascaderFocus(){
                $(".index-sel input").val(this.inputVal);
                this.inputVal = ''
            },*/

            /*获取template*/
            getTemplateVal(){
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/metadata/getTemplates.do',this.$qs.stringify())
                        .then(res=>{
                            layer.closeAll('loading');
                            this.templateOpt = res.data;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取indexFirstOpt*/
            getIndexFirstOpt(val){
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/metadata/getPreIndexByTemplate.do',this.$qs.stringify({
                        templateName:val
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            //设置index前缀名组
                            this.indexFirstOpt = res.data;
                         /*   //判断数组长度是否为1  是 则直接赋值下一级
                            if(this.indexFirstOpt.length === 1){
                                this.indexFirstVal = res.data[0].value;
                                this.indexFirstValChange(this.indexFirstVal)
                            }*/

                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*获取indexSecondOpt*/
            getIndexSecondOpt(val){
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/metadata/getSuffixIndexByPre.do',this.$qs.stringify({
                        templateName:this.templateVal,
                        preIndexName:val
                    }))
                        .then(res=>{
                            layer.closeAll('loading');
                            //设置index后缀时间组
                            this.indexSecondOpt = res.data;
                          /*  //判断数组长度是否为1  是 则直接赋值下一级
                            if(this.indexSecondOpt.length === 1){
                                this.indexSecondVal = res.data[0].value;
                                this.indexSecondValChange(this.indexSecondVal)
                            }
*/
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*template改变事件*/
            templateValChange(val){
                //清空index的值
                this.indexFirstVal='';
                this.indexFirstOpt=[];
                this.indexSecondVal='';
                this.indexSecondOpt=[];
                //获取indexfirst值集合
                this.getIndexFirstOpt(val);
                this.$nextTick(()=>{
                    //绑定事件
                    let arr = [this.templateVal,this.indexFirstVal,this.indexSecondVal]
                    bus.$emit(this.busName,arr);
                })

            },
            /*indexFirst改变事件*/
            indexFirstValChange(val){
                //清空时间后缀
                this.indexSecondVal='*';
                this.indexSecondOpt=[];
                //获取时间后缀
                this.getIndexSecondOpt(val);
                this.$nextTick(()=>{
                    //绑定事件
                    let arr = [this.templateVal,this.indexFirstVal,this.indexSecondVal]
                    bus.$emit(this.busName,arr);
                })

            },
            /*indexSecond改变事件*/
            indexSecondValChange(val){
                let arr = [this.templateVal,this.indexFirstVal,val]
                //绑定事件
                bus.$emit(this.busName,arr);
            },
        }
    }
</script>

<style scoped>
    .choose-wapper{
        float: left;
        text-shadow: none;
        color: #409eff;
    }
    .choose-wapper .index-sel /deep/ .el-input__inner{
        border-top: 0;
        border-left: 0;
        border-right: 0;
        background: none;
        font-size: 16px;
        color: #e4956d;
    }
    .index-sel{
        position: relative;
        z-index: 3;
    }
    .input-ss{
        position: absolute;
        width: 209px;
        left: 135px;
        border: 0;
        font-size: 16px;
        z-index: 2;
    }
    .input-ss /deep/ .el-input__inner{
        border: 0;
        background: 0;
        color: #e4956d;
    }
    .choose-wapper .btn{
        background: none;
        border: 0;
        color: #409eff;
    }
    .header-ul .li-item-01,.header-ul .li-item-02{
        font-weight: 600;
        color:#409eff;
        font-size: 14px;
    }
    .li-item-01,.li-item-02{
        display: inline-block;
        width: 150px;
        height: 26px;
        line-height: 26px;
        color: #bfc9d4;
        font-size: 12px;
    }
    .prop-wapper{
        max-height: 400px;
        overflow-y: auto;
    }
    .content-ul li{
        border-bottom: 1px solid #3c5d80;
    }
    .choose-wapper{
        line-height: 20px;
    }
    .chooseClass /deep/ .el-input__inner{
        background: #1a242f;
        width: 150px;
    }
    .ifs /deep/ .el-input__inner{
        color: #de946f;
        border-right: 0;
    }
    .iss /deep/ .el-input__inner{
        color: #de946f;
        border-left: 0;
    }
</style>
