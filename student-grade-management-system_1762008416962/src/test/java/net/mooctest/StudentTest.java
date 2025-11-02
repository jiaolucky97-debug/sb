package net.mooctest;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class StudentTest {

    private InMemoryStudentRepository studentRepository;
    private InMemoryCourseRepository courseRepository;
    private InMemoryEnrollmentRepository enrollmentRepository;
    private GradingPolicy gradingPolicy;
    private GradeService gradeService;
    private EnrollmentService enrollmentService;
    private ReportService reportService;
    private Student student;
    private Course course;

    @Before
    public void setUp() {
        studentRepository = new InMemoryStudentRepository();
        courseRepository = new InMemoryCourseRepository();
        enrollmentRepository = new InMemoryEnrollmentRepository();
        Map<GradeComponentType, GradeComponent> components = new EnumMap<>(GradeComponentType.class);
        components.put(GradeComponentType.ASSIGNMENT, new GradeComponent(GradeComponentType.ASSIGNMENT, 0.4));
        components.put(GradeComponentType.MIDTERM, new GradeComponent(GradeComponentType.MIDTERM, 0.2));
        components.put(GradeComponentType.FINAL, new GradeComponent(GradeComponentType.FINAL, 0.4));
        gradingPolicy = new GradingPolicy(components);
        gradeService = new GradeService(enrollmentRepository, gradingPolicy);
        enrollmentService = new EnrollmentService(studentRepository, courseRepository, enrollmentRepository, gradingPolicy);
        reportService = new ReportService(studentRepository, courseRepository, enrollmentRepository, gradingPolicy, gradeService);
        student = studentRepository.save(new Student("Alice", LocalDate.of(2000, 1, 1)));
        course = courseRepository.save(new Course("CS101", "Intro", 3));
    }

    @Test
    public void testStudentConstructionAndUpdates() {
        // 验证学生对象在构造和属性修改时能正确处理合法数据
        Student s = new Student(" Bob ", LocalDate.of(1999, 5, 5));
        assertEquals("Bob", s.getName());
        s.setName("  Carol  ");
        assertEquals("Carol", s.getName());
        s.setDateOfBirth(LocalDate.of(1998, 6, 1));
        assertEquals(LocalDate.of(1998, 6, 1), s.getDateOfBirth());
        assertEquals(s, s);
        Student other = new Student("Bob", LocalDate.of(1999, 5, 5));
        assertNotEquals(s.getId(), other.getId());
        assertNotEquals(s, other);
    }

    @Test(expected = ValidationException.class)
    public void testStudentConstructorRejectsBlankName() {
        // 验证学生构造方法会拒绝空白姓名
        new Student(" ", LocalDate.now());
    }

    @Test
    public void testStudentConstructorRejectsFutureDate() {
        // 验证学生构造方法会拒绝未来的出生日期
        try {
            new Student("Alice", LocalDate.now().plusDays(1));
            fail("应当抛出校验异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("past or present"));
        }
    }

    @Test
    public void testStudentSetterValidation() {
        // 验证学生的setter方法在遇到非法参数时能抛出异常
        Student s = new Student("Alice", LocalDate.now());
        try {
            s.setName("\t\n");
            fail("名称为空白时应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("name"));
        }
        try {
            s.setDateOfBirth(null);
            fail("日期为空时应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("dateOfBirth"));
        }
    }

    @Test
    public void testCourseConstructionAndMutations() {
        // 验证课程对象的构造和属性修改逻辑
        Course c = new Course(" cs102 ", " Data ", 4);
        assertEquals("CS102", c.getCode());
        c.setCode("math101");
        assertEquals("MATH101", c.getCode());
        c.setTitle(" Linear Algebra ");
        assertEquals("Linear Algebra", c.getTitle());
        c.setCreditHours(5);
        assertEquals(5, c.getCreditHours());
        assertEquals(c, c);
        Course other = new Course("CS102", "Data", 4);
        assertNotEquals(c, other);
    }

    @Test
    public void testCourseValidation() {
        // 验证课程在遇到非法参数时的校验逻辑
        try {
            new Course("", "Title", 3);
            fail("空代码应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("code"));
        }
        try {
            new Course("CS", "", 3);
            fail("空标题应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("title"));
        }
        try {
            new Course("CS", "Title", 0);
            fail("学分非正应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("creditHours"));
        }
    }

    @Test
    public void testGradeComponentMutations() {
        // 验证成绩组件的类型与权重调整逻辑
        GradeComponent component = new GradeComponent(GradeComponentType.ASSIGNMENT, 0.5);
        component.setType(GradeComponentType.QUIZ);
        assertEquals(GradeComponentType.QUIZ, component.getType());
        component.setWeight(0.75);
        assertEquals(0.75, component.getWeight(), 1e-9);
        GradeComponent sameType = new GradeComponent(GradeComponentType.QUIZ, 0.1);
        assertEquals(component, sameType);
        GradeComponent different = new GradeComponent(GradeComponentType.FINAL, 0.1);
        assertNotEquals(component, different);
    }

    @Test
    public void testGradeComponentValidation() {
        // 验证成绩组件在遇到非法数据时的防御性检查
        try {
            new GradeComponent(null, 0.5);
            fail("类型为空应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("type"));
        }
        try {
            new GradeComponent(GradeComponentType.ASSIGNMENT, 1.1);
            fail("权重越界应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("weight"));
        }
        GradeComponent component = new GradeComponent(GradeComponentType.ASSIGNMENT, 0.3);
        try {
            component.setType(null);
            fail("设置空类型应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("type"));
        }
    }

    @Test
    public void testGradeRecordOperations() {
        // 验证成绩记录的构造和分数更新逻辑
        GradeRecord record = new GradeRecord(GradeComponentType.MIDTERM, 80);
        assertEquals(GradeComponentType.MIDTERM, record.getComponentType());
        assertEquals(80, record.getScore(), 1e-9);
        record.setScore(95);
        assertEquals(95, record.getScore(), 1e-9);
    }

    @Test
    public void testGradeRecordValidation() {
        // 验证成绩记录在遇到非法参数时的异常处理
        try {
            new GradeRecord(null, 80);
            fail("空类型应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("componentType"));
        }
        try {
            new GradeRecord(GradeComponentType.MIDTERM, -1);
            fail("分数越界应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("score"));
        }
    }

    @Test
    public void testValidationUtilRequireNonBlank() {
        // 验证非空白字符串校验工具的行为
        ValidationUtil.requireNonBlank("ok", "field");
        try {
            ValidationUtil.requireNonBlank(" ", "field");
            fail("空白字符串应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("field"));
        }
    }

    @Test
    public void testValidationUtilNumericGuards() {
        // 验证正数和非负校验工具的边界
        ValidationUtil.requirePositive(1, "value");
        ValidationUtil.requireNonNegative(0, "value");
        try {
            ValidationUtil.requirePositive(0, "value");
            fail("非正数应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("positive"));
        }
        try {
            ValidationUtil.requireNonNegative(-0.01, "value");
            fail("负数应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("non-negative"));
        }
    }

    @Test
    public void testValidationUtilRequireBetween() {
        // 验证区间校验逻辑在上下界情况下的表现
        ValidationUtil.requireBetween(0.5, 0.0, 1.0, "ratio");
        try {
            ValidationUtil.requireBetween(1.5, 0.0, 1.0, "ratio");
            fail("超出上界应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("between"));
        }
    }

    @Test
    public void testValidationUtilRequirePastOrPresent() {
        // 验证日期必须为过去或当前的校验逻辑
        ValidationUtil.requirePastOrPresent(LocalDate.now(), "date");
        try {
            ValidationUtil.requirePastOrPresent(null, "date");
            fail("空日期应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("must not be null"));
        }
        try {
            ValidationUtil.requirePastOrPresent(LocalDate.now().plusDays(1), "date");
            fail("未来日期应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("past or present"));
        }
    }

    @Test
    public void testGradingPolicyCreation() {
        // 验证评分策略的构建以及返回的映射不可修改
        Map<GradeComponentType, GradeComponent> components = gradingPolicy.getComponents();
        assertEquals(3, components.size());
        assertTrue(components.containsKey(GradeComponentType.ASSIGNMENT));
        try {
            components.remove(GradeComponentType.ASSIGNMENT);
            fail("不允许修改只读映射");
        } catch (UnsupportedOperationException expected) {
            // ignore
        }
    }

    @Test
    public void testGradingPolicyInvalidConfiguration() {
        // 验证评分策略在配置非法数据时能抛出异常
        try {
            new GradingPolicy(new HashMap<GradeComponentType, GradeComponent>());
            fail("空映射应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("must not be empty"));
        }
        Map<GradeComponentType, GradeComponent> invalid = new EnumMap<>(GradeComponentType.class);
        invalid.put(GradeComponentType.ASSIGNMENT, new GradeComponent(GradeComponentType.ASSIGNMENT, 0.4));
        invalid.put(GradeComponentType.MIDTERM, new GradeComponent(GradeComponentType.MIDTERM, 0.4));
        try {
            new GradingPolicy(invalid);
            fail("权重和不为1应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("Total weight"));
        }
    }

    @Test
    public void testEnrollmentStatusTransitions() {
        // 验证选课状态在合法流程下的状态迁移
        Enrollment enrollment = new Enrollment(student.getId(), course.getId(), 2024, Term.SPRING);
        assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        enrollment.markIncomplete();
        assertEquals(EnrollmentStatus.INCOMPLETE, enrollment.getStatus());
        enrollment.drop();
        assertEquals(EnrollmentStatus.DROPPED, enrollment.getStatus());
    }

    @Test
    public void testEnrollmentDropForbiddenAfterCompletion() {
        // 验证已完成的选课无法退课
        Enrollment enrollment = new Enrollment(student.getId(), course.getId(), 2024, Term.SPRING);
        enrollment.complete();
        try {
            enrollment.drop();
            fail("完成后退课应抛出异常");
        } catch (DomainException ex) {
            assertTrue(ex.getMessage().contains("Cannot drop"));
        }
    }

    @Test
    public void testEnrollmentCompleteForbiddenAfterDrop() {
        // 验证已退课的记录无法标记为完成
        Enrollment enrollment = new Enrollment(student.getId(), course.getId(), 2024, Term.SPRING);
        enrollment.drop();
        try {
            enrollment.complete();
            fail("退课后完成应抛出异常");
        } catch (DomainException ex) {
            assertTrue(ex.getMessage().contains("Cannot complete"));
        }
    }

    @Test
    public void testEnrollmentMarkIncompleteFromInvalidState() {
        // 验证非在读状态下不允许标记为未完成
        Enrollment enrollment = new Enrollment(student.getId(), course.getId(), 2024, Term.SPRING);
        enrollment.drop();
        try {
            enrollment.markIncomplete();
            fail("非法状态下标记未完成应抛出异常");
        } catch (DomainException ex) {
            assertTrue(ex.getMessage().contains("Incomplete"));
        }
    }

    @Test
    public void testEnrollmentRecordGradeAndAccessors() {
        // 验证选课记录成绩后能正确计算平均分
        Enrollment enrollment = new Enrollment(student.getId(), course.getId(), 2024, Term.SPRING);
        GradeRecord record = new GradeRecord(GradeComponentType.ASSIGNMENT, 90);
        enrollment.recordGrade(record);
        Map<GradeComponentType, GradeComponent> components = gradingPolicy.getComponents();
        assertEquals(90 * 0.4 / 1.0, enrollment.getAverageScore(components), 1e-9);
        try {
            enrollment.getGradesByComponent().put(GradeComponentType.MIDTERM, record);
            fail("成绩映射应为只读");
        } catch (UnsupportedOperationException expected) {
            // ignore
        }
    }

    @Test
    public void testEnrollmentRecordGradeValidation() {
        // 验证选课记录成绩时的异常场景
        Enrollment enrollment = new Enrollment(student.getId(), course.getId(), 2024, Term.SPRING);
        try {
            enrollment.recordGrade(null);
            fail("空记录应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("record"));
        }
        enrollment.drop();
        try {
            enrollment.recordGrade(new GradeRecord(GradeComponentType.ASSIGNMENT, 80));
            fail("退课后记录成绩应抛出异常");
        } catch (DomainException ex) {
            assertTrue(ex.getMessage().contains("dropped"));
        }
    }

    @Test
    public void testEnrollmentAverageValidation() {
        // 验证计算平均分时的非法参数检测
        Enrollment enrollment = new Enrollment(student.getId(), course.getId(), 2024, Term.SPRING);
        try {
            enrollment.getAverageScore(null);
            fail("空策略应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("policyComponents"));
        }
        try {
            enrollment.getAverageScore(new HashMap<GradeComponentType, GradeComponent>());
            fail("空映射应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("must not be empty"));
        }
        Map<GradeComponentType, GradeComponent> zeroWeight = new EnumMap<>(GradeComponentType.class);
        zeroWeight.put(GradeComponentType.PROJECT, new GradeComponent(GradeComponentType.PROJECT, 0.0));
        try {
            enrollment.getAverageScore(zeroWeight);
            fail("总权重为0应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("Total weight"));
        }
    }

    @Test
    public void testStudentRepositoryOperations() {
        // 验证学生仓储的增删改查逻辑
        Student bob = studentRepository.save(new Student("Bob", LocalDate.of(1999, 3, 3)));
        assertTrue(studentRepository.findById(bob.getId()).isPresent());
        assertTrue(studentRepository.findByName("bob").isPresent());
        List<Student> all = studentRepository.findAll();
        assertTrue(all.contains(bob));
        studentRepository.deleteById(bob.getId());
        assertFalse(studentRepository.findById(bob.getId()).isPresent());
    }

    @Test
    public void testStudentRepositoryRejectNull() {
        // 验证学生仓储在保存空对象时抛出异常
        try {
            studentRepository.save(null);
            fail("空学生应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("student"));
        }
    }

    @Test
    public void testCourseRepositoryOperations() {
        // 验证课程仓储的常规操作
        Course math = courseRepository.save(new Course("MATH100", "Math", 4));
        assertTrue(courseRepository.findById(math.getId()).isPresent());
        assertTrue(courseRepository.findByCode("math100").isPresent());
        List<Course> all = courseRepository.findAll();
        assertTrue(all.contains(math));
        courseRepository.deleteById(math.getId());
        assertFalse(courseRepository.findById(math.getId()).isPresent());
    }

    @Test
    public void testCourseRepositoryRejectNull() {
        // 验证课程仓储在保存空对象时抛出异常
        try {
            courseRepository.save(null);
            fail("空课程应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("course"));
        }
    }

    @Test
    public void testEnrollmentRepositoryOperations() {
        // 验证选课仓储的查询过滤逻辑
        Enrollment e1 = new Enrollment(student.getId(), course.getId(), 2024, Term.SPRING);
        enrollmentRepository.save(e1);
        Course anotherCourse = courseRepository.save(new Course("CS202", "Algorithms", 3));
        Student anotherStudent = studentRepository.save(new Student("Chris", LocalDate.of(2001, 2, 2)));
        Enrollment e2 = new Enrollment(anotherStudent.getId(), anotherCourse.getId(), 2024, Term.FALL);
        enrollmentRepository.save(e2);
        assertTrue(enrollmentRepository.findById(e1.getId()).isPresent());
        assertEquals(1, enrollmentRepository.findByStudentId(student.getId()).size());
        assertEquals(1, enrollmentRepository.findByCourseId(course.getId()).size());
        assertEquals(2, enrollmentRepository.findAll().size());
        enrollmentRepository.deleteById(e1.getId());
        assertFalse(enrollmentRepository.findById(e1.getId()).isPresent());
    }

    @Test
    public void testEnrollmentRepositoryRejectNull() {
        // 验证选课仓储保存空对象时的异常
        try {
            enrollmentRepository.save(null);
            fail("空选课应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("enrollment"));
        }
    }

    @Test
    public void testEnrollmentServiceEnrollFlow() {
        // 验证选课服务的正常选课流程
        Enrollment enrollment = enrollmentService.enroll(student.getId(), course.getId(), 2024, Term.SPRING);
        assertEquals(student.getId(), enrollment.getStudentId());
        assertTrue(enrollmentRepository.findById(enrollment.getId()).isPresent());
    }

    @Test
    public void testEnrollmentServiceDuplicateDetection() {
        // 验证选课服务能阻止重复选课
        enrollmentService.enroll(student.getId(), course.getId(), 2024, Term.SPRING);
        try {
            enrollmentService.enroll(student.getId(), course.getId(), 2024, Term.SPRING);
            fail("重复选课应抛出异常");
        } catch (DomainException ex) {
            assertTrue(ex.getMessage().contains("already enrolled"));
        }
    }

    @Test
    public void testEnrollmentServiceDrop() {
        // 验证选课服务的退课逻辑
        Enrollment enrollment = enrollmentService.enroll(student.getId(), course.getId(), 2024, Term.SPRING);
        enrollmentService.drop(enrollment.getId());
        assertEquals(EnrollmentStatus.DROPPED, enrollmentRepository.findById(enrollment.getId()).get().getStatus());
    }

    @Test
    public void testEnrollmentServiceDropMissing() {
        // 验证退课时找不到记录的异常处理
        try {
            enrollmentService.drop("missing");
            fail("缺失记录应抛出异常");
        } catch (DomainException ex) {
            assertTrue(ex.getMessage().contains("Enrollment not found"));
        }
    }

    @Test
    public void testEnrollmentServiceComputePercentage() {
        // 验证选课服务的平均分计算
        Enrollment enrollment = enrollmentService.enroll(student.getId(), course.getId(), 2024, Term.SPRING);
        enrollment.recordGrade(new GradeRecord(GradeComponentType.ASSIGNMENT, 80));
        enrollment.recordGrade(new GradeRecord(GradeComponentType.FINAL, 100));
        enrollmentRepository.save(enrollment);
        double percentage = enrollmentService.computeEnrollmentPercentage(enrollment.getId());
        assertEquals((80 * 0.4 + 100 * 0.4) / 1.0, percentage, 1e-9);
    }

    @Test
    public void testGradeServiceRecordUpdateCompute() {
        // 验证成绩服务的录入、更新与计算流程
        Enrollment enrollment = enrollmentService.enroll(student.getId(), course.getId(), 2024, Term.SPRING);
        gradeService.recordGrade(enrollment.getId(), GradeComponentType.ASSIGNMENT, 70);
        gradeService.updateGrade(enrollment.getId(), GradeComponentType.ASSIGNMENT, 90);
        assertEquals(90, enrollmentRepository.findById(enrollment.getId()).get()
                .getGradesByComponent().get(GradeComponentType.ASSIGNMENT).getScore(), 1e-9);
        double percentage = gradeService.computePercentage(enrollment.getId());
        assertEquals(90 * 0.4, percentage, 1e-9);
        assertEquals(0.0, gradeService.computeGpa(enrollment.getId()), 1e-9);
    }

    @Test
    public void testGradeServiceEnrollmentMissing() {
        // 验证成绩服务在找不到选课记录时的异常
        try {
            gradeService.recordGrade("missing", GradeComponentType.ASSIGNMENT, 80);
            fail("缺失选课应抛出异常");
        } catch (DomainException ex) {
            assertTrue(ex.getMessage().contains("Enrollment not found"));
        }
    }

    @Test
    public void testGradeServiceMissingComponent() {
        // 验证成绩服务对不存在的评分组件进行防御
        Map<GradeComponentType, GradeComponent> simple = new EnumMap<>(GradeComponentType.class);
        simple.put(GradeComponentType.ASSIGNMENT, new GradeComponent(GradeComponentType.ASSIGNMENT, 1.0));
        GradeService simpleService = new GradeService(enrollmentRepository, new GradingPolicy(simple));
        Enrollment enrollment = enrollmentService.enroll(student.getId(), course.getId(), 2024, Term.SPRING);
        try {
            simpleService.recordGrade(enrollment.getId(), GradeComponentType.PROJECT, 80);
            fail("缺少组件应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("Component"));
        }
    }

    @Test
    public void testGradeServiceToGpaBoundaries() {
        // 验证成绩与GPA转换的各个分段边界
        double[][] cases = {
                {95, 4.0}, {93, 4.0}, {92.9, 3.7}, {90, 3.7},
                {88, 3.3}, {83, 3.0}, {82.9, 2.7}, {80, 2.7},
                {78, 2.3}, {73, 2.0}, {72.9, 1.7}, {70, 1.7},
                {68, 1.3}, {63, 1.0}, {61, 0.7}, {59.9, 0.0}
        };
        for (double[] c : cases) {
            assertEquals(c[1], gradeService.toGpa(c[0]), 1e-9);
        }
    }

    @Test
    public void testGradeServiceToGpaValidation() {
        // 验证GPA计算时非法百分比的处理
        try {
            gradeService.toGpa(-1);
            fail("百分比越下界应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("percentage"));
        }
        try {
            gradeService.toGpa(120);
            fail("百分比越上界应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("percentage"));
        }
    }

    @Test
    public void testReportServiceBuildTranscript() {
        // 验证成绩单服务能正确构建多门课程的记录
        Enrollment enrollment1 = enrollmentService.enroll(student.getId(), course.getId(), 2024, Term.SPRING);
        gradeService.recordGrade(enrollment1.getId(), GradeComponentType.ASSIGNMENT, 80);
        gradeService.recordGrade(enrollment1.getId(), GradeComponentType.FINAL, 90);
        enrollment1.complete();
        enrollmentRepository.save(enrollment1);

        Course course2 = courseRepository.save(new Course("CS102", "Data", 4));
        Enrollment enrollment2 = enrollmentService.enroll(student.getId(), course2.getId(), 2024, Term.FALL);
        enrollment2.markIncomplete();
        enrollmentRepository.save(enrollment2);

        Transcript transcript = reportService.buildTranscript(student.getId());
        assertEquals(2, transcript.getItems().size());
        Map<String, Transcript.LineItem> itemMap = new HashMap<>();
        for (Transcript.LineItem item : transcript.getItems()) {
            itemMap.put(item.getCourseCode(), item);
        }
        assertTrue(itemMap.containsKey("CS101"));
        Transcript.LineItem cs101 = itemMap.get("CS101");
        assertEquals("Intro", cs101.getCourseTitle());
        assertEquals(68.0, cs101.getPercentage(), 1e-9);
        assertEquals(1.3, cs101.getGpaPoints(), 1e-9);
    }

    @Test
    public void testReportServiceStudentMissing() {
        // 验证成绩单构建时学生不存在的情况
        try {
            reportService.buildTranscript("missing");
            fail("缺失学生应抛出异常");
        } catch (DomainException ex) {
            assertTrue(ex.getMessage().contains("Student not found"));
        }
    }

    @Test
    public void testReportServiceCourseMissing() {
        // 验证成绩单构建过程中课程缺失的异常
        Enrollment enrollment1 = enrollmentService.enroll(student.getId(), course.getId(), 2024, Term.SPRING);
        enrollment1.complete();
        enrollmentRepository.save(enrollment1);
        courseRepository.deleteById(course.getId());
        try {
            reportService.buildTranscript(student.getId());
            fail("缺失课程应抛出异常");
        } catch (DomainException ex) {
            assertTrue(ex.getMessage().contains("Course not found"));
        }
    }

    @Test
    public void testTranscriptAddAndCompute() {
        // 验证成绩单条目添加和累计绩点计算
        Transcript transcript = new Transcript();
        Transcript.LineItem item1 = new Transcript.LineItem("CS101", "Intro", 3, 90, 3.7);
        Transcript.LineItem item2 = new Transcript.LineItem("CS102", "Data", 4, 80, 2.7);
        transcript.addItem(item1);
        transcript.addItem(item2);
        assertEquals(2, transcript.getItems().size());
        double expectedGpa = (3 * 3.7 + 4 * 2.7) / 7.0;
        assertEquals(expectedGpa, transcript.computeCumulativeGpa(), 1e-9);
        try {
            transcript.getItems().add(item1);
            fail("成绩单条目列表应只读");
        } catch (UnsupportedOperationException expected) {
            // ignore
        }
    }

    @Test
    public void testTranscriptAddNull() {
        // 验证成绩单拒绝添加空条目
        Transcript transcript = new Transcript();
        try {
            transcript.addItem(null);
            fail("空条目应抛出异常");
        } catch (ValidationException ex) {
            assertTrue(ex.getMessage().contains("item"));
        }
    }

    @Test
    public void testTranscriptComputeEmpty() {
        // 验证成绩单在没有课程时的累计绩点结果
        Transcript transcript = new Transcript();
        assertEquals(0.0, transcript.computeCumulativeGpa(), 1e-9);
    }

    @Test
    public void testDomainExceptionConstructors() {
        // 验证领域异常的构造函数行为
        DomainException ex = new DomainException("msg", new IllegalStateException("cause"));
        assertEquals("msg", ex.getMessage());
        assertTrue(ex.getCause() instanceof IllegalStateException);
    }
}
