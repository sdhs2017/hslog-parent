<template>
    <div class="content-bg">
        <div class="top-title">元数据</div>
        <div class="prop-wapper">
            <div class="left-wapper">
                <div class="left-tit">数据库</div>
<!--                <ul class="left-list" >-->
<!--                    <li v-for="(item,index) in this.indexArr" :key="index" v-bind:class="{'current-li': currentIndex == index}" @click="chooseIndex(item,index)">{{item}}</li>-->
<!--                </ul>-->
                <div class="left-list">
                    <el-tree
                        :props="propTree"
                        :data="propData"
                        @node-click="nodeClick"
                    >
                    </el-tree>
                </div>

            </div>
            <div class="right-wapper">
                <h3 class="table-tit">{{currentName}} <!--<button class=" " @click="setting()">配置</button>--></h3>
                <el-table
                    class="table-box"
                    :data="this.tableDataArr"
                    :border="true"
                    stripe
                    lazy
                    :load="load"
                    :height="this.tableHeight"
                    row-key="id"
                    :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
                    style="width: 100%">
                    <el-table-column
                        prop="fieldName"
                        label="字段名"
                        width="200">
                    </el-table-column>
                    <el-table-column
                        prop="fieldType"
                        label="数据类型"
                        :formatter="toLowcase"
                        width="">
                    </el-table-column>
                    <el-table-column
                        prop="fieldData"
                        label="是否可聚合">
                    </el-table-column>
                    <el-table-column
                        prop="format"
                        label="格式">
                    </el-table-column>
                    <el-table-column
                        prop="analyzer"
                        :formatter="toLowcase"
                        label="存储分词器">
                    </el-table-column>
                    <el-table-column
                        prop="searchAnalyzer"
                        :formatter="toLowcase"
                        label="查询分词器">
                    </el-table-column>
                    <el-table-column label="Raw">
                        <el-table-column
                            prop="rawType"
                            :formatter="toLowcase"
                            label="数据类型">
                        </el-table-column>
                        <el-table-column
                            prop="rawIgnoreAbove"
                            label="限制长度"
                            :formatter="toEmpty"
                        >
                        </el-table-column>
                    </el-table-column>
<!--                    <el-table-column v-for="(item,index) in this.tableHead" :key="index" :prop="item.prop" :label="item.label" :width="item.width !== 0 ? item.width : ''" :formatter="item.formatter">-->
<!--                        <el-table-column v-for="(ite,i) in item.children" :key="index" :prop="ite.prop" :label="ite.label" :width="ite.width !== 0 ? ite.width : ''" :formatter="ite.formatter"></el-table-column>-->
<!--                    </el-table-column>-->
                </el-table>
            </div>
        </div>
        <!--配置弹窗-->

    </div>
    
</template>

<script>
    export default {
        name: "property",
        data() {
            return {
                //属性树
                propTree:{
                    children: 'menus',
                    label: 'menuName'
                },
                propData: [],
                //索引数组
                //indexArr:['hslog_packet','hslog_syslog'],
                //当前显示的索引index
                currentIndex:'0',
                currentName:'',
                tableHead:[{
                    "children": [],
                    "formatter": "",
                    "label": "字段名",
                    "prop": "fieldName",
                    "width": 200
                }, {
                    "children": [],
                    "formatter": "toLowcase",
                    "label": "数据类型",
                    "prop": "fieldType",
                    "width": 0
                }, {
                    "children": [],
                    "formatter": "changeVal",
                    "label": "是否可聚合",
                    "prop": "fieldData",
                    "width": 0
                }, {
                    "children": [],
                    "formatter": "",
                    "label": "格式",
                    "prop": "format",
                    "width": 0
                }, {
                    "children": [],
                    "formatter": "toLowcase",
                    "label": "存储分词器",
                    "prop": "analyzer",
                    "width": 0
                }, {
                    "children": [],
                    "formatter": "toLowcase",
                    "label": "查询分词器",
                    "prop": "searchAnalyzer",
                    "width": 0
                }, {
                    "children": [{
                        "children": [],
                        "formatter": "toLowcase",
                        "label": "数据类型",
                        "prop": "rawType",
                        "width": 0
                    }, {
                        "children": [],
                        "formatter": "toEmpty",
                        "label": "限制长度",
                        "prop": "rawIgnoreAbove",
                        "width": 0
                    }
                    ],
                    "formatter": "toLowcase1",
                    "label": "Raw",
                    "prop": "Raw",
                    "width": 0
                }
                ],
                //属性名称数据
                tableDataArr:[],
                //表格高度
                tableHeight:'',
                //当前类型
                currentType:''
            }
        },
        created(){
            //获取数据
           // this.getProps('hslog_packet');
            this.getTreeData()
            this.tableHeight = document.body.clientHeight - 320 ;
            window.onresize = () => {
                this.tableHeight = document.body.clientHeight - 320 ;
            };
        },
        methods:{
            /*索引列表点击*/
            chooseIndex(item,index){
                this.currentIndex = index;
                this.currentName = item;
                this.getProps(this.currentName)
            },
            /*获取propsData*/
            getProps(url,params){
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+ url,this.$qs.stringify(params))
                        .then(res=>{
                            layer.closeAll('loading');
                            this.tableDataArr = [];
                            setTimeout(()=>{
                                this.tableDataArr = res.data;
                            },10)

                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*转小写*/
            toLowcase(row, column, cellValue, index){
                if(cellValue){
                    let val = cellValue.toLowerCase();
                    return val
                }else {
                    return cellValue
                }
            },
            toEmpty(row, column, cellValue, index){
                if (cellValue == '0'){
                    return ''
                }
            },
            //转换布尔值
            changeVal(row, column, cellValue, index){
                if (cellValue == true){
                    return '是'
                } else if(cellValue == false){
                    return '否'
                }else{
                    return cellValue;
                }
            },
            /*节点点击*/
            nodeClick(node){
                this.currentName = node.menuName;
                if (node.state  == 1){
                    this.currentType = 'template'
                    let url = '/metadata/getMedataByTemplateDynamically.do';
                    let param = {
                        id:'',
                        template:node.id
                    }
                    this.getProps(url,param)

                } else if (node.state == 2) {
                    this.currentType = 'index'
                    let url = '/metadata/getMedataByIndexDynamically.do';
                    let param = {
                        index:node.id
                    }
                    this.getProps(url,param)
                }
            },
            //获取树节点数据
            getTreeData(){
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+'/metadata/getIndexTree.do','')
                        .then(res=>{
                            layer.closeAll('loading');
                            this.propData = res.data;
                        })
                        .catch(err=>{
                            layer.closeAll('loading');

                        })
                })
            },
            /*表格懒加载*/
            load(tree, treeNode, resolve){
                let url = '';
                let param = {};
                if (this.currentType === 'template') {
                    url = '/metadata/getMedataByTemplateDynamically.do';
                    param = {
                        id:tree.id,
                        template:this.currentName
                    }
                }else{
                    url = '/metadata/getMedataByIndexDynamically.do';
                    param = {
                        id:tree.id,
                        index:this.currentName
                    }
                }
                this.$nextTick(()=>{
                    layer.load(1);
                    this.$axios.post(this.$baseUrl+url,this.$qs.stringify(param))
                        .then(res=>{
                            layer.closeAll('loading');
                            resolve(res.data)
                        })
                        .catch(err=>{
                            layer.closeAll('loading');
                            console.log('获取失败')
                        })
                })
            }
        }
    }
</script>

<style scoped>
    .prop-wapper{
        display: flex;
        height: 100%;
    }
    .prop-wapper>div{
        margin: 10px;
        background: #303e4e;
    }
    .left-wapper{
        width: 250px;
        height: calc(100vh - 250px);
        border: 1px solid #5a7494;

    }
    .right-wapper{
        width: calc(100% - 280px);
        padding: 10px;
        /*height: calc(100vh - 250px);*/
    }
    .left-tit{
        height: 40px;
        line-height: 40px;
        padding-left: 20px;
        /* background: #5a7494; */
        background: #3a8ee6;
        font-size: 15px;
        font-weight: 600;
    }
    .left-list{
        height: calc(100% - 55px);
        overflow-y: auto;
        padding: 5px;
    }
    .left-list li{
        height: 36px;
        line-height: 36px;
        padding:0 10px;
        font-size: 13px;
        border-bottom: 1px solid #40556d;
        text-align: center;
    }
    .left-list li:hover{
        background: #40556d;
        cursor: pointer;
    }
    .left-list .current-li{
        background: #4a678a;
    }
    .table-tit{
        height: 50px;
        line-height: 50px;
        border-bottom: 1px solid #5a7494;
        padding-left: 20px;
        color: #3a8ee6;
    }
    .table-box{
        /*height: calc(100vh - 300px);*/
    }
    .right-wapper /deep/ .el-table thead.is-group th {
        background: #485c73;
    }
    .right-wapper /deep/ .el-table--border, .el-table--group{
        border:0
    }
    .right-wapper /deep/ .el-table--border th, .el-table__fixed-right-patch {
        border-bottom: 1px solid #5a728e;
    }
    .right-wapper /deep/ .el-table--border th{
        border-right: 1px solid #5a728e;
    }
    .left-list /deep/ .el-tree-node__content{
        height: 30px;
        line-height: 30px;
        color: #D6DFEB;
    }
    .left-list /deep/ .el-tree-node__label{
        border-bottom: 1px solid #405165;
        color: #D6DFEB;
    }
    .right-wapper /deep/ .el-icon-arrow-right{
        color: #fff;
    }
    .left-list /deep/ .el-tree {
        width: 100%;
        height: 100%;
        /*overflow: scroll;*/
    }

    .left-list /deep/ .el-tree>.el-tree-node {
        display: inline-block;
        min-width: 100%;
    }
</style>
