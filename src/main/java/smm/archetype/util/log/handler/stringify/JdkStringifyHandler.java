package smm.archetype.util.log.handler.stringify;

import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author Leonardo
 * @since 2025/7/15
 * JDK默认序列化
 */
@Component
public class JdkStringifyHandler implements StringifyHandler {
    
    @Override
    public StringifyType getStringifyType() {
        return StringifyType.JDK;
    }
    
    @Override
    public String stringify(Object target) {
        if (target == null) {
            return "null";
        }
        if (target instanceof Iterable<?> || target.getClass().isArray()) {
            List<String> outputStrings = new ArrayList<>();
            if (target.getClass().isArray()) {
                int length = Array.getLength(target);
                for (int i = 0; i < length; i++) {
                    outputStrings.add(this.stringify(Array.get(target, i)));
                }
            } else {
                assert target instanceof Iterable<?>;
                for (Object object : (Iterable<?>) target) {
                    outputStrings.add(this.stringify(object));
                }
            }
            StringJoiner joiner = new StringJoiner(",");
            outputStrings.forEach(joiner::add);
            return "[" + joiner + "]";
        }
        return target.toString();
    }
    
}
