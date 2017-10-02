package xml;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/6/8.
 */
public class XmlToObj {
    static XStream xstream = new XStream();

    public static void main(String[] args) {
        Map<String,String> sqlMap = new HashMap<String,String>();
        sqlMap.put("aaa","select a from x");
        sqlMap.put("bbb","select b from x");

        String xmlStr = xstream.toXML(sqlMap);
        try {
            FileUtils.writeStringToFile(new File("/opt/concentrate/admin/sqlContext.xml"),xmlStr,"utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String,String> deserialMap = (Map<String, String>) xstream.fromXML(xmlStr);

        System.out.println(xstream.toXML(sqlMap));
    }
}
