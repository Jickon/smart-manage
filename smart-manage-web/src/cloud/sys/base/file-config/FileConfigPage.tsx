import { useMemo, useState } from 'react';
import { Button, Form, Input, InputNumber, Message, Select, Spin, Switch } from '@arco-design/web-react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { EditPage } from '@/cloud/common/page';
import { getMissingRequiredField, requiredFieldMessage } from '@/cloud/common/page/formValidation';
import { useAppWorkspaceStore } from '@/stores/appWorkspace';
import { fileConfigApi } from './api';
import type { PageComponentProps } from '@/cloud/common/page';
import type { FileConfigDetailVO, FileConfigSaveForm, FileStorageType, FtpTestForm } from './types';

interface FileConfigFormState {
  id?: string;
  storageType: FileStorageType;
  localDir?: string;
  ftpHost?: string;
  ftpPort?: number;
  ftpUsername?: string;
  ftpPassword?: string;
  ftpDir?: string;
  ftpPassiveMode: boolean;
}

const emptyFileConfigForm = (): FileConfigFormState => ({
  storageType: 'LOCAL',
  localDir: '',
  ftpHost: '',
  ftpPort: 21,
  ftpUsername: '',
  ftpPassword: '',
  ftpDir: '',
  ftpPassiveMode: true,
});

const toFileConfigForm = (detail: Partial<FileConfigDetailVO>): FileConfigFormState => ({
  id: detail.id,
  storageType: detail.storageType ?? 'LOCAL',
  localDir: detail.localDir ?? '',
  ftpHost: detail.ftpHost ?? '',
  ftpPort: detail.ftpPort ?? 21,
  ftpUsername: detail.ftpUsername ?? '',
  ftpPassword: detail.ftpPassword ?? '',
  ftpDir: detail.ftpDir ?? '',
  ftpPassiveMode: detail.ftpPassiveMode ?? true,
});

const toFileConfigSaveForm = (formState: FileConfigFormState): FileConfigSaveForm => ({
  id: formState.id,
  storageType: formState.storageType,
  localDir: formState.localDir?.trim(),
  ftpHost: formState.ftpHost?.trim(),
  ftpPort: formState.ftpPort,
  ftpUsername: formState.ftpUsername?.trim(),
  ftpPassword: formState.ftpPassword?.trim(),
  ftpDir: formState.ftpDir?.trim(),
  ftpPassiveMode: formState.ftpPassiveMode,
});

const toFtpTestForm = (formState: FileConfigFormState): FtpTestForm => ({
  ftpHost: formState.ftpHost?.trim(),
  ftpPort: formState.ftpPort,
  ftpUsername: formState.ftpUsername?.trim(),
  ftpPassword: formState.ftpPassword?.trim(),
  ftpDir: formState.ftpDir?.trim(),
  ftpPassiveMode: formState.ftpPassiveMode,
});

const validateFileConfigForm = (formState: FileConfigFormState) => {
  const commonMissingField = getMissingRequiredField([{ label: '存储类型', value: formState.storageType }]);
  if (commonMissingField) {
    Message.warning(requiredFieldMessage(commonMissingField));
    return false;
  }

  if (formState.storageType === 'LOCAL') {
    const localMissingField = getMissingRequiredField([{ label: '本地目录', value: formState.localDir }]);
    if (localMissingField) {
      Message.warning(requiredFieldMessage(localMissingField));
      return false;
    }
  }

  if (formState.storageType === 'FTP') {
    const ftpMissingField = getMissingRequiredField([
      { label: 'FTP 主机', value: formState.ftpHost },
      { label: 'FTP 用户名', value: formState.ftpUsername },
      { label: 'FTP 密码', value: formState.ftpPassword },
    ]);
    if (ftpMissingField) {
      Message.warning(requiredFieldMessage(ftpMissingField));
      return false;
    }
  }

  return true;
};

const validateFtpTestForm = (formState: FileConfigFormState) => {
  const missingField = getMissingRequiredField([
    { label: 'FTP 主机', value: formState.ftpHost },
    { label: 'FTP 用户名', value: formState.ftpUsername },
    { label: 'FTP 密码', value: formState.ftpPassword },
  ]);
  if (missingField) {
    Message.warning(requiredFieldMessage(missingField));
    return false;
  }
  return true;
};

const FileConfigPage = (props: PageComponentProps) => {
  const [formPatch, setFormPatch] = useState<Partial<FileConfigFormState>>({});
  const queryClient = useQueryClient();
  const removeContentTab = useAppWorkspaceStore((store) => store.removeContentTab);

  const activeQuery = useQuery({
    queryKey: ['file-config-active'],
    queryFn: fileConfigApi.active,
  });

  const baseFormState = useMemo(() => {
    if (activeQuery.data) {
      return toFileConfigForm(activeQuery.data);
    }
    return emptyFileConfigForm();
  }, [activeQuery.data]);

  const formState = { ...baseFormState, ...formPatch };

  const saveMutation = useMutation({
    mutationFn: () => fileConfigApi.save(toFileConfigSaveForm(formState)),
    onSuccess: async () => {
      Message.success('保存成功');
      setFormPatch({});
      await queryClient.invalidateQueries({ queryKey: ['file-config-active'] });
    },
    onError: (error) => Message.error(error.message),
  });

  const testFtpMutation = useMutation({
    mutationFn: () => fileConfigApi.testFtp(toFtpTestForm(formState)),
    onSuccess: (message) => Message.success(message || 'FTP 连接成功'),
    onError: (error) => Message.error(error.message),
  });

  return (
    <EditPage
      {...props}
      title="文件配置"
      submitVisible={false}
      toolbarActions={
        <Button
          type="primary"
          loading={testFtpMutation.isPending}
          onClick={() => {
            if (validateFtpTestForm(formState)) {
              testFtpMutation.mutate();
            }
          }}
        >
          测试 FTP
        </Button>
      }
      onSave={() => {
        if (validateFileConfigForm(formState)) {
          saveMutation.mutate();
        }
      }}
      onCancel={() => removeContentTab(props.appNumber, props.tabKey)}
      sections={[
        {
          key: 'basic',
          title: '基本信息',
          content: (
            <Spin loading={activeQuery.isFetching}>
              <Form className="sm-edit-form" layout="vertical">
                <Form.Item label="存储类型" required>
                  <Select
                    value={formState.storageType}
                    placeholder="请选择存储类型"
                    onChange={(storageType) =>
                      setFormPatch((current) => ({ ...current, storageType: storageType as FileStorageType }))
                    }
                  >
                    <Select.Option value="LOCAL">本地存储</Select.Option>
                    <Select.Option value="FTP">FTP 存储</Select.Option>
                  </Select>
                </Form.Item>
                <Form.Item label="本地目录" required={formState.storageType === 'LOCAL'}>
                  <Input
                    value={formState.localDir}
                    placeholder="请输入本地目录路径"
                    onChange={(localDir) => setFormPatch((current) => ({ ...current, localDir }))}
                  />
                </Form.Item>
              </Form>
            </Spin>
          ),
        },
        {
          key: 'ftp',
          title: 'FTP 配置',
          content: (
            <Spin loading={activeQuery.isFetching}>
              <Form className="sm-edit-form" layout="vertical">
                <Form.Item label="FTP 主机" required={formState.storageType === 'FTP'}>
                  <Input
                    value={formState.ftpHost}
                    placeholder="请输入 FTP 主机"
                    onChange={(ftpHost) => setFormPatch((current) => ({ ...current, ftpHost }))}
                  />
                </Form.Item>
                <Form.Item label="FTP 端口">
                  <InputNumber
                    value={formState.ftpPort}
                    min={1}
                    max={65535}
                    onChange={(ftpPort) => setFormPatch((current) => ({ ...current, ftpPort }))}
                  />
                </Form.Item>
                <Form.Item label="FTP 用户名" required={formState.storageType === 'FTP'}>
                  <Input
                    value={formState.ftpUsername}
                    placeholder="请输入 FTP 用户名"
                    onChange={(ftpUsername) => setFormPatch((current) => ({ ...current, ftpUsername }))}
                  />
                </Form.Item>
                <Form.Item label="FTP 密码" required={formState.storageType === 'FTP'}>
                  <Input.Password
                    value={formState.ftpPassword}
                    placeholder="请输入 FTP 密码"
                    onChange={(ftpPassword) => setFormPatch((current) => ({ ...current, ftpPassword }))}
                  />
                </Form.Item>
                <Form.Item label="FTP 目录">
                  <Input
                    value={formState.ftpDir}
                    placeholder="请输入 FTP 远程目录"
                    onChange={(ftpDir) => setFormPatch((current) => ({ ...current, ftpDir }))}
                  />
                </Form.Item>
                <Form.Item label="被动模式">
                  <Switch
                    checked={formState.ftpPassiveMode}
                    onChange={(ftpPassiveMode) => setFormPatch((current) => ({ ...current, ftpPassiveMode }))}
                  />
                </Form.Item>
              </Form>
            </Spin>
          ),
        },
      ]}
    />
  );
};

export default FileConfigPage;
