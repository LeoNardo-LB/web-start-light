package smm.archetype.entity.api;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Page;
import smm.archetype.entity.enums.ResultCode;

import java.util.List;

/**
 * @param <T> 结果类型
 * @author Leonardo
 * @since 2025/7/14
 * 分页结果
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BasePageResult<T> extends BaseResult<List<T>> {
    
    /**
     * 总数
     */
    private long total;
    
    /**
     * 当前页
     */
    private int pageNo;
    
    /**
     * 页大小
     */
    private int pageSize;
    
    /**
     * 从 jpa 分页结果转换成 BasePageResult
     *
     * @param page jpa 分页结果
     * @param <T>  泛型
     * @return BasePageResult
     */
    public static <T> BasePageResult<T> fromPage(Page<T> page) {
        BasePageResult<T> BasePageResult = new BasePageResult<>();
        BasePageResult.setTotal(page.getTotalElements());
        BasePageResult.setPageNo(page.getNumber() + 1);
        BasePageResult.setPageSize(page.getSize());
        BasePageResult.setCode(ResultCode.SUCCESS.getCode());
        BasePageResult.setMessage(ResultCode.SUCCESS.getMessage());
        BasePageResult.setData(page.getContent());
        return BasePageResult;
    }
    
}
