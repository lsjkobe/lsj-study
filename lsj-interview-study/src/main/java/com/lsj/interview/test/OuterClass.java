package com.lsj.interview.test;

public class OuterClass {
    String str = new String("hello");
    char[] ch = {'a', 'b'};

    public static void main(String[] args) {
//        OuterClass a = new OuterClass();
//        a.change(a.str, a.ch);
//        System.out.println(a.str + " and ");
//        System.out.println(a.ch);
        String a[]=new String[5];for(int i=0;i<5;a[i++]="");
        String b[] = {"", "", "" ,"", ""};
        String [] c = new String[5];
        for (int i = 0; i < 5; c[i++]=null) {
            
        }
        System.out.println("");
//        int nu =[]={};
    }

    public void change(String str, char[] ch) {
        str = "test OK";
        ch[0]='c';
    }

    public OuterClass nihao() {
        return this;
    }

    public class Test extends OuterClass {
        public OuterClass nihao() {
                return null;
        }
        void test111() {

        }
    }

    public interface testI {
        void test1();

        public double test2();

//        protected double test3();
    }
}
