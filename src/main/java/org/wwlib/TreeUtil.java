package org.wwlib;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author walery
 */
public class TreeUtil {


 /**
  * Convert tree structore to plain list
  * @param treeList - list on ITreeNodes with tree structure
  * @return plai list of ITreeNode
  */

 public static List<ITreeNode> tree2Plain(List<ITreeNode> treeList) {
  List<ITreeNode> list = new ArrayList<ITreeNode>();
  int curLevel = 1;
  level2Plain(treeList, list, curLevel);
  return list;
 }


 /**
  * Find node by id in tree
  *
  * @param nodeList List with tree structure
  * @param id find it
  * @return node or null, if not found
  */

 public static ITreeNode findNode(List<ITreeNode> nodeList, String id) {
  for (ITreeNode node : nodeList) {
   if (id.equals(node.getId())) return node;
    else if (node.getChildren()!=null) {
     ITreeNode cn = findNode(node.getChildren(), id);
     if (cn!=null) return cn;
    }
  }
  return null;
 }

 
 private static void level2Plain(List<ITreeNode> treeList, List<ITreeNode> rawList, int level) {
  for (ITreeNode nd : treeList) {
   nd.setLevel(level);
   rawList.add(nd);
   List<ITreeNode> children = nd.getChildren();
   if ((children!=null) && children.size()>0) {
    level2Plain(children, rawList, level+1);
   }
  }
 }





}
