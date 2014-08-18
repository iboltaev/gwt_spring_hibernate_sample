package ru.naumen.knowledgebase.model;

import javax.persistence.*;
import org.hibernate.annotations.Cascade;

import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;

@Entity
@Table
public class NodeText {
    private static final long serialVersionUID = 1L;

    public NodeText() {}

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "text", nullable = false)
    private String text;

    @OneToOne(mappedBy = "nodeText")
    private TreeNode treeNode;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public void setTreeNode(TreeNode node) { treeNode = node;} 

    @Override
    public boolean equals(java.lang.Object other) {
        if (this == other)
            return true;

        if (other == null)
            return false;

        if (getClass() != other.getClass())
            return false;

        return id == ((NodeText)other).id;
    }

    @Override
    public int hashCode() {
        return (int)Hasher.hashCombine(3541, id);
    }
}
