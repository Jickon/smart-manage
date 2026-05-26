package sm.system.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 通用分页结果类
 *
 * @author Chekfu
 */
@Data
@NoArgsConstructor
public class PageResult<T> {
	// 总记录数
	private long total;
	// 当前页数据列表
	private List<T> records;

	public PageResult(long total, List<T> records) {
		this.total = total;
		this.records = records;
	}

	public static <T> PageResult<T> of(long total, List<T> records) {
		return new PageResult<>(total, records);
	}
}
