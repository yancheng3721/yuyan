package com.yuyan.emall.admin.service.tree;

import java.util.Collection;

public class DefaultNode implements Node {
    
    

    public static final Integer INITIAL_ID_SEQUENCE = 0;
    
    protected int coreRoot;
    protected Integer id;
    protected Integer parentId;
    protected int level ;
    protected Node root;
    protected Node parent;
    protected boolean leaf;
    protected Collection<Node> children;

    /**
     * 频道下目录数据是否发生变化，如果发生变化需要同步，1：变化，2：未变化
     */
    protected int isChanged = 0;
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getParentId() {
        return parentId;
    }
    
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
    
    public int getCoreRoot() {
        return coreRoot;
    }
    
    public void setCoreRoot(int coreRoot) {
        this.coreRoot = coreRoot;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    

    
    public Node getParent() {
        return parent;
    }
    
    public void setParent(Node parent) {
        this.parent = parent;
    }
    
    public boolean isLeaf() {
        return leaf;
    }
    
    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public void setChildren(Collection<Node> children) {
        this.children = children;
    }

    public Collection<Node> getChildren() {
        return this.children;
    }

	public int getIsChanged() {
		return isChanged;
	}

	public void setIsChanged(int isChanged) {
		this.isChanged = isChanged;
	}
	
	
}
