package net.mooctest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

/**
 * FloydWarshall算法的测试类
 * 测试FloydWarshall算法在各种场景下的最短路径查找功能
 */
public class FloydWarshallTest {
    
    private Graph graph;
    private Node node1;
    private Node node2;
    private Node node3;
    private Node node4;
    private Node node5;

    @Before
    public void setUp() {
        // 创建测试图
        graph = new Graph();
        node1 = new Node(1, false, "Node1", false, false, false, 1.0, 0, 24);
        node2 = new Node(2, false, "Node2", false, false, false, 1.0, 0, 24);
        node3 = new Node(3, false, "Node3", false, false, false, 1.0, 0, 24);
        node4 = new Node(4, false, "Node4", false, false, false, 1.0, 0, 24);
        node5 = new Node(5, false, "Node5", false, false, false, 1.0, 0, 24);
        
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);
        graph.addNode(node5);
        
        // 添加边
        graph.addEdge(1, 2, 10.0);
        graph.addEdge(1, 3, 15.0);
        graph.addEdge(2, 4, 10.0);
        graph.addEdge(3, 4, 5.0);
        graph.addEdge(2, 3, 2.0);
        graph.addEdge(4, 5, 8.0);
        graph.addEdge(3, 5, 12.0);
    }

    /**
     * 测试FloydWarshall构造函数
     * 验证算法能正确初始化
     */
    @Test
    public void testFloydWarshallConstructor() {
        FloydWarshall floydWarshall = new FloydWarshall(graph);
        
        assertNotNull("FloydWarshall算法应该被正确创建", floydWarshall);
    }

    /**
     * 测试FloydWarshall基本路径查找
     * 验证能找到最短路径
     */
    @Test
    public void testFloydWarshallBasicPathFinding() {
        FloydWarshall floydWarshall = new FloydWarshall(graph);
        
        // 测试从节点1到节点4的路径
        FloydWarshallTestPathResult result = floydWarshall.getShortestPath(node1, node4);
        
        assertNotNull("路径结果不应该为null", result);
        assertFalse("路径不应该为空", result.isEmpty());
        
        // 验证路径包含起始和结束节点
        assertEquals("路径应该以起始节点开始", node1, result.get(0));
        assertEquals("路径应该以结束节点结束", node4, result.get(result.size() - 1));
        
        // 验证最短路径：1->2->3->4 (总距离10+2+5=17) 比 1->3->4 (15+5=20) 和 1->2->4 (10+10=20) 更短
        assertEquals("最短路径长度应该为4", 4, result.size());
        assertEquals("第二个节点应该是节点2", node2, result.get(1));
        assertEquals("第三个节点应该是节点3", result.get(2));
    }

    /**
     * 测试FloydWarshall直接路径
     * 验证当存在直接路径时的处理
     */
    @Test
    public void testFloydWarshallDirectPath() {
        // 添加直接路径
        graph.addEdge(1, 4, 5.0);
        
        FloydWarshall floydWarshall = new FloydWarshall(graph);
        
        FloydWarshallTestPathResult result = floydWarshall.getShortestPath(node1, node4);
        
        assertNotNull("直接路径结果不应该为null", result);
        assertEquals("直接路径长度应该为2", 2, result.size());
        assertEquals("直接路径应该以起始节点开始", node1, result.get(0));
        assertEquals("直接路径应该以结束节点结束", node4, result.get(1));
    }

    /**
     * 测试FloydWarshall无路径情况
     * 验证当没有路径时的处理
     */
    @Test
    public void testFloydWarshallNoPath() {
        // 创建没有连接的图
        Graph noPathGraph = new Graph();
        Node isolatedStart = new Node(1, false, "Isolated Start", false, false, false, 1.0, 0, 24);
        Node isolatedEnd = new Node(2, false, "Isolated End", false, false, false, 1.0, 0, 24);
        
        noPathGraph.addNode(isolatedStart);
        noPathGraph.addNode(isolatedEnd);
        
        FloydWarshall floydWarshall = new FloydWarshall(noPathGraph);
        
        FloydWarshallTestPathResult result = floydWarshall.getShortestPath(isolatedStart, isolatedEnd);
        
        assertNotNull("无路径时结果不应该为null", result);
        assertTrue("无路径时应该返回空路径", result.isEmpty());
    }

    /**
     * 测试FloydWarshall相同起始和结束节点
     * 验证当起始和结束节点相同时的处理
     */
    @Test
    public void testFloydWarshallSameStartEnd() {
        FloydWarshall floydWarshall = new FloydWarshall(graph);
        
        FloydWarshallTestPathResult result = floydWarshall.getShortestPath(node1, node1);
        
        assertNotNull("相同节点路径结果不应该为null", result);
        assertEquals("相同节点路径长度应该为1", 1, result.size());
        assertEquals("路径应该只包含起始节点", node1, result.get(0));
    }

    /**
     * 测试FloydWarshall与不同节点组合
     * 验证各种节点组合的路径查找
     */
    @Test
    public void testFloydWarshallDifferentNodeCombinations() {
        FloydWarshall floydWarshall = new FloydWarshall(graph);
        
        // 测试所有节点组合
        FloydWarshallTestPathResult result12 = floydWarshall.getShortestPath(node1, node2);
        FloydWarshallTestPathResult result13 = floydWarshall.getShortestPath(node1, node3);
        FloydWarshallTestPathResult result14 = floydWarshall.getShortestPath(node1, node4);
        FloydWarshallTestPathResult result15 = floydWarshall.getShortestPath(node1, node5);
        FloydWarshallTestPathResult result23 = floydWarshall.getShortestPath(node2, node3);
        FloydWarshallTestPathResult result24 = floydWarshall.getShortestPath(node2, node4);
        FloydWarshallTestPathResult result25 = floydWarshall.getShortestPath(node2, node5);
        FloydWarshallTestPathResult result34 = floydWarshall.getShortestPath(node3, node4);
        FloydWarshallTestPathResult result35 = floydWarshall.getShortestPath(node3, node5);
        FloydWarshallTestPathResult result45 = floydWarshall.getShortestPath(node4, node5);
        
        // 验证所有结果都不为null
        assertNotNull("1->2路径不应该为null", result12);
        assertNotNull("1->3路径不应该为null", result13);
        assertNotNull("1->4路径不应该为null", result14);
        assertNotNull("1->5路径不应该为null", result15);
        assertNotNull("2->3路径不应该为null", result23);
        assertNotNull("2->4路径不应该为null", result24);
        assertNotNull("2->5路径不应该为null", result25);
        assertNotNull("3->4路径不应该为null", result34);
        assertNotNull("3->5路径不应该为null", result35);
        assertNotNull("4->5路径不应该为null", result45);
        
        // 验证直接连接的路径
        assertEquals("1->2直接路径长度应该为2", 2, result12.size());
        assertEquals("2->4直接路径长度应该为2", 2, result24.size());
        assertEquals("4->5直接路径长度应该为2", 2, result45.size());
    }

    /**
     * 测试FloydWarshall与空图
     * 验证空图的处理
     */
    @Test
    public void testFloydWarshallEmptyGraph() {
        Graph emptyGraph = new Graph();
        
        FloydWarshall floydWarshall = new FloydWarshall(emptyGraph);
        
        assertNotNull("空图FloydWarshall应该被正确创建", floydWarshall);
    }

    /**
     * 测试FloydWarshall与单节点图
     * 验证单节点图的处理
     */
    @Test
    public void testFloydWarshallSingleNodeGraph() {
        Graph singleNodeGraph = new Graph();
        Node singleNode = new Node(1, false, "Single", false, false, false, 1.0, 0, 24);
        singleNodeGraph.addNode(singleNode);
        
        FloydWarshall floydWarshall = new FloydWarshall(singleNodeGraph);
        
        FloydWarshallTestPathResult result = floydWarshall.getShortestPath(singleNode, singleNode);
        
        assertNotNull("单节点路径结果不应该为null", result);
        assertEquals("单节点路径长度应该为1", 1, result.size());
        assertEquals("单节点路径应该包含该节点", singleNode, result.get(0));
    }

    /**
     * 测试FloydWarshall与两节点图
     * 验证两节点图的处理
     */
    @Test
    public void testFloydWarshallTwoNodeGraph() {
        Graph twoNodeGraph = new Graph();
        Node nodeA = new Node(1, false, "A", false, false, false, 1.0, 0, 24);
        Node nodeB = new Node(2, false, "B", false, false, false, 1.0, 0, 24);
        
        twoNodeGraph.addNode(nodeA);
        twoNodeGraph.addNode(nodeB);
        twoNodeGraph.addEdge(1, 2, 10.0);
        
        FloydWarshall floydWarshall = new FloydWarshall(twoNodeGraph);
        
        FloydWarshallTestPathResult result = floydWarshall.getShortestPath(nodeA, nodeB);
        
        assertNotNull("两节点路径结果不应该为null", result);
        assertEquals("两节点路径长度应该为2", 2, result.size());
        assertEquals("两节点路径应该以起始节点开始", nodeA, result.get(0));
        assertEquals("两节点路径应该以结束节点结束", nodeB, result.get(1));
    }

    /**
     * 测试FloydWarshall与复杂图
     * 验证在复杂图结构中的表现
     */
    @Test
    public void testFloydWarshallComplexGraph() {
        // 创建更复杂的图
        Graph complexGraph = new Graph();
        for (int i = 1; i <= 8; i++) {
            complexGraph.addNode(new Node(i, false, "Node" + i, false, false, false, 1.0, 0, 24));
        }
        
        // 添加复杂的连接
        complexGraph.addEdge(1, 2, 8.0);
        complexGraph.addEdge(1, 3, 12.0);
        complexGraph.addEdge(2, 4, 6.0);
        complexGraph.addEdge(3, 4, 5.0);
        complexGraph.addEdge(2, 5, 10.0);
        complexGraph.addEdge(4, 6, 7.0);
        complexGraph.addEdge(5, 6, 4.0);
        complexGraph.addEdge(4, 7, 8.0);
        complexGraph.addEdge(6, 8, 5.0);
        complexGraph.addEdge(7, 8, 3.0);
        
        Node complexStart = complexGraph.getNode(1);
        Node complexEnd = complexGraph.getNode(8);
        
        FloydWarshall complexFloydWarshall = new FloydWarshall(complexGraph);
        
        FloydWarshallTestPathResult complexResult = complexFloydWarshall.getShortestPath(complexStart, complexEnd);
        
        assertNotNull("复杂图路径结果不应该为null", complexResult);
        assertFalse("复杂图路径不应该为空", complexResult.isEmpty());
        
        assertEquals("复杂图路径应该以起始节点开始", complexStart, complexResult.get(0));
        assertEquals("复杂图路径应该以结束节点结束", complexEnd, complexResult.get(complexResult.size() - 1));
    }

    /**
     * 测试FloydWarshall与特殊节点类型
     * 验证对特殊类型节点的处理
     */
    @Test
    public void testFloydWarshallWithSpecialNodeTypes() {
        // 创建包含特殊节点的图
        Graph specialGraph = new Graph();
        Node obstacleNode = new Node(1, true, "Obstacle", false, false, false, 0.0, 0, 24);
        Node tollNode = new Node(2, false, "Toll", true, false, false, 2.0, 0, 24);
        Node restrictedNode = new Node(3, false, "Restricted", false, true, false, 1.5, 0, 24);
        Node highRiskNode = new Node(4, false, "High Risk", false, false, true, 3.0, 0, 24);
        
        specialGraph.addNode(obstacleNode);
        specialGraph.addNode(tollNode);
        specialGraph.addNode(restrictedNode);
        specialGraph.addNode(highRiskNode);
        
        // 添加连接
        specialGraph.addEdge(1, 2, 10.0);
        specialGraph.addEdge(2, 3, 10.0);
        specialGraph.addEdge(3, 4, 10.0);
        
        FloydWarshall specialFloydWarshall = new FloydWarshall(specialGraph);
        
        FloydWarshallTestPathResult result = specialFloydWarshall.getShortestPath(obstacleNode, highRiskNode);
        
        assertNotNull("特殊节点路径结果不应该为null", result);
        
        // 验证路径包含起始和结束节点
        if (!result.isEmpty()) {
            assertEquals("路径应该以起始节点开始", obstacleNode, result.get(0));
            assertEquals("路径应该以结束节点结束", highRiskNode, result.get(result.size() - 1));
        }
    }

    /**
     * 测试FloydWarshall的printDistanceMatrix方法
     * 验证距离矩阵打印功能
     */
    @Test
    public void testFloydWarshallPrintDistanceMatrix() {
        FloydWarshall floydWarshall = new FloydWarshall(graph);
        
        try {
            floydWarshall.printDistanceMatrix();
            // 如果没有抛出异常，则测试通过
            assertTrue("printDistanceMatrix应该正常执行", true);
        } catch (Exception e) {
            fail("printDistanceMatrix不应该抛出异常: " + e.getMessage());
        }
    }

    /**
     * 测试FloydWarshall与null参数
     * 验证null参数的处理
     */
    @Test
    public void testFloydWarshallWithNullParameters() {
        FloydWarshall floydWarshall = new FloydWarshall(graph);
        
        try {
            FloydWarshallTestPathResult result1 = floydWarshall.getShortestPath(null, node2);
            assertTrue("null起始节点应该被处理", result1 == null || result1.isEmpty());
            
            FloydWarshallTestPathResult result2 = floydWarshall.getShortestPath(node1, null);
            assertTrue("null结束节点应该被处理", result2 == null || result2.isEmpty());
            
            FloydWarshallTestPathResult result3 = floydWarshall.getShortestPath(null, null);
            assertTrue("两个null节点应该被处理", result3 == null || result3.isEmpty());
        } catch (Exception e) {
            fail("null参数不应该导致异常: " + e.getMessage());
        }
    }

    /**
     * 测试FloydWarshall的多次调用
     * 验证多次调用的结果一致性
     */
    @Test
    public void testFloydWarshallMultipleCalls() {
        FloydWarshall floydWarshall = new FloydWarshall(graph);
        
        // 多次调用getShortestPath
        FloydWarshallTestPathResult result1 = floydWarshall.getShortestPath(node1, node4);
        FloydWarshallTestPathResult result2 = floydWarshall.getShortestPath(node1, node4);
        FloydWarshallTestPathResult result3 = floydWarshall.getShortestPath(node1, node4);
        
        assertNotNull("第一次调用结果不应该为null", result1);
        assertNotNull("第二次调用结果不应该为null", result2);
        assertNotNull("第三次调用结果不应该为null", result3);
        
        // 验证结果的一致性
        assertEquals("多次调用路径长度应该相同", result1.size(), result2.size());
        assertEquals("多次调用路径长度应该相同", result2.size(), result3.size());
        
        // 验证路径内容的一致性
        for (int i = 0; i < result1.size(); i++) {
            assertEquals("多次调用路径内容应该相同", result1.get(i), result2.get(i));
            assertEquals("多次调用路径内容应该相同", result2.get(i), result3.get(i));
        }
    }

    /**
     * 测试FloydWarshall的边界情况
     * 验证各种边界条件的处理
     */
    @Test
    public void testFloydWarshallBoundaryConditions() {
        // 测试大权重边
        Graph largeWeightGraph = new Graph();
        Node nodeA = new Node(1, false, "A", false, false, false, 1.0, 0, 24);
        Node nodeB = new Node(2, false, "B", false, false, false, 1.0, 0, 24);
        
        largeWeightGraph.addNode(nodeA);
        largeWeightGraph.addNode(nodeB);
        largeWeightGraph.addEdge(1, 2, Double.MAX_VALUE);
        
        FloydWarshall largeWeightFloydWarshall = new FloydWarshall(largeWeightGraph);
        
        FloydWarshallTestPathResult largeWeightResult = largeWeightFloydWarshall.getShortestPath(nodeA, nodeB);
        
        assertNotNull("大权重路径结果不应该为null", largeWeightResult);
        assertEquals("大权重路径长度应该为2", 2, largeWeightResult.size());
        
        // 测试零权重边
        Graph zeroWeightGraph = new Graph();
        Node nodeC = new Node(1, false, "C", false, false, false, 1.0, 0, 24);
        Node nodeD = new Node(2, false, "D", false, false, false, 1.0, 0, 24);
        
        zeroWeightGraph.addNode(nodeC);
        zeroWeightGraph.addNode(nodeD);
        zeroWeightGraph.addEdge(1, 2, 0.0);
        
        FloydWarshall zeroWeightFloydWarshall = new FloydWarshall(zeroWeightGraph);
        
        FloydWarshallTestPathResult zeroWeightResult = zeroWeightFloydWarshall.getShortestPath(nodeC, nodeD);
        
        assertNotNull("零权重路径结果不应该为null", zeroWeightResult);
        assertEquals("零权重路径长度应该为2", 2, zeroWeightResult.size());
    }

    /**
     * 辅助类，用于测试FloydWarshall返回的路径结果
     */
    private static class FloydWarshallTestPathResult extends java.util.ArrayList<Node> {
        public FloydWarshallTestPathResult() {
            super();
        }
        
        public FloydWarshallTestPathResult(java.util.List<Node> nodes) {
            super(nodes);
        }
    }
}