package sm.cloud.sys.base.role.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.role.domain.entity.RoleEntity;
import sm.cloud.sys.base.role.domain.entity.table.RoleTable;
import sm.cloud.sys.base.role.domain.form.RoleListForm;
import sm.cloud.sys.base.role.domain.form.RoleSaveForm;
import sm.cloud.sys.base.role.domain.form.RoleSelectForm;
import sm.cloud.sys.base.role.domain.vo.RoleCreateNewDataVO;
import sm.cloud.sys.base.role.domain.vo.RoleDetailVO;
import sm.cloud.sys.base.role.domain.vo.RoleListVO;
import sm.cloud.sys.base.role.domain.vo.RoleSelectVO;
import sm.cloud.sys.base.role.mapper.RoleMapper;
import sm.system.exception.BizException;
import sm.system.response.PageResult;
import sm.system.response.ResultEnum;

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

	public PageResult<RoleListVO> listPage(RoleListForm form) {
		QueryWrapper qw = QueryWrapper.create()
				.from(RoleTable.ROLE)
				.orderBy(RoleTable.ROLE.ID, true);
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String kw = "%" + form.getKeyword().trim() + "%";
			qw.and(RoleTable.ROLE.NAME.like(kw).or(RoleTable.ROLE.NUMBER.like(kw)));
		}
		Page<RoleEntity> page = Page.of(form.getPageNum(), form.getPageSize());
		Page<RoleEntity> result = mapper.paginate(page, qw);
		var vos = result.getRecords().stream().map(this::toRoleListVO).collect(Collectors.toList());
		return PageResult.of(result.getTotalRow(), vos);
	}

	/**
	 * 基础资料选择：分页查询角色。
	 */
	public PageResult<RoleSelectVO> select(RoleSelectForm form) {
		QueryWrapper qw = QueryWrapper.create()
				.from(RoleTable.ROLE)
				.orderBy(RoleTable.ROLE.ID, true);
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String kw = "%" + form.getKeyword().trim() + "%";
			qw.and(RoleTable.ROLE.NUMBER.like(kw).or(RoleTable.ROLE.NAME.like(kw)));
		}
		Page<RoleEntity> page = Page.of(form.getPageNum(), form.getPageSize());
		Page<RoleEntity> result = mapper.paginate(page, qw);
		List<RoleSelectVO> vos = result.getRecords().stream().map(this::toRoleSelectVO).collect(Collectors.toList());
		return PageResult.of(result.getTotalRow(), vos);
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
		return mapper.selectOneById(id);
	}

	public RoleDetailVO getDetail(Long id) {
		if (id == null) {
			throw new BizException(ResultEnum.PARAM_ERROR, "角色ID不能为空");
		}
		RoleEntity entity = mapper.selectOneById(id);
		if (entity == null) {
			throw new BizException(ResultEnum.NOT_FOUND, "角色不存在");
		}
		return toDetailVo(entity);
	}

	private RoleDetailVO toDetailVo(RoleEntity e) {
		RoleDetailVO vo = new RoleDetailVO();
		vo.setId(String.valueOf(e.getId()));
		vo.setName(e.getName());
		vo.setNumber(e.getNumber());
		vo.setCreateTime(e.getCreateTime());
		vo.setUpdateTime(e.getUpdateTime());
		vo.setCreateUser(e.getCreateUser());
		vo.setUpdateUser(e.getUpdateUser());
		return vo;
	}

	public RoleCreateNewDataVO createNewData() {
		return new RoleCreateNewDataVO();
	}

	@Transactional(rollbackFor = Exception.class)
	public Long save(RoleSaveForm form) {
		// 检查角色编码唯一性
		QueryWrapper checkWrapper = QueryWrapper.create()
				.from(RoleTable.ROLE)
				.where(RoleTable.ROLE.NUMBER.eq(form.getNumber()));
		if (form.getId() != null) {
			checkWrapper.and(RoleTable.ROLE.ID.ne(form.getId()));
		}
		if (mapper.selectCountByQuery(checkWrapper) > 0) {
			throw new BizException("角色编码已存在");
		}

		RoleEntity e;
		if (form.getId() != null) {
			e = mapper.selectOneById(form.getId());
			if (e == null) {
				throw new BizException("角色不存在");
			}
		} else {
			e = new RoleEntity();
		}
		e.setName(form.getName());
		e.setNumber(form.getNumber());

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
			throw new BizException(ResultEnum.PARAM_ERROR, "角色ID不能为空");
		}
		RoleEntity role = mapper.selectOneById(id);
		if (role == null) {
			throw new BizException(ResultEnum.NOT_FOUND, "角色不存在");
		}
		mapper.deleteById(id);
	}
}
