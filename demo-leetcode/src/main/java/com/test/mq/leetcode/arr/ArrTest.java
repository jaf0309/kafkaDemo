package com.test.mq.leetcode.arr;

public class ArrTest {
    //两数相加
    private  int[] twoSum(int[] nums, int target) {
        int [] a =new int[2];
        boolean result = false;
        for(int i = 0;i<nums.length;i++){
            for(int j = 0;j<nums.length;j++){
                if(result){
                    return a;
                }
                if(nums[j] == target-nums[i] && i<j){
                    a[0] = i;
                    a[1] = j;
                    result = true;
                }
            }
        }
        return a;
    }

    public int lengthOfLongestSubstring(String s) {


        return 0;
    }


}
