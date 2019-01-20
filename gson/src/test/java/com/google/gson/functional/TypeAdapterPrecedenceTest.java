/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson.functional;

import java.lang.Object;
import java.lang.Class;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;
import java.lang.String;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import junit.framework.TestCase;

public final class TypeAdapterPrecedenceTest extends TestCase {
  public void testNonstreamingFollowedByNonstreaming() {
	this.typeAdapterPrecedenceTestTestNonstreamingFollowedByNonstreamingTemplate(
			new TypeAdapterPrecedenceTestTestNonstreamingFollowedByNonstreamingAdapterImpl(), "deserializer 2",
			"deserializer 1", "serializer 2", "serializer 1", "\"foo via serializer 2\"", "foo via deserializer 2");
}

  public void testStreamingFollowedByStreaming() {
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(Foo.class, newTypeAdapter("type adapter 1"))
        .registerTypeAdapter(Foo.class, newTypeAdapter("type adapter 2"))
        .create();
    assertEquals("\"foo via type adapter 2\"", gson.toJson(new Foo("foo")));
    assertEquals("foo via type adapter 2", gson.fromJson("foo", Foo.class).name);
  }

  public void testSerializeNonstreamingTypeAdapterFollowedByStreamingTypeAdapter() {
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(Foo.class, newSerializer("serializer"))
        .registerTypeAdapter(Foo.class, newDeserializer("deserializer"))
        .registerTypeAdapter(Foo.class, newTypeAdapter("type adapter"))
        .create();
    assertEquals("\"foo via type adapter\"", gson.toJson(new Foo("foo")));
    assertEquals("foo via type adapter", gson.fromJson("foo", Foo.class).name);
  }

  public void testStreamingFollowedByNonstreaming() {
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(Foo.class, newTypeAdapter("type adapter"))
        .registerTypeAdapter(Foo.class, newSerializer("serializer"))
        .registerTypeAdapter(Foo.class, newDeserializer("deserializer"))
        .create();
    assertEquals("\"foo via serializer\"", gson.toJson(new Foo("foo")));
    assertEquals("foo via deserializer", gson.fromJson("foo", Foo.class).name);
  }

  public void testStreamingHierarchicalFollowedByNonstreaming() {
    Gson gson = new GsonBuilder()
        .registerTypeHierarchyAdapter(Foo.class, newTypeAdapter("type adapter"))
        .registerTypeAdapter(Foo.class, newSerializer("serializer"))
        .registerTypeAdapter(Foo.class, newDeserializer("deserializer"))
        .create();
    assertEquals("\"foo via serializer\"", gson.toJson(new Foo("foo")));
    assertEquals("foo via deserializer", gson.fromJson("foo", Foo.class).name);
  }

  public void testStreamingFollowedByNonstreamingHierarchical() {
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(Foo.class, newTypeAdapter("type adapter"))
        .registerTypeHierarchyAdapter(Foo.class, newSerializer("serializer"))
        .registerTypeHierarchyAdapter(Foo.class, newDeserializer("deserializer"))
        .create();
    assertEquals("\"foo via type adapter\"", gson.toJson(new Foo("foo")));
    assertEquals("foo via type adapter", gson.fromJson("foo", Foo.class).name);
  }

  public void testStreamingHierarchicalFollowedByNonstreamingHierarchical() {
    Gson gson = new GsonBuilder()
        .registerTypeHierarchyAdapter(Foo.class, newSerializer("serializer"))
        .registerTypeHierarchyAdapter(Foo.class, newDeserializer("deserializer"))
        .registerTypeHierarchyAdapter(Foo.class, newTypeAdapter("type adapter"))
        .create();
    assertEquals("\"foo via type adapter\"", gson.toJson(new Foo("foo")));
    assertEquals("foo via type adapter", gson.fromJson("foo", Foo.class).name);
  }

  public void testNonstreamingHierarchicalFollowedByNonstreaming() {
	this.typeAdapterPrecedenceTestTestNonstreamingFollowedByNonstreamingTemplate(
			new TypeAdapterPrecedenceTestTestNonstreamingHierarchicalFollowedByNonstreamingAdapterImpl(),
			"non hierarchical", "non hierarchical", "hierarchical", "hierarchical", "\"foo via non hierarchical\"",
			"foo via non hierarchical");
}

  private static class Foo {
    final String name;
    private Foo(String name) {
      this.name = name;
    }
  }

  private JsonSerializer<Foo> newSerializer(final String name) {
    return new JsonSerializer<Foo>() {
      @Override
      public JsonElement serialize(Foo src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.name + " via " + name);
      }
    };
  }

  private JsonDeserializer<Foo> newDeserializer(final String name) {
    return new JsonDeserializer<Foo>() {
      @Override
      public Foo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return new Foo(json.getAsString() + " via " + name);
      }
    };
  }

  private TypeAdapter<Foo> newTypeAdapter(final String name) {
    return new TypeAdapter<Foo>() {
      @Override public Foo read(JsonReader in) throws IOException {
        return new Foo(in.nextString() + " via " + name);
      }
      @Override public void write(JsonWriter out, Foo value) throws IOException {
        out.value(value.name + " via " + name);
      }
    };
  }

public void typeAdapterPrecedenceTestTestNonstreamingFollowedByNonstreamingTemplate(
		TypeAdapterPrecedenceTestTestNonstreamingFollowedByNonstreamingAdapter adapter, String string1, String string2,
		String string3, String string4, String string5, String string6) {
	Gson gson = adapter
			.registerTypeAdapter(adapter.registerTypeAdapter(new GsonBuilder(), Foo.class, newSerializer(string4)),
					Foo.class, adapter.newAction1(string3))
			.registerTypeAdapter(Foo.class, adapter.newAction(string2))
			.registerTypeAdapter(Foo.class, newDeserializer(string1)).create();
	assertEquals(string5, gson.toJson(new Foo("foo")));
	assertEquals(string6, gson.fromJson("foo", Foo.class).name);
}

interface TypeAdapterPrecedenceTestTestNonstreamingFollowedByNonstreamingAdapter {
	Object newAction(String string1);

	Object newAction1(String string1);

	GsonBuilder registerTypeAdapter(GsonBuilder gsonBuilder1, Type type1, Object object1);
}

class TypeAdapterPrecedenceTestTestNonstreamingFollowedByNonstreamingAdapterImpl
		implements TypeAdapterPrecedenceTestTestNonstreamingFollowedByNonstreamingAdapter {
	public Object newAction(String string1) {
		return newDeserializer(string1);
	}

	public Object newAction1(String string1) {
		return newSerializer(string1);
	}

	public GsonBuilder registerTypeAdapter(GsonBuilder gsonBuilder1, Type type1, Object object1) {
		return gsonBuilder1.registerTypeAdapter(type1, object1);
	}
}

class TypeAdapterPrecedenceTestTestNonstreamingHierarchicalFollowedByNonstreamingAdapterImpl
		implements TypeAdapterPrecedenceTestTestNonstreamingFollowedByNonstreamingAdapter {
	public Object newAction(String string1) {
		return newSerializer(string1);
	}

	public Object newAction1(String string1) {
		return newDeserializer(string1);
	}

	public GsonBuilder registerTypeAdapter(GsonBuilder gsonBuilder1, Type type1, Object object1) {
		return gsonBuilder1.registerTypeHierarchyAdapter((Class<?>) type1, object1);
	}
}
}
