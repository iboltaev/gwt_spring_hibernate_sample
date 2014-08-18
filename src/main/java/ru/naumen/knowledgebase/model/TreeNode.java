package ru.naumen.knowledgebase.model;

import javax.persistence.*;
import org.hibernate.annotations.Cascade;

import java.util.List;

@Entity
@Table
public class TreeNode {
    private static final long serialVersionUID = 1L;

    public TreeNode() {}

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne()
    @JoinColumn(name = "parentId", nullable = true, insertable = false, updatable = false)
    private TreeNode parent;

    @OneToMany(mappedBy = "parent", orphanRemoval=true)
    @OrderBy("id")
    @Cascade({org.hibernate.annotations.CascadeType.DELETE})
    private List<TreeNode> childs;

    @Column(name = "parentId", nullable = true)
    @Cascade({org.hibernate.annotations.CascadeType.DELETE})
    private Integer parentId;

    @Column(name ="briefName", nullable = false)
    private String briefName;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne()
    @JoinColumn(name = "textId", nullable = true, insertable = true, updatable = true, unique = true)
    @Cascade({org.hibernate.annotations.CascadeType.DELETE})
    private NodeText nodeText;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId;}

    public String getBriefName() { return briefName; }
    public void setBriefName(String briefName) { this.briefName = briefName; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public NodeText getNodeText() { return nodeText; }
    public void setNodeText(NodeText text) { this.nodeText = text; }

    // hierarchy representation
    public TreeNode parent() { return parent; }
    public List<TreeNode> children() { return childs; }

    @Override
    public boolean equals(java.lang.Object other) {
        if (this == other)
            return true;

        if (other == null)
            return false;

        if (getClass() != other.getClass())
            return false;

        return id == ((TreeNode)other).id;
    }

    @Override
    public int hashCode() {
        return (int)Hasher.hashCombine(3541, id);
    }
}
