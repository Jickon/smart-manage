package sm.domain.sys.base.role.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sm.domain.sys.base.role.model.entity.RoleEntity;
import sm.domain.sys.base.role.model.form.RoleListForm;
import sm.domain.sys.base.role.model.form.RoleSaveForm;
import sm.domain.sys.base.role.model.form.RoleSelectForm;
import sm.domain.sys.base.role.model.vo.RoleCreateNewDataVO;
import sm.domain.sys.base.role.model.vo.RoleDetailVO;
import sm.domain.sys.base.role.model.vo.RoleListVO;
import sm.domain.sys.base.role.model.vo.RoleSelectVO;
import sm.domain.sys.base.role.mapper.RoleMapper;
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
	private final RoleTxService txService;

	public PageResult<RoleListVO> listPage(RoleListForm form) {
		LambdaQueryWrapper<RoleEntity> qw = new LambdaQueryWrapper<RoleEntity>()
				.orderByAsc(RoleEntity::getId);
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String kw = "%" + form.getKeyword().trim() + "%";
			qw.and(condition -> condition.like(RoleEntity::getName, kw).or().like(RoleEntity::getNumber, kw));
		}
		Page<RoleEntity> page = new Page<>(form.getPageNum(), form.getPageSize());
		Page<RoleEntity> result = mapper.selectPage(page, qw);
		var vos = result.getRecords().stream().map(this::toRoleListVO).collect(Collectors.toList());
		return PageResult.of(result.getTotal(), vos);
	}

	/**
	 * 基础资料选择：分页查询角色。
	 */
	public PageResult<RoleSelectVO> select(RoleSelectForm form) {
		LambdaQueryWrapper<RoleEntity> qw = new LambdaQueryWrapper<RoleEntity>()
				.orderByAsc(RoleEntity::getId);
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String kw = "%" + form.getKeyword().trim() + "%";
			qw.and(condition -> condition.like(RoleEntity::getName, kw).or().like(RoleEntity::getNumber, kw));
		}
		Page<RoleEntity> page = new Page<>(form.getPageNum(), form.getPageSize());
		Page<RoleEntity> result = mapper.selectPage(page, qw);
		List<RoleSelectVO> voList = result.getRecords().stream().map(this::toRoleSelectVO).collect(Collectors.toList());
		return PageResult.of(result.getTotal(), voList);
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
		return vo;
	}

	public RoleCreateNewDataVO createNewData() {
		return new RoleCreateNewDataVO();
	}

	public Long save(RoleSaveForm form) {
		return txService.save(form);
	}

	public void deleteById(Long id) {
		txService.deleteById(id);
	}
}
