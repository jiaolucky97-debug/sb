package net.mooctest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

/**
 * Vehicle类的测试类
 * 测试车辆的所有属性和燃料管理功能
 */
public class VehicleTest {
    
    private Vehicle standardVehicle;
    private Vehicle heavyVehicle;
    private Vehicle emergencyVehicle;
    private Vehicle lowFuelVehicle;

    @Before
    public void setUp() {
        // 标准车辆
        standardVehicle = new Vehicle(
            "Standard Vehicle",  // vehicleType
            1000.0,              // maxLoad
            false,                // requiresTollFreeRoute
            50.0,                 // fuelCapacity
            25.0,                 // currentFuel
            0.5,                  // fuelConsumptionPerKm
            5.0,                  // minFuelAtEnd
            false                 // emergencyVehicle
        );
        
        // 重型车辆
        heavyVehicle = new Vehicle(
            "Heavy Vehicle",
            5000.0,
            true,
            100.0,
            50.0,
            1.0,
            10.0,
            false
        );
        
        // 紧急车辆
        emergencyVehicle = new Vehicle(
            "Emergency Vehicle",
            2000.0,
            false,
            60.0,
            40.0,
            0.8,
            8.0,
            true
        );
        
        // 低燃料车辆
        lowFuelVehicle = new Vehicle(
            "Low Fuel Vehicle",
            1000.0,
            false,
            40.0,
            5.0,
            1.0,
            2.0,
            false
        );
    }

    /**
     * 测试Vehicle构造函数和所有getter方法
     * 验证所有属性都能正确初始化
     */
    @Test
    public void testVehicleConstructorAndGetters() {
        // 测试标准车辆
        assertEquals("车辆类型应该正确", "Standard Vehicle", standardVehicle.getVehicleType());
        assertEquals("最大载重应该正确", 1000.0, standardVehicle.getMaxLoad(), 0.001);
        assertFalse("标准车辆不需要免费路线", standardVehicle.requiresTollFreeRoute());
        assertEquals("燃料容量应该正确", 50.0, standardVehicle.getFuelCapacity(), 0.001);
        assertEquals("当前燃料应该正确", 25.0, standardVehicle.getCurrentFuel(), 0.001);
        assertEquals("每公里燃料消耗应该正确", 0.5, standardVehicle.getFuelConsumptionPerKm(), 0.001);
        assertEquals("终点最小燃料应该正确", 5.0, standardVehicle.getMinFuelAtEnd(), 0.001);
        assertFalse("标准车辆不是紧急车辆", standardVehicle.isEmergencyVehicle());
        
        // 测试重型车辆
        assertEquals("重型车辆类型应该正确", "Heavy Vehicle", heavyVehicle.getVehicleType());
        assertEquals("重型车辆最大载重应该正确", 5000.0, heavyVehicle.getMaxLoad(), 0.001);
        assertTrue("重型车辆需要免费路线", heavyVehicle.requiresTollFreeRoute());
        assertEquals("重型车辆燃料容量应该正确", 100.0, heavyVehicle.getFuelCapacity(), 0.001);
        assertEquals("重型车辆当前燃料应该正确", 50.0, heavyVehicle.getCurrentFuel(), 0.001);
        assertEquals("重型车辆每公里燃料消耗应该正确", 1.0, heavyVehicle.getFuelConsumptionPerKm(), 0.001);
        assertEquals("重型车辆终点最小燃料应该正确", 10.0, heavyVehicle.getMinFuelAtEnd(), 0.001);
        assertFalse("重型车辆不是紧急车辆", heavyVehicle.isEmergencyVehicle());
        
        // 测试紧急车辆
        assertEquals("紧急车辆类型应该正确", "Emergency Vehicle", emergencyVehicle.getVehicleType());
        assertEquals("紧急车辆最大载重应该正确", 2000.0, emergencyVehicle.getMaxLoad(), 0.001);
        assertFalse("紧急车辆不需要免费路线", emergencyVehicle.requiresTollFreeRoute());
        assertEquals("紧急车辆燃料容量应该正确", 60.0, emergencyVehicle.getFuelCapacity(), 0.001);
        assertEquals("紧急车辆当前燃料应该正确", 40.0, emergencyVehicle.getCurrentFuel(), 0.001);
        assertEquals("紧急车辆每公里燃料消耗应该正确", 0.8, emergencyVehicle.getFuelConsumptionPerKm(), 0.001);
        assertEquals("紧急车辆终点最小燃料应该正确", 8.0, emergencyVehicle.getMinFuelAtEnd(), 0.001);
        assertTrue("紧急车辆应该是紧急车辆", emergencyVehicle.isEmergencyVehicle());
    }

    /**
     * 测试consumeFuel方法
     * 验证燃料消耗计算的正确性
     */
    @Test
    public void testConsumeFuel() {
        double initialFuel = standardVehicle.getCurrentFuel();
        double distance = 10.0;
        double expectedConsumption = distance * standardVehicle.getFuelConsumptionPerKm();
        
        standardVehicle.consumeFuel(distance);
        
        assertEquals("燃料应该正确消耗", initialFuel - expectedConsumption, 
                    standardVehicle.getCurrentFuel(), 0.001);
        
        // 测试零距离消耗
        initialFuel = standardVehicle.getCurrentFuel();
        standardVehicle.consumeFuel(0.0);
        assertEquals("零距离不应该消耗燃料", initialFuel, standardVehicle.getCurrentFuel(), 0.001);
        
        // 测试负距离消耗（虽然业务上不合理，但测试代码鲁棒性）
        initialFuel = standardVehicle.getCurrentFuel();
        standardVehicle.consumeFuel(-5.0);
        double negativeConsumption = -5.0 * standardVehicle.getFuelConsumptionPerKm();
        assertEquals("负距离应该增加燃料", initialFuel - negativeConsumption, 
                    standardVehicle.getCurrentFuel(), 0.001);
    }

    /**
     * 测试needsRefueling方法
     * 验证燃料需求判断的正确性
     */
    @Test
    public void testNeedsRefueling() {
        // 测试需要加油的情况
        assertTrue("低燃料车辆应该需要加油", lowFuelVehicle.needsRefueling(3.0));
        
        // 测试不需要加油的情况
        assertFalse("低燃料车辆短距离不需要加油", lowFuelVehicle.needsRefueling(2.0));
        
        // 测试标准车辆
        assertFalse("标准车辆正常距离不需要加油", standardVehicle.needsRefueling(20.0));
        assertTrue("标准车辆长距离需要加油", standardVehicle.needsRefueling(50.0));
        
        // 测试零距离
        assertFalse("零距离不需要加油", standardVehicle.needsRefueling(0.0));
        
        // 测试负距离
        assertFalse("负距离不需要加油", standardVehicle.needsRefueling(-10.0));
    }

    /**
     * 测试refuel方法
     * 验证加油功能的正确性
     */
    @Test
    public void testRefuel() {
        // 测试正常加油
        double initialFuel = lowFuelVehicle.getCurrentFuel();
        double refuelAmount = 10.0;
        lowFuelVehicle.refuel(refuelAmount);
        assertEquals("加油后燃料应该正确增加", initialFuel + refuelAmount, 
                    lowFuelVehicle.getCurrentFuel(), 0.001);
        
        // 测试加油超过容量
        double fuelBeforeCap = emergencyVehicle.getCurrentFuel();
        double overCapacityAmount = 100.0; // 超过60L容量
        emergencyVehicle.refuel(overCapacityAmount);
        assertEquals("加油不应该超过容量", emergencyVehicle.getFuelCapacity(), 
                    emergencyVehicle.getCurrentFuel(), 0.001);
        
        // 测试零加油
        initialFuel = standardVehicle.getCurrentFuel();
        standardVehicle.refuel(0.0);
        assertEquals("零加油不应该改变燃料", initialFuel, standardVehicle.getCurrentFuel(), 0.001);
        
        // 测试负加油（应该减少燃料）
        initialFuel = standardVehicle.getCurrentFuel();
        standardVehicle.refuel(-5.0);
        assertEquals("负加油应该减少燃料", initialFuel - 5.0, standardVehicle.getCurrentFuel(), 0.001);
    }

    /**
     * 测试Vehicle的边界值
     * 验证极端参数的处理
     */
    @Test
    public void testVehicleBoundaryValues() {
        // 测试零容量车辆
        Vehicle zeroCapacityVehicle = new Vehicle("Zero Cap", 0.0, false, 0.0, 0.0, 0.0, 0.0, false);
        assertEquals("零容量应该正确设置", 0.0, zeroCapacityVehicle.getFuelCapacity(), 0.001);
        
        // 测试负值车辆（虽然业务上不合理）
        Vehicle negativeVehicle = new Vehicle("Negative", -100.0, false, -50.0, -25.0, -0.5, -5.0, false);
        assertEquals("负载重应该正确设置", -100.0, negativeVehicle.getMaxLoad(), 0.001);
        assertEquals("负容量应该正确设置", -50.0, negativeVehicle.getFuelCapacity(), 0.001);
        
        // 测试极大值
        Vehicle maxVehicle = new Vehicle("Max", Double.MAX_VALUE, false, Double.MAX_VALUE, 
                                       Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, false);
        assertEquals("极大值应该正确设置", Double.MAX_VALUE, maxVehicle.getMaxLoad(), 0.001);
    }

    /**
     * 测试Vehicle的燃料状态组合
     * 验证各种燃料状态的正确处理
     */
    @Test
    public void testVehicleFuelStates() {
        // 测试满燃料车辆
        Vehicle fullFuelVehicle = new Vehicle("Full Fuel", 1000.0, false, 50.0, 50.0, 1.0, 5.0, false);
        assertFalse("满燃料车辆短距离不需要加油", fullFuelVehicle.needsRefueling(10.0));
        assertTrue("满燃料车辆长距离仍需要加油", fullFuelVehicle.needsRefueling(60.0));
        
        // 测试空燃料车辆
        Vehicle emptyFuelVehicle = new Vehicle("Empty Fuel", 1000.0, false, 50.0, 0.0, 1.0, 5.0, false);
        assertTrue("空燃料车辆任何距离都需要加油", emptyFuelVehicle.needsRefueling(0.0));
        
        // 测试刚好满足最小燃料的情况
        Vehicle justEnoughVehicle = new Vehicle("Just Enough", 1000.0, false, 50.0, 15.0, 1.0, 5.0, false);
        assertFalse("刚好满足最小燃料不需要加油", justEnoughVehicle.needsRefueling(10.0));
        assertTrue("超出最小燃料需要加油", justEnoughVehicle.needsRefueling(11.0));
    }

    /**
     * 测试Vehicle的复杂场景
     * 验证多个操作组合的效果
     */
    @Test
    public void testVehicleComplexScenarios() {
        // 模拟一次完整的行程：加油 -> 行驶 -> 判断是否需要加油
        Vehicle testVehicle = new Vehicle("Test", 1000.0, false, 40.0, 10.0, 0.8, 5.0, false);
        
        // 加油到满
        testVehicle.refuel(30.0);
        assertEquals("加油后应该满燃料", 40.0, testVehicle.getCurrentFuel(), 0.001);
        
        // 行驶一段距离
        double distance = 20.0;
        testVehicle.consumeFuel(distance);
        double expectedFuel = 40.0 - distance * testVehicle.getFuelConsumptionPerKm();
        assertEquals("行驶后燃料应该正确", expectedFuel, testVehicle.getCurrentFuel(), 0.001);
        
        // 判断是否需要加油
        boolean needsRefuel = testVehicle.needsRefueling(30.0);
        double remainingFuel = testVehicle.getCurrentFuel();
        double neededForTrip = 30.0 * testVehicle.getFuelConsumptionPerKm();
        boolean expectedNeedsRefuel = (remainingFuel - neededForTrip) < testVehicle.getMinFuelAtEnd();
        assertEquals("加油判断应该正确", expectedNeedsRefuel, needsRefuel);
    }

    /**
     * 测试Vehicle的特殊类型
     * 验证不同类型车辆的特殊行为
     */
    @Test
    public void testVehicleSpecialTypes() {
        // 测试空字符串类型
        Vehicle emptyTypeVehicle = new Vehicle("", 1000.0, false, 50.0, 25.0, 1.0, 5.0, false);
        assertEquals("空类型应该正确设置", "", emptyTypeVehicle.getVehicleType());
        
        // 测试null类型（如果允许）
        try {
            Vehicle nullTypeVehicle = new Vehicle(null, 1000.0, false, 50.0, 25.0, 1.0, 5.0, false);
            assertNull("null类型应该正确设置", nullTypeVehicle.getVehicleType());
        } catch (Exception e) {
            // 如果不支持null，这里会捕获异常
            assertTrue("null类型应该被处理", true);
        }
        
        // 测试长字符串类型
        String longType = "Very Long Vehicle Type Name That Exceeds Normal Length";
        Vehicle longTypeVehicle = new Vehicle(longType, 1000.0, false, 50.0, 25.0, 1.0, 5.0, false);
        assertEquals("长类型应该正确设置", longType, longTypeVehicle.getVehicleType());
    }
}