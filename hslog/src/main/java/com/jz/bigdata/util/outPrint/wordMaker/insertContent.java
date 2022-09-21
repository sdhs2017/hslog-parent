package com.jz.bigdata.util.outPrint.wordMaker;

import com.jz.bigdata.util.outPrint.print.outTO.to.OutTO;
import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.rtf.style.RtfFont;

import java.awt.*;


public class insertContent{
	/**
	 * 插入一段带格式的内容。
	 * 段前5磅，段后15磅，行间距22磅，首行2汉字缩进，宋体，字号12，黑色
	 * @param doc---文档对象
	 * @throws Exception
	 */
	public static void insertContent(Document doc, OutTO oto, RtfFont style) throws Exception{
		Paragraph contents = new Paragraph(oto.getContent());
		//段后距离
		contents.setSpacingAfter(15f);
		//段前距离
		contents.setSpacingBefore(5f);
		//设置第一行空的列数 ---30f为两个汉字 
		contents.setFirstLineIndent(30f); 
		//行间距
		contents.setLeading(22f);
		//设置基本格式
		contents.setFont(style);
		doc.add(contents);
	}
	
	/**
	 * @param doc--文档对象
	 * @param content--内容
	 * @param SA--段前
	 * @param SB--段后
	 * @param FLI--首行缩进
	 * @param lead--行间距
	 * @param alignment -上下左右居
	 * @throws Exception
	 */
	public static void insertContent(Document doc,String content,int SA,int SB ,int FLI,int lead, int alignment,RtfFont style) throws Exception{
		Paragraph contents = new Paragraph(content);
		//段后距离
		contents.setSpacingAfter(SA);
		//段前距离
		contents.setSpacingBefore(SB);
		//设置第一行空的列数 ---30f为两个汉字 
		contents.setFirstLineIndent(FLI); 
		//行间距
		contents.setLeading(lead);
		//设置基本格式
		contents.setFont(style);

		contents.setAlignment(alignment);
		doc.add(contents);
	}
	
	/**插入一段带格式的Table的题注
	 * 段前15磅，段后5磅，行间距22磅，左右居中，宋体，字号12，黑色
	 * @param doc
	 * @param oto
	 * @param style
	 * @throws Exception
	 */
	public static void insertTableName(Document doc,OutTO oto,RtfFont style)throws Exception{
		Paragraph contents = new Paragraph(oto.getContent());
		//段后距离
		contents.setSpacingAfter(15f);
		//段前距离
		contents.setSpacingBefore(5f);
		//左右居中
		contents.setAlignment(Element.ALIGN_CENTER);
		//行间距
		contents.setLeading(22f);
		//设置基本格式
		contents.setFont(style);
		doc.add(contents);
	}
	
	/**
	 * 将两端字符串以固定的格式组装起来
	 * A：_B_
	 * @param doc--文件对象
	 * @param A--字符串A
	 * @param B--字符串B
	 * @param size--字体大小
	 * @param boldA--字段A是否加粗
	 * @param boldB--字段B是否加粗
	 * @return
	 * @throws Exception 
	 */
	public static Paragraph buildContent(Document doc,String A,String B,int size,boolean boldA,boolean boldB) throws Exception{
		//“报告编号：”-宋体，字号，字体颜色黑色
		RtfFont AFont = new RtfFont("宋体",size,Font.BOLD,Color.BLACK);
		//带下划线
		RtfFont BFont = new RtfFont("宋体",size,Font.UNDERLINE,Color.BLACK);
		//系统名称:____
		Chunk Text = new Chunk(A);
		Chunk _Text = new Chunk(B+"");
		Text.setFont(AFont);
		_Text.setFont(BFont);
		//将两个字块依次放入一个Paragraph（段落）对象中
		Paragraph con = new Paragraph();
		con.add(Text);
		con.add(_Text);
		return con;
	}
	
}