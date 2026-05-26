package sm.cloud.sys.base.sysparam.service;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.sysparam.domain.entity.SysParamEntity;
import sm.cloud.sys.base.sysparam.domain.entity.table.SysParamTable;
import sm.cloud.sys.base.sysparam.domain.form.SysParamListForm;
import sm.cloud.sys.base.sysparam.domain.form.SysParamSaveForm;
import sm.cloud.sys.base.sysparam.domain.vo.SysParamCreateNewDataVO;
import sm.cloud.sys.base.sysparam.domain.vo.SysParamVO;
import sm.cloud.sys.base.sysparam.mapper.SysParamMapper;
import sm.system.exception.BizException;
import sm.system.response.PageResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 系统参数服务
 *
 * @author Chekfu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysParamService {
    private final SysParamMapper mapper;

    /** 管理端分页列表 */
    public PageResult<SysParamVO> listPage(SysParamListForm form) {
        QueryWrapper qw = QueryWrapper.create().from(SysParamTable.SYS_PARAM);
        if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
            String kw = "%" + form.getKeyword().trim() + "%";
            qw.and(SysParamTable.SYS_PARAM.NUMBER.like(kw)
                    .or(SysParamTable.SYS_PARAM.NAME.like(kw)));
        }
        qw.orderBy(SysParamTable.SYS_PARAM.NUMBER, true);
        Page<SysParamEntity> page = Page.of(form.getPageNum(), form.getPageSize());
        Page<SysParamEntity> result = mapper.paginate(page, qw);
        List<SysParamVO> vos = result.getRecords().stream().map(this::toVo).collect(Collectors.toList());
        return PageResult.of(result.getTotalRow(), vos);
    }

    /** 详情 */
    public SysParamVO getById(Long id) {
        SysParamEntity entity = mapper.selectOneById(id);
        return entity != null ? toVo(entity) : null;
    }

    /** 新增默认值 */
    public SysParamCreateNewDataVO createNewData() {
        return new SysParamCreateNewDataVO();
    }

    /** 新增/编辑，清除缓存 */
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "sys-params", key = "'all'")
    public Long save(SysParamSaveForm form) {
        SysParamEntity entity;
        if (form.getId() != null) {
            entity = mapper.selectOneById(form.getId());
            if (entity == null) {
                return null;
            }
            // 系统内置参数只允许修改 value
            if (Boolean.TRUE.equals(entity.getIsSystem())) {
                entity.setValue(form.getValue());
                mapper.update(entity);
                return entity.getId();
            }
        } else {
            entity = new SysParamEntity();
        }
        entity.setNumber(form.getNumber());
        entity.setName(form.getName());
        entity.setValue(form.getValue());
        entity.setRemark(form.getRemark());
        if (form.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.update(entity);
        }
        return entity.getId();
    }

    /** 删除，清除缓存 */
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "sys-params", key = "'all'")
    public void deleteById(Long id) {
        SysParamEntity entity = mapper.selectOneById(id);
        if (entity == null) {
            return;
        }
        if (Boolean.TRUE.equals(entity.getIsSystem())) {
            throw new BizException("系统内置参数不可删除");
        }
        mapper.deleteById(id);
    }

    // ==================== 消费端（带缓存） ====================

    /** 全量获取 number → value 映射（Caffeine 本地缓存） */
    @Cached(cacheType = CacheType.LOCAL, name = "sys-params", key = "'all'", expire = 30, timeUnit = TimeUnit.MINUTES)
    public Map<String, String> getAll() {
        List<SysParamEntity> entities = mapper.selectAll();
        Map<String, String> map = new HashMap<>();
        for (SysParamEntity entity : entities) {
            map.put(entity.getNumber(), entity.getValue());
        }
        return map;
    }

    /** 获取字符串值 */
    public String getString(String number) {
        return getAll().get(number);
    }

    /** 获取布尔值（"true" 或 "1" 为 true，其余 false） */
    public boolean getBoolean(String number) {
        String value = getString(number);
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

    /** 获取整数值，不存在或解析失败返回 null */
    public Integer getInt(String number) {
        String value = getString(number);
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            log.warn("系统参数 {} 无法转为整数: {}", number, value);
            return null;
        }
    }

    // ==================== 内部方法 ====================

    private SysParamVO toVo(SysParamEntity entity) {
        SysParamVO vo = new SysParamVO();
        vo.setId(entity.getId());
        vo.setNumber(entity.getNumber());
        vo.setName(entity.getName());
        vo.setValue(entity.getValue());
        vo.setRemark(entity.getRemark());
        vo.setIsSystem(entity.getIsSystem());
        return vo;
    }
}
