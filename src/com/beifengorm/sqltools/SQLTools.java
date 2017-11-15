package com.beifengorm.sqltools;

public class SQLTools {

	public static SQLInfo getSQLInfo(String bql){
		SQLInfo info=new SQLInfo();
		bql=bql.replaceAll("\\s+ ", " ");
		//System.out.println(bql);
		String [] bqls=bql.split(" ");
		String condition="";
		for(int i=0;i<bqls.length;i++){
			if(bqls[i].equalsIgnoreCase("from")){
				info.setTablename(bqls[i+1]);
			}else if(bqls[i].equalsIgnoreCase("where")){
				condition=bql.substring(bql.indexOf("where"));
			}
		}
		info.setCondition(condition);
		return info;
	}
	
	public static void main(String[] args) {
		getSQLInfo("from Style   where styleId=1 or styleId=2");
	}
}
