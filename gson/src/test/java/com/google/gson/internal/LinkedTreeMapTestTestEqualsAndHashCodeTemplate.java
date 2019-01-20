package com.google.gson.internal;
import java.util.AbstractMap;
import com.google.gson.common.MoreAsserts;
import static com.google.gson.common.MoreAsserts.assertEqualsAndHashCode;
public class LinkedTreeMapTestTestEqualsAndHashCodeTemplate {
  public static <TLinkedTreeMapStringInteger extends AbstractMap<String,Integer>>void linkedTreeMapTestTestEqualsAndHashCodeTemplate(  Class<TLinkedTreeMapStringInteger> clazzTLinkedTreeMapStringInteger) throws Exception {
    TLinkedTreeMapStringInteger map1=clazzTLinkedTreeMapStringInteger.newInstance();
    map1.put("A",1);
    map1.put("B",2);
    map1.put("C",3);
    map1.put("D",4);
    TLinkedTreeMapStringInteger map2=clazzTLinkedTreeMapStringInteger.newInstance();
    map2.put("C",3);
    map2.put("B",2);
    map2.put("D",4);
    map2.put("A",1);
    MoreAsserts.assertEqualsAndHashCode(map1,map2);
  }
}
