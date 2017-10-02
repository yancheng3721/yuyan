package com.yuyan.emall.admin.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 
 * 〈一句话功能简述〉<br> 
 * JSON序列化工具
 *
 * @author administrator
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class JSONUtil {
    
    
    
    
	public static String toJSONString(Object source,String propertiesfilters){
        String result = null;
        if(source!=null){
            Collection<PropertyFilter> filters = createFilters(propertiesfilters);
            SerializeWriter sw = new SerializeWriter();
            JSONSerializer serializer= new JSONSerializer(sw);
            if(filters!=null){
                serializer.getPropertyFilters().addAll(filters);
            }
            serializer.write(source);
//            System.out.println(sw.toString());
            result = sw.toString().replaceAll("\\'", "\\\\'");
        }
        return result;
    }
    
	public static String toJSONString(Object source,String propertiesfilters,String dateFormat){
        String result = null;
        if(source!=null){
            Collection<PropertyFilter> filters = createFilters(propertiesfilters);
            SerializeWriter sw = new SerializeWriter();
            sw.config(SerializerFeature.WriteDateUseDateFormat, true);
            JSONSerializer serializer= new JSONSerializer(sw);
//            DateFormat df = new SimpleDateFormat(dateFormat);
//            serializer.setDateFormat(df);
            if(filters!=null){
                serializer.getPropertyFilters().addAll(filters);
            }
            serializer.write(source);
//            serializer.writeWithFormat(source, dateFormat);
            result = sw.toString().replaceAll("\\'", "\\\\'");
        }
        return result;
    }
    
    
    private static Collection<PropertyFilter> createFilters(String propertiesfilters) {
        Collection<PropertyFilter> filters = new HashSet<PropertyFilter>();
        if(StringUtils.isNotEmpty(propertiesfilters)){
            String [] nameAry = propertiesfilters.split(",");
            if(nameAry!=null){
                for(String name:nameAry){
                    filters.add(createNameFilter(name));
                } 
            }
        }
        return filters;
    }

    private static PropertyFilter createNameFilter(final String nameIn){
        PropertyFilter pf = new PropertyFilter(){
            public boolean apply(Object source, String name, Object value) {
//                System.out.println(nameIn+":"+name);
                return !nameIn.equals(name);
            }
        };
        return pf;
    }
    
    public static void main(String[] args) {
        Map<String,String> obj = new HashMap<String,String>();
        obj.put("field1", "ok");
        obj.put("field2", "okok");
        obj.put("field3", "okokokok");
        System.out.println(JSONUtil.toJSONString(obj, "field1,field3"));
    }

}
