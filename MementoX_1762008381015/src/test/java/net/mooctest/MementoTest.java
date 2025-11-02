package net.mooctest;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

public class MementoTest {

    @Test
    public void testCalendarManagerByDayAndMonth() {
        // 测试日历管理器在按天与按月检索笔记时的行为
        CalendarManager manager = new CalendarManager();
        Note note1 = new Note("工作计划");
        Note note2 = new Note("会议纪要");
        Note note3 = new Note("度假安排");
        Date july10 = new GregorianCalendar(2024, Calendar.JULY, 10).getTime();
        Date july11 = new GregorianCalendar(2024, Calendar.JULY, 11).getTime();
        Date august1 = new GregorianCalendar(2024, Calendar.AUGUST, 1).getTime();
        manager.addNoteByDate(note1, july10);
        manager.addNoteByDate(note2, july11);
        manager.addNoteByDate(note3, august1);
        assertEquals(1, manager.getNotesByDay(july10).size());
        assertTrue(manager.getNotesByDay(july10).contains(note1));
        assertEquals(2, manager.getNotesByMonth(july10).size());
        assertTrue(manager.getNotesByMonth(july10).containsAll(Arrays.asList(note1, note2)));
        assertTrue(manager.getNotesByDay(new GregorianCalendar(2024, Calendar.DECEMBER, 1).getTime()).isEmpty());
        assertEquals(Collections.singletonList(note3), manager.getNotesByMonth(august1));
    }

    @Test
    public void testReminderStateAndDefensiveCopy() {
        // 测试提醒对象的异常分支与防御性拷贝能力
        Note note = new Note("提醒内容");
        Date now = new Date();
        CalendarManager.Reminder reminder = new CalendarManager.Reminder(note, now);
        assertSame(note, reminder.getNote());
        assertFalse(reminder.isTriggered());
        reminder.setTriggered(true);
        assertTrue(reminder.isTriggered());
        Date returned = reminder.getRemindTime();
        returned.setTime(0L);
        assertNotEquals(0L, reminder.getRemindTime().getTime());
        try {
            new CalendarManager.Reminder(null, now);
            fail("应当因为笔记为空而抛出异常");
        } catch (IllegalArgumentException expected) {
            assertEquals("null arg", expected.getMessage());
        }
        try {
            new CalendarManager.Reminder(note, null);
            fail("应当因为时间为空而抛出异常");
        } catch (IllegalArgumentException expected) {
            assertEquals("null arg", expected.getMessage());
        }
    }

    @Test
    public void testCaretakerUndoRedoFlow() throws MementoException {
        // 测试备忘录看护人保存、撤销、重做及分支截断
        Caretaker caretaker = new Caretaker();
        NoteMemento m1 = new NoteMemento("初稿");
        NoteMemento m2 = new NoteMemento("二稿");
        NoteMemento m3 = new NoteMemento("三稿");
        caretaker.save(m1);
        caretaker.save(m2);
        caretaker.save(m3);
        assertEquals("三稿", ((NoteMemento) caretaker.getCurrent()).getState());
        NoteMemento undoResult = (NoteMemento) caretaker.undo();
        assertEquals("二稿", undoResult.getState());
        NoteMemento redoResult = (NoteMemento) caretaker.redo();
        assertEquals("三稿", redoResult.getState());
        caretaker.undo();
        caretaker.save(new NoteMemento("二稿修订"));
        try {
            caretaker.redo();
            fail("重新保存后不应再允许重做");
        } catch (MementoException expected) {
            assertTrue(expected.getMessage().contains("Cannot redo"));
        }
    }

    @Test
    public void testCaretakerExceptionPaths() {
        // 测试备忘录看护人在边界条件下抛出的异常
        Caretaker caretaker = new Caretaker();
        try {
            caretaker.undo();
            fail("空历史不应允许撤销");
        } catch (MementoException expected) {
            assertTrue(expected.getMessage().contains("Cannot undo"));
        }
        try {
            caretaker.getCurrent();
            fail("没有历史时不应存在当前状态");
        } catch (MementoException expected) {
            assertTrue(expected.getMessage().contains("No current"));
        }
        caretaker.save(new NoteMemento("初稿"));
        try {
            caretaker.undo();
            fail("首个状态无法撤销");
        } catch (MementoException expected) {
            assertTrue(expected.getMessage().contains("Cannot undo"));
        }
        caretaker.save(new NoteMemento("二稿"));
        try {
            caretaker.redo();
            fail("位于末尾时无法重做");
        } catch (MementoException expected) {
            assertTrue(expected.getMessage().contains("Cannot redo"));
        }
    }

    @Test
    public void testHistoryManagerLinearOperations() throws MementoException {
        // 测试历史管理器的线性保存、撤销与重做逻辑
        Note note = new Note("起始");
        HistoryManager manager = new HistoryManager(note);
        note.setContent("版本1");
        manager.save();
        note.setContent("版本2");
        manager.save();
        manager.undo();
        assertEquals("版本1", note.getContent());
        manager.undo();
        assertEquals("起始", note.getContent());
        manager.redo();
        assertEquals("版本1", note.getContent());
        manager.redo();
        assertEquals("版本2", note.getContent());
        note.setContent("版本2分支");
        manager.undo();
        note.setContent("版本1修订");
        manager.save();
        try {
            manager.redo();
            fail("新增保存后历史尾部应被截断");
        } catch (MementoException expected) {
            assertTrue(expected.getMessage().contains("Cannot redo"));
        }
        manager.clearHistory();
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void testHistoryManagerBranching() throws MementoException {
        // 测试历史管理器的分支创建与切换功能
        Note note = new Note("基础");
        HistoryManager manager = new HistoryManager(note);
        note.setContent("主干修改");
        manager.save();
        manager.createBranch("feature");
        manager.switchBranch("feature");
        note.setContent("分支修改");
        manager.save();
        manager.switchBranch("main");
        assertEquals("主干修改", note.getContent());
        manager.switchBranch("feature");
        assertEquals("分支修改", note.getContent());
        assertTrue(manager.getAllBranches().containsAll(Arrays.asList("main", "feature")));
        try {
            manager.switchBranch("missing");
            fail("不存在的分支应抛出异常");
        } catch (MementoException expected) {
            assertTrue(expected.getMessage().contains("Branch not found"));
        }
    }

    @Test
    public void testLabelValidationAndStructure() {
        // 测试标签的构造校验与层级结构生成
        try {
            new Label(" ");
            fail("空名称应当抛出异常");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Label name"));
        }
        Label root = new Label("根", null);
        Label child = new Label("子", root);
        assertEquals("子", child.getName());
        assertSame(root, child.getParent());
        assertEquals("根/子", child.getFullPath());
        assertTrue(root.getChildren().contains(child));
        Label sameName = new Label("子", root);
        assertEquals(child, sameName);
        assertEquals(child.hashCode(), sameName.hashCode());
        assertNotEquals(child, "子");
    }

    @Test
    public void testLabelManagerAddRemoveFlow() {
        // 测试标签管理器为笔记添加和移除标签的流程
        LabelManager manager = new LabelManager();
        Label label = new Label("项目", null);
        Note note = new Note("项目计划");
        manager.addLabelToNote(label, note);
        assertTrue(note.getLabels().contains(label));
        assertTrue(manager.getNotesByLabel(label).contains(note));
        manager.removeLabelFromNote(label, note);
        assertFalse(note.getLabels().contains(label));
        assertTrue(manager.getNotesByLabel(label).isEmpty());
        assertFalse(manager.getAllLabels().contains(label));
    }

    @Test
    public void testLabelSuggestionService() {
        // 测试标签推荐服务对内容关键字的匹配能力
        LabelSuggestionService service = new LabelSuggestionService();
        Note note = new Note("参加Work会议");
        Collection<Label> labels = Arrays.asList(new Label("work", null), new Label("home", null));
        List<Label> suggested = service.suggestLabels(note, labels);
        assertEquals(1, suggested.size());
        assertEquals("work", suggested.get(0).getName());
    }

    @Test
    public void testNoteContentManagement() throws MementoException {
        // 测试笔记对内容与标签的维护逻辑
        Note note = new Note(null);
        assertEquals("", note.getContent());
        note.setContent(null);
        assertEquals("", note.getContent());
        Label label = new Label("学习", null);
        note.addLabel(label);
        note.addLabel(null);
        assertTrue(note.getLabels().contains(label));
        note.removeLabel(label);
        assertFalse(note.getLabels().contains(label));
        Memento memento = note.createMemento();
        note.setContent("新内容");
        note.restoreMemento(memento);
        assertEquals("", note.getContent());
        try {
            note.restoreMemento(new Memento() {
                @Override
                public Object getState() {
                    return null;
                }
            });
            fail("错误类型的备忘录应当抛出异常");
        } catch (MementoException expected) {
            assertTrue(expected.getMessage().contains("Wrong memento"));
        }
    }

    @Test
    public void testNoteDiffUtilOutputs() {
        // 测试笔记差异工具对比内容的输出格式
        String diff = NoteDiffUtil.diff("行1\n行2", "行1\n变化");
        String expected = "  行1\n- 行2\n+ 变化\n";
        assertEquals(expected, diff);
        assertEquals("+ 新\n", NoteDiffUtil.diff("", "新"));
    }

    @Test
    public void testNoteEncryptorReversible() {
        // 测试笔记加解密的可逆性与空值处理
        String original = "加密内容";
        String encrypted = NoteEncryptor.encrypt(original);
        assertNotEquals(original, encrypted);
        assertEquals(original, NoteEncryptor.decrypt(encrypted));
        assertNull(NoteEncryptor.encrypt(null));
        assertNull(NoteEncryptor.decrypt(null));
    }

    @Test
    public void testPermissionManagerFlow() {
        // 测试权限管理器的授权、撤销及权限判定逻辑
        PermissionManager manager = new PermissionManager();
        User owner = new User("拥有者");
        User viewer = new User("访客");
        manager.grantPermission(owner, Permission.OWNER);
        manager.grantPermission(viewer, Permission.VIEW);
        assertTrue(manager.canEdit(owner));
        assertFalse(manager.canEdit(viewer));
        assertTrue(manager.canView(viewer));
        manager.revokePermission(viewer);
        assertFalse(manager.canView(viewer));
        assertTrue(manager.listCollaborators().contains(owner));
    }

    @Test
    public void testPluginManagerExecution() {
        // 测试插件管理器的注册、执行与防御性拷贝
        PluginManager manager = new PluginManager();
        final boolean[] executed = {false};
        Plugin plugin = new Plugin() {
            @Override
            public String getName() {
                return "统计";
            }

            @Override
            public void execute(UserManager userManager) {
                executed[0] = true;
            }
        };
        manager.register(plugin);
        manager.register(null);
        assertEquals(1, manager.getPlugins().size());
        manager.executeAll(new UserManager());
        assertTrue(executed[0]);
        try {
            manager.getPlugins().add(plugin);
            fail("插件列表应为只读");
        } catch (UnsupportedOperationException expected) {
            // 预期异常
        }
    }

    @Test
    public void testRecycleBinBehaviour() {
        // 测试回收站的回收、还原与清空逻辑
        RecycleBin bin = new RecycleBin();
        Note note = new Note("删除的笔记");
        bin.recycle(note);
        assertTrue(bin.isInBin(note));
        assertTrue(bin.listDeletedNotes().contains(note));
        assertTrue(bin.restore(note));
        assertFalse(bin.isInBin(note));
        bin.recycle(note);
        bin.clear();
        assertFalse(bin.isInBin(note));
    }

    @Test
    public void testRuleEngineExecution() {
        // 测试规则引擎按顺序执行规则并暴露只读集合
        RuleEngine engine = new RuleEngine();
        final List<String> log = new ArrayList<>();
        engine.addRule((note, manager) -> log.add("规则1"));
        engine.addRule((note, manager) -> log.add("规则2"));
        engine.addRule(null);
        engine.applyAll(new Note("内容"), new UserManager());
        assertEquals(Arrays.asList("规则1", "规则2"), log);
        try {
            engine.getRules().add((note, manager) -> {});
            fail("规则集合应为只读");
        } catch (UnsupportedOperationException expected) {
            // 预期异常
        }
    }

    @Test
    public void testSearchServiceByLabelAndKeyword() {
        // 测试搜索服务基于标签与关键字的匹配功能
        SearchService service = new SearchService();
        User user = new User("搜索者");
        Note note1 = new Note("学习Java");
        Note note2 = new Note("学习Python");
        Label label = new Label("Java", null);
        note1.addLabel(label);
        user.addNote(note1);
        user.addNote(note2);
        List<Note> byLabel = service.searchByLabel(user, label);
        assertEquals(Collections.singletonList(note1), byLabel);
        List<Note> byKeyword = service.searchByKeyword(user, "学习");
        assertEquals(2, byKeyword.size());
        assertTrue(service.searchByKeyword(user, null).isEmpty());
        List<Note> fuzzy = service.fuzzySearch(user, "python");
        assertEquals(Collections.singletonList(note2), fuzzy);
    }

    @Test
    public void testSearchServiceHighlightAndAllUsers() {
        // 测试高亮逻辑和跨用户关键字搜索
        SearchService service = new SearchService();
        User user1 = new User("甲");
        User user2 = new User("乙");
        Note note1 = new Note("团队会议");
        Note note2 = new Note("周会安排");
        user1.addNote(note1);
        user2.addNote(note2);
        List<Note> all = service.searchByKeywordAllUsers(Arrays.asList(user1, user2), "会");
        assertEquals(2, all.size());
        assertTrue(service.searchByKeywordAllUsers(Arrays.asList(user1, user2), null).isEmpty());
        assertEquals("[[会议]]纪要", service.highlight("会议纪要", "会议"));
        assertEquals("原文", service.highlight("原文", null));
        assertNull(service.highlight(null, "词"));
    }

    @Test
    public void testStatisticsServiceAggregations() {
        // 测试统计服务对标签使用次数与笔记数量的计算
        StatisticsService service = new StatisticsService();
        User user1 = new User("用户1");
        User user2 = new User("用户2");
        Label labelA = new Label("标签A", null);
        Label labelB = new Label("标签B", null);
        Note note1 = new Note("内容1");
        note1.addLabel(labelA);
        note1.addLabel(labelB);
        Note note2 = new Note("内容2");
        note2.addLabel(labelA);
        user1.addNote(note1);
        user2.addNote(note2);
        Map<Label, Integer> usage = service.labelUsage(Arrays.asList(user1, user2));
        assertEquals(Integer.valueOf(2), usage.get(labelA));
        assertEquals(Integer.valueOf(1), usage.get(labelB));
        assertEquals(2, service.noteCount(Arrays.asList(user1, user2)));
    }

    @Test
    public void testUserOperationsAndHistory() {
        // 测试用户添加笔记、历史管理和防御性拷贝
        User user = new User("开发者");
        Note note = new Note("初稿");
        user.addNote(note);
        user.addNote(note);
        assertEquals(1, user.getNotes().size());
        assertNotNull(user.getHistoryManager(note));
        List<Note> notesCopy = user.getNotes();
        notesCopy.clear();
        assertEquals(1, user.getNotes().size());
        user.removeNote(note);
        assertNull(user.getHistoryManager(note));
        assertTrue(user.getNotes().isEmpty());
    }

    @Test
    public void testUserConstructorValidation() {
        // 测试用户构造函数对非法名称的校验
        try {
            new User(null);
            fail("用户名为空应当抛出异常");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Username"));
        }
        try {
            new User("   ");
            fail("空白用户名应当抛出异常");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Username"));
        }
        User user = new User("合法");
        assertEquals("合法", user.getName());
    }

    @Test
    public void testUserManagerLifecycle() {
        // 测试用户管理器的注册、查询与删除
        UserManager manager = new UserManager();
        User user = manager.registerUser("Alice");
        assertSame(user, manager.getUser("Alice"));
        assertTrue(manager.getAllUsers().contains(user));
        try {
            manager.registerUser("Alice");
            fail("重复注册应当抛出异常");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("User already exists"));
        }
        manager.removeUser("Alice");
        assertNull(manager.getUser("Alice"));
        assertTrue(manager.getAllUsers().isEmpty());
    }
}
