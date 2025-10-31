package net.mooctest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

/**
 * Node类的测试类
 * 测试节点的所有属性和方法，包括时间窗口、邻居管理等
 */
public class NodeTest {
    
    private Node normalNode;
    private Node obstacleNode;
    private Node tollRoadNode;
    private Node restrictedNode;
    private Node highRiskNode;
    private Node timeRestrictedNode;

    @Before
    public void setUp() {
        // 初始化各种类型的测试节点
        normalNode = new Node(1, false, "Regular Road", false, false, false, 1.0, 0, 24);
        obstacleNode = new Node(2, true, "Blocked", false, false, false, 0.0, 0, 24);
        tollRoadNode = new Node(3, false, "Toll Road", true, false, false, 2.0, 0, 24);
        restrictedNode = new Node(4, false, "Highway", false, true, false, 1.5, 0, 24);
        highRiskNode = new Node(5, false, "High Risk Area", false, false, true, 3.0, 0, 24);
        timeRestrictedNode = new Node(6, false, "Time Restricted", false, false, false, 1.0, 9, 17);
    }

    /**
     * 测试Node构造函数和基本getter方法
     * 验证所有属性都能正确初始化
     */
    @Test
    public void testNodeConstructorAndGetters() {
        // 测试普通节点
        assertEquals("节点ID应该正确设置", 1, normalNode.getNodeId());
        assertFalse("普通节点不应该是障碍", normalNode.isObstacle());
        assertEquals("道路类型应该正确设置", "Regular Road", normalNode.getRoadType());
        assertFalse("普通节点不应该是收费道路", normalNode.isTollRoad());
        assertFalse("普通节点不应该限制重型车辆", normalNode.isRestrictedForHeavyVehicles());
        assertFalse("普通节点不应该是高风险区域", normalNode.isHighRiskArea());
        assertEquals("每公里成本应该正确设置", 1.0, normalNode.getCostPerKm(), 0.001);
        
        // 测试障碍节点
        assertTrue("障碍节点应该是障碍", obstacleNode.isObstacle());
        
        // 测试收费道路节点
        assertTrue("收费道路节点应该是收费道路", tollRoadNode.isTollRoad());
        
        // 测试限制重型车辆节点
        assertTrue("限制节点应该限制重型车辆", restrictedNode.isRestrictedForHeavyVehicles());
        
        // 测试高风险区域节点
        assertTrue("高风险节点应该是高风险区域", highRiskNode.isHighRiskArea());
    }

    /**
     * 测试节点时间窗口功能
     * 验证节点在不同时间的开放状态
     */
    @Test
    public void testNodeTimeWindow() {
        // 测试全天开放的节点
        assertTrue("全天节点应该在0点开放", normalNode.isOpenAt(0));
        assertTrue("全天节点应该在12点开放", normalNode.isOpenAt(12));
        assertTrue("全天节点应该在24点开放", normalNode.isOpenAt(24));
        
        // 测试时间限制节点（9:00-17:00）
        assertFalse("时间限制节点不应该在8点开放", timeRestrictedNode.isOpenAt(8));
        assertTrue("时间限制节点应该在9点开放", timeRestrictedNode.isOpenAt(9));
        assertTrue("时间限制节点应该在12点开放", timeRestrictedNode.isOpenAt(12));
        assertTrue("时间限制节点应该在17点开放", timeRestrictedNode.isOpenAt(17));
        assertFalse("时间限制节点不应该在18点开放", timeRestrictedNode.isOpenAt(18));
        
        // 测试边界值
        Node edgeNode1 = new Node(7, false, "Edge Test", false, false, false, 1.0, 0, 0);
        assertTrue("零时间窗口节点应该在0点开放", edgeNode1.isOpenAt(0));
        assertFalse("零时间窗口节点不应该在1点开放", edgeNode1.isOpenAt(1));
    }

    /**
     * 测试添加邻居功能
     * 验证节点能正确添加和管理邻居节点
     */
    @Test
    public void testAddNeighbor() {
        // 创建邻居节点
        Node neighbor1 = new Node(10, false, "Highway", false, false, false, 0.8, 0, 24);
        Node neighbor2 = new Node(11, false, "Regular Road", false, false, false, 1.2, 0, 24);
        Node obstacleNeighbor = new Node(12, true, "Blocked", false, false, false, 0.0, 0, 24);
        
        // 初始状态应该没有邻居
        assertEquals("初始状态邻居数量应该为0", 0, normalNode.getNeighbors().size());
        
        // 添加正常邻居
        normalNode.addNeighbor(neighbor1, 5.0);
        assertEquals("添加一个邻居后数量应该为1", 1, normalNode.getNeighbors().size());
        
        // 添加第二个邻居
        normalNode.addNeighbor(neighbor2, 3.5);
        assertEquals("添加两个邻居后数量应该为2", 2, normalNode.getNeighbors().size());
        
        // 验证邻居信息
        Edge edge1 = normalNode.getNeighbors().get(0);
        assertSame("第一个邻居应该正确", neighbor1, edge1.getNeighbor());
        assertEquals("第一个邻居距离应该正确", 5.0, edge1.getDistance(), 0.001);
        
        Edge edge2 = normalNode.getNeighbors().get(1);
        assertSame("第二个邻居应该正确", neighbor2, edge2.getNeighbor());
        assertEquals("第二个邻居距离应该正确", 3.5, edge2.getDistance(), 0.001);
        
        // 测试添加障碍节点作为邻居（应该被忽略）
        int neighborCountBefore = normalNode.getNeighbors().size();
        normalNode.addNeighbor(obstacleNeighbor, 10.0);
        assertEquals("障碍节点不应该被添加为邻居", neighborCountBefore, normalNode.getNeighbors().size());
    }

    /**
     * 测试邻居列表的不可变性
     * 验证返回的邻居列表是原始列表的引用
     */
    @Test
    public void testNeighborListReference() {
        Node neighbor = new Node(20, false, "Test Road", false, false, false, 1.0, 0, 24);
        normalNode.addNeighbor(neighbor, 7.5);
        
        // 获取邻居列表并直接修改
        normalNode.getNeighbors().clear();
        
        // 验证邻居列表确实被清空了
        assertEquals("邻居列表应该被清空", 0, normalNode.getNeighbors().size());
    }

    /**
     * 测试边界值和特殊情况
     * 验证极端参数的处理
     */
    @Test
    public void testBoundaryValues() {
        // 测试负数成本
        Node negativeCostNode = new Node(30, false, "Negative Cost", false, false, false, -1.0, 0, 24);
        assertEquals("负成本应该正确设置", -1.0, negativeCostNode.getCostPerKm(), 0.001);
        
        // 测试极大成本
        Node maxCostNode = new Node(31, false, "Max Cost", false, false, false, Double.MAX_VALUE, 0, 24);
        assertEquals("极大成本应该正确设置", Double.MAX_VALUE, maxCostNode.getCostPerKm(), 0.001);
        
        // 测试负数时间窗口
        Node negativeTimeNode = new Node(32, false, "Negative Time", false, false, false, 1.0, -5, 5);
        assertTrue("负时间开放时间应该正确处理", negativeTimeNode.isOpenAt(-3));
        assertFalse("超出范围时间应该返回false", negativeTimeNode.isOpenAt(6));
        
        // 测试零距离邻居
        Node zeroDistanceNeighbor = new Node(33, false, "Zero Distance", false, false, false, 1.0, 0, 24);
        normalNode.addNeighbor(zeroDistanceNeighbor, 0.0);
        assertEquals("零距离邻居应该被添加", 0.0, normalNode.getNeighbors().get(0).getDistance(), 0.001);
    }
}