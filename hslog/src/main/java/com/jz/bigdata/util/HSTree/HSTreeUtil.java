package com.jz.bigdata.util.HSTree;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.json.JSONUtil;
import com.jz.bigdata.common.ReportModel.entity.ReportModelInfo;
import com.mysql.jdbc.StringUtils;
import org.apache.avro.generic.GenericData;
import org.apache.poi.ss.formula.functions.T;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: yiyang
 * @Date: 2022/6/6 16:53
 * @Description: 用于进行多级tree操作的工具类
 * 1.将数据库中查询出来的数据转成tree结构数据
 *  build方法的sourceData数据的格式有要求，需要将原始数据进行转换，转换示例：
 *  -----------------
 *          List<ReportModelInfo>  reportModelInfoList = reportModelDao.getReportModelInfoByReportModelID(reportModelID);
 *          //对源数据进行转换对应，即tree节点会用到的  id ，pid，name 显示名称，weight（权重，即排序字段）
 * 			List<TreeNode<String>> treeNodeList = reportModelInfoList.stream().map(reportModelInfo -> {
 * 				//tree单个节点的数据构建
 * 				TreeNode<String> treeNode = new TreeNode<>();
 * 				treeNode.setId(reportModelInfo.getReportModelInfoID());
 * 				treeNode.setParentId(reportModelInfo.getParentID());
 * 				treeNode.setName(reportModelInfo.getContent());
 * 				treeNode.setWeight(reportModelInfo.getOrderCode());
 * 				//扩展数据
 * 				//示例代码
 * 				//Map<String,Object> extra = new HashMap<>();
 * 				//extra.put("test_key","test_value");
 * 				//.....
 * 				//treeNode.setExtra(extra);
 * 				return treeNode;
 *          }).collect(Collectors.toList());
 * ------------------
 */
public class HSTreeUtil {
    //定义tree的宽度的乘数 即宽度最高999
    private static final long SPAN_MULTIPLIER=  1000;
    //tree的深度 5级菜单共需要15位数，小数点后由于精度限制，无法正常进行运算，因此采用整型，long
    private static final long DEEP_SIZE = 5;
    /**
     *  构建tree结构数据
     * @param sourceData 源数据
     * @param Alias_ID id别名
     * @param Alias_PID pid别名
     * @param Alias_Name 显示名称 别名
     * @param Alias_Order 排序字段 别名
     * @param rootID 根节点id
     * @param sourceExtraFields 扩展字段（数组）
     * @return
     */
    public static List<Tree<String>> build(List<TreeNode<String>> sourceData,String Alias_ID,String Alias_PID,String Alias_Name,String Alias_Order,String rootID,String... sourceExtraFields){
        //初始化配置项信息---设置tree 结构的的key
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        /**
         * 判断是否有别名传入，如果有，写入配置项中
         */
        //节点id
        if(StringUtils.isNullOrEmpty(Alias_ID)){

            treeNodeConfig.setIdKey(Alias_ID);
        }else{
            //无别名，不设置
        }
        //父节点id
        if(StringUtils.isNullOrEmpty(Alias_ID)){
            treeNodeConfig.setIdKey(Alias_PID);
        }else{
            //无别名，不设置
        }
        //显示名称
        if(StringUtils.isNullOrEmpty(Alias_ID)){
            treeNodeConfig.setNameKey(Alias_Name);
        }else{
            //无别名，不设置
        }
        //排序字段
        if(StringUtils.isNullOrEmpty(Alias_ID)){
            treeNodeConfig.setWeightKey(Alias_Order);
        }else{
            //无别名，不设置
        }
        //构建tree，主要定义转换器的设置
        List<Tree<String>> result = TreeUtil.build(sourceData,rootID,treeNodeConfig,(source, tree) ->{
            //将基础的 节点id、父节点id、名称、排序字段（权重） 进行赋值
            tree.setId(source.getId());
            tree.setParentId(source.getParentId());
            tree.setWeight(source.getWeight());
            tree.setName(source.getName());
            //extra  扩展字段信息
            for(String sourceExtraField:sourceExtraFields){
                tree.putExtra(sourceExtraField,source.getExtra().getOrDefault(sourceExtraField,null));
            }
        });
        return result;
    }

    /**
     * 根据源数据构建tree结构数据，tree结构数据的key使用TreeNode节点的默认字段
     * @param sourceData  源数据（转换后）
     * @param rootID 根节点ID
     * @return
     */
    public static List<Tree<String>> build(List<TreeNode<String>> sourceData,String rootID){
        //通过hutool TreeUtil 构建，配置项都使用默认值
        List<Tree<String>> result = TreeUtil.build(sourceData,rootID);
        return result;
    }

    /**
     *对返回数据进行全局排序编号，编号作为map的key
     * @param list 要排序的list
     * @param deep 当前list所在的深度
     * @param p_orderID 父节点的序号
     * @param result 要写入的结果集
     */
    public static void  orderAll(List<Tree<String>> list, int deep, long p_orderID, Map<Long,Tree<String>> result){
        if(list!=null){
            //初始第一条记录的广度编号为1
            long span = 1;
            //遍历tree的节点
            for(Tree<String> tree:list){
                // 节点编号为 eg：4.1.1.1.1   4001001001001  深度默认为5
                // 序号存储三位 广度 999 基本能满足所有编号的要求 math.pow   x的y次方
                long orderID = p_orderID+new Double(span * Math.pow(SPAN_MULTIPLIER,DEEP_SIZE-deep)).longValue() ;
                //写入数据
                result.put(orderID,tree);
                //是否有子节点，进行递归
                if(tree.getChildren()!=null&&tree.getChildren().size()>0){
                    List<Tree<String>> children = tree.getChildren();
                    //深度+1
                    orderAll(children,deep+1,orderID,result);
                }else{
                    //无子节点，继续执行
                }
                //循环一次，广度+1
                span++;
            }
        }
    }
    public static void main(String[] args) {
        Map<String,String> map = new HashMap<>();
        map.put("0.001","001");
        map.put("0.001002","001002");
        map.put("0.002","002");

        int span = 11;
        System.out.print(String.format("%02d",span));
        //源数据
//        List<ReportModelInfo> dataList = new ArrayList<>();
//        List<TreeNode<String>> treeList = dataList.stream().map(node -> {
//            TreeNode<String> treeNode = new TreeNode<>();
//            //set
//            return treeNode;
//        }).collect(Collectors.toList());
//
//        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
//
//        treeNodeConfig.setIdKey("ReportModelInfoID");
//        treeNodeConfig.setChildrenKey("ParentID");
//        treeNodeConfig.setWeightKey("OrderCode");
//
//        List<Tree<String>> result = TreeUtil.build(treeList,"",treeNodeConfig,(treeNode, tree) ->{
//            tree.setId("");
//            tree.setParentId("");
//            tree.setWeight("");
//            tree.setName("");
//            //extra信息
//        });
//        System.out.print(JSONUtil.parse(result));

    }

}
