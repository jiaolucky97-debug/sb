package net.mooctest;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Edge类的测试类
 * 测试边的数据结构和基本功能
 */
public class EdgeTest {

    /**
     * 测试Edge构造函数和getter方法
     * 验证边对象能正确初始化并返回邻居节点和距离
     */
    @Test
    public void testEdgeConstructorAndGetters() {
        // 创建测试用的邻居节点
        Node neighbor = new Node(2, false, "Highway", false, false, false, 1.0, 0, 24);
        double distance = 5.5;
        
        // 创建边对象
        Edge edge = new Edge(neighbor, distance);
        
        // 验证邻居节点
        assertSame("邻居节点应该正确设置", neighbor, edge.getNeighbor());
        
        // 验证距离
        assertEquals("距离应该正确设置", distance, edge.getDistance(), 0.001);
    }
    
    /**
     * 测试Edge的边界值情况
     * 验证零距离和负距离的处理
     */
    @Test
    public void testEdgeBoundaryValues() {
        Node neighbor = new Node(3, false, "Regular Road", false, false, false, 1.0, 0, 24);
        
        // 测试零距离
        Edge zeroDistanceEdge = new Edge(neighbor, 0.0);
        assertEquals("零距离应该正确设置", 0.0, zeroDistanceEdge.getDistance(), 0.001);
        
        // 测试负距离（虽然从业务逻辑上不应该出现，但需要测试代码的鲁棒性）
        Edge negativeDistanceEdge = new Edge(neighbor, -1.0);
        assertEquals("负距离应该正确设置", -1.0, negativeDistanceEdge.getDistance(), 0.001);
        
        // 测试大距离值
        Edge largeDistanceEdge = new Edge(neighbor, Double.MAX_VALUE);
        assertEquals("大距离值应该正确设置", Double.MAX_VALUE, largeDistanceEdge.getDistance(), 0.001);
    }
    
    /**
     * 测试Edge与不同类型节点的组合
     * 验证边可以连接各种类型的节点
     */
    @Test
    public void testEdgeWithDifferentNodeTypes() {
        // 测试与障碍节点的连接
        Node obstacleNode = new Node(1, true, "Blocked", false, false, false, 0.0, 0, 24);
        Edge edgeToObstacle = new Edge(obstacleNode, 10.0);
        assertTrue("障碍节点应该能作为邻居", edgeToObstacle.getNeighbor().isObstacle());
        
        // 测试与收费道路节点的连接
        Node tollRoadNode = new Node(2, false, "Toll Road", true, false, false, 2.0, 0, 24);
        Edge edgeToTollRoad = new Edge(tollRoadNode, 15.0);
        assertTrue("收费道路节点应该能作为邻居", edgeToTollRoad.getNeighbor().isTollRoad());
        
        // 测试与高风险区域节点的连接
        Node highRiskNode = new Node(3, false, "High Risk Area", false, false, true, 3.0, 0, 24);
        Edge edgeToHighRisk = new Edge(highRiskNode, 20.0);
        assertTrue("高风险区域节点应该能作为邻居", edgeToHighRisk.getNeighbor().isHighRiskArea());
    }
}