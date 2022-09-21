package com.jz.bigdata.util.outPrint.wordMaker;

import com.lowagie.text.Cell;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import com.lowagie.text.rtf.style.RtfFont;

import java.awt.*;

public class addCell{
	/**
	 * @param table--表格对象
	 * @param content--格子要填充的内容
	 * @param colspan--跨多少列
	 * @param rowspan--跨多少行
	 * @param center--左右居中
	 * @param middle--上下居中
	 * @param font--样式
	 * @param backgroundColor--格子背景颜色
	 * @throws Exception 
	 */
	public static void add(Table table,String content,int colspan,int rowspan,boolean center,boolean middle,RtfFont font,Color backgroundColor) throws Exception{
		Paragraph p = new Paragraph(content);
		p.setFont(font);
		p.setLeading(10f);
		if(center){
			p.setAlignment(Element.ALIGN_CENTER);
		}
		Cell cell = new Cell(p);
		cell.setBackgroundColor(backgroundColor);
		cell.setColspan(colspan);
		cell.setRowspan(rowspan);
		if(middle){
			cell.setUseAscender(true);
			cell.setVerticalAlignment(Cell.ALIGN_MIDDLE);
		}
		table.addCell(cell);
	}
}