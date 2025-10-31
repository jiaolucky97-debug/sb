package net.mooctest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ElevatorManagerTest {

    @Before
    public void setUp() throws Exception {
        resetSingleton(ElevatorManager.class);
        resetSingleton(SystemConfig.class);
        resetSingleton(Scheduler.class);
        resetSingleton(LogManager.class);
        resetSingleton(NotificationService.class);
        resetSingleton(EventBus.class);
        resetSingleton(AnalyticsEngine.class);
        resetSingleton(SecurityMonitor.class);
        resetSingleton(ThreadPoolManager.class);
        resetSingleton(MaintenanceManager.class);
    }

    @After
    public void tearDown() throws Exception {
        resetSingleton(ThreadPoolManager.class);
        resetSingleton(SecurityMonitor.class);
        resetSingleton(MaintenanceManager.class);
        resetSingleton(Scheduler.class);
        resetSingleton(NotificationService.class);
    }

    private void resetSingleton(Class<?> clazz) throws Exception {
        try {
            Field field = findField(clazz, "instance");
            Object current = field.get(null);
            if (current instanceof ThreadPoolManager) {
                ((ThreadPoolManager) current).shutdown();
            } else if (current != null) {
                try {
                    ExecutorService executor = (ExecutorService) findField(current.getClass(), "executorService").get(current);
                    executor.shutdownNow();
                } catch (Exception ignored) {
                    // ignore when the field does not exist
                }
            }
            field.set(null, null);
        } catch (NoSuchFieldException ignored) {
            // ignore when singleton field is absent
        }
    }

    private Field findField(Class<?> type, String name) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                Field field = current.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    @Test(timeout = 4000)
    public void testElevatorManagerSingleton() {
        // 验证单例模式返回同一个实例
        ElevatorManager first = ElevatorManager.getInstance();
        ElevatorManager second = ElevatorManager.getInstance();
        assertSame(first, second);
    }

    @Test(timeout = 4000)
    public void testElevatorManagerRegisterAndRetrieve() {
        // 验证电梯注册后能够被正确获取
        ElevatorManager manager = ElevatorManager.getInstance();
        Elevator elevator = new Elevator(1, null);
        manager.registerElevator(elevator);
        assertSame(elevator, manager.getElevatorById(1));
        Collection<Elevator> all = manager.getAllElevators();
        assertTrue(all.contains(elevator));
    }

    @Test(timeout = 4000)
    public void testElevatorManagerRegisterOverwrite() {
        // 验证重复注册同一编号会覆盖旧的电梯引用
        ElevatorManager manager = ElevatorManager.getInstance();
        Elevator first = new Elevator(1, null);
        Elevator second = new Elevator(1, null);
        manager.registerElevator(first);
        manager.registerElevator(second);
        assertSame(second, manager.getElevatorById(1));
        assertEquals(1, manager.getAllElevators().size());
    }

    @Test(timeout = 4000)
    public void testSystemConfigDefaultsAndSetters() {
        // 验证系统配置的默认值与正常设置逻辑
        SystemConfig config = SystemConfig.getInstance();
        assertEquals(20, config.getFloorCount());
        assertEquals(4, config.getElevatorCount());
        assertEquals(800.0, config.getMaxLoad(), 0.001);

        config.setFloorCount(25);
        config.setElevatorCount(6);
        config.setMaxLoad(900.0);

        assertEquals(25, config.getFloorCount());
        assertEquals(6, config.getElevatorCount());
        assertEquals(900.0, config.getMaxLoad(), 0.001);
    }

    @Test(timeout = 4000)
    public void testSystemConfigInvalidSetters() {
        // 验证非法参数不会覆盖已有配置
        SystemConfig config = SystemConfig.getInstance();
        config.setFloorCount(10);
        config.setElevatorCount(5);
        config.setMaxLoad(700.0);

        config.setFloorCount(0);
        config.setElevatorCount(-1);
        config.setMaxLoad(-10.0);

        assertEquals(10, config.getFloorCount());
        assertEquals(5, config.getElevatorCount());
        assertEquals(700.0, config.getMaxLoad(), 0.001);
    }

    @Test(timeout = 4000)
    public void testPassengerRequestDirectionAndToString() {
        // 验证乘客请求的方向推断与文本描述
        PassengerRequest upRequest = new PassengerRequest(1, 5, Priority.HIGH, RequestType.STANDARD);
        PassengerRequest downRequest = new PassengerRequest(8, 3, Priority.LOW, RequestType.DESTINATION_CONTROL);

        assertEquals(Direction.UP, upRequest.getDirection());
        assertEquals(Direction.DOWN, downRequest.getDirection());
        assertTrue(upRequest.toString().contains("From 1 to 5"));
        assertTrue(downRequest.toString().contains("Priority: LOW"));
    }

    @Test(timeout = 4000)
    public void testFloorRequestLifecycle() {
        // 验证楼层请求入队与出队流程
        Floor floor = new Floor(3);
        PassengerRequest request = new PassengerRequest(3, 6, Priority.MEDIUM, RequestType.STANDARD);
        floor.addRequest(request);
        List<PassengerRequest> requests = floor.getRequests(Direction.UP);
        assertEquals(1, requests.size());
        assertEquals(request, requests.get(0));
        assertTrue(floor.getRequests(Direction.UP).isEmpty());
    }

    @Test(timeout = 4000)
    public void testEventBusPublish() {
        // 验证事件总线能够正确通知监听器
        EventBus bus = EventBus.getInstance();
        AtomicBoolean triggered = new AtomicBoolean(false);
        bus.subscribe(EventType.EMERGENCY, event -> {
            triggered.set(true);
            assertEquals("ALERT", event.getData());
        });
        bus.publish(new EventBus.Event(EventType.EMERGENCY, "ALERT"));
        assertTrue(triggered.get());
    }

    @Test(timeout = 4000)
    public void testNotificationServiceChannelSelection() {
        // 验证紧急通知会同时触发短信与邮件渠道
        NotificationService service = NotificationService.getInstance();
        NotificationService.Notification notification = new NotificationService.Notification(
                NotificationService.NotificationType.EMERGENCY,
                "紧急情况",
                Arrays.asList("10086")
        );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            service.sendNotification(notification);
        } finally {
            System.setOut(original);
        }
        String output = out.toString();
        assertTrue(output.contains("Sending SMS notification"));
        assertTrue(output.contains("Sending email notification"));
    }

    @Test(timeout = 4000)
    public void testNotificationServiceEmailOnly() {
        // 验证普通通知仅发送邮件渠道
        NotificationService service = NotificationService.getInstance();
        NotificationService.Notification notification = new NotificationService.Notification(
                NotificationService.NotificationType.INFORMATION,
                "系统公告",
                Arrays.asList("user@example.com")
        );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            service.sendNotification(notification);
        } finally {
            System.setOut(original);
        }
        String output = out.toString();
        assertFalse(output.contains("SMS notification"));
        assertTrue(output.contains("email notification"));
    }

    @Test(timeout = 4000)
    public void testLogManagerRecordingAndQuery() {
        // 验证日志记录与查询条件过滤
        LogManager manager = LogManager.getInstance();
        long now = System.currentTimeMillis();
        manager.recordElevatorEvent(1, "Door opened");
        manager.recordSchedulerEvent("Dispatch started");
        manager.recordEvent("SecurityMonitor", "Handled emergency");

        List<LogManager.SystemLog> logs = manager.queryLogs("SecurityMonitor", now - 1000, now + 1000);
        assertEquals(1, logs.size());
        assertEquals("Handled emergency", logs.get(0).getMessage());
    }

    @Test(timeout = 4000)
    public void testAnalyticsEnginePeakHours() throws Exception {
        // 验证统计引擎高峰期判断逻辑
        AnalyticsEngine engine = AnalyticsEngine.getInstance();
        ElevatorStatusReport report = new ElevatorStatusReport(1, 5, Direction.UP, ElevatorStatus.MOVING, 1.2, 200.0, 4);
        engine.processStatusReport(report);
        Field field = findField(AnalyticsEngine.class, "statusReports");
        List<?> reports = (List<?>) field.get(engine);
        assertEquals(1, reports.size());

        engine.updateFloorPassengerCount(1, 30);
        engine.updateFloorPassengerCount(2, 25);
        assertTrue(engine.isPeakHours());

        engine.updateFloorPassengerCount(1, 10);
        engine.updateFloorPassengerCount(2, 5);
        assertFalse(engine.isPeakHours());
    }

    @Test(timeout = 4000)
    public void testAnalyticsEngineReport() {
        // 验证统计报告生成内容
        AnalyticsEngine engine = AnalyticsEngine.getInstance();
        AnalyticsEngine.Report report = engine.generatePerformanceReport();
        assertEquals("System Performance Report", report.getTitle());
        assertTrue(report.getGeneratedTime() > 0);
    }

    @Test(timeout = 4000)
    public void testElevatorLoadPassengersRespectsMaxLoad() {
        // 验证装载乘客时会遵守最大载荷限制
        SystemConfig config = SystemConfig.getInstance();
        config.setMaxLoad(140.0);
        StubScheduler scheduler = new StubScheduler();
        Elevator elevator = new Elevator(1, scheduler);
        scheduler.attach(elevator);
        List<PassengerRequest> requests = Arrays.asList(
                new PassengerRequest(1, 5, Priority.HIGH, RequestType.STANDARD),
                new PassengerRequest(1, 6, Priority.MEDIUM, RequestType.STANDARD),
                new PassengerRequest(1, 7, Priority.LOW, RequestType.STANDARD)
        );
        scheduler.setRequests(1, Direction.UP, requests);
        elevator.setCurrentFloor(1);
        elevator.setDirection(Direction.UP);
        elevator.setStatus(ElevatorStatus.IDLE);
        elevator.loadPassengers();

        assertEquals(2, elevator.getPassengerList().size());
        Set<Integer> destinations = elevator.getDestinationSet();
        assertTrue(destinations.contains(5));
        assertTrue(destinations.contains(6));
        assertFalse(destinations.contains(7));
        assertEquals(140.0, elevator.getCurrentLoad(), 0.001);
    }

    @Test(timeout = 4000)
    public void testElevatorOpenDoorFlow() throws InterruptedException {
        // 验证开门流程包括卸客与再次装客的完整逻辑
        SystemConfig config = SystemConfig.getInstance();
        config.setMaxLoad(210.0);
        StubScheduler scheduler = new StubScheduler();
        Elevator elevator = new Elevator(2, scheduler);
        scheduler.attach(elevator);
        scheduler.setRequests(1, Direction.UP, Arrays.asList(
                new PassengerRequest(1, 3, Priority.HIGH, RequestType.STANDARD)
        ));
        elevator.setCurrentFloor(1);
        elevator.setDirection(Direction.UP);
        elevator.loadPassengers();
        scheduler.setRequests(3, Direction.UP, Arrays.asList(
                new PassengerRequest(3, 4, Priority.MEDIUM, RequestType.STANDARD)
        ));
        elevator.setCurrentFloor(3);
        elevator.openDoor();
        assertEquals(ElevatorStatus.STOPPED, elevator.getStatus());
        assertEquals(1, elevator.getPassengerList().size());
        assertTrue(elevator.getDestinationSet().contains(4));
        assertEquals(70.0, elevator.getCurrentLoad(), 0.001);
    }

    @Test(timeout = 4000)
    public void testElevatorUpdateDirectionBranches() {
        // 验证方向更新时的分支逻辑
        Elevator elevator = new Elevator(3, null);
        elevator.setStatus(ElevatorStatus.MOVING);
        elevator.updateDirection();
        assertEquals(ElevatorStatus.IDLE, elevator.getStatus());

        elevator.getDestinationSet().add(10);
        elevator.setCurrentFloor(1);
        elevator.updateDirection();
        assertEquals(Direction.UP, elevator.getDirection());

        elevator.getDestinationSet().add(0);
        elevator.setCurrentFloor(5);
        elevator.updateDirection();
        assertEquals(Direction.DOWN, elevator.getDirection());
    }

    @Test(timeout = 4000)
    public void testElevatorHandleEmergencyClearsState() {
        // 验证紧急处理会清空状态并通知观察者
        SystemConfig config = SystemConfig.getInstance();
        config.setMaxLoad(210.0);
        StubScheduler scheduler = new StubScheduler();
        EmergencyAwareElevator elevator = new EmergencyAwareElevator(4, scheduler);
        scheduler.attach(elevator);
        scheduler.setRequests(1, Direction.UP, Arrays.asList(
                new PassengerRequest(1, 5, Priority.HIGH, RequestType.STANDARD)
        ));
        elevator.setCurrentFloor(1);
        elevator.setDirection(Direction.UP);
        elevator.loadPassengers();

        elevator.handleEmergency();

        assertEquals(ElevatorStatus.EMERGENCY, elevator.getStatus());
        assertTrue(elevator.getPassengerList().isEmpty());
        assertEquals(1, elevator.getDestinationSet().size());
        assertTrue(elevator.getDestinationSet().contains(1));
        assertEquals(ElevatorStatus.EMERGENCY, elevator.getLastNotified());
    }

    @Test(timeout = 4000)
    public void testElevatorClearAllRequests() {
        // 验证清空请求时会返回原有乘客列表
        SystemConfig config = SystemConfig.getInstance();
        config.setMaxLoad(210.0);
        StubScheduler scheduler = new StubScheduler();
        Elevator elevator = new Elevator(5, scheduler);
        scheduler.attach(elevator);
        scheduler.setRequests(2, Direction.UP, Arrays.asList(
                new PassengerRequest(2, 7, Priority.MEDIUM, RequestType.STANDARD)
        ));
        elevator.setCurrentFloor(2);
        elevator.setDirection(Direction.UP);
        elevator.loadPassengers();
        List<PassengerRequest> pending = elevator.clearAllRequests();
        assertEquals(1, pending.size());
        assertTrue(elevator.getPassengerList().isEmpty());
        assertTrue(elevator.getDestinationSet().isEmpty());
    }

    @Test(timeout = 4000)
    public void testNearestElevatorStrategySelection() {
        // 验证最近电梯策略能够筛选合适电梯
        NearestElevatorStrategy strategy = new NearestElevatorStrategy();
        PassengerRequest request = new PassengerRequest(5, 8, Priority.HIGH, RequestType.STANDARD);
        Elevator idleElevator = new Elevator(6, null);
        idleElevator.setCurrentFloor(2);
        idleElevator.setStatus(ElevatorStatus.IDLE);

        Elevator movingWrongDirection = new Elevator(7, null);
        movingWrongDirection.setCurrentFloor(4);
        movingWrongDirection.setStatus(ElevatorStatus.MOVING);
        movingWrongDirection.setDirection(Direction.DOWN);

        Elevator movingCorrectDirection = new Elevator(8, null);
        movingCorrectDirection.setCurrentFloor(3);
        movingCorrectDirection.setStatus(ElevatorStatus.MOVING);
        movingCorrectDirection.setDirection(Direction.UP);

        List<Elevator> elevators = Arrays.asList(idleElevator, movingWrongDirection, movingCorrectDirection);
        assertTrue(strategy.isEligible(idleElevator, request));
        assertFalse(strategy.isEligible(movingWrongDirection, request));
        assertTrue(strategy.isEligible(movingCorrectDirection, request));

        Elevator selected = strategy.selectElevator(elevators, request);
        assertSame(movingCorrectDirection, selected);
    }

    @Test(timeout = 4000)
    public void testHighEfficiencyStrategySelection() {
        // 验证高效策略根据距离选择最佳电梯
        HighEfficiencyStrategy strategy = new HighEfficiencyStrategy();
        PassengerRequest request = new PassengerRequest(5, 1, Priority.MEDIUM, RequestType.STANDARD);
        Elevator idleElevator = new Elevator(9, null);
        idleElevator.setCurrentFloor(9);
        idleElevator.setStatus(ElevatorStatus.IDLE);

        Elevator movingSameDirection = new Elevator(10, null);
        movingSameDirection.setCurrentFloor(6);
        movingSameDirection.setStatus(ElevatorStatus.MOVING);
        movingSameDirection.setDirection(Direction.DOWN);

        assertTrue(strategy.isCloser(movingSameDirection, idleElevator, request));
        Elevator selected = strategy.selectElevator(Arrays.asList(idleElevator, movingSameDirection), request);
        assertSame(movingSameDirection, selected);
    }

    @Test(timeout = 4000)
    public void testEnergySavingStrategySelection() {
        // 验证节能策略优先空闲电梯并处理备选情况
        EnergySavingStrategy strategy = new EnergySavingStrategy();
        PassengerRequest request = new PassengerRequest(4, 7, Priority.LOW, RequestType.STANDARD);

        StrategyElevator idleElevator = new StrategyElevator(11, ElevatorStatus.IDLE, Direction.UP, 1);
        StrategyElevator movingCandidate = new StrategyElevator(12, ElevatorStatus.MOVING, Direction.UP, 2);
        StrategyElevator farElevator = new StrategyElevator(13, ElevatorStatus.MOVING, Direction.UP, 10);

        List<Elevator> elevators = new ArrayList<>();
        elevators.add(idleElevator);
        elevators.add(movingCandidate);
        elevators.add(farElevator);
        Elevator result = strategy.selectElevator(elevators, request);
        assertSame(idleElevator, result);

        idleElevator.setStatus(ElevatorStatus.MOVING);
        List<Elevator> secondGroup = new ArrayList<>();
        secondGroup.add(idleElevator);
        secondGroup.add(movingCandidate);
        result = strategy.selectElevator(secondGroup, request);
        assertSame(movingCandidate, result);

        List<Elevator> finalGroup = new ArrayList<>();
        finalGroup.add(farElevator);
        assertNull(strategy.selectElevator(finalGroup, request));
    }

    @Test(timeout = 4000)
    public void testPredictiveSchedulingStrategySelection() {
        // 验证预测调度策略根据成本选择电梯
        PredictiveSchedulingStrategy strategy = new PredictiveSchedulingStrategy();
        PassengerRequest request = new PassengerRequest(3, 9, Priority.HIGH, RequestType.STANDARD);
        Elevator closeElevator = new Elevator(14, null);
        closeElevator.setCurrentFloor(2);
        Elevator farElevator = new Elevator(15, null);
        farElevator.setCurrentFloor(10);

        Elevator chosen = strategy.selectElevator(Arrays.asList(closeElevator, farElevator), request);
        assertSame(closeElevator, chosen);
        double cost = strategy.calculatePredictedCost(closeElevator, request);
        assertEquals(1.0, cost, 0.001);
    }

    @Test(timeout = 4000)
    public void testSchedulerSubmitAndDispatch() throws Exception {
        // 验证调度器提交请求后会派发电梯
        Elevator elevator = new Elevator(16, null);
        List<Elevator> elevatorList = new ArrayList<>();
        elevatorList.add(elevator);
        DispatchStrategy strategy = (elevators, request) -> elevator;
        Scheduler scheduler = new Scheduler(elevatorList, 10, strategy);
        PassengerRequest high = new PassengerRequest(2, 6, Priority.HIGH, RequestType.STANDARD);
        scheduler.submitRequest(high);
        Field highPriorityField = findField(Scheduler.class, "highPriorityQueue");
        Queue<?> queue = (Queue<?>) highPriorityField.get(scheduler);
        assertEquals(1, queue.size());
        assertTrue(elevator.getDestinationSet().contains(high.getStartFloor()));

        PassengerRequest normal = new PassengerRequest(4, 1, Priority.LOW, RequestType.STANDARD);
        scheduler.submitRequest(normal);
        List<PassengerRequest> floorRequests = scheduler.getRequestsAtFloor(4, Direction.DOWN);
        assertEquals(1, floorRequests.size());
    }

    @Test(timeout = 4000)
    public void testSchedulerUpdateHandling() throws Exception {
        // 验证调度器针对故障与紧急事件的处理逻辑
        StubElevator elevator = new StubElevator(17);
        elevator.setPending(Arrays.asList(
                new PassengerRequest(5, 8, Priority.MEDIUM, RequestType.STANDARD),
                new PassengerRequest(6, 9, Priority.LOW, RequestType.STANDARD)
        ));
        List<Elevator> elevators = new ArrayList<>();
        elevators.add(elevator);
        RecordingScheduler scheduler = new RecordingScheduler(elevators);
        scheduler.update(elevator, new Event(EventType.ELEVATOR_FAULT, null));
        assertEquals(2, scheduler.getDispatched().size());

        scheduler.update(elevator, new Event(EventType.EMERGENCY, null));
        assertTrue(scheduler.isEmergencyTriggered());
        assertTrue(elevator.isEmergencyHandled());
    }

    @Test(timeout = 4000)
    public void testSchedulerExecuteEmergencyProtocol() {
        // 验证紧急协议会通知所有电梯
        StubElevator elevator = new StubElevator(18);
        List<Elevator> elevators = new ArrayList<>();
        elevators.add(elevator);
        RecordingScheduler scheduler = new RecordingScheduler(elevators);
        scheduler.executeEmergencyProtocol();
        assertTrue(elevator.isEmergencyHandled());
    }

    @Test(timeout = 4000)
    public void testMaintenanceManagerOnEventAndRecords() throws Exception {
        // 验证维修管理在接收到故障事件后能够记录任务
        TestMaintenanceManager manager = new TestMaintenanceManager();
        Elevator elevator = new Elevator(19, null);
        manager.onEvent(new EventBus.Event(EventType.ELEVATOR_FAULT, elevator));
        Queue<?> taskQueue = (Queue<?>) findField(MaintenanceManager.class, "taskQueue").get(manager);
        assertEquals(1, taskQueue.size());

        manager.recordMaintenanceResult(19, "完成");
        List<?> records = (List<?>) findField(MaintenanceManager.class, "maintenanceRecords").get(manager);
        assertEquals(1, records.size());
        MaintenanceManager.MaintenanceRecord record = (MaintenanceManager.MaintenanceRecord) records.get(0);
        assertEquals(19, record.getElevatorId());
        assertEquals("完成", record.getResult());

        ExecutorService executor = (ExecutorService) findField(MaintenanceManager.class, "executorService").get(manager);
        executor.shutdownNow();
    }

    @Test(timeout = 4000)
    public void testElevatorStatusReportToString() {
        // 验证状态报告的属性与文本内容
        ElevatorStatusReport report = new ElevatorStatusReport(20, 10, Direction.DOWN, ElevatorStatus.STOPPED, 0.8, 180.0, 3);
        assertEquals(20, report.getElevatorId());
        assertEquals(10, report.getCurrentFloor());
        assertEquals(Direction.DOWN, report.getDirection());
        assertTrue(report.toString().contains("elevatorId=20"));
    }

    @Test(timeout = 4000)
    public void testEventDataAccess() {
        // 验证事件对象能够正确返回类型与数据
        Event event = new Event(EventType.CONFIG_UPDATED, "配置已更新");
        assertEquals(EventType.CONFIG_UPDATED, event.getType());
        assertEquals("配置已更新", event.getData());
    }

    @Test(timeout = 4000)
    public void testThreadPoolManagerSubmitAndShutdown() throws Exception {
        // 验证线程池管理器能够执行任务并干净关闭
        ThreadPoolManager manager = ThreadPoolManager.getInstance();
        CountDownLatch latch = new CountDownLatch(1);
        manager.submitTask(latch::countDown);
        assertTrue(latch.await(2, TimeUnit.SECONDS));
        manager.shutdown();
        resetSingleton(ThreadPoolManager.class);
    }

    @Test(timeout = 4000)
    public void testSecurityMonitorHandleEmergency() throws Exception {
        // 验证安全监控在紧急情况时的完整流程
        List<Elevator> elevatorList = new ArrayList<>();
        StubElevator elevator = new StubElevator(21);
        elevatorList.add(elevator);
        RecordingScheduler scheduler = new RecordingScheduler(elevatorList);
        Field schedulerInstance = findField(Scheduler.class, "instance");
        schedulerInstance.set(null, scheduler);

        SecurityMonitor monitor = SecurityMonitor.getInstance();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            monitor.handleEmergency("火灾");
        } finally {
            System.setOut(original);
        }
        List<?> events = (List<?>) findField(SecurityMonitor.class, "securityEvents").get(monitor);
        assertEquals(1, events.size());
        SecurityMonitor.SecurityEvent event = (SecurityMonitor.SecurityEvent) events.get(0);
        assertEquals("Emergency situation", event.getDescription());
        assertEquals("火灾", event.getData());

        LogManager logManager = LogManager.getInstance();
        List<LogManager.SystemLog> logs = logManager.queryLogs("SecurityMonitor", System.currentTimeMillis() - 5000, System.currentTimeMillis() + 5000);
        assertFalse(logs.isEmpty());
        assertTrue(out.toString().contains("Emergency situation detected"));
        assertTrue(scheduler.isEmergencyTriggered());
        assertTrue(elevator.isEmergencyHandled());

        ExecutorService executor = (ExecutorService) findField(SecurityMonitor.class, "executorService").get(monitor);
        executor.shutdownNow();
    }

    private static class StrategyElevator extends Elevator {
        private ElevatorStatus status;
        private Direction direction;
        private int floor;

        StrategyElevator(int id, ElevatorStatus status, Direction direction, int floor) {
            super(id, null);
            this.status = status;
            this.direction = direction;
            this.floor = floor;
            super.setStatus(status);
            super.setDirection(direction);
            super.setCurrentFloor(floor);
        }

        @Override
        public ElevatorStatus getStatus() {
            return status;
        }

        @Override
        public Direction getDirection() {
            return direction;
        }

        @Override
        public int getCurrentFloor() {
            return floor;
        }

        @Override
        public void setStatus(ElevatorStatus status) {
            super.setStatus(status);
            this.status = status;
        }

        @Override
        public void setDirection(Direction direction) {
            super.setDirection(direction);
            this.direction = direction;
        }

        @Override
        public void setCurrentFloor(int currentFloor) {
            super.setCurrentFloor(currentFloor);
            this.floor = currentFloor;
        }
    }

    private static class EmergencyAwareElevator extends Elevator {
        private Object lastNotified;

        EmergencyAwareElevator(int id, Scheduler scheduler) {
            super(id, scheduler);
        }

        @Override
        public void notifyObservers(Object arg) {
            lastNotified = arg;
        }

        Object getLastNotified() {
            return lastNotified;
        }
    }

    private static class StubScheduler extends Scheduler {
        private final List<Elevator> elevators;
        private final Map<Integer, Map<Direction, List<PassengerRequest>>> presets = new HashMap<>();

        StubScheduler() {
            this(new ArrayList<>(), 20);
        }

        StubScheduler(List<Elevator> elevators, int floors) {
            super(elevators, floors, new NearestElevatorStrategy());
            this.elevators = elevators;
        }

        void attach(Elevator elevator) {
            this.elevators.add(elevator);
        }

        void setRequests(int floor, Direction direction, List<PassengerRequest> requests) {
            Map<Direction, List<PassengerRequest>> value = presets.computeIfAbsent(floor, k -> new EnumMap<Direction, List<PassengerRequest>>(Direction.class));
            value.put(direction, new ArrayList<>(requests));
        }

        @Override
        public List<PassengerRequest> getRequestsAtFloor(int floorNumber, Direction direction) {
            Map<Direction, List<PassengerRequest>> value = presets.get(floorNumber);
            if (value == null || !value.containsKey(direction)) {
                return new ArrayList<>();
            }
            List<PassengerRequest> result = new ArrayList<>(value.remove(direction));
            if (value.isEmpty()) {
                presets.remove(floorNumber);
            }
            return result;
        }

        @Override
        public void dispatchElevator(PassengerRequest request) {
            // 测试环境下无需真正调度
        }
    }

    private static class StubElevator extends Elevator {
        private List<PassengerRequest> pending = new ArrayList<>();
        private boolean emergencyHandled;

        StubElevator(int id) {
            super(id, null);
        }

        void setPending(List<PassengerRequest> requests) {
            this.pending = new ArrayList<>(requests);
        }

        @Override
        public List<PassengerRequest> clearAllRequests() {
            return new ArrayList<>(pending);
        }

        @Override
        public void handleEmergency() {
            this.emergencyHandled = true;
        }

        boolean isEmergencyHandled() {
            return emergencyHandled;
        }
    }

    private static class RecordingScheduler extends Scheduler {
        private final List<PassengerRequest> dispatched = new ArrayList<>();
        private final List<Elevator> elevators;
        private boolean emergencyTriggered;

        RecordingScheduler(List<Elevator> elevators) {
            super(elevators, 10, new NearestElevatorStrategy());
            this.elevators = elevators;
        }

        @Override
        public void dispatchElevator(PassengerRequest request) {
            dispatched.add(request);
        }

        @Override
        public void executeEmergencyProtocol() {
            emergencyTriggered = true;
            for (Elevator elevator : elevators) {
                elevator.handleEmergency();
            }
        }

        List<PassengerRequest> getDispatched() {
            return dispatched;
        }

        boolean isEmergencyTriggered() {
            return emergencyTriggered;
        }
    }

    private static class TestMaintenanceManager extends MaintenanceManager {
        @Override
        public void processTasks() {
            // 覆盖父类逻辑以避免后台线程无限循环
        }
    }
}
