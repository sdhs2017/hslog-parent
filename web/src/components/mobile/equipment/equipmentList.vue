<template>
    <div class="content-bg">
        <div class="search-wapper">
            <el-select v-model="searchType" placeholder="请选择" class="searchType">
                <el-option
                    v-for="item in options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                </el-option>
            </el-select>
            <el-input type="text" v-model="searchVal"></el-input>
            <el-button @click="searchData" type="primary" class="btn"><i class="el-icon-search"></i></el-button>
        </div>
        <p class="tip-wapper">(点击下列资产可查看报表)</p>
        <div class="equipment-wapper" v-loading.fullscreen.lock="fullscreenLoading" element-loading-text="获取中" element-loading-background="rgba(0, 0, 0, 0.6)">
            <!--<div class="wapper-tit">资产列表</div>-->
            <div class="wapper-con">
                <ul class="con-ul">
                    <li class="con-item" v-for="(item,index) in equipmentList" @click="currentIndex = index">
                        <div class="li-btn" v-if="currentIndex === index">
                            <i class="el-icon-error" style="position: absolute;left: 5px;top: 5px;font-size: 15px;" @click.stop="currentIndex = ''"></i>
                            <div>
                                <el-button type="primary" size="mini" v-if="item.logType === 'metric'" @click="goToDashboard(item)">仪表盘</el-button>
                                <el-button type="primary" size="mini" @click="goToChart(item)">图表</el-button>
                            </div>
                        </div>
                        <span class="logCount">{{item.log_count}}</span>
                        <div class="item-inf">
                            <p>{{item.logType}}</p>
                            <p>{{item.type}}</p>
                            <p>{{item.ip}}</p>
                        </div>
                        <div class="item-name">{{item.name}}</div>
                    </li>
                </ul>
                <p class="next-page" v-show="allCount> (searchObj.pageIndex * searchObj.pageSize)" @click="nextPage">
                    <i class="el-icon-d-arrow-right"></i>
                </p>
            </div>
        </div>
    </div>

</template>

<script>
    export default {
        name: "equipmentList",
        data() {
            return {
                currentIndex:'',
                fullscreenLoading: false,
                options: [{
                    value: 'name',
                    label: '资产名称'
                }, {
                    value: 'hostName',
                    label: '主机名'
                }, {
                    value: 'ip',
                    label: 'IP地址'
                }, {
                    value: 'logType',
                    label: '日志类型'
                }],
                searchType: 'name',
                searchVal: '',
                searchObj: {
                    name: '',
                    hostName: '',
                    ip: '',
                    logType: '',
                    pageIndex: 1,
                    pageSize: 15
                },
                allCount: 0,
                equipmentList: [],
                equipmentType: [

                ]
            }
        },
        created() {
            this.getEquipmentType();
            //获取数据
            this.getEuquipmentData(1);
        },
        methods: {
            /*获取资产类型*/
            getEquipmentType(){
                this.$nextTick(()=>{
                    this.$axios.get('static/filejson/equiptype.json',{})
                        .then((res)=>{
                            this.equipmentType = res.data
                        })
                        .catch((err)=>{
                            console.log(err)
                        })
                })
            },
            /*获取资产列表*/
            getEuquipmentData(page) {
                this.fullscreenLoading = true;
                this.searchObj.pageIndex = page;
                this.$axios.post(this.$baseUrl + '/equipment/selectPage.do', this.$qs.stringify(this.searchObj))
                    .then(res => {
                        this.fullscreenLoading = false;
                        this.allCount = res.data[0].count.count;
                        let arr = res.data[0].equipment;
                        for (let i in arr) {
                            let type = '';
                            const str = arr[i].type.substring(0, 2);
                            for (let n in this.equipmentType) {
                                let obj = this.equipmentType[n];
                                if (obj.value == str) {
                                    type += obj.label + '-';
                                    for (let j in obj.children) {
                                        if (obj.children[j].value == arr[i].type) {
                                            type += obj.children[j].label;
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            arr[i].type = type;
                            this.equipmentList.push(arr[i]);
                        }
                    })
                    .catch(err => {
                        this.fullscreenLoading = false;
                        this.$Message.err('获取数据失败');
                    })
            },
            /*检索按钮*/
            searchData() {
                //条件初始化
                this.searchObj = {
                    name: '',
                    hostName: '',
                    ip: '',
                    logType: '',
                    pageIndex: 1,
                    pageSize: 15
                }
                this.searchObj[this.searchType] = this.searchVal;
                this.equipmentList = [];
                this.getEuquipmentData(1);
            },
            /*下一页*/
            nextPage() {
                this.getEuquipmentData(this.searchObj.pageIndex + 1);
            },
            /*路由跳转-图表*/
            goToChart(item) {
                if (item.logType === 'winlog') {
                    //jumpHtml('winEquipmentCharts'+item.id,'mobile/equipment/winEquipmentCharts.vue',{ name:item.name,id: item.id ,logType:item.logType},'统计');
                    let sss = ('winEquipmentCharts' + item.id).replace(/\//g, "&-")
                    let newRouters = [{
                        path: '/mobile',
                        component: resolve => require(['../../../components/mobile/common/home'], resolve),
                        children: [
                            {
                                path: '/mobile/' + sss,
                                name: 'equipmentCharts' + item.id,
                                component: resolve => require(['@/components/mobile/equipment/winEquipmentCharts.vue'], resolve),
                                meta: {title: '统计'}
                            }
                        ]
                    }]
                    this.$router.addRoutes(newRouters);
                    this.$router.push({
                        path: '/mobile/' + sss,
                        query: {name: item.name, id: item.id, logType: item.logType}
                    })
                } else {
                    //jumpHtml('equipmentCharts'+item.id,'mobile/equipment/equipmentCharts.vue',{ name:item.name,id: item.id ,logType:item.logType},'统计');
                    let sss = ('equipmentCharts' + item.id).replace(/\//g, "&-")
                    let newRouters = [{
                        path: '/mobile',
                        component: resolve => require(['../../../components/mobile/common/home'], resolve),
                        children: [
                            {
                                path: '/mobile/' + sss,
                                name: 'equipmentCharts' + item.id,
                                component: resolve => require(['@/components/mobile/equipment/equipmentCharts.vue'], resolve),
                                meta: {title: '统计'}
                            }
                        ]
                    }]
                    this.$router.addRoutes(newRouters);
                    this.$router.push({
                        path: '/mobile/' + sss,
                        query: {name: item.name, id: item.id, logType: item.logType}
                    })

                }

            },
            /*路由跳转-仪表盘*/
            goToDashboard(item){
                //name:rowData.name+'指标数据统计',eid: rowData.id,id:'y_qMB3IBmkPMjFRE7O-_',type:'EQedit'
                let sss = ('equipmentDashboard' + item.id).replace(/\//g, "&-")
                let newRouters = [{
                    path: '/mobile',
                    component: resolve => require(['../../../components/mobile/common/home'], resolve),
                    children: [
                        {
                            path: '/mobile/' + sss,
                            name: 'equipmentDashboard' + item.id,
                            component: resolve => require(['@/components/mobile/dashboard/dashboard_m.vue'], resolve),
                            meta: {title: '统计'}
                        }
                    ]
                }]
                this.$router.addRoutes(newRouters);
                this.$router.push({
                    path: '/mobile/' + sss,
                    query: {name:item.name+'指标数据统计',eid: item.id,id:'y_qMB3IBmkPMjFRE7O-_',type:'EQedit'}
                })

            }
        }
    }
</script>

<style scoped>
    .content-bg {
        padding: 30px 0.2rem;
    }

    .search-wapper {
        height: 50px;
        display: flex;
        align-items: center;
        padding: 0 10px;
        margin-bottom: 5px;
    }
    .tip-wapper{
        text-align: center;
        font-size: 12px;
        margin-bottom: 20px;
    }
    .searchType {
        width: 180px;
        -webkit-border-radius: 0;
        -moz-border-radius: 0;
        border-radius: 0;
    }

    .btn {
        background-color: #185bff;
        border-color: #185bff;
        -webkit-border-radius: 0;
        -moz-border-radius: 0;
        border-radius: 0;
    }

    .equipment-wapper {
        /*background: url("../../../static/img/panel-l-t.png");
        background-size: 100% 100%;*/
    }

    .wapper-tit {
        height: 1rem;
        line-height: 1rem;
        font-size: 0.38rem;
        text-align: left;
        padding-left: 25px;
        background: url("../../../../static/img/title-bg.png");
        background-size: 100% 100%;
    }

    .wapper-con {
        /*height: 100px;*/
    }

    .con-ul {
        display: flex;
        justify-content: space-around;
        flex-wrap: wrap;
    }

    .con-item {
        width: 33%;
        max-width: 120px;
        height: 160px;
        font-size: 0.3rem;
        margin-top: 5px;
        margin-bottom: 5px;
        position: relative;
    }

    .li-btn {
        width: 100%;
        height: 100%;
        position: absolute;
        background: rgba(0, 0, 0, 0.5);
        text-align: center;
        display: flex;
        justify-content:center;
        align-items: center;
    }
    .li-btn button{
        width: 68px;
        margin: 10px;
    }
    .logCount {
        position: absolute;
        padding: 2px 5px;
        color: #FFF;
        top: -9px;
        right: 5px;
        display: inline-block;
        background: #e4956d;
    }

    .item-inf {
        width: 100px;
        margin: 0 auto;
        background: url("../../../../static/img/panel-l-t.png");
        background-size: 100% 100%;
        height: 100px;
        padding: 0 5px;
        padding-top: 10px;
        border: 1px dashed #102d84;
    }

    .item-inf p {
        border-bottom: 1px dashed #102d84;
        height: 30px;
        line-height: 30px;
        margin: 0;
        text-align: center;
        font-size: 0.5rem;
    }

    .item-name {
        height: 40px;
        background: #0e1d59;
        border: 1px solid #102d84;
        font-size: 0.5rem;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #39c0bd;
    }

    .next-page {
        font-size: 1rem;
        padding: 10px 0;
        margin: 0;
        text-align: center;
    }

    .next-page i {
        transform: rotate(90deg);
    }

    /deep/ .el-input__inner {
        background: 0;
        border-radius: 0;
        border: 1px solid #175aff;
    }

</style>
