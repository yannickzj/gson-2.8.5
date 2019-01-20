/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson.functional;

import com.google.gson.common.TestTypes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.common.TestTypes.Base;
import com.google.gson.common.TestTypes.BaseSerializer;
import com.google.gson.common.TestTypes.ClassWithBaseArrayField;
import com.google.gson.common.TestTypes.ClassWithBaseField;
import com.google.gson.common.TestTypes.Sub;
import com.google.gson.common.TestTypes.SubSerializer;

import junit.framework.TestCase;

import java.lang.reflect.Type;

/**
 * Functional Test exercising custom serialization only.  When test applies to both
 * serialization and deserialization then add it to CustomTypeAdapterTest.
 *
 * @author Inderjeet Singh
 */
public class CustomSerializerTest extends TestCase {

   public void testBaseClassSerializerInvokedForBaseClassFields() throws Exception {
	this.customSerializerTestTestClassSerializerInvokedForBaseClassFieldsSubClassTemplate(TestTypes.Base.class,
			BaseSerializer.NAME);
}

   public void testSubClassSerializerInvokedForBaseClassFieldsHoldingSubClassInstances() throws Exception {
	this.customSerializerTestTestClassSerializerInvokedForBaseClassFieldsSubClassTemplate(TestTypes.Sub.class,
			SubSerializer.NAME);
}

   public void testSubClassSerializerInvokedForBaseClassFieldsHoldingArrayOfSubClassInstances() {
     Gson gson = new GsonBuilder()
         .registerTypeAdapter(Base.class, new BaseSerializer())
         .registerTypeAdapter(Sub.class, new SubSerializer())
         .create();
     ClassWithBaseArrayField target = new ClassWithBaseArrayField(new Base[] {new Sub(), new Sub()});
     JsonObject json = (JsonObject) gson.toJsonTree(target);
     JsonArray array = json.get("base").getAsJsonArray();
     for (JsonElement element : array) {
       JsonElement serializerKey = element.getAsJsonObject().get(Base.SERIALIZER_KEY);
      assertEquals(SubSerializer.NAME, serializerKey.getAsString());
     }
   }

   public void testBaseClassSerializerInvokedForBaseClassFieldsHoldingSubClassInstances() {
     Gson gson = new GsonBuilder()
         .registerTypeAdapter(Base.class, new BaseSerializer())
         .create();
     ClassWithBaseField target = new ClassWithBaseField(new Sub());
     JsonObject json = (JsonObject) gson.toJsonTree(target);
     JsonObject base = json.get("base").getAsJsonObject();
     assertEquals(BaseSerializer.NAME, base.get(Base.SERIALIZER_KEY).getAsString());
   }

   public void testSerializerReturnsNull() {
     Gson gson = new GsonBuilder()
       .registerTypeAdapter(Base.class, new JsonSerializer<Base>() {
         public JsonElement serialize(Base src, Type typeOfSrc, JsonSerializationContext context) {
           return null;
         }
       })
       .create();
       JsonElement json = gson.toJsonTree(new Base());
       assertTrue(json.isJsonNull());
   }

public <TBase> void customSerializerTestTestClassSerializerInvokedForBaseClassFieldsSubClassTemplate(
		Class<TBase> clazzTBase, String string1) throws Exception {
	Gson gson = new GsonBuilder().registerTypeAdapter(Base.class, new BaseSerializer())
			.registerTypeAdapter(Sub.class, new SubSerializer()).create();
	ClassWithBaseField target = new ClassWithBaseField((TestTypes.Base) clazzTBase.newInstance());
	JsonObject json = (JsonObject) gson.toJsonTree(target);
	JsonObject base = json.get("base").getAsJsonObject();
	assertEquals(string1, base.get(Base.SERIALIZER_KEY).getAsString());
}
}
