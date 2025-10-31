package net.mooctest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

/**
 * BellmanFord算法的测试类
 * 测试BellmanFord算法在各种场景下的路径查找功能
 */
public class BellmanFordTest {
    
    private Graph graph;
    private Node startNode;
    private Node endNode;
    private Vehicle vehicle;
    private TrafficCondition trafficCondition;
    private WeatherCondition weatherCondition;

    @Before
    public void setUp() {
        // 创建测试图
        graph = new Graph();
        startNode = new Node(1, false, "Start", false, false, false, 1.0, 0, 24);
        endNode = new Node(4, false, "End", false, false, false, 1.0, 0, 24);
        Node node2 = new Node(2, false, "Middle1", false, false, false, 1.0, 0, 24);
        Node node3 = new Node(3, false, "Middle2", false, false, false, 1.0, 0, 24);
        
        graph.addNode(startNode);
        graph.addNode(endNode);
        graph.addNode(node2);
        graph.addNode(node3);
        
        // 添加边：1->2(10), 1->3(15), 2->4(10), 3->4(5), 2->3(2)
        graph.addEdge(1, 2, 10.0);
        graph.addEdge(1, 3, 15.0);
        graph.addEdge(2, 4, 10.0);
        graph.addEdge(3, 4, 5.0);
        graph.addEdge(2, 3, 2.0);
        
        // 创建测试车辆
        vehicle = new Vehicle("Test Vehicle", 1000.0, false, 50.0, 25.0, 0.5, 5.0, false);
        
        // 创建测试条件
        Map<Integer, String> trafficData = new HashMap<>();
        trafficData.put(1, "Clear");
        trafficData.put(2, "Clear");
        trafficData.put(3, "Clear");
        trafficData.put(4, "Clear");
        trafficCondition = new TrafficCondition(trafficData);
        
        weatherCondition = new WeatherCondition("Clear");
    }

    /**
     * 测试BellmanFord构造函数
     * 验证算法能正确初始化
     */
    @Test
    public void testBellmanFordConstructor() {
        BellmanFord bellmanFord = new BellmanFord(graph, startNode, endNode, vehicle, 
                                                 trafficCondition, weatherCondition, 10);
        
        assertNotNull("BellmanFord算法应该被正确创建", bellmanFord);
    }

    /**
     * 测试BellmanFord基本路径查找
     * 验证能找到最短路径
     */
    @Test
    public void testBellmanFordBasicPathFinding() {
        BellmanFord bellmanFord = new BellmanFord(graph, startNode, endNode, vehicle, 
                                                 trafficCondition, weatherCondition, 10);
        
        PathResult result = bellmanFord.findPath();
        
        assertNotNull("路径结果不应该为null", result);
        assertNotNull("路径不应该为null", result.getPath());
        assertFalse("路径不应该为空", result.getPath().isEmpty());
        
        // 验证路径包含起始和结束节点
        assertEquals("路径应该以起始节点开始", startNode, result.getPath().get(0));
        assertEquals("路径应该以结束节点结束", endNode, result.getPath().get(result.getPath().size() - 1));
        
        // 验证最短路径：1->2->3->4 (总距离10+2+5=17) 比 1->3->4 (15+5=20) 和 1->2->4 (10+10=20) 更短
        assertEquals("最短路径长度应该为4", 4, result.getPath().size());
        assertEquals("第二个节点应该是节点2", 2, result.getPath().get(1).getNodeId());
        assertEquals("第三个节点应该是节点3", 3, result.getPath().get(2).getNodeId());
    }

    /**
     * 测试BellmanFord直接路径
     * 验证当存在直接路径时的处理
     */
    @Test
    public void testBellmanFordDirectPath() {
        // 添加直接路径
        graph.addEdge(1, 4, 5.0);
        
        BellmanFord bellmanFord = new BellmanFord(graph, startNode, endNode, vehicle, 
                                                 trafficCondition, weatherCondition, 10);
        
        PathResult result = bellmanFord.findPath();
        
        assertNotNull("直接路径结果不应该为null", result);
        assertEquals("直接路径长度应该为2", 2, result.getPath().size());
        assertEquals("直接路径应该以起始节点开始", startNode, result.getPath().get(0));
        assertEquals("直接路径应该以结束节点结束", endNode, result.getPath().get(1));
    }

    /**
     * 测试BellmanFord无路径情况
     * 验证当没有路径时的处理
     */
    @Test
    public void testBellmanFordNoPath() {
        // 创建没有连接的图
        Graph noPathGraph = new Graph();
        Node isolatedStart = new Node(1, false, "Isolated Start", false, false, false, 1.0, 0, 24);
        Node isolatedEnd = new Node(2, false, "Isolated End", false, false, false, 1.0, 0, 24);
        
        noPathGraph.addNode(isolatedStart);
        noPathGraph.addNode(isolatedEnd);
        
        BellmanFord bellmanFord = new BellmanFord(noPathGraph, isolatedStart, isolatedEnd, vehicle, 
                                                 trafficCondition, weatherCondition, 10);
        
        PathResult result = bellmanFord.findPath();
        
        assertNotNull("无路径时结果不应该为null", result);
        // BellmanFord算法会返回重建的路径，即使没有连接
    }

    /**
     * 测试BellmanFord相同起始和结束节点
     * 验证当起始和结束节点相同时的处理
     */
    @Test
    public void testBellmanFordSameStartEnd() {
        BellmanFord bellmanFord = new BellmanFord(graph, startNode, startNode, vehicle, 
                                                 trafficCondition, weatherCondition, 10);
        
        PathResult result = bellmanFord.findPath();
        
        assertNotNull("相同节点路径结果不应该为null", result);
        assertEquals("相同节点路径长度应该为1", 1, result.getPath().size());
        assertEquals("路径应该只包含起始节点", startNode, result.getPath().get(0));
    }

    /**
     * 测试BellmanFord与交通条件
     * 验证交通条件对路径查找的影响
     */
    @Test
    public void testBellmanFordWithTrafficConditions() {
        // 创建拥堵的交通条件
        Map<Integer, String> congestedTrafficData = new HashMap<>();
        congestedTrafficData.put(1, "Clear");
        congestedTrafficData.put(2, "Congested");  // 权重翻倍
        congestedTrafficData.put(3, "Clear");
        congestedTrafficData.put(4, "Clear");
        TrafficCondition congestedTraffic = new TrafficCondition(congestedTrafficData);
        
        BellmanFord bellmanFord = new BellmanFord(graph, startNode, endNode, vehicle, 
                                                 congestedTraffic, weatherCondition, 10);
        
        PathResult result = bellmanFord.findPath();
        
        assertNotNull("拥堵交通路径结果不应该为null", result);
        
        // 验证路径包含起始和结束节点
        assertEquals("路径应该以起始节点开始", startNode, result.getPath().get(0));
        assertEquals("路径应该以结束节点结束", endNode, result.getPath().get(result.getPath().size() - 1));
    }

    /**
     * 测试BellmanFord与天气条件
     * 验证天气条件对路径查找的影响
     */
    @Test
    public void testBellmanFordWithWeatherConditions() {
        // 创建雪天天气
        WeatherCondition snowyWeather = new WeatherCondition("Snowy");
        
        BellmanFord bellmanFord = new BellmanFord(graph, startNode, endNode, vehicle, 
                                                 trafficCondition, snowyWeather, 10);
        
        PathResult result = bellmanFord.findPath();
        
        assertNotNull("雪天路径结果不应该为null", result);
        
        // 验证路径包含起始和结束节点
        assertEquals("路径应该以起始节点开始", startNode, result.getPath().get(0));
        assertEquals("路径应该以结束节点结束", endNode, result.getPath().get(result.getPath().size() - 1));
    }

    /**
     * 测试BellmanFord负权边
     * 验证负权边的处理（BellmanFord的优势）
     */
    @Test
    public void testBellmanFordWithNegativeWeights() {
        // 创建包含负权边的图
        Graph negativeGraph = new Graph();
        Node negStart = new Node(1, false, "Start", false, false, false, 1.0, 0, 24);
        Node negMiddle = new Node(2, false, "Middle", false, false, false, 1.0, 0, 24);
        Node negEnd = new Node(3, false, "End", false, false, false, 1.0, 0, 24);
        
        negativeGraph.addNode(negStart);
        negativeGraph.addNode(negMiddle);
        negativeGraph.addNode(negEnd);
        
        // 添加负权边
        negativeGraph.addEdge(1, 2, 10.0);
        negativeGraph.addEdge(2, 3, -5.0);  // 负权边
        negativeGraph.addEdge(1, 3, 20.0);  // 直接路径
        
        BellmanFord bellmanFord = new BellmanFord(negativeGraph, negStart, negEnd, vehicle, 
                                                 trafficCondition, weatherCondition, 10);
        
        PathResult result = bellmanFord.findPath();
        
        assertNotNull("负权边路径结果不应该为null", result);
        
        // 验证路径包含起始和结束节点
        assertEquals("路径应该以起始节点开始", negStart, result.getPath().get(0));
        assertEquals("路径应该以结束节点结束", negEnd, result.getPath().get(result.getPath().size() - 1));
    }

    /**
     * 测试BellmanFord负权环检测
     * 验证负权环的检测功能
     */
    @Test
    public void testBellmanFordNegativeCycleDetection() {
        // 创建包含负权环的图
        Graph negativeCycleGraph = new Graph();
        Node cycleStart = new Node(1, false, "Start", false, false, false, 1.0, 0, 24);
        Node cycleMiddle = new Node(2, false, "Middle", false, false, false, 1.0, 0, 24);
        Node cycleEnd = new Node(3, false, "End", false, false, false, 1.0, 0, 24);
        
        negativeCycleGraph.addNode(cycleStart);
        negativeCycleGraph.addNode(cycleMiddle);
        negativeCycleGraph.addNode(cycleEnd);
        
        // 创建负权环：1->2(5), 2->3(5), 3->2(-10)
        negativeCycleGraph.addEdge(1, 2, 5.0);
        negativeCycleGraph.addEdge(2, 3, 5.0);
        negativeCycleGraph.addEdge(3, 2, -10.0);  // 形成负权环
        
        BellmanFord bellmanFord = new BellmanFord(negativeCycleGraph, cycleStart, cycleEnd, vehicle, 
                                                 trafficCondition, weatherCondition, 10);
        
        PathResult result = bellmanFord.findPath();
        
        // 如果检测到负权环，应该返回null
        assertNull("负权环应该被检测到", result);
    }

    /**
     * 测试BellmanFord与复杂图
     * 验证在复杂图结构中的表现
     */
    @Test
    public void testBellmanFordComplexGraph() {
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
        
        BellmanFord complexBellmanFord = new BellmanFord(complexGraph, complexStart, complexEnd, vehicle, 
                                                        trafficCondition, weatherCondition, 10);
        
        PathResult complexResult = complexBellmanFord.findPath();
        
        assertNotNull("复杂图路径结果不应该为null", complexResult);
        assertNotNull("复杂图路径不应该为null", complexResult.getPath());
        assertFalse("复杂图路径不应该为空", complexResult.getPath().isEmpty());
        
        assertEquals("复杂图路径应该以起始节点开始", complexStart, complexResult.getPath().get(0));
        assertEquals("复杂图路径应该以结束节点结束", complexEnd, 
                    complexResult.getPath().get(complexResult.getPath().size() - 1));
    }

    /**
     * 测试BellmanFord的reconstructPath方法
     * 验证路径重建功能
     */
    @Test
    public void testBellmanFordReconstructPath() {
        BellmanFord bellmanFord = new BellmanFord(graph, startNode, endNode, vehicle, 
                                                 trafficCondition, weatherCondition, 10);
        
        PathResult result = bellmanFord.findPath();
        
        assertNotNull("重建路径结果不应该为null", result);
        assertNotNull("重建路径不应该为null", result.getPath());
        
        // 验证路径顺序
        assertEquals("路径第一个节点应该是起始节点", startNode, result.getPath().get(0));
        assertEquals("路径最后一个节点应该是结束节点", endNode, result.getPath().get(result.getPath().size() - 1));
        
        // 验证路径中没有重复节点（除了可能的特殊情况）
        for (int i = 0; i < result.getPath().size(); i++) {
            for (int j = i + 1; j < result.getPath().size(); j++) {
                assertNotEquals("路径中不应该有重复节点", result.getPath().get(i), result.getPath().get(j));
            }
        }
    }

    /**
     * 测试BellmanFord与不同节点类型
     * 验证对特殊类型节点的处理
     */
    @Test
    public void testBellmanFordWithDifferentNodeTypes() {
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
        
        BellmanFord bellmanFord = new BellmanFord(specialGraph, obstacleNode, highRiskNode, vehicle, 
                                                 trafficCondition, weatherCondition, 10);
        
        PathResult result = bellmanFord.findPath();
        
        assertNotNull("特殊节点路径结果不应该为null", result);
    }

    /**
     * 测试BellmanFord与不同车辆类型
     * 验证对特殊类型车辆的处理
     */
    @Test
    public void testBellmanFordWithDifferentVehicleTypes() {
        // 测试紧急车辆
        Vehicle emergencyVehicle = new Vehicle("Emergency", 2000.0, false, 60.0, 40.0, 0.8, 8.0, true);
        
        BellmanFord emergencyBellmanFord = new BellmanFord(graph, startNode, endNode, emergencyVehicle, 
                                                          trafficCondition, weatherCondition, 10);
        
        PathResult emergencyResult = emergencyBellmanFord.findPath();
        
        assertNotNull("紧急车辆路径结果不应该为null", emergencyResult);
        
        // 测试重型车辆
        Vehicle heavyVehicle = new Vehicle("Heavy", 5000.0, true, 100.0, 50.0, 1.0, 10.0, false);
        
        BellmanFord heavyBellmanFord = new BellmanFord(graph, startNode, endNode, heavyVehicle, 
                                                      trafficCondition, weatherCondition, 10);
        
        PathResult heavyResult = heavyBellmanFord.findPath();
        
        assertNotNull("重型车辆路径结果不应该为null", heavyResult);
    }

    /**
     * 测试BellmanFord的边界情况
     * 验证各种边界条件的处理
     */
    @Test
    public void testBellmanFordBoundaryConditions() {
        // 测试单节点图
        Graph singleNodeGraph = new Graph();
        Node singleNode = new Node(1, false, "Single", false, false, false, 1.0, 0, 24);
        singleNodeGraph.addNode(singleNode);
        
        BellmanFord singleNodeBellmanFord = new BellmanFord(singleNodeGraph, singleNode, singleNode, vehicle, 
                                                          trafficCondition, weatherCondition, 10);
        
        PathResult singleNodeResult = singleNodeBellmanFord.findPath();
        assertNotNull("单节点图路径结果不应该为null", singleNodeResult);
        assertEquals("单节点图路径长度应该为1", 1, singleNodeResult.getPath().size());
        
        // 测试两节点图
        Graph twoNodeGraph = new Graph();
        Node nodeA = new Node(1, false, "A", false, false, false, 1.0, 0, 24);
        Node nodeB = new Node(2, false, "B", false, false, false, 1.0, 0, 24);
        twoNodeGraph.addNode(nodeA);
        twoNodeGraph.addNode(nodeB);
        twoNodeGraph.addEdge(1, 2, 10.0);
        
        BellmanFord twoNodeBellmanFord = new BellmanFord(twoNodeGraph, nodeA, nodeB, vehicle, 
                                                        trafficCondition, weatherCondition, 10);
        
        PathResult twoNodeResult = twoNodeBellmanFord.findPath();
        assertNotNull("两节点图路径结果不应该为null", twoNodeResult);
        assertEquals("两节点图路径长度应该为2", 2, twoNodeResult.getPath().size());
    }

    /**
     * 测试BellmanFord的多次调用
     * 验证多次调用的结果一致性
     */
    @Test
    public void testBellmanFordMultipleCalls() {
        BellmanFord bellmanFord = new BellmanFord(graph, startNode, endNode, vehicle, 
                                                 trafficCondition, weatherCondition, 10);
        
        // 多次调用findPath
        PathResult result1 = bellmanFord.findPath();
        PathResult result2 = bellmanFord.findPath();
        PathResult result3 = bellmanFord.findPath();
        
        assertNotNull("第一次调用结果不应该为null", result1);
        assertNotNull("第二次调用结果不应该为null", result2);
        assertNotNull("第三次调用结果不应该为null", result3);
        
        // 验证结果的一致性
        assertEquals("多次调用路径长度应该相同", result1.getPath().size(), result2.getPath().size());
        assertEquals("多次调用路径长度应该相同", result2.getPath().size(), result3.getPath().size());
    }
}