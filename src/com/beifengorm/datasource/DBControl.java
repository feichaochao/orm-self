package com.beifengorm.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.beifengorm.tools.Constant;
import com.beifengorm.tools.XMLFactory;

//���ݿ�����Ӳ�����
public class DBControl {

	private static Map<String, String> DBMAP=null;
	private Connection conn=null;
	static{
		load();
	}
	private static void load(){
		if(null==DBMAP){
			XMLFactory.getXMLInfo(XMLFactory.class.getResource("/beifengorm.datasource.xml").getFile());
			DBMAP=Constant.DBMAP;
		}
	}
	public DBControl(){
		if(null==DBMAP){
			load();
		}else{
			getSimpleConn();
		}
	}
	
	public Connection getSimpleConn(){
		try {
			Class.forName(DBMAP.get("driver"));
			conn=DriverManager.getConnection(DBMAP.get("url"), DBMAP.get("username"),DBMAP.get("password"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
	
	//������ӣ��޸ģ�ɾ�������ݿ⹤�߷���
	public void setData(String sql){
		Statement stmt;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public ResultSet getData(String sql){
		ResultSet rs=null;
		try {
			Statement stmt=conn.createStatement();
			rs=stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	
	//�ر����ӵķ���
	public void close(){
		try {
			if(conn!=null || !conn.isClosed()){
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
