
(function( $ ) {
	/*
	 *功能：  鼠标拖选页面文字插件
	 * 注：1.提前引入JQ、layer.js
	 */
	
	$.fn.selectText = function(paramObj) {
		//页面添加选项菜单
		var html = '<div class="selectedMenu" style="position: fixed;width: 80px; color:#fff;box-shadow: 0 3px 15px #4781bb;font-size: 12px;background: rgb(52, 114, 174);z-index: 99999999900;display: none;">'
				 +   	'<div class="commit" style="width:100%;height: 30px;text-align: center;cursor: pointer;line-height: 30px;border-bottom: 1px solid rgb(92, 144, 192);">确定</div>'
				 +   	'<div class="cancle" style="width:100%;height: 30px;text-align: center;cursor: pointer;line-height: 30px;">取消</div>'
				 +	'</div>'
		$("body").append(html)	
		$(".selectedMenu>div").hover(function(){
			$(this).css("background","rgb(29, 80, 128)");
		},function(){
			$(this).css("background","rgb(52, 114, 174)");
		})
		//扩展参数
		var opt = $.extend( {
	      	"sFunc":''//执行方法
	    }, paramObj);
	    
	    //鼠标按下事件
		$(this).mousedown(function () {
			$(".selectedMenu").css("display","none");
		})
		var selectedText = "";
		var $this = "";
		
		//菜单提交按钮点击事件
		$(".commit").click(function (event) {
			var e = event || window.event||arguments.callee.caller.arguments[0];
			e.stopPropagation();
			//关闭菜单
			$(".selectedMenu").css("display","none");
			var obj = {};
			obj.selectedText = selectedText;
			obj.eventDom = $this;
			opt.sFunc(obj);
			/*layer.prompt(
				{
					title: '输入文本', 
					formType: 3
				}, 
				function(text, index){
			    	layer.close(index);
			    	alert('选中文本：'+selectedText+' <br/>输入文本：'+text)
			    }
			);*/
			/*layer.open({				
		 		type: 1,
		 		title:'添加动作',//标题
				//area: ['450px', '400px'], //宽高
				btn: ['确定','取消'], //按钮
				btn1:function(index){
					var inputText = $(".layui-layer-input").val();
					//alert('选中文本：'+selectedText+' 输入文本：'+inputText)
					if(inputText != ''){
						
						var obj = {};
						obj.selectedText = selectedText;
						obj.inputText = inputText;
						obj.eventDom = $this;
						opt.sFunc(obj);
						layer.close(index);
						
					}
					
				},
				content: '<p style="margin-left:20px;margin-right:20px;display:flex;margin-bottom:10px;">'
						+	'<span style="flex:0 0 80px;width:80px;">已选文本：</span><span style="flex:1;word-break: break-all;">'+selectedText+'</span>'
						+'</p>'
						+'<p style="margin-left:20px;margin-right:20px;display:flex;">'
						+	'<span style="flex:0 0 80px;width:80px;">动作名称：</span><input autofocus="autofocus" style="flex:1;display: block;width: 220px;height: 24px;margin: 0 auto;line-height: 24px;padding: 0 5px;" type="text" class="layui-layer-input form-control" value=""></p>'
			})*/
		})
		//菜单关闭按钮
		$(".cancle").click(function (event) {
			var e = event || window.event||arguments.callee.caller.arguments[0];
			e.stopPropagation();
			//关闭菜单
			$(".selectedMenu").css("display","none");
		})
		
		//鼠标按键抬起事件
		$(this).on('mouseup',function(event){
			$this = $(this);
			selectedText = "";
			//获得选中文本
			var selectionObj = null;
			var rangeObj = null;
			if(window.getSelection){ //webkit浏览器
				selectionObj = window.getSelection();
	　　　　　　	selectedText = selectionObj.toString();
	　　　　	}else if(document.selection){ // IE浏览器
	　　　　　　	selectionObj = document.selection;
	　　　　　　	rangeObj = selectionObj.createRange();
	　　　　　　	selectedText = rangeObj.text;
	　　　　	}

			if (selectedText.length > 0) {
				//菜单选项
				//获得鼠标当前位置坐标
				var e = event || window.event || arguments.callee.caller.arguments[0];
				var mouseX = e.originalEvent.x || e.originalEvent.layerX || 0;
				var mouxeY = e.originalEvent.y || e.originalEvent.layerY || 0;
				$(".selectedMenu").css({"display":"block","left":mouseX,"top":mouxeY});
			
			}	　　　　	
		})
	}
	
})( jQuery );
/*
 *功能： 页面跳转方法
 * 参数：
 *  src-页面路径
 *	titleVal-标签值
 *  des - 标题文字描述
 *	objStr-参数的集合
 */
//跳转页面    type-页面类型（排行榜、关系图） 
function jumpHtml(src,titleVal,des,objStr){
	//标签页面计数
	var htmlNum = Number(sessionStorage.getItem("htmlNum"));	
	//长度超出15显示...号
	var tVal = "";
	if(titleVal.length > 15){
		tVal = titleVal.substring(0,15)+'...';
	}else{
		tVal = titleVal;
	}
	//标签页名称
	var tagsName = tVal + des;
	var html ='<a href="javascript:;" class="active J_menuTab" data-obj="'+objStr+'" title="'+titleVal+'" data-id="'+src+htmlNum+'">'+tagsName+'<i class="fa fa-times-circle"></i></a>'
		//移除导航菜单选中属性
		$('.page-tabs-content', parent.document).click().children("a").removeClass("active");
		//添加导航菜单
		$('.page-tabs-content', parent.document).click().append(html);
		var iframe = '<iframe class="J_iframe" width="100%" height="100%" src="'+src+'" frameborder="0" data-id="'+src+htmlNum+'" seamless="" style="display: inline;"></iframe>'
		//移除其他页面
		$('#content-main', parent.document).click().children("iframe").hide();
		$('#content-main', parent.document).click().append(iframe);
		htmlNum++;
		sessionStorage.setItem("htmlNum",htmlNum);
}	
