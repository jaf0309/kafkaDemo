package com.test.mq.leetcode.arr;





public class leedCodeTest {
    public static void main(String[] args) {
        ListNode listNode4 = new ListNode(4);
        ListNode listNode5 = new ListNode(5);
        ListNode listNode1 = new ListNode(1);
        ListNode listNode9 = new ListNode(9);
        listNode4.setNext(listNode5);
        listNode5.setNext(listNode1);
        listNode1.setNext(listNode9);
        StringBuilder sb = new StringBuilder();
        //System.out.println(listNode4.getVal()+","+listNode4.getNext().getVal()+","+ listNode4.getNext().getNext().getVal() +","+listNode4.getNext().getNext().getNext().getVal());
        //System.out.println(print(listNode4,sb).toString());
        ListNode temp = new ListNode(-1);
        ListNode listNode = deleteNode(listNode4, 1);

        //输出
        System.out.println(print(listNode,sb).toString());
    }

    //输出
    private static StringBuilder print(ListNode head,  StringBuilder sb){
        if( head.getNext() != null ){
            return print(head.getNext(),sb.append(head.getVal()+",")) ;
        }
        return sb.append(head.getVal());
    }




    public static ListNode deleteNode(ListNode head, int val) {
        if(head == null ){
            return null;
        }
        //第一个
        if(head.getVal() == val){
            return head;
        }
        if(head.getNext() != null){
            head.setNext(deleteListNodeByVal(head, head.getNext(), val));
            return head;
        }
        return head;

    }


    private static ListNode deleteListNodeByVal(ListNode last,ListNode curr,int val){
       if(curr.getVal()==val){
           last.setNext(curr.getNext());
           return last;
       }
       if(curr.getNext()!=null){
           return deleteListNodeByVal(curr, curr.getNext(), val);
       }
        return null;
    }




}
