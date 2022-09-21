package com.jz.bigdata.util.outPrint.wordMaker;

import com.jz.bigdata.util.outPrint.print.outTO.to.OutTO;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.rtf.style.RtfFont;
import com.lowagie.text.rtf.style.RtfParagraphStyle;

public class insertTitle{

	/**
	 * 插入标题---带大纲等级
	 * @param doc--文档对象
	 * @param type--标题格式
	 * @throws Exception
	 */
	public static void insertTitle(Document doc, OutTO oto, RtfParagraphStyle type) throws Exception{
		Paragraph t = new Paragraph(oto.getTitle());
		t.setFont(type);
		doc.add(t);
	}
	/**
	 * 插入标题---不带大纲等级--居中显示
	 * @param doc--文档对象
	 * @param title--标题名称
	 * @param type--标题格式
	 * @throws Exception
	 */
	public static void insertTitle(Document doc,OutTO oto,RtfFont type) throws Exception{
		Paragraph t = new Paragraph(oto.getTitle());
		t.setFont(type);
		t.setAlignment(Element.ALIGN_CENTER);
		doc.add(t);
	}
	
	/**
	 * 通过传入参数，生成一个带标题样式的paragraph对象
	 * @param doc--文档对象
	 * @param title--标题名称
	 * @param type--标题格式
	 * @throws Exception
	 */
	public static Paragraph createTitle(OutTO oto,RtfParagraphStyle type) throws Exception{
		Paragraph t = new Paragraph(oto.getTitle());
		t.setFont(type);
		return t;
	}
	
}