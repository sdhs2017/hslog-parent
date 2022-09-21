package com.jz.bigdata.common.ReportModel.service.impl;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import com.hs.elsearch.dao.biDao.IBIDao;
import com.hs.elsearch.dao.logDao.ILogCrudDao;
import com.hs.elsearch.entity.Metric;
import com.hs.elsearch.entity.SearchConditions;
import com.hs.elsearch.util.ElasticConstant;
import com.jz.bigdata.common.Constant;
import com.jz.bigdata.common.ReportModel.dao.IReportModelDao;
import com.jz.bigdata.common.ReportModel.entity.ReportModel;
import com.jz.bigdata.common.ReportModel.entity.ReportModelData;
import com.jz.bigdata.common.ReportModel.entity.ReportModelInfo;
import com.jz.bigdata.common.ReportModel.service.IReportModelService;

import com.jz.bigdata.common.businessIntelligence.service.IBIService;
import com.jz.bigdata.common.license.LicenseExtra;
import com.jz.bigdata.common.license.VerifyLicense;
import com.jz.bigdata.util.*;
import com.jz.bigdata.util.HSTree.HSTree;
import com.jz.bigdata.util.HSTree.HSTreeUtil;
import com.jz.bigdata.util.outPrint.javaExportDoc.Itext2wordUtil;
import com.jz.bigdata.util.outPrint.print.outTO.to.OutTO;
import com.jz.bigdata.util.outPrint.print.style.to.StyleTO;
import com.jz.bigdata.util.outPrint.wordMaker.insertContent;
import com.jz.bigdata.util.outPrint.wordMaker.insertTitle;
import com.jz.bigdata.util.outPrint.wordMaker.style;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.style.RtfFont;
import com.lowagie.text.rtf.style.RtfParagraphStyle;
import de.schlichtherle.license.LicenseContent;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.ss.formula.functions.T;
import org.elasticsearch.common.Strings;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**

 * @description
 */
@Slf4j
@Service(value = "ReportModelService")
public class ReportModelServiceImpl implements IReportModelService {
	@Resource
	private IReportModelDao reportModelDao;
	@Resource(name = "BIService")
	private IBIService iBIService;
	@Resource(name = "licenseService")
	private VerifyLicense verifyLicense;
	@Resource(name ="configProperty")
	private ConfigProperty configProperty;
	@Autowired
	protected IBIDao ibiDao;
	@Autowired
	protected ILogCrudDao logCurdDao;
	//今天、这周、本月等时间类型对应的格式化定义
	private static final String startPattern = "yyyy-MM-dd 00:00:00";
	private static final String endPattern = "yyyy-MM-dd 23:59:59";
	private static final String simple = "yyyy-MM-dd HH:mm:ss";
	private static final DateTimeFormatter dtf_start = DateTimeFormatter.ofPattern(startPattern);
	private static final DateTimeFormatter dtf_end = DateTimeFormatter.ofPattern(endPattern);
	private static final DateTimeFormatter dtf_simple = DateTimeFormatter.ofPattern(simple);

	@Override
	public String createReport(String images, String tables, String metrics,String dashboardID,String reportModelType,SearchConditions searchConditions) throws Exception {

		//对参数进行格式化，参数都可以转成json
		JSONArray imagesArray = JSONArray.fromObject(images);
		JSONArray tablesArray = JSONArray.fromObject(tables);
		JSONArray metricArray = JSONArray.fromObject(metrics);
		try{

			//获取模板的信息
			List<ReportModel> reportModelList = reportModelDao.getReportModelByDashboardIDAndType(dashboardID,reportModelType);
			//如果能正确获取到模板信息
			if(reportModelList.size()==1){
				ReportModel reportModel = reportModelList.get(0);
				/** 创建Document对象（word文档） */
				Document doc = new Document(PageSize.A4,90,90,72,72);
				/** 新建字节数组输出流 */
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				/** 建立一个书写器与document对象关联，通过书写器可以将文档写入到输出流中*/
				RtfWriter2 writer = RtfWriter2.getInstance(doc, baos);
				//文本样式
				StyleTO sto = style.setStyle(writer);
				doc.open();
				//写入word
				//页眉写入时间范围

				StringBuffer timeAreaAppend = new StringBuffer();//要添加到页眉的内容
				timeAreaAppend.append(" ");
				timeAreaAppend.append(searchConditions.getStartTime());
				timeAreaAppend.append(" ~ ");
				timeAreaAppend.append(searchConditions.getEndTime());
				Itext2wordUtil.insertHeaderFooter(doc,sto,reportModel.getReportModelHeader()+timeAreaAppend,reportModel.getReportModelFooter());

				//根据报告ID获取报告详情信息
				List<ReportModelInfo>  reportModelInfoList = reportModelDao.getReportModelInfoByReportModelID(reportModel.getReportModelID());
				//对源数据进行转换对应，即tree节点会用到的  id ，pid，name 显示名称，weight（权重，即排序字段） 以及扩展字段
				List<TreeNode<String>> treeNodeList = reportModelInfoList.stream().map(reportModelInfo -> {
					//tree单个节点的数据构建
					TreeNode<String> treeNode = new TreeNode<>();
					treeNode.setId(reportModelInfo.getReportModelInfoID());
					treeNode.setParentId(reportModelInfo.getParentID());
					treeNode.setWeight(reportModelInfo.getOrderCode());
					//扩展数据
					Map<String,Object> extra = new HashMap<>();
					extra.put("contentType",reportModelInfo.getContentType());
					extra.put("content",reportModelInfo.getContent());
					extra.put("bold",reportModelInfo.getBold());
					extra.put("alignment",reportModelInfo.getAlignment());
					extra.put("fontSize",reportModelInfo.getFontSize());
					extra.put("remark",reportModelInfo.getRemark());
					extra.put("spacingBefore",reportModelInfo.getSpacingBefore());
					extra.put("imageName",reportModelInfo.getImageName());
					extra.put("tableName",reportModelInfo.getTableName());
					extra.put("titleLevel",reportModelInfo.getTitleLevel());
					//.....
					treeNode.setExtra(extra);
					return treeNode;
				}).collect(Collectors.toList());
				//构建tree结构数据，根节点的id为空
				List<Tree<String>> reportModelInfo_tree = HSTreeUtil.build(treeNodeList,"");
				//通过treemap实现key的自动排序
				Map<Long,Tree<String>> result = new TreeMap<>();
				//对tree自上而下的元素进行排序
                HSTreeUtil.orderAll(reportModelInfo_tree,1,0,result);
				//遍历数据，写入word
				writeToWord(result,doc,sto,imagesArray,tablesArray,metricArray,reportModelType);
				//旧版本
                //writeDocRecursion(reportModel.getReportModelID(),"",doc,sto,imagesArray,tablesArray,metricArray,reportModelType);
				//生成文件
				doc.close();
				//获取生成文件的存放路径
				String path = getClass().getClassLoader().getResource("/reportDownload/").getPath();
				//拼装文件名称，格式：单位名称（license）- 报告模板名称-报告类型-日期
				//获取单位名称
				LicenseContent licenseContent = verifyLicense.getLicenseContent();
				LicenseExtra extra = (LicenseExtra)licenseContent.getExtra();
				//文件名相同会自动覆盖
				String fileName = extra.getCustom_name()+"-"+reportModel.getReportModelName()+"-"+Constant.REPORT_MODEL_TYPE.get(reportModel.getReportModelType())+"-"+DateTime.now().toString("yyyy-MM-dd")+".doc";
				File f = new File(path+fileName);
				//如果文件已存在，删除文件
				if(f.exists()){
					f.delete();
				}else{
					//文件不存在，不做处理，继续生成文件即可
				}
				//重新生成文件
				FileOutputStream out = new FileOutputStream(path+fileName,false);
				System.out.print("--------"+path+fileName);
				try{
					out.write(baos.toByteArray());
				}finally {
					//关闭流，否则出现文件占用
					out.close();
				}
				return path+fileName;
			}else if(reportModelList.size()==0){
				//未查到模板
				return "ReportModelNotFound";
			}else{
				//查到多个模板
				return "ReportModelNotOnly";
			}

		}catch (Exception e){
			throw e;
		}
	}

	@Override
	public Map<String, Object> getReportModelListByPage(int startRecord, int PageSize, String reportModelName, String reportModelType) {
		//返回结果
		Map<String, Object> map = new HashMap<String, Object>();
		//获取count数
		List<List<Map<String,String>>> resultCount = reportModelDao.getReportModelCount(reportModelName,reportModelType);
		map.put("count",resultCount.get(0).get(0).get("count"));
		List<ReportModel> list = reportModelDao.getReportModelListByPage(startRecord,PageSize,reportModelName,reportModelType);
		//遍历
		for(ReportModel reportModel:list){
			//报告模板类型  英文-中文
			reportModel.setReportModelType_CN(Constant.REPORT_MODEL_TYPE.get(reportModel.getReportModelType()));
		}
		map.put("reportModelList",list);
		return map;

	}

	@Override
	public ReportModel getReportModelByReportModelID(String reportModelID) {
		ReportModel reportModel = reportModelDao.getReportModelByReportModelID(reportModelID);
		//报告模板类型  英文-中文
		reportModel.setReportModelType_CN(Constant.REPORT_MODEL_TYPE.get(reportModel.getReportModelType()));
		return reportModel;
	}

	@Override
	public int updateReportModelByReportModelID(ReportModel reportModel){
		return reportModelDao.updateReportModelByReportModelID(reportModel);
	}

	@Override
	public ReportModelInfo getReportModelInfoByReportModelInfoID(String reportModelInfoID) {
		return reportModelDao.getReportModelInfoByReportModelInfoID(reportModelInfoID);
	}

	@Override
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	public boolean updateReportModelInfo(List<ReportModelInfo> list, ReportModel reportModel){
		//更新页眉页脚
		reportModelDao.updateReportModelHeaderAndFooterByReportModelID(reportModel);
		//更新详情列表
		for(ReportModelInfo reportModelInfo:list){
			reportModelDao.updateReportModelInfoByReportModelInfoID(reportModelInfo);
		}
		return true;
	}

	@Override
	public List<HSTree> getReportModelInfoTree(String reportModelID) {
		//生成tree，第一级的parentid为空
		List<HSTree> tree = getReportModelInfoTreeNodes("",reportModelID);
		return tree;
	}

	@Override
	public Map<String, Object> getReportModelInfoAll(String reportModelID) {
		Map<String,Object> result = new HashMap<>();
		//1.获取基本信息
		ReportModel reportModel = reportModelDao.getReportModelByReportModelID(reportModelID);
		//报告模板类型  英文-中文
		reportModel.setReportModelType_CN(Constant.REPORT_MODEL_TYPE.get(reportModel.getReportModelType()));
		//判断是否能正常获取到基本信息
		if(reportModel!=null){
			//如果能正常获取到
			result.put("reportModel",reportModel);
			//通过reportModelID 获取详情信息（tree）
//			List<HSTree> reportModelInfo =  getReportModelInfoTreeNodes("",reportModelID);
			List<ReportModelInfo>  reportModelInfoList = reportModelDao.getReportModelInfoByReportModelID(reportModelID);
			//对源数据进行转换对应，即tree节点会用到的  id ，pid，name 显示名称，weight（权重，即排序字段）
 			List<TreeNode<String>> treeNodeList = reportModelInfoList.stream().map(reportModelInfo -> {
				//tree单个节点的数据构建
				TreeNode<String> treeNode = new TreeNode<String>();
				treeNode.setId(reportModelInfo.getReportModelInfoID());
				treeNode.setParentId(reportModelInfo.getParentID());
				//不同内容类型的显示名称字段不同
				switch(reportModelInfo.getContentType()){
					case "image"://图片
						treeNode.setName(reportModelInfo.getImageName());
						break;
					case "table"://表格
						treeNode.setName(reportModelInfo.getTableName());
						break;
					case "text":
						//TODO
						JSONArray array = JSONArray.fromObject(reportModelInfo.getContent());
						if(array.size()>0){
							for(int i=0;i<array.size();i++){
								String text = array.getJSONObject(i).getString("text");
								if(text.length()>0){
									treeNode.setName(text);
									break;
								}
							}
						}else{
							//文本中无数据，返回的节点内容为空
							treeNode.setName("");
						}
						//treeNode.setLabel(JSONArray.fromObject()reportModelInfo.getContent());
						break;
					default://其他默认
						treeNode.setName(reportModelInfo.getContent());
						break;
				}
				treeNode.setWeight(reportModelInfo.getOrderCode());
				//扩展数据
				//示例代码
				//Map<String,Object> extra = new HashMap<>();
				//extra.put("test_key","test_value");
				//.....
				//treeNode.setExtra(extra);
				return treeNode;
			}).collect(Collectors.toList());
			//构建tree结构数据，根节点的id为空
			List<Tree<String>> reportModelInfo_tree = HSTreeUtil.build(treeNodeList,"");
			result.put("reportModelInfo",reportModelInfo_tree==null?new ArrayList<Tree<String>>():reportModelInfo_tree);
		}else{
			//如果无法正常获取，直接返回result，controller层通过检测map的size确定要返回到前端的信息
		}
		return result;
	}

    @Override
    public int updateReportModelInfoByReportModelInfoID(ReportModelInfo reportModelInfo) {
        return reportModelDao.updateReportModelInfoByReportModelInfoID(reportModelInfo);
    }

	@Override
	public List<ComboxEntity> getReportModelListByDashboardID(String dashboardID) {
		List<ComboxEntity> result = new ArrayList<>();
		//根据仪表板id查询出所有模板信息
		List<ReportModel> list = reportModelDao.getReportModelListByDashboardID(dashboardID);
		for(ReportModel reportModel:list){
			//组装一条combox
			ComboxEntity entity = new ComboxEntity();
			entity.setValue(reportModel.getReportModelType());
			entity.setLabel(reportModel.getReportModelName()+"-"+Constant.REPORT_MODEL_TYPE.get(reportModel.getReportModelType()));
			result.add(entity);
		}
		return result;
	}

	@Override
	public List<ComboxEntity> getReportModelListByDashboardID_ReportModelAdd(String dashboardID) {
		List<ComboxEntity> result = new ArrayList<>();
		//组装一条combox（无）
		ComboxEntity entity_null = new ComboxEntity();
		entity_null.setValue("");
		entity_null.setLabel("无");
		result.add(entity_null);
		//根据仪表板id查询出所有模板信息
		List<ReportModel> list = reportModelDao.getReportModelListByDashboardID(dashboardID);
		for(ReportModel reportModel:list){
			//组装一条combox
			ComboxEntity entity = new ComboxEntity();
			entity.setValue(reportModel.getReportModelID());
			entity.setLabel(reportModel.getReportModelName()+"-"+Constant.REPORT_MODEL_TYPE.get(reportModel.getReportModelType()));
			result.add(entity);
		}

		return result;
	}

	@Override
	public List<ComboxEntity> getAllDashboardList() throws Exception {
		List<ComboxEntity> result = new ArrayList<>();
		//默认存储数据可视化的index（hsdata），以及仪表板对应的字段   dashboard
		List<Map<String, Object>> list = ibiDao.getListExistsField(configProperty.getEs_hsdata_index(), ElasticConstant.HSDATA_DASHBOARD);
		for(Map<String, Object> map:list){
			ComboxEntity entity = new ComboxEntity();
			entity.setLabel(JSONObject.fromObject(map.get("dashboard")).get("title").toString());
			entity.setValue(map.get("id").toString());
			result.add(entity);
		}
		return result;
	}

	@Override
	public List<ComboxEntity> getTablesByDashboardID(String dashboardID) throws Exception {
		List<ComboxEntity> result = new ArrayList<>();
		//根据id获取图表详情
		Map<String, Object> map = logCurdDao.searchById(configProperty.getEs_hsdata_index(),"",dashboardID);
		//获取option 信息，其中存储的是dashboard中所有图标的信息 array
		JSONArray array = JSONArray.fromObject(JSONObject.fromObject(map.get("dashboard")).get("option"));
		for(int i=0;i<array.size();i++){
			//如果dashboard中的图标的信息，获取图标类型，判断是不是table
			if(array.getJSONObject(i).get("chartType").toString().equals("table")){
				//是table的，写入返回结果
				ComboxEntity entity = new ComboxEntity();
				entity.setLabel(array.getJSONObject(i).get("tit").toString());
				entity.setValue(array.getJSONObject(i).get("eId").toString());
				result.add(entity);
			}
		}
		return result;
	}

	@Override
	public List<ComboxEntity> getImagesByDashboardID(String dashboardID) throws Exception {
		List<ComboxEntity> result = new ArrayList<>();
		//根据id获取图表详情
		Map<String, Object> map = logCurdDao.searchById(configProperty.getEs_hsdata_index(),"",dashboardID);
		//获取option 信息，其中存储的是dashboard中所有图标的信息 array
		JSONArray array = JSONArray.fromObject(JSONObject.fromObject(map.get("dashboard")).get("option"));
		for(int i=0;i<array.size();i++){
			//如果dashboard中的图标的信息，获取图标类型，判断是不是柱状图/饼图/折线图
			String chartType = array.getJSONObject(i).get("chartType").toString();
			if("pie".equals(chartType)||"bar".equals(chartType)||"line".equals(chartType)){
				//是table的，写入返回结果
				ComboxEntity entity = new ComboxEntity();
				entity.setLabel(array.getJSONObject(i).get("tit").toString());
				entity.setValue(array.getJSONObject(i).get("eId").toString());
				result.add(entity);
			}
		}
		return result;
	}

	@Override
	public List<ComboxEntity> getMetricsByDashboardID(String dashboardID) throws Exception {
		List<ComboxEntity> result = new ArrayList<>();
		//根据id获取图表详情
		Map<String, Object> map = logCurdDao.searchById(configProperty.getEs_hsdata_index(),"",dashboardID);
		//获取option 信息，其中存储的是dashboard中所有图标的信息 array
		JSONArray array = JSONArray.fromObject(JSONObject.fromObject(map.get("dashboard")).get("option"));
		for(int i=0;i<array.size();i++){
			//如果dashboard中的图标的信息，获取图标类型，判断是不是table
			if(array.getJSONObject(i).get("chartType").toString().equals("metric")){
				//是table的，写入返回结果
				ComboxEntity entity = new ComboxEntity();
				entity.setLabel(array.getJSONObject(i).get("tit").toString());
				entity.setValue(array.getJSONObject(i).get("eId").toString());
				result.add(entity);
			}
		}
		return result;
	}

	@Override
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	public int UpdateTreeOrder(String nodeList_Str) {
		List<ReportModelInfo> updateList = new ArrayList<>();
		//String转array
		JSONArray array = JSONArray.fromObject(nodeList_Str);
		//遍历
		for(int i=0;i<array.size();i++){
			ReportModelInfo reportModelInfo = new ReportModelInfo();
			JSONObject jsonObj = array.getJSONObject(i);
			reportModelInfo.setReportModelInfoID(jsonObj.get("deptId").toString());
			reportModelInfo.setParentID(jsonObj.get("parentDeptId").toString());
			reportModelInfo.setOrderCode(Integer.parseInt(jsonObj.get("order").toString()));
			updateList.add(reportModelInfo);
		}
		//批量更新
		return reportModelDao.batchUpdateTreeInfo(updateList);
	}

	@Override
	public ReportModelInfo insertReportModelInfo(ReportModelInfo reportModelInfo) {
		//要添加的数据，设置唯一ID
		reportModelInfo.setReportModelInfoID(Uuid.getUUID());
		//设置默认文本
		reportModelInfo.setContent("[{\"text\":\"新增节点\",\"metricID\":\"\"}]");
		reportModelInfo.setContentType("text");
		reportModelInfo.setFontSize(14);
		reportModelInfo.setBold(0);
		reportModelInfo.setSpacingBefore(0);
		reportModelInfo.setAlignment(0);
		//如果parentID为空，将值设置为空字符串，保证生成tree时第一级节点正常。
		if(reportModelInfo.getParentID()==null){
			reportModelInfo.setParentID("");
		}
		reportModelDao.insertReportModelInfo(reportModelInfo);
		//前端返回要求，进行特殊处理
		reportModelInfo.setContent("新增节点");
		return reportModelInfo;
	}

	@Override
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	public int deleteReportModelInfo(String reportModelInfoID) {
		return reportModelDao.deleteReportModelInfo(reportModelInfoID);
	}

	@Override
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	public int insertReportModel(ReportModel reportModel,String extendReportModelID) {
		String reportModelID = Uuid.getUUID();
		//设置唯一ID
		reportModel.setReportModelID(reportModelID);
		if(Strings.isNullOrEmpty(extendReportModelID)){

		}else{
			//根据要继承的模板的ID，获取所有的报告模板详情数据
			List<ReportModelInfo>  reportModelInfoList = reportModelDao.getReportModelInfoByReportModelID(extendReportModelID);
			//基于上面的list构建一个map key:reportModelInfoID   value ReportModelInfo
			Map<String,ReportModelInfo> reportModelInfoMap = new HashMap<>();
			for(ReportModelInfo reportModelInfo:reportModelInfoList){
				reportModelInfoMap.put(reportModelInfo.getReportModelInfoID(),reportModelInfo);
			}

			//对源数据进行转换对应，即tree节点会用到的  id ，pid，weight（权重，即排序字段）
			List<TreeNode<String>> treeNodeList = reportModelInfoList.stream().map(reportModelInfo -> {
				//tree单个节点的数据构建
				TreeNode<String> treeNode = new TreeNode<String>();
				treeNode.setId(reportModelInfo.getReportModelInfoID());
				treeNode.setParentId(reportModelInfo.getParentID());
				treeNode.setWeight(reportModelInfo.getOrderCode());
				return treeNode;
			}).collect(Collectors.toList());
			//构建tree结构数据，根节点的id为空
			List<Tree<String>> reportModelInfo_tree = HSTreeUtil.build(treeNodeList,"");
			//定义要重新写入的ReportModelInfoList
			List<ReportModelInfo>  reportModelInfoList_copy = new ArrayList<>();

			recursionTreeNodeAndInsertCopy(reportModelID,"",reportModelInfo_tree,reportModelInfoMap,reportModelInfoList_copy);

			reportModelDao.batchInsertReportModelInfo(reportModelInfoList_copy);

		}

		return reportModelDao.saveReportModel(reportModel);
	}

	private void recursionTreeNodeAndInsertCopy(String reportModelID,String parentID,List<Tree<String>> treeNodeList,Map<String,ReportModelInfo> reportModelInfoMap,List<ReportModelInfo>  reportModelInfoList_copy){
		for(Tree<String> treeNode:treeNodeList){
			ReportModelInfo reportModelInfo = reportModelInfoMap.get(treeNode.getId());
			//将新的ID、PID、ModelID 写入ReportModelInfo中
			String newUUID_ReportModelInfoID = Uuid.getUUID();
			reportModelInfo.setReportModelInfoID(newUUID_ReportModelInfoID);
			reportModelInfo.setParentID(parentID);
			reportModelInfo.setReportModelID(reportModelID);
			reportModelInfoList_copy.add(reportModelInfo);
			//如果存在子节点，进行递归
			if(treeNode.getChildren()!=null&&treeNode.getChildren().size()>0){
				recursionTreeNodeAndInsertCopy(reportModelID,newUUID_ReportModelInfoID,treeNode.getChildren(),reportModelInfoMap,reportModelInfoList_copy);
			}

		}
	}

	@Override
	@Transactional(propagation= Propagation.REQUIRED,rollbackFor= Exception.class)
	public int deleteReportModel(String reportModelIDs) {
		int result = 0;
		String[] reportModelArray = reportModelIDs.split(",");
		for(String reportModelID:reportModelArray){
			result = result+reportModelDao.deleteReportModel(reportModelID);
		}
		return result;
	}


	/**
	 * 递归，通过parentid获取子节点
	 * @param parentID 父节点id
	 * @param reportModelID 模板id
	 */
	private List<HSTree> getReportModelInfoTreeNodes(String parentID, String reportModelID){
		//定义tree结构数据
		List<HSTree> tree = new ArrayList<>();
		//根据父节点获取list
		List<ReportModelInfo> reportModelInfoList = reportModelDao.getReportModelInfoListByParentIDAndReportModelID(parentID,reportModelID);
		//遍历
		for(ReportModelInfo reportModelInfo:reportModelInfoList){
			//每一条记录都是一个tree节点
			HSTree treeNode = new HSTree();
			//根据类型判断是否有图片和表格，这两种情况在tree节点上的文本显示有专门的字段
			switch(reportModelInfo.getContentType()){
				case "image":
					treeNode.setLabel(reportModelInfo.getImageName());
					break;
				case "table":
					treeNode.setLabel(reportModelInfo.getTableName());
					break;
				case "text":
					//TODO
					JSONArray array = JSONArray.fromObject(reportModelInfo.getContent());
					if(array.size()>0){
						for(int i=0;i<array.size();i++){
							String text = array.getJSONObject(i).getString("text");
							if(text.length()>0){
								treeNode.setLabel("text");
								break;
							}
						}
					}else{
						//文本中无数据，返回的节点内容为空
						treeNode.setLabel("");
					}
					//treeNode.setLabel(JSONArray.fromObject()reportModelInfo.getContent());
					break;
				default:
					treeNode.setLabel(reportModelInfo.getContent());
					break;
			}
			//ID为该模板详情的ReportModelID（唯一）
			treeNode.setId(reportModelInfo.getReportModelInfoID());
			//填充子节点
			treeNode.setChildren(getReportModelInfoTreeNodes(reportModelInfo.getReportModelInfoID(),reportModelID));
			tree.add(treeNode);
		}
		return tree;
	}

	/**
	 * 根据数据写入word
	 * @param data 模板详情信息
	 * @param doc doc对象
	 * @param sto 格式类
	 * @param imagesArray 图片
	 * @param tablesArray 表格
	 * @param metricArray 指标
	 * @param reportModelType 模板类型
	 * @throws Exception
	 */
	private void writeToWord(Map<Long,Tree<String>> data,Document doc,StyleTO sto,JSONArray imagesArray,JSONArray tablesArray,JSONArray metricArray,String reportModelType) throws Exception {
		//直接遍历数据进行处理--直接遍历值
		for(Tree<String> row:data.values()){
			switch(row.get("contentType").toString()){
				case "title":
					//添加信息到word
					OutTO oto1 = new OutTO();
					oto1.setTitle(row.get("content").toString());
					//设置标题格式 heading 1 会取用word中的标题1 的段落属性  字体
					//RtfParagraphStyle heading = new RtfParagraphStyle("heading "+row.get("titleLevel"),"宋体",Integer.parseInt(row.get("fontSize").toString()), Integer.parseInt(row.get("bold").toString()), Color.BLACK);
					RtfParagraphStyle heading = new RtfParagraphStyle("heading "+row.get("titleLevel"),"宋体",0, 0, Color.BLACK);
					insertTitle.insertTitle(doc, oto1,heading);
					break;
				case "text":
					//获取文字内容
					String content_text = row.get("content").toString();
					// 要写入文档的文字内容，需要进行组装
					StringBuilder paragraph = new StringBuilder();
					//content 转数组
					JSONArray array = JSONArray.fromObject(content_text);
					for(int i=0;i<array.size();i++){
						JSONObject object = array.getJSONObject(i);
						String metricID = object.get("metricID").toString();
						paragraph.append(object.get("text").toString());
						if(!Strings.isNullOrEmpty(metricID)){
							paragraph.append(getMetricValueByID(metricID,metricArray));
						}
					}
					RtfFont font = new RtfFont("宋体");
					font.setSize(Integer.parseInt(row.get("fontSize").toString()));
					font.setStyle(Integer.parseInt(row.get("bold").toString()));
					insertContent.insertContent(doc,paragraph.toString(), 0, Integer.parseInt(row.get("spacingBefore").toString()), 24, 22,Integer.parseInt(row.get("alignment").toString()),font);
					//判断文字中是否有特殊标识
//					Matcher matcher = StringUtils.StringPatternWithAtLabel(content_text);
//					while(matcher.find()){
//						//获取到特殊标签内容，
//						//获取标签对应的获取对应数据的方式的信息ReportModelData
//						content_text = content_text.replace("@!"+matcher.group(0)+"@",getDataByReportModelDataKey(matcher.group(0),reportModelType));
//					}
//					RtfFont font = new RtfFont("宋体");
//					font.setSize(Integer.parseInt(row.get("fontSize").toString()));
//					font.setStyle(Integer.parseInt(row.get("bold").toString()));
//					//一段文字
//					insertContent.insertContent(doc,content_text, 0, Integer.parseInt(row.get("spacingBefore").toString()), 24, 22,Integer.parseInt(row.get("alignment").toString()),font);
					break;
				case "image":
					//获取内容
					String content_image = row.get("content").toString();
					Itext2wordUtil.insertBase64ImageByVisualId(doc,imagesArray,content_image);
					//判断文字中是否有特殊标识
//					Matcher matcher_image = StringUtils.StringPatternWithAtLabel(content_image);
//					while(matcher_image.find()){
//						Itext2wordUtil.insertBase64ImageByVisualId(doc,imagesArray,matcher_image.group(0));
//					}
					break;
				case "table":
					//获取文字内容
					String content_table = row.get("content").toString();
					Itext2wordUtil.insertTableForReportModel(doc,sto,tablesArray,content_table);
					//判断文字中是否有特殊标识
//					Matcher matcher_table = StringUtils.StringPatternWithAtLabel(content_table);
//					while(matcher_table.find()){
//						Itext2wordUtil.insertTableForReportModel(doc,sto,tablesArray,matcher_table.group(0));
//					}
					break;
				default:
					//其他情况不做处理
					break;
			}
		}
	}
	/**
	 * 写word的递归操作
	 * 	 * 通过parentID递归获取tree的外层-内层的节点，并将获取的数据写入word
	 * 	 *
	 * @param parentID 父节点ID
	 * @param doc
	 * @param sto
	 * @param imagesArray 图片
	 * @param tablesArray 表格
	 * @param metricArray 指标
	 * @param reportModelType 模板类型
	 * @throws Exception
	 */
	private void writeDocRecursion(String reportModelID,String parentID,Document doc,StyleTO sto,JSONArray imagesArray,JSONArray tablesArray,JSONArray metricArray,String reportModelType) throws Exception {
		//根据父节点获取
		List<ReportModelInfo> reportModelInfoList = reportModelDao.getReportModelInfoListByParentIDAndReportModelID(parentID,reportModelID);
		//遍历
		for(ReportModelInfo reportModelInfo:reportModelInfoList){
			//首先判断内容类型
			switch(reportModelInfo.getContentType()){
				//TODO OutTO作用以及重新整合
				case "heading_1":
					//添加信息到word
					OutTO oto1 = new OutTO();
					oto1.setTitle(reportModelInfo.getContent());
					insertTitle.insertTitle(doc, oto1, sto.getHeading_1());
					break;
				case "heading_2":
					//添加信息到word
					OutTO oto2 = new OutTO();
					oto2.setTitle(reportModelInfo.getContent());
					insertTitle.insertTitle(doc, oto2, sto.getHeading_2());
					break;
				case "heading_3":
					//添加信息到word
					OutTO oto3 = new OutTO();
					oto3.setTitle(reportModelInfo.getContent());
					insertTitle.insertTitle(doc, oto3, sto.getHeading_3());
					break;
				case "text":
					//获取文字内容
					String content_text = reportModelInfo.getContent();
					// 要写入文档的文字内容，需要进行组装
					StringBuilder paragraph = new StringBuilder();
					//content 转数组
					JSONArray array = JSONArray.fromObject(content_text);
					for(int i=0;i<array.size();i++){
						JSONObject object = array.getJSONObject(i);
						String metricID = object.get("metricID").toString();
						if(!Strings.isNullOrEmpty(metricID)){
							paragraph.append(getMetricValueByID(metricID,metricArray));
						}
					}
					RtfFont font = new RtfFont("宋体");
					font.setSize(reportModelInfo.getFontSize());
					font.setStyle(reportModelInfo.getBold());
					insertContent.insertContent(doc,paragraph.toString(), 0, reportModelInfo.getSpacingBefore(), 24, 22,reportModelInfo.getAlignment(),font);
					/*
					//判断文字中是否有特殊标识
					Matcher matcher = StringUtils.StringPatternWithAtLabel(content_text);
					while(matcher.find()){
						//获取到特殊标签内容，
						//获取标签对应的获取对应数据的方式的信息ReportModelData
						content_text = content_text.replace("@!"+matcher.group(0)+"@",getDataByReportModelDataKey(matcher.group(0),reportModelType));
					}
					RtfFont font = new RtfFont("宋体");
					font.setSize(reportModelInfo.getFontSize());
					font.setStyle(reportModelInfo.getBold());
					//一段文字
					insertContent.insertContent(doc,content_text, 0, reportModelInfo.getSpacingBefore(), 24, 22,reportModelInfo.getAlignment(),font);
					*/
					break;
				case "table":
					//获取文字内容
					String content_table = reportModelInfo.getContent();
					//判断文字中是否有特殊标识
					Matcher matcher_table = StringUtils.StringPatternWithAtLabel(content_table);
					while(matcher_table.find()){
						Itext2wordUtil.insertTableForReportModel(doc,sto,tablesArray,matcher_table.group(0));
					}
					break;
				case "image":
					//获取文字内容
					String content_image = reportModelInfo.getContent();
					//判断文字中是否有特殊标识
					Matcher matcher_image = StringUtils.StringPatternWithAtLabel(content_image);
					while(matcher_image.find()){
						Itext2wordUtil.insertBase64ImageByVisualId(doc,imagesArray,matcher_image.group(0));
					}
					break;

				default:
					//其他情况不做处理
					break;

			}
			//继续递归
			writeDocRecursion(reportModelID,reportModelInfo.getReportModelInfoID(),doc,sto,imagesArray,tablesArray,metricArray,reportModelType);
		}
	}

	/**
	 * 在前端传过来的指标数组中查找visual_id与metricID相同的，并返回value
	 * @param metricID
	 * @param metricArray
	 * @return 找不到则返回空字符串
	 */
	private String getMetricValueByID(String metricID,JSONArray metricArray){
		StringBuilder result = new StringBuilder();
		for(int i=0;i<metricArray.size();i++){
			if(metricID.equals(metricArray.getJSONObject(i).get("visual_id"))){
				String value = metricArray.getJSONObject(i).get("value").toString();
				//指标value的格式固定，eg：[{"name":"winlogbeat-日志数","value":"246"},{"name":"syslog-日志数","value":"12"}]
				//进行组装 组装后的文本为： winlogbeat-日志数：246 syslog-日志数：12
				JSONArray valueArray = JSONArray.fromObject(value);
				for(int j=0;j<valueArray.size();j++){
					result.append(" "+valueArray.getJSONObject(j).getString("name"));
					result.append("：");
					result.append(valueArray.getJSONObject(j).getString("value"));
				}
				return result.toString();
			}
		}
		return "";
	}
	/**
	 * 通过key获取到获取数据的信息，并调用相关方法获取数据
	 * @param reportModelDataKey 获取数据的关键词 key
	 * @return
	 * @throws Exception
	 */
	private String getDataByReportModelDataKey(String reportModelDataKey,String reportModelType ) throws Exception {
		String resultValue = "";//要返回的结果
		//计算时间范围
		String starttime = "";
		String endtime = "";
		//定义时间对象，并初始化年月日
		LocalDateTime now = LocalDateTime.now();
		switch(reportModelType){
			case "day":
				starttime = DateTime.now().toString(startPattern);
				endtime = DateTime.now().toString(endPattern);
				break;
			case "week":
				TemporalField fieldISO = WeekFields.of(Locale.FRANCE).dayOfWeek();//法国时间，周一为第一天，中国显示的是周日
				starttime = now.with(fieldISO,1).format(dtf_start);//本周第一天
				endtime = now.with(fieldISO,7).format(dtf_end);//本周第七天
				break;
			case "month":
				starttime = now.with(TemporalAdjusters.firstDayOfMonth()).format(dtf_start);
				endtime = now.with(TemporalAdjusters.lastDayOfMonth()).format(dtf_end);
				break;
			default:
				break;
		}
		//时间范围的值单独进行处理，并且直接进行返回
		if(reportModelDataKey.equals("DayArea")&&starttime.length()>10&&endtime.length()>10){
			return starttime.substring(0,10)+"至" +endtime.substring(0,10);
		}else{
			//不处理，顺序执行下面代码即可。
		}
		ReportModelData reportModelData = reportModelDao.getReportModelDataByKey(reportModelDataKey);
		if(reportModelData!=null){

			switch(reportModelData.getReportModelDataType()){
				case "metric":
					String content = reportModelData.getContent();
					//内容转成bean
					SearchConditions metricSearchConditions = JavaBeanUtil.mapToBean((Map) JSONObject.fromObject(content), SearchConditions.class);
					JSONArray array = JSONArray.fromObject(JSONObject.fromObject(content).get("metricList"));
					for(Object obj:array){
						Metric metric = new Metric();
						metric.setAggType(JSONObject.fromObject(obj).get("aggType").toString());
						metric.setField(JSONObject.fromObject(obj).get("field").toString());
						metricSearchConditions.getMetricList().add(metric);
					}
					metricSearchConditions.setStartTime(starttime);
					metricSearchConditions.setEndTime(endtime);
					List<Map<String,Object>> result = iBIService.getMultiAggregationData_metric(metricSearchConditions);
					//确定只有一条数据
					if(result.size()==1){
						resultValue = result.get(0).get(reportModelData.getDataGetFields()).toString();
					}else{
						//存在多条数据时，无法处理，用NaN表示
						resultValue = "NaN";
					}
					break;
				case "date":
					//这里存储的是日期的格式
					String date_content = reportModelData.getContent();
					resultValue = DateTime.now().toString(date_content);
					//
					break;
				default:
					//其他未知类型不处理
					resultValue = "";
					break;
			}
		}

		return resultValue;
	}
}
