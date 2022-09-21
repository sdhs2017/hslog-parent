package com.jz.bigdata.util.outPrint.print.style.to;

import com.lowagie.text.rtf.style.RtfFont;
import com.lowagie.text.rtf.style.RtfParagraphStyle;

public class StyleTO {
	private RtfFont boldFont;//加粗样式
	private RtfFont cpHeader;//结果记录的“对象，测评内容，符合性”几个字的样式
	private RtfFont normalFont;//普通样式
	private RtfFont normalFontA;//普通样式，用于对其他自定义样式的存储，比如字号的修改，加粗等等
	private RtfFont frontPageTitleFont;//首页标题的格式
	private RtfFont titleFont;//普通标题的样式，不带大纲级别
	private RtfFont footerHeaderFont;//页眉页脚样式
	
	private RtfParagraphStyle heading_1;//一级大纲
	private RtfParagraphStyle heading_2;//二级
	private RtfParagraphStyle heading_3;//三级
	private RtfParagraphStyle heading_4;//四级
	
	public RtfFont getFooterHeaderFont() {
		return footerHeaderFont;
	}
	public void setFooterHeaderFont(RtfFont footerHeaderFont) {
		this.footerHeaderFont = footerHeaderFont;
	}
	public RtfFont getTitleFont() {
		return titleFont;
	}
	public void setTitleFont(RtfFont titleFont) {
		this.titleFont = titleFont;
	}
	public RtfFont getBoldFont() {
		return boldFont;
	}
	public void setBoldFont(RtfFont boldFont) {
		this.boldFont = boldFont;
	}
	public RtfFont getCpHeader() {
		return cpHeader;
	}
	public RtfFont getFrontPageTitleFont() {
		return frontPageTitleFont;
	}
	public void setFrontPageTitleFont(RtfFont frontPageTitleFont) {
		this.frontPageTitleFont = frontPageTitleFont;
	}
	public void setCpHeader(RtfFont cpHeader) {
		this.cpHeader = cpHeader;
	}
	public RtfFont getNormalFont() {
		return normalFont;
	}
	public void setNormalFont(RtfFont normalFont) {
		this.normalFont = normalFont;
	}
	public RtfParagraphStyle getHeading_1() {
		return heading_1;
	}
	public void setHeading_1(RtfParagraphStyle heading_1) {
		this.heading_1 = heading_1;
	}
	public RtfParagraphStyle getHeading_2() {
		return heading_2;
	}
	public void setHeading_2(RtfParagraphStyle heading_2) {
		this.heading_2 = heading_2;
	}
	public RtfParagraphStyle getHeading_3() {
		return heading_3;
	}
	public void setHeading_3(RtfParagraphStyle heading_3) {
		this.heading_3 = heading_3;
	}
	public RtfParagraphStyle getHeading_4() {
		return heading_4;
	}
	public void setHeading_4(RtfParagraphStyle heading_4) {
		this.heading_4 = heading_4;
	}
	public RtfFont getNormalFontA() {
		return normalFontA;
	}
	public void setNormalFontA(RtfFont normalFontA) {
		this.normalFontA = normalFontA;
	}
}