/*
 * Copyright (C) 2014 Google Inc.
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

package com.google.gson.stream;

import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.internal.bind.JsonTreeReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
public class JsonReaderPathTest {
  @Parameterized.Parameters(name = "{0}")
  public static List<Object[]> parameters() {
    return Arrays.asList(
        new Object[] { Factory.STRING_READER },
        new Object[] { Factory.OBJECT_READER }
    );
  }

  @Parameterized.Parameter
  public Factory factory;

  @Test public void path() throws IOException {
    JsonReader reader = factory.create("{\"a\":[2,true,false,null,\"b\",{\"c\":\"d\"},[3]]}");
    assertEquals("$", reader.getPath());
    reader.beginObject();
    assertEquals("$.", reader.getPath());
    reader.nextName();
    assertEquals("$.a", reader.getPath());
    reader.beginArray();
    assertEquals("$.a[0]", reader.getPath());
    reader.nextInt();
    assertEquals("$.a[1]", reader.getPath());
    reader.nextBoolean();
    assertEquals("$.a[2]", reader.getPath());
    reader.nextBoolean();
    assertEquals("$.a[3]", reader.getPath());
    reader.nextNull();
    assertEquals("$.a[4]", reader.getPath());
    reader.nextString();
    assertEquals("$.a[5]", reader.getPath());
    reader.beginObject();
    assertEquals("$.a[5].", reader.getPath());
    reader.nextName();
    assertEquals("$.a[5].c", reader.getPath());
    reader.nextString();
    assertEquals("$.a[5].c", reader.getPath());
    reader.endObject();
    assertEquals("$.a[6]", reader.getPath());
    reader.beginArray();
    assertEquals("$.a[6][0]", reader.getPath());
    reader.nextInt();
    assertEquals("$.a[6][1]", reader.getPath());
    reader.endArray();
    assertEquals("$.a[7]", reader.getPath());
    reader.endArray();
    assertEquals("$.a", reader.getPath());
    reader.endObject();
    assertEquals("$", reader.getPath());
  }

  @Test public void objectPath() throws IOException {
    JsonReader reader = factory.create("{\"a\":1,\"b\":2}");
    assertEquals("$", reader.getPath());

    reader.peek();
    assertEquals("$", reader.getPath());
    reader.beginObject();
    assertEquals("$.", reader.getPath());

    reader.peek();
    assertEquals("$.", reader.getPath());
    reader.nextName();
    assertEquals("$.a", reader.getPath());

    reader.peek();
    assertEquals("$.a", reader.getPath());
    reader.nextInt();
    assertEquals("$.a", reader.getPath());

    reader.peek();
    assertEquals("$.a", reader.getPath());
    reader.nextName();
    assertEquals("$.b", reader.getPath());

    reader.peek();
    assertEquals("$.b", reader.getPath());
    reader.nextInt();
    assertEquals("$.b", reader.getPath());

    reader.peek();
    assertEquals("$.b", reader.getPath());
    reader.endObject();
    assertEquals("$", reader.getPath());

    reader.peek();
    assertEquals("$", reader.getPath());
    reader.close();
    assertEquals("$", reader.getPath());
  }

  @Test public void arrayPath() throws IOException {
    JsonReader reader = factory.create("[1,2]");
    assertEquals("$", reader.getPath());

    reader.peek();
    assertEquals("$", reader.getPath());
    reader.beginArray();
    assertEquals("$[0]", reader.getPath());

    reader.peek();
    assertEquals("$[0]", reader.getPath());
    reader.nextInt();
    assertEquals("$[1]", reader.getPath());

    reader.peek();
    assertEquals("$[1]", reader.getPath());
    reader.nextInt();
    assertEquals("$[2]", reader.getPath());

    reader.peek();
    assertEquals("$[2]", reader.getPath());
    reader.endArray();
    assertEquals("$", reader.getPath());

    reader.peek();
    assertEquals("$", reader.getPath());
    reader.close();
    assertEquals("$", reader.getPath());
  }

  @Test public void multipleTopLevelValuesInOneDocument() throws IOException {
    assumeTrue(factory == Factory.STRING_READER);

    JsonReader reader = factory.create("[][]");
    reader.setLenient(true);
    reader.beginArray();
    reader.endArray();
    assertEquals("$", reader.getPath());
    reader.beginArray();
    reader.endArray();
    assertEquals("$", reader.getPath());
  }

  @Test public void skipArrayElements() throws IOException {
    JsonReader reader = factory.create("[1,2,3]");
    reader.beginArray();
    reader.skipValue();
    reader.skipValue();
    assertEquals("$[2]", reader.getPath());
  }

  @Test public void skipObjectNames() throws IOException {
    JsonReader reader = factory.create("{\"a\":1}");
    reader.beginObject();
    reader.skipValue();
    assertEquals("$.null", reader.getPath());
  }

  @Test public void skipObjectValues() throws IOException {
    JsonReader reader = factory.create("{\"a\":1,\"b\":2}");
    reader.beginObject();
    assertEquals("$.", reader.getPath());
    reader.nextName();
    reader.skipValue();
    assertEquals("$.null", reader.getPath());
    reader.nextName();
    assertEquals("$.b", reader.getPath());
  }

  @Test public void skipNestedStructures() throws IOException {
    JsonReader reader = factory.create("[[1,2,3],4]");
    reader.beginArray();
    reader.skipValue();
    assertEquals("$[1]", reader.getPath());
  }

  @Test
public void arrayOfObjects() throws Exception {
	this.jsonReaderPathTestArrayOfTemplate(new JsonReaderPathTestArrayOfObjectsAdapterImpl(), "[{},{},{}]", "$[0].",
			"$[1].", "$[2].");
}

  @Test
public void arrayOfArrays() throws Exception {
	this.jsonReaderPathTestArrayOfTemplate(new JsonReaderPathTestArrayOfArraysAdapterImpl(), "[[],[],[]]", "$[0][0]",
			"$[1][0]", "$[2][0]");
}

  enum Factory {
    STRING_READER {
      @Override public JsonReader create(String data) {
        return new JsonReader(new StringReader(data));
      }
    },
    OBJECT_READER {
      @Override public JsonReader create(String data) {
        JsonElement element = Streams.parse(new JsonReader(new StringReader(data)));
        return new JsonTreeReader(element);
      }
    };

    abstract JsonReader create(String data);
  }

public void jsonReaderPathTestArrayOfTemplate(JsonReaderPathTestArrayOfAdapter adapter, String string1, String string2,
		String string3, String string4) throws Exception {
	JsonReader reader = factory.create(string1);
	reader.beginArray();
	assertEquals("$[0]", reader.getPath());
	adapter.begin(reader);
	assertEquals(string2, reader.getPath());
	adapter.end(reader);
	assertEquals("$[1]", reader.getPath());
	adapter.begin(reader);
	assertEquals(string3, reader.getPath());
	adapter.end(reader);
	assertEquals("$[2]", reader.getPath());
	adapter.begin(reader);
	assertEquals(string4, reader.getPath());
	adapter.end(reader);
	assertEquals("$[3]", reader.getPath());
	reader.endArray();
	assertEquals("$", reader.getPath());
}

interface JsonReaderPathTestArrayOfAdapter {
	void begin(JsonReader jsonReader1) throws IOException;

	void end(JsonReader jsonReader1) throws IOException;
}

class JsonReaderPathTestArrayOfObjectsAdapterImpl implements JsonReaderPathTestArrayOfAdapter {
	public void begin(JsonReader reader) throws IOException {
		reader.beginObject();
	}

	public void end(JsonReader reader) throws IOException {
		reader.endObject();
	}
}

class JsonReaderPathTestArrayOfArraysAdapterImpl implements JsonReaderPathTestArrayOfAdapter {
	public void begin(JsonReader reader) throws IOException {
		reader.beginArray();
	}

	public void end(JsonReader reader) throws IOException {
		reader.endArray();
	}
}
}
