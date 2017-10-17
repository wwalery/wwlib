package org.wwlib;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author walery
 */
public class TreeNode implements ITreeNode {

 private String id;
 private String name;
 private int level;
 private List<ITreeNode> children;


 public TreeNode(String id, String name, List<ITreeNode> children) {
  this.id = id;
  this.name = name;
  this.children = children;
 }

 public TreeNode(String id, String name) {
  this.id = id;
  this.name = name;
 }


 /**
  * @param id the id to set
  */
 public void setId(String id) {
  this.id = id;
 }

 /**
  * @param name the name to set
  */
 public void setName(String name) {
  this.name = name;
 }

 /**
  * @param children the children to set
  */
 public void setChildren(List<ITreeNode> children) {
  this.children = children;
 }

 /**
  * @return the id
  */
 public String getId() {
  return id;
 }

 /**
  * @return the name
  */
 public String getName() {
  return name;
 }

 /**
  * @return the children
  */
 public List<ITreeNode> getChildren() {
  return children;
 }

 /**
  * @return the level
  */
 public int getLevel() {
  return level;
 }

 /**
  * @param level the level to set
  */
 public void setLevel(int level) {
  this.level = level;
 }


 public void addChild(ITreeNode node) {
  if (children==null) {
   children = new ArrayList<ITreeNode>();
  }
  children.add(node);
 }

}
