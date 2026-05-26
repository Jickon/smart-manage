package sm.system.form;

import lombok.Data;

/**
 * 基础分页参数类
 *
 * @author Chekfu
 */
@Data
public class PageForm {
	// 当前页码，默认第一页
	private Integer pageNum = 1;
	// 每页数量，默认10条
	private Integer pageSize = 10;

	public Integer getPageNum() {
		return pageNum == null || pageNum < 1 ? 1 : pageNum;
	}

	public Integer getPageSize() {
		if (pageSize == null || pageSize < 1) {
			return 10;
		}
		// 限制最大100条
		return pageSize > 100 ? 100 : pageSize;
	}
}
