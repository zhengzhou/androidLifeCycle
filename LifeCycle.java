package java.person.zhou.views.tools;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * LifeCycle.标记字段是在生命周期内的.销毁时候需要清理.
 * <h4>Sample Code</h4>
 * <p>
 * <pre>
 * \@LifeCycle private Context mContext;
 * \@LifeCycle private List data;
 *
 * 在base类中的onDestroy则会实现类似效果:
 * <code>
 *     mContext = null;
 *     data.clear();
 *     data = null;
 * </code>
 * </pre>
 * </p>
 * Created by zhou on 12/12/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface LifeCycle {
}
