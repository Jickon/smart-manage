package sm.cloud.sys.base.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sm.cloud.sys.base.app.domain.vo.AppVO;
import sm.cloud.sys.base.app.domain.vo.CloudAppRowVO;
import sm.cloud.sys.base.app.domain.vo.CloudAppsVO;
import sm.cloud.sys.base.app.mapper.AppMapper;
import sm.system.exception.BizException;
import sm.system.response.ResultEnum;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用入口相关查询。
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AppHelper {
    private final AppMapper appMapper;

    public List<CloudAppsVO> getUserCloudApps(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return toCloudApps(appMapper.selectUserCloudApps(userId));
    }

    public List<CloudAppsVO> getAllCloudApps() {
        return toCloudApps(appMapper.selectAllCloudApps());
    }

    private List<CloudAppsVO> toCloudApps(List<CloudAppRowVO> rows) {
        Map<Long, CloudAppsVO> cloudMap = new LinkedHashMap<>();
        Map<Long, Map<Long, AppVO>> appMap = new LinkedHashMap<>();
        for (CloudAppRowVO row : rows) {
            if (row.getCloudId() == null) {
                continue;
            }
            CloudAppsVO cloud = cloudMap.computeIfAbsent(row.getCloudId(), cloudId -> {
                CloudAppsVO item = new CloudAppsVO();
                item.setId(cloudId);
                item.setName(row.getCloudName());
                item.setNumber(row.getCloudNumber());
                item.setSeq(row.getCloudSeq());
                item.setAppList(new ArrayList<>());
                return item;
            });
            if (row.getAppId() == null) {
                continue;
            }
            Map<Long, AppVO> apps = appMap.computeIfAbsent(row.getCloudId(), cloudId -> new LinkedHashMap<>());
            if (apps.containsKey(row.getAppId())) {
                continue;
            }
            AppVO app = new AppVO();
            app.setId(row.getAppId());
            app.setName(row.getAppName());
            app.setNumber(row.getAppNumber());
            app.setIcon(row.getAppIcon());
            app.setIconColor(row.getAppIconColor());
            app.setSeq(row.getAppSeq());
            app.setDescription(row.getAppDescription());
            apps.put(row.getAppId(), app);
            cloud.getAppList().add(app);
        }
        return new ArrayList<>(cloudMap.values());
    }

    public AppVO getUserAppByNumber(Long userId, String appNumber) {
        if (userId == null) {
            throw new BizException(ResultEnum.UNAUTHORIZED);
        }
        if (appNumber == null || appNumber.isBlank()) {
            throw new BizException(ResultEnum.PARAM_ERROR, "应用编码不能为空");
        }
        AppVO app = appMapper.selectUserAppByNumber(userId, appNumber);
        if (app == null) {
            throw new BizException(ResultEnum.NOT_FOUND, "应用不存在或无权访问");
        }
        return app;
    }
}
