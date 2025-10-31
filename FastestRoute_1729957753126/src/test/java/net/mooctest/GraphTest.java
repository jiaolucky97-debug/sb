package net.mooctest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

/**
 * Graph类的测试类
 * 测试图的基本功能，包括节点管理和边管理
 */
public class GraphTest {
    
    private Graph graph;
    private Node node1;
    private Node node2;
    private Node node3;

    @Before
    public void setUp() {
        graph = new Graph();
        node1 = new Node(1, false, "Regular Road", false, false, false, 1.0, 0, 24);
        node2 = new Node(2, false, "Highway", false, false, false, 0.8, 0, 24);
        node3 = new Node(3, false, "Toll Road", true, false, false, 2.0, 0, 24);
    }

    /**
     * 测试Graph构造函数
     * 验证新创建的图是空的
     */
    @Test
    public void testGraphConstructor() {
        Graph newGraph = new Graph();
        assertNotNull("图应该被正确创建", newGraph);
        assertEquals("新图应该没有节点", 0, newGraph.getNodes().size());
    }

    /**
     * 测试添加节点功能
     * 验证节点能正确添加到图中
     */
    @Test
    public void testAddNode() {
        // 初始状态应该没有节点
        assertEquals("初始状态节点数量应该为0", 0, graph.getNodes().size());
        
        // 添加第一个节点
        graph.addNode(node1);
        assertEquals("添加一个节点后数量应该为1", 1, graph.getNodes().size());
        assertSame("添加的节点应该能被正确获取", node1, graph.getNode(1));
        
        // 添加第二个节点
        graph.addNode(node2);
        assertEquals("添加两个节点后数量应该为2", 2, graph.getNodes().size());
        assertSame("第二个节点应该能被正确获取", node2, graph.getNode(2));
        
        // 测试添加重复节点（应该覆盖原有节点）
        Node newNode1 = new Node(1, true, "New Type", false, false, false, 5.0, 0, 24);
        graph.addNode(newNode1);
        assertEquals("添加重复节点后数量应该不变", 2, graph.getNodes().size());
        assertSame("重复节点应该被覆盖", newNode1, graph.getNode(1));
        assertTrue("新节点应该被正确设置", graph.getNode(1).isObstacle());
    }

    /**
     * 测试获取节点功能
     * 验证能正确获取存在的和不存在的节点
     */
    @Test
    public void testGetNode() {
        // 添加测试节点
        graph.addNode(node1);
        graph.addNode(node2);
        
        // 测试获取存在的节点
        assertSame("应该能获取存在的节点", node1, graph.getNode(1));
        assertSame("应该能获取存在的节点", node2, graph.getNode(2));
        
        // 测试获取不存在的节点
        assertNull("不存在的节点应该返回null", graph.getNode(99));
        assertNull("负数ID节点应该返回null", graph.getNode(-1));
        assertNull("零ID节点应该返回null", graph.getNode(0));
    }

    /**
     * 测试添加边功能
     * 验证边能正确添加到图中
     */
    @Test
    public void testAddEdge() {
        // 添加节点
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        
        // 添加边
        graph.addEdge(1, 2, 10.5);
        
        // 验证边被正确添加
        assertEquals("节点1应该有一个邻居", 1, node1.getNeighbors().size());
        Edge edge = node1.getNeighbors().get(0);
        assertSame("邻居应该是节点2", node2, edge.getNeighbor());
        assertEquals("边距离应该正确", 10.5, edge.getDistance(), 0.001);
        
        // 添加第二条边
        graph.addEdge(1, 3, 15.0);
        assertEquals("节点1应该有两个邻居", 2, node1.getNeighbors().size());
        
        // 添加反向边
        graph.addEdge(2, 1, 8.0);
        assertEquals("节点2应该有一个邻居", 1, node2.getNeighbors().size());
    }

    /**
     * 测试添加边时的边界情况
     * 验证各种异常情况的处理
     */
    @Test
    public void testAddEdgeBoundaryCases() {
        // 添加节点
        graph.addNode(node1);
        graph.addNode(node2);
        
        // 测试添加不存在的源节点的边
        graph.addEdge(99, 1, 5.0);
        assertEquals("不存在的源节点不应该创建边", 0, node1.getNeighbors().size());
        
        // 测试添加不存在的目标节点的边
        graph.addEdge(1, 99, 5.0);
        assertEquals("不存在的目标节点不应该创建边", 0, node1.getNeighbors().size());
        
        // 测试两个节点都不存在的情况
        graph.addEdge(98, 99, 5.0);
        assertEquals("两个节点都不存在时不应该创建边", 0, node1.getNeighbors().size());
        
        // 测试负距离边
        graph.addEdge(1, 2, -5.0);
        assertEquals("负距离边应该被创建", 1, node1.getNeighbors().size());
        assertEquals("负距离应该正确设置", -5.0, node1.getNeighbors().get(0).getDistance(), 0.001);
        
        // 测试零距离边
        graph.addEdge(1, 2, 0.0);
        assertEquals("零距离边应该被创建", 1, node1.getNeighbors().size());
        assertEquals("零距离应该正确设置", 0.0, node1.getNeighbors().get(0).getDistance(), 0.001);
    }

    /**
     * 测试添加边到障碍节点
     * 验证目标节点为障碍时边的处理
     */
    @Test
    public void testAddEdgeToObstacleNode() {
        Node obstacleNode = new Node(4, true, "Obstacle", false, false, false, 0.0, 0, 24);
        
        graph.addNode(node1);
        graph.addNode(obstacleNode);
        
        // 添加边到障碍节点
        graph.addEdge(1, 4, 10.0);
        
        // 根据Node.addNeighbor的逻辑，障碍节点不会被添加为邻居
        assertEquals("障碍节点不应该被添加为邻居", 0, node1.getNeighbors().size());
    }

    /**
     * 测试获取所有节点功能
     * 验证能正确获取图中所有节点的映射
     */
    @Test
    public void testGetNodes() {
        // 初始状态
        assertEquals("初始节点映射应该为空", 0, graph.getNodes().size());
        
        // 添加节点
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        
        // 验证节点映射
        assertEquals("节点映射大小应该正确", 3, graph.getNodes().size());
        assertSame("映射应该包含节点1", node1, graph.getNodes().get(1));
        assertSame("映射应该包含节点2", node2, graph.getNodes().get(2));
        assertSame("映射应该包含节点3", node3, graph.getNodes().get(3));
        
        // 测试返回的映射是原始映射的引用
        graph.getNodes().clear();
        assertEquals("清空映射应该影响原对象", 0, graph.getNodes().size());
    }

    /**
     * 测试复杂图结构
     * 验证图能处理复杂的连接关系
     */
    @Test
    public void testComplexGraphStructure() {
        // 创建更多节点
        Node node4 = new Node(4, false, "Highway", false, false, false, 0.7, 0, 24);
        Node node5 = new Node(5, false, "Regular Road", false, false, false, 1.2, 0, 24);
        
        // 添加所有节点
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);
        graph.addNode(node5);
        
        // 创建复杂的连接
        graph.addEdge(1, 2, 10.0);   // 1 -> 2
        graph.addEdge(1, 3, 15.0);   // 1 -> 3
        graph.addEdge(2, 4, 8.0);    // 2 -> 4
        graph.addEdge(3, 5, 12.0);   // 3 -> 5
        graph.addEdge(4, 5, 6.0);    // 4 -> 5
        graph.addEdge(5, 1, 20.0);   // 5 -> 1 (创建环)
        
        // 验证连接
        assertEquals("节点1应该有2个邻居", 2, node1.getNeighbors().size());
        assertEquals("节点2应该有1个邻居", 1, node2.getNeighbors().size());
        assertEquals("节点3应该有1个邻居", 1, node3.getNeighbors().size());
        assertEquals("节点4应该有1个邻居", 1, node4.getNeighbors().size());
        assertEquals("节点5应该有1个邻居", 1, node5.getNeighbors().size());
        
        // 验证特定连接
        boolean found = false;
        for (Edge edge : node1.getNeighbors()) {
            if (edge.getNeighbor().getNodeId() == 2 && edge.getDistance() == 10.0) {
                found = true;
                break;
            }
        }
        assertTrue("应该找到1->2的连接", found);
    }
}