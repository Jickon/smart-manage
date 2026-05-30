import { Modal } from '@arco-design/web-react';
import type { ReactNode } from 'react';

export interface EditModalProps {
  visible: boolean;
  title: string;
  children: ReactNode;
  okText?: ReactNode;
  cancelText?: ReactNode;
  confirmLoading?: boolean;
  onSave?: () => void;
  onCancel?: () => void;
}

const EditModal = ({
  visible,
  title,
  children,
  okText = '保存',
  cancelText = '取消',
  confirmLoading,
  onSave,
  onCancel,
}: EditModalProps) => {
  return (
    <Modal
      className="sm-edit-modal"
      title={title}
      visible={visible}
      closable
      maskClosable={false}
      escToExit={false}
      okText={okText}
      cancelText={cancelText}
      confirmLoading={confirmLoading}
      onOk={onSave}
      onCancel={onCancel}
      footer={(cancelButtonNode, okButtonNode) => (
        <div className="sm-edit-modal-footer">
          {cancelButtonNode}
          {okButtonNode}
        </div>
      )}
    >
      <div className="sm-edit-modal-body">{children}</div>
    </Modal>
  );
};

export default EditModal;
