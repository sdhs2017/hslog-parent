<template>
    <div v-loading="loading" element-loading-background="rgba(48, 62, 78, 0.5)">
        <el-steps :active="active" simple>
            <el-step title="基本信息" icon="el-icon-edit-outline"></el-step>
            <el-step title="数据源" icon="el-icon-coin"></el-step>
            <el-step title="选择字段" icon="el-icon-thumb"></el-step>
            <el-step title="完成" icon="el-icon-success"></el-step>
        </el-steps>
        <div class="con-wapper" v-show="active === 0">
            <div class="form-con">
                <div class="form-item">
                    <span class="mustWrite" style="left: 23px;">*</span>
                    <div class="item-label" style="position: absolute;width: 80px;">名称：</div>
                    <div class="item-con" style="margin-left: 80px;width: 350px;">
                        <el-input v-model="form.alert_name" size="mini" placeholder="名称"></el-input>
                    </div>
                </div>
                <div class="form-item">
                    <span class="mustWrite" style="left: -8px;">*</span>
                    <div class="item-label" style="position: absolute;width: 80px;top: 0;">执行周期：</div>
                    <div class="item-con" style="margin-left: 80px;width: 350px;">
                        <template>
                            <el-radio v-model="form.alert_exec_type" label="simple">普通</el-radio>
                            <el-radio v-model="form.alert_exec_type" label="complex">高级</el-radio>
                        </template>
                        <div v-if="form.alert_exec_type === 'simple'" style="display:flex;margin-top: 10px;">
                            <el-input v-model="form.alert_time_cycle_num" size="mini" type="number" style="margin-right: 10px;" min="1" @change="timeCycleNumChange"></el-input>
                            <el-select v-model="form.alert_time_cycle_type" placeholder="请选择" size="mini" @change="timeCycleTypeChange">
                                <el-option label="秒" value="second"></el-option>
                                <el-option label="分钟" value="minute"></el-option>
                                <el-option label="小时" value="hour"></el-option>
                            </el-select>
                        </div>
                        <div v-if="form.alert_exec_type === 'complex'" style="margin-top: 10px;">
                            <el-input v-model="form.alert_cron" size="mini" placeholder="请输入cron表达式"></el-input>
                        </div>
                        <p  v-if="form.alert_exec_type === 'simple'" style="color: #e4956d;font-size: 12px;margin:5px 0 0 0;">(普通模式下，秒、分钟小于60，小时小于24，且都为整数。)</p>
                    </div>
                </div>
                <div class="form-item" v-if="form.alert_exec_type === 'complex'">
                    <span class="mustWrite" style="left: -8px;">*</span>
                    <div class="item-label" style="position: absolute;width: 80px;">时间周期：</div>
                    <div class="item-con" style="margin-left: 80px;width: 350px;">
                        <date-layout :busName="busNameObj.busDateName" :defaultVal="defaultVal"></date-layout>
                    </div>
                </div>
                <div class="form-item">
                    <!--                    <span class="mustWrite" >*</span>-->
                    <div class="item-label" style="position: absolute;top: 5px;width: 80px;">说明：</div>
                    <div class="item-con" style="margin-left: 80px;">
                        <el-input v-model="form.alert_note" size="mini" type="textarea" rows="10" placeholder="说明"></el-input>
                    </div>
                </div>
            </div>
            <div class="btn-wapper" style="display: flex;">
<!--                <el-button @click="active = 1" type="primary">上一步</el-button>-->
                <el-button type="primary"  @click="active = 1" :disabled="(form.alert_name === '' || (form.alert_exec_type === 'simple' ? (form.alert_time_cycle_num === '' ? true : false) : (form.alert_cron === '' ? true :false))) ? true : false">下一步</el-button>
            </div>
        </div>
        <div class="con-wapper" v-show="active === 1">
            <div class="form-con">
                <div class="form-item">
                    <span class="mustWrite" style="left: -10px;">*</span>
                    <choose-index :busName="this.busNameObj.busIndexName" :arr="indexVal"></choose-index>
                </div>
                <div class="form-item">
<!--                    <span class="mustWrite" >*</span>-->
                    <div class="item-label" style="position: absolute; top: 9px;width: 64px;">筛选：</div>
                    <div class="item-con" style="margin-left: 66px;">
                        <queryFilter
                            :busFilterName="this.busNameObj.busFilterName"
                            :defaultFilterArr="this.defaultFilter"
                            :queryState="false"
                            useObject="chart"
                            :templateName="this.form.template_name"
                            :preIndexName="this.form.pre_index_name"
                            :suffixIndexName="this.form.suffix_index_name"
                        >
                        </queryFilter>
                    </div>
                </div>
                <div class="form-item">
                    <div class="item-label" style="position: absolute; top: 9px;width: 64px;">统计：</div>
                    <div class="item-con" style="margin-left: 58px;" v-loading="leftYLoading" element-loading-background="rgba(48, 62, 78, 0.5)">
                        <el-collapse v-model="configOpened">
                            <el-collapse-item class="tablist" v-for="(yItem,i) in yAxisArr" :key="i" name="1">
                                <template slot="title" class="collapseTit">
                                    {{yItem.aggregationType}}
                                    <i class="header-icon el-icon-error removeTab" @click="deleteMetric"></i>
                                </template>
                                <el-form label-position="top" style="position: relative;">
                                    <el-form-item label="聚合类型">
                                        <el-select v-model="yItem.aggregationType" @change="yAggregationChange($event,i)" placeholder="请选择" style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in yAggregation"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="聚合参数"  v-if="yItem.aggregationType !== '' && yItem.aggregationType !== 'Count' ">
                                        <el-select v-model="yItem.aggregationParam" placeholder="请选择" filterable style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in yItem.aggregationParamArr"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                </el-form>
                            </el-collapse-item>
                        </el-collapse>
                        <p v-if="yAxisArr.length === 0" class="addWapper"><span @click="addMetric"><i class="el-icon-plus"></i>添加</span></p>
                    </div>
                </div>
                <div class="form-item" >
                    <div class="item-label" style="position: absolute; top: 9px;width: 64px;">分组：</div>
                    <div class="item-con" style="margin-left: 58px;" v-loading="leftXLoading" element-loading-background="rgba(48, 62, 78, 0.5)">
                        <el-collapse v-model="configOpened">
                            <el-collapse-item class="tablist" v-for="(xItem,i) in xAxisArr" :key="i" name="1">
                                <template slot="title" class="collapseTit">
                                    {{xItem.aggregationParam}}
                                    <i class="header-icon el-icon-error removeTab" @click="xAxisArr = []"></i>
                                </template>
                                <el-form label-position="top" style="position: relative">
                                    <el-form-item label="聚合类型">
                                        <el-select v-model="xItem.aggregationType" @change="xAggregationChange($event,i)" placeholder="请选择" style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in xAggregation"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="聚合参数">
                                        <el-select v-model="xItem.aggregationParam" filterable placeholder="请选择" style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in xItem.aggregationParamArr"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="间隔类型" v-if="xItem.aggregationType === 'Date Histogram'">
                                        <el-select v-model="xItem.timeType" placeholder="请选择" style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in timeIntervalArr"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="间隔数值" v-if="xItem.aggregationType === 'Date Histogram'">
                                        <el-input v-model="xItem.timeInterval" size="mini"></el-input>
                                    </el-form-item>
                                    <el-form-item label="排序方式" v-if="xItem.aggregationType === 'Terms'">
                                        <el-select v-model="xItem.orderType" placeholder="请选择" style="width: 100%;" size="mini">
                                            <el-option label="降序" value="desc"></el-option>
                                            <el-option label="升序" value="asc"></el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="展示数量" v-if="xItem.aggregationType === 'Terms'">
                                        <el-input v-model="xItem.topSum" size="mini"></el-input>
                                    </el-form-item>
                                    <el-form-item label="范围" v-if="xItem.aggregationType === 'Range'">
                                        <p class="range-p" v-for="(numRange,i) in xItem.numberRange">
                                            <el-input v-model="numRange.start" type="number" placeholder="大于等于" size="mini"></el-input>
                                            <span>-</span>
                                            <el-input v-model="numRange.end"  type="number" placeholder="小于" size="mini"></el-input>
                                            <i class="el-icon-error" @click="removeItem(xItem.numberRange,i)" disabled></i>
                                        </p>
                                        <p class="range-btn-p"><span @click="xItem.numberRange.push({start:'',end:''})"> <i class="el-icon-circle-plus"></i> 添加范围</span></p>
                                    </el-form-item>
                                    <el-form-item label="范围" v-if="xItem.aggregationType === 'Date Range'">
                                        <p class="range-p" v-for="(dateRange,i) in xItem.dateRange">
                                            <el-input v-model="dateRange.start"  size="mini"></el-input>
                                            <span>-</span>
                                            <el-input v-model="dateRange.end"  size="mini"></el-input>
                                            <i class="el-icon-error" @click="removeItem(xItem.dateRange,i)" disabled></i>
                                        </p>
                                        <p class="range-btn-p"><span @click="xItem.dateRange.push({start:'',end:''})"> <i class="el-icon-circle-plus"></i> 添加范围</span></p>
                                    </el-form-item>
                                    <el-form-item label="范围" v-if="xItem.aggregationType === 'IPv4 Range'">
                                        <p class="range-p" v-for="(ipRange,i) in xItem.ipRange">
                                            <el-input v-model="ipRange.start"  size="mini"></el-input>
                                            <span>-</span>
                                            <el-input v-model="ipRange.end"  size="mini"></el-input>
                                            <i class="el-icon-error" @click="removeItem(xItem.ipRange,i)" disabled></i>
                                        </p>
                                        <p class="range-btn-p"><span @click="xItem.ipRange.push({start:'',end:''})"> <i class="el-icon-circle-plus"></i> 添加范围</span></p>
                                    </el-form-item>
                                </el-form>
                            </el-collapse-item>
                        </el-collapse>
                        <p v-if="xAxisArr.length === 0" class="addWapper"><span @click="addBucket"><i class="el-icon-plus"></i>添加</span></p>
                    </div>
                </div>
                <div class="form-item">
                    <div class="item-label" style="position: absolute;width: 64px;">资产组：</div>
                    <div class="item-con" style="margin-left: 80px;width: 350px;">
                        <el-select v-model="form.alert_assetGroup_id" placeholder="" size="mini" clearable @change="assetGroupChange" :class="{emptyClass:form.alert_assetGroup_id === '' ? true : false}">
                            <el-option
                                v-for="item in assetGroupOptions"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                        <span v-if="form.alert_assetGroup_id !== ''" @click="form.alert_assetGroup_id = ''" style="color: #409eff;font-size: 12px;cursor:pointer;">清空</span>
                    </div>
                </div>
                <div class="form-item">
                    <div class="item-label" style="position: absolute;width: 64px;">资产：</div>
                    <div class="item-con" style="margin-left: 80px;width: 350px;">
                        <el-select v-model="form.alert_equipment_id" placeholder="" size="mini" clearable @change="assetChange" :class="{emptyClass:form.alert_equipment_id === '' ? true : false}">
                            <el-option
                                v-for="item in assetOptions"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                        <span v-if="form.alert_equipment_id !== ''"  @click="form.alert_equipment_id = ''" style="color: #409eff;font-size: 12px;cursor:pointer;">清空</span>
                    </div>
                </div>
            </div>
            <div  class="btn-wapper" style="display: flex;">
                <el-button @click="active = 0" type="primary">上一步</el-button>
                <el-button type="primary" @click="getFields()" :disabled="isCanCreate1">下一步</el-button>
            </div>
        </div>
        <div class="con-wapper" v-show="active === 2">
            <div class="form-con">
                <div class="form-item">
                    <span class="mustWrite" >*</span>
                    <div class="item-label" style="position: absolute; top: 9px;width: 80px;">告警条件：</div>
                    <div class="item-con alarm-list" style="margin-left: 80px;margin-top: 11px;font-size: 14px;">
                        <div class="alarm-item" v-for="(item,i) in alarmList" :key="i">
                            <span @click="resiveAlarm(i)" v-if="item.operator !== 'is one of' && item.operator !== 'is not one of' " >{{item.field}} {{item.operator}} <b>{{item.value}}</b></span>
                            <span @click="resiveAlarm(i)" v-else>{{item.field}} {{item.operator}} <b>{{item.values}}</b></span>
                            <i class="el-icon-close" @click="alarmList.splice(i,1)" ></i>
                        </div>
                        <span class="addFilter" @click="addAlarmBtn()"> <i class="el-icon-plus"></i>添加条件</span>
                    </div>
                </div>
                <p class="tip-wapper">提示：告警条件所选字段为下方示例数据的key（即标红字段）</p>
                <p style="color: #409eff;font-weight: 600;font-size: 15px;margin-bottom: 10px;">示例数据</p>
                <jsonView :data="this.JSONData" theme="one-dark" :line-height="20" :deep="5" class="jsonView"></jsonView>
            </div>
            <div class="btn-wapper" style="display: flex;">
                <el-button @click="active = 1" type="primary">上一步</el-button>
                <el-button @click="commitFunc" type="primary" :disabled="(alarmList.length === 0) ? true : false">提交</el-button>
            </div>
        </div>

        <div class="con-wapper" v-show="active === 3">
            <div class="form-con">
                <div class="success-wapper">
                    <div class="icon-wapper">
                        <i class="el-icon-success"></i>
                    </div>
                    <div class="text-wapper">{{this.alarmId === '' ? '添加完成' : '修改完成'}}</div>
                </div>
                <div class="error-wapper"></div>
                <div style="width: 300px;margin: 50px auto;"> <el-button style="width: 100%;" @click="addAgain" v-if="alarmId === ''" type="primary">再次添加</el-button></div>
            </div>
        </div>
        <el-dialog title="编辑告警" :visible.sync="alarmDialog" width="450px">
            <div class="filter-form-wapper" v-loading="alarmLoading"  element-loading-background="rgba(26,36,47, 0.2)">
                <div class="filter-form">
                    <el-form  inline label-width="80px" label-position="top" style="display: flex;">
                        <el-form-item label="Field" style="width:70%;">
                            <el-select v-model="formDialog.field" @change="fieldChange"  placeholder="请选择" filterable default-first-option  class="chooseClass iss"   size="mini">
                                <el-option
                                    v-for="item in fieldOpt"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="Operator">
                            <el-select v-model="formDialog.operator" @change="operatorChange()" filterable allow-create default-first-option  class="chooseClass iss"   size="mini">
                                <el-option
                                    v-for="item in operatorOpt"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-form>
                    <!--&lt;!&ndash;初始显示&ndash;&gt;
                    <el-form ref="form" label-width="80px" label-position="top" v-if="formDialog.operator === '' || formDialog.field === ''">
                        <el-form-item label="Value">
                            <el-input v-model="formDialog.value" size="mini"></el-input>
                        </el-form-item>
                    </el-form>-->
                    <!--单个值-->
                    <el-form ref="form" label-width="80px" label-position="top" v-if=" formDialog.operator !== 'is one of' && formDialog.operator !== 'is not one of'">
                        <!--string-->
                        <el-form-item label="Value" v-if="fieldType[formDialog.field] === 'string'">
                            <el-input v-model="formDialog.value" size="mini"></el-input>
                        </el-form-item>
                        <!--number-->
                        <el-form-item label="Value" v-else-if="fieldType[formDialog.field] === 'number'">
                            <el-input v-model="formDialog.value" type="number" size="mini"></el-input>
                        </el-form-item>
                        <!--ip-->
                        <!--<el-form-item label="Value" v-else-if="fieldType[form.field] === 'ip'">
                            <el-input v-model="form.value" size="mini"></el-input>
                        </el-form-item>
                        &lt;!&ndash;date&ndash;&gt;-->
                        <el-form-item label="Value" v-else-if="fieldType[formDialog.field] === 'date'">
                            <el-date-picker
                                style="width: 100%;"
                                v-model="formDialog.value"
                                type="datetime"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                placeholder="选择日期时间">
                            </el-date-picker>
                        </el-form-item>
                        <!--Boolean-->
                        <el-form-item label="Value" v-else-if="fieldType[formDialog.field] === 'boolean'">
                            <el-select v-model="formDialog.value" placeholder="请选择" style="width: 100%">
                                <el-option label="true" value="true"></el-option>
                                <el-option label="false" value="false"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-form>
                    <!--多个值-->
                    <div v-if="formDialog.field !== '' && ( formDialog.operator === 'is one of' || formDialog.operator === 'is not one of')">
                        <p class="values-tit">Value</p>
                        <div class="values-wapper">
                            <span v-for="(item ,n) in formDialog.values" :key="n">{{item}}<i class="el-icon-close" title="删除" @click="formDialog.values.splice(n,1)"></i></span>
                        </div>
                        <div class="morevalue-form">
                            <el-form style="height: 32px;width: 300px;">
                                <!--string-->
                                <el-form-item v-if="fieldType[formDialog.field] === 'string'">
                                    <el-input v-model="addFromValue" size="mini"></el-input>
                                </el-form-item>
                                <!--number-->
                                <el-form-item v-if="fieldType[formDialog.field] === 'number'">
                                    <el-input v-model="addFromValue" type="number" size="mini"></el-input>
                                </el-form-item>
                                <!--ip-->
                                <el-form-item  v-if="fieldType[formDialog.field] === 'ip'">
                                    <el-input v-model="addFromValue" size="mini"></el-input>
                                </el-form-item>
                                <!--date-->
                                <el-form-item  v-if="fieldType[formDialog.field] === 'date'">
                                    <el-date-picker
                                        style="width: 300px;"
                                        v-model="addFromValue"
                                        type="datetime"
                                        size="mini"
                                        value-format="yyyy-MM-dd HH:mm:ss"
                                        placeholder="选择日期时间">
                                    </el-date-picker>
                                </el-form-item>
                            </el-form>
                            <el-button type="primary" style="background: 0;border-radius:0;" size="mini" @click="addValue()">添加</el-button>
                        </div>

                    </div>

                </div>
                <!--<div class="custom-laber-wapper">
                    <el-form  label-width="82px" style="margin-bottom: 10px;">
                        <el-form-item label="自定义标签" style="margin-bottom: 0">
                            <el-switch v-model="form.label_status"></el-switch>
                        </el-form-item>
                    </el-form>
                    <el-form  label-width="82px" v-if="form.label_status" label-position="left">
                        <el-form-item label="标签名称">
                            <el-input v-model="form.label" size="mini"></el-input>
                        </el-form-item>
                    </el-form>
                </div>-->
                <div slot="footer" class="dialog-footer">
                    <el-button @click="alarmDialog = false">取 消</el-button>
<!--                    <el-button  type="primary" v-if="form.operator === 'exists' || form.operator === 'does not exists'" @click="saveFilter()">确 定</el-button>-->
                    <el-button  type="primary" @click="addAlarm()" :disabled="(formDialog.field === '' || formDialog.operator === '' || (formDialog.value === '' && formDialog.start === '' && formDialog.values.length === 0 && formDialog.end === '') || (formDialog.label_status && formDialog.label === '')) ? 'disabled' : false">确 定</el-button>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import dateLayout from '../common/dateLayout'
    import chooseIndex from '../dashboard/chooseIndex'
    import queryFilter from '../dashboard/queryFilter'
    import jsonView from 'vue-json-views'
    import {setChartParam} from "../../../static/js/common";
    import bus from '../common/bus';
    export default {
        name: "alarmFrom",
        props:{
            formType:{
                type:String,
                default(){
                    return''
                }
            },
            alarmId:{
                type:String,
                default(){
                    return''
                }
            },
            url:{
                type: String,
                default(){
                    return''
                }
            },
            defaultFrom:{
                type:Object
            },
            busNameObj:{
                type:Object,
                default(){
                    return{
                        busIndexName:'',
                        busFilterName:'',
                        busDateName:'',
                    }
                }
            }
        },
        data() {
            return {
                changeOver:false,//用于修改时 标记第一次填充数据完成
                configOpened:['1','2'],
                JSONData:{},
                loading: false,
                alarmLoading:false,
                leftYLoading:false,
                leftXLoading:false,
                active:0,
                indexVal:[],
                defaultVal:{
                    //具体时间参数
                    lastVal:'15-min',
                    //起始时间
                    starttime:'',
                    //结束时间
                    endtime:'',
                    //具体时间 类型状态
                    dateBlock:false,
                    //是否存在轮询框
                    isIntervalBox:false,
                    //轮询状态
                    intervalState:false,
                    //轮询数值间隔
                    intervalVal:'',
                    //轮询参数类型
                    intervalType:'',
                    //‘快速选择’功能参数类型
                    dateUnit:'min',
                    //‘快速选择’功能参数数值
                    dateCount:'15',
                    //‘常用’ 时间值
                    commonlyVal:'',
                    //是否可以切换精确日期
                    changeState:true
                },
                form:{
                    alert_name:'',
                    template_name:'',
                    pre_index_name:'',
                    suffix_index_name:'',
                    alert_exec_type:'simple',//执行周期类型
                    alert_time_cycle_num:'15',//简单 - 执行周期数值
                    alert_time_cycle_type:'second',//简单 - 执行周期数值类型
                    alert_assetGroup_id:'',//资产组id
                    alert_equipment_id:'',//资产id
                    datefield:'',
                    alert_search_filters:'',//过滤条件
                    alert_cron:'',//高级 - 执行周期
                    alert_note:'',//说明
                    alert_search_metric:[],
                    alert_search_bucket:[],
                    alert_conditions:[],//告警条件
                    alert_structure:'',//前端结构
                    alert_time:'15-min',//高级 - 执行周期 - 时间周期
                    alert_time_type : 'last' //高级 - 执行周期 - 时间周期类型
                },
                //参数
                paramObj:{},
                defaultFilter:[],
                //bucket
                xAxisArr:[],
                //metric
                yAxisArr:[],
                //metric聚合类型
                yAggregation: [
                    {
                        value: 'Count',
                        label: 'Count'
                    },{
                        value: 'Sum',
                        label: 'Sum'
                    },{
                        value: 'Average',
                        label: 'Average'
                    },{
                        value: 'Max',
                        label: 'Max'
                    },{
                        value: 'Min',
                        label: 'Min'
                    }
                ],
                //bucket聚合类型
                xAggregation: [
                    {
                        value: 'Terms',
                        label: 'Terms'
                    },
                    {
                        value: 'Range',
                        label: 'Range'
                    },{
                        value: 'Date Histogram',
                        label: 'Date Histogram'
                    },{
                        value: 'Date Range',
                        label: 'Date Range'
                    },{
                        value: 'IPv4 Range',
                        label: 'IPv4 Range'
                    }
                ],
                //资产组
                assetGroupOptions:[],
                //资产
                assetOptions:[],
                //x轴时间间隔
                timeIntervalArr:[
                    /*{
                        value: 'second',
                        label: '秒'
                    },*/
                    {
                        value: 'minute',
                        label: '分钟'
                    },
                    {
                        value: 'hourly',
                        label: '小时'
                    },{
                        value: 'daily',
                        label: '天'
                    },{
                        value: 'weekly',
                        label: '周'
                    },{
                        value: 'monthly',
                        label: '月'
                    },{
                        value: 'yearly',
                        label: '年'
                    }
                ],
                //告警弹窗状态
                alarmDialog:false,
                //弹窗类型
                dialogType:'add',
                //告警列表
                alarmList:[],
                formDialog:{
                    field:'',
                    fieldType:'',
                    operator:'',
                    value:'',
                    start:'',
                    end:'',
                    values:[],
                },
                //field类型   string、number、ip、date
                fieldType:{},
                formQueryVal:'',
                //field值集合
                fieldOpt:[],
                //operator值集合
                operatorOpt:[],
                //多个值临时参数
                addFromValue:'',
                //告警条件索引
                currentIndex:0,
            }
        },
        created(){
            this.getAssetGroup();
            this.getAsset();
            //数据源监听
            bus.$on(this.busNameObj.busIndexName,(arr)=>{
                //还原配置
                this.alarmList = [];
                this.yAxisArr =[];
                this.xAxisArr=[];
                this.form.alert_search_filters = '';
                this.form.alert_search_metric = [];
                this.form.alert_search_bucket = [];
                this.form.alert_conditions = [];
                this.form.alert_structure = '';
                //设置数据源
                this.form.suffix_index_name = arr[2];
                this.form.pre_index_name = arr[1];
                this.form.template_name = arr[0];
                this.form.datefield = arr[3];
                //获取column数据
                //this.getColumnField()
            })
            //监听过滤条件
            bus.$on(this.busNameObj.busFilterName,(str)=>{
                this.form.alert_search_filters = str;

            })
            //时间
            //时间范围监听事件
            bus.$on(this.busNameObj.busDateName,(obj)=>{
                /* if(JSON.stringify(obj) !== '[]'){
                     this.defaultVal = obj;
                     let arr = setChartParam(obj);
                     let dateObj = arr[0];
                     //判断时间类型
                     if(dateObj.last === ''){ //时间范围
                         this.form.alert_time_type = 'range';
                         this.form.alert_time = `${dateObj.starttime},${dateObj.endtime}`;
                     }else{//最近....
                         this.form.alert_time_type = 'last'
                         this.form.alert_time = dateObj.last;
                     }
                 }*/
                this.defaultVal = obj;
                let arr = setChartParam(obj);
                let dateObj = arr[0];
                //判断时间类型
                if(dateObj.last === ''){ //时间范围
                    this.form.alert_time_type = 'range';
                    this.form.alert_time = `${dateObj.starttime},${dateObj.endtime}`;
                }else{//最近....
                    this.form.alert_time_type = 'last'
                    this.form.alert_time = dateObj.last;
                }
            })
        },
        beforeDestroy(){
            bus.$off(this.busNameObj.busIndexName)
            bus.$off(this.busNameObj.busFilterName)
            bus.$off(this.busNameObj.busDateName)
        },
        watch:{
            'defaultFrom'(){
                if(this.alarmId){
                    for(let i in this.form){
                        this.form[i] = this.defaultFrom[i]
                    }
                    if(this.form.alert_search_filters){
                        this.defaultFilter = JSON.parse(this.form.alert_search_filters)
                    }else{
                        this.defaultFilter = []
                    }
                    let strObj = JSON.parse(this.form.alert_structure)

                    this.form.datefield = strObj.dateField
                    this.indexVal = [this.form.template_name,this.form.pre_index_name,this.form.suffix_index_name,this.form.datefield]
                    this.alarmList= JSON.parse(this.form.alert_conditions)
                    this.xAxisArr = strObj.buckets
                    this.yAxisArr = strObj.mertics
                    this.defaultVal = strObj.date
                    setTimeout(()=>{
                        this.changeOver = true;
                    },1500)
                    this.getAssetGroup();
                    this.getAsset(this.form.alert_assetGroup_id);
                }

            },
            //告警条件弹窗状态
            'alarmDialog'(){
                if(!this.alarmDialog){
                    this.formDialog={
                        field:'',
                        fieldType:'',
                        operator:'',
                        value:'',
                        start:'',
                        end:'',
                        values:[],
                    }
                }else{
                    this.getFieldData()
                }
            },
            //数据源
            'form.suffix_index_name'(){
                //清空告警条件
                this.clearAlertCondition()
            },
            //执行周期 类型
            'form.alert_exec_type'(){
                //清空告警条件
                this.clearAlertCondition()
            },
            //执行周期-简单 -数值
            'form.alert_time_cycle_num'(){
                //清空告警条件
                this.clearAlertCondition()
            },
            //执行周期-简单 - 类型
            'form.alert_time_cycle_type'(){
                //清空告警条件
                this.clearAlertCondition()
            },
            //执行周期-高级 - cron
            'form.alert_cron'(){
                //清空告警条件
                this.clearAlertCondition()
            },
            //时间周期
            'form.alert_time'(){
                //清空告警条件
                this.clearAlertCondition()
            },
            //分组
            xAxisArr:{
                handler() {
                    this.$nextTick( ()=> {
                        //清空告警条件
                        this.clearAlertCondition()
                    })
                },
                immediate: false,
                deep: true
            },
            //统计
            yAxisArr:{
                handler() {
                    this.$nextTick( ()=> {
                        //清空告警条件
                        this.clearAlertCondition()
                    })
                },
                immediate: false,
                deep: true
            },
            //向导 索引
            'active'(){
                if(this.active === 0){
                    if(this.alarmId === ''){
                        //清除第1步后面的参数
                        this.alarmList = [];
                       /* this.form.alert_name = '';
                        this.form.alert_cron = '';
                        this.form.alert_note = '';
                        this.form.alert_time = '15-min';
                        this.form.alert_time_type='last';*/
                    }

                }else if(this.active === 1){
                    if(this.alarmId === ''){
                        //清除第2步后面的参数
                        this.alarmList = [];
                        /*this.form.alert_name = '';
                        this.form.alert_cron = '';
                        this.form.alert_note = '';
                        this.form.alert_time = '15-min';
                        this.form.alert_time_type='last';*/
                    }

                }
            }
        },
        computed:{
            /*生成按钮状态*/
            'isCanCreate1'(){
                let dataState = false;
                let yState = false;
                let xState = false;
                //判断数据源
                if(this.form.suffix_index_name === ''){
                    dataState = 'disabled'
                }
                //判断y轴
                this.yAxisArr.forEach((item)=>{
                    if (item.aggregationType === ''){
                        yState = 'disabled';
                    } else if(item.aggregationParam === '' && item.aggregationType !== 'Count'){
                        yState = 'disabled';
                    }
                })
                //判断x轴
                this.xAxisArr.forEach((item)=>{
                    if (item.aggregationType === ''){
                        xState = 'disabled';
                    } else if(item.aggregationParam === '' && item.aggregationType !== 'Count'){
                        xState = 'disabled';
                    }
                })

                if(dataState ==false && yState == false && xState == false){
                    return false;
                }else {
                    return 'disabled';
                }
            }
        },
        methods:{
            /*清空告警条件*/
            clearAlertCondition(){
                //判断是否是修改 还是 添加 页面
                if(this.alarmId){//修改
                    //判断是否是第一次填充数据 引起的改变
                    if(this.changeOver){//是
                        this.alarmList = [];
                        this.form.alert_conditions = [];
                    }
                }else{
                    this.alarmList = [];
                    this.form.alert_conditions = [];
                }
            },
            /*执行时间数据改变*/
            timeCycleNumChange(val){
                this.form.alert_time_cycle_num = Math.floor(val)
                //判断值合法性
                if(this.form.alert_time_cycle_num <= 0){//不合法
                    this.form.alert_time_cycle_num = 1;
                }else{//合法
                    //判断时间类型
                    if(this.form.alert_time_cycle_type === 'second' || this.form.alert_time_cycle_type === 'minute'){//分秒 值不能超过59
                        if(val > 59){
                            this.form.alert_time_cycle_num = 59;
                        }
                    }else{ //时间
                        if(val > 23){
                            this.form.alert_time_cycle_num = 23;
                        }
                    }
                }
            },
            /*执行时间类型改变*/
            timeCycleTypeChange(val){
                //判断时间类型
                if(val === 'second' || val === 'minute'){//分秒 值不能超过59
                    if(this.form.alert_time_cycle_num > 59){
                        this.form.alert_time_cycle_num = 59;
                    }
                }else{ //时间
                    if(this.form.alert_time_cycle_num > 23){
                        this.form.alert_time_cycle_num = 23;
                    }
                }
            },
            /*还原配置*/
            initialize(){
                this.form.alert_name = '';
                this.form.alert_cron = '';
                this.form.alert_note = '';
                this.form.alert_assetGroup_id='';
                this.form.alert_equipment_id='';
                this.form.alert_exec_type='simple';
                this.form.alert_time_cycle_num='' ;
                this.form.alert_time_cycle_type='second';
                this.form.alert_search_filters = '';
                this.form.alert_search_metric = [];
                this.form.alert_search_bucket = [];
                this.form.alert_conditions = [];
                this.form.alert_structure = '';
                this.form.alert_time = '15-min';
                this.form.alert_time_type = 'last';
                this.indexVal = [];
                this.alarmList = [];
                this.yAxisArr =[];
                this.xAxisArr=[];
                this.defaultVal={
                    //具体时间参数
                    lastVal:'15-min',
                    //起始时间
                    starttime:'',
                    //结束时间
                    endtime:'',
                    //具体时间 类型状态
                    dateBlock:false,
                    //是否存在轮询框
                    isIntervalBox:false,
                    //轮询状态
                    intervalState:false,
                    //轮询数值间隔
                    intervalVal:'',
                    //轮询参数类型
                    intervalType:'',
                    //‘快速选择’功能参数类型
                    dateUnit:'min',
                    //‘快速选择’功能参数数值
                    dateCount:'15',
                    //‘常用’ 时间值
                    commonlyVal:'',
                    //是否可以切换精确日期
                    changeState:true
                }
            },
            /*y轴聚合类型改变*/
            yAggregationChange($event,index){
                if (this.form.pre_index_name === '' || this.form.suffix_index_name === '' || this.form.template_name === ''){
                    layer.msg('请选择数据源',{icon:'5'});
                    return;
                }
                //清空聚合字段
                this.yAxisArr[index].aggregationParam = ''
                this.yAxisArr[index].aggregationParamArr = [];
                //获取参数集合
                this.$nextTick(()=>{
                    this.leftYLoading = true;
                    this.$axios.post(this.$baseUrl+'/BI/getFieldByYAxisAggregation.do',this.$qs.stringify(
                        {
                            agg:$event,
                            pre_index_name:this.form.pre_index_name,
                            suffix_index_name:this.form.suffix_index_name,
                            template_name:this.form.template_name
                        }
                    ))
                        .then(res=>{
                            this.leftYLoading = false;
                            res.data.forEach(item=>{
                                let obj = {
                                    value:item.fieldName,
                                    label:item.fieldName
                                }
                                this.yAxisArr[index].aggregationParamArr.push(obj)

                            })
                        })
                        .catch(err=>{
                            this.leftYLoading = false;

                        })
                })
            },
            /*x轴聚合类型改变*/
            xAggregationChange($event,index){
                if (this.form.pre_index_name === '' || this.form.suffix_index_name === '' || this.form.template_name === ''){
                    layer.msg('请选择数据源',{icon:'5'});
                    return;
                }
                //清空聚合字段
                this.xAxisArr[index].aggregationParam = '';
                this.xAxisArr[index].aggregationParamArr = [];
                this.xAxisArr[index].ipRange=[{start:'',end:''}];
                this.xAxisArr[index].numberRange=[{start:'',end:''}];
                this.xAxisArr[index].dateRange=[{start:'',end:''}];

                this.$nextTick(()=>{
                    this.leftXLoading = true;
                    this.$axios.post(this.$baseUrl+'/BI/getFieldByXAxisAggregation.do',this.$qs.stringify({
                        agg:$event,
                        pre_index_name:this.form.pre_index_name,
                        suffix_index_name:this.form.suffix_index_name,
                        template_name:this.form.template_name
                    }))
                        .then(res=>{
                            this.leftXLoading = false;
                            res.data.forEach(item=>{
                                let obj = {
                                    value:item.fieldName,
                                    label:item.fieldName
                                }
                                this.xAxisArr[index].aggregationParamArr.push(obj)
                            })
                        })
                        .catch(err=>{
                            this.leftXLoading = false;

                        })
                })
            },
            /*添加metric*/
            addMetric(){
                if(this.form.suffix_index_name !== ''){
                    this.yAxisArr =[{
                        aggregationType:'',//聚合类型
                        aggregationParam:'',//聚合字段
                        aggregationParamArr:[],//字段集合
                        yAxisName:'',//y轴名称
                        legendName:'',//图例说明
                    }]
                }else{
                    layer.msg('请先选择数据源',{icon:5})
                }

            },
            /*删除metric*/
            deleteMetric(){
                if (this.xAxisArr.length === 1){
                    layer.msg('请先删除分组',{icon:5})
                } else {
                    this.yAxisArr =[]
                }
            },
            /*添加bucket*/
            addBucket(){
                if(this.form.suffix_index_name !== ''){
                    if(this.yAxisArr.length !== 0){
                        this.xAxisArr=[{
                            aggregationType:'',//聚合类型
                            aggregationParam:'',//聚合字段
                            aggregationParamArr:[],//字段集合
                            timeInterval:'1',//时间间隔 聚合字段为时间时
                            timeType:'hourly',
                            xAxisName:'',//x轴名称
                            orderType:'desc',//排序方式
                            topSum:'5',//展示个数
                            numberRange:[ //数值类范围 集合
                                {start:'',end:''}
                            ],
                            dateRange:[//时间类范围 集合
                                {start:'',end:''}
                            ],
                            ipRange:[
                                {start:'',end:''}
                            ]
                        }]
                    }else{
                        layer.msg('请先添加统计',{icon:5})
                    }
                }else{
                    layer.msg('请先选择数据源',{icon:5})
                }
            },
            /*获取资产组*/
            getAssetGroup(){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/assetGroup/getAssetGroupList4Combobox.do',this.$qs.stringify())
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success = 'true'){
                                this.assetGroupOptions = obj.data;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            /*资产组改变事件*/
            assetGroupChange(val){
                this.form.alert_asset = [];
                this.alarmList = [];
                this.form.alert_conditions = [];
                this.getAsset(val);
            },
            /*获取资产*/
            getAsset(asset_group_id){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/assetGroup/getAssetList4Combobox.do',this.$qs.stringify({
                        asset_group_id:asset_group_id
                    }))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success = 'true'){
                                this.assetOptions = obj.data;
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                            this.loading = false;
                        })
                })
            },
            /*资产改变事件*/
            assetChange(){
                this.alarmList = [];
                this.form.alert_conditions = [];
            },
            /*获取字段*/
            getFields(){
                //构建metrics（y）参数 [{aggType:"count",field:"logdate"}]
                let metricsArr = [];
                for(let i in this.yAxisArr){
                    let obj = {};
                    obj.aggType = this.yAxisArr[i].aggregationType;
                    obj.aliasName = this.yAxisArr[i].yAxisName
                    //聚合类型为count的做特殊处理
                    if(obj.aggType === 'Count'){
                        obj.field = this.form.datefield
                    }else{
                        obj.field = this.yAxisArr[i].aggregationParam;
                    }

                    metricsArr.push(obj)
                }
                //构建buckets(x)参数
                let bucketsArr = [];
                for(let i in this.xAxisArr){
                    let obj = {};
                    obj.aggType = this.xAxisArr[i].aggregationType;
                    obj.field = this.xAxisArr[i].aggregationParam;
                    obj.size=this.xAxisArr[i].topSum;
                    obj.sort=this.xAxisArr[i].orderType;
                    obj.intervalType=this.xAxisArr[i].timeType;
                    obj.intervalValue=this.xAxisArr[i].timeInterval;
                    let numberRange = [];
                    let dateRange = [];
                    let ipRange = [];
                    for(let j in this.xAxisArr[i].numberRange){
                        numberRange.push({
                            from:this.xAxisArr[i].numberRange[j].start,
                            to:this.xAxisArr[i].numberRange[j].end
                        })
                    }
                    for(let j in this.xAxisArr[i].dateRange){
                        dateRange.push({
                            from:this.xAxisArr[i].dateRange[j].start,
                            to:this.xAxisArr[i].dateRange[j].end
                        })
                    }
                    for(let j in this.xAxisArr[i].ipRange){
                        ipRange.push({
                            from:this.xAxisArr[i].ipRange[j].start,
                            to:this.xAxisArr[i].ipRange[j].end
                        })
                    }
                    if(obj.aggType === 'Range'){
                        obj.ranges = numberRange
                    }else if(obj.aggType === 'IPv4 Range'){
                        obj.ranges = ipRange
                    }else if(obj.aggType === 'Date Range'){
                        obj.ranges = dateRange
                    }else{
                        obj.ranges = [];
                    }
                    bucketsArr.push(obj)
                }

                //this.paramObj = JSON.parse(JSON.stringify(this.form));
                this.form.alert_search_metric = JSON.stringify(metricsArr)
                this.form.alert_search_bucket = JSON.stringify(bucketsArr)
                if(this.form.alert_search_filters === ''){
                    this.form.alert_search_filters = []
                }
                //时间
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/alert/getAlertResult.do',this.$qs.stringify(this.form))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data
                            if (obj.success === 'true'){
                                this.active = 2;
                                this.JSONData = obj.data
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            /*添加告警按钮*/
            addAlarmBtn(){
                this.alarmDialog = true;
                this.dialogType = 'add';
            },
            /*添加告警*/
            addAlarm(){
                let obj = {};
                let str = JSON.stringify(this.formDialog);
                obj = JSON.parse(str);
                if(this.dialogType === 'resive'){
                    this.alarmList[this.currentIndex] = obj
                }else{
                    this.alarmList.push(obj);
                }
                this.alarmDialog = false;
            },
            /*修改告警*/
            resiveAlarm(i){
                this.currentIndex = i;
                this.formDialog =JSON.parse(JSON.stringify(this.alarmList[i]));
                this.dialogType = 'resive';
                this.alarmDialog = true;
            },
            /*获取field数据*/
            getFieldData(val){
                this.$nextTick(()=>{
                    this.alarmLoading = true;
                    let params = {};
                    //判断参数类型
                    if(this.form.alert_search_bucket === '[]' && this.form.alert_search_metric === '[]'){
                        params.aggType = 'All'
                    }else{
                        params.aggType = 'alertAgg'
                    }
                    params.template_name = this.form.template_name;
                    this.$axios.post(this.$baseUrl+'/alert/getFieldByAggResult.do',this.$qs.stringify(params))
                        .then(res=>{
                            this.alarmLoading = false;
                            //清空数据
                            this.fieldOpt = [];
                            this.fieldType = {};
                            //循环push
                            let arr = res.data;
                            arr.forEach(item =>{
                                this.fieldOpt.push({
                                    value:item.fieldName,
                                    label:item.fieldName
                                })
                                //判断类型
                                if(item.fieldType === 'long' || item.fieldType === 'integer' || item.fieldType === 'short' || item.fieldType === 'byte' || item.fieldType === 'double' || item.fieldType === 'float' || item.fieldType === 'half_float' || item.fieldType === 'scaled_float'){
                                    this.fieldType[item.fieldName] = 'number'
                                }else if(item.fieldType === 'boolean'){
                                    this.fieldType[item.fieldName] = 'boolean'
                                }else if(item.fieldType === 'text' || item.fieldType === 'keyword'){
                                    this.fieldType[item.fieldName] = 'string'
                                }else if(item.fieldType === 'ip'){
                                    this.fieldType[item.fieldName] = 'ip'
                                }else if(item.fieldType === 'date'){
                                    this.fieldType[item.fieldName] = 'date'
                                }
                            })
                        })
                        .catch(err=>{
                            this.alarmLoading = false;
                        })
                })
            },
            /*获取operator数据*/
            getOperatorData(field){
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+'/alert/getOperatorByFiledType.do',this.$qs.stringify({
                        fieldType:this.fieldType[field]
                    }))
                        .then(res=>{
                            this.loading = false;
                            this.operatorOpt = [];
                            res.data.forEach(item =>{
                                this.operatorOpt.push({
                                    value:item,
                                    label:item
                                })
                            })
                        })
                        .catch(err=>{
                            this.loading = false;
                        })
                })
            },
            /*field改变事件*/
            fieldChange(val){
                //清空值
                this.formDialog.value = '';
                this.formDialog.start = '';
                this.formDialog.end = '';
                this.formDialog.values = [];
                this.addFromValue = '';
                this.formDialog.operator = '';
                this.formDialog.fieldType = this.fieldType[val];
                //获取operator集合
                this.getOperatorData(val)
            },
            /*operator改变事件*/
            operatorChange(){
                //清空值
                this.formDialog.value = '';
                this.formDialog.start = '';
                this.formDialog.end = '';
                this.formDialog.values = [];
                this.addFromValue = '';
            },
            /*添加按钮*/
            addValue(){
                //判断值是否存在/是否合法
                if(this.addFromValue && !this.formDialog.values.includes(this.addFromValue)){
                    /*//判断ip类型
                    if(this.fieldType[this.form.field] === 'ip'){
                        //检查ip合法性
                        if(!this.checkIp(this.addFromValue)){
                            layer.msg('IP不合法',{icon:5});
                            return;
                        }
                    }*/
                    this.formDialog.values.push(this.addFromValue)
                    this.addFromValue = '';
                }
            },
            /*提交保存*/
            commitFunc(){
                let paramsObj = JSON.parse(JSON.stringify(this.form))
                //判断是否是修改
                if(this.alarmId !== ''){//修改
                    paramsObj.alert_id = this.alarmId
                }else{//添加

                }
                paramsObj.alert_conditions = JSON.stringify(this.alarmList)
                let obj = {};
                obj.buckets = this.xAxisArr;
                obj.mertics = this.yAxisArr;
                obj.date = this.defaultVal;
                obj.dateField=this.form.datefield
                paramsObj.alert_structure = JSON.stringify(obj)
                //拼接参数
                this.$nextTick(()=>{
                    this.loading = true;
                    this.$axios.post(this.$baseUrl+this.url,this.$qs.stringify(paramsObj))
                        .then(res=>{
                            this.loading = false;
                            let obj = res.data;
                            if(obj.success === 'true'){
                               /!* layer.msg(obj.message,{1})*!/
                                this.active = 3
                            }else{
                                layer.msg(obj.message,{icon:5})
                            }
                        })
                        .catch(err=>{
                             this.loading = false;
                        })
                })
            },
            /*再次添加*/
            addAgain(){
                this.initialize();
                this.active = 0;
               /* this.indexVal=[];
                this.form.template_name='';
                this.form.pre_index_name='';
                this.form.suffix_index_name='';*/
            }
        },
        components:{
            dateLayout,
            chooseIndex,
            queryFilter,
            jsonView
        }
    }
</script>

<style scoped>
    .con-wapper{
        height: 500px;
        padding: 20px;
        padding-top: 40px;
    }
    .form-con{
        height: 450px;
        overflow: auto;
        border: 1px dashed #3a4a5d;
        padding: 20px;
        padding-left: 50px;
    }
    .btn-wapper{
        width: 100%;
        margin-top: 5px;
    }
    .btn-wapper button{
        width: 100%;
        margin-top: 10px;
    }
    .form-wapper>div{
        position: relative;
    }
    .form-item{
        display: flex;
        margin-bottom: 20px;
        align-items: center;
        position: relative;
    }
    .item-label{
        color: #409eff;
        font-size: 16px;
        width: 56px;
        text-align: end;

    }
    .item-con{
        width: calc(100% - 60px);
    }
    .form-item /deep/ .chooseClass .el-input__inner{
        background: #303e4e;
    }
    .mustWrite{
        color: red;
        position: absolute;
        top: 2px;
        left: -2px;
    }
    .form-item /deep/ .el-collapse-item__header{
        height: 34px;
        background: #3a4a5d;
        border-top: 1px solid #409eff;
        color: #409eff;
        border-radius: 5px;
        position: relative;
        padding-left: 30px;
    }
    .form-item /deep/ .el-form-item__label{
        color: #FFF!important;
        font-size: 12px;
    }
    .form-item /deep/ .el-collapse-item__wrap{
        /*background: 0;*/
        padding: 10px;
        border-radius: 5px;

    }
    .tablist{
        width: 320px;
    }
    .tablist .removeTab{
        position: absolute;
        right: 10px;
        top: 12px;
    }
    .tablist .removeTab:hover{
        color: #e4956d;
    }
    .tablist /deep/ .el-collapse-item__arrow{
        position: absolute;
        left: 10px;
        top: 12px;
    }
    .addWapper{
        height: 28px;
        font-size: 13px;
        line-height: 38px;
        padding-left: 10px;
    }
    .addWapper:hover {
        cursor: pointer;
        text-decoration: underline;
        color: #409eff;
    }
    .range-p {
        display: -webkit-box;
        display: -ms-flexbox;
        display: flex;
        -webkit-box-align: center;
        -ms-flex-align: center;
        align-items: center;
    }
    .range-btn-p{
        text-align: center;
        margin-top: 5px;
    }
    .range-btn-p span:hover{
        cursor: pointer;
        color: #409eff;
    }
    /deep/ .el-form-item--mini.el-form-item, .el-form-item--small.el-form-item {
        margin-bottom: 5px;
    }
    .alarm-list{
        display: flex;
        flex-wrap: wrap;
    }
    .alarm-item{
        padding:3px 5px;
        border: 1px solid #48617d;
        font-size: 12px;
        margin: 5px;
        margin-left: 0;
        color: #a0cfff;
        margin-top: 0;
    }
    .alarm-item span:hover{
        cursor: pointer;
        text-decoration: underline;
    }
    .alarm-item .el-icon-view{
        margin-left: 3px;
    }
    .alarm-item .el-icon-view:hover{
        color: #32ff1d;
    }
    .alarm-item i:hover{
        cursor: pointer;
    }
    .addFilter:hover{
        cursor: pointer;
        text-decoration: underline;
        color: #409eff;
    }
    .filter-form{
        padding: 10px 0;
        border-top: 1px solid #3c5d80;
        border-bottom: 1px solid #3c5d80;
    }
    .filter-btn{
        height: 50px;
        display: flex;
        justify-content: flex-end;
        align-items: center;
    }
    .between-wapper /deep/ .el-form-item__content{
        display: flex;
    }
    .between-wapper span{
        margin: 0 5px;
        color: #fff;
    }
    .values-tit{
        color: #e4956d;
        font-weight: 600;
        padding: 0;
    }
    .morevalue-form{
        display: flex;
        align-items: center;
        justify-content: space-between;
    }
    .values-wapper{
        margin: 5px 0;
        display: flex;
        flex-wrap: wrap;
        /*border: 1px solid #409eff;*/
    }
    .values-wapper span{
        border: 1px dashed #3c5d80;
        padding: 4px 5px;
        color: #409eff;
        font-size: 10px;
        margin: 5px;
        border-left: 2px solid #409eff;
    }
    .values-wapper span i{
        margin-left: 3px;
        cursor: pointer;
    }
    .values-wapper span i:hover{
        color: #e4956d;
    }
    .dialog-footer{
        margin-top: 10px;
        /* text-align: end; */
        display: flex;
        justify-content: space-around;
    }
    .custom-laber-wapper{
        border-bottom: 1px solid #3c5d80;
        padding: 10px 0;
    }
    .custom-laber-wapper /deep/.el-form--label-top .el-form-item__label{
        color: #fff!important;
    }
    .tip-wapper{
        margin-bottom: 10px;
        color: #e4956d;
    }
    .success-wapper{
        margin-top: 70px;
    }
    .success-wapper>div{
        text-align: center;
        color: #1ab394;
    }
    .success-wapper .icon-wapper{
        font-size: 50px;
    }
    .success-wapper .text-wapper{
        font-size: 50px;
    }
    .emptyClass /deep/ .el-input--mini .el-input__inner{
        color: #7b8a9a;
    }
</style>
