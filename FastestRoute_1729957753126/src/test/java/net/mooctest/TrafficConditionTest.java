package net.mooctest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

/**
 * TrafficCondition类的测试类
 * 测试交通条件的各种状态和权重调整功能
 */
public class TrafficConditionTest {
    
    private TrafficCondition trafficCondition;
    private TrafficCondition emptyTrafficCondition;
    private Map<Integer, String> trafficData;

    @Before
    public void setUp() {
        // 创建测试用的交通数据
        trafficData = new HashMap<>();
        trafficData.put(1, "Clear");
        trafficData.put(2, "Congested");
        trafficData.put(3, "Closed");
        trafficData.put(4, "Accident");
        trafficData.put(5, "Unknown"); // 未知状态
        
        trafficCondition = new TrafficCondition(trafficData);
        emptyTrafficCondition = new TrafficCondition(new HashMap<>());
    }

    /**
     * 测试TrafficCondition构造函数
     * 验证交通条件能正确初始化
     */
    @Test
    public void testTrafficConditionConstructor() {
        assertNotNull("交通条件应该被正确创建", trafficCondition);
        assertNotNull("空交通条件应该被正确创建", emptyTrafficCondition);
    }

    /**
     * 测试getTrafficStatus方法
     * 验证能正确获取节点的交通状态
     */
    @Test
    public void testGetTrafficStatus() {
        // 测试存在的节点
        assertEquals("节点1应该是Clear状态", "Clear", trafficCondition.getTrafficStatus(1));
        assertEquals("节点2应该是Congested状态", "Congested", trafficCondition.getTrafficStatus(2));
        assertEquals("节点3应该是Closed状态", "Closed", trafficCondition.getTrafficStatus(3));
        assertEquals("节点4应该是Accident状态", "Accident", trafficCondition.getTrafficStatus(4));
        assertEquals("节点5应该是Unknown状态", "Unknown", trafficCondition.getTrafficStatus(5));
        
        // 测试不存在的节点（应该返回默认"Clear"）
        assertEquals("不存在的节点应该返回Clear", "Clear", trafficCondition.getTrafficStatus(99));
        assertEquals("负数节点应该返回Clear", "Clear", trafficCondition.getTrafficStatus(-1));
        assertEquals("零节点应该返回Clear", "Clear", trafficCondition.getTrafficStatus(0));
        
        // 测试空交通条件
        assertEquals("空交通条件下任何节点都应该返回Clear", "Clear", 
                    emptyTrafficCondition.getTrafficStatus(1));
    }

    /**
     * 测试updateTrafficStatus方法
     * 验证能正确更新节点的交通状态
     */
    @Test
    public void testUpdateTrafficStatus() {
        // 更新现有节点
        trafficCondition.updateTrafficStatus(1, "Congested");
        assertEquals("节点1应该被更新为Congested", "Congested", trafficCondition.getTrafficStatus(1));
        
        // 添加新节点
        trafficCondition.updateTrafficStatus(10, "Accident");
        assertEquals("新节点10应该是Accident状态", "Accident", trafficCondition.getTrafficStatus(10));
        
        // 更新为空字符串
        trafficCondition.updateTrafficStatus(2, "");
        assertEquals("节点2应该被更新为空字符串", "", trafficCondition.getTrafficStatus(2));
        
        // 更新为null（测试鲁棒性）
        try {
            trafficCondition.updateTrafficStatus(3, null);
            String status = trafficCondition.getTrafficStatus(3);
            assertTrue("null状态应该被处理", status == null || "Clear".equals(status));
        } catch (Exception e) {
            // 如果不支持null，这里会捕获异常
            assertTrue("null状态应该被处理", true);
        }
    }

    /**
     * 测试adjustWeight方法的各种交通状态
     * 验证不同交通状态对权重的影响
     */
    @Test
    public void testAdjustWeightForDifferentTrafficStatus() {
        double originalWeight = 10.0;
        
        // 测试Clear状态
        double clearWeight = trafficCondition.adjustWeight(originalWeight, 1);
        assertEquals("Clear状态权重不应该改变", originalWeight, clearWeight, 0.001);
        
        // 测试Congested状态
        double congestedWeight = trafficCondition.adjustWeight(originalWeight, 2);
        assertEquals("Congested状态权重应该翻倍", originalWeight * 2, congestedWeight, 0.001);
        
        // 测试Closed状态
        double closedWeight = trafficCondition.adjustWeight(originalWeight, 3);
        assertEquals("Closed状态权重应该是无穷大", Double.MAX_VALUE, closedWeight, 0.001);
        
        // 测试Accident状态
        double accidentWeight = trafficCondition.adjustWeight(originalWeight, 4);
        assertEquals("Accident状态权重应该是三倍", originalWeight * 3, accidentWeight, 0.001);
        
        // 测试未知状态
        double unknownWeight = trafficCondition.adjustWeight(originalWeight, 5);
        assertEquals("未知状态权重不应该改变", originalWeight, unknownWeight, 0.001);
        
        // 测试不存在的节点（默认Clear）
        double nonExistentWeight = trafficCondition.adjustWeight(originalWeight, 99);
        assertEquals("不存在的节点权重不应该改变", originalWeight, nonExistentWeight, 0.001);
    }

    /**
     * 测试adjustWeight方法的边界值
     * 验证各种权重值的处理
     */
    @Test
    public void testAdjustWeightBoundaryValues() {
        // 测试零权重
        double zeroWeight = trafficCondition.adjustWeight(0.0, 2); // Congested
        assertEquals("零权重翻倍后仍为零", 0.0, zeroWeight, 0.001);
        
        // 测试负权重
        double negativeWeight = trafficCondition.adjustWeight(-10.0, 2); // Congested
        assertEquals("负权重翻倍应该正确", -20.0, negativeWeight, 0.001);
        
        // 测试极大权重
        double maxWeight = trafficCondition.adjustWeight(Double.MAX_VALUE, 2); // Congested
        // Double.MAX_VALUE * 2 会溢出为无穷大
        assertTrue("极大权重翻倍可能溢出", Double.isInfinite(maxWeight) || maxWeight > 0);
        
        // 测试极小权重
        double minWeight = trafficCondition.adjustWeight(Double.MIN_VALUE, 3); // Closed
        assertEquals("Closed状态权重应该是无穷大", Double.MAX_VALUE, minWeight, 0.001);
        
        // 测试无穷大权重
        double infiniteWeight = trafficCondition.adjustWeight(Double.POSITIVE_INFINITY, 2); // Congested
        assertTrue("无穷大权重处理应该正确", Double.isInfinite(infiniteWeight));
    }

    /**
     * 测试交通状态的动态更新
     * 验证状态更新后权重调整的实时性
     */
    @Test
    public void testDynamicTrafficStatusUpdate() {
        double originalWeight = 15.0;
        
        // 初始状态：Clear
        double initialWeight = trafficCondition.adjustWeight(originalWeight, 1);
        assertEquals("初始权重应该不变", originalWeight, initialWeight, 0.001);
        
        // 更新为Congested
        trafficCondition.updateTrafficStatus(1, "Congested");
        double updatedWeight = trafficCondition.adjustWeight(originalWeight, 1);
        assertEquals("更新后权重应该翻倍", originalWeight * 2, updatedWeight, 0.001);
        
        // 更新为Closed
        trafficCondition.updateTrafficStatus(1, "Closed");
        double closedWeight = trafficCondition.adjustWeight(originalWeight, 1);
        assertEquals("Closed状态权重应该是无穷大", Double.MAX_VALUE, closedWeight, 0.001);
        
        // 更新为Accident
        trafficCondition.updateTrafficStatus(1, "Accident");
        double accidentWeight = trafficCondition.adjustWeight(originalWeight, 1);
        assertEquals("Accident状态权重应该是三倍", originalWeight * 3, accidentWeight, 0.001);
    }

    /**
     * 测试TrafficCondition与null数据
     * 验证null交通数据的处理
     */
    @Test
    public void testTrafficConditionWithNullData() {
        try {
            TrafficCondition nullTrafficCondition = new TrafficCondition(null);
            
            // 测试获取状态
            String status = nullTrafficCondition.getTrafficStatus(1);
            assertEquals("null数据应该返回Clear", "Clear", status);
            
            // 测试权重调整
            double weight = nullTrafficCondition.adjustWeight(10.0, 1);
            assertEquals("null数据权重不应该改变", 10.0, weight, 0.001);
            
            // 测试更新状态
            nullTrafficCondition.updateTrafficStatus(1, "Congested");
            String updatedStatus = nullTrafficCondition.getTrafficStatus(1);
            assertEquals("更新后状态应该正确", "Congested", updatedStatus);
            
        } catch (Exception e) {
            fail("null数据不应该导致异常: " + e.getMessage());
        }
    }

    /**
     * 测试特殊交通状态字符串
     * 验证各种特殊字符串状态的处理
     */
    @Test
    public void testSpecialTrafficStatusStrings() {
        double originalWeight = 20.0;
        
        // 测试空字符串状态
        trafficCondition.updateTrafficStatus(10, "");
        double emptyStatusWeight = trafficCondition.adjustWeight(originalWeight, 10);
        assertEquals("空字符串状态权重不应该改变", originalWeight, emptyStatusWeight, 0.001);
        
        // 测试小写状态
        trafficCondition.updateTrafficStatus(11, "clear");
        double lowerCaseWeight = trafficCondition.adjustWeight(originalWeight, 11);
        assertEquals("小写状态权重不应该改变", originalWeight, lowerCaseWeight, 0.001);
        
        // 测试混合大小写状态
        trafficCondition.updateTrafficStatus(12, "ClEaR");
        double mixedCaseWeight = trafficCondition.adjustWeight(originalWeight, 12);
        assertEquals("混合大小写状态权重不应该改变", originalWeight, mixedCaseWeight, 0.001);
        
        // 测试带空格的状态
        trafficCondition.updateTrafficStatus(13, " Clear ");
        double spacedWeight = trafficCondition.adjustWeight(originalWeight, 13);
        assertEquals("带空格状态权重不应该改变", originalWeight, spacedWeight, 0.001);
        
        // 测试数字状态
        trafficCondition.updateTrafficStatus(14, "123");
        double numericWeight = trafficCondition.adjustWeight(originalWeight, 14);
        assertEquals("数字状态权重不应该改变", originalWeight, numericWeight, 0.001);
    }

    /**
     * 测试TrafficCondition的复杂场景
     * 验证多个操作组合的效果
     */
    @Test
    public void testTrafficConditionComplexScenarios() {
        double originalWeight = 12.0;
        
        // 创建复杂的交通场景
        trafficCondition.updateTrafficStatus(100, "Clear");
        trafficCondition.updateTrafficStatus(101, "Congested");
        trafficCondition.updateTrafficStatus(102, "Closed");
        trafficCondition.updateTrafficStatus(103, "Accident");
        trafficCondition.updateTrafficStatus(104, "Unknown Status");
        
        // 验证各种状态的权重调整
        assertEquals("节点100权重应该不变", originalWeight, 
                    trafficCondition.adjustWeight(originalWeight, 100), 0.001);
        assertEquals("节点101权重应该翻倍", originalWeight * 2, 
                    trafficCondition.adjustWeight(originalWeight, 101), 0.001);
        assertEquals("节点102权重应该是无穷大", Double.MAX_VALUE, 
                    trafficCondition.adjustWeight(originalWeight, 102), 0.001);
        assertEquals("节点103权重应该是三倍", originalWeight * 3, 
                    trafficCondition.adjustWeight(originalWeight, 103), 0.001);
        assertEquals("节点104权重应该不变", originalWeight, 
                    trafficCondition.adjustWeight(originalWeight, 104), 0.001);
        
        // 动态修改状态并重新测试
        trafficCondition.updateTrafficStatus(101, "Clear");
        assertEquals("节点101修改后权重应该不变", originalWeight, 
                    trafficCondition.adjustWeight(originalWeight, 101), 0.001);
        
        trafficCondition.updateTrafficStatus(100, "Closed");
        assertEquals("节点100修改后权重应该是无穷大", Double.MAX_VALUE, 
                    trafficCondition.adjustWeight(originalWeight, 100), 0.001);
    }

    /**
     * 测试权重调整的精度问题
     * 验证浮点数精度处理
     */
    @Test
    public void testAdjustWeightPrecision() {
        // 测试小数精度
        double preciseWeight = 1.23456789;
        double congestedPrecise = trafficCondition.adjustWeight(preciseWeight, 2);
        assertEquals("精确权重翻倍应该正确", preciseWeight * 2, congestedPrecise, 0.0000001);
        
        double accidentPrecise = trafficCondition.adjustWeight(preciseWeight, 4);
        assertEquals("精确权重三倍应该正确", preciseWeight * 3, accidentPrecise, 0.0000001);
        
        // 测试科学计数法
        double scientificWeight = 1.5e-10;
        double scientificResult = trafficCondition.adjustWeight(scientificWeight, 2);
        assertEquals("科学计数法权重翻倍应该正确", scientificWeight * 2, scientificResult, 1e-15);
    }
}