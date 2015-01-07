package java.person.zhou.views.tools;

import android.graphics.drawable.Drawable;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * <h3>
 * 在组件销毁的时候清理带有LifeCycle注解的字段.
 * </h3>
 * <p>
 * 目前实现的是数组和集合的clear．普通对象置空.<br/>
 * <strong>todo drawable.bitmap...
 * </strong><br/>
 * 根据需求可以实现＠Destroyable接口，手动清理．
 * </p>
 * 　<ps>使用memory monitor监测内存释放效果．<ps/>
 * <p/>
 * <h3>在activity,Fragment中使用：<h3/>
 * <pre>
 *     public class SomeActivity {
 *
 *         /@LifeCycle Object someField;
 *         /@LifeCycle Object[] someFields;
 *         /@LifeCycle Destroyable someReferences;
 *
 *         void onDestroy(){
 *             GarbageCollector.destroy(this)
 *         }
 *     }
 * </pre>
 * <h3>在一般类中使用：<h3/>
 * <pre>
 *     public class SomeClass implement Destroyable{
 *
 *         /@LifeCycle Object someFields;
 *         /@LifeCycle Destroyable someReferences;
 *
 *          //这个对象的清理依赖关联他的对象的清理．
 *         void onCallDestroy(){
 *             GarbageCollector.destroy(this)
 *         }
 *     }
 * </pre>
 * <br/>
 * Created by zhou on 12/12/14.
 */
public class GarbageCollector {

    public static final String TAG = "GarbageCollector";

    /**
     * 清除对象的引用．
     * @param object　目标对象．
     */
    public static void destroy(Object object) {
        try {
            destroyField(object);
        } catch (ClassCastException e) {
            Logger.e("ClassCastException.");
        } catch (IllegalAccessException e){
            Logger.e("field cannot be access");
        }
    }

    /**
     * 清除对象的引用．
     * 这个可以清除对象在父类中声明的一些属性．
     * @param object 目标对象
     * @param clazz 类型，这个可以清除对象在父类中声明的一些属性．
     */
    public static void destroy(Object object, Class clazz) {
        try {
            destroyField(object, clazz);
        } catch (ClassCastException e) {
            Logger.e("ClassCastException.");
        } catch (IllegalAccessException e){
            Logger.e("field cannot be access");
        }
    }


    /**
     * 默认处理本类声明的属性
     *
     * @param object 目标对象．
     * @throws IllegalAccessException
     */
    private static void destroyField(Object object) throws IllegalAccessException {
        destroyField(object, object.getClass());
    }

    /**
     * 执行清理操作.
     *
     * @param object 目标对象．
     * @param clazz  　类型，用于找需要清理的属性．
     * @throws IllegalAccessException
     */
    private static void destroyField(Object object, Class clazz) throws IllegalAccessException {
        if (object != null) {
            if (Logger.Debug()) {
                Logger.i(TAG, "begin destroy:" + clazz.getName());
            }
            final Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                final LifeCycle cycledField = field.getAnnotation(LifeCycle.class);
                if (cycledField != null) {
                    if (Logger.Debug()) {
                        Logger.i(TAG, "found field[" + field.getName() + "] to be destroy");
                    }
                    final Class fieldClass = field.getType();
                    //基本数据类型忽略
                    if (fieldClass.isPrimitive()) {
                        continue;
                    }
                    field.setAccessible(true);
                    final Object fieldObj = field.get(object);
                    if (fieldObj == null) {
                        //已经清理了．
                        continue;
                    }

                    //开始清理．
                    if (Destroyable.class.isInstance(fieldObj)) {
                        ((Destroyable) fieldObj).onCallDestroy();
                    }
                    clearArray(fieldClass, fieldObj);
                    clearCollection(fieldClass, fieldObj);
                    if (fieldClass.isAssignableFrom(Drawable.class)) {
                        //todo 图片类型．bitmap清理．
                        Logger.i(TAG, "clear 图片数据.");
                    }

                    //最后置空．
                    field.set(object, null);
                }
            }

        }
    }

    /**
     * 清理集合．
     *
     * @param fieldClass 　字段类型
     * @param fieldObj   　字段对象．
     */
    private static void clearCollection(Class<?> fieldClass, Object fieldObj) {
        if (fieldClass.isAssignableFrom(Collection.class)) {
            //list,map类型．
            Collection collection = (Collection) fieldObj;
            collection.clear();
            Logger.i(TAG, "clear the collection.");
        }
    }

    /**
     * 清理数组
     *
     * @param fieldClass 　字段类型
     * @param fieldObj   　字段对象．
     */
    private static void clearArray(Class<?> fieldClass, Object fieldObj) {
        if (fieldClass.isArray()) {
            //清空数组
            Object[] data = (Object[]) fieldObj;
            //XXX 是否要深度清理？？　todo 有待生产环境中监测
            if (data != null) {
                for (Object o : data) {
                    if (Destroyable.class.isInstance(o)) {
                        ((Destroyable) o).onCallDestroy();
                    }
                }
            }
            Logger.i(TAG, "clear the data[].");
        }
    }
}
