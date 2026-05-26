package sm.cloud.sys.base.app.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.cloud.sys.base.app.domain.entity.AppEntity;
import sm.cloud.sys.base.app.domain.entity.table.AppTable;
import sm.cloud.sys.base.cloud.domain.entity.table.CloudTable;
import sm.cloud.sys.base.app.domain.form.AppListForm;
import sm.cloud.sys.base.app.domain.form.AppSaveForm;
import sm.cloud.sys.base.app.domain.vo.AppCreateNewDataVO;
import sm.cloud.sys.base.app.domain.vo.AppDetailVO;
import sm.cloud.sys.base.app.domain.vo.AppListVO;
import sm.cloud.sys.base.app.mapper.AppMapper;
import sm.system.response.PageResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppService {
	private final AppMapper mapper;

	public PageResult<AppListVO> listPage(AppListForm form) {
		QueryWrapper qw = QueryWrapper.create().from(AppTable.APP);
		if (form.getCloudId() != null) {
			qw.and(AppTable.APP.CLOUD_ID.eq(form.getCloudId()));
		}
		if (form.getKeyword() != null && !form.getKeyword().isBlank()) {
			String kw = "%" + form.getKeyword().trim() + "%";
			qw.and(AppTable.APP.NAME.like(kw).or(AppTable.APP.NUMBER.like(kw)));
		}
		qw.orderBy(AppTable.APP.SEQ, true).orderBy(AppTable.APP.ID, true);
		Page<AppEntity> page = Page.of(form.getPageNum(), form.getPageSize());
		Page<AppEntity> result = mapper.paginate(page, qw);
		List<AppEntity> entities = result.getRecords();

		// 统一由后端 listPage 返回 cloudName，避免前端为了展示名称额外请求云详情
		Set<Long> cloudIds = new HashSet<>();
		for (AppEntity e : entities) {
			if (e.getCloudId() != null) {
				cloudIds.add(e.getCloudId());
			}
		}
		Map<Long, String> cloudNameById = new HashMap<>();
		if (!cloudIds.isEmpty()) {
			// 注意：PostgreSQL 不支持把数组当成单个 IN(?) 参数绑定；这里必须展开成 in(?, ?, ...)
			List<Row> cloudRows = Db.selectListByQuery(
					QueryWrapper.create()
							.select(CloudTable.CLOUD.ID.as("cid"), CloudTable.CLOUD.NAME.as("cname"))
							.from(CloudTable.CLOUD)
							.and(CloudTable.CLOUD.ID.in((Object[]) cloudIds.toArray(new Long[0])))
			);
			for (Row r : cloudRows) {
				Long cid = r.getLong("cid");
				if (cid != null) {
					cloudNameById.put(cid, r.getString("cname"));
				}
			}
		}

		List<AppListVO> vos = entities.stream().map(e -> toListVo(e, cloudNameById.get(e.getCloudId()))).collect(Collectors.toList());
		return PageResult.of(result.getTotalRow(), vos);
	}

	private AppListVO toListVo(AppEntity e, String cloudName) {
		AppListVO vo = new AppListVO();
		vo.setId(e.getId());
		vo.setName(e.getName());
		vo.setNumber(e.getNumber());
		vo.setIcon(e.getIcon());
		vo.setIconColor(e.getIconColor());
		vo.setSeq(e.getSeq());
		vo.setDescription(e.getDescription());
		vo.setCloudId(e.getCloudId());
		vo.setCloudName(cloudName);
		vo.setEnableFlag(e.getEnableFlag());
		vo.setCreateTime(e.getCreateTime());
		vo.setUpdateTime(e.getUpdateTime());
		return vo;
	}

	public AppEntity getById(Long id) {
		return mapper.selectOneById(id);
	}

	/**
	 * 详情页需要展示所属云信息（编码/名称），避免前端为 label 再请求一次 cloud/detail。
	 * 返回 null 表示记录不存在。
	 */
	public AppDetailVO detail(Long id) {
		if (id == null) {
			return null;
		}
		QueryWrapper query = QueryWrapper.create()
				.select(
						AppTable.APP.ID.as("aid"),
						AppTable.APP.NAME.as("aname"),
						AppTable.APP.NUMBER.as("anumber"),
						AppTable.APP.ICON.as("aicon"),
						AppTable.APP.ICON_COLOR.as("aiconcolor"),
						AppTable.APP.SEQ.as("aseq"),
						AppTable.APP.DESCRIPTION.as("adescription"),
						AppTable.APP.CLOUD_ID.as("acloudId"),
						AppTable.APP.ENABLE_FLAG.as("aenableFlag"),
						AppTable.APP.CREATE_TIME.as("acreateTime"),
						AppTable.APP.UPDATE_TIME.as("aupdateTime"),
						CloudTable.CLOUD.ID.as("cid"),
						CloudTable.CLOUD.NUMBER.as("cnumber"),
						CloudTable.CLOUD.NAME.as("cname")
				)
				.from(AppTable.APP)
				.leftJoin(CloudTable.CLOUD).on(CloudTable.CLOUD.ID.eq(AppTable.APP.CLOUD_ID))
				.where(AppTable.APP.ID.eq(id))
				.limit(1);
		Row row = Db.selectOneByQuery(query);
		if (row == null) {
			return null;
		}
		AppDetailVO vo = new AppDetailVO();
		vo.setId(row.getLong("aid"));
		vo.setName(row.getString("aname"));
		vo.setNumber(row.getString("anumber"));
		vo.setIcon(row.getString("aicon"));
		vo.setIconColor(row.getString("aiconcolor"));
		vo.setSeq(row.getInt("aseq"));
		vo.setDescription(row.getString("adescription"));
		vo.setEnableFlag(row.getBoolean("aenableFlag"));
		vo.setCreateTime(row.getTimestamp("acreateTime").toLocalDateTime());
		vo.setUpdateTime(row.getTimestamp("aupdateTime").toLocalDateTime());
		Long cloudId = row.getLong("cid");
		if (cloudId != null) {
			AppDetailVO.CloudRef cloud = new AppDetailVO.CloudRef();
			cloud.setId(cloudId);
			cloud.setNumber(row.getString("cnumber"));
			cloud.setName(row.getString("cname"));
			vo.setCloud(cloud);
		} else {
			vo.setCloud(null);
		}
		return vo;
	}

	public AppCreateNewDataVO createNewData() {
		AppCreateNewDataVO vo = new AppCreateNewDataVO();
		vo.setSeq(99);
		vo.setEnableFlag(true);
		return vo;
	}

	@Transactional(rollbackFor = Exception.class)
	public Long save(AppSaveForm form) {
		AppEntity e;
		if (form.getId() != null) {
			e = mapper.selectOneById(form.getId());
			if (e == null) {
				return null;
			}
		} else {
			e = new AppEntity();
		}
		e.setName(form.getName());
		e.setNumber(form.getNumber());
		e.setIcon(form.getIcon());
		e.setIconColor(form.getIconColor());
		e.setSeq(form.getSeq() != null ? form.getSeq() : 99);
		e.setDescription(form.getDescription());
		e.setCloudId(form.getCloudId());
		e.setEnableFlag(form.getEnableFlag() != null ? form.getEnableFlag() : true);
		if (form.getId() == null) {
			mapper.insert(e);
		} else {
			mapper.update(e);
		}
		return e.getId();
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteById(Long id) {
		mapper.deleteById(id);
	}
}

