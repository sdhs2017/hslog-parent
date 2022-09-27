<template>
    <div class="content-bg">
        <!-- tab页内左上角小标题-->
        <div class="top-title">DNS精确查询</div>
        <div class="search-wapper">
            <!-- 查询框 -->
            <el-form :inline="true" onsubmit="return false" class="demo-form-inline conditions-wapper" >
                <!--  时间框，timeArea 变量绑定时间范围，返回数据为数组格式-->
                <div style="width: 384px;">
                    <span class="input-lable">日期范围</span>
                    <el-date-picker
                        v-model="timeArea"
                        type="datetimerange"
                        range-separator="至"
                        size="mini"
                        start-placeholder="开始日期"
                        value-format="yyyy-MM-dd HH:mm:ss"
                        @change="dateAreaChange"
                        :default-time="['00:00:00', '23:59:59']"
                        end-placeholder="结束日期">
                    </el-date-picker>
                </div>
                <div >
                    <!-- 普通输入框，绑定searchCondition对象的fields.ip字段， 对于a.b这种带特殊字符的key  需要用[]方式-->
                    <span class="input-lable">IP地址</span>
                    <span> <el-input size="mini" v-model="searchCondition['fields.ip']" placeholder="请输入内容"></el-input></span>
                </div>
                <div >
                    <!-- 多选下拉框  multiple 绑定变量selectedLogLevel 选择时触发事件logLevelSelectChange -->
                    <span class="input-lable">日志级别</span>
                    <el-select class="multiple-width" multiple collapse-tags v-model="selectedLogLevel" placeholder="请选择"  size="mini" @change="logLevelSelectChange">
                        <!--下拉框中的数据加载是通过v-for 标签遍历logLevel变量（数组） 进行组装-->
                        <el-option
                            v-for="op in logLevel"
                            :key="op.value"
                            :label="op.label"
                            :value="op.value">
                        </el-option>
                    </el-select>
                </div>
                <div>
                    <!-- 查询框，type为elementUI内置样式，绑定点击事件logSearch，参数为当前页吗，默认第一页-->
                    <el-button type="primary"  size="mini" @click="logSearch(1)">查询</el-button>
                </div>
            </el-form>
        </div>
        <div v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
            <el-row :gutter="10">
                <el-col :span="24">
                    <div class="content-right-wapper">
                        <div class="searchlogs-tools">
                            <el-button type="danger" plain size="mini" style="background: 0;margin: 10px 3px;" @click="moreDelete" >删除</el-button>
                            <div class="searchlogs-tools-right" style="float: right;">
                                <!--分页 绑定事件handleCurrentChange，点击分页时触发 绑定当前页c_page，绑定每页条数size，绑定总数total，自动通过total 和size计算，显示分页信息-->
                                <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="size" :total="total"></el-pagination>
                            </div>
                            <div class="searchlogs-tools-center"  style="float: right;">
                                检索到日志 <b class="totalLogs">{{allCounts}}</b> 条，最大展示
                                <el-select v-model="selectVal" placeholder="请选择" size="mini" style="width: 84px;" @change="selectChange" class="maxSelect">
                                    <!--最大展示条数下拉框，通过遍历变量selectOption进行组装-->
                                    <el-option
                                        v-for="item in selectOption"
                                        :key="item.value"
                                        :label="item.label"
                                        :value="item.value">
                                    </el-option>
                                </el-select> 条
                            </div>

                        </div>
                        <!--表格定义，绑定tableData变量，通过对该变量赋值，自动加载显示数据
                        stripe：隔行显示的样式
                        绑定选择框事件handleSelectionChange，点击时触发事件，组装已选择的行信息，为后面删除数据组装参数
                        -->
                        <el-table
                            :data="tableData"
                            stripe
                            @selection-change="handleSelectionChange"
                            style="width: 100%;boder:0;">
                            <!-- 选择框 -->
                            <el-table-column
                                type="selection"
                                width="55">
                            </el-table-column>
                            <el-table-column
                                prop="@timestamp"
                                label="日期"
                                width="250">
                            </el-table-column>
                            <el-table-column
                                prop="log.level"
                                label="日志级别"
                                width="120">
                            </el-table-column>
                            <el-table-column
                                prop="fields.equipmentname"
                                label="资产名称"
                                width="120">
                            </el-table-column>
                            <el-table-column
                                prop="fields.ip"
                                label="资产IP"
                                width="120">
                            </el-table-column>
                            <el-table-column
                                prop="message"
                                label="日志内容"
                                >
                            </el-table-column>
                            <el-table-column
                                label="操作"
                                width="100">
                                <!-- slot-scope="scope" + scope.row 可以实现传递当前行的数据-->
                                <template slot-scope="scope">
                                    <el-button @click="itemCheck(scope.row)" type="text" size="small">查看</el-button>
                                    <el-button @click="itemDelete(scope.row)" type="text" size="small">删除</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>
                </el-col>
            </el-row>
        </div>
        <!--弹窗（查看详情）-->
        <div style="display: none"><!-- 不显示该信息，保证layer.open时，该html不会显示在页面-->
            <div v-if="this.layerObj.formDetailsState"> <!--判断该变量的值，true时显示，false不显示-->
                <div>
                    <div id="con" ref="con"><!--id用来下面通过$("#con") 获取对象-->
                        <!--<div class="qwe">{{this.de tailsData}}</div>-->
                        <ul class="details-list-wapper">
                            <!--遍历变量formDetailColumns数组，组装要显示的内容-->
                            <li v-for="(obj,index) in formDetailColumns" :key="index">
                                <el-row>
                                    <el-col :span="6"><div class="grid-content bg-purple details-list-name">{{obj.label}}:</div></el-col>
                                    <el-col :span="18"><div class="grid-content bg-purple-light details-list-val" v-html="dataChange(layerObj.formDetailsData,obj.prop)"></div></el-col>
                                </el-row>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

    </div>
</template>

<script>
    export default {
        name: "dnsSearchTest",
        data(){
            return{

                searchCondition:{//查询数据时的变量，类型为对象， 以K/V的方式存储，在查询时，JSON.string 方法将该变量转成参数
                    starttime:'',//起始时间
                    endtime:'',//截止时间
                    'fields.ip':'',//IP地址
                    'log.level':''//日志类型
                },
                timeArea:[],//时间范围，数组，与时间控件绑定
                logLevel:[],//下拉框所有元素
                selectedLogLevel:[],//下拉框已选择的元素
                loading:false,//table 加载提示
                moreDeleteArr:[],//要删除的日志id
                total:0,//总条数 （用于分页）
                c_page:1,//当前高亮页码
                size:10,//默认每页的显示的条数
                page:1,//默认第一页
                allCounts:0,//总条数 （用于记录）
                tableData:[],//表格数据
                selectOption:[ //最大展示条数，下拉框数据
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
                selectVal:100000,//最大显示条数默认值
                layerObj:{//table的查看详情表单
                    formDetailsData:{},//弹窗数据
                    formDetailsState:false//弹窗状态
                },
                formDetailColumns:[],//弹窗表单的数据生命
            }
        },
        created(){
            this.setLogLevel('syslog');//日志级别，通过日志类型发送请求获取，后面可以以此实现下拉框的联动
            //查看详情需要显示的字段
            this.formDetailColumns=[{
                label:'时间',
                prop:'@timestamp'
            },{
                label:'日志级别',
                prop:'log.level'
            },{
                label:'资产名称',
                prop:'fields.equipmentname'
            },{
                label:'IP',
                prop:'fields.ip'
            },{
                label:'message',
                prop:'message'
            }]
        },
        methods:{
            /*改变日志级别*/
            setLogLevel(logType){
                //保证数据已加载完
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/log/getLogLevelByLogType.do',this.$qs.stringify({
                        logType:logType
                    }))
                        .then(res=>{
                            let obj = res.data;
                            //请求返回成功
                            if(obj.success == 'true'){
                                this.logLevel = []//清空
                                for (let i=0;i < obj.data.length;i++){
                                    //遍历data元素，依次写入logLevel数组中
                                    this.$set(this.logLevel,i,obj.data[i])
                                }

                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            //出现异常时，清除加载提示框
                            this.loading = false;
                        })
                })
            },
            //查询数据，参数为当前页码
            logSearch(currentPage){
                this.$nextTick(()=> {
                    this.loading = true;//显示加载提示
                    this.searchCondition.page=currentPage;//当前页写入参数中
                    this.searchCondition.size=this.size;//每页条数写入参数中
                    this.$axios.post(this.$baseUrl + '/ecsWinlog/getLogListByBlend.do', this.$qs.stringify({
                        hsData:JSON.stringify(this.searchCondition)//参数
                    }))
                        .then(res=>{
                            this.loading = false;//请求返回，隐藏加载提示
                            //填充表格数据
                            this.tableData = res.data[0].list;
                            //日志总条数
                            this.allCounts = res.data[0].count;
                            //如果查询出的数据大于最大显示条数，将最大显示条数赋值给total，让分页信息按照最大显示条数计算分页数
                            if(this.allCounts >this.selectVal){
                                this.total = this.selectVal;
                            }else{//否则就用查询出的数据计算
                                this.total = this.allCounts;
                            }
                        })
                        .catch(err=>{
                            this.loading = false;
                        })
                })
            },
            //时间控件点击时，触发该事件，将起始和截止时间赋值给参数对象
            dateAreaChange(){
                this.searchCondition.starttime=this.timeArea[0];
                this.searchCondition.endtime=this.timeArea[1];
            },
            //选择日志级别下拉框时触发该事件
            logLevelSelectChange(){
                //先清空
                this.searchCondition['log.level']='';
                //累加组装参数，以逗号分割
                this.selectedLogLevel.forEach((item)=>{
                    this.searchCondition['log.level'] +=item+',';
                })

            },
            /*批量删除按钮*/
            moreDelete(){
                if(this.moreDeleteArr.length === 0){
                    layer.msg('未选中任何日志',{icon: 5});
                }else{
                    this.removeLog(this.moreDeleteArr);
                }
            },
            /*删除日志*/
            removeLog(arr){
                //询问框
                layer.confirm('您确定删除日志信息么？', {
                    btn: ['确定','取消'] //按钮
                }, (index)=>{
                    layer.load(1);
                    this.$nextTick(()=>{
                        this.$axios.post(this.$baseUrl+'/ecsWinlog/deleteById.do', this.$qs.stringify({
                            hsData : JSON.stringify(arr)
                        }))
                            .then((res)=>{
                                layer.closeAll('loading');
                                if(res.data == "删除成功"){
                                    layer.msg('删除成功',{icon: 1});
                                    setTimeout(()=>{
                                        this.logSearch(this.c_page);//等待1秒，重新获取一次数据
                                    },1000)
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

            },
            /*下拉框改变函数*/
            selectChange(selVal){
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
            //分页页码改变
            handleCurrentChange(page){
                //获取数据
                this.logSearch(page);
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
            //table的checkbox点选时触发
            handleSelectionChange(val){
                //清空参数
                this.moreDeleteArr = [];
                //遍历选中项
                for(let i=0;i<val.length;i++){
                    let obj = {}
                    obj.index = val[i].index;
                    obj.type = val[i].type;
                    obj.id = val[i].id;
                    //将选中行 组装后的数据放入变量moreDeleteArr数组中，后面点击删除按钮时，使用该变量作为参数
                    this.moreDeleteArr.push(obj)
                }
            },
            //查看按钮点击触发
            itemCheck(row){
                //设置状态true，使弹窗html生成
                this.layerObj.formDetailsState = true;
                //将每行的数据传给该变量
                this.layerObj.formDetailsData = row;
                //等待页面元素刷新完毕再执行里面的操作
                this.$nextTick(()=>{
                    layer.open({
                        type:'1',
                        title:'日志详情',
                        area:['620px','520px'],
                        content:$('#con').html(),
                        // end 点击弹窗右上角 关闭按钮时触发
                        end:()=>{//箭头函数，保证可以使用this
                            this.layerObj.formDetailsState = false;
                        }
                    })
                })
            },
            //每行数据后面的删除按钮触发事件 row：每行的数据
            itemDelete(row){
                //组装后端传参需要的数据格式
                let array = [];
                let obj = {}
                obj.index = row.index;
                obj.type = row.type;
                obj.id = row.id;
                array.push(obj);
                //删除
                this.removeLog(array);

            },
            //查看按钮后的数据处理
            // formDetailData：每行信息的数据
            //prop：要显示的字段key
            dataChange(formDetailData,prop){
                //eg： fields.ip
                let arr = prop.split('.');
                let currentData = formDetailData;
                //遍历后一层一层的向里获取值
                for (let i in arr){
                    currentData = currentData[arr[i]];
                }
                return  currentData;
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
    .select-width{
        min-width: 59px;
    }
    .multiple-width input{
        width: 127px!important;
    }
    button{
        border-radius: 0!important;
    }
    .top-title{
        font-size: 18px;
        font-weight: 600;
        padding-left: 20px;
        height: 50px;
        line-height: 50px;
        margin-bottom: 20px;
        color: #e4956d;
        text-shadow: 3px 4px 3px #4e7ba9;
        border-bottom: 1px solid #303e4e;
    }
    .equipemnt-tools-btns{
        float: right;
        margin-right: 10px;
    }
    .search-wapper{
        display: flex;
        justify-content: center;
        margin-bottom: 20px;
    }
    .input-searchword{
        width: 300px;
    }
    .content-wapper{
        height: 600px;
        padding: 0 10px 20px 10px;
        /*border:2px solid #303e4e;*/
    }
    .content-wapper>div{
        height: 100%;
    }
    .export-progress-box{
        position:absolute;
        top:35px;
        /*right:5px;*/
        display: flex;
        transition: all .2s;
        font-size: 13px;
    }
    .export-progress-wapper{
        width:50px;
        height:55px;
        border:2px solid #fff;
        border-bottom:0;
        color:#fff;
        background: #00bcd4;
        border-top:0;
        margin:0 30px;
    }
    .progress-name{
        width:46px;
        height:30px;
        text-align:center;
        /* line-height:30px; */
        line-height: 14px;
        word-break: break-all;
    }
    .progress-val{
        width:50px;
        height:50px;
        position:relative;
        left:-2px;
        text-align:center;
        line-height:50px;
        border-radius: 100%;
        border:2px solid #fff;
        font-weight:600;
        font-size:14px;
        background: #00bcd4;

    }
    .dialog-wapper p{
        color: #fff;
        line-height: 30px;
    }
    .dialog-wapper p span{
        color: #4da5ff;
    }
    .back-state{
        width: 100%;
        height: 82%;
        background: #303e4e;
        position: absolute;
        left: 0;
        top: 55px;
        z-index: 100;
    }
    .i-box{
        text-align: center;
        font-size: 50px;
        padding: 35px;
        padding-bottom: 5px;
    }
    .back-h3{
        text-align: center;
        font-size: 38px;
    }
    .back-text{
        height: 275px;
        margin: 0 20px;
        border: 1px solid #5e738c;
        margin-top: 10px;
        padding: 10px;
    }
    .back-btn{
        width: 100%;
        height: 50px;
        text-align: center;
        position: absolute;
        bottom: 0;
    }
    #con{
        display: none;
    }
    .details-list-wapper{
        /*width: 100%;
        height: 100%;
        overflow: auto;*/
        padding: 10px;
        font-size: 14px;
    }
    .details-list-wapper li{
        min-height: 40px;

    }
    .details-list-wapper li:nth-child(even){
        background: #303e4e;
    }
    .details-list-name{
        height: 30px;
        padding-top:10px;
        text-align: center;
    }
    .details-list-val{
        min-height: 20px;
        padding-top: 10px;
        padding-bottom: 10px;
        line-height: 20px;
        word-break: break-all;
    }
    .layui-layer-page .layui-layer-content{
        background: rgb(26,36,47)!important;
        overflow: auto!important;
        border: 1px solid #303e4e!important;
    }
/*   .content-right-wapper /deep/ .el-table .el-table__row:hover {
        background: #5a7494;
    }
    .content-right-wapper /deep/ .el-table--enable-row-hover .el-table__body tr:hover>td{
        background: #5a7494;
    }
    .content-right-wapper /deep/  .el-table--striped .el-table__body tr.el-table__row--striped:hover>td{
        background: #5a7494;
    }
    .content-right-wapper /deep/ .el-table .el-table__row:hover {
        background: #5a7494;
    }*/
    /*.el-table--enable-row-hover .el-table__body tr:hover>td{
        background-color: #212e3e !important;
    }*/
    .content-right-wapper /deep/ .el-table__body tr.current-row>td {
        background-color: #5a7494!important;
    }
    .content-right-wapper /deep/ .el-table__body tr.hover-row.current-row>td, .el-table__body tr.hover-row.el-table__row--striped.current-row>td, .el-table__body tr.hover-row.el-table__row--striped>td, .el-table__body tr.hover-row>td {
        background-color: #5a7494!important;
    }
</style>
