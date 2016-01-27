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
package junit.googlecode.genericdao.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.googlecode.genericdao.search.jpa.ReflectionUtil;

public class ReflectionUtilTest {

    @Test
    public void testCallMethod() throws IllegalArgumentException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        MyList myList = new MyList();
        IMyList iMyList = new MyList();
        List list = new MyList();
        String string = "";
        X x = new X();

        int result;

        result = x.foo1(myList); // ArrayList > AbstractList
        assertEquals(result, callMethod(x, "foo1", MyList.class));

        // result = x.foo1(list); //Fails
        try {
            callMethod(x, "foo1", List.class);
            fail("Should be ambiguous.");
        } catch (NoSuchMethodException e) {
        }

        result = x.foo2(myList); // AbstractList > List
        assertEquals(result, callMethod(x, "foo2", MyList.class));

        result = x.foo3(myList); // MyList > AbstractList
        assertEquals(result, callMethod(x, "foo3", MyList.class));

        result = x.foo4(myList); // IMyList > List
        //		assertEquals(result, callMethod(x, "foo4", MyList.class));

        result = x.foo4(iMyList); // IMyList > List
        //		assertEquals(result, callMethod(x, "foo4", IMyList.class));

        result = x.bar1(iMyList, iMyList);
        assertEquals(result, callMethod(x, "bar1", IMyList.class, IMyList.class));

        result = x.bar1(myList, myList);
        assertEquals(result, callMethod(x, "bar1", MyList.class, MyList.class));

        // result = x.bar2(myList, myList);
        try {
            callMethod(x, "foo1", MyList.class, MyList.class);
            fail("Should be ambiguous.");
        } catch (NoSuchMethodException e) {
        }

        // result = x.bar2(iMyList, iMyList);
        try {
            callMethod(x, "foo1", IMyList.class, IMyList.class);
            fail("Should be ambiguous.");
        } catch (NoSuchMethodException e) {
        }

        result = x.bar2(myList, list);
        assertEquals(result, callMethod(x, "bar2", MyList.class, List.class));

        result = x.bar2(iMyList, list);
        assertEquals(result, callMethod(x, "bar2", IMyList.class, List.class));

        // result = x.bar2(list, list);
        try {
            callMethod(x, "foo1", List.class, List.class);
            fail("Should be ambiguous.");
        } catch (NoSuchMethodException e) {
        }

        result = x.bar3(myList, myList);
        assertEquals(result, callMethod(x, "bar3", MyList.class, MyList.class));

        // result = x.bar4(myList, myList);
        try {
            callMethod(x, "foo1", MyList.class, MyList.class);
            fail("Should be ambiguous.");
        } catch (NoSuchMethodException e) {
        }

        result = x.a(myList); // (MyList x)
        assertEquals(result, callMethod(x, "a", MyList.class));

        result = x.a(iMyList); // (IMyList x)
        assertEquals(result, callMethod(x, "a", IMyList.class));

        result = x.a(string); // (Object x)
        assertEquals(result, callMethod(x, "a", String.class));

        // x.a(myList, string); //ambiguous
        try {
            callMethod(x, "foo1", MyList.class, String.class);
            fail("Should be ambiguous.");
        } catch (NoSuchMethodException e) {
        }

        // x.a(string, myList); //ambiguous
        try {
            callMethod(x, "foo1", String.class, MyList.class);
            fail("Should be ambiguous.");
        } catch (NoSuchMethodException e) {
        }

        result = x.a(list, list, list); // (Object x, Object y, Object z)
        assertEquals(result, callMethod(x, "a", List.class, List.class, List.class));

        // result = x.a(list, list, list, list); //ambiguous
        try {
            callMethod(x, "foo1", List.class, List.class, List.class, List.class);
            fail("Should be ambiguous.");
        } catch (NoSuchMethodException e) {
        }

        result = x.a(); // 0 ()
        assertEquals(result, callMethod(x, "a"));

        result = x.a(string, string); // (Object x, Object y)
        assertEquals(result, callMethod(x, "a", String.class, String.class));

        result = x.a(string, string, string); // (Object x, Object y, Object z)
        assertEquals(result, callMethod(x, "a", String.class, String.class, String.class));

        result = x.a(string, string, myList); // (Object x, Object y, Object z)
        assertEquals(result, callMethod(x, "a", String.class, String.class, MyList.class));

        result = x.a(string, string, list); // (Object x, Object y, Object z)
        assertEquals(result, callMethod(x, "a", String.class, String.class, List.class));

        result = x.b(list, list); // (Object x, Object y)
        assertEquals(result, callMethod(x, "b", List.class, List.class));

        result = x.b(list); // (List x, List... y)
        //we're not doing this case. It's too complicated. we'll always fail if multiple var-args match and only var-args
        //assertEquals(result, callMethod(x, "b", List.class));

        result = x.b(list, list, list); // (List x, List... y)
        //we're not doing this case. It's too complicated. we'll always fail if multiple var-args match and only var-args
        //assertEquals(result, callMethod(x, "b", List.class, List.class, List.class));

        result = x.b(list, list, string); // (List x, Object... y)
        assertEquals(result, callMethod(x, "b", List.class, List.class, String.class));

        result = x.b(string, list, list); // (Object x, List... y)
        //we're not doing this case. It's too complicated. we'll always fail if multiple var-args match and only var-args
        //assertEquals(result, callMethod(x, "b", String.class, List.class, List.class));

        result = x.c(list, list); // (List x, List... y)
        //we're not doing this case. It's too complicated. we'll always fail if multiple var-args match and only var-args
        //assertEquals(result, callMethod(x, "c", List.class, List.class));

        result = x.c(myList, myList); // List x, ArrayList... y)
        //we're not doing this case. It's too complicated. we'll always fail if multiple var-args match and only var-args
        //assertEquals(result, callMethod(x, "c", MyList.class, MyList.class));

        result = x.c(string, myList); // (Object... x)
        assertEquals(result, callMethod(x, "c", String.class, MyList.class));

        result = x.c(list, myList, list); // (List x, List... y)
        //we're not doing this case. It's too complicated. we'll always fail if multiple var-args match and only var-args
        //assertEquals(result, callMethod(x, "c", List.class, MyList.class, List.class));

        result = x.c(list, myList, myList); // (List x, ArrayList... y)
        //we're not doing this case. It's too complicated. we'll always fail if multiple var-args match and only var-args
        //assertEquals(result, callMethod(x, "c", List.class, MyList.class, MyList.class));

        result = x.d(myList, myList); // (List x, ArrayList... y)
        //we're not doing this case. It's too complicated. we'll always fail if multiple var-args match and only var-args
        //assertEquals(result, callMethod(x, "d", MyList.class, MyList.class));

        result = x.d(myList, myList, myList); // (List x, ArrayList... y)
        //we're not doing this case. It's too complicated. we'll always fail if multiple var-args match and only var-args
        //assertEquals(result, callMethod(x, "d", MyList.class, MyList.class, MyList.class));

        result = x.d(myList, myList, myList); // (List x, ArrayList... y)
        //we're not doing this case. It's too complicated. we'll always fail if multiple var-args match and only var-args
        //assertEquals(result, callMethod(x, "d", MyList.class, MyList.class, MyList.class));

        // -- Test with arrays as var-arg
        result = x.a(new Object[0]); //22
        assertEquals(result, callMethod(x, "a", Object[].class));

        result = x.a(new String[0]); //22
        assertEquals(result, callMethod(x, "a", String[].class));

        result = x.a(list, new Object[0]); //23
        assertEquals(result, callMethod(x, "a", List.class, Object[].class));

        result = x.a(list, new String[0]); //23
        assertEquals(result, callMethod(x, "a", List.class, String[].class));

        result = x.a(list, new List[0]); //24
        assertEquals(result, callMethod(x, "a", List.class, List[].class));

        result = x.a(list, new MyList[0]); //24
        assertEquals(result, callMethod(x, "a", List.class, MyList[].class));

        result = x.a(list, new IMyList[0]); //24
        assertEquals(result, callMethod(x, "a", List.class, IMyList[].class));

    }

    public static class X {

        public int foo1(ArrayList x) {
            return 1;
        }

        public int foo1(AbstractList x) {
            return 2;
        }

        public int foo2(List x) {
            return 3;
        }

        public int foo2(AbstractList x) {
            return 4;
        }

        public int foo3(MyList x) {
            return 5;
        }

        public int foo3(AbstractList x) {
            return 6;
        }

        public int foo4(IMyList x) {
            return 7;
        }

        public int foo4(List x) {
            return 8;
        }

        public int bar1(IMyList x, List y) {
            return 10;
        }

        public int bar1(IMyList x, IMyList y) {
            return 11;
        }

        public int bar2(IMyList x, List y) {
            return 12;
        }

        public int bar2(List x, IMyList y) {
            return 13;
        }

        public int bar3(ArrayList x, AbstractList y) {
            return 14;
        }

        public int bar3(ArrayList x, ArrayList y) {
            return 15;
        }

        public int bar4(ArrayList x, AbstractList y) {
            return 16;
        }

        public int bar4(List x, ArrayList y) {
            return 17;
        }

        public int a() {
            return 20;
        }

        public int a(Object x) {
            return 21;
        }

        public int a(Object... x) {
            return 22;
        }

        public int a(Object x, Object... y) {
            return 23;
        }

        public int a(Object x, List... y) {
            return 24;
        }

        public int a(MyList x) {
            return 25;
        }

        public int a(IMyList x) {
            return 26;
        }

        public int a(List x) {
            return 27;
        }

        public int a(ArrayList x) {
            return 28;
        }

        public int a(Object x, Object y) {
            return 29;
        }

        public int a(Object x, Object y, Object z) {
            return 30;
        }

        public int b(List x, Object... y) {
            return 40;
        }

        public int b(Object x, Object y) {
            return 41;
        }

        public int b(List x, List... y) {
            return 42;
        }

        public int b(Object x, List... y) {
            return 43;
        }

        public int c(List x, List... y) {
            return 50;
        }

        public int c(Object... x) {
            return 51;
        }

        public int c(List x, ArrayList... y) {
            return 52;
        }

        public int d(MyList x, Object y, Object... z) {
            return 60;
        }

        public int d(MyList x, MyList... y) {
            return 61;
        }

        public int d(MyList x, Object y, MyList... z) {
            return 62;
        }

        // public int d(MyList x, MyList y, MyList... z) {
        // return 63;
        // }

    }

    @SuppressWarnings("rawtypes")
    private static class MyList extends ArrayList implements IMyList {
        public int compareTo(Object o) {
            return 0;
        }
    }

    @SuppressWarnings("rawtypes")
    private static interface IMyList extends List, Comparable {
    }

    private static Object callMethod(Object object, String methodName, Class<?>... paramTypes)
            throws IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return ReflectionUtil.callMethod(object, methodName, paramTypes, new Object[paramTypes.length]);
    }

}
