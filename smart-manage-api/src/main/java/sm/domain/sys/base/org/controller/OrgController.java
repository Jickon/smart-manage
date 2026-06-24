package sm.domain.sys.base.org.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sm.domain.sys.base.org.model.form.OrgListForm;
import sm.domain.sys.base.org.model.vo.OrgListVO;
import sm.domain.sys.base.org.service.OrgService;
import sm.system.response.PageResult;
import sm.system.response.Result;

/**
 * @author Chekfu
 */
@RestController
@Tag(name = "组织", description = "组织信息")
@RequiredArgsConstructor
public class OrgController {
	private final OrgService service;

	@Operation(summary = "组织列表", description = "获取组织分页列表数据")
	@PostMapping("/sys/base/org/listPage")
	public Result<PageResult<OrgListVO>> listPage(@RequestBody OrgListForm form) {
		return Result.success(service.listPage(form));
	}
}
