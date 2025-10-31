package net.mooctest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * PathResult类的测试类
 * 测试路径结果的管理和显示功能
 */
public class PathResultTest {
    
    private List<Node> testPath;
    private List<Node> emptyPath;
    private List<Node> singleNodePath;
    private PathResult pathResult;
    private PathResult emptyResult;
    private PathResult singleNodeResult;

    @Before
    public void setUp() {
        // 创建测试路径
        testPath = new ArrayList<>();
        testPath.add(new Node(1, false, "Start", false, false, false, 1.0, 0, 24));
        testPath.add(new Node(2, false, "Middle", false, false, false, 1.0, 0, 24));
        testPath.add(new Node(3, false, "End", false, false, false, 1.0, 0, 24));
        
        // 创建空路径
        emptyPath = new ArrayList<>();
        
        // 创建单节点路径
        singleNodePath = new ArrayList<>();
        singleNodePath.add(new Node(1, false, "Single", false, false, false, 1.0, 0, 24));
        
        // 创建PathResult对象
        pathResult = new PathResult(testPath);
        emptyResult = new PathResult(emptyPath);
        singleNodeResult = new PathResult(singleNodePath);
    }

    /**
     * 测试PathResult构造函数和getPath方法
     * 验证路径结果能正确初始化
     */
    @Test
    public void testPathResultConstructorAndGetPath() {
        // 测试正常路径
        assertNotNull("路径结果应该被正确创建", pathResult);
        assertNotNull("路径应该不为null", pathResult.getPath());
        assertEquals("路径长度应该正确", 3, pathResult.getPath().size());
        
        // 验证路径内容
        assertEquals("第一个节点ID应该正确", 1, pathResult.getPath().get(0).getNodeId());
        assertEquals("第二个节点ID应该正确", 2, pathResult.getPath().get(1).getNodeId());
        assertEquals("第三个节点ID应该正确", 3, pathResult.getPath().get(2).getNodeId());
        
        // 测试空路径
        assertNotNull("空路径结果应该被正确创建", emptyResult);
        assertNotNull("空路径应该不为null", emptyResult.getPath());
        assertEquals("空路径长度应该为0", 0, emptyResult.getPath().size());
        
        // 测试单节点路径
        assertNotNull("单节点路径结果应该被正确创建", singleNodeResult);
        assertEquals("单节点路径长度应该为1", 1, singleNodeResult.getPath().size());
    }

    /**
     * 测试getPath返回的列表引用
     * 验证返回的是原始列表的引用
     */
    @Test
    public void testGetPathReference() {
        List<Node> retrievedPath = pathResult.getPath();
        int originalSize = retrievedPath.size();
        
        // 修改返回的列表
        retrievedPath.clear();
        
        // 验证原始路径被修改
        assertEquals("修改返回的列表应该影响原始路径", 0, pathResult.getPath().size());
    }

    /**
     * 测试printPath方法
     * 验证路径打印功能（虽然输出到控制台，但至少确保方法能正常执行）
     */
    @Test
    public void testPrintPath() {
        // 测试正常路径打印
        try {
            pathResult.printPath();
            // 如果没有抛出异常，则测试通过
            assertTrue("printPath应该正常执行", true);
        } catch (Exception e) {
            fail("printPath不应该抛出异常: " + e.getMessage());
        }
        
        // 测试空路径打印
        try {
            emptyResult.printPath();
            assertTrue("空路径printPath应该正常执行", true);
        } catch (Exception e) {
            fail("空路径printPath不应该抛出异常: " + e.getMessage());
        }
        
        // 测试单节点路径打印
        try {
            singleNodeResult.printPath();
            assertTrue("单节点路径printPath应该正常执行", true);
        } catch (Exception e) {
            fail("单节点路径printPath不应该抛出异常: " + e.getMessage());
        }
    }

    /**
     * 测试PathResult与null路径
     * 验证传入null路径时的处理
     */
    @Test
    public void testPathResultWithNullPath() {
        // 测试传入null路径
        try {
            PathResult nullPathResult = new PathResult(null);
            assertNull("null路径应该被正确处理", nullPathResult.getPath());
        } catch (Exception e) {
            fail("null路径不应该导致异常: " + e.getMessage());
        }
    }

    /**
     * 测试PathResult的边界情况
     * 验证各种特殊路径的处理
     */
    @Test
    public void testPathResultBoundaryCases() {
        // 测试包含障碍节点的路径
        List<Node> pathWithObstacle = new ArrayList<>();
        pathWithObstacle.add(new Node(1, false, "Start", false, false, false, 1.0, 0, 24));
        pathWithObstacle.add(new Node(2, true, "Obstacle", false, false, false, 0.0, 0, 24));
        pathWithObstacle.add(new Node(3, false, "End", false, false, false, 1.0, 0, 24));
        
        PathResult obstacleResult = new PathResult(pathWithObstacle);
        assertEquals("包含障碍节点的路径长度应该正确", 3, obstacleResult.getPath().size());
        assertTrue("路径应该包含障碍节点", obstacleResult.getPath().get(1).isObstacle());
        
        // 测试长路径
        List<Node> longPath = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            longPath.add(new Node(i, false, "Node" + i, false, false, false, 1.0, 0, 24));
        }
        
        PathResult longPathResult = new PathResult(longPath);
        assertEquals("长路径长度应该正确", 100, longPathResult.getPath().size());
        assertEquals("长路径第一个节点ID应该正确", 1, longPathResult.getPath().get(0).getNodeId());
        assertEquals("长路径最后一个节点ID应该正确", 100, longPathResult.getPath().get(99).getNodeId());
        
        // 测试包含重复节点的路径
        List<Node> pathWithDuplicates = new ArrayList<>();
        Node repeatedNode = new Node(5, false, "Repeated", false, false, false, 1.0, 0, 24);
        pathWithDuplicates.add(repeatedNode);
        pathWithDuplicates.add(new Node(2, false, "Other", false, false, false, 1.0, 0, 24));
        pathWithDuplicates.add(repeatedNode);
        
        PathResult duplicateResult = new PathResult(pathWithDuplicates);
        assertEquals("包含重复节点的路径长度应该正确", 3, duplicateResult.getPath().size());
        assertSame("重复节点应该是同一个对象", duplicateResult.getPath().get(0), duplicateResult.getPath().get(2));
    }

    /**
     * 测试PathResult与不同类型节点的组合
     * 验证路径能包含各种类型的节点
     */
    @Test
    public void testPathResultWithDifferentNodeTypes() {
        List<Node> mixedPath = new ArrayList<>();
        mixedPath.add(new Node(1, false, "Regular", false, false, false, 1.0, 0, 24));
        mixedPath.add(new Node(2, false, "Toll Road", true, false, false, 2.0, 0, 24));
        mixedPath.add(new Node(3, false, "Highway", false, true, false, 0.8, 0, 24));
        mixedPath.add(new Node(4, false, "High Risk", false, false, true, 3.0, 0, 24));
        
        PathResult mixedResult = new PathResult(mixedPath);
        
        assertEquals("混合类型路径长度应该正确", 4, mixedResult.getPath().size());
        assertFalse("第一个节点不应该是收费道路", mixedResult.getPath().get(0).isTollRoad());
        assertTrue("第二个节点应该是收费道路", mixedResult.getPath().get(1).isTollRoad());
        assertTrue("第三个节点应该限制重型车辆", mixedResult.getPath().get(2).isRestrictedForHeavyVehicles());
        assertTrue("第四个节点应该是高风险区域", mixedResult.getPath().get(3).isHighRiskArea());
    }

    /**
     * 测试PathResult的toString相关行为
     * 验证路径显示的完整性
     */
    @Test
    public void testPathResultDisplayBehavior() {
        // 创建包含特殊字符的路径
        List<Node> specialPath = new ArrayList<>();
        specialPath.add(new Node(0, false, "Start Point", false, false, false, 1.0, 0, 24));
        specialPath.add(new Node(-1, false, "Negative ID", false, false, false, 1.0, 0, 24));
        
        PathResult specialResult = new PathResult(specialPath);
        
        // 确保printPath能处理特殊节点ID
        try {
            specialResult.printPath();
            assertTrue("特殊节点路径printPath应该正常执行", true);
        } catch (Exception e) {
            fail("特殊节点路径printPath不应该抛出异常: " + e.getMessage());
        }
        
        // 验证路径节点能被正确访问
        assertEquals("特殊路径第一个节点ID应该正确", 0, specialResult.getPath().get(0).getNodeId());
        assertEquals("特殊路径第二个节点ID应该正确", -1, specialResult.getPath().get(1).getNodeId());
    }
}