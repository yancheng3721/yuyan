package com.yuyan.emall.admin.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉 通用导出excel
 * 
 * @author Administrator
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ExportUtil {

    private static final Logger logger = LoggerFactory.getLogger(ExportUtil.class);

    private JdbcTemplate jdbcTemplate;

    public ExportUtil() {
        super();
    }

    /***
     * 通用的导出excel(csv)文件
     * 
     * @param list
     * @param titles
     * @param response
     * @return 提示信息 ：
     * 
     */
    public String export2Excel(List<String[]> list, String[] titles, HttpServletResponse response, String name) {
        String message = "";
        Date date = new Date();
        String fileName = name + date.getTime();
        response.setCharacterEncoding("GBK");
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName + ".CSV");
        BufferedWriter wr = null;
        try {
            wr = new BufferedWriter(response.getWriter());
            wr.write(parseArray2String(titles));
            for (String[] row : list) {
                wr.write(parseArray2String(row, titles.length));
                wr.flush();
            }
            message = "导出成功！！";
        } catch (IOException e) {
            message = "导出失败！！请重试！";
            logger.error(e.getMessage(), e);
        }
        return message;

    }

    /***
     * 通用的导出excel(csv)文件
     * 
     * @param sql
     * @param titles
     * @param response
     * @return 提示信息 ： 其中 :select中的 列数要和 titles数组的长度一致
     * 
     */
    public String export2Excel(String sql, String[] titles, HttpServletResponse response, Object[] params,
            String name) {
        String message = "";
        Date date = new Date();
        String fileName = name + date.getTime();
        response.setCharacterEncoding("GBK");
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName + ".CSV");
        List<String[]> list = null;
        BufferedWriter wr = null;
        try {
            wr = new BufferedWriter(response.getWriter());
            wr.write(parseArray2String(titles));
            list = parseSql2List(sql, params);
            for (String[] row : list) {
                wr.write(parseArray2String(row));
                wr.flush();
            }
            message = "导出成功！！";
        } catch (IOException e) {
            message = "导出失败！！请重试！";
            logger.error(e.getMessage(), e);
        }
        return message;

    }

    /***
     * 根据SQL和参数数组 解析转换结果到 List<String[]>
     * 
     * @param sql
     * @param params
     * @return
     */
    private List<String[]> parseSql2List(String sql, Object[] params) {
        List<String[]> list = new ArrayList<String[]>();
        SqlRowSet rs = null;
        try {
            rs = jdbcTemplate.queryForRowSet(sql, params);
            int columnSize = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                String[] arrs = new String[columnSize];
                for (int i = 1; i <= columnSize; i++) {
                    arrs[i - 1] = rs.getObject(i) != null ? rs.getObject(i).toString() : "";
                }
                list.add(arrs);
            }
        } catch (DataAccessException e) {
            logger.error("导出Excel查询出错：sql="+sql + e.getMessage(), e);
        }
        return list;
    }

    /**
     * 解析String[]数组为字符串，每个字符用,连接
     * 
     * @param arr 解析数组[str1,str2,str3]-->字符串格式 "str1,str2,str3\n"
     * @return
     */
    private String parseArray2String(String[] arr) {
        String result="";
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            if (s != null) {
                result = s.replaceAll(",", "  ");
            }
            sb.append(result).append(",");
        }
        return sb.substring(0, sb.length()) + "\n";
    }

    /***
     * 解析String[]数组为字符串，每个字符用,连接
     * 
     * @param arr
     * @param ops 需要的长度 解析数组[str1,str2,str3,str4,str5,str6]--> （ops=3） 字符串格式 "str1,str2,str3\n"
     * @return
     */
    private String parseArray2String(String[] arr, int ops) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ops; i++) {
            String s = arr[i];
            if (s != null) {
                s = s.replaceAll(",", "  ");
            }
            if ("null".equals(s)) {
                s = "";
            }
            if (s == null) {
                s = "";
            }
            sb.append(s).append(",");
        }
        return sb.substring(0, sb.length()) + "\n";
    }

    // set method
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
