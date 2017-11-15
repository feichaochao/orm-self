package com.beifengorm.tools;

import java.util.HashMap;
import java.util.Map;

import com.beifengorm.tableinfo.TableInfo;

public class Constant {

	//存放数据库连接信息
	public static Map<String, String> DBMAP=new HashMap<String, String>();
	
	//存放配置文件的信息
	public static Map<String,TableInfo> TABLEMAP=new HashMap<String, TableInfo>();
}
