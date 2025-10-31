package net.mooctest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

/**
 * IterativeDeepeningSearch算法的测试类
 * 测试迭代深化搜索算法在各种场景下的路径查找功能
 */
public class IterativeDeepeningSearchTest {
    
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
        
        // 添加边：创建一个简单的树状结构
        graph.addEdge(1, 2, 10.0);
        graph.addEdge(1, 3, 15.0);
        graph.addEdge(2, 4, 10.0);
        graph.addEdge(3, 4, 5.0);
        
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
     * 测试IterativeDeepeningSearch构造函数
     * 验证算法能正确初始化
     */
    @Test
    public void testIterativeDeepeningSearchConstructor() {
        IterativeDeepeningSearch ids = new IterativeDeepeningSearch(graph, startNode, endNode, vehicle, 
                                                                  trafficCondition, weatherCondition, 10, 5);
        
        assertNotNull("IDS算法应该被正确创建", ids);
    }

    /**
     * 测试IterativeDeepeningSearch基本路径查找
     * 验证能找到路径
     */
    @Test
    public void testIterativeDeepeningSearchBasicPathFinding() {
        IterativeDeepeningSearch ids = new IterativeDeepeningSearch(graph, startNode, endNode, vehicle, 
                                                                  trafficCondition, weatherCondition, 10, 5);
        
        PathResult result = ids.findPath();
        
        assertNotNull("路径结果不应该为null", result);
        assertNotNull("路径不应该为null", result.getPath());
        assertFalse("路径不应该为空", result.getPath().isEmpty());
        
        // 验证路径包含起始和结束节点
        assertEquals("路径应该以起始节点开始", startNode, result.getPath().get(0));
        assertEquals("路径应该以结束节点结束", endNode, result.getPath().get(result.getPath().size() - 1));
        
        // 验证路径长度合理
        assertTrue("路径长度应该合理", result.getPath().size() >= 2 && result.getPath().size() <= 6);
    }

    /**
     * 测试IterativeDeepeningSearch直接路径
     * 验证当存在直接路径时的处理
     */
    @Test
    public void testIterativeDeepeningSearchDirectPath() {
        // 添加直接路径
        graph.addEdge(1, 4, 5.0);
        
        IterativeDeepeningSearch ids = new IterativeDeepeningSearch(graph, startNode, endNode, vehicle, 
                                                                  trafficCondition, weatherCondition, 10, 3);
        
        PathResult result = ids.findPath();
        
        assertNotNull("直接路径结果不应该为null", result);
        assertEquals("直接路径长度应该为2", 2, result.getPath().size());
        assertEquals("直接路径应该以起始节点开始", startNode, result.getPath().get(0));
        assertEquals("直接路径应该以结束节点结束", endNode, result.getPath().get(1));
    }

    /**
     * 测试IterativeDeepeningSearch无路径情况
     * 验证当没有路径时的处理
     */
    @Test
    public void testIterativeDeepeningSearchNoPath() {
        // 创建没有连接的图
        Graph noPathGraph = new Graph();
        Node isolatedStart = new Node(1, false, "Isolated Start", false, false, false, 1.0, 0, 24);
        Node isolatedEnd = new Node(2, false, "Isolated End", false, false, false, 1.0, 0, 24);
        
        noPathGraph.addNode(isolatedStart);
        noPathGraph.addNode(isolatedEnd);
        
        IterativeDeepeningSearch ids = new IterativeDeepeningSearch(noPathGraph, isolatedStart, isolatedEnd, vehicle, 
                                                                  trafficCondition, weatherCondition, 10, 5);
        
        PathResult result = ids.findPath();
        
        assertNull("无路径时应该返回null", result);
    }

    /**
     * 测试IterativeDeepeningSearch相同起始和结束节点
     * 验证当起始和结束节点相同时的处理
     */
    @Test
    public void testIterativeDeepeningSearchSameStartEnd() {
        IterativeDeepeningSearch ids = new IterativeDeepeningSearch(graph, startNode, startNode, vehicle, 
                                                                  trafficCondition, weatherCondition, 10, 5);
        
        PathResult result = ids.findPath();
        
        assertNotNull("相同节点路径结果不应该为null", result);
        assertEquals("相同节点路径长度应该为1", 1, result.getPath().size());
        assertEquals("路径应该只包含起始节点", startNode, result.getPath().get(0));
    }

    /**
     * 测试IterativeDeepeningSearch深度限制
     * 验证深度限制对搜索的影响
     */
    @Test
    public void testIterativeDeepeningSearchDepthLimit() {
        // 测试深度限制为0
        IterativeDeepeningSearch idsDepth0 = new IterativeDeepeningSearch(graph, startNode, endNode, vehicle, 
                                                                        trafficCondition, weatherCondition, 10, 0);
        
        PathResult resultDepth0 = idsDepth0.findPath();
        assertNull("深度为0时应该找不到路径", resultDepth0);
        
        // 测试深度限制为1
        IterativeDeepeningSearch idsDepth1 = new IterativeDeepeningSearch(graph, startNode, endNode, vehicle, 
                                                                        trafficCondition, weatherCondition, 10, 1);
        
        PathResult resultDepth1 = idsDepth1.findPath();
        assertNull("深度为1时应该找不到路径", resultDepth1);
        
        // 测试深度限制为2
        IterativeDeepeningSearch idsDepth2 = new IterativeDeepeningSearch(graph, startNode, endNode, vehicle, 
                                                                        trafficCondition, weatherCondition, 10, 2);
        
        PathResult resultDepth2 = idsDepth2.findPath();
        
        // 添加直接路径
        graph.addEdge(1, 4, 5.0);
        
        IterativeDeepeningSearch idsDepth2WithDirect = new IterativeDeepeningSearch(graph, startNode, endNode, vehicle, 
                                                                                 trafficCondition, weatherCondition, 10, 2);
        
        PathResult resultDepth2WithDirect = idsDepth2WithDirect.findPath();
        
        assertNotNull("深度为2且有直接路径时应该找到路径", resultDepth2WithDirect);
        assertEquals("深度为2时路径长度应该为2", 2, resultDepth2WithDirect.getPath().size());
    }

    /**
     * 测试IterativeDeepeningSearch与交通条件
     * 验证交通条件对路径查找的影响
     */
    @Test
    public void testIterativeDeepeningSearchWithTrafficConditions() {
        // 创建拥堵的交通条件
        Map<Integer, String> congestedTrafficData = new HashMap<>();
        congestedTrafficData.put(1, "Clear");
        congestedTrafficData.put(2, "Congested");  // 权重翻倍
        congestedTrafficData.put(3, "Clear");
        congestedTrafficData.put(4, "Clear");
        TrafficCondition congestedTraffic = new TrafficCondition(congestedTrafficData);
        
        IterativeDeepeningSearch ids = new IterativeDeepeningSearch(graph, startNode, endNode, vehicle, 
                                                                  congestedTraffic, weatherCondition, 10, 5);
        
        PathResult result = ids.findPath();
        
        assertNotNull("拥堵交通路径结果不应该为null", result);
        
        // 验证路径包含起始和结束节点
        assertEquals("路径应该以起始节点开始", startNode, result.getPath().get(0));
        assertEquals("路径应该以结束节点结束", endNode, result.getPath().get(result.getPath().size() - 1));
    }

    /**
     * 测试IterativeDeepeningSearch与天气条件
     * 验证天气条件对路径查找的影响
     */
    @Test
    public void testIterativeDeepeningSearchWithWeatherConditions() {
        // 创建雪天天气
        WeatherCondition snowyWeather = new WeatherCondition("Snowy");
        
        IterativeDeepeningSearch ids = new IterativeDeepeningSearch(graph, startNode, endNode, vehicle, 
                                                                  trafficCondition, snowyWeather, 10, 5);
        
        PathResult result = ids.findPath();
        
        assertNotNull("雪天路径结果不应该为null", result);
        
        // 验证路径包含起始和结束节点
        assertEquals("路径应该以起始节点开始", startNode, result.getPath().get(0));
        assertEquals("路径应该以结束节点结束", endNode, result.getPath().get(result.getPath().size() - 1));
    }

    /**
     * 测试IterativeDeepeningSearch与时间限制
     * 验证时间窗口对路径的影响
     */
    @Test
    public void testIterativeDeepeningSearchWithTimeRestrictions() {
        // 创建有时间限制的节点
        Node timeRestrictedNode = new Node(5, false, "Time Restricted", false, false, false, 1.0, 9, 17);
        graph.addNode(timeRestrictedNode);
        graph.addEdge(1, 5, 5.0);
        graph.addEdge(5, 4, 5.0);
        
        // 在允许时间内（10点）
        IterativeDeepeningSearch idsWithinTime = new IterativeDeepeningSearch(graph, startNode, endNode, vehicle, 
                                                                            trafficCondition, weatherCondition, 10, 5);
        
        PathResult resultWithinTime = idsWithinTime.findPath();
        assertNotNull("允许时间内路径结果不应该为null", resultWithinTime);
        
        // 在不允许时间内（8点）
        IterativeDeepeningSearch idsOutsideTime = new IterativeDeepeningSearch(graph, startNode, endNode, vehicle, 
                                                                             trafficCondition, weatherCondition, 8, 5);
        
        PathResult resultOutsideTime = idsOutsideTime.findPath();
        assertNotNull("不允许时间内路径结果不应该为null", resultOutsideTime);
    }

    /**
     * 测试IterativeDeepeningSearch与紧急车辆
     * 验证紧急车辆的特殊处理
     */
    @Test
    public void testIterativeDeepeningSearchWithEmergencyVehicle() {
        // 创建紧急车辆
        Vehicle emergencyVehicle = new Vehicle("Emergency", 2000.0, false, 60.0, 40.0, 0.8, 8.0, true);
        
        // 创建高风险区域
        Node highRiskNode = new Node(6, false, "High Risk", false, false, true, 3.0, 0, 24);
        graph.addNode(highRiskNode);
        graph.addEdge(1, 6, 8.0);
        graph.addEdge(6, 4, 8.0);
        
        IterativeDeepeningSearch ids = new IterativeDeepeningSearch(graph, startNode, endNode, emergencyVehicle, 
                                                                  trafficCondition, weatherCondition, 10, 5);
        
        PathResult result = ids.findPath();
        
        assertNotNull("紧急车辆路径结果不应该为null", result);
        // 紧急车辆可以通行高风险区域，可能选择更短的路径
        assertTrue("紧急车辆路径应该有效", result.getPath().size() >= 2);
    }

    /**
     * 测试IterativeDeepeningSearch与复杂图
     * 验证在复杂图结构中的表现
     */
    @Test
    public void testIterativeDeepeningSearchComplexGraph() {
        // 创建更复杂的图
        Graph complexGraph = new Graph();
        for (int i = 1; i <= 8; i++) {
            complexGraph.addNode(new Node(i, false, "Node" + i, false, false, false, 1.0, 0, 24));
        }
        
        // 添加复杂的连接，创建一个较深的树
        complexGraph.addEdge(1, 2, 5.0);
        complexGraph.addEdge(1, 3, 8.0);
        complexGraph.addEdge(2, 4, 6.0);
        complexGraph.addEdge(2, 5, 10.0);
        complexGraph.addEdge(3, 6, 7.0);
        complexGraph.addEdge(4, 7, 8.0);
        complexGraph.addEdge(5, 7, 4.0);
        complexGraph.addEdge(6, 8, 5.0);
        complexGraph.addEdge(7, 8, 3.0);
        
        Node complexStart = complexGraph.getNode(1);
        Node complexEnd = complexGraph.getNode(8);
        
        IterativeDeepeningSearch complexIds = new IterativeDeepeningSearch(complexGraph, complexStart, complexEnd, vehicle, 
                                                                         trafficCondition, weatherCondition, 10, 7);
        
        PathResult complexResult = complexIds.findPath();
        
        assertNotNull("复杂图路径结果不应该为null", complexResult);
        if (complexResult != null) {
            assertNotNull("复杂图路径不应该为null", complexResult.getPath());
            assertFalse("复杂图路径不应该为空", complexResult.getPath().isEmpty());
            
            assertEquals("复杂图路径应该以起始节点开始", complexStart, complexResult.getPath().get(0));
            assertEquals("复杂图路径应该以结束节点结束", complexEnd, 
                        complexResult.getPath().get(complexResult.getPath().size() - 1));
        }
    }

    /**
     * 测试IterativeDeepeningSearch的depthLimitedSearch方法
     * 验证深度限制搜索的功能
     */
    @Test
    public void testIterativeDeepeningSearchDepthLimitedSearch() {
        IterativeDeepeningSearch ids = new IterativeDeepeningSearch(graph, startNode, endNode, vehicle, 
                                                                  trafficCondition, weatherCondition, 10, 5);
        
        PathResult result = ids.findPath();
        
        assertNotNull("深度限制搜索结果不应该为null", result);
        
        // 验证路径长度不超过深度限制+1（因为起始节点也算一层）
        if (result != null) {
            assertTrue("路径长度不应该超过深度限制", result.getPath().size() <= 6);
        }
    }

    /**
     * 测试IterativeDeepeningSearch的边界情况
     * 验证各种边界条件的处理
     */
    @Test
    public void testIterativeDeepeningSearchBoundaryConditions() {
        // 测试单节点图
        Graph singleNodeGraph = new Graph();
        Node singleNode = new Node(1, false, "Single", false, false, false, 1.0, 0, 24);
        singleNodeGraph.addNode(singleNode);
        
        IterativeDeepeningSearch singleNodeIds = new IterativeDeepeningSearch(singleNodeGraph, singleNode, singleNode, vehicle, 
                                                                          trafficCondition, weatherCondition, 10, 5);
        
        PathResult singleNodeResult = singleNodeIds.findPath();
        assertNotNull("单节点图路径结果不应该为null", singleNodeResult);
        assertEquals("单节点图路径长度应该为1", 1, singleNodeResult.getPath().size());
        
        // 测试两节点图
        Graph twoNodeGraph = new Graph();
        Node nodeA = new Node(1, false, "A", false, false, false, 1.0, 0, 24);
        Node nodeB = new Node(2, false, "B", false, false, false, 1.0, 0, 24);
        twoNodeGraph.addNode(nodeA);
        twoNodeGraph.addNode(nodeB);
        twoNodeGraph.addEdge(1, 2, 10.0);
        
        IterativeDeepeningSearch twoNodeIds = new IterativeDeepeningSearch(twoNodeGraph, nodeA, nodeB, vehicle, 
                                                                       trafficCondition, weatherCondition, 10, 5);
        
        PathResult twoNodeResult = twoNodeIds.findPath();
        assertNotNull("两节点图路径结果不应该为null", twoNodeResult);
        assertEquals("两节点图路径长度应该为2", 2, twoNodeResult.getPath().size());
        
        // 测试极大深度限制
        IterativeDeepeningSearch maxDepthIds = new IterativeDeepeningSearch(graph, startNode, endNode, vehicle, 
                                                                         trafficCondition, weatherCondition, 10, Integer.MAX_VALUE);
        
        PathResult maxDepthResult = maxDepthIds.findPath();
        assertNotNull("极大深度限制路径结果不应该为null", maxDepthResult);
        
        // 测试负深度限制
        IterativeDeepeningSearch negativeDepthIds = new IterativeDeepeningSearch(graph, startNode, endNode, vehicle, 
                                                                               trafficCondition, weatherCondition, 10, -1);
        
        PathResult negativeDepthResult = negativeDepthIds.findPath();
        assertNull("负深度限制应该找不到路径", negativeDepthResult);
    }

    /**
     * 测试IterativeDeepeningSearch的多次调用
     * 验证多次调用的结果一致性
     */
    @Test
    public void testIterativeDeepeningSearchMultipleCalls() {
        IterativeDeepeningSearch ids = new IterativeDeepeningSearch(graph, startNode, endNode, vehicle, 
                                                                  trafficCondition, weatherCondition, 10, 5);
        
        // 多次调用findPath
        PathResult result1 = ids.findPath();
        PathResult result2 = ids.findPath();
        PathResult result3 = ids.findPath();
        
        assertNotNull("第一次调用结果不应该为null", result1);
        assertNotNull("第二次调用结果不应该为null", result2);
        assertNotNull("第三次调用结果不应该为null", result3);
        
        // 验证结果的一致性
        assertEquals("多次调用路径长度应该相同", result1.getPath().size(), result2.getPath().size());
        assertEquals("多次调用路径长度应该相同", result2.getPath().size(), result3.getPath().size());
        
        // 验证路径内容的一致性
        for (int i = 0; i < result1.getPath().size(); i++) {
            assertEquals("多次调用路径内容应该相同", result1.getPath().get(i), result2.getPath().get(i));
            assertEquals("多次调用路径内容应该相同", result2.getPath().get(i), result3.getPath().get(i));
        }
    }

    /**
     * 测试IterativeDeepeningSearch与环状图
     * 验证对环状图的处理
     */
    @Test
    public void testIterativeDeepeningSearchWithCycles() {
        // 创建包含环的图
        Graph cyclicGraph = new Graph();
        Node nodeA = new Node(1, false, "A", false, false, false, 1.0, 0, 24);
        Node nodeB = new Node(2, false, "B", false, false, false, 1.0, 0, 24);
        Node nodeC = new Node(3, false, "C", false, false, false, 1.0, 0, 24);
        Node nodeD = new Node(4, false, "D", false, false, false, 1.0, 0, 24);
        
        cyclicGraph.addNode(nodeA);
        cyclicGraph.addNode(nodeB);
        cyclicGraph.addNode(nodeC);
        cyclicGraph.addNode(nodeD);
        
        // 创建环：A->B->C->A 和 C->D
        cyclicGraph.addEdge(1, 2, 5.0);
        cyclicGraph.addEdge(2, 3, 5.0);
        cyclicGraph.addEdge(3, 1, 5.0);  // 形成环
        cyclicGraph.addEdge(3, 4, 5.0);
        
        IterativeDeepeningSearch cyclicIds = new IterativeDeepeningSearch(cyclicGraph, nodeA, nodeD, vehicle, 
                                                                        trafficCondition, weatherCondition, 10, 5);
        
        PathResult cyclicResult = cyclicIds.findPath();
        
        assertNotNull("环状图路径结果不应该为null", cyclicResult);
        if (cyclicResult != null) {
            assertNotNull("环状图路径不应该为null", cyclicResult.getPath());
            assertFalse("环状图路径不应该为空", cyclicResult.getPath().isEmpty());
            
            assertEquals("环状图路径应该以起始节点开始", nodeA, cyclicResult.getPath().get(0));
            assertEquals("环状图路径应该以结束节点结束", nodeD, 
                        cyclicResult.getPath().get(cyclicResult.getPath().size() - 1));
            
            // 验证路径中没有重复节点（visited set应该防止无限循环）
            for (int i = 0; i < cyclicResult.getPath().size(); i++) {
                for (int j = i + 1; j < cyclicResult.getPath().size(); j++) {
                    assertNotEquals("路径中不应该有重复节点", cyclicResult.getPath().get(i), cyclicResult.getPath().get(j));
                }
            }
        }
    }
}