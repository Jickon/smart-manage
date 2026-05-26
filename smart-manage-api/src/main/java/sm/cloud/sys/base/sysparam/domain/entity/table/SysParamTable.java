package sm.cloud.sys.base.sysparam.domain.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * 系统参数表定义（APT 生成后可直接替换）
 */
public class SysParamTable extends TableDef {

    public static final SysParamTable SYS_PARAM = new SysParamTable();

    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn NUMBER = new QueryColumn(this, "number");
    public final QueryColumn NAME = new QueryColumn(this, "name");
    public final QueryColumn VALUE = new QueryColumn(this, "value");
    public final QueryColumn REMARK = new QueryColumn(this, "remark");
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");
    public final QueryColumn CREATE_USER = new QueryColumn(this, "create_user");
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");
    public final QueryColumn UPDATE_USER = new QueryColumn(this, "update_user");

    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{
        ID, NUMBER, NAME, VALUE, REMARK,
        CREATE_TIME, CREATE_USER, UPDATE_TIME, UPDATE_USER
    };

    public SysParamTable() {
        super("", "t_sys_param");
    }

    private SysParamTable(String schema, String name, String alias) {
        super(schema, name, alias);
    }

    public SysParamTable as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new SysParamTable("", "t_sys_param", alias));
    }
}
