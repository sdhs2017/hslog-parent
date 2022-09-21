package com.jz.bigdata.util.outPrint.wordMaker;

import com.jz.bigdata.util.outPrint.print.style.to.StyleTO;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.document.RtfDocumentSettings;
import com.lowagie.text.rtf.style.RtfFont;
import com.lowagie.text.rtf.style.RtfParagraphStyle;

import java.awt.*;

public class style{
	/**
	 * 初始化
	 * 声明各种类型的文本样式，主要是字体，字号，是否加粗，字体颜色，几级文本
	 * @param writer
	 * @return
	 * @throws Exception 
	 */
	public static StyleTO setStyle(RtfWriter2 writer) throws Exception{
		StyleTO to = new StyleTO();
		/**带加粗的基本段落样式-宋体，字号12，加粗，字体颜色黑色*/
		to.setBoldFont(new RtfFont("宋体",12,Font.BOLD,Color.BLACK));
		/**对象，现状，符合性的小标题的样式-宋体，字号12，加粗，字体颜色黑色*/
		to.setCpHeader(new RtfFont("宋体",12,Font.BOLD,Color.BLACK));
		/**基本段落的样式-宋体，字号12，字体颜色黑色*/
		to.setNormalFont(new RtfFont("宋体",12,Font.NORMAL,Color.BLACK));
		//字号14-四号
		to.setNormalFontA(new RtfFont("宋体",14,Font.NORMAL,Color.BLACK));
		/**首页标题的格式-宋体，字号26，加粗，字体颜色黑色*/
		to.setFrontPageTitleFont(new RtfFont("宋体",26,Font.BOLD,Color.BLACK));
		/**一般标题的样式，宋体，字号16，加粗，字体颜色黑色*/
		to.setTitleFont(new RtfFont("宋体",22,Font.BOLD,Color.BLACK));
		/**页眉页脚样式，宋体，字号10，不加粗，字体颜色黑色*/
		to.setFooterHeaderFont(new RtfFont("宋体",10,Font.NORMAL,Color.BLACK));
		/**一级标题样式--宋体，字号16，加粗，字体颜色黑色*/
		RtfParagraphStyle h1 = new RtfParagraphStyle("heading 1","宋体 (中文正文)",16,Font.BOLD,Color.BLACK);
		h1.setSpacingBefore(10);
		h1.setSpacingAfter(10);
		h1.setAlignment(Element.ALIGN_LEFT);
		to.setHeading_1(h1);
		/**二级标题样式--宋体，字号14，加粗，字体颜色黑色*/
		RtfParagraphStyle h2 = new RtfParagraphStyle("heading 2","宋体 (中文正文)",14,Font.BOLD,Color.BLACK);
		h2.setSpacingBefore(8);
		h2.setSpacingAfter(8);
		to.setHeading_2(h2);
		/**三级标题样式--宋体，字号12，不加粗，字体颜色黑色*/
		RtfParagraphStyle h3 = new RtfParagraphStyle("heading 3","宋体 (中文正文)",12,Font.NORMAL,Color.BLACK);
		h3.setSpacingBefore(5);
		h3.setSpacingAfter(5);
		to.setHeading_3(h3);
		/**四级标题样式--宋体，字号12，不加粗，字体颜色黑色*/
		RtfParagraphStyle h4 = new RtfParagraphStyle("heading 4","宋体 (中文正文)",12,Font.NORMAL,Color.BLACK);
		to.setHeading_4(h4);
		//使自定义的N级文本样式生效
		RtfDocumentSettings settings = writer.getDocumentSettings();
		settings.registerParagraphStyle(h1);
        settings.registerParagraphStyle(h2);
        settings.registerParagraphStyle(h3);
        settings.registerParagraphStyle(h4);
		return to;
	}
}