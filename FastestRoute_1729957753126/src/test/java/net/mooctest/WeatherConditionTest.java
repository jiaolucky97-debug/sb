package net.mooctest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

/**
 * WeatherCondition类的测试类
 * 测试天气条件对路径权重的影响
 */
public class WeatherConditionTest {
    
    private WeatherCondition clearWeather;
    private WeatherCondition rainyWeather;
    private WeatherCondition snowyWeather;
    private WeatherCondition stormyWeather;
    private WeatherCondition unknownWeather;
    private Node testNode;

    @Before
    public void setUp() {
        // 创建不同天气条件
        clearWeather = new WeatherCondition("Clear");
        rainyWeather = new WeatherCondition("Rainy");
        snowyWeather = new WeatherCondition("Snowy");
        stormyWeather = new WeatherCondition("Stormy");
        unknownWeather = new WeatherCondition("Foggy"); // 未知天气
        
        // 创建测试节点
        testNode = new Node(1, false, "Highway", false, false, false, 1.0, 0, 24);
    }

    /**
     * 测试WeatherCondition构造函数
     * 验证天气条件能正确初始化
     */
    @Test
    public void testWeatherConditionConstructor() {
        assertNotNull("晴朗天气应该被正确创建", clearWeather);
        assertNotNull("雨天应该被正确创建", rainyWeather);
        assertNotNull("雪天应该被正确创建", snowyWeather);
        assertNotNull("暴风雨天应该被正确创建", stormyWeather);
        assertNotNull("未知天气应该被正确创建", unknownWeather);
    }

    /**
     * 测试adjustWeightForWeather方法的各种天气条件
     * 验证不同天气对权重的影响
     */
    @Test
    public void testAdjustWeightForDifferentWeather() {
        double originalWeight = 10.0;
        
        // 测试Clear天气
        double clearWeight = clearWeather.adjustWeightForWeather(originalWeight, testNode);
        assertEquals("晴朗天气权重不应该改变", originalWeight, clearWeight, 0.001);
        
        // 测试Rainy天气
        double rainyWeight = rainyWeather.adjustWeightForWeather(originalWeight, testNode);
        assertEquals("雨天权重应该增加50%", originalWeight * 1.5, rainyWeight, 0.001);
        
        // 测试Snowy天气
        double snowyWeight = snowyWeather.adjustWeightForWeather(originalWeight, testNode);
        assertEquals("雪天权重应该翻倍", originalWeight * 2.0, snowyWeight, 0.001);
        
        // 测试Stormy天气
        double stormyWeight = stormyWeather.adjustWeightForWeather(originalWeight, testNode);
        assertEquals("暴风雨天权重应该是三倍", originalWeight * 3.0, stormyWeight, 0.001);
        
        // 测试未知天气
        double unknownWeight = unknownWeather.adjustWeightForWeather(originalWeight, testNode);
        assertEquals("未知天气权重不应该改变", originalWeight, unknownWeight, 0.001);
    }

    /**
     * 测试adjustWeightForWeather方法的边界值
     * 验证各种权重值的处理
     */
    @Test
    public void testAdjustWeightBoundaryValues() {
        // 测试零权重
        double zeroWeight = rainyWeather.adjustWeightForWeather(0.0, testNode);
        assertEquals("零权重增加50%后仍为零", 0.0, zeroWeight, 0.001);
        
        // 测试负权重
        double negativeWeight = snowyWeather.adjustWeightForWeather(-10.0, testNode);
        assertEquals("负权重翻倍应该正确", -20.0, negativeWeight, 0.001);
        
        // 测试极大权重
        double maxWeight = stormyWeather.adjustWeightForWeather(Double.MAX_VALUE, testNode);
        // Double.MAX_VALUE * 3 会溢出为无穷大
        assertTrue("极大权重三倍可能溢出", Double.isInfinite(maxWeight) || maxWeight > 0);
        
        // 测试极小权重
        double minWeight = rainyWeather.adjustWeightForWeather(Double.MIN_VALUE, testNode);
        assertEquals("极小权重增加50%应该正确", Double.MIN_VALUE * 1.5, minWeight, 0.0);
        
        // 测试无穷大权重
        double infiniteWeight = snowyWeather.adjustWeightForWeather(Double.POSITIVE_INFINITY, testNode);
        assertTrue("无穷大权重处理应该正确", Double.isInfinite(infiniteWeight));
    }

    /**
     * 测试adjustWeightForWeather与不同类型的节点
     * 验证天气调整与节点类型的独立性
     */
    @Test
    public void testAdjustWeightWithDifferentNodeTypes() {
        double originalWeight = 15.0;
        
        // 测试与障碍节点
        Node obstacleNode = new Node(2, true, "Obstacle", false, false, false, 0.0, 0, 24);
        double obstacleWeight = rainyWeather.adjustWeightForWeather(originalWeight, obstacleNode);
        assertEquals("障碍节点在雨天权重应该增加50%", originalWeight * 1.5, obstacleWeight, 0.001);
        
        // 测试与收费道路节点
        Node tollNode = new Node(3, false, "Toll Road", true, false, false, 2.0, 0, 24);
        double tollWeight = snowyWeather.adjustWeightForWeather(originalWeight, tollNode);
        assertEquals("收费道路节点在雪天权重应该翻倍", originalWeight * 2.0, tollWeight, 0.001);
        
        // 测试与高风险区域节点
        Node highRiskNode = new Node(4, false, "High Risk", false, false, true, 3.0, 0, 24);
        double highRiskWeight = stormyWeather.adjustWeightForWeather(originalWeight, highRiskNode);
        assertEquals("高风险区域节点在暴风雨天权重应该是三倍", originalWeight * 3.0, highRiskWeight, 0.001);
        
        // 测试与null节点
        try {
            double nullNodeWeight = clearWeather.adjustWeightForWeather(originalWeight, null);
            assertEquals("null节点在晴朗天气权重不应该改变", originalWeight, nullNodeWeight, 0.001);
        } catch (Exception e) {
            fail("null节点不应该导致异常: " + e.getMessage());
        }
    }

    /**
     * 测试特殊天气字符串
     * 验证各种特殊字符串天气的处理
     */
    @Test
    public void testSpecialWeatherStrings() {
        double originalWeight = 20.0;
        
        // 测试空字符串天气
        WeatherCondition emptyWeather = new WeatherCondition("");
        double emptyWeight = emptyWeather.adjustWeightForWeather(originalWeight, testNode);
        assertEquals("空字符串天气权重不应该改变", originalWeight, emptyWeight, 0.001);
        
        // 测试小写天气
        WeatherCondition lowerCaseWeather = new WeatherCondition("clear");
        double lowerCaseWeight = lowerCaseWeather.adjustWeightForWeather(originalWeight, testNode);
        assertEquals("小写天气权重不应该改变", originalWeight, lowerCaseWeight, 0.001);
        
        // 测试混合大小写天气
        WeatherCondition mixedCaseWeather = new WeatherCondition("ClEaR");
        double mixedCaseWeight = mixedCaseWeather.adjustWeightForWeather(originalWeight, testNode);
        assertEquals("混合大小写天气权重不应该改变", originalWeight, mixedCaseWeight, 0.001);
        
        // 测试带空格的天气
        WeatherCondition spacedWeather = new WeatherCondition(" Clear ");
        double spacedWeight = spacedWeather.adjustWeightForWeather(originalWeight, testNode);
        assertEquals("带空格天气权重不应该改变", originalWeight, spacedWeight, 0.001);
        
        // 测试数字天气
        WeatherCondition numericWeather = new WeatherCondition("123");
        double numericWeight = numericWeather.adjustWeightForWeather(originalWeight, testNode);
        assertEquals("数字天气权重不应该改变", originalWeight, numericWeight, 0.001);
    }

    /**
     * 测试权重调整的精度问题
     * 验证浮点数精度处理
     */
    @Test
    public void testAdjustWeightPrecision() {
        // 测试小数精度
        double preciseWeight = 1.23456789;
        
        double rainyPrecise = rainyWeather.adjustWeightForWeather(preciseWeight, testNode);
        assertEquals("雨天精确权重应该正确", preciseWeight * 1.5, rainyPrecise, 0.0000001);
        
        double snowyPrecise = snowyWeather.adjustWeightForWeather(preciseWeight, testNode);
        assertEquals("雪天精确权重应该正确", preciseWeight * 2.0, snowyPrecise, 0.0000001);
        
        double stormyPrecise = stormyWeather.adjustWeightForWeather(preciseWeight, testNode);
        assertEquals("暴风雨天精确权重应该正确", preciseWeight * 3.0, stormyPrecise, 0.0000001);
        
        // 测试科学计数法
        double scientificWeight = 1.5e-10;
        double scientificResult = rainyWeather.adjustWeightForWeather(scientificWeight, testNode);
        assertEquals("科学计数法权重应该正确", scientificWeight * 1.5, scientificResult, 1e-15);
    }

    /**
     * 测试WeatherCondition与null天气
     * 验证null天气的处理
     */
    @Test
    public void testWeatherConditionWithNullWeather() {
        try {
            WeatherCondition nullWeather = new WeatherCondition(null);
            double originalWeight = 10.0;
            
            double weight = nullWeather.adjustWeightForWeather(originalWeight, testNode);
            assertEquals("null天气权重不应该改变", originalWeight, weight, 0.001);
        } catch (Exception e) {
            fail("null天气不应该导致异常: " + e.getMessage());
        }
    }

    /**
     * 测试极端天气组合
     * 验证极端天气对权重的影响
     */
    @Test
    public void testExtremeWeatherConditions() {
        double originalWeight = 5.0;
        
        // 测试连续的权重调整
        double afterRainy = rainyWeather.adjustWeightForWeather(originalWeight, testNode);
        double afterSnowy = snowyWeather.adjustWeightForWeather(afterRainy, testNode);
        double afterStormy = stormyWeather.adjustWeightForWeather(afterSnowy, testNode);
        
        // 验证连续调整的效果
        assertEquals("雨天后权重应该正确", originalWeight * 1.5, afterRainy, 0.001);
        assertEquals("雪天后权重应该正确", afterRainy * 2.0, afterSnowy, 0.001);
        assertEquals("暴风雨天后权重应该正确", afterSnowy * 3.0, afterStormy, 0.001);
        
        // 测试极端权重值
        double extremeWeight = 1e100;
        double extremeResult = stormyWeather.adjustWeightForWeather(extremeWeight, testNode);
        assertTrue("极端权重处理应该正确", Double.isInfinite(extremeResult) || extremeResult > 0);
    }

    /**
     * 测试天气条件的一致性
     * 验证相同天气条件的行为一致性
     */
    @Test
    public void testWeatherConditionConsistency() {
        double originalWeight = 12.0;
        
        // 创建两个相同的天气条件
        WeatherCondition rainyWeather2 = new WeatherCondition("Rainy");
        
        // 测试一致性
        double weight1 = rainyWeather.adjustWeightForWeather(originalWeight, testNode);
        double weight2 = rainyWeather2.adjustWeightForWeather(originalWeight, testNode);
        
        assertEquals("相同天气条件应该产生相同结果", weight1, weight2, 0.001);
        
        // 测试不同节点的一致性
        Node anotherNode = new Node(2, false, "Regular Road", false, false, false, 1.0, 0, 24);
        double weight3 = rainyWeather.adjustWeightForWeather(originalWeight, anotherNode);
        
        assertEquals("天气调整应该与节点类型无关", weight1, weight3, 0.001);
    }

    /**
     * 测试WeatherCondition的复杂场景
     * 验证多个操作组合的效果
     */
    @Test
    public void testWeatherConditionComplexScenarios() {
        // 创建不同权重的测试场景
        double[] weights = {0.0, 1.0, 10.0, 100.0, -5.0, Double.MIN_VALUE, Double.MAX_VALUE};
        
        for (double weight : weights) {
            // 测试各种天气条件
            double clearResult = clearWeather.adjustWeightForWeather(weight, testNode);
            double rainyResult = rainyWeather.adjustWeightForWeather(weight, testNode);
            double snowyResult = snowyWeather.adjustWeightForWeather(weight, testNode);
            double stormyResult = stormyWeather.adjustWeightForWeather(weight, testNode);
            
            // 验证结果
            assertEquals("晴朗天气权重应该不变", weight, clearResult, 0.001);
            
            if (Double.isInfinite(weight) || weight == Double.MAX_VALUE) {
                assertTrue("大权重雨天处理应该正确", Double.isInfinite(rainyResult) || rainyResult > 0);
            } else {
                assertEquals("雨天权重应该增加50%", weight * 1.5, rainyResult, 0.001);
            }
            
            if (Double.isInfinite(weight) || weight == Double.MAX_VALUE) {
                assertTrue("大权重雪天处理应该正确", Double.isInfinite(snowyResult) || snowyResult > 0);
            } else {
                assertEquals("雪天权重应该翻倍", weight * 2.0, snowyResult, 0.001);
            }
            
            if (Double.isInfinite(weight) || weight == Double.MAX_VALUE) {
                assertTrue("大权重暴风雨天处理应该正确", Double.isInfinite(stormyResult) || stormyResult > 0);
            } else {
                assertEquals("暴风雨天权重应该是三倍", weight * 3.0, stormyResult, 0.001);
            }
        }
    }
}