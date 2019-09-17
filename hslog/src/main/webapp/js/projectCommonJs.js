		



/* <div class="searchTimeBox">
	<div class="searchBoxTitle">时间范围</div>
	<input type="text" class="time-input form-control" id="startTime" placeholder="起始日期" name="begin_time" readonly class="form_datetime">  
	<i class="glyphicon glyphicon-remove startTimeRemove"></i>	
	<span> ~ </span>
	<input type="text" class="time-input form-control" id="endTime" placeholder="结束日期" name="end_time" readonly class="form_datetime">  		
	<i class="glyphicon glyphicon-remove endTimeRemove"></i>	
</div>
 * bootstrap-datetimepicker 事件控件 通用设置
 * 开始时间id：startTime  结束时间id：endTime
 * 设置描述：
 * 		1. 开始时间与结束时间初始化
 * 		2. 开始时间或结束时间发生改变，与之对应的时间范围也相继发生改变
 *  	3. 时间清空按钮
 *  */
		$('#startTime').datetimepicker({  
		    format: 'yyyy-mm-dd hh:ii:ss',//定义时间格式  
		    autoclose: true,//选择好自动关闭  
//			minView: 2,//只选择到小时  
		    language: 'zh-CN', //汉化   
		 });
		
		$('#endTime').datetimepicker({  
		    format: 'yyyy-mm-dd hh:ii:ss',//定义时间格式  
		    autoclose: true,//选择好自动关闭  
//			    minView: 2,//只选择到小时  
		    language: 'zh-CN', //汉化   
		 });
	  
		//开始时间控件 值改变事件
	     $('#startTime').datetimepicker().on('changeDate', function(ev){
	     	
	    	//删除结束时间控件
	    	$('#endTime').datetimepicker('remove');
	    	//获取开始时间
	  		var startDate = $("#startTime").val();
	    	//初始化结束时间控件数据
	  		$("#endTime ").datetimepicker({
				format: 'yyyy-mm-dd hh:ii:ss',//定义时间格式  
			    autoclose: true,//选择好自动关闭  
//			    minView: 2,//只选择到小时  
				startDate: startDate,//开始时间
			    language: 'zh-CN', //汉化
	   		});
	  	}); 
	   //结束时间控件 值改变事件
	    $('#endTime').datetimepicker().on('changeDate', function(ev){
	    	//删除开始时间控件
	    	$('#startTime').datetimepicker('remove');
	    	//获取结束时间
	  		var endDate = $("#endTime .form-control").val();
	    	$('#startTime').datetimepicker({  
			    format: 'yyyy-mm-dd hh:ii:ss',//定义时间格式  
			    autoclose: true,//选择好自动关闭  
//			    minView: 2,//只选择到小时  
				endDate:endDate,//结束时间
			    language: 'zh-CN', //汉化   
			 });			
	  	});
	  //日期清除
	   $(".startTimeRemove").click(function(){
		   $("#startTime").val("")
	   })
	   $(".endTimeRemove").click(function(){
		   $("#endTime").val("")
	   })
		   