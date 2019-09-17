//正常的ajaxpost方法
//参数 url：请求的url路径
//参数param：要传递的参数，以{key:value,key1:value1...}形式
//参数successFunc：要执行的回调函数，操作成功后执行
//参数failureFunc：要执行的回调函数，正常返回操作失败后执行
//参数errorFunc：要执行的回调函数，异常返回失败后执行
function ajaxPost(url,param,successFunc,failureFunc,errorFunc){
	//查看param参数类型，如果不存在或类型不为obj，强制转成空obj来预防ajax提交出现问题。
	if(typeof param != "object"){ param={}; }
	//显示进度条
	layer.load(1);
	//ajax提交
	$.ajax({
		type:'post',
		dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
		url:url,
		timeout:60000,
		data:param,
		success:function(data){
			//console.log(data)
			//关闭进度条
			layer.closeAll('loading');
			//执行错误
			if(data.success=="false"){
				layer.msg(data.message,{icon:data.state});				
				//如果类型为function，执行。
				if(typeof failureFunc == "function"){ failureFunc(); }
			}else{//执行成功
				//var data = data.result;
				if(data.message!=undefined&&data.state!=undefined){layer.msg(data.message,{icon:data.state});}
				//如果类型为function，执行。
				if(typeof successFunc == "function"){ successFunc(data); }
			}
		},
		error:function(data){
			//关闭进度条
			layer.closeAll('loading');
			//请求超时
			if(data.status=0){
				layer.msg("请求超时，请重试！",{icon:2});
			}else if(data.responseText!=undefined&&data.responseText!=""){//后台返回异常
				if(data.responseText == "您没有权限"){
					layer.msg(data.responseText,{icon:2});
				}else{
					layer.msg("服务器异常",{icon:2});
				}
				
				/**/
			}else{
				layer.msg('请求出现异常，请重试！',{icon:2});
			}
			//如果类型为function，执行。
			if(typeof errorFunc == "function"){ errorFunc(); }
		}
	});
}
//正常的ajaxpost方法
//参数 url：请求的url路径
//参数param：要传递的参数，以{key:value,key1:value1...}形式
//参数successFunc：要执行的回调函数，操作成功后执行
//参数failureFunc：要执行的回调函数，正常返回操作失败后执行
//参数errorFunc：要执行的回调函数，异常返回失败后执行
function ajaxPostWithOutLayer(url,param,successFunc,failureFunc,errorFunc){
	//查看param参数类型，如果不存在或类型不为obj，强制转成空obj来预防ajax提交出现问题。
	if(typeof param != "object"){ param={}; }
	//显示进度条
	//layer.load(1);
	//ajax提交
	$.ajax({
		type:'post',
		//dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
		url:url,
		timeout:60000,
		data:param,
		success:function(data){
			//关闭进度条
			//layer.closeAll('loading');
			//执行错误
			if(data.success=="false"){
				layer.msg(data.message,{icon:data.state});
				//如果类型为function，执行。
				if(typeof failureFunc == "function"){ failureFunc(); }
			}else{//执行成功
				//if(data.message!=undefined&&data.state!=undefined){layer.msg(data.message,{icon:data.state});}
				//如果类型为function，执行。
				if(typeof successFunc == "function"){ successFunc(data); }
			}
		},
		error:function(data){
			//关闭进度条
			layer.closeAll('loading');
			//请求超时
			if(data.status=0){
				layer.msg("请求超时，请重试！",{icon:2});
			}else if(data.responseText!=undefined&&data.responseText!=""){//后台返回异常
				/*layer.msg(data.responseText,{icon:2});*/
				layer.msg("服务器异常",{icon:2});
			}else{
				layer.msg('请求出现异常，请重试！',{icon:2});
			}
			//如果类型为function，执行。
			if(typeof errorFunc == "function"){ errorFunc(); }
		}
	});
}
//ajaxForm提交
//需要引入easyui的js文件，并且easyui的js文件要引入在前面
//参数param：要传递的参数，以{key:value,key1:value1...}形式
//参数successFunc：要执行的回调函数，操作成功后执行
//参数failureFunc：要执行的回调函数，正常返回操作失败后执行
//参数errorFunc：要执行的回调函数，异常返回失败后执行
function ajaxFormSubmit(_this,url,param,successFunc,failureFunc,errorFunc){
	//查看param参数类型，如果不存在或类型不为obj，强制转成空obj来预防ajax提交出现问题。
	if(typeof param != "object"){ param={}; }
	//显示进度条
	layer.load(1);
	//form ajax提交
	$(_this).ajaxSubmit({
		type:'post',
		dataType:"json", //数据类型 ，必须标注，后端返回数据都是json格式
		url:url,
		timeout:10000,
		data:param,
		success:function(data){
			//关闭进度条
			layer.closeAll('loading');
			//执行错误
			if(data.success=="false"){
				layer.msg(data.message,{icon:data.state});
				//如果类型为function，执行。
				if(typeof failureFunc == "function"){ failureFunc(); }
			}else{//执行成功
				layer.msg(data.message,{icon:data.state});
				//如果类型为function，执行。
				if(typeof successFunc == "function"){ successFunc(); }
			}
		},
		error:function(data){
			//关闭进度条
			layer.closeAll('loading');
			//请求超时
			if(data.status=0){
				layer.msg("请求超时，请重试！",{icon:2});
			}else if(data.responseText!=undefined&&data.responseText!=""){//后台返回异常
				/*layer.msg(data.responseText,{icon:2});*/
				layer.msg("服务器异常",{icon:2});
			}else{
				layer.msg('请求出现异常，请重试！',{icon:2});
			}
			//如果类型为function，执行。
			if(typeof errorFunc == "function"){ errorFunc(); }
		}
	});
}