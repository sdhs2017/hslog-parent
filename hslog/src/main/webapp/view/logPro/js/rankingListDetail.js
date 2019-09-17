
/**
 * Copyright (C) 2012 by Justin Windle
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

(function($) {

    // Global initialisation flag
    var initialized = false;

    // For detecting browser prefix and capabilities
    var el = document.createElement( 'div' );
    var re = /^(Moz|(w|W)ebkit|O|ms)(?=[A-Z])/;

    // Establish vendor prefix and CSS 3D support
    var vendor = (function() { for ( var p in el.style ) if( re.test(p) ) return p.match(re)[0]; })() || '';
    var canRun = vendor + 'Perspective' in el.style;
    var prefix = '-' + vendor.toLowerCase() + '-';

    var $this, $root, $base, $kids, $node, $item, $over, $back;
    var wait, anim, last;

    // Public API
    var api = {

        // Toggle open / closed
        toggle: function() {

            $this = $( this );
            $this.makisu( $this.hasClass( 'open' ) ? 'close' : 'open' );
        },

        // Trigger the unfold animation
        open: function( speed, overlap, easing ) {

            // Cache DOM references
            $this = $(this);
            $root = $this.find( '.root' );
            $kids = $this.find( '.node' ).not( $root );

            // Establish values or fallbacks
            speed = utils.resolve( $this, 'speed', speed );
            easing = utils.resolve( $this, 'easing', easing );
            overlap = utils.resolve( $this, 'overlap', overlap );

            $kids.each( function( index, el ) {

                // Establish settings for this iteration
                anim = 'unfold' + ( !index ? '-first' : '' );
                last = index === $kids.length - 1;
                time = speed * ( 1 - overlap );
                wait = index * time;

                // Cache DOM references
                $item = $( el );
                $over = $item.find( '.over' );

                // Element animation
                $item.css(utils.prefix({
                    'transform': 'rotateX(180deg)',
                    'animation': anim + ' ' + speed + 's ' + easing + ' ' + wait + 's 1 normal forwards'
                }));

                // Shading animation happens when the next item starts
                if ( !last ) wait = ( index + 1 ) * time;

                // Shading animation
                $over.css(utils.prefix({
                    'animation': 'unfold-over ' + (speed * 0.45) + 's ' + easing + ' ' + wait + 's 1 normal forwards'
                }));
            });

            // Add momentum to the container
            $root.css(utils.prefix({
                'animation': 'swing-out ' + ( $kids.length * time * 1.4 ) + 's ease-in-out 0s 1 normal forwards'
            }));

            $this.addClass( 'open' );
        },

        // Trigger the fold animation
        close: function( speed, overlap, easing ) {

            // Cache DOM references
            $this = $(this);
            $root = $this.find( '.root' );
            $kids = $this.find( '.node' ).not( $root );

            // Establish values or fallbacks
            speed = utils.resolve( $this, 'speed', speed ) * 0.66;
            easing = utils.resolve( $this, 'easing', easing );
            overlap = utils.resolve( $this, 'overlap', overlap );

            $kids.each( function( index, el ) {

                // Establish settings for this iteration
                anim = 'fold' + ( !index ? '-first' : '' );
                last = index === 0;
                time = speed * ( 1 - overlap );
                wait = ( $kids.length - index - 1 ) * time;

                // Cache DOM references
                $item = $( el );
                $over = $item.find( '.over' );

                // Element animation
                $item.css(utils.prefix({
                    'transform': 'rotateX(0deg)',
                    'animation': anim + ' ' + speed + 's ' + easing + ' ' + wait + 's 1 normal forwards'
                }));

                // Adjust delay for shading
                if ( !last ) wait = ( ( $kids.length - index - 2 ) * time ) + ( speed * 0.35 );

                // Shading animation
                $over.css(utils.prefix({
                    'animation': 'fold-over ' + (speed * 0.45) + 's ' + easing + ' ' + wait + 's 1 normal forwards'
                }));
            });

            // Add momentum to the container
            $root.css(utils.prefix({
                'animation': 'swing-in ' + ( $kids.length * time * 1.0 ) + 's ease-in-out 0s 1 normal forwards'
            }));

            $this.removeClass( 'open' );
        }
    };

    // Utils
    var utils = {

        // Resolves argument values to defaults
        resolve: function( $el, key, val ) {
            return typeof val === 'undefined' ? $el.data( key ) : val;
        },

        // Prefixes a hash of styles with the current vendor
        prefix: function( style ) {
            
            for ( var key in style ) {
                style[ prefix + key ] = style[ key ];
            }

            return style;
        },

        // Inserts rules into the document styles
        inject: function( rule ) {

            try {

                var style = document.createElement( 'style' );
                style.innerHTML = rule;
                document.getElementsByTagName( 'head' )[0].appendChild( style );

            } catch ( error ) {}
        }
    };

    // Element templates
    var markup = {
        node: '<span class="node"/>',
        back: '<span class="face back"/>',
        over: '<span class="face over"/>'
    };

    // Plugin definition
    $.fn.makisu = function( options ) {

        // Notify if 3D isn't available
        if ( !canRun ) {

            var message = 'Failed to detect CSS 3D support';

            if( console && console.warn ) {
                
                // Print warning to the console
                console.warn( message );

                // Trigger errors on elements
                this.each( function() {
                    $( this ).trigger( 'error', message );
                });
            }

            return;
        }

        // Fires only once
        if ( !initialized ) {

            initialized = true;

            // Unfold
            utils.inject( '@' + prefix + 'keyframes unfold        {' +

                '0%   {' + prefix + 'transform: rotateX(180deg);  }' +
                '50%  {' + prefix + 'transform: rotateX(-30deg);  }' +
                '100% {' + prefix + 'transform: rotateX(0deg);    }' +

                '}');

            // Unfold (first item)
            utils.inject( '@' + prefix + 'keyframes unfold-first  {' +

                '0%   {' + prefix + 'transform: rotateX(-90deg);  }' +
                '50%  {' + prefix + 'transform: rotateX(60deg);   }' +
                '100% {' + prefix + 'transform: rotateX(0deg);    }' +

                '}');

            // Fold
            utils.inject( '@' + prefix + 'keyframes fold          {' +

                '0%   {' + prefix + 'transform: rotateX(0deg);    }' +
                '100% {' + prefix + 'transform: rotateX(180deg);  }' +

                '}');

            // Fold (first item)
            utils.inject( '@' + prefix + 'keyframes fold-first    {' +

                '0%   {' + prefix + 'transform: rotateX(0deg);    }' +
                '100% {' + prefix + 'transform: rotateX(-180deg); }' +

                '}');

            // Swing out
            utils.inject( '@' + prefix + 'keyframes swing-out     {' +

                '0%   {' + prefix + 'transform: rotateX(0deg);    }' +
                '30%  {' + prefix + 'transform: rotateX(-30deg);  }' +
                '60%  {' + prefix + 'transform: rotateX(15deg);   }' +
                '100% {' + prefix + 'transform: rotateX(0deg);    }' +

                '}');

            // Swing in
            utils.inject( '@' + prefix + 'keyframes swing-in      {' +

                '0%   {' + prefix + 'transform: rotateX(0deg);    }' +
                '50%  {' + prefix + 'transform: rotateX(-10deg);  }' +
                '90%  {' + prefix + 'transform: rotateX(15deg);   }' +
                '100% {' + prefix + 'transform: rotateX(0deg);    }' +

                '}');

            // Shading (unfold)
            utils.inject( '@' + prefix + 'keyframes unfold-over   {' +
                '0%   { opacity: 1.0; }' +
                '100% { opacity: 0.0; }' +
                '}');

            // Shading (fold)
            utils.inject( '@' + prefix + 'keyframes fold-over     {' +
                '0%   { opacity: 0.0; }' +
                '100% { opacity: 1.0; }' +
                '}');
            var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
            if (userAgent.indexOf("Firefox") > -1) { //判断是否Firefox浏览器
            	 utils.inject( '.node {' +
                 'position: relative;' +
                 'display: block;' +
                 '}');
            };
            if (userAgent.indexOf("Chrome") > -1) {//判断是否Chrome浏览器
            	 utils.inject( '.node {' +
            		'position: relative;' +
                    'display: block;' +
                 '}');	
            }
            // Node styles
           

            // Face styles
            utils.inject( '.face {' +
                'pointer-events: none;' +
                'position: absolute;' +
                'display: block;' +
                'height: 100%;' +
                'width: 100%;' +
                'left: 0;' +
                'top: 0;' +
                '}');
        }

        // Merge options & defaults
        var opts = $.extend( {}, $.fn.makisu.defaults, options );

        // Extract api method arguments
        var args = Array.prototype.slice.call( arguments, 1 );

        // Main plugin loop
        return this.each( function () {

            // If the user is calling a method...
            if ( api[ options ] ) {
                return api[ options ].apply( this, args );
            }

            // Store options in view
            $this = $( this ).data( opts );

            // Only proceed if the scene hierarchy isn't already built
            if ( !$this.data( 'initialized' ) ) {

                $this.data( 'initialized', true );

                // Select the first level of matching child elements
                $kids = $this.children( opts.selector );

                // Build a scene graph for elements
                $root = $( markup.node ).addClass( 'root' );
                $base = $root;
                
                // Process each element and insert into hierarchy
                $kids.each( function( index, el ) {

                    $item = $( el );

                    // Which animation should this node use?
                    anim = 'fold' + ( !index ? '-first' : '' );

                    // Since we're adding absolutely positioned children
                    $item.css( 'position', 'relative' );

                    // Give the item some depth to avoid clipping artefacts
                    $item.css(utils.prefix({
                        'transform-style': 'preserve-3d',
                        'transform': 'translateZ(-0.1px)'
                    }));

                    // Create back face
                    $back = $( markup.back );
                    $back.css( 'background', $item.css( 'background' ) );
                    $back.css(utils.prefix({ 'transform': 'translateZ(-0.1px)' }));

                    // Create shading
                    $over = $( markup.over );
                    $over.css(utils.prefix({ 'transform': 'translateZ(0.1px)' }));
                    $over.css({
                        'background': opts.shading,
                        'opacity': 0.0
                    });
                    
                    // Begin folded
                    $node = $( markup.node ).append( $item );
                    $node.css(utils.prefix({
                        'transform-origin': '50% 0%',
                        'transform-style': 'preserve-3d',
                        'animation': anim + ' 1ms linear 0s 1 normal forwards'
                    }));

                    // Build display list
                    $item.append( $over );
                    $item.append( $back );
                    $base.append( $node );

                    // Use as parent in next iteration
                    $base = $node;
                });

                // Set root transform settings
                $root.css(utils.prefix({
                    'transform-origin': '50% 0%',
                    'transform-style': 'preserve-3d'
                }));

                // Apply perspective
                $this.css(utils.prefix({
                    'transform': 'perspective(' + opts.perspective + 'px)'
                }));

                // Display the scene
                $this.append( $root );
            }
        });
    };

    // Default options
    $.fn.makisu.defaults = {

        // Perspective to apply to rotating elements
        perspective: 1200,

        // Default shading to apply (null => no shading)
        shading: 'rgba(0,0,0,0.12)',

        // Area of rotation (fraction or pixel value)
        selector: null,

        // Fraction of speed (0-1)
        overlap: 0.6,

        // Duration per element
        speed: 0.8,

        // Animation curve
        easing: 'ease-in-out'
    };

    $.fn.makisu.enabled = canRun;

})( jQuery );


//获取从排行榜传过来的值
var rankingListVal = rankingListVal = $('.page-tabs-content', parent.document).click().children("a.active").attr("data-val");
//传送参数
var obj = {};
obj.groupfiled = rankingListVal.split('-')[0];
obj.iporport = rankingListVal.split('-')[2];
//填充页面相关数据
$(".top_title").html(rankingListVal.split('-')[1]+':'+rankingListVal.split('-')[2]);//类型：值

var listName = {
	"ipv4_src_addr":"源IP",
	"ipv4_dst_addr":"目的IP",
	"l4_src_port":"源端口",
	"l4_dst_port":"目的端口"
}
//成功方法函数
var sfunc = function(data){//成功
	//用于区分哪一个 列表 累加
	var m = 0;
	/*var startIpObj = data[0].ipv4_src_addr;//源ip数组集合
	var endIpObj = data[0].ipv4_dst_addr;//目的ip数组集合
	var startPortObj  = data[0].l4_src_port;//源端口数组集合
	var endPortObj = data[0].l4_dst_port;//目的端口数组集合			
*/	
	for(var i in data[0]){		
		if(m == 0){
			//加载列表
			splitList(listName[i],data[0][i],'nigiri');
			//设置第一个条件框名称
			$(".cond1_title").html(listName[i]);
		}else if(m == 1){
			splitList(listName[i],data[0][i],'maki');
			//设置第一个条件框名称
			$(".cond2_title").html(listName[i]);
		}else if(m == 2){
			splitList(listName[i],data[0][i],'sashimi');
			//设置第一个条件框名称
			$(".cond3_title").html(listName[i]);
		}
		
		m++;
	}
	//触发下拉效果
	if ( $.fn.makisu.enabled ) {
	    var $sashimi = $( '.sashimi' );
	    var $nigiri = $( '.nigiri' );
	    var $maki = $( '.maki' );
	    // Create Makisus
	    $nigiri.makisu({
	        selector: 'dd',
	        overlap: 0.85,
	        speed: 1.5
	    });

	    $maki.makisu({
	        selector: 'dd',
	        overlap: 0.6,
	        speed: 0.85
	    });

	    $sashimi.makisu({
	        selector: 'dd',
	        overlap: 0.2,
	        speed: 0.5
	    });

	    // Open all
	    
	    $( '.list' ).makisu( 'open' );

	    // Toggle on click

	    $( '.toggle' ).on( 'click', function() {
	        $( '.list' ).makisu( 'toggle' );
	    });

	    // Disable all links

	    $( '.demo a' ).click( function( event ) {
	        event.preventDefault();
	    });

	} else {

	    $( '.warning' ).show();
	}
	 var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
     if (userAgent.indexOf("Firefox") > -1) { //判断是否Firefox浏览器
     	$(".node").hover(function(){
     		$(this).css("display","inline");
     	},function(){
     		$(this).css("display","block");
     	})
     }
}
//失败回调函数
var efunc = function(data){
	layer.msg('操作失败',{icon: 5});
}
//发送请求
ajaxPost("../../log/getIPAndPortTop.do",obj,sfunc,"",efunc);

//拼接列表  title-列表名   data-列表数据   eleClassName-列表class名
function splitList(title, data,eleClassName){
	var html = '<dt>'+title+'排行榜</dt>';
	var count = '';
	for(var i = 0;i<data.length;i++){
		var className = '';//class名 用于颜色显示
		//判断顺序 添加class名
		if(i == 0){
			className = 'firstItem';
		}else if(i == 1){
			className = 'secondItem';
		}else if(i == 2){
			className = 'thirdItem';
		}
		var obj = data[i];	
		//判断次数大小 超过一万 做万位处理
		if(obj.count > 10000){
			count = (Number(obj.count) / 10000).toFixed(1) + ' 万';
		}else {
			count = obj.count;
		}
		html += '<dd onclick="ddClick($(this))" ondragstart="drag(event)" draggable="true" class="'+className+'"><a href="#"><span class="rankingOrder">'+(i+1)+'</span><span class="rankingText">'+obj.IpOrPort+'</span><span class="rankingNum">'+count+'</span></a></dd>'	    
		//添加到页面中
		$("."+eleClassName).html(html);
		/*$("dd").click(function(){
			console.log($(this).children('a').children(".rankingText").html())
		})*/
	}
}
//点击列表（dd） 事件
function ddClick($this,event){
	//获得当前点击行的值
	var targetVal = $this.children("a").children(".rankingText").html();
	//获得当前点击的位置
	var e = window.event||event||arguments.callee.caller.arguments[0];
	var startLeft = $(e.target).offset().left;
	var startTop = $(e.target).offset().top;
	//获得当前点击的类别的id
	var parentId = $this.parents('dl').attr("id");
	//获得结束盒子
	var endDiv = "";
	if(parentId == 'list01'){
		endDiv = $(".cond1_text");
	}else if(parentId == 'list02'){
		endDiv = $(".cond2_text");
	}else if(parentId == 'list03'){
		endDiv = $(".cond3_text");
	}
	//判断结束盒子里的值是否等于当前点击的值
	if($(endDiv).children("div").length ==  0 || targetVal !== $(endDiv).children("div").children('.val').html()){
		//创建动画盒子
		var outer = createBall(targetVal),//outer框
			inner = outer.firstElementChild//内部框
		//设置动画盒子的初始位置
		$(".outer").css({left:startLeft,top:startTop})
		//计算水平移动的位置
		var instanceX = endDiv.offset().left - startLeft;
		var instanceY = endDiv.offset().top - startTop;
		outer.style.transform = 'translate3d('+instanceX+'px,0,0)';//水平方向匀速移动，参考样式设置
		inner.style.transform = 'translate3d(0,'+instanceY+'px,0)';//垂直方向变速运动，参考样式设置
		inner.style.opacity = 0;
		//延时0.5s填充数据
		setTimeout(function(){
			endDiv.html('<div><span class="val">'+targetVal+'</span><i class=" fa fa-times"></i></div>');
			//删除动画盒子
			$(".outer").remove();
		},500)
		//初始化
		outer = inner = null;
	}
	
	
	
}
//创建动画盒子
function createBall(targetVal){
	var inner = document.createElement('div'),
		outer = document.createElement('div');
	inner.classList.add('inner');
	outer.classList.add('outer');
	inner.innerHTML = targetVal;
	outer.appendChild(inner);
	document.body.appendChild(outer);
	return outer;
}


//拖拽开始函数
function drag(event){	
	var e = window.event||event||arguments.callee.caller.arguments[0];
	var eParent =  $(e.target).parents("dl").attr("id");
	//console.log( $(e.target).children('.rankingText').html())
	var eVal = $(e.target).children('.rankingText').html();
	e.dataTransfer.setData("text/plain",eParent+"-"+eVal);
	// 存储当前拖动的对象的id
	//e.dataTransfer.setData("Text",e.target);
	
}
//拖拽停止 放下动作函数
function drop(event){
	var e = window.event||event||arguments.callee.caller.arguments[0];
	//清除默认行为
	e.preventDefault();
	e.stopPropagation();
	var text = e.dataTransfer.getData("text/plain");
	if($(e.target).attr("class") == "cond1_text" && text.split("-")[0] == "list01"){
		if($(".cond1_text").children('div').length == 0){
			$(".cond1_text").html('<div><span class="val">'+text.split("-")[1]+'</span><i class=" fa fa-times"></i></div>')
		}
	}else if($(e.target).attr("class") == "cond2_text" && text.split("-")[0] == "list02"){
		if($(".cond2_text").children('div').length == 0){
			$(".cond2_text").html('<div><span class="val">'+text.split("-")[1]+'</span><i class=" fa fa-times"></i></div>')
		}
	}else if($(e.target).attr("class") == "cond3_text" && text.split("-")[0] == "list03"){
		if($(".cond3_text").children('div').length == 0){
			$(".cond3_text").html('<div><span class="val">'+text.split("-")[1]+'</span><i class=" fa fa-times"></i></div>')
		}
	}
}
function allowDrop(event){
	var e = window.event||event||arguments.callee.caller.arguments[0];
	//清除默认行为
	e.preventDefault();
}

//鼠标悬停
$(".cond1_text,.cond2_text,.cond3_text").hover(function(){
	$(this).find("i").css("display","inline-block");
},function(){
	$(this).find("i").css("display","none");
})
//删除所选的查询条件
$(".cond1_text,.cond2_text,.cond3_text").on("click","i",function(){
	$(this).parent().remove();
})
var htmlNum = 0;
var sendObj = {};
//查看日志
$(".btnBox").click(function(){
	sendObj.type = "netflow";
	sendObj.ipv4_src_addr = "";
	sendObj.ipv4_dst_addr = "";
	sendObj.l4_src_port = "";
	sendObj.l4_dst_port = "";
	
	differentiateType(rankingListVal.split('-')[1],rankingListVal.split('-')[2])
	//获取参数
	var tit1 = $(".cond1_title").html(); 
	var val1 = $(".cond1_text").find(".val").html();
	differentiateType(tit1,val1)
	
	var tit2 = $(".cond2_title").html(); 
	var val2 = $(".cond2_text").find(".val").html();
	differentiateType(tit2,val2)

	var tit3 = $(".cond3_title").html(); 
	var val3 = $(".cond3_text").find(".val").html();
	differentiateType(tit3,val3)

	//跳转页面
	// 储存在本地
	//sessionStorage.setItem("netflowSearchObj",JSON.stringify(sendObj));
	var objstr = JSON.stringify(sendObj).replace(/"/g,"'");
	//拼接导航
	var html ='<a href="javascript:;" class="active J_menuTab" data-obj="'+objstr+'" data-id="netflowLogs'+htmlNum+'">'+rankingListVal.split('-')[2]+'日志 <i class="fa fa-times-circle"></i></a>'
	//移除导航菜单选中属性
	$('.page-tabs-content', parent.document).click().children("a").removeClass("active");
	//添加导航菜单
	$('.page-tabs-content', parent.document).click().append(html);
	var iframe = '<iframe class="J_iframe" width="100%" height="100%" src="logPro/netflowLogs.html" frameborder="0" data-id="netflowLogs'+htmlNum+'" seamless="" style="display: inline;"></iframe>'
	//移除其他页面
	$('#content-main', parent.document).click().children("iframe").hide();
	$('#content-main', parent.document).click().append(iframe);
	htmlNum++;
})

//判参数类型函数
function differentiateType(name,val){
	switch(name){
		case "源IP":
			sendObj.ipv4_src_addr = val;
		    break;
		case "目的IP":
			sendObj.ipv4_dst_addr = val;
		    break;
		case "源端口":
			sendObj.l4_src_port= val;
		    break;
		case "目的端口":
			sendObj.l4_dst_port = val;
		    break;
		default:
		    
		    break;
	}
}

