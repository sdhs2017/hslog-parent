 /*
 *将图片以PDF格式导出 方法 downloadToPDF(PDFtitle,imgBase64Arr) 
 * 参数imgBase64Arr:需要导出的图片集合  注：图片格式必须为Base64格式
 * 参数imgTop:图片之间的间隔（上下）
 * 参数imgWidth：原始图片的宽度 可以是数组 但是长度必须跟imgBase64Arr长度相同
 * 参数imgHeight：原始图片的高度 可以是数组 但是长度必须跟imgBase64Arr长度相同
 * 说明：	1.--此方法依赖于jsPDF.js插件 使用时应先引入此插件--
 * 		2.导出的pdf纸张为A4纸大小
 * 		3.图片会根据原始大小在A4纸上进行缩放
*/
 function downloadToPDF(imgBase64Arr,imgTop,imgWidth,imgHeight) {
 	//存储imgtop值 用于多页的初始值
 	var oldImgtop = imgTop;
  	//初始化
  	var pdf = new jsPDF('', 'pt', 'a4');
  	//循环添加图片
  	for (var i = 0;i<imgBase64Arr.length;i++){
  		//判断类型
  		if(imgWidth.constructor == Array){
  			var contentWidth = imgWidth[i];
  		}else{
  			var contentWidth = imgWidth;
  		}
		if(imgHeight.constructor == Array){
  			var contentHeight = imgHeight[i];
  		}else{
  			var contentHeight = imgHeight;
  		}
	   
	    //a4纸的尺寸[595.28,841.89]，html页面生成的canvas在pdf中图片的宽高
	    var imgWidth = 595.28;
	    var imgHeight = 592.28/contentWidth * contentHeight;
	    //判断是否大于一页
	    if ((imgTop+imgHeight) < 841.89) {
	    	//添加
	     	pdf.addImage(imgBase64Arr[i], 'JPEG', 0, imgTop, imgWidth, imgHeight );
	     	 
	    } else {
	    	//添加新的一页
	        pdf.addPage();
	    	//重新定义距顶端偏移量
	        imgTop = oldImgtop;
	    	//添加
	        pdf.addImage(imgBase64Arr[i], 'JPEG', 0, imgTop, imgWidth, imgHeight)
	        
	    }
	    //距顶端偏移量  += 图片高度 
		imgTop =imgTop+ oldImgtop+imgHeight;
	}
	//下载	 
    pdf.save('content.pdf');
 }