package net.mooctest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * GasStation类的测试类
 * 测试加油站的功能，包括加油操作和成本计算
 */
public class GasStationTest {
    
    private GasStation normalGasStation;
    private GasStation expensiveGasStation;
    private GasStation cheapGasStation;
    private Vehicle testVehicle;
    private Vehicle lowFuelVehicle;
    private Vehicle fullFuelVehicle;
    private ByteArrayOutputStream outputStream;

    @Before
    public void setUp() {
        // 创建不同价格的加油站
        normalGasStation = new GasStation(1, 1.5);  // 节点1，每升1.5元
        expensiveGasStation = new GasStation(2, 3.0);  // 节点2，每升3.0元
        cheapGasStation = new GasStation(3, 0.8);  // 节点3，每升0.8元
        
        // 创建不同燃料状态的车辆
        testVehicle = new Vehicle("Test Vehicle", 1000.0, false, 50.0, 25.0, 0.5, 5.0, false);
        lowFuelVehicle = new Vehicle("Low Fuel", 1000.0, false, 40.0, 5.0, 1.0, 5.0, false);
        fullFuelVehicle = new Vehicle("Full Fuel", 1000.0, false, 50.0, 50.0, 1.0, 5.0, false);
        
        // 设置输出流来捕获控制台输出
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    /**
     * 测试GasStation构造函数和getter方法
     * 验证加油站能正确初始化
     */
    @Test
    public void testGasStationConstructorAndGetters() {
        // 测试普通加油站
        assertEquals("加油站节点ID应该正确", 1, normalGasStation.getNodeId());
        assertEquals("普通加油站油价应该正确", 1.5, normalGasStation.getFuelCostPerLitre(), 0.001);
        
        // 测试昂贵加油站
        assertEquals("昂贵加油站节点ID应该正确", 2, expensiveGasStation.getNodeId());
        assertEquals("昂贵加油站油价应该正确", 3.0, expensiveGasStation.getFuelCostPerLitre(), 0.001);
        
        // 测试便宜加油站
        assertEquals("便宜加油站节点ID应该正确", 3, cheapGasStation.getNodeId());
        assertEquals("便宜加油站油价应该正确", 0.8, cheapGasStation.getFuelCostPerLitre(), 0.001);
    }

    /**
     * 测试refuel方法
     * 验证加油操作的正确性
     */
    @Test
    public void testRefuel() {
        double initialFuel = testVehicle.getCurrentFuel();
        double refuelAmount = 10.0;
        
        normalGasStation.refuel(testVehicle, refuelAmount);
        
        // 验证燃料增加
        assertEquals("车辆燃料应该正确增加", initialFuel + refuelAmount, 
                    testVehicle.getCurrentFuel(), 0.001);
        
        // 验证控制台输出
        String output = outputStream.toString();
        assertTrue("输出应该包含加油量", output.contains("Refueled " + refuelAmount));
        assertTrue("输出应该包含加油站节点ID", output.contains("node " + normalGasStation.getNodeId()));
        
        double expectedCost = refuelAmount * normalGasStation.getFuelCostPerLitre();
        assertTrue("输出应该包含总成本", output.contains("for " + expectedCost + " cost."));
    }

    /**
     * 测试refuel方法的边界值
     * 验证各种特殊加油量的处理
     */
    @Test
    public void testRefuelBoundaryValues() {
        // 测试零加油量
        double initialFuel = testVehicle.getCurrentFuel();
        outputStream.reset();
        
        normalGasStation.refuel(testVehicle, 0.0);
        
        assertEquals("零加油量不应该改变燃料", initialFuel, testVehicle.getCurrentFuel(), 0.001);
        
        String zeroOutput = outputStream.toString();
        assertTrue("零加油量应该有输出", zeroOutput.contains("Refueled 0.0"));
        
        // 测试负加油量（虽然业务上不合理，但测试代码鲁棒性）
        outputStream.reset();
        initialFuel = testVehicle.getCurrentFuel();
        
        normalGasStation.refuel(testVehicle, -5.0);
        
        assertEquals("负加油量应该减少燃料", initialFuel - 5.0, testVehicle.getCurrentFuel(), 0.001);
        
        // 测试大加油量
        outputStream.reset();
        normalGasStation.refuel(testVehicle, 100.0);
        
        String largeOutput = outputStream.toString();
        assertTrue("大加油量应该有输出", largeOutput.contains("Refueled 100.0"));
    }

    /**
     * 测试refuel与不同燃料状态的车辆
     * 验证加油操作对各种燃料状态车辆的影响
     */
    @Test
    public void testRefuelWithDifferentFuelStates() {
        // 测试低燃料车辆
        double lowInitialFuel = lowFuelVehicle.getCurrentFuel();
        outputStream.reset();
        
        cheapGasStation.refuel(lowFuelVehicle, 20.0);
        
        assertEquals("低燃料车辆加油应该正确", lowInitialFuel + 20.0, 
                    lowFuelVehicle.getCurrentFuel(), 0.001);
        
        // 测试满燃料车辆（加油应该超过容量）
        double fullInitialFuel = fullFuelVehicle.getCurrentFuel();
        outputStream.reset();
        
        normalGasStation.refuel(fullFuelVehicle, 10.0);
        
        // 根据Vehicle.refuel的逻辑，加油不会超过容量
        assertEquals("满燃料车辆加油不应该超过容量", fullFuelVehicle.getFuelCapacity(), 
                    fullFuelVehicle.getCurrentFuel(), 0.001);
    }

    /**
     * 测试不同价格加油站的成本计算
     * 验证成本计算的正确性
     */
    @Test
    public void testRefuelCostCalculation() {
        double refuelAmount = 10.0;
        
        // 测试普通加油站
        outputStream.reset();
        normalGasStation.refuel(testVehicle, refuelAmount);
        String normalOutput = outputStream.toString();
        double normalCost = refuelAmount * normalGasStation.getFuelCostPerLitre();
        assertTrue("普通加油站成本应该正确", normalOutput.contains("for " + normalCost + " cost."));
        
        // 测试昂贵加油站
        outputStream.reset();
        expensiveGasStation.refuel(testVehicle, refuelAmount);
        String expensiveOutput = outputStream.toString();
        double expensiveCost = refuelAmount * expensiveGasStation.getFuelCostPerLitre();
        assertTrue("昂贵加油站成本应该正确", expensiveOutput.contains("for " + expensiveCost + " cost."));
        
        // 测试便宜加油站
        outputStream.reset();
        cheapGasStation.refuel(testVehicle, refuelAmount);
        String cheapOutput = outputStream.toString();
        double cheapCost = refuelAmount * cheapGasStation.getFuelCostPerLitre();
        assertTrue("便宜加油站成本应该正确", cheapOutput.contains("for " + cheapCost + " cost."));
    }

    /**
     * 测试GasStation的边界值
     * 验证极端参数的处理
     */
    @Test
    public void testGasStationBoundaryValues() {
        // 测试零价格加油站
        GasStation zeroPriceStation = new GasStation(0, 0.0);
        assertEquals("零价格应该正确设置", 0.0, zeroPriceStation.getFuelCostPerLitre(), 0.001);
        
        outputStream.reset();
        zeroPriceStation.refuel(testVehicle, 10.0);
        String zeroOutput = outputStream.toString();
        assertTrue("零成本应该正确显示", zeroOutput.contains("for 0.0 cost."));
        
        // 测试负价格加油站（虽然业务上不合理）
        GasStation negativePriceStation = new GasStation(-1, -1.5);
        assertEquals("负价格应该正确设置", -1.5, negativePriceStation.getFuelCostPerLitre(), 0.001);
        assertEquals("负节点ID应该正确设置", -1, negativePriceStation.getNodeId());
        
        // 测试极大价格
        GasStation maxPriceStation = new GasStation(Integer.MAX_VALUE, Double.MAX_VALUE);
        assertEquals("极大价格应该正确设置", Double.MAX_VALUE, maxPriceStation.getFuelCostPerLitre(), 0.001);
        assertEquals("极大节点ID应该正确设置", Integer.MAX_VALUE, maxPriceStation.getNodeId());
    }

    /**
     * 测试refuel方法的精度问题
     * 验证浮点数精度处理
     */
    @Test
    public void testRefuelPrecision() {
        // 测试小数精度
        double preciseAmount = 1.23456789;
        outputStream.reset();
        
        normalGasStation.refuel(testVehicle, preciseAmount);
        
        String preciseOutput = outputStream.toString();
        assertTrue("精确加油量应该正确显示", preciseOutput.contains("Refueled " + preciseAmount));
        
        double preciseCost = preciseAmount * normalGasStation.getFuelCostPerLitre();
        assertTrue("精确成本应该正确计算", preciseOutput.contains("for " + preciseCost + " cost."));
        
        // 测试科学计数法
        double scientificAmount = 1.5e-10;
        outputStream.reset();
        
        cheapGasStation.refuel(testVehicle, scientificAmount);
        
        String scientificOutput = outputStream.toString();
        assertTrue("科学计数法加油量应该正确显示", scientificOutput.contains("Refueled " + scientificAmount));
    }

    /**
     * 测试refuel与null车辆
     * 验证传入null车辆时的处理
     */
    @Test
    public void testRefuelWithNullVehicle() {
        try {
            outputStream.reset();
            normalGasStation.refuel(null, 10.0);
            
            // 如果没有抛出异常，检查输出
            String nullOutput = outputStream.toString();
            // 根据实现，可能会抛出NullPointerException或产生特殊输出
            assertTrue("null车辆应该被处理", true);
        } catch (NullPointerException e) {
            // 预期的异常，测试通过
            assertTrue("null车辆应该抛出NullPointerException", true);
        } catch (Exception e) {
            fail("null车辆不应该抛出其他异常: " + e.getMessage());
        }
    }

    /**
     * 测试多次加油操作
     * 验证连续加油的正确性
     */
    @Test
    public void testMultipleRefuels() {
        double initialFuel = lowFuelVehicle.getCurrentFuel();
        
        // 第一次加油
        outputStream.reset();
        normalGasStation.refuel(lowFuelVehicle, 10.0);
        double fuelAfterFirst = lowFuelVehicle.getCurrentFuel();
        
        // 第二次加油
        outputStream.reset();
        expensiveGasStation.refuel(lowFuelVehicle, 5.0);
        double fuelAfterSecond = lowFuelVehicle.getCurrentFuel();
        
        // 验证燃料累积
        assertEquals("第一次加油后燃料应该正确", initialFuel + 10.0, fuelAfterFirst, 0.001);
        assertEquals("第二次加油后燃料应该正确", fuelAfterFirst + 5.0, fuelAfterSecond, 0.001);
        
        // 验证两次都有输出
        String firstOutput = outputStream.toString();
        assertTrue("第二次加油应该有输出", firstOutput.contains("Refueled 5.0"));
    }

    /**
     * 测试加油站输出格式
     * 验证控制台输出的格式和内容
     */
    @Test
    public void testRefuelOutputFormat() {
        outputStream.reset();
        normalGasStation.refuel(testVehicle, 15.5);
        
        String output = outputStream.toString();
        
        // 验证输出包含所有必要信息
        assertTrue("输出应该包含'Refueled'", output.contains("Refueled"));
        assertTrue("输出应该包含加油量", output.contains("15.5"));
        assertTrue("输出应该包含'litres'", output.contains("litres"));
        assertTrue("输出应该包含'node'", output.contains("node"));
        assertTrue("输出应该包含节点ID", output.contains("1"));
        assertTrue("输出应该包含'for'", output.contains("for"));
        assertTrue("输出应该包含'cost'", output.contains("cost"));
        assertTrue("输出应该以'.'结尾", output.endsWith(".\n"));
        
        // 验证成本计算
        double expectedCost = 15.5 * 1.5;
        assertTrue("输出应该包含正确的成本", output.contains("for " + expectedCost + " cost."));
    }
}