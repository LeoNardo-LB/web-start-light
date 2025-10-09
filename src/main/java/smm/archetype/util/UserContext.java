package smm.archetype.util;

/**
 * @author Leonardo
 * @since 2025/7/15
 * 通用用户上下文
 */
public class UserContext {
    
    /**
     * 获取用户id
     *
     * @return 用户id, 默认为system
     */
    public static String getId() {
        return "system";
    }
    
}
