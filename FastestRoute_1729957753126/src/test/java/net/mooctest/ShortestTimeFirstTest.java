package net.mooctest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

/**
 * ShortestTimeFirst算法的测试类
 * 测试最短时间优先算法在各种场景下的路径查找功能
 */
public class ShortestTimeFirstTest {
    
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
     * 测试ShortestTimeFirst构造函数
     * 验证算法能正确初始化
     */
    @Test
    public void testShortestTimeFirstConstructor() {
        ShortestTimeFirst stf = new ShortestTimeFirst(graph, startNode, endNode, vehicle, 
                                                      trafficCondition, weatherCondition, 10);
        
        assertNotNull("STF算法应该被正确创建", stf);
    }

    /**
     * 测试ShortestTimeFirst基本路径查找
     * 验证能找到最短时间路径
     */
    @Test
    public void testShortestTimeFirstBasicPathFinding() {
        ShortestTimeFirst stf = new ShortestTimeFirst(graph, startNode, endNode, vehicle, 
                                                      trafficCondition, weatherCondition, 10);
        
        PathResult result = stf.findPath();
        
        assertNotNull("路径结果不应该为null", result);
        assertNotNull("路径不应该为null", result.getPath());
        assertFalse("路径不应该为空", result.getPath().isEmpty());
        
        // 验证路径包含起始和结束节点
        assertEquals("路径应该以起始节点开始", startNode, result.getPath().get(0));
        assertEquals("路径应该以结束节点结束", endNode, result.getPath().get(result.getPath().size() - 1));
        
        // 验证路径长度合理
        assertTrue("路径长度应该合理", result.getPath().size() >= 2);
    }

    /**
     * 测试ShortestTimeFirst直接路径
     * 验证当存在直接路径时的处理
     */
    @Test
    public void testShortestTimeFirstDirectPath() {
        // 添加直接路径
        graph.addEdge(1, 4, 5.0);
        
        ShortestTimeFirst stf = new ShortestTimeFirst(graph, startNode, endNode, vehicle, 
                                                      trafficCondition, weatherCondition, 10);
        
        PathResult result = stf.findPath();
        
        assertNotNull("直接路径结果不应该为null", result);
        assertEquals("直接路径长度应该为2", 2, result.getPath().size());
        assertEquals("直接路径应该以起始节点开始", startNode, result.getPath().get(0));
        assertEquals("直接路径应该以结束节点结束", endNode, result.getPath().get(1));
    }

    /**
     * 测试ShortestTimeFirst无路径情况
     * 验证当没有路径时的处理
     */
    @Test
    public void testShortestTimeFirstNoPath() {
        // 创建没有连接的图
        Graph noPathGraph = new Graph();
        Node isolatedStart = new Node(1, false, "Isolated Start", false, false, false, 1.0, 0, 24);
        Node isolatedEnd = new Node(2, false, "Isolated End", false, false, false, 1.0, 0, 24);
        
        noPathGraph.addNode(isolatedStart);
        noPathGraph.addNode(isolatedEnd);
        
        ShortestTimeFirst stf = new ShortestTimeFirst(noPathGraph, isolatedStart, isolatedEnd, vehicle, 
                                                      trafficCondition, weatherCondition, 10);
        
        PathResult result = stf.findPath();
        
        assertNull("无路径时应该返回null", result);
    }

    /**
     * 测试ShortestTimeFirst相同起始和结束节点
     * 验证当起始和结束节点相同时的处理
     */
    @Test
    public void testShortestTimeFirstSameStartEnd() {
        ShortestTimeFirst stf = new ShortestTimeFirst(graph, startNode, startNode, vehicle, 
                                                      trafficCondition, weatherCondition, 10);
        
        PathResult result = stf.findPath();
        
        assertNotNull("相同节点路径结果不应该为null", result);
        assertEquals("相同节点路径长度应该为1", 1, result.getPath().size());
        assertEquals("路径应该只包含起始节点", startNode, result.getPath().get(0));
    }

    /**
     * 测试calculateTravelTime方法
     * 验证行驶时间计算的正确性
     */
    @Test
    public void testCalculateTravelTime() {
        ShortestTimeFirst stf = new ShortestTimeFirst(graph, startNode, endNode, vehicle, 
                                                      trafficCondition, weatherCondition, 10);
        
        // 创建不同类型的边
        Node highwayNode = new Node(10, false, "Highway", false, false, false, 1.0, 0, 24);
        Node tollNode = new Node(11, false, "Toll Road", true, false, false, 2.0, 0, 24);
        Node regularNode = new Node(12, false, "Regular Road", false, false, false, 1.0, 0, 24);
        
        Edge highwayEdge = new Edge(highwayNode, 100.0);  // 100km高速公路
        Edge tollEdge = new Edge(tollNode, 80.0);         // 80km收费道路
        Edge regularEdge = new Edge(regularNode, 50.0);   // 50km普通道路
        
        // 测试标准车辆
        double highwayTime = stf.calculateTravelTime(highwayEdge, vehicle);
        assertEquals("高速公路时间应该正确", 100.0 / 100.0, highwayTime, 0.001);
        
        double tollTime = stf.calculateTravelTime(tollEdge, vehicle);
        assertEquals("收费道路时间应该正确", 80.0 / 80.0, tollTime, 0.001);
        
        double regularTime = stf.calculateTravelTime(regularEdge, vehicle);
        assertEquals("普通道路时间应该正确", 50.0 / 50.0, regularTime, 0.001);
        
        // 测试重型车辆
        Vehicle heavyVehicle = new Vehicle("Heavy Vehicle", 5000.0, true, 100.0, 50.0, 1.0, 10.0, false);
        
        double highwayTimeHeavy = stf.calculateTravelTime(highwayEdge, heavyVehicle);
        assertEquals("重型车辆高速公路时间应该正确", 100.0 / (100.0 * 0.75), highwayTimeHeavy, 0.001);
        
        double tollTimeHeavy = stf.calculateTravelTime(tollEdge, heavyVehicle);
        assertEquals("重型车辆收费道路时间应该正确", 80.0 / (80.0 * 0.75), tollTimeHeavy, 0.001);
        
        double regularTimeHeavy = stf.calculateTravelTime(regularEdge, heavyVehicle);
        assertEquals("重型车辆普通道路时间应该正确", 50.0 / (50.0 * 0.75), regularTimeHeavy, 0.001);
    }

    /**
     * 测试ShortestTimeFirst与交通条件
     * 验证交通条件对时间计算的影响
     */
    @Test
    public void testShortestTimeFirstWithTrafficConditions() {
        // 创建拥堵的交通条件
        Map<Integer, String> congestedTrafficData = new HashMap<>();
        congestedTrafficData.put(1, "Clear");
        congestedTrafficData.put(2, "Congested");  // 权重翻倍
        congestedTrafficData.put(3, "Accident");   // 权重三倍
        congestedTrafficData.put(4, "Clear");
        TrafficCondition congestedTraffic = new TrafficCondition(congestedTrafficData);
        
        ShortestTimeFirst stf = new ShortestTimeFirst(graph, startNode, endNode, vehicle, 
                                                      congestedTraffic, weatherCondition, 10);
        
        PathResult result = stf.findPath();
        
        assertNotNull("拥堵交通路径结果不应该为null", result);
        
        // 验证路径包含起始和结束节点
        assertEquals("路径应该以起始节点开始", startNode, result.getPath().get(0));
        assertEquals("路径应该以结束节点结束", endNode, result.getPath().get(result.getPath().size() - 1));
    }

    /**
     * 测试ShortestTimeFirst与天气条件
     * 验证天气条件对时间计算的影响
     */
    @Test
    public void testShortestTimeFirstWithWeatherConditions() {
        // 创建暴风雨天气
        WeatherCondition stormyWeather = new WeatherCondition("Stormy");
        
        ShortestTimeFirst stf = new ShortestTimeFirst(graph, startNode, endNode, vehicle, 
                                                      trafficCondition, stormyWeather, 10);
        
        PathResult result = stf.findPath();
        
        assertNotNull("暴风雨天气路径结果不应该为null", result);
        
        // 验证路径包含起始和结束节点
        assertEquals("路径应该以起始节点开始", startNode, result.getPath().get(0));
        assertEquals("路径应该以结束节点结束", endNode, result.getPath().get(result.getPath().size() - 1));
    }

    /**
     * 测试ShortestTimeFirst与时间限制
     * 验证时间窗口对路径的影响
     */
    @Test
    public void testShortestTimeFirstWithTimeRestrictions() {
        // 创建有时间限制的节点
        Node timeRestrictedNode = new Node(5, false, "Time Restricted", false, false, false, 1.0, 9, 17);
        graph.addNode(timeRestrictedNode);
        graph.addEdge(1, 5, 5.0);
        graph.addEdge(5, 4, 5.0);
        
        // 在允许时间内（10点）
        ShortestTimeFirst stfWithinTime = new ShortestTimeFirst(graph, startNode, endNode, vehicle, 
                                                               trafficCondition, weatherCondition, 10);
        
        PathResult resultWithinTime = stfWithinTime.findPath();
        assertNotNull("允许时间内路径结果不应该为null", resultWithinTime);
        
        // 在不允许时间内（8点）
        ShortestTimeFirst stfOutsideTime = new ShortestTimeFirst(graph, startNode, endNode, vehicle, 
                                                                trafficCondition, weatherCondition, 8);
        
        PathResult resultOutsideTime = stfOutsideTime.findPath();
        assertNotNull("不允许时间内路径结果不应该为null", resultOutsideTime);
    }

    /**
     * 测试ShortestTimeFirst与不同车辆类型
     * 验证不同车辆类型对时间计算的影响
     */
    @Test
    public void testShortestTimeFirstWithDifferentVehicleTypes() {
        // 测试紧急车辆
        Vehicle emergencyVehicle = new Vehicle("Emergency", 2000.0, false, 60.0, 40.0, 0.8, 8.0, true);
        
        ShortestTimeFirst emergencyStf = new ShortestTimeFirst(graph, startNode, endNode, emergencyVehicle, 
                                                              trafficCondition, weatherCondition, 10);
        
        PathResult emergencyResult = emergencyStf.findPath();
        assertNotNull("紧急车辆路径结果不应该为null", emergencyResult);
        
        // 测试重型车辆
        Vehicle heavyVehicle = new Vehicle("Heavy Vehicle", 5000.0, true, 100.0, 50.0, 1.0, 10.0, false);
        
        ShortestTimeFirst heavyStf = new ShortestTimeFirst(graph, startNode, endNode, heavyVehicle, 
                                                          trafficCondition, weatherCondition, 10);
        
        PathResult heavyResult = heavyStf.findPath();
        assertNotNull("重型车辆路径结果不应该为null", heavyResult);
        
        // 测试低燃料车辆
        Vehicle lowFuelVehicle = new Vehicle("Low Fuel", 1000.0, false, 40.0, 5.0, 1.0, 2.0, false);
        
        ShortestTimeFirst lowFuelStf = new ShortestTimeFirst(graph, startNode, endNode, lowFuelVehicle, 
                                                            trafficCondition, weatherCondition, 10);
        
        PathResult lowFuelResult = lowFuelStf.findPath();
        assertNotNull("低燃料车辆路径结果不应该为null", lowFuelResult);
    }

    /**
     * 测试ShortestTimeFirst与不同道路类型
     * 验证不同道路类型对时间计算的影响
     */
    @Test
    public void testShortestTimeFirstWithDifferentRoadTypes() {
        // 创建不同道路类型的图
        Graph roadTypeGraph = new Graph();
        Node roadStart = new Node(1, false, "Start", false, false, false, 1.0, 0, 24);
        Node highwayNode = new Node(2, false, "Highway", false, false, false, 1.0, 0, 24);
        Node tollNode = new Node(3, false, "Toll Road", true, false, false, 2.0, 0, 24);
        Node regularNode = new Node(4, false, "Regular Road", false, false, false, 1.0, 0, 24);
        Node roadEnd = new Node(5, false, "End", false, false, false, 1.0, 0, 24);
        
        roadTypeGraph.addNode(roadStart);
        roadTypeGraph.addNode(highwayNode);
        roadTypeGraph.addNode(tollNode);
        roadTypeGraph.addNode(regularNode);
        roadTypeGraph.addNode(roadEnd);
        
        // 设置节点的道路类型
        roadTypeGraph.addEdge(1, 2, 50.0);   // 50km高速公路
        roadTypeGraph.addEdge(2, 3, 40.0);   // 40km收费道路
        roadTypeGraph.addEdge(3, 4, 30.0);   // 30km普通道路
        roadTypeGraph.addEdge(4, 5, 20.0);   // 20km普通道路
        
        ShortestTimeFirst roadTypeStf = new ShortestTimeFirst(roadTypeGraph, roadStart, roadEnd, vehicle, 
                                                              trafficCondition, weatherCondition, 10);
        
        PathResult roadTypeResult = roadTypeStf.findPath();
        
        assertNotNull("不同道路类型路径结果不应该为null", roadTypeResult);
        assertEquals("路径应该以起始节点开始", roadStart, roadTypeResult.getPath().get(0));
        assertEquals("路径应该以结束节点结束", roadEnd, roadTypeResult.getPath().get(roadTypeResult.getPath().size() - 1));
    }

    /**
     * 测试ShortestTimeFirst的reconstructPath方法
     * 验证路径重建功能
     */
    @Test
    public void testShortestTimeFirstReconstructPath() {
        ShortestTimeFirst stf = new ShortestTimeFirst(graph, startNode, endNode, vehicle, 
                                                      trafficCondition, weatherCondition, 10);
        
        PathResult result = stf.findPath();
        
        assertNotNull("重建路径结果不应该为null", result);
        assertNotNull("重建路径不应该为null", result.getPath());
        
        // 验证路径顺序
        assertEquals("路径第一个节点应该是起始节点", startNode, result.getPath().get(0));
        assertEquals("路径最后一个节点应该是结束节点", endNode, result.getPath().get(result.getPath().size() - 1));
        
        // 验证路径中没有重复节点
        for (int i = 0; i < result.getPath().size(); i++) {
            for (int j = i + 1; j < result.getPath().size(); j++) {
                assertNotEquals("路径中不应该有重复节点", result.getPath().get(i), result.getPath().get(j));
            }
        }
    }

    /**
     * 测试ShortestTimeFirst的复杂场景
     * 验证在复杂图结构中的表现
     */
    @Test
    public void testShortestTimeFirstComplexScenarios() {
        // 创建更复杂的图
        Graph complexGraph = new Graph();
        for (int i = 1; i <= 10; i++) {
            complexGraph.addNode(new Node(i, false, "Node" + i, false, false, false, 1.0, 0, 24));
        }
        
        // 添加复杂的连接
        complexGraph.addEdge(1, 2, 50.0);   // 高速公路
        complexGraph.addEdge(1, 3, 80.0);   // 收费道路
        complexGraph.addEdge(2, 4, 60.0);   // 高速公路
        complexGraph.addEdge(3, 4, 40.0);   // 收费道路
        complexGraph.addEdge(2, 5, 100.0);  // 普通道路
        complexGraph.addEdge(4, 6, 70.0);   // 高速公路
        complexGraph.addEdge(5, 6, 30.0);   // 普通道路
        complexGraph.addEdge(4, 7, 90.0);   // 收费道路
        complexGraph.addEdge(6, 8, 50.0);   // 高速公路
        complexGraph.addEdge(7, 8, 40.0);   // 收费道路
        complexGraph.addEdge(6, 9, 120.0);  // 普通道路
        complexGraph.addEdge(8, 10, 60.0);  // 高速公路
        complexGraph.addEdge(9, 10, 80.0);  // 普通道路
        
        Node complexStart = complexGraph.getNode(1);
        Node complexEnd = complexGraph.getNode(10);
        
        ShortestTimeFirst complexStf = new ShortestTimeFirst(complexGraph, complexStart, complexEnd, vehicle, 
                                                             trafficCondition, weatherCondition, 10);
        
        PathResult complexResult = complexStf.findPath();
        
        assertNotNull("复杂场景路径结果不应该为null", complexResult);
        assertNotNull("复杂场景路径不应该为null", complexResult.getPath());
        assertFalse("复杂场景路径不应该为空", complexResult.getPath().isEmpty());
        
        assertEquals("复杂场景路径应该以起始节点开始", complexStart, complexResult.getPath().get(0));
        assertEquals("复杂场景路径应该以结束节点结束", complexEnd, 
                    complexResult.getPath().get(complexResult.getPath().size() - 1));
    }

    /**
     * 测试ShortestTimeFirst的边界情况
     * 验证各种边界条件的处理
     */
    @Test
    public void testShortestTimeFirstBoundaryConditions() {
        // 测试单节点图
        Graph singleNodeGraph = new Graph();
        Node singleNode = new Node(1, false, "Single", false, false, false, 1.0, 0, 24);
        singleNodeGraph.addNode(singleNode);
        
        ShortestTimeFirst singleNodeStf = new ShortestTimeFirst(singleNodeGraph, singleNode, singleNode, vehicle, 
                                                               trafficCondition, weatherCondition, 10);
        
        PathResult singleNodeResult = singleNodeStf.findPath();
        assertNotNull("单节点图路径结果不应该为null", singleNodeResult);
        assertEquals("单节点图路径长度应该为1", 1, singleNodeResult.getPath().size());
        
        // 测试两节点图
        Graph twoNodeGraph = new Graph();
        Node nodeA = new Node(1, false, "A", false, false, false, 1.0, 0, 24);
        Node nodeB = new Node(2, false, "B", false, false, false, 1.0, 0, 24);
        twoNodeGraph.addNode(nodeA);
        twoNodeGraph.addNode(nodeB);
        twoNodeGraph.addEdge(1, 2, 50.0);
        
        ShortestTimeFirst twoNodeStf = new ShortestTimeFirst(twoNodeGraph, nodeA, nodeB, vehicle, 
                                                             trafficCondition, weatherCondition, 10);
        
        PathResult twoNodeResult = twoNodeStf.findPath();
        assertNotNull("两节点图路径结果不应该为null", twoNodeResult);
        assertEquals("两节点图路径长度应该为2", 2, twoNodeResult.getPath().size());
        
        // 测试零距离边
        Graph zeroDistanceGraph = new Graph();
        Node zeroStart = new Node(1, false, "Zero Start", false, false, false, 1.0, 0, 24);
        Node zeroEnd = new Node(2, false, "Zero End", false, false, false, 1.0, 0, 24);
        zeroDistanceGraph.addNode(zeroStart);
        zeroDistanceGraph.addNode(zeroEnd);
        zeroDistanceGraph.addEdge(1, 2, 0.0);
        
        ShortestTimeFirst zeroDistanceStf = new ShortestTimeFirst(zeroDistanceGraph, zeroStart, zeroEnd, vehicle, 
                                                                 trafficCondition, weatherCondition, 10);
        
        PathResult zeroDistanceResult = zeroDistanceStf.findPath();
        assertNotNull("零距离路径结果不应该为null", zeroDistanceResult);
        assertEquals("零距离路径长度应该为2", 2, zeroDistanceResult.getPath().size());
    }

    /**
     * 测试ShortestTimeFirst的多次调用
     * 验证多次调用的结果一致性
     */
    @Test
    public void testShortestTimeFirstMultipleCalls() {
        ShortestTimeFirst stf = new ShortestTimeFirst(graph, startNode, endNode, vehicle, 
                                                      trafficCondition, weatherCondition, 10);
        
        // 多次调用findPath
        PathResult result1 = stf.findPath();
        PathResult result2 = stf.findPath();
        PathResult result3 = stf.findPath();
        
        assertNotNull("第一次调用结果不应该为null", result1);
        assertNotNull("第二次调用结果不应该为null", result2);
        assertNotNull("第三次调用结果不应该为null", result3);
        
        // 验证结果的一致性
        assertEquals("多次调用路径长度应该相同", result1.getPath().size(), result2.getPath().size());
        assertEquals("多次调用路径长度应该相同", result2.getPath().size(), result3.getPath().size());
        
        // 验证起始和结束节点的一致性
        assertEquals("多次调用起始节点应该相同", result1.getPath().get(0), result2.getPath().get(0));
        assertEquals("多次调用起始节点应该相同", result2.getPath().get(0), result3.getPath().get(0));
        
        assertEquals("多次调用结束节点应该相同", 
                    result1.getPath().get(result1.getPath().size() - 1), 
                    result2.getPath().get(result2.getPath().size() - 1));
        assertEquals("多次调用结束节点应该相同", 
                    result2.getPath().get(result2.getPath().size() - 1), 
                    result3.getPath().get(result3.getPath().size() - 1));
    }
}