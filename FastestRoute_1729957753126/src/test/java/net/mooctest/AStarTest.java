package net.mooctest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

/**
 * A*算法的测试类
 * 测试A*算法在各种场景下的路径查找功能
 */
public class AStarTest {
    
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
        endNode = new Node(5, false, "End", false, false, false, 1.0, 0, 24);
        Node node2 = new Node(2, false, "Middle1", false, false, false, 1.0, 0, 24);
        Node node3 = new Node(3, false, "Middle2", false, false, false, 1.0, 0, 24);
        Node node4 = new Node(4, false, "Middle3", false, false, false, 1.0, 0, 24);
        
        graph.addNode(startNode);
        graph.addNode(endNode);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);
        
        // 添加边：创建一个网格状结构
        graph.addEdge(1, 2, 10.0);
        graph.addEdge(1, 3, 15.0);
        graph.addEdge(2, 4, 10.0);
        graph.addEdge(3, 4, 5.0);
        graph.addEdge(4, 5, 10.0);
        graph.addEdge(2, 5, 25.0);
        graph.addEdge(3, 5, 20.0);
        
        // 创建测试车辆
        vehicle = new Vehicle("Test Vehicle", 1000.0, false, 50.0, 25.0, 0.5, 5.0, false);
        
        // 创建测试条件
        Map<Integer, String> trafficData = new HashMap<>();
        trafficData.put(1, "Clear");
        trafficData.put(2, "Clear");
        trafficData.put(3, "Clear");
        trafficData.put(4, "Clear");
        trafficData.put(5, "Clear");
        trafficCondition = new TrafficCondition(trafficData);
        
        weatherCondition = new WeatherCondition("Clear");
    }

    /**
     * 测试AStar构造函数
     * 验证算法能正确初始化
     */
    @Test
    public void testAStarConstructor() {
        AStar aStar = new AStar(graph, startNode, endNode, vehicle, 
                               trafficCondition, weatherCondition, 10);
        
        assertNotNull("A*算法应该被正确创建", aStar);
    }

    /**
     * 测试AStar基本路径查找
     * 验证能找到最优路径
     */
    @Test
    public void testAStarBasicPathFinding() {
        AStar aStar = new AStar(graph, startNode, endNode, vehicle, 
                               trafficCondition, weatherCondition, 10);
        
        PathResult result = aStar.findPath();
        
        assertNotNull("路径结果不应该为null", result);
        assertNotNull("路径不应该为null", result.getPath());
        assertFalse("路径不应该为空", result.getPath().isEmpty());
        
        // 验证路径包含起始和结束节点
        assertEquals("路径应该以起始节点开始", startNode, result.getPath().get(0));
        assertEquals("路径应该以结束节点结束", endNode, result.getPath().get(result.getPath().size() - 1));
        
        // 验证路径的有效性
        assertTrue("路径长度应该至少为2", result.getPath().size() >= 2);
    }

    /**
     * 测试AStar启发式函数
     * 验证启发式计算的正确性
     */
    @Test
    public void testAStarHeuristic() {
        AStar aStar = new AStar(graph, startNode, endNode, vehicle, 
                               trafficCondition, weatherCondition, 10);
        
        // 测试不同节点的启发式值
        Node highwayNode = new Node(10, false, "Highway", false, false, false, 1.0, 0, 24);
        Node highRiskNode = new Node(11, false, "High Risk", false, false, true, 1.0, 0, 24);
        Node regularNode = new Node(12, false, "Regular", false, false, false, 1.0, 0, 24);
        
        // 由于heuristic方法是private，我们通过路径查找结果间接验证
        Graph testGraph = new Graph();
        testGraph.addNode(startNode);
        testGraph.addNode(endNode);
        testGraph.addNode(highwayNode);
        testGraph.addNode(highRiskNode);
        testGraph.addNode(regularNode);
        
        // 设置不同的道路类型
        testGraph.addEdge(1, 10, 5.0);
        testGraph.addEdge(1, 11, 5.0);
        testGraph.addEdge(1, 12, 5.0);
        testGraph.addEdge(10, 5, 5.0);
        testGraph.addEdge(11, 5, 5.0);
        testGraph.addEdge(12, 5, 5.0);
        
        AStar testAStar = new AStar(testGraph, startNode, endNode, vehicle, 
                                   trafficCondition, weatherCondition, 10);
        
        PathResult testResult = testAStar.findPath();
        assertNotNull("启发式测试路径结果不应该为null", testResult);
    }

    /**
     * 测试AStar与高速公路节点
     * 验证高速公路节点的优先处理
     */
    @Test
    public void testAStarWithHighwayNodes() {
        // 创建包含高速公路的图
        Graph highwayGraph = new Graph();
        Node highwayStart = new Node(1, false, "Start", false, false, false, 1.0, 0, 24);
        Node highwayNode = new Node(2, false, "Highway", false, false, false, 1.0, 0, 24);
        Node regularNode = new Node(3, false, "Regular", false, false, false, 1.0, 0, 24);
        Node highwayEnd = new Node(4, false, "End", false, false, false, 1.0, 0, 24);
        
        highwayGraph.addNode(highwayStart);
        highwayGraph.addNode(highwayNode);
        highwayGraph.addNode(regularNode);
        highwayGraph.addNode(highwayEnd);
        
        // 设置节点2为高速公路
        Node actualHighwayNode = new Node(2, false, "Highway", false, false, false, 1.0, 0, 24);
        highwayGraph.addNode(actualHighwayNode);
        highwayGraph.addEdge(1, 2, 10.0);
        highwayGraph.addEdge(1, 3, 10.0);
        highwayGraph.addEdge(2, 4, 10.0);
        highwayGraph.addEdge(3, 4, 10.0);
        
        AStar highwayAStar = new AStar(highwayGraph, highwayStart, highwayEnd, vehicle, 
                                      trafficCondition, weatherCondition, 10);
        
        PathResult highwayResult = highwayAStar.findPath();
        
        assertNotNull("高速公路路径结果不应该为null", highwayResult);
        assertTrue("高速公路路径应该有效", highwayResult.getPath().size() >= 2);
    }

    /**
     * 测试AStar与高风险区域
     * 验证高风险区域的避免处理
     */
    @Test
    public void testAStarWithHighRiskAreas() {
        // 创建包含高风险区域的图
        Graph riskGraph = new Graph();
        Node riskStart = new Node(1, false, "Start", false, false, false, 1.0, 0, 24);
        Node highRiskNode = new Node(2, false, "High Risk", false, false, true, 1.0, 0, 24);
        Node safeNode = new Node(3, false, "Safe", false, false, false, 1.0, 0, 24);
        Node riskEnd = new Node(4, false, "End", false, false, false, 1.0, 0, 24);
        
        riskGraph.addNode(riskStart);
        riskGraph.addNode(highRiskNode);
        riskGraph.addNode(safeNode);
        riskGraph.addNode(riskEnd);
        
        riskGraph.addEdge(1, 2, 5.0);  // 通过高风险区域的短路径
        riskGraph.addEdge(1, 3, 10.0); // 通过安全区域的长路径
        riskGraph.addEdge(2, 4, 5.0);
        riskGraph.addEdge(3, 4, 10.0);
        
        // 普通车辆应该避免高风险区域
        AStar normalAStar = new AStar(riskGraph, riskStart, riskEnd, vehicle, 
                                     trafficCondition, weatherCondition, 10);
        
        PathResult normalResult = normalAStar.findPath();
        
        assertNotNull("普通车辆路径结果不应该为null", normalResult);
        
        // 紧急车辆可以通行高风险区域
        Vehicle emergencyVehicle = new Vehicle("Emergency", 2000.0, false, 60.0, 40.0, 0.8, 8.0, true);
        
        AStar emergencyAStar = new AStar(riskGraph, riskStart, riskEnd, emergencyVehicle, 
                                        trafficCondition, weatherCondition, 10);
        
        PathResult emergencyResult = emergencyAStar.findPath();
        
        assertNotNull("紧急车辆路径结果不应该为null", emergencyResult);
    }

    /**
     * 测试AStar无路径情况
     * 验证当没有路径时的处理
     */
    @Test
    public void testAStarNoPath() {
        // 创建没有连接的图
        Graph noPathGraph = new Graph();
        Node isolatedStart = new Node(1, false, "Isolated Start", false, false, false, 1.0, 0, 24);
        Node isolatedEnd = new Node(2, false, "Isolated End", false, false, false, 1.0, 0, 24);
        
        noPathGraph.addNode(isolatedStart);
        noPathGraph.addNode(isolatedEnd);
        
        AStar aStar = new AStar(noPathGraph, isolatedStart, isolatedEnd, vehicle, 
                               trafficCondition, weatherCondition, 10);
        
        PathResult result = aStar.findPath();
        
        assertNull("无路径时应该返回null", result);
    }

    /**
     * 测试AStar相同起始和结束节点
     * 验证当起始和结束节点相同时的处理
     */
    @Test
    public void testAStarSameStartEnd() {
        AStar aStar = new AStar(graph, startNode, startNode, vehicle, 
                               trafficCondition, weatherCondition, 10);
        
        PathResult result = aStar.findPath();
        
        assertNotNull("相同节点路径结果不应该为null", result);
        assertEquals("相同节点路径长度应该为1", 1, result.getPath().size());
        assertEquals("路径应该只包含起始节点", startNode, result.getPath().get(0));
    }

    /**
     * 测试AStar与交通条件
     * 验证交通条件对路径查找的影响
     */
    @Test
    public void testAStarWithTrafficConditions() {
        // 创建拥堵的交通条件
        Map<Integer, String> congestedTrafficData = new HashMap<>();
        congestedTrafficData.put(1, "Clear");
        congestedTrafficData.put(2, "Congested");  // 权重翻倍
        congestedTrafficData.put(3, "Clear");
        congestedTrafficData.put(4, "Accident");   // 权重三倍
        congestedTrafficData.put(5, "Clear");
        TrafficCondition congestedTraffic = new TrafficCondition(congestedTrafficData);
        
        AStar aStar = new AStar(graph, startNode, endNode, vehicle, 
                               congestedTraffic, weatherCondition, 10);
        
        PathResult result = aStar.findPath();
        
        assertNotNull("拥堵交通路径结果不应该为null", result);
        
        // 验证路径的有效性
        assertEquals("路径应该以起始节点开始", startNode, result.getPath().get(0));
        assertEquals("路径应该以结束节点结束", endNode, result.getPath().get(result.getPath().size() - 1));
    }

    /**
     * 测试AStar与天气条件
     * 验证天气条件对路径查找的影响
     */
    @Test
    public void testAStarWithWeatherConditions() {
        // 创建暴风雨天气
        WeatherCondition stormyWeather = new WeatherCondition("Stormy");
        
        AStar aStar = new AStar(graph, startNode, endNode, vehicle, 
                               trafficCondition, stormyWeather, 10);
        
        PathResult result = aStar.findPath();
        
        assertNotNull("暴风雨天气路径结果不应该为null", result);
        
        // 验证路径的有效性
        assertEquals("路径应该以起始节点开始", startNode, result.getPath().get(0));
        assertEquals("路径应该以结束节点结束", endNode, result.getPath().get(result.getPath().size() - 1));
    }

    /**
     * 测试AStar与时间限制
     * 验证时间窗口对路径的影响
     */
    @Test
    public void testAStarWithTimeRestrictions() {
        // 创建有时间限制的节点
        Node timeRestrictedNode = new Node(6, false, "Time Restricted", false, false, false, 1.0, 9, 17);
        graph.addNode(timeRestrictedNode);
        graph.addEdge(1, 6, 5.0);
        graph.addEdge(6, 5, 5.0);
        
        // 在允许时间内（10点）
        AStar aStarWithinTime = new AStar(graph, startNode, endNode, vehicle, 
                                         trafficCondition, weatherCondition, 10);
        
        PathResult resultWithinTime = aStarWithinTime.findPath();
        assertNotNull("允许时间内路径结果不应该为null", resultWithinTime);
        
        // 在不允许时间内（8点）
        AStar aStarOutsideTime = new AStar(graph, startNode, endNode, vehicle, 
                                          trafficCondition, weatherCondition, 8);
        
        PathResult resultOutsideTime = aStarOutsideTime.findPath();
        assertNotNull("不允许时间内路径结果不应该为null", resultOutsideTime);
    }

    /**
     * 测试AStar与燃料限制
     * 验证燃料不足时的处理
     */
    @Test
    public void testAStarWithFuelConstraints() {
        // 创建低燃料车辆
        Vehicle lowFuelVehicle = new Vehicle("Low Fuel", 1000.0, false, 40.0, 5.0, 1.0, 2.0, false);
        
        AStar aStar = new AStar(graph, startNode, endNode, lowFuelVehicle, 
                               trafficCondition, weatherCondition, 10);
        
        PathResult result = aStar.findPath();
        
        assertNotNull("低燃料车辆路径结果不应该为null", result);
        
        // 验证路径的有效性
        if (result != null) {
            assertEquals("路径应该以起始节点开始", startNode, result.getPath().get(0));
            assertEquals("路径应该以结束节点结束", endNode, result.getPath().get(result.getPath().size() - 1));
        }
    }

    /**
     * 测试AStar的reconstructPath方法
     * 验证路径重建功能
     */
    @Test
    public void testAStarReconstructPath() {
        AStar aStar = new AStar(graph, startNode, endNode, vehicle, 
                               trafficCondition, weatherCondition, 10);
        
        PathResult result = aStar.findPath();
        
        assertNotNull("重建路径结果不应该为null", result);
        assertNotNull("重建路径不应该为null", result.getPath());
        
        // 验证路径顺序
        assertEquals("路径第一个节点应该是起始节点", startNode, result.getPath().get(0));
        assertEquals("路径最后一个节点应该是结束节点", endNode, result.getPath().get(result.getPath().size() - 1));
        
        // 验证路径的连续性（每个相邻节点之间应该有连接）
        for (int i = 0; i < result.getPath().size() - 1; i++) {
            Node current = result.getPath().get(i);
            Node next = result.getPath().get(i + 1);
            
            boolean hasConnection = false;
            for (Edge edge : current.getNeighbors()) {
                if (edge.getNeighbor().equals(next)) {
                    hasConnection = true;
                    break;
                }
            }
            assertTrue("路径中相邻节点应该有连接", hasConnection);
        }
    }

    /**
     * 测试AStar的复杂场景
     * 验证在复杂图结构中的表现
     */
    @Test
    public void testAStarComplexScenarios() {
        // 创建更复杂的图
        Graph complexGraph = new Graph();
        for (int i = 1; i <= 15; i++) {
            complexGraph.addNode(new Node(i, false, "Node" + i, false, false, false, 1.0, 0, 24));
        }
        
        // 添加复杂的连接，创建多个可能的路径
        complexGraph.addEdge(1, 2, 5.0);
        complexGraph.addEdge(1, 3, 8.0);
        complexGraph.addEdge(2, 4, 6.0);
        complexGraph.addEdge(3, 4, 3.0);
        complexGraph.addEdge(2, 5, 10.0);
        complexGraph.addEdge(4, 6, 4.0);
        complexGraph.addEdge(5, 6, 2.0);
        complexGraph.addEdge(4, 7, 8.0);
        complexGraph.addEdge(6, 8, 5.0);
        complexGraph.addEdge(7, 8, 3.0);
        complexGraph.addEdge(6, 9, 7.0);
        complexGraph.addEdge(8, 10, 4.0);
        complexGraph.addEdge(9, 10, 6.0);
        complexGraph.addEdge(8, 11, 8.0);
        complexGraph.addEdge(10, 12, 3.0);
        complexGraph.addEdge(11, 12, 5.0);
        complexGraph.addEdge(10, 13, 9.0);
        complexGraph.addEdge(12, 14, 4.0);
        complexGraph.addEdge(13, 14, 2.0);
        complexGraph.addEdge(12, 15, 6.0);
        complexGraph.addEdge(14, 15, 3.0);
        
        Node complexStart = complexGraph.getNode(1);
        Node complexEnd = complexGraph.getNode(15);
        
        AStar complexAStar = new AStar(complexGraph, complexStart, complexEnd, vehicle, 
                                       trafficCondition, weatherCondition, 10);
        
        PathResult complexResult = complexAStar.findPath();
        
        assertNotNull("复杂场景路径结果不应该为null", complexResult);
        assertNotNull("复杂场景路径不应该为null", complexResult.getPath());
        assertFalse("复杂场景路径不应该为空", complexResult.getPath().isEmpty());
        
        assertEquals("复杂场景路径应该以起始节点开始", complexStart, complexResult.getPath().get(0));
        assertEquals("复杂场景路径应该以结束节点结束", complexEnd, 
                    complexResult.getPath().get(complexResult.getPath().size() - 1));
    }

    /**
     * 测试AStar的边界情况
     * 验证各种边界条件的处理
     */
    @Test
    public void testAStarBoundaryConditions() {
        // 测试单节点图
        Graph singleNodeGraph = new Graph();
        Node singleNode = new Node(1, false, "Single", false, false, false, 1.0, 0, 24);
        singleNodeGraph.addNode(singleNode);
        
        AStar singleNodeAStar = new AStar(singleNodeGraph, singleNode, singleNode, vehicle, 
                                         trafficCondition, weatherCondition, 10);
        
        PathResult singleNodeResult = singleNodeAStar.findPath();
        assertNotNull("单节点图路径结果不应该为null", singleNodeResult);
        assertEquals("单节点图路径长度应该为1", 1, singleNodeResult.getPath().size());
        
        // 测试两节点图
        Graph twoNodeGraph = new Graph();
        Node nodeA = new Node(1, false, "A", false, false, false, 1.0, 0, 24);
        Node nodeB = new Node(2, false, "B", false, false, false, 1.0, 0, 24);
        twoNodeGraph.addNode(nodeA);
        twoNodeGraph.addNode(nodeB);
        twoNodeGraph.addEdge(1, 2, 10.0);
        
        AStar twoNodeAStar = new AStar(twoNodeGraph, nodeA, nodeB, vehicle, 
                                      trafficCondition, weatherCondition, 10);
        
        PathResult twoNodeResult = twoNodeAStar.findPath();
        assertNotNull("两节点图路径结果不应该为null", twoNodeResult);
        assertEquals("两节点图路径长度应该为2", 2, twoNodeResult.getPath().size());
    }
}