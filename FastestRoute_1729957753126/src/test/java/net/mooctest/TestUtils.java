package net.mooctest;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试工具类
 * 提供通用的测试辅助功能，包括数据创建、验证等
 */
public class TestUtils {
    
    /**
     * 创建标准测试车辆
     * @return 标准车辆实例
     */
    public static Vehicle createStandardVehicle() {
        return new Vehicle("Standard Vehicle", 1000.0, false, 50.0, 25.0, 0.5, 5.0, false);
    }
    
    /**
     * 创建紧急车辆
     * @return 紧急车辆实例
     */
    public static Vehicle createEmergencyVehicle() {
        return new Vehicle("Emergency Vehicle", 2000.0, false, 60.0, 40.0, 0.8, 8.0, true);
    }
    
    /**
     * 创建重型车辆
     * @return 重型车辆实例
     */
    public static Vehicle createHeavyVehicle() {
        return new Vehicle("Heavy Vehicle", 5000.0, true, 100.0, 50.0, 1.0, 10.0, false);
    }
    
    /**
     * 创建低燃料车辆
     * @return 低燃料车辆实例
     */
    public static Vehicle createLowFuelVehicle() {
        return new Vehicle("Low Fuel Vehicle", 1000.0, false, 40.0, 5.0, 1.0, 2.0, false);
    }
    
    /**
     * 创建标准交通条件
     * @return 标准交通条件实例
     */
    public static TrafficCondition createStandardTrafficCondition() {
        Map<Integer, String> trafficData = new HashMap<>();
        trafficData.put(1, "Clear");
        trafficData.put(2, "Clear");
        trafficData.put(3, "Clear");
        trafficData.put(4, "Clear");
        trafficData.put(5, "Clear");
        return new TrafficCondition(trafficData);
    }
    
    /**
     * 创建拥堵交通条件
     * @return 拥堵交通条件实例
     */
    public static TrafficCondition createCongestedTrafficCondition() {
        Map<Integer, String> trafficData = new HashMap<>();
        trafficData.put(1, "Clear");
        trafficData.put(2, "Congested");
        trafficData.put(3, "Accident");
        trafficData.put(4, "Closed");
        trafficData.put(5, "Clear");
        return new TrafficCondition(trafficData);
    }
    
    /**
     * 创建晴朗天气条件
     * @return 晴朗天气条件实例
     */
    public static WeatherCondition createClearWeatherCondition() {
        return new WeatherCondition("Clear");
    }
    
    /**
     * 创建恶劣天气条件
     * @return 恶劣天气条件实例
     */
    public static WeatherCondition createBadWeatherCondition() {
        return new WeatherCondition("Stormy");
    }
    
    /**
     * 创建简单测试图
     * @return 简单图实例，包含5个节点
     */
    public static Graph createSimpleGraph() {
        Graph graph = new Graph();
        
        // 创建节点
        Node node1 = new Node(1, false, "Start", false, false, false, 1.0, 0, 24);
        Node node2 = new Node(2, false, "Middle1", false, false, false, 1.0, 0, 24);
        Node node3 = new Node(3, false, "Middle2", false, false, false, 1.0, 0, 24);
        Node node4 = new Node(4, false, "Middle3", false, false, false, 1.0, 0, 24);
        Node node5 = new Node(5, false, "End", false, false, false, 1.0, 0, 24);
        
        // 添加节点到图
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
        graph.addEdge(4, 5, 10.0);
        graph.addEdge(2, 5, 25.0);
        
        return graph;
    }
    
    /**
     * 创建复杂测试图
     * @return 复杂图实例，包含10个节点
     */
    public static Graph createComplexGraph() {
        Graph graph = new Graph();
        
        // 创建节点
        for (int i = 1; i <= 10; i++) {
            Node node = new Node(i, false, "Node" + i, false, false, false, 1.0, 0, 24);
            graph.addNode(node);
        }
        
        // 添加复杂的连接
        graph.addEdge(1, 2, 5.0);
        graph.addEdge(1, 3, 10.0);
        graph.addEdge(2, 4, 6.0);
        graph.addEdge(3, 4, 3.0);
        graph.addEdge(2, 5, 15.0);
        graph.addEdge(4, 6, 4.0);
        graph.addEdge(5, 6, 2.0);
        graph.addEdge(4, 7, 8.0);
        graph.addEdge(6, 8, 5.0);
        graph.addEdge(7, 8, 3.0);
        graph.addEdge(6, 9, 7.0);
        graph.addEdge(8, 10, 4.0);
        graph.addEdge(9, 10, 6.0);
        
        return graph;
    }
    
    /**
     * 创建加油站映射
     * @return 加油站映射实例
     */
    public static Map<Integer, GasStation> createGasStations() {
        Map<Integer, GasStation> gasStations = new HashMap<>();
        gasStations.put(1, new GasStation(1, 1.5));
        gasStations.put(2, new GasStation(2, 1.8));
        gasStations.put(3, new GasStation(3, 2.0));
        gasStations.put(4, new GasStation(4, 1.6));
        gasStations.put(5, new GasStation(5, 1.9));
        return gasStations;
    }
    
    /**
     * 验证路径的有效性
     * @param result 路径结果
     * @param startNode 起始节点
     * @param endNode 结束节点
     * @return 路径是否有效
     */
    public static boolean isValidPath(PathResult result, Node startNode, Node endNode) {
        if (result == null || result.getPath() == null || result.getPath().isEmpty()) {
            return false;
        }
        
        // 检查起始和结束节点
        if (!startNode.equals(result.getPath().get(0)) || 
            !endNode.equals(result.getPath().get(result.getPath().size() - 1))) {
            return false;
        }
        
        // 检查路径连续性
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
            
            if (!hasConnection) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 创建特殊类型的节点
     * @param nodeId 节点ID
     * @param nodeType 节点类型
     * @return 特殊节点实例
     */
    public static Node createSpecialNode(int nodeId, String nodeType) {
        switch (nodeType.toLowerCase()) {
            case "obstacle":
                return new Node(nodeId, true, "Obstacle", false, false, false, 0.0, 0, 24);
            case "toll":
                return new Node(nodeId, false, "Toll Road", true, false, false, 2.0, 0, 24);
            case "restricted":
                return new Node(nodeId, false, "Restricted", false, true, false, 1.5, 0, 24);
            case "highrisk":
                return new Node(nodeId, false, "High Risk", false, false, true, 3.0, 0, 24);
            case "timerestricted":
                return new Node(nodeId, false, "Time Restricted", false, false, false, 1.0, 9, 17);
            default:
                return new Node(nodeId, false, "Regular", false, false, false, 1.0, 0, 24);
        }
    }
    
    /**
     * 比较两个浮点数是否相等（考虑精度）
     * @param expected 期望值
     * @param actual 实际值
     * @param delta 允许的误差
     * @return 是否相等
     */
    public static boolean doubleEquals(double expected, double actual, double delta) {
        return Math.abs(expected - actual) <= delta;
    }
    
    /**
     * 创建包含环的图
     * @return 包含环的图实例
     */
    public static Graph createCyclicGraph() {
        Graph graph = new Graph();
        
        // 创建节点
        Node node1 = new Node(1, false, "A", false, false, false, 1.0, 0, 24);
        Node node2 = new Node(2, false, "B", false, false, false, 1.0, 0, 24);
        Node node3 = new Node(3, false, "C", false, false, false, 1.0, 0, 24);
        Node node4 = new Node(4, false, "D", false, false, false, 1.0, 0, 24);
        
        // 添加节点
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);
        
        // 创建环：1->2->3->1 和 3->4
        graph.addEdge(1, 2, 5.0);
        graph.addEdge(2, 3, 5.0);
        graph.addEdge(3, 1, 5.0);  // 形成环
        graph.addEdge(3, 4, 5.0);
        
        return graph;
    }
}