package com.test.leetcode;

/**
 * 相关排序
 * 参考动画：https://www.cnblogs.com/onepixel/p/7674659.html
 * @author zjf
 * @time
 */
public class Sort {
    /**
     * 冒泡排序
     * @param arr
     * @return
     */
    public  Integer[] bubbleSort(Integer[] arr) {
        int len = arr.length;
        for(int i = 0; i < len - 1; i++) {
            for(int j = 0; j < len - 1 - i; j++) {
                // 相邻元素两两对比
                if(arr[j] > arr[j+1]) {
                    // 元素交换
                    Integer temp = arr[j+1];
                    arr[j+1] = arr[j];
                    arr[j] = temp;
                }
            }
        }
        return arr;
    }
    /**
     * 选择排序
     */
    public Integer[] selectSort(Integer[] arr){
        int len = arr.length;
        int minIndex, temp;
        for(int i = 0; i < len - 1; i++) {
            minIndex = i;
            for(int j = i + 1; j < len; j++) {
                if(arr[j] < arr[minIndex]) {     // 寻找最小的数
                    minIndex = j;                 // 将最小数的索引保存
                }
            }
            temp = arr[i];
            arr[i] = arr[minIndex];
            arr[minIndex] = temp;
        }
        return arr;
}


/**
 * 快排
 */

    public void quickSort(Object[] r, int low, int high){
        if (low<high){
            //找出枢轴
            int pa = partition(r,low,high);
            //递归排序左子序
            quickSort(r,low,pa-1);
            //递归排序右子序
            quickSort(r,pa+1,high);
        }
    }
    private int partition(Object[] r, int low, int high){
        Object pivot = r[low]; //使用 r[low]作为枢轴元素
        while (low<high){ //从两端交替向内扫描
            while(low<high&& this.compare(r[high],pivot)>=0) {high--;}
            //将比 pivot 小的元素移向低端
            r[low] = r[high];
            while(low<high&& this.compare(r[low],pivot)<=0) {low++;}
            //将比 pivot 大的元素移向高端
            r[high] = r[low];

        }
        r[low] = pivot; //设置枢轴
        return low; //返回枢轴元素位置
    }

    /**
     * 比较两个数据元素的大小
     * 如果 obj1 < obj2 返回-1
     * 如果 obj1 = obj2 返回 0
     * 如果 obj1 > obj2 返回 1
     */
    public int compare(Object obj1, Object obj2) {
        return obj1.toString().compareTo(obj2.toString());
    }
    /**
     * 插入排序
     */
    public Integer[] insertionSort(Integer[] arr) {
        int len = arr.length;
        int preIndex, current;
        for(int i = 1; i < len; i++) {
            preIndex = i - 1;
            current = arr[i];
            while(preIndex >= 0 && arr[preIndex] > current) {
                arr[preIndex + 1] = arr[preIndex];
                preIndex--;
            }
            arr[preIndex + 1] = current;
        }
        return arr;
    }


    /**
     * 希尔排序（Shell Sort）
     */
    public Integer[] shellSort(Integer[] arr) {
        int len = arr.length;
        for(int gap = (int)Math.floor(len / 2); gap > 0; gap = (int)Math.floor(gap / 2)) {
            // 注意：这里和动图演示的不一样，动图是分组执行，实际操作是多个分组交替执行
            for(int i = gap; i < len; i++) {
                int j = i;
                int current = arr[i];
                while(j - gap >= 0 && current < arr[j - gap]) {
                    arr[j] = arr[j - gap];
                    j = j - gap;
                }
                arr[j] = current;
            }
        }
        return arr;
    }
}
