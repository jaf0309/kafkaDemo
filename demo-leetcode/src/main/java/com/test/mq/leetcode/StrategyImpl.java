package com.test.leetcode;

public class StrategyImpl implements Strategy {
    public boolean equal(Object obj1, Object obj2) {
        return false;
    }

    public int compare(Object obj1, Object obj2) {
            return obj1.toString().compareTo(obj2.toString());
    }


    public static void main(String[] args) {
        StrategyImpl strategyimpl = new StrategyImpl();
        System.out.println(strategyimpl.compare(1, 2));
    }
}
