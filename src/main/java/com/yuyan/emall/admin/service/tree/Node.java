package com.yuyan.emall.admin.service.tree;

import java.util.Collection;

public interface Node {
    public Integer getId();
    
    public void setId(Integer id) ;
    
    public Integer getParentId() ;
    
    public void setParentId(Integer parentId) ;

    public int getLevel() ;

    public void setLevel(int level);
    
    public Collection<Node> getChildren();
    
    public void setChildren(Collection<Node> children);
    
    public Node getParent() ;
    
    public void setParent(Node parent) ;
    
    public boolean isLeaf();
    
    public void setLeaf(boolean leaf) ;

    int getCoreRoot();
    
    void setCoreRoot(int coreRoot);
    
    public void setIsChanged(int isChanged);
}
