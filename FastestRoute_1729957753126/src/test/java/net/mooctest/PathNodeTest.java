package net.mooctest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

/**
 * PathNode类的测试类
 * 测试路径节点的数据结构和构造函数
 */
public class PathNodeTest {
    
    private Node testNode;
    private Node startNode;
    private Node endNode;

    @Before
    public void setUp() {
        testNode = new Node(5, false, "Test Node", false, false, false, 1.0, 0, 24);
        startNode = new Node(1, false, "Start", false, false, false, 1.0, 0, 24);
        endNode = new Node(10, false, "End", false, false, false, 1.0, 0, 24);
    }

    /**
     * 测试PathNode两个参数的构造函数
     * 验证节点和距离能正确初始化，estimatedTotalDistance等于distance
     */
    @Test
    public void testPathNodeTwoParameterConstructor() {
        double distance = 15.5;
        PathNode pathNode = new PathNode(testNode, distance);
        
        // 验证节点
        assertSame("节点应该正确设置", testNode, pathNode.getNode());
        
        // 验证距离
        assertEquals("距离应该正确设置", distance, pathNode.getDistance(), 0.001);
        
        // 验证估计总距离（应该等于距离）
        assertEquals("估计总距离应该等于距离", distance, pathNode.getEstimatedTotalDistance(), 0.001);
    }

    /**
     * 测试PathNode三个参数的构造函数
     * 验证节点、距离和估计总距离能正确初始化
     */
    @Test
    public void testPathNodeThreeParameterConstructor() {
        double distance = 10.0;
        double estimatedTotalDistance = 25.5;
        PathNode pathNode = new PathNode(testNode, distance, estimatedTotalDistance);
        
        // 验证节点
        assertSame("节点应该正确设置", testNode, pathNode.getNode());
        
        // 验证距离
        assertEquals("距离应该正确设置", distance, pathNode.getDistance(), 0.001);
        
        // 验证估计总距离
        assertEquals("估计总距离应该正确设置", estimatedTotalDistance, pathNode.getEstimatedTotalDistance(), 0.001);
    }

    /**
     * 测试PathNode的边界值
     * 验证各种极端值的处理
     */
    @Test
    public void testPathNodeBoundaryValues() {
        // 测试零值
        PathNode zeroPathNode = new PathNode(testNode, 0.0);
        assertEquals("零距离应该正确设置", 0.0, zeroPathNode.getDistance(), 0.001);
        assertEquals("零估计总距离应该正确设置", 0.0, zeroPathNode.getEstimatedTotalDistance(), 0.001);
        
        // 测试负值
        PathNode negativePathNode = new PathNode(testNode, -5.0);
        assertEquals("负距离应该正确设置", -5.0, negativePathNode.getDistance(), 0.001);
        assertEquals("负估计总距离应该正确设置", -5.0, negativePathNode.getEstimatedTotalDistance(), 0.001);
        
        // 测试极大值
        PathNode maxPathNode = new PathNode(testNode, Double.MAX_VALUE);
        assertEquals("极大距离应该正确设置", Double.MAX_VALUE, maxPathNode.getDistance(), 0.001);
        assertEquals("极大估计总距离应该正确设置", Double.MAX_VALUE, maxPathNode.getEstimatedTotalDistance(), 0.001);
        
        // 测试极小正值
        PathNode minPathNode = new PathNode(testNode, Double.MIN_VALUE);
        assertEquals("极小距离应该正确设置", Double.MIN_VALUE, minPathNode.getDistance(), 0.001);
        assertEquals("极小估计总距离应该正确设置", Double.MIN_VALUE, minPathNode.getEstimatedTotalDistance(), 0.001);
        
        // 测试无穷大值
        PathNode infinityPathNode = new PathNode(testNode, Double.POSITIVE_INFINITY);
        assertTrue("无穷大距离应该正确设置", Double.isInfinite(infinityPathNode.getDistance()));
        assertTrue("无穷大估计总距离应该正确设置", Double.isInfinite(infinityPathNode.getEstimatedTotalDistance()));
    }

    /**
     * 测试PathNode与不同类型的节点
     * 验证能与各种类型节点配合工作
     */
    @Test
    public void testPathNodeWithDifferentNodeTypes() {
        // 测试与障碍节点
        Node obstacleNode = new Node(2, true, "Obstacle", false, false, false, 0.0, 0, 24);
        PathNode obstaclePathNode = new PathNode(obstacleNode, 5.0);
        assertTrue("障碍节点应该正确设置", obstaclePathNode.getNode().isObstacle());
        
        // 测试与收费道路节点
        Node tollNode = new Node(3, false, "Toll Road", true, false, false, 2.0, 0, 24);
        PathNode tollPathNode = new PathNode(tollNode, 8.0);
        assertTrue("收费道路节点应该正确设置", tollPathNode.getNode().isTollRoad());
        
        // 测试与限制重型车辆节点
        Node restrictedNode = new Node(4, false, "Restricted", false, true, false, 1.5, 0, 24);
        PathNode restrictedPathNode = new PathNode(restrictedNode, 12.0);
        assertTrue("限制节点应该正确设置", restrictedPathNode.getNode().isRestrictedForHeavyVehicles());
        
        // 测试与高风险区域节点
        Node highRiskNode = new Node(5, false, "High Risk", false, false, true, 3.0, 0, 24);
        PathNode highRiskPathNode = new PathNode(highRiskNode, 15.0);
        assertTrue("高风险节点应该正确设置", highRiskPathNode.getNode().isHighRiskArea());
    }

    /**
     * 测试PathNode距离和估计总距离的不同组合
     * 验证各种数值组合的正确性
     */
    @Test
    public void testPathNodeDistanceCombinations() {
        // 测试估计总距离大于距离的情况
        PathNode greaterEstimatePathNode = new PathNode(testNode, 10.0, 20.0);
        assertEquals("距离应该正确", 10.0, greaterEstimatePathNode.getDistance(), 0.001);
        assertEquals("估计总距离应该正确", 20.0, greaterEstimatePathNode.getEstimatedTotalDistance(), 0.001);
        assertTrue("估计总距离应该大于距离", 
                  greaterEstimatePathNode.getEstimatedTotalDistance() > greaterEstimatePathNode.getDistance());
        
        // 测试估计总距离小于距离的情况
        PathNode smallerEstimatePathNode = new PathNode(testNode, 20.0, 10.0);
        assertEquals("距离应该正确", 20.0, smallerEstimatePathNode.getDistance(), 0.001);
        assertEquals("估计总距离应该正确", 10.0, smallerEstimatePathNode.getEstimatedTotalDistance(), 0.001);
        assertTrue("估计总距离应该小于距离", 
                  smallerEstimatePathNode.getEstimatedTotalDistance() < smallerEstimatePathNode.getDistance());
        
        // 测试估计总距离等于距离的情况
        PathNode equalPathNode = new PathNode(testNode, 15.0, 15.0);
        assertEquals("距离应该正确", 15.0, equalPathNode.getDistance(), 0.001);
        assertEquals("估计总距离应该正确", 15.0, equalPathNode.getEstimatedTotalDistance(), 0.001);
        assertEquals("估计总距离应该等于距离", 
                    equalPathNode.getEstimatedTotalDistance(), equalPathNode.getDistance(), 0.001);
    }

    /**
     * 测试PathNode的null节点处理
     * 验证传入null节点时的行为
     */
    @Test
    public void testPathNodeWithNullNode() {
        try {
            PathNode nullNodePathNode = new PathNode(null, 5.0);
            assertNull("null节点应该被正确处理", nullNodePathNode.getNode());
            assertEquals("距离应该正确设置", 5.0, nullNodePathNode.getDistance(), 0.001);
            assertEquals("估计总距离应该正确设置", 5.0, nullNodePathNode.getEstimatedTotalDistance(), 0.001);
        } catch (Exception e) {
            fail("null节点不应该导致异常: " + e.getMessage());
        }
        
        try {
            PathNode nullNodePathNode2 = new PathNode(null, 5.0, 10.0);
            assertNull("null节点应该被正确处理", nullNodePathNode2.getNode());
            assertEquals("距离应该正确设置", 5.0, nullNodePathNode2.getDistance(), 0.001);
            assertEquals("估计总距离应该正确设置", 10.0, nullNodePathNode2.getEstimatedTotalDistance(), 0.001);
        } catch (Exception e) {
            fail("null节点不应该导致异常: " + e.getMessage());
        }
    }

    /**
     * 测试PathNode的精度问题
     * 验证浮点数精度处理
     */
    @Test
    public void testPathNodePrecision() {
        // 测试小数精度
        double preciseDistance = 1.23456789;
        PathNode precisePathNode = new PathNode(testNode, preciseDistance);
        assertEquals("精确距离应该正确设置", preciseDistance, precisePathNode.getDistance(), 0.0000001);
        assertEquals("精确估计总距离应该正确设置", preciseDistance, precisePathNode.getEstimatedTotalDistance(), 0.0000001);
        
        // 测试非常小的差值
        PathNode tinyDifferencePathNode = new PathNode(testNode, 1.0, 1.0000001);
        assertEquals("微小差值应该被正确识别", 1.0000001, tinyDifferencePathNode.getEstimatedTotalDistance(), 0.0000001);
        
        // 测试科学计数法
        PathNode scientificPathNode = new PathNode(testNode, 1.5e-10, 2.5e-10);
        assertEquals("科学计数法距离应该正确", 1.5e-10, scientificPathNode.getDistance(), 1e-15);
        assertEquals("科学计数法估计总距离应该正确", 2.5e-10, scientificPathNode.getEstimatedTotalDistance(), 1e-15);
    }

    /**
     * 测试PathNode的一致性
     * 验证两个参数构造函数的行为一致性
     */
    @Test
    public void testPathNodeConstructorConsistency() {
        // 使用两个参数构造函数
        PathNode twoParamPathNode = new PathNode(testNode, 15.5);
        
        // 使用三个参数构造函数，设置相同的估计总距离
        PathNode threeParamPathNode = new PathNode(testNode, 15.5, 15.5);
        
        // 验证两个构造函数产生相同结果
        assertEquals("两个构造函数的距离应该相同", 
                    twoParamPathNode.getDistance(), threeParamPathNode.getDistance(), 0.001);
        assertEquals("两个构造函数的估计总距离应该相同", 
                    twoParamPathNode.getEstimatedTotalDistance(), threeParamPathNode.getEstimatedTotalDistance(), 0.001);
        assertSame("两个构造函数的节点应该是同一个", 
                  twoParamPathNode.getNode(), threeParamPathNode.getNode());
    }
}