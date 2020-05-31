
import java.util.Scanner;

public class test {

    public static void main(String[] args) {
        //    Scanner sc=new Scanner(System.in);
        //    String s=sc.next();
        //    Stack<Character> st=new Stack<>();
        //    String output="";
        //    int left_flag=0;
        //    for(int i=0;i<s.length();i++)
        //    {
        //        //先要找到边界处
        //        if(s.charAt(i)==')'||s.charAt(i)=='}'||s.charAt(i)==']')
        //        {
        //            String temp="";
        //            Character c=st.pop();
        //            while(c!='('&&c!='['&&c!='{')
        //            {
        //                temp=temp+c.toString();
        //                c=st.pop();
        //            }
        //            int n=st.pop()-'0';
        //            String s1=temp;
        //            //解决内部批次
        //            while(n>1)
        //            {
        //                temp=temp+s1;
        //                n--;
        //            }
        //            for(int j=temp.length()-1;j>=0;j--)
        //            {
        //                st.push(temp.charAt(j));
        //            }
        //        }
        //        else
        //        {
        //            st.push(s.charAt(i));
        //        }
        //    }
        //
        //    while(!st.isEmpty())
        //    {
        //        output=output+st.pop().toString();
        //    }
        //    System.out.println(output);
        //}
        Scanner scanner = new Scanner(System.in);
        String[] string_inputs = scanner.nextLine().split(" ");
        if(string_inputs.length > 500) {
            System.out.println("输入有误");
        } else {
            int[] inputs = new int[string_inputs.length];
            for (int i =0; i< string_inputs.length;i++) {
                inputs[i] = Integer.parseInt(string_inputs[i]);
            }
            int input = inputs.length;
            int left = inputs.length;
            boolean flag[] = new boolean[inputs.length]; //用来标记是否删除了这个数
            int num = 0; //用来计数
            int count=1;
            int result = 0;
            for (int i = 0; i < input; i=i+count) {
                num++;
                count++;
                if (!flag[i]) {
                    if (num % count == 0) {
                        flag[i] = true;
                        left--;
                        System.out.println(i+"    out");
                    }
                    if (left == 1) {
                        break;
                    }
                } else {
                    num--;
                }
                if (i + 1 == input) {
                    i = -1;
                }
            }
            //找出最后那个数
            for (int j = 0; j < input; j++) {
                if (!flag[j]) {
                    result = j;
                    break;
                }
            }
            System.out.println(result);
        }
   }

    public static int getLastDeletedIndex(int len) {
        if (len <= 0) { // 如果数组长度不满足要求则返回 -1
            return -1;
        }

        int[] arr = new int[len];
        for (int i = 0; i < len; i++) { // 初始化每个元素的值为当前下标
            arr[i] = len;
        }

        final int DELFLAG = len + 1; // 删除标志位
        int currentSize = len; // 记录数组当前有效长度（即未被置为删除标志的元素个数），最后变为 0
        int STEP = 2; // 步长
        int count = 0; // 步长计数
        int lastDelIndex = 0; // 记录最后被删除的元素的下标
        int i = 0; // 循环下标

        while (currentSize != 0) {
            if (arr[i] != DELFLAG) { // 判读当前元素是否等于删除标志
                if (count++ == STEP) { // 当步长计数满足步长则
                    arr[i] = DELFLAG; // 将元素置为删除标志位
                    lastDelIndex = i; // 记录该处下标
                    currentSize--; // 有效数组长度减 1
                    count = 0; // 步长计数归零
                    System.out.println("Deleted index is " + i % len);
                }
            }
            i = (i + 1) % len; // 下标取余实现循环下标
        }
        return lastDelIndex;
    }

}