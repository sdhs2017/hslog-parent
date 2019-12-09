<template>
    <div class="content-bg">
        <div class="top-title">资产概览</div>
        <div class="equipemnt-tools-form">
            <v-search-form :formItem="formConditionsArr" :busName="busName"></v-search-form>
        </div>
        <div class="eq-wapper">
            <ul class="eq-ul">
                <li v-for="(i,index) in equipmentList"  :key="index"  class="eq-li" @mouseenter="liMouseenter($event)" @mouseleave="liMouseleave($event)" @>
                    <div class="eq-name">{{i.name}}</div>
                    <div class="eq-type">{{i.type}}</div>
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
                                <div class="inf-val">{{i.name}}</div>
                            </li>
                            <li class="inf-type">
                                <div class="inf-tit">资产类型：</div>
                                <div class="inf-val">主机-windows</div>
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
                                <div class="inf-val">{{i.port == '1' ? '是' : '否'}}</div>
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
                    <div class="eq-tools">
                        <a href="javascript:void(0)" class="tools-more">
                            <i class="el-icon-arrow-down "></i>
                        </a>
                    </div>
                    <ul class="tools-ul" data-angle="[0,215]" @mouseenter="zzIndex = 2" @mouseleave="zzIndex = -2">
                        <li class="item"><i class="el-icon-edit "></i></li>
                        <li class="item"><i class="el-icon-tickets"></i></li>
                        <li class="item"><i class="el-icon-share"></i></li>
                        <li class="item"><i class="el-icon-date"></i></li>
                        <li class="item"><i class="el-icon-bell"></i></li>
                        <li class="item"><i class="el-icon-view"></i></li>
                    </ul>
                </li>
            </ul>
        </div>
        <div class="equipment-table-page">
            <span>共检索到资产数量为 <b>{{allCounts}}</b> 台</span>
            <el-pagination background layout="prev, pager, next" @current-change="handleCurrentChange" :current-page.sync="c_page" :page-size="size" :total="allCounts"></el-pagination>
        </div>
        <div class="zz-box" :style="{zIndex:zzIndex}"></div>
    </div>
    
</template>

<script>
    import vSearchForm from '../common/BaseSearchForm';
    import bus from '../common/bus';
	export default {
		name: "device2",
		data() {
			return {
			    zzIndex:-2,
                busName:'searchEquipment',
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
                equipmentList:[],
                formConditionsArr:[
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
                        paramName:'hostname',
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
            }
		},
        created(){
            //获取日志类型数据
            this.getLogType();
            this.getEquipmentType();
            //监听查询条件组件传过来的条件
            bus.$on(this.busName,(params)=>{
                this.searchConditions = params;
                this.getData(this.searchConditions,1)
                this.c_page = 1;
            })
            this.getData(this.searchConditions,1)
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
                                    label:res.data[i].type
                                };
                                this.logType.push(obj);
                            }
                        })
                        .catch((err)=>{
                            console.log(err)
                        })
                })
            },
            /*获得资产类型数据*/
            getEquipmentType(){
                this.$nextTick(()=>{
                    this.$axios.get('static/filejson/equiptype.json',{})
                        .then((res)=>{
                            this.typeArr = res.data
                            this.formConditionsArr[this.formConditionsArr.length - 1].options = this.typeArr
                        })
                        .catch((err)=>{
                            console.log(err)
                        })
                })
            },
		    /*获取数据*/
            getData(searchObj,page){
                this.$nextTick(()=>{
                    layer.load(1);
                    let obj = searchObj;
                    obj.pageIndex = page;//当前页
                    obj.pageSize = this.size;//页的条数
                    this.$axios.post(this.$baseUrl+'/equipment/selectPage.do',this.$qs.stringify(obj))
                        .then(res=>{
                            layer.closeAll('loading');
                            let data = res.data[0].equipment;
                            this.allCounts = JSON.parse(res.data[0].count.count);
                            //将查询条件保存，用于分页
                            this.saveCondition = this.searchConditions;
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
                            this.$nextTick(()=>{
                                $(".tools-more").wheelmenu({
                                    trigger: "hover",
                                    animation: "fly",
                                    animationSpeed: "fast",
                                    angle: "all",
                                });
                            })

                        })
                        .catch(err=>{
                            layer.closeAll('loading');

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
            }
        },
        components:{
            vSearchForm
        }
	}
</script>

<style scoped>
    .eq-wapper{
        background: #293846;
        padding: 20px;
        margin: 20px;

    }
    .eq-ul{
        padding-bottom: 100px;
    }
    .eq-ul:after{
        content: '';
        clear: both;
        display: block;
        height: 0;
        visibility: hidden;
    }
    .eq-li{
        width: 238px;
        float: left;
        color: #5bc0de;
        margin: 20px;
        position: relative;
        transition: all .2s linear;
        height: 110px;
        padding: 0 10px;
        background-image: linear-gradient(to right, #2f455c 0%, #386c9a 100%);
        border: 1px solid #365778;
    }
    .eq-li:after{
        content: '';
        position: absolute;
        left: 0;
        top: 110px;
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
        cursor: pointer;
    }
    .eq-name{
        color: #E4956D;
        font-size: 18px;
        font-weight: 600;
        height: 56px;
        line-height: 70px;
        border-bottom: 1px dashed #5bc0de;
        transition: all 0.2s linear;
        overflow: hidden;
        position: relative;
        /*z-index: 2;*/
        cursor: pointer;
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
        font-size: 12px;
        color: #fff;
        background: #2f455c;
        border: 5px solid #376995;
        overflow: hidden;
        /*z-index: 3;*/
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
</style>
