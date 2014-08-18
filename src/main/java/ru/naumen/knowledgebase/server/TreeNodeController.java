package ru.naumen.knowledgebase.server;

import ru.naumen.knowledgebase.model.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.beans.factory.annotation.Autowired;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;

/**
   Simple tree node controller, written in spring.

   TODO: It would be better to have separate class for object model.

   TODO: exceptions should at least be logged; this is demo application,
   so we omit this part.
 */
@Controller 
public class TreeNodeController {

    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping(value = "treenode/get", method = RequestMethod.GET)
    public @ResponseBody List<TreeNode> getTreeNodes() throws Exception 
    {
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            List<TreeNode> result = (List<TreeNode>)session
                    .createCriteria(TreeNode.class)
                    .add(Restrictions.isNull("parent"))
                    .addOrder(Order.asc("id")).list();

            tx.commit();

            return result;
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            throw e;
        }
    }

    @RequestMapping(value = "treenode/get/{id}", method = RequestMethod.GET)
    public @ResponseBody List<TreeNode> getTreeNode(
        @PathVariable int id) throws Exception 
    {
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            TreeNode result = (TreeNode)session.get(TreeNode.class, id);
            List<TreeNode> children = new ArrayList();

            if (result != null)
                children.addAll(result.children());

            tx.commit();

            return children;
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            throw e;
        }
    }

    @RequestMapping(value = "treenode/create/", method = RequestMethod.POST)
    public @ResponseBody TreeNode createTreeNode(@RequestBody TreeNode c) throws Exception {
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // we should check that parent of this node does not have textData attached
            // TODO: exception thrown must be typed
            if (!checkParentValid(c.parent()))
                throw new Exception(
                    "Attempt to add child to the text node");

            if (c.getNodeText() != null)
                session.save(c.getNodeText());

            session.save(c);
            tx.commit();
            return c;
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            throw e;
        }
    }

    @RequestMapping(value = "treenode/delete/{id}", method = RequestMethod.DELETE)
    public void deleteTreeNode(@PathVariable int id) throws Exception {
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            TreeNode node = (TreeNode)session.get(TreeNode.class, id);

            if (node == null) {
                tx.commit();
                return;
            }

            if (node.getNodeText() != null)
                session.delete(node.getNodeText());
            else
                session.delete(node);

            tx.commit();
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            throw e;
        }
    }

    @RequestMapping(value = "treenode/edit/", method = RequestMethod.PUT)
    public @ResponseBody TreeNode editTreeNode(@RequestBody TreeNode object) throws Exception {
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            if (!checkParentValid(object.parent()))
                throw new Exception(
                    "Attempt to move object to the text node");

            if (object.getNodeText() != null) {
                // make cross-reference
                object.getNodeText().setTreeNode(object);
                session.update(object.getNodeText());
            }

            session.update(object);

            tx.commit();
            return object;
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            throw e;
        }
    }

    private boolean checkParentValid(TreeNode node) {
        return node == null || node.getNodeText() == null;
    }
} 
