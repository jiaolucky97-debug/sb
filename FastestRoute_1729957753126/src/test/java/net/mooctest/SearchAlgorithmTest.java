package net.mooctest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

/**
 * SearchAlgorithm抽象类的测试类
 * 测试搜索算法的抽象基类功能
 */
public class SearchAlgorithmTest {
    
    private Graph graph;
    private Node startNode;
    private Node endNode;
    private Vehicle vehicle;
    private TrafficCondition trafficCondition;
    private WeatherCondition weatherCondition;
    private Map<Integer, GasStation> gasStations;

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
        
        // 创建测试车辆
        vehicle = new Vehicle("Test Vehicle", 1000.0, false, 50.0, 25.0, 0.5, 5.0, false);
        
        // 创建测试条件
        Map<Integer, String> trafficData = new HashMap<>();
        trafficData.put(1, "Clear");
        trafficData.put(2, "Clear");
        trafficData.put(3, "Clear");
        trafficCondition = new TrafficCondition(trafficData);
        
        weatherCondition = new WeatherCondition("Clear");
        
        // 创建加油站
        gasStations = new HashMap<>();
        gasStations.put(1, new GasStation(1, 1.5));
        gasStations.put(3, new GasStation(3, 1.8));
    }

    /**
     * 测试SearchAlgorithm的具体实现类
     * 通过具体实现类测试抽象类的功能
     */
    @Test
    public void testSearchAlgorithmImplementation() {
        // 创建一个简单的测试实现
        TestSearchAlgorithm testAlgorithm = new TestSearchAlgorithm(
            graph, startNode, endNode, vehicle, trafficCondition, weatherCondition, 10
        );
        
        // 验证构造函数参数设置
        assertSame("图应该正确设置", graph, testAlgorithm.getGraph());
        assertSame("起始节点应该正确设置", startNode, testAlgorithm.getStartNode());
        assertSame("结束节点应该正确设置", endNode, testAlgorithm.getEndNode());
        assertSame("车辆应该正确设置", vehicle, testAlgorithm.getVehicle());
        assertSame("交通条件应该正确设置", trafficCondition, testAlgorithm.getTrafficCondition());
        assertSame("天气条件应该正确设置", weatherCondition, testAlgorithm.getWeatherCondition());
        assertEquals("当前时间应该正确设置", 10, testAlgorithm.getCurrentTime());
        
        // 测试findPath方法
        PathResult result = testAlgorithm.findPath();
        assertNotNull("路径结果不应该为null", result);
        assertEquals("路径应该包含起始节点", startNode, result.getPath().get(0));
        assertEquals("路径应该包含结束节点", endNode, result.getPath().get(result.getPath().size() - 1));
    }

    /**
     * 测试SearchAlgorithm构造函数的边界值
     * 验证各种null参数的处理
     */
    @Test
    public void testSearchAlgorithmConstructorWithNullParameters() {
        // 测试null图
        try {
            TestSearchAlgorithm nullGraphAlgorithm = new TestSearchAlgorithm(
                null, startNode, endNode, vehicle, trafficCondition, weatherCondition, 10
            );
            assertNull("null图应该被正确处理", nullGraphAlgorithm.getGraph());
        } catch (Exception e) {
            fail("null图不应该导致异常: " + e.getMessage());
        }
        
        // 测试null起始节点
        try {
            TestSearchAlgorithm nullStartAlgorithm = new TestSearchAlgorithm(
                graph, null, endNode, vehicle, trafficCondition, weatherCondition, 10
            );
            assertNull("null起始节点应该被正确处理", nullStartAlgorithm.getStartNode());
        } catch (Exception e) {
            fail("null起始节点不应该导致异常: " + e.getMessage());
        }
        
        // 测试null结束节点
        try {
            TestSearchAlgorithm nullEndAlgorithm = new TestSearchAlgorithm(
                graph, startNode, null, vehicle, trafficCondition, weatherCondition, 10
            );
            assertNull("null结束节点应该被正确处理", nullEndAlgorithm.getEndNode());
        } catch (Exception e) {
            fail("null结束节点不应该导致异常: " + e.getMessage());
        }
        
        // 测试null车辆
        try {
            TestSearchAlgorithm nullVehicleAlgorithm = new TestSearchAlgorithm(
                graph, startNode, endNode, null, trafficCondition, weatherCondition, 10
            );
            assertNull("null车辆应该被正确处理", nullVehicleAlgorithm.getVehicle());
        } catch (Exception e) {
            fail("null车辆不应该导致异常: " + e.getMessage());
        }
        
        // 测试null交通条件
        try {
            TestSearchAlgorithm nullTrafficAlgorithm = new TestSearchAlgorithm(
                graph, startNode, endNode, vehicle, null, weatherCondition, 10
            );
            assertNull("null交通条件应该被正确处理", nullTrafficAlgorithm.getTrafficCondition());
        } catch (Exception e) {
            fail("null交通条件不应该导致异常: " + e.getMessage());
        }
        
        // 测试null天气条件
        try {
            TestSearchAlgorithm nullWeatherAlgorithm = new TestSearchAlgorithm(
                graph, startNode, endNode, vehicle, trafficCondition, null, 10
            );
            assertNull("null天气条件应该被正确处理", nullWeatherAlgorithm.getWeatherCondition());
        } catch (Exception e) {
            fail("null天气条件不应该导致异常: " + e.getMessage());
        }
    }

    /**
     * 测试SearchAlgorithm的时间参数
     * 验证不同时间值的处理
     */
    @Test
    public void testSearchAlgorithmTimeParameters() {
        // 测试正常时间
        TestSearchAlgorithm normalTimeAlgorithm = new TestSearchAlgorithm(
            graph, startNode, endNode, vehicle, trafficCondition, weatherCondition, 12
        );
        assertEquals("正常时间应该正确设置", 12, normalTimeAlgorithm.getCurrentTime());
        
        // 测试零时间
        TestSearchAlgorithm zeroTimeAlgorithm = new TestSearchAlgorithm(
            graph, startNode, endNode, vehicle, trafficCondition, weatherCondition, 0
        );
        assertEquals("零时间应该正确设置", 0, zeroTimeAlgorithm.getCurrentTime());
        
        // 测试负时间
        TestSearchAlgorithm negativeTimeAlgorithm = new TestSearchAlgorithm(
            graph, startNode, endNode, vehicle, trafficCondition, weatherCondition, -5
        );
        assertEquals("负时间应该正确设置", -5, negativeTimeAlgorithm.getCurrentTime());
        
        // 测试极大时间
        TestSearchAlgorithm maxTimeAlgorithm = new TestSearchAlgorithm(
            graph, startNode, endNode, vehicle, trafficCondition, weatherCondition, Integer.MAX_VALUE
        );
        assertEquals("极大时间应该正确设置", Integer.MAX_VALUE, maxTimeAlgorithm.getCurrentTime());
        
        // 测试极小时间
        TestSearchAlgorithm minTimeAlgorithm = new TestSearchAlgorithm(
            graph, startNode, endNode, vehicle, trafficCondition, weatherCondition, Integer.MIN_VALUE
        );
        assertEquals("极小时间应该正确设置", Integer.MIN_VALUE, minTimeAlgorithm.getCurrentTime());
    }

    /**
     * 测试SearchAlgorithm与不同类型的节点
     * 验证算法能处理各种类型的节点
     */
    @Test
    public void testSearchAlgorithmWithDifferentNodeTypes() {
        // 创建特殊类型的节点
        Node obstacleStart = new Node(1, true, "Obstacle Start", false, false, false, 0.0, 0, 24);
        Node tollEnd = new Node(2, false, "Toll End", true, false, false, 2.0, 0, 24);
        Node restrictedMiddle = new Node(3, false, "Restricted", false, true, false, 1.5, 0, 24);
        
        Graph specialGraph = new Graph();
        specialGraph.addNode(obstacleStart);
        specialGraph.addNode(tollEnd);
        specialGraph.addNode(restrictedMiddle);
        specialGraph.addEdge(1, 3, 10.0);
        specialGraph.addEdge(3, 2, 10.0);
        
        TestSearchAlgorithm specialAlgorithm = new TestSearchAlgorithm(
            specialGraph, obstacleStart, tollEnd, vehicle, trafficCondition, weatherCondition, 10
        );
        
        // 验证特殊节点被正确设置
        assertSame("障碍起始节点应该正确设置", obstacleStart, specialAlgorithm.getStartNode());
        assertTrue("起始节点应该是障碍", specialAlgorithm.getStartNode().isObstacle());
        
        assertSame("收费结束节点应该正确设置", tollEnd, specialAlgorithm.getEndNode());
        assertTrue("结束节点应该是收费道路", specialAlgorithm.getEndNode().isTollRoad());
        
        // 测试路径查找
        PathResult result = specialAlgorithm.findPath();
        assertNotNull("特殊节点路径结果不应该为null", result);
    }

    /**
     * 测试SearchAlgorithm与不同类型的车辆
     * 验证算法能处理各种类型的车辆
     */
    @Test
    public void testSearchAlgorithmWithDifferentVehicleTypes() {
        // 测试紧急车辆
        Vehicle emergencyVehicle = new Vehicle("Emergency", 2000.0, false, 60.0, 40.0, 0.8, 8.0, true);
        TestSearchAlgorithm emergencyAlgorithm = new TestSearchAlgorithm(
            graph, startNode, endNode, emergencyVehicle, trafficCondition, weatherCondition, 10
        );
        
        assertTrue("紧急车辆应该正确设置", emergencyAlgorithm.getVehicle().isEmergencyVehicle());
        
        // 测试重型车辆
        Vehicle heavyVehicle = new Vehicle("Heavy", 5000.0, true, 100.0, 50.0, 1.0, 10.0, false);
        TestSearchAlgorithm heavyAlgorithm = new TestSearchAlgorithm(
            graph, startNode, endNode, heavyVehicle, trafficCondition, weatherCondition, 10
        );
        
        assertTrue("重型车辆应该正确设置", heavyAlgorithm.getVehicle().requiresTollFreeRoute());
        assertEquals("重型车辆类型应该正确", "Heavy", heavyAlgorithm.getVehicle().getVehicleType());
        
        // 测试低燃料车辆
        Vehicle lowFuelVehicle = new Vehicle("Low Fuel", 1000.0, false, 40.0, 5.0, 1.0, 2.0, false);
        TestSearchAlgorithm lowFuelAlgorithm = new TestSearchAlgorithm(
            graph, startNode, endNode, lowFuelVehicle, trafficCondition, weatherCondition, 10
        );
        
        assertEquals("低燃料车辆当前燃料应该正确", 5.0, lowFuelAlgorithm.getVehicle().getCurrentFuel(), 0.001);
    }

    /**
     * 测试SearchAlgorithm与不同条件组合
     * 验证算法在各种条件下的行为
     */
    @Test
    public void testSearchAlgorithmWithDifferentConditions() {
        // 测试恶劣交通条件
        Map<Integer, String> badTrafficData = new HashMap<>();
        badTrafficData.put(1, "Congested");
        badTrafficData.put(2, "Closed");
        badTrafficData.put(3, "Accident");
        TrafficCondition badTraffic = new TrafficCondition(badTrafficData);
        
        TestSearchAlgorithm badTrafficAlgorithm = new TestSearchAlgorithm(
            graph, startNode, endNode, vehicle, badTraffic, weatherCondition, 10
        );
        
        assertSame("恶劣交通条件应该正确设置", badTraffic, badTrafficAlgorithm.getTrafficCondition());
        
        // 测试恶劣天气条件
        WeatherCondition badWeather = new WeatherCondition("Stormy");
        TestSearchAlgorithm badWeatherAlgorithm = new TestSearchAlgorithm(
            graph, startNode, endNode, vehicle, trafficCondition, badWeather, 10
        );
        
        assertSame("恶劣天气条件应该正确设置", badWeather, badWeatherAlgorithm.getWeatherCondition());
        
        // 测试组合条件
        TestSearchAlgorithm combinedAlgorithm = new TestSearchAlgorithm(
            graph, startNode, endNode, vehicle, badTraffic, badWeather, 10
        );
        
        assertSame("组合交通条件应该正确设置", badTraffic, combinedAlgorithm.getTrafficCondition());
        assertSame("组合天气条件应该正确设置", badWeather, combinedAlgorithm.getWeatherCondition());
    }

    /**
     * 测试SearchAlgorithm的复杂场景
     * 验证算法在复杂场景下的行为
     */
    @Test
    public void testSearchAlgorithmComplexScenarios() {
        // 创建复杂的图结构
        Graph complexGraph = new Graph();
        for (int i = 1; i <= 10; i++) {
            complexGraph.addNode(new Node(i, false, "Node" + i, false, false, false, 1.0, 0, 24));
        }
        
        // 添加复杂的连接
        for (int i = 1; i < 10; i++) {
            complexGraph.addEdge(i, i + 1, 10.0);
        }
        
        Node complexStart = complexGraph.getNode(1);
        Node complexEnd = complexGraph.getNode(10);
        
        TestSearchAlgorithm complexAlgorithm = new TestSearchAlgorithm(
            complexGraph, complexStart, complexEnd, vehicle, trafficCondition, weatherCondition, 15
        );
        
        // 验证复杂算法设置
        assertSame("复杂图应该正确设置", complexGraph, complexAlgorithm.getGraph());
        assertSame("复杂起始节点应该正确设置", complexStart, complexAlgorithm.getStartNode());
        assertSame("复杂结束节点应该正确设置", complexEnd, complexAlgorithm.getEndNode());
        assertEquals("复杂算法时间应该正确设置", 15, complexAlgorithm.getCurrentTime());
        
        // 测试路径查找
        PathResult complexResult = complexAlgorithm.findPath();
        assertNotNull("复杂路径结果不应该为null", complexResult);
    }

    /**
     * 测试用的SearchAlgorithm具体实现类
     * 用于测试抽象类的功能
     */
    private static class TestSearchAlgorithm extends SearchAlgorithm {
        
        public TestSearchAlgorithm(Graph graph, Node startNode, Node endNode, Vehicle vehicle,
                                 TrafficCondition trafficCondition, WeatherCondition weatherCondition, int currentTime) {
            super(graph, startNode, endNode, vehicle, trafficCondition, weatherCondition, currentTime);
        }
        
        @Override
        public PathResult findPath() {
            // 简单实现：返回包含起始和结束节点的路径
            if (graph == null || startNode == null || endNode == null) {
                return new PathResult(new java.util.ArrayList<>());
            }
            
            java.util.List<Node> path = new java.util.ArrayList<>();
            path.add(startNode);
            
            // 如果起始和结束节点不同，尝试找到一条简单路径
            if (!startNode.equals(endNode)) {
                // 检查是否有直接连接
                for (Edge edge : startNode.getNeighbors()) {
                    if (edge.getNeighbor().equals(endNode)) {
                        path.add(endNode);
                        return new PathResult(path);
                    }
                }
                
                // 如果没有直接连接，添加结束节点
                path.add(endNode);
            }
            
            return new PathResult(path);
        }
        
        // 提供getter方法用于测试
        public Graph getGraph() { return graph; }
        public Node getStartNode() { return startNode; }
        public Node getEndNode() { return endNode; }
        public Vehicle getVehicle() { return vehicle; }
        public TrafficCondition getTrafficCondition() { return trafficCondition; }
        public WeatherCondition getWeatherCondition() { return weatherCondition; }
        public int getCurrentTime() { return currentTime; }
    }
}