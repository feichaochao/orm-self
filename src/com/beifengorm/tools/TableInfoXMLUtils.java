package com.beifengorm.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.beifengorm.tableinfo.Id;
import com.beifengorm.tableinfo.ManytoOne;
import com.beifengorm.tableinfo.OnetoMany;
import com.beifengorm.tableinfo.Property;
import com.beifengorm.tableinfo.TableInfo;
//读取所有的实体映射文件的工具类
public class TableInfoXMLUtils {

	@SuppressWarnings("unchecked")
	public static Map<String, TableInfo> getTableInfoXML(List<String> filelist){
		Map<String,TableInfo> tablemap=new HashMap<String, TableInfo>();
		for(String filename:filelist){
			File file=new File(TableInfoXMLUtils.class.getResource(filename).getFile());
			SAXReader reader=new SAXReader();
			Document doc=null;
			TableInfo table=new TableInfo();
			Id id=null;
			List<Property> plist=null;
			List<OnetoMany> onelist=null;
			List<ManytoOne> manylist=null;
			try {
				doc=reader.read(file);
				//读取根标签，就是class标签
				Element root=doc.getRootElement();
				table.setClassname(root.attributeValue("name"));
				table.setTablename(root.attributeValue("table"));
				List<Element> elist=root.elements();
				for(Element e:elist){
					if(e.getName().equals("Id")){
						id=new Id();
						id.setIdname(e.attributeValue("name"));
						id.setIdcolumn(e.attributeValue("column"));
					}else if(e.getName().equals("property")){
						if(null==plist){
							plist=new ArrayList<Property>();
						}
						Property p=new Property();
						p.setPropertyname(e.attributeValue("name"));
						p.setPropertycolumn(e.attributeValue("column"));
						plist.add(p);
					}else if(e.getName().equals("onetomany")){
						if(null==onelist){
							onelist=new ArrayList<OnetoMany>();
						}
						OnetoMany one=new OnetoMany();
						one.setOnename(e.attributeValue("name"));
						one.setOneclass(e.attributeValue("class"));
						one.setOnecolumn(e.element("column").attributeValue("name"));
						onelist.add(one);
					}else if(e.getName().equals("manytoone")){
						if(null==manylist){
							manylist=new ArrayList<ManytoOne>();
						}
						ManytoOne many=new ManytoOne();
						many.setManyname(e.attributeValue("name"));
						many.setManyclass(e.attributeValue("class"));
						many.setManycolumn(e.element("column").attributeValue("name"));
						manylist.add(many);
					}
				}
				table.setId(id);
				table.setPlist(plist);
				table.setOnelist(onelist);
				table.setManylist(manylist);
				tablemap.put(table.getClassname(), table);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return tablemap;
	}
}
