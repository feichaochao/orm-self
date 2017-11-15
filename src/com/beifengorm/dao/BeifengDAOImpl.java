package com.beifengorm.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.beifengorm.bean.Book;
import com.beifengorm.bean.Style;
import com.beifengorm.datasource.DBControl;
import com.beifengorm.sqltools.SQLInfo;
import com.beifengorm.sqltools.SQLTools;
import com.beifengorm.tableinfo.Id;
import com.beifengorm.tableinfo.ManytoOne;
import com.beifengorm.tableinfo.OnetoMany;
import com.beifengorm.tableinfo.Property;
import com.beifengorm.tableinfo.TableInfo;
import com.beifengorm.tools.Constant;

public class BeifengDAOImpl implements BeifengDAO {

	private DBControl db;
	
	public void save(Object o) {
		db=new DBControl();
		Class c=o.getClass();
		String classname=c.getName();
		TableInfo table=Constant.TABLEMAP.get(classname);
		Id id=table.getId();
		List<Property> plist=table.getPlist();
		List<ManytoOne> manylist=table.getManylist();
		String idname=id.getIdname();
		String idcolumn=id.getIdcolumn();
		Object idvalue=getValueByObject(idname, o);
		//System.out.println(idvalue);
		StringBuffer sb=new StringBuffer("insert into ").append(table.getTablename()).append(" (");
		sb.append(idcolumn).append(",");
		List<Object> valuelist=new ArrayList<Object>();
		if(plist!=null){
			for(Property p:plist){
				String pname=p.getPropertyname();
				String pcolumn=p.getPropertycolumn();
				sb.append(pcolumn).append(",");
				valuelist.add(getValueByObject(pname, o));
			}
		}
		if(manylist!=null){
			//���������Ҫ���
			for(ManytoOne many:manylist){
				String manyname=many.getManyname();
				String manycolumn=many.getManycolumn();
				Object manyobject=getValueByObject(manyname, o);
				Object manyvalue=getValueByObject(manycolumn, manyobject);
				sb.append(manycolumn).append(",");
				valuelist.add(manyvalue);
			}
		}
		//����ʱ��������Ҫƴ�ӵ��ֶΣ����Ѿ�׼������
		String sqlString=sb.substring(0, sb.toString().length()-1);
		
		sqlString+=") values("+idvalue+",";
		for(Object value:valuelist){
			sqlString+="'"+value+"',";
		}
		sqlString=sqlString.substring(0,sqlString.length()-1)+")";
		//System.out.println(sqlString);
		db.setData(sqlString);
		db.close();
	}

	private Object getValueByObject(String name,Object o){
		Field f=null;
		Object value=null;
		try {
			f=o.getClass().getDeclaredField(name);
			f.setAccessible(true);
			value=f.get(o);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}
	
	public void update(Object o) {
		db=new DBControl();
		Class c=o.getClass();
		String classname=c.getName();
		TableInfo table=Constant.TABLEMAP.get(classname);
		Id id=table.getId();
		String idname=id.getIdname();
		String idcolumn=id.getIdcolumn();
		//����ƴ��SQL��������
		Object idvalue=getValueByObject(idname, o);
		List<String> columnlist=new ArrayList<String>();
		List<Object> valuelist=new ArrayList<Object>();
		List<Property> plist=table.getPlist();
		for(Property p:plist){
			String pname=p.getPropertyname();
			Object pvalue=getValueByObject(pname, o);
			if(pvalue!=null){
				valuelist.add(pvalue);
				columnlist.add(p.getPropertycolumn());
			}
		}
		List<ManytoOne> manylist=table.getManylist();
		if(manylist!=null){
			for(ManytoOne many:manylist){
				String manyname=many.getManyname();
				Object manyvalue=getValueByObject(manyname, o);
				if(manyvalue!=null){
					String manycolumn=many.getManycolumn();
					Object mvalue=getValueByObject(manycolumn, manyvalue);
					valuelist.add(mvalue);
					columnlist.add(manycolumn);
				}
			}
		}		
		if(valuelist.size()==0){
			System.out.println("û�и����κ����ݣ�");
			db.close();
			return;
		}
		StringBuffer sb=new StringBuffer("update ");
		sb.append(table.getTablename()).append(" set ");
		for(int i=0;i<valuelist.size();i++){
			String column=columnlist.get(i);
			Object value=valuelist.get(i);
			sb.append(column).append("='").append(value).append("',");
		}
		//update book set �ֶ�1=ֵ1���ֶ�2=ֵ2
		String sql=sb.toString();
		sql=sql.substring(0,sql.length()-1);
		sql+=" where "+idcolumn+"="+idvalue;
		System.out.println(sql);
		db.setData(sql);
		db.close();
	}

	
	public void del(Object o) {
		//delete from ���� where ����(����=XXX)
		db=new DBControl();
		Class c=o.getClass();
		String classname=c.getName();
		TableInfo table=Constant.TABLEMAP.get(classname);
		List<OnetoMany> onelist=table.getOnelist();
		//onelist����һ�Զ���б�����б�Ϊ�գ���ô�ʹ����м�����ϵ��������ɾ��
		if(onelist!=null){
			System.out.println("�м�����ϵ��������ɾ��");
			db.close();
			return;
		}
		Id id=table.getId();
		String idcolumn=id.getIdcolumn();
		String idname=id.getIdname();
		Object idvalue=getValueByObject(idname, o);
		if(null==idvalue){
			System.out.println("û����������");
		}
		String sql="delete from "+table.getTablename()+" where "+idcolumn+"="+idvalue;
		//System.out.println(sql);
		db.setData(sql);
		db.close();
	}

	
	public Object query(String bql) {
		Object o=null;
		List list=queryList(bql);
		if(list!=null && list.size()>0){
			o=list.get(0);
		}
		return o;
	}

	
	public List queryList(String bql) {
		db=new DBControl();
		SQLInfo info=SQLTools.getSQLInfo(bql);
		String classname=info.getTablename();
		Map<String, TableInfo> map=Constant.TABLEMAP;
		Set<String> set=map.keySet();
		Iterator<String> it=set.iterator();
		TableInfo table=null;
		while(it.hasNext()){
			String key=it.next();
			String [] ts=key.split("\\.");
			if(classname.equals(ts[ts.length-1])){
				table=map.get(key);
				break;
			}
		}
		//System.out.println(table.getTablename());
		String sql="select * from "+table.getTablename()+" "+info.getCondition();
		System.out.println(sql);
		ResultSet rs=db.getData(sql);
		List<Object> list=new ArrayList<Object>();
		Id id=table.getId();
		List<Property> plist=table.getPlist();
		try {
			while(rs.next()){
				Class c=Class.forName(table.getClassname());
				Object o=c.newInstance();
				Field idf=c.getDeclaredField(id.getIdname());
				Class idc=idf.getType();
				Method idmethod=c.getDeclaredMethod("set"+id.getIdname().substring(0,1).toUpperCase()+id.getIdname().substring(1), idc);
				idmethod.invoke(o, rs.getObject(id.getIdcolumn()));
				for(Property p:plist){
					Field pf=c.getDeclaredField(p.getPropertyname());
					Class pc=pf.getType();
					Method pmethod=c.getDeclaredMethod("set"+p.getPropertyname().substring(0,1).toUpperCase()+p.getPropertyname().substring(1), new Class[]{pc});
					pmethod.invoke(o, rs.getObject(p.getPropertycolumn()));
				}
				list.add(o);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			db.close();
		}
		return list;
	}

	public static void main(String[] args) {
		BeifengDAO dao=new BeifengDAOImpl();
//		List<Style> list=dao.queryList("from   Style where styleId=2 or style='Computer'");
//		for(Style s:list){
//			System.out.println(s.getStyleId()+" "+s.getStyle());
//		}
		Style s=(Style)dao.query("from Style where styleId=1");
		System.out.println(s.getStyleId()+" "+s.getStyle());
	}
}
