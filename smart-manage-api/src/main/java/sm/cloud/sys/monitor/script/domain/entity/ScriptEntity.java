package sm.cloud.sys.monitor.script.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sm.system.entity.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_sys_script")
public class ScriptEntity extends BaseEntity {
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;
    private String number;
    private String name;
    private String content;
    private String remark;
}
