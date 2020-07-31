<template>
    <div class="content-bg">
        <div class="top-title">虚拟资产概览
            <div class="equipment-tools">
                <div class="equipemnt-tools-btns">
                    <el-button type="info" size="mini" plain ><a id="eqDownload" @click='downLoadEq'>模板下载</a></el-button>
                    <el-button type="warning" size="mini" plain @click="importState = true">资产导入</el-button>
                    <el-button type="primary" size="mini" plain @click="goToAddEquipment">添加资产</el-button>
                    <el-button type="danger" size="mini" plain  @click="removeEquipment">删除资产</el-button>
                    <el-button type="success" size="mini" plain  @click="getData(searchConditions,1)">刷新</el-button>
<!--                    <el-button type="success" size="mini" plain @click="goToAllEcharts">报表</el-button>-->
                </div>
            </div>
        </div>
        <div class="equipemnt-tools-form">
            <v-search-form :formItem="formConditionsArr" :busName="busName"></v-search-form>
        </div>
        <div class="eq-wapper" v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
            <el-checkbox-group v-model="checkList">
            <ul class="eq-ul">
                <li v-for="(i,index) in equipmentList"  :key="index"  class="eq-li" @mouseenter="liMouseenter($event)" @mouseleave="liMouseleave($event)" @>
                    <div class="eq-tools-01">
                        <i class="el-icon-edit " title="修改资产" @click="reviseEquipment(i)"></i>
                        <span>
                            今日: <b title="今日日志数">{{i.log_count}}</b>
                        </span>
                        <el-checkbox :label="i.id"></el-checkbox>
                    </div>
                    <div class="eq-name">
                        <span class="eq-name-span">{{i.name}}</span>
                        <span class="eq-t-span">{{i.type}}</span>
                    </div>
                    <div class="eq-type"><i class="el-icon-data-line" title="查看资产指标统计" @click="equipmentDashboard(i)"></i></div>
                    <div class="eq-inf">
                        <span class="eq-logtype">{{i.logType}}</span>
                        <span class="eq-ip">{{i.ip}}</span>
                        <span class="eq-more" @mouseenter="spanMouseenter($event)" @mouseleave="spanMouseleave($event)">
                            <i class="el-icon-arrow-right"></i>
                        </span>
                        <ul class="inf-ul">
                            <li class="inf-logtype">
                                <div class="inf-tit">日志类型：</div>
                                <div class="inf-val">{{i.logType}}</div>
                            </li>
                            <li class="inf-ip">
                                <div class="inf-tit">IP地址：</div>
                                <div class="inf-val">{{i.ip}}</div>
                            </li>
                            <li class="inf-hostname">
                                <div class="inf-tit">主机名：</div>
                                <div class="inf-val">{{i.hostName}}</div>
                            </li>
                            <li class="inf-type">
                                <div class="inf-tit">资产类型：</div>
                                <div class="inf-val">{{i.type}}</div>
                            </li>
                            <li class="inf-domain">
                                <div class="inf-tit">根域名：</div>
                                <div class="inf-val">{{i.domain}}</div>
                            </li>
                            <li class="inf-port">
                                <div class="inf-tit">端口：</div>
                                <div class="inf-val">{{i.port}}</div>
                            </li>
                            <li class="inf-iswork">
                                <div class="inf-tit">是否启用：</div>
                                <div class="inf-val">{{i.startUp == '1' ? '是' : '否'}}</div>
                            </li>
                            <li class="inf-starttime">
                                <div class="inf-tit">创建时间：</div>
                                <div class="inf-val">{{i.createTime}}</div>
                            </li>
                            <li class="inf-updatetime">
                                <div class="inf-tit">更新时间：</div>
                                <div class="inf-val">{{i.updateTime}}</div>
                            </li>
                            <li class="inf-endtime">
                                <div class="inf-tit">停用时间：</div>
                                <div class="inf-val">{{i.endTime}}</div>
                            </li>
                        </ul>
                    </div>
                    <div class="eq-tools-02">
                        <i class="el-icon-tickets"  title="查看资产日志"  @click="equipmentLogs(i)"></i>
                        <i class="el-icon-s-data" title="查看资产报表" @click="equipmentEcharts(i)"></i>
                        <i class="el-icon-date" title="查看资产事件" @click="equipmentEvents(i)"></i>
                        <i class="el-icon-bell" title="设置安全策略" @click="setSafe(i)"></i>
                        <i class="el-icon-view" title="潜在威胁分析" :style="{color:i.high_risk !== 0 ? '#f55446' : i.moderate_risk !== 0 ? '#f1ae09' : '#4995bb'}" @click="theartAnalyse(i)"></i>
                    </div>
                </li>
            </ul>
            </el-checkbox-group>
        </div>
        <div class="equipment-table-page">
            <span>共检索到资产数量为 <b>{{allCounts}}</b> 台</span>
            <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="size" :total="allCounts"></el-pagination>
        </div>
        <div class="zz-box" :style="{zIndex:zzIndex}"></div>
        <el-dialog title="资产导入" :visible.sync="importState" width="680px" height="550px" class="dialog-wapper">
            <div class="state" v-if="backState">
                <p class="i-box"><i class="el-icon-success" v-if="this.backStateObj.state == 'true'" style="color:#279e72;"></i><i class="el-icon-error" v-else style="color:#e27145;"></i></p>
                <h3 class="back-h3" v-if="this.backStateObj.state == 'true'"  style="color:#279e72;">导入成功</h3>
                <h3 class="back-h3" v-else style="color:#e27145;">导入失败</h3>
                <p class="back-text">{{this.backStateObj.text}}</p>
                <p class="back-btn"><el-button type="primary" size="mini" @click="backState = false">返回导入界面</el-button></p>
            </div>
            <p>通过浏览本地文件或将文件拖到下面指定区域，上传资产文件。</p>
            <p>文件支持的类型：<span class="txtColor">.xlsx</span> <span class="txtColor">.xls</span></p>
            <p>文件的名称必须为：<span class="txtColor">资产清单</span>  (例:资产清单.xlsx)</p>
            <div style="padding-right: 30px;">
                <input type="file" multiple id="ssi-upload"/>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button @click="importState = false">取 消</el-button>
            </div>
        </el-dialog>
    </div>
    
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import bus from '../common/bus';
    import {jumpHtml} from "../../../static/js/common";
    export default {
		name: "device2",
		data() {
			return {
                backState:false,//导入结果状态框显示与否
                backStateObj:{
                    text:'',
                    state:'false'
                },//结果状态参数集合
                importState:false,//导入框状态
			    loading:false,
                checkList:[],
			    zzIndex:-2,
                busName:'searchEquipment2',
                size:15,
                c_page:1,
                allCounts:0,
                searchConditions:{//查询条件
                    name:'',
                    hostname:'',
                    ip:'',
                    logType:''
                },
                logBaseJson:[],
                logType:[],
                typeArr:[],
                selectEquipmentId:'',//日志页面跳转过来的资产id
                equipmentList:[],
                formConditionsArr:[]
            }
		},
        created(){
            //获取日志类型数据
            this.getLogType();
            // this.getEquipmentType();
            this.formConditionsArr=[
                {
                    label:'资产名称',
                    paramName:'name',
                    model:{
                        model:''
                    },
                    itemType:'',
                    type:'input'
                },
                {
                    label:'IP地址',
                    paramName:'ip',
                    itemType:'',
                    model:{
                        model:''
                    },
                    type:'input'
                },
                {
                    label:'主机名',
                    paramName:'hostName',
                    model:{
                        model:''
                    },
                    itemType:'',
                    type:'input'
                },
                {
                    label:'日志类型',
                    paramName:'logType',
                    type:'select',
                    itemType:'',
                    model:{
                        model:''
                    },
                    options:this.logType
                },
                {
                    label:'资产类型',
                    paramName:'type',
                    type:'cascader',
                    itemType:'',
                    model:{
                        model:[]
                    },
                    options:[]
                }
            ]
            //监听查询条件组件传过来的条件
            bus.$on(this.busName,(params)=>{
                this.searchConditions = params;
                this.getData(this.searchConditions,1)
                this.c_page = 1;
            })
        },
        beforeDestroy(){
            //销毁
            bus.$off(this.busName)
        },
        mounted(){

        },
        methods:{
            /*获得日志类型*/
            getLogType(){
                this.$nextTick(()=>{
                    this.$axios.get('static/filejson/logTypeLevel.json',{})
                        .then((res)=>{
                            this.logBaseJson = res.data;
                            this.logType.push({value:'',label:'全部'});
                            for(let i=0;i<res.data.length; i++){
                                let obj = {
                                    value:res.data[i].type,
                                    label:res.data[i].label
                                };
                                this.logType.push(obj);
                            }
                            // console.log(this.logType)
                        })
                        .catch((err)=>{
                            console.log(err)
                        })
                })
            },
            /*获得资产类型数据*/
            getEquipmentType(data){
                this.$nextTick(()=>{
                    this.$axios.get('static/filejson/equiptype.json',{})
                        .then((res)=>{
                            this.typeArr = res.data
                            this.formConditionsArr[this.formConditionsArr.length - 1].options = this.typeArr;
                            for (let i in data){
                                let type = '';
                                const str = data[i].type.substring(0,2);
                                for (let n in this.typeArr){
                                    let obj = this.typeArr[n];
                                    if(obj.value == str){
                                        type += obj.label +'-';
                                        for(let j in obj.children){
                                            if(obj.children[j].value ==  data[i].type){
                                                type += obj.children[j].label;
                                                data[i].type = type;
                                                break;
                                            }
                                        }
                                        break;
                                    }


                                }

                            }
                            this.equipmentList = data;
                        })
                        .catch((err)=>{
                            console.log(err)
                        })
                })
            },
		    /*获取数据*/
            getData(searchObj,page){
                this.$nextTick(()=>{
                    this.loading = true;
                    let obj = searchObj;
                    obj.pageIndex = page;//当前页
                    obj.pageSize = this.size;//页的条数
                    this.$axios.post(this.$baseUrl+'/equipment/selectPage.do',this.$qs.stringify(obj))
                        .then(res=>{
                            this.loading = false;
                            let data = res.data[0].equipment;
                            this.allCounts = JSON.parse(res.data[0].count.count);
                            //将查询条件保存，用于分页
                            this.saveCondition = this.searchConditions;
                            //判断资产类型是否已经加载
                            this.getEquipmentType(data);

/*                            this.$nextTick(()=>{
                                $(".tools-more").wheelmenu({
                                    trigger: "hover",
                                    animation: "fly",
                                    animationSpeed: "fast",
                                    angle: "all",
                                });
                            })*/

                        })
                        .catch(err=>{
                            this.loading = false;
                            console.log(err)
                        })
                })
            },
            /*根据id查询资产*/
            selectEquipment(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/equipment/selectEquipmentByLog.do',this.$qs.stringify({id:this.selectEquipmentId}))
                        .then(res=>{
                            this.loading = false;
                            if(res.data.success === 'false'){
                                layer.msg(res.data.message,{icon:5});
                            }else {
                                let data = res.data.equipment;
                                this.allCounts = data.length;
                                //判断资产类型是否已经加载
                                this.getEquipmentType(data);
                                this.$nextTick(()=>{
                                    $(".tools-more").wheelmenu({
                                        trigger: "hover",
                                        animation: "fly",
                                        animationSpeed: "fast",
                                        angle: "all",
                                    });
                                })
                            }
                        })
                        .catch(err=>{
                            this.loading = false;
                            console.log(err)
                        })
                })
            },
            /*分页页码改变*/
            handleCurrentChange(page){
                //获取数据
                this.getData(this.saveCondition,page);
                //改变标识
               // this.firstGetData = false;
            },
            /*跳转到添加资产页面*/
            goToAddEquipment(){
                this.$router.push({path:'addEquipment2'});
            },
            /*跳转到所有资产报表*/
            goToAllEcharts(){
                this.$router.push({path:'allEcharts'})
            },
            /*删除资产*/
            removeEquipment(){
                if(this.checkList.length === 0){
                    layer.msg('未选中任何资产',{icon: 5});
                }else{
                    //询问框
                    layer.confirm('您确定删除么？', {
                        btn: ['确定','取消'] //按钮
                    }, (index)=>{
                        layer.close(index);
                        let delectEquipmentIds = ''
                        for(let i in this.checkList){
                            delectEquipmentIds += this.checkList[i]+','
                        }
                        this.$nextTick(()=>{
                            this.$axios.post(this.$baseUrl+'/equipment/delete.do',this.$qs.stringify({id:delectEquipmentIds}))
                                .then((res)=>{
                                    if(res.data.success == "true"){
                                        layer.msg("删除成功",{icon:1});
                                        this.getData(this.searchConditions,1)
                                        this.c_page = 1;
                                    }else{
                                        layer.msg("删除失败",{icon:5});
                                    }

                                })
                                .catch((err)=>{
                                    layer.msg("删除失败",{icon:5});
                                })
                        })
                    }, function(){
                        layer.close();
                    })
                }
            },
            /*修改资产按钮*/
            reviseEquipment(rowData,index){
                //跳转页面
                jumpHtml('reviseEquipment2'+rowData.id,'equipment/reviseEquipment2.vue',{ name:rowData.name,id: rowData.id },'资产修改')
            },
            /*查看资产日志*/
            equipmentLogs(rowData,index){
                //跳转页面
                jumpHtml('equipmentLogs2'+rowData.id,'logsManage/equipmentLogs2.vue',{ name:rowData.name,id: rowData.id ,logType:rowData.logType},'日志')
            },
            /*查看资产图表*/
            equipmentEcharts(rowData,index){
                //判断资产日志类型
                let logType = rowData.logType;
                if(logType === 'syslog'){
                    //跳转页面
                    jumpHtml('syslogEquipmentEcharts'+rowData.id,'equipment/syslogEquipmentEcharts.vue',{ name:rowData.name,id: rowData.id },'统计')
                }else if(logType === 'winlog' || logType === 'winlogbeat'){
                    //跳转页面
                    jumpHtml('winEquipmentEcharts'+rowData.id,'equipment/winEquipmentEcharts.vue',{ name:rowData.name,id: rowData.id },'统计')
                }
            },
            /*资产仪表盘*/
            equipmentDashboard(rowData,index){
                jumpHtml('equipmentDashboard'+rowData.id,'dashboard/dashboard.vue',{ name:rowData.name+'指标数据统计',eid: rowData.id,id:'y_qMB3IBmkPMjFRE7O-_',type:'EQedit' },'修改')
            },
            /*查看资产事件*/
            equipmentEvents(rowData,index){
                //跳转页面
                jumpHtml('equipmentEvents2'+rowData.id,'eventManage/equipmentEvents2.vue',{ name:rowData.name,id: rowData.id },'事件')
            },
            /*设置安全策略*/
            setSafe(rowData,index){
                //跳转页面
                jumpHtml('equipmentSafe'+rowData.id,'equipment/equipmentSafe.vue',{ name:rowData.name,id: rowData.id ,logType:rowData.logType},'安全策略')
            },
            /*潜在威胁分析*/
            theartAnalyse(rowData,index){
                //跳转页面
                jumpHtml('equipmentThreat'+rowData.id,'equipment/equipmentThreat.vue',{ name:rowData.name,id: rowData.id ,logType:rowData.logType},'威胁分析')
            },
            liMouseenter(event){
                let ce = event.currentTarget;
                $(ce).find('.eq-more').css({"padding":"0 8px","borderWidth":"1","width":'auto'});
            },
            liMouseleave(event){
                let ce = event.currentTarget;
                $(ce).find('.eq-more').css({"padding":"0","borderWidth":"0","width":'0'});
            },
            spanMouseenter(event){
                let ce = event.currentTarget;
                var iLeft = $(ce).offset().left;
                var fWidth = $('.eq-wapper').width();
                $(ce).css('zIndex','5')
                this.zzIndex = 2;
                if(fWidth - iLeft < 188){
                    $(ce).next().css({"zIndex":"4","opacity":"1","right":"50px"});
                }else{
                    $(ce).next().css({"zIndex":"4","opacity":"1","right":"-220px"});
                }

            },
            spanMouseleave(event){
                $(ce).css('zIndex','1')
                this.zzIndex = -2;
                let ce = event.currentTarget;
                $(ce).next().css({"zIndex":"-1","opacity":"0","right":"-240px"});
            },
            /*下载资产模板*/
            downLoadEq(){
                console.log('ddd')
                let ahtml = document.getElementById('eqDownload');
                ahtml.href = this.$baseUrl+'/equipment/equipmentDownload.do';
                ahtml.click();
            }
        },
        watch:{
            'importState'(){
                if(this.importState === true){
                    setTimeout(()=>{
                        $('#ssi-upload').ssi_uploader({
                            url:this.$baseUrl+'/equipment/insertEquipmentUpload.do',//地址
                            maxNumberOfFiles:1,
                            allowed:['xlsx','xls'],//允许上传文件的类型
                            ajaxOptions: {
                                success: function(res) {
                                    let obj = JSON.parse(res)
                                    this.backState = true;
                                    this.backStateObj ={
                                        state:obj.success,
                                        text:obj.message
                                    }
                            /*
                                    if(res.success === 'true'){
                                        layer.msg(res.message,{icon:1})
                                    }else if(res.success === 'false'){
                                        layer.msg(res.message,{icon:5})
                                    }*/
                                },
                                error:function(data){
                                    layer.msg("导入失败",{icon: 5});
                                    $(".ssi-abortUpload").click()
                                }
                            }
                        })
                    },100)

                }
            }
        },
        beforeRouteEnter(to, from, next) {
            next (vm => {
                //vm.getEquipmentType();
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //console.log(to.params.equipmentid)
                if(to.params.equipmentid !== undefined){
                    vm.selectEquipmentId = to.params.equipmentid;
                    vm.selectEquipment();
                    vm.c_page = 1;
                }else {
                    vm.getData(vm.searchConditions,1)
                    vm.c_page = 1;
                }
            })
        },
        components:{
            vSearchForm
        }
	}
</script>

<style scoped>
    .equipment-tools{
        height: 30px;
        padding: 0 20px;
        float: right;
        margin-right: 10px;
    }
    .equipemnt-tools-btns button{
        margin: 0;
        background: 0;
    }
    .eq-wapper{
        background: #293846;
        padding: 20px;
        margin: 20px;

    }
    .eq-wapper /deep/ .el-checkbox__label{
        display: none;
    }
    .eq-ul{
        padding-bottom: 10px;
        display: flex;
        flex-wrap: wrap;
        /*justify-content: space-evenly;*/
        justify-content: center;
    }
    .eq-ul:after{
        /*content: '';*/
        /*clear: both;*/
        /*display: block;*/
        /*height: 0;*/
        /*visibility: hidden;*/
    }
    .eq-li{
        width: 238px;
        float: left;
        color: #5bc0de;
        margin: 20px 10px;
        position: relative;
        transition: all .2s linear;
        height: 145px;
        padding: 0 10px;
        background-image: linear-gradient(to right, #2f455c 0%, #386c9a 100%);
        border: 1px solid #365778;
    }
    .eq-li:after{
        content: '';
        position: absolute;
        left: -1px;
        top: 145px;
        width: 0;
        border: 8px solid;
        transition: all .3s;
        border-top-color: #0b4c86;
        border-right-color: #0b4c86;
        border-bottom-color: transparent;
        border-left-color: transparent;
        bottom: -16px;
    }
    .eq-li:hover{
        box-shadow: 5px 5px 20px #4995ba;
    }
    .eq-tools-01{
        position: absolute;
        left: 11px;
        top: 3px;
        font-size: 13px;
        color: #ccc;
        z-index: 3;
        width: 245px;
        display: flex;
        justify-content: space-between;
        height: 20px;
        line-height: 20px;
    }
    .eq-tools-01 span{
        color: #4995ba;
    }
    .eq-tools-01 b{
        color: #409eff;
        border: 1px solid #409eff;
        padding: 0 3px;
    }
    .eq-tools-01 i{
        float: left;
    }
    .eq-tools-02{
        display: flex;
        justify-content: space-evenly;
        cursor: pointer;
        font-size: 15px;
    }
    .eq-tools-02 i{
        color: #4995bb;
    }
    .eq-tools-02 i:hover ,.eq-tools-01 i:hover{
        color: #E4956D;
        cursor: pointer;
    }
    .eq-name{
        color: #E4956D;
        font-size: 18px;
        font-weight: 600;
        height: 76px;
        border-bottom: 1px dashed #5bc0de;
        transition: all 0.2s linear;
        overflow: hidden;
        position: relative;
        /*z-index: 2;*/
        /*cursor: pointer;*/
    }
    .eq-name i{
        font-size: 12px;
        color: #5bc0de;
        cursor: pointer;
    }
    .eq-name i:hover{
        color: #E4956D;
    }
    .eq-name:after{
        content: "";
        position: absolute;
        top: 0;
        left: 0;
        width: 500%;
        height: 1000%;
        background: #5bc0de;
        /*z-index: 0;*/
        opacity: 0.5;
        transform-origin: 0% 0%;
        transform: translateX(calc(20% - 25px)) translateY(10%) rotate(-45deg);
        transition: transform .3s;
    }
    .eq-name-span{
        position: absolute;
        top: 37px;
        overflow: hidden;
        white-space: nowrap;
    }
    .eq-type{
        box-sizing: border-box;
        width: 50px;
        height: 50px;
        text-align: center;
        line-height: 40px;
        border-radius: 100%;
        position: absolute;
        font-weight: 600;
        right: 22px;
        top: -25px;
   /*     font-size: 12px;
        color: #fff;*/
        background: #2f455c;
        border: 5px solid #376995;
        overflow: hidden;
        font-size: 19px;
        color: #359bd6;
        cursor: pointer;
        /*z-index: 3;*/
    }
    .eq-t-span{
        color: #fff;
        font-size: 12px;
        float: right;
        line-height: 133px;
        display: inline-block;
        height: 77px;
        position: absolute;
        right: 3px;
    }
    .eq-inf{
        display: flex;
        font-size: 12px;
        align-items: center;
        justify-content: space-around;
        position: relative;
        height: 44px;
        transition: all 0.2s linear;
    }
    .eq-inf span{
        display: inline-block;
        height: 26px;
        line-height: 26px;
        padding: 0 8px;
        background: #2f455c;
        border: 1px solid #365778;
    }
    .eq-inf .eq-more{
        cursor: pointer;
        position: relative;
        /*z-index: 3;*/
        width: 0;
        overflow: hidden;
        padding: 0;
        border: 0;
        transition: all 0.2s linear;
    }
    .inf-ul{
        width: 188px;
        position: absolute;
        background: #2f455c;
        /* right: -194px; */
        right: -240px;
        padding: 10px;
        opacity: 0;
        transition: all 0.3s linear;
        border: 1px solid #365778;
        z-index: -1;
    }
    .inf-ul li{
        height: 20px;
        line-height: 20px;
        display: flex;
        border-bottom: 1px solid #365778;
        /* padding: 0 10px; */
    }
    .inf-ul li .inf-tit{
        width: 68px;
        text-align: right;
    }
    .inf-ul li .inf-val{
        flex: 1;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
    }
    .eq-tools{
        height: 20px;
        width: 20px;
        /*background: #386c9a;*/
        border-radius: 100%;
        position: absolute;
        text-align: center;
        bottom: -10px;
        left: 118px;
        font-size: 12px;
        line-height: 23px;
        cursor: pointer;
        background: #376995;
        border: 1px solid #376995;
        /*z-index: 3;*/
    }
    .tools-more{
        color: #fff;
    }
    .tools-ul{
        margin: 0;
        padding: 0;
        list-style: none;
        width: 180px;
        height: 180px;
        /* visibility: hidden; */
        position: relative;
        display: none;
        z-index: 6;
    }
    .dialog-wapper p{
        color: #fff;
        line-height: 30px;
    }
    .dialog-wapper p span{
        color: #4da5ff;
    }
    .tools-ul li{
        overflow: hidden;
        height: 40px;
        width: 40px;
        color: #fff;
        border-radius: 100%;
        float: left;
        background: #386c9a;
        text-align: center;
        line-height: 40px;
        cursor: pointer;
    }
    .equipment-table-page{
        margin-bottom:20px;
        padding-right: 20px;
        display: flex;
        justify-content: flex-end;
    }
    .zz-box{
        width: 100vw;
        height: 100vh;
        background: rgba(0,0,0,0.5);
        position: fixed;
        left: 0;
        top: 0;
        z-index: -1;
        transition: all 0.1s linear;
    }
    .state{
        width: 100%;
        height: 82%;
        background: #303e4e;
        position: absolute;
        left: 0;
        top: 55px;
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
</style>
