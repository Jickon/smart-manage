package sm.domain.sys.base.fileconfig.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sm.domain.sys.base.fileconfig.model.form.FileConfigListForm;
import sm.domain.sys.base.fileconfig.model.form.FileConfigSaveForm;
import sm.domain.sys.base.fileconfig.model.form.FtpTestForm;
import sm.domain.sys.base.fileconfig.model.vo.FileConfigDetailVO;
import sm.domain.sys.base.fileconfig.service.FileConfigService;
import sm.system.form.IdForm;
import sm.system.response.PageData;
import sm.system.response.Result;

/**
 * 文件配置管理
 *
 * @author Chekfu
 */
@RestController
@Tag(name = "系统建模-文件配置", description = "文件配置管理接口")
@RequiredArgsConstructor
@Slf4j
public class FileConfigController {
    private final FileConfigService service;

    @PostMapping("/sys/base/file-config/listPage")
    @Operation(summary = "文件配置列表")
    @SaCheckPermission("sys:base:file-config:listPage")
    public Result<PageData<FileConfigDetailVO>> listPage(@RequestBody FileConfigListForm form) {
        return Result.success(service.listPage(form));
    }

    @PostMapping("/sys/base/file-config/detail")
    @Operation(summary = "文件配置详情")
    @SaCheckPermission("sys:base:file-config:detail")
    public Result<FileConfigDetailVO> detail(@RequestBody @Valid IdForm form) {
        return Result.success(service.getDetail(form.getId()));
    }

    @PostMapping("/sys/base/file-config/save")
    @Operation(summary = "保存文件配置")
    @SaCheckPermission("sys:base:file-config:save")
    public Result<Long> save(@Valid @RequestBody FileConfigSaveForm form) {
        return Result.success(service.save(form));
    }

    @PostMapping("/sys/base/file-config/delete")
    @Operation(summary = "删除文件配置")
    @SaCheckPermission("sys:base:file-config:delete")
    public Result<String> delete(@RequestBody @Valid IdForm form) {
        service.deleteById(form.getId());
        return Result.success();
    }

    @PostMapping("/sys/base/file-config/active")
    @Operation(summary = "获取活跃配置", description = "获取当前活跃的文件配置（无需登录）")
    @SaIgnore
    public Result<FileConfigDetailVO> active() {
        return Result.success(service.getActiveConfig());
    }

    @PostMapping("/sys/base/file-config/test-ftp")
    @Operation(summary = "测试FTP连接", description = "使用表单中的FTP参数测试连接是否正常")
    @SaCheckPermission("sys:base:file-config:save")
    public Result<String> testFtp(@Valid @RequestBody FtpTestForm form) {
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(form.getFtpHost(), form.getFtpPort() != null ? form.getFtpPort() : 21);
            if (!ftp.login(form.getFtpUsername(), form.getFtpPassword())) {
                return Result.error("FTP 登录失败: " + ftp.getReplyString());
            }
            if (form.getFtpPassiveMode() != null && form.getFtpPassiveMode()) {
                ftp.enterLocalPassiveMode();
            }
            if (form.getFtpDir() != null && !form.getFtpDir().isBlank()) {
                if (!ftp.changeWorkingDirectory(form.getFtpDir())) {
                    return Result.error("FTP 目录切换失败: " + ftp.getReplyString());
                }
            }
            ftp.logout();
            return Result.success("FTP 连接成功");
        } catch (Exception e) {
            log.warn("FTP 连接测试失败", e);
            return Result.error("FTP 连接失败: " + e.getMessage());
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (Exception ignored) { /* ignore */ }
            }
        }
    }
}
