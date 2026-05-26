package sm.system.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Chekfu
 */
@Data
public class BaseEntity implements Serializable {

	/*
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/*
	 * 更新时间
	 */
	private LocalDateTime updateTime;
	/**
	 * 创建人
	 */
	private Long createUser;
	/**
	 * 修改人
	 */
	private Long updateUser;
}
