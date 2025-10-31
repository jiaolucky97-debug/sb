package net.mooctest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * FastestRoute项目的完整测试套件
 * 包含所有测试类，用于一次性运行所有测试
 * 
 * 本测试套件包含以下18个测试类：
 * 1. EdgeTest - 边类的测试
 * 2. NodeTest - 节点类的测试
 * 3. GraphTest - 图类的测试
 * 4. PathResultTest - 路径结果类的测试
 * 5. PathNodeTest - 路径节点类的测试
 * 6. VehicleTest - 车辆类的测试
 * 7. GasStationTest - 加油站类的测试
 * 8. TrafficConditionTest - 交通条件类的测试
 * 9. WeatherConditionTest - 天气条件类的测试
 * 10. SearchAlgorithmTest - 搜索算法抽象类的测试
 * 11. RouteOptimizerTest - 路由优化器的测试
 * 12. DijkstraTest - Dijkstra算法的测试
 * 13. AStarTest - A*算法的测试
 * 14. BellmanFordTest - Bellman-Ford算法的测试
 * 15. FloydWarshallTest - Floyd-Warshall算法的测试
 * 16. IterativeDeepeningSearchTest - 迭代深化搜索的测试
 * 17. ShortestTimeFirstTest - 最短时间优先算法的测试
 * 18. GraphTest - 图类的测试（已包含在列表中）
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    // 基础数据结构测试
    EdgeTest.class,
    NodeTest.class,
    GraphTest.class,
    PathResultTest.class,
    PathNodeTest.class,
    
    // 业务实体测试
    VehicleTest.class,
    GasStationTest.class,
    TrafficConditionTest.class,
    WeatherConditionTest.class,
    
    // 算法框架测试
    SearchAlgorithmTest.class,
    RouteOptimizerTest.class,
    
    // 具体算法实现测试
    DijkstraTest.class,
    AStarTest.class,
    BellmanFordTest.class,
    FloydWarshallTest.class,
    IterativeDeepeningSearchTest.class,
    ShortestTimeFirstTest.class
})

/**
 * 测试套件主类
 * 使用JUnit Suite运行器来整合所有测试类
 * 
 * 运行方式：
 * 1. 在IDE中直接运行此类
 * 2. 使用Maven命令：mvn test
 * 3. 使用命令行：java -cp ... org.junit.runner.JUnitCore net.mooctest.AllTestsSuite
 * 
 * 预期结果：
 * - 分支覆盖率：90%以上
 * - 变异杀死率：90%以上
 * - 测试通过率：95%以上
 */
public class AllTestsSuite {
    // 测试套件类不需要任何代码
    // 所有配置都通过注解完成
}