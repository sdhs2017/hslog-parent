<template>
    <div v-loading="allLoading"  element-loading-background="rgba(26,36,47, 0.2)">
        <div class="top-title" style="padding-left: 10px;"><!--{{htmlTitle}}-->
            <div class="top-zz" v-if="operType === 'see'"></div>
            <div class="choose-wapper">
                <choose-index :busName="this.busIndexName" :arr = "indexVal"></choose-index>
            </div>
            <el-button class="saveChart" type="success" plain @click="dialogFormVisible = true"  size="mini">保存</el-button>
            <el-button class="update-btn" v-if="updateBtn && isCanCreate !== 'disabled'"  type="success" size="mini"  @click="updateChart" style="float: right;margin-right: 5px;margin-top: 10px;position: relative;z-index: 101;">更新</el-button>
            <el-button type="primary" v-else size="mini" plain @click="refreshChart" style="float: right;margin-right: 5px;margin-top: 10px;position: relative;z-index: 101;">刷新</el-button>
            <div class="date-wapper"><date-layout :busName="busName" :defaultVal="defaultVal" :refresh="refresh"></date-layout></div>
        </div>
        <div class="filter-box">
            <queryFilter
                :busFilterName="this.busFilterName"
                :busQueryName="this.busQueryName"
                :filterArr="this.defaultFilter"
                :queryVal="this.defaultQuery"
                :useType="this.operType"
                useObject="chart"
                :templateName="this.chartsConfig.templateName"
                :preIndexName="this.chartsConfig.preIndexName"
                :suffixIndexName="this.chartsConfig.suffixIndexName"
            >
            </queryFilter>
        </div>
        <div class="chart-wapper">
            <div class="config-wapper">
                <!--                <el-button class="creatBtn prevBtn" :disabled="this.chartsConfigArr.length <= 1 ? 'disabled' : false" type="primary" @click="prevCreate" title="上一步"><i class="el-icon-back"></i></el-button>-->
                <!--                <el-button class="creatBtn nextBtn" :disabled="this.chartsConfigArr.length <= 1 ? 'disabled' : false" type="primary" @click="prevCreate" title="下一步"><i class="el-icon-right"></i></el-button>-->
                <el-button class="creatBtn" type="primary" @click="createBtn" :disabled="isCanCreate || operType === 'see'">生成</el-button>
                <el-tabs v-model="activeName" style="height: 100%;" type="border-card"  v-loading="leftLoading"  element-loading-background="rgba(26,36,47, 0.2)">
                    <el-tab-pane label="数据" name="first">
                        <el-collapse>
                            <el-collapse-item class="tablist" v-for="(yItem,i) in chartsConfig.yAxisArr" :key="i">
                                <template slot="title" class="collapseTit">
                                    {{ chartType !=='pie' && chartType !=='metric' ? 'Y轴' : '指标'}} {{yItem.legendName}}<i class="header-icon el-icon-error removeTab" @click="removeYaxisTab(i,$event)" v-if="operType !== 'see' && chartsConfig.yAxisArr.length !== 1"></i>
                                </template>
                                <el-form label-position="top" style="position: relative;">
                                    <div class="from-zz" v-if="operType === 'see'"></div>
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
                                    <el-form-item label="图例名称" v-if="chartType !=='pie'">
                                        <el-input v-model="yItem.yAxisName" size="mini"></el-input>
                                    </el-form-item>
                                    <!--<el-form-item label="颜色类型">
                                        <el-select v-model="yItem.colorType" placeholder="请选择" style="width: 100%;" size="mini">
                                            <el-option
                                                v-for="item in colorTypeArr"
                                                :key="item.value"
                                                :label="item.label"
                                                :value="item.value">
                                            </el-option>
                                        </el-select>
                                    </el-form-item>-->
                                    <!-- <el-form-item label="颜色选择">
                                         <el-color-picker v-model="yItem.color1" show-alpha v-if="yItem.colorType === 'solidColor'"></el-color-picker>
                                         <p style="display: flex;" v-else>
                                             <span>0:</span><el-color-picker v-model="yItem.color2[0]" show-alpha></el-color-picker>
                                             <span>1:</span><el-color-picker v-model="yItem.color2[1]" show-alpha></el-color-picker>
                                         </p>
                                     </el-form-item>-->
                                </el-form>
                            </el-collapse-item>
                            <p style="text-align: center;font-size: 12px;margin-bottom: 10px;" v-if="operType !== 'see' && chartType !== 'pie'"><span class="addY" @click="addY"> <i class="el-icon-circle-plus"></i> {{chartType === 'metric' ? '添加指标' :'添加Y轴'}}</span></p>
                        </el-collapse>
                        <el-collapse>
                            <el-collapse-item class="tablist" v-for="(xItem,i) in chartsConfig.xAxisArr" :key="i">
                                <template slot="title" class="collapseTit">
                                    {{i === 0 && chartType !=='pie' ? 'X轴' : '拆分序列'}} <i class="header-icon el-icon-error removeTab" v-if="(operType === 'see') ? 'false' : (i === 0) ? (chartType === 'metric') ? 'true' :'false' : 'true'" @click="removeXaxisTab(i,$event)"></i>
                                </template>
                                <el-form label-position="top" style="position: relative">
                                    <div class="from-zz" v-if="operType === 'see'"></div>
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
                                    <el-form-item label="名称" v-if="i === 0 && chartType !=='pie'">
                                        <el-input v-model="xItem.xAxisName" size="mini"></el-input>
                                    </el-form-item>
                                </el-form>
                            </el-collapse-item>
                            <p style="text-align: center;font-size: 12px;margin-bottom: 10px;" v-if="operType !== 'see' "><span class="addY" @click="addX"> <i class="el-icon-circle-plus"></i> 添加拆分序列</span></p>
                        </el-collapse>
                    </el-tab-pane>
                    <el-tab-pane label="基本设定" v-if="chartType === 'metric'" name="second">
                        <el-collapse v-model="configOpened">
                            <el-collapse-item title="标题" class="tablist" name="1">
                                <el-form label-position="left" label-width="50px" style="position:relative;">
                                    <div class="from-zz" v-if="operType === 'see'"></div>
                                    <el-form-item label="标题">
                                        <el-input v-model="chartsConfig.title.text" size="mini"></el-input>
                                    </el-form-item>
                                </el-form>
                            </el-collapse-item>
                            <el-collapse-item title="样式" class="tablist" name="2">
                                <el-form label-position="left" label-width="50px" style="position:relative;">
                                    <div class="from-zz" v-if="operType === 'see'"></div>
                                    <el-form-item label="颜色">
                                        <el-color-picker v-model="chartsConfig.style.color" size="mini"></el-color-picker>
                                    </el-form-item>
                                    <el-form-item label="大小">
                                        <el-slider v-model="chartsConfig.style.fontSize" :min="12" :max="120" size="mini"></el-slider>
                                    </el-form-item>
                                </el-form>
                            </el-collapse-item>
                        </el-collapse>
                    </el-tab-pane>
                    <el-tab-pane label="基本设定" v-else name="second">
                        <div class="config-item">
                            <el-collapse v-model="configOpened">
                                <el-collapse-item title="标题" class="tablist" name="1">
                                    <el-form label-position="left" label-width="50px" style="position:relative;">
                                        <div class="from-zz" v-if="operType === 'see'"></div>
                                        <el-form-item label="标题">
                                            <el-input v-model="chartsConfig.title.text" size="mini"></el-input>
                                        </el-form-item>
                                    </el-form>
                                </el-collapse-item>
                                <!--        <el-collapse-item title="形式" class="tablist" name="9" v-if="this.chartType === 'pie'">
                                            <el-form label-position="right" label-width="90px" style="position: relative;">
                                                <div class="from-zz" v-if="operType === 'see'"></div>
                                                <el-form-item label="环形显示">
                                                    <el-switch v-model="chartsConfig.raduis.show"></el-switch>
                                                </el-form-item>
                                                <el-form-item label="内环大小" v-if="chartsConfig.raduis.show">
                                                    <el-input v-model="chartsConfig.raduis.raduisArr[0]" size="mini"></el-input>
                                                </el-form-item>
                                                <el-form-item label="外环大小" v-if="chartsConfig.raduis.show">
                                                    <el-input v-model="chartsConfig.raduis.raduisArr[1]" size="mini"></el-input>
                                                </el-form-item>
                                                <p class="tip-w"  style="margin-bottom: 20px;" v-if="chartsConfig.raduis.show">数值允许：'10'（px）、'10%'两种</p>
                                                <el-form-item label="南丁格尔图">
                                                    <el-switch v-model="chartsConfig.roseType.show"></el-switch>
                                                </el-form-item>
                                                <el-form-item label="方式" v-if="chartsConfig.roseType.show">
                                                    <el-switch
                                                        v-model="chartsConfig.roseType.type"
                                                        active-value="radius"
                                                        inactive-value="area"
                                                        active-text="radius"
                                                        inactive-text="area">
                                                    </el-switch>
                                                </el-form-item>
                                                <p class="tip-w"  v-if="chartsConfig.roseType.show">'radius' 扇区圆心角展现数据的百分比，半径展现数据的大小。'area' 所有扇区圆心角相同，仅通过半径展现数据大小。</p>
                                            </el-form>
                                        </el-collapse-item>-->
                                <el-collapse-item title="图形" class="tablist" name="8" v-if="this.chartType === 'bar'">
                                    <el-form label-position="left" label-width="80px" style="position: relative">
                                        <div class="from-zz" v-if="operType === 'see'"></div>
                                        <el-form-item label="标签显示">
                                            <el-switch v-model="chartsConfig.graph.label.show"></el-switch>
                                        </el-form-item>
                                        <!--<el-form-item label="填充类型">
                                            <el-select v-model="chartsConfig.graph.colorType" placeholder="请选择" style="width: 100%;" size="mini">
                                                <el-option
                                                    v-for="item in colorTypeArr"
                                                    :key="item.value"
                                                    :label="item.label"
                                                    :value="item.value">
                                                </el-option>
                                            </el-select>
                                        </el-form-item>-->
                                    </el-form>
                                </el-collapse-item>
                                <el-collapse-item title="图形" class="tablist" name="8" v-if="this.chartType === 'line'">
                                    <el-form label-position="left" label-width="80px" style="position: relative">
                                        <div class="from-zz" v-if="operType === 'see'"></div>
                                        <el-form-item label="标签显示">
                                            <el-switch v-model="chartsConfig.graph.label.show"></el-switch>
                                        </el-form-item>
                                        <el-form-item label="区域填充">
                                            <el-switch
                                                v-model="chartsConfig.graph.areaShow"
                                                active-value="1"
                                                inactive-value="0"
                                            >
                                            </el-switch>
                                        </el-form-item>
                                        <!--<el-form-item label="填充类型" v-if="chartsConfig.graph.areaShow === '1'">
                                            <el-select v-model="chartsConfig.graph.colorType" placeholder="请选择" style="width: 100%;" size="mini">
                                                <el-option
                                                    v-for="item in colorTypeArr"
                                                    :key="item.value"
                                                    :label="item.label"
                                                    :value="item.value">
                                                </el-option>
                                            </el-select>
                                        </el-form-item>-->
                                    </el-form>
                                </el-collapse-item>
                                <el-collapse-item title="X轴" class="tablist" name="5"  v-if="this.chartType !== 'pie'">
                                    <el-form label-position="left" label-width="80px" style="position: relative">
                                        <div class="from-zz" v-if="operType === 'see'"></div>
                                        <el-form-item label="名称颜色">
                                            <el-color-picker v-model="chartsConfig.xNormal.nameTextStyle.color" size="mini"></el-color-picker>
                                        </el-form-item>
                                        <el-form-item label="轴线颜色">
                                            <el-color-picker v-model="chartsConfig.xNormal.axisLine.lineStyle.color" size="mini"></el-color-picker>
                                        </el-form-item>
                                        <el-form-item label="刻度间隔">
                                            <el-input v-model="chartsConfig.xNormal.axisLabel.interval" size="mini"></el-input>
                                        </el-form-item>
                                        <p class="tip-w" style="margin-bottom: 15px;">坐标轴刻度标签的显示间隔,'auto'为默认不重叠，设置成 0 强制显示所有标签,设置为 1，表示『隔一个标签显示一个标签』,依次类推。</p>
                                        <el-form-item label="刻度角度">
                                            <el-input v-model="chartsConfig.xNormal.axisLabel.rotate" size="mini"></el-input>
                                        </el-form-item>
                                        <p class="tip-w" style="margin-bottom: 15px;">旋转的角度从 -90 度到 90 度。</p>
                                        <el-form-item label="文本颜色">
                                            <el-color-picker v-model="chartsConfig.xNormal.axisLabel.color" size="mini"></el-color-picker>
                                        </el-form-item>
                                        <el-form-item label="文本大小">
                                            <el-input v-model="chartsConfig.xNormal.axisLabel.fontSize" size="mini"></el-input>
                                        </el-form-item>
                                    </el-form>
                                </el-collapse-item>
                                <el-collapse-item title="Y轴" class="tablist" name="6"  v-if="this.chartType !== 'pie'">
                                    <el-form label-position="left" label-width="85px" style="position: relative;">
                                        <div class="from-zz" v-if="operType === 'see'"></div>
                                        <el-form-item label="名称">
                                            <el-input v-model="chartsConfig.yNormal.name" size="mini"></el-input>
                                        </el-form-item>
                                        <el-form-item label="名称颜色">
                                            <el-color-picker v-model="chartsConfig.yNormal.nameTextStyle.color" size="mini"></el-color-picker>
                                        </el-form-item>
                                        <el-form-item label="单位类型" >
                                            <el-select v-model="chartsConfig.yNormal.unitType" style="width: 100%;" size="mini">
                                                <el-option label="无" value=""></el-option>
                                                <el-option label="百分比" value="%"></el-option>
                                                <el-option label="byte" value="byte"></el-option>
                                            </el-select>
                                        </el-form-item>
                                        <el-form-item label="单位数值" v-if="chartsConfig.yNormal.unitType === 'byte'">
                                            <el-select v-model="chartsConfig.yNormal.unitValue" style="width: 100%;" size="mini">
                                                <el-option label="MB" value="MB"></el-option>
                                                <el-option label="GB" value="GB"></el-option>
                                                <el-option label="TB" value="TB"></el-option>
                                            </el-select>
                                        </el-form-item>
                                        <el-form-item label="轴线颜色">
                                            <el-color-picker v-model="chartsConfig.yNormal.axisLine.lineStyle.color" size="mini"></el-color-picker>
                                        </el-form-item>
                                        <el-form-item label="文本颜色">
                                            <el-color-picker v-model="chartsConfig.yNormal.axisLabel.color" size="mini"></el-color-picker>
                                        </el-form-item>
                                        <el-form-item label="文本大小">
                                            <el-input v-model="chartsConfig.yNormal.axisLabel.fontSize" size="mini"></el-input>
                                        </el-form-item>
                                        <el-form-item label="分割线">
                                            <el-switch v-model="chartsConfig.yNormal.splitLine.show" size="mini"></el-switch>
                                        </el-form-item>
                                        <el-form-item label="分割线颜色" v-if="chartsConfig.yNormal.splitLine.show">
                                            <el-color-picker v-model="chartsConfig.yNormal.splitLine.lineStyle.color" size="mini"></el-color-picker>
                                        </el-form-item>
                                        <el-form-item label="分割线样式" v-if="chartsConfig.yNormal.splitLine.show">
                                            <el-select v-model="chartsConfig.yNormal.splitLine.lineStyle.type" style="width: 100%;" size="mini">
                                                <el-option label="solid" value="solid"></el-option>
                                                <el-option label="dashed" value="dashed"></el-option>
                                                <el-option label="dotted" value="dotted"></el-option>
                                            </el-select>
                                        </el-form-item>
                                        <el-form-item label="分割线透明" v-if="chartsConfig.yNormal.splitLine.show">
                                            <el-input v-model="chartsConfig.yNormal.splitLine.lineStyle.opacity" size="mini"></el-input>
                                        </el-form-item>
                                        <p class="tip-w" v-if="chartsConfig.yNormal.splitLine.show">支持从 0 到 1 的数字，为 0 时不绘制分割线。</p>
                                    </el-form>
                                </el-collapse-item>
                                <el-collapse-item title="图例" class="tablist" name="2">
                                    <el-form label-position="left" label-width="80px" style="position: relative;">
                                        <div class="from-zz" v-if="operType === 'see'"></div>
                                        <el-form-item label="是否显示">
                                            <el-switch v-model="chartsConfig.legend.show"></el-switch>
                                        </el-form-item>
                                        <el-form-item label="左边距" v-if="chartsConfig.legend.show">
                                            <el-input v-model="chartsConfig.legend.left" size="mini"></el-input>
                                        </el-form-item>
                                        <el-form-item label="上边距" v-if="chartsConfig.legend.show">
                                            <el-input v-model="chartsConfig.legend.top" size="mini"></el-input>
                                        </el-form-item>
                                        <el-form-item label="文本颜色" v-if="chartsConfig.legend.show">
                                            <el-color-picker v-model="chartsConfig.legend.textStyle.color"></el-color-picker>
                                        </el-form-item>
                                        <p class="tip-w">边距值可选类型<br/>1:'center‘、'left‘、'top‘、'bottom‘;<br/>2:'10‘，单位为像素；<br/>3:'10%‘,百分比</p>
                                    </el-form>
                                </el-collapse-item>
                                <el-collapse-item title="工具栏" class="tablist" name="3">
                                    <el-form label-position="left" label-width="80px" style="position: relative;">
                                        <div class="from-zz" v-if="operType === 'see'"></div>
                                        <el-form-item label="是否显示">
                                            <el-switch v-model="chartsConfig.toolbox.show"></el-switch>
                                        </el-form-item>
                                        <el-form-item label="工具选择" v-if="chartsConfig.toolbox.show">
                                            <el-checkbox-group v-model="chartsConfig.toolbox.feature">
                                                <el-checkbox label="dataView" name="type">数据视图</el-checkbox>
                                                <el-checkbox label="magicType" name="type" v-if="chartType !=='pie'">类型切换</el-checkbox>
                                                <!--                                                <el-checkbox label="dataZoom" name="type">区域缩放</el-checkbox>-->
                                                <el-checkbox label="restore" name="type" v-if="chartType !=='pie'">还原</el-checkbox>
                                                <el-checkbox label="saveAsImage" name="type">保存</el-checkbox>
                                            </el-checkbox-group>
                                        </el-form-item>
                                    </el-form>
                                </el-collapse-item>
                                <el-collapse-item title="边距" class="tablist" name="4" style="position: relative;" v-if="chartType !== 'pie'">
                                    <div class="from-zz" v-if="operType === 'see'"></div>
                                    <el-form label-position="left" label-width="80px">
                                        <el-form-item label="左边距">
                                            <el-input v-model="chartsConfig.grid.left" size="mini"></el-input>
                                        </el-form-item>
                                        <el-form-item label="右边距">
                                            <el-input v-model="chartsConfig.grid.right" size="mini"></el-input>
                                        </el-form-item>
                                        <el-form-item label="上边距">
                                            <el-input v-model="chartsConfig.grid.top" size="mini"></el-input>
                                        </el-form-item>
                                        <el-form-item label="下边距">
                                            <el-input v-model="chartsConfig.grid.bottom" size="mini"></el-input>
                                        </el-form-item>
                                    </el-form>
                                    <p class="tip-w">边距值可选类型<br/>1:'center‘、'left‘、'top‘、'bottom‘;<br/>2:'10‘，单位为像素；<br/>3:'10%‘,百分比</p>
                                </el-collapse-item>
                            </el-collapse>
                        </div>
                    </el-tab-pane>

                </el-tabs>
            </div>
            <div class="view-wapper"  v-loading="loading"  element-loading-background="rgba(48, 62, 78, 0.5)">
                <div class="charts-title">{{chartsConfig.title.text}}</div>
                <div id="charts-wapper"></div>
                <div class="empty-tip" v-if="this.emptyTipState">暂无结果</div>
                <el-popover
                    v-if="!this.emptyTipState"
                    class="source-data"
                    placement="left"
                    title=""
                    width="400"
                    trigger="click"
                >
                    <jsonView :data="this.sourceData" theme="one-dark" :line-height="20" :deep="5" class="jsonView"></jsonView>
                    <el-button type="primary" plain size="mini" slot="reference" >源数据</el-button>
                </el-popover>
            </div>
        </div>
        <el-dialog title="保存" :visible.sync="dialogFormVisible" width="400px">
            <el-form>
                <el-form-item label="名称">
                    <el-input v-model="chartParams.chartName"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="chartParams.chartDes"></el-input>
                </el-form-item>
                <el-form-item v-if="chartId !== ''">
                    <el-switch
                        v-model="chartParams.otherSave"
                        active-text="另存为新图表">
                    </el-switch>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveChart" :disabled="chartParams.chartName === '' ? 'disabled' : false">确 定</el-button>
            </div>
        </el-dialog>
    </div>

</template>

<script>
    import dateLayout from '../common/dateLayout'
    import {setChartParam} from "../../../static/js/common";
    import bus from '../common/bus';
    import jsonView from 'vue-json-views'
    import queryFilter from '../dashboard/queryFilter'
    import chooseIndex from '../dashboard/chooseIndex'
    const echarts = require('echarts');
    export default {
        name: "chartBaseConfig",
        props:{
            //操作类型
            operType:{
                type:String,
                defaultVal() {
                    return 'edit'
                }
            },
            //图表类型
            chartType:{
                type:String
            },
            //图表id
            chartId:{
                type:String
            }
        },
        data() {
            return {
                refresh:0,
                allLoading:false,
                leftLoading:false,
                loading:false,
                //时间控件参数 柱状图
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
                    isIntervalBox:true,
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
                //保存图表的弹窗状态
                dialogFormVisible:false,
                //保存图表表单参数
                chartParams:{
                    //图表名称
                    chartName:'',
                    //图表描述
                    chartDes:'',
                    //查询条件
                    searchParam:'',
                    //另存
                    otherSave:false
                },
                //echarts结构
                opt:{},
                //时间范围
                dateObj:{
                    starttime:'',//起始时间
                    endtime:'',//结束时间
                    last:'15-min',
                },
                //轮询参数
                intervalObj:{
                    state:false,
                    interval:'5000'
                },
                //轮询
                interval:'',
                //返回的源数据
                sourceData:'',
                //数据为空时提示状态
                emptyTipState:true,
                //默认配置 用于还原原始配置
                defaultConfig:'',
                //bus监听事件的名称
                busName:'createBarChart',
                busIndexName:'barIndexName',
                busFilterName:'',
                busQueryName:'',
                //默认数据源索引
                indexVal:[],
                //展开的选项卡
                configOpened:['1','2','3','4','5','6','7','8','9'],
                yUnit :'',
                //图表基本配置
                chartsConfig:{
                    //templateName
                    templateName:'',
                    //index前名
                    preIndexName:'',
                    //index后时间
                    suffixIndexName:'',
                    //datefield
                    datefield:'',
                    //标题
                    title:{
                        text:''
                    },
                    //指标类图 样式
                    style:{
                        color:'#409EFF',
                        fontSize:100,
                    },
                    //图例
                    legend:{
                        show:true,
                        type:'scroll',
                        pageTextStyle:{
                            color:'#eee'
                        },
                        left:'center',
                        top:'20',
                        textStyle:{
                            color:'#eee'
                        },
                        pageIconColor:'#fff'
                    },
                    //南丁格尔图
                    roseType:{
                        show:false,
                        type:'radius'
                    },
                    /* //内外环大小
                     raduis:{
                         show:false,
                         raduisArr:['0','70%']
                     },*/
                    //工具栏
                    toolbox: {
                        show:false,
                        feature:[],
                    },
                    //边距
                    grid:{
                        left:'10%',
                        right:'10%',
                        bottom:'20%',
                        top:'60'
                    },
                    //图形
                    graph:{
                        label:{ //文本是否显示
                            show:false
                        },
                        areaShow:'0',
                        colorType:'tbColor',//颜色填充类型
                    },
                    //x轴通用配置
                    xNormal:{
                        nameTextStyle:{
                            color:'#5bc0de'
                        },
                        axisLine:{
                            lineStyle:{
                                color:'#5bc0de'
                            }
                        },
                        axisLabel:{
                            interval:'auto',
                            rotate:"20",
                            color:'#5bc0de',
                            fontSize:'12'
                        }
                    },
                    yNormal:{
                        name:'',
                        unitType:'',
                        unitValue:'',
                        nameTextStyle:{
                            color:'#5bc0de'
                        },
                        axisLine:{
                            lineStyle:{
                                color:'#5bc0de'
                            }
                        },
                        splitLine:{
                            show:false,
                            lineStyle:{
                                color:'#417D79',
                                type:'dashed',
                                opacity:'0.5'
                            }
                        },
                        axisLabel:{
                            color:'#5bc0de',
                            fontSize:'12'
                        },

                    },
                    //x轴参数集合
                    xAxisArr:[
                        {
                            aggregationType:'',//聚合类型
                            aggregationParam:'',//聚合子端
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
                        }
                    ],
                    //y轴参数集合
                    yAxisArr:[
                        {
                            aggregationType:'',//聚合类型
                            aggregationParam:'',//聚合字段
                            aggregationParamArr:[],//字段集合
                            yAxisName:'',//y轴名称
                            legendName:'',//图例说明
                        }],
                },
                htmlTitle:'创建柱状图',
                activeName:'first',
                //y轴聚合类型
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

                //x轴聚合类型
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
                //颜色类型集合
                colorTypeArr:[
                    {
                        value: 'solidColor',
                        label: '纯色'
                    },{
                        value: 'tbColor',
                        label: '上下渐变'
                    },{
                        value: 'lrColor',
                        label: '左右渐变'
                    }
                ],
                //纯色
                solidColorArr:['#14A7F5','#5D6AFF','#FD8704','#14A7F5'],
                //预设渐变颜色值
                /* colorArr:[
                     ['rgba(15,219,243,1)','rgba(68,47,148,0.5)'],
                     ['rgba(255,221,0,1)','rgba(255,115,4,0.5)'],
                     ['rgba(160,80,255,1)','rgba(52,123,255,0.5)'],
                     ['rgba(38,253,149,1)','rgba(229,247,17,0.5)'],
                 ],*/
                colorArr :['#00EABD','#20C1F3','#FC686F','#F9D124','#DE1AFB','#C0D7FC','#A9F4B7','#FF9E96','#75B568','#323A81'],

                //保存的每一步生成的结构数据，用于返回操作
                chartsConfigArr:[],
                //过滤条件
                filters:'',
                //默认过滤条件
                defaultFilter:[],
                //更新按钮
                updateBtn:false,
                //query值
                queryVal:'',
                oldQuery:'',
                defaultQuery:''
            }
        },
        created(){
            if(this.chartType === "metric"){
                this.chartsConfig.xAxisArr = []
            }
            //保存配置
            this.defaultConfig = JSON.stringify(this.chartsConfig)
            //判断操作性质
            if(this.operType === 'edit'){//修改
                // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                //修改此组件的name值
                //this.$options.name = 'editBarChart'+ this.$route.query.id;
                //修改data参数
                this.htmlTitle = `编辑 ${this.$route.query.name}`;
                this.busName = this.chartType + 'resiveChart'+this.$route.query.id;
                this.busIndexName = this.chartType + 'IndexName' +this.$route.query.id;
                this.busFilterName = this.chartType + 'FilterName' +this.$route.query.id;
                this.busQueryName = this.chartType + 'QueryName' +this.$route.query.id;
                //时间范围监听事件
                bus.$on(this.busName,(obj)=>{
                    let arr = setChartParam(obj);
                    this.dateObj = arr[0];
                    this.intervalObj = arr[1];
                })
                //数据源监听
                bus.$on(this.busIndexName,(arr)=>{
                    //还原配置
                    this.initialize();
                    //设置数据源
                    this.chartsConfig.suffixIndexName = arr[2];
                    this.chartsConfig.preIndexName = arr[1];
                    this.chartsConfig.templateName = arr[0];
                    this.chartsConfig.datefield = arr[3];
                })
                //监听过滤条件
                bus.$on(this.busFilterName,(str)=>{
                    this.filters = str;
                    //刷新
                    //判断是否具备生成图表的条件
                    if(this.isCanCreate !== 'disabled'){
                        this.loading = true;
                        //获取数据
                        this.getData()
                    }
                })
                //监听query
                bus.$on(this.busQueryName,(str)=>{
                    this.isUpdate(str)
                })
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'editBarChart'+this.$route.query.id,
                    component:'dashboard/barChart.vue',
                    title:'编辑'
                }
                sessionStorage.setItem('/editBarChart'+this.$route.query.id,JSON.stringify(obj))
                if(this.chartId === '' || this.chartId !== this.$route.query.id){
                    this.chartId = this.$route.query.id;
                }
            }else if(this.operType === 'see'){//查看
                //修改此组件的name值
                //this.$options.name = 'seeBarChart'+ this.$route.query.id;
                //修改data参数
                this.htmlTitle = `查看 ${this.$route.query.name}`;
                this.busName = 'seeChart'+this.$route.query.id;
                this.busIndexName = 'seeIndexName' +this.$route.query.id;
                this.busFilterName = 'seeFliterName' +this.$route.query.id;
                this.busQueryName = 'seeQueryName' +this.$route.query.id;
                //时间范围监听事件
                bus.$on(this.busName,(obj)=>{
                    let arr = setChartParam(obj);
                    this.dateObj = arr[0];
                    this.intervalObj = arr[1];
                })
                //数据源监听
                bus.$on(this.busIndexName,(arr)=>{
                    //还原配置
                    this.initialize();
                    //设置数据源
                    this.chartsConfig.suffixIndexName = arr[2];
                    this.chartsConfig.preIndexName = arr[1];
                    this.chartsConfig.templateName = arr[0];
                    this.chartsConfig.datefield = arr[3];
                })
                //监听过滤条件
                bus.$on(this.busFilterName,(str)=>{
                    this.filters = str;
                    //刷新
                    //判断是否具备生成图表的条件
                    if(this.isCanCreate !== 'disabled'){
                        this.loading = true;
                        //获取数据
                        this.getData()
                    }
                })
                //监听query
                bus.$on(this.busQueryName,(str)=>{
                    this.isUpdate(str)
                })
                //将路由存放在本地 用来刷新页面时添加路由
                let obj = {
                    path:'seeBarChart'+this.$route.query.id,
                    component:'dashboard/barChart.vue',
                    title:'查看'
                }
                sessionStorage.setItem('/seeBarChart'+this.$route.query.id,JSON.stringify(obj))
                if(this.chartId === '' || this.chartId !== this.$route.query.id){
                    this.chartId = this.$route.query.id;
                }
            }else{//添加
                this.busName = this.chartType + 'addChart';
                this.busIndexName = this.chartType + 'addIndexName';
                this.busFilterName = this.chartType + 'addFliterName';
                this.busQueryName = this.chartType + 'addQueryName';
                //时间范围监听事件
                bus.$on(this.busName,(obj)=>{
                    let arr = setChartParam(obj);
                    this.dateObj = arr[0];
                    this.intervalObj = arr[1];
                })
                //数据源监听
                bus.$on(this.busIndexName,(arr)=>{
                    //还原配置
                    this.initialize();
                    //设置数据源
                    this.chartsConfig.suffixIndexName = arr[2];
                    this.chartsConfig.preIndexName = arr[1];
                    this.chartsConfig.templateName = arr[0];
                    this.chartsConfig.datefield = arr[3];
                })
                //监听过滤条件
                bus.$on(this.busFilterName,(str)=>{
                    this.filters = str;
                    //刷新
                    //判断是否具备生成图表的条件
                    if(this.isCanCreate !== 'disabled'){
                        this.loading = true;
                        //获取数据
                        this.getData()
                    }
                })
                //监听query
                bus.$on(this.busQueryName,(str)=>{
                    this.isUpdate(str)
                })
            }
        },

        beforeDestroy(){
            //在组件销毁前移除监听事件
            bus.$off(this.busName);
            bus.$off(this.busIndexName);
            bus.$off(this.busFilterName);
            bus.$off(this.busQueryName);
            //清楚计时器
            clearInterval(this.interval);
        },
        computed:{
            /*生成按钮状态*/
            'isCanCreate'(){
                let yState = false;
                let xState = false;
                //判断y轴
                this.chartsConfig.yAxisArr.forEach((item)=>{
                    if (item.aggregationType === ''){
                        yState = 'disabled';
                    } else if(item.aggregationParam === '' && item.aggregationType !== 'Count'){
                        yState = 'disabled';
                    }
                })
                //判断x轴
                this.chartsConfig.xAxisArr.forEach((item)=>{
                    if (item.aggregationType === ''){
                        xState = 'disabled';
                    } else if(item.aggregationParam === '' && item.aggregationType !== 'Count'){
                        xState = 'disabled';
                    }
                })

                if(yState == false && xState == false){
                    return false;
                }else {
                    //设置query值
                    this.oldQuery = this.queryVal;
                    this.updateBtn = false
                    return 'disabled';
                }
            }
        },
        methods:{
            //刷新
            refreshChart(){
                this.refresh++;
            },
            updateChart(){
                //this.queryVal = this.oldQuery;
                this.refresh++;
                this.updateBtn = false;
            },
            //判断是否出现更新按钮
            isUpdate(currentVal){
                this.queryVal = currentVal;
                //判断是否可以生产报表
                if(this.isCanCreate !== 'disabled'){
                    //判断目标值是否有改变
                    if(currentVal !== this.oldQuery){
                        this.updateBtn = true
                        if($(".layui-layer-tips").length === 0){
                            layer.tips('点击更新', '.update-btn', {
                                tips: [3, '#3595CC'],
                                time: 2000
                            });
                        }

                    }else{
                        this.updateBtn = false
                    }
                }else{
                    this.oldQuery = this.queryVal;
                }
            },
            // 初始化
            initialize(){
                //还原配置
                this.chartsConfig = JSON.parse(this.defaultConfig);
                this.emptyTipState = true;
                //销毁已经创建的图表
                this.opt = {};
                echarts.init(document.getElementById('charts-wapper')).dispose();//销毁前一个实例
                this.chartParams.searchParam='';
            },
            /*添加y轴*/
            addY(){
                //this.isCanCreate = 'disabled';
                this.chartsConfig.yAxisArr.push({
                    aggregationType:'',
                    aggregationParam:'',
                    aggregationParamArr:[],//字段集合
                    yAxisName:'',
                    legendName:'',
                })
            },
            /*添加拆分序列*/
            addX(){
                this.chartsConfig.xAxisArr.push({
                    aggregationType:'',//聚合类型
                    aggregationParam:'',//聚合子端
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
                })
            },
            /*删除项*/
            removeItem(arr,i){
                //至少保留一项
                if(arr.length > 1){
                    arr.splice(i,1)
                }
            },
            /*y轴聚合类型改变*/

            yAggregationChange($event,index){
                if (this.chartsConfig.preIndexName === '' || this.chartsConfig.suffixIndexName === '' || this.chartsConfig.templateName === ''){
                    layer.msg('请选择数据源',{icon:'5'});
                    return;
                }
                //清空聚合字段
                this.chartsConfig.yAxisArr[index].aggregationParam = ''
                this.chartsConfig.yAxisArr[index].aggregationParamArr = [];
                //获取参数集合
                this.$nextTick(()=>{
                    this.leftLoading = true;
                    this.$axios.post(this.$baseUrl+'/BI/getFieldByYAxisAggregation.do',this.$qs.stringify(
                        {
                            agg:$event,
                            pre_index_name:this.chartsConfig.preIndexName,
                            suffix_index_name:this.chartsConfig.suffixIndexName,
                            template_name:this.chartsConfig.templateName
                        }
                    ))
                        .then(res=>{
                            this.leftLoading = false;
                            res.data.forEach(item=>{
                                let obj = {
                                    value:item.fieldName,
                                    label:item.fieldName
                                }
                                this.chartsConfig.yAxisArr[index].aggregationParamArr.push(obj)

                            })
                        })
                        .catch(err=>{
                            this.leftLoading = false;

                        })
                })
            },
            /*x轴聚合类型改变*/
            xAggregationChange($event,index){
                if (this.chartsConfig.preIndexName === '' || this.chartsConfig.suffixIndexName === '' || this.chartsConfig.templateName === ''){
                    layer.msg('请选择数据源',{icon:'5'});
                    return;
                }
                //清空聚合字段
                this.chartsConfig.xAxisArr[index].aggregationParam = '';
                this.chartsConfig.xAxisArr[index].aggregationParamArr = [];
                this.chartsConfig.xAxisArr[index].ipRange=[{start:'',end:''}];
                this.chartsConfig.xAxisArr[index].numberRange=[{start:'',end:''}];
                this.chartsConfig.xAxisArr[index].dateRange=[{start:'',end:''}];

                this.$nextTick(()=>{
                    this.leftLoading = true;
                    this.$axios.post(this.$baseUrl+'/BI/getFieldByXAxisAggregation.do',this.$qs.stringify({
                        agg:$event,
                        pre_index_name:this.chartsConfig.preIndexName,
                        suffix_index_name:this.chartsConfig.suffixIndexName,
                        template_name:this.chartsConfig.templateName
                    }))
                        .then(res=>{
                            this.leftLoading = false;
                            res.data.forEach(item=>{
                                let obj = {
                                    value:item.fieldName,
                                    label:item.fieldName
                                }
                                this.chartsConfig.xAxisArr[index].aggregationParamArr.push(obj)
                            })
                        })
                        .catch(err=>{
                            this.leftLoading = false;

                        })
                })
            },
            /*生成按钮*/
            createBtn(){
                this.loading = true;
                this.getData()
            },
            /*获取数据*/
            getData(){
                //判断请求的方法
                let url = '';
                if(this.chartType === 'pie'){
                    url = '/BI/getDataByChartParams_pie.do'
                }else if(this.chartType === 'bar'){
                    url = '/BI/getDataByChartParams_bar.do'
                }else if(this.chartType === 'line'){
                    url = '/BI/getDataByChartParams_line.do'
                }else if(this.chartType === 'metric'){
                    this.emptyTipState = false;
                    this.loading = false;
                    let data2 = [
                        {name:'count',value:9858247},
                       // {name:'count2',value:9858247}
                    ]
                    this.sourceData = data2
                    this.createMetric(data2);
                    return;
                }
                //构建metrics（y）参数 [{aggType:"count",field:"logdate"}]
                let metricsArr = [];
                for(let i in this.chartsConfig.yAxisArr){
                    let obj = {};
                    obj.aggType = this.chartsConfig.yAxisArr[i].aggregationType;
                    obj.aliasName = this.chartsConfig.yAxisArr[i].yAxisName
                    //聚合类型为count的做特殊处理
                    if(obj.aggType === 'Count'){
                        obj.field = this.chartsConfig.datefield
                    }else{
                        obj.field = this.chartsConfig.yAxisArr[i].aggregationParam;
                    }

                    metricsArr.push(obj)
                }
                //构建buckets(x)参数
                let bucketsArr = [];
                for(let i in this.chartsConfig.xAxisArr){
                    let obj = {};
                    obj.aggType = this.chartsConfig.xAxisArr[i].aggregationType;
                    obj.field = this.chartsConfig.xAxisArr[i].aggregationParam;
                    obj.size=this.chartsConfig.xAxisArr[i].topSum;
                    obj.sort=this.chartsConfig.xAxisArr[i].orderType;
                    obj.intervalType=this.chartsConfig.xAxisArr[i].timeType;
                    obj.intervalValue=this.chartsConfig.xAxisArr[i].timeInterval;
                    let numberRange = [];
                    let dateRange = [];
                    let ipRange = [];
                    for(let j in this.chartsConfig.xAxisArr[i].numberRange){
                        numberRange.push({
                            from:this.chartsConfig.xAxisArr[i].numberRange[j].start,
                            to:this.chartsConfig.xAxisArr[i].numberRange[j].end
                        })
                    }
                    for(let j in this.chartsConfig.xAxisArr[i].dateRange){
                        dateRange.push({
                            from:this.chartsConfig.xAxisArr[i].dateRange[j].start,
                            to:this.chartsConfig.xAxisArr[i].dateRange[j].end
                        })
                    }
                    for(let j in this.chartsConfig.xAxisArr[i].ipRange){
                        ipRange.push({
                            from:this.chartsConfig.xAxisArr[i].ipRange[j].start,
                            to:this.chartsConfig.xAxisArr[i].ipRange[j].end
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
                //单位
                if(this.chartsConfig.yNormal.unitType === ''){
                    this.yUnit = ''
                }else if(this.chartsConfig.yNormal.unitType === '%'){
                    this.yUnit = '%'
                }else if(this.chartsConfig.yNormal.unitType === 'byte'){
                    this.yUnit = this.chartsConfig.yNormal.unitValue
                }
                let param = {
                    starttime:this.dateObj.starttime,//起始时间
                    endtime:this.dateObj.endtime,//结束时间
                    last:this.dateObj.last,
                    unit:this.yUnit,
                    pre_index_name:this.chartsConfig.preIndexName,
                    suffix_index_name:this.chartsConfig.suffixIndexName,
                    template_name:this.chartsConfig.templateName,
                    metrics:JSON.stringify(metricsArr),
                    buckets:JSON.stringify(bucketsArr),
                    filters_visual:this.filters,
                    queryBox:this.queryVal
                }
                this.$nextTick(()=>{
                    this.$axios.post(this.$baseUrl+url,this.$qs.stringify(param))
                        .then(res=>{
                            this.loading = false;
                            //存储查询条件
                            this.chartParams.searchParam = JSON.stringify(param);
                            //判断x轴label间隔类型
                            if(this.chartsConfig.xNormal.axisLabel.interval !== 'auto'){
                                this.chartsConfig.xNormal.axisLabel.interval = Number(this.chartsConfig.xNormal.axisLabel.interval)
                            }
                            //处理数据
                            let obj = res.data;
                            if (obj.success === 'true'){
                                let xDataArr =''
                                if(this.chartType === 'pie'){
                                    xDataArr='pie'
                                }else{
                                    xDataArr = obj.data[0].dimensions;
                                }
                                if(xDataArr.length > 1){
                                    // echarts.init(document.getElementById('charts-wapper')).dispose();//销毁前一个实例
                                    this.sourceData = obj.data;
                                    this.emptyTipState = false;
                                    //this.createChart(obj.data[0]);
                                    if(this.chartType === 'bar'){
                                        this.createBarChart(obj.data[0])
                                    }else if(this.chartType === 'line'){
                                        this.createLineChart(obj.data[0])
                                    }else if(this.chartType === 'pie'){
                                        this.createPieChart(obj.data)
                                    }
                                }else{
                                    this.emptyTipState = true;
                                    echarts.init(document.getElementById('charts-wapper')).dispose();//销毁前一个实例
                                }

                            } else {
                                layer.msg(obj.message,{icon:5});
                            }

                        })
                        .catch(err=>{
                            this.loading = false;
                        })
                })
            },
            /*创建柱状图图表*/
            createBarChart(data){
                this.opt = {
                    title: {
                        text: "",
                        x:"center",
                        textStyle:{
                            color:"#5bc0de"
                        }
                    },
                    legend: this.chartsConfig.legend,
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'shadow'
                        }
                    },
                    toolbox: {
                        show:this.chartsConfig.toolbox.show,
                        feature: {
                            dataView: {
                                show:this.chartsConfig.toolbox.feature.includes('dataView'),
                                readOnly: "false",
                                backgroundColor :"#3f5267",
                                textareaColor:'#3f5267',
                                textColor: "#fff"
                            },
                            magicType: {
                                show:this.chartsConfig.toolbox.feature.includes('magicType'),
                                type: ["line", "bar"]
                            },
                            restore: {
                                show:this.chartsConfig.toolbox.feature.includes('restore'),
                            },
                            dataZoom:{
                                show:this.chartsConfig.toolbox.feature.includes('dataZoom'),
                            },
                            saveAsImage: {
                                show:this.chartsConfig.toolbox.feature.includes('saveAsImage'),
                            }
                        },
                        iconStyle:{
                            borderColor: "#56769a"
                        }
                    },
                    dataset: [data],
                    grid:this.chartsConfig.grid,
                    xAxis: {
                        type: 'category',
                        name:this.chartsConfig.xAxisArr[0].xAxisName,
                        nameTextStyle:{
                            color:this.chartsConfig.xNormal.nameTextStyle.color
                        },
                        axisLine:this.chartsConfig.xNormal.axisLine,
                        axisLabel:this.chartsConfig.xNormal.axisLabel,
                    },
                    yAxis: {
                        name:this.chartsConfig.yNormal.name+' '+ this.yUnit,
                        nameTextStyle:{
                            color:this.chartsConfig.yNormal.nameTextStyle.color,
                        },
                        splitLine:this.chartsConfig.yNormal.splitLine,
                        axisLine:this.chartsConfig.yNormal.axisLine,
                        axisLabel: {
                            color:this.chartsConfig.yNormal.axisLabel.color,
                            fontSize:this.chartsConfig.yNormal.axisLabel.fontSize,
                            formatter: function (value, index) {
                                if (value >= 10000 && value < 10000000) {
                                    value = value / 10000 + "万";
                                } else if (value >= 10000000) {
                                    value = value / 10000000 + "千万";
                                }
                                return value;
                            }
                        }
                    },
                    series: []
                }
                //循环创建图表的维度
                let xL = data.dimensions.length - 1;//维度
                let colorIndex = 0;//颜色索引
                for(let i=0;i<xL;i++){
                    let obj = {
                        name: this.chartsConfig.yAxisArr[0].legendName,
                        type: 'bar',
                        label:{
                            show:this.chartsConfig.graph.label.show,
                            position:'top',
                            color:`${this.colorArr[colorIndex]}`
                        },
                        itemStyle: {
                            color: this.colorArr[colorIndex]
                        },
                    }
                    this.opt.series.push(obj)
                    colorIndex++;
                    if(colorIndex === this.colorArr.length){
                        colorIndex = 0;
                    }
                }
                let myChart =  echarts.init(document.getElementById('charts-wapper'));
                myChart.setOption(this.opt,true);
                window.addEventListener("resize",()=>{
                    myChart.resize();
                });
                //保存结构数据
                this.chartsConfigArr.push(JSON.stringify(this.chartsConfig));
            },
            /*创建折线图图表*/
            createLineChart(data){
                this.opt = {
                    title: {
                        text: "",
                        x:"center",
                        textStyle:{
                            color:"#5bc0de"
                        }
                    },
                    legend: this.chartsConfig.legend,
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'cross'
                        }
                    },
                    toolbox: {
                        show:this.chartsConfig.toolbox.show,
                        feature: {
                            dataView: {
                                show:this.chartsConfig.toolbox.feature.includes('dataView'),
                                readOnly: "false",
                                backgroundColor :"#3f5267",
                                textareaColor:'#3f5267',
                                textColor: "#fff"
                            },
                            magicType: {
                                show:this.chartsConfig.toolbox.feature.includes('magicType'),
                                type: ["line", "bar"]
                            },
                            restore: {
                                show:this.chartsConfig.toolbox.feature.includes('restore'),
                            },
                            dataZoom:{
                                show:this.chartsConfig.toolbox.feature.includes('dataZoom'),
                            },
                            saveAsImage: {
                                show:this.chartsConfig.toolbox.feature.includes('saveAsImage'),
                            }
                        },
                        iconStyle:{
                            borderColor: "#56769a"
                        }
                    },
                    dataset: [data],
                    grid:this.chartsConfig.grid,
                    xAxis: {
                        type: 'category',
                        name:this.chartsConfig.xAxisArr[0].xAxisName,
                        boundaryGap: false,
                        nameTextStyle:{
                            color:this.chartsConfig.xNormal.nameTextStyle.color
                        },
                        axisLine:this.chartsConfig.xNormal.axisLine,
                        axisLabel:this.chartsConfig.xNormal.axisLabel,
                    },
                    yAxis: {
                        name:this.chartsConfig.yNormal.name+' '+ this.yUnit,
                        nameTextStyle:{
                            color:this.chartsConfig.yNormal.nameTextStyle.color,
                        },
                        splitLine:this.chartsConfig.yNormal.splitLine,
                        axisLine:this.chartsConfig.yNormal.axisLine,
                        axisLabel: {
                            color:this.chartsConfig.yNormal.axisLabel.color,
                            fontSize:this.chartsConfig.yNormal.axisLabel.fontSize,
                            formatter: function (value, index) {
                                if (value >= 10000 && value < 10000000) {
                                    value = value / 10000 + "万";
                                } else if (value >= 10000000) {
                                    value = value / 10000000 + "千万";
                                }
                                return value;
                            }
                        }
                    },
                    series: []
                }
                let xL = data.dimensions.length - 1;
                let colorIndex = 0;//颜色索引
                for(let i=0;i<xL;i++){
                    if(this.chartsConfig.graph.areaShow === '0'){
                        let obj = {
                            name: this.chartsConfig.yAxisArr[0].legendName,
                            type: 'line',
                            smooth:true,
                            label:{
                                show:this.chartsConfig.graph.label.show,
                                position:'top',
                            },
                            itemStyle:{
                                color: this.colorArr[colorIndex]
                            },
                            lineStyle:{
                                color: this.colorArr[colorIndex]
                            },
                            areaStyle: {
                                color:this.colorArr[colorIndex],
                                opacity:this.chartsConfig.graph.areaShow
                            },
                        }
                        this.opt.series.push(obj)
                    }else{
                        let obj = {
                            name: this.chartsConfig.yAxisArr[0].legendName,
                            type: 'line',
                            smooth:true,
                            label:{
                                show:this.chartsConfig.graph.label.show,
                                position:'top',
                            },
                            itemStyle:{
                                color: this.colorArr[colorIndex]
                            },
                            lineStyle:{
                                color: this.colorArr[colorIndex]
                            },
                            areaStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                                    offset: 0,
                                    color: this.colorArr[colorIndex]
                                },{
                                    offset: 1,
                                    color: 'rgba(116,235,213,0.1)'
                                }]),
                                opacity:this.chartsConfig.graph.areaShow
                            },
                        }
                        this.opt.series.push(obj)
                    }
                    colorIndex++;
                    if(colorIndex === this.colorArr.length){
                        colorIndex = 0;
                    }
                }
                //echarts.init(document.getElementById('charts-wapper')).dispose();//销毁前一个实例
                let myChart =  echarts.init(document.getElementById('charts-wapper'));
                myChart.setOption(this.opt,true);
                window.addEventListener("resize",()=>{
                    myChart.resize();
                });
            },
            /*创建饼图图表*/
            createPieChart(pieArr){
                if(this.chartsConfig.roseType.show === false){
                    this.chartsConfig.roseType.type = false;
                }
                this.opt = {
                    title: {
                        text: "",
                        x:"center",
                        textStyle:{
                            color:"#5bc0de"
                        }
                    },
                    legend: this.chartsConfig.legend,
                    tooltip : {
                        trigger: 'item',
                        /*formatter:function (params,ticket,callback) {
                            return `${params.value[params.dimensionNames[0]]}:<br>${params.value[params.dimensionNames[1]]}(${params.percent}%)`
                        }*/
                    },
                    //dataset: [pieArr],
                    roseType:this.chartsConfig.roseType.type,
                    toolbox: {
                        show:this.chartsConfig.toolbox.show,
                        feature: {
                            dataView: {
                                show:this.chartsConfig.toolbox.feature.includes('dataView'),
                                readOnly: "false",
                                backgroundColor :"#3f5267",
                                textareaColor:'#3f5267',
                                textColor: "#fff"
                            },
                            saveAsImage: {
                                show:this.chartsConfig.toolbox.feature.includes('saveAsImage'),
                            }
                        },
                        iconStyle:{
                            borderColor: "#56769a"
                        }
                    },
                    color:['#1E73F0','#00D1CE','#33C3F7','#3952D3','#185BFF','#2455AD','#74EE9A','#253479','#3C7FD3','#72B5D3'],
                    grid:this.chartsConfig.grid,
                    series: []
                }
                //饼图圆环个数
                let pieCount = pieArr.length;
                //圆环间隔
                let pieSpace = 5;
                //最大范围
                let raduisMax = 70;
                //每个环平均的宽度
                let raduisVal = (raduisMax-pieSpace*(pieCount-1))/(pieCount + 1);
                for(let i = 0;i<pieCount;i++){
                    if(i === 0){
                        let obj = {
                            type: 'pie',
                            radius:[0,`${raduisVal*(i+2)}%`],
                            data:pieArr[i],
                            label:{
                                normal:{
                                    show:false
                                }
                            },
                            itemStyle: {
                                emphasis: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        }
                        this.opt.series.push(obj)
                    }else{
                        let obj = {
                            type: 'pie',
                            radius:[`${raduisVal*(i+1)+pieSpace*i}%`,`${raduisVal*(i+2)+pieSpace*i}%`],
                            label:{
                                normal:{
                                    show:false
                                }
                            },
                            data:pieArr[i],
                            itemStyle: {
                                emphasis: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        }
                        this.opt.series.push(obj)
                    }
                }
                let myChart =  echarts.init(document.getElementById('charts-wapper'));
                //console.log(JSON.stringify(this.opt))
                myChart.setOption(this.opt,true);
                window.addEventListener("resize",()=>{
                    myChart.resize();
                });
            },
            /*创建指标数据图表*/
            createMetric(data){
                //将配置保存在serise，用于dashbord添加
                this.opt = {
                    style :this.chartsConfig.style
                }
                //清空原始数据
                $("#charts-wapper").html('')
                let str = ''
                //循环拼接数据
                for(let i in data){
                    data[i].value = parseInt(data[i].value).toLocaleString();
                    str += `<span style="margin: 50px;"><p>${data[i].name}</p><p style="font-size: ${this.chartsConfig.style.fontSize}px;color: ${this.chartsConfig.style.color};font-weight: 600;">${data[i].value}</p></span>`
                }
                let box = '<div style="width: 100%;height: 100%;display: flex;justify-content: center;align-items: center">'+str+'</div>'
                //添加到页面中
                $("#charts-wapper").append(box);
            },
            /*上一步*/
            prevCreate(){
                this.chartsConfig = JSON.parse(this.chartsConfigArr[0]);
                console.log(this.chartsConfig)
                //this.createChart();
            },
            /*保存图表*/
            saveChart(){
                //清空已经获取的参数
                // this.chartsConfig.xAxisArr
                for (let i in this.chartsConfig.xAxisArr){
                    this.chartsConfig.xAxisArr[i].aggregationParamArr = [];
                    //this.opt.xAxis.data = [];
                }
                //this.chartsConfig.xAxisArr
                for (let j in this.chartsConfig.yAxisArr){
                    this.chartsConfig.yAxisArr[j].aggregationParamArr = [];
                    //this.opt.series[j].data = [];
                }
                //清空echart结构中获取的图表数据
                this.opt.dataset = [];
                if(this.opt.series){
                    this.opt.series = [this.opt.series[0]];
                }
                //定义参数
                let optStr = {
                    config:this.chartsConfig,
                    opt:this.opt
                }
                let params = {
                    title:this.chartParams.chartName,
                    description:this.chartParams.chartDes,
                    filters_visual:this.filters,
                    type:this.chartType,
                    pre_index_name:this.chartsConfig.preIndexName,
                    suffix_index_name:this.chartsConfig.suffixIndexName,
                    template_name:this.chartsConfig.templateName,
                    option:JSON.stringify(optStr),
                    params:this.chartParams.searchParam,
                    // queryBox:this.queryVal,
                    isSaveAs:true
                }
                //判断是否是修改图表
                if(this.chartId !== ''){
                    //判断是否是另存图表
                    if(!this.chartParams.otherSave){//不是另存（修改原先的）
                        params.id = this.chartId;
                        params.isSaveAs = false;
                    }
                }
                this.$nextTick(()=>{
                    this.allLoading = true;
                    this.$axios.post(this.$baseUrl+'/BI/saveVisualization.do',this.$qs.stringify(params))
                        .then(res=>{
                            this.allLoading = false;
                            if(res.data.success == 'true'){
                                this.dialogFormVisible = false;
                                layer.msg(res.data.message,{icon:1})
                            }else{
                                layer.msg(res.data.message,{icon:5})
                            }

                        })
                        .catch(err=>{
                            this.allLoading = false;
                            layer.msg('保存失败',{icon:5})
                        })
                })
            },
            /*删除x轴*/
            removeXaxisTab(i,event){
                this.chartsConfig.xAxisArr.splice(i,1);
                event.stopPropagation();
            },
            /*删除y轴*/
            removeYaxisTab(i,event){
                if(this.chartsConfig.yAxisArr.length > 1){
                    this.chartsConfig.yAxisArr.splice(i,1);
                    event.stopPropagation();
                }
            }
        },
        watch:{
            //检测有无id 有则是修改
            chartId:{
                handler(newV) {
                    if(newV !== ''){
                        this.$nextTick(()=>{
                            this.allLoading = true;
                            this.$axios.post(this.$baseUrl+'/BI/getVisualizationById.do',this.$qs.stringify({
                                id:this.chartId
                            }))
                                .then(res=>{
                                    this.allLoading = false;
                                    let obj = res.data;
                                    if (obj.success == 'true'){
                                        //赋值
                                        if(JSON.parse(obj.data.params).filters_visual){
                                            this.filters = JSON.parse(obj.data.params).filters_visual;
                                            this.defaultFilter = JSON.parse(JSON.parse(obj.data.params).filters_visual);
                                        }
                                        let option = JSON.parse(obj.data.option);
                                        this.indexVal = [obj.data.template_name,obj.data.pre_index_name,obj.data.suffix_index_name,this.chartsConfig.datefield]
                                        this.chartsConfig = option.config;
                                        //x轴聚合参数
                                        /* let xap = this.chartsConfig.xAxisArr[0].aggregationParam;
                                         this.xAggregationChange();
                                         this.chartsConfig.xAxisArr[0].aggregationParam = xap;*/
                                        for(let i=0;i < this.chartsConfig.xAxisArr.length;i++){
                                            //选保存数据 避免在获取其他数据时被清空
                                            let xap= this.chartsConfig.xAxisArr[i].aggregationParam;
                                            let numberRange = this.chartsConfig.xAxisArr[i].numberRange;
                                            let ipRange = this.chartsConfig.xAxisArr[i].ipRange;
                                            let dateRange = this.chartsConfig.xAxisArr[i].dateRange;
                                            this.xAggregationChange(this.chartsConfig.xAxisArr[i].aggregationType,i);
                                            //赋值
                                            this.chartsConfig.xAxisArr[i].aggregationParam = xap;
                                            this.chartsConfig.xAxisArr[i].numberRange = numberRange;
                                            this.chartsConfig.xAxisArr[i].ipRange = ipRange;
                                            this.chartsConfig.xAxisArr[i].dateRange = dateRange;
                                        }
                                        //y轴聚合参数
                                        for(let i=0;i < this.chartsConfig.yAxisArr.length;i++){
                                            let yap= this.chartsConfig.yAxisArr[i].aggregationParam;
                                            this.yAggregationChange(this.chartsConfig.yAxisArr[i].aggregationType,i);
                                            this.chartsConfig.yAxisArr[i].aggregationParam = yap
                                        }

                                        this.chartParams.chartName = obj.data.title;
                                        this.chartParams.chartDes = obj.data.description;
                                        this.chartParams.searchParam = obj.data.params;

                                        //后台直接返回数据
                                        /*let xD = JSON.parse(obj.data.data)[0].name;
                                        let yD = JSON.parse(obj.data.data)[0].data;
                                        this.createChart(xD,yD);
                                        this.emptyTipState = false;
                                        this.sourceData = JSON.parse(obj.data.data);*/
                                        //前台自己获取数据
                                        this.getData()
                                    }else{
                                        layer.msg(res.data.message,{icon:5})
                                    }
                                })
                                .catch(err=>{
                                    this.allLoading = false;
                                })
                        })
                    }
                },
                immediate: true,
                deep: true
            },
            //时间范围改变
            'dateObj'(nv,ov){
                //判断是否具备生成图表的条件
                if(this.isCanCreate !== 'disabled'){
                    this.loading = true;
                    //获取数据
                    this.getData()
                }
            },
            //轮询状态改变
            'intervalObj'(){
                //判断是否启用轮询获取数据
                if (this.intervalObj.state){
                    clearInterval(this.interval)
                    this.interval = setInterval(()=>{
                        //判断是否具备生成图表的条件
                        if(this.isCanCreate !== 'disabled'){
                            //获取数据
                            this.getData()
                        }
                    },this.intervalObj.interval)
                }else {
                    clearInterval(this.interval)
                }
            }
        },
        /*  beforeRouteEnter(to, from, next) {
              next (vm => {
                  //判断是否有参数  有参数说明是修改/查看功能页面
                  if(JSON.stringify(to.query) !== "{}" && to.query.type === 'edit'){//修改
                      // 这里通过 vm 来访问组件实例解决了没有 this 的问题
                      //修改此组件的name值
                      vm.$options.name = 'editBarChart'+ to.query.id;
                      //修改data参数
                      vm.htmlTitle = `编辑 ${to.query.name}`;
                      vm.busName = 'resiveBarChart'+to.query.id;
                      vm.busIndexName = 'barIndexName' +to.query.id;
                      console.log('bbb')
                      //时间范围监听事件
                      bus.$on(vm.busName,(arr)=>{
                          console.log(arr)
                          vm.dateArr = arr;
                      })
                      //数据源监听
                      bus.$on(vm.busIndexName,(arr)=>{
                          //还原配置
                          vm.initialize();
                          //设置数据源
                          //设置数据源
                          vm.chartsConfig.suffixIndexName = arr[2];
                          vm.chartsConfig.preIndexName = arr[1];
                          vm.chartsConfig.templateName = arr[0];
                      })
                      //将路由存放在本地 用来刷新页面时添加路由
                      let obj = {
                          path:'editBarChart'+to.query.id,
                          component:'dashboard/barChart.vue',
                          title:'编辑'
                      }
                      sessionStorage.setItem('/editBarChart'+to.query.id,JSON.stringify(obj))
                      if(vm.chartId === '' || vm.chartId !== to.query.id){
                          vm.chartId = to.query.id;
                      }
                  }else if(to.query.type === 'see'){//查看
                      //修改此组件的name值
                      vm.$options.name = 'seeBarChart'+ to.query.id;
                      //修改data参数
                      vm.htmlTitle = `查看 ${to.query.name}`;
                      //数据源监听
                      bus.$on(vm.busIndexName,(arr)=>{
                          //还原配置
                          vm.initialize();
                          //设置数据源
                          vm.chartsConfig.suffixIndexName = arr[2];
                          vm.chartsConfig.preIndexName = arr[1];
                          vm.chartsConfig.templateName = arr[0];
                      })
                      bus.$off(vm.busIndexName)
                      //将路由存放在本地 用来刷新页面时添加路由
                      let obj = {
                          path:'seeBarChart'+to.query.id,
                          component:'dashboard/barChart.vue',
                          title:'查看'
                      }
                      sessionStorage.setItem('/seeBarChart'+to.query.id,JSON.stringify(obj))
                      if(vm.chartId === '' || vm.chartId !== to.query.id){
                          vm.chartId = to.query.id;
                      }
                  }else{//添加
                      //时间范围监听事件
                      bus.$on('createBarChart',(arr)=>{
                          vm.dateArr = arr;
                      })
                      //数据源监听
                      bus.$on('barIndexName',(arr)=>{
                          //还原配置
                          vm.initialize();
                          //设置数据源
                          vm.chartsConfig.suffixIndexName = arr[2];
                          vm.chartsConfig.preIndexName = arr[1];
                          vm.chartsConfig.templateName = arr[0];
                      })
                  }


              })

          },*/
        components:{
            dateLayout,
            chooseIndex,
            jsonView,
            queryFilter
        }
    }
</script>

<style scoped>
    .top-zz{
        text-align: center;
        width: 100%;
        height: 50px;
        position: absolute;
        /*background: #;*/
        z-index: 100;
        cursor: no-drop;
        text-shadow: none;
        color: #455b75;
    }
    .from-zz{
        width: 242px;
        height:100%;
        cursor: no-drop;
        position: absolute;
        z-index: 100;
    }
    .choose-wapper{
        height: 50px;
        display: flex;
        align-items: center;
        float: left;
    }
    .saveChart{
        float: right;
        margin-right: 10px;
        margin-top: 10px;
    }
    .date-wapper{
        float: right;
        margin-right: 10px;
        margin-top: 10px;
        position: relative;
        z-index: 101;
    }
    .chart-wapper{
        height: 100%;
        overflow: hidden;
        padding-bottom: 10px;
    }
    .chart-wapper>div{
        float: left;
        background: #303e4e;
        height: calc(100vh - 237px);
    }
    .config-wapper{
        width: 300px;
        margin: 0 10px;
        position: relative;
    }
    .creatBtn{
        /*width: 50px;*/
        height: 39px;
        text-align: center;
        line-height: 39px;
        position: absolute;
        right: 0;
        top: 0;
        z-index: 1;
        /*background: #3a8ee6;*/
        font-size: 14px;
        padding: 0 8px;
        border-radius: 0;
    }
    .prevBtn{
        right: 82px;
    }
    .nextBtn{
        right: 48px;
    }
    .creatBtn:hover{
        /*cursor: pointer;*/
        /*background: #66b1ff;*/
    }
    .config-wapper /deep/ .el-tabs__content{
        height: calc(100% - 69px);
        overflow: auto;
    }
    .chart-wapper /deep/ .el-collapse-item__content{
        padding-bottom: 0;
    }
    .view-wapper{
        width: calc(100% - 330px);
        margin-right: 10px;
        position: relative;
    }
    .config-item{
        /*height:100*/
    }
    .config-wapper /deep/ .el-collapse-item__header{
        height: 35px;
        background: #455b75;
        border-top: 0;
        color: #409eff;
    }
    .config-wapper /deep/ .el-form-item__label{
        color: #FFF!important;
        font-size: 12px;
    }
    .config-wapper /deep/ .el-collapse-item__wrap{
        background: 0;
        padding: 10px;
    }
    .tablist{
        background: #3e4f63;
        margin: 10px 0;
    }
    .charts-title{
        height: 50px;
        text-align: center;
        line-height: 50px;
        color: #10d9f2;
        font-weight: 600;
        font-size: 19px;
    }
    #charts-wapper{
        height: calc(100% - 50px);
    }
    .tip-w{
        font-size: 10px;
        color: #e4956d;
    }
    .addY:hover{
        cursor: pointer;
        color: #10d9f2;
    }
    .tablist /deep/ .el-collapse-item__header{
        position: relative;
        padding-left: 30px;
    }
    .tablist /deep/ .el-collapse-item__arrow{
        position: absolute;
        left: 5px;
        top: 12px;
    }
    .tablist .removeTab{
        position: absolute;
        right: 10px;
        top: 12px;
    }
    .tablist .removeTab:hover{
        color: #e4956d;
    }
    /deep/.el-form-item--small.el-form-item{
        margin-bottom: 10px;
    }
    .empty-tip{
        position: absolute;
        top: 50%;
        left: 50%;
        margin-left: -35px;
        margin-top: -10px;
        color: #6885a7;
    }
    .source-data{
        position: absolute;
        top: 10px;
        right: 10px;
        overflow: auto;
    }
    .source-data button{
        background: 0;
        border: 0;
    }
    .jsonView{
        width: 100%;
        max-height: 80vh;
        overflow: auto;
    }
    .range-p{
        display: flex;
        align-items: center;
    }
    /*    .range-p /deep/ .el-input__inner[type=number]{
            padding-right: 2px;
        }*/
    .range-p span{
        margin: 0 5px;
    }
    .range-p i{
        margin-left: 5px;
    }
    .range-p i:hover{
        cursor: pointer;
        color: #e4956d;
    }
    .range-btn-p{
        text-align: center;
        margin-top: 5px;
    }
    .range-btn-p span:hover{
        cursor: pointer;
        color: #409eff;
    }
    .filter-box{
        padding: 0 10px;
        position: relative;
        top: -10px;
    }
    .filter-box /deep/ .el-input__inner{
        border-radius: 0;
    }
</style>
