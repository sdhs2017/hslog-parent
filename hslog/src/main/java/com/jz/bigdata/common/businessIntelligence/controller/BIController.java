package com.jz.bigdata.common.businessIntelligence.controller;

import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.businessIntelligence.entity.MappingField;
import com.jz.bigdata.common.businessIntelligence.service.IBIService;
import com.jz.bigdata.common.metadata.controller.MetadataController;
import com.jz.bigdata.common.metadata.service.IMetadataService;
import com.jz.bigdata.util.DescribeLog;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import scala.collection.immutable.Stream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * BI报表
 */
@Controller
@RequestMapping("/BI")
public class BIController {
    private static Logger logger = Logger.getLogger(BIController.class);

    @Resource(name = "BIService")
    private IBIService iBIService;
    /**
     * Buckets
     * 通过X轴的聚合方式获取fields
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getFieldByXAxisAggregation", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "通过X轴的聚合方式获取对应的列")
    public String getFieldByXAxisAggregation(HttpServletRequest request){
        try{
            //聚合方式  支持count/max/min/sum/avg
            String agg = request.getParameter("agg");
            //索引名称
            String indexName = request.getParameter("indexName");
            //template名称
            String templateName = request.getParameter("templateName");
            //
            List<MappingField> result = iBIService.getFieldByXAxisAggregation(templateName,indexName,agg);
            return JSONArray.fromObject(result).toString();
        }catch(Exception e){
            logger.error("通过X轴的聚合方式获取对应的列失败："+e.getStackTrace().toString());
            return Constant.failureMessage("数据查询失败");
        }
    }
    /**
     * Metrics
     * 通过Y轴的聚合方式获取fields
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getFieldByYAxisAggregation", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "通过Y轴的聚合方式获取对应的列")
    public String getFieldByYAxisAggregation(HttpServletRequest request){
        try{
            //聚合方式  支持count/max/min/sum/avg
            String agg = request.getParameter("agg");
            //索引名称
            String indexName = request.getParameter("indexName");
            //template名称
            String templateName = request.getParameter("templateName");
            List<MappingField> result = iBIService.getFieldByYAxisAggregation(templateName,indexName,agg);
            return JSONArray.fromObject(result).toString();
        }catch(Exception e){
            logger.error("通过Y轴的聚合方式获取对应的列失败："+e.getStackTrace().toString());
            return Constant.failureMessage("数据查询失败");
        }
    }

    /**
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getDataByChartParams", produces = "application/json; charset=utf-8")
    @DescribeLog(describe = "通过图表参数获取查询结果，并返回")
    public String getDataByChartParams(HttpServletRequest request){
        try{
            //聚合方式
            String x_agg = request.getParameter("x_agg");
            String y_agg = request.getParameter("y_agg");
            //聚合的字段
            String x_field = request.getParameter("x_field");
            String y_field = request.getParameter("y_field");
            //查询数据条数
            String size = request.getParameter("size");
            //默认为10条记录
            int sizeInt = 10;
            if(size!=null){
                try{
                    sizeInt = Integer.parseInt(size);
                }catch (Exception e){
                    logger.error("size参数不为整数");
                }
            }
            //排序，Y轴
            String sort = request.getParameter("sort");
            //时间间隔类型
            String intervalType = request.getParameter("intervalType");
            //时间间隔数值
            String intervalValue = request.getParameter("intervalValue");
            //索引名称
            String indexName = request.getParameter("indexName");
            //返回结果
            String result;
            //根据聚合方式调用不同的方法
            switch (y_agg){
                case "Average":
                    result = iBIService.groupByThenAvg(indexName,x_field,x_agg,y_field,sizeInt,sort,null,null,null);
                    break;
                case "Sum":
                    result = iBIService.groupByThenSum(indexName,x_field,x_agg,y_field,sizeInt,sort,null,null,null);
                    break;
                case "Max":
                    result = iBIService.groupByThenMax(indexName,x_field,x_agg,y_field,sizeInt,sort,null,null,null);
                    break;
                case "Min":
                    result = iBIService.groupByThenMin(indexName,x_field,x_agg,y_field,sizeInt,sort,null,null,null);
                    break;
                case "Count":
                    result = iBIService.groupByThenCount(indexName,x_field,x_agg,y_field,sizeInt,sort,null,null,null);
                    break;
                default:
                    result = Constant.failureMessage("聚合格式错误");
                    break;
            }
            return Constant.successData(result);
        }catch(Exception e){
            logger.error("通过Y轴的聚合方式获取对应的列失败："+e.getStackTrace().toString());
            return Constant.failureMessage("数据查询失败");
        }
    }
}
