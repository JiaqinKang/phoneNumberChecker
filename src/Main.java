import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.Vector;

public class Main extends JFrame {

    private JTextField numberField;
    private DefaultTableModel tableModel;
    private JTable recordTable;

    public Main() {
        setTitle("手机尾号评价打分");
        //set title font size
        setSize(320, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 设置背景颜色
        Color backgroundColor = new Color(214, 231, 248);
        Color textFieldColor = new Color(200, 221, 241);

        // 设置背景颜色
        getContentPane().setBackground(backgroundColor);

        // 创建表格模型
        String[] columnNames = {"尾号4位数", "评分", "等级", "类型", "价格"};
        tableModel = new DefaultTableModel(columnNames, 0);

        // 设置表格背景颜色
        tableModel.setColumnIdentifiers(columnNames);


        // 设置列背景颜色
        recordTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(recordTable);

        // 创建并设置居中渲染器
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        recordTable.setDefaultRenderer(Object.class, centerRenderer);

        // 设置表格和滚动面板的背景颜色
        recordTable.setBackground(textFieldColor);
        recordTable.setOpaque(false);
        scrollPane.getViewport().setBackground(textFieldColor);

        // 创建输入面板
        numberField = new JTextField(10);
        numberField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    addRecord();
                }
            }
        });

        // 设置文本域的背景颜色
        numberField.setBackground(textFieldColor);

        JLabel promptLabel = new JLabel("*输入尾号");
        JPanel inputPanel = new JPanel();
        inputPanel.add(numberField);
        inputPanel.add(promptLabel);

        // 设置输入面板背景颜色
        inputPanel.setBackground(backgroundColor);

        add(new JLabel("看看你的尾号："), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        // 设置标签背景颜色，需要封装到JPanel后设置
        JPanel northPanel = new JPanel();
        northPanel.setBackground(backgroundColor);
        JLabel titleLabel = new JLabel("看看你的尾号：");
        northPanel.add(titleLabel);
        add(northPanel, BorderLayout.NORTH);
    }

    private void addRecord() {
        String number = numberField.getText();
        if (number.length() != 4 || !number.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "请输入有效的4位数字！");
            return;
        }

        int score = evaluateNumber(number);
        String grade = getGrade(score);
        String type = getType(number);
        String price = evaluatePrice(number, score);

        Vector<String> newRow = new Vector<>();
        newRow.add(number);
        newRow.add(score + "分");
        newRow.add(grade);
        newRow.add(type);
        newRow.add(price);

        if (tableModel.getRowCount() == 16) {
            tableModel.removeRow(0);
        }

        tableModel.addRow(newRow);

        numberField.setText("");
    }

    private int evaluateNumber(String number) {
        int baseScore = 70; // 基础分数

        // 四个相同
        if (number.matches("(\\d)\\1\\1\\1")) {
            baseScore = 100;
        }
        // aabb模式
        else if (number.matches("(\\d)\\1(\\d)\\2")) {
            baseScore = 90;
        }
        // aaab模式
        else if (number.matches("(\\d)\\1\\1(\\d)")) {
            baseScore = 80;
        }
        // abba模式
        else if (number.matches("(\\d)(\\d)\\2\\1")) {
            baseScore = 85;
        }
        // abab模式
        else if (number.matches("(\\d)(\\d)\\1\\2")) {
            baseScore = 70;
        }
        // abcc模式
        else if (number.matches("(\\d)(\\d)(\\d)\\3")) {
            baseScore = 60;
        }
        // 顺子如1234
        else if (number.matches("1234|2345|3456|4567|5678|6789")) {
            baseScore = 95;
        }
        // 倒顺子如4321
        else if (number.matches("4321|5432|6543|7654|8765|9876")) {
            baseScore = 90;
        }
        // 对称如1221
        else if (number.matches("(\\d)(\\d)\\2\\1")) {
            baseScore = 85;
        }
        // 特殊号码如1688
        else if (number.matches("1688|1888|6688|6888|1999|6999|9999")) {
            baseScore = 99;
        }
        // 520
        else if (number.equals("520")) {
            baseScore = 90;
        }
        else if (number.equals("5200")) {
            baseScore = 85;
        }
        else if (number.equals("0520")) {
            baseScore = 85;
        }
        // 1314
        else if (number.equals("1314")) {
            baseScore = 95;
        }
        // 特殊号码如8888
        else if (number.equals("8888")) {
            baseScore = 100;
        }

        // 其它吉利数字
        if (number.contains("8")) {
            baseScore += 5; // 8是吉利数字，加5分
        }
        if (number.contains("6")) {
            baseScore += 5; // 6是吉利数字，加5分
        }
        if (number.contains("9")) {
            baseScore += 5; // 9是吉利数字，加5分
        }

        // 含4扣分
        if (number.contains("4") && !number.matches("1234|2345|3456|4567|5678|6789|4321|5432|6543|7654|8765|9876")) {
            baseScore -= 5; // 含4扣5分
        }

        // 引入随机性
        Random random = new Random(number.hashCode());
        int randomFactor = random.nextInt(11) - 5; // 随机范围[-5,+5]

        // 避免重复加分超过100分或低于0分
        return Math.max(Math.min(baseScore + randomFactor, 100), 0);
    }

    private String getGrade(int score) {
        if (score >= 90) {
            return "很棒";
        } else if (score >= 70) {
            return "优秀";
        } else {
            return "中上";
        }
    }

    private String getType(String number) {
        // 判断类型
        if (number.matches("(\\d)\\1\\1\\1")) {
            return "可以收藏";
        } else if (number.matches("(\\d)\\1(\\d)\\2")) {
            return "保持使用";
        } else {
            return "正常使用";
        }
    }

    private String evaluatePrice(String number, int score) {
        int basePrice = 100; //  基础价格

        // 特殊号码显著提高价格
        if (number.equals("8888")) {
            basePrice += 5000;
        } else if (number.equals("1314") || number.equals("520")) {
            basePrice += 2000;
        } else if (number.matches("1688|1888|6688|6888|1999|6999|9999")) {
            basePrice += 1500;
        }

        // 四个相同的数字
        if (number.matches("(\\d)\\1\\1\\1")) {
            basePrice += 3000;
        }
        // 顺子
        if (number.matches("1234|2345|3456|4567|5678|6789")) {
            basePrice += 1000;
        }

        // 吉利数字增加价格
        if (number.contains("8")) {
            basePrice += 500 * (number.length() - number.replace("8", "").length());
        }
        if (number.contains("6")) {
            basePrice += 300 * (number.length() - number.replace("6", "").length());
        }
        if (number.contains("9")) {
            basePrice += 200 * (number.length() - number.replace("9", "").length());
        }
        // 含4扣减价格
        if (number.contains("4")) {
            basePrice -= 500 * (number.length() - number.replace("4", "").length());
        }

        // 引入基于号码哈希值的随机性
        Random random = new Random(number.hashCode());
        basePrice += random.nextInt(500) + 200;

        return "¥" + basePrice;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main evaluator = new Main();
            evaluator.setVisible(true);
            evaluator.setBackground(new Color(214, 231, 248 ));
            // icon null
            evaluator.setIconImage(null);
            // resizable false
            evaluator.setResizable(false);
        });
    }
}