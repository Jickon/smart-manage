package sm.cloud.sys.base.app.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sm.cloud.sys.base.app.domain.entity.AppEntity;
import sm.cloud.sys.base.app.domain.form.AppListForm;
import sm.cloud.sys.base.app.domain.form.AppSaveForm;
import sm.cloud.sys.base.app.domain.vo.AppCreateNewDataVO;
import sm.cloud.sys.base.app.domain.vo.AppDetailVO;
import sm.cloud.sys.base.app.domain.vo.AppListVO;
import sm.cloud.sys.base.app.mapper.AppMapper;
import sm.system.exception.BizException;
import sm.system.response.PageResult;
import sm.system.response.ResultEnum;


@Service
@Slf4j
@RequiredArgsConstructor
public class AppService {
	private static final String DEFAULT_ICON = "app";
	private static final String DEFAULT_ICON_COLOR = "#165dff";

	private final AppMapper mapper;
	private final AppTxService txService;

	public PageResult<AppListVO> listPage(AppListForm form) {
		Page<AppListVO> result = mapper.selectListPage(new Page<>(form.getPageNum(), form.getPageSize()), form);
		return PageResult.of(result.getTotal(), result.getRecords());
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
}
