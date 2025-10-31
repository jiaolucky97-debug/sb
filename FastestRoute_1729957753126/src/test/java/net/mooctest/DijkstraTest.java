package net.mooctest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

/**
 * Dijkstra算法的测试类
 * 测试Dijkstra算法在各种场景下的路径查找功能
 */
public class DijkstraTest {
    
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
        
        // 创建加油站
        gasStations = new HashMap<>();
        gasStations.put(1, new GasStation(1, 1.5));
        gasStations.put(2, new GasStation(2, 1.8));
        gasStations.put(3, new GasStation(3, 2.0));
    }

    /**
     * 测试Dijkstra构造函数
     * 验证算法能正确初始化
     */
    @Test
    public void testDijkstraConstructor() {
        Dijkstra dijkstra = new Dijkstra(graph, startNode, endNode, vehicle, 
                                       trafficCondition, weatherCondition, 10, gasStations);
        
        assertNotNull("Dijkstra算法应该被正确创建", dijkstra);
    }

    /**
     * 测试Dijkstra基本路径查找
     * 验证能找到最短路径
     */
    @Test
    public void testDijkstraBasicPathFinding() {
        Dijkstra dijkstra = new Dijkstra(graph, startNode, endNode, vehicle, 
                                       trafficCondition, weatherCondition, 10, gasStations);
        
        PathResult result = dijkstra.findPath();
        
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
     * 测试Dijkstra直接路径
     * 验证当存在直接路径时的处理
     */
    @Test
    public void testDijkstraDirectPath() {
        // 添加直接路径
        graph.addEdge(1, 4, 5.0);
        
        Dijkstra dijkstra = new Dijkstra(graph, startNode, endNode, vehicle, 
                                       trafficCondition, weatherCondition, 10, gasStations);
        
        PathResult result = dijkstra.findPath();
        
        assertNotNull("直接路径结果不应该为null", result);
        assertEquals("直接路径长度应该为2", 2, result.getPath().size());
        assertEquals("直接路径应该以起始节点开始", startNode, result.getPath().get(0));
        assertEquals("直接路径应该以结束节点结束", endNode, result.getPath().get(1));
    }

    /**
     * 测试Dijkstra无路径情况
     * 验证当没有路径时的处理
     */
    @Test
    public void testDijkstraNoPath() {
        // 创建没有连接的图
        Graph noPathGraph = new Graph();
        Node isolatedStart = new Node(1, false, "Isolated Start", false, false, false, 1.0, 0, 24);
        Node isolatedEnd = new Node(2, false, "Isolated End", false, false, false, 1.0, 0, 24);
        
        noPathGraph.addNode(isolatedStart);
        noPathGraph.addNode(isolatedEnd);
        
        Dijkstra dijkstra = new Dijkstra(noPathGraph, isolatedStart, isolatedEnd, vehicle, 
                                       trafficCondition, weatherCondition, 10, gasStations);
        
        PathResult result = dijkstra.findPath();
        
        assertNull("无路径时应该返回null", result);
    }

    /**
     * 测试Dijkstra相同起始和结束节点
     * 验证当起始和结束节点相同时的处理
     */
    @Test
    public void testDijkstraSameStartEnd() {
        Dijkstra dijkstra = new Dijkstra(graph, startNode, startNode, vehicle, 
                                       trafficCondition, weatherCondition, 10, gasStations);
        
        PathResult result = dijkstra.findPath();
        
        assertNotNull("相同节点路径结果不应该为null", result);
        assertEquals("相同节点路径长度应该为1", 1, result.getPath().size());
        assertEquals("路径应该只包含起始节点", startNode, result.getPath().get(0));
    }

    /**
     * 测试Dijkstra与交通条件
     * 验证交通条件对路径查找的影响
     */
    @Test
    public void testDijkstraWithTrafficConditions() {
        // 创建拥堵的交通条件
        Map<Integer, String> congestedTrafficData = new HashMap<>();
        congestedTrafficData.put(1, "Clear");
        congestedTrafficData.put(2, "Congested");  // 权重翻倍
        congestedTrafficData.put(3, "Clear");
        congestedTrafficData.put(4, "Clear");
        TrafficCondition congestedTraffic = new TrafficCondition(congestedTrafficData);
        
        Dijkstra dijkstra = new Dijkstra(graph, startNode, endNode, vehicle, 
                                       congestedTraffic, weatherCondition, 10, gasStations);
        
        PathResult result = dijkstra.findPath();
        
        assertNotNull("拥堵交通路径结果不应该为null", result);
        
        // 由于节点2拥堵，路径应该选择 1->3->4 (15+5=20) 而不是 1->2->3->4 (10*2+2+5=27)
        assertEquals("拥堵时路径长度应该为3", 3, result.getPath().size());
        assertEquals("拥堵时第二个节点应该是节点3", 3, result.getPath().get(1).getNodeId());
    }

    /**
     * 测试Dijkstra与天气条件
     * 验证天气条件对路径查找的影响
     */
    @Test
    public void testDijkstraWithWeatherConditions() {
        // 创建雪天天气
        WeatherCondition snowyWeather = new WeatherCondition("Snowy");
        
        Dijkstra dijkstra = new Dijkstra(graph, startNode, endNode, vehicle, 
                                       trafficCondition, snowyWeather, 10, gasStations);
        
        PathResult result = dijkstra.findPath();
        
        assertNotNull("雪天路径结果不应该为null", result);
        // 雪天所有权重翻倍，但相对比例不变，仍应选择最短路径
        assertEquals("雪天路径长度应该为4", 4, result.getPath().size());
    }

    /**
     * 测试Dijkstra与紧急车辆
     * 验证紧急车辆的特殊处理
     */
    @Test
    public void testDijkstraWithEmergencyVehicle() {
        // 创建紧急车辆
        Vehicle emergencyVehicle = new Vehicle("Emergency", 2000.0, false, 60.0, 40.0, 0.8, 8.0, true);
        
        // 创建高风险区域
        Node highRiskNode = new Node(5, false, "High Risk", false, false, true, 3.0, 0, 24);
        graph.addNode(highRiskNode);
        graph.addEdge(1, 5, 8.0);
        graph.addEdge(5, 4, 8.0);
        
        Dijkstra dijkstra = new Dijkstra(graph, startNode, endNode, emergencyVehicle, 
                                       trafficCondition, weatherCondition, 10, gasStations);
        
        PathResult result = dijkstra.findPath();
        
        assertNotNull("紧急车辆路径结果不应该为null", result);
        // 紧急车辆可以通行高风险区域，可能选择更短的路径
        assertTrue("紧急车辆路径应该有效", result.getPath().size() >= 2);
    }

    /**
     * 测试Dijkstra与时间限制
     * 验证时间窗口对路径的影响
     */
    @Test
    public void testDijkstraWithTimeRestrictions() {
        // 创建有时间限制的节点
        Node timeRestrictedNode = new Node(5, false, "Time Restricted", false, false, false, 1.0, 9, 17);
        graph.addNode(timeRestrictedNode);
        graph.addEdge(1, 5, 5.0);
        graph.addEdge(5, 4, 5.0);
        
        // 在允许时间内（10点）
        Dijkstra dijkstraWithinTime = new Dijkstra(graph, startNode, endNode, vehicle, 
                                                  trafficCondition, weatherCondition, 10, gasStations);
        
        PathResult resultWithinTime = dijkstraWithinTime.findPath();
        assertNotNull("允许时间内路径结果不应该为null", resultWithinTime);
        
        // 在不允许时间内（8点）
        Dijkstra dijkstraOutsideTime = new Dijkstra(graph, startNode, endNode, vehicle, 
                                                   trafficCondition, weatherCondition, 8, gasStations);
        
        PathResult resultOutsideTime = dijkstraOutsideTime.findPath();
        assertNotNull("不允许时间内路径结果不应该为null", resultOutsideTime);
    }

    /**
     * 测试Dijkstra与燃料管理
     * 验证燃料不足和加油站的处理
     */
    @Test
    public void testDijkstraWithFuelManagement() {
        // 创建低燃料车辆
        Vehicle lowFuelVehicle = new Vehicle("Low Fuel", 1000.0, false, 40.0, 5.0, 1.0, 2.0, false);
        
        Dijkstra dijkstra = new Dijkstra(graph, startNode, endNode, lowFuelVehicle, 
                                       trafficCondition, weatherCondition, 10, gasStations);
        
        PathResult result = dijkstra.findPath();
        
        assertNotNull("低燃料车辆路径结果不应该为null", result);
        // 低燃料车辆需要考虑加油站，可能选择不同的路径
        assertTrue("低燃料车辆路径应该有效", result.getPath().size() >= 2);
    }

    /**
     * 测试Dijkstra与null参数
     * 验证null参数的处理
     */
    @Test
    public void testDijkstraWithNullParameters() {
        // 测试null加油站
        Dijkstra dijkstraNullGas = new Dijkstra(graph, startNode, endNode, vehicle, 
                                                trafficCondition, weatherCondition, 10, null);
        
        PathResult resultNullGas = dijkstraNullGas.findPath();
        assertNotNull("null加油站路径结果不应该为null", resultNullGas);
        
        // 测试空加油站
        Map<Integer, GasStation> emptyGasStations = new HashMap<>();
        Dijkstra dijkstraEmptyGas = new Dijkstra(graph, startNode, endNode, vehicle, 
                                                 trafficCondition, weatherCondition, 10, emptyGasStations);
        
        PathResult resultEmptyGas = dijkstraEmptyGas.findPath();
        assertNotNull("空加油站路径结果不应该为null", resultEmptyGas);
    }

    /**
     * 测试Dijkstra的reconstructPath方法
     * 验证路径重建功能
     */
    @Test
    public void testDijkstraReconstructPath() {
        Dijkstra dijkstra = new Dijkstra(graph, startNode, endNode, vehicle, 
                                       trafficCondition, weatherCondition, 10, gasStations);
        
        PathResult result = dijkstra.findPath();
        
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
     * 测试Dijkstra的复杂场景
     * 验证在复杂图结构中的表现
     */
    @Test
    public void testDijkstraComplexScenarios() {
        // 创建更复杂的图
        Graph complexGraph = new Graph();
        for (int i = 1; i <= 10; i++) {
            complexGraph.addNode(new Node(i, false, "Node" + i, false, false, false, 1.0, 0, 24));
        }
        
        // 添加复杂的连接
        complexGraph.addEdge(1, 2, 5.0);
        complexGraph.addEdge(1, 3, 10.0);
        complexGraph.addEdge(2, 4, 5.0);
        complexGraph.addEdge(3, 4, 2.0);
        complexGraph.addEdge(2, 5, 15.0);
        complexGraph.addEdge(4, 6, 5.0);
        complexGraph.addEdge(5, 6, 2.0);
        complexGraph.addEdge(6, 7, 3.0);
        complexGraph.addEdge(7, 8, 4.0);
        complexGraph.addEdge(8, 9, 2.0);
        complexGraph.addEdge(9, 10, 3.0);
        complexGraph.addEdge(6, 10, 20.0);
        
        Node complexStart = complexGraph.getNode(1);
        Node complexEnd = complexGraph.getNode(10);
        
        Dijkstra complexDijkstra = new Dijkstra(complexGraph, complexStart, complexEnd, vehicle, 
                                               trafficCondition, weatherCondition, 10, gasStations);
        
        PathResult complexResult = complexDijkstra.findPath();
        
        assertNotNull("复杂场景路径结果不应该为null", complexResult);
        assertNotNull("复杂场景路径不应该为null", complexResult.getPath());
        assertFalse("复杂场景路径不应该为空", complexResult.getPath().isEmpty());
        
        assertEquals("复杂场景路径应该以起始节点开始", complexStart, complexResult.getPath().get(0));
        assertEquals("复杂场景路径应该以结束节点结束", complexEnd, 
                    complexResult.getPath().get(complexResult.getPath().size() - 1));
    }
}