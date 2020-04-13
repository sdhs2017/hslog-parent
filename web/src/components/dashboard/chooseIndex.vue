<template>
    <div class="choose-wapper">
        当前数据源:
<!--        <el-select v-model="index" filterable  placeholder="请选择" @change="indexBlur"  size="mini" class="index-sel">-->
        <el-cascader
            ref="refHandle"
            class="index-sel"
            v-model="index"
            :options="dataArr"
            :props="{checkStrictly: true, label:'menuName' ,value:'id',children:'menus'}"
            :show-all-levels="false"
            filterable
            popper-class="cascaderPopper"
            @change="changeCascader"
        >
        </el-cascader>
<!--        </el-select>-->
<!--        <el-button  type="primary" plain @click="tableState = true" >详情</el-button>-->
      <!--  <el-dialog title="属性详情" :visible.sync="tableState" width="75vw">
            <el-table
                class="table-box"
                :data="this.tableDataArr"
                :border="true"
                :height="tableHeight"
                stripe
                style="width: 100%">
                <el-table-column
                    prop="fieldName"
                    label="字段名"
                    width="">
                </el-table-column>
                <el-table-column
                    prop="fieldType"
                    label="数据类型"
                    :formatter="toLowcase1"
                    width="">
                </el-table-column>
                <el-table-column
                    prop="fieldData"
                    :formatter="changeVal"
                    label="是否可聚合">
                </el-table-column>
                <el-table-column
                    prop="format"
                    label="格式">
                </el-table-column>
                <el-table-column
                    prop="analyzer"
                    :formatter="toLowcase1"
                    label="存储分词器">
                </el-table-column>
                <el-table-column
                    prop="searchAnalyzer"
                    :formatter="toLowcase1"
                    label="查询分词器">
                </el-table-column>
                <el-table-column
                    prop="rawType"
                    :formatter="toLowcase1"
                    label="数据类型">
                </el-table-column>
                <el-table-column
                    prop="rawIgnoreAbove"
                    label="限制长度">
                </el-table-column>
            </el-table>
        </el-dialog>-->

    </div>
    
</template>

<script>
    import bus from '../common/bus';
    export default {
        name: "chooseIndex",
        props:{
            indexVal:{
                type:String
            },
            busName:{
                type:String
            }
        },
        data() {
            return {
                tableState:false,
                tableHeight:'',
                //tableDataArr:[{"fieldName":"client_hostname","rawType":null,"fieldData":true,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":"INDEX_ANSJ","searchAnalyzer":"QUERY_ANSJ","sourceName":"","fieldType":"TEXT"},{"fieldName":"client_ip","rawType":"KEYWORD","fieldData":false,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"IP"},{"fieldName":"client_mac","rawType":null,"fieldData":true,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":"INDEX_ANSJ","searchAnalyzer":"QUERY_ANSJ","sourceName":"","fieldType":"TEXT"},{"fieldName":"deptid","rawType":null,"fieldData":false,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"KEYWORD"},{"fieldName":"dhcp_type","rawType":null,"fieldData":true,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":"INDEX_ANSJ","searchAnalyzer":"QUERY_ANSJ","sourceName":"","fieldType":"TEXT"},{"fieldName":"dns_ana_type","rawType":null,"fieldData":true,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":"INDEX_ANSJ","searchAnalyzer":"QUERY_ANSJ","sourceName":"","fieldType":"TEXT"},{"fieldName":"dns_clientip","rawType":"KEYWORD","fieldData":false,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"IP"},{"fieldName":"dns_domain_name","rawType":null,"fieldData":true,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":"INDEX_ANSJ","searchAnalyzer":"QUERY_ANSJ","sourceName":"","fieldType":"TEXT"},{"fieldName":"dns_server","rawType":null,"fieldData":true,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":"INDEX_ANSJ","searchAnalyzer":"QUERY_ANSJ","sourceName":"","fieldType":"TEXT"},{"fieldName":"dns_view","rawType":null,"fieldData":true,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":"INDEX_ANSJ","searchAnalyzer":"QUERY_ANSJ","sourceName":"","fieldType":"TEXT"},{"fieldName":"equipmentid","rawType":null,"fieldData":true,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"TEXT"},{"fieldName":"equipmentname","rawType":"KEYWORD","fieldData":true,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":"INDEX_ANSJ","searchAnalyzer":"QUERY_ANSJ","sourceName":"","fieldType":"TEXT"},{"fieldName":"error_log","rawType":null,"fieldData":true,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":"INDEX_ANSJ","searchAnalyzer":"QUERY_ANSJ","sourceName":"","fieldType":"TEXT"},{"fieldName":"event_des","rawType":null,"fieldData":false,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":"INDEX_ANSJ","searchAnalyzer":"QUERY_ANSJ","sourceName":"","fieldType":"TEXT"},{"fieldName":"event_level","rawType":null,"fieldData":false,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"INTEGER"},{"fieldName":"event_type","rawType":null,"fieldData":true,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"TEXT"},{"fieldName":"eventid","rawType":null,"fieldData":true,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"TEXT"},{"fieldName":"hostname","rawType":null,"fieldData":true,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":"INDEX_ANSJ","searchAnalyzer":"QUERY_ANSJ","sourceName":"","fieldType":"TEXT"},{"fieldName":"hslog_type","rawType":null,"fieldData":true,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"TEXT"},{"fieldName":"id","rawType":null,"fieldData":false,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"KEYWORD"},{"fieldName":"index_suffix","rawType":"KEYWORD","fieldData":false,"format":"","fieldAlisName":"","rawIgnoreAbove":256,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"TEXT"},{"fieldName":"ip","rawType":"KEYWORD","fieldData":false,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"IP"},{"fieldName":"is_business","rawType":null,"fieldData":false,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"BOOLEAN"},{"fieldName":"logdate","rawType":null,"fieldData":false,"format":"yyyy-MM-dd HH:mm:ss","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"DATE"},{"fieldName":"logtime","rawType":null,"fieldData":true,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"TEXT"},{"fieldName":"network_error","rawType":null,"fieldData":true,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":"INDEX_ANSJ","searchAnalyzer":"QUERY_ANSJ","sourceName":"","fieldType":"TEXT"},{"fieldName":"operation_des","rawType":null,"fieldData":false,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":"INDEX_ANSJ","searchAnalyzer":"QUERY_ANSJ","sourceName":"","fieldType":"TEXT"},{"fieldName":"operation_facility","rawType":null,"fieldData":true,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"TEXT"},{"fieldName":"operation_level","rawType":null,"fieldData":true,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"TEXT"},{"fieldName":"pid","rawType":null,"fieldData":false,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"TEXT"},{"fieldName":"PID","rawType":"KEYWORD","fieldData":false,"format":"","fieldAlisName":"","rawIgnoreAbove":256,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"TEXT"},{"fieldName":"pri","rawType":null,"fieldData":false,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"TEXT"},{"fieldName":"process","rawType":null,"fieldData":true,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":"INDEX_ANSJ","searchAnalyzer":"QUERY_ANSJ","sourceName":"","fieldType":"TEXT"},{"fieldName":"process_id","rawType":null,"fieldData":false,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"KEYWORD"},{"fieldName":"process_name","rawType":null,"fieldData":false,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"TEXT"},{"fieldName":"relay_ip","rawType":"KEYWORD","fieldData":false,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"IP"},{"fieldName":"user_domain","rawType":null,"fieldData":false,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"TEXT"},{"fieldName":"user_name","rawType":null,"fieldData":false,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"TEXT"},{"fieldName":"userid","rawType":null,"fieldData":false,"format":"","fieldAlisName":"","rawIgnoreAbove":0,"fieldTypeName":"","sourceType":null,"analyzer":null,"searchAnalyzer":null,"sourceName":"","fieldType":"KEYWORD"}],
                index:'',
                dataArr:[{"childId":0,"id":"hslog_syslog1","menuName":"hslog_syslog1","menus":[{"childId":0,"id":"hslog_syslog2019-12-31","menuName":"hslog_syslog2019-12-31","state":"2"},{"childId":0,"id":"hslog_syslog2020-01-02","menuName":"hslog_syslog2020-01-02","state":"2"},{"childId":0,"id":"hslog_syslog2020-01-03","menuName":"hslog_syslog2020-01-03","state":"2"},{"childId":0,"id":"hslog_syslog2020-01-04","menuName":"hslog_syslog2020-01-04","state":"2"},{"childId":0,"id":"hslog_syslog2020-01-05","menuName":"hslog_syslog2020-01-05","state":"2"},{"childId":0,"id":"hslog_syslog2020-01-06","menuName":"hslog_syslog2020-01-06","state":"2"},{"childId":0,"id":"hslog_syslog2020-01-07","menuName":"hslog_syslog2020-01-07","state":"2"},{"childId":0,"id":"hslog_syslog2020-01-08","menuName":"hslog_syslog2020-01-08","state":"2"},{"childId":0,"id":"hslog_syslog2020-01-09","menuName":"hslog_syslog2020-01-09","state":"2"},{"childId":0,"id":"hslog_syslog2020-01-10","menuName":"hslog_syslog2020-01-10","state":"2"},{"childId":0,"id":"hslog_syslog2020-01-11","menuName":"hslog_syslog2020-01-11","state":"2"},{"childId":0,"id":"hslog_syslog2020-01-12","menuName":"hslog_syslog2020-01-12","state":"2"},{"childId":0,"id":"hslog_syslog2020-01-13","menuName":"hslog_syslog2020-01-13","state":"2"},{"childId":0,"id":"hslog_syslog2020-01-14","menuName":"hslog_syslog2020-01-14","state":"2"},{"childId":0,"id":"hslog_syslog2020-01-15","menuName":"hslog_syslog2020-01-15","state":"2"}],"state":"1"},{"childId":0,"id":"hslog_packet","menuName":"hslog_packet","menus":[{"childId":0,"id":"hslog_packet2019-12-27","menuName":"hslog_packet2019-12-27","state":"2"},{"childId":0,"id":"hslog_packet2019-12-28","menuName":"hslog_packet2019-12-28","state":"2"},{"childId":0,"id":"hslog_packet2019-12-29","menuName":"hslog_packet2019-12-29","state":"2"},{"childId":0,"id":"hslog_packet2019-12-30","menuName":"hslog_packet2019-12-30","state":"2"},{"childId":0,"id":"hslog_packet2019-12-31","menuName":"hslog_packet2019-12-31","state":"2"},{"childId":0,"id":"hslog_packet2020-01-01","menuName":"hslog_packet2020-01-01","state":"2"},{"childId":0,"id":"hslog_packet2020-01-02","menuName":"hslog_packet2020-01-02","state":"2"},{"childId":0,"id":"hslog_packet2020-01-03","menuName":"hslog_packet2020-01-03","state":"2"},{"childId":0,"id":"hslog_packet2020-01-04","menuName":"hslog_packet2020-01-04","state":"2"},{"childId":0,"id":"hslog_packet2020-01-05","menuName":"hslog_packet2020-01-05","state":"2"},{"childId":0,"id":"hslog_packet2020-01-06","menuName":"hslog_packet2020-01-06","state":"2"},{"childId":0,"id":"hslog_packet2020-01-07","menuName":"hslog_packet2020-01-07","state":"2"},{"childId":0,"id":"hslog_packet2020-01-08","menuName":"hslog_packet2020-01-08","state":"2"},{"childId":0,"id":"hslog_packet2020-01-09","menuName":"hslog_packet2020-01-09","state":"2"},{"childId":0,"id":"hslog_packet2020-01-10","menuName":"hslog_packet2020-01-10","state":"2"},{"childId":0,"id":"hslog_packet2020-01-11","menuName":"hslog_packet2020-01-11","state":"2"},{"childId":0,"id":"hslog_packet2020-01-12","menuName":"hslog_packet2020-01-12","state":"2"},{"childId":0,"id":"hslog_packet2020-01-13","menuName":"hslog_packet2020-01-13","state":"2"},{"childId":0,"id":"hslog_packet2020-01-14","menuName":"hslog_packet2020-01-14","state":"2"},{"childId":0,"id":"hslog_packet2020-01-15","menuName":"hslog_packet2020-01-15","state":"2"},{"childId":0,"id":"hslog_packet2020-01-16","menuName":"hslog_packet2020-01-16","state":"2"},{"childId":0,"id":"hslog_packet2020-01-17","menuName":"hslog_packet2020-01-17","state":"2"},{"childId":0,"id":"hslog_packet2020-01-18","menuName":"hslog_packet2020-01-18","state":"2"},{"childId":0,"id":"hslog_packet2020-01-19","menuName":"hslog_packet2020-01-19","state":"2"},{"childId":0,"id":"hslog_packet2020-03-03","menuName":"hslog_packet2020-03-03","state":"2"},{"childId":0,"id":"hslog_packet2020-03-04","menuName":"hslog_packet2020-03-04","state":"2"},{"childId":0,"id":"hslog_packet2020-03-05","menuName":"hslog_packet2020-03-05","state":"2"},{"childId":0,"id":"hslog_packet2020-03-06","menuName":"hslog_packet2020-03-06","state":"2"},{"childId":0,"id":"hslog_packet2020-03-07","menuName":"hslog_packet2020-03-07","state":"2"},{"childId":0,"id":"hslog_packet2020-03-08","menuName":"hslog_packet2020-03-08","state":"2"},{"childId":0,"id":"hslog_packet2020-03-09","menuName":"hslog_packet2020-03-09","state":"2"},{"childId":0,"id":"hslog_packet2020-03-10","menuName":"hslog_packet2020-03-10","state":"2"},{"childId":0,"id":"hslog_packet2020-03-11","menuName":"hslog_packet2020-03-11","state":"2"},{"childId":0,"id":"hslog_packet2020-03-12","menuName":"hslog_packet2020-03-12","state":"2"}],"state":"1"}],
                indexArr:[
                    {
                        value: 'hslog_packet',
                        label: 'hslog_packet'
                    },
                    {
                        value: 'hslog',
                        label: 'hslog'
                    }
                ],
                indexPropsArr:[
                    {
                        name:'源端口',
                        prop:'l4_src_port'
                    },
                    {
                        name:'根域名',
                        prop:'domain_url'
                    },
                    {
                        name:'目的端口',
                        prop:'l4_dst_port'
                    },
                    {
                        name:'日志级别',
                        prop:'operation_level'
                    }
                ]
            }
        },
        created(){
            this.tableHeight = document.body.clientHeight - 320 ;
            window.onresize = () => {
                this.tableHeight = document.body.clientHeight - 320 ;
            };
            //获取索引数据
            this.getIndex();

        },
        watch:{
            'indexVal'(){
                console.log(this.indexVal)
                this.index = this.indexVal
            }
        },
        methods:{
            //获取index
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
            //获取index对应的属性
            getIndexProps(indexVal){
                // this.$nextTick(()=>{
                //     layer.load(1);
                //     this.$axios.post(this.$baseUrl+'',this.$qs.stringify())
                //         .then(res=>{
                //             layer.closeAll('loading');
                //         })
                //         .catch(err=>{
                //             layer.closeAll('loading');
                //
                //         })
                // })
            },
            //层级下拉框值改变事件
            changeCascader(val){
                let obj = this.$refs.refHandle.getCheckedNodes();
                //绑定事件
                bus.$emit(this.busName,this.index);
                //隐藏下拉框
                this.$refs.refHandle.dropDownVisible = false; //监听值发生变化就关闭它
                //获取属性数据
              //  this.getIndexProps(val)

            },
            /*转小写*/
            toLowcase1(row, column, cellValue, index){
                if(cellValue){
                    let val = cellValue.toLowerCase();
                    return val
                }else {
                    return cellValue
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
                // if (node.state  == 1){
                //     let url = '/metadata/getMedataByTemplate.do';
                //     let param = {
                //         template:node.id
                //     }
                //     this.getProps(url,param)
                //
                // } else if (node.state == 2) {
                //     let url = '/metadata/getMedataByIndex.do';
                //     let param = {
                //         index:node.id
                //     }
                //     this.getProps(url,param)
                // }
            }
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
    .choose-wapper /deep/ .el-table thead.is-group th {
        background: #485c73;
    }
    .choose-wapper /deep/ .el-table--border, .el-table--group{
        border:0
    }
    .choose-wapper /deep/ .el-table--border th, .el-table__fixed-right-patch {
        border-bottom: 1px solid #5a728e;
    }
    .choose-wapper /deep/ .el-table--border th{
        border-right: 1px solid #5a728e;
    }
</style>
