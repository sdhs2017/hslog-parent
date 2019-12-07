<template>
    <div class="content-bg">
        <div class="top-title">业务系统监控</div>
        <div class="all-monitor-wapper">
            <p class="con-tit">名称：业务系统</p>
            <el-row :gutter="10" class="wapper-row" >
                <el-col :span="11">
                    <div class="">
                        <div class="item-wapper ring-wapper-01">
                            <div class="item-tit">内存使用率</div>
                            <el-row>
                                <el-col :span="8">
                                    <div class="ring ring1">
                                        <v-echarts echartType="ring" :echartData="this.pie1Data"></v-echarts>
                                    </div>
                                    <div class="eq-name">server1</div>
                                </el-col>
                                <el-col :span="8">
                                    <div class="ring ring2">
                                        <v-echarts echartType="ring" :echartData="this.pie2Data"></v-echarts>
                                    </div>
                                    <div class="eq-name">server2</div>
                                </el-col>
                                <el-col :span="8">
                                    <div class="ring ring3">
                                        <v-echarts echartType="ring" :echartData="this.pie3Data"></v-echarts>
                                    </div>
                                    <div class="eq-name">server3</div>
                                </el-col>
                            </el-row>
                        </div>
                        <div class="item-wapper ring-wapper-02">
                            <div class="item-tit">磁盘空间</div>
                            <el-row>
                                <el-col :span="8">
                                    <div class="ring ring1">
                                        <v-echarts echartType="ring" :echartData="this.pie4Data"></v-echarts>
                                    </div>
                                    <div class="eq-name">server1</div>
                                </el-col>
                                <el-col :span="8">
                                    <div class="ring ring2">
                                        <v-echarts echartType="ring" :echartData="this.pie5Data"></v-echarts>
                                    </div>
                                    <div class="eq-name">server2</div>
                                </el-col>
                                <el-col :span="8">
                                    <div class="ring ring3">
                                        <v-echarts echartType="ring" :echartData="this.pie6Data"></v-echarts>
                                    </div>
                                    <div class="eq-name">server3</div>
                                </el-col>
                            </el-row>
                        </div>
                        <div class="ring-wapper-02"></div>
                    </div>
                </el-col>
                <el-col :span="13">
                    <div class="item-wapper line line1">
                        <div class="item-tit">内存使用率</div>
                        <div class="item-con">
                            <v-echarts echartType="moreline" :echartData="this.line1Data"></v-echarts>
                        </div>
                    </div>
                </el-col>
            </el-row>
            <el-row :gutter="10" class="wapper-row" >
                <el-col :span="14">
                    <div class="item-wapper line line2">
                        <div class="item-tit">server2 通讯量</div>
                        <div class="item-con">
                            <v-echarts echartType="line" :echartData="this.line2Data"></v-echarts>
                        </div>
                    </div>
                </el-col>
                <el-col :span="10">
                    <div class="item-wapper bar bar1">
                        <div class="item-tit">ERROR 日志量</div>
                        <div class="item-con">
                            <v-echarts echartType="bar" :echartData="this.bar1Data"></v-echarts>
                        </div>
                    </div>
                </el-col>
            </el-row>
            <div class="item-wapper">
                <div class="item-tit">server2 通讯量</div>
                <el-row :gutter="10" class="wapper-row" >
                    <el-col :span="8">
                        <div class="item-con line line3">
                            <v-echarts echartType="moreline" :echartData="this.line3Data"></v-echarts>
                        </div>
                        <div class="eq-name">server1</div>
                    </el-col>
                    <el-col :span="8">
                        <div class="item-con line line4">
                            <v-echarts echartType="moreline" :echartData="this.line4Data"></v-echarts>
                        </div>
                        <div class="eq-name">server2</div>
                    </el-col>
                    <el-col :span="8">
                        <div class="item-con line line5">
                            <v-echarts echartType="moreline" :echartData="this.line5Data"></v-echarts>
                        </div>
                        <div class="eq-name">server3</div>
                    </el-col>

                </el-row>
            </div>

        </div>
    </div>
    
</template>

<script>
    import vEcharts from '../common/echarts'
	export default {
		name: "allDeviceMonitor",
		data() {
			return {
                pie1Data:{
                    baseConfig:{
                        title:'内存总数'
                    },
                    yAxisArr:[
                        {name:'已使用',val:5.97,label:true,color1:'#bfd9fe',color2:'#df89b5'},
                        {name:'未使用',val:10,label:false,color1:'#5bc0de',color2:'#5FE2E4'}
                    ]
                },
                pie2Data:{
                    baseConfig:{
                        title:'内存总数'
                    },
                    yAxisArr:[
                        {name:'已使用',val:8,label:true,color1:'#bfd9fe',color2:'#df89b5'},
                        {name:'未使用',val:7.56,label:false,color1:'#5bc0de',color2:'#5FE2E4'}
                    ]
                },
                pie3Data:{
                    baseConfig:{
                        title:'内存总数'
                    },
                    yAxisArr:[
                        {name:'已使用',val:1,label:true,color1:'#bfd9fe',color2:'#df89b5'},
                        {name:'未使用',val:14.56,label:false,color1:'#5bc0de',color2:'#5FE2E4'}
                    ]
                },
                pie4Data:{
                    baseConfig:{
                        title:'磁盘空间'
                    },
                    yAxisArr:[
                        {name:'已使用',val:100,label:true,color1:'#bfd9fe',color2:'#df89b5'},
                        {name:'未使用',val:400,label:false,color1:'#5bc0de',color2:'#5FE2E4'}
                    ]
                },
                pie5Data:{
                    baseConfig:{
                        title:'磁盘空间'
                    },
                    yAxisArr:[
                        {name:'已使用',val:50,label:true,color1:'#bfd9fe',color2:'#df89b5'},
                        {name:'未使用',val:450,label:false,color1:'#5bc0de',color2:'#5FE2E4'}
                    ]
                },
                pie6Data:{
                    baseConfig:{
                        title:'磁盘空间'
                    },
                    yAxisArr:[
                        {name:'已使用',val:300,label:true,color1:'#bfd9fe',color2:'#df89b5'},
                        {name:'未使用',val:200,label:false,color1:'#5bc0de',color2:'#5FE2E4'}
                    ]
                },
                line1Data:{
                    baseConfig:{
                        title:''
                    },
                    xAxisArr:['10:00:00','10:00:10','10:00:20','10:00:30','10:00:40','10:00:50','10:01:00','10:01:10','10:01:20','10:01:30','10:01:40','10:01:50'],
                    yAxisArr:[
                        {
                            name: 'CPU空闲时间',
                            color:'#00EABD',
                            data: ['90', '91', '95', '80', '99', '97', '89', '97', '90', '90', '99', '99']
                        },
                        {
                            name: 'CPU用户时间',
                            color:'#20C1F3',
                            data: ['10', '20', '5', '18', '1', '8', '10', '10', '2', '5','10','9']
                        },
                        {
                            name: 'CPU系统时间',
                            color:'#FC686F',
                            data: ['1', '8', '3', '10', '5', '5', '3', '11', '1', '5','3','1']
                        },
                        {
                            name: 'CPU等待时间',
                            color:'#F9D124',
                            data: ['10', '2', '2', '7', '11', '3', '7', '1', '10', '1','8','1']
                        },
                        {
                            name: '低优先级用户态时间',
                            color:'#DE1AFB',
                            data: ['8', '1', '7', '4', '1', '1', '1', '0', '0', '1','11','1']
                        },
                        {
                            name: 'CPU中断时间',
                            color:'#C0D7FC',
                            data: ['0', '0', '0', '9', '3', '0', '0', '0', '0', '0','0','1']
                        },
                        {
                            name: 'CPU筛选时间',
                            color:'#A9F4B7',
                            data: ['31', '20', '32', '14', '35', '14', '31', '20', '11', '10','10','10']
                        },
                        {
                            name: 'CPU被占用时间',
                            color:'#FF9E96',
                            data:  ['1', '2', '3', '4', '5', '4', '3', '2', '1', '1','1','1']
                        },
                        {
                            name: '运行虚拟机时间',
                            color:'#75B568',
                            data: ['1', '2', '3', '4', '5', '4', '3', '2', '1', '1','1','1']
                        },
                        {
                            name: '低优先级运行虚拟机的时间',
                            color:'#323A81',
                            data:  ['1', '2', '3', '4', '5', '4', '3', '2', '1', '1','1','1']
                        }
                    ]
                },
                line2Data:{
                    baseConfig:{
                        title:''
                    },
                    xAxisArr:['10:00:00','10:00:10','10:00:20','10:00:30','10:00:40','10:00:50','10:01:00','10:01:10','10:01:20','10:01:30','10:01:40','10:01:50'],
                    yAxisArr: ['8', '10', '20', '20', '20', '30', '20', '10', '20', '20', '30', '10', '0']

                },
                line3Data:{
                    baseConfig:{
                        title:''
                    },
                    xAxisArr:['10:00:00','10:00:10','10:00:20','10:00:30','10:00:40','10:00:50','10:01:00','10:01:10','10:01:20','10:01:30','10:01:40','10:01:50'],
                    yAxisArr:[
                        {
                            name: '进入',
                            color:'#00EABD',
                            data: [10, 52, 200, 334, 43, 87,400,580,300,400,258,550,400]
                        },
                        {
                            name: '流出',
                            color:'#20C1F3',
                            data:[10, 20, 10, 8, 10, 15,8,13,20,4,2,10,10]
                        }
                    ]
                },
                line4Data:{
                    baseConfig:{
                        title:''
                    },
                    xAxisArr:['10:00:00','10:00:10','10:00:20','10:00:30','10:00:40','10:00:50','10:01:00','10:01:10','10:01:20','10:01:30','10:01:40','10:01:50'],
                    yAxisArr:[
                        {
                            name: '进入',
                            color:'#00EABD',
                            data: [50, 100, 100, 50, 80, 100,200,400,300,400,258,550,400]
                        },
                        {
                            name: '流出',
                            color:'#20C1F3',
                            data: [100, 80, 50, 10, 10, 10,10,40,40,4,2,10,10]
                        }
                    ]
                },
                line5Data:{
                    baseConfig:{
                        title:''
                    },
                    xAxisArr:['10:00:00','10:00:10','10:00:20','10:00:30','10:00:40','10:00:50','10:01:00','10:01:10','10:01:20','10:01:30','10:01:40','10:01:50'],
                    yAxisArr:[
                        {
                            name: '进入',
                            color:'#00EABD',
                            data: [10, 8, 20, 10, 10, 8,8,5,2,0,2,2,10]
                        },
                        {
                            name: '流出',
                            color:'#20C1F3',
                            data: [50, 200, 100, 80, 100, 105,80,103,80,70,72,100,100]
                        }
                    ]
                },
                bar1Data:{
                    baseConfig:{
                        title:''
                    },
                    xAxisArr:['server1','server2','server3'],
                    yAxisArr:['141','300','200']
                }
            }
		},
        components:{
            vEcharts
        }
	}
</script>

<style scoped>
    .all-monitor-wapper{
        padding: 20px;
    }
    .con-tit{
        height: 80px;
        line-height: 80px;
        color: #e4956d;
        /* font-weight: 600; */
        font-size: 18px;
        text-align: center;
        background: #303e4e;
        margin-bottom: 10px;
    }
    .wapper-row{
        margin-bottom: 10px;
    }
    .item-wapper{
        min-height: 150px;
        background: #303e4e;
        padding: 15px;
        font-size: 13px;
    }
    .ring-wapper-01{
        margin-bottom: 10px;
    }
    .item-tit{
        color: #e4956d;
        height: 30px;
        /*line-height: 30px;*/
        padding-left: 20px;
        font-weight: 600;
    }
    .ring{
        height: 130px;
    }
    .eq-name{
        height: 20px;
        line-height: 20px;
        text-align:center ;
        color: #5bc0de;
        font-weight: 600;
    }
    .line1{
        height: 400px;
    }
    .line1 .item-con{
        height: 380px;
    }
    .line2 .item-con{
        height: 300px;
    }
    .bar .item-con {
        height: 300px;
    }
    .line3,.line4,.line5{
        height: 300px;
    }
</style>
