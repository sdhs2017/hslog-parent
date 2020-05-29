<template>
    <div class="content-bg">
        <div class="top-title">'{{equipmentName}}'事件</div>
        <div class="event-search-condition">
            <v-search-form :formItem="formConditionsArr" :busName="busName"></v-search-form>
        </div>
        <div class="equipment-table" v-loading="loading1"  element-loading-background="rgba(48, 62, 78, 0.5)">
            <v-basetable2 :tableHead="equipmentHead" :tableData="equipmentData"></v-basetable2>
        </div>
        <div class="event-search-content"  v-loading="loading2"  element-loading-background="rgba(48, 62, 78, 0.5)">
            <v-basetable2 :tableHead="tableHead" :tableData="tableData"></v-basetable2>
            <div class="event-table-page">
                共检索到事件数为 <b>{{allCounts}}</b>，最大展示
                <el-select v-model="selectVal" placeholder="请选择" size="mini" style="width: 84px;" @change="seletChange" class="maxSelect">
                    <el-option
                        v-for="(item,index) in selectOption"
                        :key="index"
                        :label="item.label"
                        :value="item.value">
                    </el-option>
                </el-select> 条
                <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="size" :total="total"></el-pagination>
            </div>
        </div>
        <div v-if="this.layerObj.layerState">
            <vListdetails2 :baseConfig="this.baseConfig" :detailsData="this.layerObj.detailData"></vListdetails2>
        </div>
    </div>
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import vBasetable from '../common/Basetable';
    import vBasetable2 from '../common/Basetable2';
    import vListdetails2 from '../common/Listdetails2';
    import bus from '../common/bus';

    export default {
        name: 'equipmentEvents2',
        data(){
            return{
                loading1:false,
                loading2:false,
                baseConfig:{ //弹窗基础配置
                    type:'2',
                    title:'事件详情',
                    areaWidth:'620px',//宽度
                    areaHeight:'520px'//高度
                },
                layerObj:{
                    detailData:{},//弹窗数据
                    layerState:false//弹窗状态
                },
                equipmentId:'',
                equipmentName:'',
                busName:'',
                typeArr:[],
                page:1,
                size:15,
                c_page:1,
                allCounts:0,
                total:0,
                formConditionsArr:[
                    {
                        label:'事件范围',
                        type:'datetimerange',
                        itemType:'',
                        paramName:'time',
                        model:{
                            model:[]
                        },
                        val:''
                    },
                    {
                        label:'事件名称',
                        paramName:'event.action',
                        itemType:'',
                        model:{
                            model:''
                        },
                        type:'input'
                    }
                ],
                tableHead:[
                    {
                        prop:'@timestamp',
                        label:'时间',
                        width:''
                    },
                    {
                        prop:'event.action',
                        label:'事件名称',
                        width:''
                    },
                    {
                        prop:'winlog',
                        label:'事件原因',
                        width:''
                    },
                    {
                        prop:'message',
                        label:'日志内容',
                        width:''
                    },
                    {
                        prop:'tools',
                        label:'操作',
                        width:'60',
                        btns:[
                            {
                                icon:'el-icon-tickets',
                                text:'查看详情',
                                btnType: 'readDetails',
                                clickFun:(row,index)=>{
                                    // //console.log(this.layerObj)
                                    let obj = row;
                                    obj.type = 'eventDetail';
                                    this.layerObj.layerState = true;
                                    this.layerObj.detailData = obj;
                                }
                            }
                        ]
                    }

                ],
                tableData:[],
                equipmentHead:[
                    {
                        prop:'name',
                        label:'资产名称',
                        width:''
                    },
                    {
                        prop:'hostName',
                        label:'主机名',
                        width:''
                    },
                    {
                        prop:'type',
                        label:'资产类型',
                        width:''

                    },
                    {
                        prop:'logType',
                        label:'日志类型',
                        width:''
                    },
                    {
                        prop:'ip',
                        label:'IP地址',
                        width:'125'
                    },
                    {
                        prop:'createTime',
                        label:'进入时间',
                        width:''
                    },
                    {
                        prop:'upDateTime',
                        label:'更新时间',
                        width:''
                    },
                    {
                        prop:'endTime',
                        label:'截止时间',
                        width:''
                    },
                    {
                        prop:'startUp',
                        label:'是否启用',
                        width:''
                    }
                ],//资产表格头
                equipmentData:[],
                selectVal:'10万',
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
                eventSearchCondition:{
                    starttime:'',
                    endtime:'',
                    'event.action':''
                },
                saveCondition:{},
            }
        },
        created(){
            //获得资产类型
            this.getEquipmentType();
        },
        methods:{
            /*获取资产信息*/
            getEquipmentData(){
                this.loading1 = true;
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/equipment/selectEquipment.do',this.$qs.stringify({id:this.equipmentId}))
                        .then((res)=>{
                            this.loading1 = false;
                            //时间类型处理
                            if( res.data[0].createTime !== null){
                                res.data[0].createTime = res.data[0].createTime.split(".")[0];
                            }
                            if( res.data[0].upDateTime !== null){
                                res.data[0].upDateTime = res.data[0].upDateTime.split(".")[0];
                            }
                            if( res.data[0].endTime !== null){
                                res.data[0].endTime = res.data[0].endTime.split(".")[0];
                            }
                            //是否启用
                            if(res.data[0].startUp == '1'){
                                res.data[0].startUp = '是'
                            }else{
                                res.data[0].startUp = '否'
                            }
                            //资产类型
                            //分割出前两位
                            let type = '';
                            const str =  res.data[0].type.substring(0,2);
                            for (let n in this.typeArr){
                                let obj = this.typeArr[n];
                                if(obj.value == str){
                                    type += obj.label +'-';

                                    for(let j in obj.children){
                                        if(obj.children[j].value == res.data[0].type){
                                            type += obj.children[j].label;
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            res.data[0].type = type;
                            this.logType =  res.data[0].logType;
                            this.equipmentData = res.data;
                        })
                        .catch((err)=>{
                            this.loading1 = false;
                        })
                })
            },
            /*获得资产类型数据*/
            getEquipmentType(){
                this.$nextTick(()=>{
                    this.$axios.get('static/filejson/equiptype.json',{})
                        .then((res)=>{
                            this.typeArr = res.data
                        })
                        .catch((err)=>{
                            console.log(err)
                        })
                })
            },
            /*获得事件列表数据*/
            getEventsData(page,params){
                this.loading2 = true;
                params.size = this.size;
                params.page = page;
                params.exists = 'event.action';
                params['fields.equipmentid'] = this.equipmentId;
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+'/ecsCommon/getEventListByBlend.do',this.$qs.stringify({
                        hsData:JSON.stringify(params)
                    }))
                        .then((res)=>{
                            this.loading2 = false;
                            this.tableData = res.data[0].list;
                            this.allCounts =  res.data[0].count;
                            if(this.allCounts >100000){
                                this.total = 100000;
                            }else{
                                this.total = this.allCounts;
                            }
                        })
                        .catch((err)=>{
                            this.loading2 = false;
                        })
                })
            },
            /*页码改变*/
            handleCurrentChange(page){
                this.getEventsData(page,this.saveCondition);
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
            /*最大展示条数改变*/
            seletChange(selVal){
                //判断所选的展示条数
                if(selVal < this.allCounts){
                    this.total = selVal;
                }else{
                    this.total = this.allCounts;
                }
                //所选值过大时，进行提示
                const html = '数据量过大时，细化查询条件，提高查询效率'
                if(selVal >= 10000000){
                    layer.tips('提示: '+html, '.maxSelect', {
                        tips: [1, '#3595CC'],
                        time: 4000
                    });
                }
            }
        },
        watch:{
            'equipmentId'(){
                //保存检索条件
                this.saveCondition = this.eventSearchCondition;
                //获取事件数据
                this.getEventsData(1,this.eventSearchCondition);
                //监听检索条件
                bus.$on(this.busName,(params)=>{
                    this.getEventsData(1,params);
                    this.c_page = 1;
                    this.saveCondition = params;
                })
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
        beforeRouteEnter(to, from, next) {
            next (vm => {
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                vm.$options.name = 'equipmentEvents2'+ to.query.id;
                //修改data参数
                vm.equipmentName = to.query.name;
                vm.busName = 'equipmentEvents2'+to.query.id;
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'equipmentEvents2'+to.query.id,
                    component:'eventManage/equipmentEvents2.vue',
                    title:'事件'
                }
                sessionStorage.setItem('/equipmentEvents2'+to.query.id,JSON.stringify(obj))
                if(vm.equipmentId === '' || vm.equipmentId !== to.query.id){
                    vm.equipmentId = to.query.id;
                    vm.eventSearchCondition['fields.equipmentid'] = vm.equipmentId;
                    vm.getEquipmentData();
                }

            })

        },
        components:{
            vSearchForm,
            vBasetable,
            vListdetails2,
            vBasetable2
        }
    }
</script>

<style scoped>
    .event-search-condition{
        display: flex;
        justify-content: center;
        margin-bottom: 30px;
        position: absolute;
        top: 32px;
        right: 30px;
        z-index: 0;
    }
    .equipment-table{
        padding: 0 10px;
        margin-bottom: 10px;
    }
    .event-search-content{
        padding: 0 10px;
        overflow: auto;
    }
    .event-table-page{
        display: flex;
        justify-content: flex-end;
        align-items: center;
        margin: 10px 0;
    }
    .event-table-page b{
        color: #1ab394;
        margin:0 10px;
    }
</style>
