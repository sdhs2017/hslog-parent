package com.jz.bigdata.components.ansj.test.test1;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;

public class AnsjTest {
	
	public static void main(String[] args){
		//http://www.jianshu.com/p/1b14b2e25b9e
		//http://blog.csdn.net/zhaizu/article/details/24476779
		String log4jStr = "2017-08-10 11:54:34 [DEBUG]  [==>  Preparing: select id, name, level, superiorId, subordinate,orderId,comment from department where 1=1 and level = ? ]  [com.jz.bigdata.common.department.dao.IDepartmentDao.selectAll] debug()[139] [210348]";
//		String log4jStr = "2017-08-10 11:54:34,352 [DEBUG]  [==>  Preparing: select id, name, level, superiorId, subordinate,orderId,comment from department where 1=1 and level = ? ]  [com.jz.bigdata.common.department.dao.IDepartmentDao.selectAll] debug()[139] [210348]";
		
		test(log4jStr);
		
	}
	
	public static void test(String str) {
		
		
//		StopRecognition re = new StopRecognition();
//		String regexes = "^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D*$";
		String regexes = "[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}[ ][0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}";
//		re.insertStopRegexes(regexes);
		
        //只关注这些词性的词
        Set<String> expectedNature = new HashSet<String>() {{
            add("n");add("v");add("vd");add("vn");add("vf");
            add("vx");add("vi");add("vl");add("vg");
            add("nt");add("nz");add("nw");add("nl");
            add("ng");add("userDefine");add("wh");
            add("en");add("m");
        }};
//        Set<String> expectedNature = new HashSet<String>() {{
//        	add("m");
//        }};
//        String str = "欢迎使用ansj_seg,(ansj中文分词)在这里如果你遇到什么问题都可以联系我.我一定尽我所能.帮助大家.ansj_seg更快,更准,更自由!" ;
        Result result = ToAnalysis.parse(str).recognition(new StopRecognition()); //分词结果的一个封装，主要是一个List<Term>的terms
//        Result result = ToAnalysis.parse(str).recognition(re); //分词结果的一个封装，主要是一个List<Term>的terms
//        Result result = BaseAnalysis.parse(str); //分词结果的一个封装，主要是一个List<Term>的terms
//        Result result = DicAnalysis.parse(str); //分词结果的一个封装，主要是一个List<Term>的terms
//        Result result = IndexAnalysis.parse(str); //分词结果的一个封装，主要是一个List<Term>的terms
//        Result result = NlpAnalysis.parse(str); //分词结果的一个封装，主要是一个List<Term>的terms
        System.out.println(result.getTerms());

        List<Term> terms = result.getTerms(); //拿到terms
        System.out.println(terms.size());

        for(int i=0; i<terms.size(); i++) {
            String word = terms.get(i).getName(); //拿到词
            String natureStr = terms.get(i).getNatureStr(); //拿到词性
//            if(expectedNature.contains(natureStr)) {
//                System.out.println(word + ":" + natureStr);
//            }
            System.out.println(word + ":" + natureStr);
        }
    }

}
