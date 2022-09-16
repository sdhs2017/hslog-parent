package com.jz.bigdata.util.outPrint.wordMaker;

import com.jz.bigdata.util.outPrint.print.style.to.StyleTO;
import com.lowagie.text.*;
import com.lowagie.text.rtf.field.RtfPageNumber;
import com.lowagie.text.rtf.field.RtfTotalPageNumber;
import com.lowagie.text.rtf.graphic.RtfShape;
import com.lowagie.text.rtf.graphic.RtfShapePosition;
import com.lowagie.text.rtf.headerfooter.RtfHeaderFooter;

public class insertFooter{
	/**
	 * 添加页眉页脚
	 * @param doc
	 * @param xmbh
	 * @param style
	 * @throws Exception
	 */
	public static void insertHeaderFooter(Document doc, String xmbh, StyleTO style) throws Exception{
		RtfShapePosition position;
		RtfShape shape;
		//页脚
		Paragraph parafooter = new Paragraph();
		//页脚的一条横线
		//对应4个参数的作用，横线开始时距上的距离，横线开始时的坐标，结束时坐标，横线结束点距上的距离
		position = new RtfShapePosition(0,0,8300,0);
		position.setXRelativePos(RtfShapePosition.POSITION_X_RELATIVE_MARGIN);
		position.setYRelativePos(RtfShapePosition.POSITION_Y_RELATIVE_PARAGRAPH);
		shape = new RtfShape(RtfShape.SHAPE_LINE, position);
		parafooter.add(shape);
		parafooter.add(new Phrase("                     "));
		parafooter.add(new Phrase("                           "));
		parafooter.add(new Phrase("第"));
		parafooter.add(new RtfPageNumber());
		parafooter.add(new Phrase("页 / 共"));
		parafooter.add(new RtfTotalPageNumber());
		parafooter.add(new Phrase("页"));
		parafooter.setFont(style.getFooterHeaderFont());
		
		HeaderFooter footer = new RtfHeaderFooter(parafooter);
		footer.setAlignment(Element.ALIGN_CENTER);
		footer.setBorder(Rectangle.NO_BORDER);
		doc.setFooter(footer);
		
		//页眉
		//页眉的一条横线
		position = new RtfShapePosition(300,0,8300,300);
		position.setXRelativePos(RtfShapePosition.POSITION_X_RELATIVE_MARGIN);
		position.setYRelativePos(RtfShapePosition.POSITION_Y_RELATIVE_PARAGRAPH);
		shape = new RtfShape(RtfShape.SHAPE_LINE, position);
		//页眉的具体内容
		Paragraph paraheader = new Paragraph("信息安全等级保护测评报告");
		paraheader.add(shape);
		paraheader.setFont(style.getFooterHeaderFont());
		HeaderFooter header = new RtfHeaderFooter(paraheader);
		header.setAlignment(Element.ALIGN_RIGHT);
		header.setBorder(Rectangle.NO_BORDER);
		doc.setHeader(header);
		
	}
}