package java/person/zhou/views.tools;

/**
 * 接口．
 * 使用可以被销毁的标记．
 * 用于多重关联的对象的清理．
 *
 * Created by zhou on 12/12/14.
 */
public interface Destroyable{

    /**
     * 使用在清理时的回调．
     * 手动清理一些数据．
     */
    void onCallDestroy();
}
