import { Card, Statistic, Grid } from '@arco-design/web-react';
import { IconUserGroup, IconApps, IconFire, IconMessage } from '@arco-design/web-react/icon';

const Row = Grid.Row;
const Col = Grid.Col;

const Home = () => (
  <div className="sm-home">
    <Row gutter={[16, 16]}>
      <Col span={6}>
        <Card>
          <Statistic title="用户总数" value={0} prefix={<IconUserGroup />} />
        </Card>
      </Col>
      <Col span={6}>
        <Card>
          <Statistic title="应用总数" value={0} prefix={<IconApps />} />
        </Card>
      </Col>
      <Col span={6}>
        <Card>
          <Statistic title="今日活跃" value={0} prefix={<IconFire />} />
        </Card>
      </Col>
      <Col span={6}>
        <Card>
          <Statistic title="系统消息" value={0} prefix={<IconMessage />} />
        </Card>
      </Col>
    </Row>
    <Row className="sm-home-row" gutter={[16, 16]}>
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
