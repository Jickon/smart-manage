import { Card, Col, Row, Statistic } from 'antd';
import { UserOutlined, AppstoreOutlined, FireOutlined, MessageOutlined } from '@ant-design/icons';
import './Home.css';

const Home = () => (
  <div className="sm-home">
    <Row gutter={[16, 16]}>
      <Col span={6}>
        <Card>
          <Statistic title="用户总数" value={0} prefix={<UserOutlined />} />
        </Card>
      </Col>
      <Col span={6}>
        <Card>
          <Statistic title="应用总数" value={0} prefix={<AppstoreOutlined />} />
        </Card>
      </Col>
      <Col span={6}>
        <Card>
          <Statistic title="今日活跃" value={0} prefix={<FireOutlined />} />
        </Card>
      </Col>
      <Col span={6}>
        <Card>
          <Statistic title="系统消息" value={0} prefix={<MessageOutlined />} />
        </Card>
      </Col>
    </Row>
    <Row gutter={[16, 16]} className="sm-home-row">
      <Col span={16}>
        <Card title="欢迎使用">Dashboard — 后续集成图表</Card>
      </Col>
      <Col span={8}>
        <Card title="快捷入口">快捷功能占位</Card>
      </Col>
    </Row>
  </div>
);

export default Home;
