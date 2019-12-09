<template>
    <div class="content-bg">
        <div class="top-title">资产监控</div>
        <div class="monitor-wapper">
            <el-row :gutter="10" class="wapper-row">
                <el-col :span="8">
                    <div class="grid-content con-wapper msg">
                        <h2 class="con-tit">名称：Zabbix server</h2>
                        <div class="con">
                            <h3>一般信息</h3>
                            <p><span class="p-t">启动时间：</span><span class="p-c">2019-06-12 10:10:10</span></p>
                            <p><span class="p-t">本地时间：</span><span class="p-c">2019-06-12 10:10:10</span></p>
                            <p><span class="p-t">主机名：</span><span class="p-c">jzLog server</span></p>
                            <h3>OS</h3>
                            <p><span class="p-t">系统信息：</span><span class="p-c">Linux localhost localdomain </span></p>
                            <p><span class="p-t">运行时间：</span><span class="p-c"> 01:00:50</span></p>
                            <p><span class="p-t">登录用户数：</span><span class="p-c">2</span></p>
                            <h3>argent</h3>
                            <p><span class="p-t">主机名：</span><span class="p-c"> Azbbixserver</span></p>
                            <p><span class="p-t">版本号：</span><span class="p-c">4.2.1</span></p>
                            <p><span class="p-t">ping：</span><span class="p-c">Up(1)</span></p>
                        </div>
                    </div>
                </el-col>
                <el-col :span="16">
                    <div class="grid-content con-wapper" style="padding: 0; background: 0">
                        <el-row :gutter="10">
                            <el-col :span="6">
                                <div class="pie pie1">
                                    <div class="pie-t">已用内存</div>
                                    <div class="pie-c">
                                        <v-echarts echartType="ring" :echartData="this.pie1Data"></v-echarts>
                                    </div>
                                </div>
                            </el-col>
                            <el-col :span="6">
                                <div class="pie pie2">
                                    <div class="pie-t">已用磁盘</div>
                                    <div class="pie-c">
                                        <v-echarts echartType="ring" :echartData="this.pie2Data"></v-echarts>
                                    </div>
                                </div>
                            </el-col>
                            <el-col :span="6">
                                <div class="pie pie3">
                                    <div class="pie-t">已运行进程</div>
                                    <div class="pie-c">
                                        <v-echarts echartType="ring" :echartData="this.pie3Data"></v-echarts>
                                    </div>
                                </div>
                            </el-col>
                            <el-col :span="6">
                                <div class="pie pie4">
                                    <div class="pie-t">已用索引接节点</div>
                                    <div class="pie-c">
                                        <v-echarts echartType="ring" :echartData="this.pie4Data"></v-echarts>
                                    </div>
                                </div>
                            </el-col>
                        </el-row>
                        <div class="line line1">
                            <div class="line-t">网络接口</div>
                            <div class="line-c">
                                <v-echarts echartType="moreline" :echartData="this.line1Data"></v-echarts>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>
            <el-row :gutter="10" class="wapper-row" >
                <el-col :span="14">
                    <div class="grid-content con-wapper line2">
                        <div class="line-t">CPU利用率</div>
                        <div class="line-c">
                            <v-echarts echartType="moreline" :echartData="this.line2Data"></v-echarts>
                        </div>
                    </div>
                </el-col>
                <el-col :span="10">
                    <div class="grid-content con-wapper line3">
                        <div class="line-t">CPU负载</div>
                        <div class="line-c">
                            <v-echarts echartType="moreline" :echartData="this.line3Data"></v-echarts>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>
    </div>
    
</template>

<script>
    import vEcharts from '../common/echarts'
	export default {
		name: "deviceMonitor",
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
                        title:'总磁盘'
                    },
                    yAxisArr:[
                        {name:'已使用',val:50,label:true,color1:'#bfd9fe',color2:'#df89b5'},
                        {name:'未使用',val:450,label:false,color1:'#5bc0de',color2:'#5FE2E4'}
                    ]
                },
                pie3Data:{
                    baseConfig:{
                        title:'总进程'
                    },
                    yAxisArr:[
                        {name:'已运行',val:11,label:true,color1:'#bfd9fe',color2:'#df89b5'},
                        {name:'未运行',val:100,label:false,color1:'#5bc0de',color2:'#5FE2E4'}
                    ]
                },
                pie4Data:{
                    baseConfig:{
                        title:'总索引'
                    },
                    yAxisArr:[
                        {name:'已使用',val:221,label:true,color1:'#bfd9fe',color2:'#df89b5'},
                        {name:'未使用',val:1010,label:false,color1:'#5bc0de',color2:'#5FE2E4'}
                    ]
                },
                line1Data:{
                    baseConfig:{
                        title:''
                    },
                    xAxisArr:['10:00:00','10:00:10','10:00:20','10:00:30','10:00:40','10:00:50','10:01:00','10:01:10','10:01:20','10:01:30','10:01:40','10:01:50','10:02:00'],
                    yAxisArr:[
                        {
                            name: '进入',
                            color:'#00EABD',
                            data: [10, 52, 200, 334, 43, 87,400,580,300,400,258,550,400]
                        },
                        {
                            name: '流出',
                            color:'#508DFF',
                            data: [10, 20, 10, 8, 10, 15,8,13,20,4,2,10,10]
                        }
                    ]
                },
                line2Data:{
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
                line3Data:{
                    baseConfig:{
                        title:''
                    },
                    xAxisArr:['10:00:00','10:00:10','10:00:20','10:00:30','10:00:40','10:00:50','10:01:00','10:01:10','10:01:20','10:01:30','10:01:40','10:01:50','10:02:00'],
                    yAxisArr:[
                        {
                            name: '处理器负载（每个核心平均1分钟）',
                            color:'#00EABD',
                            data:  ['18', '20', '40', '50', '12', '30', '20', '10', '20', '20', '30', '10','0']
                        },
                        {
                            name: '处理器负载（每个核心平均5分钟）',
                            color:'#20C1F3',
                            data:  ['10', '15', '30', '40', '10', '20', '18', '8', '18', '10', '20', '5','0']
                        },
                        {
                            name: '处理器负载（每个核心平均15分钟）',
                            color:'#C0D7FC',
                            data:['5', '10', '20', '30', '5', '10', '8', '5', '1', '1', '10', '0','0']
                        }
                    ]
                }
            }
		},
        created(){

        },
        components:{
            vEcharts
        }
	}
</script>

<style scoped>
    .wapper-row{
        margin: 10px;
    }

    .con-wapper{
        min-height: 150px;
        background: #303e4e;
        padding: 15px;
        font-size: 13px;
    }
    .msg{
        height: 460px;
    }
    .msg h2{
        height: 50px;
        line-height: 50px;
        color: #e4956d;
        font-size: 20px;
    }
    .msg .con h3{
        height: 34px;
        line-height: 34px;
        padding-left: 30px;
        background: #386c9a;
    }
    .msg .con p{
        height: 30px;
        line-height: 30px;
        /* padding-left: 60px; */
        background-color: #2f455c;
        border-bottom: 1px solid #365778;
    }
    .msg .con p .p-t{
        display: inline-block;
        width: 120px;
        text-align: end;
        margin-right: 20px;
    }
    .msg .con p .p-c{

    }
    .pie{
        height: 150px;
        display: flex;
        background: #303e4e;
    }
    .pie-t{
        width: 20px;
        height: 100%;
        color: #e4956d;
        padding-top: 10px;
        font-weight: 600;
        margin-left: 10px;
    }
    .pie-c{
        width: calc(100% - 20px);
        height: 150px;
    }
    .line{
        height: 330px;
        background: #303e4e;
        margin-top: 10px;
    }
    .line-t{
        height: 30px;
        line-height: 30px;
        color: #e4956d;
        padding-left: 20px;
    }
    .line-c{
        height: 300px;
    }

</style>
