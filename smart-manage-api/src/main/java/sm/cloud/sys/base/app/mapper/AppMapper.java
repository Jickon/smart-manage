package sm.cloud.sys.base.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sm.cloud.sys.base.app.domain.entity.AppEntity;
import sm.cloud.sys.base.app.domain.form.AppListForm;
import sm.cloud.sys.base.app.domain.vo.AppDetailVO;
import sm.cloud.sys.base.app.domain.vo.AppListVO;
import sm.cloud.sys.base.app.domain.vo.AppVO;
import sm.cloud.sys.base.app.domain.vo.CloudAppRowVO;

import java.util.List;

@Mapper
public interface AppMapper extends BaseMapper<AppEntity> {
    Page<AppListVO> selectListPage(Page<AppListVO> page, @Param("form") AppListForm form);

    AppDetailVO selectDetailById(Long id);

    List<CloudAppRowVO> selectUserCloudApps(Long userId);

    List<CloudAppRowVO> selectAllCloudApps();

    AppVO selectUserAppByNumber(@Param("userId") Long userId, @Param("appNumber") String appNumber);
}

