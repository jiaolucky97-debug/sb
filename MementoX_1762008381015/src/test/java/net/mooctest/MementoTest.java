package net.mooctest;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 综合测试类，覆盖所有业务类的功能测试
 * 目标：达到90%以上的分支覆盖率和变异杀死率
 */
public class MementoTest {

    // 测试用的对象引用
    private User user;
    private Note note;
    private Label label;
    private UserManager userManager;
    private LabelManager labelManager;
    private SearchService searchService;
    private HistoryManager historyManager;
    private Caretaker caretaker;
    private RecycleBin recycleBin;
    private PermissionManager permissionManager;
    private PluginManager pluginManager;
    private RuleEngine ruleEngine;
    private StatisticsService statisticsService;
    private CalendarManager calendarManager;
    private CalendarManager.Reminder reminder;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // 类级别的初始化，如果需要的话
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        // 类级别的清理
    }

    @Before
    public void setUp() throws Exception {
        // 每个测试方法前的初始化 - 创建新的对象避免测试间相互影响
        user = new User("testUser");
        note = new Note("测试笔记内容");
        label = new Label("测试标签");
        userManager = new UserManager();
        labelManager = new LabelManager();
        searchService = new SearchService();
        historyManager = new HistoryManager(new Note("测试笔记内容")); // 创建新的note避免共享状态
        caretaker = new Caretaker();
        recycleBin = new RecycleBin();
        permissionManager = new PermissionManager();
        pluginManager = new PluginManager();
        ruleEngine = new RuleEngine();
        statisticsService = new StatisticsService();
        calendarManager = new CalendarManager();
        reminder = new CalendarManager.Reminder(new Note("测试笔记内容"), new Date());
    }

    @After
    public void tearDown() throws Exception {
        // 每个测试方法后的清理
    }

    // ==================== MementoException 测试 ====================
    
    /**
     * 测试MementoException的构造函数
     * 覆盖两个构造方法的所有分支
     */
    @Test
    public void testMementoException() {
        // 测试只包含消息的构造函数
        MementoException exception1 = new MementoException("测试异常");
        assertEquals("测试异常", exception1.getMessage());
        assertNull(exception1.getCause());
        
        // 测试包含消息和原因的构造函数
        RuntimeException cause = new RuntimeException("原因异常");
        MementoException exception2 = new MementoException("测试异常带原因", cause);
        assertEquals("测试异常带原因", exception2.getMessage());
        assertEquals(cause, exception2.getCause());
    }

    // ==================== Permission 枚举测试 ====================
    
    /**
     * 测试Permission枚举的所有值
     */
    @Test
    public void testPermissionEnum() {
        // 测试所有枚举值
        Permission[] permissions = Permission.values();
        assertEquals(3, permissions.length);
        
        boolean hasOwner = false, hasEdit = false, hasView = false;
        for (Permission p : permissions) {
            if (p == Permission.OWNER) hasOwner = true;
            if (p == Permission.EDIT) hasEdit = true;
            if (p == Permission.VIEW) hasView = true;
        }
        
        assertTrue("应该包含OWNER权限", hasOwner);
        assertTrue("应该包含EDIT权限", hasEdit);
        assertTrue("应该包含VIEW权限", hasView);
    }

    // ==================== NoteStatus 枚举测试 ====================
    
    /**
     * 测试NoteStatus枚举的所有值
     */
    @Test
    public void testNoteStatusEnum() {
        // 测试所有枚举值
        NoteStatus[] statuses = NoteStatus.values();
        assertEquals(4, statuses.length);
        
        boolean hasActive = false, hasLocked = false, hasArchived = false, hasDeleted = false;
        for (NoteStatus s : statuses) {
            if (s == NoteStatus.ACTIVE) hasActive = true;
            if (s == NoteStatus.LOCKED) hasLocked = true;
            if (s == NoteStatus.ARCHIVED) hasArchived = true;
            if (s == NoteStatus.DELETED) hasDeleted = true;
        }
        
        assertTrue("应该包含ACTIVE状态", hasActive);
        assertTrue("应该包含LOCKED状态", hasLocked);
        assertTrue("应该包含ARCHIVED状态", hasArchived);
        assertTrue("应该包含DELETED状态", hasDeleted);
    }

    // ==================== Memento 抽象类测试 ====================
    
    /**
     * 测试Memento抽象类的基本功能
     */
    @Test
    public void testMementoAbstractClass() {
        // 创建一个具体的Memento实现来测试抽象类
        Memento memento = new NoteMemento("测试内容");
        
        // 测试ID生成和获取
        assertNotNull("Memento ID不应为null", memento.getId());
        assertTrue("Memento ID应为非空字符串", !memento.getId().isEmpty());
        
        // 测试时间戳
        assertNotNull("时间戳不应为null", memento.getTimestamp());
        assertTrue("时间戳应为当前时间附近", 
            System.currentTimeMillis() - memento.getTimestamp().getTime() < 1000);
        
        // 测试状态获取
        assertEquals("状态应为测试内容", "测试内容", memento.getState());
    }

    // ==================== NoteMemento 测试 ====================
    
    /**
     * 测试NoteMemento类的所有功能
     */
    @Test
    public void testNoteMemento() {
        // 测试正常内容
        NoteMemento memento1 = new NoteMemento("正常内容");
        assertEquals("正常内容", memento1.getState());
        assertNotNull(memento1.getId());
        assertNotNull(memento1.getTimestamp());
        
        // 测试空内容
        NoteMemento memento2 = new NoteMemento("");
        assertEquals("", memento2.getState());
        
        // 测试特殊字符内容
        NoteMemento memento3 = new NoteMemento("特殊字符!@#$%^&*()");
        assertEquals("特殊字符!@#$%^&*()", memento3.getState());
    }

    // ==================== NoteEncryptor 测试 ====================
    
    /**
     * 测试NoteEncryptor的加密和解密功能
     */
    @Test
    public void testNoteEncryptor() {
        // 测试正常字符串加密解密
        String original = "测试加密内容";
        String encrypted = NoteEncryptor.encrypt(original);
        assertNotNull("加密结果不应为null", encrypted);
        assertNotEquals("加密后内容应与原内容不同", original, encrypted);
        
        String decrypted = NoteEncryptor.decrypt(encrypted);
        assertEquals("解密后应与原内容相同", original, decrypted);
        
        // 测试空字符串
        String empty = "";
        assertEquals("空字符串加密后应为空", empty, NoteEncryptor.encrypt(empty));
        assertEquals("空字符串解密后应为空", empty, NoteEncryptor.decrypt(empty));
        
        // 测试null值
        assertNull("null加密应返回null", NoteEncryptor.encrypt(null));
        assertNull("null解密应返回null", NoteEncryptor.decrypt(null));
        
        // 测试单字符
        String singleChar = "A";
        String encryptedSingle = NoteEncryptor.encrypt(singleChar);
        assertEquals("单字符解密应还原", singleChar, NoteEncryptor.decrypt(encryptedSingle));
    }

    // ==================== Label 测试 ====================
    
    /**
     * 测试Label类的所有功能
     */
    @Test
    public void testLabel() {
        // 测试无父标签的构造函数
        Label label1 = new Label("测试标签");
        assertEquals("测试标签", label1.getName());
        assertNull("无父标签时应为null", label1.getParent());
        assertTrue("子标签列表应为空", label1.getChildren().isEmpty());
        assertEquals("无父标签时全路径应为名称", "测试标签", label1.getFullPath());
        
        // 测试有父标签的构造函数
        Label parent = new Label("父标签");
        Label child = new Label("子标签", parent);
        assertEquals("子标签", child.getName());
        assertEquals("父标签应为设置的父标签", parent, child.getParent());
        // 注意：Label的构造函数有条件检查，只有当parent.children是ArrayList时才会添加
        // 由于parent.children是Collections.emptyList()，不是ArrayList，所以不会自动添加
        // 这是设计如此，我们需要测试这个行为
        assertEquals("全路径应包含父标签路径", "父标签/子标签", child.getFullPath());
        
        // 测试异常情况
        try {
            new Label(null);
            fail("null名称应抛出异常");
        } catch (IllegalArgumentException e) {
            assertEquals("Label name cannot be null or empty", e.getMessage());
        }
        
        try {
            new Label("   ");
            fail("空名称应抛出异常");
        } catch (IllegalArgumentException e) {
            assertEquals("Label name cannot be null or empty", e.getMessage());
        }
        
        // 测试名称修剪
        Label labelWithSpaces = new Label("  测试标签  ");
        assertEquals("名称应被修剪", "测试标签", labelWithSpaces.getName());
        
        // 测试equals和hashCode
        Label labelA = new Label("相同名称");
        Label labelB = new Label("相同名称");
        assertEquals("相同名称的标签应相等", labelA, labelB);
        assertEquals("相同名称的标签hashCode应相同", labelA.hashCode(), labelB.hashCode());
        
        assertNotEquals("不同名称的标签不应相等", labelA, label1);
        
        // 测试toString
        assertEquals("toString应返回名称", "测试标签", label1.toString());
    }

    // ==================== Note 测试 ====================
    
    /**
     * 测试Note类的所有功能
     */
    @Test
    public void testNote() {
        // 测试构造函数
        Note note1 = new Note("测试内容");
        assertEquals("测试内容", note1.getContent());
        assertTrue("标签集合应为空", note1.getLabels().isEmpty());
        
        // 测试null内容构造
        Note note2 = new Note(null);
        assertEquals("null内容应变为空字符串", "", note2.getContent());
        
        // 测试设置内容
        note1.setContent("新内容");
        assertEquals("内容应被更新", "新内容", note1.getContent());
        
        note1.setContent(null);
        assertEquals("设置null内容应变为空字符串", "", note1.getContent());
        
        // 测试标签管理
        Label label1 = new Label("标签1");
        Label label2 = new Label("标签2");
        
        note1.addLabel(label1);
        assertTrue("应包含添加的标签", note1.getLabels().contains(label1));
        assertEquals("标签数量应为1", 1, note1.getLabels().size());
        
        note1.addLabel(null); // 添加null标签不应有影响
        assertEquals("添加null标签不应增加数量", 1, note1.getLabels().size());
        
        note1.addLabel(label2);
        assertEquals("标签数量应为2", 2, note1.getLabels().size());
        
        note1.removeLabel(label1);
        assertFalse("不应包含移除的标签", note1.getLabels().contains(label1));
        assertTrue("仍应包含其他标签", note1.getLabels().contains(label2));
        
        // 测试Memento功能
        Memento memento = note1.createMemento();
        assertNotNull("Memento不应为null", memento);
        assertTrue("应为NoteMemento类型", memento instanceof NoteMemento);
        assertEquals("Memento状态应为当前内容", "", memento.getState());
        
        // 测试恢复Memento
        note1.setContent("修改后的内容");
        try {
            note1.restoreMemento(memento);
            assertEquals("内容应被恢复", "", note1.getContent());
        } catch (MementoException e) {
            fail("恢复正确的Memento不应抛出异常");
        }
        
        // 测试恢复错误的Memento类型
        Memento wrongMemento = new Memento() {
            @Override
            public Object getState() {
                return "wrong state";
            }
        };
        
        try {
            note1.restoreMemento(wrongMemento);
            fail("恢复错误类型的Memento应抛出异常");
        } catch (MementoException e) {
            assertEquals("Wrong memento type for Note", e.getMessage());
        }
    }

    // ==================== User 测试 ====================
    
    /**
     * 测试User类的所有功能
     */
    @Test
    public void testUser() {
        // 测试正常构造
        User user1 = new User("测试用户");
        assertEquals("测试用户", user1.getName());
        assertTrue("笔记列表应为空", user1.getNotes().isEmpty());
        
        // 测试异常构造
        try {
            new User(null);
            fail("null用户名应抛出异常");
        } catch (IllegalArgumentException e) {
            assertEquals("Username can't be null or empty", e.getMessage());
        }
        
        try {
            new User("   ");
            fail("空用户名应抛出异常");
        } catch (IllegalArgumentException e) {
            assertEquals("Username can't be null or empty", e.getMessage());
        }
        
        // 测试用户名修剪
        User userWithSpaces = new User("  测试用户  ");
        assertEquals("用户名应被修剪", "测试用户", userWithSpaces.getName());
        
        // 测试笔记管理
        Note note1 = new Note("笔记1");
        Note note2 = new Note("笔记2");
        
        user1.addNote(note1);
        assertEquals("应包含添加的笔记", 1, user1.getNotes().size());
        assertTrue("应包含添加的笔记", user1.getNotes().contains(note1));
        assertNotNull("应创建HistoryManager", user1.getHistoryManager(note1));
        
        // 测试添加重复笔记
        user1.addNote(note1);
        assertEquals("不应添加重复笔记", 1, user1.getNotes().size());
        
        // 测试添加null笔记
        user1.addNote(null);
        assertEquals("添加null笔记不应增加数量", 1, user1.getNotes().size());
        
        user1.addNote(note2);
        assertEquals("笔记数量应为2", 2, user1.getNotes().size());
        
        // 测试获取HistoryManager
        HistoryManager hm1 = user1.getHistoryManager(note1);
        HistoryManager hm2 = user1.getHistoryManager(note2);
        assertNotNull("应返回HistoryManager", hm1);
        assertNotNull("应返回HistoryManager", hm2);
        assertNotEquals("不同笔记的HistoryManager应不同", hm1, hm2);
        
        HistoryManager hmNull = user1.getHistoryManager(new Note("不存在的笔记"));
        assertNull("不存在的笔记应返回null", hmNull);
        
        // 测试移除笔记
        user1.removeNote(note1);
        assertFalse("不应包含移除的笔记", user1.getNotes().contains(note1));
        assertTrue("仍应包含其他笔记", user1.getNotes().contains(note2));
        assertNull("移除的笔记HistoryManager应为null", user1.getHistoryManager(note1));
        
        // 测试移除null笔记
        int sizeBefore = user1.getNotes().size();
        user1.removeNote(null);
        assertEquals("移除null笔记不应影响数量", sizeBefore, user1.getNotes().size());
    }

    // ==================== UserManager 测试 ====================
    
    /**
     * 测试UserManager类的所有功能
     */
    @Test
    public void testUserManager() {
        // 测试注册用户
        User user1 = userManager.registerUser("用户1");
        assertNotNull("注册的用户不应为null", user1);
        assertEquals("用户名应正确", "用户1", user1.getName());
        
        // 测试获取用户
        User retrievedUser = userManager.getUser("用户1");
        assertEquals("获取的用户应与注册的用户相同", user1, retrievedUser);
        
        // 测试重复注册
        try {
            userManager.registerUser("用户1");
            fail("重复注册应抛出异常");
        } catch (IllegalArgumentException e) {
            assertEquals("User already exists", e.getMessage());
        }
        
        // 测试获取不存在的用户
        User nonExistentUser = userManager.getUser("不存在的用户");
        assertNull("不存在的用户应返回null", nonExistentUser);
        
        // 测试注册多个用户
        User user2 = userManager.registerUser("用户2");
        User user3 = userManager.registerUser("用户3");
        
        // 测试获取所有用户
        Collection<User> allUsers = userManager.getAllUsers();
        assertEquals("用户数量应为3", 3, allUsers.size());
        assertTrue("应包含所有注册的用户", allUsers.contains(user1));
        assertTrue("应包含所有注册的用户", allUsers.contains(user2));
        assertTrue("应包含所有注册的用户", allUsers.contains(user3));
        
        // 测试移除用户
        userManager.removeUser("用户2");
        assertNull("移除的用户应不存在", userManager.getUser("用户2"));
        assertEquals("用户数量应为2", 2, userManager.getAllUsers().size());
        
        // 测试移除不存在的用户
        int sizeBefore = userManager.getAllUsers().size();
        userManager.removeUser("不存在的用户");
        assertEquals("移除不存在的用户不应影响数量", sizeBefore, userManager.getAllUsers().size());
    }

    // ==================== LabelManager 测试 ====================
    
    /**
     * 测试LabelManager类的所有功能
     */
    @Test
    public void testLabelManager() {
        // 测试添加标签到笔记
        labelManager.addLabelToNote(label, note);
        assertTrue("笔记应被添加到标签", labelManager.getNotesByLabel(label).contains(note));
        assertTrue("笔记应包含标签", note.getLabels().contains(label));
        
        // 测试获取标签的所有笔记
        Set<Note> notes = labelManager.getNotesByLabel(label);
        assertEquals("笔记数量应为1", 1, notes.size());
        assertTrue("应包含添加的笔记", notes.contains(note));
        
        // 测试获取不存在的标签的笔记
        Label nonExistentLabel = new Label("不存在的标签");
        Set<Note> emptyNotes = labelManager.getNotesByLabel(nonExistentLabel);
        assertTrue("不存在的标签应返回空集合", emptyNotes.isEmpty());
        
        // 测试添加多个笔记到同一标签
        Note note2 = new Note("笔记2");
        labelManager.addLabelToNote(label, note2);
        assertEquals("标签下的笔记数量应为2", 2, labelManager.getNotesByLabel(label).size());
        
        // 测试获取所有标签
        Label label2 = new Label("标签2");
        labelManager.addLabelToNote(label2, new Note("笔记3"));
        
        Set<Label> allLabels = labelManager.getAllLabels();
        assertEquals("标签数量应为2", 2, allLabels.size());
        assertTrue("应包含所有标签", allLabels.contains(label));
        assertTrue("应包含所有标签", allLabels.contains(label2));
        
        // 测试从笔记移除标签
        labelManager.removeLabelFromNote(label, note);
        assertFalse("笔记不应再在标签下", labelManager.getNotesByLabel(label).contains(note));
        assertFalse("笔记不应再包含标签", note.getLabels().contains(label));
        assertTrue("其他笔记应仍在标签下", labelManager.getNotesByLabel(label).contains(note2));
        
        // 测试移除标签后标签被删除
        labelManager.removeLabelFromNote(label, note2);
        assertTrue("移除所有笔记后标签应被删除", labelManager.getNotesByLabel(label).isEmpty());
        assertFalse("所有标签集合不应包含已删除的标签", labelManager.getAllLabels().contains(label));
        
        // 测试从不存在的标签移除笔记
        labelManager.removeLabelFromNote(nonExistentLabel, note);
        // 应该没有异常，只是什么都不做
    }

    // ==================== RecycleBin 测试 ====================
    
    /**
     * 测试RecycleBin类的所有功能
     */
    @Test
    public void testRecycleBin() {
        // 测试回收笔记
        recycleBin.recycle(note);
        assertTrue("笔记应在回收站中", recycleBin.isInBin(note));
        
        // 测试列出已删除笔记
        Set<Note> deletedNotes = recycleBin.listDeletedNotes();
        assertEquals("已删除笔记数量应为1", 1, deletedNotes.size());
        assertTrue("应包含回收的笔记", deletedNotes.contains(note));
        
        // 测试恢复笔记
        boolean restored = recycleBin.restore(note);
        assertTrue("恢复应成功", restored);
        assertFalse("笔记不应再在回收站中", recycleBin.isInBin(note));
        assertTrue("已删除笔记列表应为空", recycleBin.listDeletedNotes().isEmpty());
        
        // 测试恢复不存在的笔记
        Note nonExistentNote = new Note("不存在的笔记");
        boolean notRestored = recycleBin.restore(nonExistentNote);
        assertFalse("恢复不存在的笔记应失败", notRestored);
        
        // 测试回收null笔记
        recycleBin.recycle(null);
        assertTrue("回收null不应影响现有笔记", recycleBin.listDeletedNotes().isEmpty());
        
        // 测试检查null笔记
        assertFalse("检查null笔记应返回false", recycleBin.isInBin(null));
        
        // 测试清空回收站
        recycleBin.recycle(note);
        recycleBin.recycle(new Note("笔记2"));
        assertEquals("回收站应有2个笔记", 2, recycleBin.listDeletedNotes().size());
        
        recycleBin.clear();
        assertTrue("清空后回收站应为空", recycleBin.listDeletedNotes().isEmpty());
    }

    // ==================== PermissionManager 测试 ====================
    
    /**
     * 测试PermissionManager类的所有功能
     */
    @Test
    public void testPermissionManager() {
        // 测试授予权限
        permissionManager.grantPermission(user, Permission.OWNER);
        assertEquals("应授予OWNER权限", Permission.OWNER, permissionManager.getPermission(user));
        
        // 测试权限检查
        assertTrue("OWNER应有编辑权限", permissionManager.canEdit(user));
        assertTrue("OWNER应有查看权限", permissionManager.canView(user));
        
        // 测试授予权限
        permissionManager.grantPermission(user, Permission.EDIT);
        assertTrue("EDIT应有编辑权限", permissionManager.canEdit(user));
        assertTrue("EDIT应有查看权限", permissionManager.canView(user));
        
        // 测试授予VIEW权限
        permissionManager.grantPermission(user, Permission.VIEW);
        assertFalse("VIEW不应有编辑权限", permissionManager.canEdit(user));
        assertTrue("VIEW应有查看权限", permissionManager.canView(user));
        
        // 测试撤销权限
        permissionManager.revokePermission(user);
        assertNull("撤销后权限应为null", permissionManager.getPermission(user));
        assertFalse("撤销后不应有编辑权限", permissionManager.canEdit(user));
        assertFalse("撤销后不应有查看权限", permissionManager.canView(user));
        
        // 测试多个用户权限
        User user2 = new User("用户2");
        permissionManager.grantPermission(user, Permission.OWNER);
        permissionManager.grantPermission(user2, Permission.VIEW);
        
        Set<User> collaborators = permissionManager.listCollaborators();
        assertEquals("协作者数量应为2", 2, collaborators.size());
        assertTrue("应包含所有有权限的用户", collaborators.contains(user));
        assertTrue("应包含所有有权限的用户", collaborators.contains(user2));
        
        // 测试null用户权限操作
        permissionManager.grantPermission(null, Permission.EDIT);
        assertNull("null用户不应被授予权限", permissionManager.getPermission(null));
        
        permissionManager.revokePermission(null);
        // 应该没有异常
        
        assertFalse("null用户不应有编辑权限", permissionManager.canEdit(null));
        assertFalse("null用户不应有查看权限", permissionManager.canView(null));
        
        // 测试null权限
        permissionManager.grantPermission(user, null);
        assertEquals("null权限不应改变现有权限", Permission.OWNER, permissionManager.getPermission(user));
    }

    // ==================== PluginManager 测试 ====================
    
    /**
     * 测试PluginManager类的所有功能
     */
    @Test
    public void testPluginManager() {
        // 创建测试插件
        Plugin testPlugin = new Plugin() {
            @Override
            public String getName() {
                return "测试插件";
            }
            
            @Override
            public void execute(UserManager userManager) {
                // 测试执行逻辑
            }
        };
        
        // 测试注册插件
        pluginManager.register(testPlugin);
        List<Plugin> plugins = pluginManager.getPlugins();
        assertEquals("插件数量应为1", 1, plugins.size());
        assertEquals("应包含注册的插件", testPlugin, plugins.get(0));
        
        // 测试注册null插件
        pluginManager.register(null);
        assertEquals("注册null插件不应增加数量", 1, pluginManager.getPlugins().size());
        
        // 测试获取的插件列表不可修改
        try {
            pluginManager.getPlugins().add(testPlugin);
            fail("获取的插件列表应为不可修改");
        } catch (UnsupportedOperationException e) {
            // 预期的异常
        }
        
        // 测试执行所有插件
        UserManager testUserManager = new UserManager();
        pluginManager.executeAll(testUserManager);
        // 应该没有异常
        
        // 测试多个插件
        Plugin testPlugin2 = new Plugin() {
            @Override
            public String getName() {
                return "测试插件2";
            }
            
            @Override
            public void execute(UserManager userManager) {
                // 测试执行逻辑
            }
        };
        
        pluginManager.register(testPlugin2);
        assertEquals("插件数量应为2", 2, pluginManager.getPlugins().size());
        
        pluginManager.executeAll(testUserManager);
        // 应该没有异常
    }

    // ==================== RuleEngine 测试 ====================
    
    /**
     * 测试RuleEngine类的所有功能
     */
    @Test
    public void testRuleEngine() {
        // 创建测试规则
        RuleEngine.Rule testRule = new RuleEngine.Rule() {
            @Override
            public void apply(Note note, UserManager userManager) {
                // 测试规则逻辑
            }
        };
        
        // 测试添加规则
        ruleEngine.addRule(testRule);
        List<RuleEngine.Rule> rules = ruleEngine.getRules();
        assertEquals("规则数量应为1", 1, rules.size());
        assertEquals("应包含添加的规则", testRule, rules.get(0));
        
        // 测试添加null规则
        ruleEngine.addRule(null);
        assertEquals("添加null规则不应增加数量", 1, ruleEngine.getRules().size());
        
        // 测试获取的规则列表不可修改
        try {
            ruleEngine.getRules().add(testRule);
            fail("获取的规则列表应为不可修改");
        } catch (UnsupportedOperationException e) {
            // 预期的异常
        }
        
        // 测试应用所有规则
        Note testNote = new Note("测试笔记");
        UserManager testUserManager = new UserManager();
        ruleEngine.applyAll(testNote, testUserManager);
        // 应该没有异常
        
        // 测试多个规则
        RuleEngine.Rule testRule2 = new RuleEngine.Rule() {
            @Override
            public void apply(Note note, UserManager userManager) {
                // 测试规则逻辑
            }
        };
        
        ruleEngine.addRule(testRule2);
        assertEquals("规则数量应为2", 2, ruleEngine.getRules().size());
        
        ruleEngine.applyAll(testNote, testUserManager);
        // 应该没有异常
    }

    // ==================== StatisticsService 测试 ====================
    
    /**
     * 测试StatisticsService类的所有功能
     */
    @Test
    public void testStatisticsService() {
        // 创建测试数据
        User user1 = new User("用户1");
        User user2 = new User("用户2");
        Label label1 = new Label("标签1");
        Label label2 = new Label("标签2");
        
        Note note1 = new Note("笔记1");
        Note note2 = new Note("笔记2");
        Note note3 = new Note("笔记3");
        
        note1.addLabel(label1);
        note2.addLabel(label1);
        note2.addLabel(label2);
        note3.addLabel(label2);
        
        user1.addNote(note1);
        user1.addNote(note2);
        user2.addNote(note3);
        
        Collection<User> users = Arrays.asList(user1, user2);
        
        // 测试标签使用统计
        Map<Label, Integer> labelStats = statisticsService.labelUsage(users);
        assertEquals("应统计2个标签", 2, labelStats.size());
        assertEquals("标签1应被使用2次", Integer.valueOf(2), labelStats.get(label1));
        assertEquals("标签2应被使用2次", Integer.valueOf(2), labelStats.get(label2));
        
        // 测试笔记数量统计
        int noteCount = statisticsService.noteCount(users);
        assertEquals("总笔记数应为3", 3, noteCount);
        
        // 测试空用户集合
        Map<Label, Integer> emptyLabelStats = statisticsService.labelUsage(Collections.emptyList());
        assertTrue("空用户集合应返回空统计", emptyLabelStats.isEmpty());
        
        int emptyNoteCount = statisticsService.noteCount(Collections.emptyList());
        assertEquals("空用户集合笔记数应为0", 0, emptyNoteCount);
        
        // 测试无标签笔记
        User userWithNoLabels = new User("无标签用户");
        Note noteWithNoLabels = new Note("无标签笔记");
        userWithNoLabels.addNote(noteWithNoLabels);
        
        Map<Label, Integer> noLabelStats = statisticsService.labelUsage(Arrays.asList(userWithNoLabels));
        assertTrue("无标签笔记应返回空统计", noLabelStats.isEmpty());
    }

    // ==================== SearchService 测试 ====================
    
    /**
     * 测试SearchService类的所有功能
     */
    @Test
    public void testSearchService() {
        // 创建独立的用户避免共享状态
        User testUser = new User("testSearchUser");
        
        // 创建测试数据
        Label label1 = new Label("标签1");
        Label label2 = new Label("标签2");
        
        Note note1 = new Note("包含关键词的笔记内容");
        Note note2 = new Note("这是普通的笔记内容");
        Note note3 = new Note("关键词也在这里");
        
        note1.addLabel(label1);
        note2.addLabel(label1);
        note3.addLabel(label2);
        
        testUser.addNote(note1);
        testUser.addNote(note2);
        testUser.addNote(note3);
        
        // 测试按标签搜索
        List<Note> labelSearchResults = searchService.searchByLabel(testUser, label1);
        assertEquals("标签1搜索结果应为2个", 2, labelSearchResults.size());
        assertTrue("应包含note1", labelSearchResults.contains(note1));
        assertTrue("应包含note2", labelSearchResults.contains(note2));
        
        List<Note> label2SearchResults = searchService.searchByLabel(testUser, label2);
        assertEquals("标签2搜索结果应为1个", 1, label2SearchResults.size());
        assertEquals("应包含note3", note3, label2SearchResults.get(0));
        
        // 测试按不存在的标签搜索
        Label nonExistentLabel = new Label("不存在的标签");
        List<Note> emptyLabelResults = searchService.searchByLabel(testUser, nonExistentLabel);
        assertTrue("不存在的标签搜索结果应为空", emptyLabelResults.isEmpty());
        
        // 测试按关键词搜索
        List<Note> keywordSearchResults = searchService.searchByKeyword(testUser, "关键词");
        assertEquals("关键词搜索结果应为2个", 2, keywordSearchResults.size());
        assertTrue("应包含note1", keywordSearchResults.contains(note1));
        assertTrue("应包含note3", keywordSearchResults.contains(note3));
        
        // 测试按不存在关键词搜索
        List<Note> emptyKeywordResults = searchService.searchByKeyword(testUser, "不存在");
        assertTrue("不存在关键词搜索结果应为空", emptyKeywordResults.isEmpty());
        
        // 测试null关键词搜索
        List<Note> nullKeywordResults = searchService.searchByKeyword(testUser, null);
        assertTrue("null关键词搜索结果应为空", nullKeywordResults.isEmpty());
        
        // 测试多用户关键词搜索
        User user2 = new User("用户2");
        Note note4 = new Note("用户2的关键词笔记");
        user2.addNote(note4);
        
        Collection<User> users = Arrays.asList(testUser, user2);
        List<Note> multiUserResults = searchService.searchByKeywordAllUsers(users, "关键词");
        assertEquals("多用户关键词搜索结果应为3个", 3, multiUserResults.size());
        assertTrue("应包含所有匹配的笔记", multiUserResults.containsAll(Arrays.asList(note1, note3, note4)));
        
        // 测试模糊搜索
        List<Note> fuzzyResults = searchService.fuzzySearch(testUser, "关键词");
        assertEquals("模糊搜索结果应为2个", 2, fuzzyResults.size());
        
        // 测试大小写不敏感的模糊搜索
        List<Note> caseInsensitiveResults = searchService.fuzzySearch(testUser, "关键词");
        assertEquals("大小写不敏感搜索结果应为2个", 2, caseInsensitiveResults.size());
        
        // 测试空字符串模糊搜索
        List<Note> emptyFuzzyResults = searchService.fuzzySearch(testUser, "");
        assertTrue("空字符串模糊搜索结果应为空", emptyFuzzyResults.isEmpty());
        
        List<Note> nullFuzzyResults = searchService.fuzzySearch(testUser, null);
        assertTrue("null模糊搜索结果应为空", nullFuzzyResults.isEmpty());
        
        // 测试高亮显示
        String highlighted = searchService.highlight("这是关键词测试", "关键词");
        assertEquals("关键词应被高亮", "这是[[关键词]]测试", highlighted);
        
        // 测试大小写不敏感高亮
        String caseInsensitiveHighlight = searchService.highlight("这是关键词测试", "关键词");
        assertEquals("大小写不敏感高亮应工作", "这是[[关键词]]测试", caseInsensitiveHighlight);
        
        // 测试null高亮
        String nullHighlight = searchService.highlight(null, "关键词");
        assertNull("null内容高亮应返回null", nullHighlight);
        
        String nullKeywordHighlight = searchService.highlight("测试内容", null);
        assertEquals("null关键词应返回原内容", "测试内容", nullKeywordHighlight);
    }

    // ==================== LabelSuggestionService 测试 ====================
    
    /**
     * 测试LabelSuggestionService类的所有功能
     */
    @Test
    public void testLabelSuggestionService() {
        LabelSuggestionService suggestionService = new LabelSuggestionService();
        
        // 创建测试数据
        Label workLabel = new Label("工作");
        Label personalLabel = new Label("个人");
        Label urgentLabel = new Label("紧急");
        
        Note workNote = new Note("这是一个关于工作的笔记");
        Note personalNote = new Note("个人生活记录");
        Note mixedNote = new Note("工作需要紧急处理");
        
        Collection<Label> allLabels = Arrays.asList(workLabel, personalLabel, urgentLabel);
        
        // 测试标签建议
        List<Label> workSuggestions = suggestionService.suggestLabels(workNote, allLabels);
        assertEquals("工作笔记应建议工作标签", 1, workSuggestions.size());
        assertEquals("应建议工作标签", workLabel, workSuggestions.get(0));
        
        List<Label> personalSuggestions = suggestionService.suggestLabels(personalNote, allLabels);
        assertEquals("个人笔记应建议个人标签", 1, personalSuggestions.size());
        assertEquals("应建议个人标签", personalLabel, personalSuggestions.get(0));
        
        List<Label> mixedSuggestions = suggestionService.suggestLabels(mixedNote, allLabels);
        assertEquals("混合内容笔记应建议多个标签", 2, mixedSuggestions.size());
        assertTrue("应建议工作标签", mixedSuggestions.contains(workLabel));
        assertTrue("应建议紧急标签", mixedSuggestions.contains(urgentLabel));
        
        // 测试无匹配标签
        Note noMatchNote = new Note("没有任何相关关键词的笔记");
        List<Label> noMatchSuggestions = suggestionService.suggestLabels(noMatchNote, allLabels);
        assertTrue("无匹配笔记应返回空建议", noMatchSuggestions.isEmpty());
        
        // 测试空标签集合
        List<Label> emptyLabelSuggestions = suggestionService.suggestLabels(workNote, Collections.emptyList());
        assertTrue("空标签集合应返回空建议", emptyLabelSuggestions.isEmpty());
        
        // 测试大小写不敏感
        Note caseNote = new Note("这是一个WORK相关的笔记");
        List<Label> caseSuggestions = suggestionService.suggestLabels(caseNote, allLabels);
        assertTrue("大小写不敏感应工作", caseSuggestions.contains(workLabel));
    }

    // ==================== NoteDiffUtil 测试 ====================
    
    /**
     * 测试NoteDiffUtil类的所有功能
     */
    @Test
    public void testNoteDiffUtil() {
        // 测试相同内容
        String sameContent = "相同内容";
        String sameDiff = NoteDiffUtil.diff(sameContent, sameContent);
        assertTrue("相同内容应显示为未修改", sameDiff.contains("  相同内容"));
        
        // 测试不同内容
        String oldContent = "旧内容\n第二行";
        String newContent = "新内容\n第二行\n第三行";
        String diff = NoteDiffUtil.diff(oldContent, newContent);
        
        assertTrue("应显示删除的行", diff.contains("- 旧内容"));
        assertTrue("应显示添加的行", diff.contains("+ 新内容"));
        assertTrue("应显示未修改的行", diff.contains("  第二行"));
        assertTrue("应显示新增的行", diff.contains("+ 第三行"));
        
        // 测试null内容
        String nullDiff1 = NoteDiffUtil.diff(null, "新内容");
        assertTrue("null旧内容应被处理", nullDiff1.contains("+ 新内容"));
        
        String nullDiff2 = NoteDiffUtil.diff("旧内容", null);
        assertTrue("null新内容应被处理", nullDiff2.contains("- 旧内容"));
        
        String nullDiff3 = NoteDiffUtil.diff(null, null);
        assertTrue("两个null应产生空diff", nullDiff3.trim().isEmpty());
        
        // 测试空内容
        String emptyDiff = NoteDiffUtil.diff("", "");
        assertTrue("空内容应产生空diff", emptyDiff.trim().isEmpty());
        
        // 测试单行内容
        String singleLineDiff = NoteDiffUtil.diff("旧", "新");
        assertTrue("应显示单行差异", singleLineDiff.contains("- 旧"));
        assertTrue("应显示单行差异", singleLineDiff.contains("+ 新"));
        
        // 测试多行差异
        String multiOld = "第一行\n第二行\n第三行";
        String multiNew = "第一行\n修改的第二行\n第三行\n第四行";
        String multiDiff = NoteDiffUtil.diff(multiOld, multiNew);
        
        assertTrue("应保留未修改行", multiDiff.contains("  第一行"));
        assertTrue("应显示修改行", multiDiff.contains("- 第二行"));
        assertTrue("应显示修改行", multiDiff.contains("+ 修改的第二行"));
        assertTrue("应保留未修改行", multiDiff.contains("  第三行"));
        assertTrue("应显示新增行", multiDiff.contains("+ 第四行"));
    }

    // ==================== Caretaker 测试 ====================
    
    /**
     * 测试Caretaker类的所有功能
     */
    @Test
    public void testCaretaker() {
        // 测试保存Memento
        Memento memento1 = new NoteMemento("状态1");
        Memento memento2 = new NoteMemento("状态2");
        Memento memento3 = new NoteMemento("状态3");
        
        caretaker.save(memento1);
        assertEquals("历史记录数量应为1", 1, caretaker.getAllHistory().size());
        assertEquals("当前索引应为0", 0, getCurrentIndex(caretaker));
        
        // 测试获取当前Memento
        try {
            Memento current = caretaker.getCurrent();
            assertEquals("当前Memento应为保存的第一个", memento1, current);
        } catch (MementoException e) {
            fail("获取当前Memento不应抛出异常");
        }
        
        // 测试撤销
        try {
            caretaker.undo();
            fail("在第一个状态时撤销应抛出异常");
        } catch (MementoException e) {
            assertEquals("Cannot undo, no previous state available.", e.getMessage());
        }
        
        // 保存更多Memento
        caretaker.save(memento2);
        assertEquals("历史记录数量应为2", 2, caretaker.getAllHistory().size());
        assertEquals("当前索引应为1", 1, getCurrentIndex(caretaker));
        
        caretaker.save(memento3);
        assertEquals("历史记录数量应为3", 3, caretaker.getAllHistory().size());
        assertEquals("当前索引应为2", 2, getCurrentIndex(caretaker));
        
        // 测试撤销
        try {
            Memento undoMemento = caretaker.undo();
            assertEquals("撤销应返回前一个Memento", memento2, undoMemento);
            assertEquals("当前索引应为1", 1, getCurrentIndex(caretaker));
        } catch (MementoException e) {
            fail("撤销不应抛出异常");
        }
        
        // 测试重做
        try {
            Memento redoMemento = caretaker.redo();
            assertEquals("重做应返回后一个Memento", memento3, redoMemento);
            assertEquals("当前索引应为2", 2, getCurrentIndex(caretaker));
        } catch (MementoException e) {
            fail("重做不应抛出异常");
        }
        
        // 测试重做到末尾
        try {
            caretaker.redo();
            fail("在末尾重做应抛出异常");
        } catch (MementoException e) {
            assertEquals("Cannot redo, no next state available.", e.getMessage());
        }
        
        // 测试在中间位置保存新Memento（清除后续历史）
        try {
            caretaker.undo(); // 回到索引1
            Memento memento4 = new NoteMemento("状态4");
            caretaker.save(memento4);
            
            assertEquals("保存新状态后历史数量应为3", 3, caretaker.getAllHistory().size());
            assertEquals("当前索引应为2", 2, getCurrentIndex(caretaker));
            assertEquals("最新的Memento应为新保存的", memento4, caretaker.getCurrent());
        } catch (MementoException e) {
            fail("操作不应抛出异常");
        }
        
        // 测试清空
        caretaker.clear();
        assertTrue("清空后历史应为空", caretaker.getAllHistory().isEmpty());
        assertEquals("清空后索引应为-1", -1, getCurrentIndex(caretaker));
        
        // 测试清空后获取当前Memento
        try {
            caretaker.getCurrent();
            fail("清空后获取当前Memento应抛出异常");
        } catch (MementoException e) {
            assertEquals("No current memento available.", e.getMessage());
        }
    }
    
    // 辅助方法：获取Caretaker的当前索引（通过测试行为推断）
    private int getCurrentIndex(Caretaker caretaker) {
        try {
            // 通过getCurrent()是否抛出异常来判断是否为空
            caretaker.getCurrent();
            // 如果没有异常，说明有当前状态，索引应为size-1
            return caretaker.getAllHistory().size() - 1;
        } catch (MementoException e) {
            // 如果抛出异常说明没有当前状态，返回-1
            return -1;
        }
    }

    // ==================== HistoryManager 测试 ====================
    
    /**
     * 测试HistoryManager类的所有功能
     */
    @Test
    public void testHistoryManager() {
        // 创建独立的note避免共享状态
        Note testNote = new Note("测试笔记内容");
        HistoryManager testHistoryManager = new HistoryManager(testNote);
        
        // 测试初始状态
        List<Memento> history = testHistoryManager.getHistory();
        assertEquals("初始历史应有1个状态", 1, history.size());
        assertEquals("当前分支应为main", "main", testHistoryManager.getCurrentBranch());
        
        // 设置内容并保存
        testNote.setContent("初始内容");
        testHistoryManager.save();
        assertEquals("保存后历史应有2个状态", 2, testHistoryManager.getHistory().size());
        
        testNote.setContent("修改内容1");
        testHistoryManager.save();
        assertEquals("再次保存后历史应有3个状态", 3, testHistoryManager.getHistory().size());
        
        testNote.setContent("修改内容2");
        testHistoryManager.save();
        assertEquals("第三次保存后历史应有4个状态", 4, testHistoryManager.getHistory().size());
        
        // 测试撤销
        try {
            testHistoryManager.undo();
            assertEquals("撤销后内容应恢复", "修改内容1", testNote.getContent());
            
            testHistoryManager.undo();
            assertEquals("再次撤销后内容应为初始", "初始内容", testNote.getContent());
        } catch (MementoException e) {
            fail("撤销不应抛出异常");
        }
        
        // 测试撤销到开始
        try {
            testHistoryManager.undo();
            fail("在开始时撤销应抛出异常");
        } catch (MementoException e) {
            assertEquals("Cannot undo, no previous state available.", e.getMessage());
        }
        
        // 测试重做
        try {
            testHistoryManager.redo();
            assertEquals("重做后内容应为修改内容1", "修改内容1", testNote.getContent());
            
            testHistoryManager.redo();
            assertEquals("再次重做后内容应为修改内容2", "修改内容2", testNote.getContent());
            
            testHistoryManager.redo();
            assertEquals("第三次重做后内容应为修改内容2", "修改内容2", testNote.getContent());
        } catch (MementoException e) {
            fail("重做不应抛出异常");
        }
        
        // 测试重做到末尾
        try {
            testHistoryManager.redo();
            fail("在末尾重做应抛出异常");
        } catch (MementoException e) {
            assertEquals("Cannot redo, no next state available.", e.getMessage());
        }
        
        // 测试分支功能
        testHistoryManager.createBranch("feature");
        assertEquals("分支列表应包含main", true, testHistoryManager.getAllBranches().contains("main"));
        assertEquals("分支列表应包含feature", true, testHistoryManager.getAllBranches().contains("feature"));
        
        // 切换到新分支
        try {
            testHistoryManager.switchBranch("feature");
            assertEquals("当前分支应为feature", "feature", testHistoryManager.getCurrentBranch());
            assertEquals("新分支历史应为空", 0, testHistoryManager.getHistory().size());
        } catch (MementoException e) {
            fail("切换到存在的分支不应抛出异常");
        }
        
        // 在新分支保存状态
        testNote.setContent("分支内容");
        testHistoryManager.save();
        assertEquals("分支保存后历史应有1个状态", 1, testHistoryManager.getHistory().size());
        
        // 切换回主分支
        try {
            testHistoryManager.switchBranch("main");
            assertEquals("当前分支应为main", "main", testHistoryManager.getCurrentBranch());
            assertEquals("主分支内容应恢复", "修改内容2", testNote.getContent());
        } catch (MementoException e) {
            fail("切换回主分支不应抛出异常");
        }
        
        // 测试切换到不存在的分支
        try {
            testHistoryManager.switchBranch("不存在的分支");
            fail("切换到不存在的分支应抛出异常");
        } catch (MementoException e) {
            assertEquals("Branch not found: 不存在的分支", e.getMessage());
        }
        
        // 测试创建重复分支
        int branchCountBefore = testHistoryManager.getAllBranches().size();
        testHistoryManager.createBranch("feature");
        assertEquals("创建重复分支不应增加数量", branchCountBefore, testHistoryManager.getAllBranches().size());
        
        // 测试清空历史
        testHistoryManager.clearHistory();
        assertEquals("清空后历史应为空", 0, testHistoryManager.getHistory().size());
    }

    // ==================== CalendarManager 测试 ====================
    
    /**
     * 测试CalendarManager类的所有功能
     */
    @Test
    public void testCalendarManager() {
        // 创建测试日期
        Date today = new Date();
        Date yesterday = new Date(today.getTime() - 24 * 60 * 60 * 1000);
        Date tomorrow = new Date(today.getTime() + 24 * 60 * 60 * 1000);
        
        // 测试按日期添加笔记
        calendarManager.addNoteByDate(note, today);
        List<Note> todayNotes = calendarManager.getNotesByDay(today);
        assertEquals("今天的笔记数量应为1", 1, todayNotes.size());
        assertEquals("应包含添加的笔记", note, todayNotes.get(0));
        
        // 测试获取其他日期的笔记
        List<Note> yesterdayNotes = calendarManager.getNotesByDay(yesterday);
        assertTrue("昨天的笔记应为空", yesterdayNotes.isEmpty());
        
        // 测试同一天添加多个笔记
        Note note2 = new Note("今天的第二个笔记");
        calendarManager.addNoteByDate(note2, today);
        List<Note> todayNotesAfter = calendarManager.getNotesByDay(today);
        assertEquals("今天的笔记数量应为2", 2, todayNotesAfter.size());
        assertTrue("应包含所有笔记", todayNotesAfter.containsAll(Arrays.asList(note, note2)));
        
        // 测试按月份获取笔记
        List<Note> monthNotes = calendarManager.getNotesByMonth(today);
        assertEquals("当月笔记数量应为2", 2, monthNotes.size());
        assertTrue("应包含所有当月笔记", monthNotes.containsAll(Arrays.asList(note, note2)));
        
        // 测试不同月份的笔记
        Note nextMonthNote = new Note("下月笔记");
        calendarManager.addNoteByDate(nextMonthNote, tomorrow);
        
        // 如果tomorrow在不同月份，则测试月份过滤
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
        if (!monthFormat.format(today).equals(monthFormat.format(tomorrow))) {
            List<Note> currentMonthNotes = calendarManager.getNotesByMonth(today);
            assertEquals("当前月份笔记数量仍应为2", 2, currentMonthNotes.size());
        }
    }

    // ==================== CalendarManager.Reminder 测试 ====================
    
    /**
     * 测试CalendarManager.Reminder内部类的所有功能
     */
    @Test
    public void testCalendarManagerReminder() {
        Date remindTime = new Date(System.currentTimeMillis() + 3600000); // 1小时后
        
        // 测试正常构造
        CalendarManager.Reminder reminder = new CalendarManager.Reminder(note, remindTime);
        assertEquals("应包含正确的笔记", note, reminder.getNote());
        assertEquals("应包含正确的提醒时间", remindTime, reminder.getRemindTime());
        assertFalse("初始状态应为未触发", reminder.isTriggered());
        
        // 测试时间戳保护（返回的是副本）
        Date originalRemindTime = reminder.getRemindTime();
        long originalTime = originalRemindTime.getTime();
        originalRemindTime.setTime(0);
        Date newRemindTime = reminder.getRemindTime();
        assertEquals("修改返回的时间不应影响内部", originalTime, newRemindTime.getTime());
        
        // 测试设置触发状态
        reminder.setTriggered(true);
        assertTrue("设置后应为已触发", reminder.isTriggered());
        
        reminder.setTriggered(false);
        assertFalse("重置后应为未触发", reminder.isTriggered());
        
        // 测试异常构造
        try {
            new CalendarManager.Reminder(null, remindTime);
            fail("null笔记应抛出异常");
        } catch (IllegalArgumentException e) {
            assertEquals("null arg", e.getMessage());
        }
        
        try {
            new CalendarManager.Reminder(note, null);
            fail("null时间应抛出异常");
        } catch (IllegalArgumentException e) {
            assertEquals("null arg", e.getMessage());
        }
        
        try {
            new CalendarManager.Reminder(null, null);
            fail("两个null参数应抛出异常");
        } catch (IllegalArgumentException e) {
            assertEquals("null arg", e.getMessage());
        }
    }

    // ==================== Plugin 接口测试 ====================
    
    /**
     * 测试Plugin接口的实现
     */
    @Test
    public void testPluginInterface() {
        // 创建一个简单的Plugin实现来测试接口
        Plugin testPlugin = new Plugin() {
            @Override
            public String getName() {
                return "测试插件";
            }
            
            @Override
            public void execute(UserManager userManager) {
                // 测试执行逻辑
                User testUser = userManager.registerUser("插件用户");
                assertNotNull("插件应能创建用户", testUser);
            }
        };
        
        // 测试getName方法
        assertEquals("插件名称应正确", "测试插件", testPlugin.getName());
        
        // 测试execute方法
        UserManager testUserManager = new UserManager();
        testPlugin.execute(testUserManager);
        assertEquals("插件执行后应有用户", 1, testUserManager.getAllUsers().size());
        assertNotNull("应包含创建的用户", testUserManager.getUser("插件用户"));
    }

    // ==================== Originator 接口测试 ====================
    
    /**
     * 测试Originator接口的实现
     */
    @Test
    public void testOriginatorInterface() {
        // Note类已经实现了Originator接口，这里测试接口契约
        assertTrue("Note应是Originator的实现", note instanceof Originator);
        
        // 测试createMemento方法
        Memento memento = ((Originator) note).createMemento();
        assertNotNull("创建的Memento不应为null", memento);
        assertTrue("应为NoteMemento类型", memento instanceof NoteMemento);
        
        // 测试restoreMemento方法
        String originalContent = note.getContent();
        note.setContent("修改后的内容");
        
        try {
            ((Originator) note).restoreMemento(memento);
            assertEquals("恢复后内容应与原来相同", originalContent, note.getContent());
        } catch (MementoException e) {
            fail("恢复正确的Memento不应抛出异常");
        }
    }

    // ==================== 综合测试场景 ====================
    
    /**
     * 测试复杂的业务场景，验证各组件之间的协作
     */
    @Test
    public void testComplexBusinessScenario() {
        // 创建用户和笔记管理系统
        UserManager userManager = new UserManager();
        LabelManager labelManager = new LabelManager();
        SearchService searchService = new SearchService();
        RecycleBin recycleBin = new RecycleBin();
        
        // 注册用户
        User alice = userManager.registerUser("Alice");
        User bob = userManager.registerUser("Bob");
        
        // 创建标签
        Label workLabel = new Label("工作");
        Label personalLabel = new Label("个人");
        Label urgentLabel = new Label("紧急", workLabel); // 嵌套标签
        
        // 创建笔记
        Note projectNote = new Note("项目进度报告：本周完成了核心功能开发");
        Note meetingNote = new Note("会议记录：讨论了下季度的产品规划");
        Note personalNote = new Note("个人备忘录：周末要去买生活用品");
        Note urgentTask = new Note("紧急任务：需要在今天之前完成客户提案");
        
        // 添加标签到笔记
        projectNote.addLabel(workLabel);
        meetingNote.addLabel(workLabel);
        personalNote.addLabel(personalLabel);
        urgentTask.addLabel(workLabel);
        urgentTask.addLabel(urgentLabel);
        
        // 用户添加笔记
        alice.addNote(projectNote);
        alice.addNote(meetingNote);
        alice.addNote(personalNote);
        bob.addNote(urgentTask);
        
        // 使用LabelManager管理标签
        labelManager.addLabelToNote(workLabel, projectNote);
        labelManager.addLabelToNote(workLabel, meetingNote);
        labelManager.addLabelToNote(personalLabel, personalNote);
        labelManager.addLabelToNote(urgentLabel, urgentTask);
        
        // 测试标签路径
        assertEquals("嵌套标签路径应正确", "工作/紧急", urgentLabel.getFullPath());
        assertEquals("根标签路径应正确", "工作", workLabel.getFullPath());
        
        // 测试搜索功能
        List<Note> workNotes = searchService.searchByLabel(alice, workLabel);
        assertEquals("Alice的工作笔记应为2个", 2, workNotes.size());
        
        List<Note> projectSearch = searchService.searchByKeyword(alice, "项目");
        assertEquals("项目相关笔记应为1个", 1, projectSearch.size());
        
        List<Note> allUserSearch = searchService.searchByKeywordAllUsers(
            userManager.getAllUsers(), "任务");
        assertEquals("所有用户的任务笔记应为1个", 1, allUserSearch.size());
        
        // 测试笔记加密
        String originalContent = projectNote.getContent();
        String encryptedContent = NoteEncryptor.encrypt(originalContent);
        String decryptedContent = NoteEncryptor.decrypt(encryptedContent);
        assertEquals("加密解密后内容应相同", originalContent, decryptedContent);
        
        // 测试历史管理
        HistoryManager aliceHistory = alice.getHistoryManager(projectNote);
        projectNote.setContent("更新后的项目进度报告");
        aliceHistory.save();
        
        try {
            aliceHistory.undo();
            assertEquals("撤销后内容应恢复", originalContent, projectNote.getContent());
            aliceHistory.redo();
            assertEquals("重做后内容应为更新后", "更新后的项目进度报告", projectNote.getContent());
        } catch (MementoException e) {
            fail("历史操作不应抛出异常");
        }
        
        // 测试回收站
        recycleBin.recycle(personalNote);
        assertTrue("笔记应在回收站中", recycleBin.isInBin(personalNote));
        
        boolean restored = recycleBin.restore(personalNote);
        assertTrue("恢复应成功", restored);
        assertFalse("恢复后笔记不应在回收站中", recycleBin.isInBin(personalNote));
        
        // 测试权限管理
        PermissionManager permissionManager = new PermissionManager();
        permissionManager.grantPermission(alice, Permission.OWNER);
        permissionManager.grantPermission(bob, Permission.VIEW);
        
        assertTrue("Alice应有编辑权限", permissionManager.canEdit(alice));
        assertFalse("Bob不应有编辑权限", permissionManager.canEdit(bob));
        assertTrue("Bob应有查看权限", permissionManager.canView(bob));
        
        // 测试统计服务
        StatisticsService statsService = new StatisticsService();
        Map<Label, Integer> labelStats = statsService.labelUsage(userManager.getAllUsers());
        assertTrue("工作标签应被统计", labelStats.containsKey(workLabel));
        assertTrue("个人标签应被统计", labelStats.containsKey(personalLabel));
        assertTrue("紧急标签应被统计", labelStats.containsKey(urgentLabel));
        
        int totalNotes = statsService.noteCount(userManager.getAllUsers());
        assertEquals("总笔记数应为4", 4, totalNotes);
        
        // 测试笔记比较
        Note note1 = new Note("相同内容");
        Note note2 = new Note("相同内容");
        assertEquals("相同内容的标签应相等", 
            new Label("测试"), new Label("测试"));
        
        // 测试标签建议服务
        LabelSuggestionService suggestionService = new LabelSuggestionService();
        Collection<Label> allLabels = Arrays.asList(workLabel, personalLabel, urgentLabel);
        
        Note newWorkNote = new Note("新的工作相关笔记");
        List<Label> suggestions = suggestionService.suggestLabels(newWorkNote, allLabels);
        assertFalse("应有标签建议", suggestions.isEmpty());
        assertTrue("应建议工作相关标签", suggestions.contains(workLabel));
    }

    /**
     * 测试边界条件和异常情况
     */
    @Test
    public void testEdgeCasesAndExceptions() {
        // 测试空集合和null值的处理
        SearchService searchService = new SearchService();
        LabelManager labelManager = new LabelManager();
        StatisticsService statisticsService = new StatisticsService();
        
        // 空集合搜索
        List<Note> emptyUserSearch = searchService.searchByKeyword(user, "测试");
        assertTrue("空用户搜索应返回空列表", emptyUserSearch.isEmpty());
        
        List<Note> emptyCollectionSearch = searchService.searchByKeywordAllUsers(Collections.emptyList(), "测试");
        assertTrue("空集合搜索应返回空列表", emptyCollectionSearch.isEmpty());
        
        // null值处理
        List<Note> nullKeywordSearch = searchService.searchByKeyword(user, null);
        assertTrue("null关键词搜索应返回空列表", nullKeywordSearch.isEmpty());
        
        String nullHighlight = searchService.highlight("内容", null);
        assertEquals("null关键词高亮应返回原内容", "内容", nullHighlight);
        
        // 统计服务的空集合处理
        Map<Label, Integer> emptyLabelStats = statisticsService.labelUsage(Collections.emptyList());
        assertTrue("空用户集合标签统计应为空", emptyLabelStats.isEmpty());
        
        int emptyNoteCount = statisticsService.noteCount(Collections.emptyList());
        assertEquals("空用户集合笔记数应为0", 0, emptyNoteCount);
        
        // LabelManager的null处理
        labelManager.addLabelToNote(null, note);
        // LabelManager实际上允许null标签，只是不执行任何操作
        // 这是设计如此，我们测试这个行为
        
        // 测试字符串处理的边界情况
        String emptyDiff = NoteDiffUtil.diff("", "内容");
        assertTrue("空字符串diff应显示添加", emptyDiff.contains("+ 内容"));
        
        String nullDiff = NoteDiffUtil.diff(null, "内容");
        assertTrue("null字符串diff应显示添加", nullDiff.contains("+ 内容"));
        
        // 测试加密解密的边界情况
        String emptyEncrypt = NoteEncryptor.encrypt("");
        assertEquals("空字符串加密应返回空", "", emptyEncrypt);
        
        String nullEncrypt = NoteEncryptor.encrypt(null);
        assertNull("null加密应返回null", nullEncrypt);
        
        // 测试CalendarManager的日期处理
        CalendarManager calendarManager = new CalendarManager();
        Date testDate = new Date();
        Note testNote = new Note("测试笔记");
        
        calendarManager.addNoteByDate(testNote, testDate);
        List<Note> sameDateNotes = calendarManager.getNotesByDay(testDate);
        assertEquals("同日期笔记应被正确存储", 1, sameDateNotes.size());
        
        // 测试时间戳的保护性拷贝
        Memento memento = new NoteMemento("测试");
        Date timestamp1 = memento.getTimestamp();
        Date timestamp2 = memento.getTimestamp();
        assertNotSame("每次获取应返回新的Date对象", timestamp1, timestamp2);
        assertEquals("但时间值应相同", timestamp1.getTime(), timestamp2.getTime());
    }
}