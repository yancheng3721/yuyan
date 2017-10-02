package com.yuyan.emall.admin.service.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractTreeService {

	private static Logger logger = LoggerFactory
			.getLogger(AbstractTreeService.class);

	private Collection<Node> roots = new ArrayList<Node>();
	private Map<String, Node> nodeMap = new HashMap<String, Node>();

	public abstract Collection<Node> getAllNodes();

	public void initial() {
		try {
			Collection<Node> allNodes = getAllNodes();
			initNodeMap(allNodes);
		} catch (Exception e) {
			logger.error("init dir tree error!", e);
		}
	}

	private void initNodeMap(Collection<Node> allNodes) {

		if (allNodes != null) {
			Map<String, Node> tmpMap = new HashMap<String, Node>();
			for (Node node : allNodes) {
				tmpMap.put("" + node.getId(), node);
			}
			roots = buildTree(allNodes);
			nodeMap.clear();
			nodeMap = tmpMap;

		}
	}

	public static Collection<Node> getRoots(Collection<Node> allNodes,
			Map<String, Node> nodeMap) {
		Collection<Node> roots = new ArrayList<Node>();
		if (allNodes != null && nodeMap != null) {
			for (Node node : allNodes) {
				nodeMap.put("" + node.getId(), node);
			}
			roots = buildTree(allNodes);
		}
		return roots;
	}

	private Node initRoot() {
		Node root = new DefaultNode();
		root.setId(DefaultNode.INITIAL_ID_SEQUENCE);
		root.setParent(null);
		root.setParentId(-1);
		root.setLevel(-1);
		root.setLeaf(false);
		return root;
	}

	@SuppressWarnings("unchecked")
	public static Collection<Node> buildTree(Collection<Node> nodes) {
		Collection<Node> roots = findRoots(nodes);
		Collection<Node> notRoots = CollectionUtils.subtract(nodes, roots);
		for (Node root : roots) {
			root.setChildren(findChildren(root, notRoots));
		}
		return roots;
	}

	public static Collection<Node> findRoots(Collection<Node> allNodes) {
		List<Node> results = new ArrayList<Node>();
		for (Node node : allNodes) {
			boolean isRoot = true;
			for (Node comparedOne : allNodes) {
				if (node.getParentId().intValue() == comparedOne.getId()
						.intValue()) {
					isRoot = false;
					break;
				}
			}
			if (isRoot) {
				node.setLevel(0);
				results.add(node);
			}
		}
		return results;
	}

	private static Collection<Node> findChildren(Node root,
			Collection<Node> allNodes) {
		Collection<Node> children = new ArrayList<Node>();

		for (Node comparedOne : allNodes) {
			if (comparedOne.getParentId().intValue() == root.getId().intValue()) {
				comparedOne.setParent(root);
				comparedOne.setLevel(root.getLevel() + 1);
				// comparedOne.setRootId(root.getRootId());
				children.add(comparedOne);
			}
		}
		Collection<Node> notChildren = CollectionUtils.subtract(allNodes,
				children);
		for (Node child : children) {
			Collection<Node> tmpChildren = findChildren(child, notChildren);
			if (tmpChildren == null || tmpChildren.size() < 1) {
				child.setLeaf(true);
			} else {
				child.setLeaf(false);
			}
			child.setChildren(tmpChildren);
		}
		return children;
	}

	public Node getNodeByNodeId(String id) {
		return nodeMap.get(id + "");
	}

	public Collection<Node> getRoots() {
		return roots;
	}

}
