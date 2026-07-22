package sm.domain.sys.monitor.sql.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.domain.sys.monitor.sql.mapper.SqlLogMapper;
import sm.domain.sys.monitor.sql.model.entity.SqlLogEntity;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

/**
 * SQL 执行审计日志事务写入服务，仅供同包 SQL 控制台服务调用。
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
class SqlLogTxService {

    private final SqlLogMapper mapper;

    public void save(SqlLogEntity entity) {
        if (mapper.insert(entity) != 1) {
            throw new BizException(ResultEnum.PERSISTENCE_ERROR, "SQL执行日志写入失败");
        }
    }
}
