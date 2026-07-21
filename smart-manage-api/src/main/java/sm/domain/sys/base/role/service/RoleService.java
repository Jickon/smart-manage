package sm.domain.sys.base.role.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sm.domain.sys.base.role.model.entity.RoleEntity;
import sm.domain.sys.base.role.model.form.RoleListForm;
import sm.domain.sys.base.role.model.form.RoleSaveForm;
import sm.domain.sys.base.role.model.form.RolePermissionAssignForm;
import sm.domain.sys.base.role.model.form.RoleSelectForm;
import sm.domain.sys.base.role.model.vo.RoleCreateNewDataVO;
import sm.domain.sys.base.role.model.vo.RoleDetailVO;
import sm.domain.sys.base.role.model.vo.RoleListVO;
import sm.domain.sys.base.role.model.vo.RoleSelectVO;
import sm.domain.sys.base.role.mapper.RoleMapper;
import sm.domain.sys.base.role.mapper.RolePermissionMapper;
import sm.domain.sys.base.role.model.entity.RolePermissionEntity;
import sm.system.exception.BizException;
import sm.system.aop.log.BizLog;
import sm.system.response.PageData;
import sm.system.response.ResultEnum;
import sm.domain.sys.base.common.helper.AuthorizationStateHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RoleService {
	private final RoleMapper mapper;
	private final RolePermissionMapper permissionMapper;
	private final RoleTxService txService;
	private final AuthorizationStateHelper authorizationStateHelper;

	public PageData<RoleListVO> listPage(RoleListForm form) {
		LambdaQueryWrapper<RoleEntity> qw = new LambdaQueryWrapper<RoleEntity>()
				.orderByAsc(RoleEntity::getId);
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String kw = form.getKeyword().trim();
			qw.and(condition -> condition.like(RoleEntity::getName, kw).or().like(RoleEntity::getNumber, kw));
		}
		Page<RoleEntity> page = new Page<>(form.getPageNum(), form.getPageSize());
		Page<RoleEntity> result = mapper.selectPage(page, qw);
		var vos = result.getRecords().stream().map(this::toRoleListVO).collect(Collectors.toList());
		return PageData.of(result.getTotal(), form.getPageNum(), form.getPageSize(), vos);
	}

	/**
	 * 用户角色分配需要一次性展示全部角色，仅返回选择所需的轻量字段。
	 */
	public List<RoleSelectVO> listAll() {
		return mapper.selectList(new LambdaQueryWrapper<RoleEntity>()
				.orderByAsc(RoleEntity::getNumber)
				.orderByAsc(RoleEntity::getId))
				.stream()
				.map(this::toRoleSelectVO)
				.toList();
	}

	/**
	 * 基础资料选择：分页查询角色。
	 */
	public PageData<RoleSelectVO> select(RoleSelectForm form) {
		LambdaQueryWrapper<RoleEntity> qw = new LambdaQueryWrapper<RoleEntity>()
				.orderByAsc(RoleEntity::getId);
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String kw = form.getKeyword().trim();
			qw.and(condition -> condition.like(RoleEntity::getName, kw).or().like(RoleEntity::getNumber, kw));
		}
		Page<RoleEntity> page = new Page<>(form.getPageNum(), form.getPageSize());
		Page<RoleEntity> result = mapper.selectPage(page, qw);
		List<RoleSelectVO> voList = result.getRecords().stream().map(this::toRoleSelectVO).collect(Collectors.toList());
		return PageData.of(result.getTotal(), form.getPageNum(), form.getPageSize(), voList);
	}

	private RoleSelectVO toRoleSelectVO(RoleEntity e) {
		RoleSelectVO vo = new RoleSelectVO();
		vo.setId(e.getId());
		vo.setNumber(e.getNumber());
		vo.setName(e.getName());
		return vo;
	}

	private RoleListVO toRoleListVO(RoleEntity e) {
		RoleListVO vo = new RoleListVO();
		vo.setId(e.getId());
		vo.setName(e.getName());
		vo.setNumber(e.getNumber());
		return vo;
	}

	public RoleEntity getById(Long id) {
		return mapper.selectById(id);
	}

	public RoleDetailVO getDetail(Long id) {
		if (id == null) {
			throw new BizException(ResultEnum.PARAM_ERROR, "角色ID不能为空");
		}
		RoleEntity entity = mapper.selectById(id);
		if (entity == null) {
			throw new BizException(ResultEnum.NOT_FOUND, "角色不存在");
		}
		return toDetailVo(entity);
	}

	private RoleDetailVO toDetailVo(RoleEntity entity) {
		RoleDetailVO vo = new RoleDetailVO();
		vo.setId(String.valueOf(entity.getId()));
		vo.setName(entity.getName());
		vo.setNumber(entity.getNumber());
		vo.setCreateTime(entity.getCreateTime());
		vo.setUpdateTime(entity.getUpdateTime());
		vo.setCreateUser(entity.getCreateUser());
		vo.setUpdateUser(entity.getUpdateUser());
		vo.setVersion(entity.getVersion());
		vo.setPermissionIds(permissionMapper.selectList(new LambdaQueryWrapper<RolePermissionEntity>()
					.select(RolePermissionEntity::getPermissionId)
					.eq(RolePermissionEntity::getRoleId, entity.getId()))
				.stream()
				.map(RolePermissionEntity::getPermissionId)
				.toList());
		return vo;
	}

	public RoleCreateNewDataVO createNewData() {
		return new RoleCreateNewDataVO();
	}

	@BizLog("保存角色")
	public Long save(RoleSaveForm form) {
		return txService.save(form);
	}

	@BizLog("删除角色")
	public void deleteById(Long id) {
		txService.deleteById(id);
	}

	@BizLog("分配角色权限")
	public void assignPermissions(RolePermissionAssignForm form) {
		txService.assignPermissions(form);
		authorizationStateHelper.invalidateRoleUsers(form.getRoleId());
	}
}
