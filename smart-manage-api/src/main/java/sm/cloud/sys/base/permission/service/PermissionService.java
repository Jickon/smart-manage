package sm.cloud.sys.base.permission.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.permission.domain.entity.PermissionEntity;
import sm.cloud.sys.base.permission.domain.entity.table.PermissionTable;
import sm.cloud.sys.base.permission.domain.form.PermissionListForm;
import sm.cloud.sys.base.permission.domain.form.PermissionSaveForm;
import sm.cloud.sys.base.permission.domain.form.PermissionSelectForm;
import sm.cloud.sys.base.permission.domain.vo.PermissionCreateNewDataVO;
import sm.cloud.sys.base.permission.domain.vo.PermissionDetailVO;
import sm.cloud.sys.base.permission.domain.vo.PermissionListVO;
import sm.cloud.sys.base.permission.domain.vo.PermissionSelectVO;
import sm.cloud.sys.base.permission.mapper.PermissionMapper;
import sm.cloud.sys.base.roleperms.domain.entity.table.RolePermsTable;
import sm.cloud.sys.base.userrole.domain.entity.table.UserRoleTable;
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

	public PageResult<PermissionListVO> listPage(PermissionListForm form) {
		QueryWrapper qw = QueryWrapper.create().from(PermissionTable.PERMISSION);
		if (form.getAppId() != null) {
			qw.and(PermissionTable.PERMISSION.APP_ID.eq(form.getAppId()));
		}
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String kw = "%" + form.getKeyword().trim() + "%";
			qw.and(PermissionTable.PERMISSION.NAME.like(kw).or(PermissionTable.PERMISSION.NUMBER.like(kw)));
		}
		qw.orderBy(PermissionTable.PERMISSION.NUMBER, true);
		Page<PermissionEntity> page = Page.of(form.getPageNum(), form.getPageSize());
		Page<PermissionEntity> result = mapper.paginate(page, qw);
		List<PermissionListVO> vos = result.getRecords().stream().map(this::toListVo).collect(Collectors.toList());
		return PageResult.of(result.getTotalRow(), vos);
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
		QueryWrapper qw = QueryWrapper.create().from(PermissionTable.PERMISSION);
		if (form.getAppId() != null) {
			qw.and(PermissionTable.PERMISSION.APP_ID.eq(form.getAppId()));
		}
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String kw = "%" + form.getKeyword().trim() + "%";
			qw.and(PermissionTable.PERMISSION.NUMBER.like(kw).or(PermissionTable.PERMISSION.NAME.like(kw)));
		}
		qw.orderBy(PermissionTable.PERMISSION.NUMBER, true);
		Page<PermissionEntity> page = Page.of(form.getPageNum(), form.getPageSize());
		Page<PermissionEntity> result = mapper.paginate(page, qw);
		List<PermissionSelectVO> vos = result.getRecords().stream().map(this::toSelectVo).collect(Collectors.toList());
		return PageResult.of(result.getTotalRow(), vos);
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
		QueryWrapper query = QueryWrapper.create()
				.select(PermissionTable.PERMISSION.NUMBER)
				.from(PermissionTable.PERMISSION)
				.innerJoin(RolePermsTable.ROLE_PERMS).on(RolePermsTable.ROLE_PERMS.PERMISSION_ID.eq(PermissionTable.PERMISSION.ID))
				.innerJoin(UserRoleTable.USER_ROLE).on(UserRoleTable.USER_ROLE.ROLE_ID.eq(RolePermsTable.ROLE_PERMS.ROLE_ID))
				.where(UserRoleTable.USER_ROLE.USER_ID.eq(userId))
				.and(UserRoleTable.USER_ROLE.ORG_ID.eq(orgId));
		List<Row> rows = Db.selectListByQuery(query);
		return rows.stream().map(r -> r.getString(PermissionTable.PERMISSION.NUMBER.getName())).filter(s -> s != null && !s.isEmpty()).distinct().toList();
	}

	/**
	 * 当前用户在指定组织下按前缀过滤的权限编码
	 */
	public List<String> getUserPermissionsByPrefix(Long userId, Long orgId, String prefix) {
		if (userId == null || orgId == null) {
			return List.of();
		}
		QueryWrapper query = QueryWrapper.create()
				.select(PermissionTable.PERMISSION.NUMBER)
				.from(PermissionTable.PERMISSION)
				.innerJoin(RolePermsTable.ROLE_PERMS).on(RolePermsTable.ROLE_PERMS.PERMISSION_ID.eq(PermissionTable.PERMISSION.ID))
				.innerJoin(UserRoleTable.USER_ROLE).on(UserRoleTable.USER_ROLE.ROLE_ID.eq(RolePermsTable.ROLE_PERMS.ROLE_ID))
				.where(UserRoleTable.USER_ROLE.USER_ID.eq(userId))
				.and(UserRoleTable.USER_ROLE.ORG_ID.eq(orgId))
				.and(PermissionTable.PERMISSION.NUMBER.like(prefix + "%"));
		List<Row> rows = Db.selectListByQuery(query);
		return rows.stream().map(r -> r.getString(PermissionTable.PERMISSION.NUMBER.getName())).filter(s -> s != null && !s.isEmpty()).distinct().toList();
	}

	public PermissionEntity getById(Long id) {
		return mapper.selectOneById(id);
	}

	public PermissionDetailVO getDetail(Long id) {
		if (id == null) {
			throw new BizException(ResultEnum.PARAM_ERROR, "权限ID不能为空");
		}
		PermissionEntity entity = mapper.selectOneById(id);
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

	@Transactional(rollbackFor = Exception.class)
	public Long save(PermissionSaveForm form) {
		PermissionEntity e;
		if (form.getId() != null) {
			e = mapper.selectOneById(form.getId());
			if (e == null) {
				throw new BizException(ResultEnum.NOT_FOUND, "权限不存在");
			}
		} else {
			e = new PermissionEntity();
		}
		e.setName(form.getName());
		e.setNumber(form.getNumber());
		e.setAppId(form.getAppId());
		if (form.getId() == null) {
			mapper.insert(e);
		} else {
			mapper.update(e);
		}
		return e.getId();
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteById(Long id) {
		if (id == null) {
			throw new BizException(ResultEnum.PARAM_ERROR, "权限ID不能为空");
		}
		PermissionEntity entity = mapper.selectOneById(id);
		if (entity == null) {
			throw new BizException(ResultEnum.NOT_FOUND, "权限不存在");
		}
		mapper.deleteById(id);
	}
}
