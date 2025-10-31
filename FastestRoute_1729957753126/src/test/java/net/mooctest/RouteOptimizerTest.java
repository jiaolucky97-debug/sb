package net.mooctest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

/**
 * RouteOptimizer类的测试类
 * 测试路由优化器的功能和各种场景
 */
public class RouteOptimizerTest {
    
    private Graph graph;
    private Node startNode;
    private Node endNode;
    private Vehicle vehicle;
    private TrafficCondition trafficCondition;
    private WeatherCondition weatherCondition;
    private SearchAlgorithm mockSearchAlgorithm;
    private RouteOptimizer routeOptimizer;

    @Before
    public void setUp() {
        // 创建测试图
        graph = new Graph();
        startNode = new Node(1, false, "Start", false, false, false, 1.0, 0, 24);
        endNode = new Node(2, false, "End", false, false, false, 1.0, 0, 24);
        Node middleNode = new Node(3, false, "Middle", false, false, false, 1.0, 0, 24);
        
        graph.addNode(startNode);
        graph.addNode(endNode);
        graph.addNode(middleNode);
        graph.addEdge(1, 3, 10.0);
        graph.addEdge(3, 2, 10.0);
        graph.addEdge(1, 2, 25.0); // 直接路径
        
        // 创建测试车辆
        vehicle = new Vehicle("Test Vehicle", 1000.0, false, 50.0, 25.0, 0.5, 5.0, false);
        
        // 创建测试条件
        Map<Integer, String> trafficData = new HashMap<>();
        trafficData.put(1, "Clear");
        trafficData.put(2, "Clear");
        trafficData.put(3, "Clear");
        trafficCondition = new TrafficCondition(trafficData);
        
        weatherCondition = new WeatherCondition("Clear");
        
        // 创建模拟搜索算法
        mockSearchAlgorithm = new MockSearchAlgorithm(graph, startNode, endNode, vehicle, 
                                                    trafficCondition, weatherCondition, 10);
        
        // 创建路由优化器
        routeOptimizer = new RouteOptimizer(mockSearchAlgorithm);
    }

    /**
     * 测试RouteOptimizer构造函数
     * 验证路由优化器能正确初始化
     */
    @Test
    public void testRouteOptimizerConstructor() {
        assertNotNull("路由优化器应该被正确创建", routeOptimizer);
    }

    /**
     * 测试optimizeRoute方法
     * 验证路径优化功能
     */
    @Test
    public void testOptimizeRoute() {
        PathResult result = routeOptimizer.optimizeRoute();
        
        assertNotNull("优化结果不应该为null", result);
        assertNotNull("路径不应该为null", result.getPath());
        assertFalse("路径不应该为空", result.getPath().isEmpty());
        
        // 验证路径包含起始和结束节点
        assertEquals("路径应该包含起始节点", startNode, result.getPath().get(0));
        assertEquals("路径应该包含结束节点", endNode, result.getPath().get(result.getPath().size() - 1));
    }

    /**
     * 测试RouteOptimizer与null搜索算法
     * 验证null搜索算法的处理
     */
    @Test
    public void testRouteOptimizerWithNullSearchAlgorithm() {
        try {
            RouteOptimizer nullOptimizer = new RouteOptimizer(null);
            PathResult result = nullOptimizer.optimizeRoute();
            
            // 根据实现，可能会抛出NullPointerException或返回null
            assertTrue("null搜索算法应该被处理", result == null);
        } catch (NullPointerException e) {
            // 预期的异常，测试通过
            assertTrue("null搜索算法应该抛出NullPointerException", true);
        } catch (Exception e) {
            fail("null搜索算法不应该抛出其他异常: " + e.getMessage());
        }
    }

    /**
     * 测试RouteOptimizer与不同的搜索算法
     * 验证能配合各种搜索算法工作
     */
    @Test
    public void testRouteOptimizerWithDifferentSearchAlgorithms() {
        // 测试Dijkstra算法
        Map<Integer, GasStation> gasStations = new HashMap<>();
        gasStations.put(1, new GasStation(1, 1.5));
        Dijkstra dijkstra = new Dijkstra(graph, startNode, endNode, vehicle, 
                                       trafficCondition, weatherCondition, 10, gasStations);
        
        RouteOptimizer dijkstraOptimizer = new RouteOptimizer(dijkstra);
        PathResult dijkstraResult = dijkstraOptimizer.optimizeRoute();
        assertNotNull("Dijkstra优化结果不应该为null", dijkstraResult);
        
        // 测试A*算法
        AStar aStar = new AStar(graph, startNode, endNode, vehicle, 
                               trafficCondition, weatherCondition, 10);
        
        RouteOptimizer aStarOptimizer = new RouteOptimizer(aStar);
        PathResult aStarResult = aStarOptimizer.optimizeRoute();
        assertNotNull("A*优化结果不应该为null", aStarResult);
        
        // 测试Bellman-Ford算法
        BellmanFord bellmanFord = new BellmanFord(graph, startNode, endNode, vehicle, 
                                                 trafficCondition, weatherCondition, 10);
        
        RouteOptimizer bellmanFordOptimizer = new RouteOptimizer(bellmanFord);
        PathResult bellmanFordResult = bellmanFordOptimizer.optimizeRoute();
        assertNotNull("Bellman-Ford优化结果不应该为null", bellmanFordResult);
        
        // 测试IterativeDeepeningSearch算法
        IterativeDeepeningSearch ids = new IterativeDeepeningSearch(graph, startNode, endNode, vehicle, 
                                                                   trafficCondition, weatherCondition, 10, 5);
        
        RouteOptimizer idsOptimizer = new RouteOptimizer(ids);
        PathResult idsResult = idsOptimizer.optimizeRoute();
        assertNotNull("IDS优化结果不应该为null", idsResult);
        
        // 测试ShortestTimeFirst算法
        ShortestTimeFirst stf = new ShortestTimeFirst(graph, startNode, endNode, vehicle, 
                                                      trafficCondition, weatherCondition, 10);
        
        RouteOptimizer stfOptimizer = new RouteOptimizer(stf);
        PathResult stfResult = stfOptimizer.optimizeRoute();
        assertNotNull("STF优化结果不应该为null", stfResult);
    }

    /**
     * 测试RouteOptimizer的复杂场景
     * 验证在复杂场景下的优化功能
     */
    @Test
    public void testRouteOptimizerComplexScenarios() {
        // 创建复杂图
        Graph complexGraph = new Graph();
        for (int i = 1; i <= 10; i++) {
            complexGraph.addNode(new Node(i, false, "Node" + i, false, false, false, 1.0, 0, 24));
        }
        
        // 添加复杂连接
        for (int i = 1; i < 10; i++) {
            complexGraph.addEdge(i, i + 1, 10.0);
        }
        // 添加一些捷径
        complexGraph.addEdge(1, 5, 30.0);
        complexGraph.addEdge(5, 10, 25.0);
        complexGraph.addEdge(1, 10, 100.0);
        
        Node complexStart = complexGraph.getNode(1);
        Node complexEnd = complexGraph.getNode(10);
        
        // 创建复杂搜索算法
        MockSearchAlgorithm complexSearchAlgorithm = new MockSearchAlgorithm(
            complexGraph, complexStart, complexEnd, vehicle, 
            trafficCondition, weatherCondition, 15
        );
        
        RouteOptimizer complexOptimizer = new RouteOptimizer(complexSearchAlgorithm);
        PathResult complexResult = complexOptimizer.optimizeRoute();
        
        assertNotNull("复杂优化结果不应该为null", complexResult);
        assertNotNull("复杂路径不应该为null", complexResult.getPath());
        assertFalse("复杂路径不应该为空", complexResult.getPath().isEmpty());
        
        // 验证路径的合理性
        assertEquals("复杂路径应该以起始节点开始", complexStart, complexResult.getPath().get(0));
        assertEquals("复杂路径应该以结束节点结束", complexEnd, 
                    complexResult.getPath().get(complexResult.getPath().size() - 1));
    }

    /**
     * 测试RouteOptimizer与特殊节点
     * 验证对特殊类型节点的处理
     */
    @Test
    public void testRouteOptimizerWithSpecialNodes() {
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
        
        // 添加连接（障碍节点不会被添加为邻居）
        specialGraph.addEdge(1, 2, 10.0);
        specialGraph.addEdge(2, 3, 10.0);
        specialGraph.addEdge(3, 4, 10.0);
        
        // 测试从障碍节点开始的优化
        MockSearchAlgorithm obstacleSearchAlgorithm = new MockSearchAlgorithm(
            specialGraph, obstacleNode, highRiskNode, vehicle, 
            trafficCondition, weatherCondition, 10
        );
        
        RouteOptimizer obstacleOptimizer = new RouteOptimizer(obstacleSearchAlgorithm);
        PathResult obstacleResult = obstacleOptimizer.optimizeRoute();
        
        assertNotNull("障碍节点优化结果不应该为null", obstacleResult);
        
        // 测试到高风险区域的优化
        MockSearchAlgorithm riskSearchAlgorithm = new MockSearchAlgorithm(
            specialGraph, tollNode, highRiskNode, vehicle, 
            trafficCondition, weatherCondition, 10
        );
        
        RouteOptimizer riskOptimizer = new RouteOptimizer(riskSearchAlgorithm);
        PathResult riskResult = riskOptimizer.optimizeRoute();
        
        assertNotNull("高风险区域优化结果不应该为null", riskResult);
    }

    /**
     * 测试RouteOptimizer与特殊车辆
     * 验证对特殊类型车辆的处理
     */
    @Test
    public void testRouteOptimizerWithSpecialVehicles() {
        // 测试紧急车辆
        Vehicle emergencyVehicle = new Vehicle("Emergency", 2000.0, false, 60.0, 40.0, 0.8, 8.0, true);
        
        MockSearchAlgorithm emergencySearchAlgorithm = new MockSearchAlgorithm(
            graph, startNode, endNode, emergencyVehicle, 
            trafficCondition, weatherCondition, 10
        );
        
        RouteOptimizer emergencyOptimizer = new RouteOptimizer(emergencySearchAlgorithm);
        PathResult emergencyResult = emergencyOptimizer.optimizeRoute();
        
        assertNotNull("紧急车辆优化结果不应该为null", emergencyResult);
        
        // 测试重型车辆
        Vehicle heavyVehicle = new Vehicle("Heavy", 5000.0, true, 100.0, 50.0, 1.0, 10.0, false);
        
        MockSearchAlgorithm heavySearchAlgorithm = new MockSearchAlgorithm(
            graph, startNode, endNode, heavyVehicle, 
            trafficCondition, weatherCondition, 10
        );
        
        RouteOptimizer heavyOptimizer = new RouteOptimizer(heavySearchAlgorithm);
        PathResult heavyResult = heavyOptimizer.optimizeRoute();
        
        assertNotNull("重型车辆优化结果不应该为null", heavyResult);
        
        // 测试低燃料车辆
        Vehicle lowFuelVehicle = new Vehicle("Low Fuel", 1000.0, false, 40.0, 5.0, 1.0, 2.0, false);
        
        MockSearchAlgorithm lowFuelSearchAlgorithm = new MockSearchAlgorithm(
            graph, startNode, endNode, lowFuelVehicle, 
            trafficCondition, weatherCondition, 10
        );
        
        RouteOptimizer lowFuelOptimizer = new RouteOptimizer(lowFuelSearchAlgorithm);
        PathResult lowFuelResult = lowFuelOptimizer.optimizeRoute();
        
        assertNotNull("低燃料车辆优化结果不应该为null", lowFuelResult);
    }

    /**
     * 测试RouteOptimizer与特殊条件
     * 验证在特殊条件下的优化功能
     */
    @Test
    public void testRouteOptimizerWithSpecialConditions() {
        // 测试恶劣交通条件
        Map<Integer, String> badTrafficData = new HashMap<>();
        badTrafficData.put(1, "Congested");
        badTrafficData.put(2, "Closed");
        badTrafficData.put(3, "Accident");
        TrafficCondition badTraffic = new TrafficCondition(badTrafficData);
        
        MockSearchAlgorithm badTrafficSearchAlgorithm = new MockSearchAlgorithm(
            graph, startNode, endNode, vehicle, badTraffic, weatherCondition, 10
        );
        
        RouteOptimizer badTrafficOptimizer = new RouteOptimizer(badTrafficSearchAlgorithm);
        PathResult badTrafficResult = badTrafficOptimizer.optimizeRoute();
        
        assertNotNull("恶劣交通条件优化结果不应该为null", badTrafficResult);
        
        // 测试恶劣天气条件
        WeatherCondition badWeather = new WeatherCondition("Stormy");
        
        MockSearchAlgorithm badWeatherSearchAlgorithm = new MockSearchAlgorithm(
            graph, startNode, endNode, vehicle, trafficCondition, badWeather, 10
        );
        
        RouteOptimizer badWeatherOptimizer = new RouteOptimizer(badWeatherSearchAlgorithm);
        PathResult badWeatherResult = badWeatherOptimizer.optimizeRoute();
        
        assertNotNull("恶劣天气条件优化结果不应该为null", badWeatherResult);
        
        // 测试组合恶劣条件
        MockSearchAlgorithm combinedBadSearchAlgorithm = new MockSearchAlgorithm(
            graph, startNode, endNode, vehicle, badTraffic, badWeather, 10
        );
        
        RouteOptimizer combinedBadOptimizer = new RouteOptimizer(combinedBadSearchAlgorithm);
        PathResult combinedBadResult = combinedBadOptimizer.optimizeRoute();
        
        assertNotNull("组合恶劣条件优化结果不应该为null", combinedBadResult);
    }

    /**
     * 测试RouteOptimizer的边界情况
     * 验证各种边界情况的处理
     */
    @Test
    public void testRouteOptimizerBoundaryCases() {
        // 测试起始和结束节点相同的情况
        MockSearchAlgorithm sameNodeSearchAlgorithm = new MockSearchAlgorithm(
            graph, startNode, startNode, vehicle, 
            trafficCondition, weatherCondition, 10
        );
        
        RouteOptimizer sameNodeOptimizer = new RouteOptimizer(sameNodeSearchAlgorithm);
        PathResult sameNodeResult = sameNodeOptimizer.optimizeRoute();
        
        assertNotNull("相同节点优化结果不应该为null", sameNodeResult);
        
        // 测试空图
        Graph emptyGraph = new Graph();
        MockSearchAlgorithm emptyGraphSearchAlgorithm = new MockSearchAlgorithm(
            emptyGraph, startNode, endNode, vehicle, 
            trafficCondition, weatherCondition, 10
        );
        
        RouteOptimizer emptyGraphOptimizer = new RouteOptimizer(emptyGraphSearchAlgorithm);
        PathResult emptyGraphResult = emptyGraphOptimizer.optimizeRoute();
        
        assertNotNull("空图优化结果不应该为null", emptyGraphResult);
        
        // 测试没有路径的情况
        Graph noPathGraph = new Graph();
        noPathGraph.addNode(startNode);
        noPathGraph.addNode(endNode);
        // 不添加边
        
        MockSearchAlgorithm noPathSearchAlgorithm = new MockSearchAlgorithm(
            noPathGraph, startNode, endNode, vehicle, 
            trafficCondition, weatherCondition, 10
        );
        
        RouteOptimizer noPathOptimizer = new RouteOptimizer(noPathSearchAlgorithm);
        PathResult noPathResult = noPathOptimizer.optimizeRoute();
        
        assertNotNull("无路径优化结果不应该为null", noPathResult);
    }

    /**
     * 测试RouteOptimizer的多次调用
     * 验证多次优化调用的一致性
     */
    @Test
    public void testRouteOptimizerMultipleCalls() {
        // 多次调用optimizeRoute
        PathResult result1 = routeOptimizer.optimizeRoute();
        PathResult result2 = routeOptimizer.optimizeRoute();
        PathResult result3 = routeOptimizer.optimizeRoute();
        
        assertNotNull("第一次优化结果不应该为null", result1);
        assertNotNull("第二次优化结果不应该为null", result2);
        assertNotNull("第三次优化结果不应该为null", result3);
        
        // 验证结果的一致性（路径长度应该相同）
        assertEquals("多次优化结果路径长度应该相同", 
                    result1.getPath().size(), result2.getPath().size());
        assertEquals("多次优化结果路径长度应该相同", 
                    result2.getPath().size(), result3.getPath().size());
        
        // 验证起始和结束节点的一致性
        assertEquals("多次优化起始节点应该相同", 
                    result1.getPath().get(0), result2.getPath().get(0));
        assertEquals("多次优化起始节点应该相同", 
                    result2.getPath().get(0), result3.getPath().get(0));
        
        assertEquals("多次优化结束节点应该相同", 
                    result1.getPath().get(result1.getPath().size() - 1), 
                    result2.getPath().get(result2.getPath().size() - 1));
        assertEquals("多次优化结束节点应该相同", 
                    result2.getPath().get(result2.getPath().size() - 1), 
                    result3.getPath().get(result3.getPath().size() - 1));
    }

    /**
     * 模拟搜索算法类，用于测试RouteOptimizer
     */
    private static class MockSearchAlgorithm extends SearchAlgorithm {
        
        public MockSearchAlgorithm(Graph graph, Node startNode, Node endNode, Vehicle vehicle,
                                 TrafficCondition trafficCondition, WeatherCondition weatherCondition, int currentTime) {
            super(graph, startNode, endNode, vehicle, trafficCondition, weatherCondition, currentTime);
        }
        
        @Override
        public PathResult findPath() {
            if (graph == null || startNode == null || endNode == null) {
                return new PathResult(new java.util.ArrayList<>());
            }
            
            java.util.List<Node> path = new java.util.ArrayList<>();
            path.add(startNode);
            
            if (!startNode.equals(endNode)) {
                // 尝试找到一条简单路径
                for (Edge edge : startNode.getNeighbors()) {
                    if (edge.getNeighbor().equals(endNode)) {
                        path.add(endNode);
                        return new PathResult(path);
                    }
                }
                
                // 如果没有直接连接，尝试通过中间节点
                for (Edge edge : startNode.getNeighbors()) {
                    Node middle = edge.getNeighbor();
                    for (Edge middleEdge : middle.getNeighbors()) {
                        if (middleEdge.getNeighbor().equals(endNode)) {
                            path.add(middle);
                            path.add(endNode);
                            return new PathResult(path);
                        }
                    }
                }
                
                // 如果仍然没有路径，直接添加结束节点
                path.add(endNode);
            }
            
            return new PathResult(path);
        }
    }
}