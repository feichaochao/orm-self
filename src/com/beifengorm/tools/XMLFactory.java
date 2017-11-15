package com.beifengorm.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.beifengorm.tableinfo.Id;
import com.beifengorm.tableinfo.ManytoOne;
import com.beifengorm.tableinfo.OnetoMany;
import com.beifengorm.tableinfo.Property;
import com.beifengorm.tableinfo.TableInfo;


//读取配置文件
public class XMLFactory {

	public static Map<String, String> getXMLInfo(String filename){
		Map<String, String> jdbcmap=new HashMap<String, String>();
		File file=new File(filename);
		SAXReader reader=new SAXReader();
		Document doc=null;
		try {
			doc=reader.read(file);
			//System.out.println(doc.asXML());
			Element roots=doc.getRootElement();
			Element root=roots.element("datasource");
			Element jdbc=root.element("jdbc");
			List<Element> propertys=jdbc.elements();
			for(Element property:propertys){
				Attribute nameattribute=property.attribute("name");
				Attribute valueattribute=property.attribute("value");
				if(valueattribute!=null){
					//有name，也有value的时候
					jdbcmap.put(nameattribute.getText(), valueattribute.getText());
				}else{
					//有name，没有value的时候
					jdbcmap.put(nameattribute.getText(), property.getText());
				}
			}
			Constant.DBMAP=jdbcmap;
			readBeanXML(roots);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jdbcmap;
	}
	
	private static void readBeanXML(Element roots){
		Element ormmapping=roots.element("orm-mapping");
		Element listelement=ormmapping.element("list");
		@SuppressWarnings("unchecked")
		List<Element> valuelist=listelement.elements();
		List<String> filelist=new ArrayList<String>();
		for(Element e:valuelist){
			filelist.add(e.getText());
		}
		readTableXMLInfo(filelist);
	}
	
	private static void readTableXMLInfo(List<String> filelist){
		Constant.TABLEMAP=TableInfoXMLUtils.getTableInfoXML(filelist);
	}

	public static void main(String[] args) {
		getXMLInfo(XMLFactory.class.getResource("/beifengorm.datasource.xml").getFile());
		Map<String, TableInfo> map=Constant.TABLEMAP;
		Set<String> set=map.keySet();
		Iterator<String> it=set.iterator();
		while(it.hasNext()){
			String key=it.next();
			TableInfo table=map.get(key);
			System.out.println("配置文件的信息");
			System.out.println("classname:"+table.getClassname()+" tablename:"+table.getTablename());
			Id id=table.getId();
			System.out.println("主键的信息: idname:"+id.getIdname()+" idcolumn:"+id.getIdcolumn());
			List<Property> plist=table.getPlist();
			if(plist!=null){
				System.out.println("属性标签的内容");
				for(Property p:plist){
					System.out.println("pname:"+p.getPropertyname()+" pcolumn:"+p.getPropertycolumn());
				}
			}
			List<OnetoMany> onelist=table.getOnelist();
			if(onelist!=null){
				System.out.println("一对多");
				for(OnetoMany one:onelist){
					System.out.println("onename:"+one.getOnename()+" oneclass:"+one.getOneclass()+" onecolumn:"+one.getOnecolumn());
				}
			}
			List<ManytoOne> manylist=table.getManylist();
			if(manylist!=null){
				System.out.println("多对一");
				for(ManytoOne many:manylist){
					System.out.println("manyname:"+many.getManyname()+" manyclass:"+many.getManyclass()+" manycolumn:"+many.getManycolumn());
				}
			}
			System.out.println();
		}
	}
}
