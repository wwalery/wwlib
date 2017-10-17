package org.wwlib;

import java.util.List;

/**
 *
 * @author walery
 */
public interface ITreeNode {

 void addChild(ITreeNode node);

 /**
  * @return the children
  */
 List<ITreeNode> getChildren();

 /**
  * @return the id
  */
 String getId();

 /**
  * @return the level
  */
 int getLevel();

 /**
  * @return the name
  */
 String getName();

 /**
  * @param children the children to set
  */
 void setChildren(List<ITreeNode> children);

 /**
  * @param id the id to set
  */
 void setId(String id);

 /**
  * @param level the level to set
  */
 void setLevel(int level);

 /**
  * @param name the name to set
  */
 void setName(String name);

}
