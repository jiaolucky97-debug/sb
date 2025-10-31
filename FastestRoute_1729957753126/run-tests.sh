#!/bin/bash

# FastestRoute项目测试执行脚本
# 用于运行所有测试并生成覆盖率报告

echo "=========================================="
echo "FastestRoute项目测试执行脚本"
echo "=========================================="

# 设置项目根目录
PROJECT_DIR="/home/engine/project/FastestRoute_1729957753126"
cd "$PROJECT_DIR"

echo "当前目录: $(pwd)"
echo "Java版本信息:"
java -version 2>&1 | head -3

echo ""
echo "=========================================="
echo "编译源代码..."
echo "=========================================="

# 编译主代码
if [ -d "src/main/java" ]; then
    echo "编译主源代码..."
    find src/main/java -name "*.java" > sources.txt
    javac -d target/classes -cp "target/classes:$(find . -name "*.jar" | tr '\n' ':')" @sources.txt
    if [ $? -eq 0 ]; then
        echo "✓ 主源代码编译成功"
    else
        echo "✗ 主源代码编译失败"
        exit 1
    fi
else
    echo "✗ 未找到主源代码目录"
    exit 1
fi

echo ""
echo "=========================================="
echo "编译测试代码..."
echo "=========================================="

# 编译测试代码
if [ -d "src/test/java" ]; then
    echo "编译测试代码..."
    find src/test/java -name "*.java" > test-sources.txt
    javac -d target/test-classes -cp "target/classes:target/test-classes:$(find . -name "*.jar" | tr '\n' ':')" @test-sources.txt
    if [ $? -eq 0 ]; then
        echo "✓ 测试代码编译成功"
    else
        echo "✗ 测试代码编译失败"
        exit 1
    fi
else
    echo "✗ 未找到测试源代码目录"
    exit 1
fi

echo ""
echo "=========================================="
echo "运行测试..."
echo "=========================================="

# 运行测试
if [ -d "target/test-classes" ]; then
    echo "执行所有测试类..."
    
    # 创建测试类列表
    TEST_CLASSES=""
    for test_class in $(find src/test/java -name "*Test.java" | sed 's|src/test/java/||' | sed 's|\.java||' | sed 's|/|.|g'); do
        if [ -z "$TEST_CLASSES" ]; then
            TEST_CLASSES="$test_class"
        else
            TEST_CLASSES="$TEST_CLASSES $test_class"
        fi
    done
    
    echo "测试类列表: $TEST_CLASSES"
    
    # 运行测试
    java -cp "target/classes:target/test-classes:$(find . -name "*.jar" | tr '\n' ':')" \
         org.junit.runner.JUnitCore \
         net.mooctest.AllTestsSuite
    
    if [ $? -eq 0 ]; then
        echo "✓ 所有测试执行成功"
    else
        echo "✗ 部分测试执行失败"
        exit 1
    fi
else
    echo "✗ 未找到编译后的测试类"
    exit 1
fi

echo ""
echo "=========================================="
echo "测试统计信息..."
echo "=========================================="

# 统计测试文件数量
TEST_FILE_COUNT=$(find src/test/java -name "*Test.java" | wc -l)
echo "测试文件数量: $TEST_FILE_COUNT"

# 统计业务文件数量
MAIN_FILE_COUNT=$(find src/main/java -name "*.java" | wc -l)
echo "业务文件数量: $MAIN_FILE_COUNT"

# 统计代码行数
echo "测试代码总行数: $(find src/test/java -name "*.java" -exec wc -l {} + | tail -1 | awk '{print $1}')"
echo "业务代码总行数: $(find src/main/java -name "*.java" -exec wc -l {} + | tail -1 | awk '{print $1}')"

echo ""
echo "=========================================="
echo "测试文件列表..."
echo "=========================================="

find src/test/java -name "*Test.java" | sort

echo ""
echo "=========================================="
echo "测试完成！"
echo "=========================================="
echo "测试结果总结:"
echo "- 测试文件数量: $TEST_FILE_COUNT"
echo "- 业务文件数量: $MAIN_FILE_COUNT"
echo "- 覆盖率目标: 90%以上"
echo "- 变异杀死率目标: 90%以上"
echo ""
echo "所有测试已成功执行！"
echo "如需查看详细的覆盖率报告，请使用Maven工具运行。"

# 清理临时文件
rm -f sources.txt test-sources.txt

exit 0