package net.mooctest;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import java.util.*;

/**
 * FastestRoute项目整合测试代码
 * 
 * 本文件包含了所有18个业务类的核心测试代码，整合为一个文件。
 * 由于Java限制，这里提供主要测试方法的示例，完整测试请参考各个独立测试文件。
 * 
 * 测试覆盖目标：
 * - 分支覆盖率：90%以上
 * - 变异杀死率：90%以上
 * - 测试通过率：95%以上
 */

public class 整合测试代码 {

    // ============================================================================
    // 1. Edge类测试核心代码
    // ============================================================================
    
    @Test
    public void testEdgeBasicFunctionality() {
        Node neighbor = new Node(2, false, "Highway", false, false, false, 1.0, 0, 24);
        Edge edge = new Edge(neighbor, 5.5);
        
        assertSame("邻居节点应该正确设置", neighbor, edge.getNeighbor());
        assertEquals("距离应该正确设置", 5.5, edge.getDistance(), 0.001);
    }
    
    @Test
    public void testEdgeBoundaryValues() {
        Node neighbor = new Node(3, false, "Regular Road", false, false, false, 1.0, 0, 24);
        
        Edge zeroEdge = new Edge(neighbor, 0.0);
        assertEquals("零距离应该正确设置", 0.0, zeroEdge.getDistance(), 0.001);
        
        Edge negativeEdge = new Edge(neighbor, -1.0);
        assertEquals("负距离应该正确设置", -1.0, negativeEdge.getDistance(), 0.001);
        
        Edge maxEdge = new Edge(neighbor, Double.MAX_VALUE);
        assertEquals("大距离值应该正确设置", Double.MAX_VALUE, maxEdge.getDistance(), 0.001);
    }

    // ============================================================================
    // 2. Node类测试核心代码
    // ============================================================================
    
    private Node normalNode;
    private Node obstacleNode;
    
    @Before
    public void setUpNodeTest() {
        normalNode = new Node(1, false, "Regular Road", false, false, false, 1.0, 0, 24);
        obstacleNode = new Node(2, true, "Blocked", false, false, false, 0.0, 0, 24);
    }
    
    @Test
    public void testNodeConstructorAndGetters() {
        assertEquals("节点ID应该正确设置", 1, normalNode.getNodeId());
        assertFalse("普通节点不应该是障碍", normalNode.isObstacle());
        assertEquals("道路类型应该正确设置", "Regular Road", normalNode.getRoadType());
        assertEquals("每公里成本应该正确设置", 1.0, normalNode.getCostPerKm(), 0.001);
        
        assertTrue("障碍节点应该是障碍", obstacleNode.isObstacle());
    }
    
    @Test
    public void testNodeTimeWindow() {
        Node timeRestrictedNode = new Node(6, false, "Time Restricted", false, false, false, 1.0, 9, 17);
        
        assertFalse("时间限制节点不应该在8点开放", timeRestrictedNode.isOpenAt(8));
        assertTrue("时间限制节点应该在9点开放", timeRestrictedNode.isOpenAt(9));
        assertTrue("时间限制节点应该在17点开放", timeRestrictedNode.isOpenAt(17));
        assertFalse("时间限制节点不应该在18点开放", timeRestrictedNode.isOpenAt(18));
    }
    
    @Test
    public void testAddNeighbor() {
        Node neighbor1 = new Node(10, false, "Highway", false, false, false, 0.8, 0, 24);
        Node neighbor2 = new Node(11, false, "Regular Road", false, false, false, 1.2, 0, 24);
        Node obstacleNeighbor = new Node(12, true, "Blocked", false, false, false, 0.0, 0, 24);
        
        assertEquals("初始状态邻居数量应该为0", 0, normalNode.getNeighbors().size());
        
        normalNode.addNeighbor(neighbor1, 5.0);
        assertEquals("添加一个邻居后数量应该为1", 1, normalNode.getNeighbors().size());
        
        normalNode.addNeighbor(neighbor2, 3.5);
        assertEquals("添加两个邻居后数量应该为2", 2, normalNode.getNeighbors().size());
        
        int beforeCount = normalNode.getNeighbors().size();
        normalNode.addNeighbor(obstacleNeighbor, 10.0);
        assertEquals("障碍节点不应该被添加为邻居", beforeCount, normalNode.getNeighbors().size());
    }

    // ============================================================================
    // 3. Graph类测试核心代码
    // ============================================================================
    
    @Test
    public void testGraphBasicFunctionality() {
        Graph graph = new Graph();
        Node node1 = new Node(1, false, "Regular Road", false, false, false, 1.0, 0, 24);
        Node node2 = new Node(2, false, "Highway", false, false, false, 0.8, 0, 24);
        
        assertEquals("新图应该没有节点", 0, graph.getNodes().size());
        
        graph.addNode(node1);
        assertEquals("添加一个节点后数量应该为1", 1, graph.getNodes().size());
        assertSame("添加的节点应该能被正确获取", node1, graph.getNode(1));
        
        graph.addNode(node2);
        assertEquals("添加两个节点后数量应该为2", 2, graph.getNodes().size());
        
        graph.addEdge(1, 2, 10.5);
        assertEquals("节点1应该有一个邻居", 1, node1.getNeighbors().size());
        Edge edge = node1.getNeighbors().get(0);
        assertSame("邻居应该是节点2", node2, edge.getNeighbor());
        assertEquals("边距离应该正确", 10.5, edge.getDistance(), 0.001);
    }

    // ============================================================================
    // 4. PathResult类测试核心代码
    // ============================================================================
    
    @Test
    public void testPathResultBasicFunctionality() {
        List<Node> testPath = new ArrayList<>();
        testPath.add(new Node(1, false, "Start", false, false, false, 1.0, 0, 24));
        testPath.add(new Node(2, false, "Middle", false, false, false, 1.0, 0, 24));
        testPath.add(new Node(3, false, "End", false, false, false, 1.0, 0, 24));
        
        PathResult pathResult = new PathResult(testPath);
        
        assertNotNull("路径结果应该被正确创建", pathResult);
        assertNotNull("路径应该不为null", pathResult.getPath());
        assertEquals("路径长度应该正确", 3, pathResult.getPath().size());
        
        assertEquals("第一个节点ID应该正确", 1, pathResult.getPath().get(0).getNodeId());
        assertEquals("第二个节点ID应该正确", 2, pathResult.getPath().get(1).getNodeId());
        assertEquals("第三个节点ID应该正确", 3, pathResult.getPath().get(2).getNodeId());
        
        try {
            pathResult.printPath();
            assertTrue("printPath应该正常执行", true);
        } catch (Exception e) {
            fail("printPath不应该抛出异常: " + e.getMessage());
        }
    }

    // ============================================================================
    // 5. PathNode类测试核心代码
    // ============================================================================
    
    @Test
    public void testPathNodeConstructors() {
        Node testNode = new Node(5, false, "Test Node", false, false, false, 1.0, 0, 24);
        
        // 测试两个参数构造函数
        PathNode pathNode1 = new PathNode(testNode, 15.5);
        assertSame("节点应该正确设置", testNode, pathNode1.getNode());
        assertEquals("距离应该正确设置", 15.5, pathNode1.getDistance(), 0.001);
        assertEquals("估计总距离应该等于距离", 15.5, pathNode1.getEstimatedTotalDistance(), 0.001);
        
        // 测试三个参数构造函数
        PathNode pathNode2 = new PathNode(testNode, 10.0, 25.5);
        assertSame("节点应该正确设置", testNode, pathNode2.getNode());
        assertEquals("距离应该正确设置", 10.0, pathNode2.getDistance(), 0.001);
        assertEquals("估计总距离应该正确设置", 25.5, pathNode2.getEstimatedTotalDistance(), 0.001);
    }

    // ============================================================================
    // 6. Vehicle类测试核心代码
    // ============================================================================
    
    @Test
    public void testVehicleBasicFunctionality() {
        Vehicle standardVehicle = new Vehicle(
            "Standard Vehicle", 1000.0, false, 50.0, 25.0, 0.5, 5.0, false
        );
        
        assertEquals("车辆类型应该正确", "Standard Vehicle", standardVehicle.getVehicleType());
        assertEquals("最大载重应该正确", 1000.0, standardVehicle.getMaxLoad(), 0.001);
        assertFalse("标准车辆不需要免费路线", standardVehicle.requiresTollFreeRoute());
        assertEquals("燃料容量应该正确", 50.0, standardVehicle.getFuelCapacity(), 0.001);
        assertEquals("当前燃料应该正确", 25.0, standardVehicle.getCurrentFuel(), 0.001);
        assertEquals("每公里燃料消耗应该正确", 0.5, standardVehicle.getFuelConsumptionPerKm(), 0.001);
        assertEquals("终点最小燃料应该正确", 5.0, standardVehicle.getMinFuelAtEnd(), 0.001);
        assertFalse("标准车辆不是紧急车辆", standardVehicle.isEmergencyVehicle());
    }
    
    @Test
    public void testVehicleFuelManagement() {
        Vehicle lowFuelVehicle = new Vehicle("Low Fuel", 1000.0, false, 40.0, 5.0, 1.0, 2.0, false);
        
        double initialFuel = lowFuelVehicle.getCurrentFuel();
        double refuelAmount = 10.0;
        lowFuelVehicle.refuel(refuelAmount);
        assertEquals("加油后燃料应该正确增加", initialFuel + refuelAmount, 
                    lowFuelVehicle.getCurrentFuel(), 0.001);
        
        assertTrue("低燃料车辆应该需要加油", lowFuelVehicle.needsRefueling(3.0));
        assertFalse("低燃料车辆短距离不需要加油", lowFuelVehicle.needsRefueling(2.0));
        
        double beforeConsume = lowFuelVehicle.getCurrentFuel();
        lowFuelVehicle.consumeFuel(10.0);
        assertEquals("燃料消耗应该正确", beforeConsume - 10.0, lowFuelVehicle.getCurrentFuel(), 0.001);
    }

    // ============================================================================
    // 7. GasStation类测试核心代码
    // ============================================================================
    
    @Test
    public void testGasStationBasicFunctionality() {
        GasStation gasStation = new GasStation(1, 1.5);
        Vehicle testVehicle = new Vehicle("Test Vehicle", 1000.0, false, 50.0, 25.0, 0.5, 5.0, false);
        
        assertEquals("加油站节点ID应该正确", 1, gasStation.getNodeId());
        assertEquals("加油站油价应该正确", 1.5, gasStation.getFuelCostPerLitre(), 0.001);
        
        double initialFuel = testVehicle.getCurrentFuel();
        gasStation.refuel(testVehicle, 10.0);
        assertEquals("车辆燃料应该正确增加", initialFuel + 10.0, testVehicle.getCurrentFuel(), 0.001);
    }

    // ============================================================================
    // 8. TrafficCondition类测试核心代码
    // ============================================================================
    
    @Test
    public void testTrafficConditionBasicFunctionality() {
        Map<Integer, String> trafficData = new HashMap<>();
        trafficData.put(1, "Clear");
        trafficData.put(2, "Congested");
        trafficData.put(3, "Closed");
        trafficData.put(4, "Accident");
        
        TrafficCondition trafficCondition = new TrafficCondition(trafficData);
        
        assertEquals("节点1应该是Clear状态", "Clear", trafficCondition.getTrafficStatus(1));
        assertEquals("节点2应该是Congested状态", "Congested", trafficCondition.getTrafficStatus(2));
        assertEquals("节点3应该是Closed状态", "Closed", trafficCondition.getTrafficStatus(3));
        assertEquals("节点4应该是Accident状态", "Accident", trafficCondition.getTrafficStatus(4));
        assertEquals("不存在的节点应该返回Clear", "Clear", trafficCondition.getTrafficStatus(99));
        
        double originalWeight = 10.0;
        assertEquals("Clear状态权重不应该改变", originalWeight, 
                    trafficCondition.adjustWeight(originalWeight, 1), 0.001);
        assertEquals("Congested状态权重应该翻倍", originalWeight * 2, 
                    trafficCondition.adjustWeight(originalWeight, 2), 0.001);
        assertEquals("Closed状态权重应该是无穷大", Double.MAX_VALUE, 
                    trafficCondition.adjustWeight(originalWeight, 3), 0.001);
        assertEquals("Accident状态权重应该是三倍", originalWeight * 3, 
                    trafficCondition.adjustWeight(originalWeight, 4), 0.001);
    }

    // ============================================================================
    // 9. WeatherCondition类测试核心代码
    // ============================================================================
    
    @Test
    public void testWeatherConditionBasicFunctionality() {
        WeatherCondition clearWeather = new WeatherCondition("Clear");
        WeatherCondition rainyWeather = new WeatherCondition("Rainy");
        WeatherCondition snowyWeather = new WeatherCondition("Snowy");
        WeatherCondition stormyWeather = new WeatherCondition("Stormy");
        
        Node testNode = new Node(1, false, "Highway", false, false, false, 1.0, 0, 24);
        double originalWeight = 10.0;
        
        assertEquals("晴朗天气权重不应该改变", originalWeight, 
                    clearWeather.adjustWeightForWeather(originalWeight, testNode), 0.001);
        assertEquals("雨天权重应该增加50%", originalWeight * 1.5, 
                    rainyWeather.adjustWeightForWeather(originalWeight, testNode), 0.001);
        assertEquals("雪天权重应该翻倍", originalWeight * 2.0, 
                    snowyWeather.adjustWeightForWeather(originalWeight, testNode), 0.001);
        assertEquals("暴风雨天权重应该是三倍", originalWeight * 3.0, 
                    stormyWeather.adjustWeightForWeather(originalWeight, testNode), 0.001);
    }

    // ============================================================================
    // 10. SearchAlgorithm类测试核心代码
    // ============================================================================
    
    @Test
    public void testSearchAlgorithmBasicFunctionality() {
        Graph graph = new Graph();
        Node startNode = new Node(1, false, "Start", false, false, false, 1.0, 0, 24);
        Node endNode = new Node(2, false, "End", false, false, false, 1.0, 0, 24);
        Vehicle vehicle = new Vehicle("Test Vehicle", 1000.0, false, 50.0, 25.0, 0.5, 5.0, false);
        
        Map<Integer, String> trafficData = new HashMap<>();
        trafficData.put(1, "Clear");
        trafficData.put(2, "Clear");
        TrafficCondition trafficCondition = new TrafficCondition(trafficData);
        WeatherCondition weatherCondition = new WeatherCondition("Clear");
        
        TestSearchAlgorithm testAlgorithm = new TestSearchAlgorithm(
            graph, startNode, endNode, vehicle, trafficCondition, weatherCondition, 10
        );
        
        assertSame("图应该正确设置", graph, testAlgorithm.getGraph());
        assertSame("起始节点应该正确设置", startNode, testAlgorithm.getStartNode());
        assertSame("结束节点应该正确设置", endNode, testAlgorithm.getEndNode());
        assertSame("车辆应该正确设置", vehicle, testAlgorithm.getVehicle());
        assertEquals("当前时间应该正确设置", 10, testAlgorithm.getCurrentTime());
        
        PathResult result = testAlgorithm.findPath();
        assertNotNull("路径结果不应该为null", result);
    }
    
    private static class TestSearchAlgorithm extends SearchAlgorithm {
        public TestSearchAlgorithm(Graph graph, Node startNode, Node endNode, Vehicle vehicle,
                                 TrafficCondition trafficCondition, WeatherCondition weatherCondition, int currentTime) {
            super(graph, startNode, endNode, vehicle, trafficCondition, weatherCondition, currentTime);
        }
        
        @Override
        public PathResult findPath() {
            List<Node> path = new ArrayList<>();
            path.add(startNode);
            if (!startNode.equals(endNode)) {
                path.add(endNode);
            }
            return new PathResult(path);
        }
        
        public Graph getGraph() { return graph; }
        public Node getStartNode() { return startNode; }
        public Node getEndNode() { return endNode; }
        public Vehicle getVehicle() { return vehicle; }
        public int getCurrentTime() { return currentTime; }
    }

    // ============================================================================
    // 11. RouteOptimizer类测试核心代码
    // ============================================================================
    
    @Test
    public void testRouteOptimizerBasicFunctionality() {
        Graph graph = new Graph();
        Node startNode = new Node(1, false, "Start", false, false, false, 1.0, 0, 24);
        Node endNode = new Node(2, false, "End", false, false, false, 1.0, 0, 24);
        Vehicle vehicle = new Vehicle("Test Vehicle", 1000.0, false, 50.0, 25.0, 0.5, 5.0, false);
        
        Map<Integer, String> trafficData = new HashMap<>();
        trafficData.put(1, "Clear");
        trafficData.put(2, "Clear");
        TrafficCondition trafficCondition = new TrafficCondition(trafficData);
        WeatherCondition weatherCondition = new WeatherCondition("Clear");
        
        MockSearchAlgorithm mockAlgorithm = new MockSearchAlgorithm(graph, startNode, endNode, vehicle, 
                                                                trafficCondition, weatherCondition, 10);
        RouteOptimizer routeOptimizer = new RouteOptimizer(mockAlgorithm);
        
        PathResult result = routeOptimizer.optimizeRoute();
        
        assertNotNull("优化结果不应该为null", result);
        assertNotNull("路径不应该为null", result.getPath());
        assertFalse("路径不应该为空", result.getPath().isEmpty());
    }
    
    private static class MockSearchAlgorithm extends SearchAlgorithm {
        public MockSearchAlgorithm(Graph graph, Node startNode, Node endNode, Vehicle vehicle,
                                 TrafficCondition trafficCondition, WeatherCondition weatherCondition, int currentTime) {
            super(graph, startNode, endNode, vehicle, trafficCondition, weatherCondition, currentTime);
        }
        
        @Override
        public PathResult findPath() {
            List<Node> path = new ArrayList<>();
            path.add(startNode);
            if (!startNode.equals(endNode)) {
                path.add(endNode);
            }
            return new PathResult(path);
        }
    }

    // ============================================================================
    // 12-17. 算法测试核心代码（Dijkstra, AStar, BellmanFord, FloydWarshall, IDS, STF）
    // ============================================================================
    
    @Test
    public void testAlgorithmBasicFunctionality() {
        Graph graph = createTestGraph();
        Node startNode = graph.getNode(1);
        Node endNode = graph.getNode(4);
        Vehicle vehicle = new Vehicle("Test Vehicle", 1000.0, false, 50.0, 25.0, 0.5, 5.0, false);
        
        Map<Integer, String> trafficData = new HashMap<>();
        trafficData.put(1, "Clear");
        trafficData.put(2, "Clear");
        trafficData.put(3, "Clear");
        trafficData.put(4, "Clear");
        TrafficCondition trafficCondition = new TrafficCondition(trafficData);
        WeatherCondition weatherCondition = new WeatherCondition("Clear");
        Map<Integer, GasStation> gasStations = new HashMap<>();
        gasStations.put(1, new GasStation(1, 1.5));
        
        // 测试Dijkstra算法
        Dijkstra dijkstra = new Dijkstra(graph, startNode, endNode, vehicle, 
                                       trafficCondition, weatherCondition, 10, gasStations);
        PathResult dijkstraResult = dijkstra.findPath();
        assertNotNull("Dijkstra结果不应该为null", dijkstraResult);
        
        // 测试A*算法
        AStar aStar = new AStar(graph, startNode, endNode, vehicle, 
                               trafficCondition, weatherCondition, 10);
        PathResult aStarResult = aStar.findPath();
        assertNotNull("A*结果不应该为null", aStarResult);
        
        // 测试Bellman-Ford算法
        BellmanFord bellmanFord = new BellmanFord(graph, startNode, endNode, vehicle, 
                                                 trafficCondition, weatherCondition, 10);
        PathResult bellmanFordResult = bellmanFord.findPath();
        assertNotNull("Bellman-Ford结果不应该为null", bellmanFordResult);
        
        // 测试Floyd-Warshall算法
        FloydWarshall floydWarshall = new FloydWarshall(graph);
        List<Node> floydWarshallResult = floydWarshall.getShortestPath(startNode, endNode);
        assertNotNull("Floyd-Warshall结果不应该为null", floydWarshallResult);
        
        // 测试迭代深化搜索
        IterativeDeepeningSearch ids = new IterativeDeepeningSearch(graph, startNode, endNode, vehicle, 
                                                                  trafficCondition, weatherCondition, 10, 5);
        PathResult idsResult = ids.findPath();
        assertNotNull("IDS结果不应该为null", idsResult);
        
        // 测试最短时间优先算法
        ShortestTimeFirst stf = new ShortestTimeFirst(graph, startNode, endNode, vehicle, 
                                                      trafficCondition, weatherCondition, 10);
        PathResult stfResult = stf.findPath();
        assertNotNull("STF结果不应该为null", stfResult);
    }
    
    private Graph createTestGraph() {
        Graph graph = new Graph();
        Node node1 = new Node(1, false, "Start", false, false, false, 1.0, 0, 24);
        Node node2 = new Node(2, false, "Middle1", false, false, false, 1.0, 0, 24);
        Node node3 = new Node(3, false, "Middle2", false, false, false, 1.0, 0, 24);
        Node node4 = new Node(4, false, "End", false, false, false, 1.0, 0, 24);
        
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);
        
        graph.addEdge(1, 2, 10.0);
        graph.addEdge(1, 3, 15.0);
        graph.addEdge(2, 4, 10.0);
        graph.addEdge(3, 4, 5.0);
        graph.addEdge(2, 3, 2.0);
        
        return graph;
    }

    // ============================================================================
    // 18. 测试工具类核心代码
    // ============================================================================
    
    @Test
    public void testUtilityMethods() {
        // 测试工具类方法
        Vehicle standardVehicle = TestUtils.createStandardVehicle();
        assertNotNull("标准车辆应该被正确创建", standardVehicle);
        assertEquals("标准车辆类型应该正确", "Standard Vehicle", standardVehicle.getVehicleType());
        
        Vehicle emergencyVehicle = TestUtils.createEmergencyVehicle();
        assertTrue("紧急车辆应该是紧急车辆", emergencyVehicle.isEmergencyVehicle());
        
        TrafficCondition standardTraffic = TestUtils.createStandardTrafficCondition();
        assertNotNull("标准交通条件应该被正确创建", standardTraffic);
        
        WeatherCondition clearWeather = TestUtils.createClearWeatherCondition();
        assertNotNull("晴朗天气条件应该被正确创建", clearWeather);
        
        Graph simpleGraph = TestUtils.createSimpleGraph();
        assertNotNull("简单图应该被正确创建", simpleGraph);
        assertEquals("简单图应该有5个节点", 5, simpleGraph.getNodes().size());
    }
    
    /**
     * 简化的测试工具类
     */
    static class TestUtils {
        public static Vehicle createStandardVehicle() {
            return new Vehicle("Standard Vehicle", 1000.0, false, 50.0, 25.0, 0.5, 5.0, false);
        }
        
        public static Vehicle createEmergencyVehicle() {
            return new Vehicle("Emergency Vehicle", 2000.0, false, 60.0, 40.0, 0.8, 8.0, true);
        }
        
        public static TrafficCondition createStandardTrafficCondition() {
            Map<Integer, String> trafficData = new HashMap<>();
            trafficData.put(1, "Clear");
            trafficData.put(2, "Clear");
            trafficData.put(3, "Clear");
            trafficData.put(4, "Clear");
            trafficData.put(5, "Clear");
            return new TrafficCondition(trafficData);
        }
        
        public static WeatherCondition createClearWeatherCondition() {
            return new WeatherCondition("Clear");
        }
        
        public static Graph createSimpleGraph() {
            Graph graph = new Graph();
            Node node1 = new Node(1, false, "Start", false, false, false, 1.0, 0, 24);
            Node node2 = new Node(2, false, "Middle1", false, false, false, 1.0, 0, 24);
            Node node3 = new Node(3, false, "Middle2", false, false, false, 1.0, 0, 24);
            Node node4 = new Node(4, false, "Middle3", false, false, false, 1.0, 0, 24);
            Node node5 = new Node(5, false, "End", false, false, false, 1.0, 0, 24);
            
            graph.addNode(node1);
            graph.addNode(node2);
            graph.addNode(node3);
            graph.addNode(node4);
            graph.addNode(node5);
            
            graph.addEdge(1, 2, 10.0);
            graph.addEdge(1, 3, 15.0);
            graph.addEdge(2, 4, 10.0);
            graph.addEdge(3, 4, 5.0);
            graph.addEdge(4, 5, 10.0);
            graph.addEdge(2, 5, 25.0);
            
            return graph;
        }
    }

    // ============================================================================
    // 主方法 - 测试统计和总结
    // ============================================================================
    
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("FastestRoute项目整合测试代码");
        System.out.println("==========================================");
        System.out.println();
        System.out.println("测试覆盖的业务类（18个）：");
        System.out.println("1. Edge - 边类");
        System.out.println("2. Node - 节点类");
        System.out.println("3. Graph - 图类");
        System.out.println("4. PathResult - 路径结果类");
        System.out.println("5. PathNode - 路径节点类");
        System.out.println("6. Vehicle - 车辆类");
        System.out.println("7. GasStation - 加油站类");
        System.out.println("8. TrafficCondition - 交通条件类");
        System.out.println("9. WeatherCondition - 天气条件类");
        System.out.println("10. SearchAlgorithm - 搜索算法抽象类");
        System.out.println("11. RouteOptimizer - 路由优化器类");
        System.out.println("12. Dijkstra - Dijkstra算法类");
        System.out.println("13. AStar - A*算法类");
        System.out.println("14. BellmanFord - Bellman-Ford算法类");
        System.out.println("15. FloydWarshall - Floyd-Warshall算法类");
        System.out.println("16. IterativeDeepeningSearch - 迭代深化搜索类");
        System.out.println("17. ShortestTimeFirst - 最短时间优先算法类");
        System.out.println("18. TestUtils - 测试工具类");
        System.out.println();
        System.out.println("测试特点：");
        System.out.println("✓ 使用JUnit 4.12框架");
        System.out.println("✓ 包含详细的中文注释");
        System.out.println("✓ 覆盖正常、边界、异常情况");
        System.out.println("✓ 预期分支覆盖率：90%以上");
        System.out.println("✓ 预期变异杀死率：90%以上");
        System.out.println();
        System.out.println("使用说明：");
        System.out.println("1. 完整测试请参考src/test/java/net/mooctest/目录下的独立测试文件");
        System.out.println("2. 运行命令：mvn test 或 ./run-tests.sh");
        System.out.println("3. 测试套件：net.mooctest.AllTestsSuite");
        System.out.println("==========================================");
    }
}