package com.jz.bigdata.util.POI;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * @program: hslog-parent
 * @description: 读取excel的数据
 * @author: jiyourui
 * @create: 2020-07-28 16:26
 **/

public class ReadExcel {


    private int totalRows = 0;//总行数
    private int totalCells = 0;//构造方法

    public ReadExcel(){}
    public int getTotalRows()  { return totalRows;}
    public int getTotalCells() {  return totalCells;}

    /**
     * 获取sheet对象
     *
     * @param file
     * @return
     */

    public static XSSFSheet getXSSFSheet(File file) {
        InputStream is = null;
        XSSFWorkbook xssfWorkbook = null;
        try {
            is = new FileInputStream(file);
            xssfWorkbook = new XSSFWorkbook(is);
        } catch (IOException e) {
            return null;
        }
        //获取工作表对象
        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
        return xssfSheet;
    }


    /**
     * 获取excel的内容
     * @param filePath
     * @return
     */
    public List<List<String>> getExcelInfo(String filePath) throws IOException {
        //初始化集合
        List<List<String>> lList=new ArrayList<List<String>>();
        //初始化输入流
        InputStream is = null;
        try{
            //根据文件名判断文件是2003版本（xls）还是2007版本（xlsx,xlsm）
            boolean isExcel2003 = true;
            if(filePath.matches("^.+\\.(?i)(xlsx)$")||filePath.matches("^.+\\.(?i)(xlsm)$")){
                isExcel2003 = false;
            }

            //根据新建的文件实例化输入流
            is = new FileInputStream(filePath);
            Workbook wb = null;
            //根据excel里面的内容读取客户信息
            if(isExcel2003){//当excel是2003时
                wb = new HSSFWorkbook(is);
            }
            else{//当excel是2007时
                wb = new XSSFWorkbook(is);
            }
            //读取Excel里面客户的信息
            lList=readExcelValue(wb);
            is.close();
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        } finally{
            if(is !=null)
            {
                try{
                    is.close();
                }catch(IOException e){
                    is = null;
                    e.printStackTrace();
                }
            }
        }
        return lList;
    }


    /**
     * 读取Excel里面客户的信息
     * @param wb
     * @return
     */
    private List<List<String>> readExcelValue(Workbook wb){
        //得到第一个shell
        Sheet sheet=wb.getSheetAt(0);

        //得到Excel的行数
        this.totalRows=sheet.getPhysicalNumberOfRows();

        //得到Excel的列数(前提是有行数)
        if(totalRows>=1 && sheet.getRow(0) != null){
            this.totalCells=sheet.getRow(0).getPhysicalNumberOfCells();
        }

        List<List<String>> lList=new ArrayList<List<String>>();
        //循环Excel行数,从第二行开始。标题不入库
        for(int r=1;r<totalRows;r++){
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            List<String> sList=new ArrayList<String>();

            //循环Excel的列
            for(int c = 0; c <this.totalCells; c++){
                Cell cell = row.getCell(c);
                if (cell != null){

//                    cell.setCellType(CellType.STRING);
//                    sList.add(cell.getStringCellValue());
                    sList.add(getCellValueByCell(cell));
                }else {
                    sList.add("");
                }
            }

            //添加
            lList.add(sList);
        }
        return lList;
    }

    /**
     * 获取表格里的值，增加对时间类型数据的处理
     * @param cell
     * @return
     */
    public static String getCellValueByCell(Cell cell) {
        //判断是否为null或空串
        if (cell==null || cell.toString().trim().equals("")) {
            return "";
        }
        String cellValue = "";
        CellType cellType=cell.getCellType();

        if(cellType.equals(CellType.NUMERIC)){
            short format = cell.getCellStyle().getDataFormat();
            if (DateUtil.isCellDateFormatted(cell)) {
                SimpleDateFormat sdf = null;
                //System.out.println("cell.getCellStyle().getDataFormat()="+cell.getCellStyle().getDataFormat());
                if (format == 20 || format == 32) {
                    sdf = new SimpleDateFormat("HH:mm");
                } else if (format == 14 || format == 31 || format == 57 || format == 58) {
                    // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    double value = cell.getNumericCellValue();
                    Date date = org.apache.poi.ss.usermodel.DateUtil
                            .getJavaDate(value);
                    cellValue = sdf.format(date);
                }else {// 日期
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                }
                try {
                    cellValue = sdf.format(cell.getDateCellValue());// 日期
                } catch (Exception e) {
                    try {
                        throw new Exception("exception on get date data !".concat(e.toString()));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }finally{
                    sdf = null;
                }
            }  else {
                BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
                cellValue = bd.toPlainString();// 数值 这种用BigDecimal包装再获取plainString，可以防止获取到科学计数值
            }
        }else{
            cell.setCellType(CellType.STRING);
            cellValue = cell.getStringCellValue();
        }
        return cellValue;

    }
}
