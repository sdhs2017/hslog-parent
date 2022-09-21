package com.jz.bigdata.util.outPrint.javaExportDoc;

import com.google.gson.JsonObject;
import com.jz.bigdata.util.outPrint.print.outTO.to.OutTO;
import com.jz.bigdata.util.outPrint.print.style.to.StyleTO;
import com.jz.bigdata.util.outPrint.wordMaker.addCell;
import com.jz.bigdata.util.outPrint.wordMaker.insertContent;
import com.jz.bigdata.util.outPrint.wordMaker.insertTitle;
import com.jz.bigdata.util.outPrint.wordMaker.style;
import com.lowagie.text.*;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.field.RtfPageNumber;
import com.lowagie.text.rtf.field.RtfTotalPageNumber;
import com.lowagie.text.rtf.graphic.RtfShape;
import com.lowagie.text.rtf.graphic.RtfShapePosition;
import com.lowagie.text.rtf.headerfooter.RtfHeaderFooter;
import com.lowagie.text.rtf.style.RtfFont;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Itext2wordUtil {
	
	public static void main(String[] args) throws Exception {
		/** 创建Document对象（word文档） */
        Document doc = new Document(PageSize.A4,90,90,72,72);
        /** 新建字节数组输出流 */
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        /** 建立一个书写器与document对象关联，通过书写器可以将文档写入到输出流中*/
        RtfWriter2 writer = RtfWriter2.getInstance(doc, baos);
//        //文本样式
        StyleTO sto = style.setStyle(writer);
        doc.open();
        /*************************/
        //添加页眉页脚
        insertHeaderFooter(doc,sto,"","");
        //报告书首页
        insertFirstPage(doc,sto);

        //新的一页
        doc.newPage();
        //添加第一部分内容
        OutTO oto = new OutTO();
        oto.setTitle("1. 简述");
        insertTitle.insertTitle(doc, oto, sto.getHeading_1());
        //一段文字
        insertContent.insertContent(doc,"宸析日志分析系统作为一个日志分析、审计平台，能够实时将来自不同厂商的主机、网络设备、操作系统、安全设备、数据库系统、用户业务系统的日志，警报等信息汇集到审计中心，实现统一的综合安全分析审计。实时对采集到来自不同厂商，不同种类设备的日志进行规范化统一描述、信息补全、关联分析，并提供统一的界面进行实时的可视化呈现，协助用户准确、快速地识别安全事故，从而及时做出响应。", 0, 0, 24, 22, 0,sto.getNormalFont());
        //表格
        insertTable(doc,sto);

        //添加第一部分内容
        oto.setTitle("2. 具体说明");
        insertTitle.insertTitle(doc, oto, sto.getHeading_1());

        oto.setTitle("2.1 日志采集情况概览");
        insertTitle.insertTitle(doc, oto, sto.getHeading_2());
        oto.setTitle("2.2 日志综合分析");
        insertTitle.insertTitle(doc, oto, sto.getHeading_2());
        //insertImageByBase64(doc,500,200,);

        doc.close();
        FileOutputStream out = new FileOutputStream("D:/text20220526.doc",false);
        out.write(baos.toByteArray());
        System.out.println("·e·n·d");
	}

    /**
     * 测试demo
     * @param images
     */
	public static void createReportDemo(String[] images){
	    try{
            /** 创建Document对象（word文档） */
            Document doc = new Document(PageSize.A4,90,90,72,72);
            /** 新建字节数组输出流 */
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            /** 建立一个书写器与document对象关联，通过书写器可以将文档写入到输出流中*/
            RtfWriter2 writer = RtfWriter2.getInstance(doc, baos);
//        //文本样式
            StyleTO sto = style.setStyle(writer);
            doc.open();
            /*************************/
            //添加页眉页脚
            insertHeaderFooter(doc,sto,"","");
            //报告书首页
            insertFirstPage(doc,sto);

            //新的一页
            doc.newPage();
            //添加第一部分内容
            OutTO oto = new OutTO();
            oto.setTitle("1. 简述");
            insertTitle.insertTitle(doc, oto, sto.getHeading_1());
            //一段文字
            insertContent.insertContent(doc,"宸析日志分析系统作为一个日志分析、审计平台，能够实时将来自不同厂商的主机、网络设备、操作系统、安全设备、数据库系统、用户业务系统的日志，警报等信息汇集到审计中心，实现统一的综合安全分析审计。实时对采集到来自不同厂商，不同种类设备的日志进行规范化统一描述、信息补全、关联分析，并提供统一的界面进行实时的可视化呈现，协助用户准确、快速地识别安全事故，从而及时做出响应。", 0, 0, 24, 22,0, sto.getNormalFont());
            //表格
            insertTable(doc,sto);

            //添加第一部分内容
            oto.setTitle("2. 具体说明");
            insertTitle.insertTitle(doc, oto, sto.getHeading_1());

            oto.setTitle("2.1 日志采集情况概览");
            insertTitle.insertTitle(doc, oto, sto.getHeading_2());
            oto.setTitle("2.2 日志综合分析");
            insertTitle.insertTitle(doc, oto, sto.getHeading_2());

            for(String str : images){
                insertImageByBase64(doc,str);
            }

            //Thread.sleep(10000);
            doc.close();
            FileOutputStream out = new FileOutputStream("D:/text20220526000.doc",false);
            try{
                out.write(baos.toByteArray());
            }finally {
                //关闭流，否则出现文件占用
                out.close();
            }

            System.out.println("·e·n·d·");
        }catch(Exception e){
	        e.printStackTrace();
        }

    }
    /**
     * 添加页眉页脚
     * @param doc
     * @param style
     * @throws Exception
     */
    public static void insertHeaderFooter(Document doc,StyleTO style,String headerStr,String footerStr) throws Exception{
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
        //添加footer信息
        Paragraph paraFooter = new Paragraph(footerStr);
        parafooter.add(paraFooter);
        //添加分页信息
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
        Paragraph paraheader = new Paragraph(headerStr);
        paraheader.add(shape);
        paraheader.setFont(style.getFooterHeaderFont());
        HeaderFooter header = new RtfHeaderFooter(paraheader);
        header.setAlignment(Element.ALIGN_RIGHT);
        header.setBorder(Rectangle.NO_BORDER);
        doc.setHeader(header);

    }

    /**
     * 首页，报告封面
     * @param doc
     * @param style
     * @throws Exception
     */
    public static void insertFirstPage(Document doc,StyleTO style) throws Exception {
        OutTO oto = new OutTO();
        /*报告编号：__编号__ 的书写*/
        Paragraph bgbh = insertContent.buildContent(doc, "报告编号：", "hs-20220527          ", 16, true, false);
        bgbh.setFirstLineIndent(250f);
        doc.add(bgbh);

        //空白部分，填充页面,高度120
        Paragraph kb1 = new Paragraph(" ");
        kb1.setSpacingBefore(120f);
        doc.add(kb1);

        Paragraph title[] = new Paragraph[3];
        /*XX系统+XXX报告的书写*/
        //XX系统
        title[0] = new Paragraph("宸析日志分析系统");
        title[0].setFont(style.getFrontPageTitleFont());
        title[0].setAlignment(Element.ALIGN_CENTER);
        title[0].setSpacingBefore(15f);
        doc.add(title[0]);
        //信息安全等级保护测评报告
        title[1] = new Paragraph("Windows&Linux资产综合检测报告（月报）");
        title[1].setFont(style.getFrontPageTitleFont());
        title[1].setAlignment(Element.ALIGN_CENTER);
        title[1].setSpacingBefore(15f);
        doc.add(title[1]);


        //空白部分，填充页面,高度160
        Paragraph kb2 = new Paragraph(" ");
        kb2.setSpacingBefore(160f);
        doc.add(kb2);

        //首页页面下部分，4行，系统名称，单位名称等...
        //单位名称：____
        Paragraph company = insertContent.buildContent(doc, "单位名称：", "山东九州信泰信息科技股份有限公司", 16, true, false);
        company.setFirstLineIndent(80f);
        company.setSpacingBefore(20f);
        doc.add(company);
        //时间范围：_____
        Paragraph bcdw = insertContent.buildContent(doc, "时间范围：", "2022-05-26至2022-06-26", 16, true, false);
        bcdw.setFirstLineIndent(80f);
        bcdw.setSpacingBefore(20f);
        doc.add(bcdw);
    }

    /**
     *
     * @param doc
     * @param style
     * @param tables 所有tables信息的数组
     * @param visual_id 要适配的visual id
     * @throws Exception
     */
    public static void insertTableForReportModel(Document doc, StyleTO style, JSONArray tables,String visual_id) throws Exception{
        //定义table的行数和列数
        int row = 0;
        int col = 0;
        //遍历tables
        for(Object obj:tables.toArray()){

            JSONObject jsonObject = JSONObject.fromObject(obj);
            //判断是否匹配visual
            if(visual_id.equals(jsonObject.get("visual_id").toString())){
                //如果匹配，将表格数据写入
                //获取table header 数组格式，一条记录代表一个字段信息
                JSONArray tableHeaderArray = JSONArray.fromObject(jsonObject.get("tableHeader"));
                //获取table data 数组格式，一条记录代表一行数据
                JSONArray tableDataArray = JSONArray.fromObject(jsonObject.get("tableData"));
                //设置列数
                col = tableHeaderArray.size()-1;//前端传过来的表格默认都会增加一个“操作”列，该列不适用与生成表格，因此-1
                //设置行数
                row = tableDataArray.size()+1;//列名行 需要+1
                //创建表格
                Table table = new Table(col,row);
//                int[] width = {10,30,40,20};
//                table.setWidths(width);
                table.setWidth(100);
                table.setPadding(10f);
                //遍历tableheader
                for(Object headerObj:tableHeaderArray.toArray()){
                    //转为json
                    Map<String,String> headerMap = (Map)JSONObject.fromObject(headerObj);
                    //“操作”列的prop值为tools
                    if(!headerMap.get("prop").equals("tools")){
                        //写入表格
                        addCell.add(table, headerMap.get("label"),1, 1, true, false, style.getBoldFont(), new Color(191,191,191));
                    }

                }
                //遍历数据
                for(Object dataObj:tableDataArray.toArray()){
                    //转为json
                    Map<String,String> dataMap = (Map)JSONObject.fromObject(dataObj);

                    for(Object headerObj:tableHeaderArray.toArray()){
                        //转为json
                        Map<String,String> headerMap = (Map)JSONObject.fromObject(headerObj);
                        //“操作”列的prop值为tools
                        if(!headerMap.get("prop").equals("tools")){
                            //通过header的key  即prop字段，找到数据中对应的值
                            addCell.add(table,dataMap.get(headerMap.get("prop").toString()),1, 1, true, true, style.getNormalFont(), null);
                        }
                    }

                }
                table.setOffset(1f);
                Paragraph p_pre =new Paragraph("");
                RtfFont font = new RtfFont("宋体");
                p_pre.setFont(font);
                doc.add(p_pre);
//                Paragraph p =new Paragraph();
//                p.add(table);
//                doc.add(p);
                doc.add(table);

            }
        }

    }
    /**
     * 表格
     * @param doc
     * @param style
     * @throws Exception
     */
    public static void insertTable(Document doc,StyleTO style) throws Exception{
        int row = 5;
        int col = 4;
        Table table = new Table(col,row);
        int[] width = {10,30,40,20};
        table.setWidths(width);
        table.setWidth(100);
        table.setPadding(10f);

        String[] header = {"序号","姓名","岗位/角色","联系方式"};
        for(int i=0;i<header.length;i++){
            addCell.add(table, header[i],1, 1, true, false, style.getBoldFont(), new Color(191,191,191));
        }

        addCell.add(table,"1",1, 1, true, true, style.getNormalFont(), null);
        addCell.add(table, "张三",1, 1, true, true, style.getNormalFont(), null);
        addCell.add(table,"技术工程师",1, 1, true, true, style.getNormalFont(), null);
        addCell.add(table,"13111111111",1, 1, true, true, style.getNormalFont(), null);


        doc.add(table);
    }

    /**
     * 根据visual id  获取images中对应的base64信息，并写入文档中
     * @param doc
     * @param images 图片数据json
     * @param visual_id 图表id
     */
    public static void insertBase64ImageByVisualId(Document doc,JSONArray images,String visual_id) throws Exception {
        //遍历images
        for(Object obj:images.toArray()) {

            JSONObject jsonObject = JSONObject.fromObject(obj);
            //找到匹配的图片
            if(visual_id.equals(jsonObject.get("visual_id"))){
                //获取图片的base64信息
                String base64_str = jsonObject.get("base64").toString();
                // 解密
                Base64.Decoder decoder = Base64.getDecoder();
                //base64无法直接使用，需要进行删减，原理不清楚
                String base64Png = base64_str.substring(base64_str.indexOf(",", 1) + 1, base64_str.length());
                byte[] b = decoder.decode(base64Png);
                for (int i = 0; i < b.length; ++i) {
                    if (b[i] < 0) {
                        b[i] += 256;
                    }
                }
                InputStream is = new ByteArrayInputStream(b);
                try{
                    BufferedImage bImage = ImageIO.read(is);
                    Image image = Image.getInstance(b);
                    //根据图片原始长宽比例进行缩放
                    image.scaleAbsolute(400,400*bImage.getHeight()/bImage.getWidth());
                    //图片以文本包裹，解决直接写入图片导致图片有大纲（x级标题）的问题
                    Paragraph p_pre =new Paragraph("");
                    RtfFont font = new RtfFont("宋体");
                    p_pre.setFont(font);
                    doc.add(p_pre);
                    Paragraph p =new Paragraph("");
                    p.add(image);
                    doc.add(p);

                }finally {
                    //关闭流
                    is.close();
                }


            }
        }
    }
    /**
     * 插入图片
     * @param doc
     * @param base64_str 图片的base64
     * @throws IOException
     * @throws DocumentException
     */
    public static void insertImageByBase64(Document doc,String base64_str) throws IOException, DocumentException {
        // 解密
        Base64.Decoder decoder = Base64.getDecoder();
        String base64Png2 = base64_str.substring(base64_str.indexOf(",", 1) + 1, base64_str.length());
        byte[] b2 = decoder.decode(base64Png2);
        for (int i = 0; i < b2.length; ++i) {
            if (b2[i] < 0) {
                b2[i] += 256;
            }
        }
        InputStream is = new ByteArrayInputStream(b2);
        BufferedImage image = ImageIO.read(is);
        Image image2 = Image.getInstance(b2);
        //根据图片原始长宽比例进行缩放
        image2.scaleAbsolute(400,400*image.getHeight()/image.getWidth());
        doc.add(image2);
        //关闭流
        is.close();
    }
}