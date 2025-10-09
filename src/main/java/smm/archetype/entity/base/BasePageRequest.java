package smm.archetype.entity.base;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.PageRequest;

/**
 * @author Leonardo
 * @since 2025/7/14
 * 基础分页请求对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BasePageRequest extends BaseRequest {
    
    /**
     * 当前页
     */
    private int pageNo;
    
    /**
     * 页大小
     */
    private int pageSize;
    
    /**
     * 将当前对象转换为Spring Data JPA的分页请求对象
     * 此方法主要用于将当前对象中包含的分页信息转换为JPA可以理解和处理的格式
     *
     * @return org.springframework.data.domain.BasePageRequest 返回Spring Data JPA的分页请求对象，包含了页码和页面大小的信息
     */
    public PageRequest toJpaType() {
        return PageRequest.of(pageNo - 1, pageSize);
    }
    
}
