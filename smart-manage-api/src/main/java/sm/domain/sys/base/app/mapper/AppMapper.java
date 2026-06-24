package sm.domain.sys.base.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sm.domain.sys.base.app.model.entity.AppEntity;
import sm.domain.sys.base.app.model.form.AppListForm;
import sm.domain.sys.base.app.model.vo.AppDetailVO;
import sm.domain.sys.base.app.model.vo.AppListVO;
import sm.domain.sys.base.app.model.vo.AppVO;
import sm.domain.sys.base.app.model.vo.CloudAppRowVO;

import java.util.List;

@Mapper
public interface AppMapper extends BaseMapper<AppEntity> {
    Page<AppListVO> selectListPage(Page<AppListVO> page, @Param("form") AppListForm form);

    AppDetailVO selectDetailById(Long id);

    List<CloudAppRowVO> selectUserCloudApps(Long userId);

    List<CloudAppRowVO> selectAllCloudApps();

    AppVO selectUserAppByNumber(@Param("userId") Long userId, @Param("appNumber") String appNumber);
}

