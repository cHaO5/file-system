package model;

//储存块
public class Block {
    private String content;
    private Node[] node = new Node[8];

    public Block() {
        content = new String();
        for (int i = 0; i < 8; i++) {
            node[i] = new Node();
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Node[] getNode() {
        return node;
    }

    public void setNode(Node[] node) {
        this.node = node;
    }
}
