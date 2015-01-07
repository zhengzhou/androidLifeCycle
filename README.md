# androidLifeCycle
a little tool to clear object reference

##功能
　清理各个对象的引用，清理bitmap资源，简化操作．
##需要使用的类
- GarbageCollector．主要工具类，一个公开的静态方法．destroy
- Destroyable.　　　 接口，实现该接口的对象可以回收他的依赖
- LifeCycle　　　　　注解，标注可以回收的属性，指该属性只存活在对象的生命周期内

##使用方法
1. 声明可被回收的类,activity,fragment不需要
        public class SomeClass implement Destroyable
2. 标注可以回收的属性
        @LifeCycle Object[] someFields;
        @LifeCycle Destroyable someReferences;
3. 回调回收事件．
 - activity:
         void onDestory(){
             GarbageCollector.destory(this)
         }
 - Destroyable:
         public void onCallDestroy() {
             GarbageCollector.destroy(this);
         }

##过程
1. 触发源头： 
 - activity ondestory时候．
 - 手动调用（少用）
2. 传递，GarbageCollector中会找到Destroyable接口的对象，递归调用．

## 注意，问题
- 不要过分的清理和置空对象，防止空指针．图片会出现使用已回收图片的的错误．
- 对象回收又重新创建时候要检查是否正在清理中，
