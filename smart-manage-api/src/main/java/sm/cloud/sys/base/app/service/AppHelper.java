package sm.cloud.sys.base.app.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sm.cloud.sys.base.app.domain.entity.table.AppTable;
import sm.cloud.sys.base.app.domain.vo.AppVO;
import sm.cloud.sys.base.app.domain.vo.CloudAppsVO;
import sm.cloud.sys.base.cloud.domain.entity.table.CloudTable;
import sm.cloud.sys.base.menu.domain.entity.table.MenuTable;
import sm.cloud.sys.base.roleperms.domain.entity.table.RolePermsTable;
import sm.cloud.sys.base.userrole.domain.entity.table.UserRoleTable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用入口相关查询（云/应用列表、按编号打开应用）
 */
@Service
@Slf4j
public class AppHelper {

	/**
	 * 按用户角色权限（菜单-权限-角色）关联，返回云及其下应用；同一用户多角色时应用去重。
	 */
	public List<CloudAppsVO> getUserCloudApps(Long userId) {
		if (userId == null) {
			return List.of();
		}
		QueryWrapper query = QueryWrapper.create()
				.select(
						CloudTable.CLOUD.ID.as("cid"),
						CloudTable.CLOUD.NAME.as("cname"),
						CloudTable.CLOUD.NUMBER.as("cnumber"),
						CloudTable.CLOUD.SEQ.as("cseq"),
						AppTable.APP.ID.as("aid"),
						AppTable.APP.NAME.as("aname"),
						AppTable.APP.NUMBER.as("anumber"),
						AppTable.APP.ICON.as("aicon"),
						AppTable.APP.ICON_COLOR.as("aiconcolor"),
						AppTable.APP.SEQ.as("aseq"),
						AppTable.APP.DESCRIPTION.as("adescription")
				)
				.from(AppTable.APP)
				.leftJoin(CloudTable.CLOUD).on(CloudTable.CLOUD.ID.eq(AppTable.APP.CLOUD_ID))
				.leftJoin(MenuTable.MENU).on(MenuTable.MENU.APP_ID.eq(AppTable.APP.ID))
				.leftJoin(RolePermsTable.ROLE_PERMS).on(RolePermsTable.ROLE_PERMS.PERMISSION_ID.eq(MenuTable.MENU.PERMISSION_ID))
				.leftJoin(UserRoleTable.USER_ROLE).on(UserRoleTable.USER_ROLE.ROLE_ID.eq(RolePermsTable.ROLE_PERMS.ROLE_ID))
				.where(UserRoleTable.USER_ROLE.USER_ID.eq(userId))
				.orderBy(CloudTable.CLOUD.SEQ, true).orderBy(AppTable.APP.SEQ, true);

		List<Row> rows = Db.selectListByQuery(query);
		return toCloudApps(rows);
	}

	/**
	 * 不按用户权限过滤，返回全量云及其下应用。
	 */
	public List<CloudAppsVO> getAllCloudApps() {
		QueryWrapper query = QueryWrapper.create()
				.select(
						CloudTable.CLOUD.ID.as("cid"),
						CloudTable.CLOUD.NAME.as("cname"),
						CloudTable.CLOUD.NUMBER.as("cnumber"),
						CloudTable.CLOUD.SEQ.as("cseq"),
						AppTable.APP.ID.as("aid"),
						AppTable.APP.NAME.as("aname"),
						AppTable.APP.NUMBER.as("anumber"),
						AppTable.APP.ICON.as("aicon"),
						AppTable.APP.SEQ.as("aseq"),
						AppTable.APP.DESCRIPTION.as("adescription")
				)
				.from(CloudTable.CLOUD)
				.leftJoin(AppTable.APP).on(AppTable.APP.CLOUD_ID.eq(CloudTable.CLOUD.ID))
				.orderBy(CloudTable.CLOUD.SEQ, true).orderBy(AppTable.APP.SEQ, true).orderBy(AppTable.APP.ID, true);
		List<Row> rows = Db.selectListByQuery(query);
		return toCloudApps(rows);
	}

	private List<CloudAppsVO> toCloudApps(List<Row> rows) {
		Map<Long, CloudAppsVO> cloudMap = new LinkedHashMap<>();
		Map<Long, Map<Long, AppVO>> dedupe = new LinkedHashMap<>();
		for (Row row : rows) {
			Long cid = row.getLong("cid");
			if (cid == null) continue;
			CloudAppsVO cloud = cloudMap.computeIfAbsent(cid, id -> {
				CloudAppsVO c = new CloudAppsVO();
				c.setId(id);
				c.setName(row.getString("cname"));
				c.setNumber(row.getString("cnumber"));
				c.setSeq(row.getInt("cseq"));
				c.setAppList(new ArrayList<>());
				return c;
			});
			Long aid = row.getLong("aid");
			if (aid == null) continue;
			Map<Long, AppVO> appsForCloud = dedupe.computeIfAbsent(cid, k -> new LinkedHashMap<>());
			if (appsForCloud.containsKey(aid)) continue;

			AppVO app = new AppVO();
			app.setId(aid);
			app.setName(row.getString("aname"));
			app.setNumber(row.getString("anumber"));
			app.setIcon(row.getString("aicon"));
			app.setIconColor(row.getString("aiconcolor"));
			app.setSeq(row.getInt("aseq"));
			app.setDescription(row.getString("adescription"));
			appsForCloud.put(aid, app);
			cloud.getAppList().add(app);
		}
		return new ArrayList<>(cloudMap.values());
	}

	/**
	 * 按应用编号（number）获取当前用户有权限访问的应用。
	 * 返回 null 表示应用不存在或无权限。
	 */
	public AppVO getUserAppByNumber(Long userId, String appNumber) {
		if (userId == null || appNumber == null || appNumber.isBlank()) {
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
						CloudTable.CLOUD.NUMBER.as("cnumber")
				)
				.from(AppTable.APP)
				.leftJoin(CloudTable.CLOUD).on(CloudTable.CLOUD.ID.eq(AppTable.APP.CLOUD_ID))
				.leftJoin(MenuTable.MENU).on(MenuTable.MENU.APP_ID.eq(AppTable.APP.ID))
				.leftJoin(RolePermsTable.ROLE_PERMS).on(RolePermsTable.ROLE_PERMS.PERMISSION_ID.eq(MenuTable.MENU.PERMISSION_ID))
				.leftJoin(UserRoleTable.USER_ROLE).on(UserRoleTable.USER_ROLE.ROLE_ID.eq(RolePermsTable.ROLE_PERMS.ROLE_ID))
				.where(UserRoleTable.USER_ROLE.USER_ID.eq(userId))
				.and(AppTable.APP.NUMBER.eq(appNumber))
				.limit(1);
		List<Row> rows = Db.selectListByQuery(query);
		if (rows == null || rows.isEmpty()) {
			return null;
		}
		Row row = rows.get(0);
		AppVO app = new AppVO();
		app.setId(row.getLong("aid"));
		app.setName(row.getString("aname"));
		app.setNumber(row.getString("anumber"));
		app.setCloudNumber(row.getString("cnumber"));
		app.setIcon(row.getString("aicon"));
		app.setIconColor(row.getString("aiconcolor"));
		app.setSeq(row.getInt("aseq"));
		app.setDescription(row.getString("adescription"));
		return app;
	}
}

