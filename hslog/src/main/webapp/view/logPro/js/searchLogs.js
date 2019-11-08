
var logsArr  = [];//数组 用于存放获取来的logs
var logDetailArr = [];//用于查看日志详情存放json日志
var logType = "";//日志类型
var pageCount = 0;//总页数
var pageTotle = 13;//每一页显示的条数
var historyArr = [];//用于存放检索历史数组
var logStatus ;//用于区别是查询操作还是过滤操作
var filterArr = [];//过滤日志存放数组
var logDesArr = [];
var firstGet = true;//定义第一次请求
var sendObj = {};//传参对象合集
var allCount ;//检索的结果总总数
var currentPage = 1;//当前显示的页码
//获取数据函数  url-请求链接  id-用于区分单个设备日志 searchWord-查询条件 page-当前页码 size-每页条数
function getLogs(url,id,searchWords,page,pageTotle){
    //定义传的参数
    var param = {};
    if(id != ''){//单个设备日志
        param.id = id;
        /*param.param = "logdate";
        param.order = "desc";*/
    }

    //判断是精确查询还是模糊查询
    if(typeof searchWords == 'object'){//精确查询
        param = searchWords;
        var  historystring = '';
        for(var i in param){
            if(param[i] != '' && param[i] != null && i != "id" && i != "param" && i != "order"&& i != "page"&& i != "size"){
                historystring += param[i]+" ; ";
            }
        }
        //判断关键字是否为空  不为空添加到历史列表  并且是第一次请求
        if(historystring != '' && firstGet == true){
            //prependHistory(historystring);
        }

    }else if (typeof searchWords == 'string'){//模糊查询
        param.words = searchWords;
        //判断关键字是否为空  不为空添加到历史列表 并且是第一次请求
        if(searchWords != '' && firstGet == true){
            prependHistory(searchWords);
        }
    }

    param.page = page;
    param.size = pageTotle;
    //保存参数对象 用于分页传参
    sendObj.url = url;
    sendObj.id = id;
    sendObj.searchWords = searchWords;
    //回调函数
    var sFunc = function(data){
        //清除总数的数值
        allCount = 0;
        //调用函数显示日志信息
        showLogs(data[0].list);
        //将当页的数据保存在本地 用于过滤
        filterArr = data[0].list;
        //储存日志
        logDetailArr = data[0].list;
        //判断是否是第一次请求
        if(firstGet == true){
            //获得总条数
            allCount = data[0].count;
            //添加总条数到页面中
            $(".allCountBox>b").html(allCount);
            //获得设置的最大显示条数
            var maxShowDataVal = $(".maxShowData").val();
            //获得默认的最大显示条数
            var defaultmaxVal = $(".maxShowData").children("option:first-child").val();
            if(maxShowDataVal != defaultmaxVal){
                maxShowDataVal = defaultmaxVal;
                $(".maxShowData").val(defaultmaxVal);
            }
            //判断总数与当前选择的最大显示的条数进行对比
            if(allCount > maxShowDataVal){
                //创建分页函数
                createPage("pageBox",$(".maxShowData").val(),pageTotle);
            }else{
                //创建分页函数
                createPage("pageBox",allCount,pageTotle);
            }
            /*//判断到处日志状态
            if(exportStatus){
                exportLogs();
            }*/

        }else{
            //显示数据
        }
    };
    //将object对象转换成字符串
    var hsObj = {};
    hsObj.hsData = JSON.stringify(param);
    //获取数据并通过回调函数进行数据加载。
    ajaxPost(url,hsObj,sFunc);
}
//最大显示数据改变事件函数 html-数值改变 提示标语
function maxShowChange(html){
    //获得当前改变的值
    var changeVal = $(".maxShowData").val();
    //判断选的数值与检索出来的总数的大小
    if(changeVal < allCount){
        //创建分页函数
        createPage("pageBox",changeVal,pageTotle);
    }else{
        //创建分页函数
        createPage("pageBox",allCount,pageTotle);
    }
    if(changeVal >= 10000000){
        layer.tips('友情提示: '+html, '.maxShowData', {
            tips: [1, '#3595CC'],
            time: 4000
        });
    }
}
//创建分页函数 elem-分页容器(id)  allcount-总条数  limit-每一页显示的条数
function createPage(elem,allCount,limit){
    //分页插件
    layui.use('laypage', function(){
        var laypage = layui.laypage;
        //执行一个laypage实例
        laypage.render({
            elem: elem //分页容器 id
            ,count: allCount //数据总数，从服务端得到
            ,limit: limit  //每页显示的条数
            ,curr:currentPage //默认显示页码
            ,jump: function(obj, first){

                //首次不执行
                if(!first){
                    //更新当前显示的页码
                    currentPage = obj.curr;
                    //请求数据
                    getLogs(sendObj.url,sendObj.id,sendObj.searchWords,obj.curr,obj.limit);
                    //判断当前页
                    if(obj.curr * obj.limit >= $(".allCountBox>b").html()){
                        layer.tips('这是最后一页', '.layui-laypage-curr', {
                            tips: [1, '#3595CC'],
                            time: 4000
                        });
                    }else{
                        if($(".layui-laypage-curr").next().html() == "下一页"){
                            layer.tips('可通过更改最大显示数量，查看后面的数据', '.maxShowData', {
                                tips: [1, '#3595CC'],
                                time: 4000
                            });
                        }
                        /**/
                    }
                }else{//首次
                    firstGet = false;
                }
            }
        });
    });
}

//添加检索历史 函数
function prependHistory(words){
    //判断历史数组中是否存在    不存在 则添加
    if(historyArr.indexOf(words) === -1){
        var htmls = '<li title="'+words+'">'+words+'<i class="glyphicon glyphicon-remove" title="删除"></i></li>';
        $(".history_list").prepend(htmls);
        historyArr.push(words);
    }
}
//删除检索历史 事件
$(".history_list").on("click","li",function(){
    //获取当前点击的内容
    var searchWords = $(this).text();
    var ev = arguments.callee.caller.arguments[0] || window.event;
    var $this =$(ev.target);
    if($this[0].nodeName == "I"){
        $(this).remove();
        for(var i in historyArr){
            if(searchWords == historyArr[i]){
                historyArr.splice(i,1)
            }
        }
    }
})
//显示每一页数据函数          page为页数
function showLogs(logsArr){
    logDesArr = [];
    //获取日志类型
    logType = $(".device_logType").html();
    //判断日志类型
    if(logType == "log4j"){
        /****************1 log4j 日志列表格式*******************/
        var logLists = "<tr><td></td><td><b>暂无日志数据</b></td></tr>";//日志列表
        var logListTittle = "";//日志列名
        //拼接日志列名 只需拼接一次
        logListTittle += '<th width="35"><input type="checkbox" id="theadCheck"></th>'
            +	 '<th width="200">时间</th>'
            +	 '<th width="85">级别</th>'
            +	 '<th width="125">IP</th>'
            +	 '<th>日志内容</th>'
            +	 '<th width="60">操作</th>'
        //添加日志表头到页面
        $(".con_title").html(logListTittle);
        //判断日志是否为空  不为空则删除“暂无日志数据”提示
        if(logsArr != ''){
            logLists = '';
            var logDesArrIndex = 0;
            for(var i in logsArr){
                var obj =  filterObj(logsArr[i]);
                //替换风险状态
                var level = '';
                /*if(obj.operation_level == "INFO"){
                    level = '<span class="label label-info">INFO</span>'
                }else if(obj.operation_level == "ERROR"){
                    level = '<span class="label label-danger">ERROR</span>'
                }	*/
                logLists += '<tr data-id="'+obj.equipmentid+'" data-logId="'+obj.id+'" data-equipmentName="'+obj.equipmentname+'">'
                    +		 '<td> <input type="checkbox"></td>'
                    +       '<td class="logs_time">'+obj.logtime+'</td>'
                    +       '<td class="logs_level">'+obj.operation_level+'</td>'
                    +       '<td class="logs_ip">'+obj.ip+'</td>'
                    +       '<td class="logs_Mes"><p>'+obj.operation_des+'</p></td>'
                    +       '<td class="logs_tools" data-index="'+logDesArrIndex+'">'
                    +       	'<i class="glyphicon glyphicon-list-alt more" title="查看详情"></i>'
                    +       	'<i class="glyphicon glyphicon-remove removeLog" title="删除"></i>'
                    +       '</td>'
                    +   '</tr>'
                logDesArrIndex++
            }
        }

        //添加 日志列表到页面中
        $("#logs_list>tbody").html(logLists);
    }else if(logType == "mysql"){
        /****************1 mysql 日志列表格式*******************/
        var logLists = "<b>暂无日志数据</b>";//日志列表
        var logListTittle = "";//日志列名
        //拼接日志列名 只需拼接一次
        logListTittle += '<th width="35"><input type="checkbox" id="theadCheck"></th>'
            +	 '<th width="200">时间</th>'
            +	 '<th width="85">级别</th>'
            +	 '<th width="125">IP</th>'
            +	 '<th>日志内容</th>'
            +	 '<th width="60">操作</th>'
        //添加日志表头到页面
        $(".con_title").html(logListTittle);
        //判断日志是否为空  不为空则删除“暂无日志数据”提示
        if(logsArr != ''){
            logLists = '';
            var logDesArrIndex = 0;
            for(var i in logsArr){
                var obj =  filterObj(logsArr[i]);
                //替换风险状态
                var level = '';
                /*if(obj.operation_level == "INFO"){
                    level = '<span class="label label-info">INFO</span>'
                }else if(obj.operation_level == "ERROR"){
                    level = '<span class="label label-danger">ERROR</span>'
                }	*/
                logLists += '<tr data-id="'+obj.equipmentid+'" data-logId="'+obj.id+'" data-equipmentName="'+obj.equipmentname+'">'
                    +		 '<td> <input type="checkbox"></td>'
                    +       '<td class="logs_time">'+obj.logtime+'</td>'
                    +       '<td class="logs_level">'+obj.operation_level+'</td>'
                    +       '<td class="logs_ip">'+obj.ip+'</td>'
                    +       '<td class="logs_Mes"><p>'+obj.operation_des+'</p></td>'
                    +       '<td class="logs_tools" data-index="'+logDesArrIndex+'">'
                    +       	'<i class="glyphicon glyphicon-list-alt more" title="查看详情"></i>'
                    +       	'<i class="glyphicon glyphicon-remove removeLog" title="删除"></i>'
                    +       '</td>'
                    +   '</tr>'
                logDesArrIndex++
            }
        }
        //添加 日志列表到页面中
        $("#logs_list>tbody").html(logLists);
    }else if(logType == "winlog"){
        /****************1 windows 日志列表格式*******************/
        var logLists = "<b>暂无日志数据</b>";//日志列表
        var logListTittle = "";//日志列名
        //拼接日志列名 只需拼接一次
        logListTittle +='<th width="35"><input type="checkbox" id="theadCheck" onclick="theadCheck()"></th>'
            +	 '<th width="200">时间</th>'
            +	 '<th width="60">事件ID</th>'
            +	 '<th width="70">级别</th>'
            +	 '<th width="125">IP</th>'
            +	 '<th width="120">计算机名</th>'
            +	 '<th>日志内容</th>'
            +	 '<th width="60">操作</th>'
        //添加日志表头到页面
        $(".con_title").html(logListTittle);
        //判断日志是否为空  不为空则删除“暂无日志数据”提示
        if(logsArr != ''){
            logLists = '';
            var logDesArrIndex = 0;
            for(var i in logsArr){
                //var obj = logsArr[page][i];
                var obj =  filterObj(logsArr[i]);
                //替换风险状态
                /*var level = '';
                if(obj.operation_level == "INFO"){
                    level = '<span class="label label-info">INFO</span>'
                }else if(obj.operation_level == "ERROR"){
                    level = '<span class="label label-danger">ERROR</span>'
                }*/

                logLists += '<tr data-id="'+obj.equipmentid+'" data-logId="'+obj.id+'">'
                    +		 '<td> <input type="checkbox"></td>'
                    +       '<td class="logs_time">'+obj.logtime+'</td>'
                    +       '<td class="logs_eventId">'+obj.eventid+'</td>'
                    +       '<td class="logs_level">'+obj.operation_level+'</td>'
                    +       '<td class="logs_ip">'+obj.ip+'</td>'
                    +       '<td class="logs_hostName">'+obj.equipmentname+'</td>'
                    +       '<td class="logs_Mes"><p>'+obj.operation_des+'</p></td>'
                    +       '<td class="logs_tools" data-index="'+logDesArrIndex+'" data-logSource="'+obj.logSource+'" data-process_id="'+obj.process_id+'" data-provider_guid="'+obj.provider_guid+'" data-record_number="'+obj.record_number+'" data-thread_id="'+obj.thread_id+'" data-user_name="'+obj.user_name+'" data-user_type="'+obj.user_type+'">'
                    +       	'<i class="glyphicon glyphicon-list-alt more" title="查看详情"></i>'
                    +       	'<i class="glyphicon glyphicon-remove removeLog" title="删除"></i>'
                    +       '</td>'
                    +   '</tr>'
                logDesArrIndex++
            }
        }

        //添加 日志列表到页面中
        $("#logs_list>tbody").html(logLists);


    }else if(logType == "syslog"){
        /****************1 syslog 日志列表格式*******************/
        var logLists = "<b>暂无日志数据</b>";//日志列表
        var logListTittle = "";//日志列名
        //拼接日志列名 只需拼接一次
        logListTittle += '<th width="35"><input type="checkbox" id="theadCheck" onclick="theadCheck()"></th>'
            +	 '<th width="200">时间</th>'
            +	 '<th width="85">级别</th>'
            +	 '<th width="125">IP</th>'
            +	 '<th>日志内容</th>'
            +	 '<th width="60">操作</th>'
        //添加日志表头到页面
        $(".con_title").html(logListTittle);
        //判断日志是否为空  不为空则删除“暂无日志数据”提示
        if(logsArr != ''){
            logLists = '';
            var logDesArrIndex = 0;
            for(var i in logsArr){
                var obj =  filterObj(logsArr[i]);
                //替换风险状态
                var level = '';
                /*if(obj.operation_level == "INFO"){
                    level = '<span class="label label-info">INFO</span>'
                }else if(obj.operation_level == "ERROR"){
                    level = '<span class="label label-danger">ERROR</span>'
                }	*/

                logLists += '<tr data-id="'+obj.equipmentid+'" data-logId="'+obj.id+'" data-equipmentName="'+obj.equipmentname+'">'
                    +		 '<td> <input type="checkbox"></td>'
                    +       '<td class="logs_time">'+obj.logtime+'</td>'
                    +       '<td class="logs_level">'+obj.operation_level+'</td>'
                    +       '<td class="logs_ip">'+obj.ip+'</td>'
                    +       '<td class="logs_Mes"><p>'+obj.operation_des+'</p></td>'
                    +       '<td class="logs_tools" data-index="'+logDesArrIndex+'">'
                    +       	'<i class="glyphicon glyphicon-list-alt more" title="查看详情"></i>'
                    +       	'<i class="glyphicon glyphicon-remove removeLog" title="删除"></i>'
                    +       '</td>'
                    +   '</tr>'
                logDesArrIndex++
            }
        }

        //添加 日志列表到页面中
        $("#logs_list>tbody").html(logLists);

    }else if(logType == "packetfilteringfirewall_log"){
        /****************1 防火墙 日志列表格式*******************/
        var logLists = "<b>暂无日志数据</b>";//日志列表
        var logListTittle = "";//日志列名
        //拼接日志列名 只需拼接一次
        logListTittle += '<th width="200">时间</th>'
            +	 '<th width="100">级别</th>'
            +	 '<th width="100">日志类型</th>'
            +	 '<th width="120">资产名称</th>'
            +	 '<th width="125">IP</th>'
            +	 '<th>日志内容</th>'
            +	 '<th width="55">操作</th>'
        //添加日志表头到页面
        $(".con_title").html(logListTittle);
        //判断日志是否为空  不为空则删除“暂无日志数据”提示
        if(logsArr != ''){
            logLists = '';
            var logDesArrIndex = 0;
            for(var i in logsArr){

                var obj =  filterObj(logsArr[i]);
                if(obj.logtime == undefined){
                    obj.logtime = "-"
                }
                if(obj.operation_level == undefined){
                    obj.operation_level = "-"
                }
                if(obj.operation_des == undefined){
                    obj.operation_des = "-"
                }
                //替换风险状态
                /*var level = '';
                if(obj.operation_level == "INFO"){
                    level = '<span class="label label-info">INFO</span>'
                }else if(obj.operation_level == "ERROR"){
                    level = '<span class="label label-danger">ERROR</span>'
                }
                */
                //删除日志内容的<br/>标签
                logDesArr.push(obj.operation_des);
                var reg = new RegExp("<br/>","g");
                var logCon = obj.operation_des.replace(reg,"");
                logLists += '<tr data-id="'+obj.equipmentid+'" data-logId="'+obj.id+'">'
                    +       '<td class="logs_time"  width="200">'+obj.logtime+'</td>'
                    +       '<td class="logs_level">'+obj.operation_level+'</td>'
                    +       '<td class="logs_type">'+obj.type+'</td>'
                    +       '<td class="property_name" data-eId="'+obj.equipmentid+'"><a href="javascript:void(0)" title="点击查看资产详情">'+obj.equipmentname+'</a></td>'
                    +       '<td class="logs_ip">'+obj.ip+'</td>'
                    +       '<td class="logs_Mes"><p>'+logCon+'</p></td>'
                    +       '<td class="logs_tools" data-index="'+logDesArrIndex+'">'
                    +       	'<i class="glyphicon glyphicon-list-alt more" title="查看详情"></i>'
                    +       	'<i class="glyphicon glyphicon-remove removeLog" title="删除"></i>'
                    +       '</td>'
                    +   '</tr>'


                logDesArrIndex++;
            }
        }


        //添加 日志列表到页面中
        $("#logs_list>tbody").html(logLists);
    }else if(logType == "applog"){
        /****************1 应用 日志列表格式*******************/
        var logLists = "<b>暂无日志数据</b>";//日志列表
        var logListTittle = "";//日志列名
        //拼接日志列名 只需拼接一次
        logListTittle += '<th width="200">时间</th>'
            +	 '<th width="100">级别</th>'
            +	 '<th width="100">日志类型</th>'
            +	 '<th width="120">资产名称</th>'
            +	 '<th width="125">IP</th>'
            +	 '<th>日志内容</th>'
            +	 '<th width="55">操作</th>'
        //添加日志表头到页面
        $(".con_title").html(logListTittle);
        //判断日志是否为空  不为空则删除“暂无日志数据”提示
        if(logsArr != ''){
            logLists = '';
            var logDesArrIndex = 0;
            for(var i in logsArr){

                var obj =  filterObj(logsArr[i]);
                if(obj.logtime == undefined){
                    obj.logtime = "-"
                }
                if(obj.operation_level == undefined){
                    obj.operation_level = "-"
                }
                if(obj.operation_des == undefined){
                    obj.operation_des = "-"
                }
                //替换风险状态
                /*var level = '';
                if(obj.operation_level == "INFO"){
                    level = '<span class="label label-info">INFO</span>'
                }else if(obj.operation_level == "ERROR"){
                    level = '<span class="label label-danger">ERROR</span>'
                }
                */
                //删除日志内容的<br/>标签
                logDesArr.push(obj.operation_des);
                var reg = new RegExp("<br/>","g");
                var logCon = obj.operation_des.replace(reg,"");
                logLists += '<tr data-id="'+obj.equipmentid+'" data-logId="'+obj.id+'">'
                    +       '<td class="logs_time"  width="200">'+obj.logtime+'</td>'
                    +       '<td class="logs_level">'+obj.operation_level+'</td>'
                    +       '<td class="logs_type">'+obj.type+'</td>'
                    +       '<td class="property_name" data-eId="'+obj.equipmentid+'"><a href="javascript:void(0)" title="点击查看资产详情">'+obj.equipmentname+'</a></td>'
                    +       '<td class="logs_ip">'+obj.ip+'</td>'
                    +       '<td class="logs_Mes"><p>'+logCon+'</p></td>'
                    +       '<td class="logs_tools" data-index="'+logDesArrIndex+'">'
                    +       	'<i class="glyphicon glyphicon-list-alt more" title="查看详情"></i>'
                    +       	'<i class="glyphicon glyphicon-remove removeLog" title="删除"></i>'
                    +       '</td>'
                    +   '</tr>'


                logDesArrIndex++;
            }
        }
        //添加 日志列表到页面中
        $("#logs_list>tbody").html(logLists);
    }else if(logType == "netflow"){
        /****************1 netflow 日志列表格式*******************/
        var logLists = "<b>暂无日志数据</b>";//日志列表
        var logListTittle = "";//日志列名
        //拼接日志列名 只需拼接一次
        logListTittle += '<th width="200">时间</th>'
            +	 '<th width="100">级别</th>'
            +	 '<th width="100">日志类型</th>'
            +	 '<th width="100">协议名称</th>'
            +	 '<th width="120">资产名称</th>'
            +	 '<th width="125">IP</th>'
            +	 '<th>日志内容</th>'
            +	 '<th width="55">操作</th>'
        //添加日志表头到页面
        $(".con_title").html(logListTittle);
        //判断日志是否为空  不为空则删除“暂无日志数据”提示
        if(logsArr != ''){
            logLists = '';
            var logDesArrIndex = 0;
            for(var i in logsArr){

                var obj =  filterObj(logsArr[i]);
                if(obj.logtime == undefined){
                    obj.logtime = "-"
                }
                if(obj.operation_level == undefined){
                    obj.operation_level = "-"
                }
                if(obj.operation_des == undefined){
                    obj.operation_des = "-"
                }
                //替换风险状态
                /*var level = '';
                if(obj.operation_level == "INFO"){
                    level = '<span class="label label-info">INFO</span>'
                }else if(obj.operation_level == "ERROR"){
                    level = '<span class="label label-danger">ERROR</span>'
                }
                */
                //删除日志内容的<br/>标签
                logDesArr.push(obj.operation_des);
                var reg = new RegExp("<br/>","g");
                var logCon = obj.operation_des.replace(reg,"");
                logLists += '<tr data-id="'+obj.equipmentid+'" data-logId="'+obj.id+'">'
                    +       '<td class="logs_time"  width="200">'+obj.logtime+'</td>'
                    +       '<td class="logs_level">'+obj.operation_level+'</td>'
                    +       '<td class="logs_type">'+obj.type+'</td>'
                    +       '<td class="logs_protocol_name">'+obj.protocol_name+'</td>'
                    +       '<td class="property_name" data-eId="'+obj.equipmentid+'"><a href="javascript:void(0)" title="点击查看资产详情">'+obj.equipmentname+'</a></td>'
                    +       '<td class="logs_ip">'+obj.ip+'</td>'
                    +       '<td class="logs_Mes"><p>'+logCon+'</p></td>'
                    +       '<td class="logs_tools" data-index="'+logDesArrIndex+'">'
                    +       	'<i class="glyphicon glyphicon-list-alt more" title="查看详情"></i>'
                    +       	'<i class="glyphicon glyphicon-remove removeLog" title="删除"></i>'
                    +       '</td>'
                    +   '</tr>'


                logDesArrIndex++;
            }
        }
        //添加 日志列表到页面中
        $("#logs_list>tbody").html(logLists);
    }else if(logType == "defaultpacket"){
        /****************1 defaultpacket 日志列表格式*******************/
        var logLists = "<b>暂无日志数据</b>";//日志列表
        var logListTittle = "";//日志列名
        //拼接日志列名 只需拼接一次
        logListTittle += '<th width="200">时间</th>'
            +	 '<th>源IP</th>'
            +	 '<th>目的IP</th>'
            +	 '<th>源端口</th>'
            +	 '<th>目的端口</th>'
            +	 '<th>传输层协议</th>'
            +	 '<th>应用层协议</th>'
            +	 '<th>加密协议</th>'
            +	 '<th>网路数据来源</th>'
            +	 '<th width="55px">操作</th>'
        //添加日志表头到页面
        $(".con_title").html(logListTittle);
        //判断日志是否为空  不为空则删除“暂无日志数据”提示
        if(logsArr != ''){
            logLists = '';
            var logDesArrIndex = 0;
            for(var i in logsArr){

                var obj =  filterObj(logsArr[i]);
                if(obj.logtime == undefined){
                    obj.logtime = "-"
                }
                if(obj.operation_level == undefined){
                    obj.operation_level = "-"
                }
                if(obj.operation_des == undefined){
                    obj.operation_des = "-"
                }
                if(obj.encryption_based_protection_protocol == undefined){
                    obj.encryption_based_protection_protocol = "-"
                }
                if(obj.application_layer_protocol == undefined){
                    obj.application_layer_protocol = "-"
                }
                if(obj.request_type == undefined){
                    obj.request_type = "-"
                }
                if(obj.request_url == undefined){
                    obj.request_url = "-"
                }
                if(obj.response_state == undefined){
                    obj.response_state = "-"
                }

                //删除日志内容的<br/>标签
                logDesArr.push(obj.operation_des);
                var reg = new RegExp("<br/>","g");
                var logCon = obj.operation_des.replace(reg,"");
                logLists += '<tr data-id="'+obj.equipmentid+'" data-logId="'+obj.id+'">'
                    +       '<td class="logs_time"  width="200">'+obj.logtime+'</td>'
                    +       '<td class="logs_ipv4_src_addr">'+obj.ipv4_src_addr+'</td>'
                    +       '<td class="logs_ipv4_dst_addr">'+obj.ipv4_dst_addr+'</td>'
                    +       '<td class="logs_l4_src_port">'+obj.l4_src_port+'</td>'
                    +       '<td class="logs_l4_dst_port">'+obj.l4_dst_port+'</td>'
                    +       '<td class="logs_protocol_name">'+obj.protocol_name+'</td>'
                    +       '<td class="logs_protocol_name">'+obj.application_layer_protocol +'</td>'
                    +       '<td class="logs_protocol_name">'+obj.encryption_based_protection_protocol+'</td>'
                    +       '<td class="logs_protocol_name">'+obj.packet_source+'</td>'
                    +       '<td class="logs_tools" data-index="'+logDesArrIndex+'">'
                    +       	'<i class="glyphicon glyphicon-list-alt more" title="查看详情"></i>'
                    +       	'<i class="glyphicon glyphicon-remove removeLog" title="删除"></i>'
                    +       '</td>'
                    +   '</tr>'
                logDesArrIndex++;
            }
        }
        //添加 日志列表到页面中
        $("#logs_list>tbody").html(logLists);
    }else if(logType == "defaultpacket_http"){
        /****************http 日志列表格式*******************/
        var logLists = "<b>暂无日志数据</b>";//日志列表
        var logListTittle = "";//日志列名
        //拼接日志列名 只需拼接一次
        logListTittle += '<th width="200">时间</th>'
            +	 '<th>源地址</th>'
            +	 '<th>目的地址</th>'
            +	 '<th>源端口</th>'
            +	 '<th>目的端口</th>'
            +	 '<th>请求/响应</th>'
            +	 '<th>请求方法</th>'
            +	 '<th>请求url</th>'
            +	 '<th>响应状态</th>'
            +	 '<th>内容</th>'
            +	 '<th width="55px">操作</th>'
        //添加日志表头到页面
        $(".con_title").html(logListTittle);
        //判断日志是否为空  不为空则删除“暂无日志数据”提示
        if(logsArr != ''){
            logLists = '';
            var logDesArrIndex = 0;
            for(var i in logsArr){

                var obj =  filterObj(logsArr[i]);
                if(obj.logtime == undefined){
                    obj.logtime = "-"
                }
                if(obj.operation_level == undefined){
                    obj.operation_level = "-"
                }
                if(obj.operation_des == undefined){
                    obj.operation_des = "-"
                }
                if(obj.application_layer_protocol == undefined){
                    obj.application_layer_protocol = "-"
                }
                if(obj.protocol_name == undefined){
                    obj.protocol_name = "-"
                }
                if(obj.request_type == undefined){
                    obj.request_type = "-"
                }
                if(obj.request_url == undefined){
                    obj.request_url = "-"
                }
                if(obj.response_state == undefined){
                    obj.response_state = "-"
                }
                //替换风险状态
                /*var level = '';
                if(obj.operation_level == "INFO"){
                    level = '<span class="label label-info">INFO</span>'
                }else if(obj.operation_level == "ERROR"){
                    level = '<span class="label label-danger">ERROR</span>'
                }
                */
                //删除日志内容的<br/>标签
                logDesArr.push(obj.operation_des);
                var reg = new RegExp("<br/>","g");
                var logCon = obj.operation_des.replace(reg,"");
                logLists += '<tr data-id="'+obj.equipmentid+'" data-logId="'+obj.id+'">'
                    +       '<td class="logs_time"  width="200">'+obj.logtime+'</td>'
                    +       '<td class="logs_source_ip">'+obj.ipv4_src_addr+'</td>'
                    +       '<td class="logs_des_ip">'+obj.ipv4_dst_addr+'</td>'
                    +       '<td class="logs_source_port">'+obj.l4_src_port+'</td>'
                    +       '<td class="logs_des_port">'+obj.l4_dst_port+'</td>'
                    +       '<td class="logs_requestorresponse">'+obj.requestorresponse+'</td>'
                    +       '<td class="logs_request_type">'+obj.request_type+'</td>'
                    +       '<td class="logs_request_url ">'+obj.request_url  +'</td>'
                    +       '<td class="logs_response_state">'+obj.response_state+'</td>'
                    +       '<td class="logs_Mes"><p><script type="text/html" style="display:block">'+logCon+'</script></p></td>'
                    +       '<td class="logs_tools" data-index="'+logDesArrIndex+'">'
                    +       	'<i class="glyphicon glyphicon-list-alt more" title="查看详情"></i>'
                    +       	'<i class="glyphicon glyphicon-remove removeLog" title="删除"></i>'
                    +       '</td>'
                    +   '</tr>'
                logDesArrIndex++;
            }
        }
        //添加 日志列表到页面中
        $("#logs_list>tbody").html(logLists);
    }else if(logType == "dns"){
        /**************** dns 日志列表格式*******************/
        var logLists = "<b>暂无日志数据</b>";//日志列表
        var logListTittle = "";//日志列名
        //拼接日志列名 只需拼接一次
        logListTittle += '<th width="35"><input type="checkbox" id="theadCheck"></th>'
            +	 '<th width="200">时间</th>'
            +	 '<th>资产名称</th>'
            +	 '<th>客户IP</th>'
            +	 '<th>dns解析类型</th>'
            +	 '<th>访问域名</th>'
            +	 '<th>DNS view</th>'
            +	 '<th>DNS服务器</th>'
            +	 '<th width="55">操作</th>'
        //添加日志表头到页面
        $(".con_title").html(logListTittle);
        //判断日志是否为空  不为空则删除“暂无日志数据”提示
        if(logsArr != ''){
            logLists = '';
            var logDesArrIndex = 0;
            for(var i in logsArr){

                var obj =  filterObj(logsArr[i]);
                if(obj.logtime == undefined){
                    obj.logtime = "-"
                }
                if(obj.operation_level == undefined){
                    obj.operation_level = "-"
                }
                if(obj.operation_des == undefined){
                    obj.operation_des = "-"
                }
                //替换风险状态
                /*var level = '';
                if(obj.operation_level == "INFO"){
                    level = '<span class="label label-info">INFO</span>'
                }else if(obj.operation_level == "ERROR"){
                    level = '<span class="label label-danger">ERROR</span>'
                }
                */
                //删除日志内容的<br/>标签
                logDesArr.push(obj.operation_des);
                var reg = new RegExp("<br/>","g");
                var logCon = obj.operation_des.replace(reg,"");
                logLists += '<tr data-id="'+obj.equipmentid+'" data-logId="'+obj.id+'">'
                    +		 '<td> <input type="checkbox"></td>'
                    +       '<td class="logs_time"  width="200">'+obj.logtime+'</td>'
                    +       '<td class="property_name" data-eId="'+obj.equipmentid+'"><a href="javascript:void(0)" title="点击查看资产详情">'+obj.equipmentname+'</a></td>'
                    +       '<td class="logs_clientip">'+obj.dns_clientip+'</td>'
                    +       '<td class="logs_ana_type">'+obj.dns_ana_type+'</td>'
                    +       '<td class="logs_domain_name">'+obj.dns_domain_name+'</td>'
                    +       '<td class="logs_dns_view">'+obj.dns_view+'</td>'
                    +       '<td class="logs_dns_server">'+obj.dns_server+'</td>'
                    +       '<td class="logs_tools" data-index="'+logDesArrIndex+'">'
                    +       	'<i class="glyphicon glyphicon-list-alt more" title="查看详情"></i>'
                    +       	'<i class="glyphicon glyphicon-remove removeLog" title="删除"></i>'
                    +       '</td>'
                    +   '</tr>'


                logDesArrIndex++;
            }
        }
        //添加 日志列表到页面中
        $("#logs_list>tbody").html(logLists);

    }else if(logType == "dhcp"){
        /**************** dhcp 日志列表格式*******************/
        var logLists = "<b>暂无日志数据</b>";//日志列表
        var logListTittle = "";//日志列名
        //拼接日志列名 只需拼接一次
        logListTittle += '<th width="35"><input type="checkbox" id="theadCheck"></th>'
            +	 '<th width="200">时间</th>'
            +	 '<th>资产名称</th>'
            +	 '<th>dhcp类型</th>'
            +	 '<th>mac地址</th>'
            +	 '<th>中继设备地址</th>'
            +	 '<th>客户端IP</th>'
            +	 '<th width="55">操作</th>'
        //添加日志表头到页面
        $(".con_title").html(logListTittle);
        //判断日志是否为空  不为空则删除“暂无日志数据”提示
        if(logsArr != ''){
            logLists = '';
            var logDesArrIndex = 0;
            for(var i in logsArr){
                var obj =  filterObj(logsArr[i]);
                //判断有无字段  若无 替换成“-”
                if(obj.logtime == undefined){
                    obj.logtime = "-"
                }
                if(obj.client_ip == undefined){
                    obj.client_ip = "-"
                }
                if(obj.dhcp_type == undefined){
                    obj.dhcp_type = "-"
                }
                if(obj.client_mac == undefined){
                    obj.client_mac = "-"
                }
                if(obj.relay_ip == undefined){
                    obj.relay_ip = "-"
                }
                if(obj.operation_des == undefined){
                    obj.operation_des = "-"
                }
                //删除日志内容的<br/>标签
                logDesArr.push(obj.operation_des);
                var reg = new RegExp("<br/>","g");
                var logCon = obj.operation_des.replace(reg,"");
                logLists += '<tr data-id="'+obj.equipmentid+'" data-logId="'+obj.id+'">'
                    +		 '<td> <input type="checkbox"></td>'
                    +       '<td class="logs_time"  width="200">'+obj.logtime+'</td>'
                    +       '<td class="property_name" data-eId="'+obj.equipmentid+'"><a href="javascript:void(0)" title="点击查看资产详情">'+obj.equipmentname+'</a></td>'
                    +       '<td class="logs_clientip">'+obj.dhcp_type+'</td>'
                    +       '<td class="logs_ana_type">'+obj.client_mac+'</td>'
                    +       '<td class="logs_domain_name">'+obj.relay_ip+'</td>'
                    +       '<td class="logs_domain_name">'+obj.client_ip+'</td>'
                    +       '<td class="logs_tools" data-index="'+logDesArrIndex+'">'
                    +       	'<i class="glyphicon glyphicon-list-alt more" title="查看详情"></i>'
                    +       	'<i class="glyphicon glyphicon-remove removeLog" title="删除"></i>'
                    +       '</td>'
                    +   '</tr>'


                logDesArrIndex++;
            }
        }
        //添加 日志列表到页面中
        $("#logs_list>tbody").html(logLists);
    }else if(logType == "app_file" || logType == "app_apache"){
        /**************** app_file/app_apache 日志列表格式*******************/
        var logLists = "<b>暂无日志数据</b>";//日志列表
        var logListTittle = "";//日志列名
        //拼接日志列名 只需拼接一次
        logListTittle += '<th width="200">时间</th>'
            +	 '<th width="100">级别</th>'
            +	 '<th width="100">日志类型</th>'
            +	 '<th width="120">资产名称</th>'
            +	 '<th width="125">IP</th>'
            +	 '<th>日志内容</th>'
            +	 '<th width="55">操作</th>'
        //添加日志表头到页面
        $(".con_title").html(logListTittle);
        //判断日志是否为空  不为空则删除“暂无日志数据”提示
        if(logsArr != ''){
            logLists = '';
            var logDesArrIndex = 0;
            for(var i in logsArr){

                var obj =  filterObj(logsArr[i]);
                if(obj.logtime == undefined){
                    obj.logtime = "-"
                }
                if(obj.operation_level == undefined){
                    obj.operation_level = "-"
                }
                if(obj.operation_des == undefined){
                    obj.operation_des = "-"
                }
                if(obj.user == undefined){
                    obj.user = "-"
                }
                //替换风险状态
                /*var level = '';
                if(obj.operation_level == "INFO"){
                    level = '<span class="label label-info">INFO</span>'
                }else if(obj.operation_level == "ERROR"){
                    level = '<span class="label label-danger">ERROR</span>'
                }
                */
                //删除日志内容的<br/>标签
                logDesArr.push(obj.operation_des);
                var reg = new RegExp("<br/>","g");
                var logCon = obj.operation_des.replace(reg,"");
                logLists += '<tr data-id="'+obj.equipmentid+'" data-logId="'+obj.id+'">'
                    +       '<td class="logs_time"  width="200">'+obj.logtime+'</td>'
                    +       '<td class="logs_level">'+obj.operation_level+'</td>'
                    +       '<td class="logs_type">'+obj.type+'</td>'
                    +       '<td class="property_name" data-eId="'+obj.equipmentid+'"><a href="javascript:void(0)" title="点击查看资产详情">'+obj.equipmentname+'</a></td>'
                    +       '<td class="logs_ip">'+obj.ip+'</td>'
                    +       '<td class="logs_Mes"><p>'+logCon+'</p></td>'
                    +       '<td class="logs_tools" data-index="'+logDesArrIndex+'">'
                    +       	'<i class="glyphicon glyphicon-list-alt more" title="查看详情"></i>'
                    +       '</td>'
                    +   '</tr>'


                logDesArrIndex++;
            }
        }
        //添加 日志列表到页面中
        $("#logs_list>tbody").html(logLists);
    } else{
        /**************** 通用 日志列表格式*******************/
        var logLists = "<b>暂无日志数据</b>";//日志列表
        var logListTittle = "";//日志列名
        /*//拼接日志列名 只需拼接一次
        logListTittle += '<th width="200">时间</th>'
                      +	 '<th width="100">级别</th>'
                      +	 '<th width="100">日志类型</th>'
                      +	 '<th width="120">资产名称</th>'
                      +	 '<th>日志内容</th>'
                      +	 '<th width="50">操作</th>'
        //添加日志表头到页面
        $(".con_title").html(logListTittle);*/
        //判断日志是否为空  不为空则删除“暂无日志数据”提示
        if(logsArr != ''){
            logLists = '';
            var logDesArrIndex = 0;
            for(var i in logsArr){

                var obj =  filterObj(logsArr[i]);
                if(obj.logtime == undefined){
                    obj.logtime = "-"
                }
                if(obj.operation_level == undefined){
                    obj.operation_level = "-"
                }
                if(obj.operation_des == undefined){
                    obj.operation_des = "-"
                }
                if(obj.user == undefined){
                    obj.user = "-"
                }
                //替换风险状态
                /*var level = '';
                if(obj.operation_level == "INFO"){
                    level = '<span class="label label-info">INFO</span>'
                }else if(obj.operation_level == "ERROR"){
                    level = '<span class="label label-danger">ERROR</span>'
                }
                */
                //删除日志内容的<br/>标签
                logDesArr.push(obj.operation_des);
                var reg = new RegExp("<br/>","g");
                var logCon = obj.operation_des.replace(reg,"");
                logLists += '<tr data-id="'+obj.equipmentid+'" data-logId="'+obj.id+'">'
                    +       '<td class="logs_time"  width="200">'+obj.logtime+'</td>'
                    +       '<td class="logs_level">'+obj.operation_level+'</td>'
                    +       '<td class="logs_type">'+obj.type+'</td>'
                    /* +       '<td class="logs_user">'+obj.user+'</td>'*/
                    +       '<td class="property_name" data-eId="'+obj.equipmentid+'"><a href="javascript:void(0)" title="点击查看资产详情">'+obj.equipmentname+'</a></td>'
                    +       '<td class="logs_ip">'+obj.ip+'</td>'
                    +       '<td class="logs_Mes"><p>'+logCon+'</p></td>'
                    +       '<td class="logs_tools" data-index="'+logDesArrIndex+'">'
                    +       	'<i class="glyphicon glyphicon-list-alt more" title="查看详情"></i>'
                    +       '</td>'
                    +   '</tr>'


                logDesArrIndex++;
            }
        }


        //添加 日志列表到页面中
        $("#logs_list>tbody").html(logLists);

        //判断是不是精确查询 出来的数据   $("#theadCheck")[0] != undefined --- 精确查询
        if($("#theadCheck")[0] != undefined){
            $(".logs_tools").append('<i class="glyphicon glyphicon-remove removeLog" title="删除日志"></i>');
            $(".logs_tools").parents("tr").prepend('<td> <input type="checkbox"></td>')
        }
    }

    //表头中的复选框 点击全选或取消全选
    $('#theadCheck').click(function(){
        //如果checkbox_Status == 0 则未选中
        if($('#theadCheck')[0].checked == true){
            //更改状态
            $("#logs_list>tbody input[type=checkbox]").prop("checked", "checked");
        }else if($('#theadCheck')[0].checked == false){
            //更改状态
            $("#logs_list>tbody input[type=checkbox]").removeAttr("checked");
        }
    })
    //鼠标拖选文字功能
    /*$(".equipmentLogBody .logs_Mes").selectText({
        "sFunc":function(obj){
            var index = obj.eventDom.attr("data-index")
            var sendObj = {};
            sendObj.name = obj.inputText;
            sendObj.feature = obj.selectedText;
            sendObj.userId = JSON.parse(localStorage.getItem('LoginUser'))[0].id;
            sendObj.equipmentId = logDetailArr[index].equipmentid;
            sendObj.type = logDetailArr[index].type;
            sendObj.equipmentUserId = logDetailArr[index].userid;
            //console.log('选中文本：'+selectedText+' 输入文本：'+inputText)
            //成功回调函数
            var sfunc = function(data){
                if(data.success == "true"){
                    layer.msg(data.message,{icon: 1});
                    //重新加载数据
                    getData();
                }else if(data.success == "false"){//失败
                    layer.msg(data.message,{icon: 5});
                }
            }
            ajaxPost('../../action/insert.do',sendObj,sfunc);
        }
    });*/
    //表格拖动列宽
    $("#logs_list").dragTableCol();
}
var htmlNum2 = 0;
//点击查看设备详情
$("#logs_list").on("click","a",function(){
    var id =  $(this).parent().attr("data-eId");
    if(id == "unknown"){
        layer.msg("资产未知",{icon:5})
    }else{
        localStorage.setItem("equipmentid",id);
        //拼接导航
        var html ='<a href="javascript:;" class="active J_menuTab" data-id="equipmentid'+htmlNum2+'">资产管理<i class="fa fa-times-circle"></i></a>'
        //移除导航菜单选中属性
        $('.page-tabs-content', parent.document).click().children("a").removeClass("active");
        //添加导航菜单
        $('.page-tabs-content', parent.document).click().append(html);
        var iframe = '<iframe class="J_iframe" width="100%" height="100%" src="device/device.html?v=4.0" frameborder="0" data-id="equipmentid'+htmlNum2+'" seamless="" style="display: inline;"></iframe>'
        //移除其他页面
        $('#content-main', parent.document).click().children("iframe").hide();
        $('#content-main', parent.document).click().append(iframe);
        htmlNum2++;
    }

})

//查看详情 点击事件
$("#logs_list").on("click",".more",function(){
    var logIndex = $(this).parent().attr("data-index");
    //获取日志类型
    //var logType = logDetailArr[logIndex].type;
    /*if(logType == undefined){//logType ！= undefined 单个设备的日志
        logType = $(this).parent().siblings('.logs_type').html();
    }*/
    /*if(logType == undefined){//精确查询与日志检索

    }*/
    var logType = logDetailArr[logIndex].type;
    //判断是否是http日志类型
    if(logDetailArr[logIndex].application_layer_protocol == "http" ){
        logType = "defaultpacket_http"
    }
    //获取日志Id
    var id = $(this).parents("tr").attr("data-id");
    //获取日志时间
    var logsTime = $(this).parent().siblings('.logs_time').html();
    //获取ip
    var logsIp = $(this).parent().siblings('.logs_ip').html();
    //获取用户名
    var userName = $(this).parent().siblings('.logs_user').html();
    //获取hostname
    var logsLevel = $(this).parent().siblings('.logs_level').html();
    //获取日志内容信息
    var logsCon = $(this).parent().siblings('.logs_Mes').children('p').html();
    //获取日志模块
    var logsName = $(this).parent().siblings('.logs_LogName').html();
    //获取事件ID
    var logsEventId = $(this).parent().siblings('.logs_eventId').html();
    //获取日志任务类型
    var logsTaskType = $(this).parent().siblings('.logs_taskType').html();
    //获取日志关键字
    var logsKeyWords = $(this).parent().siblings('.logs_keywords').html();
    //获取计算机名
    var logsHostName = $(this).parent().siblings('.logs_hostName').html();
    //获取资产名称  log4j
    var equipmentName = $(this).parents("tr").attr("data-equipmentname");
    //获取日志模块
    var logsIp = $(this).parent().siblings('.logs_ip').html();
    if(logType == "winlog"){
        //获得日志来源
        var logsSource = $(this).parent().attr("data-logSource");
        if(logsSource == "undefined"){
            logsSource = "-"
        }
        //获得进程ID
        var processId = $(this).parent().attr("data-process_id");
        if(processId == "undefined"){
            processId = "-"
        }
        //获得进程GUID
        var processGuid = $(this).parent().attr("data-provider_guid");
        if(processGuid == "undefined"){
            processGuid = "-";
        }
        //获得记录号
        var recordNumber = $(this).parent().attr("data-record_number");
        if(recordNumber == "undefined"){
            recordNumber = "-"
        }
        //获得线程ID
        var threadId = $(this).parent().attr("data-thread_id");
        if(threadId == "undefined"){
            threadId = "-"
        }
        //获得用户
        var userName = $(this).parent().attr("data-user_name");
        if(userName == "undefined"){
            userName = "-"
        }
        //获得用户类型
        var userType= $(this).parent().attr("data-user_type");
        if(userType == "undefined"){
            userType = "-"
        }
        //拼接弹窗 html
        var html = '<div class="layer_box">'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">时间:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].logtime+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">事件ID:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].eventid+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">级别:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].operation_level+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">IP:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].ip+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">计算机名:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].equipmentname+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:24px">'
            +			'<div class="col-xs-3">日志内容:</div>'
            +			'<div class="col-xs-9 layCen logdes" data-index="'+logIndex+'">'+logDetailArr[logIndex].operation_des+'</div>'
            +		'</div>'
            +	'</div>'
    }else if(logType == "log4j" || logType == "syslog" || logType == "mysql"){
        //拼接弹窗 html
        var html = '<div class="layer_box">'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">时间:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].logtime+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">级别:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].operation_level+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">日志类型:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].type+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">资产名称:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].equipmentname+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">IP:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].ip+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:24px">'
            +			'<div class="col-xs-3">日志内容:</div>'
            +			'<div class="col-xs-9 layCen logdes" data-index="'+logIndex+'">'+logDetailArr[logIndex].operation_des+'</div>'
            +		'</div>'
            +	'</div>'
    }else if(logType == "packetfilteringfirewall_log"){
        //拼接弹窗 html
        var html = '<div class="layer_box">'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">时间:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].logtime+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">级别:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].operation_level+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">资产名称:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].equipmentname+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">IP:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].ip+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">dsp_msg:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].dsp_msg+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">act:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].act+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">mod:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].mod+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:24px">'
            +			'<div class="col-xs-3">日志内容:</div>'
            +			'<div class="col-xs-9 layCen logdes" data-index="'+logIndex+'">'+logDetailArr[logIndex].operation_des+'</div>'
            +		'</div>'
            +	'</div>'
    }else if(logType == "netflow"){
        //拼接弹窗 html
        var html = '<div class="layer_box">'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">时间:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].logtime+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">级别:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].operation_level+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">资产名称:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].equipmentname+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">IP:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].ip+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">IP协议:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].protocol+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">协议名称:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].protocol_name+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">源IP:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].ipv4_src_addr+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">目的IP:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].ipv4_dst_addr+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">源端口:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].l4_src_port+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">目的端口:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].l4_dst_port+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:24px">'
            +			'<div class="col-xs-3">日志内容:</div>'
            +			'<div class="col-xs-9 layCen logdes" data-index="'+logIndex+'">'+logDetailArr[logIndex].operation_des+'</div>'
            +		'</div>'
            +	'</div>'
    }else if(logType == "dns"){
        //拼接弹窗 html
        var html = '<div class="layer_box">'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">时间:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].logtime+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">客户IP:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].dns_clientip+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">资产名称:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].equipmentname+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">IP:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].ip+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">DNS view:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].dns_view+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">访问域名:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].dns_domain_name+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">dns解析类型:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].dns_ana_type+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">dns服务器:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].dns_server+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:24px">'
            +			'<div class="col-xs-3">日志内容:</div>'
            +			'<div class="col-xs-9 layCen logdes" data-index="'+logIndex+'">'+logDetailArr[logIndex].operation_des+'</div>'
            +		'</div>'
            +	'</div>'
    }else if(logType == "dhcp"){
        //判断有无字段  若无替换成‘-’
        if(logDetailArr[logIndex].client_ip == undefined){
            logDetailArr[logIndex].client_ip = "-"
        }
        if(logDetailArr[logIndex].dhcp_type == undefined){
            logDetailArr[logIndex].dhcp_type = "-"
        }
        if(logDetailArr[logIndex].client_mac == undefined){
            logDetailArr[logIndex].client_mac = "-"
        }
        if(logDetailArr[logIndex].relay_ip == undefined){
            logDetailArr[logIndex].relay_ip = "-"
        }
        if(logDetailArr[logIndex].client_hostname == undefined){
            logDetailArr[logIndex].client_hostname = "-"
        }
        if(logDetailArr[logIndex].error_log == undefined){
            logDetailArr[logIndex].error_log = "-"
        }
        if(logDetailArr[logIndex].network_error == undefined){
            logDetailArr[logIndex].network_error = "-"
        }
        //拼接弹窗 html
        var html = '<div class="layer_box">'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">时间:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].logtime+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">资产名称:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].equipmentname+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">dhcp类型:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].dhcp_type+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">mac地址:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].client_mac+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">客户端主机名:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].client_hostname+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">中继设备地址:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].relay_ip+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">错误信息:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].error_log+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">客户端ip:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].client_ip+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">出现问题网段:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].network_error+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:24px">'
            +			'<div class="col-xs-3">日志内容:</div>'
            +			'<div class="col-xs-9 layCen logdes" data-index="'+logIndex+'">'+logDetailArr[logIndex].operation_des+'</div>'
            +		'</div>'
            +	'</div>'
    }else if(logType == "defaultpacket_http"){
        //拼接弹窗 html
        var html = '<div class="layer_box">'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">时间:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].logtime+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">源地址:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].ipv4_src_addr+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">目的地址:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].ipv4_dst_addr +'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">源端口:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].l4_src_port+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">目的端口:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].l4_dst_port+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">请求或响应:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].requestorresponse+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">请求方法:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].request_type+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">请求url:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].request_url+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">响应状态:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].response_state+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:24px">'
            +			'<div class="col-xs-3">日志内容:</div>'
            +			'<div class="col-xs-9 layCen logdes" data-index="'+logIndex+'"><script type="text/html" style="display:block">'+logDetailArr[logIndex].operation_des+'</script></div>'
            +		'</div>'
            +	'</div>'
    }else if(logType == 'defaultpacket'){
        //拼接弹窗 html
        var html = '<div class="layer_box">'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">时间:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].logtime+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">源IP:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].ipv4_src_addr+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">目的IP:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].ipv4_dst_addr+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">源端口:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].l4_src_port+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">目的端口:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].l4_dst_port+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">传输层协议:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].protocol_name+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">应用层协议:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].application_layer_protocol+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">网路数据来源:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].packet_source+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">加密协议:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].encryption_based_protection_protocol+'</div>'
            +		'</div>'
            +	'</div>'
    } else{
        //var logsCon = logDesArr[index];
        //拼接弹窗 html
        var html = '<div class="layer_box">'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">时间:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].logtime+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">级别:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].operation_level+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px" class="ltype">'
            +			'<div class="col-xs-3">日志类型:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].type+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">资产名称:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].equipmentname+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:50px">'
            +			'<div class="col-xs-3">IP:</div>'
            +			'<div class="col-xs-9 layCen">'+logDetailArr[logIndex].ip+'</div>'
            +		'</div>'
            +		'<div class="row" style="line-height:24px">'
            +			'<div class="col-xs-3">日志内容:</div>'
            +			'<div class="col-xs-9 layCen logdes" data-index="'+logIndex+'">'+logDetailArr[logIndex].operation_des+'</div>'
            +		'</div>'
            +	'</div>'
    }

    //弹窗
    layer.open({
        type: 1,
        title:"详细日志",//标题
        area: ['820px', 'auto'], //宽高
        content: html
    });
    //判断拖选动作按钮是否开启  true-开启 绑定拖选方法
    if($(".equipmentLogBody input[name='status']").prop('checked') == true){
        //鼠标拖选文字功能
        $(".equipmentLogBody .logdes").selectText({
            "sFunc":function(obj){
                //创建特征到页面中
                $(".keywordsBox").append('<span>'+obj.selectedText+'<i class="glyphicon glyphicon-remove removeKeywords"></i></span>');
                $(".keywordsBox").attr("log-type",logDetailArr[obj.eventDom.attr("data-index")].type);
                //鼠标悬停在动作特征除 显示删除图表
                $(".keywordsBox span").hover(function(){
                    $(this).children("i").css("display","block");
                },function(){
                    $(this).children("i").css("display","none");
                })
                //点击删除动作特征
                $(".removeKeywords").click(function(){
                    $(this).parent().remove();
                })
            }
        });
    }else{
        //关闭拖选事件
        $(".equipmentLogBody .logdes").off('mouseup');
    }

})


//过滤按钮 点击事件
$("#filterLogs").click(function(){
    //获得过滤关键词
    var filterWords = $(".filterWords").val();
    //执行过滤函数
    filterLogs(filterWords);
})


//过滤函数   filterWords-过滤关键词    logsArr-要过滤的日志数组
function filterLogs(filterWords){
    //获得logs数组的长度；
    var len = filterArr.length;
    //定义一个空数组 用于暂存过滤后的数组
    var arr2 = [];
    var index = 0;
    //定义果过滤字段
    var reg = new RegExp(filterWords,"gi");
    console.log(reg);
    //循环过滤数组  将符合过滤条件的信息添加到arr数组中
    for(var i=0;i<len;i++){
        for(var n in filterArr[i]){
            //如果字符串中不包含目标字符会返回-1
            if(String(filterArr[i][n]).match(reg)){
                //创建一个对象 用于替换过滤出来的日志对象   （避免对象赋值后 更改问题）
                var obj = {};
                for(var q in filterArr[i]){
                    obj[q] = filterArr[i][q];
                }
                arr2[index] = obj;
                index++;
                break;
            }
        }
    }
    if(filterWords != ''){
        //循环替换关键字  加高亮
        for(var i = 0;i < arr2.length; i++){
            for(var n in arr2[i]){
                arr2[i][n] = String(arr2[i][n]).replace(filterWords,'<span>'+filterWords+'</span>');
            }
        }
    }
    //添加到页面中
    showLogs(arr2);

}

//数据对象过滤  null/undefined  转为'-';
function filterObj(obj){
    for(var i in obj){
        if(obj[i] == undefined || obj[i] == null || obj[i] == ""){
            obj[i] = '-';
        }
    }
    return obj;
}
