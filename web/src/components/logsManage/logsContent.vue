<template>
    <div v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
        <el-row :gutter="10">
            <el-col :span="4">
                <div class="content-left-wapper">
                    <div class="history-title">检索历史</div>
                    <ul class="history-content">
                        <li v-for="(item,index) in historySearchArr" :key="index" @click="historyListClick(item)" :title="JSON.stringify(item)">
                            <span v-for="(kn,index) in item" v-if="kn.length > 0 && index !=='size' && index !=='id' && index !=='page'">{{kn}},</span>
                        </li>
                    </ul>
                </div>
            </el-col>
            <el-col :span="20">
                <div class="content-right-wapper">
                    <div class="searchlogs-tools">
                        <el-button v-show="moreDeleteBtn" type="danger" plain size="mini" style="background: 0;margin: 0 3px;" @click="moreDelete">删除</el-button>
                        <div class="searchlogs-tools-left">
                            <el-form :inline="true" class="demo-form-inline" style="display: flex;">
                                <el-form-item style="flex: 1;" class="inputBox">
                                    <el-input class="input-fliterlog" v-model="filterWords" placeholder="输入过滤条件"></el-input>
                                </el-form-item>
                                <el-form-item>
                                    <el-button type="primary" class="filterLogs" @click="filterLogs" style="padding: 8px 5px;">过滤</el-button>
                                </el-form-item>
                            </el-form>
                        </div>
                        <div class="searchlogs-tools-center">
                            检索到日志 <b class="totalLogs">{{allCounts}}</b> 条，最大展示
                            <el-select v-model="selectVal" placeholder="请选择" size="mini" style="width: 84px;" @change="seletChange" class="maxSelect">
                                <el-option
                                    v-for="item in selectOption"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select> 条
                        </div>
                        <div class="searchlogs-tools-right">
                            <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="size" :total="total"></el-pagination>
                            <el-button v-if="exportBtn" type="primary" class="exportLogs" plain size="mini" @click="exportLogs">导出</el-button>
                        </div>
                    </div>
                    <v-basetable :tableHead="tableHead" :tableData="tableData" :selection="moreDeleteBtn" :busName="{'selectionName':moreSelName}"></v-basetable>
                </div>
            </el-col>
        </el-row>
        <div id="form-wapper">
            <div class="form-wapper-content">
                <el-form label-width="80px" label-position="top">
                    <el-form-item label="日志总条数">
                        <el-input :placeholder="allCounts" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="导出日志条数">
                        <el-input class="exportNum" type="number"></el-input>
                    </el-form-item>
                </el-form>
                <p class="instruction">说明：日志以时间倒序排列导出</p>
            </div>
        </div>
        <div v-if="this.layerObj.layerState">
            <vListdetails :baseConfig="this.baseConfig" :detailsData="this.layerObj.detailData"></vListdetails>
        </div>
    </div>
</template>

<script>
    import vListdetails from '../common/Listdetails';
    import vBasetable from '../common/Basetable'
    import bus from '../common/bus';

    export default {
        props:{
            //表头
            tableHead:{
                type: Array
            },
            //检索条件
            searchConditions:{
                type:Object
            },
            //检索路径
            searchUrl:{
                type:String
            },
            //导出按钮
            exportBtn:{
                type:Boolean
            },
            //批量删除按钮
            moreDeleteBtn:{
                type:Boolean,
                default(){
                    return false;
                }
            },
            //产看详情弹窗状态
            layerObj:{
                type:Object,
                default(){
                    return{
                        detailData:{},//弹窗数据
                        layerState:false//弹窗状态
                    }
                }
            }
        },
        data(){
            return{
                loading:false,
                baseConfig:{ //弹窗基础配置
                    type:'2',
                    title:'日志详情',
                    areaWidth:'620px',//宽度
                    areaHeight:'520px'//高度
                },
                moreSelName:'',//多选监听名称
                moreDeleteArr:[],//多选删除集合
                size:13,//每页显示条数
                total:0,//总条数 （用于分页）
                allCounts:0,//总条数 （用于记录）
                c_page:1,//当前高亮页码
                tableData:[],//表格数据
                currentPageLogs:[],//保存当前页的数据
                firstSearch:true,//第一次请求
                saveCondition:{},//将查询条件保存
                filterWords:'',//过滤关键词
                filterLogsArr:[],//过滤后的日志数组
                selectOption:[ //下拉框数据
                    {
                        value:100000,
                        label:'10万'
                    },
                    {
                        value:200000,
                        label:'20万'
                    },
                    {
                        value:500000,
                        label:'50万'
                    },
                    {
                        value:1000000,
                        label:'100万'
                    },
                    {
                        value:10000000,
                        label:'1000万'
                    },
                    {
                        value:100000000,
                        label:'1亿'
                    },
                ],
                getIndex:0,
                selectVal:'10万',
                historySearchArr:[], //检索历史记录
                exporting:false //记录是否有导出任务正在进行
            }
        },
        created(){
            //判断是否有多选按钮
            if (this.moreDeleteBtn){
                this.moreSelName = JSON.stringify(new Date());
                //监听选中的资产
                bus.$on(this.moreSelName,(params)=>{
                    this.moreDeleteArr = [];
                    params.forEach((item)=>{
                        let obj = {}
                        obj.index = item.index;
                        obj.type = item.type;
                        obj.id = item.id;
                        this.moreDeleteArr.push(obj)
                    })
                })
            }

        },
        mounted(){
            //获取日志数据
            if(this.searchConditions.id === undefined){
                this. getLogsData(this.searchConditions,1);
            }
        },
        /*activated(){
            bus.$on('logDetails',(rowData)=>{
                this.layerState = true;
                this.detailData = rowData;
                console.log('jj')
            })
            bus.$on('closeLayer',(params)=>{
                console.log('cc')
                //更改弹窗状态
                this.layerState = false;
            })
        },
        deactivated(){
            bus.$off('logDetails');
            bus.$off('closeLayer');

        },*/
        computed: {
            closeState() {
                return this.$store.state.closeState;
            }
        },
        watch:{
            /*检测检索条件的变化 重新获取数据*/
            searchConditions:{
                handler(val, oldVal){
                    this.getLogsData(this.searchConditions,1);
                    this.firstSearch=true;
                    this.c_page = 1;
                },
                deep:true
            },
            //检测删除日志操作是否完成
            closeState:{
                handler(newV, oldVal){
                    if(newV === true){
                        this. getLogsData(this.searchConditions,1);
                        this.$store.commit('updateCloseState',false)
                    }else{
                    }
                },
                deep:true
            },
            /*检测弹窗状态*/
            'layerObj.layerState': {
                handler(newV, oldV) {
                    if (newV){
                        bus.$on('closeLayer',(params)=>{
                            //更改弹窗状态
                            this.layerObj.layerState = false;
                            //及时关闭检测
                            bus.$off('closeLayer')
                        })
                    }
                },
                deep: true,
                immediate: true
            }
        },
        methods:{
            /*获取日志信息*/
            getLogsData(searchObj,page){
                this.filterWords = '';
                this.loading = true;
                //赋值
                let obj = searchObj;
                obj.page = page;
                obj.size = this.size;
                var hsObj = {};
                hsObj.hsData = obj;
                //请求
                this.$nextTick( ()=> {
                    this.$axios.get(this.$baseUrl+'/'+this.searchUrl,{
                        params:hsObj
                    })
                        .then((res) => {
                            this.loading = false;
                            let logsData = res.data[0].list;
                            /*logsData.forEach(item =>{
                                item.equipmentname =  '<a class="goToEquipment">'+item.equipmentname+'</a>'
                            })*/
                            //填充表格数据
                            this.tableData = logsData;
                            this.currentPageLogs = res.data[0].list;
                            //检索发起的请求
                            if(this.firstSearch){
                                this.allCounts = res.data[0].count;
                                //填充分页数据
                                if(this.allCounts >100000){
                                    this.total = 100000;
                                }else{
                                    this.total = this.allCounts;
                                }
                                //将查询条件保存，用于分页
                                this.saveCondition = this.searchConditions;
                                //添加到历史列表
                                let searchObj = Object.assign({}, obj);
                                if(JSON.stringify(this.historySearchArr).indexOf(JSON.stringify(searchObj)) == -1 ){
                                    this.historySearchArr.push(searchObj);
                                }


                            }

                        })
                        .catch((err) => {
                            this.loading = false;
                            console.log(err);
                        })
                })
            },
            /*历史记录点击查询日志*/
            historyListClick(kn){
                this.getLogsData(kn,1);
                this.firstSearch=true;
                this.c_page = 1;
            },
            //分页页码改变
            handleCurrentChange(page){
                //获取数据
                this.getLogsData(this.saveCondition,page);
                //改变标识
                this.firstSearch = false;
                //判断
                if((page * this.size) > this.allCounts){
                    layer.tips('这是最后一页', '.el-pager .active', {
                        tips: [1, '#3595CC'],
                        time: 4000
                    });
                }else{
                    if($('.el-pager .active').next().length < 1){
                        layer.tips('可通过更改最大显示数量，查看后面的数据', '.maxSelect', {
                            tips: [1, '#3595CC'],
                            time: 4000
                        });
                    }
                }
            },
            /*下拉框改变函数*/
            seletChange(selVal){
                //判断所选的展示条数
                if(selVal < this.allCounts){
                    this.total = selVal;
                }else{
                    this.total = this.allCounts;
                }
                //所选值过大时，进行提示
                const html = '数据量过大时，可通过 <精确查询> 提高查询效率'
                if(selVal >= 10000000){
                    layer.tips('提示: '+html, '.maxSelect', {
                        tips: [1, '#3595CC'],
                        time: 4000
                    });
                }
            },
            /*日志过滤*/
            filterLogs(){
                //清空存储的过滤的日志
                this.filterLogsArr = [];
                //判断过滤字段是否为空
                if(this.filterWords != ''){
                    //正则
                    let reg = new RegExp(this.filterWords,"gi");
                    //循环拿出想要的日志列表
                    for(let i=0;i<this.tableData.length;i++){
                        for(let j in this.tableData[i]){
                            //如果字符串中不包含目标字符会返回-1
                            if(String(this.tableData[i][j]).match(reg)){
                                //创建一个对象 用于替换过滤出来的日志对象   （避免对象赋值后 更改问题）
                                this.filterLogsArr.push(this.tableData[i]);
                                break;
                            }
                        }
                    }
                    //替换关键字段
                    for(let n=0;n<this.filterLogsArr.length;n++){
                        console.log("dd")
                        for(let m in this.filterLogsArr[n]){
                            this.filterLogsArr[n][m] = String(this.filterLogsArr[n][m]).replace(this.filterWords,'<span style="color:red;">'+this.filterWords+'</span>');
                        }
                    }
                    //重新加载表格数据
                    this.tableData = this.filterLogsArr;
                }else{
                    //重新加载表格数据
                    this.tableData = this.currentPageLogs;
                }

            },
            /*日志导出*/
            exportLogs(){
                if(!this.exporting){
                    //查一遍数据
                    this.getLogsData(this.searchConditions,1);

                    //出现弹窗
                    layer.open({
                        type: 1,
                        title: "导出日志",//标题
                        area: ['350px', '450'], //宽高
                        btn: ['确定', '取消'], //按钮
                        btn1: (index)=> {
                            //获取导出的日志的条数
                           let exportSize = $('.exportNum input')[1].value;
                           //判断合理性
                            if(exportSize == ''){
                                layer.msg('导出日志条数为空',{icon: 5});
                            }else if(exportSize <= 0){
                                layer.msg('导出日志条数错误',{icon: 5});
                            }else{
                                //定义传送条件
                                let exportObj = this.searchConditions;
                                exportObj.exportSize = exportSize;
                                let hsObj = {
                                    hsData : JSON.stringify(exportObj)
                                }
                                //创建进度盒子
                                bus.$emit('exportStart','true');
                                //将导出任务存在localstorage中用于页面打开时加载
                                localStorage.setItem('exportLogs','true');
                                //导出请求
                                this.$nextTick(()=>{
                                    this.$axios.post('/jz/log/exportLogList.do',this.$qs.stringify(hsObj))
                                        .then((res)=>{
                                            //console.log(res.data[0].msg)
                                            if(res.data[0].state == 'false'){
                                               // closeProgress()
                                                layer.msg(res.data[0].msg,{icon: 5});
                                                this.$emit('exportFail','false');
                                                //删除本地保存的导出任务
                                                localStorage.removeItem('exportLogs');
                                            }
                                        })
                                        .catch((err)=>{
                                            this.$emit('exportFail','false');
                                        })
                                })
                            }
                            layer.close(index);
                        },
                        content:$('#form-wapper').html()
                    })
                }else{
                    layer.msg('有导出任务正在进行中，请稍后',{icon: 5});
                }
            },
            /*批量删除按钮*/
            moreDelete(){
                if(this.moreDeleteArr.length === 0){
                    layer.msg('未选中任何资产',{icon: 5});
                }else{
                    this.removeLog(this.moreDeleteArr);
                }
            },
            /*删除操作参数处理*/
            removeParams(arr){
                let arr2 = [];
                for(let i in arr){
                    let obj = {}
                    obj.index = arr[i].index;
                    obj.type = arr[i].type;
                    obj.id = arr[i].id;
                    arr2.push(obj)
                }
                this.removeLog(arr2)
            },
            /*删除日志*/
            removeLog(arr){
                //询问框
                layer.confirm('您确定删除日志信息么？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.load(1);
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/log/deleteById.do', this.$qs.stringify({
                           hsData : JSON.stringify(arr)
                        }))
                            .then((res)=>{
                                layer.closeAll('loading');
                                if(res.data == "DELETED"){
                                    layer.msg('删除成功',{icon: 1});
                                    this.$store.commit('updateCloseState',true)

                                }else{
                                    layer.msg('删除失败',{icon: 5});
                                }
                            })
                            .catch((err)=>{
                                layer.closeAll('loading');
                                layer.msg('删除失败',{icon: 5});
                            })
                    })
                    //关闭弹窗
                    layer.close();
                }, function(){
                    layer.close();
                });

            }
        },
        components:{
            vListdetails,
            vBasetable
        }
    }
</script>

<style scoped>
    .content-left-wapper,.content-right-wapper{
        height: 600px;
        /*background:rgb(26,36,47);*/
        background: #303e4e;
        padding: 0 5px 5px 5px;
    }
    .content-left-wapper{
        /*border-right: 2px solid #303e4e;*/
    }
    .history-title{
        height: 90px;
        text-align: center;
        line-height: 90px;
        border-bottom: 1px solid #5a7494;
        position: relative;
        z-index: 2;
        background: #303e4e;

    }
    .searchlogs-tools{
        height: 50px;
        border-bottom: 1px solid #4e7ba9;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
    .filterLogs{
        height: 28px;
    }
    .searchlogs-tools-left{
        margin-top: 15px;
        flex: 1;
    }
    .searchlogs-tools-center{
        font-size: 15px;
        margin: 0 5px;
    }
    .searchlogs-tools-right{
        display: flex;
    }
    .totalLogs{
        color: #409EFF;
    }
    .history-content{
        height: 507px;
        overflow-y: auto;
        position: relative;
        top: -41px;
    }
    .history-content li{
        padding: 0 10px;
        height: 40px;
        line-height: 40px;
        text-align: center;
        cursor: pointer;
        font-size: 12px;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        outline: none;
        position: relative;
        border-bottom: 1px solid #3a4a5d;
    }
    .history-content li:hover{
        background: #5a7494;
    }
    #form-wapper{
        display: none;
    }
    .exportLogs{
        height: 28px;
        line-height: 28px;
        padding: 0 5px;
    }
    .form-wapper-content{
        padding: 10px 20px;
        height: calc(100% - 20px);
        background: #303e4e;
    }
    .instruction{
        font-size: 12px;
    }

</style>
