package com.yuyan.emall.admin.dao.selector;

import com.yuyan.emall.admin.config.Constants;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by admin on 2017/6/8.
 */
public class SqlContext {
    private static Logger logger = LoggerFactory
            .getLogger(SqlContext.class);
    static XStream xstream = new XStream();
    static{

    }
    private static final Map<String,String> sqlContextHolder = new ConcurrentHashMap<String,String>();

    public static String getSqlByModule(String module){
        return sqlContextHolder.get(module);
    }

    public static void loadSql(){
        Thread t = new Thread(){
            public void run(){
                while (1>0){
                    File f = new File(Constants.CONFIG_FILE);
                    String xmlStr=null;
                    try {
                        xmlStr = FileUtils.readFileToString(f, "utf-8");
                    } catch (IOException e) {
                        logger.error("read file error",e);
                    }
                    Map<String,String> deserialMap = (Map<String, String>) xstream.fromXML(xmlStr);
                    synchronized (sqlContextHolder){
                        sqlContextHolder.clear();
                        sqlContextHolder.putAll(deserialMap);
                    }
                    try {
                        Thread.sleep(600*1000);
                    } catch (InterruptedException e) {
                        logger.error("interaputed",e);
                    }
                }
            }
        };
        t.setName("sqlLoaderThread");
        t.setDaemon(true);
        t.start();

    }
}
