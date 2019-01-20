package com.google.gson.internal;
import java.util.Random;
import java.util.AbstractMap;
import java.lang.Integer;
import java.lang.Math;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertEquals;
public class LinkedTreeMapTestTestTemplate {
  public static <TLinkedTreeMapStringString extends AbstractMap<String,String>>void linkedTreeMapTestTestTemplate(  Class<TLinkedTreeMapStringString> clazzTLinkedTreeMapStringString) throws Exception {
    Random random=new Random(1367593214724L);
    TLinkedTreeMapStringString map=clazzTLinkedTreeMapStringString.newInstance();
    String[] keys=new String[1000];
    for (int i=0; i < keys.length; i++) {
      keys[i]=Integer.toString(Math.abs(random.nextInt()),36) + "-" + i;
      map.put(keys[i],"" + i);
    }
    for (int i=0; i < keys.length; i++) {
      String key=keys[i];
      assertTrue(map.containsKey(key));
      assertEquals("" + i,map.get(key));
    }
  }
}
