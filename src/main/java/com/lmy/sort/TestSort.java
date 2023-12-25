package com.lmy.sort;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * 多字段排序
 * 三个集合， A，B，C，  A依赖B,C,先根据B的顺序排序，再根据C的顺序排序
 * @author mingyang.lu
 * @date 2023/12/20 15:13
 */
public class TestSort {

    public static void main(String[] args) {
        List<A> aList = new ArrayList<>();
        aList.add(new A("1","1","3","1"));
        aList.add(new A("2","2",null,"2"));
        aList.add(new A("3","3","1","2"));
        aList.add(new A("4","4","1","1"));
        aList.add(new A("5","5","2","2"));
        aList.add(new A("6","6","2","1"));


        List<B> bList = new ArrayList<>();
        bList.add( new B("1","1"));
        bList.add( new B("2","2"));
        bList.add( new B("3","3"));

        Map<String, Integer> bMap = bList.stream().collect(Collectors.toMap(B::getId, bList::indexOf));

        List<C> cList = new ArrayList<>();
        cList.add(new C("1","1"));
        cList.add(new C("2","2"));

        Map<String, Integer> cMap = cList.stream().collect(Collectors.toMap(C::getId, cList::indexOf));

        aList = aList.stream()
                .sorted(Comparator
                        .comparing((Function<A, Integer>) a -> bMap.getOrDefault(a.bId, 9999))
                        .thenComparing(a -> cMap.getOrDefault(a.cId,9999)))
                .collect(Collectors.toList());

        aList.forEach(a->{
            System.out.println(a);
        });


    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class A {

        private String id;

        private String name;

        private String bId;

        private String cId;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
   static class B {
        private String id;

        private String name;


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
   static class C {
        private String id;

        private String name;
    }
}
