package com.yuyan.emall.admin.service.tree.defdoc;

import com.yuyan.emall.admin.service.tree.DefaultNode;
import com.yuyan.emall.admin.service.tree.Node;

/**
 * Created by admin on 2017/12/8.
 */
public class DefDoc extends DefaultNode implements Node {
    public DefDoc() {
    }

    protected String docCode;
    protected String docName;
    protected String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocCode() {
        return docCode;
    }

    public void setDocCode(String docCode) {
        this.docCode = docCode;
    }
}
