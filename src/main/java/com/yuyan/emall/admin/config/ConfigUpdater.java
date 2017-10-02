package com.yuyan.emall.admin.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yuyan.emall.admin.util.PropertiesUtil;

public class ConfigUpdater {
    
    static final Logger LOGGER = LoggerFactory.getLogger(ConfigUpdater.class);

    
    /**
     * 
     * 功能描述: <br>
     * 使用Properties配置文件，覆盖静态属性值<br>
     * 支持自动覆盖的条件有：<br>
     * 1 配置名与属性名相同 2 属性修饰符为public static 且 不是final<br>
     * 3 属性类型：boolean,int,String,long,float<br>
     * 如果不满足以上条件，不进行配置修改
     * 
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static <T> void overrideDefaultByConfigFile(Properties p,T c) {
        Field[] fs = c.getClass().getDeclaredFields();
        if (fs != null && fs.length > 0) {
            for (int i = 0; i < fs.length; i++) {
                Field f = fs[i];
                if (Modifier.isStatic(f.getModifiers())
                        && Modifier.isPublic(f.getModifiers())) {
                    if (!Modifier.isFinal(f.getModifiers())) {
                        String k = f.getName();
                        String v = p.getProperty(k);
                        if (v != null) {
                            v = v.trim();
                            LOGGER.info("override proprety :" + k + "=" + v);
                            
                            Object value = null;
                            try {
                                if (f.getType().equals(boolean.class) || f.getType().equals(Boolean.class)) {
                                        value= Boolean.parseBoolean(v);
                                } else if (f.getType().equals(int.class)||f.getType().equals(Integer.class)) {
                                        value= Integer.parseInt(v);
                                } else if (f.getType().equals(String.class)) {
                                        value=v;
                                } else if (f.getType().equals(long.class)||f.getType().equals(Long.class)) {
                                        value= Long.parseLong(v);
                                } else if (f.getType().equals(float.class)||f.getType().equals(Float.class)) {
                                    value= Float.parseFloat(v);
                                }else if (f.getType().equals(double.class)||f.getType().equals(Double.class)) {
                                    value= Double.parseDouble(v);
                                } else {
                                    LOGGER.warn("未支持的属性类型：属性名 " + k + " | 属性值-" + v
                                            + " | 属性类型-" + f.getType().getName());
                                }
                                if(v!=null){
                                    f.set(c, value);
                                }
                            } catch (Exception e) {
                                LOGGER.error("覆盖配置信息发生异常：属性名 " + k
                                        + " | 属性值-" + v + " | 属性类型-"
                                        + f.getType().getName());
                            }
                            
                        }
                    }
                }
            }
        }
    }
    
    
    public static Map<String, Object> currentConfigs(Class<?> claz) {
		Map<String, Object> result = new TreeMap<String, Object>();
		Field[] fs = claz.getDeclaredFields();
		if (fs != null && fs.length > 0) {
			for (int i = 0; i < fs.length; i++) {
				Field f = fs[i];
				if (Modifier.isStatic(f.getModifiers())
						&& Modifier.isPublic(f.getModifiers())) {
					if (!Modifier.isFinal(f.getModifiers())) {
						String k = f.getName();
						try {
                          result.put(k, f.get(claz) + "");
                          LOGGER.info("read config :" + k + "="
                                  + f.get(claz));
                          } catch (Exception e) {
                              LOGGER.error("读取属性失败",e);
                          }

					}
				}
			}
		}
		return result;
	}
    
    
    public static void updateConfigByTemplate(Properties p,Map<?, ?> template,
            boolean persist,String path) {
        if (template != null && !template.isEmpty()) {
            p.putAll(template);
            if (persist) {
                PropertiesUtil.store(p, path);
            }
        }
    }
}
