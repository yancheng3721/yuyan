package com.yuyan.emall.admin.service.tree;

import java.util.Collection;

public interface Node {
    public Long getId();
    
    public void setId(Long id) ;
    
    public Long getParentId() ;
    
    public void setParentId(Long parentId) ;

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
