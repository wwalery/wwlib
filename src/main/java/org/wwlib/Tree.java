package org.wwlib;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class Tree<T> {

 private List<Tree<T>> childs;
 private Tree owner;
 private T object;

 public Tree(T value) {
  object = value;
 }


 public void addChild(T value) {
  if (childs==null) childs = new ArrayList<Tree<T>>();
  Tree<T> tree = new Tree<T>(value);
  tree.setOwner(this);
  childs.add(tree);
 }

 public List<Tree<T>> getChilds() {
  return childs;
 }

 public void setOwner(Tree owner) {
  this.owner = owner;
 }

 public Tree getOwner() {
  return owner;
 }

 public T getObject() {
  return object;
 }

 public Tree findNode(T value, Comparator<T> comparator) {
  if (comparator.compare(value,this.getObject())==0) return this;
  else if (childs!=null) {
   for (Tree tree : childs) {
    Tree found = tree.findNode(value, comparator);
    if (found!=null) return found;
   }
  }
  return null;
 }

}
