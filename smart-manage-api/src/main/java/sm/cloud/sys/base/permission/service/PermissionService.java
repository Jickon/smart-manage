package sm.cloud.sys.base.permission.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sm.cloud.sys.base.permission.domain.entity.PermissionEntity;
import sm.cloud.sys.base.permission.domain.form.PermissionListForm;
import sm.cloud.sys.base.permission.domain.form.PermissionSaveForm;
import sm.cloud.sys.base.permission.domain.form.PermissionSelectForm;
import sm.cloud.sys.base.permission.domain.vo.PermissionCreateNewDataVO;
import sm.cloud.sys.base.permission.domain.vo.PermissionDetailVO;
import sm.cloud.sys.base.permission.domain.vo.PermissionListVO;
import sm.cloud.sys.base.permission.domain.vo.PermissionSelectVO;
import sm.cloud.sys.base.permission.mapper.PermissionMapper;
import sm.system.exception.BizException;
import sm.system.response.PageResult;
import sm.system.response.ResultEnum;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PermissionService {
	private final PermissionMapper mapper;
	private final PermissionTxService txService;

	public PageResult<PermissionListVO> listPage(PermissionListForm form) {
		LambdaQueryWrapper<PermissionEntity> wrapper = new LambdaQueryWrapper<PermissionEntity>();
		wrapper.eq(form.getAppId() != null, PermissionEntity::getAppId, form.getAppId());
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String keyword = form.getKeyword().trim();
			wrapper.and(condition -> condition.like(PermissionEntity::getName, keyword)
					.or().like(PermissionEntity::getNumber, keyword));
		}
		wrapper.orderByAsc(PermissionEntity::getNumber);
		Page<PermissionEntity> result = mapper.selectPage(new Page<>(form.getPageNum(), form.getPageSize()), wrapper);
		List<PermissionListVO> records = result.getRecords().stream().map(this::toListVo).collect(Collectors.toList());
		return PageResult.of(result.getTotal(), records);
	}

	private PermissionListVO toListVo(PermissionEntity e) {
		PermissionListVO vo = new PermissionListVO();
		vo.setId(e.getId());
		vo.setName(e.getName());
		vo.setNumber(e.getNumber());
		vo.setAppId(e.getAppId());
		return vo;
	}

	/**
	 * 基础资料选择：分页查询权限。
	 * 支持按应用、关键词过滤；按编码排序。
	 */
	public PageResult<PermissionSelectVO> select(PermissionSelectForm form) {
		LambdaQueryWrapper<PermissionEntity> wrapper = new LambdaQueryWrapper<PermissionEntity>();
		wrapper.eq(form.getAppId() != null, PermissionEntity::getAppId, form.getAppId());
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String keyword = form.getKeyword().trim();
			wrapper.and(condition -> condition.like(PermissionEntity::getNumber, keyword)
					.or().like(PermissionEntity::getName, keyword));
		}
		wrapper.orderByAsc(PermissionEntity::getNumber);
		Page<PermissionEntity> result = mapper.selectPage(new Page<>(form.getPageNum(), form.getPageSize()), wrapper);
		List<PermissionSelectVO> records = result.getRecords().stream().map(this::toSelectVo).collect(Collectors.toList());
		return PageResult.of(result.getTotal(), records);
	}

	private PermissionSelectVO toSelectVo(PermissionEntity e) {
		PermissionSelectVO vo = new PermissionSelectVO();
		vo.setId(e.getId());
		vo.setNumber(e.getNumber());
		vo.setName(e.getName());
		vo.setAppId(e.getAppId());
		return vo;
	}

	/**
	 * 当前用户在指定组织下拥有的权限编码
	 */
	public List<String> getUserPermissions(Long userId, Long orgId) {
		if (userId == null || orgId == null) {
			return List.of();
		}
		return mapper.selectUserPermissionNumbers(userId, orgId, null);
	}

	/**
	 * 当前用户在指定组织下按前缀过滤的权限编码
	 */
	public List<String> getUserPermissionsByPrefix(Long userId, Long orgId, String prefix) {
		if (userId == null || orgId == null) {
			return List.of();
		}
		return mapper.selectUserPermissionNumbers(userId, orgId, prefix);
	}

	public PermissionEntity getById(Long id) {
		return mapper.selectById(id);
	}

	public PermissionDetailVO getDetail(Long id) {
		if (id == null) {
			throw new BizException(ResultEnum.PARAM_ERROR, "权限ID不能为空");
		}
		PermissionEntity entity = mapper.selectById(id);
		if (entity == null) {
			throw new BizException(ResultEnum.NOT_FOUND, "权限不存在");
		}
		return toDetailVo(entity);
	}

	private PermissionDetailVO toDetailVo(PermissionEntity e) {
		PermissionDetailVO vo = new PermissionDetailVO();
		vo.setId(String.valueOf(e.getId()));
		vo.setName(e.getName());
		vo.setNumber(e.getNumber());
		vo.setAppId(e.getAppId());
		vo.setCreateTime(e.getCreateTime());
		vo.setUpdateTime(e.getUpdateTime());
		vo.setCreateUser(e.getCreateUser());
		vo.setUpdateUser(e.getUpdateUser());
		return vo;
	}

	public PermissionCreateNewDataVO createNewData() {
		return new PermissionCreateNewDataVO();
	}

	public Long save(PermissionSaveForm form) {
		return txService.save(form);
	}

	public void deleteById(Long id) {
		txService.deleteById(id);
	}
}
