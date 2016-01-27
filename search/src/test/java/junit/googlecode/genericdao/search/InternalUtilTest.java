/* Copyright 2013 David Wolverton
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package junit.googlecode.genericdao.search;

import static org.junit.Assert.*;

import org.junit.Test;

import com.googlecode.genericdao.search.InternalUtil;

public class InternalUtilTest {
	
	@Test
	public void testConvertIfNeeded() {
		assertEquals(null, InternalUtil.convertIfNeeded(null, Double.class));
		
		assertEquals(13.0, InternalUtil.convertIfNeeded(13L, Double.class));
		assertEquals(Double.class, InternalUtil.convertIfNeeded(13L, Double.class).getClass());
		assertEquals(13.0, InternalUtil.convertIfNeeded(13, Double.class));
		assertEquals(Double.class, InternalUtil.convertIfNeeded(13, Double.class).getClass());
		assertEquals(47.5, InternalUtil.convertIfNeeded(47.5f, Double.class));
		assertEquals(Double.class, InternalUtil.convertIfNeeded(47.5f, Double.class).getClass());
		assertEquals(47.5, InternalUtil.convertIfNeeded(47.5, Double.class));
		assertEquals(Double.class, InternalUtil.convertIfNeeded(47.5, Double.class).getClass());
		assertEquals(47.5, InternalUtil.convertIfNeeded("47.5", Double.class));
		assertEquals(Double.class, InternalUtil.convertIfNeeded("47.5", Double.class).getClass());
		
		assertEquals(13L, InternalUtil.convertIfNeeded(13, Long.class));
		assertEquals(Long.class, InternalUtil.convertIfNeeded(13, Long.class).getClass());
		assertEquals(13L, InternalUtil.convertIfNeeded(13L, Long.class));
		assertEquals(Long.class, InternalUtil.convertIfNeeded(13L, Long.class).getClass());
		assertEquals(13L, InternalUtil.convertIfNeeded(13d, Long.class));
		assertEquals(Long.class, InternalUtil.convertIfNeeded(13d, Long.class).getClass());
		assertEquals(13L, InternalUtil.convertIfNeeded(13f, Long.class));
		assertEquals(Long.class, InternalUtil.convertIfNeeded(13f, Long.class).getClass());
		assertEquals(13L, InternalUtil.convertIfNeeded("13", Long.class));
		assertEquals(Long.class, InternalUtil.convertIfNeeded("13", Long.class).getClass());
		
		assertEquals("13", InternalUtil.convertIfNeeded("13", String.class));
		assertEquals(String.class, InternalUtil.convertIfNeeded("13", String.class).getClass());
		assertEquals("13", InternalUtil.convertIfNeeded(13L, String.class));
		assertEquals(String.class, InternalUtil.convertIfNeeded(13L, String.class).getClass());
		assertEquals("13", InternalUtil.convertIfNeeded(13, String.class));
		assertEquals(String.class, InternalUtil.convertIfNeeded(13, String.class).getClass());
		assertEquals("13.0", InternalUtil.convertIfNeeded(13.0f, String.class));
		assertEquals(String.class, InternalUtil.convertIfNeeded(13.0f, String.class).getClass());
		assertEquals("class java.lang.String", InternalUtil.convertIfNeeded(String.class, String.class));
		assertEquals(String.class, InternalUtil.convertIfNeeded(String.class, String.class).getClass());
		
		assertEquals(String.class, InternalUtil.convertIfNeeded(String.class, Class.class));
		assertEquals(Class.class, InternalUtil.convertIfNeeded(String.class, Class.class).getClass());
		assertEquals(String.class, InternalUtil.convertIfNeeded("java.lang.String", Class.class));
		assertEquals(Class.class, InternalUtil.convertIfNeeded("java.lang.String", Class.class).getClass());
	}
}
