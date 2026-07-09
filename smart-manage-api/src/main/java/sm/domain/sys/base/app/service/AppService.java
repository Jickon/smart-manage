package sm.domain.sys.base.app.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sm.domain.sys.base.app.model.entity.AppEntity;
import sm.domain.sys.base.app.model.form.AppListForm;
import sm.domain.sys.base.app.model.form.AppSaveForm;
import sm.domain.sys.base.app.model.vo.*;
import sm.domain.sys.base.app.mapper.AppMapper;
import sm.system.exception.BizException;
import sm.system.response.PageData;
import sm.system.response.ResultEnum;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class AppService {
	private static final String DEFAULT_ICON = "app";
	private static final String DEFAULT_ICON_COLOR = "#165dff";

	private final AppMapper mapper;
	private final AppTxService txService;

	public PageData<AppListVO> listPage(AppListForm form) {
		Page<AppListVO> result = mapper.selectListPage(new Page<>(form.getPageNum(), form.getPageSize()), form);
		return PageData.of(result.getTotal(), form.getPageNum(), form.getPageSize(), result.getRecords());
	}

	public AppEntity getById(Long id) {
		return mapper.selectById(id);
	}

	/**
	 * 详情页需要展示所属云信息（编码/名称），避免前端为 label 再请求一次 cloud/detail。
	 */
	public AppDetailVO detail(Long id) {
		if (id == null) {
			throw new BizException(ResultEnum.PARAM_ERROR, "应用ID不能为空");
		}
		AppDetailVO detail = mapper.selectDetailById(id);
		if (detail == null) {
			throw new BizException(ResultEnum.NOT_FOUND, "应用不存在");
		}
		return detail;
	}

	public AppCreateNewDataVO createNewData() {
		AppCreateNewDataVO vo = new AppCreateNewDataVO();
		vo.setIcon(DEFAULT_ICON);
		vo.setIconColor(DEFAULT_ICON_COLOR);
		vo.setSeq(99);
		vo.setEnableFlag(true);
		return vo;
	}

	public Long save(AppSaveForm form) {
		return txService.save(form);
	}

	public void deleteById(Long id) {
		txService.deleteById(id);
	}

	// ==================== 云+应用入口查询 ====================

	public List<CloudAppsVO> getUserCloudApps(Long userId) {
		if (userId == null) {
			return List.of();
		}
		return toCloudApps(mapper.selectUserCloudApps(userId));
	}

	public List<CloudAppsVO> getAllCloudApps() {
		return toCloudApps(mapper.selectAllCloudApps());
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
			Map<Long, AppVO> appsMap = appMap.computeIfAbsent(row.getCloudId(), cloudId -> new LinkedHashMap<>());
			if (appsMap.containsKey(row.getAppId())) {
				continue;
			}
			AppVO vo = new AppVO();
			vo.setId(row.getAppId());
			vo.setName(row.getAppName());
			vo.setNumber(row.getAppNumber());
			vo.setIcon(row.getAppIcon());
			vo.setIconColor(row.getAppIconColor());
			vo.setSeq(row.getAppSeq());
			vo.setDescription(row.getAppDescription());
			appsMap.put(row.getAppId(), vo);
			cloud.getAppList().add(vo);
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
		AppVO vo = mapper.selectUserAppByNumber(userId, appNumber);
		if (vo == null) {
			throw new BizException(ResultEnum.NOT_FOUND, "应用不存在或无权访问");
		}
		return vo;
	}
}
