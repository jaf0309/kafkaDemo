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
     * 选择排序：首先在未排序序列中找到最小（大）元素，存放到排序序列的起始位置，
     * 然后，再从剩余未排序元素中继续寻找最小（大）元素，然后放到已排序序列的末尾。以此类推，直到所有元素均排序完毕。
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
 * 是通过构建有序序列，对于未排序数据，在已排序序列中从后向前扫描，找到相应位置并插入。
 */

/**
 * 快排：通过一趟排序将待排记录分隔成独立的两部分，
 * 其中一部分记录的关键字均比另一部分的关键字小，则可分别对这两部分记录继续进行排序，以达到整个序列有序。
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
     *      归并排序:是建立在归并操作上的一种有效的排序算法。
     *      该算法是采用分治法（Divide and Conquer）的一个非常典型的应用。将已有序的子序列合并，得到完全有序的序列；
     *      即先使每个子序列有序，再使子序列段间有序。若将两个有序表合并成一个有序表，称为2-路归并。
     *          if (low<high){
     *             mergeSort(r,low,(high+low)/2);
     *             mergeSort(r,(high+low)/2+1,high);
     *             merge(r,low,(high+low)/2,high);
     *         }
     */

    public void mergeSort(int[] data, int left, int right) {
        print(data);
        if (left >= right) {return;}
        //两路归并
        // 找出中间索引
        int center = (left + right) / 2;
        // 对左边数组进行递归
        mergeSort(data, left, center);
        // 对右边数组进行递归
        mergeSort(data, center + 1, right);
        // 合并
        merge(data, left, center, center + 1, right);
        print(data);
    }

    private String print(int[] data){
        if(data==null || data.length==0){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for(int t=0;t<data.length;t++){
            sb.append(data[t]);
        }
        System.out.println(sb.toString());
        return "";
    }

    public void merge(int[] data, int leftStart, int leftEnd, int rightStart, int rightEnd) {
        int i = leftStart;
        int j = rightStart;
        int k = 0;
        // 临时数组
        int[] temp = new int[rightEnd - leftStart + 1]; //创建一个临时的数组来存放临时排序的数组
        // 确认分割后的两段数组是否都取到了最后一个元素
        while (i <= leftEnd && j <= rightEnd) {
            // 从两个数组中取出最小的放入临时数组
            if (data[i] > data[j]) {
                temp[k++] = data[j++];
            } else {
                temp[k++] = data[i++];
            }
        }
        // 剩余部分依次放入临时数组（实际上两个while只会执行其中一个）
        while (i <= leftEnd) {
            temp[k++] = data[i++];
        }
        while (j <= rightEnd) {
            temp[k++] = data[j++];
        }
        //System.out.println("第"+(++number)+"趟排序:\t");
        k = leftStart;
        // 将临时数组中的内容拷贝回原数组中 // （原left-right范围的内容被复制回原数组）
        for (int element : temp) {
            data[k++] = element;
        }
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


    public static void main(String[] args) {
        Sort sort = new Sort();
        //归并排序
        int[] mergeInt = {4, 8, 9, 5, 2, 1, 4, 6};
        sort.mergeSort(mergeInt,0,7);

    }
}
