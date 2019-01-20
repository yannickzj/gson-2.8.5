package com.google.gson.internal;
import java.util.AbstractMap;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertEquals;
public class LinkedTreeMapTestTestPutOverridesTemplate {
  public static <TLinkedTreeMapStringString extends AbstractMap<String,String>>void linkedTreeMapTestTestPutOverridesTemplate(  Class<TLinkedTreeMapStringString> clazzTLinkedTreeMapStringString) throws Exception {
    TLinkedTreeMapStringString map=clazzTLinkedTreeMapStringString.newInstance();
    assertNull(map.put("d","donut"));
    assertNull(map.put("e","eclair"));
    assertNull(map.put("f","froyo"));
    assertEquals(3,map.size());
    assertEquals("donut",map.get("d"));
    assertEquals("donut",map.put("d","done"));
    assertEquals(3,map.size());
  }
}
