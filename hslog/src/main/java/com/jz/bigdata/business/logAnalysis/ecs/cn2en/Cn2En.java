package com.jz.bigdata.business.logAnalysis.ecs.cn2en;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: hsgit
 * @description: 事件的中英文对照
 * @author: jiyourui
 * @create: 2020-04-09 11:43
 **/
public class Cn2En {

    public final static Map<String, String> event_cn = new HashMap<>();
    public final static Map<String, String> event_en = new HashMap<>();

    static {
        /**
         * 中文对照英文
         */
        event_cn.put("帐户登录失败","logon-failed"); // event.code=4625 对照windows事件表

        event_cn.put("用户帐户管理","User Account Management"); // event.code=4797\5379\5381\5382

        event_cn.put("枚举了用户的本地组成员身份","group-membership-enumerated"); //** event.code=4798
        event_cn.put("审核策略更改","Auditing settings on object were changed"); // event.code=4907 对照windows事件表
        event_cn.put("成功登录帐户","logged-in"); // event.code=4624 对照windows事件表
        event_cn.put("分配给新的登录特权","logged-in-special"); // event.code=4672 对照windows事件表
        event_cn.put("枚举了启用安全性的本地组成员身份","user-member-enumerated"); //** event.code=4799
        event_cn.put("组成员身份","Group membership information"); // event.code=4627 对照windows事件表
        event_cn.put("已创建一个新的进程","created-process"); // event.code=4688 对照windows事件表
        event_cn.put("登录","Logon");
        event_cn.put("帐户被注销","logged-out"); // event.code=4634 对照windows事件表
        event_cn.put("安全状态更改","Security State Change"); // event.code=4608\4616

        event_cn.put("用户帐户已更改","modified-user-account"); // event.code=4738 对照windows事件表
        event_cn.put("服务关闭","The event logging service has shut down"); // event.code=4738 对照windows事件表

        event_cn.put("注销","Logoff");
        event_cn.put("其他策略更改事件","Other Policy Change Events");
        event_cn.put("审核策略更改","Audit Policy Change");
        event_cn.put("其他系统事件","Other System Events"); // event.code=5024\5033\5058\5059
        event_cn.put("加密操作","System Integrity"); // event.code=5061 对照windows事件表
        event_cn.put("域策略已更改","Authentication Policy Change"); // event.code=4739 对照windows事件表
        event_cn.put("一个主令牌被分配来处理","Process Creation"); // event.code=4696 对照windows事件表
        event_cn.put("其他登录/注销事件","The workstation was locked/unlocked"); // event.code=4800\4801 对照windows事件表

        /**
         * 英文对照中文
         */
        event_en.put("logon-failed","登录失败");
        event_en.put("User Account Management","用户帐户管理"); // event.code=4797\5379\5381\5382\

        event_en.put("group-membership-enumerated","枚举了用户的本地组成员身份");
        event_en.put("Auditing settings on object were changed","审核策略更改");
        event_en.put("logged-in","成功登录帐户");
        event_en.put("logged-in-special","分配给新的登录特权");
        event_en.put("user-member-enumerated","枚举了启用安全性的本地组成员身份");
        event_en.put("Group membership information","组成员身份");
        event_en.put("created-process","已创建一个新的进程");
        event_en.put("Logon","登录");
        event_en.put("logged-out","帐户被注销");
        event_en.put("Security State Change","安全状态更改"); // event.code=4616\4608

        event_en.put("modified-user-account","用户帐户已更改");
        event_en.put("The event logging service has shut down","服务关闭");

        event_en.put("Logoff","注销");
        event_en.put("Other Policy Change Events","其他策略更改事件");
        event_en.put("Audit Policy Change","审核策略更改");
        event_en.put("Authentication Policy Change","域策略已更改");
        event_en.put("Other System Events","其他系统事件"); // event.code=5024\5033\5058\5059
        event_en.put("System Integrity","加密操作");

        event_en.put("Process Creation","一个主令牌被分配来处理");
        event_en.put("The workstation was locked/unlocked","其他登录/注销事件"); // event.code=4800\4801
    }

    public static void main(String [] args){
        String S = "Other System Events";
        String ss = "this";
        Cn2En.event_en.get(S);
        Cn2En.event_en.get(ss);
    }
}
