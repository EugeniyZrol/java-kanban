package task;

public class Node<E> {
    public E data;
    public Node<E> next;
    public Node<E> prev;


    public Node(Node<E> prev, E data, Node<E> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

    public E getData() {
        return data;
    }

    public void setNext(Node<E> next){
        this.next = next;
    }

    public Node<E> getNext() {
        return next;
    }

    public void setPrev(Node<E> prev) {
        this.prev = prev;
    }

    public Node<E> getPrev(){
        return prev;
    }
}
