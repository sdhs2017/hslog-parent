<template>
    <div v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
        <el-table :data="tableData" ref="table"  stripe style="width: 100%;border: 0;" border :row-key="loadObj.key" @select="selectOne" @select-all="selectAll"  @selection-change="handleSelectionChange" :height="height" :empty-text="emptyText"
                  lazy
                  :load="load"
                  @expand-change="expandFunc"
                  :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
            <el-table-column
                v-if="selection"
                type="selection"
                width="40">
            </el-table-column>
            <template v-for="(item,index) in tableHead">
                <el-table-column v-if="item.prop !== 'tools'"  show-overflow-tooltip  :prop="item.prop"  :label="item.label" :width="item.width" :key="index">
                    <template slot-scope="scope">
                        <span v-if="!item.formatData && !item.clickFun " v-html="dataChange(scope.row,item.prop)"></span>
                        <el-button v-else-if="!item.formatData && item.clickFun " v-html="dataChange(scope.row,item.prop)" @click.native.prevent="item.clickFun(scope.row,scope.index)" type="text" size="mini" style="margin-left: 0;padding: 0px;color:#409EFF;"></el-button>
                        <span v-else-if="item.formatData && !item.clickFun " v-html="tableFormatter(dataChange(scope.row,item.prop),item.formatData,scope.row)" ></span>
                        <el-button v-else v-html="tableFormatter(dataChange(scope.row,item.prop),item.formatData,scope.row)" @click.native.prevent="item.clickFun(scope.row,scope.$index)" type="text" size="mini" style="margin-left: 0;padding: 0px;color:#409EFF;"></el-button>
                    </template>
<!--                    <template slot-scope="scope">-->
<!--                        <span >{{dataChange(scope.row,item.prop)}}</span>-->
<!--                    </template>-->
                </el-table-column>
                <el-table-column v-else-if ="item.prop === 'tools'" show-overflow-tooltip   :prop="item.prop"  :label="item.label" :width="item.width" :key="index">
                    <template slot-scope="scope">
                        <el-button v-for="(ic, ind) in item.btns" :key="ind"  @click.native.prevent="ic.clickFun(scope.row,scope.$index)" v-if="!ic.formatData ? true : tableFormatter(scope.row,ic.formatData,scope.row)" type="text" size="mini" style="margin-left: 0;padding: 0px;color:#D6DFEB;">
                            <i :class="ic.icon" :title="ic.text"></i>
                        </el-button>
                    </template>
                </el-table-column>
            </template>

        </el-table>

    </div>
</template>


<script>
    /*
    * 说明 此组件与Basetable区别 在于处理表格prop的参数多级问题 例如：a.b.c
    * */
    import vListdetails from '../common/Listdetails';
    import bus from '../common/bus';
    export default {
        name: "basetable",
        props:{
            emptyText:{
                type:String,
                default:()=>{
                    return '暂无数据'
                }
            },
            //高度
            height:'',
            //表头
            tableHead:{
                type: Array,
            },
            //数据
            tableData:{
                type:Array
            },
            //懒加载url-参数
            loadObj:{
              type:Object,
                default:()=>{
                    return {
                        rowKey:'',
                        key:'id',
                        url:'',
                        params:[]
                    }
                }
            },
            //是否有多选框
            selection:{
                type:Boolean,
                default:false
            },
            selectionArr:{
                type:Array,
                default(){
                    return[]
                }
            },
            //监听方法名称
            busName:{
                type:Object,
                default: ()=>{
                    return{
                        selectionName:'',//多选
                        editSafeName:'',//修改安全策略
                        reviseUserName:'',//修改用户
                        reviseServiceListName:''//修改服务列表
                    }
                }
            }
        },
        data() {
            return {
                loading:false,
                fff:''
            }
        },
        created(){

        },
        mounted(){

        },
        watch:{
            'tableData'(){
                if(this.tableData.length !== 0){
                   this.$nextTick(()=>{
                       //清除table 缓存
                       this.$set(this.$refs.table.store.states, 'lazyTreeNodeMap', {})
                       this.selectionArr.forEach(row=>{
                           this.$refs.table.toggleRowSelection(row,true);
                       })

                   })

                }
            }
        },
        methods:{
            /*懒加载方法*/
            load(tree, treeNode, resolve){
                this.$nextTick(()=>{
                    this.loading = true;
                    let params = {}
                    for(let i in this.loadObj.params){
                        let obj = this.loadObj.params[i]
                        if(obj.value === ''){
                            params[obj.key] = tree[obj.rowKey]
                        }else{
                            params[obj.key] = obj.value
                        }
                    }
                    this.$axios.post(this.$baseUrl+this.loadObj.url,this.$qs.stringify(params))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success == 'true'){
                                resolve(obj.data)
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            expandFunc(row,expanded){
                if(expanded){
                   //this.load('','',this.fff)
                }
            },
            //数据处理
            dataChange(obj,prop){
                let arr = prop.split('.');
                let currentData = obj;
                for (let i in arr){
                    if(currentData[arr[i]]){
                        currentData = currentData[arr[i]];
                    }else{
                       return ''
                    }

                }
                return  currentData;
            },
            /*多选框点击单个*/
            selectOne(val){
                bus.$emit(this.busName.selectionName,val)
            },
            /*多选框点击全部*/
            selectAll(val){
                bus.$emit(this.busName.selectionName,val)
            },
            /*多选框点击*/
            handleSelectionChange(val){
                //bus.$emit(this.busName.selectionName,val)
            },
        },
        components:{
            vListdetails
        }
    }
</script>

<style scoped>
    i{
        margin:0 1px;
    }
    i:hover{
        color: #409eff;
        cursor: pointer;
    }
    .goToEquipment{
        cursor: pointer;
    }
    .goToEquipment:hover{
        
        text-decoration: underline;
    }
    .el-table [class*=el-table__row--level] .el-table__expand-icon{
        color: #fff;
    }
    /deep/ .el-icon-arrow-right{
        color: #fff;
    }
</style>
