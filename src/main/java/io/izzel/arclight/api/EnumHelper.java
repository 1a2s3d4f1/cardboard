package io.izzel.arclight.api;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EnumHelper {

    /**
     * JavaC stores the field differently than EJC
     * @author CardboardMod
     */
    private static Field getValuesField(Class<?> cl) throws NoSuchFieldException, SecurityException {
        try {
            return cl.getDeclaredField("ENUM$VALUES");// EJC
        } catch (NoSuchFieldException | SecurityException e) {
            return cl.getDeclaredField("$VALUES");    // JavaC
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T addEnum(Class<T> cl, String name, List<Class<?>> ctorTypes, List<Object> ctorParams) {
        try {
            Unsafe.ensureClassInitialized(cl);
            Field field = getValuesField(cl);
            Object base = Unsafe.staticFieldBase(field);
            long offset = Unsafe.staticFieldOffset(field);
            T[] arr = (T[]) Unsafe.getObject(base, offset);
            T[] newArr = (T[]) Array.newInstance(cl, arr.length + 1);
            System.arraycopy(arr, 0, newArr, 0, arr.length);

            T newInstance = makeEnum(cl, name, arr.length, ctorTypes, ctorParams);

            newArr[arr.length] = newInstance;
            Unsafe.putObject(base, offset, newArr);
            reset(cl);
            return newInstance;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> void addEnums(Class<T> cl, List<T> list) {
        try {
            Field field = getValuesField(cl);
            Object base = Unsafe.staticFieldBase(field);
            long offset = Unsafe.staticFieldOffset(field);
            T[] arr = (T[]) Unsafe.getObject(base, offset);
            T[] newArr = (T[]) Array.newInstance(cl, arr.length + list.size());
            System.arraycopy(arr, 0, newArr, 0, arr.length);
            for (int i = 0; i < list.size(); i++) {
                newArr[arr.length + i] = list.get(i);
            }
            Unsafe.putObject(base, offset, newArr);
            reset(cl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T makeEnum(Class<T> cl, String name, int i, List<Class<?>> ctorTypes, List<Object> ctorParams) {
        try {
            Unsafe.ensureClassInitialized(cl);
            List<Class<?>> ctor = new ArrayList<>(ctorTypes.size() + 2);
            ctor.add(String.class);
            ctor.add(int.class);
            ctor.addAll(ctorTypes);
            MethodHandle constructor = Unsafe.lookup().findConstructor(cl, MethodType.methodType(void.class, ctor));
            List<Object> param = new ArrayList<>(ctorParams.size() + 2);
            param.add(name);
            param.add(i);
            param.addAll(ctorParams);
            return (T) constructor.invokeWithArguments(param);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    private static long enumConstantDirectoryOffset;
    private static long enumConstantsOffset;
    private static long enumVarsOffset

    static {
       if (System.getProperty("java.vm.name").contains("OpenJ9")) {
       try {
             Field enumVars = Class.class.getDeclaredField("enumVars");
             enumVarsOffset = Unsafe.objectFieldOffset(enumVars);
         } catch (NoSuchFieldException e) {
             throw new IllegalStateException(e);
         }
       } else {
        try {
            Field enumConstantDirectory = Class.class.getDeclaredField("enumConstantDirectory");
            Field enumConstants = Class.class.getDeclaredField("enumConstants");
            enumConstantDirectoryOffset = Unsafe.objectFieldOffset(enumConstantDirectory);
            enumConstantsOffset = Unsafe.objectFieldOffset(enumConstants);
        } catch (NoSuchFieldException e) {
            System.out.println("Cardboard currently does not work with Eclipse OpenJ9");
            throw new IllegalStateException(e);
        }
      }
    }

    private static void reset(Class<?> cl) {
      if (System.getProperty("java.vm.name").contains("OpenJ9")) {
        Unsafe.putObject(cl, enumVarsOffset, null);
      } else {
        Unsafe.putObject(cl, enumConstantDirectoryOffset, null);
        Unsafe.putObject(cl, enumConstantsOffset, null);
    }
  }
}
