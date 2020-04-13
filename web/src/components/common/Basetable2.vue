<template>
    <div>
        <el-table :data="tableData" stripe fit style="width: 100%;border: 0;" border  @selection-change="handleSelectionChange" :height="height">
            <el-table-column
                v-if="selection"
                type="selection"
                width="40">
            </el-table-column>
            <template v-for="(item,index) in tableHead">
                <el-table-column v-if="item.prop !== 'tools' && item.prop !== 'threats'"  show-overflow-tooltip  :prop="item.prop"  :label="item.label" :width="item.width" :key="index">
                    <template slot-scope="scope">
                        <span v-if="!item.formatData && !item.clickFun " v-html="dataChange(scope.row,item.prop)"></span>
                        <el-button v-else-if="!item.formatData && item.clickFun " v-html="dataChange(scope.row,item.prop)" @click.native.prevent="item.clickFun(scope.row,scope.index)" type="text" size="mini" style="margin-left: 0;padding: 0px;color:#409EFF;"></el-button>
                        <span v-else-if="item.formatData && !item.clickFun " v-html="tableFormatter(dataChange(scope.row,item.prop),item.formatData,scope.row)" ></span>
                        <el-button v-else v-html="tableFormatter(dataChange(scope.row,item.prop),item.formatData,scope.row)" @click.native.prevent="item.clickFun(scope.row,scope.index)" type="text" size="mini" style="margin-left: 0;padding: 0px;color:#409EFF;"></el-button>
                    </template>
<!--                    <template slot-scope="scope">-->
<!--                        <span >{{dataChange(scope.row,item.prop)}}</span>-->
<!--                    </template>-->
                </el-table-column>
                <el-table-column v-else-if ="item.prop === 'tools'" show-overflow-tooltip   :prop="item.prop"  :label="item.label" :width="item.width" :key="index">
                    <template slot-scope="scope">
                        <el-button v-for="(ic, ind) in item.btns" :key="ind"  @click.native.prevent="ic.clickFun(scope.row,scope.$index)" type="text" size="mini" style="margin-left: 0;padding: 0px;color:#D6DFEB;">
                            <i :class="ic.icon" :title="ic.text"></i>
                        </el-button>
                    </template>
                </el-table-column>
                <el-table-column v-else-if="item.prop === 'threats'"  show-overflow-tooltip  :prop="item.prop"  :label="item.label" :width="item.width" :key="index">
                    <template slot-scope="scope">
                        <span v-if="!item.formatData && !item.clickFun " v-html="scope.row[item.prop]"></span>
                        <el-button v-else-if="!item.formatData && item.clickFun " v-html="scope.row[item.prop]" @click.native.prevent="item.clickFun(scope.row,scope.index)" type="text" size="mini" style="margin-left: 0;padding: 0px;color:#409EFF;"></el-button>
                        <span v-else-if="item.formatData && !item.clickFun " v-html="tableFormatter(scope.row[item.prop],item.formatData,scope.row)" ></span>
                        <el-button v-else v-html="tableFormatter(scope.row[item.prop],item.formatData,scope.row)" @click.native.prevent="item.clickFun(scope.row,scope.index)" type="text" size="mini" style="margin-left: 0;padding: 0px;color:#409EFF;"></el-button>
                    </template>
                </el-table-column>
            </template>

        </el-table>

    </div>
</template>


<script>
    import vListdetails from '../common/Listdetails';
    import bus from '../common/bus';
    export default {
        name: "basetable",
        props:{
            //高度
            height:'',
            //表头
            tableHead:{
                type: Array
            },
            //数据
            tableData:{
                type:Array
            },
            //是否有多选框
            selection:{
                type:Boolean,
                default:false
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

            }
        },
        created(){

        },
        methods:{
            //数据处理
            dataChange(obj,prop){
                let arr = prop.split('.');
                let currentData = obj;
                for (let i in arr){
                    currentData = currentData[arr[i]];
                }
                return  currentData;
            },
            /*多选框点击*/
            handleSelectionChange(val){
                bus.$emit(this.busName.selectionName,val)
            },
            btnClick($event,rowData){
                const btnType = $event.currentTarget.getAttribute('btnType');
                switch (btnType) {
                    //查看详情
                    case 'readDetails':
                        this.readDetails(rowData)
                        break;
                    //删除日志
                    case  'removeItem':
                        this.removeLog(rowData)
                        break;
                    //修改资产
                    case  'reviseEquipment':
                        this.reviseEquipment(rowData)
                        break;
                    //查看资产日志
                    case  'equipmentLogs':
                        this.readEquipmentLogs(rowData)
                        break;
                    //查看资产图表统计
                    case  'equipmentEcharts':
                        this.readEquipmentEcharts(rowData)
                        break;
                    //查看资产事件列表
                    case  'equipmentEvents':
                        this.readEquipmentEvents(rowData)
                        break;
                    //设置资产安全策略
                    case  'setSafe':
                        this.setEquipmentSafe(rowData)
                        break;
                    //修改安全策略
                    case  'editEquipmentSafe':
                        this.editEquipmentSafe(rowData)
                        break;
                    //潜在威胁分析
                    case  'theartAnalyse':
                        this.equipmentThreat(rowData)
                        break;
                    //修改用户
                    case  'reviseUser':
                        this.reviseUser(rowData)
                        break;
                    //修改用户reviseService
                    case  'reviseService':
                        this.reviseService(rowData)
                        break;
                }
            },
            //删除单个日志
            removeLog(rowData){
                //询问框
                layer.confirm('您确定删除日志信息么？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.load(1);
                    this.$nextTick(()=>{
                        this.$axios.post('/jz/log/deleteById.do', this.$qs.stringify({
                            index:'estest',
                            type:rowData.type,
                            id:rowData.id
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
</style>
